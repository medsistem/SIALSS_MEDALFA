package mx.medalfa.sialss.dao.kardex;

import com.medalfa.saa.querys.kardexQuerys;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.medalfa.sialss.config.BaseDatos;
import mx.medalfa.sialss.dao.Consultas;
import mx.medalfa.sialss.dao.Fila;

/**
 * Consultas del Kardex por clave / por lote (antes dentro de kardex/gnrKardexClaveReload.jsp).
 * Mismas consultas y mismos usuarios de base que antes, todas con parámetros.
 */
public class KardexDao {

    /** Parámetros que manda kardex/kardex.jsp. */
    public record Filtro(String btn, String clave, String lote, String caducidad, String origen,
                         List<Integer> proyectos, String fechaInicial, String fechaFinal) {

        /** Botón "Clave" sin lote elegido: existencia y lotes de toda la clave. */
        public boolean sinLote() {
            return "Clave".equals(btn) && "-Seleccione-".equals(lote);
        }

        /** Movimientos por lote y caducidad (cualquier botón distinto de "Clave"). */
        public boolean porLote() {
            return !"Clave".equals(btn);
        }

        public boolean conFechas() {
            return fechaInicial != null && !fechaInicial.isEmpty() && fechaFinal != null && !fechaFinal.isEmpty();
        }

        public int origenNumero() {
            return Integer.parseInt(origen.trim());
        }
    }

    /** Condición de proyecto sobre tb_lote (alias l) y sus parámetros. */
    private record CondicionProyecto(String sql, List<Integer> parametros) {
    }

