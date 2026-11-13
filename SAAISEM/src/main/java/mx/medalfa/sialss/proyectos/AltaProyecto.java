package mx.medalfa.sialss.proyectos;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import mx.medalfa.sialss.config.BaseDatos;
import mx.medalfa.sialss.dao.Consultas;
import mx.medalfa.sialss.dao.Fila;

/**
 * Alta de un proyecto nuevo con todo lo que el sistema necesita para operarlo:
 * <ol>
 *   <li>{@code tb_proyectos}: nombre, dirección, RFC, encabezado, responsables (F_Qfb), nomenclatura {@code ABR-AA-}
 *       (abreviatura, año en 2 dígitos) también en F_NomOC, y F_Campo.</li>
 *   <li>{@code tb_medica}: F_Campo apunta a una columna de proyecto libre (que ningún proyecto use); si no hay, se crea
 *       {@code F_Project<id>} (INT, 0 por omisión).</li>
 *   <li>{@code tb_indice}: columna {@code F_IndFactP<id>}; si no existe se crea (inicia en 1).</li>
 *   <li>{@code tb_ubica}: 3 ubicaciones de ingreso ({@code nueva_ABR}, {@code nueva_ABR-APE}, {@code nueva_ABR-RF}) como las
 *       "Stgrecibo" y 3 de salida ({@code ABR}, {@code ABR-APE}, {@code ABR-RF}) como las "Stgemb".</li>
 *   <li>{@code tb_ubicanueva}: las 3 de ingreso (F_ClaCli = id del proyecto).</li>
 *   <li>{@code tb_ubicafact} y {@code tb_tipocause}: con el MISMO id del proyecto; nombre = abreviatura.</li>
 * </ol>
 * Las columnas nuevas (ALTER) se crean antes de la transacción porque MariaDB confirma cada ALTER; si después falla un
 * INSERT, la transacción se deshace y la columna queda libre para el siguiente intento.
 */
public final class AltaProyecto {

    public static final String[] SUFIJOS = {"", "-APE", "-RF"};
    private static final Pattern ABREVIATURA = Pattern.compile("[A-Z0-9]{2,6}");
    private static final Pattern RFC = Pattern.compile("[A-ZÑ&]{3,4}\\d{6}[A-Z0-9]{3}");
    private static final Pattern COLUMNA_PROYECTO = Pattern.compile("(?i)F_Project\\d+|F_ProIsem");
    private static final Gson GSON = new Gson();

    /** Lo que captura la pantalla. */
    public record Datos(String nombre, String abreviatura, String direccion, String rfc, String encabezado, String contrato,
                        String qfbMedicamento, String qfbDispositivos) {
    }

    /** Lo que se va a hacer (la pantalla lo muestra antes de guardar). */
    public record Plan(int id, String nomenclatura, String abreviatura, String campoMedica, boolean crearCampoMedica,
                       int insumosYaMarcados, String columnaIndice, boolean crearColumnaIndice, List<String> ingreso,
                       List<String> salida, String ubicaSql, List<String> errores) {
        public boolean valido() {
            return errores.isEmpty();
        }
    }

    private AltaProyecto() {
    }

    public static String limpiar(String s) {
        return s == null ? "" : s.trim().replaceAll("\\s+", " ");
    }

    /** Abreviatura sugerida: iniciales de las palabras del nombre (o las primeras letras si es una sola palabra). */
    public static String sugerirAbreviatura(String nombre) {
        String n = java.text.Normalizer.normalize(limpiar(nombre), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9 ]", " ").trim();
        if (n.isEmpty()) {
            return "";
        }
        String[] p = n.split("\\s+");
        StringBuilder sb = new StringBuilder();
        if (p.length == 1) {
            sb.append(p[0], 0, Math.min(4, p[0].length()));
        } else {
            for (String x : p) {
                if (sb.length() < 6 && !x.isEmpty()) {
                    sb.append(x.charAt(0));
                }
            }
        }
        return sb.toString();
    }

    public static String nomenclatura(String abreviatura, LocalDate fecha) {
        return abreviatura + "-" + String.format("%02d", fecha.getYear() % 100) + "-";
    }

