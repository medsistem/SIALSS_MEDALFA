package mx.medicalminds.almacen.modelo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Renglón de la bitácora de accesos.
 *
 * Guarda el nombre de usuario que se intentó, exista o no, pero nunca la
 * contraseña ni nada parecido a ella. Si alguien se equivoca de campo y teclea
 * su contraseña en el usuario, ese texto terminaría aquí; por eso el motivo del
 * rechazo se guarda como una etiqueta fija y no como el mensaje de la excepción.
 */
@Entity
@Table(name = "bitacora_acceso")
public class BitacoraAcceso implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario", nullable = false, length = 50)
    private String usuario;

    @Column(name = "exito", nullable = false)
    private boolean exito;

    @Column(name = "motivo", length = 120)
    private String motivo;

    @Column(name = "ip", length = 45)
    private String ip;

    @Column(name = "agente", length = 255)
    private String agente;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    public Long getId()                  { return id; }
    public void setId(Long id)           { this.id = id; }
    public String getUsuario()           { return usuario; }
    public void setUsuario(String u)     { this.usuario = u; }
    public boolean isExito()             { return exito; }
    public void setExito(boolean e)      { this.exito = e; }
    public String getMotivo()            { return motivo; }
    public void setMotivo(String m)      { this.motivo = m; }
    public String getIp()                { return ip; }
    public void setIp(String ip)         { this.ip = ip; }
    public String getAgente()            { return agente; }
    public void setAgente(String a)      { this.agente = a; }
    public LocalDateTime getFecha()      { return fecha; }
    public void setFecha(LocalDateTime f){ this.fecha = f; }
}
