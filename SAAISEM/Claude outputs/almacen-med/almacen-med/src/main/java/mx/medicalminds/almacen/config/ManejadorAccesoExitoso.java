package mx.medicalminds.almacen.config;

import mx.medicalminds.almacen.repositorio.UsuarioRepositorio;
import mx.medicalminds.almacen.servicio.BitacoraServicio;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Qué pasa cuando alguien entra bien: se sella la fecha, se limpia el contador
 * de fallos, se escribe la bitácora y se manda a la página de inicio.
 */
@Component
public class ManejadorAccesoExitoso extends SimpleUrlAuthenticationSuccessHandler {

    private final UsuarioRepositorio usuarios;
    private final BitacoraServicio bitacora;

    public ManejadorAccesoExitoso(UsuarioRepositorio usuarios, BitacoraServicio bitacora) {
        this.usuarios = usuarios;
        this.bitacora = bitacora;
        setDefaultTargetUrl("/inicio.xhtml");
        /*
         * true a propósito: siempre a inicio. Sin esto, Spring Security
         * reenvía a la última dirección pedida, que después de una sesión
         * vencida suele ser una petición AJAX de JSF y deja la pantalla rota.
         */
        setAlwaysUseDefaultTargetUrl(true);
    }

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest peticion,
                                        HttpServletResponse respuesta,
                                        Authentication autenticacion)
            throws IOException, ServletException {

        String usuario = autenticacion.getName();
        usuarios.registrarAccesoExitoso(usuario, LocalDateTime.now());
        bitacora.registrar(usuario, true, null, peticion);

        super.onAuthenticationSuccess(peticion, respuesta, autenticacion);
    }
}
