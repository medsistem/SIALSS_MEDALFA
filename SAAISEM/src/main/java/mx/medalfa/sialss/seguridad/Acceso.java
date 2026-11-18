package mx.medalfa.sialss.seguridad;

import java.util.Set;

/**
 * Cada entrada al sistema con su propia tabla de usuarios y su pantalla de inicio de sesión.
 * Los nombres de tabla son fijos (no vienen del usuario), por eso se pueden poner en el SQL.
 */
public enum Acceso {

    /** indexalmacen.jsp → servlet /login */
    ALMACEN("tb_usuario", "indexalmacen.jsp", "main_menu.jsp", true),
    /** indexCompras.jsp e indexMedalfa.jsp → servlet loginISEM */
    COMPRAS("tb_usuariocompra", "indexCompras.jsp", "main_menuCompras.jsp", false),
    /**
     * indexAuditoria.jsp → servlet /LoginAuditoria. Usuarios de tb_usuario (la misma tabla que Almacén, así que
     * los intentos fallidos y la sesión única cuentan junto con Almacén); solo entran los perfiles
     * 28 AUDITORIA, 1 ADMINISTRADOR y 100 SUPERUSUARIO.
     */
    AUDITORIA("tb_usuario", "indexAuditoria.jsp", "main_menuAuditoria.jsp", true, "28", "1", "100");

    public final String tabla;
    public final String paginaLogin;
    /** Menú principal de este acceso (logotipo de la barra y regreso tras desbloquear). */
    public final String menu;
    /** Solo tb_usuario lleva además el registro en tb_usuariosuspendidos y la columna F_Area. */
    public final boolean registroSuspension;
    /** Perfiles (F_TipUsu) que pueden entrar; vacío = todos. */
    public final Set<String> perfiles;

    Acceso(String tabla, String paginaLogin, String menu, boolean registroSuspension, String... perfiles) {
        this.tabla = tabla;
        this.paginaLogin = paginaLogin;
        this.menu = menu;
        this.registroSuspension = registroSuspension;
        this.perfiles = Set.of(perfiles);
    }

    /** ¿Puede entrar por este acceso un usuario con este perfil? */
    public boolean permitePerfil(String perfil) {
        return perfiles.isEmpty() || (perfil != null && perfiles.contains(perfil.trim()));
    }

    /** Usuarios de tb_usuario (Almacén y Auditoría): perfiles suspendibles en "Perfiles de usuario". */
    public boolean usaTbUsuario() {
        return "tb_usuario".equals(tabla);
    }
}
