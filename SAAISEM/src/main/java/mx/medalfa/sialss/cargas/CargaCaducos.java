package mx.medalfa.sialss.cargas;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mx.medalfa.sialss.dao.Consultas;
import mx.medalfa.sialss.dao.Fila;
import mx.medalfa.sialss.inventario.Folios;
import mx.medalfa.sialss.seguridad.Acceso;

/**
 * Carga de caducos devueltos por las unidades (reemplaza cargacaducos.jsp → FileUploadServletCaducos →
 * LeeExcelCaducos). Columnas: Unidad (tb_uniatn.F_ClaCli), Clave, Lote, Caducidad, Cantidad.
 * <p>
 * Entran a la ubicación {@value #UBICACION} con el concepto 30 (ENTRADA POR TRANSFERENCIA UNIDADES), como antes:
 * si el lote se le facturó a esa unidad (tb_factura) se usa su folio y sus datos (CB, marca, origen, proveedor,
 * proyecto) y, si ya está en CADUCADOSFARMACIA, se suma; si no, folio y CB nuevos con los valores de la carga
 * anterior, salvo el proveedor: 90000 SIN PROVEEDOR (el 900000000 de antes no existe y tb_lote lo exige). Un documento por unidad (tb_indice.F_IndMov, el mismo de las demás entradas) con su nota en tb_obsmov.
 * Diferencias con la carga anterior: la unidad es F_ClaCli (antes F_IdReporte, que en MDF vale 0 en todas), el
 * documento sale de F_IndMov (antes F_IndFolMov) y el INSERT a tb_lote lleva nombres de columna (el anterior
 * fallaba porque tb_lote ya tiene más columnas).
 */
public final class CargaCaducos implements CargaArchivo {

    static final String UBICACION = "CADUCADOSFARMACIA";
    static final int CONCEPTO = 30;
    /** tb_proveedor 90000 "SIN PROVEEDOR": lotes sin historial (tb_lote.F_ClaOrg es llave de tb_proveedor). */
    static final int SIN_PROVEEDOR = 90000;
    static final String[] COLUMNAS = {"Unidad", "Clave", "Lote", "Caducidad", "Cantidad"};
    private static final DateTimeFormatter DMA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    record Renglon(int numero, String unidad, String clave, String lote, LocalDate caducidad, int cantidad)
            implements Serializable {
    }

    @Override
    public String nombre() {
        return "caducos";
    }

    @Override
    public String pagina() {
        return "CargaArchivoCaducos.jsp";
    }

    @Override
    public Set<Acceso> accesos() {
        return Set.of(Acceso.ALMACEN);
    }

    @Override
    public void plantilla(Connection c, OutputStream salida) throws IOException, SQLException {
        Excel.plantilla(salida, "Caducos", COLUMNAS, 5,
                new String[][]{{"1", "200962.02", "L12345", "31/01/2026", "5"}},
                List.of(new Excel.Bloque("Unidad (columna A)", Consultas.lista(c,
                        "SELECT F_ClaCli, CONCAT(F_NomCli, IF(F_StsCli = 'A', '', ' · SUSPENDIDA')) FROM tb_uniatn ORDER BY F_ClaCli"))),
                List.of("Carga de caducos SIALSS. Un renglón por unidad, clave, lote y caducidad; todas las columnas son obligatorias.",
                        "Unidad: la clave de la unidad que devuelve (hoja Catálogos). Caducidad: fecha de Excel o texto dd/mm/aaaa.",
                        "Las piezas entran a la ubicación " + UBICACION + " (concepto 30). Si el lote se le facturó a esa unidad conserva su folio.",
                        "Un renglón con la misma unidad, clave, lote y caducidad que ya se cargó a " + UBICACION + ", o repetido en el archivo, se omite: solo entran los caducos nuevos.",
                        "Máximo " + Excel.MAXIMO_RENGLONES + " renglones. Si un renglón tiene error no se carga nada: corrija el archivo y vuelva a subirlo."));
    }

