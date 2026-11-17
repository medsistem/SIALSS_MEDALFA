package mx.medicalminds.almacen.repositorio;

import mx.medicalminds.almacen.modelo.BitacoraAcceso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BitacoraAccesoRepositorio extends JpaRepository<BitacoraAcceso, Long> {

    Page<BitacoraAcceso> findAllByOrderByFechaDesc(Pageable pagina);

    Page<BitacoraAcceso> findByUsuarioOrderByFechaDesc(String usuario, Pageable pagina);
}
