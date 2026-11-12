package mx.medalfa.sialss.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

/** Utilerías comunes de los DAO: consultas con parámetros que devuelven filas ya leídas. */
public final class Consultas {

    private Consultas() {
    }

    public static List<Fila> lista(Connection c, String sql, Object... parametros) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, parametros);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                List<String> etiquetas = new ArrayList<>();
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    etiquetas.add(md.getColumnLabel(i));
                }
                List<Fila> filas = new ArrayList<>();
                while (rs.next()) {
                    List<String> v = new ArrayList<>(etiquetas.size());
                    for (int i = 1; i <= etiquetas.size(); i++) {
                        v.add(rs.getString(i));
                    }
                    filas.add(new Fila(etiquetas, v));
                }
                return filas;
            }
        }
    }

    public static void asignar(PreparedStatement ps, Object... parametros) throws SQLException {
        int i = 1;
        for (Object p : parametros) {
            if (p instanceof Collection<?> col) {
                for (Object x : col) {
                    ps.setObject(i++, x);
                }
            } else {
                ps.setObject(i++, p);
            }
        }
    }

    /** "?, ?, ?" para un IN con {@code n} elementos. */
    public static String marcas(int n) {
        StringJoiner j = new StringJoiner(", ");
        for (int i = 0; i < n; i++) {
            j.add("?");
        }
        return j.toString();
    }

    /** Lista de enteros a partir de "1,2, 3" (lo que llegaba concatenado en los IN). Ignora lo que no es número. */
    public static List<Integer> enteros(String texto) {
        List<Integer> l = new ArrayList<>();
        if (texto == null) {
            return l;
        }
        for (String s : texto.split(",")) {
            String t = s.trim().replace("'", "");
            if (t.matches("-?\\d+")) {
                l.add(Integer.valueOf(t));
            }
        }
        return l;
    }
}
