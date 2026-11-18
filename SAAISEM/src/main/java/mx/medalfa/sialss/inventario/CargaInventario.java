package mx.medalfa.sialss.inventario;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mx.medalfa.sialss.dao.Consultas;
import mx.medalfa.sialss.dao.Fila;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Carga de inventario desde Excel directo a tb_lote y tb_movinv (concepto 11 ALTA DE INVENTARIO),
 * con el usuario de la sesión. Dos pasos: {@link #revisar} lee y valida todo el archivo sin escribir;
 * {@link #aplicar} escribe todo en una transacción (si un renglón falla, no se guarda nada).
 *
 * Mismo lote = misma clave + lote + caducidad + proyecto + origen: reutiliza su folio (como
 * LeeExcel/CargaExcelInvIni). Si ese folio ya está en la ubicación, suma la cantidad; si no, crea el renglón.
 */
public final class CargaInventario {

    public static final int CONCEPTO_ALTA = 11;
    public static final int MAXIMO_RENGLONES = 5000;
    public static final String[] COLUMNAS = {"Clave", "Lote", "Caducidad", "Cantidad", "Ubicación", "Proyecto", "Origen",
        "Proveedor", "Marca", "Código de barras", "Fabricación", "Tarima"};
    private static final DateTimeFormatter DMA = DateTimeFormatter.ofPattern("d/M/uuuu");

    private CargaInventario() {
    }

    /** Un renglón ya validado, listo para guardar. */
    public record Renglon(int numero, String clave, String descripcion, String lote, LocalDate caducidad, int cantidad,
                          String ubicacion, int proyecto, int origen, Integer proveedor, Integer marca, String cb,
                          LocalDate fabricacion, String tarima) implements Serializable {
    }

    /**
     * Resultado de revisar el archivo: renglones válidos, errores por renglón y renglones omitidos por repetidos
     * (lote con existencia en esa ubicación o repetido en el archivo; no son error).
     */
    public record Revision(List<Renglon> renglones, List<String> errores, int piezas, List<String> omitidos) implements Serializable {
        public Revision(List<Renglon> renglones, List<String> errores, int piezas) {
            this(renglones, errores, piezas, List.of());
        }

        public boolean valida() {
            return errores.isEmpty() && !renglones.isEmpty();
        }

        public boolean sinNuevos() {
            return errores.isEmpty() && renglones.isEmpty() && !omitidos.isEmpty();
        }
    }

    public record Aplicado(int documento, int lotesNuevos, int lotesSumados, int piezas) {
    }

    // ------------------------------------------------------------------ plantilla

    public static void plantilla(Connection c, OutputStream salida) throws IOException, SQLException {
        try (Workbook wb = new XSSFWorkbook()) {
            CellStyle negritas = wb.createCellStyle();
            Font f = wb.createFont();
            f.setBold(true);
            negritas.setFont(f);

            Sheet s = wb.createSheet("Inventario");
            Row h = s.createRow(0);
            for (int i = 0; i < COLUMNAS.length; i++) {
                Cell cel = h.createCell(i);
                cel.setCellValue(COLUMNAS[i] + (i < 7 ? " *" : ""));
                cel.setCellStyle(negritas);
                s.setColumnWidth(i, 18 * 256);
            }
            String[] ejemplo = {"200962.02", "S/L", "31/12/2030", "10", "NUEVA", "1", "1", "", "", "", "", ""};
            Row e = s.createRow(1);
            for (int i = 0; i < ejemplo.length; i++) {
                e.createCell(i).setCellValue(ejemplo[i]);
            }

            Sheet cat = wb.createSheet("Catálogos");
            int r = 0;
            r = bloque(cat, r, negritas, "Proyecto (columna F)", Consultas.lista(c, "SELECT F_Id, F_DesProy FROM tb_proyectos ORDER BY F_Id"));
            r = bloque(cat, r, negritas, "Origen (columna G)", Consultas.lista(c, "SELECT F_ClaOri, F_DesOri FROM tb_origen ORDER BY F_ClaOri"));
            r = bloque(cat, r, negritas, "Proveedor (columna H, opcional)", Consultas.lista(c, "SELECT F_ClaProve, F_NomPro FROM tb_proveedor ORDER BY F_ClaProve"));
            bloque(cat, r, negritas, "Marca (columna I, opcional)", Consultas.lista(c, "SELECT F_ClaMar, F_DesMar FROM tb_marca ORDER BY F_ClaMar"));
            cat.setColumnWidth(0, 14 * 256);
            cat.setColumnWidth(1, 50 * 256);

            Sheet ayuda = wb.createSheet("Instrucciones");
            String[] lineas = {
                "Carga de inventario SIALSS. Un renglón por lote y ubicación; los marcados con * son obligatorios.",
                "Caducidad y Fabricación: fecha de Excel o texto dd/mm/aaaa. Si no hay fabricación se usa la fecha de la carga.",
                "Proyecto, Origen, Proveedor y Marca: el número de la hoja Catálogos.",
                "Mismo lote = misma clave, lote, caducidad, proyecto y origen: conserva su folio.",
                "Un lote (clave, lote y caducidad) que ya tiene existencia en esa ubicación, o repetido en el archivo, se omite: solo se cargan los lotes nuevos.",
                "Código de barras vacío: se usa el del lote existente o se genera uno nuevo.",
                "Máximo " + MAXIMO_RENGLONES + " renglones. Si un renglón tiene error no se carga nada: corrija el archivo y vuelva a subirlo."};
            for (int i = 0; i < lineas.length; i++) {
                ayuda.createRow(i).createCell(0).setCellValue(lineas[i]);
            }
            ayuda.setColumnWidth(0, 120 * 256);
            wb.write(salida);
        }
    }

    private static int bloque(Sheet s, int r, CellStyle negritas, String titulo, List<Fila> filas) {
        Cell t = s.createRow(r++).createCell(0);
        t.setCellValue(titulo);
        t.setCellStyle(negritas);
        for (Fila f : filas) {
            Row row = s.createRow(r++);
            row.createCell(0).setCellValue(f.get(1));
            row.createCell(1).setCellValue(f.get(2));
        }
        return r + 1;
    }

    // ------------------------------------------------------------------ revisar

    public static Revision revisar(Connection c, InputStream archivo) throws IOException, SQLException {
        List<String> errores = new ArrayList<>();
        List<String[]> crudos = new ArrayList<>();
        List<Integer> numeros = new ArrayList<>();
        try (Workbook wb = WorkbookFactory.create(archivo)) {
            Sheet s = wb.getSheetAt(0);
            DataFormatter fmt = new DataFormatter();
            for (int i = 1; i <= s.getLastRowNum(); i++) {
                Row row = s.getRow(i);
                if (row == null) {
                    continue;
                }
                String[] v = new String[COLUMNAS.length];
                boolean vacio = true;
                for (int j = 0; j < COLUMNAS.length; j++) {
                    v[j] = celda(row.getCell(j), fmt, j == 2 || j == 10);
                    vacio &= v[j].isEmpty();
                }
                if (vacio) {
                    continue;
                }
                crudos.add(v);
                numeros.add(i + 1);
            }
        } catch (RuntimeException | org.apache.poi.openxml4j.exceptions.InvalidFormatException ex) {
            errores.add("No se pudo leer el archivo como Excel (.xlsx o .xls): " + ex.getMessage());
            return new Revision(List.of(), errores, 0);
        }
        if (crudos.isEmpty()) {
            errores.add("El archivo no tiene renglones con datos (la primera fila son los títulos).");
            return new Revision(List.of(), errores, 0);
        }
        if (crudos.size() > MAXIMO_RENGLONES) {
            errores.add("El archivo tiene " + crudos.size() + " renglones; el máximo es " + MAXIMO_RENGLONES + ".");
            return new Revision(List.of(), errores, 0);
        }

        Map<String, String> insumos = mapa(c, "SELECT F_ClaPro, F_DesPro FROM tb_medica");
        Set<String> ubicaciones = conjunto(c, "SELECT F_ClaUbi FROM tb_ubica");
        Set<String> proyectos = conjunto(c, "SELECT F_Id FROM tb_proyectos");
        Set<String> origenes = conjunto(c, "SELECT F_ClaOri FROM tb_origen");
        Set<String> proveedores = conjunto(c, "SELECT F_ClaProve FROM tb_proveedor");
        Set<String> marcas = conjunto(c, "SELECT F_ClaMar FROM tb_marca");

        // Lotes con existencia por clave|lote|caducidad|ubicación: no se vuelven a cargar.
        Set<String> conExistencia = new HashSet<>();
        for (Fila f : Consultas.lista(c, "SELECT F_ClaPro, F_ClaLot, F_FecCad, F_Ubica FROM tb_lote WHERE F_ExiLot > 0")) {
            conExistencia.add(llave(f.get(1), f.get(2), f.get(3), f.get(4)));
        }
        Set<String> enArchivo = new HashSet<>();
        mx.medalfa.sialss.cargas.Omitidos omitidos = new mx.medalfa.sialss.cargas.Omitidos();
        List<Renglon> renglones = new ArrayList<>();
        int piezas = 0;
        for (int k = 0; k < crudos.size(); k++) {
            String[] v = crudos.get(k);
            int n = numeros.get(k);
            List<String> mal = new ArrayList<>();
            String clave = v[0];
            if (clave.isEmpty()) {
                mal.add("falta la clave");
            } else if (!insumos.containsKey(clave)) {
                mal.add("la clave " + clave + " no existe");
            }
            String lote = v[1];
            if (lote.isEmpty()) {
                mal.add("falta el lote");
            } else if (lote.length() > mx.medalfa.sialss.config.ErrorBase.LARGO_LOTE) {
                mal.add("el lote pasa de " + mx.medalfa.sialss.config.ErrorBase.LARGO_LOTE + " caracteres");
            }
            LocalDate caducidad = fecha(v[2], "la caducidad", true, mal);
            int cantidad = 0;
            try {
                double d = Double.parseDouble(v[3].replace(",", ""));
                if (d <= 0 || d != Math.floor(d)) {
                    mal.add("la cantidad debe ser un entero mayor a 0");
                } else {
                    cantidad = (int) d;
                }
            } catch (NumberFormatException ex) {
                mal.add("la cantidad no es un número");
            }
            String ubicacion = v[4].toUpperCase();
            if (ubicacion.isEmpty()) {
                mal.add("falta la ubicación");
            } else if (!ubicaciones.contains(ubicacion)) {
                mal.add("la ubicación " + ubicacion + " no existe");
            }
            Integer proyecto = id(v[5], proyectos, "el proyecto", true, mal);
            Integer origen = id(v[6], origenes, "el origen", true, mal);
            Integer proveedor = id(v[7], proveedores, "el proveedor", false, mal);
            Integer marca = id(v[8], marcas, "la marca", false, mal);
            String cb = v[9].isEmpty() ? null : v[9];
            if (cb != null && cb.length() > 255) {
                mal.add("el código de barras pasa de 255 caracteres");
            }
            LocalDate fabricacion = fecha(v[10], "la fabricación", false, mal);
            String tarima = v[11].isEmpty() ? null : v[11];
            if (tarima != null && tarima.length() > 100) {
                mal.add("la tarima pasa de 100 caracteres");
            }
            if (!mal.isEmpty()) {
                errores.add("Renglón " + n + ": " + String.join("; ", mal) + ".");
                continue;
            }
            String llave = llave(clave, lote, caducidad.toString(), ubicacion);
            if (conExistencia.contains(llave) || !enArchivo.add(llave)) {
                omitidos.agregar(n, "clave " + clave + ", lote " + lote + ", caducidad " + caducidad.format(DMA) + " en " + ubicacion + ": "
                        + (conExistencia.contains(llave) ? "ya tiene existencia en esa ubicación" : "se repite en el archivo"));
                continue;
            }
            renglones.add(new Renglon(n, clave, insumos.get(clave), lote, caducidad, cantidad, ubicacion, proyecto,
                    origen, proveedor, marca, cb, fabricacion, tarima));
            piezas += cantidad;
        }
        return new Revision(errores.isEmpty() ? renglones : List.of(), errores, errores.isEmpty() ? piezas : 0,
                errores.isEmpty() ? omitidos.lista() : List.of());
    }

    private static String llave(String clave, String lote, String caducidad, String ubicacion) {
        return (clave + "|" + lote + "|" + caducidad + "|" + ubicacion).trim().toUpperCase(java.util.Locale.ROOT);
    }

    private static String celda(Cell cel, DataFormatter fmt, boolean esFecha) {
        if (cel == null) {
            return "";
        }
        // POI 3.15: getCellTypeEnum() (en 4.x es getCellType()).
        if (esFecha && cel.getCellTypeEnum() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cel)) {
            LocalDate d = cel.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return d.format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        return fmt.formatCellValue(cel).trim();
    }

    private static LocalDate fecha(String v, String nombre, boolean obligatoria, List<String> mal) {
        if (v.isEmpty()) {
            if (obligatoria) {
                mal.add("falta " + nombre);
            }
            return null;
        }
        try {
            return v.matches("\\d{4}-\\d{2}-\\d{2}") ? LocalDate.parse(v) : LocalDate.parse(v, DMA);
        } catch (DateTimeParseException ex) {
            mal.add(nombre + " no es una fecha (dd/mm/aaaa)");
            return null;
        }
    }

    private static Integer id(String v, Set<String> validos, String nombre, boolean obligatorio, List<String> mal) {
        if (v.isEmpty()) {
            if (obligatorio) {
                mal.add("falta " + nombre);
            }
            return null;
        }
        String limpio = v.endsWith(".0") ? v.substring(0, v.length() - 2) : v;
        if (!limpio.matches("\\d+") || !validos.contains(limpio)) {
            mal.add(nombre + " " + v + " no existe");
            return null;
        }
        return Integer.valueOf(limpio);
    }

    private static Map<String, String> mapa(Connection c, String sql) throws SQLException {
        Map<String, String> m = new HashMap<>();
        for (Fila f : Consultas.lista(c, sql)) {
            m.put(f.get(1), f.get(2));
        }
        return m;
    }

    private static Set<String> conjunto(Connection c, String sql) throws SQLException {
        Set<String> s = new HashSet<>();
        for (Fila f : Consultas.lista(c, sql)) {
            s.add(f.get(1));
        }
        return s;
    }

    // ------------------------------------------------------------------ aplicar

    public static Aplicado aplicar(Connection c, List<Renglon> renglones, String usuario) throws SQLException {
        boolean auto = c.getAutoCommit();
        c.setAutoCommit(false);
        try {
            int documento = Folios.siguienteDocumento(c);
            Map<String, String[]> folios = new HashMap<>();   // llave de lote -> {folio, cb}
            int nuevos = 0, sumados = 0, piezas = 0;
            for (Renglon r : renglones) {
                String llave = r.clave() + "|" + r.lote() + "|" + r.caducidad() + "|" + r.proyecto() + "|" + r.origen();
                String[] fc = folios.get(llave);
                if (fc == null) {
                    List<Fila> ya = Consultas.lista(c,
                            "SELECT F_FolLot, F_Cb FROM tb_lote WHERE F_ClaPro = ? AND F_ClaLot = ? AND F_FecCad = ? "
                            + "AND F_Proyecto = ? AND F_Origen = ? ORDER BY F_IdLote LIMIT 1",
                            r.clave(), r.lote(), Date.valueOf(r.caducidad()), r.proyecto(), r.origen());
                    if (ya.isEmpty()) {
                        fc = new String[]{String.valueOf(Folios.siguienteLote(c)), r.cb() != null ? r.cb() : Folios.siguienteCb(c)};
                    } else {
                        fc = new String[]{ya.get(0).get(1), r.cb() != null ? r.cb() : ya.get(0).get(2)};
                    }
                    folios.put(llave, fc);
                }
                int folio = Integer.parseInt(fc[0]);
                List<Fila> enUbicacion = Consultas.lista(c,
                        "SELECT F_IdLote FROM tb_lote WHERE F_FolLot = ? AND F_ClaPro = ? AND F_Ubica = ? LIMIT 1",
                        folio, r.clave(), r.ubicacion());
                if (enUbicacion.isEmpty()) {
                    try (PreparedStatement ps = c.prepareStatement(
                            "INSERT INTO tb_lote (F_ClaPro, F_ClaLot, F_FecCad, F_ExiLot, F_FolLot, F_ClaOrg, F_Ubica, F_FecFab, F_Cb, "
                            + "F_ClaMar, F_Origen, F_ClaPrv, F_tarima, F_Proyecto, F_observacion) "
                            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        Consultas.asignar(ps, r.clave(), r.lote(), Date.valueOf(r.caducidad()), r.cantidad(), folio,
                                r.proveedor(), r.ubicacion(),
                                Date.valueOf(r.fabricacion() != null ? r.fabricacion() : LocalDate.now()),
                                fc[1], r.marca(), r.origen(), r.proveedor(), r.tarima(), r.proyecto(),
                                "Carga de inventario doc " + documento);
                        ps.executeUpdate();
                    }
                    nuevos++;
                } else {
                    try (PreparedStatement ps = c.prepareStatement(
                            "UPDATE tb_lote SET F_ExiLot = F_ExiLot + ?" + (r.tarima() != null ? ", F_tarima = ?" : "")
                            + " WHERE F_IdLote = ?")) {
                        if (r.tarima() != null) {
                            Consultas.asignar(ps, r.cantidad(), r.tarima(), enUbicacion.get(0).entero(1));
                        } else {
                            Consultas.asignar(ps, r.cantidad(), enUbicacion.get(0).entero(1));
                        }
                        ps.executeUpdate();
                    }
                    sumados++;
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO tb_movinv (F_FecMov, F_DocMov, F_ConMov, F_ProMov, F_CantMov, F_CostMov, F_TotMov, F_SigMov, "
                        + "F_LotMov, F_UbiMov, F_ClaProve, F_hora, F_User, F_Comentarios) "
                        + "VALUES (CURDATE(), ?, ?, ?, ?, 0, 0, 1, ?, ?, ?, CURTIME(), ?, 'Carga de inventario (Excel)')")) {
                    Consultas.asignar(ps, String.valueOf(documento), CONCEPTO_ALTA, r.clave(), r.cantidad(), folio,
                            r.ubicacion(), r.proveedor() != null ? r.proveedor() : 0, usuario);
                    ps.executeUpdate();
                }
                piezas += r.cantidad();
            }
            c.commit();
            return new Aplicado(documento, nuevos, sumados, piezas);
        } catch (SQLException | RuntimeException ex) {
            c.rollback();
            throw ex;
        } finally {
            c.setAutoCommit(auto);
        }
    }

    /** Resumen para la pantalla (los primeros 200 renglones). */
    public static Map<String, Object> resumen(Revision r) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("valida", r.valida());
        m.put("renglones", r.renglones().size());
        m.put("piezas", r.piezas());
        m.put("errores", r.errores().size() > 200 ? r.errores().subList(0, 200) : r.errores());
        m.put("totalErrores", r.errores().size());
        m.put("omitidos", r.omitidos().size() > 200 ? r.omitidos().subList(0, 200) : r.omitidos());
        m.put("totalOmitidos", r.omitidos().size());
        m.put("sinNuevos", r.sinNuevos());
        List<Map<String, Object>> muestra = new ArrayList<>();
        for (Renglon x : r.renglones().subList(0, Math.min(200, r.renglones().size()))) {
            Map<String, Object> p = new LinkedHashMap<>();
            p.put("renglon", x.numero());
            p.put("clave", x.clave());
            p.put("descripcion", x.descripcion());
            p.put("lote", x.lote());
            p.put("caducidad", x.caducidad().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            p.put("cantidad", x.cantidad());
            p.put("ubicacion", x.ubicacion());
            p.put("proyecto", x.proyecto());
            p.put("tarima", x.tarima() == null ? "" : x.tarima());
            muestra.add(p);
        }
        m.put("muestra", muestra);
        return m;
    }
}
