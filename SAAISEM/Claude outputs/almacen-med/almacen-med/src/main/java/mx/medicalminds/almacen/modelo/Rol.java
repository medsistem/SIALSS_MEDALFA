package mx.medicalminds.almacen.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Perfil de acceso: ADMINISTRADOR, RECIBO, ALMACEN, FACTURACION.
 *
 * La clave se guarda sin el prefijo ROLE_. Spring Security lo necesita, pero
 * se antepone al construir las autoridades, en DetallesUsuarioServicio; así la
 * tabla queda legible para quien la consulte desde fuera.
 */
@Entity
@Table(name = "rol")
public class Rol implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "clave", nullable = false, length = 40)
    private String clave;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    public Integer getId()            { return id; }
    public void setId(Integer id)     { this.id = id; }
    public String getClave()          { return clave; }
    public void setClave(String c)    { this.clave = c; }
    public String getNombre()         { return nombre; }
    public void setNombre(String n)   { this.nombre = n; }
    public String getDescripcion()    { return descripcion; }
    public void setDescripcion(String d) { this.descripcion = d; }
    public boolean isActivo()         { return activo; }
    public void setActivo(boolean a)  { this.activo = a; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rol)) return false;
        Rol otro = (Rol) o;
        return id != null && id.equals(otro.id);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }
}
