package mx.medalfa.sialss.cargas;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import mx.medalfa.sialss.dao.Consultas;
import mx.medalfa.sialss.dao.Fila;
import mx.medalfa.sialss.inventario.EquipoDao;
import mx.medalfa.sialss.inventario.Folios;
import mx.medalfa.sialss.seguridad.Acceso;

/**
 * Carga de equipos por Excel: lo mismo que la pantalla Entrada de Equipos, muchos a la vez. Cada renglón se guarda con
 * {@link EquipoDao#registrar} (lote, movimiento 49, tb_compra y tb_equipo) dentro de una sola transacción; los renglones
 * con la misma orden de compra y remisión comparten documento (un acuse y sus marbetes en Reimpresión de compras).
 */
public final class CargaEquipos implements CargaArchivo {

    static final String[] COLUMNAS = {"Clave", "Marca", "Proyecto", "Origen", "Proveedor", "Ubicación",
            "No. de serie o lote", "Código de barras", "Modelo", "Categoría", "Condición", "Costo", "Fecha de compra",
            "Garantía hasta", "Accesorios", "Notas", "No. de tarima", "Tarimas completas", "Cajas por tarima",
            "Piezas por caja", "Tarimas incompletas", "Cajas incompletas", "Resto", "Orden de compra", "Remisión o factura"};
    private static final int OBLIGATORIAS = 6;
    private static final DateTimeFormatter DMA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /** Renglón ya validado (serializable: la revisión se guarda en la sesión). */
    record Renglon(int numero, String clave, int marca, Integer modelo, String serie, String cb, String categoria,
                   BigDecimal costo, String condicion, LocalDate compra, LocalDate garantia, String accesorios, String notas,
                   int proyecto, int origen, int proveedor, String ubicacion, String tarima, String remision, String oc,
                   int tc, int cxt, int pxc, int ti, int ci, int resto) implements Serializable {

        EquipoDao.Empaque empaque() {
            return new EquipoDao.Empaque(tc, cxt, pxc, ti, ci, resto);
        }

        EquipoDao.Equipo equipo() {
            return new EquipoDao.Equipo(clave, marca, null, modelo, null, serie, cb, categoria, costo, condicion, compra,
                    garantia, accesorios, notas, proyecto, origen, proveedor, ubicacion, tarima, remision, oc, empaque());
        }
    }

    @Override
    public String nombre() {
        return "equipos";
    }

    @Override
    public String pagina() {
        return "CargaArchivoEquipos.jsp";
    }

    @Override
    public Set<Acceso> accesos() {
        return Set.of(Acceso.ALMACEN); // tb_movinv.F_User es llave de tb_usuario
    }

