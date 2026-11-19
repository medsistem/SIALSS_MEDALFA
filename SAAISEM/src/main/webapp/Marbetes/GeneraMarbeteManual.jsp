<%-- 
    Document   : Marbete
    Created on : 11/05/2016, 05:06:07 PM
    Author     : juan
--%>

<%@page import="java.util.logging.Logger"%>
<%@page import="java.util.logging.Level"%>
<%@page import="net.sf.jasperreports.engine.JasperRunManager"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.io.File"%>
<%@page import="java.sql.Connection"%>
<%@page import="conn.ConectionDB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<% /*Parametros para realizar la conexión*/

    HttpSession sesion = request.getSession();
    ConectionDB con = new ConectionDB();

    if (sesion.getAttribute("nombre") != null) {
    } else {
        response.sendRedirect("../index.jsp");
        return;
    }

    try {
        Connection conexion;
        con.conectar();
        conexion = con.getConn();
        
        String Clave = request.getParameter("clave");
        String Lote = request.getParameter("lote");
        // El reporte declara Proyecto como java.lang.String: con un Integer Jasper rechaza el parámetro.
        String Proyecto = String.valueOf(Integer.parseInt(request.getParameter("Proyecto")));
        
        System.out.println("Marbete jasper: "+Clave+"/"+Lote+"/" +Proyecto);

        String Ruta = "";

        Ruta = "/reportes/MarbeteManual.jasper";

        File reportFile = new File(application.getRealPath(Ruta));

        /* No enviamos parámetros porque nuestro reporte no los necesita asi que escriba 
         cualquier cadena de texto ya que solo seguiremos el formato del método runReportToPdf*/
        Map parameters = new HashMap();
        parameters.put("Clave", Clave);
        parameters.put("Lote", Lote);
        parameters.put("Proyecto", Proyecto);
        parameters.put("Logo", application.getRealPath("/reportes/image/LogoMedalfa.png"));

        /*Enviamos la ruta del reporte, los parámetros y la conexión(objeto Connection)*/
        byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conexion);
        /*Indicamos que la respuesta va a ser en formato PDF*/ response.setContentType("application/pdf");
        response.setContentLength(bytes.length);
        ServletOutputStream ouputStream = response.getOutputStream();
        ouputStream.write(bytes, 0, bytes.length);
        /*Limpiamos y cerramos flujos de salida*/ ouputStream.flush();
        ouputStream.close();

    } catch (Exception e) {
        Logger.getLogger("GeneraMarbeteManual.jsp").log(Level.SEVERE, "No se pudo generar el marbete manual", e);
        // Antes el error se tragaba y el usuario veía una página en blanco.
        if (!response.isCommitted()) {
            response.setContentType("text/html;charset=UTF-8");
            out.clearBuffer();
            out.print("<!DOCTYPE html><html lang=\"es\"><head><meta charset=\"UTF-8\"><title>Marbete manual</title></head>"
                    + "<body style=\"font-family:Arial,sans-serif;padding:24px\"><h3>No se pudo generar el marbete</h3>"
                    + "<p>Revise que el proyecto, el producto y el lote sean correctos. Si el problema sigue, avise a sistemas.</p>"
                    + "<p style=\"color:#666;font-size:12px\">Detalle: "
                    + String.valueOf(e.getMessage()).replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                    + "</p></body></html>");
        }
    } finally {
        try {
            con.cierraConexion();
        } catch (Exception ex) {
           ex.getMessage();
            Logger.getLogger("GeneraMarbeteManual.jsp").log(Level.SEVERE, null, ex);
        }
    }
%>
