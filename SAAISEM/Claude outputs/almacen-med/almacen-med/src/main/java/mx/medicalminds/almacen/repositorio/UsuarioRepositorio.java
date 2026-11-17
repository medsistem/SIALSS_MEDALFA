package mx.medicalminds.almacen.repositorio;

import mx.medicalminds.almacen.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Integer> {

    Optional<Usuario> findByUsuario(String usuario);

    /** Acceso correcto: sella la fecha y deja el contador de fallos en cero. */
    @Modifying
    @Query("UPDATE Usuario u SET u.ultimoAcceso = :fecha, u.intentosFallidos = 0 "
         + "WHERE u.usuario = :usuario")
    int registrarAccesoExitoso(@Param("usuario") String usuario,
                               @Param("fecha") LocalDateTime fecha);

    /**
     * Acceso fallido: suma uno al contador.
     *
     * Se hace con UPDATE y no leyendo-modificando-guardando para que dos
     * intentos simultáneos no se pisen el contador.
     */
    @Modifying
    @Query("UPDATE Usuario u SET u.intentosFallidos = u.intentosFallidos + 1 "
         + "WHERE u.usuario = :usuario")
    int sumarIntentoFallido(@Param("usuario") String usuario);

    /** Bloquea al pasarse del límite de intentos. */
    @Modifying
    @Query("UPDATE Usuario u SET u.bloqueado = true "
         + "WHERE u.usuario = :usuario AND u.intentosFallidos >= :limite")
    int bloquearSiExcede(@Param("usuario") String usuario, @Param("limite") int limite);
}
