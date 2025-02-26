<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.sql.ResultSet"%>

<%@page import="conn.*" %>
<%@ page contentType="text/html; charset=iso-8859-1" language="java" import="java.sql.*" import="java.text.*" import="java.lang.*" import="java.util.*" import= "javax.swing.*" import="java.io.*" import="java.text.DateFormat" import="java.text.ParseException" import="java.text.SimpleDateFormat" import="java.util.Calendar" import="java.util.Date"  import="java.text.NumberFormat" import="java.util.Locale" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns:v="urn:schemas-microsoft-com:vml"
xmlns:o="urn:schemas-microsoft-com:office:office"
xmlns:x="urn:schemas-microsoft-com:office:excel"
xmlns="http://www.w3.org/TR/REC-html40">
<% 
DecimalFormat formatter = new DecimalFormat("#,###,###");
    DecimalFormat formatterDecimal = new DecimalFormat("#,###,##0.00");
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator('.');
    custom.setGroupingSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    formatterDecimal.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "",F_User="",FolCon1="";
    String FolCon="",Reporte="";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
    } else {
        response.sendRedirect("index.jsp");
    }
    try {
        FolCon = request.getParameter("Folio");
    } catch (Exception e) {
    }
    ConectionDB con = new ConectionDB();
/*    
NumberFormat nf1 = NumberFormat.getInstance(Locale.US); 
DecimalFormat formatter2 = new DecimalFormat("#,###,###.##");

ResultSet rset2 = null;
ResultSet rset = null;
String fecha1="",fecha2="",radio="",F_Idsur="",F_DesSur="",F_Descob="",F_DesSum="";
int F_IdePro=0,F_Cvesum=0;
double costo=0.0,iva=0.0,total=0.0;
String only="";

	try {
		fecha1 = request.getParameter("f1");       			   
                fecha2 = request.getParameter("f2");       			   
                radio = request.getParameter("ra");       			   
		       			   
        } catch (Exception e) { }
        
        System.out.println("f1"+fecha1+" f2:"+fecha2+" ra:"+radio);
String but="r";
*/	  

response.setContentType("application/vnd.ms-excel");
response.setHeader("Content-Disposition", "attachment;filename="+FolCon+".xls");


//ingresar un try y realizar una sola conexión
   
				  
%>
<head>
<meta http-equiv=Content-Type content="text/html; charset=windows-1252">
<meta name=ProgId content=Excel.Sheet>
<meta name=Generator content="Microsoft Excel 12">
<link id=Main-File rel=Main-File href="../Libro1.htm">
<link rel=File-List href=filelist.xml>
<!--link rel=Stylesheet href=stylesheet.css-->
<style> 
<!--table
	{mso-displayed-decimal-separator:"\.";
	mso-displayed-thousand-separator:"\,";}
@page
	{margin:.75in .7in .75in .7in;
	mso-header-margin:.3in;
	mso-footer-margin:.3in;
	mso-page-orientation:landscape;}
.style1 {color: #FF0000}
-->
</style>
<![if !supportTabStrip]><script language="JavaScript"> 
<!--
function fnUpdateTabs()
 {
  if (parent.window.g_iIEVer>=4) {
   if (parent.document.readyState=="complete"
    && parent.frames['frTabs'].document.readyState=="complete")
   parent.fnSetActiveSheet(0);
  else
   window.setTimeout("fnUpdateTabs();",150);
 }
}
 
if (window.name!="frSheet")
 window.location.replace("../Libro1.htm");
else
 fnUpdateTabs();
//-->
</script>
<![endif]>
</head>
 
<body link=blue vlink=purple>
 
<table border=0 cellpadding=0 cellspacing=0 width=439 style='border-collapse:
 collapse;table-layout:fixed;width:330pt'>
 <col width=120 style='mso-width-source:userset;mso-width-alt:4388;width:90pt'>
 <col width=117 style='mso-width-source:userset;mso-width-alt:4278;width:88pt'>
 <col width=101 span=2 style='mso-width-source:userset;mso-width-alt:3693;
 width:76pt'>
 
 
 <%
         try{
       con.conectar();
       
  %>
   <tr height=20 style='height:15.0pt'>
  <td height=20 style='height:15.0pt;mso-ignore:colspan'>Fecha Pedido</td>
  <td class=xl66>Fecha Entrega</td>
  <td class=xl66>Clave SAP</td>
  <td class=xl66>Cantidad</td>
  <td class=xl66>Lote</td>
  <td class=xl66>Periodo</td>
  <td class=xl66>Secuencial</td>
  <td class=xl66>Reporte MEDALFA</td>
  <td class=xl66>Tipo de Venta</td>
  <td class=xl66>Pob Abi/Seg Pop</td>
  <td class=xl66>Tipo Medicamento</td>
  <td class=xl66>Clave Unidad</td>
  <td class=xl66>Clave MEDALFA</td>  
  </tr>
   
  <tr height=20 style='height:15.0pt'>
 
 <%
   ResultSet rset2 = con.consulta("SELECT * FROM tb_imprepconglobalote WHERE F_Reporte='"+FolCon+"'");
    while(rset2.next()){
 %>  
  <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset2.getString(2)%></td>
  <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset2.getString(3)%></td>
  <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset2.getString(4)%></td>
  <td height=20 class=xl75><%=rset2.getString(5)%></td>
  <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset2.getString(6)%></td>
  <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset2.getString(7)%></td>
  <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset2.getString(8)%></td>
  <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset2.getString(9)%></td>
  <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset2.getString(10)%></td>
  <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset2.getString(11)%></td>
  <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset2.getString(12)%></td> 
  <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset2.getString(13)%></td>
  <td height=20 class=xl75 style='height:15.0pt;mso-number-format:"@"'><%=rset2.getString(14)%></td> 
 </tr>
 <%}%>
  <![if supportMisalignedColumns]>
 <tr height=0 style='display:none'>
  <td width=120 style='width:90pt'></td>
  <td width=117 style='width:88pt'></td>
  <td width=101 style='width:76pt'></td>
  <td width=101 style='width:76pt'></td>
 </tr>
 <![endif]>
</table>
 
</body>
 
</html>
<%
 con.cierraConexion();
 } catch (Exception e) {
     System.out.println(e.getMessage());
 }
                                
%>