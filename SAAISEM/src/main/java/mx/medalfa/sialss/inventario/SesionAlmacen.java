package mx.medalfa.sialss.inventario;

import jakarta.servlet.http.HttpSession;
import mx.medalfa.sialss.seguridad.Acceso;
import mx.medalfa.sialss.seguridad.PoliticaSesion;

/**
 * Usuario de la sesión para las pantallas que escriben en tb_lote y tb_movinv. Solo el acceso de
 * Almacén: tb_movinv.F_User es llave foránea de tb_usuario (los usuarios de Compras no están ahí).
 */
public record SesionAlmacen(String usuario, int idUsuario, Acceso acceso) {

    /** null si no hay sesión válida de Almacén. */
    public static SesionAlmacen de(HttpSession s) {
        if (s == null) {
            return null;
        }
        Acceso a = PoliticaSesion.acceso(s);
        Object u = s.getAttribute(PoliticaSesion.USUARIO);
        Object id = s.getAttribute(PoliticaSesion.ID_USUARIO);
        if (a != Acceso.ALMACEN || u == null || !(id instanceof Integer n)) {
            return null;
        }
        return new SesionAlmacen(String.valueOf(u), n, a);
    }
}
