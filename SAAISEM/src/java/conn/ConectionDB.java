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
    public ConectionDB() {
        //poner los datos apropiados


        this.usuario = "sialss_mdf";
        this.clave = "S1a15s_MdF@2025";

//        this.url = "jdbc:mariadb://127.0.0.1:3306/sialss_mdf?autoReconnect=true";
        this.url = "jdbc:mariadb://192.168.9.180:3306/sialss_mdf?autoReconnect=true";
//        this.url = "jdbc:mariadb://192.168.0.184:3306/sialss_mdf";
        
        this.driverClassName = "org.mariadb.jdbc.Driver";
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
            Class.forName(this.driverClassName).newInstance();
            this.conn = DriverManager.getConnection(this.url, this.usuario, this.clave);
            System.out.print("");
        } catch (Exception err) {
            err.printStackTrace();
        }
        return conn;
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
            Class.forName(this.driverClassName).newInstance();
            this.conn = DriverManager.getConnection(this.url, this.usuario, this.clave);
            System.out.print("");

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
