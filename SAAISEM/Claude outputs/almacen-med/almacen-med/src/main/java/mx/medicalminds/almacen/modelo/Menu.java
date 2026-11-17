package mx.medicalminds.almacen.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Opción del menú.
 *
 * padre_id se deja como un entero simple en vez de una relación JPA a sí misma.
 * Con una @ManyToOne reflexiva habría que pelear con carga perezosa y con el
 * orden de los hijos; aquí la consulta trae el árbol completo en un viaje y
 * MenuServicio lo acomoda en memoria. Son menos de veinte renglones.
 *
 * destino en NULL significa que el nodo solo agrupa y no navega a ningún lado.
 */
@Entity
@Table(name = "menu")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "padre_id")
    private Integer padreId;

    @Column(name = "clave", nullable = false, length = 60)
    private String clave;

    @Column(name = "etiqueta", nullable = false, length = 100)
    private String etiqueta;

    @Column(name = "icono", length = 60)
    private String icono;

    @Column(name = "destino", length = 200)
    private String destino;

    @Column(name = "orden", nullable = false)
    private int orden;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    /** Se llena en memoria, no viene de la base. */
    @Transient
    private List<Menu> hijos = new ArrayList<Menu>();

    public Integer getId()               { return id; }
    public void setId(Integer id)        { this.id = id; }
    public Integer getPadreId()          { return padreId; }
    public void setPadreId(Integer p)    { this.padreId = p; }
    public String getClave()             { return clave; }
    public void setClave(String c)       { this.clave = c; }
    public String getEtiqueta()          { return etiqueta; }
    public void setEtiqueta(String e)    { this.etiqueta = e; }
    public String getIcono()             { return icono; }
    public void setIcono(String i)       { this.icono = i; }
    public String getDestino()           { return destino; }
    public void setDestino(String d)     { this.destino = d; }
    public int getOrden()                { return orden; }
    public void setOrden(int o)          { this.orden = o; }
    public boolean isActivo()            { return activo; }
    public void setActivo(boolean a)     { this.activo = a; }
    public List<Menu> getHijos()         { return hijos; }
    public void setHijos(List<Menu> h)   { this.hijos = h; }

    public boolean esRaiz()              { return padreId == null; }
    public boolean navega()              { return destino != null && !destino.trim().isEmpty(); }
}