    @Override
    public void plantilla(Connection c, OutputStream salida) throws IOException, SQLException {
        String hoy = LocalDate.now().format(DMA);
        Excel.plantilla(salida, "Equipos", COLUMNAS, OBLIGATORIAS,
                new String[][]{
                        {"EQ0000100", "100", "1", "1", "90000", "Stgrecibo1", "SN-0001", "", "", "Monitoreo", "Nuevo", "15000.00",
                                hoy, "31/12/2028", "Cable, sensor", "", "T-1", "", "", "", "", "", "", "OC-EQ-001", "R-001"},
                        {"EQ0000100", "100", "1", "1", "90000", "Stgrecibo1", "L-2026-01", "", "", "Consumible", "Nuevo", "35.50",
                                hoy, "", "", "", "T-2", "2", "10", "4", "1", "3", "2", "OC-EQ-001", "R-001"}},
                List.of(new Excel.Bloque("Marca (columna B: número o nombre)", Consultas.lista(c, "SELECT F_ClaMar, F_DesMar FROM tb_marca ORDER BY F_DesMar")),
                        new Excel.Bloque("Proyecto (columna C)", Consultas.lista(c, "SELECT F_Id, F_DesProy FROM tb_proyectos ORDER BY F_Id")),
                        new Excel.Bloque("Origen (columna D)", Consultas.lista(c, "SELECT F_ClaOri, F_DesOri FROM tb_origen ORDER BY F_ClaOri")),
                        new Excel.Bloque("Proveedor (columna E)", Consultas.lista(c, "SELECT F_ClaProve, F_NomPro FROM tb_proveedor ORDER BY F_ClaProve")),
                        new Excel.Bloque("Ubicación de ingreso (columna F) · proyecto (- = todos)",
                                Consultas.lista(c, "SELECT DescUbi, F_ClaCli FROM tb_ubicanueva WHERE Status = 'A' ORDER BY F_ClaCli <> '-', DescUbi")),
                        new Excel.Bloque("Modelo (columna I: número o nombre, opcional)", Consultas.lista(c, "SELECT F_ClaModelo, F_DesModelo FROM tb_modelo ORDER BY F_DesModelo")),
                        new Excel.Bloque("Condición (columna K, opcional)", condiciones(c))),
                List.of("Carga de equipos SIALSS: igual que la pantalla Entrada de Equipos, un renglón por entrada. Los marcados con * son obligatorios.",
                        "Marca y Modelo: el número o el nombre exacto de la hoja Catálogos (no se dan de alta desde el archivo).",
                        "Ubicación: una de ingreso (tb_ubicanueva) general o del mismo proyecto del renglón.",
                        "Empaque (como Entrada Manual): cajas = tarimas completas × cajas por tarima + cajas incompletas; piezas = cajas × piezas por caja + resto. Sin empaque entra 1 pieza.",
                        "Con 1 pieza la columna G es el número de serie (no puede repetirse ni tener existencia); con varias es el lote. Vacía = se usa el código de barras (se genera si también va vacío).",
                        "Fechas: fecha de Excel o texto dd/mm/aaaa. La compra no puede ser futura y la garantía no puede terminar antes de la compra.",
                        "Orden de compra y remisión: vacías = EQUIPOS y S/R. Los renglones con la misma orden y remisión comparten documento: un acuse y sus marbetes en Reimpresión de compras.",
                        "Máximo " + Excel.MAXIMO_RENGLONES + " renglones. Si un renglón tiene error no se carga nada: corrija el archivo y vuelva a subirlo."));
    }

    /** Condiciones como filas (texto, vacío) para la hoja Catálogos. */
    private static List<Fila> condiciones(Connection c) throws SQLException {
        String sql = String.join(" UNION ALL ", java.util.Collections.nCopies(EquipoDao.CONDICIONES.size(), "SELECT ?, ''"));
        return Consultas.lista(c, sql, EquipoDao.CONDICIONES.toArray());
    }

