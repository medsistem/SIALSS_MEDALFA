<%-- 
    Document   : generaAbastoCSV
    Created on : 21/04/2015, 09:15:47 AM
    Author     : Americo
--%>
<%@page import="in.co.sneh.service.AbastoService"%>
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
    //response.setContentType("text/csv");
    //response.setHeader("Content-Disposition", "attachment; filename=Abasto_" + request.getParameter("F_ClaDoc") + ".txt");
    //ConectionDB con = new ConectionDB();
    HttpSession sesion = request.getSession();
    String usua = "", tipo = "";
    if (sesion.getAttribute("nombre") != null) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("index.jsp");
    }

    Class.forName("org.mariadb.jdbc.Driver").newInstance();
    Connection conn = DriverManager.getConnection("jdbc:mariadb://localhost:3306/medalfa_isem", "facturacion_saa", "S44faCt0r4cI0n$c");
    
    File archivo;
    archivo = new File("C:\\ABASTO\\ISEM\\Abasto_" + request.getParameter("F_ClaDoc") + "-" + request.getParameter("ConInv") + ".csv");
    BufferedWriter fw = new BufferedWriter(new FileWriter(archivo));
    
//con.conectar();
    //ResultSet rset = con.consulta("SELECT F.F_ClaPro,M.F_DesPro,L.F_ClaLot,L.F_FecCad,SUM(F.F_CantSur),L.F_Origen,L.F_Cb FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote=L.F_FolLot AND F.F_ClaPro=L.F_ClaPro AND F.F_Ubicacion=L.F_Ubica INNER JOIN tb_medica M ON F.F_ClaPro=M.F_ClaPro WHERE F_ClaDoc='"+request.getParameter("F_ClaDoc")+"' AND F_CantSur>0 GROUP BY F.F_ClaPro,L.F_ClaLot,L.F_FecCad,L.F_Origen;");
    int Origen = 0, Origen2 = 0, Contar = 0;
    String Origenes = "", Origenes2 = "", Proyecto = "", Documento = "";
    ConectionDB con = new ConectionDB();
    Statement stmt = con.getConn().createStatement();
    PreparedStatement ps = null;
    

    Proyecto = request.getParameter("idProyecto");
    Documento = request.getParameter("F_ClaDoc");

    try {
    /*    AbastoService abasto = null;
abasto.crearAbastoWeb(Integer.parseInt(Documento),  Integer.parseInt( Proyecto), usua);*/
       String queryElimina = "DELETE FROM tb_abastoweb WHERE F_Sts = 0 AND F_Proyecto = ? AND F_ClaDoc = ?;";

        String queryValida = "SELECT COUNT(*) FROM tb_abastoweb WHERE F_Sts = 1 AND F_Proyecto ='" + request.getParameter("idProyecto") + "' AND F_ClaDoc ='" + request.getParameter("F_ClaDoc") + "';";
        
        //String queryInserta = "INSERT INTO tb_abastoweb VALUES (?,?,?,?,?,?,?,?,?,?,NOW(),?,0,0);";
        String queryInserta = "INSERT INTO tb_abastoweb VALUES (?,?,?,?,?,?,?,?,?,?,NOW(),?,0,0,?);";
       
       // String queryDatosCsV = "SELECT F.F_ClaCli, F.F_Proyecto, F.F_ClaDoc, LTRIM(RTRIM(F.F_ClaPro)), M.F_DesPro, LTRIM(RTRIM(L.F_ClaLot)), DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F.F_CantSur), CASE WHEN ORI.F_TipOri = 'AR' THEN '1' ELSE '0' END AS ORIGEN, SUBSTR(L.F_Cb, 1, 13) AS F_Cb, NOW() FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot AND F.F_ClaPro = L.F_ClaPro AND F.F_Ubicacion = L.F_Ubica INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen ORI ON ORI.F_ClaOri = L.F_Origen WHERE F_ClaDoc = '" + request.getParameter("F_ClaDoc") + "' AND F_CantSur > 0 AND F_StsFact = 'A' AND F.F_Proyecto = '" + request.getParameter("idProyecto") + "' GROUP BY F.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen;";
        String queryDatosCsV = "SELECT F.F_ClaCli, F.F_Proyecto, F.F_ClaDoc, LTRIM(RTRIM(F.F_ClaPro)), M.F_DesPro, LTRIM(RTRIM(L.F_ClaLot)), DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F.F_CantSur), L.F_Origen, SUBSTR(L.F_Cb, 1, 13) AS F_Cb, NOW(),CASE WHEN ORI.F_DesOri LIKE 'SC%' THEN '2' WHEN ORI.F_TipOri = 'AR' THEN '1' ELSE '0' END AS ORIGEN, F.F_Lote as LOTE FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot AND F.F_ClaPro = L.F_ClaPro AND F.F_Ubicacion = L.F_Ubica INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen ORI ON ORI.F_ClaOri = L.F_Origen WHERE F_ClaDoc = '" + request.getParameter("F_ClaDoc") + "' AND F_CantSur > 0 AND F_StsFact = 'A' AND F.F_Proyecto = '" + request.getParameter("idProyecto") + "' GROUP BY F.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen;";
       
        String getFactorEmpaque = "SELECT IFNULL(F_FactorEmpaque, 0) as factor FROM tb_compra where F_Lote = ? order by F_IdCom DESC";
        
        ResultSet rsetValida = stmt.executeQuery(queryValida);
        while (rsetValida.next()) {
            Contar = rsetValida.getInt(1);
        }

//        if (Contar == 0) {
            con.conectar();
            ps = con.getConn().prepareStatement(queryElimina);
            ps.setString(1, Proyecto);
            ps.setString(2, Documento);
            ps.executeUpdate();

            ResultSet rsetDatos = stmt.executeQuery(queryDatosCsV);
            while (rsetDatos.next()) {

                Origenes2 = "0";
                
                int factorEmpaque = 1;
                int folLot = rsetDatos.getInt("LOTE");
                PreparedStatement psfe = con.getConn().prepareStatement(getFactorEmpaque);
                psfe.setInt(1, folLot);
                ResultSet rsfe = psfe.executeQuery();
                if (rsfe.next()) {
                    factorEmpaque = rsfe.getInt("factor");
                }
             
                ps.clearParameters();

                ps = con.getConn().prepareStatement(queryInserta);
                ps.setString(1, rsetDatos.getString(1));
                ps.setString(2, rsetDatos.getString(2));
                ps.setString(3, rsetDatos.getString(3));
                ps.setString(4, rsetDatos.getString(4));
                ps.setString(5, rsetDatos.getString(5));
                ps.setString(6, rsetDatos.getString(6));
                ps.setString(7, rsetDatos.getString(7));
                ps.setString(8, rsetDatos.getString(8));
                ps.setString(9, rsetDatos.getString(12));
                ps.setString(10, rsetDatos.getString(10));
                ps.setString(11, usua);
                ps.setInt(12, factorEmpaque);
                ps.executeUpdate();
            }

//        }
        con.cierraConexion();
    } catch (Exception e) {
        //Logger.getLogger(AdministraRemisiones.class.getName()).log(Level.SEVERE, null, e);
    }

    String query = "SELECT LTRIM(RTRIM(F.F_ClaPro)),M.F_DesPro,LTRIM(RTRIM(L.F_ClaLot)),DATE_FORMAT(L.F_FecCad,'%d/%m/%Y') AS F_FecCad,SUM(F.F_CantSur),CASE WHEN ORI.F_TipOri = 'AR' THEN '1' ELSE '0' END AS ORIGEN,SUBSTR(L.F_Cb,1,13) AS F_Cb FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote=L.F_FolLot AND F.F_ClaPro=L.F_ClaPro AND F.F_Ubicacion=L.F_Ubica INNER JOIN tb_medica M ON F.F_ClaPro=M.F_ClaPro INNER JOIN tb_origen ORI ON ORI.F_ClaOri = L.F_Origen  WHERE F_ClaDoc='" + request.getParameter("F_ClaDoc") + "' AND F_CantSur>0 AND F_StsFact='A' AND F.F_Proyecto = '" + request.getParameter("idProyecto") + "' GROUP BY F.F_ClaPro,L.F_ClaLot,L.F_FecCad;";
    ResultSet rset = stmt.executeQuery(query);
    while (rset.next()) {

        Origenes = "0";

        /*if ((Origen == 0) || (Origen == 1)) {
            Origenes = "0";
        } else {
            Origenes = "2";
        }*/
        //out.println(rset.getString(1) + "," + rset.getString(2) + "," + rset.getString(3) + "," + rset.getString(4) + "," + rset.getString(5) + "," + rset.getString(6) + "," + rset.getString(7));
        fw.append(rset.getString(1));
        //fw.append(rset.getString(2));
        fw.append(",");
        fw.append("-");
        fw.append(",");
        fw.append(rset.getString(3));
        fw.append(",");
        fw.append(rset.getString(4));
        fw.append(",");
        fw.append(rset.getString(5));
        //fw.append(",");fw.append(rset.getString(6));
        fw.append(",");
        fw.append(rset.getString("ORIGEN"));
        //fw.append(",");fw.append("0");
        fw.append(",");
        fw.append(rset.getString(7));
        fw.newLine();
    }
    fw.flush();
    fw.close();
    conn.close();
    //out.println("<script>window.location='../reimp_factura.jsp'</script>");
    //con.cierraConexion();
%>
<head>
    <script type="text/javascript">

        var ventana = window.self;
        ventana.opener = window.self;
        setTimeout("window.close()", 7000);

    </script>
</head>