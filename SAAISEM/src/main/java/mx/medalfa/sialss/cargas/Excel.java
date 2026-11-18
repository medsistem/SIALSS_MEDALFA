package mx.medalfa.sialss.cargas;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import mx.medalfa.sialss.dao.Fila;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Lectura y plantillas de Excel comunes a las cargas por archivo (POI 3.15: {@code getCellTypeEnum()}).
 * Primera hoja, la fila 1 son los títulos, los renglones vacíos se ignoran.
 */
public final class Excel {

    public static final int MAXIMO_RENGLONES = 5000;
    private static final DateTimeFormatter DMA = DateTimeFormatter.ofPattern("d/M/uuuu");
    private static final DateTimeFormatter DMA_GUION = DateTimeFormatter.ofPattern("d-M-uuuu");

    /** Renglón con datos: número de fila en Excel (2 = primera después de los títulos) y valores como texto. */
    public record Renglon(int numero, String[] v) {
    }

    /** Bloque de la hoja "Catálogos" de la plantilla: título y pares (clave, descripción). */
    public record Bloque(String titulo, List<Fila> filas) {
    }

    /** El archivo no se pudo leer o no tiene renglones válidos; el mensaje se muestra al usuario. */
    public static final class Ilegible extends Exception {
        public Ilegible(String mensaje) {
            super(mensaje);
        }
    }

    private Excel() {
    }

    /** Lee la primera hoja. {@code fechas}: índices de columnas que pueden traer fecha de Excel. */
    public static List<Renglon> leer(InputStream archivo, int columnas, Set<Integer> fechas) throws IOException, Ilegible {
        return leer(archivo, columnas, fechas, new String[0][]);
    }

    /**
     * Lee la primera hoja y descarta los renglones de ejemplo de la plantilla ({@code ejemplos}, comparados sin las
     * columnas de fecha, que cambian cada día). Rechaza el archivo vacío: sin hojas, sin renglones debajo de los
     * títulos o con solo el ejemplo de la plantilla.
     */
    public static List<Renglon> leer(InputStream archivo, int columnas, Set<Integer> fechas, String[][] ejemplos)
            throws IOException, Ilegible {
        List<Renglon> r = new ArrayList<>();
        int ejemplosQuitados = 0;
        try (Workbook wb = WorkbookFactory.create(archivo)) {
            if (wb.getNumberOfSheets() == 0) {
                throw new Ilegible("El archivo está vacío: no tiene hojas. Use la plantilla y vuelva a subirlo.");
            }
            Sheet s = wb.getSheetAt(0);
            DataFormatter fmt = new DataFormatter();
            for (int i = 1; i <= s.getLastRowNum(); i++) {
                Row row = s.getRow(i);
                if (row == null) {
                    continue;
                }
                String[] v = new String[columnas];
                boolean vacio = true;
                for (int j = 0; j < columnas; j++) {
                    v[j] = celda(row.getCell(j), fmt, fechas.contains(j));
                    vacio &= v[j].isEmpty();
                }
                if (vacio) {
                    continue;
                }
                if (esEjemplo(v, ejemplos, fechas)) {
                    ejemplosQuitados++;
                    continue;
                }
                r.add(new Renglon(i + 1, v));
            }
        } catch (RuntimeException | org.apache.poi.openxml4j.exceptions.InvalidFormatException ex) {
            throw new Ilegible(ex.getMessage() != null && ex.getMessage().toLowerCase().contains("zero bytes")
                    ? "El archivo está vacío (0 bytes). Elija el Excel con sus datos."
                    : "No se pudo leer el archivo como Excel (.xlsx o .xls): " + ex.getMessage());
        }
        if (r.isEmpty()) {
            throw new Ilegible(ejemplosQuitados > 0
                    ? "El archivo solo trae " + (ejemplosQuitados == 1 ? "el renglón" : "los renglones") + " de ejemplo de la plantilla: "
                    + "escriba sus datos debajo de los títulos (puede borrar el ejemplo) y vuelva a subirlo. No se guardó nada."
                    : "El archivo está vacío: no tiene renglones con datos debajo de los títulos (fila 1). No se guardó nada.");
        }
        if (r.size() > MAXIMO_RENGLONES) {
            throw new Ilegible("El archivo tiene " + r.size() + " renglones; el máximo es " + MAXIMO_RENGLONES + ".");
        }
        return r;
    }

    /** ¿Es el renglón igual a un ejemplo de la plantilla? Sin contar las columnas de fecha ni mayúsculas. */
    public static boolean esEjemplo(String[] v, String[][] ejemplos, Set<Integer> fechas) {
        for (String[] e : ejemplos) {
            boolean igual = true;
            for (int j = 0; j < v.length && igual; j++) {
                if (!fechas.contains(j)) {
                    String ej = j < e.length && e[j] != null ? e[j].trim() : "";
                    igual = ej.equalsIgnoreCase(v[j]);
                }
            }
            if (igual) {
                return true;
            }
        }
        return false;
    }

