package mx.medalfa.sialss.cargas;

/** Textos para los mensajes de las cargas. */
final class Texto {

    private Texto() {
    }

    /** "1 renglón", "3 renglones", "1,250 piezas". */
    static String cuantos(long n, String uno, String varios) {
        return String.format("%,d", n) + " " + (n == 1 ? uno : varios);
    }
}
