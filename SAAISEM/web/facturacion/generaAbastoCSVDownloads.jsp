<%-- 
    Document   : generaAbastoCSV
    Created on : 21/04/2015, 09:15:47 AM
    Author     : Americo
--%>
<%@page import="java.io.FileReader"%>
<%@page import="java.io.BufferedReader"%>
<%@page import="java.io.FileInputStream"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.io.File"%>
<%@page import="java.io.FileWriter"%>
<%@page import="java.io.BufferedWriter"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="conn.ConectionDB"%>
<%
    HttpSession sesion = request.getSession();
     String usua = "", tipo = "",   Proyecto = "", Documento = "", queryElimina = "", Query2 = "",queryValida = "", queryInserta ="", getFactorEmpaque ="", queryDatosCsV ="";
     int  Contar = 0;
 
    
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }
    
  
  // BufferedWriter fw = new BufferedWriter(new FileWriter(archivo));
  
        final File archivo = new File("C:\\ABASTO\\ISEM\\Abasto_" + request.getParameter("F_ClaDoc") + "-" + request.getParameter("ConInv") + ".csv");

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + archivo.getName());

        final BufferedReader br = new BufferedReader(new FileReader(archivo));
        try {
            String line;
            while ((line = br.readLine()) != null) {
                response.getWriter().write(line+'\n');
            }
        } finally {
            br.close();
        }
    
    


%>
