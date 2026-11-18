package mx.medalfa.sialss.cargas;

import java.util.List;

/** Lectura de celdas de texto para las cargas de catálogos. */
final class Campos {

    private Campos() {
    }

    /** Texto recortado; null si va vacío. Si pasa del largo agrega el error. */
    static String texto(String v, int maximo, String nombre, List<String> mal) {
        String t = v == null ? "" : v.trim().replaceAll("\\s+", " ");
        if (t.isEmpty()) {
            return null;
        }
        if (t.length() > maximo) {
            mal.add(nombre + " pasa de " + maximo + " caracteres");
        }
        return t;
    }

    /** Texto obligatorio. */
    static String obligatorio(String v, int maximo, String nombre, List<String> mal) {
        String t = texto(v, maximo, nombre, mal);
        if (t == null) {
            mal.add("falta " + nombre);
        }
        return t;
    }

    static String oGuion(String v) {
        return v == null ? "-" : v;
    }
}
