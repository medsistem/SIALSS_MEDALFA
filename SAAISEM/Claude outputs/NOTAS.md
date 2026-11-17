# GeneraMarbeteManual.jsp — diagnóstico

## La causa

El archivo en disco se llama **`MarbeteManual.jasper`**. El JSP pedía
**`Marbete_Manual.jasper`**, con guion bajo.

```java
// línea 41, original
Ruta = "/reportes/Marbete_Manual.jasper";

// corregido
Ruta = "/reportes/MarbeteManual.jasper";
```

Revisé las dos carpetas de tu proyecto y en ninguna existe un archivo con
guion bajo:

| Carpeta | `MarbeteManual.jasper` | `Marbete_Manual.jasper` |
|---|---|---|
| `web\reportes\` | sí, 40,668 bytes | no existe |
| `build\web\reportes\` | sí, 40,668 bytes | no existe |

El `build` está al día: NetBeans ya había copiado el `.jasper` recién
compilado. No es un problema de Clean and Build.

**Si solo quieres destrabarte, cambia esa línea y listo.** El resto de este
archivo es lo demás que encontré en el mismo JSP.

---

## Comprobado, no supuesto

Monté tu `MarbeteManual.jasper`, tu `jasperreports-5.1.0.jar`, `iText`, `zxing`
y `groovy` tal como están en `build\web\WEB-INF\lib`, más un procedimiento
`MarbeteManual` de prueba que devuelve las 21 columnas que declara el `.jrxml`.

- Ruta original → **no existe**.
- Ruta corregida → existe, carga, conecta, ejecuta el `CALL`, mapea los campos,
  arma el código QR y produce un **PDF de 32,517 bytes** con los datos en su
  lugar (clave, lote, tarima, marca, modelo, cantidad).

En el camino me equivoqué en algo y lo corrijo aquí: supuse que `Proyecto`
también iba a fallar, porque el JSP lo manda como `Integer` (línea 34) y el
reporte lo declara `java.lang.String`. Lo probé y **JasperReports 5.1.0 lo
acepta sin problema**. El PDF sale igual con `Integer` que con `String`. No
hace falta tocarlo.

---

## Lo demás que trae ese JSP

**Fuga de dos conexiones por cada marbete impreso.** Líneas 27-30:

```java
con.conectar();                 // abre la conexión A, la guarda en el campo conn
Connection conexion;
con.conectar();                 // abre B, conn = B. A queda abierta y sin referencia
conexion = con.getConn();       // abre C, conn = C. B queda abierta y sin referencia
```

Lo confirmé leyendo `ConectionDB`: tanto `conectar()` como `getConn()` hacen
`DriverManager.getConnection(...)` y sobrescriben el mismo campo, y
`cierraConexion()` cierra solo el último. Con `getConn()` basta: abre y guarda,
y el `finally` la cierra.

**El `sendRedirect` no corta la ejecución** (líneas 21-24). Sin `return`, el
resto del JSP sigue corriendo para alguien sin sesión. Además la ruta es
`index.jsp` y este archivo vive en `/Marbetes/`, así que apunta a
`/Marbetes/index.jsp`; debe ser `../index.jsp`.

**`Integer.parseInt(request.getParameter("Proyecto"))` sin validar** (línea 34).
Si el parámetro falta o no es número, lanza `NumberFormatException`.

**Cuando algo falla, el usuario ve una página en blanco.** El `catch` registra
en el log y no escribe nada en la respuesta: la ventana que abrió el
`window.open` queda vacía, sin explicación. Por eso tuviste que ir al log para
enterarte de este error. En la versión corregida el motivo se muestra en
pantalla.

**`e.getMessage()` suelto** en las líneas 63 y 68: llama al método y descarta
el resultado. No hace nada.

---

## Fuera de este archivo

`ConectionDB.java` trae usuario, contraseña y el IP del servidor de base de
datos escritos en el código, dentro de un repositorio de GitHub. Vale la pena
moverlos a un `context.xml` o a variables de entorno y rotar esa contraseña,
porque queda en el historial del repo aunque se borre del archivo.

También: `cierraConexion()` hace `this.conn.close()` sin verificar null, así
que lanza `NullPointerException` si nunca se llegó a conectar — justo dentro de
un `finally`, donde puede tapar la excepción original.

---

## Verificación de la versión corregida

Compilada con el compilador de JSP de Tomcat 9 (`org.apache.jasper.JspC`),
con tu `ConectionDB` y tu `jasperreports-5.1.0.jar` en el classpath:

```
INFO: Generation completed with [0] errors
```
