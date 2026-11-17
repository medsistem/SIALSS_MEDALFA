package mx.medalfa.sialss.inventario;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mx.medalfa.sialss.dao.Consultas;
import mx.medalfa.sialss.dao.Fila;

/**
 * Entrada de equipos: el lote en tb_lote (serie o lote en F_ClaLot, marca en F_ClaMar, modelo en F_Modelo, código
 * en F_Cb, tarima en F_tarima) con la cantidad que da el empaque (tarimas, cajas y piezas, como la Entrada Manual),
 * su movimiento de entrada en tb_movinv (concepto 49 ENTRADA DE EQUIPOS), la compra en tb_compra (para el acuse y
 * los marbetes de Reimpresión de compras) y el resto de sus datos en tb_equipo (por clave + folio).
 */
public class EquipoDao {

    public static final int CONCEPTO_ENTRADA = 49;
    /** Caducidad que ya usan los equipos en MDF (tb_lote.F_FecCad no admite vacío). */
    public static final LocalDate CADUCIDAD_EQUIPO = LocalDate.of(2035, 12, 31);
    public static final List<String> CONDICIONES = List.of("Nuevo", "Usado", "Reacondicionado", "Dañado");

    private final Connection c;

    public EquipoDao(Connection c) {
        this.c = c;
    }

    /** Ubicaciones de ingreso activas de tb_ubicanueva: generales (F_ClaCli "-") o de un proyecto (F_ClaCli = id). */
    static final String UBICACIONES_INGRESO =
            "SELECT DescUbi, F_ClaCli FROM tb_ubicanueva WHERE Status = 'A' ORDER BY F_ClaCli <> '-', DescUbi";

    /** Catálogos de la pantalla. Ubicaciones: las de ingreso de tb_ubicanueva (como el Alta de Proyecto las crea). */
    public Map<String, Object> catalogos() throws SQLException {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("proyectos", pares("SELECT F_Id, F_DesProy FROM tb_proyectos ORDER BY F_Id"));
        m.put("origenes", pares("SELECT F_ClaOri, F_DesOri FROM tb_origen ORDER BY F_ClaOri"));
        m.put("proveedores", pares("SELECT F_ClaProve, F_NomPro FROM tb_proveedor ORDER BY F_NomPro"));
        m.put("marcas", pares("SELECT F_ClaMar, F_DesMar FROM tb_marca ORDER BY F_DesMar"));
        m.put("modelos", pares("SELECT F_ClaModelo, F_DesModelo FROM tb_modelo ORDER BY F_DesModelo"));
        List<Map<String, String>> ubicaciones = new ArrayList<>();
        for (Fila f : Consultas.lista(c, UBICACIONES_INGRESO)) {
            ubicaciones.add(Map.of("id", f.get(1), "nombre", f.get(1), "proyecto", f.get(2) == null ? "-" : f.get(2).trim()));
        }
        m.put("ubicaciones", ubicaciones);
        List<String> cat = new ArrayList<>();
        for (Fila f : Consultas.lista(c, "SELECT DISTINCT F_Categoria FROM tb_equipo WHERE F_Categoria <> '' ORDER BY F_Categoria")) {
            cat.add(f.get(1));
        }
        m.put("categorias", cat);
        m.put("condiciones", CONDICIONES);
        return m;
    }

    private List<Map<String, String>> pares(String sql) throws SQLException {
        List<Map<String, String>> l = new ArrayList<>();
        for (Fila f : Consultas.lista(c, sql)) {
            Map<String, String> p = new LinkedHashMap<>();
            p.put("id", f.get(1));
            p.put("nombre", f.get(2));
            l.add(p);
        }
        return l;
    }

    /** Insumos por clave o descripción (máx. 30). */
    public List<Map<String, String>> buscarInsumo(String texto) throws SQLException {
        String t = texto == null ? "" : texto.trim();
        List<Map<String, String>> l = new ArrayList<>();
        if (t.length() < 2) {
            return l;
        }
        for (Fila f : Consultas.lista(c,
                "SELECT F_ClaPro, F_DesPro FROM tb_medica WHERE F_ClaPro LIKE ? OR F_DesPro LIKE ? "
                + "ORDER BY (F_ClaPro = ?) DESC, (F_ClaPro LIKE ?) DESC, F_ClaPro LIMIT 30",
                t + "%", "%" + t + "%", t, t + "%")) {
            Map<String, String> p = new LinkedHashMap<>();
            p.put("clave", f.get(1));
            p.put("descripcion", f.get(2));
            l.add(p);
        }
        return l;
    }

