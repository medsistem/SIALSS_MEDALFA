import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Diagnóstico de solo lectura de una base MariaDB.
 *
 * Qué lee: information_schema, SHOW VARIABLES y SHOW STATUS. Es decir,
 * metadatos y contadores del servidor.
 *
 * Qué NO lee: ni una sola fila de tus tablas. No hay un SELECT contra
 * tb_medica, tb_lote ni ninguna otra tabla de la aplicación. Ningún dato de
 * paciente, proveedor o inventario sale en el archivo de salida.
 *
 * No modifica nada. No crea, no altera, no borra.
 *
 * Uso:
 *   javac Diagnostico.java
 *   java -cp .;mariadb-java-client-2.5.4.jar Diagnostico <host> <puerto> <base> <usuario> <clave> [salida.txt]
 *
 * En Linux o Mac el separador del classpath son dos puntos en vez de punto y coma.
 *
 * Las consultas que el usuario no tenga permiso de ejecutar se marcan como
 * omitidas y el programa continúa. Es normal que varias fallen con un usuario
 * de aplicación; lo que salga igual sirve.
 */
public class Diagnostico {

    /** Tope de renglones por consulta, para que el archivo no se vuelva inmanejable. */
    private static final int MAX_FILAS = 300;

    private static Writer out;
    private static int ejecutadas = 0;
    private static int omitidas = 0;

