package mx.medalfa.sialss.dao;

import in.co.sneh.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Consultas del inicio de sesión (antes concatenadas dentro de servlets.login).
 * Todas con parámetros: el usuario y la contraseña ya no entran al texto del SQL.
 */
public class LoginDao {

    /** Intentos fallidos con los que se suspende al usuario (regla existente). */
    public static final int INTENTOS_MAXIMOS = 4;

    private final Connection c;

    public LoginDao(Connection c) {
        this.c = c;
    }

    /** Usuario activo con esa contraseña (la contraseña se guarda en MD5), o null. */
    public Usuario buscarActivo(String usuario, String contrasena) throws SQLException {
        String sql = "SELECT F_Usu, F_Nombre, F_TipUsu, F_IdUsu, F_Area, F_Proyecto "
                + "FROM tb_usuario WHERE F_Usu = ? AND F_Pass = MD5(?) AND F_Status = 'A'";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, usuario);
            ps.setString(2, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Usuario u = new Usuario();
                u.setUsuario(rs.getString("F_Usu"));
                u.setNombre(rs.getString("F_Nombre"));
                u.setTipo(rs.getString("F_TipUsu"));
                u.setId(rs.getInt("F_IdUsu"));
                u.setArea(rs.getString("F_Area"));
                u.setProyecto(rs.getString("F_Proyecto"));
                return u;
            }
        }
    }

    /** Días desde el último cambio de contraseña; null si nunca se ha registrado. */
    public Integer diasDesdeCambioContrasena(int idUsuario) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT DATEDIFF(CURDATE(), F_FecMod) FROM tb_usuario WHERE F_IdUsu = ?")) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int d = rs.getInt(1);
                    return rs.wasNull() ? null : d;
                }
                return null;
            }
        }
    }

    /** Entrada correcta: la registra y borra los intentos fallidos acumulados. */
    public void registrarEntradaCorrecta(String usuario) throws SQLException {
        registrarEntrada(usuario, 1);
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM tb_registroentradas WHERE F_Usu = ? AND F_Correcto = 0")) {
            ps.setString(1, usuario);
            ps.executeUpdate();
        }
    }

    /** Intento fallido: lo registra y devuelve cuántos lleva. */
    public int registrarIntentoFallido(String usuario) throws SQLException {
        registrarEntrada(usuario, 0);
        try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(F_Usu) FROM tb_registroentradas WHERE F_Usu = ? AND F_Correcto = 0")) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /** Suspende al usuario y abre su registro de suspensión si no tiene uno abierto. */
    public void suspender(String usuario) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE tb_usuario SET F_Status = 'S' WHERE F_Usu = ?")) {
            ps.setString(1, usuario);
            ps.executeUpdate();
        }
        boolean abierto;
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT COUNT(F_id) FROM tb_usuariosuspendidos WHERE F_FechaAct = '0000-00-00' AND F_Usuario = ?")) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                abierto = rs.next() && rs.getInt(1) > 0;
            }
        }
        if (!abierto) {
            try (PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO tb_usuariosuspendidos (F_Usuario, F_FechaSusp, F_FechaAct) VALUES (?, CURDATE(), '0000-00-00')")) {
                ps.setString(1, usuario);
                ps.executeUpdate();
            }
        }
    }

    private void registrarEntrada(String usuario, int correcto) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "INSERT INTO tb_registroentradas (F_Usu, F_FechaHora, F_Correcto) VALUES (?, NOW(), ?)")) {
            ps.setString(1, usuario);
            ps.setInt(2, correcto);
            ps.executeUpdate();
        }
    }
}