    /** Últimas entradas de equipos (máx. 20). */
    public List<Map<String, String>> recientes() throws SQLException {
        List<Map<String, String>> l = new ArrayList<>();
        for (Fila f : Consultas.lista(c,
                "SELECT DATE_FORMAT(e.F_FecAlta, '%d/%m/%Y %H:%i') AS fecha, e.F_ClaPro AS clave, m.F_DesPro AS descripcion, "
                + "l.F_ClaLot AS serie, l.F_Cb AS cb, ma.F_DesMar AS marca, mo.F_DesModelo AS modelo, e.F_Categoria AS categoria, "
                + "e.F_Condicion AS condicion, e.F_Costo AS costo, l.F_Ubica AS ubicacion, e.F_FolLot AS folio, e.F_Usuario AS usuario, "
                + "COALESCE(co.F_CanCom, 1) AS piezas, co.F_ClaDoc AS compra, co.F_OrdCom AS oc, co.F_FolRemi AS remision, "
                + "DATE_FORMAT(co.F_FecApl, '%Y-%m-%d') AS fechaCompra "
                + "FROM tb_equipo e JOIN tb_lote l ON l.F_IdLote = e.F_IdLoteAlta JOIN tb_medica m ON m.F_ClaPro = e.F_ClaPro "
                + "LEFT JOIN tb_marca ma ON ma.F_ClaMar = l.F_ClaMar LEFT JOIN tb_modelo mo ON mo.F_ClaModelo = l.F_Modelo "
                + "LEFT JOIN tb_compra co ON co.F_Lote = e.F_FolLot AND co.F_ClaPro = e.F_ClaPro "
                + "ORDER BY e.F_IdEquipo DESC LIMIT 20")) {
            Map<String, String> p = new LinkedHashMap<>();
            for (String k : List.of("fecha", "clave", "descripcion", "serie", "cb", "marca", "modelo", "categoria",
                    "condicion", "costo", "ubicacion", "folio", "usuario", "piezas", "compra", "oc", "remision", "fechaCompra")) {
                p.put(k, f.get(k));
            }
            l.add(p);
        }
        return l;
    }

    /** Datos capturados. Los opcionales pueden venir null. */
    public record Equipo(String clave, Integer marca, String marcaNueva, Integer modelo, String modeloNuevo,
                         String serie, String cb, String categoria, BigDecimal costo, String condicion,
                         LocalDate fechaCompra, LocalDate garantiaHasta, String accesorios, String notas,
                         int proyecto, int origen, int proveedor, String ubicacion, String tarima, String remision,
                         String ordenCompra, Empaque empaque) {
    }

    /**
     * Empaque como en la Entrada Manual (captura.jsp, totalPiezas): cajas = tarimas completas × cajas por tarima +
     * cajas de las tarimas incompletas; piezas = cajas × piezas por caja + resto. Sin empaque = 1 pieza (resto 1).
     */
    public record Empaque(int tarimasCompletas, int cajasPorTarima, int piezasPorCaja, int tarimasIncompletas,
                          int cajasIncompletas, int resto) {
        public static final Empaque UNA_PIEZA = new Empaque(0, 0, 0, 0, 0, 1);

        public int cajasCompletas() {
            return tarimasCompletas * cajasPorTarima;
        }

        public int cajas() {
            return cajasCompletas() + cajasIncompletas;
        }

        public int piezas() {
            return cajas() * piezasPorCaja + resto;
        }

        public int tarimas() {
            int t = tarimasCompletas + tarimasIncompletas;
            return t == 0 && resto > 0 ? 1 : t;
        }

        public boolean vacio() {
            return tarimasCompletas == 0 && cajasPorTarima == 0 && piezasPorCaja == 0 && tarimasIncompletas == 0
                    && cajasIncompletas == 0 && resto == 0;
        }
    }

    /** Datos del alta. {@code compra} es tb_compra.F_ClaDoc, con F_OrdCom, F_FolRemi y F_FecApl (para los formatos). */
    public record Resultado(int folio, int documento, int idLote, String serie, String cb, int piezas, int compra,
                            String ordenCompra, String remision, String fecha) {
    }

    /** Problema que se le muestra al usuario tal cual (no es una falla del sistema). */
    public static class Rechazo extends Exception {
        public Rechazo(String mensaje) {
            super(mensaje);
        }
    }

