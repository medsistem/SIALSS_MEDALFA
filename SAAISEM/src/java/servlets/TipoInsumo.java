package servlets;

import conn.ConectionDB;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/TipoInsumo")
public class TipoInsumo extends HttpServlet {

    ConectionDB con = new ConectionDB();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession sesion = request.getSession(true);

        String ingresarClave = request.getParameter("ingresarClave");
        String tipoIns = request.getParameter("tipoIns");
        String eliminarClave = request.getParameter("F_Clapro");
        String insOnco = request.getParameter("insOnco");
        
        int contadorClave = 0;

        try {

            if (request.getParameter("accion").equals("agregar")) {

                try {
                    con.conectar();
                    ResultSet rs = null;
                    switch (tipoIns) {
                        case "Controlado":
                            rs = con.consulta("SELECT COUNT(F_ClaPro) FROM tb_controlados WHERE F_ClaPro = '" + ingresarClave + "';");
                            break;
                        case "Red_Fria":
                            rs = con.consulta("SELECT COUNT(F_ClaPro) FROM tb_redfria WHERE F_ClaPro = '" + ingresarClave + "';");
                            break;
                        case "APE":
                            rs = con.consulta("SELECT COUNT(F_ClaPro) FROM tb_ape WHERE F_ClaPro = '" + ingresarClave + "';");
                            break;
                        case "Oncologico":
                            rs = con.consulta("SELECT COUNT(F_ClaPro) FROM tb_onco WHERE F_ClaPro = '" + ingresarClave + "';");
                            break;
                        default:
                            break;
                    }
                    while (rs.next()) {
                        contadorClave = rs.getInt(1);
                    }
                    if (contadorClave > 0) {
                        
                        
                        out.println("<script>alert('La clave " + ingresarClave + " ya existe en el reporte de " + tipoIns + "')</script>");
                        out.println("<script>window.location.href='reporteInsumoEspecial.jsp?insumoEspecial=" + tipoIns +"&datosInsumoEspecial_length=10'</script>");
                       
                        
                    } else {
                            String query = "SELECT F_ClaPro, SUBSTRING(F_DesPro, 1, 100) FROM tb_medica WHERE F_ProIsem = 1 AND F_ClaPro='" + ingresarClave + "';";
                        ResultSet DatosC = con.consulta(query);                        
                        String descripcion = "";
                        while (DatosC.next()) {
                            descripcion = DatosC.getString(2);
                        }
                        
                        switch (tipoIns) {
                            case "Controlado":
                                con.insertar("INSERT INTO tb_controlados VALUES('" + ingresarClave + "', '" + descripcion + "')");
                                break;
                            case "Red_Fria":
                                con.insertar("INSERT INTO tb_redfria VALUE('" + ingresarClave + "', '" + descripcion + "')");
                                break;
                            case "APE":
                                con.insertar("INSERT INTO tb_ape VALUE('" + ingresarClave + "', '" + descripcion + "')");
                                break;
                            case "Oncologico":
                                con.insertar("INSERT INTO tb_onco VALUE('" + ingresarClave + "', '" + descripcion + "', '" + insOnco + "')");
                                break;
                            default:
                                break;
                        }
                        out.println("<script>alert('La clave " + ingresarClave + " se ingreso en el catálogo " + tipoIns +" ')</script>");
                        out.println("<script>window.location='reporteInsumoEspecial.jsp?insumoEspecial=" + tipoIns +"&datosInsumoEspecial_length=10'</script>");        
                    }
                    con.cierraConexion();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

            }
            System.out.println(eliminarClave + tipoIns);
            if (request.getParameter("accion").equals("eliminar")) {
                try {
                    con.conectar();
                    switch (tipoIns) {
                        case "Controlado":
                            con.borrar2("DELETE FROM tb_controlados WHERE F_ClaPro = '" + eliminarClave + "';");
                            break;
                        case "Red_Fria":
                            con.borrar2("DELETE FROM tb_redfria WHERE F_ClaPro = '" + eliminarClave + "';");
                            break;
                        case "APE":
                            con.borrar2("DELETE FROM tb_ape WHERE F_ClaPro = '" + eliminarClave + "';");
                            break;
                        case "Oncologico":
                            con.borrar2("DELETE FROM tb_onco WHERE F_ClaPro = '" + eliminarClave + "';");
                            break;
                        default:
                            break;
                    } 
                    con.cierraConexion();
                    out.println("<script>alert('La clave " + eliminarClave + " fue eliminada del reporte de " + tipoIns +"')</script>");
                    out.println("<script>window.location='reporteInsumoEspecial.jsp?insumoEspecial=" + tipoIns +"&datosInsumoEspecial_length=10'</script>");
                   

                } catch (SQLException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }

}
    
