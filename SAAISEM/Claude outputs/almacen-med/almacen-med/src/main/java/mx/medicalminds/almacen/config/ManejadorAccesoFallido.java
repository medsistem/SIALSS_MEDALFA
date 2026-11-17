package mx.medicalminds.almacen.config;

import mx.medicalminds.almacen.repositorio.UsuarioRepositorio;
import mx.medicalminds.almacen.servicio.BitacoraServicio;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Qué pasa cuando el acceso falla: se suma el intento, se bloquea la cuenta si
 * ya se pasó del límite, se escribe la bitácora y se regresa al login.
 *
 * La pantalla siempre dice lo mismo, sin importar la causa. El motivo real solo
 * queda en la bitácora. Decirle a quien tantea "esa cuenta está bloqueada" o
 * "ese usuario no existe" le confirma qué cuentas son válidas.
 */
@Component
public class ManejadorAccesoFallido extends SimpleUrlAuthenticationFailureHandler {

    private final UsuarioRepositorio usuarios;
    private final BitacoraServicio bitacora;

    public ManejadorAccesoFallido(UsuarioRepositorio usuarios, BitacoraServicio bitacora) {
        this.usuarios = usuarios;
        this.bitacora = bitacora;
        setDefaultFailureUrl("/login.xhtml?error=1");
    }

    @Override
    @Transactional
    public void onAuthenticationFailure(HttpServletRequest peticion,
                                        HttpServletResponse respuesta,
                                        AuthenticationException excepcion)
            throws IOException, ServletException {

        String usuario = peticion.getParameter("usuario");
        if (usuario == null) {
            usuario = "(sin usuario)";
        }

        /*
         * Etiqueta fija, nunca excepcion.getMessage(). El mensaje de la
         * excepción puede arrastrar lo que se capturó, y si alguien tecleó su
         * contraseña en el campo de usuario, ese texto acabaría guardado.
         */
        String motivo;
        if (excepcion instanceof LockedException) {
            motivo = "CUENTA_BLOQUEADA";
        } else if (excepcion instanceof DisabledException) {
            motivo = "CUENTA_INACTIVA";
        } else {
            motivo = "CREDENCIALES_INCORRECTAS";
            usuarios.sumarIntentoFallido(usuario);
            usuarios.bloquearSiExcede(usuario, SeguridadConfig.MAX_INTENTOS);
        }

        bitacora.registrar(usuario, false, motivo, peticion);

        super.onAuthenticationFailure(peticion, respuesta, excepcion);
    }
}
