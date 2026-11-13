package mx.medalfa.sialss.menu;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import mx.medalfa.sialss.config.BaseDatos;
import mx.medalfa.sialss.dao.Consultas;
import mx.medalfa.sialss.dao.Fila;
import mx.medalfa.sialss.seguridad.Acceso;

/** Lecturas y cambios de la pantalla "Menús por Perfil y Usuario" (tablas tb_menu_*). */
public final class MenuDao {

    public record Perfil(String id, String nombre) {
    }

    public record Usuario(int id, String usuario, String nombre, String perfil, String status) {
    }

    private MenuDao() {
    }

    /** Perfiles de tb_tipusuario más los que usa el catálogo aunque no tengan nombre registrado. */
    public static List<Perfil> perfiles(Acceso acceso) throws SQLException {
        Map<String, String> nombres = new LinkedHashMap<>();
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            for (Fila f : Consultas.lista(c, "SELECT F_IdTipo, F_DesTipo FROM tb_tipusuario ORDER BY F_IdTipo")) {
                nombres.put(f.get(1), f.get(2));
            }
        }
        Set<String> usados = new TreeSet<>((a, b) -> Integer.compare(Integer.parseInt(a), Integer.parseInt(b)));
        MenuSialss.catalogo(acceso).forEach(m -> m.opciones().forEach(o -> usados.addAll(o.perfiles())));
        try {
            usuarios(acceso).forEach(u -> usados.add(u.perfil()));
        } catch (SQLException e) {
            // El acceso puede no tener tabla de usuarios (Auditoría en MDF): se listan solo los del catálogo.
        }
        List<Perfil> r = new ArrayList<>();
        for (String id : usados) {
            r.add(new Perfil(id, nombres.getOrDefault(id, "Perfil " + id + " (sin nombre en tb_tipusuario)")));
        }
        return r;
    }

    /** Usuarios del acceso (de su propia tabla: tb_usuario, tb_usuariocompra...). */
    public static List<Usuario> usuarios(Acceso acceso) throws SQLException {
        String sql = "SELECT F_IdUsu, F_Usu, TRIM(CONCAT_WS(' ', F_Nombre, F_Apellido, F_ApellidoM)) nombre, F_TipUsu, F_Status "
                + "FROM " + acceso.tabla + " ORDER BY F_Status, F_Usu";
        List<Usuario> r = new ArrayList<>();
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            for (Fila f : Consultas.lista(c, sql)) {
                r.add(new Usuario(f.entero(1), f.get(2), f.get(3), f.get(4), f.get(5)));
            }
        }
        return r;
    }

    public static Usuario usuario(Acceso acceso, int id) throws SQLException {
        return usuarios(acceso).stream().filter(u -> u.id() == id).findFirst().orElse(null);
    }

    /** Deja al perfil con exactamente estas opciones del acceso. */
    public static void guardarPerfil(Acceso acceso, int perfil, Collection<Integer> opciones, String quien, String detalle)
            throws SQLException {
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            boolean auto = c.getAutoCommit();
            c.setAutoCommit(false);
            try {
                actualizar(c, "DELETE p FROM tb_menu_perfil p JOIN tb_menu_opcion o ON o.F_IdOpcion = p.F_IdOpcion "
                        + "WHERE o.F_Acceso = ? AND p.F_TipUsu = ?", acceso.name(), perfil);
                if (!opciones.isEmpty()) {
                    actualizar(c, "INSERT INTO tb_menu_perfil (F_IdOpcion, F_TipUsu) SELECT F_IdOpcion, ? FROM tb_menu_opcion "
                            + "WHERE F_Acceso = ? AND F_IdOpcion IN (" + Consultas.marcas(opciones.size()) + ")",
                            perfil, acceso.name(), opciones);
                }
                bitacora(c, quien, acceso, "PERFIL", String.valueOf(perfil), detalle);
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(auto);
            }
        }
        MenuSialss.recargar();
    }

    /** Deja al usuario con exactamente estas excepciones (id de opción → P o B) dentro del acceso. */
    public static void guardarUsuario(Acceso acceso, int idUsuario, Map<Integer, Character> excepciones, String quien,
                                      String destino, String detalle) throws SQLException {
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            boolean auto = c.getAutoCommit();
            c.setAutoCommit(false);
            try {
                actualizar(c, "DELETE u FROM tb_menu_usuario u JOIN tb_menu_opcion o ON o.F_IdOpcion = u.F_IdOpcion "
                        + "WHERE o.F_Acceso = ? AND u.F_IdUsu = ?", acceso.name(), idUsuario);
                for (Map.Entry<Integer, Character> e : excepciones.entrySet()) {
                    actualizar(c, "INSERT INTO tb_menu_usuario (F_IdOpcion, F_IdUsu, F_Permiso, F_UsuMod, F_FecMod) "
                            + "SELECT F_IdOpcion, ?, ?, ?, NOW() FROM tb_menu_opcion WHERE F_Acceso = ? AND F_IdOpcion = ?",
                            idUsuario, String.valueOf(e.getValue()), quien, acceso.name(), e.getKey());
                }
                bitacora(c, quien, acceso, "USUARIO", destino, detalle);
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(auto);
            }
        }
        MenuSialss.recargar();
    }

    /** Últimos cambios registrados. */
    public static List<Fila> bitacora(int limite) throws SQLException {
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            return Consultas.lista(c, "SELECT DATE_FORMAT(F_FecHora, '%d/%m/%Y %H:%i') fecha, F_UsuMod, F_Acceso, F_Tipo, "
                    + "F_Destino, F_Detalle FROM tb_menu_bitacora ORDER BY F_Id DESC LIMIT ?", limite);
        }
    }

    private static void bitacora(Connection c, String quien, Acceso acceso, String tipo, String destino, String detalle)
            throws SQLException {
        actualizar(c, "INSERT INTO tb_menu_bitacora (F_FecHora, F_UsuMod, F_Acceso, F_Tipo, F_Destino, F_Detalle) "
                + "VALUES (NOW(), ?, ?, ?, ?, ?)", quien, acceso.name(), tipo, destino,
                detalle.length() > 2000 ? detalle.substring(0, 1997) + "..." : detalle);
    }

    private static int actualizar(Connection c, String sql, Object... parametros) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            Consultas.asignar(ps, parametros);
            return ps.executeUpdate();
        }
    }
}
