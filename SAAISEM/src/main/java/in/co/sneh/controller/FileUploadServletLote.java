/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.sneh.controller;

import LeeExcel.LeeExcelLote;
import conn.ConectionDB;
import in.co.sneh.model.FileUpload;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileUploadException;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;

/**
 *
 * @author Anibal GNKL
 */
@WebServlet(name = "FileUploadServletLote", urlPatterns = {"/FileUploadServletLote"})
public class FileUploadServletLote extends HttpServlet {

    LeeExcelLote lee = new LeeExcelLote();

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ConectionDB con = new ConectionDB();
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String Unidad = "";
        String Usuario = "";
        boolean isMultiPart = JakartaServletFileUpload.isMultipartContent(request);
        if (isMultiPart) {
            JakartaServletFileUpload upload = new JakartaServletFileUpload();
            try {
                HttpSession sesion = request.getSession(true);
                try {
                    Usuario = (String) sesion.getAttribute("nombre");
                } catch (Exception e) {
                    System.out.println();
                }

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
                        if (FileUpload.processFile(path, item)) {
                            //response.getWriter().println("file uploaded successfully");
                            if (lee.obtieneArchivo(path, item.getName(), Usuario)) {
                                out.println("<script>alert('Se cargó el Requerimiento Correctamente')</script>");
                                out.println("<script>window.location='requerimientoLote.jsp'</script>");
                            }
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
            out.println("<script>window.location='requerimientoLote.jsp'</script>");
            //response.sendRedirect("carga.jsp");
        }

        /*
         * Para insertar el excel en tablas
         */
    }
}
