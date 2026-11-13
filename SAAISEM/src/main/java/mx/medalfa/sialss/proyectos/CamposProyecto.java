package mx.medalfa.sialss.proyectos;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import mx.medalfa.sialss.dao.Consultas;
import mx.medalfa.sialss.dao.Fila;

/**
 * Columnas de proyecto de {@code tb_medica}: cada proyecto de {@code tb_proyectos} apunta con {@code F_Campo} a una
 * columna (F_ProIsem, F_Project2, ...) que vale 1 si el insumo es de ese proyecto.
 * Se leen de la base cada vez, así el alta y la modificación de claves incluyen sola cualquier columna nueva.
 */
public final class CamposProyecto {

    /** Nombre de columna válido (se pone en el SQL, por eso se valida). */
    static final Pattern COLUMNA = Pattern.compile("F_[A-Za-z0-9_]{1,40}");

    public record Campo(int idProyecto, String proyecto, String columna) {
    }

    private CamposProyecto() {
    }

    /** Proyectos con su columna en tb_medica (solo las columnas que existen de verdad). */
    public static List<Campo> listar(Connection c) throws SQLException {
        Set<String> existentes = columnas(c, "tb_medica");
        List<Campo> r = new ArrayList<>();
        for (Fila f : Consultas.lista(c, "SELECT F_Id, F_DesProy, F_Campo FROM tb_proyectos ORDER BY F_Id")) {
            String col = f.get(3) == null ? "" : f.get(3).trim();
            if (COLUMNA.matcher(col).matches() && existentes.contains(col.toLowerCase(Locale.ROOT))) {
                r.add(new Campo(f.entero(1), f.get(2), col));
            }
        }
        return r;
    }

    /** Nombres de columna (en minúsculas) de una tabla de la base actual. */
    static Set<String> columnas(Connection c, String tabla) throws SQLException {
        Set<String> r = new HashSet<>();
        for (Fila f : Consultas.lista(c, "SELECT COLUMN_NAME FROM information_schema.COLUMNS "
                + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = ?", tabla)) {
            r.add(f.get(1).toLowerCase(Locale.ROOT));
        }
        return r;
    }
}
