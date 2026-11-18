package mx.medalfa.sialss.cargas;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import mx.medalfa.sialss.dao.Consultas;
import mx.medalfa.sialss.dao.Fila;
import mx.medalfa.sialss.seguridad.Acceso;

/**
 * Carga del catálogo de proveedores (tb_proveedor). Nombre en mayúsculas como el alta (servlets.Proveedor); los datos que
 * no vienen se guardan con "-" (como InterfaceSendetoDaoImpl.INSERT_PROVEEDOR, todas las columnas son NOT NULL).
 * No se duplican: nombre igual o parecido sin contar la razón social (S.A. de C.V., S. de R.L.…), o el mismo RFC.
 */
public final class CargaProveedores implements CargaArchivo {

    static final String[] COLUMNAS = {"Nombre o razón social", "RFC", "Dirección", "Colonia", "Código postal", "Teléfono",
            "Fax", "Correo", "Población", "Clave SAP"};
    private static final int[] LARGOS = {150, 20, 150, 150, 10, 20, 20, 80, 80, 20};
    /** RFC de persona moral (12) o física (13); los demás valores ("0", "00000", "-") no se comparan. */
    private static final Pattern RFC = Pattern.compile("[A-ZÑ&]{3,4}[0-9]{6}[A-Z0-9]{3}");

    record Datos(ArrayList<String[]> proveedores, int omitidos) implements Serializable {
    }

    @Override
    public String nombre() {
        return "proveedores";
    }

    @Override
    public String pagina() {
        return "CargaArchivoProveedores.jsp";
    }

    @Override
    public Set<Acceso> accesos() {
        return Set.of(Acceso.ALMACEN);
    }

    @Override
    public void plantilla(Connection c, OutputStream salida) throws IOException, SQLException {
        Excel.plantilla(salida, "Proveedores", COLUMNAS, 1,
                new String[][]{{"PROVEEDOR DE EJEMPLO, S.A. DE C.V.", "PEJ010101AB1", "AV. EJEMPLO 100", "CENTRO", "50000",
                        "7220000000", "", "contacto@ejemplo.mx", "TOLUCA", ""}},
                List.of(new Excel.Bloque("Proveedores ya registrados", Consultas.lista(c,
                        "SELECT F_ClaProve, CONCAT(F_NomPro, '  ·  RFC ', F_Rfc) FROM tb_proveedor ORDER BY F_NomPro"))),
                List.of("Carga del catálogo de proveedores SIALSS. Un renglón por proveedor; solo el nombre es obligatorio.",
                        "El nombre se guarda en mayúsculas; los datos vacíos se guardan con \"-\".",
                        "No se duplican: un proveedor con el nombre igual o parecido a uno registrado (sin importar acentos, signos, "
                                + "espacios ni la razón social S.A. de C.V., S. de R.L., etc.) o con el mismo RFC se omite, "
                                + "igual que los repetidos en el archivo. La revisión dice a cuál se parece.",
                        "Máximo " + Excel.MAXIMO_RENGLONES + " renglones. Si un renglón tiene error no se carga nada: corrija el archivo y vuelva a subirlo."));
    }

