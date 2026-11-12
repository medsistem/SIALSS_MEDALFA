/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Respaldo;

import java.sql.*;

/**
 * Conexión a la Base de Datos SQL isem prueba
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
public class ConnectionSQLIsem {

    private String UsuarioBD;
    private String Password;
    private String Url;
    private String DriverForName;
    private Connection Conn = null;
    private Statement estancia;
    private Object Obj;

    public ConnectionSQLIsem(String Usuario, String Password, String Url, String DriverForName) {
        this.UsuarioBD = Usuario;
        this.Password = Password;
        this.Url = Url;
        this.DriverForName = DriverForName;
    }

    public ConnectionSQLIsem() {
        this.UsuarioBD = "sa";
        this.Password = "gnklmex";
        this.Url = "jdbc:sqlserver://192.168.2.170:1433;databaseName=gnklmex_prueba2015";
        //this.Url="jdbc:sqlserver://SISTEMAS-HP:1433;databaseName=gnklmex6";

        this.DriverForName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    }

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

    public String getDriverForName() {
        return DriverForName;
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
            Class.forName(this.DriverForName).newInstance();
            this.Conn = DriverManager.getConnection(this.Url, this.UsuarioBD, this.Password);
            System.out.println("Conexión Exitosa");

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
        this.estancia = (Statement) Conn.createStatement();

        return this.estancia.executeQuery(consulta);
    }

    // -------------------
    public void actualizar(String actualiza) throws SQLException {
        this.estancia = (Statement) Conn.createStatement();
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
        return st.executeUpdate(inserta);
    }

}