    @Override
    public Revision revisar(Connection c, InputStream archivo, Usuario u) throws IOException, SQLException {
        List<Excel.Renglon> crudos;
        try {
            crudos = Excel.leer(archivo, COLUMNAS.length, Set.of(3));
        } catch (Excel.Ilegible e) {
            return Revision.conError(e.getMessage());
        }
        if (Consultas.lista(c, "SELECT 1 FROM tb_ubica WHERE F_ClaUbi = ?", UBICACION).isEmpty()) {
            return Revision.conError("No existe la ubicación " + UBICACION + " en tb_ubica.");
        }
        Map<String, Fila> unidades = new HashMap<>();
        for (Fila f : Consultas.lista(c, "SELECT F_ClaCli, F_NomCli, F_StsCli FROM tb_uniatn")) {
            unidades.put(f.get(1), f);
        }
        Map<String, String> insumos = new HashMap<>();
        for (Fila f : Consultas.lista(c, "SELECT F_ClaPro, F_DesPro FROM tb_medica")) {
            insumos.put(f.get(1), f.get(2));
        }
        // Caducos ya cargados: unidad (tb_obsmov.F_Req del documento) | clave | lote | caducidad con movimiento 30.
        Set<String> cargados = new java.util.HashSet<>();
        for (Fila f : Consultas.lista(c, "SELECT DISTINCT o.F_Req, m.F_ProMov, l.F_ClaLot, l.F_FecCad FROM tb_movinv m "
                + "JOIN tb_lote l ON l.F_FolLot = m.F_LotMov AND l.F_Ubica = m.F_UbiMov JOIN tb_obsmov o ON o.F_IdFact = m.F_DocMov "
                + "WHERE m.F_ConMov = ? AND m.F_UbiMov = ?", CONCEPTO, UBICACION)) {
            cargados.add(llave(f.get(1), f.get(2), f.get(3), f.get(4)));
        }
        Set<String> enArchivo = new java.util.HashSet<>();
        Omitidos omitidos = new Omitidos();
        List<String> errores = new ArrayList<>();
        List<Renglon> renglones = new ArrayList<>();
        List<List<String>> muestra = new ArrayList<>();
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
            }
            String clave = Claves.resolver(v[1], insumos);
            if (v[1].isEmpty()) {
                mal.add("falta la clave");
            } else if (clave == null) {
                mal.add("la clave " + v[1] + " no existe");
            }
            String lote = v[2];
            if (lote.isEmpty()) {
                mal.add("falta el lote");
            } else if (lote.length() > mx.medalfa.sialss.config.ErrorBase.LARGO_LOTE) {
                mal.add("el lote pasa de " + mx.medalfa.sialss.config.ErrorBase.LARGO_LOTE + " caracteres");
            }
            LocalDate caducidad = Excel.fecha(v[3], "la caducidad", true, mal);
            int cantidad = Excel.cantidad(v[4], "la cantidad", mal);
            if (!mal.isEmpty()) {
                errores.add("Renglón " + cr.numero() + ": " + String.join("; ", mal) + ".");
                continue;
            }
            String llave = llave(unidad, clave, lote, caducidad.toString());
            if (cargados.contains(llave) || !enArchivo.add(llave)) {
                omitidos.agregar(cr.numero(), "unidad " + unidad + ", clave " + clave + ", lote " + lote + ", caducidad " + caducidad.format(DMA) + ": "
                        + (cargados.contains(llave) ? "ya se cargó a " + UBICACION : "se repite en el archivo"));
                continue;
            }
            renglones.add(new Renglon(cr.numero(), unidad, clave, lote, caducidad, cantidad));
            piezas += cantidad;
            if (muestra.size() < 200) {
                muestra.add(List.of(String.valueOf(cr.numero()), unidad, uni.get(2), clave, insumos.get(clave), lote,
                        caducidad.format(DMA), String.valueOf(cantidad)));
            }
        }
        if (!errores.isEmpty()) {
            return new Revision(errores, muestra, "", null);
        }
        if (renglones.isEmpty()) {
            return omitidos.sinNuevos(crudos.size(), "caducos nuevos");
        }
        long unidadesArchivo = renglones.stream().map(Renglon::unidad).distinct().count();
        String resumen = Texto.cuantos(renglones.size(), "renglón", "renglones") + ", "
                + Texto.cuantos(unidadesArchivo, "unidad", "unidades") + ", " + Texto.cuantos(piezas, "pieza", "piezas")
                + " a " + UBICACION + " (un documento por unidad).";
        return omitidos.valida(muestra, resumen, new Datos(new ArrayList<>(renglones), omitidos.cuantos()));
    }

    /** Lo que se guarda en la sesión entre revisar y confirmar. */
    record Datos(ArrayList<Renglon> renglones, int omitidos) implements Serializable {
    }

    private static String llave(String unidad, String clave, String lote, String caducidad) {
        return (unidad + "|" + clave + "|" + lote + "|" + caducidad).trim().toUpperCase(java.util.Locale.ROOT);
    }

    @Override
    public String aplicar(Connection c, Revision r, Usuario u) throws SQLException {
        Datos datos = (Datos) r.datos();
        List<Renglon> renglones = datos.renglones();
        // unidad → (clave|lote|caducidad → cantidad)
        Map<String, Map<String, Integer>> porUnidad = new LinkedHashMap<>();
        for (Renglon x : renglones) {
            porUnidad.computeIfAbsent(x.unidad(), k -> new LinkedHashMap<>())
                     .merge(x.clave() + "|" + x.lote() + "|" + x.caducidad(), x.cantidad(), Integer::sum);
        }
        Map<String, Fila> unidades = new HashMap<>();
        for (Fila f : Consultas.lista(c, "SELECT F_ClaCli, F_NomCli, F_IdReporte, F_Proyecto FROM tb_uniatn")) {
            unidades.put(f.get(1), f);
        }
        boolean auto = c.getAutoCommit();
        c.setAutoCommit(false);
        List<String> documentos = new ArrayList<>();
        int nuevos = 0;
        int sumados = 0;
        long piezas = 0;
        try {
            for (Map.Entry<String, Map<String, Integer>> e : porUnidad.entrySet()) {
                Fila uni = unidades.get(e.getKey());
                int documento = Folios.siguienteDocumento(c);
                documentos.add(String.valueOf(documento));
                for (Map.Entry<String, Integer> l : e.getValue().entrySet()) {
                    String[] p = l.getKey().split("\\|", 3);
                    String clave = p[0];
                    String lote = p[1];
                    LocalDate caducidad = LocalDate.parse(p[2]);
                    int cantidad = l.getValue();
                    piezas += cantidad;
                    Integer folio = folioFacturado(c, clave, lote, caducidad, e.getKey());
                    if (folio == null) {
                        folio = folioEnCaducados(c, clave, lote, caducidad); // cargado antes sin factura
                    }
                    if (folio != null && sumar(c, folio, cantidad)) {
                        sumados++;
                    } else {
                        if (folio == null) {
                            folio = Folios.siguienteLote(c);
                        }
                        insertarLote(c, folio, clave, lote, caducidad, cantidad, uni);
                        nuevos++;
                    }
                    movimiento(c, documento, clave, cantidad, folio, proveedorDe(c, folio), u.usuario());
                }
                try (PreparedStatement ps = c.prepareStatement(
                        "INSERT INTO tb_obsmov (F_IdFact, F_Obser, F_Id, F_Req, F_Tipo) VALUES (?, ?, 0, ?, 'ORDINARIO')")) {
                    ps.setInt(1, documento);
                    ps.setString(2, "( " + e.getKey() + " - " + uni.get(2) + " - " + uni.get(3) + ") Carga de caducos");
                    ps.setString(3, e.getKey());
                    ps.executeUpdate();
                }
            }
            c.commit();
        } catch (SQLException | RuntimeException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(auto);
        }
        return "Caducos cargados a " + UBICACION + ": " + Texto.cuantos(piezas, "pieza", "piezas") + ", "
                + Texto.cuantos(nuevos, "renglón nuevo", "renglones nuevos") + " y " + Texto.cuantos(sumados, "sumado", "sumados")
                + " a lotes que ya estaban ahí. " + (documentos.size() == 1 ? "Documento " : "Documentos ")
                + String.join(", ", documentos) + " (usuario " + u.usuario() + ")." + Omitidos.mensajeFinal(datos.omitidos());
    }

    /** Folio del lote que se le facturó a la unidad (como la carga anterior), o null. */
    private static Integer folioFacturado(Connection c, String clave, String lote, LocalDate caducidad, String unidad)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT l.F_FolLot FROM tb_lote l INNER JOIN tb_factura f ON l.F_FolLot = f.F_Lote "
                + "WHERE f.F_ClaPro = ? AND l.F_ClaPro = ? AND l.F_ClaLot = ? AND l.F_FecCad = ? AND f.F_ClaCli = ? LIMIT 1")) {
            ps.setString(1, clave);
            ps.setString(2, clave);
            ps.setString(3, lote);
            ps.setDate(4, Date.valueOf(caducidad));
            ps.setString(5, unidad);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }

    /** Folio del mismo lote que ya está en CADUCADOSFARMACIA (de una carga anterior), o null. */
    private static Integer folioEnCaducados(Connection c, String clave, String lote, LocalDate caducidad) throws SQLException {
        List<Fila> f = Consultas.lista(c, "SELECT F_FolLot FROM tb_lote WHERE F_ClaPro = ? AND F_ClaLot = ? AND F_FecCad = ? AND F_Ubica = ? LIMIT 1",
                clave, lote, Date.valueOf(caducidad), UBICACION);
        return f.isEmpty() ? null : f.get(0).entero(1);
    }

    /** Proveedor del lote para el movimiento (tb_movinv.F_ClaProve es número). */
    private static int proveedorDe(Connection c, int folio) throws SQLException {
        List<Fila> f = Consultas.lista(c, "SELECT F_ClaPrv FROM tb_lote WHERE F_FolLot = ? LIMIT 1", folio);
        try {
            return f.isEmpty() ? SIN_PROVEEDOR : Integer.parseInt(f.get(0).get(1).trim());
        } catch (RuntimeException ex) {
            return SIN_PROVEEDOR;
        }
    }

    /** Suma al renglón del folio en CADUCADOSFARMACIA; false si el folio no está en esa ubicación. */
    private static boolean sumar(Connection c, int folio, int cantidad) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "UPDATE tb_lote SET F_ExiLot = F_ExiLot + ? WHERE F_FolLot = ? AND F_Ubica = ? LIMIT 1")) {
            ps.setInt(1, cantidad);
            ps.setInt(2, folio);
            ps.setString(3, UBICACION);
            return ps.executeUpdate() > 0;
        }
    }

    private static void insertarLote(Connection c, int folio, String clave, String lote, LocalDate caducidad,
                                     int cantidad, Fila unidad) throws SQLException {
        // Datos del lote original (si el folio ya existe en otra ubicación); si no, los de la carga anterior.
        String cb = null;
        String marca = "100";
        String origen = "1";
        String proveedor = String.valueOf(SIN_PROVEEDOR);
        String organizacion = String.valueOf(SIN_PROVEEDOR);
        String proyecto = unidad.get(4) == null || unidad.get(4).isEmpty() ? "1" : unidad.get(4);
        LocalDate fabricacion = caducidad;
        List<Fila> original = Consultas.lista(c,
                "SELECT F_Cb, F_ClaMar, F_Origen, F_ClaPrv, F_ClaOrg, F_Proyecto, F_FecFab FROM tb_lote WHERE F_FolLot = ? LIMIT 1", folio);
        boolean mismoFolio = !original.isEmpty();
        if (original.isEmpty()) {
            // Folio nuevo: si el mismo lote existe en otra ubicación se copian sus datos (no su CB, que es de ese folio).
            original = Consultas.lista(c, "SELECT NULL, F_ClaMar, F_Origen, F_ClaPrv, F_ClaOrg, F_Proyecto, F_FecFab FROM tb_lote "
                    + "WHERE F_ClaPro = ? AND F_ClaLot = ? AND F_FecCad = ? LIMIT 1", clave, lote, Date.valueOf(caducidad));
        }
        if (!original.isEmpty()) {
            Fila o = original.get(0);
            cb = o.get(1);
            marca = o.get(2);
            origen = o.get(3);
            proveedor = o.get(4);
            organizacion = o.get(5);
            proyecto = o.get(6);
            try {
                fabricacion = LocalDate.parse(o.get(7).substring(0, 10));
            } catch (RuntimeException ex) {
                fabricacion = caducidad; // sin fecha o 0000-00-00
            }
        }
        if (!mismoFolio || cb == null || cb.isEmpty()) {
            cb = Folios.siguienteCb(c);
        }
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO tb_lote (F_ClaPro, F_ClaLot, F_FecCad, F_ExiLot, F_FolLot, F_ClaOrg, F_Ubica, F_FecFab, F_Cb, "
                + "F_ClaMar, F_Origen, F_ClaPrv, F_tarima, F_Proyecto, F_observacion) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NULL, ?, ?)")) {
            ps.setString(1, clave);
            ps.setString(2, lote);
            ps.setDate(3, Date.valueOf(caducidad));
            ps.setInt(4, cantidad);
            ps.setInt(5, folio);
            ps.setString(6, organizacion);
            ps.setString(7, UBICACION);
            ps.setDate(8, Date.valueOf(fabricacion));
            ps.setString(9, cb);
            ps.setString(10, marca);
            ps.setString(11, origen);
            ps.setString(12, proveedor);
            ps.setString(13, proyecto);
            ps.setString(14, "INGRESO DE CADUCADOS");
            ps.executeUpdate();
        }
    }

    private static void movimiento(Connection c, int documento, String clave, int cantidad, int folio, int proveedor, String usuario)
            throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO tb_movinv (F_FecMov, F_DocMov, F_ConMov, F_ProMov, F_CantMov, F_CostMov, F_TotMov, F_SigMov, "
                + "F_LotMov, F_UbiMov, F_ClaProve, F_hora, F_User, F_Comentarios) "
                + "VALUES (CURDATE(), ?, ?, ?, ?, 0, 0, 1, ?, ?, ?, CURTIME(), ?, 'INGRESO DE CADUCADOS')")) {
            ps.setString(1, String.valueOf(documento));
            ps.setInt(2, CONCEPTO);
            ps.setString(3, clave);
            ps.setInt(4, cantidad);
            ps.setInt(5, folio);
            ps.setString(6, UBICACION);
            ps.setInt(7, proveedor);
            ps.setString(8, usuario);
            ps.executeUpdate();
        }
    }
}
