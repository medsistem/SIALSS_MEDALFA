package mx.medicalminds.almacen.servicio;

import mx.medicalminds.almacen.modelo.BitacoraAcceso;
import mx.medicalminds.almacen.repositorio.BitacoraAccesoRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * Registra cada intento de acceso.
 *
 * REQUIRES_NEW a propósito: la bitácora debe quedar escrita aunque el intento
 * de autenticación haya fallado y lo que lo rodea se deshaga.
 */
@Service
public class BitacoraServicio {

    /** Tope de caracteres de la cabecera User-Agent que se guarda. */
    private static final int MAX_AGENTE = 255;

    private final BitacoraAccesoRepositorio repositorio;

    public BitacoraServicio(BitacoraAccesoRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String usuario, boolean exito, String motivo, HttpServletRequest peticion) {
        BitacoraAcceso b = new BitacoraAcceso();
        b.setUsuario(recortar(usuario, 50));
        b.setExito(exito);
        b.setMotivo(recortar(motivo, 120));
        b.setFecha(LocalDateTime.now());
        if (peticion != null) {
            b.setIp(ipDe(peticion));
            b.setAgente(recortar(peticion.getHeader("User-Agent"), MAX_AGENTE));
        }
        repositorio.save(b);
    }

    /**
     * IP real del cliente.
     *
     * Si hay un proxy inverso enfrente, getRemoteAddr() devuelve la IP del
     * proxy. X-Forwarded-For trae la cadena original, y el primer elemento es
     * el cliente.
     *
     * Ojo: esa cabecera la puede falsificar quien llame directo a la
     * aplicación. Sirve para una bitácora de operación, no como prueba.
     * Si el servidor no está detrás de un proxy de confianza, quita este
     * bloque y quédate con getRemoteAddr().
     */
    private String ipDe(HttpServletRequest peticion) {
        String reenviada = peticion.getHeader("X-Forwarded-For");
        if (reenviada != null && !reenviada.trim().isEmpty()) {
            int coma = reenviada.indexOf(',');
            String primera = coma > 0 ? reenviada.substring(0, coma) : reenviada;
            return recortar(primera.trim(), 45);
        }
        return recortar(peticion.getRemoteAddr(), 45);
    }

    private String recortar(String valor, int max) {
        if (valor == null) {
            return null;
        }
        return valor.length() <= max ? valor : valor.substring(0, max);
    }
}
