package mx.medalfa.sialss.cargas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Renglones que una carga no sube por estar repetidos: ya registrados (igual o parecido) o repetidos más arriba en el
 * mismo archivo. No son error: se informan aparte ({@link CargaArchivo.Revision#omitidos()}) y se carga lo demás.
 */
public final class Omitidos {

    private final List<String> lista = new ArrayList<>();

    /** {@code detalle}: qué trae el renglón y por qué se omite ("PISA: ya existe la marca \"PISA\""). */
    public void agregar(int renglon, String detalle) {
        lista.add("Renglón " + renglon + ": " + detalle + ".");
    }

    public int cuantos() {
        return lista.size();
    }

    public List<String> lista() {
        return List.copyOf(lista);
    }

    /** Texto para el final del resumen; vacío si no hay omitidos. */
    public String resumen() {
        return lista.isEmpty() ? "" : (lista.size() == 1 ? " Se omite " : " Se omiten ") + Texto.cuantos(lista.size(), "renglón repetido", "renglones repetidos") + ".";
    }

    /** Revisión con lo nuevo que sí se va a guardar. */
    public CargaArchivo.Revision valida(List<List<String>> muestra, String resumen, Serializable datos) {
        return new CargaArchivo.Revision(List.of(), muestra, resumen + resumen(), datos, lista());
    }

    /** Revisión cuando no quedó nada nuevo que subir (no es error). */
    public CargaArchivo.Revision sinNuevos(int leidos, String que) {
        if (leidos == 0 || lista.isEmpty()) {
            return CargaArchivo.Revision.conError("El archivo no tiene renglones.");
        }
        return new CargaArchivo.Revision(List.of(), List.of(), "No hay " + que + ": "
                + (lista.size() == 1 ? "el renglón ya está cargado o se repite." : "los " + lista.size() + " renglones ya están cargados o se repiten.")
                + " No se guardó nada.", null, lista());
    }

    /** Final del mensaje de "Se cargaron…". */
    public static String mensajeFinal(int omitidos) {
        return omitidos == 0 ? "" : (omitidos == 1 ? " No se cargó " : " No se cargaron ") + Texto.cuantos(omitidos, "renglón repetido", "renglones repetidos") + ".";
    }
}
