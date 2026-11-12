<%-- 
    Document   : Reporte
    Created on : 26/12/2012, 09:05:24 AM
    Author     : Unknown
--%>

<%@page import="conn.ConectionDB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="net.sf.jasperreports.engine.*" %> 
<%@ page import="java.util.*" %> 
<%@ page import="java.io.*" %> 
<%@ page import="java.sql.*" %> 
<% /*Parametros para realizar la conexión*/

    HttpSession sesion = request.getSession();
    ConectionDB con = new ConectionDB();
    String usua = "";
    int SumaMedReq = 0, SumaMedSur = 0, SumaMedReqT = 0, SumaMedSurT = 0;
    double MontoMed = 0.0, MontoTMed = 0.0;
    String Unidad = "", Fecha = "", Direc = "", F_FecApl = "", F_Obs = "", F_Obs2 = "";
    int SumaMatReq = 0, SumaMatSur = 0, SumaMatReqT = 0, SumaMatSurT = 0;
    double MontoMat = 0.0, MontoTMat = 0.0;
    int RegistroC = 0, Ban = 0, HojasC = 0, HojasR = 0;
    String DesV = "";
    double Hoja = 0.0, Hoja2 = 0.0;

    int TotalReq = 0, TotalSur = 0;
    double TotalMonto = 0.0;
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
    } else {
        response.sendRedirect("index.jsp");
    }
    Connection conexion;
    Class.forName("org.mariadb.jdbc.Driver").newInstance();
    conexion = con.getConn();

    File reportFile = new File(application.getRealPath("/ReportesPuntos/MarbeteSobres.jasper"));
    /* No enviamos parámetros porque nuestro reporte no los necesita asi que escriba 
     cualquier cadena de texto ya que solo seguiremos el formato del método runReportToPdf*/
    Map parameters = new HashMap();
    parameters.put("Folfact", "");
    parameters.put("F_Obs", "");
    /*Enviamos la ruta del reporte, los parámetros y la conexión(objeto Connection)*/
    byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conexion);
    /*Indicamos que la respuesta va a ser en formato PDF*/ response.setContentType("application/pdf");
    response.setContentLength(bytes.length);
    ServletOutputStream ouputStream = response.getOutputStream();
    ouputStream.write(bytes, 0, bytes.length); /*Limpiamos y cerramos flujos de salida*/ ouputStream.flush();
    ouputStream.close();


%>
