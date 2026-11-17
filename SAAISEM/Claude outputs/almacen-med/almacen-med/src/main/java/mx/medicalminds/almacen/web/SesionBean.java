package mx.medicalminds.almacen.web;

import mx.medicalminds.almacen.modelo.Menu;
import mx.medicalminds.almacen.servicio.MenuServicio;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Lo que la pantalla necesita saber de quien está dentro: su nombre, sus roles
 * y su menú.
 *
 * Es un bean de Spring con alcance de sesión, no un bean de CDI. Spring Boot no
 * trae CDI, así que @Named con javax.faces.view.ViewScoped no funcionaría;
 * JoinFaces registra el SpringBeanFacesELResolver y por eso #{sesionBean} se
 * resuelve igual desde las vistas. También queda declarado en faces-config.xml.
 *
 * El menú se arma una vez al entrar y se guarda aquí. Para una pantalla que se
 * usa toda la jornada, repetir la consulta en cada navegación no aporta nada.
 * La contrapartida: si un administrador cambia permisos, el usuario los ve
 * hasta volver a entrar, o cuando alguien llama a recargarMenu().
 */
@Component("sesionBean")
@SessionScope
public class SesionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final transient MenuServicio menuServicio;

    private MenuModel modeloMenu;
    private List<Menu> arbol;

    public SesionBean(MenuServicio menuServicio) {
        this.menuServicio = menuServicio;
    }

    // ------------------------------------------------------------------
    // Quién está dentro
    // ------------------------------------------------------------------

    public String getUsuario() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        return a == null ? "" : a.getName();
    }

    /** Roles sin el prefijo ROLE_, para mostrarlos en pantalla. */
    public List<String> getRoles() {
        List<String> salida = new ArrayList<String>();
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a != null) {
            for (GrantedAuthority ga : a.getAuthorities()) {
                String r = ga.getAuthority();
                salida.add(r.startsWith("ROLE_") ? r.substring(5) : r);
            }
        }
        return salida;
    }

    public String getRolesComoTexto() {
        return String.join(", ", getRoles());
    }

    public boolean tieneRol(String clave) {
        return getRoles().contains(clave);
    }

    // ------------------------------------------------------------------
    // Menú
    // ------------------------------------------------------------------

    public MenuModel getModeloMenu() {
        if (modeloMenu == null) {
            construirMenu();
        }
        return modeloMenu;
    }

    /** Árbol crudo, por si alguna vista prefiere dibujarlo a mano. */
    public List<Menu> getArbol() {
        if (arbol == null) {
            construirMenu();
        }
        return arbol;
    }

    public void recargarMenu() {
        modeloMenu = null;
        arbol = null;
    }

    private void construirMenu() {
        String usuario = getUsuario();
        arbol = usuario.isEmpty()
              ? new ArrayList<Menu>()
              : menuServicio.arbolDe(usuario);

        DefaultMenuModel modelo = new DefaultMenuModel();
        String contexto = contextoDeLaAplicacion();

        for (Menu raiz : arbol) {
            if (raiz.getHijos().isEmpty() && raiz.navega()) {
                // Raíz que navega directo, sin desplegar nada.
                modelo.getElements().add(itemDe(raiz, contexto));
                continue;
            }

            DefaultSubMenu grupo = DefaultSubMenu.builder()
                    .label(raiz.getEtiqueta())
                    .icon(raiz.getIcono())
                    .build();

            for (Menu hijo : raiz.getHijos()) {
                grupo.getElements().add(itemDe(hijo, contexto));
            }
            modelo.getElements().add(grupo);
        }
        modeloMenu = modelo;
    }

    private DefaultMenuItem itemDe(Menu m, String contexto) {
        return DefaultMenuItem.builder()
                .value(m.getEtiqueta())
                .icon(m.getIcono())
                // El destino se guarda relativo a la raíz; aquí se le antepone
                // el contexto para que funcione aunque la aplicación no esté
                // desplegada en /.
                .url(m.navega() ? contexto + m.getDestino() : null)
                .build();
    }

    private String contextoDeLaAplicacion() {
        FacesContext fc = FacesContext.getCurrentInstance();
        return fc == null ? "" : fc.getExternalContext().getRequestContextPath();
    }
}
