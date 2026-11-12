package mx.medalfa.sialss.dao;

import in.co.sneh.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import mx.medalfa.sialss.seguridad.Acceso;

/**
 * Consultas del inicio de sesión y del desbloqueo de pantalla, para cualquiera de las tablas
 * de usuarios ({@link Acceso}). Todas con parámetros; la contraseña se guarda en MD5.
 */
public class LoginDao {

    private final Connection c;
    private final Acceso acceso;

    public LoginDao(Connection c) {
        this(c, Acceso.ALMACEN);
    }

    public LoginDao(Connection c, Acceso acceso) {
        this.c = c;
        this.acceso = acceso;
    }

    /** Usuario con esa contraseña y su estatus (A activo, S suspendido, B baja), o null si no coinciden. */
    public Usuario buscar(String usuario, String contrasena) throws SQLException {
        String area = acceso.registroSuspension ? ", F_Area" : "";
        String sql = "SELECT F_Usu, F_Nombre, F_TipUsu, F_IdUsu, F_Proyecto, F_Status" + area
                + " FROM " + acceso.tabla + " WHERE F_Usu = ? AND F_Pass = MD5(?)";
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
                u.setProyecto(rs.getString("F_Proyecto"));
                u.setStatus(rs.getString("F_Status"));
                if (acceso.registroSuspension) {
                    u.setArea(rs.getString("F_Area"));
                }
                return u;
            }
        }
    }

    /** Usuario activo con esa contraseña, o null. */
    public Usuario buscarActivo(String usuario, String contrasena) throws SQLException {
        Usuario u = buscar(usuario, contrasena);
        return u != null && "A".equals(u.getStatus()) ? u : null;
    }

    /** Para el desbloqueo de pantalla: la contraseña corresponde al usuario de la sesión y sigue activo. */
    public boolean contrasenaCorrecta(int idUsuario, String contrasena) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT 1 FROM " + acceso.tabla + " WHERE F_IdUsu = ? AND F_Pass = MD5(?) AND F_Status = 'A'")) {
            ps.setInt(1, idUsuario);
            ps.setString(2, contrasena);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Días desde el último cambio de contraseña; null si nunca se ha registrado. */
    public Integer diasDesdeCambioContrasena(int idUsuario) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT DATEDIFF(CURDATE(), F_FecMod) FROM " + acceso.tabla + " WHERE F_IdUsu = ?")) {
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

    /** Intento fallido: lo registra y devuelve cuántos lleva desde su última entrada correcta. */
    public int registrarIntentoFallido(String usuario) throws SQLException {
        registrarEntrada(usuario, 0);
        try (PreparedStatement ps = c.prepareStatement("SELECT COUNT(F_Usu) FROM tb_registroentradas WHERE F_Usu = ? AND F_Correcto = 0")) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /** true si el usuario existe en la tabla de este acceso (para no bloquear nombres inventados). */
    public boolean existe(String usuario) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT 1 FROM " + acceso.tabla + " WHERE F_Usu = ?")) {
            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    /** Bloquea (F_Status = 'S') y, en tb_usuario, abre su registro de suspensión si no tiene uno abierto. */
    public void suspender(String usuario) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE " + acceso.tabla + " SET F_Status = 'S' WHERE F_Usu = ?")) {
            ps.setString(1, usuario);
            ps.executeUpdate();
        }
        if (!acceso.registroSuspension) {
            return;
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
