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
        response.sendRedirect("index.jsp");
    }
    
    try {
        con.conectar();
        Connection conexion;
        con.conectar();
        conexion = con.getConn();
        
        String Clave = request.getParameter("clave");
        String Lote = request.getParameter("lote");
       int Proyecto = Integer.parseInt(request.getParameter("Proyecto"));
       // int Proyecto = Integer.parseInt(request.getParameter("proyectoF"));
        
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

        /*Enviamos la ruta del reporte, los parámetros y la conexión(objeto Connection)*/
        byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conexion);
        /*Indicamos que la respuesta va a ser en formato PDF*/ response.setContentType("application/pdf");
        response.setContentLength(bytes.length);
        ServletOutputStream ouputStream = response.getOutputStream();
        ouputStream.write(bytes, 0, bytes.length);
        /*Limpiamos y cerramos flujos de salida*/ ouputStream.flush();
        ouputStream.close();

    } catch (Exception e) {
        Logger.getLogger("GeneraMarbeteManual.jsp").log(Level.SEVERE, null, e);
        e.getMessage();
    } finally {
        try {
            con.cierraConexion();
        } catch (Exception ex) {
           ex.getMessage();
            Logger.getLogger("GeneraMarbeteManual.jsp").log(Level.SEVERE, null, ex);
        }
    }
%>
