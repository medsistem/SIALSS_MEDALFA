package mx.medalfa.sialss.manual;

import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.ListItem;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import mx.medalfa.sialss.manual.ManualUsuario.Bloque;
import mx.medalfa.sialss.manual.ManualUsuario.Capitulo;
import mx.medalfa.sialss.manual.ManualUsuario.Manual;
import mx.medalfa.sialss.manual.ManualUsuario.Seccion;
import mx.medalfa.sialss.manual.ManualUsuario.Segmento;

/**
 * Manual de usuario en PDF con iText 2.1.7 (com.lowagie): portada, contenido, generalidades y un capítulo por
 * módulo del menú del usuario. Los capítulos y las opciones quedan como marcadores del PDF.
 * Helvetica con WINANSI cubre acentos, ñ, ¿ y ¡.
 */
public final class ManualPdf {

    private static final Color PRIMARIO = new Color(0x1F, 0x4E, 0x79);
    private static final Color TEXTO = new Color(0x22, 0x22, 0x22);
    private static final Color SECUNDARIO = new Color(0x66, 0x66, 0x66);
    private static final Color FONDO_TABLA = new Color(0xE8, 0xEE, 0xF4);
    private static final Color FONDO_NOTA = new Color(0xFF, 0xF6, 0xD9);

    private final BaseFont normal;
    private final BaseFont negrita;
    private final BaseFont mono;

    private ManualPdf() throws DocumentException, IOException {
        normal = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        negrita = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
        mono = BaseFont.createFont(BaseFont.COURIER, BaseFont.WINANSI, BaseFont.NOT_EMBEDDED);
    }

    public static void escribir(Manual manual, String usuario, OutputStream out) throws DocumentException, IOException {
        new ManualPdf().generar(manual, usuario, out);
    }

    private Font f(BaseFont b, float tam, Color c) {
        return new Font(b, tam, Font.NORMAL, c);
    }

    private void generar(Manual manual, String usuario, OutputStream out) throws DocumentException, IOException {
        Document doc = new Document(PageSize.LETTER, 60, 60, 60, 60);
        PdfWriter w = PdfWriter.getInstance(doc, out);
        w.setPageEvent(new Pie(manual));
        doc.addTitle("Manual de usuario SIALSS_MDF");
        doc.addSubject("Acceso " + manual.acceso() + ", perfil " + manual.perfil());
        doc.addCreator("SIALSS_MDF");
        doc.open();

        portada(doc, manual, usuario);

        int numero = 0;
        if (!manual.generales().isEmpty()) {
            Chapter cap = capitulo("Generalidades", ++numero);
            for (Seccion s : manual.generales()) {
                Section sec = seccion(cap, s.titulo());
                bloques(sec, s.bloques());
            }
            doc.add(cap);
        }
        for (Capitulo c : manual.capitulos()) {
            Chapter cap = capitulo(c.titulo(), ++numero);
            for (Element e : elementos(c.introduccion())) {
                cap.add(e);
            }
            for (Seccion s : c.secciones()) {
                Section sec = seccion(cap, s.titulo());
                bloques(sec, s.bloques());
            }
            doc.add(cap);
        }
        doc.close();
    }

