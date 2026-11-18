package mx.medalfa.sialss.cargas;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Busca nombres iguales o parecidos para no duplicar catálogos. Dos nombres son el mismo si, sin acentos, mayúsculas,
 * signos ni espacios de más, quedan iguales (o iguales sin espacios: "BAXTER" / "BAX TER"). Son parecidos si difieren en
 * muy pocas letras (distancia de Levenshtein: 1 desde 4 letras, 10 % en nombres largos; ver {@link #permitidas}) y tienen
 * exactamente los mismos números, para que "PARACETAMOL 500 MG" y "PARACETAMOL 750 MG" no se tomen por el mismo.
 */
public final class Parecidos {

    /** Porcentaje mínimo de letras iguales para decir que dos nombres se parecen. */
    static final double SIMILITUD = 0.90;

    /** Terminaciones de razón social que no distinguen a un proveedor (S.A. de C.V., S. de R.L., etc.). */
    private static final Set<String> SOCIEDAD = Set.of("SA", "DE", "CV", "RL", "SAPI", "SAB", "SC", "AC", "SPR", "RI",
            "MI", "S", "A", "C", "V", "R", "L", "P", "I", "B");

    /** Resultado de una búsqueda: con qué nombre ya registrado coincide y si es igual o parecido. */
    public record Coincidencia(String nombre, boolean exacta) {
        public String motivo(String que) {
            return exacta ? "ya existe " + que + " \"" + nombre + "\"" : "se parece a " + que + " \"" + nombre + "\"";
        }
    }

    private record Entrada(String original, String normal, String compacto) {
    }

    private final boolean sinSociedad;
    private final Map<String, String> exactos = new HashMap<>();
    /** Por firma de números ("500|10"), para comparar solo nombres con los mismos números. */
    private final Map<String, List<Entrada>> porNumeros = new HashMap<>();

    /** @param sinSociedad true para proveedores: quita "S.A. de C.V." y similares antes de comparar. */
    public Parecidos(boolean sinSociedad) {
        this.sinSociedad = sinSociedad;
    }

    public void agregar(String nombre) {
        String n = normalizar(nombre);
        if (n.isEmpty()) {
            return;
        }
        String compacto = n.replace(" ", "");
        exactos.putIfAbsent(n, nombre);
        exactos.putIfAbsent(compacto, nombre);
        porNumeros.computeIfAbsent(numeros(n), k -> new ArrayList<>()).add(new Entrada(nombre, n, compacto));
    }

    /** El nombre registrado igual o parecido, o null si es nuevo. */
    public Coincidencia buscar(String nombre) {
        String n = normalizar(nombre);
        if (n.isEmpty()) {
            return null;
        }
        String compacto = n.replace(" ", "");
        String igual = exactos.containsKey(n) ? exactos.get(n) : exactos.get(compacto);
        if (igual != null) {
            return new Coincidencia(igual, true);
        }
        Entrada mejor = null;
        double mejorSim = 0;
        for (Entrada e : porNumeros.getOrDefault(numeros(n), List.of())) {
            int largo = Math.max(compacto.length(), e.compacto().length());
            int permitidas = permitidas(Math.min(compacto.length(), e.compacto().length()));
            if (permitidas == 0 || Math.abs(compacto.length() - e.compacto().length()) > permitidas) {
                continue;
            }
            int d = distancia(compacto, e.compacto());
            double sim = 1 - (double) d / largo;
            if (d <= permitidas && sim > mejorSim) {
                mejor = e;
                mejorSim = sim;
            }
        }
        return mejor == null ? null : new Coincidencia(mejor.original(), false);
    }

    /** Sin acentos, en mayúsculas, solo letras y números separados por un espacio. */
    String normalizar(String t) {
        if (t == null) {
            return "";
        }
        String n = Normalizer.normalize(t, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9Ñ]+", " ").trim();
        if (sinSociedad) {
            List<String> p = new ArrayList<>(List.of(n.split(" ")));
            while (p.size() > 1 && SOCIEDAD.contains(p.get(p.size() - 1))) {
                p.remove(p.size() - 1);
            }
            n = String.join(" ", p);
        }
        return n;
    }

    /** Letras distintas que se toleran: ninguna en nombres de menos de 4 letras, 1 hasta 19 y el 10 % en los largos. */
    static int permitidas(int largo) {
        return largo < 4 ? 0 : Math.max(1, (int) Math.floor(largo * (1 - SIMILITUD)));
    }

    private static String numeros(String n) {
        StringBuilder sb = new StringBuilder();
        for (String p : n.split("[^0-9]+")) {
            if (!p.isEmpty()) {
                sb.append(p.replaceFirst("^0+(?=.)", "")).append('|');
            }
        }
        return sb.toString();
    }

    private static int distancia(String a, String b) {
        int[] prev = new int[b.length() + 1];
        int[] cur = new int[b.length() + 1];
        for (int j = 0; j <= b.length(); j++) {
            prev[j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            cur[0] = i;
            for (int j = 1; j <= b.length(); j++) {
                int costo = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                cur[j] = Math.min(Math.min(cur[j - 1] + 1, prev[j] + 1), prev[j - 1] + costo);
            }
            int[] t = prev;
            prev = cur;
            cur = t;
        }
        return prev[b.length()];
    }
}