    @Override
    public Revision revisar(Connection c, InputStream archivo, Usuario u) throws IOException, SQLException {
        List<Excel.Renglon> crudos;
        try {
            crudos = Excel.leer(archivo, COLUMNAS.length, Set.of(12, 13));
        } catch (Excel.Ilegible e) {
            return Revision.conError(e.getMessage());
        }
        Map<String, String> insumos = mapa(c, "SELECT F_ClaPro, F_DesPro FROM tb_medica");
        Map<String, String> marcas = new HashMap<>();
        Map<String, String> marcaPorNombre = new HashMap<>();
        for (Fila f : Consultas.lista(c, "SELECT F_ClaMar, F_DesMar FROM tb_marca")) {
            marcas.put(f.get(1), f.get(2));
            marcaPorNombre.putIfAbsent(clave(f.get(2)), f.get(1));
        }
        Map<String, String> modelos = new HashMap<>();
        Map<String, String> modeloPorNombre = new HashMap<>();
        for (Fila f : Consultas.lista(c, "SELECT F_ClaModelo, F_DesModelo FROM tb_modelo")) {
            modelos.put(f.get(1), f.get(2));
            modeloPorNombre.putIfAbsent(clave(f.get(2)), f.get(1));
        }
        Map<String, String> proyectos = mapa(c, "SELECT F_Id, F_DesProy FROM tb_proyectos");
        Set<String> origenes = mapa(c, "SELECT F_ClaOri, F_DesOri FROM tb_origen").keySet();
        Set<String> proveedores = mapa(c, "SELECT F_ClaProve, F_NomPro FROM tb_proveedor").keySet();
        Map<String, String> ubicaciones = new HashMap<>(); // DescUbi (minúsculas) -> {DescUbi, proyecto}
        Map<String, String> ubicacionReal = new HashMap<>();
        for (Fila f : Consultas.lista(c, "SELECT DescUbi, F_ClaCli FROM tb_ubicanueva WHERE Status = 'A'")) {
            ubicaciones.put(f.get(1).toLowerCase(Locale.ROOT), f.get(2) == null ? "-" : f.get(2).trim());
            ubicacionReal.put(f.get(1).toLowerCase(Locale.ROOT), f.get(1));
        }

        List<String> errores = new ArrayList<>();
        List<Renglon> renglones = new ArrayList<>();
        List<List<String>> muestra = new ArrayList<>();
        Set<String> seriesArchivo = new HashSet<>();
        Set<String> cbArchivo = new HashSet<>();
        long piezasTotal = 0;
        for (Excel.Renglon cr : crudos) {
            String[] v = cr.v();
            List<String> mal = new ArrayList<>();

            String clave = Claves.resolver(v[0], insumos);
            if (v[0].isEmpty()) {
                mal.add("falta la clave");
            } else if (clave == null) {
                mal.add("la clave " + v[0] + " no existe");
            }
            String marca = catalogo(v[1], marcas, marcaPorNombre);
            if (v[1].isEmpty()) {
                mal.add("falta la marca");
            } else if (marca == null) {
                mal.add("la marca " + v[1] + " no existe (hoja Catálogos)");
            }
            String proyecto = Excel.sinDecimalCero(v[2]);
            if (proyecto.isEmpty()) {
                mal.add("falta el proyecto");
            } else if (!proyectos.containsKey(proyecto)) {
                mal.add("el proyecto " + proyecto + " no existe");
            }
            String origen = Excel.sinDecimalCero(v[3]);
            if (origen.isEmpty()) {
                mal.add("falta el origen");
            } else if (!origenes.contains(origen)) {
                mal.add("el origen " + origen + " no existe");
            }
            String proveedor = Excel.sinDecimalCero(v[4]);
            if (proveedor.isEmpty()) {
                mal.add("falta el proveedor");
            } else if (!proveedores.contains(proveedor)) {
                mal.add("el proveedor " + proveedor + " no existe");
            }
            String ubicacion = null;
            String ubiProyecto = ubicaciones.get(v[5].toLowerCase(Locale.ROOT));
            if (v[5].isEmpty()) {
                mal.add("falta la ubicación");
            } else if (ubiProyecto == null) {
                mal.add("la ubicación " + v[5] + " no es de ingreso (tb_ubicanueva)");
            } else if (!"-".equals(ubiProyecto) && !ubiProyecto.equals(proyecto)) {
                mal.add("la ubicación " + v[5] + " es del proyecto " + ubiProyecto);
            } else {
                ubicacion = ubicacionReal.get(v[5].toLowerCase(Locale.ROOT));
            }
            String serie = texto(v[6], 150, "la serie o lote", mal);
            String cb = texto(v[7], 255, "el código de barras", mal);
            String modelo = null;
            if (!v[8].isEmpty()) {
                modelo = catalogo(v[8], modelos, modeloPorNombre);
                if (modelo == null) {
                    mal.add("el modelo " + v[8] + " no existe (hoja Catálogos)");
                }
            }
            String categoria = texto(v[9], 80, "la categoría", mal);
            String condicion = null;
            if (!v[10].isEmpty()) {
                condicion = EquipoDao.CONDICIONES.stream().filter(x -> clave(x).equals(clave(v[10]))).findFirst().orElse(null);
                if (condicion == null) {
                    mal.add("la condición " + v[10] + " no es " + String.join(", ", EquipoDao.CONDICIONES));
                }
            }
            BigDecimal costo = null;
            if (!v[11].isEmpty()) {
                try {
                    costo = new BigDecimal(v[11].replace(",", "").replace("$", "").trim());
                    if (costo.signum() < 0) {
                        mal.add("el costo no puede ser negativo");
                    }
                } catch (NumberFormatException ex) {
                    mal.add("el costo (" + v[11] + ") no es un número");
                }
            }
            LocalDate compra = Excel.fecha(v[12], "la fecha de compra", false, mal);
            LocalDate garantia = Excel.fecha(v[13], "la garantía", false, mal);
            if (compra != null && compra.isAfter(LocalDate.now())) {
                mal.add("la fecha de compra no puede ser futura");
            }
            if (compra != null && garantia != null && garantia.isBefore(compra)) {
                mal.add("la garantía termina antes de la compra");
            }
            String accesorios = texto(v[14], 500, "los accesorios", mal);
            String notas = texto(v[15], 1000, "las notas", mal);
            String tarima = texto(v[16], 100, "el número de tarima", mal);
            int tc = entero(v[17], "tarimas completas", mal), cxt = entero(v[18], "cajas por tarima", mal),
                    pxc = entero(v[19], "piezas por caja", mal), ti = entero(v[20], "tarimas incompletas", mal),
                    ci = entero(v[21], "cajas incompletas", mal), resto = entero(v[22], "resto", mal);
            String oc = texto(v[23], 40, "la orden de compra", mal);
            String remision = texto(v[24], 40, "la remisión", mal);

            EquipoDao.Empaque emp = new EquipoDao.Empaque(tc, cxt, pxc, ti, ci, resto);
            if (emp.vacio()) {
                emp = EquipoDao.Empaque.UNA_PIEZA;
            }
            if (tc > 0 && cxt == 0) {
                mal.add("faltan las cajas por tarima");
            }
            if (emp.cajas() > 0 && pxc == 0) {
                mal.add("faltan las piezas por caja");
            }
            if (ti > 0 && ci == 0 && resto == 0) {
                mal.add("faltan las cajas o el resto de las tarimas incompletas");
            }
            long piezas = ((long) tc * cxt + ci) * pxc + resto;
            if (emp == EquipoDao.Empaque.UNA_PIEZA) {
                piezas = 1;
            }
            if (piezas > 10_000_000L) {
                mal.add("pasa de 10,000,000 de piezas");
            }

            // Serie (1 pieza) y código de barras: sin repetir en el archivo ni en la base.
            if (clave != null && piezas == 1 && serie != null) {
                if (!seriesArchivo.add(clave + "|" + serie.toLowerCase(Locale.ROOT))) {
                    mal.add("la serie " + serie + " se repite en el archivo");
                } else if (!Consultas.lista(c, "SELECT 1 FROM tb_lote WHERE F_ClaPro = ? AND F_ClaLot = ? AND F_ExiLot > 0 LIMIT 1",
                        clave, serie).isEmpty()) {
                    mal.add("la serie " + serie + " ya tiene existencia con la clave " + clave);
                }
            }
            if (cb != null) {
                if (!cbArchivo.add(cb)) {
                    mal.add("el código de barras " + cb + " se repite en el archivo");
                } else if (!Consultas.lista(c, "SELECT 1 FROM tb_lote WHERE F_Cb = ? LIMIT 1", cb).isEmpty()) {
                    mal.add("el código de barras " + cb + " ya existe");
                }
            }

            if (!mal.isEmpty()) {
                errores.add("Renglón " + cr.numero() + ": " + String.join("; ", mal) + ".");
                continue;
            }
            renglones.add(new Renglon(cr.numero(), clave, Integer.parseInt(marca), modelo == null ? null : Integer.valueOf(modelo),
                    serie, cb, categoria, costo, condicion, compra, garantia, accesorios, notas, Integer.parseInt(proyecto),
                    Integer.parseInt(origen), Integer.parseInt(proveedor), ubicacion, tarima, remision, oc,
                    emp.tarimasCompletas(), emp.cajasPorTarima(), emp.piezasPorCaja(), emp.tarimasIncompletas(),
                    emp.cajasIncompletas(), emp.resto()));
            piezasTotal += piezas;
            if (muestra.size() < 200) {
                muestra.add(List.of(String.valueOf(cr.numero()), clave, insumos.get(clave),
                        serie == null ? (cb == null ? "(CB nuevo)" : cb) : serie, String.format("%,d", piezas),
                        marcas.get(marca), ubicacion, proyecto, (oc == null ? "EQUIPOS" : oc) + " / " + (remision == null ? "S/R" : remision)));
            }
        }
        if (!errores.isEmpty()) {
            return new Revision(errores, muestra, "", null);
        }
        long documentos = renglones.stream().map(CargaEquipos::grupo).distinct().count();
        String resumen = Texto.cuantos(renglones.size(), "equipo", "equipos") + ", " + Texto.cuantos(piezasTotal, "pieza", "piezas")
                + ", " + Texto.cuantos(documentos, "documento", "documentos") + " (uno por orden de compra y remisión).";
        return new Revision(List.of(), muestra, resumen, new ArrayList<>(renglones));
    }