    /** Descripción de la clave (F_DesPro, F_NomGen), limitada a los proyectos del filtro. */
    public List<Fila> producto(Filtro f) throws SQLException {
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            String campos = camposProyecto(c, f.proyectos());
            return Consultas.lista(c, "SELECT F_DesPro, F_NomGen FROM tb_medica WHERE F_ClaPro = ? " + campos, f.clave());
        }
    }

    /** Existencia actual calculada con los movimientos (columna 2). */
    public List<Fila> existencia(Filtro f) throws SQLException {
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            if (f.sinLote()) {
                return Consultas.lista(c, "SELECT m.F_ProMov, (SUM(m.F_CantMov*m.F_SigMov)) AS existencia FROM tb_movinv AS m WHERE m.F_ProMov = ?", f.clave());
            }
            CondicionProyecto p = condicionProyecto(f.proyectos());
            String sql = "SELECT m.F_ProMov, (SUM(m.F_CantMov*m.F_SigMov)) AS existencia FROM tb_movinv AS m "
                    + "INNER JOIN (SELECT l.F_ClaPro, l.F_ClaLot, l.F_Ubica, l.F_FolLot, m.F_DesPro FROM tb_lote l "
                    + "INNER JOIN tb_ubica AS u ON u.F_ClaUbi = l.F_Ubica INNER JOIN tb_medica m ON l.F_ClaPro = m.F_ClaPro "
                    + "WHERE l.F_ClaPro = ? AND F_ClaLot = ? AND F_FecCad = ? AND l.F_Origen = ? AND F_ExiLot != 0 " + p.sql()
                    + " GROUP BY l.F_IdLote) AS lot ON lot.F_ClaPro = m.F_ProMov AND lot.F_FolLot = m.F_LotMov AND lot.F_Ubica = m.F_UbiMov";
            return Consultas.lista(c, sql, f.clave(), f.lote(), f.caducidad(), f.origen(), p.parametros());
        }
    }

    /** Lotes con existencia por ubicación (clave, lote, caducidad, ubicación, existencia, temporal). */
    public List<Fila> lotes(Filtro f) throws SQLException {
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            if (f.sinLote()) {
                return Consultas.lista(c, "SELECT m.F_ProMov AS clave, l.F_ClaLot, l.F_FecCad, m.F_UbiMov, (SUM(m.F_CantMov*m.F_SigMov)) AS existencia, "
                        + "IF(u.es_temporal = 1, 'SI', 'NO') AS esTemporal, l.F_IdLote FROM tb_movinv AS m "
                        + "INNER JOIN tb_ubica AS u ON u.F_ClaUbi = m.F_UbiMov "
                        + "INNER JOIN tb_lote l ON m.F_LotMov = l.F_FolLot AND m.F_UbiMov = l.F_Ubica AND m.F_ProMov = l.F_ClaPro "
                        + "WHERE m.F_ProMov = ? GROUP BY m.F_LotMov, m.F_UbiMov HAVING existencia <> 0", f.clave());
            }
            CondicionProyecto p = condicionProyecto(f.proyectos());
            String sql = "SELECT m.F_ProMov AS clave, l.F_ClaLot, l.F_FecCad, m.F_UbiMov, (SUM(m.F_CantMov*m.F_SigMov)) AS existencia, "
                    + "IF(u.es_temporal = 1, 'SI', 'NO') AS esTemporal, l.F_IdLote FROM tb_movinv AS m "
                    + "INNER JOIN tb_ubica AS u ON u.F_ClaUbi = m.F_UbiMov "
                    + "INNER JOIN tb_lote l ON m.F_LotMov = l.F_FolLot AND m.F_UbiMov = l.F_Ubica AND m.F_ProMov = l.F_ClaPro "
                    + "AND l.F_ClaPro = ? AND l.F_ClaLot = ? AND l.F_FecCad = ? AND l.F_Origen = ? AND l.F_ExiLot <> 0 " + p.sql()
                    + " WHERE m.F_ProMov = ? GROUP BY m.F_LotMov, m.F_UbiMov, l.F_IdLote HAVING existencia <> 0";
            return Consultas.lista(c, sql, f.clave(), f.lote(), f.caducidad(), f.origen(), p.parametros(), f.clave());
        }
    }

    /** Ingresos y egresos (consultas de kardexQuerys, usuario de almacén). */
    public List<Fila> movimientos(Filtro f) throws SQLException {
        String fechas = f.conFechas() ? "AND m.F_FecMov BETWEEN ? AND ?" : "";
        List<Object> params = new ArrayList<>();
        String sql;
        if (!f.porLote()) {
            sql = String.format(kardexQuerys.OBTENER_KARDEX_POR_CLAVE, fechas);
            params.addAll(List.of(f.clave(), f.clave(), f.clave(), f.clave()));
        } else {
            sql = String.format(kardexQuerys.OBTENER_KARDEX_POR_LOTE_CADUCIDAD, fechas);
            params.addAll(List.of(f.lote(), f.caducidad(), f.origenNumero(), f.clave(), f.clave(), f.clave(), f.clave()));
        }
        if (f.conFechas()) {
            params.add(f.fechaInicial());
            params.add(f.fechaFinal());
        }
        try (Connection c = BaseDatos.ALMACEN.conexion()) {
            return Consultas.lista(c, sql, params.toArray());
        }
    }

    /** Redistribución en almacén (consultas de kardexQuerys, usuario de almacén). */
    public List<Fila> reubicaciones(Filtro f) throws SQLException {
        String fechas = f.conFechas() ? "AND m.F_FecMov BETWEEN ? AND ?" : "";
        List<Object> params = new ArrayList<>();
        String sql;
        if (!f.porLote()) {
            sql = String.format(kardexQuerys.OBTENER_KARDEX_REUBICACIONES_POR_CLAVE, fechas);
            params.add(f.clave());
        } else {
            sql = String.format(kardexQuerys.OBTENER_KARDEX_REUBICACIONES_POR_LOTE_Y_CADUCIDAD, fechas);
            params.addAll(List.of(f.clave(), f.lote(), f.caducidad(), f.origenNumero()));
        }
        if (f.conFechas()) {
            params.add(f.fechaInicial());
            params.add(f.fechaFinal());
        }
        try (Connection c = BaseDatos.ALMACEN.conexion()) {
            return Consultas.lista(c, sql, params.toArray());
        }
    }

    private static CondicionProyecto condicionProyecto(List<Integer> proyectos) {
        if (proyectos == null || proyectos.isEmpty()) {
            return new CondicionProyecto("", List.of());
        }
        return new CondicionProyecto(" AND l.F_Proyecto IN (" + Consultas.marcas(proyectos.size()) + ") ", proyectos);
    }

    /**
     * " AND ( F_ProIsem= 1 OR ... ) " con las columnas de tb_medica que marcan cada proyecto
     * (tb_proyectos.F_Campo). Los nombres de columna vienen de la base, no del usuario, y aun así
     * se validan antes de ponerlos en el SQL.
     */
    private static String camposProyecto(Connection c, List<Integer> proyectos) throws SQLException {
        if (proyectos == null || proyectos.isEmpty()) {
            return "";
        }
        List<String> condiciones = new ArrayList<>();
        for (Fila fila : Consultas.lista(c, "SELECT F_Campo FROM tb_proyectos WHERE F_Id IN (" + Consultas.marcas(proyectos.size()) + ")", proyectos)) {
            String campo = fila.get(1);
            if (campo != null && campo.matches("\\w+")) {
                condiciones.add(campo + "= 1");
            }
        }
        return condiciones.isEmpty() ? "" : " AND ( " + String.join(" OR ", condiciones) + " ) ";
    }
}
