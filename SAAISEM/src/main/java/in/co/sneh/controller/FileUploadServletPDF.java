/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.sneh.controller;

import in.co.sneh.model.FileUploadPDF;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileUploadException;
import java.io.InputStream;
import java.io.PrintWriter;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import jakarta.servlet.http.HttpSession;
import conn.*;

/**
 * Proceso de carga archivo pdf iso 9000
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
public class FileUploadServletPDF extends HttpServlet {
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ConectionDB con = new ConectionDB();
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div style='text-align: center; font-family: Verdana;'>");
        out.println("<img src='imagenes/loading.gif' width='100px' height='100px' align='center' /><br>");
        out.println("Se está finalizando la subida");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
        String Unidad = "";
        boolean isMultiPart = JakartaServletFileUpload.isMultipartContent(request);
        if (isMultiPart) {
            JakartaServletFileUpload upload = new JakartaServletFileUpload();
            try {
                HttpSession sesion = request.getSession(true);
                FileItemInputIterator itr = upload.getItemIterator(request);
                while (itr.hasNext()) {
                    FileItemInput item = itr.next();
                    if (item.isFormField()) {
                        String fielName = item.getFieldName();
                        InputStream is = item.getInputStream();
                        byte[] b = new byte[is.available()];
                        is.read(b);
                        String value = new String(b);
                        //response.getWriter().println(fielName + ":" + value + "<br/>");
                        if (fielName.equals("id_uni")) {
                            Unidad = value;
                        }
                    } else {
                        String path = getServletContext().getRealPath("/");
                        if (FileUploadPDF.processFile(path, item, Unidad)) {
                            try {
                                con.conectar();
                                //con.actualizar("DELETE FROM tb_pdf WHERE F_Oc='" + Unidad + "';");
                                //con.insertar("insert into tb_pdf values('" + Unidad + "', '" + item.getName() + "')");
                                con.actualizar("UPDATE tb_iso SET F_NomCom='"+item.getName()+"' WHERE F_IdFor='"+Unidad+"'");
                                con.cierraConexion();
                            } catch (Exception e) {

                            }
                            //response.getWriter().println("file uploaded successfully");
                            sesion.setAttribute("ban", "1");
                            //response.sendRedirect("cargaFotosCensos.jsp");
                        } else {
                            //response.getWriter().println("file uploading falied");
                            //response.sendRedirect("cargaFotosCensos.jsp");
                        }
                    }
                }
            } catch (FileUploadException fue) {
                fue.printStackTrace();
            }

            out.println("<script>alert('Se cargo correctamente el archivo PDF.')</script>");
            out.println("<script>window.location='/SIALSS_MDF_1/ListaISOModifica?Lista=formatos'</script>");
            //response.sendRedirect("indexCapR.jsp");
        }
    }
}
