package mx.medicalminds.almacen.servicio;

import mx.medicalminds.almacen.modelo.Rol;
import mx.medicalminds.almacen.modelo.Usuario;
import mx.medicalminds.almacen.repositorio.UsuarioRepositorio;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Carga la cuenta desde MariaDB para que Spring Security la verifique.
 *
 * Esta clase no compara contraseñas: solo entrega el hash guardado y los
 * indicadores de la cuenta. Quien compara es el BCryptPasswordEncoder que se
 * declara en SeguridadConfig.
 */
@Service
public class DetallesUsuarioServicio implements UserDetailsService {

    private final UsuarioRepositorio repositorio;

    public DetallesUsuarioServicio(UsuarioRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usuario) throws UsernameNotFoundException {

        /*
         * El mensaje es deliberadamente igual al de una contraseña equivocada.
         * Distinguir "ese usuario no existe" de "la contraseña no es" le regala
         * a quien tantea una lista de usuarios válidos.
         */
        Usuario u = repositorio.findByUsuario(usuario)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario o contraseña incorrectos"));

        List<GrantedAuthority> autoridades = new ArrayList<GrantedAuthority>();
        for (Rol rol : u.getRoles()) {
            if (rol.isActivo()) {
                // Spring Security espera el prefijo ROLE_; la tabla lo guarda sin él.
                autoridades.add(new SimpleGrantedAuthority("ROLE_" + rol.getClave()));
            }
        }

        return org.springframework.security.core.userdetails.User
                .withUsername(u.getUsuario())
                .password(u.getClaveHash())
                .authorities(autoridades)
                .disabled(!u.isActivo())
                .accountLocked(u.isBloqueado())
                .accountExpired(false)
                .credentialsExpired(false)
                .build();
    }
}
