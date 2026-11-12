/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.sneh.controller;

import LeeExcel.LeeExcelSAP;
import in.co.sneh.model.SubeOrdenSAP;
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
 * Proceso de carga información de SAP
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
public class SubeOrdenSapServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ConectionDB con = new ConectionDB();
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String Unidad = "";
        LeeExcelSAP lee = new LeeExcelSAP();

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
                        response.getWriter().println(fielName + ":" + value + "<br/>");
                    } else {
                        String path = getServletContext().getRealPath("/");
                        if (SubeOrdenSAP.processFile(path, item)) {
                            //response.getWriter().println("file uploaded successfully");
                            String mensaje = "";
                            try {
                                mensaje = lee.obtieneArchivo(path, item.getName(), (String) sesion.getAttribute("nombre"));
                            } catch (Exception e) {
                                mensaje = e.getMessage();
                            }
                            out.println("<script>alert('" + mensaje + "')</script>");
                            out.println("<script>window.location='SAP/cargarOrdenSAP.jsp'</script>");
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
            out.println("<script>alert('No se pudo cargar el Folio, verifique las celdas')</script>");
            out.println("<script>window.location='SAP/cargarOrdenSAP.jsp'</script>");
            //response.sendRedirect("carga.jsp");
        }

        /*
         * Para insertar el excel en tablas
         */
    }
}
