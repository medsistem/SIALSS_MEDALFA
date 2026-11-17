package mx.medicalminds.almacen.servicio;

import mx.medicalminds.almacen.modelo.Menu;
import mx.medicalminds.almacen.repositorio.MenuRepositorio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Arma el árbol del menú para un usuario.
 *
 * El repositorio devuelve los renglones ya ordenados: cada raíz seguida de sus
 * hijos. Aquí solo se cuelgan los hijos de su padre y se descartan los grupos
 * que quedaron sin nada adentro, que de otro modo aparecerían como un menú que
 * no abre ninguna opción.
 */
@Service
public class MenuServicio {

    private final MenuRepositorio repositorio;

    public MenuServicio(MenuRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional(readOnly = true)
    public List<Menu> arbolDe(String usuario) {
        return armarArbol(repositorio.buscarPorUsuario(usuario));
    }

    /**
     * Acomoda en árbol la lista plana que devuelve la consulta.
     *
     * Separada del acceso a datos a propósito: así se puede probar con una
     * lista armada a mano, sin levantar Spring ni la base.
     */
    List<Menu> armarArbol(List<Menu> planos) {

        // LinkedHashMap para conservar el orden en que vinieron las raíces.
        Map<Integer, Menu> raices = new LinkedHashMap<Integer, Menu>();
        List<Menu> sueltos = new ArrayList<Menu>();

        for (Menu m : planos) {
            m.getHijos().clear();
            if (m.esRaiz()) {
                raices.put(m.getId(), m);
            } else {
                sueltos.add(m);
            }
        }

        for (Menu hijo : sueltos) {
            Menu padre = raices.get(hijo.getPadreId());
            if (padre != null) {
                padre.getHijos().add(hijo);
            }
            /*
             * Si el padre no vino, el permiso del grupo no está capturado. Se
             * omite en lugar de inventar una raíz: el menú debe reflejar lo que
             * dice la base, no adivinarlo. La consulta 3 de
             * sql/03_consultas_menu.sql detecta ese caso.
             */
        }

        List<Menu> arbol = new ArrayList<Menu>();
        for (Menu raiz : raices.values()) {
            // Una raíz sin hijos solo sirve si ella misma navega a algún lado.
            if (!raiz.getHijos().isEmpty() || raiz.navega()) {
                arbol.add(raiz);
            }
        }
        return arbol;
    }
}