    private static String celda(Cell cel, DataFormatter fmt, boolean esFecha) {
        if (cel == null) {
            return "";
        }
        if (esFecha && cel.getCellTypeEnum() == CellType.NUMERIC) {
            double n = cel.getNumericCellValue();
            // Fecha con formato, o número de serie de Excel sin formato (1960-2150).
            if (DateUtil.isCellDateFormatted(cel) || (n > 21916 && n < 91311 && n == Math.floor(n))) {
                LocalDate d = DateUtil.getJavaDate(n).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                return d.format(DateTimeFormatter.ISO_LOCAL_DATE);
            }
        }
        return fmt.formatCellValue(cel).replace(' ', ' ').trim();
    }

    /** Fecha en aaaa-mm-dd, dd/mm/aaaa o dd-mm-aaaa. Agrega el error a {@code mal} y regresa null si no sirve. */
    public static LocalDate fecha(String v, String nombre, boolean obligatoria, List<String> mal) {
        if (v.isEmpty()) {
            if (obligatoria) {
                mal.add("falta " + nombre);
            }
            return null;
        }
        try {
            if (v.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                return LocalDate.parse(v.length() == 10 ? v : String.format("%s-%02d-%02d", v.substring(0, 4),
                        Integer.parseInt(v.split("-")[1]), Integer.parseInt(v.split("-")[2])));
            }
            return v.contains("-") ? LocalDate.parse(v, DMA_GUION) : LocalDate.parse(v, DMA);
        } catch (DateTimeParseException | NumberFormatException ex) {
            mal.add(nombre + " (" + v + ") no es una fecha dd/mm/aaaa");
            return null;
        }
    }

    /** Entero mayor a 0 (acepta "10", "10.0" y "1,000"). */
    public static int cantidad(String v, String nombre, List<String> mal) {
        if (v.isEmpty()) {
            mal.add("falta " + nombre);
            return 0;
        }
        try {
            double d = Double.parseDouble(v.replace(",", ""));
            if (d <= 0 || d != Math.floor(d) || d > Integer.MAX_VALUE) {
                mal.add(nombre + " debe ser un entero mayor a 0");
                return 0;
            }
            return (int) d;
        } catch (NumberFormatException ex) {
            mal.add(nombre + " (" + v + ") no es un número");
            return 0;
        }
    }

    /** Quita el ".0" con que Excel muestra a veces los números enteros. */
    public static String sinDecimalCero(String v) {
        return v.matches("-?\\d+\\.0+") ? v.substring(0, v.indexOf('.')) : v;
    }

    /**
     * Plantilla: hoja de datos (títulos, las primeras {@code obligatorias} con *, y ejemplos), hoja "Catálogos" e
     * "Instrucciones".
     */
    public static void plantilla(OutputStream salida, String hoja, String[] columnas, int obligatorias,
                                 String[][] ejemplos, List<Bloque> catalogos, List<String> instrucciones) throws IOException {
        try (Workbook wb = new XSSFWorkbook()) {
            CellStyle negritas = wb.createCellStyle();
            Font f = wb.createFont();
            f.setBold(true);
            negritas.setFont(f);
            CellStyle texto = wb.createCellStyle();
            texto.setDataFormat(wb.createDataFormat().getFormat("@"));

            Sheet s = wb.createSheet(hoja);
            Row h = s.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cel = h.createCell(i);
                cel.setCellValue(columnas[i] + (i < obligatorias ? " *" : ""));
                cel.setCellStyle(negritas);
                s.setColumnWidth(i, 20 * 256);
                s.setDefaultColumnStyle(i, texto); // texto: las claves no pierden ceros ni decimales
            }
            for (int r = 0; r < ejemplos.length; r++) {
                Row e = s.createRow(r + 1);
                for (int i = 0; i < ejemplos[r].length; i++) {
                    Cell cel = e.createCell(i);
                    cel.setCellStyle(texto);
                    cel.setCellValue(ejemplos[r][i]);
                }
            }
            if (!catalogos.isEmpty()) {
                Sheet cat = wb.createSheet("Catálogos");
                int r = 0;
                for (Bloque b : catalogos) {
                    Cell t = cat.createRow(r++).createCell(0);
                    t.setCellValue(b.titulo());
                    t.setCellStyle(negritas);
                    for (Fila fila : b.filas()) {
                        Row row = cat.createRow(r++);
                        row.createCell(0).setCellValue(fila.get(1));
                        row.createCell(1).setCellValue(fila.get(2));
                    }
                    r++;
                }
                cat.setColumnWidth(0, 16 * 256);
                cat.setColumnWidth(1, 60 * 256);
            }
            Sheet ayuda = wb.createSheet("Instrucciones");
            instrucciones = new ArrayList<>(instrucciones);
            instrucciones.add("El renglón de ejemplo no se carga: escriba sus datos debajo de los títulos (puede borrarlo). Un archivo sin datos se rechaza.");
            for (int i = 0; i < instrucciones.size(); i++) {
                ayuda.createRow(i).createCell(0).setCellValue(instrucciones.get(i));
            }
            ayuda.setColumnWidth(0, 130 * 256);
            wb.write(salida);
        }
    }
}