    /** Responsables del proyecto 1 (para precargar la pantalla): {2504 → texto, 2505 → texto}. */
    public static Map<String, String> responsablesBase() throws SQLException {
        Map<String, String> r = new LinkedHashMap<>();
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            List<Fila> f = Consultas.lista(c, "SELECT F_Qfb FROM tb_proyectos WHERE F_Id = 1");
            if (!f.isEmpty() && f.get(0).get(1) != null) {
                List<Map<String, String>> lista = GSON.fromJson(f.get(0).get(1), new TypeToken<List<Map<String, String>>>() { }.getType());
                for (Map<String, String> m : lista) {
                    r.put(m.get("tipo"), m.get("quimico"));
                }
            }
        } catch (RuntimeException e) {
            // F_Qfb con formato inesperado: se capturan a mano.
        }
        return r;
    }

    public static Plan planear(Datos d) throws SQLException {
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            return planear(c, d);
        }
    }

    static Plan planear(Connection c, Datos d) throws SQLException {
        List<String> errores = new ArrayList<>();
        String nombre = limpiar(d.nombre());
        String abr = limpiar(d.abreviatura()).toUpperCase(Locale.ROOT);
        if (nombre.isEmpty() || nombre.length() > 30) {
            errores.add("El nombre es obligatorio y admite hasta 30 caracteres.");
        }
        if (!ABREVIATURA.matcher(abr).matches()) {
            errores.add("La abreviatura lleva de 2 a 6 letras o números, sin espacios ni guiones.");
        }
        if (limpiar(d.direccion()).isEmpty() || limpiar(d.direccion()).length() > 255) {
            errores.add("La dirección es obligatoria (hasta 255 caracteres).");
        }
        if (!RFC.matcher(limpiar(d.rfc()).toUpperCase(Locale.ROOT)).matches()) {
            errores.add("El RFC no tiene formato válido (12 caracteres persona moral, 13 persona física).");
        }
        if (limpiar(d.encabezado()).isEmpty() || limpiar(d.encabezado()).length() > 150) {
            errores.add("El encabezado es obligatorio (hasta 150 caracteres).");
        }
        if (limpiar(d.qfbMedicamento()).isEmpty() || limpiar(d.qfbDispositivos()).isEmpty()) {
            errores.add("Escriba los dos responsables sanitarios.");
        }
        String nomen = nomenclatura(abr, LocalDate.now());

        // Duplicados con proyectos existentes.
        for (Fila f : Consultas.lista(c, "SELECT F_Id, F_DesProy, F_Nomenclatura FROM tb_proyectos")) {
            if (f.get(2) != null && f.get(2).trim().equalsIgnoreCase(nombre)) {
                errores.add("Ya existe el proyecto \"" + f.get(2) + "\" (id " + f.get(1) + ").");
            }
            if (f.get(3) != null && f.get(3).toUpperCase(Locale.ROOT).startsWith(abr + "-")) {
                errores.add("La abreviatura " + abr + " ya la usa el proyecto " + f.get(2) + " (" + f.get(3) + ").");
            }
        }

        // Id: el siguiente después del mayor menor a 100 que esté libre también en tb_ubicafact y tb_tipocause.
        int id = Consultas.lista(c, "SELECT COALESCE(MAX(F_Id), 0) + 1 FROM tb_proyectos WHERE F_Id < 100").get(0).entero(1);
        while (id < 100 && ocupado(c, id)) {
            id++;
        }
        if (id >= 100) {
            errores.add("No hay un id libre menor a 100 en tb_proyectos, tb_ubicafact y tb_tipocause.");
        }

        // Columna de proyecto en tb_medica.
        Set<String> colsMedica = CamposProyecto.columnas(c, "tb_medica");
        Set<String> usadas = new java.util.HashSet<>();
        for (Fila f : Consultas.lista(c, "SELECT F_Campo FROM tb_proyectos")) {
            if (f.get(1) != null) {
                usadas.add(f.get(1).trim().toLowerCase(Locale.ROOT));
            }
        }
        String propia = "F_Project" + id;
        String campo = null;
        if (colsMedica.contains(propia.toLowerCase(Locale.ROOT)) && !usadas.contains(propia.toLowerCase(Locale.ROOT))) {
            campo = propia;
        } else {
            for (Fila f : Consultas.lista(c, "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() "
                    + "AND TABLE_NAME = 'tb_medica' ORDER BY ORDINAL_POSITION")) {
                String col = f.get(1);
                if (COLUMNA_PROYECTO.matcher(col).matches() && !usadas.contains(col.toLowerCase(Locale.ROOT))) {
                    campo = col;
                    break;
                }
            }
        }
        boolean crearCampo = campo == null;
        if (crearCampo) {
            campo = propia;
            if (colsMedica.contains(propia.toLowerCase(Locale.ROOT))) {
                errores.add("La columna " + propia + " existe en tb_medica pero ya la usa otro proyecto.");
            }
        }
        int marcados = 0;
        if (!crearCampo && CamposProyecto.COLUMNA.matcher(campo).matches()) {
            marcados = Consultas.lista(c, "SELECT COUNT(*) FROM tb_medica WHERE " + campo + " <> 0").get(0).entero(1);
        }

        String colIndice = "F_IndFactP" + id;
        boolean crearIndice = !CamposProyecto.columnas(c, "tb_indice").contains(colIndice.toLowerCase(Locale.ROOT));

        List<String> ingreso = new ArrayList<>();
        List<String> salida = new ArrayList<>();
        for (String s : SUFIJOS) {
            ingreso.add("nueva_" + abr + s);
            salida.add(abr + s);
        }
        if (ABREVIATURA.matcher(abr).matches()) {
            List<String> todas = new ArrayList<>(ingreso);
            todas.addAll(salida);
            for (Fila f : Consultas.lista(c, "SELECT F_ClaUbi FROM tb_ubica WHERE F_ClaUbi IN (" + Consultas.marcas(todas.size()) + ")", todas)) {
                errores.add("La ubicación " + f.get(1) + " ya existe en tb_ubica.");
            }
            for (Fila f : Consultas.lista(c, "SELECT DescUbi FROM tb_ubicanueva WHERE DescUbi IN (" + Consultas.marcas(3) + ")", ingreso)) {
                errores.add("La ubicación " + f.get(1) + " ya existe en tb_ubicanueva.");
            }
            for (Fila f : Consultas.lista(c, "SELECT F_Cause FROM tb_tipocause WHERE F_Cause = ?", abr)) {
                errores.add("Ya existe el tipo de cause " + f.get(1) + ".");
            }
        }
        String ubicaSql = "WHERE F_Ubica IN (" + String.join(",", salida.stream().map(x -> "'" + x + "'").toList()) + ")";
        return new Plan(id, nomen, abr, campo, crearCampo, marcados, colIndice, crearIndice, ingreso, salida, ubicaSql, errores);
    }

    private static boolean ocupado(Connection c, int id) throws SQLException {
        return !Consultas.lista(c, "SELECT 1 FROM tb_proyectos WHERE F_Id = ? UNION ALL SELECT 1 FROM tb_ubicafact WHERE F_idUbicaFac = ? "
                + "UNION ALL SELECT 1 FROM tb_tipocause WHERE F_Id = ?", id, id, id).isEmpty();
    }

    /** Da de alta el proyecto. Vuelve a planear dentro (por si algo cambió desde la vista previa). */
    public static Plan ejecutar(Datos d) throws SQLException {
        try (Connection c = BaseDatos.GENERAL.conexion()) {
            asegurarColumnasProyectos(c);
            Plan p = planear(c, d);
            if (!p.valido()) {
                return p;
            }
            // 1. Estructura (cada ALTER se confirma solo).
            try (Statement st = c.createStatement()) {
                if (p.crearCampoMedica()) {
                    st.execute("ALTER TABLE tb_medica ADD COLUMN IF NOT EXISTS " + p.campoMedica()
                            + " INT(11) NOT NULL DEFAULT 0 COMMENT 'Insumo del proyecto " + p.id() + " (1 = sí)'");
                }
                if (p.crearColumnaIndice()) {
                    st.execute("ALTER TABLE tb_indice ADD COLUMN IF NOT EXISTS " + p.columnaIndice()
                            + " INT(11) NOT NULL DEFAULT 1 COMMENT 'Consecutivo de facturación del proyecto " + p.id() + "'");
                }
            }
            // 2. Datos, todo o nada.
            boolean auto = c.getAutoCommit();
            c.setAutoCommit(false);
            try {
                String qfb = GSON.toJson(List.of(responsable("2504", d.qfbMedicamento()), responsable("2505", d.qfbDispositivos())));
                String contrato = limpiar(d.contrato()).isEmpty() ? "N/A" : limpiar(d.contrato());
                actualizar(c, "INSERT INTO tb_proyectos (F_Id, F_DesProy, F_Campo, F_Contrato, F_Nomenclatura, F_Encabezado, F_Qfb, "
                        + "F_NomOC, F_IdOC, F_Direccion, F_Rfc) VALUES (?, ?, ?, ?, ?, ?, ?, ?, 1, ?, ?)",
                        p.id(), limpiar(d.nombre()), p.campoMedica(), contrato, p.nomenclatura(), limpiar(d.encabezado()), qfb,
                        p.nomenclatura(), limpiar(d.direccion()), limpiar(d.rfc()).toUpperCase(Locale.ROOT));
                // Ingreso: como Stgrecibo (sección recibo, tipo 6); salida: como Stgemb (sección Remision, tipo 7).
                for (String u : p.ingreso()) {
                    actualizar(c, "INSERT INTO tb_ubica (F_ClaUbi, F_DesUbi, seccion, ubicacion_mapa, nivel, letra, es_temporal, F_Tipo, "
                            + "Id_Bodega, Estatus, F_Clasificacion) VALUES (?, ?, 'recibo', 1, 1, 'reci', 1, 6, 1, 1, 1)", u, u);
                    actualizar(c, "INSERT INTO tb_ubicanueva (DescUbi, F_ClaCli, Status) VALUES (?, ?, 'A')", u, String.valueOf(p.id()));
                }
                for (String u : p.salida()) {
                    actualizar(c, "INSERT INTO tb_ubica (F_ClaUbi, F_DesUbi, seccion, ubicacion_mapa, nivel, letra, es_temporal, F_Tipo, "
                            + "Id_Bodega, Estatus, F_Clasificacion) VALUES (?, ?, 'Remision', 1, 1, 'remi', 1, 7, 1, 1, 1)", u, u);
                }
                actualizar(c, "INSERT INTO tb_ubicafact (F_idUbicaFac, F_UbicaFact, F_UbicaSQL, F_UbicaSQL2) VALUES (?, ?, ?, ?)",
                        p.id(), p.abreviatura(), p.ubicaSql(), p.ubicaSql().replace("WHERE F_Ubica", "WHERE l.F_Ubica"));
                actualizar(c, "INSERT INTO tb_tipocause (F_Id, F_Cause) VALUES (?, ?)", p.id(), p.abreviatura());
                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(auto);
            }
            return p;
        }
    }

    /** {"tipo": ..., "quimico": ...} en ese orden, como los F_Qfb existentes. */
    private static Map<String, String> responsable(String tipo, String texto) {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("tipo", tipo);
        m.put("quimico", limpiar(texto));
        return m;
    }

    /** F_Direccion y F_Rfc en tb_proyectos (también en despliegue/proyecto_nuevo.sql). */
    static void asegurarColumnasProyectos(Connection c) throws SQLException {
        try (Statement st = c.createStatement()) {
            st.execute("ALTER TABLE tb_proyectos ADD COLUMN IF NOT EXISTS F_Direccion VARCHAR(255) NULL COMMENT 'Dirección del proyecto', "
                    + "ADD COLUMN IF NOT EXISTS F_Rfc VARCHAR(13) NULL COMMENT 'RFC del proyecto'");
        }
    }

    private static void actualizar(Connection c, String sql, Object... p) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            Consultas.asignar(ps, p);
            ps.executeUpdate();
        }
    }
}
