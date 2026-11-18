package mx.medalfa.sialss.cargas;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import mx.medalfa.sialss.dao.Consultas;
import mx.medalfa.sialss.dao.Fila;
import mx.medalfa.sialss.seguridad.Acceso;

/**
 * Carga del catálogo de marcas (tb_marca). Se guarda en mayúsculas, como el alta de marcas (servlets.Marca).
 * Una marca igual o parecida a una registrada, o repetida en el archivo, se omite ({@link Parecidos}).
 */
public final class CargaMarcas implements CargaArchivo {

    static final String[] COLUMNAS = {"Marca"};

    record Datos(ArrayList<String> marcas, int omitidos) implements Serializable {
    }

    /** Renglones de ejemplo de la plantilla: no se cargan si el archivo los trae tal cual. */
    static String[][] ejemplos() {
        return new String[][]{{"MARCA DE EJEMPLO"}};
    }

    @Override
    public String nombre() {
        return "marcas";
    }

    @Override
    public String pagina() {
        return "CargaArchivoMarcas.jsp";
    }

    @Override
    public Set<Acceso> accesos() {
        return Set.of(Acceso.ALMACEN);
    }

    @Override
    public void plantilla(Connection c, OutputStream salida) throws IOException, SQLException {
        Excel.plantilla(salida, "Marcas", COLUMNAS, 1, ejemplos(),
                List.of(new Excel.Bloque("Marcas ya registradas", Consultas.lista(c, "SELECT F_ClaMar, F_DesMar FROM tb_marca ORDER BY F_DesMar"))),
                List.of("Carga del catálogo de marcas SIALSS. Un renglón por marca (hasta 80 caracteres); se guarda en mayúsculas.",
                        "No se duplican: una marca igual o parecida a una registrada (sin importar acentos, mayúsculas, signos o espacios, "
                                + "o con una letra distinta) se omite, igual que las repetidas en el archivo. La revisión dice a cuál se parece.",
                        "Máximo " + Excel.MAXIMO_RENGLONES + " renglones. Si un renglón tiene error no se carga nada: corrija el archivo y vuelva a subirlo."));
    }

    @Override
    public Revision revisar(Connection c, InputStream archivo, Usuario u) throws IOException, SQLException {
        List<Excel.Renglon> crudos;
        try {
            crudos = Excel.leer(archivo, COLUMNAS.length, Set.of(), ejemplos());
        } catch (Excel.Ilegible e) {
            return Revision.conError(e.getMessage());
        }
        Parecidos registradas = new Parecidos(false);
        for (Fila f : Consultas.lista(c, "SELECT F_DesMar FROM tb_marca")) {
            registradas.agregar(f.get(1));
        }
        Parecidos archivo2 = new Parecidos(false);
        List<String> errores = new ArrayList<>();
        ArrayList<String> marcas = new ArrayList<>();
        List<List<String>> muestra = new ArrayList<>();
        Omitidos omitidos = new Omitidos();
        for (Excel.Renglon cr : crudos) {
            List<String> mal = new ArrayList<>();
            String marca = Campos.obligatorio(cr.v()[0], 80, "la marca", mal);
            if (!mal.isEmpty()) {
                errores.add("Renglón " + cr.numero() + ": " + String.join("; ", mal) + ".");
                continue;
            }
            marca = marca.toUpperCase(Locale.ROOT);
            Parecidos.Coincidencia ya = registradas.buscar(marca);
            Parecidos.Coincidencia rep = ya == null ? archivo2.buscar(marca) : null;
            if (ya != null || rep != null) {
                omitidos.agregar(cr.numero(), marca + ": " + (ya != null ? ya.motivo("la marca") : "se repite en el archivo con \"" + rep.nombre() + "\""));
                continue;
            }
            archivo2.agregar(marca);
            marcas.add(marca);
            if (muestra.size() < MAX_MUESTRA) {
                muestra.add(List.of(String.valueOf(cr.numero()), marca));
            }
        }
        if (!errores.isEmpty()) {
            return new Revision(errores, muestra, "", null);
        }
        if (marcas.isEmpty()) {
            return omitidos.sinNuevos(crudos.size(), "marcas nuevas");
        }
        return omitidos.valida(muestra, Texto.cuantos(marcas.size(), "marca nueva", "marcas nuevas") + ".",
                new Datos(marcas, omitidos.cuantos()));
    }

    @Override
    public String aplicar(Connection c, Revision r, Usuario u) throws SQLException {
        Datos d = (Datos) r.datos();
        boolean auto = c.getAutoCommit();
        c.setAutoCommit(false);
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO tb_marca (F_DesMar) VALUES (?)")) {
            for (String m : d.marcas()) {
                ps.setString(1, m);
                ps.addBatch();
            }
            ps.executeBatch();
            c.commit();
        } catch (SQLException | RuntimeException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(auto);
        }
        return "Se cargaron " + Texto.cuantos(d.marcas().size(), "marca", "marcas") + "."
                + Omitidos.mensajeFinal(d.omitidos());
    }
}
