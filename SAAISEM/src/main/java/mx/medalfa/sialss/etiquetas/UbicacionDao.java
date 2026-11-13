package mx.medalfa.sialss.etiquetas;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mx.medalfa.sialss.config.BaseDatos;
import mx.medalfa.sialss.dao.Consultas;
import mx.medalfa.sialss.dao.Fila;
import mx.medalfa.sialss.etiquetas.EtiquetaUbicacionPdf.Ubicacion;

/** Ubicaciones del almacén (tb_ubica) para imprimir sus etiquetas. */
public final class UbicacionDao {

    public record Fila2(String clave, String descripcion, String seccion) {
    }

    /** Orden natural de las ubicaciones: sección y luego clave. */
    private static final String ORDEN = " ORDER BY seccion, F_ClaUbi";

    private UbicacionDao() {
    }

    public static List<String> secciones() throws SQLException {
        List<String> r = new ArrayList<>();
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            for (Fila f : Consultas.lista(c, "SELECT DISTINCT COALESCE(NULLIF(TRIM(seccion), ''), 'S/A') FROM tb_ubica ORDER BY 1")) {
                r.add(f.get(1));
            }
        }
        return r;
    }

    /** Busca por sección (vacío = todas) y texto en clave o descripción; como máximo {@code limite} filas. */
    public static List<Fila2> buscar(String seccion, String texto, int limite) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT F_ClaUbi, F_DesUbi, COALESCE(NULLIF(TRIM(seccion), ''), 'S/A') FROM tb_ubica WHERE 1 = 1");
        List<Object> p = new ArrayList<>();
        if (seccion != null && !seccion.isBlank()) {
            sql.append(" AND COALESCE(NULLIF(TRIM(seccion), ''), 'S/A') = ?");
            p.add(seccion.trim());
        }
        if (texto != null && !texto.isBlank()) {
            sql.append(" AND (F_ClaUbi LIKE ? OR F_DesUbi LIKE ?)");
            String like = "%" + texto.trim().replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_") + "%";
            p.add(like);
            p.add(like);
        }
        sql.append(ORDEN).append(" LIMIT ?");
        p.add(limite);
        List<Fila2> r = new ArrayList<>();
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            for (Fila f : Consultas.lista(c, sql.toString(), p.toArray())) {
                r.add(new Fila2(f.get(1), f.get(2), f.get(3)));
            }
        }
        return r;
    }

    /** Las ubicaciones de esas claves, en el orden natural; las claves que no existen se ignoran. */
    public static List<Ubicacion> porClaves(Collection<String> claves) throws SQLException {
        Map<String, Ubicacion> r = new LinkedHashMap<>();
        List<String> lista = new ArrayList<>(claves);
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            // En bloques para no armar un IN enorme.
            for (int i = 0; i < lista.size(); i += 500) {
                List<String> bloque = lista.subList(i, Math.min(lista.size(), i + 500));
                for (Fila f : Consultas.lista(c, "SELECT F_ClaUbi, F_DesUbi FROM tb_ubica WHERE F_ClaUbi IN ("
                        + Consultas.marcas(bloque.size()) + ")" + ORDEN, bloque)) {
                    r.put(f.get(1), new Ubicacion(f.get(1), f.get(2)));
                }
            }
        }
        return new ArrayList<>(r.values());
    }
}
