package mx.medalfa.sialss.seguridad;

/**
 * Cada entrada al sistema con su propia tabla de usuarios y su pantalla de inicio de sesión.
 * Los nombres de tabla son fijos (no vienen del usuario), por eso se pueden poner en el SQL.
 */
public enum Acceso {

    /** indexalmacen.jsp → servlet /login */
    ALMACEN("tb_usuario", "indexalmacen.jsp", true),
    /** indexCompras.jsp e indexMedalfa.jsp → servlet loginISEM */
    COMPRAS("tb_usuariocompra", "indexCompras.jsp", false),
    /** indexAuditoria.jsp → servlet /LoginAuditoria (la tabla no existe en sialss_mdf) */
    AUDITORIA("tb_usuarioauditoria", "indexAuditoria.jsp", false);

    public final String tabla;
    public final String paginaLogin;
    /** Solo tb_usuario lleva además el registro en tb_usuariosuspendidos y la columna F_Area. */
    public final boolean registroSuspension;

    Acceso(String tabla, String paginaLogin, boolean registroSuspension) {
        this.tabla = tabla;
        this.paginaLogin = paginaLogin;
        this.registroSuspension = registroSuspension;
    }
}
