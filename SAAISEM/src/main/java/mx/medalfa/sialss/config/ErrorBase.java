package mx.medalfa.sialss.config;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Convierte un error de la base de datos en un mensaje para el usuario, sin textos técnicos
 * (conexión, nombres de tabla, SQL). El detalle completo queda en el registro del servidor con una
 * referencia que el mensaje muestra, para que sistemas lo encuentre.
 */
public final class ErrorBase {

    private static final Logger LOG = Logger.getLogger(ErrorBase.class.getName());

    /**
     * Largo máximo real de un lote o serie: tb_lote admite más, pero el trigger loteIns lo copia a
     * tb_inventario.F_ClaLot, que es varchar(30).
     */
    public static final int LARGO_LOTE = 30;

    private static final Pattern COLUMNA = Pattern.compile("column '([^']+)'|FOREIGN KEY \\(`([^`]+)`\\)", Pattern.CASE_INSENSITIVE);
    private static final DateTimeFormatter REF = DateTimeFormatter.ofPattern("yyMMdd-HHmmss");

    /** Nombre que ve el usuario para las columnas que más se llenan desde pantallas y archivos. */
    private static final Map<String, String> CAMPOS = Map.ofEntries(
            Map.entry("F_ClaLot", "el lote o número de serie"),
            Map.entry("F_ClaPro", "la clave del insumo"),
            Map.entry("F_Cb", "el código de barras"),
            Map.entry("F_Ubica", "la ubicación"),
            Map.entry("F_tarima", "el número de tarima"),
            Map.entry("F_FolRemi", "la remisión"),
            Map.entry("F_OrdCom", "la orden de compra"),
            Map.entry("F_NoCompra", "la orden de compra"),
            Map.entry("F_DesTipo", "el nombre del perfil"),
            Map.entry("F_ClaOrg", "el proveedor"),
            Map.entry("F_ClaMar", "la marca"));

    private ErrorBase() {
    }

    /** Estado SQL de los rechazos del propio sistema: su texto ya es para el usuario. */
    private static final String RECHAZO = "SIALS";

    /** Error con un mensaje ya redactado para el usuario, que corta la transacción igual que un error de base. */
    public static SQLException rechazo(String mensaje) {
        return new SQLException(mensaje, RECHAZO);
    }

    public static String mensaje(SQLException e) {
        if (RECHAZO.equals(e.getSQLState())) {
            return e.getMessage() + " No se guardó ningún cambio.";
        }
        String ref = REF.format(LocalDateTime.now());
        LOG.warning(() -> "Referencia " + ref + ": [" + e.getErrorCode() + "/" + e.getSQLState() + "] " + e.getMessage());
        return explicacion(e) + " No se guardó ningún cambio. Si el problema sigue, avise a sistemas con la referencia " + ref + ".";
    }

    private static String explicacion(SQLException e) {
        String m = e.getMessage() == null ? "" : e.getMessage();
        String estado = e.getSQLState() == null ? "" : e.getSQLState();
        switch (e.getErrorCode()) {
            case 1406:
                return "Un dato es más largo de lo que admite el sistema" + campo(m)
                        + (m.contains("F_ClaLot") ? " (hasta " + LARGO_LOTE + " caracteres)" : "") + ". Acórtelo y vuelva a intentarlo.";
            case 1062:
                return "Ya existe un registro con esos datos" + campo(m) + ".";
            case 1452:
                return "Uno de los datos no existe en su catálogo" + campo(m) + ". Revise que esté dado de alta.";
            case 1451:
                return "No se puede cambiar o borrar porque otros registros dependen de él.";
            case 1048:
                return "Falta un dato obligatorio" + campo(m) + ".";
            case 1264:
            case 1366:
            case 1292:
                return "Un dato no tiene el formato o el rango correcto" + campo(m) + ".";
            case 1205:
            case 1213:
                return "Otro usuario estaba guardando al mismo tiempo. Vuelva a intentarlo en unos segundos.";
            default:
                if (estado.startsWith("08")) {
                    return "No hay conexión con la base de datos.";
                }
                return "La base de datos no aceptó la información.";
        }
    }

    private static String campo(String mensaje) {
        Matcher m = COLUMNA.matcher(mensaje);
        if (!m.find()) {
            return "";
        }
        String c = CAMPOS.get(m.group(1) != null ? m.group(1) : m.group(2));
        return c == null ? "" : ": " + c;
    }
}
