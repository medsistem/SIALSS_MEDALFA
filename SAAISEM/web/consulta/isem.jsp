<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.ParseException"%>
<%@page import="java.util.Date"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.sql.SQLException"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.ResultSetMetaData"%>
<%@page contentType="application/json" pageEncoding="UTF-8"%>
<%@page import="conn.ConectionDBTrans" %>
<%
    DecimalFormat formatter = new DecimalFormat("#,###,###");
    DecimalFormat formatterDecimal = new DecimalFormat("#,###,##0.00");
    DecimalFormatSymbols custom = new DecimalFormatSymbols();
    custom.setDecimalSeparator('.');
    custom.setGroupingSeparator(',');
    formatter.setDecimalFormatSymbols(custom);
    formatterDecimal.setDecimalFormatSymbols(custom);
    HttpSession sesion = request.getSession();
    String usua = "";
    String tipo = "";
    int IdOrigen = 0;
    if (sesion.getAttribute("nombre") != null && sesion.getAttribute("Tipo").equals("27")) {
        usua = (String) sesion.getAttribute("nombre");
        tipo = (String) sesion.getAttribute("Tipo");
    } else {
        response.sendRedirect("/SIALSS_MDF/indexalmacen.jsp");
    }
    
    
    ConectionDBTrans con = new ConectionDBTrans();
    ResultSet rset = null;

    // URl
    //http://localhost:8080/SIALSS_MDF/consulta/isem.jsp?clues=&folio=&clave=&lote=&fecha_ini=&fecha_fin=&pagina=
    try {
        con.conectar();
        String clues = request.getParameter("clues");
        String clave = request.getParameter("clave");
        String tipoGrupo = request.getParameter("tipo");
        String fecha_ini = request.getParameter("fecha_ini");
        String fecha_fin = request.getParameter("fecha_fin");
        String pagina = request.getParameter("pagina");
        
        
        
        
        // Verificar si el parámetro de página es un número entero si está presente
    if (pagina != null && !pagina.isEmpty() && pagina != "0") {
        try {
            Integer.parseInt(pagina);
        } catch (NumberFormatException e) {
            response.setStatus(400); // Establecer código de estado HTTP 400 (Solicitud incorrecta)
            out.println("{ \"mensaje\": \"El parámetro 'pagina' debe ser un número entero\" }");
            return; // Salir del script JSP
        }
    }
    // Verificar formato de fecha_ini
        if (fecha_ini != null && !fecha_ini.isEmpty()) {
            if (!fecha_ini.matches("\\d{4}-\\d{2}-\\d{2}")) {
                response.setStatus(400); // Establecer código de estado HTTP 400 (Solicitud incorrecta)
                out.println("{ \"mensaje\": \"El formato de fecha de inicio proporcionado es incorrecto. Debe ser en el formato yyyy-MM-dd\" }");
                return; // Salir del script JSP
            }
        }

        // Verificar formato de fecha_fin
        if (fecha_fin != null && !fecha_fin.isEmpty()) {
            if (!fecha_fin.matches("\\d{4}-\\d{2}-\\d{2}")) {
                response.setStatus(400); // Establecer código de estado HTTP 400 (Solicitud incorrecta)
                out.println("{ \"mensaje\": \"El formato de fecha de fin proporcionado es incorrecto. Debe ser en el formato yyyy-MM-dd\" }");
                return; // Salir del script JSP
            }
        }
    
    if (fecha_ini != null && !fecha_ini.isEmpty() && fecha_fin != null && !fecha_fin.isEmpty()) {
    
    try {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date fechaInicio = dateFormat.parse(fecha_ini);
        Date fechaFin = dateFormat.parse(fecha_fin);
        
        if (fechaFin.before(fechaInicio)) {
            response.setStatus(400); // Establecer código de estado HTTP 400 (Solicitud incorrecta)
            out.println("{ \"mensaje\": \"La fecha de finalización debe ser posterior a la fecha de inicio\" }");
            return; // Salir del script JSP
        }
    } catch (ParseException e) {        
        e.printStackTrace();
        response.setStatus(400); // Establecer código de estado HTTP 400 (Solicitud incorrecta)
        out.println("{ \"mensaje\": \"Error al analizar las fechas. La estructura de fecha proporcionada es incorrecta. Debe ser en el formato AAAA-MM-DD\" }");
        return; // Salir del script JSP
    }
}
       

        // Construir la consulta SQL con los filtros proporcionados
        String query = "SELECT v.Clues, v.Unidad, v.Clave, v.Clave_larga, v.Nombre_generico, v.Descripcion_especifica, v.Presentacion, v.Grupo_terapeutico, v.Cantidad, v.Fecha, v.Remision, v.Origen FROM v_facturacion_isem AS v WHERE 1=1";
        if (clues != null && !clues.isEmpty()) {
            query += " AND v.Clues LIKE '%" + clues + "%'";
        }
        if (clave != null && !clave.isEmpty()) {
            query += " AND v.Clave_larga = '" + clave + "'";
        }
        if (tipoGrupo != null && !tipoGrupo.isEmpty()) {
            query += " AND v.Grupo_terapeutico LIKE '%" + tipoGrupo + "%'";
        }
        if (fecha_ini != null && !fecha_ini.isEmpty() && fecha_fin != null && !fecha_fin.isEmpty()) {
            query += " AND v.Fecha BETWEEN '" + fecha_ini + "' AND '" + fecha_fin + "'";
        }

        // Obtener el número total de registros
        int totalCount = 0;
        String countQuery = "SELECT COUNT(*) AS total FROM v_facturacion_isem AS v WHERE 1=1";
        if (clues != null && !clues.isEmpty()) {
            countQuery += " AND v.Clues LIKE '%" + clues + "%'";
        }
        if (clave != null && !clave.isEmpty()) {
            countQuery += " AND v.Clave_larga = '" + clave + "'";
        }
        if (tipoGrupo != null && !tipoGrupo.isEmpty()) {
            countQuery += " AND v.Grupo_terapeutico LIKE '%" + tipoGrupo + "%'";
        }
        if (fecha_ini != null && !fecha_ini.isEmpty() && fecha_fin != null && !fecha_fin.isEmpty()) {
            countQuery += " AND v.Fecha BETWEEN '" + fecha_ini + "' AND '" + fecha_fin + "'";
        }
        PreparedStatement countStmt = con.getConn().prepareStatement(countQuery);
        ResultSet countResult = countStmt.executeQuery();
        if (countResult.next()) {
            totalCount = countResult.getInt("total");
        }
        countResult.close();
        countStmt.close();

        // Agregar paginación si es necesario
        int offset = 0;
        int pageSize = 1000; // Número de registros por página
        if (pagina != null && !pagina.isEmpty()) {
            offset = (Integer.parseInt(pagina) - 1) * pageSize;
            query += " LIMIT " + pageSize + " OFFSET " + offset;
        }
            
       
if ((clues == null || clues.isEmpty()) && (clave == null || clave.isEmpty()) && (tipoGrupo == null || tipoGrupo.isEmpty()) && ((fecha_ini == null || fecha_ini.isEmpty()) || (fecha_fin == null || fecha_fin.isEmpty()))) {
    response.setStatus(400); // Establecer código de estado HTTP 400 (Solicitud incorrecta)
    out.println("{ \"mensaje\": \"Debes ingresar al menos un parámetro de consulta (clues, clave, tipo) o un rango completo de fechas\" }");
    return; // Salir del script JSP
}

PreparedStatement pstm = con.getConn().prepareStatement(query);
        System.out.println(query); // Solo para depuración
        rset = pstm.executeQuery();

        JSONArray json = new JSONArray();
        ResultSetMetaData rsmd = rset.getMetaData();
        while (rset.next()) {
            int numColumns = rsmd.getColumnCount();
            JSONObject obj = new JSONObject();
            for (int i = 1; i <= numColumns; i++) {
                String column_name = rsmd.getColumnName(i);
                obj.put(column_name, rset.getObject(column_name));
            }
            json.put(obj);
        }
        JSONObject dataObject = new JSONObject();
        dataObject.put("resultado", json);
        dataObject.put("registros_mostrados", json.length());
        dataObject.put("total_registros", totalCount);

%>{ 
"peticion" : {
"unidad":"<%= clues%>",    
"clave":"<%= clave%>",
"tipo":"<%= tipoGrupo%>",
"fecha_ini":"<%= fecha_ini%>",
"fecha_fin":"<%= fecha_fin%>",
"pagina":"<%= pagina%>"
},
"respuesta" :  <%=dataObject%>,
}
<%
} catch (Exception e) {
%>{ 
"error" : "<%=e.toString()%>"
}
<%
    } finally {
        if (rset != null) {
            try {
                rset.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        con.cierraConexion();
    }
%>