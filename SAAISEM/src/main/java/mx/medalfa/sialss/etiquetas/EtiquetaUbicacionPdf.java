package mx.medalfa.sialss.etiquetas;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.Barcode128;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * PDF de etiquetas de ubicación (iText 2.1.7 + ZXing, ambos ya en el proyecto). Todo vectorial: el código se imprime
 * nítido a cualquier resolución.
 * <ul>
 *   <li>{@link Tipo#POSTE}: 5 cm × 4 cm, código de barras centrado y abajo la ubicación; varias por hoja carta o A4,
 *       con guías de corte.</li>
 *   <li>{@link Tipo#BARRA}: 15 cm × 10 cm, una etiqueta por página del tamaño de la etiqueta.</li>
 *   <li>{@link Tipo#QR}: 15 cm × 10 cm, QR y ubicación, una por página del tamaño de la etiqueta.</li>
 * </ul>
 * El código (barras o QR) lleva la clave de la ubicación ({@code F_ClaUbi}), que es lo que buscan los lectores; el texto
 * grande es la descripción ({@code F_DesUbi}).
 */
public final class EtiquetaUbicacionPdf {

    public enum Tipo {
        POSTE(50, 40), BARRA(150, 100), QR(150, 100);

        public final float anchoMm;
        public final float altoMm;

        Tipo(float anchoMm, float altoMm) {
            this.anchoMm = anchoMm;
            this.altoMm = altoMm;
        }
    }

    public enum Papel {
        CARTA(215.9f, 279.4f), A4(210f, 297f);

        public final float anchoMm;
        public final float altoMm;

        Papel(float anchoMm, float altoMm) {
            this.anchoMm = anchoMm;
            this.altoMm = altoMm;
        }
    }

    public record Ubicacion(String clave, String descripcion) {
    }

    /** Margen mínimo de la hoja para etiquetas de poste (la mayoría de las impresoras no imprime en los últimos 4-5 mm). */
    static final float MARGEN_HOJA_MM = 5f;

    private static final Color NEGRO = Color.BLACK;
    private static final Color GUIA = new Color(170, 170, 170);

    private EtiquetaUbicacionPdf() {
    }

    public static float mm(float mm) {
        return mm * 72f / 25.4f;
    }

    /** Cuántas etiquetas de poste caben por hoja: {columnas, filas}. */
    public static int[] cuadricula(Papel papel) {
        int columnas = (int) Math.floor((papel.anchoMm - 2 * MARGEN_HOJA_MM + 0.001f) / Tipo.POSTE.anchoMm);
        int filas = (int) Math.floor((papel.altoMm - 2 * MARGEN_HOJA_MM + 0.001f) / Tipo.POSTE.altoMm);
        return new int[] {columnas, filas};
    }

    public static void generar(List<Ubicacion> ubicaciones, Tipo tipo, Papel papel, OutputStream salida)
            throws IOException, DocumentException {
        Rectangle pagina = tipo == Tipo.POSTE
                ? new Rectangle(mm(papel.anchoMm), mm(papel.altoMm))
                : new Rectangle(mm(tipo.anchoMm), mm(tipo.altoMm));
        Document doc = new Document(pagina, 0, 0, 0, 0);
        PdfWriter w = PdfWriter.getInstance(doc, salida);
        doc.addTitle("Etiquetas de ubicación");
        doc.addCreator("SIALSS");
        doc.open();
        PdfContentByte cb = w.getDirectContent();
        BaseFont negrita = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        BaseFont normal = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        if (ubicaciones.isEmpty()) {
            cb.beginText();
            cb.setFontAndSize(normal, 10);
            cb.showTextAligned(PdfContentByte.ALIGN_CENTER, "Sin ubicaciones seleccionadas", pagina.getWidth() / 2, pagina.getHeight() / 2, 0);
            cb.endText();
        } else if (tipo == Tipo.POSTE) {
            postes(doc, cb, ubicaciones, papel, negrita);
        } else {
            boolean primera = true;
            for (Ubicacion u : ubicaciones) {
                if (!primera) {
                    doc.newPage();
                }
                primera = false;
                if (tipo == Tipo.BARRA) {
                    barraGrande(cb, u, negrita, normal);
                } else {
                    qrGrande(cb, u, negrita, normal);
                }
            }
        }
        doc.close();
    }

    // ------------------------------------------------------------------ poste 5 x 4 cm

    private static void postes(Document doc, PdfContentByte cb, List<Ubicacion> ubicaciones, Papel papel, BaseFont negrita) {
        int[] cq = cuadricula(papel);
        int columnas = cq[0], filas = cq[1], porHoja = columnas * filas;
        float ancho = mm(Tipo.POSTE.anchoMm), alto = mm(Tipo.POSTE.altoMm);
        // Cuadrícula centrada en la hoja.
        float x0 = (mm(papel.anchoMm) - columnas * ancho) / 2;
        float yTope = mm(papel.altoMm) - (mm(papel.altoMm) - filas * alto) / 2;
        for (int i = 0; i < ubicaciones.size(); i++) {
            if (i > 0 && i % porHoja == 0) {
                doc.newPage();
            }
            int pos = i % porHoja;
            float x = x0 + (pos % columnas) * ancho;
            float y = yTope - (pos / columnas + 1) * alto;
            guiaCorte(cb, x, y, ancho, alto);
            Ubicacion u = ubicaciones.get(i);
            // Código: hasta 44 mm de ancho (3 mm libres a cada lado) y 17 mm de alto; abajo la ubicación.
            codigoBarras(cb, u.clave(), x + ancho / 2, y + mm(17), mm(44), mm(17));
            texto(cb, negrita, u.descripcion(), x + ancho / 2, y + mm(6), mm(46), 13);
        }
    }

    private static void guiaCorte(PdfContentByte cb, float x, float y, float ancho, float alto) {
        cb.saveState();
        cb.setColorStroke(GUIA);
        cb.setLineWidth(0.3f);
        cb.setLineDash(2f, 2f, 0f);
        cb.rectangle(x, y, ancho, alto);
        cb.stroke();
        cb.restoreState();
    }

    // ------------------------------------------------------------------ barra 15 x 10 cm

    private static void barraGrande(PdfContentByte cb, Ubicacion u, BaseFont negrita, BaseFont normal) {
        float ancho = mm(Tipo.BARRA.anchoMm), alto = mm(Tipo.BARRA.altoMm);
        rotulo(cb, normal, "UBICACIÓN", ancho / 2, alto - mm(11));
        codigoBarras(cb, u.clave(), ancho / 2, mm(38), mm(130), mm(42));
        texto(cb, negrita, u.descripcion(), ancho / 2, mm(14), mm(134), 54);
    }

    // ------------------------------------------------------------------ QR 15 x 10 cm

    private static void qrGrande(PdfContentByte cb, Ubicacion u, BaseFont negrita, BaseFont normal) {
        float alto = mm(Tipo.QR.altoMm);
        float lado = mm(80);
        float x = mm(8), y = (alto - lado) / 2;
        qr(cb, u.clave(), x, y, lado);
        // A la derecha la ubicación, partida por guiones para que se lea de lejos (2PS01 / L1 / F1).
        float centro = x + lado + (mm(Tipo.QR.anchoMm) - x - lado) / 2;
        float disponible = mm(Tipo.QR.anchoMm) - x - lado - mm(8);
        rotulo(cb, normal, "UBICACIÓN", centro, alto - mm(14));
        String[] partes = u.descripcion().split("-");
        if (partes.length < 2 || partes.length > 4) {
            partes = new String[] {u.descripcion()};
        }
        float paso = mm(17);
        float yTexto = alto / 2 + (partes.length - 1) * paso / 2 - mm(4);
        for (String parte : partes) {
            texto(cb, negrita, parte.trim(), centro, yTexto, disponible, 40);
            yTexto -= paso;
        }
    }

    // ------------------------------------------------------------------ piezas

    /** Code 128 sin texto propio, escalado para caber en el ancho y alto dados, centrado en (cx, yBase). */
    private static void codigoBarras(PdfContentByte cb, String valor, float cx, float yBase, float anchoMax, float alto) {
        Barcode128 b = new Barcode128();
        b.setCodeType(Barcode128.CODE128);
        b.setCode(valor);
        b.setFont(null);
        b.setX(1f);
        b.setBarHeight(alto);
        PdfTemplate t = b.createTemplateWithBarcode(cb, NEGRO, NEGRO);
        // Ancho del módulo: lo más grueso posible sin pasar el ancho (mínimo legible ~0.25 mm).
        float escala = Math.min(anchoMax / t.getWidth(), mm(0.6f));
        float ancho = t.getWidth() * escala;
        cb.addTemplate(t, escala, 0, 0, 1, cx - ancho / 2, yBase);
    }

    private static void qr(PdfContentByte cb, String valor, float x, float y, float lado) {
        BitMatrix m;
        try {
            m = new QRCodeWriter().encode(valor, BarcodeFormat.QR_CODE, 0, 0,
                    Map.of(EncodeHintType.MARGIN, 0, EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M,
                            EncodeHintType.CHARACTER_SET, "UTF-8"));
        } catch (WriterException e) {
            throw new IllegalArgumentException("No se pudo generar el QR de " + valor, e);
        }
        float modulo = lado / m.getWidth();
        cb.saveState();
        cb.setColorFill(NEGRO);
        for (int fila = 0; fila < m.getHeight(); fila++) {
            int col = 0;
            while (col < m.getWidth()) {
                if (!m.get(col, fila)) {
                    col++;
                    continue;
                }
                int ini = col;
                while (col < m.getWidth() && m.get(col, fila)) {
                    col++;
                }
                // Un rectángulo por tramo negro de la fila (y un poco de traslape para que no queden rayas finas).
                cb.rectangle(x + ini * modulo, y + lado - (fila + 1) * modulo, (col - ini) * modulo, modulo + 0.05f);
            }
        }
        cb.fill();
        cb.restoreState();
    }

    /** Texto centrado en una línea; reduce el tamaño hasta que quepa en el ancho. */
    private static void texto(PdfContentByte cb, BaseFont f, String t, float cx, float y, float anchoMax, float tamano) {
        float tam = tamano;
        while (tam > 6 && f.getWidthPoint(t, tam) > anchoMax) {
            tam -= 0.5f;
        }
        cb.beginText();
        cb.setColorFill(NEGRO);
        cb.setFontAndSize(f, tam);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, t, cx, y, 0);
        cb.endText();
    }

    private static void rotulo(PdfContentByte cb, BaseFont f, String t, float cx, float y) {
        cb.beginText();
        cb.setColorFill(new Color(80, 80, 80));
        cb.setFontAndSize(f, 11);
        cb.setCharacterSpacing(1.5f);
        cb.showTextAligned(PdfContentByte.ALIGN_CENTER, t, cx, y, 0);
        cb.setCharacterSpacing(0f);
        cb.endText();
    }
}
