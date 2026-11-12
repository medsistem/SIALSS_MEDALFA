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
import mx.medalfa.sialss.config.BaseDatos;

/**
 * Conexión a la Base de Datos isem
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
public class conection {

    //Varibles para la conexión
    private String UsuarioBD;
    private String Password;
    private String Url;
    private String DriverClassName;
    private Connection Conn = null;
    private Statement estancia;
    private Object Obj;

    public conection(String Usuario, String Password, String Url, String DriverClassName) {
        this.UsuarioBD = Usuario;
        this.Password = Password;
        this.Url = Url;
        this.DriverClassName = DriverClassName;
    }

    /** Usa el pool GENERAL de {@link BaseDatos} (context.xml o .env); ya no lleva datos de conexión en el código. */
    public conection() {
        this.DriverClassName = null;
    }

    // Metodo de Recuperación los datos de Conexión
    public String getUsurioBD() {
        return UsuarioBD;
    }

    public String getPassword() {
        return Password;
    }

    public String getUrl() {
        return Url;
    }

    public Connection getConn() {
        return Conn;
    }

    public String getDriverClassName() {
        return DriverClassName;
    }

    // Metodo Establecer los valores de Conexión
    public void setPassword(String Password) {
        this.Password = Password;
    }

    public void setUsuarioBD(String UsuarioBD) {
        this.UsuarioBD = UsuarioBD;
    }

    public void setUrl(String Url) {
        this.Url = Url;
    }

    public void setConn(Connection Conn) {
        this.Conn = Conn;
    }

    //Conexion
    public void conectar() throws SQLException {

        try {
            if (this.DriverClassName == null) {
                this.Conn = BaseDatos.GENERAL.conexion();
            } else {
                Class.forName(this.DriverClassName);
                this.Conn = DriverManager.getConnection(this.Url, this.UsuarioBD, this.Password);
            }
        } catch (Exception err) {
            System.out.println("Error" + err.getMessage());
        }
    }

    // cierre de conexión
    public void CierreConn() throws SQLException {
        this.Conn.close();
    }

    //METODOS PARA TRABAJAR CON LA BASE DE DATOS
    public ResultSet consulta(String consulta) throws SQLException {
        System.out.println(consulta);
        this.estancia = (Statement) Conn.createStatement();
        return this.estancia.executeQuery(consulta);
    }

    // -------------------
    public void actualizar(String actualiza) throws SQLException {
        this.estancia = (Statement) Conn.createStatement();
        System.out.println(actualiza);
        estancia.executeUpdate(actualiza);
        //this.conn.commit();
    }

    public ResultSet borrar(String borra) throws SQLException {
        Statement st = (Statement) this.Conn.createStatement();
        return (ResultSet) st.executeQuery(borra);
    }

    public void borrar2(String borra) throws SQLException {
        this.estancia = (Statement) Conn.createStatement();
        estancia.executeUpdate(borra);
    }

    public int insertar(String inserta) throws SQLException {
        Statement st = (Statement) this.Conn.createStatement();
        System.out.println(inserta);
        return st.executeUpdate(inserta);
    }
}
