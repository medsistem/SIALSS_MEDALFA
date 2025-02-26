package in.co.sneh.controller;

import LeeExcel.LeeExcelAuditoria;
import conn.ConectionDB;
import in.co.sneh.model.FileUpload;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

@WebServlet(name = "FileUploadServletAuditoria", urlPatterns = {"/FileUploadServletAuditoria"})
public class FileUploadServletAuditoria extends HttpServlet {

    LeeExcelAuditoria lee = new LeeExcelAuditoria();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String Usuario;

        boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
        if (isMultiPart) {
            ServletFileUpload upload = new ServletFileUpload();
            String Proyecto = "", DesProyecto = "", Campo = "", nombre = "";
            try {

                HttpSession sesion = request.getSession(true);
                Usuario = (String) sesion.getAttribute("Usuario");
                nombre = (String) sesion.getAttribute("nombre");

                FileItemIterator itr = upload.getItemIterator(request);
                while (itr.hasNext()) {
                    FileItemStream item = itr.next();
                    if (item.isFormField()) {
                        String fielName = item.getFieldName();
                        InputStream is = item.openStream();
                        byte[] b = new byte[is.available()];
                        is.read(b);
                        String value = new String(b);
                        response.getWriter().println(fielName + ":" + value + "<br/>");
                    } else {
                        String path = getServletContext().getRealPath("/");
                        if (FileUpload.processFile(path, item)) {
                            if (lee.obtieneArchivo(path, item.getName(), Usuario, Proyecto, Campo)) {

                                try {
                                    ConectionDB con = new ConectionDB();
                                    con.conectar();
                                   
                                    int NoRegBien = 0, NoRegMal = 0;
                                    ResultSet Consulta = con.consulta("SELECT F_Usu, COUNT(*), SUM(validaclave), SUM(validaorigen), SUM(validaestatus), SUM(validalote), SUM(validacantidad), SUM(validacaducidad) FROM tb_reporteauditoriatemp WHERE F_Usu = '" + Usuario + "'");
                                    while (Consulta.next()) {

                                        int NoReg = Consulta.getInt(2);
                                        int NoClave = Consulta.getInt(3);
                                        int NoOrigen = Consulta.getInt(4);
                                        int NoEstatus = Consulta.getInt(5);
                                        int NoLote = Consulta.getInt(6);
                                        int NoCantidad = Consulta.getInt(7);
                                        int NoCaducidad = Consulta.getInt(8);

                                        if ((NoReg == NoClave) && (NoReg == NoOrigen) && (NoReg == NoEstatus) && (NoReg == NoLote) && (NoReg == NoCantidad) && (NoReg == NoCaducidad)) {
                                            NoRegBien++;

                                            ResultSet rs = con.consulta("SELECT rat.clave, m.F_ClaProSS, SUBSTRING( m.F_DesPro, 1, 250 ), rat.lote, rat.caducidad, rat.cantidad, tb_origen.F_DesOri, tb_reporteauditoriaestatus.estatus, rat.ordencompra FROM tb_reporteauditoriatemp AS rat INNER JOIN tb_medica AS m ON rat.clave = m.F_ClaPro INNER JOIN tb_origen ON rat.origen = tb_origen.F_ClaOri INNER JOIN tb_reporteauditoriaestatus ON rat.estatus = tb_reporteauditoriaestatus.id_estatus WHERE F_uSU = '" + Usuario + "'");
                                            while (rs.next()) {
                                                String proyecto = "ISEM";
                                                String claPro = rs.getString(1);
                                                String claProSS = rs.getString(2);
                                                String descripcion = rs.getString(3);
                                                String lote = rs.getString(4);
                                                String caducidad = rs.getString(5);
                                                String cantidad = rs.getString(6);
                                                String origen = rs.getString(7);
                                                String estatus = rs.getString(8);
                                                String ordenCompra = rs.getString(9);
                                                if ( ordenCompra == null){
                                                    ordenCompra = "";
                                                }

                                                con.insertar("INSERT INTO tb_reporteauditoria VALUES ( 0, '" + proyecto + "', '" + claPro + "', '" + claProSS + "', '" + descripcion + "', '" + lote + "', '" + caducidad + "', '" + cantidad + "', '" + origen + "', '" + estatus + "', '" + ordenCompra + "')");
                                                
                                                out.println("<script>alert('El documento se cargo correctamente')</script>");
                                                out.println("<script>window.location='cargarInformacion.jsp'</script>");
                                            }
                                        } else {

                                            NoRegMal++;
                                        }
                                    }
                                    if (NoRegMal > 0) {

                                        out.println("<script>alert('El documento no se cargo')</script>");
                                    }

                                    con.cierraConexion();
                                } catch (Exception e) {
                                    Logger.getLogger("LeeExcel").log(Level.SEVERE, null, e);
                                }
                                out.println("<script>alert('No se pudo cargar, verifique las celdas')</script>");
                                out.println("<script>window.location='cargarInformacion.jsp'</script>");
                            }
                        }
                    }
                }
            } catch (FileUploadException e) {
                Logger.getLogger(FileUploadServlet.class.getName()).log(Level.SEVERE, null, e);
            } catch (SQLException ex) {
                Logger.getLogger(FileUploadServletAuditoria.class.getName()).log(Level.SEVERE, null, ex);
            }
            out.println("<script>alert('No se pudo cargar, verifique las celdas')</script>");
               }

    }
}