    /** Valida y guarda en una sola transacción: lote, movimiento de entrada, compra y datos del equipo. */
    public Resultado guardar(Equipo e, String usuario) throws SQLException, Rechazo {
        boolean auto = c.getAutoCommit();
        c.setAutoCommit(false);
        try {
            Resultado r = registrar(e, usuario, null);
            c.commit();
            return r;
        } catch (SQLException | Rechazo | RuntimeException ex) {
            c.rollback();
            throw ex;
        } finally {
            c.setAutoCommit(auto);
        }
    }

    /**
     * Valida y guarda un equipo dentro de la transacción de quien llama (no hace commit ni rollback); lo usan
     * {@link #guardar} y la Carga de Equipos por Excel. {@code documento}: folio de compra ya tomado para agrupar
     * varios equipos en un mismo acuse; null = folio nuevo de tb_indice.F_IndCom.
     */
    public Resultado registrar(Equipo e, String usuario, Integer documentoDado) throws SQLException, Rechazo {
        {
            if (!existe("SELECT 1 FROM tb_medica WHERE F_ClaPro = ?", e.clave())) {
                throw new Rechazo("La clave " + e.clave() + " no existe en el catálogo de insumos.");
            }
            List<Fila> ubi = Consultas.lista(c, "SELECT F_ClaCli FROM tb_ubicanueva WHERE Status = 'A' AND DescUbi = ?", e.ubicacion());
            if (ubi.isEmpty()) {
                throw new Rechazo("La ubicación " + e.ubicacion() + " no es una ubicación de ingreso activa (tb_ubicanueva).");
            }
            String deProyecto = ubi.get(0).get(1) == null ? "-" : ubi.get(0).get(1).trim();
            if (!"-".equals(deProyecto) && !deProyecto.equals(String.valueOf(e.proyecto()))) {
                throw new Rechazo("La ubicación " + e.ubicacion() + " es del proyecto " + deProyecto + ", no del proyecto elegido.");
            }
            if (!existe("SELECT 1 FROM tb_proyectos WHERE F_Id = ?", e.proyecto())
                    || !existe("SELECT 1 FROM tb_origen WHERE F_ClaOri = ?", e.origen())
                    || !existe("SELECT 1 FROM tb_proveedor WHERE F_ClaProve = ?", e.proveedor())) {
                throw new Rechazo("Proyecto, origen o proveedor no válido.");
            }
            int marca = e.marca() != null ? e.marca() : alta("tb_marca", "F_DesMar", e.marcaNueva());
            if (e.marca() != null && !existe("SELECT 1 FROM tb_marca WHERE F_ClaMar = ?", marca)) {
                throw new Rechazo("La marca no existe.");
            }
            Integer modelo = e.modelo() != null ? e.modelo()
                    : (e.modeloNuevo() == null ? null : alta("tb_modelo", "F_DesModelo", e.modeloNuevo()));

            Empaque emp = e.empaque() == null || e.empaque().vacio() ? Empaque.UNA_PIEZA : e.empaque();
            if (emp.tarimasCompletas() > 0 && emp.cajasPorTarima() == 0) {
                throw new Rechazo("Indique cuántas cajas trae cada tarima completa.");
            }
            if (emp.cajas() > 0 && emp.piezasPorCaja() == 0) {
                throw new Rechazo("Indique cuántas piezas trae cada caja.");
            }
            if (emp.tarimasIncompletas() > 0 && emp.cajasIncompletas() == 0 && emp.resto() == 0) {
                throw new Rechazo("Indique las cajas o el resto de las tarimas incompletas.");
            }
            int piezas = emp.piezas();
            if (piezas < 1) {
                throw new Rechazo("La entrada debe tener al menos una pieza.");
            }

            String cb = e.cb() == null ? Folios.siguienteCb(c) : e.cb();
            String serie = e.serie() == null ? cb : e.serie();

            // Una pieza = número de serie: no puede repetirse con existencia. Varias piezas = lote.
            List<Fila> conSerie = piezas > 1 ? List.of() : Consultas.lista(c,
                    "SELECT F_FolLot, F_Ubica FROM tb_lote WHERE F_ClaPro = ? AND F_ClaLot = ? AND F_ExiLot > 0 LIMIT 1",
                    e.clave(), serie);
            if (!conSerie.isEmpty()) {
                throw new Rechazo("La serie " + serie + " ya tiene existencia con la clave " + e.clave()
                        + " (folio " + conSerie.get(0).get(1) + " en " + conSerie.get(0).get(2) + ").");
            }
            if (e.cb() != null) {
                List<Fila> conCb = Consultas.lista(c, "SELECT F_ClaPro, F_FolLot FROM tb_lote WHERE F_Cb = ? LIMIT 1", cb);
                if (!conCb.isEmpty()) {
                    throw new Rechazo("El código de barras " + cb + " ya está en la clave " + conCb.get(0).get(1)
                            + " (folio " + conCb.get(0).get(2) + ").");
                }
            }

            int folio = Folios.siguienteLote(c);
            // Mismo folio para el movimiento y la compra, como la Entrada Manual (tb_indice.F_IndCom).
            int documento = documentoDado != null ? documentoDado : Folios.siguienteCompra(c);
            String ordenCompra = e.ordenCompra() == null ? "EQUIPOS" : e.ordenCompra();
            String remision = e.remision() == null ? "S/R" : e.remision();
            LocalDate fabricacion = e.fechaCompra() != null ? e.fechaCompra() : LocalDate.now();
            BigDecimal costo = e.costo() != null ? e.costo() : BigDecimal.ZERO;

            int idLote;
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO tb_lote (F_ClaPro, F_ClaLot, F_FecCad, F_ExiLot, F_FolLot, F_ClaOrg, F_Ubica, F_FecFab, F_Cb, "
                    + "F_ClaMar, F_Origen, F_ClaPrv, F_tarima, F_Proyecto, F_Modelo, F_observacion) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                int i = 1;
                ps.setString(i++, e.clave());
                ps.setString(i++, serie);
                ps.setDate(i++, Date.valueOf(CADUCIDAD_EQUIPO));
                ps.setInt(i++, piezas);
                ps.setInt(i++, folio);
                ps.setInt(i++, e.proveedor());
                ps.setString(i++, e.ubicacion());
                ps.setDate(i++, Date.valueOf(fabricacion));
                ps.setString(i++, cb);
                ps.setInt(i++, marca);
                ps.setInt(i++, e.origen());
                ps.setInt(i++, e.proveedor());
                ps.setString(i++, e.tarima());
                ps.setInt(i++, e.proyecto());
                if (modelo == null) {
                    ps.setNull(i++, Types.INTEGER);
                } else {
                    ps.setInt(i++, modelo);
                }
                ps.setString(i, "Entrada de equipos");
                ps.executeUpdate();
                try (ResultSet k = ps.getGeneratedKeys()) {
                    k.next();
                    idLote = k.getInt(1);
                }
            }

            String comentario = "Entrada de equipos · OC " + ordenCompra + " · remisión " + remision;
            BigDecimal total = costo.multiply(BigDecimal.valueOf(piezas));
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO tb_movinv (F_FecMov, F_DocMov, F_ConMov, F_ProMov, F_CantMov, F_CostMov, F_TotMov, F_SigMov, "
                    + "F_LotMov, F_UbiMov, F_ClaProve, F_hora, F_User, F_Comentarios) "
                    + "VALUES (CURDATE(), ?, ?, ?, ?, ?, ?, 1, ?, ?, ?, CURTIME(), ?, ?)")) {
                Consultas.asignar(ps, String.valueOf(documento), CONCEPTO_ENTRADA, e.clave(), piezas, costo, total, folio,
                        e.ubicacion(), e.proveedor(), usuario, comentario.length() > 100 ? comentario.substring(0, 100) : comentario);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO tb_equipo (F_ClaPro, F_FolLot, F_IdLoteAlta, F_Categoria, F_Costo, F_Condicion, F_FecCompra, "
                    + "F_GarantiaHasta, F_Accesorios, F_Notas, F_Usuario) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                int i = 1;
                ps.setString(i++, e.clave());
                ps.setInt(i++, folio);
                ps.setInt(i++, idLote);
                ps.setString(i++, e.categoria());
                ps.setBigDecimal(i++, e.costo());
                ps.setString(i++, e.condicion());
                ps.setDate(i++, e.fechaCompra() == null ? null : Date.valueOf(e.fechaCompra()));
                ps.setDate(i++, e.garantiaHasta() == null ? null : Date.valueOf(e.garantiaHasta()));
                ps.setString(i++, e.accesorios());
                ps.setString(i++, e.notas());
                ps.setString(i, usuario);
                ps.executeUpdate();
            }
            compra(e, emp, documento, folio, cb, costo, ordenCompra, remision, usuario);
            String fecha = Consultas.lista(c, "SELECT CURDATE()").get(0).get(1);
            return new Resultado(folio, documento, idLote, serie, cb, piezas, documento, ordenCompra, remision, fecha);
        }
    }

