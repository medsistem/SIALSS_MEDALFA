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
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import mx.medalfa.sialss.dao.Consultas;
import mx.medalfa.sialss.dao.Fila;
import mx.medalfa.sialss.seguridad.Acceso;

/**
 * Carga del catálogo de unidades de atención (tb_uniatn), con las mismas reglas que el alta (servlets.AltaUnidad):
 * estatus A, jurisdicción del municipio (tb_muniis), nivel y tipo de farmacia del tipo de unidad (tb_tipunidad),
 * ruta R1001. A diferencia de AltaUnidad, el INSERT nombra sus columnas y guarda el proyecto de la unidad.
 * No se duplican: misma clave, nombre igual o parecido, o la misma CLUES.
 */
public final class CargaUnidades implements CargaArchivo {

    static final String[] COLUMNAS = {"Clave de la unidad", "Nombre", "Tipo de unidad", "Municipio", "Proyecto",
            "Dirección", "CLUES", "Nivel de unidad", "Ruta"};

    record Unidad(String clave, String nombre, int tipo, int municipio, String jurisdiccion, int proyecto, String direccion,
                  String clues, int nivel, String tipoFarmacia, String ruta) implements Serializable {
    }

    record Datos(ArrayList<Unidad> unidades, int omitidos) implements Serializable {
    }

    /** Renglones de ejemplo de la plantilla: no se cargan si el archivo los trae tal cual. */
    static String[][] ejemplos() {
        return new String[][]{{"U-001", "CENTRO DE SALUD DE EJEMPLO", "3", "1", "1", "AV. EJEMPLO 10", "MCSSA000000", "", ""}};
    }

    @Override
    public String nombre() {
        return "unidades";
    }

    @Override
    public String pagina() {
        return "CargaArchivoUnidades.jsp";
    }

    @Override
    public Set<Acceso> accesos() {
        return Set.of(Acceso.ALMACEN);
    }

