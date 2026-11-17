package mx.medicalminds.almacen.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Cuenta de acceso.
 *
 * La contraseña nunca se guarda ni se maneja en claro: solo viaja el hash
 * BCrypt, y solo en una dirección. No hay método que devuelva la contraseña.
 */
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario", nullable = false, length = 50, unique = true)
    private String usuario;

    @Column(name = "clave_hash", nullable = false, length = 100)
    private String claveHash;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "correo", length = 150)
    private String correo;

    @Column(name = "activo", nullable = false)
    private boolean activo = true;

    @Column(name = "bloqueado", nullable = false)
    private boolean bloqueado = false;

    @Column(name = "forzar_cambio_clave", nullable = false)
    private boolean forzarCambioClave = true;

    @Column(name = "intentos_fallidos", nullable = false)
    private int intentosFallidos = 0;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @Column(name = "fecha_alta", nullable = false)
    private LocalDateTime fechaAlta = LocalDateTime.now();

    /*
     * EAGER a propósito. Los roles se necesitan al construir el UserDetails, que
     * ocurre fuera de cualquier transacción abierta; con LAZY reventaría con
     * LazyInitializationException. Son dos o tres renglones por usuario, así que
     * el costo es despreciable.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_rol",
               joinColumns = @JoinColumn(name = "usuario_id"),
               inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Rol> roles = new HashSet<Rol>();

    public Integer getId()                   { return id; }
    public void setId(Integer id)            { this.id = id; }
    public String getUsuario()               { return usuario; }
    public void setUsuario(String u)         { this.usuario = u; }
    public String getClaveHash()             { return claveHash; }
    public void setClaveHash(String h)       { this.claveHash = h; }
    public String getNombre()                { return nombre; }
    public void setNombre(String n)          { this.nombre = n; }
    public String getCorreo()                { return correo; }
    public void setCorreo(String c)          { this.correo = c; }
    public boolean isActivo()                { return activo; }
    public void setActivo(boolean a)         { this.activo = a; }
    public boolean isBloqueado()             { return bloqueado; }
    public void setBloqueado(boolean b)      { this.bloqueado = b; }
    public boolean isForzarCambioClave()     { return forzarCambioClave; }
    public void setForzarCambioClave(boolean f) { this.forzarCambioClave = f; }
    public int getIntentosFallidos()         { return intentosFallidos; }
    public void setIntentosFallidos(int i)   { this.intentosFallidos = i; }
    public LocalDateTime getUltimoAcceso()   { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime u) { this.ultimoAcceso = u; }
    public LocalDateTime getFechaAlta()      { return fechaAlta; }
    public void setFechaAlta(LocalDateTime f) { this.fechaAlta = f; }
    public Set<Rol> getRoles()               { return roles; }
    public void setRoles(Set<Rol> r)         { this.roles = r; }

    /** Nunca incluye el hash: este texto puede acabar en una bitácora. */
    @Override
    public String toString() {
        return "Usuario{" + usuario + ", activo=" + activo + "}";
    }
}
