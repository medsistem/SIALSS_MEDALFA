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

@WebServlet("/AsignarUnidadFonsabi")
public class AsignarUnidadFonsabi extends HttpServlet {

    ConectionDB con = new ConectionDB();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        HttpSession sesion = request.getSession(true);

        String folLot = request.getParameter("folLotMod");
        String unidad = request.getParameter("unidadMod");
        String clave = request.getParameter("claveMod");
        String lote = request.getParameter("loteMod");
        String nombreUnidad = request.getParameter("unidadNMod");

        int contadorClave = 0;
        String sinUnidad = "";
        try {

            if (request.getParameter("accion").equals("agregar")) {

                try {
                    con.conectar();
                    ResultSet rs = null;
                    rs = con.consulta("SELECT COUNT(*), U.F_ClaCli FROM tb_unidadfonsabi AS U WHERE U.F_FolLot = '" + folLot + "';");
                    while (rs.next()) {
                        contadorClave = rs.getInt(1);
                        sinUnidad = rs.getString(2);

                        System.out.println(contadorClave);

                    }
                    if (contadorClave > 0) {
                        if (sinUnidad.equals("S/U")) {
                            con.insertar("UPDATE tb_unidadfonsabi SET `F_ClaCli` = '"+unidad+"' WHERE `F_FolLot` = '"+folLot+"';");
                            out.println("<script>alert('La unidad " + unidad + " fue asignada con exito!! ')</script>");
                            out.println("<script>window.location='asignarUnidadFonsabi.jsp'</script>");
                        } else {

                            out.println("<script>alert('La clave: " + clave + "\\n Lote: " + lote + " \\n ya tiene asignada la unidad: " + nombreUnidad + "')</script>");
                            out.println("<script>window.location.href='asignarUnidadFonsabi.jsp'</script>");

                        }

                    } else {

                        con.insertar("INSERT INTO tb_unidadfonsabi VALUE(0,'" + unidad + "', '" + folLot + "')");

                        out.println("<script>alert('La unidad " + unidad + " fue asignada con exito!! ')</script>");
                        out.println("<script>window.location='asignarUnidadFonsabi.jsp'</script>");
                    }
                    con.cierraConexion();
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

            }

        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }
}
