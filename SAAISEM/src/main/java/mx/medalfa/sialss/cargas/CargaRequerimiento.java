package mx.medalfa.sialss.cargas;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mx.medalfa.sialss.dao.Consultas;
import mx.medalfa.sialss.dao.Fila;
import mx.medalfa.sialss.seguridad.Acceso;

/**
 * Carga de requerimiento de unidades (reemplaza requerimiento.jsp → FileUploadServlet → LeeExcel).
 * Columnas como el archivo de siempre: Unidad (tb_uniatn.F_ClaCli), Clave, Fecha de entrega, Cantidad (piezas).
 * <p>
 * Al aplicar, por cada unidad del archivo: los requerimientos pendientes anteriores (tb_unireq F_Status 0) pasan
 * a 1 y se registra el nuevo (una fila por unidad, clave y fecha, sumando cantidades); también se guarda el detalle
 * en tb_cargareq (con el usuario) y una marca en tb_monitor_req por unidad y fecha, como la carga anterior.
 * Diferencia: antes las claves inexistentes, suspendidas o fuera del nivel de la unidad se omitían sin avisar;
 * ahora son errores y no se carga nada hasta corregir el archivo.
 */
public final class CargaRequerimiento implements CargaArchivo {

    static final String[] COLUMNAS = {"Unidad", "Clave", "Fecha de entrega", "Cantidad"};

    record Renglon(int numero, String unidad, String clave, LocalDate fecha, int cantidad) implements Serializable {
    }

