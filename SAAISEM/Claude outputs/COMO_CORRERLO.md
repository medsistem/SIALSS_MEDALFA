# Qué necesito para analizar y optimizar la base

No puedo consultar tu base desde aquí: está en 192.168.0.184, una IP de tu red
interna, y esta sesión no tiene shell en tu máquina. Así que el camino es que
tú corras un diagnóstico de solo lectura y me mandes la salida.

---

## Lo que te mando

| Archivo | Para qué |
|---|---|
| `Diagnostico.java` | Programa Java 8 que corre las 34 consultas y deja un solo archivo de texto |
| `diagnostico.sql` | Lo mismo, para línea de comandos o cliente gráfico |

Son 34 consultas de solo lectura. **No consultan ninguna fila de tus tablas**:
solo `information_schema`, `SHOW VARIABLES` y `SHOW STATUS`. Lo verifiqué
corriéndolo contra una base con datos y revisando que ningún valor de las
tablas apareciera en la salida. Tampoco crean, alteran ni borran nada.

---

## Cómo correrlo

### Opción A — el programa Java (la que recomiendo)

Ya tienes Java y el driver de MariaDB en el proyecto, así que no necesitas
instalar nada.

```bat
javac Diagnostico.java

java -cp .;D:\Users\dotor\Documents\GitHub\MEDALFA\SIALSS_MEDALFA\SAAISEM\build\web\WEB-INF\lib\mariadb-java-client-2.5.4.jar ^
     Diagnostico 192.168.0.184 3306 sialss_mdf sialss_mdf TU_CLAVE diagnostico.txt
```

La clave va como argumento, no dentro del archivo, para que no quede escrita en
ningún lado del repositorio.

Al terminar te dice cuántas consultas corrieron y cuántas se omitieron.
Con el usuario de la aplicación es normal que **4 se omitan por permisos**; lo
probé y el programa sigue adelante sin problema.

### Opción B — línea de comandos

```bat
mariadb -h 192.168.0.184 -u sialss_mdf -p sialss_mdf --force --table ^
        < diagnostico.sql > diagnostico.txt 2>&1
```

El `--force` es necesario: sin él, la primera consulta sin permiso aborta el
resto.

### Opción C — HeidiSQL, DBeaver o Workbench

Abre `diagnostico.sql`, córrelo completo y exporta los resultados. Cada
consulta viene precedida de un `SELECT '...' AS seccion` para que sepas qué
estás viendo en cada rejilla.

---

## Antes de mandarme el archivo

Dos secciones pueden traer texto de consultas reales, y en consultas reales a
veces viajan valores literales:

- **29 y 30** (`mysql.slow_log`): traen el texto tal cual de las consultas
  lentas. Si te salen con contenido, dales una leída antes de mandármelas.
- **31** (`performance_schema`): esta viene normalizada, los literales
  aparecen como `?`. No hay riesgo.

Lo más probable es que las tres se omitan por permisos y el punto sea irrelevante.

---

## Lo que voy a poder decirte con eso

- Tablas sin llave primaria y sin índices, con su tamaño y número de filas.
- Índices redundantes (uno que ya está contenido en otro) y candidatos a sobrar.
- Si hay llaves foráneas declaradas o la integridad vive solo en el código.
- Fechas guardadas como texto, `VARCHAR` sobredimensionados, colaciones
  mezcladas (que tiran los índices en los JOIN), tablas con demasiadas columnas.
- `AUTO_INCREMENT` acercándose al tope de su tipo.
- Si el buffer pool de InnoDB alcanza para tu volumen, y qué tanto se está
  yendo a disco.
- Si la aplicación está dejando conexiones abiertas (`Threads_created` contra
  `Connections`, y conexiones dormidas por origen).
- Qué tan seguido se recorren tablas completas y cuántas tablas temporales
  acaban en disco.

---

## Lo que NO voy a poder ver con el usuario de la aplicación

El **registro de consultas lentas**, que es la mejor fuente para saber qué duele
de verdad. Vive en `mysql.slow_log` o en un archivo del servidor, y el usuario
`sialss_mdf` no tiene permiso para leerlos. Igual con `performance_schema`, y
con `SHOW ENGINE INNODB STATUS`.

Dos salidas, y puedes tomar las dos:

**1. Pedir que lo activen.** Si infraestructura puede, con esto basta y no
reinicia nada:

```sql
SET GLOBAL slow_query_log = ON;
SET GLOBAL long_query_time = 1;        -- segundos
SET GLOBAL log_output = 'TABLE';       -- queda en mysql.slow_log
-- y luego: GRANT SELECT ON mysql.slow_log TO 'sialss_mdf'@'%';
```

Déjalo un día hábil completo y vuelve a correr el diagnóstico. Sube un poco el
disco y el I/O, así que no lo dejes encendido para siempre.

**2. Ir por el lado del código.** Ya tengo acceso a tu carpeta del proyecto.
Puedo sacar todas las sentencias SQL que están escritas en los JSP y servlets,
revisarlas y correrles `EXPLAIN`. Eso no necesita ningún permiso extra y
encuentra el mismo tipo de problema desde el otro lado. Dime si quieres que lo
haga y empiezo.

---

## Opcional, pero ayuda mucho

Un volcado del esquema sin datos:

```bat
mysqldump -h 192.168.0.184 -u sialss_mdf -p --no-data --routines --triggers ^
          --skip-add-drop-table sialss_mdf > esquema.sql
```

No lleva ni una fila, así que no sale ningún dato de paciente ni de inventario.
Sí lleva el cuerpo de tus procedimientos almacenados; si eso te incomoda,
quítale `--routines`. Con el esquema completo puedo montar una copia vacía aquí
y revisar los `CREATE TABLE` con detalle, en vez de deducirlos de
`information_schema`.

---

## Verificación de los scripts

Los corrí contra MariaDB 10.11 con un usuario con los mismos permisos que
tendría el tuyo (`SELECT, INSERT, UPDATE, DELETE` sobre una sola base, nada más):

| | |
|---|---|
| Versión Java | 30 de 34 consultas ejecutadas, 4 omitidas con mensaje claro |
| Versión SQL | las 34 secciones salen, los 4 errores de permiso no detienen el resto |
| Datos de tablas en la salida | ninguno, verificado buscando valores conocidos |

Los dos archivos se generan del mismo catálogo de consultas, así que no se
pueden desincronizar.
