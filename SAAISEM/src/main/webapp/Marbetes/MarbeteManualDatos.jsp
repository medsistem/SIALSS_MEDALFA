<%--
    Marbete Manual: catálogos encadenados en JSON.
      ?accion=claves&proyecto=8              -> claves que tienen lotes en ese proyecto
      ?accion=lotes&proyecto=8&clave=RXF     -> lotes de esa clave en ese proyecto
    Respuesta: [{"valor":"...","texto":"..."}, ...]
--%><%@page import="java.sql.Connection"%><%@page import="java.sql.PreparedStatement"%><%@page import="java.sql.ResultSet"%><%@page import="conn.ConectionDB"%><%@page contentType="application/json; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%><%!
    private static String json(String s) {
        if (s == null) {
            return "";
        }
        StringBuilder b = new StringBuilder(s.length() + 8);
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"': b.append("\\\""); break;
                case '\\': b.append("\\\\"); break;
                case '\n': b.append("\\n"); break;
                case '\r': b.append("\\r"); break;
                case '\t': b.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        b.append(String.format("\\u%04x", (int) c));
                    } else {
                        b.append(c);
                    }
            }
        }
        return b.toString();
    }
%><%
    response.setHeader("Cache-Control", "no-store");
    if (request.getSession().getAttribute("nombre") == null) {
        response.setStatus(401);
        out.print("{\"error\":\"La sesión terminó. Vuelva a iniciar sesión.\"}");
        return;
    }

    String accion = request.getParameter("accion");
    int proyecto;
    try {
        proyecto = Integer.parseInt(request.getParameter("proyecto"));
    } catch (Exception e) {
        response.setStatus(400);
        out.print("{\"error\":\"Proyecto no válido.\"}");
        return;
    }
    String clave = request.getParameter("clave");

    String sql;
    if ("claves".equals(accion)) {
        sql = "SELECT l.F_ClaPro AS valor, CONCAT(l.F_ClaPro, ' - ', IFNULL(MAX(m.F_DesPro), '')) AS texto "
            + "FROM tb_lote l LEFT JOIN tb_medica m ON m.F_ClaPro = l.F_ClaPro "
            + "WHERE l.F_Proyecto = ? GROUP BY l.F_ClaPro ORDER BY l.F_ClaPro";
    } else if ("lotes".equals(accion) && clave != null && !clave.isBlank()) {
        sql = "SELECT F_ClaLot AS valor, F_ClaLot AS texto FROM tb_lote "
            + "WHERE F_Proyecto = ? AND F_ClaPro = ? GROUP BY F_ClaLot ORDER BY F_ClaLot";
    } else {
        response.setStatus(400);
        out.print("{\"error\":\"Consulta no válida.\"}");
        return;
    }

    ConectionDB con = new ConectionDB();
    StringBuilder r = new StringBuilder("[");
    try {
        con.conectar();
        Connection c = con.getConn();
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, proyecto);
            if ("lotes".equals(accion)) {
                ps.setString(2, clave);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (r.length() > 1) {
                        r.append(',');
                    }
                    r.append("{\"valor\":\"").append(json(rs.getString("valor")))
                     .append("\",\"texto\":\"").append(json(rs.getString("texto"))).append("\"}");
                }
            }
        }
        r.append(']');
        out.print(r);
    } catch (Exception e) {
        log("Error al consultar " + accion + " en MarbeteManualDatos.jsp", e);
        response.setStatus(500);
        out.print("{\"error\":\"No se pudo consultar el catálogo.\"}");
    } finally {
        try { con.cierraConexion(); } catch (Exception ex) { }
    }
%>
