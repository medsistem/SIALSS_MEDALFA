package mx.medalfa.sialss.cargas;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import mx.medalfa.sialss.dao.Consultas;
import mx.medalfa.sialss.dao.Fila;
import mx.medalfa.sialss.proyectos.CamposProyecto;
import mx.medalfa.sialss.proyectos.InsumoProyectos;
import mx.medalfa.sialss.seguridad.Acceso;

/**
 * Carga del catálogo de productos (insumos: medicamento, material de curación… en tb_medica), con los mismos valores
 * que Alta de Claves (servlets.Medicamentos): F_Catipo, F_Cp7, F_Tipo y F_PesoGra en 0, prioridad 1, niveles de unidad
 * F_N1…F_N30 y las columnas de proyecto con {@link InsumoProyectos#insertar} (incluye solas las columnas nuevas).
 * No se duplican: la misma clave (también 104 = 0104) o una descripción igual o parecida, con los mismos números.
 */
public final class CargaProductos implements CargaArchivo {

    static final String[] COLUMNAS = {"Clave", "Descripción", "Tipo de insumo", "Origen", "Precio", "Proyectos", "Clave SS",
            "Presentación", "Origen NIM", "Grupo terapéutico", "Cantidad máxima a recibir", "Catálogo (año)",
            "Niveles de unidad", "Estatus"};
    /** Columnas F_N de nivel de unidad, en el orden de Alta de Claves. */
    static final List<String> NIVELES = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15",
            "16", "17", "217", "18", "19", "20", "30");
    private static final Set<String> ORIGEN_NIM = Set.of("LP", "CC", "CC/LP");

    record Producto(String clave, String descripcion, int tipo, int origen, String precio, ArrayList<String> proyectos,
                    String claveSS, String presentacion, String origenNim, String grupo, int cantidadMaxima, int catalogo,
                    ArrayList<String> niveles, String estatus) implements Serializable {
    }

    record Datos(ArrayList<Producto> productos, int omitidos) implements Serializable {
    }

    /** Renglones de ejemplo de la plantilla: no se cargan si el archivo los trae tal cual. */
    static String[][] ejemplos() {
        return new String[][]{{"CLAVE-EJEMPLO", "PARACETAMOL TABLETA 500 MG CAJA CON 10 TABLETAS", "2504", "1", "12.50", "1",
                        "010.000.0104.00", "CAJA CON 10 TABLETAS", "LP", "", "100000", String.valueOf(Year.now().getValue()), "", "A"}};
    }

    @Override
    public String nombre() {
        return "productos";
    }

    @Override
    public String pagina() {
        return "CargaArchivoProductos.jsp";
    }

    @Override
    public Set<Acceso> accesos() {
        return Set.of(Acceso.ALMACEN);
    }

    @Override
    public void plantilla(Connection c, OutputStream salida) throws IOException, SQLException {
        Excel.plantilla(salida, "Productos", COLUMNAS, 6,
                ejemplos(),
                List.of(new Excel.Bloque("Tipo de insumo (columna C)", Consultas.lista(c, "SELECT F_TipMed, F_DesMed FROM tb_tipmed ORDER BY F_TipMed")),
                        new Excel.Bloque("Origen (columna D)", Consultas.lista(c, "SELECT F_ClaOri, F_DesOri FROM tb_origen ORDER BY F_ClaOri")),
                        new Excel.Bloque("Proyectos (columna F, uno o varios separados por coma)",
                                Consultas.lista(c, "SELECT F_Id, F_DesProy FROM tb_proyectos ORDER BY F_Id")),
                        new Excel.Bloque("Grupo terapéutico (columna J, el nombre)", Consultas.lista(c, "SELECT F_idGT, F_Grupo FROM tb_gruptera ORDER BY F_Grupo"))),
                List.of("Carga del catálogo de productos (insumos) SIALSS. Un renglón por clave.",
                        "Obligatorias: Clave (hasta 20 caracteres), Descripción, Tipo de insumo, Origen, Precio y Proyectos.",
                        "Tipo, Origen y Proyectos: números de la hoja Catálogos. Proyectos: uno o varios separados por coma (1,2).",
                        "Opcionales: Clave SS (vacía = la clave), Presentación, Origen NIM (LP, CC o CC/LP), Grupo terapéutico (vacío = POR DEFINIR), "
                                + "Cantidad máxima (vacía = 100,000), Catálogo (vacío = año actual), Estatus (A o S; vacío = A).",
                        "Niveles de unidad: vacío = todos; o los números separados por coma (" + String.join(", ", NIVELES) + ").",
                        "No se duplican: una clave ya registrada o una descripción igual o parecida a la de otro producto (con los mismos "
                                + "números: 500 MG y 750 MG son distintos) se omite, igual que las repetidas en el archivo.",
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
        Map<String, String> insumos = new HashMap<>();
        Parecidos descripciones = new Parecidos(false);
        Map<String, String> clavePorDescripcion = new HashMap<>();
        for (Fila f : Consultas.lista(c, "SELECT F_ClaPro, F_DesPro FROM tb_medica")) {
            insumos.put(f.get(1), f.get(2));
            descripciones.agregar(f.get(2));
            clavePorDescripcion.putIfAbsent(f.get(2), f.get(1));
        }
        Map<String, String> tipos = mapa(c, "SELECT F_TipMed, F_DesMed FROM tb_tipmed");
        Map<String, String> origenes = mapa(c, "SELECT F_ClaOri, F_DesOri FROM tb_origen");
        Map<String, String> proyectoColumna = new HashMap<>();
        Map<String, String> proyectoNombre = new HashMap<>();
        for (CamposProyecto.Campo cp : CamposProyecto.listar(c)) {
            proyectoColumna.put(String.valueOf(cp.idProyecto()), cp.columna());
            proyectoNombre.put(String.valueOf(cp.idProyecto()), cp.proyecto());
        }
        Map<String, String> grupos = new HashMap<>();
        for (Fila f : Consultas.lista(c, "SELECT F_Grupo FROM tb_gruptera")) {
            grupos.putIfAbsent(f.get(1).trim().toUpperCase(Locale.ROOT), f.get(1));
        }
        String grupoDefecto = grupos.getOrDefault("POR DEFINIR", "POR DEFINIR");

        Parecidos enArchivo = new Parecidos(false);
        Set<String> clavesArchivo = new HashSet<>();
        List<String> errores = new ArrayList<>();
        ArrayList<Producto> nuevos = new ArrayList<>();
        List<List<String>> muestra = new ArrayList<>();
        Omitidos omitidos = new Omitidos();
        int anio = Year.now().getValue();
        for (Excel.Renglon cr : crudos) {
            String[] v = cr.v();
            List<String> mal = new ArrayList<>();
            String clave = Campos.obligatorio(Excel.sinDecimalCero(v[0]), 20, "la clave", mal);
            String descripcion = Campos.obligatorio(v[1], 2000, "la descripción", mal);
            String tipo = Excel.sinDecimalCero(v[2]);
            if (tipo.isEmpty()) {
                mal.add("falta el tipo de insumo");
            } else if (!tipos.containsKey(tipo)) {
                mal.add("el tipo de insumo " + tipo + " no existe (hoja Catálogos)");
            }
            String origen = Excel.sinDecimalCero(v[3]);
            if (origen.isEmpty()) {
                mal.add("falta el origen");
            } else if (!origenes.containsKey(origen)) {
                mal.add("el origen " + origen + " no existe (hoja Catálogos)");
            }
            String precio = null;
            if (v[4].isEmpty()) {
                mal.add("falta el precio");
            } else {
                try {
                    BigDecimal p = new BigDecimal(v[4].replace(",", "").replace("$", "").trim());
                    precio = p.stripTrailingZeros().toPlainString();
                    if (p.signum() < 0) {
                        mal.add("el precio no puede ser negativo");
                    } else if (precio.length() > 11) {
                        mal.add("el precio pasa de 11 caracteres");
                    }
                } catch (NumberFormatException ex) {
                    mal.add("el precio (" + v[4] + ") no es un número");
                }
            }
            ArrayList<String> proyectos = new ArrayList<>();
            List<String> nombresProyecto = new ArrayList<>();
            if (v[5].isEmpty()) {
                mal.add("faltan los proyectos");
            } else {
                for (String p : lista(v[5])) {
                    if (!proyectoColumna.containsKey(p)) {
                        mal.add("el proyecto " + p + " no existe o no tiene columna en tb_medica");
                    } else if (!proyectos.contains(proyectoColumna.get(p))) {
                        proyectos.add(proyectoColumna.get(p));
                        nombresProyecto.add(proyectoNombre.get(p));
                    }
                }
            }
            String claveSS = Campos.texto(v[6], 50, "la clave SS", mal);
            String presentacion = Campos.texto(v[7], 1000, "la presentación", mal);
            String origenNim = v[8].trim().toUpperCase(Locale.ROOT);
            if (!origenNim.isEmpty() && !ORIGEN_NIM.contains(origenNim)) {
                mal.add("el origen NIM " + v[8] + " no es LP, CC ni CC/LP");
            }
            String grupo = grupoDefecto;
            if (!v[9].isEmpty()) {
                grupo = grupos.get(v[9].trim().toUpperCase(Locale.ROOT));
                if (grupo == null) {
                    mal.add("el grupo terapéutico " + v[9] + " no existe (hoja Catálogos)");
                }
            }
            int cantidadMaxima = v[10].isEmpty() ? 100_000 : Excel.cantidad(v[10], "la cantidad máxima", mal);
            int catalogo = anio;
            if (!v[11].isEmpty()) {
                try {
                    catalogo = Integer.parseInt(Excel.sinDecimalCero(v[11]));
                    if (catalogo < 2000 || catalogo > anio + 1) {
                        mal.add("el catálogo (año) " + catalogo + " no es válido");
                    }
                } catch (NumberFormatException ex) {
                    mal.add("el catálogo (año) " + v[11] + " no es un año");
                }
            }
            ArrayList<String> niveles = new ArrayList<>(NIVELES);
            if (!v[12].isEmpty()) {
                niveles.clear();
                for (String n : lista(v[12])) {
                    if (!NIVELES.contains(n)) {
                        mal.add("el nivel de unidad " + n + " no existe");
                    } else if (!niveles.contains(n)) {
                        niveles.add(n);
                    }
                }
            }
            String estatus = v[13].trim().toUpperCase(Locale.ROOT);
            if (estatus.isEmpty()) {
                estatus = "A";
            } else if (!estatus.equals("A") && !estatus.equals("S")) {
                mal.add("el estatus debe ser A (activo) o S (suspendido)");
            }
            if (!mal.isEmpty()) {
                errores.add("Renglón " + cr.numero() + ": " + String.join("; ", mal) + ".");
                continue;
            }
            String existente = Claves.resolver(clave, insumos);
            Parecidos.Coincidencia ya = existente == null ? descripciones.buscar(descripcion) : null;
            String motivo = existente != null ? "la clave ya existe (" + existente + ")"
                    : ya != null ? (ya.exacta() ? "la descripción ya existe en la clave " : "la descripción se parece a la de la clave ")
                    + clavePorDescripcion.get(ya.nombre()) + " \"" + ya.nombre() + "\"" : null;
            if (motivo == null) {
                Parecidos.Coincidencia rep = enArchivo.buscar(descripcion);
                motivo = clavesArchivo.contains(clave.toUpperCase(Locale.ROOT)) ? "la clave se repite en el archivo"
                        : rep != null ? "la descripción se repite en el archivo con \"" + rep.nombre() + "\"" : null;
            }
            if (motivo != null) {
                omitidos.agregar(cr.numero(), clave + ": " + motivo);
                continue;
            }
            enArchivo.agregar(descripcion);
            clavesArchivo.add(clave.toUpperCase(Locale.ROOT));
            nuevos.add(new Producto(clave, descripcion, Integer.parseInt(tipo), Integer.parseInt(origen), precio, proyectos,
                    claveSS == null ? clave : claveSS, presentacion == null ? "" : presentacion, origenNim.isEmpty() ? "-" : origenNim,
                    grupo, cantidadMaxima, catalogo, niveles, estatus));
            if (muestra.size() < MAX_MUESTRA) {
                muestra.add(List.of(String.valueOf(cr.numero()), clave, descripcion, tipos.get(tipo), origenes.get(origen),
                        "$" + precio, String.join(", ", nombresProyecto), niveles.size() == NIVELES.size() ? "Todos" : String.join(", ", niveles)));
            }
        }
        if (!errores.isEmpty()) {
            return new Revision(errores, muestra, "", null);
        }
        if (nuevos.isEmpty()) {
            return omitidos.sinNuevos(crudos.size(), "productos nuevos");
        }
        return omitidos.valida(muestra, Texto.cuantos(nuevos.size(), "producto nuevo", "productos nuevos") + ".",
                new Datos(nuevos, omitidos.cuantos()));
    }

    @Override
    public String aplicar(Connection c, Revision r, Usuario u) throws SQLException {
        Datos d = (Datos) r.datos();
        boolean auto = c.getAutoCommit();
        c.setAutoCommit(false);
        try {
            for (Producto p : d.productos()) {
                Map<String, Object> base = new LinkedHashMap<>();
                base.put("F_ClaPro", p.clave());
                base.put("F_ClaProSS", p.claveSS());
                base.put("F_DesPro", p.descripcion());
                base.put("F_StsPro", p.estatus());
                base.put("F_TipMed", p.tipo());
                base.put("F_Costo", p.precio());
                base.put("F_PrePro", p.presentacion());
                base.put("F_Origen", p.origen());
                base.put("F_Catipo", "0");
                base.put("F_Cp7", "0");
                base.put("F_Tipo", "0");
                base.put("F_PesoGra", "0");
                base.put("F_IncMen", 0);
                Set<String> marcados = new TreeSet<>(p.niveles());
                for (String n : NIVELES) {
                    base.put("F_N" + n, marcados.contains(n) ? 1 : 0);
                }
                base.put("F_OriNim", p.origenNim());
                base.put("F_CostoNim", p.precio());
                base.put("F_Grupo", p.grupo());
                base.put("F_Comentario", "");
                base.put("F_Catalogo", String.valueOf(p.catalogo()));
                base.put("F_CantMax", p.cantidadMaxima());
                base.put("F_Prioridad", "1");
                base.put("F_NomGen", "");
                base.put("F_FormaFarm", "");
                base.put("F_Concentracion", "");
                base.put("F_DesProEsp", "");
                InsumoProyectos.insertar(c, base, p.proyectos());
            }
            c.commit();
        } catch (SQLException | RuntimeException e) {
            c.rollback();
            throw e;
        } finally {
            c.setAutoCommit(auto);
        }
        return "Se cargaron " + Texto.cuantos(d.productos().size(), "producto", "productos") + "." + Omitidos.mensajeFinal(d.omitidos());
    }

    private static List<String> lista(String v) {
        List<String> r = new ArrayList<>();
        for (String p : v.split("[,;/ ]+")) {
            String t = Excel.sinDecimalCero(p.trim());
            if (!t.isEmpty()) {
                r.add(t);
            }
        }
        return r;
    }

    private static Map<String, String> mapa(Connection c, String sql) throws SQLException {
        Map<String, String> m = new HashMap<>();
        for (Fila f : Consultas.lista(c, sql)) {
            m.put(f.get(1), f.get(2));
        }
        return m;
    }
}
