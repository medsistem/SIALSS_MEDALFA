<%-- 
    Document   : Reporte
    Created on : 26/12/2012, 09:05:24 AM
    Author     : Unknown
--%>

<%@page import="net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter"%>
<%@page import="net.sf.jasperreports.engine.export.JRPrintServiceExporter"%>
<%@page import="javax.print.attribute.standard.Copies"%>
<%@page import="javax.print.attribute.standard.MediaSizeName"%>
<%@page import="javax.print.attribute.standard.MediaSize"%>
<%@page import="javax.print.attribute.standard.MediaPrintableArea"%>
<%@page import="javax.print.attribute.PrintRequestAttributeSet"%>
<%@page import="javax.print.attribute.HashPrintRequestAttributeSet"%>
<%@page import="javax.print.PrintServiceLookup"%>
<%@page import="javax.print.PrintService"%>
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
    
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
    } else {
        //response.sendRedirect("index.jsp");
    }
    
    int Copy = 0;
    //String Folio1 = request.getParameter("Folio1");
    //String Folio2 = request.getParameter("Folio2");
    //String FecMin = request.getParameter("FecMin");
    String Impresora = request.getParameter("Impresora");
    //String Impresora = request.getParameter("Impresora");

    Connection conexion;
    //Class.forName("org.mariadb.jdbc.Driver").newInstance();
     Class.forName("org.mariadb.jdbc.Driver").newInstance();
    conexion = con.getConn();
    int count = 0, Epson = 0, Impre = 0;
    String Nom = "";
    PrintService[] impresoras = PrintServiceLookup.lookupPrintServices(null, null);
    PrintService imprePredet = PrintServiceLookup.lookupDefaultPrintService();
    PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
    MediaSizeName mediaSizeName = MediaSize.findMedia(4, 4, MediaPrintableArea.INCH);
    printRequestAttributeSet.add(mediaSizeName);
    printRequestAttributeSet.add(new Copies(1));

    for (PrintService printService : impresoras) {
        Nom = printService.getName();
        System.out.println("impresora" + Nom);
 
        if (Nom.contains(Impresora)) {
            Epson = count;
        } else {
            Impre = count;
        }
        count++;
    }
    System.out.println("user"+usua);
    //FecMin = "[" + FecMin + "]";
    //FecMax = "[" + FecMax + "]";
    /*Establecemos la ruta del reporte*/
    ResultSet FoliosC = con.consulta("SELECT * FROM tb_imprelacion as F INNER JOIN tb_factura FT ON F.F_ClaDoc = FT.F_ClaDoc AND F.F_Proyecto = FT.F_Proyecto WHERE F.F_User = '" + usua + "' GROUP BY F.F_ClaDoc, F.F_Proyecto ORDER BY F.F_ClaDoc + 0 LIMIT 1;");
    while (FoliosC.next()) {
    Copy = FoliosC.getInt(8);
    System.out.println("copies"+Copy);
    for (int x = 0; x < Copy; x++) {
    File reportFile = new File(application.getRealPath("/reportes/ImprimeRelacion.jasper"));
    Map parameters = new HashMap();
    parameters.put("Usu", usua);
    //parameters.put("FecMin", FecMin);
    //parameters.put("FecMax", FecMax);
    JasperPrint jasperPrint = JasperFillManager.fillReport(reportFile.getPath(), parameters, conexion);
    JRPrintServiceExporter exporter = new JRPrintServiceExporter();
    exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
    exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, impresoras[Epson].getAttributes());
    exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
    exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);

    try {
        exporter.exportReport();
    } catch (Exception ex) {
        System.out.println("Error-> " + ex);
    }
    }}

    conexion.close();
    
%>
<script type="text/javascript">

    var ventana = window.self;
    ventana.opener = window.self;
    setTimeout("window.close()", 5000);

</script>