    @Override
    @SuppressWarnings("unchecked")
    public String aplicar(Connection c, Revision r, Usuario u) throws SQLException {
        List<Renglon> renglones = (List<Renglon>) r.datos();
        EquipoDao dao = new EquipoDao(c);
        Map<String, Integer> documentos = new LinkedHashMap<>();
        long piezas = 0;
        boolean auto = c.getAutoCommit();
        c.setAutoCommit(false);
        try {
            for (Renglon x : renglones) {
                String g = grupo(x);
                Integer doc = documentos.get(g);
                if (doc == null) {
                    doc = Folios.siguienteCompra(c);
                    documentos.put(g, doc);
                }
                try {
                    piezas += dao.registrar(x.equipo(), u.usuario(), doc).piezas();
                } catch (EquipoDao.Rechazo ex) {
                    throw new SQLException("Renglón " + x.numero() + ": " + ex.getMessage());
                }
            }
            c.commit();
        } catch (SQLException | RuntimeException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(auto);
        }
        List<String> docs = new ArrayList<>();
        documentos.forEach((g, d) -> docs.add(d + " (" + g.replace("|", ", remisión ") + ")"));
        return "Se cargaron " + Texto.cuantos(renglones.size(), "equipo", "equipos") + " con " + Texto.cuantos(piezas, "pieza", "piezas")
                + ". " + (docs.size() == 1 ? "Documento " : "Documentos ") + String.join("; ", docs)
                + ". El acuse y los marbetes están en Reimpresión de compras y en Entrada de Equipos › Últimas entradas.";
    }

