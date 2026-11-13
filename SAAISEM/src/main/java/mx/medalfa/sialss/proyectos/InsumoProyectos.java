package mx.medalfa.sialss.proyectos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeSet;
import mx.medalfa.sialss.dao.Consultas;
import mx.medalfa.sialss.dao.Fila;

/**
 * Alta y cambio de los proyectos de un insumo en tb_medica. El INSERT nombra sus columnas y agrega, leídas de la base,
 * todas las columnas de proyecto ({@link CamposProyecto}): así, cada vez que el alta de proyecto crea una columna nueva
 * en tb_medica, el alta de claves ya la incluye sin tocar código (antes el INSERT era posicional y se rompía).
 */
public final class InsumoProyectos {

    private InsumoProyectos() {
    }

    /**
     * Inserta el insumo. {@code base} = columna → valor de los campos propios del insumo (sin columnas de proyecto);
     * {@code proyectos} = columnas de proyecto marcadas (valen 1; las demás 0).
     */
    public static void insertar(Connection c, Map<String, Object> base, Collection<String> proyectos) throws SQLException {
        List<CamposProyecto.Campo> campos = CamposProyecto.listar(c);
        StringJoiner cols = new StringJoiner(", ");
        List<Object> valores = new ArrayList<>();
        for (Map.Entry<String, Object> e : base.entrySet()) {
            if (!CamposProyecto.COLUMNA.matcher(e.getKey()).matches()) {
                throw new IllegalArgumentException("Columna no válida: " + e.getKey());
            }
            cols.add(e.getKey());
            valores.add(e.getValue());
        }
        Set<String> marcadas = minusculas(proyectos);
        for (CamposProyecto.Campo cp : campos) {
            if (!base.containsKey(cp.columna())) {
                cols.add(cp.columna());
                valores.add(marcadas.contains(cp.columna().toLowerCase()) ? 1 : 0);
            }
        }
        String sql = "INSERT INTO tb_medica (" + cols + ") VALUES (" + Consultas.marcas(valores.size()) + ")";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            Consultas.asignar(ps, valores.toArray());
            ps.executeUpdate();
        }
    }

    /** Deja al insumo en exactamente esos proyectos (para la modificación de claves). */
    public static void actualizar(Connection c, String clave, Collection<String> proyectos) throws SQLException {
        List<CamposProyecto.Campo> campos = CamposProyecto.listar(c);
        if (campos.isEmpty()) {
            return;
        }
        Set<String> marcadas = minusculas(proyectos);
        StringJoiner set = new StringJoiner(", ");
        List<Object> valores = new ArrayList<>();
        for (CamposProyecto.Campo cp : campos) {
            set.add(cp.columna() + " = ?");
            valores.add(marcadas.contains(cp.columna().toLowerCase()) ? 1 : 0);
        }
        valores.add(clave);
        try (PreparedStatement ps = c.prepareStatement("UPDATE tb_medica SET " + set + " WHERE F_ClaPro = ?")) {
            Consultas.asignar(ps, valores.toArray());
            ps.executeUpdate();
        }
    }

    /** Columnas de proyecto que tiene marcadas un insumo. */
    public static Set<String> marcadas(Connection c, String clave) throws SQLException {
        Set<String> r = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        List<CamposProyecto.Campo> campos = CamposProyecto.listar(c);
        if (campos.isEmpty()) {
            return r;
        }
        StringJoiner cols = new StringJoiner(", ");
        campos.forEach(cp -> cols.add(cp.columna()));
        List<Fila> f = Consultas.lista(c, "SELECT " + cols + " FROM tb_medica WHERE F_ClaPro = ?", clave);
        if (!f.isEmpty()) {
            for (int i = 0; i < campos.size(); i++) {
                String v = f.get(0).get(i + 1);
                if (v != null && !"0".equals(v.trim())) {
                    r.add(campos.get(i).columna());
                }
            }
        }
        return r;
    }

    private static Set<String> minusculas(Collection<String> c) {
        Set<String> r = new TreeSet<>();
        if (c != null) {
            c.forEach(x -> r.add(x.trim().toLowerCase()));
        }
        return r;
    }
}