    /**
     * Renglón de tb_compra con los mismos campos que la Entrada Manual (InterfaceSendetoDaoImpl.INSERTAR_COMPRA), para que
     * Reimpresión de compras imprima el acuse (reportes/compras.jasper) y los marbetes de tarimas y cajas.
     * IVA: 0 si la clave es medicamento (F_TipMed 2504), 16 % en lo demás (como servlets/Altas).
     */
    private void compra(Equipo e, Empaque emp, int folioCompra, int folioLote, String cb, BigDecimal costo,
                        String ordenCompra, String remision, String usuario) throws SQLException {
        List<Fila> tipo = Consultas.lista(c, "SELECT F_TipMed FROM tb_medica WHERE F_ClaPro = ?", e.clave());
        BigDecimal iva = !tipo.isEmpty() && "2504".equals(tipo.get(0).get(1)) ? BigDecimal.ZERO : new BigDecimal("0.16");
        BigDecimal subtotal = costo.multiply(BigDecimal.valueOf(emp.piezas()));
        BigDecimal impuesto = subtotal.multiply(iva).setScale(2, java.math.RoundingMode.HALF_UP);
        String obser = e.notas() == null ? "ENTRADA DE EQUIPOS" : e.notas();
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO tb_compra SET F_ClaDoc=?, F_ProVee=?, F_StsCom='A', F_FecApl=CURDATE(), F_ClaPro=?, F_CanCom=?, "
                + "F_Costo=?, F_Cajas=?, F_ImpTo=?, F_ComTot=?, F_FolRemi=?, F_OrdCom=?, F_ClaOrg=?, F_Cb=?, F_Hora=CURTIME(), "
                + "F_User=?, F_Obser=?, F_Web=0, F_Lote=?, F_Tarimas=?, F_TarimasI=?, F_CajasI=?, F_Pz=?, F_Resto=?, F_Proyecto=?, "
                + "F_UserIngreso=?, F_FecCaptura=CURDATE(), F_HoraCaptura=CURTIME(), F_FactorEmpaque=?, F_OrdenSuministro='', "
                + "F_IdVolumetria=0, F_CartaCanje='', F_FuenteFinanza='', F_MarcaComercial=''")) {
            Consultas.asignar(ps, folioCompra, e.proveedor(), e.clave(), emp.piezas(), costo, emp.cajasCompletas(), impuesto,
                    subtotal.add(impuesto), remision, ordenCompra, e.proveedor(), cb, usuario, obser, folioLote,
                    emp.tarimasCompletas(), emp.tarimasIncompletas(), emp.cajasIncompletas(), emp.piezasPorCaja(), emp.resto(),
                    e.proyecto(), usuario, Math.max(1, emp.piezasPorCaja()));
            ps.executeUpdate();
        }
    }

    /** Código de barras nuevo para mostrarlo antes de guardar (queda registrado en tb_gencb). */
    public String nuevoCb() throws SQLException {
        boolean auto = c.getAutoCommit();
        c.setAutoCommit(false);
        try {
            String cb = Folios.siguienteCb(c);
            c.commit();
            return cb;
        } catch (SQLException ex) {
            c.rollback();
            throw ex;
        } finally {
            c.setAutoCommit(auto);
        }
    }

    private boolean existe(String sql, Object valor) throws SQLException {
        return !Consultas.lista(c, sql, valor).isEmpty();
    }

    /** Da de alta una marca o un modelo por nombre (o devuelve el que ya existe con ese nombre). */
    private int alta(String tabla, String columna, String nombre) throws SQLException, Rechazo {
        if (nombre == null) {
            throw new Rechazo(tabla.equals("tb_marca") ? "Elija la marca." : "Elija el modelo.");
        }
        String id = tabla.equals("tb_marca") ? "F_ClaMar" : "F_ClaModelo";
        List<Fila> ya = Consultas.lista(c, "SELECT " + id + " FROM " + tabla + " WHERE UPPER(" + columna + ") = UPPER(?) LIMIT 1", nombre);
        if (!ya.isEmpty()) {
            return ya.get(0).entero(1);
        }
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO " + tabla + " (" + columna + ") VALUES (?)",
                Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nombre.toUpperCase());
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                k.next();
                return k.getInt(1);
            }
        }
    }
}