    private static String grupo(Renglon x) {
        return "OC " + (x.oc() == null ? "EQUIPOS" : x.oc()) + "|" + (x.remision() == null ? "S/R" : x.remision());
    }

    private static Map<String, String> mapa(Connection c, String sql) throws SQLException {
        Map<String, String> m = new HashMap<>();
        for (Fila f : Consultas.lista(c, sql)) {
            m.put(f.get(1), f.get(2));
        }
        return m;
    }

    /** Número del catálogo o nombre exacto (sin acentos ni mayúsculas); null si no existe. */
    private static String catalogo(String v, Map<String, String> porId, Map<String, String> porNombre) {
        if (v.isEmpty()) {
            return null;
        }
        String id = Excel.sinDecimalCero(v);
        return porId.containsKey(id) ? id : porNombre.get(clave(v));
    }

    private static String clave(String t) {
        return t == null ? "" : java.text.Normalizer.normalize(t, java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }

    private static String texto(String v, int maximo, String nombre, List<String> mal) {
        if (v.isEmpty()) {
            return null;
        }
        if (v.length() > maximo) {
            mal.add(nombre + " pasa de " + maximo + " caracteres");
        }
        return v;
    }

    private static int entero(String v, String nombre, List<String> mal) {
        if (v.isEmpty()) {
            return 0;
        }
        String x = Excel.sinDecimalCero(v.replace(",", ""));
        if (!x.matches("\\d{1,7}")) {
            mal.add(nombre + " (" + v + ") debe ser un entero de 0 a 9,999,999");
            return 0;
        }
        return Integer.parseInt(x);
    }
}
