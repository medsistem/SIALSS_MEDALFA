/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.sneh.controller;


import LeeExcel.LeeExcelReciboContrato;

import in.co.sneh.model.FileUpload;
import java.io.IOException;
import java.io.InputStream;
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


/**
 *
 * @author Anibal MEDALFA
 */
@WebServlet(name = "FileUploadServletReciboContrato", urlPatterns = {"/FileUploadServletReciboContrato"})
public class FileUploadServletReciboContrato extends HttpServlet {

String IdContra = "";
    LeeExcelReciboContrato lee = new LeeExcelReciboContrato();

    
  
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");

String Usuario = "";
   
        boolean isMultiPart = ServletFileUpload.isMultipartContent(request);
       
        System.out.println("isMultiPart:" + isMultiPart);   
        if (isMultiPart) {
            ServletFileUpload upload = new ServletFileUpload();
            try {
                HttpSession sesion = request.getSession(true);
                Usuario = (String) sesion.getAttribute("nombre");
                
//                FileItemIterator itr1 = upload.getItemIterator(request);
//                while (itr1.hasNext()) {
//                    System.out.println("Busqueda de contrato");
//                    FileItemStream item = itr1.next();
//
//                    if (item.isFormField()) {
//                        String fielName = item.getFieldName();
//                        InputStream is = item.openStream();
//                        byte[] b = new byte[is.available()];
//                        is.read(b);
//                        String value = new String(b);
//                        System.out.println(fielName + ":" + value);
//                        if (fielName.equals("IdContrato")) {
//                            IdContra = value;
//                        }
//
//                    }
//                }
                 FileItemIterator itr = upload.getItemIterator(request);
                 String Nombre= "";
                while (itr.hasNext()) {
//                    System.out.println("entre a dividir");
                    FileItemStream item = itr.next();

                    if (item.isFormField()) {
                        String fielName = item.getFieldName();
                        InputStream is = item.openStream();
                        byte[] b = new byte[is.available()];
                        is.read(b);
                        String value = new String(b);
//                        System.out.println(fielName + ":" + value);
                        if (fielName.equals("IdContrato")) {
                            IdContra = value;
                        }

                    } else {
                        String ruta = getServletContext().getRealPath("/");
                        System.out.println("Ruta: " + ruta);
                        if (FileUpload.processFile(ruta, item)) {
                            Nombre = item.getName();

                        }
                    }
                }
                String ruta = getServletContext().getRealPath("/");
                if (!Nombre.equals("")) {

                    if (lee.obtieneArchivo(ruta, Nombre, Usuario, IdContra)) {
                        System.out.println("ya para salir");
                        request.getRequestDispatcher("/CargaReciboContrato.jsp").forward(request, response);
                    }
                } else {
                    response.getWriter().println("<script>alert('Error al cargar archivo'); window.location.href='CargaReciboContrato.jsp'</script>");
                    
                }

            } catch (FileUploadException e) {
                Logger.getLogger(FileUploadServlet.class.getName()).log(Level.SEVERE, null, e);
            }
            System.out.println("Termino");

        }


    }
    
   
    
}
