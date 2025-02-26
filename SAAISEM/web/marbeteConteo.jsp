<%-- 
    Document   : Reporte
    Created on : 26/12/2012, 09:05:24 AM
    Author     : Unknown
--%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>Page Title</title>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <meta name="format-detection" content="telephone=no" />
</head>
<body>

<%@page import="org.apache.commons.codec.Charsets"%>
<%@page import="in.co.sneh.model.Marbetes"%>
<%@page import="in.co.sneh.model.Marbete"%>
<%@page import="com.google.gson.Gson"%>
<%@page import="java.util.stream.Collectors"%>
<%@page import="conn.ConectionDB"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="net.sf.jasperreports.engine.*" %> 
<%@ page import="java.util.*" %> 
<%@ page import="java.io.*" %> 
<%@ page import="java.sql.*" %> 
<% /*Parametros para realizar la conexión*/
    response.setHeader("Access-Control-Allow-Origin", "*");
    request.setCharacterEncoding("UTF-8");
    HttpSession sesion = request.getSession();
    ConectionDB con = new ConectionDB();

    String usua = "";

    int Tarimas = 0, TTarimas = 0, TarimasI = 0, Piezas = 0, TPiezas = 0, Cajas = 0, TCajas = 0, CajasI = 0, Resto = 0, Restop = 0, PiezasT = 0, PiezasC = 0, PiezasTI = 0, TotalP = 0, Bandera = 0;

    String Clave = "", Cb = "", Lote = "", Cadu = "", Descrip = "", Orden = "", F_OrdCom = "", FechaA = "";
//
//    if (sesion.getAttribute("nombre") != null) {
//        usua = (String) sesion.getAttribute("nombre");
//    } else {
//        response.sendRedirect("index.jsp");
//    }
    String body = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    System.out.println(body);
    Gson gson = new Gson();
    
    Marbetes marbetes = gson.fromJson(body, Marbetes.class);
    try {
        con.conectar();

        con.insertar("delete from tb_cajasmarbetes where F_ClaDoc= -1");
        for(Marbete m: marbetes.getMarbetes()){
            String qry = "insert into tb_cajasmarbetes values ('-1','" + m.getPurchaseOrder() + "','','"+m.getProviderName()+"','" + m.getBrandName() + "','ISEM', '" + m.getShortKey() + "', '" + m.getDescription() + "', '" + m.getBatch() + "','" + m.getExpiration() + "','" + m.getBarcode() + "','" + m.getPiecesPerBox() + "','"+m.getReception()+" ', 0, 0, 0, '1 / 1','I',0)";
            con.insertar(qry);
        }
        
        con.cierraConexion();
    } catch (Exception e) {
        System.out.println(e.getMessage());
    }

    Connection conexion;

    Class.forName(
            "org.mariadb.jdbc.Driver").newInstance();
    conexion = con.getConn();
    /*Establecemos la ruta del reporte*/
    File reportFile = new File(application.getRealPath("/reportes/MarbeteCajasRConteo.jasper"));
    /* No enviamos parámetros porque nuestro reporte no los necesita asi que escriba 
     cualquier cadena de texto ya que solo seguiremos el formato del método runReportToPdf*/
    Map parameters = new HashMap();

    parameters.put(
            "documento", -1);
//    parameters.put(
//            "oc", request.getParameter("F_OrdCom"));
//    parameters.put(
//            "remision", request.getParameter("F_FolRemi"));
    /*Enviamos la ruta del reporte, los parámetros y la conexión(objeto Connection)*/
    byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conexion);

    /*Indicamos que la respuesta va a ser en formato PDF*/ response.setContentType(
            "application/pdf");
    response.setContentLength(bytes.length);
    ServletOutputStream ouputStream = response.getOutputStream();

    ouputStream.write(bytes,
            0, bytes.length);
    /*Limpiamos y cerramos flujos de salida*/ ouputStream.flush();

    ouputStream.close();
%>
</body>
</html>