    @Override
    public Revision revisar(Connection c, InputStream archivo, Usuario u) throws IOException, SQLException {
        List<Excel.Renglon> crudos;
        try {
            crudos = Excel.leer(archivo, COLUMNAS.length, Set.of());
        } catch (Excel.Ilegible e) {
            return Revision.conError(e.getMessage());
        }
        Parecidos registrados = new Parecidos(true);
        Map<String, String> rfcs = new HashMap<>();
        for (Fila f : Consultas.lista(c, "SELECT F_NomPro, F_Rfc FROM tb_proveedor")) {
            registrados.agregar(f.get(1));
            String rfc = f.get(2) == null ? "" : f.get(2).trim().toUpperCase(Locale.ROOT);
            if (RFC.matcher(rfc).matches()) {
                rfcs.putIfAbsent(rfc, f.get(1));
            }
        }
        Parecidos enArchivo = new Parecidos(true);
        Map<String, String> rfcArchivo = new HashMap<>();
        List<String> errores = new ArrayList<>();
        ArrayList<String[]> nuevos = new ArrayList<>();
        List<List<String>> muestra = new ArrayList<>();
        Omitidos omitidos = new Omitidos();
        for (Excel.Renglon cr : crudos) {
            List<String> mal = new ArrayList<>();
            String[] p = new String[COLUMNAS.length];
            for (int i = 0; i < COLUMNAS.length; i++) {
                String nombreCampo = i == 0 ? "el nombre" : "el campo " + COLUMNAS[i];
                p[i] = i == 0 ? Campos.obligatorio(cr.v()[i], LARGOS[i], nombreCampo, mal) : Campos.texto(cr.v()[i], LARGOS[i], nombreCampo, mal);
            }
            if (p[0] != null) {
                p[0] = p[0].toUpperCase(Locale.ROOT);
            }
            String rfc = p[1] == null ? "" : p[1].toUpperCase(Locale.ROOT).replaceAll("[\\s-]", "");
            if (p[1] != null) {
                p[1] = rfc;
                if (!RFC.matcher(rfc).matches()) {
                    mal.add("el RFC " + rfc + " no tiene el formato de 12 o 13 caracteres");
                }
            }
            if (p[7] != null && !p[7].matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
                mal.add("el correo " + p[7] + " no es válido");
            }
            if (!mal.isEmpty()) {
                errores.add("Renglón " + cr.numero() + ": " + String.join("; ", mal) + ".");
                continue;
            }
            Parecidos.Coincidencia ya = registrados.buscar(p[0]);
            String motivo = ya != null ? ya.motivo("el proveedor")
                    : rfcs.containsKey(rfc) ? "el RFC " + rfc + " ya es del proveedor \"" + rfcs.get(rfc) + "\"" : null;
            if (motivo == null) {
                Parecidos.Coincidencia rep = enArchivo.buscar(p[0]);
                motivo = rep != null ? "se repite en el archivo con \"" + rep.nombre() + "\""
                        : rfcArchivo.containsKey(rfc) ? "el RFC " + rfc + " se repite en el archivo" : null;
            }
            if (motivo != null) {
                omitidos.agregar(cr.numero(), p[0] + ": " + motivo);
                continue;
            }
            enArchivo.agregar(p[0]);
            if (!rfc.isEmpty()) {
                rfcArchivo.put(rfc, p[0]);
            }
            nuevos.add(p);
            if (muestra.size() < 200) {
                muestra.add(List.of(String.valueOf(cr.numero()), p[0], Campos.oGuion(p[1]), Campos.oGuion(p[2]),
                        Campos.oGuion(p[5]), Campos.oGuion(p[7])));
            }
        }
        if (!errores.isEmpty()) {
            return new Revision(errores, muestra, "", null);
        }
        if (nuevos.isEmpty()) {
            return omitidos.sinNuevos(crudos.size(), "proveedores nuevos");
        }
        return omitidos.valida(muestra, Texto.cuantos(nuevos.size(), "proveedor nuevo", "proveedores nuevos") + ".",
                new Datos(nuevos, omitidos.cuantos()));
    }

    @Override
    public String aplicar(Connection c, Revision r, Usuario u) throws SQLException {
        Datos d = (Datos) r.datos();
        boolean auto = c.getAutoCommit();
        c.setAutoCommit(false);
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO tb_proveedor (F_NomPro, F_Rfc, F_Dir, F_Col, F_Cp, F_Tel, "
                + "F_Fax, F_Mail, F_Poblacion, F_ClaSap) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            for (String[] p : d.proveedores()) {
                for (int i = 0; i < p.length; i++) {
                    ps.setString(i + 1, Campos.oGuion(p[i]));
                }
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
        return "Se cargaron " + Texto.cuantos(d.proveedores().size(), "proveedor", "proveedores") + "." + Omitidos.mensajeFinal(d.omitidos());
    }
}
