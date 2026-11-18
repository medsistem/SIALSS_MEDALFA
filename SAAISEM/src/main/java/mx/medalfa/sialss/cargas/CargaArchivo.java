package mx.medalfa.sialss.cargas;

import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import mx.medalfa.sialss.seguridad.Acceso;
import mx.medalfa.sialss.seguridad.PoliticaSesion;

/**
 * Una carga por Excel con el modelo de "Carga de inventario": plantilla descargable, revisión de todo el archivo
 * sin escribir (errores por renglón y vista previa) y, solo si no hay errores, aplicación en una transacción
 * después de confirmar con la contraseña del usuario de la sesión ({@link CargaArchivoServlet}).
 */
public interface CargaArchivo {

    /**
     * Resultado de revisar. {@code datos} es lo que necesita {@link #aplicar}; null si hubo errores o si no quedó nada
     * nuevo. {@code omitidos}: renglones repetidos (ya cargados o repetidos en el archivo) que no se suben; no son error.
     */
    record Revision(List<String> errores, List<List<String>> muestra, String resumen, Serializable datos,
                    List<String> omitidos) implements Serializable {

        public Revision(List<String> errores, List<List<String>> muestra, String resumen, Serializable datos) {
            this(errores, muestra, resumen, datos, List.of());
        }

        public boolean valida() {
            return errores.isEmpty() && datos != null;
        }

        /** Todo el archivo estaba repetido: no hay error, pero tampoco nada que guardar. */
        public boolean sinNuevos() {
            return errores.isEmpty() && datos == null && !omitidos.isEmpty();
        }

        public static Revision conError(String mensaje) {
            return new Revision(List.of(mensaje), List.of(), "", null);
        }
    }

    /** Usuario de la sesión que carga. {@code usuario} es F_Usu (tb_movinv.F_User es llave de tb_usuario.F_Usu). */
    record Usuario(String usuario, int idUsuario, String perfil, Acceso acceso) {
        public static Usuario de(HttpSession s) {
            if (s == null) {
                return null;
            }
            Acceso a = PoliticaSesion.acceso(s);
            Object u = s.getAttribute(PoliticaSesion.USUARIO);
            Object id = s.getAttribute(PoliticaSesion.ID_USUARIO);
            if (a == null || u == null || !(id instanceof Integer n)) {
                return null;
            }
            return new Usuario(String.valueOf(u), n, PoliticaSesion.perfil(s), a);
        }
    }

    /** Nombre corto para mensajes y para el archivo de la plantilla ("requerimiento"). */
    String nombre();

    /** Página del menú que da permiso de usar la carga (MenuSialss.tieneOpcion). */
    String pagina();

    /** Accesos (inicio de sesión) que pueden usarla. */
    Set<Acceso> accesos();

    void plantilla(Connection c, OutputStream salida) throws IOException, SQLException;

    Revision revisar(Connection c, InputStream archivo, Usuario u) throws IOException, SQLException;

    /** Escribe todo en una transacción y regresa el mensaje para el usuario. */
    String aplicar(Connection c, Revision r, Usuario u) throws SQLException;
}