    public static void main(String[] args) throws Exception {

        if (args.length < 5) {
            System.out.println("Uso: java Diagnostico <host> <puerto> <base> <usuario> <clave> [salida.txt]");
            System.out.println("Ej:  java -cp .;mariadb-java-client-2.5.4.jar Diagnostico "
                             + "192.168.0.184 3306 sialss_mdf sialss_mdf MICLAVE diagnostico.txt");
            return;
        }

        String host   = args[0];
        String puerto = args[1];
        String base   = args[2];
        String user   = args[3];
        String clave  = args[4];
        String salida = args.length > 5 ? args[5] : "diagnostico-" + base + ".txt";

        String url = "jdbc:mariadb://" + host + ":" + puerto + "/" + base
                   + "?useUnicode=true&characterEncoding=UTF-8&connectTimeout=10000&socketTimeout=120000";

        out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(salida), "UTF-8"));

        Connection cn = null;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            cn = DriverManager.getConnection(url, user, clave);
            // Por si acaso: la sesión queda marcada de solo lectura.
            try { cn.setReadOnly(true); } catch (SQLException ignorado) { }

            escribir("DIAGNOSTICO DE BASE DE DATOS");
            escribir("base: " + base + "   host: " + host + ":" + puerto + "   usuario: " + user);
            escribir("generado: " + new java.util.Date());
            escribir("solo lectura: no se consulta ninguna fila de las tablas de la aplicación");
            escribir("");

            for (Consulta c : catalogo()) {
                correr(cn, c);
            }

            escribir("");
            escribir("=== FIN ===");
            escribir("consultas ejecutadas: " + ejecutadas + "   omitidas por permisos u otro error: " + omitidas);

            System.out.println("Listo. " + ejecutadas + " consultas ejecutadas, " + omitidas + " omitidas.");
            System.out.println("Archivo: " + salida);

        } catch (Exception e) {
            System.out.println("No se pudo conectar o falló el diagnóstico: " + e.getMessage());
            escribir("ERROR GENERAL: " + e.getMessage());
        } finally {
            if (cn != null) { try { cn.close(); } catch (SQLException ignorado) { } }
            out.flush();
            out.close();
        }
    }

    // ------------------------------------------------------------------
    // Catálogo de consultas
    // ------------------------------------------------------------------

    private static List<Consulta> catalogo() {
        List<Consulta> c = new ArrayList<Consulta>();

        c.add(new Consulta("01. Identidad del servidor",
            "SELECT VERSION() AS version, @@version_comment AS distribucion, @@hostname AS servidor, "
          + "DATABASE() AS base, USER() AS usuario, NOW() AS hora_servidor, "
          + "@@time_zone AS zona, @@system_time_zone AS zona_sistema"));

        c.add(new Consulta("02. Juego de caracteres y sql_mode",
            "SELECT @@character_set_server AS charset_servidor, @@collation_server AS colacion_servidor, "
          + "@@character_set_database AS charset_base, @@collation_database AS colacion_base, "
          + "@@sql_mode AS sql_mode, @@lower_case_table_names AS mayusculas_tablas"));

        c.add(new Consulta("03. Resumen de la base",
            "SELECT COUNT(*) AS tablas, "
          + "ROUND(SUM(DATA_LENGTH)/1024/1024,1) AS datos_mb, "
          + "ROUND(SUM(INDEX_LENGTH)/1024/1024,1) AS indices_mb, "
          + "ROUND(SUM(DATA_LENGTH+INDEX_LENGTH)/1024/1024,1) AS total_mb, "
          + "ROUND(SUM(DATA_FREE)/1024/1024,1) AS espacio_libre_mb, "
          + "SUM(TABLE_ROWS) AS filas_aprox "
          + "FROM information_schema.TABLES "
          + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_TYPE = 'BASE TABLE'"));

        c.add(new Consulta("04. Motores de almacenamiento en uso",
            "SELECT ENGINE AS motor, COUNT(*) AS tablas, "
          + "ROUND(SUM(DATA_LENGTH+INDEX_LENGTH)/1024/1024,1) AS total_mb "
          + "FROM information_schema.TABLES "
          + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_TYPE = 'BASE TABLE' "
          + "GROUP BY ENGINE"));

        c.add(new Consulta("05. Tablas por tamaño (las 60 mayores)",
            "SELECT TABLE_NAME AS tabla, ENGINE AS motor, ROW_FORMAT AS formato, "
          + "TABLE_ROWS AS filas_aprox, "
          + "ROUND(DATA_LENGTH/1024/1024,1) AS datos_mb, "
          + "ROUND(INDEX_LENGTH/1024/1024,1) AS indices_mb, "
          + "ROUND(DATA_FREE/1024/1024,1) AS libre_mb, "
          + "TABLE_COLLATION AS colacion, AUTO_INCREMENT AS autoinc, "
          + "CREATE_TIME AS creada, UPDATE_TIME AS modificada "
          + "FROM information_schema.TABLES "
          + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_TYPE = 'BASE TABLE' "
          + "ORDER BY DATA_LENGTH + INDEX_LENGTH DESC LIMIT 60"));

        c.add(new Consulta("06. Tablas SIN llave primaria",
            "Una tabla InnoDB sin llave primaria se ordena por una llave interna oculta; "
          + "la replicación y los índices secundarios se degradan.",
            "SELECT t.TABLE_NAME AS tabla, t.ENGINE AS motor, t.TABLE_ROWS AS filas_aprox, "
          + "ROUND((t.DATA_LENGTH+t.INDEX_LENGTH)/1024/1024,1) AS total_mb "
          + "FROM information_schema.TABLES t "
          + "LEFT JOIN information_schema.TABLE_CONSTRAINTS k "
          + "  ON k.TABLE_SCHEMA = t.TABLE_SCHEMA AND k.TABLE_NAME = t.TABLE_NAME "
          + " AND k.CONSTRAINT_TYPE = 'PRIMARY KEY' "
          + "WHERE t.TABLE_SCHEMA = DATABASE() AND t.TABLE_TYPE = 'BASE TABLE' "
          + "  AND k.CONSTRAINT_NAME IS NULL "
          + "ORDER BY t.DATA_LENGTH + t.INDEX_LENGTH DESC"));

        c.add(new Consulta("07. Tablas con datos y SIN ningún índice secundario",
            "Candidatas a recorrido completo en cada consulta que no filtre por la llave primaria.",
            "SELECT t.TABLE_NAME AS tabla, t.TABLE_ROWS AS filas_aprox, "
          + "ROUND(t.DATA_LENGTH/1024/1024,1) AS datos_mb "
          + "FROM information_schema.TABLES t "
          + "WHERE t.TABLE_SCHEMA = DATABASE() AND t.TABLE_TYPE = 'BASE TABLE' "
          + "  AND t.TABLE_ROWS > 500 "
          + "  AND NOT EXISTS ( SELECT 1 FROM information_schema.STATISTICS s "
          + "                   WHERE s.TABLE_SCHEMA = t.TABLE_SCHEMA "
          + "                     AND s.TABLE_NAME = t.TABLE_NAME "
          + "                     AND s.INDEX_NAME <> 'PRIMARY' ) "
          + "ORDER BY t.TABLE_ROWS DESC"));

        c.add(new Consulta("08. Todos los índices, con cardinalidad",
            "Cardinalidad 1 o muy baja en una tabla grande = índice que el optimizador casi nunca usará.",
            "SELECT TABLE_NAME AS tabla, INDEX_NAME AS indice, "
          + "NON_UNIQUE AS no_unico, SEQ_IN_INDEX AS pos, COLUMN_NAME AS columna, "
          + "CARDINALITY AS cardinalidad, NULLABLE AS acepta_nulo, INDEX_TYPE AS tipo "
          + "FROM information_schema.STATISTICS "
          + "WHERE TABLE_SCHEMA = DATABASE() "
          + "ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX"));

        c.add(new Consulta("09. Índices redundantes (uno es prefijo de otro)",
            "El índice A sobra si sus columnas ya son el comienzo del índice B. Ocupa disco y "
          + "encarece cada INSERT sin aportar nada.",
            "SELECT a.TABLE_NAME AS tabla, a.INDEX_NAME AS indice_sobrante, a.cols AS sus_columnas, "
          + "b.INDEX_NAME AS cubierto_por, b.cols AS columnas_del_otro "
          + "FROM ( SELECT TABLE_SCHEMA, TABLE_NAME, INDEX_NAME, "
          + "              GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) AS cols "
          + "       FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() "
          + "       GROUP BY TABLE_SCHEMA, TABLE_NAME, INDEX_NAME ) a "
          + "JOIN ( SELECT TABLE_SCHEMA, TABLE_NAME, INDEX_NAME, "
          + "              GROUP_CONCAT(COLUMN_NAME ORDER BY SEQ_IN_INDEX) AS cols "
          + "       FROM information_schema.STATISTICS WHERE TABLE_SCHEMA = DATABASE() "
          + "       GROUP BY TABLE_SCHEMA, TABLE_NAME, INDEX_NAME ) b "
          + "  ON a.TABLE_NAME = b.TABLE_NAME AND a.INDEX_NAME <> b.INDEX_NAME "
          + " AND b.cols LIKE CONCAT(a.cols, ',%') "
          + "ORDER BY a.TABLE_NAME"));

        c.add(new Consulta("10. Llaves foráneas declaradas",
            "Si sale vacío, la integridad entre tablas depende solo del código de la aplicación.",
            "SELECT CONSTRAINT_NAME AS restriccion, TABLE_NAME AS tabla, "
          + "COLUMN_NAME AS columna, REFERENCED_TABLE_NAME AS tabla_referida, "
          + "REFERENCED_COLUMN_NAME AS columna_referida "
          + "FROM information_schema.KEY_COLUMN_USAGE "
          + "WHERE TABLE_SCHEMA = DATABASE() AND REFERENCED_TABLE_NAME IS NOT NULL "
          + "ORDER BY TABLE_NAME"));

        c.add(new Consulta("11. Tipos de dato en uso",
            "SELECT DATA_TYPE AS tipo, COUNT(*) AS columnas "
          + "FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() "
          + "GROUP BY DATA_TYPE ORDER BY columnas DESC"));

        c.add(new Consulta("12. Fechas guardadas como texto",
            "Columnas que por el nombre parecen fecha pero están declaradas como texto o número. "
          + "Impiden comparar rangos con índice y dejan pasar valores inválidos.",
            "SELECT TABLE_NAME AS tabla, COLUMN_NAME AS columna, COLUMN_TYPE AS tipo "
          + "FROM information_schema.COLUMNS "
          + "WHERE TABLE_SCHEMA = DATABASE() "
          + "  AND ( COLUMN_NAME LIKE '%Fec%' OR COLUMN_NAME LIKE '%fecha%' "
          + "     OR COLUMN_NAME LIKE '%Cad%' OR COLUMN_NAME LIKE '%Fab%' ) "
          + "  AND DATA_TYPE NOT IN ('date','datetime','timestamp','time','year') "
          + "ORDER BY TABLE_NAME, COLUMN_NAME"));

        c.add(new Consulta("13. Columnas de texto muy anchas",
            "VARCHAR holgados inflan las tablas temporales y los ordenamientos.",
            "SELECT TABLE_NAME AS tabla, COLUMN_NAME AS columna, COLUMN_TYPE AS tipo, "
          + "CHARACTER_MAXIMUM_LENGTH AS largo "
          + "FROM information_schema.COLUMNS "
          + "WHERE TABLE_SCHEMA = DATABASE() "
          + "  AND DATA_TYPE IN ('varchar','char') AND CHARACTER_MAXIMUM_LENGTH >= 500 "
          + "ORDER BY CHARACTER_MAXIMUM_LENGTH DESC LIMIT 80"));

        c.add(new Consulta("14. Colaciones mezcladas",
            "Unir dos columnas con colaciones distintas obliga a convertir y tira el índice.",
            "SELECT COLLATION_NAME AS colacion, COUNT(*) AS columnas, "
          + "COUNT(DISTINCT TABLE_NAME) AS tablas "
          + "FROM information_schema.COLUMNS "
          + "WHERE TABLE_SCHEMA = DATABASE() AND COLLATION_NAME IS NOT NULL "
          + "GROUP BY COLLATION_NAME ORDER BY columnas DESC"));

        c.add(new Consulta("15. Tablas con más de 40 columnas",
            "Suelen esconder grupos de banderas o campos repetidos que piden una tabla aparte.",
            "SELECT TABLE_NAME AS tabla, COUNT(*) AS columnas "
          + "FROM information_schema.COLUMNS WHERE TABLE_SCHEMA = DATABASE() "
          + "GROUP BY TABLE_NAME HAVING COUNT(*) > 40 ORDER BY columnas DESC"));

        c.add(new Consulta("16. AUTO_INCREMENT cerca del tope del tipo",
            "SELECT t.TABLE_NAME AS tabla, c.COLUMN_NAME AS columna, c.COLUMN_TYPE AS tipo, "
          + "t.AUTO_INCREMENT AS valor_actual, "
          + "CASE WHEN c.COLUMN_TYPE LIKE 'tinyint%unsigned%' THEN 255 "
          + "     WHEN c.COLUMN_TYPE LIKE 'tinyint%' THEN 127 "
          + "     WHEN c.COLUMN_TYPE LIKE 'smallint%unsigned%' THEN 65535 "
          + "     WHEN c.COLUMN_TYPE LIKE 'smallint%' THEN 32767 "
          + "     WHEN c.COLUMN_TYPE LIKE 'mediumint%unsigned%' THEN 16777215 "
          + "     WHEN c.COLUMN_TYPE LIKE 'mediumint%' THEN 8388607 "
          + "     WHEN c.COLUMN_TYPE LIKE 'int%unsigned%' THEN 4294967295 "
          + "     WHEN c.COLUMN_TYPE LIKE 'int%' THEN 2147483647 "
          + "     ELSE NULL END AS tope_del_tipo "
          + "FROM information_schema.TABLES t "
          + "JOIN information_schema.COLUMNS c "
          + "  ON c.TABLE_SCHEMA = t.TABLE_SCHEMA AND c.TABLE_NAME = t.TABLE_NAME "
          + " AND c.EXTRA LIKE '%auto_increment%' "
          + "WHERE t.TABLE_SCHEMA = DATABASE() AND t.AUTO_INCREMENT IS NOT NULL "
          + "ORDER BY t.AUTO_INCREMENT DESC LIMIT 40"));

        c.add(new Consulta("17. Fragmentación (espacio libre dentro de la tabla)",
            "SELECT TABLE_NAME AS tabla, "
          + "ROUND(DATA_LENGTH/1024/1024,1) AS datos_mb, "
          + "ROUND(DATA_FREE/1024/1024,1) AS libre_mb, "
          + "ROUND(100*DATA_FREE/NULLIF(DATA_LENGTH+DATA_FREE,0),1) AS pct_libre "
          + "FROM information_schema.TABLES "
          + "WHERE TABLE_SCHEMA = DATABASE() AND TABLE_TYPE = 'BASE TABLE' "
          + "  AND DATA_FREE > 50*1024*1024 "
          + "ORDER BY DATA_FREE DESC LIMIT 30"));

        c.add(new Consulta("18. Procedimientos, funciones y disparadores",
            "SELECT ROUTINE_TYPE AS tipo, ROUTINE_NAME AS nombre, "
          + "CREATED AS creado, LAST_ALTERED AS modificado, "
          + "CHAR_LENGTH(ROUTINE_DEFINITION) AS largo_del_cuerpo "
          + "FROM information_schema.ROUTINES WHERE ROUTINE_SCHEMA = DATABASE() "
          + "ORDER BY ROUTINE_TYPE, ROUTINE_NAME"));

        c.add(new Consulta("19. Vistas",
            "SELECT TABLE_NAME AS vista, IS_UPDATABLE AS actualizable, "
          + "CHAR_LENGTH(VIEW_DEFINITION) AS largo_definicion "
          + "FROM information_schema.VIEWS WHERE TABLE_SCHEMA = DATABASE()"));

        c.add(new Consulta("20. Disparadores",
            "SELECT TRIGGER_NAME AS disparador, EVENT_OBJECT_TABLE AS tabla, "
          + "ACTION_TIMING AS momento, EVENT_MANIPULATION AS evento "
          + "FROM information_schema.TRIGGERS WHERE TRIGGER_SCHEMA = DATABASE()"));

        c.add(new Consulta("21. Configuración: memoria e InnoDB",
            "SHOW VARIABLES WHERE Variable_name IN ("
          + "'innodb_buffer_pool_size','innodb_buffer_pool_instances','innodb_log_file_size',"
          + "'innodb_log_buffer_size','innodb_flush_log_at_trx_commit','innodb_flush_method',"
          + "'innodb_file_per_table','innodb_io_capacity','innodb_read_io_threads',"
          + "'innodb_write_io_threads','innodb_lock_wait_timeout','innodb_stats_persistent',"
          + "'key_buffer_size','tmp_table_size','max_heap_table_size','sort_buffer_size',"
          + "'join_buffer_size','read_buffer_size','max_allowed_packet')"));

        c.add(new Consulta("22. Configuración: conexiones y tiempos de espera",
            "SHOW VARIABLES WHERE Variable_name IN ("
          + "'max_connections','max_user_connections','thread_cache_size','thread_handling',"
          + "'wait_timeout','interactive_timeout','connect_timeout','net_read_timeout',"
          + "'net_write_timeout','table_open_cache','table_definition_cache','open_files_limit')"));

        c.add(new Consulta("23. Configuración: bitácora de consultas lentas",
            "Si slow_query_log está en OFF, no hay forma de saber qué consulta duele.",
            "SHOW VARIABLES WHERE Variable_name IN ("
          + "'slow_query_log','slow_query_log_file','long_query_time','log_output',"
          + "'log_queries_not_using_indexes','log_slow_admin_statements','min_examined_row_limit',"
          + "'general_log','query_cache_type','query_cache_size','query_cache_limit')"));

        c.add(new Consulta("24. Estado: tiempo encendido y conexiones",
            "Threads_created muy alto frente a Connections = no hay caché de hilos. "
          + "Aborted_connects alto = conexiones que se cierran mal.",
            "SHOW GLOBAL STATUS WHERE Variable_name IN ("
          + "'Uptime','Connections','Threads_connected','Threads_running','Threads_created',"
          + "'Threads_cached','Max_used_connections','Aborted_connects','Aborted_clients',"
          + "'Connection_errors_max_connections')"));

        c.add(new Consulta("25. Estado: perfil de carga",
            "SHOW GLOBAL STATUS WHERE Variable_name IN ("
          + "'Questions','Queries','Com_select','Com_insert','Com_update','Com_delete',"
          + "'Com_commit','Com_rollback','Com_call_procedure','Slow_queries')"));

        c.add(new Consulta("26. Estado: recorridos completos y temporales en disco",
            "Handler_read_rnd_next alto frente a Handler_read_key = se están recorriendo tablas "
          + "enteras. Created_tmp_disk_tables alto = temporales que no cupieron en memoria.",
            "SHOW GLOBAL STATUS WHERE Variable_name IN ("
          + "'Handler_read_first','Handler_read_key','Handler_read_next','Handler_read_rnd',"
          + "'Handler_read_rnd_next','Select_full_join','Select_scan','Select_range',"
          + "'Created_tmp_tables','Created_tmp_disk_tables','Created_tmp_files',"
          + "'Sort_merge_passes','Sort_scan')"));

        c.add(new Consulta("27. Estado: eficacia del buffer pool de InnoDB",
            "Lo que importa es la proporción entre Innodb_buffer_pool_reads (fue a disco) y "
          + "read_requests (pidió una página). Mucho disco = el pool se quedó chico.",
            "SHOW GLOBAL STATUS WHERE Variable_name IN ("
          + "'Innodb_buffer_pool_read_requests','Innodb_buffer_pool_reads',"
          + "'Innodb_buffer_pool_pages_total','Innodb_buffer_pool_pages_free',"
          + "'Innodb_buffer_pool_pages_dirty','Innodb_buffer_pool_wait_free',"
          + "'Innodb_row_lock_waits','Innodb_row_lock_time_avg','Innodb_deadlocks',"
          + "'Innodb_rows_read','Innodb_rows_inserted','Innodb_rows_updated','Innodb_rows_deleted')"));

        c.add(new Consulta("28. Estado: tablas y bloqueos",
            "SHOW GLOBAL STATUS WHERE Variable_name IN ("
          + "'Open_tables','Opened_tables','Open_table_definitions','Opened_table_definitions',"
          + "'Table_locks_immediate','Table_locks_waited','Table_open_cache_hits',"
          + "'Table_open_cache_misses','Table_open_cache_overflows')"));

        // Las siguientes suelen requerir privilegios que un usuario de aplicación no tiene.
        // Si fallan, se marcan como omitidas y no pasa nada.

        c.add(new Consulta("29. Consultas lentas registradas en tabla (requiere permiso sobre mysql)",
            "SELECT COUNT(*) AS registros, MIN(start_time) AS desde, MAX(start_time) AS hasta "
          + "FROM mysql.slow_log"));

        c.add(new Consulta("30. Las 20 consultas lentas más frecuentes (requiere permiso sobre mysql)",
            "SELECT LEFT(sql_text, 300) AS consulta, COUNT(*) AS veces, "
          + "ROUND(AVG(query_time_seconds),2) AS seg_promedio, MAX(rows_examined) AS max_filas_leidas "
          + "FROM ( SELECT CONVERT(sql_text USING utf8) AS sql_text, "
          + "              TIME_TO_SEC(query_time) AS query_time_seconds, rows_examined "
          + "       FROM mysql.slow_log ) z "
          + "GROUP BY LEFT(sql_text, 300) ORDER BY veces DESC LIMIT 20"));

        c.add(new Consulta("31. Resumen de sentencias por tiempo total (requiere performance_schema)",
            "SELECT DIGEST_TEXT AS consulta, COUNT_STAR AS veces, "
          + "ROUND(SUM_TIMER_WAIT/1000000000000,1) AS seg_totales, "
          + "ROUND(AVG_TIMER_WAIT/1000000000,1) AS ms_promedio, "
          + "SUM_ROWS_EXAMINED AS filas_leidas, SUM_ROWS_SENT AS filas_devueltas, "
          + "SUM_NO_INDEX_USED AS veces_sin_indice "
          + "FROM performance_schema.events_statements_summary_by_digest "
          + "WHERE SCHEMA_NAME = DATABASE() "
          + "ORDER BY SUM_TIMER_WAIT DESC LIMIT 25"));

        c.add(new Consulta("32. Índices que nunca se han usado (requiere performance_schema)",
            "SELECT object_name AS tabla, index_name AS indice, count_star AS usos "
          + "FROM performance_schema.table_io_waits_summary_by_index_usage "
          + "WHERE object_schema = DATABASE() AND index_name IS NOT NULL "
          + "  AND index_name <> 'PRIMARY' AND count_star = 0 "
          + "ORDER BY object_name, index_name"));

        c.add(new Consulta("33. Conexiones activas en este momento (requiere privilegio PROCESS)",
            "SELECT USER AS usuario, HOST AS origen, DB AS base, COMMAND AS comando, "
          + "TIME AS segundos, STATE AS estado, LEFT(INFO, 200) AS consulta "
          + "FROM information_schema.PROCESSLIST ORDER BY TIME DESC LIMIT 50"));

        c.add(new Consulta("34. Conexiones agrupadas por origen (requiere privilegio PROCESS)",
            "Muchas conexiones desde un mismo host en estado Sleep suele significar "
          + "que la aplicación abre conexiones y no las devuelve.",
            "SELECT SUBSTRING_INDEX(HOST,':',1) AS origen, COMMAND AS comando, "
          + "COUNT(*) AS conexiones, MAX(TIME) AS seg_mas_vieja "
          + "FROM information_schema.PROCESSLIST GROUP BY origen, comando ORDER BY conexiones DESC"));

        return c;
    }

    // ------------------------------------------------------------------

    private static void correr(Connection cn, Consulta c) throws Exception {
        escribir("");
        escribir("================================================================");
        escribir(c.titulo);
        if (c.nota != null) {
            escribir("-- " + c.nota);
        }
        escribir("================================================================");

        Statement st = null;
        ResultSet rs = null;
        try {
            st = cn.createStatement();
            st.setFetchSize(100);
            rs = st.executeQuery(c.sql);
            volcar(rs);
            ejecutadas++;
        } catch (SQLException e) {
            omitidas++;
            escribir("(omitida) " + e.getMessage());
        } finally {
            if (rs != null) { try { rs.close(); } catch (SQLException ignorado) { } }
            if (st != null) { try { st.close(); } catch (SQLException ignorado) { } }
        }
    }

    /** Vuelca el ResultSet como tabla de texto de ancho fijo. */
    private static void volcar(ResultSet rs) throws Exception {
        ResultSetMetaData md = rs.getMetaData();
        int n = md.getColumnCount();

        List<String[]> filas = new ArrayList<String[]>();
        String[] encabezado = new String[n];
        for (int i = 1; i <= n; i++) {
            encabezado[i - 1] = md.getColumnLabel(i);
        }
        filas.add(encabezado);

        int leidas = 0;
        boolean cortado = false;
        while (rs.next()) {
            if (leidas >= MAX_FILAS) { cortado = true; break; }
            String[] fila = new String[n];
            for (int i = 1; i <= n; i++) {
                String v = rs.getString(i);
                if (v == null) {
                    v = "NULL";
                }
                v = v.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ');
                if (v.length() > 120) {
                    v = v.substring(0, 117) + "...";
                }
                fila[i - 1] = v;
            }
            filas.add(fila);
            leidas++;
        }

        if (leidas == 0) {
            escribir("(sin resultados)");
            return;
        }

        int[] ancho = new int[n];
        for (String[] f : filas) {
            for (int i = 0; i < n; i++) {
                if (f[i].length() > ancho[i]) { ancho[i] = f[i].length(); }
            }
        }

        StringBuilder sep = new StringBuilder();
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < ancho[i] + 2; k++) { sep.append('-'); }
            sep.append('+');
        }

        for (int f = 0; f < filas.size(); f++) {
            StringBuilder linea = new StringBuilder();
            for (int i = 0; i < n; i++) {
                linea.append(' ').append(pad(filas.get(f)[i], ancho[i])).append(" |");
            }
            escribir(linea.toString());
            if (f == 0) { escribir(sep.toString()); }
        }

        escribir("(" + leidas + " renglones" + (cortado ? ", recortado en " + MAX_FILAS : "") + ")");
    }

    private static String pad(String s, int ancho) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < ancho) { sb.append(' '); }
        return sb.toString();
    }

    private static void escribir(String s) throws Exception {
        out.write(s);
        out.write(System.getProperty("line.separator"));
    }

    private static class Consulta {
        final String titulo;
        final String nota;
        final String sql;

        Consulta(String titulo, String sql) {
            this(titulo, null, sql);
        }

        Consulta(String titulo, String nota, String sql) {
            this.titulo = titulo;
            this.nota = nota;
            this.sql = sql;
        }
    }
}