    /** Renglones de ejemplo de la plantilla: no se cargan si el archivo los trae tal cual. */
    static String[][] ejemplos() {
        return new String[][]{{"1", "200962.02", LocalDate.now().plusDays(7).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), "10"}};
    }

    @Override
    public String nombre() {
        return "requerimiento";
    }

    @Override
    public String pagina() {
        return "CargaArchivoRequerimiento.jsp";
    }

    @Override
    public Set<Acceso> accesos() {
        return Set.of(Acceso.ALMACEN);
    }

    @Override
    public void plantilla(Connection c, OutputStream salida) throws IOException, SQLException {
        Excel.plantilla(salida, "Requerimiento", COLUMNAS, 4,
                ejemplos(),
                List.of(new Excel.Bloque("Unidad (columna A) y nivel", Consultas.lista(c,
                        "SELECT F_ClaCli, CONCAT(F_NomCli, ' · nivel ', F_NivelUni, IF(F_StsCli = 'A', '', ' · SUSPENDIDA')) FROM tb_uniatn ORDER BY F_ClaCli"))),
                List.of("Carga de requerimiento SIALSS. Un renglón por unidad, clave y fecha de entrega; todas las columnas son obligatorias.",
                        "Unidad: la clave de la unidad (hoja Catálogos). Clave: la del catálogo de insumos, activa y autorizada para el nivel de la unidad.",
                        "Fecha de entrega: fecha de Excel o texto dd/mm/aaaa. Cantidad: piezas, entero mayor a 0.",
                        "Los pendientes se conservan: un renglón con la misma unidad, clave y fecha de entrega que un requerimiento pendiente, o repetido en el archivo, se omite; solo se agregan los nuevos.",
                        "Máximo " + Excel.MAXIMO_RENGLONES + " renglones. Si un renglón tiene error no se carga nada: corrija el archivo y vuelva a subirlo."));
    }

    @Override
    public Revision revisar(Connection c, InputStream archivo, Usuario u) throws IOException, SQLException {
        List<Excel.Renglon> crudos;
        try {
            crudos = Excel.leer(archivo, COLUMNAS.length, Set.of(2), ejemplos());
        } catch (Excel.Ilegible e) {
            return Revision.conError(e.getMessage());
        }
        Map<String, Fila> unidades = new HashMap<>();
        for (Fila f : Consultas.lista(c, "SELECT F_ClaCli, F_NomCli, F_StsCli, F_NivelUni FROM tb_uniatn")) {
            unidades.put(f.get(1), f);
        }
        Map<String, Fila> insumos = new HashMap<>();
        for (Fila f : Consultas.lista(c, "SELECT F_ClaPro, F_DesPro, F_StsPro, F_N17, F_N18 FROM tb_medica")) {
            insumos.put(f.get(1), f);
        }

        // Pendientes (F_Status 0) por unidad|clave|fecha: lo que ya está cargado no se vuelve a subir.
        Set<String> pendientes = new java.util.HashSet<>();
        for (Fila f : Consultas.lista(c, "SELECT F_ClaUni, F_ClaPro, F_Fecha FROM tb_unireq WHERE F_Status = 0")) {
            pendientes.add(f.get(1) + "|" + f.get(2) + "|" + f.get(3));
        }
        Set<String> enArchivo = new java.util.HashSet<>();
        Omitidos omitidos = new Omitidos();
        List<String> errores = new ArrayList<>();
        List<Renglon> renglones = new ArrayList<>();
        List<List<String>> muestra = new ArrayList<>();
        Set<String> unidadesArchivo = new LinkedHashSet<>();
        long piezas = 0;
        for (Excel.Renglon cr : crudos) {
            String[] v = cr.v();
            List<String> mal = new ArrayList<>();
            String unidad = Excel.sinDecimalCero(v[0]);
            Fila uni = unidades.get(unidad);
            if (unidad.isEmpty()) {
                mal.add("falta la unidad");
            } else if (uni == null) {
                mal.add("la unidad " + unidad + " no existe");
            } else if (!"A".equals(uni.get(3))) {
                mal.add("la unidad " + unidad + " está suspendida");
            }
            String clave = Claves.resolver(v[1], insumos);
            Fila ins = clave == null ? null : insumos.get(clave);
            if (v[1].isEmpty()) {
                mal.add("falta la clave");
            } else if (ins == null) {
                mal.add("la clave " + v[1] + " no existe");
            } else if (!"A".equals(ins.get(3))) {
                mal.add("la clave " + clave + " no está activa");
            } else if (uni != null && "17".equals(uni.get(4)) && !"1".equals(ins.get(4))) {
                mal.add("la clave " + clave + " no está autorizada para el nivel 17 de la unidad");
            } else if (uni != null && "18".equals(uni.get(4)) && !"1".equals(ins.get(5))) {
                mal.add("la clave " + clave + " no está autorizada para el nivel 18 de la unidad");
            }
            LocalDate fecha = Excel.fecha(v[2], "la fecha de entrega", true, mal);
            int cantidad = Excel.cantidad(v[3], "la cantidad", mal);
            if (!mal.isEmpty()) {
                errores.add("Renglón " + cr.numero() + ": " + String.join("; ", mal) + ".");
                continue;
            }
            String llave = unidad + "|" + clave + "|" + fecha;
            String fechaTxt = fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            if (pendientes.contains(llave) || !enArchivo.add(llave)) {
                omitidos.agregar(cr.numero(), "unidad " + unidad + ", clave " + clave + ", entrega " + fechaTxt + ": "
                        + (pendientes.contains(llave) ? "ya está en un requerimiento pendiente" : "se repite en el archivo"));
                continue;
            }
            renglones.add(new Renglon(cr.numero(), unidad, clave, fecha, cantidad));
            unidadesArchivo.add(unidad);
            piezas += cantidad;
            if (muestra.size() < 200) {
                muestra.add(List.of(String.valueOf(cr.numero()), unidad, uni.get(2), clave, ins.get(2),
                        fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), String.valueOf(cantidad)));
            }
        }
        if (!errores.isEmpty()) {
            return new Revision(errores, muestra, "", null);
        }
        if (renglones.isEmpty()) {
            return omitidos.sinNuevos(crudos.size(), "renglones nuevos de requerimiento");
        }
        String resumen = Texto.cuantos(renglones.size(), "renglón nuevo", "renglones nuevos") + ", "
                + Texto.cuantos(unidadesArchivo.size(), "unidad", "unidades") + ", " + Texto.cuantos(piezas, "pieza", "piezas")
                + ". Los pendientes anteriores se conservan.";
        return omitidos.valida(muestra, resumen, new Datos(new ArrayList<>(renglones), omitidos.cuantos()));
    }

    /** Lo que se guarda en la sesión entre revisar y confirmar. */
    record Datos(ArrayList<Renglon> renglones, int omitidos) implements java.io.Serializable {
    }

    @Override
    public String aplicar(Connection c, Revision r, Usuario u) throws SQLException {
        Datos datos = (Datos) r.datos();
        List<Renglon> renglones = datos.renglones();
        // unidad → (clave|fecha → cantidad), en el orden del archivo
        Map<String, Map<String, Integer>> porUnidad = new LinkedHashMap<>();
        Set<String> monitor = new LinkedHashSet<>();
        for (Renglon x : renglones) {
            porUnidad.computeIfAbsent(x.unidad(), k -> new LinkedHashMap<>())
                     .merge(x.clave() + "|" + x.fecha(), x.cantidad(), Integer::sum);
            monitor.add(x.fecha() + "|" + x.unidad());
        }
        boolean auto = c.getAutoCommit();
        c.setAutoCommit(false);
        int registros = 0;
        try {
            try (PreparedStatement ps = c.prepareStatement("DELETE FROM tb_cargareq WHERE F_User = ?")) {
                ps.setString(1, u.usuario());
                ps.executeUpdate();
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO tb_cargareq (F_ClaUni, F_ClaPro, F_CajasReq, F_PiezasReq, F_FecCarg, F_Status, F_Fecha, F_Solicitado, F_User, F_Hoy) "
                    + "VALUES (?, ?, 0, ?, CURDATE(), 0, ?, ?, ?, NOW())")) {
                for (Renglon x : renglones) {
                    ps.setString(1, x.unidad());
                    ps.setString(2, x.clave());
                    ps.setInt(3, x.cantidad());
                    ps.setDate(4, Date.valueOf(x.fecha()));
                    ps.setInt(5, x.cantidad());
                    ps.setString(6, u.usuario());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO tb_monitor_req (F_Fecha, F_ClaUni, F_Creacion) VALUES (?, ?, NOW())")) {
                for (String m : monitor) {
                    String[] p = m.split("\\|", 2);
                    ps.setString(1, p[0]);
                    ps.setString(2, p[1]);
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            // Ya no se cierran los pendientes de la unidad: solo se agregan los renglones nuevos (los repetidos se omitieron).
            try (PreparedStatement alta = c.prepareStatement(
                         "INSERT INTO tb_unireq (F_ClaUni, F_ClaPro, F_CajasReq, F_PiezasReq, F_FecCarg, F_Status, F_Fecha, F_Solicitado, F_Obs) "
                         + "VALUES (?, ?, 0, ?, CURDATE(), 0, ?, ?, '')")) {
                for (Map.Entry<String, Map<String, Integer>> e : porUnidad.entrySet()) {
                    for (Map.Entry<String, Integer> cf : e.getValue().entrySet()) {
                        String[] p = cf.getKey().split("\\|", 2);
                        alta.setString(1, e.getKey());
                        alta.setString(2, p[0]);
                        alta.setInt(3, cf.getValue());
                        alta.setDate(4, Date.valueOf(LocalDate.parse(p[1])));
                        alta.setInt(5, cf.getValue());
                        alta.addBatch();
                        registros++;
                    }
                }
                alta.executeBatch();
            }
            c.commit();
        } catch (SQLException | RuntimeException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(auto);
        }
        return "Requerimiento cargado: " + Texto.cuantos(porUnidad.size(), "unidad", "unidades") + ", "
                + Texto.cuantos(registros, "clave nueva", "claves nuevas") + " (por unidad y fecha). Los pendientes anteriores se conservan."
                + Omitidos.mensajeFinal(datos.omitidos());
    }
}
