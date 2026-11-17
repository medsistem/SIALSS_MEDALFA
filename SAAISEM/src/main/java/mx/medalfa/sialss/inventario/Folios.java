package mx.medalfa.sialss.inventario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Consecutivos que comparten todas las pantallas de entrada. Cada método toma el número y deja el
 * siguiente dentro de la transacción de la conexión (SELECT ... FOR UPDATE), para que dos capturas
 * al mismo tiempo no reciban el mismo folio.
 * <ul>
 *   <li>Folio de lote: {@code tb_indice.F_IndLote} es el SIGUIENTE a usar (igual que servlets/Nuevo.java).</li>
 *   <li>Documento de movimiento: {@code tb_indice.F_IndMov} es el siguiente {@code tb_movinv.F_DocMov}.</li>
 *   <li>Código de barras: {@code tb_gencb} guarda los generados; el nuevo es el mayor + 1 (como servlets/Altas.java).</li>
 * </ul>
 */
public final class Folios {

    private Folios() {
    }

    public static int siguienteLote(Connection c) throws SQLException {
        return siguiente(c, "F_IndLote");
    }

    public static int siguienteDocumento(Connection c) throws SQLException {
        return siguiente(c, "F_IndMov");
    }

    private static int siguiente(Connection c, String columna) throws SQLException {
        int valor;
        try (PreparedStatement ps = c.prepareStatement("SELECT " + columna + " FROM tb_indice LIMIT 1 FOR UPDATE");
             ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                throw new SQLException("tb_indice no tiene renglón.");
            }
            valor = rs.getInt(1);
        }
        try (PreparedStatement ps = c.prepareStatement("UPDATE tb_indice SET " + columna + " = ?")) {
            ps.setInt(1, valor + 1);
            ps.executeUpdate();
        }
        return valor;
    }

    /** Nuevo código de barras interno (13 dígitos en MDF) registrado en tb_gencb. */
    public static String siguienteCb(Connection c) throws SQLException {
        long ultimo;
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT MAX(CAST(F_IdCb AS UNSIGNED)) FROM tb_gencb WHERE F_IdCb REGEXP '^[0-9]+$' FOR UPDATE");
             ResultSet rs = ps.executeQuery()) {
            ultimo = rs.next() ? rs.getLong(1) : 0L;
        }
        String cb = Long.toString(ultimo + 1);
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO tb_gencb (F_IdCb, F_Nom) VALUES (?, 'MEDALFA')")) {
            ps.setString(1, cb);
            ps.executeUpdate();
        }
        return cb;
    }
}