    private void portada(Document doc, Manual m, String usuario) throws DocumentException {
        Paragraph espacio = new Paragraph(" ");
        espacio.setSpacingBefore(120);
        doc.add(espacio);
        Paragraph t = new Paragraph("Manual de usuario", f(negrita, 30, PRIMARIO));
        doc.add(t);
        doc.add(new Paragraph("SIALSS_MDF", f(normal, 18, SECUNDARIO)));
        Paragraph datos = new Paragraph();
        datos.setSpacingBefore(40);
        datos.setLeading(18);
        datos.add(new Chunk("Acceso: ", f(negrita, 11, TEXTO)));
        datos.add(new Chunk(m.acceso() + "\n", f(normal, 11, TEXTO)));
        datos.add(new Chunk("Perfil: ", f(negrita, 11, TEXTO)));
        datos.add(new Chunk(m.perfil() + (m.nombrePerfil().isBlank() ? "" : " · " + m.nombrePerfil()) + "\n", f(normal, 11, TEXTO)));
        if (usuario != null && !usuario.isBlank()) {
            datos.add(new Chunk("Generado para: ", f(negrita, 11, TEXTO)));
            datos.add(new Chunk(usuario + "\n", f(normal, 11, TEXTO)));
        }
        datos.add(new Chunk("Fecha: ", f(negrita, 11, TEXTO)));
        datos.add(new Chunk(LocalDate.now().format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy",
                new Locale("es", "MX"))) + "\n", f(normal, 11, TEXTO)));
        if (!m.version().isBlank()) {
            datos.add(new Chunk("Versión del manual: ", f(negrita, 11, TEXTO)));
            datos.add(new Chunk(m.version() + "\n", f(normal, 11, TEXTO)));
        }
        doc.add(datos);

        Paragraph nota = new Paragraph("Este manual contiene solo los módulos y opciones que este perfil tiene en su menú.",
                f(normal, 10, SECUNDARIO));
        nota.setSpacingBefore(30);
        doc.add(nota);

        doc.newPage();
        doc.add(new Paragraph("Contenido", f(negrita, 18, PRIMARIO)));
        int n = 0;
        if (!m.generales().isEmpty()) {
            doc.add(linea(++n + ". Generalidades", true));
        }
        for (Capitulo c : m.capitulos()) {
            doc.add(linea(++n + ". " + c.titulo(), true));
            int s = 0;
            for (Seccion sec : c.secciones()) {
                doc.add(linea("      " + n + "." + (++s) + "  " + sec.titulo(), false));
            }
        }
    }

    private Paragraph linea(String t, boolean principal) {
        Paragraph p = new Paragraph(t, principal ? f(negrita, 11, TEXTO) : f(normal, 10, TEXTO));
        p.setSpacingBefore(principal ? 6 : 1);
        return p;
    }

    private Chapter capitulo(String titulo, int numero) {
        Paragraph t = new Paragraph(titulo, f(negrita, 20, PRIMARIO));
        t.setSpacingAfter(10);
        Chapter c = new Chapter(t, numero);
        c.setNumberDepth(1);
        c.setBookmarkTitle(titulo);
        return c;
    }

    private Section seccion(Chapter cap, String titulo) {
        Paragraph t = new Paragraph(titulo, f(negrita, 14, PRIMARIO));
        t.setSpacingBefore(14);
        t.setSpacingAfter(4);
        Section s = cap.addSection(t);
        s.setNumberDepth(2);
        s.setBookmarkTitle(titulo);
        s.setBookmarkOpen(false);
        return s;
    }

    private void bloques(Section sec, List<Bloque> bloques) {
        for (Element e : elementos(bloques)) {
            sec.add(e);
        }
    }

    private List<Element> elementos(List<Bloque> bloques) {
        List<Element> r = new java.util.ArrayList<>();
        for (Bloque b : bloques) {
            switch (b.tipo()) {
                case TITULO -> {
                    Paragraph p = new Paragraph(texto(b.lineas().get(0), 11.5f, negrita));
                    p.setSpacingBefore(8);
                    p.setSpacingAfter(2);
                    r.add(p);
                }
                case PARRAFO -> {
                    Paragraph p = new Paragraph(texto(b.lineas().get(0), 10, normal));
                    p.setSpacingAfter(5);
                    p.setLeading(14);
                    r.add(p);
                }
                case NOTA -> {
                    PdfPTable t = new PdfPTable(1);
                    t.setWidthPercentage(100);
                    t.setSpacingBefore(3);
                    t.setSpacingAfter(6);
                    PdfPCell c = new PdfPCell(texto(b.lineas().get(0), 9.5f, normal));
                    c.setBackgroundColor(FONDO_NOTA);
                    c.setBorder(Rectangle.LEFT);
                    c.setBorderWidthLeft(3);
                    c.setBorderColorLeft(new Color(0xE0, 0xA8, 0x00));
                    c.setPadding(6);
                    t.addCell(c);
                    r.add(t);
                }
                case LISTA, LISTA_NUMERADA -> {
                    com.lowagie.text.List l = new com.lowagie.text.List(b.tipo() == ManualUsuario.Tipo.LISTA_NUMERADA, 16);
                    if (b.tipo() == ManualUsuario.Tipo.LISTA) {
                        l.setListSymbol(new Chunk("•  ", f(normal, 10, TEXTO)));
                    }
                    l.setIndentationLeft(8);
                    for (String x : b.lineas()) {
                        ListItem li = new ListItem(texto(x, 10, normal));
                        li.setLeading(14);
                        l.add(li);
                    }
                    Paragraph envoltura = new Paragraph();
                    envoltura.add(l);
                    envoltura.setSpacingAfter(5);
                    r.add(envoltura);
                }
                case TABLA -> {
                    int cols = b.filas().stream().mapToInt(List::size).max().orElse(1);
                    PdfPTable t = new PdfPTable(cols);
                    t.setWidthPercentage(100);
                    t.setSpacingBefore(3);
                    t.setSpacingAfter(7);
                    t.setHeaderRows(1);
                    for (int i = 0; i < b.filas().size(); i++) {
                        List<String> fila = b.filas().get(i);
                        for (int j = 0; j < cols; j++) {
                            String x = j < fila.size() ? fila.get(j) : "";
                            PdfPCell c = new PdfPCell(texto(x, 9, i == 0 ? negrita : normal));
                            c.setPadding(4);
                            c.setBorderColor(new Color(0xC8, 0xD2, 0xDC));
                            if (i == 0) {
                                c.setBackgroundColor(FONDO_TABLA);
                            }
                            t.addCell(c);
                        }
                    }
                    r.add(t);
                }
            }
        }
        return r;
    }

    /** Texto con negritas y código en línea. */
    private Phrase texto(String t, float tam, BaseFont base) {
        Phrase p = new Phrase();
        p.setLeading(tam * 1.4f);
        for (Segmento s : ManualUsuario.segmentos(t)) {
            BaseFont bf = s.codigo() ? mono : s.negrita() ? negrita : base;
            p.add(new Chunk(s.texto(), f(bf, tam, TEXTO)));
        }
        return p;
    }

    /** Pie: título y número de página (desde la segunda hoja). */
    private final class Pie extends PdfPageEventHelper {
        private final String titulo;

        Pie(Manual m) {
            titulo = "Manual de usuario SIALSS_MDF · " + m.acceso() + " · Perfil " + m.perfil();
        }

        @Override
        public void onEndPage(PdfWriter w, Document d) {
            if (w.getPageNumber() == 1) {
                return;
            }
            PdfContentByte cb = w.getDirectContent();
            Font fp = f(normal, 8, SECUNDARIO);
            ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase(titulo, fp), d.left(), d.bottom() - 25, 0);
            ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase("Página " + w.getPageNumber(), fp),
                    d.right(), d.bottom() - 25, 0);
        }
    }
}
