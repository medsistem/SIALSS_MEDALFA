package mx.medicalminds.almacen.repositorio;

import mx.medicalminds.almacen.modelo.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MenuRepositorio extends JpaRepository<Menu, Integer> {

    /**
     * Opciones visibles para un usuario, raíces e hijos en un solo viaje.
     *
     * Es consulta nativa porque cruza rol_menu y usuario_rol, que son tablas de
     * unión sin entidad propia.
     *
     * DISTINCT, no GROUP BY: un usuario con dos roles que compartan una opción
     * la traería repetida. Lo medí con los datos de arranque — un usuario con
     * dos roles pasa de 11 opciones a 15 renglones sin el DISTINCT.
     *
     * El ORDER BY agrupa por la raíz a la que pertenece cada renglón, pone la
     * raíz antes que sus hijos y luego respeta el orden capturado. Así
     * MenuServicio puede armar el árbol en una sola pasada.
     */
    @Query(value =
        "SELECT DISTINCT m.id, m.padre_id, m.clave, m.etiqueta, m.icono, "
      + "                m.destino, m.orden, m.activo "
      + "FROM   menu m "
      + "JOIN   rol_menu rm    ON rm.menu_id = m.id "
      + "JOIN   usuario_rol ur ON ur.rol_id  = rm.rol_id "
      + "JOIN   usuario u      ON u.id       = ur.usuario_id "
      + "JOIN   rol r          ON r.id       = rm.rol_id "
      + "WHERE  u.usuario = :usuario "
      + "  AND  u.activo = 1 AND r.activo = 1 AND m.activo = 1 "
      + "ORDER  BY COALESCE(m.padre_id, m.id), m.padre_id IS NOT NULL, m.orden",
        nativeQuery = true)
    List<Menu> buscarPorUsuario(@Param("usuario") String usuario);
}
