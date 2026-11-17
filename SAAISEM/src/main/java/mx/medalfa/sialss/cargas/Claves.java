package mx.medalfa.sialss.cargas;

import java.util.Map;

/**
 * Busca en el catálogo (tb_medica) la clave escrita en Excel. Primero tal cual; si no, con la normalización de
 * las cargas anteriores (LeeExcel): rellena con ceros a 4 dígitos y ajusta los decimales que Excel cambia
 * ("1.10" → "0001.1", "260.02" → "0260.02", "5.00" → "0005").
 */
public final class Claves {

    private Claves() {
    }

    /** La clave del catálogo, o null si no existe. */
    public static <T> String resolver(String escrita, Map<String, T> catalogo) {
        if (escrita == null || escrita.isEmpty()) {
            return null;
        }
        String v = escrita.trim();
        for (String candidata : new String[]{v, Excel.sinDecimalCero(v), v.toUpperCase(), normalizar(v)}) {
            if (candidata != null && catalogo.containsKey(candidata)) {
                return candidata;
            }
        }
        return null;
    }

    static String normalizar(String clave) {
        String c = clave;
        String[] p = c.split("\\.");
        if (p.length > 1) {
            String entero = "0".repeat(Math.max(0, 4 - p[0].length())) + p[0];
            c = switch (p[1]) {
                case "01", "02", "03", "04", "05" -> entero + "." + p[1];
                case "1", "10" -> entero + ".1";
                case "2", "20" -> entero + ".2";
                case "30" -> entero + ".3";
                case "40" -> entero + ".4";
                case "50" -> entero + ".5";
                default -> entero;
            };
        }
        if (c.length() < 4 && !c.startsWith("0")) {
            c = "0".repeat(4 - c.length()) + c;
        }
        return c;
    }
}
