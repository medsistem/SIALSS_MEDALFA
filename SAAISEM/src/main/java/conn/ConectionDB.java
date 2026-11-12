/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package conn;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import mx.medalfa.sialss.config.BaseDatos;

/**
 * Conexión a la Base de Datos isem
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
public class ConectionDB {

//variables miembro
    private String usuario;
    private String clave;
    private String url;
    private String driverClassName;
    private Connection conn = null;
    public Statement estancia;
    public Object objeto;

    private ResultSet rset;
    public String id_medico;

    public static ArrayList<ConectionDB> instancias;

    static {
        instancias = new ArrayList<ConectionDB>();
        instancias.add(new ConectionDB());
    }

//CONSTRUCTORES
    //Constructor que toma los datos de conexion por medio de parametros
    public ConectionDB(String usuario, String clave, String url, String driverClassName) {
        this.usuario = usuario;
        this.clave = clave;
        this.url = url;
        this.driverClassName = driverClassName;
    }

    //Constructor que crea la conexion sin parametros con unos definidos en la clase
    //(meter los datos correspondientes)
    /** Usa el pool GENERAL de {@link BaseDatos} (context.xml o .env); ya no lleva datos de conexión en el código. */
    public ConectionDB() {
        this.driverClassName = null;
    }

    //metodos para recuperar los datos de conexion
    public String getClave() {
        return clave;
    }

    public String getUrl() {
        return url;
    }

    public String getUsuario() {
        return usuario;
    }

    public Connection getConn() {
        try {
            this.conn = abrir();
        } catch (Exception err) {
            err.printStackTrace();
        }
        return conn;
    }

    /** Pool GENERAL, salvo que se hayan dado datos de conexión explícitos en el constructor. */
    private Connection abrir() throws Exception {
        if (this.driverClassName == null) {
            return BaseDatos.GENERAL.conexion();
        }
        Class.forName(this.driverClassName);
        return DriverManager.getConnection(this.url, this.usuario, this.clave);
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    //metodos para establecer los valores de conexion
    public void setClave(String clave) {
        this.clave = clave;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUsuario(String usuario) throws SQLException {
        this.usuario = usuario;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

//la conexion propiamente dicha
    public void conectar() throws SQLException {
        try {
            this.conn = abrir();
        } catch (Exception err) {
            System.out.println("Error " + err.getMessage());
        }
    }
    //Cerrar la conexion

    public void cierraConexion() throws SQLException {
        this.conn.close();
    }

//METODOS PARA TRABAJAR CON LA BASE DE DATOS
    public ResultSet consulta(String consulta) throws SQLException {
        this.estancia = (Statement) conn.createStatement();
        return this.estancia.executeQuery(consulta);
    }

    // -------------------
    public void actualizar(String actualiza) throws SQLException {
        System.out.println(actualiza);
        this.estancia = (Statement) conn.createStatement();
        estancia.executeUpdate(actualiza);
        //this.conn.commit();
    }

    public ResultSet borrar(String borra) throws SQLException {
        Statement st = (Statement) this.conn.createStatement();
        return (ResultSet) st.executeQuery(borra);
    }

    public void borrar2(String borra) throws SQLException {
        this.estancia = (Statement) conn.createStatement();
        estancia.executeUpdate(borra);
    }

    public int insertar(String inserta) throws SQLException {
        System.out.println(inserta);
        Statement st = (Statement) this.conn.createStatement();
        return st.executeUpdate(inserta);
    }
}
/**/
