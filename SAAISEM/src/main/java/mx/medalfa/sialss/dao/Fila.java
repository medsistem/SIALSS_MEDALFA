package mx.medalfa.sialss.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Un renglón de resultado ya leído (la conexión se cierra antes de pintar la página).
 * Se consulta igual que un ResultSet: por posición desde 1 o por etiqueta de columna.
 */
public final class Fila {

    private final List<String> valores;
    private final Map<String, Integer> posiciones;

    Fila(List<String> etiquetas, List<String> valores) {
        this.valores = Collections.unmodifiableList(new ArrayList<>(valores));
        Map<String, Integer> p = new HashMap<>();
        for (int i = 0; i < etiquetas.size(); i++) {
            p.putIfAbsent(etiquetas.get(i).toLowerCase(), i);
        }
        this.posiciones = p;
    }

    /** Valor de la columna {@code n} (1 = primera), como {@code ResultSet.getString(n)}. */
    public String get(int n) {
        return valores.get(n - 1);
    }

    /** Valor por etiqueta (sin distinguir mayúsculas), como {@code ResultSet.getString(etiqueta)}. */
    public String get(String etiqueta) {
        Integer i = posiciones.get(etiqueta.toLowerCase());
        if (i == null) {
            throw new IllegalArgumentException("La consulta no tiene la columna " + etiqueta);
        }
        return valores.get(i);
    }

    /** Como {@code ResultSet.getInt}: null o vacío vale 0. */
    public int entero(String etiqueta) {
        String v = get(etiqueta);
        return v == null || v.isBlank() ? 0 : new java.math.BigDecimal(v.trim()).intValue();
    }

    public int entero(int n) {
        String v = get(n);
        return v == null || v.isBlank() ? 0 : new java.math.BigDecimal(v.trim()).intValue();
    }
}
