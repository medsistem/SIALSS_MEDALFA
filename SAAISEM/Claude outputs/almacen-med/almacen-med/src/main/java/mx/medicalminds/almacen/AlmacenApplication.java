package mx.medicalminds.almacen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de arranque.
 *
 * JoinFaces se encarga de levantar JSF (Mojarra) y PrimeFaces dentro del
 * Tomcat embebido; no hace falta web.xml ni registrar el FacesServlet a mano.
 */
@SpringBootApplication
public class AlmacenApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlmacenApplication.class, args);
    }
}
