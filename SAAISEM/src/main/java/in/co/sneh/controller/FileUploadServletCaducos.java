/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.sneh.controller;

import LeeExcel.LeeExcelCaducos;
import in.co.sneh.model.FileUpload;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
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
@WebServlet(name = "FileUploadServletCaducos", urlPatterns = {"/FileUploadServletCaducos"})
public class FileUploadServletCaducos extends HttpServlet {

    LeeExcelCaducos lee = new LeeExcelCaducos();

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String Usuario;
        boolean isMultiPart = JakartaServletFileUpload.isMultipartContent(request);
        if (isMultiPart) {
            JakartaServletFileUpload upload = new JakartaServletFileUpload();
            try {
                HttpSession sesion = request.getSession(true);
                Usuario = (String) sesion.getAttribute("nombre");

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
                            if (lee.obtieneArchivo(path, item.getName(), Usuario)) {
                                out.println("<script>alert('Se cargó Correctamente')</script>");
                                out.println("<script>window.location='cargacaducos.jsp'</script>");
                            }
                        }
                    }
                }
            } catch (FileUploadException e) {
                Logger.getLogger(FileUploadServlet.class.getName()).log(Level.SEVERE, null, e);
            }
            out.println("<script>alert('No se pudo cargar, verifique las celdas')</script>");
            out.println("<script>window.location='cargacaducos.jsp'</script>");
        }

    }
}