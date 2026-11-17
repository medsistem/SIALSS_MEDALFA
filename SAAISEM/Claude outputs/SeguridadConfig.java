package mx.medicalminds.almacen.config;

import mx.medicalminds.almacen.servicio.DetallesUsuarioServicio;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * Configuración de seguridad.
 *
 * Spring Security 5.7 (el que trae Spring Boot 2.7) ya no usa
 * WebSecurityConfigurerAdapter: se declara un SecurityFilterChain como bean.
 */
@Configuration
@EnableWebSecurity
public class SeguridadConfig {

    /** Fallos seguidos antes de bloquear la cuenta. */
    public static final int MAX_INTENTOS = 5;

    private final DetallesUsuarioServicio detallesUsuario;
    private final AuthenticationSuccessHandler manejadorExito;
    private final AuthenticationFailureHandler manejadorFallo;

    public SeguridadConfig(DetallesUsuarioServicio detallesUsuario,
                           AuthenticationSuccessHandler manejadorExito,
                           AuthenticationFailureHandler manejadorFallo) {
        this.detallesUsuario = detallesUsuario;
        this.manejadorExito  = manejadorExito;
        this.manejadorFallo  = manejadorFallo;
    }

    /**
     * BCrypt con costo 10, que es el valor por omisión.
     *
     * Los hash de sql/02_datos_iniciales.sql se generaron con ese mismo costo.
     * Subir el costo es compatible hacia atrás: BCrypt guarda el costo dentro
     * del propio hash, así que los antiguos se siguen verificando.
     */
    @Bean
    public PasswordEncoder codificadorClaves() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public DaoAuthenticationProvider proveedorAutenticacion() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(detallesUsuario);
        p.setPasswordEncoder(codificadorClaves());
        /*
         * Con esto, un usuario inexistente igual pasa por una verificación de
         * BCrypt contra un hash falso. Sin eso, la respuesta sería notoriamente
         * más rápida y permitiría averiguar qué usuarios existen midiendo el
         * tiempo.
         */
        p.setHideUserNotFoundExceptions(true);
        return p;
    }

    @Bean
    public AuthenticationManager administradorAutenticacion(AuthenticationConfiguration cfg)
            throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain cadenaFiltros(HttpSecurity http) throws Exception {

        http
            .authenticationProvider(proveedorAutenticacion())

            .authorizeRequests()
                // El login y los recursos estáticos de JSF y PrimeFaces van abiertos.
                .antMatchers("/login.xhtml").permitAll()
                .antMatchers("/javax.faces.resource/**").permitAll()
                .antMatchers("/resources/**", "/recursos/**").permitAll()
                .antMatchers("/error", "/favicon.ico").permitAll()

                // Las rutas de administración, solo para ese perfil.
                .antMatchers("/admin/**").hasRole("ADMINISTRADOR")

                /*
                 * El menú ya esconde lo que no corresponde, pero esconder no es
                 * proteger: cualquiera puede teclear la dirección. Estas reglas
                 * son las que de verdad impiden el paso.
                 */
                .antMatchers("/entradas/**").hasAnyRole("ADMINISTRADOR", "RECIBO", "ALMACEN")
                .antMatchers("/salidas/**").hasAnyRole("ADMINISTRADOR", "ALMACEN", "FACTURACION")
                .antMatchers("/impresiones/**")
                    .hasAnyRole("ADMINISTRADOR", "RECIBO", "ALMACEN", "FACTURACION")

                .anyRequest().authenticated()
            .and()

            .formLogin()
                .loginPage("/login.xhtml")
                .loginProcessingUrl("/login")
                .usernameParameter("usuario")
                .passwordParameter("clave")
                .successHandler(manejadorExito)
                .failureHandler(manejadorFallo)
                .permitAll()
            .and()

            .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login.xhtml?salida=1")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            .and()

            /*
             * CSRF queda encendido. Cada formulario de la aplicación incluye el
             * campo oculto del token: ver WEB-INF/plantillas/csrf.xhtml, que se
             * inserta dentro de cada <h:form> y del formulario del login.
             */
            .csrf()
            .and()

            .sessionManagement()
                // Renueva el identificador de sesión al entrar, contra fijación de sesión.
                .sessionFixation().migrateSession()
                .invalidSessionUrl("/login.xhtml?expirada=1")
            .and()

            .headers()
                // PrimeFaces usa un iframe para la carga de archivos; same-origin lo permite.
                .frameOptions().sameOrigin()
            .and()

            .exceptionHandling()
                .accessDeniedPage("/login.xhtml?denegado=1");

        return http.build();
    }
}
