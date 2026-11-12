/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.sneh.controller;

import in.co.sneh.model.*;
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
import LeeExcel.*;

/**
 * Proceso de carga de relación de facturas
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
public class FileUploadFactura extends HttpServlet {

    LeerFactXls lee = new LeerFactXls();
    CargaFacturas carga = new CargaFacturas();
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        
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
                        if (FileUploadFact.processFile(path, item)) {
                            //response.getWriter().println("file uploaded successfully");
                            sesion.setAttribute("ban", "1");
                            if (lee.obtieneArchivo(path, item.getName())) {
                                String mensaje = carga.cargafacturas((String) sesion.getAttribute("id_usu"), item.getName());
                                out.println("<script>alert('"+mensaje+"')</script>");
                                out.println("<script>window.location='RelacionarF.jsp'</script>");
                            }
                            //response.sendRedirect("cargaFotosCensos.jsp");
                        } else {
                            //response.getWriter().println("file uploading falied");
                            //response.sendRedirect("cargaFotosCensos.jsp");
                        }
                    }
                }
            } catch (FileUploadException fue) {
                System.out.println(fue.getMessage());
                fue.printStackTrace();
            }
            out.println("<script>alert('No se pudo subir archivo, verifique las celdas')</script>");
            out.println("<script>window.location='RelacionarF.jsp'</script>");
            //response.sendRedirect("carga.jsp");
        }

        /*
         * Para insertar el excel en tablas
         */
    }
}
