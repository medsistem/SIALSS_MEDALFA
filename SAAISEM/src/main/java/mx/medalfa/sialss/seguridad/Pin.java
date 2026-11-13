package mx.medalfa.sialss.seguridad;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import mx.medalfa.sialss.config.Entorno;

/**
 * PIN de inicio de sesión: reglas y cifrado.
 * <p>
 * Se guarda como {@code pbkdf2$iteraciones$sal$hash} (PBKDF2-HMAC-SHA256, sal aleatoria por usuario), nunca en claro.
 * Un PIN corto se adivina rápido si alguien se lleva la tabla, por eso lo que lo protege en línea es la misma regla de
 * la contraseña: a los {@link PoliticaSesion#intentosMaximos()} intentos fallidos (PIN o contraseña) se bloquea el usuario.
 */
public final class Pin {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 120_000;
    private static final SecureRandom AZAR = new SecureRandom();

    private Pin() {
    }

    /** Dígitos del PIN ({@code PIN_DIGITOS} en el .env, 6 si no está; entre 4 y 8). */
    public static int digitos() {
        int d = Entorno.entero("PIN_DIGITOS", 6);
        return Math.max(4, Math.min(8, d));
    }

    /** null si el PIN se puede usar; si no, el motivo para mostrarlo al usuario. */
    public static String problema(String pin) {
        int n = digitos();
        if (pin == null || !pin.matches("\\d{" + n + "}")) {
            return "El PIN debe tener exactamente " + n + " números.";
        }
        if (pin.chars().distinct().count() == 1) {
            return "El PIN no puede repetir el mismo número (por ejemplo 111111).";
        }
        boolean sube = true, baja = true;
        for (int i = 1; i < pin.length(); i++) {
            int dif = pin.charAt(i) - pin.charAt(i - 1);
            sube &= dif == 1 || dif == -9;
            baja &= dif == -1 || dif == 9;
        }
        if (sube || baja) {
            return "El PIN no puede ser una secuencia (por ejemplo 123456 o 654321).";
        }
        return null;
    }

    public static String cifrar(String pin) {
        byte[] sal = new byte[16];
        AZAR.nextBytes(sal);
        Base64.Encoder b64 = Base64.getEncoder().withoutPadding();
        return "pbkdf2$" + ITERACIONES + "$" + b64.encodeToString(sal) + "$" + b64.encodeToString(derivar(pin, sal, ITERACIONES));
    }

    public static boolean verificar(String pin, String guardado) {
        if (pin == null || pin.isEmpty() || guardado == null) {
            return false;
        }
        String[] p = guardado.split("\\$");
        if (p.length != 4 || !"pbkdf2".equals(p[0])) {
            return false;
        }
        try {
            Base64.Decoder b64 = Base64.getDecoder();
            byte[] esperado = b64.decode(p[3]);
            return MessageDigest.isEqual(esperado, derivar(pin, b64.decode(p[2]), Integer.parseInt(p[1])));
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derivar(String pin, byte[] sal, int iteraciones) {
        PBEKeySpec spec = new PBEKeySpec(pin.toCharArray(), sal, iteraciones, 256);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 no disponible", e);
        } finally {
            spec.clearPassword();
        }
    }
}
