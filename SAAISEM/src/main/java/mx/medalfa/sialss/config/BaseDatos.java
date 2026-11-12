package mx.medalfa.sialss.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

/**
 * Único punto de conexión a la base. Cada usuario de base de datos que ya usaba el sistema
 * es un elemento de este enum.
 *
 * Para cada uno se busca primero el recurso JNDI del context.xml ({@code java:comp/env/jdbc/...});
 * si no está declarado, se arma un pool de Tomcat con los datos del .env:
 * {@code BD_URL} y {@code <prefijo>_USUARIO} / {@code <prefijo>_CONTRASENA}.
 */
public enum BaseDatos {

    /** Usuario general (antes en ConectionDB, ConectionDBTrans y conection). */
    GENERAL("jdbc/sialss_mdf", "BD"),
    RECEPCION("jdbc/recepcion_sialss_mdf", "BD_RECEPCION"),
    AUDITORIA("jdbc/auditoria_sialss_mdf", "BD_AUDITORIA"),
    ALMACEN("jdbc/almacen_sialss_mdf", "BD_ALMACEN"),
    FACTURACION("jdbc/facturacion_sialss_mdf", "BD_FACTURACION");

    private static final Logger LOG = Logger.getLogger(BaseDatos.class.getName());
    private static final ConcurrentHashMap<BaseDatos, DataSource> pools = new ConcurrentHashMap<>();

    private final String jndi;
    private final String prefijo;

    BaseDatos(String jndi, String prefijo) {
        this.jndi = jndi;
        this.prefijo = prefijo;
    }

    public String jndi() {
        return jndi;
    }

    public DataSource dataSource() {
        return pools.computeIfAbsent(this, BaseDatos::crear);
    }

    public Connection conexion() throws SQLException {
        return dataSource().getConnection();
    }

    /** Cierra los pools creados desde el .env (los de JNDI los cierra Tomcat). */
    public static void cerrarTodo() {
        pools.values().forEach(ds -> {
            if (ds instanceof BasicDataSource b) {
                try {
                    b.close();
                } catch (SQLException e) {
                    LOG.warning("No se cerró el pool: " + e.getMessage());
                }
            }
        });
        pools.clear();
    }

    private static DataSource crear(BaseDatos bd) {
        try {
            DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/" + bd.jndi);
            LOG.info(() -> bd + ": recurso JNDI " + bd.jndi + " del context.xml");
            return ds;
        } catch (NamingException | RuntimeException sinJndi) {
            // No está en el context.xml: se arma desde el .env.
        }
        BasicDataSource b = new BasicDataSource();
        b.setDriverClassName(Entorno.get("BD_DRIVER", "org.mariadb.jdbc.Driver"));
        b.setUrl(Entorno.requerido("BD_URL"));
        b.setUsername(Entorno.requerido(bd.prefijo + "_USUARIO"));
        b.setPassword(Entorno.requerido(bd.prefijo + "_CONTRASENA"));
        b.setMaxTotal(Entorno.entero("BD_POOL_MAXIMO", 30));
        b.setMaxIdle(Entorno.entero("BD_POOL_INACTIVAS", 5));
        b.setMaxWait(java.time.Duration.ofSeconds(Entorno.entero("BD_POOL_ESPERA_SEG", 15)));
        b.setValidationQuery("SELECT 1");
        b.setTestOnBorrow(true);
        // Muchas pantallas antiguas no cierran su conexión: el pool las recupera.
        b.setRemoveAbandonedOnBorrow(true);
        b.setRemoveAbandonedOnMaintenance(true);
        b.setRemoveAbandonedTimeout(java.time.Duration.ofSeconds(Entorno.entero("BD_POOL_ABANDONO_SEG", 120)));
        b.setDurationBetweenEvictionRuns(java.time.Duration.ofSeconds(60));
        LOG.info(() -> bd + ": pool desde " + Entorno.descripcionArchivo() + " con usuario " + b.getUserName());
        return b;
    }
}