    @Override
    public void plantilla(Connection c, OutputStream salida) throws IOException, SQLException {
        Excel.plantilla(salida, "Unidades", COLUMNAS, 5,
                ejemplos(),
                List.of(new Excel.Bloque("Tipo de unidad (columna C)", Consultas.lista(c,
                                "SELECT F_idTipUni, CONCAT(F_NomUni, ' (', F_NomeUnidad, ', nivel ', F_NivelUni, ')') FROM tb_tipunidad ORDER BY F_idTipUni")),
                        new Excel.Bloque("Municipio (columna D)", Consultas.lista(c,
                                "SELECT F_ClaMunIS, CONCAT(F_DesMunIS, ' · jurisdicción ', F_JurMunIS) FROM tb_muniis ORDER BY F_ClaMunIS")),
                        new Excel.Bloque("Proyecto (columna E)", Consultas.lista(c, "SELECT F_Id, F_DesProy FROM tb_proyectos ORDER BY F_Id")),
                        new Excel.Bloque("Unidades ya registradas", Consultas.lista(c, "SELECT F_ClaCli, F_NomCli FROM tb_uniatn ORDER BY F_ClaCli"))),
                List.of("Carga del catálogo de unidades SIALSS. Un renglón por unidad.",
                        "Obligatorias: Clave (hasta 10 caracteres), Nombre, Tipo de unidad, Municipio y Proyecto (números de la hoja Catálogos).",
                        "La jurisdicción sale del municipio. Nivel de unidad vacío = el del tipo de unidad. Ruta vacía = R1001.",
                        "No se duplican: una unidad con la misma clave, la misma CLUES o el nombre igual o parecido a una registrada "
                                + "se omite, igual que las repetidas en el archivo. La revisión dice a cuál se parece.",
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
        Map<String, Fila> tipos = new HashMap<>();
        for (Fila f : Consultas.lista(c, "SELECT F_idTipUni, F_NivelUni, F_NomeUnidad, F_NomUni FROM tb_tipunidad")) {
            tipos.put(f.get(1), f);
        }
        Map<String, Fila> municipios = new HashMap<>();
        for (Fila f : Consultas.lista(c, "SELECT F_ClaMunIS, F_JurMunIS, F_DesMunIS FROM tb_muniis")) {
            municipios.put(f.get(1), f);
        }
        Map<String, String> proyectos = new HashMap<>();
        for (Fila f : Consultas.lista(c, "SELECT F_Id, F_DesProy FROM tb_proyectos")) {
            proyectos.put(f.get(1), f.get(2));
        }
        Map<String, String> claves = new HashMap<>();
        Map<String, String> clues = new HashMap<>();
        Parecidos nombres = new Parecidos(false);
        for (Fila f : Consultas.lista(c, "SELECT F_ClaCli, F_NomCli, F_Clues FROM tb_uniatn")) {
            claves.put(f.get(1).trim().toUpperCase(Locale.ROOT), f.get(2));
            nombres.agregar(f.get(2));
            if (f.get(3) != null && f.get(3).trim().length() > 3) {
                clues.putIfAbsent(f.get(3).trim().toUpperCase(Locale.ROOT), f.get(2));
            }
        }
        Parecidos nombresArchivo = new Parecidos(false);
        Set<String> clavesArchivo = new HashSet<>();
        Set<String> cluesArchivo = new HashSet<>();
        List<String> errores = new ArrayList<>();
        ArrayList<Unidad> nuevas = new ArrayList<>();
        List<List<String>> muestra = new ArrayList<>();
        Omitidos omitidos = new Omitidos();
        for (Excel.Renglon cr : crudos) {
            String[] v = cr.v();
            List<String> mal = new ArrayList<>();
            String clave = Campos.obligatorio(Excel.sinDecimalCero(v[0]), 10, "la clave", mal);
            String nombre = Campos.obligatorio(v[1], 150, "el nombre", mal);
            Fila tipo = tipos.get(Excel.sinDecimalCero(v[2]));
            if (v[2].isEmpty()) {
                mal.add("falta el tipo de unidad");
            } else if (tipo == null) {
                mal.add("el tipo de unidad " + v[2] + " no existe (hoja Catálogos)");
            }
            Fila mun = municipios.get(Excel.sinDecimalCero(v[3]));
            if (v[3].isEmpty()) {
                mal.add("falta el municipio");
            } else if (mun == null) {
                mal.add("el municipio " + v[3] + " no existe (hoja Catálogos)");
            }
            String proyecto = Excel.sinDecimalCero(v[4]);
            if (proyecto.isEmpty()) {
                mal.add("falta el proyecto");
            } else if (!proyectos.containsKey(proyecto)) {
                mal.add("el proyecto " + proyecto + " no existe");
            }
            String direccion = Campos.texto(v[5], 250, "la dirección", mal);
            String cluesU = Campos.texto(v[6], 100, "la CLUES", mal);
            int nivel = 0;
            if (!v[7].isEmpty()) {
                try {
                    nivel = Integer.parseInt(Excel.sinDecimalCero(v[7]));
                    if (nivel < 1 || nivel > 99) {
                        mal.add("el nivel de unidad debe ser de 1 a 99");
                    }
                } catch (NumberFormatException ex) {
                    mal.add("el nivel de unidad (" + v[7] + ") no es un número");
                }
            } else if (tipo != null) {
                nivel = Integer.parseInt(tipo.get(2));
            }
            String ruta = Campos.texto(v[8], 20, "la ruta", mal);
            if (!mal.isEmpty()) {
                errores.add("Renglón " + cr.numero() + ": " + String.join("; ", mal) + ".");
                continue;
            }
            String claveM = clave.toUpperCase(Locale.ROOT);
            String cluesM = cluesU == null ? "" : cluesU.toUpperCase(Locale.ROOT);
            Parecidos.Coincidencia ya = nombres.buscar(nombre);
            String motivo = claves.containsKey(claveM) ? "la clave " + clave + " ya es de \"" + claves.get(claveM) + "\""
                    : clues.containsKey(cluesM) ? "la CLUES " + cluesU + " ya es de \"" + clues.get(cluesM) + "\""
                    : ya != null ? ya.motivo("la unidad") : null;
            if (motivo == null) {
                Parecidos.Coincidencia rep = nombresArchivo.buscar(nombre);
                motivo = clavesArchivo.contains(claveM) ? "la clave " + clave + " se repite en el archivo"
                        : !cluesM.isEmpty() && cluesArchivo.contains(cluesM) ? "la CLUES " + cluesU + " se repite en el archivo"
                        : rep != null ? "se repite en el archivo con \"" + rep.nombre() + "\"" : null;
            }
            if (motivo != null) {
                omitidos.agregar(cr.numero(), clave + " " + nombre + ": " + motivo);
                continue;
            }
            nombresArchivo.agregar(nombre);
            clavesArchivo.add(claveM);
            if (!cluesM.isEmpty()) {
                cluesArchivo.add(cluesM);
            }
            Unidad un = new Unidad(clave, nombre, Integer.parseInt(tipo.get(1)), Integer.parseInt(mun.get(1)), mun.get(2),
                    Integer.parseInt(proyecto), direccion, cluesU, nivel, tipo.get(3), ruta == null ? "R1001" : ruta);
            nuevas.add(un);
            if (muestra.size() < 200) {
                muestra.add(List.of(String.valueOf(cr.numero()), clave, nombre, tipo.get(4), mun.get(3) + " (jur. " + mun.get(2) + ")",
                        proyectos.get(proyecto), Campos.oGuion(cluesU), String.valueOf(nivel)));
            }
        }
        if (!errores.isEmpty()) {
            return new Revision(errores, muestra, "", null);
        }
        if (nuevas.isEmpty()) {
            return omitidos.sinNuevos(crudos.size(), "unidades nuevas");
        }
        return omitidos.valida(muestra, Texto.cuantos(nuevas.size(), "unidad nueva", "unidades nuevas") + ".",
                new Datos(nuevas, omitidos.cuantos()));
    }

    @Override
    public String aplicar(Connection c, Revision r, Usuario u) throws SQLException {
        Datos d = (Datos) r.datos();
        boolean auto = c.getAutoCommit();
        c.setAutoCommit(false);
        try (PreparedStatement ps = c.prepareStatement("INSERT INTO tb_uniatn (F_ClaCli, F_NomCli, F_StsCli, F_ClaJur, F_ClaJurNum, "
                + "F_Tipo, F_ClaMun, F_Direc, F_Ruta, F_Dispen, F_Razon, F_Clues, F_Proyecto, F_IdReporte, F_NivelUni, F_TipFarmacia, "
                + "F_Nivel) VALUES (?, ?, 'A', ?, ?, ?, ?, ?, ?, '', '', ?, ?, '0', ?, ?, '1')")) {
            for (Unidad x : d.unidades()) {
                Consultas.asignar(ps, new Object[]{x.clave(), x.nombre(), x.jurisdiccion(), x.municipio(), x.tipo(), x.municipio(),
                        x.direccion() == null ? "" : x.direccion(), x.ruta(), x.clues() == null ? "" : x.clues(), x.proyecto(),
                        x.nivel(), x.tipoFarmacia()});
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
        return "Se cargaron " + Texto.cuantos(d.unidades().size(), "unidad", "unidades") + "." + Omitidos.mensajeFinal(d.omitidos());
    }
}
