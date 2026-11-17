package mx.medalfa.sialss.usuarios;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import mx.medalfa.sialss.seguridad.PoliticaSesion;

/**
 * Perfiles de usuario (tb_tipusuario): lista, alta y suspensión. El estatus vive en {@code F_Status}
 * ('A' activo, 'S' suspendido), que agrega {@code 06_perfiles_superusuario.sql}; mientras la columna no exista
 * todos los perfiles cuentan como activos y no se puede suspender.
 * <p>
 * Un perfil suspendido no puede iniciar sesión (ServicioAcceso) ni se ofrece al dar de alta usuarios.
 */
public final class PerfilDao {

    public record Perfil(int id, String nombre, boolean activo, int usuarios, int usuariosActivos) {
    }

    /** Regla de negocio rota (se muestra tal cual al usuario). */
    public static final class Rechazo extends Exception {
        public Rechazo(String mensaje) {
            super(mensaje);
        }
    }

    private PerfilDao() {
    }

    /** ¿Ya existe tb_tipusuario.F_Status? */
    public static boolean tieneEstatus(Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT 1 FROM information_schema.columns WHERE table_schema = DATABASE() "
                + "AND table_name = 'tb_tipusuario' AND column_name = 'F_Status'");
             ResultSet rs = ps.executeQuery()) {
            return rs.next();
        }
    }

    /** true si el perfil está suspendido. Sin la columna F_Status (script no aplicado) siempre false. */
    public static boolean suspendido(Connection c, String perfil) {
        if (perfil == null || !perfil.trim().matches("\\d+")) {
            return false;
        }
        try (PreparedStatement ps = c.prepareStatement("SELECT F_Status FROM tb_tipusuario WHERE F_IdTipo = ?")) {
            ps.setInt(1, Integer.parseInt(perfil.trim()));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && "S".equalsIgnoreCase(rs.getString(1));
            }
        } catch (SQLException e) {
            return false; // 42S22: columna aún no creada
        }
    }

    public static List<Perfil> lista(Connection c) throws SQLException {
        String estatus = tieneEstatus(c) ? "t.F_Status" : "'A'";
        List<Perfil> r = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(
                "SELECT t.F_IdTipo, t.F_DesTipo, " + estatus + ", COUNT(u.F_IdUsu), COALESCE(SUM(u.F_Status = 'A'), 0) "
                + "FROM tb_tipusuario t LEFT JOIN tb_usuario u ON u.F_TipUsu = t.F_IdTipo "
                + "GROUP BY t.F_IdTipo, t.F_DesTipo ORDER BY t.F_IdTipo");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                r.add(new Perfil(rs.getInt(1), rs.getString(2), !"S".equalsIgnoreCase(rs.getString(3)), rs.getInt(4), rs.getInt(5)));
            }
        }
        return r;
    }

    /** Da de alta un perfil activo; el id lo pone la base (AUTO_INCREMENT). */
    public static Perfil alta(Connection c, String nombre) throws SQLException, Rechazo {
        String limpio = nombre == null ? "" : nombre.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
        if (limpio.length() < 3 || limpio.length() > 30) {
            throw new Rechazo("El nombre del perfil debe tener de 3 a 30 caracteres.");
        }
        if (!limpio.matches("[A-ZÁÉÍÓÚÜÑ0-9 .\\-]+")) {
            throw new Rechazo("El nombre solo puede llevar letras, números, espacios, punto y guion.");
        }
        try (PreparedStatement ps = c.prepareStatement("SELECT F_IdTipo FROM tb_tipusuario WHERE UPPER(TRIM(F_DesTipo)) = ?")) {
            ps.setString(1, limpio);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    throw new Rechazo("Ya existe el perfil " + limpio + " (número " + rs.getInt(1) + ").");
                }
            }
        }
        String sql = tieneEstatus(c)
                ? "INSERT INTO tb_tipusuario (F_DesTipo, F_Status) VALUES (?, 'A')"
                : "INSERT INTO tb_tipusuario (F_DesTipo) VALUES (?)";
        try (PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, limpio);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return new Perfil(rs.getInt(1), limpio, true, 0, 0);
            }
        }
    }

    /**
     * Suspende o reactiva un perfil. No se puede suspender el SUPERUSUARIO ni el perfil con el que se está
     * trabajando (se quedaría sin acceso).
     */
    public static void cambiarEstatus(Connection c, int id, boolean activo, String perfilPropio) throws SQLException, Rechazo {
        if (!tieneEstatus(c)) {
            throw new Rechazo("Falta aplicar en la base el script 06_perfiles_superusuario.sql (columna F_Status de tb_tipusuario).");
        }
        if (!activo && PoliticaSesion.SUPERUSUARIO.equals(String.valueOf(id))) {
            throw new Rechazo("El perfil SUPERUSUARIO no se puede suspender.");
        }
        if (!activo && String.valueOf(id).equals(perfilPropio)) {
            throw new Rechazo("No puede suspender su propio perfil.");
        }
        try (PreparedStatement ps = c.prepareStatement("UPDATE tb_tipusuario SET F_Status = ? WHERE F_IdTipo = ?")) {
            ps.setString(1, activo ? "A" : "S");
            ps.setInt(2, id);
            if (ps.executeUpdate() == 0) {
                throw new Rechazo("El perfil " + id + " no existe.");
            }
        }
    }

    /**
     * Consulta (id, nombre) de los perfiles que se pueden asignar al dar de alta un usuario: activos y, si quien
     * captura no es superusuario, sin ADMINISTRADOR (1) ni SUPERUSUARIO (100). Texto SQL fijo, sin datos del usuario.
     */
    public static String consultaParaAltaDeUsuarios(Connection c, boolean superusuario) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT tu.F_IdTipo, tu.F_DesTipo FROM tb_tipusuario tu WHERE 1 = 1");
        if (!superusuario) {
            sql.append(" AND tu.F_IdTipo NOT IN (1, ").append(Integer.parseInt(PoliticaSesion.SUPERUSUARIO)).append(')');
        }
        if (tieneEstatus(c)) {
            sql.append(" AND tu.F_Status = 'A'");
        }
        return sql.append(" ORDER BY tu.F_DesTipo").toString();
    }
}
