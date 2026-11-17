# Almacén de medicamentos e insumos médicos

Login con fondo animado, página de inicio con menú dinámico y cuatro perfiles
de usuario.

Spring Boot 2.7 · Java 8 · JSF 2.3 (JoinFaces) · PrimeFaces · Spring Security ·
MariaDB

---

## Antes que nada: no pude compilarlo

El entorno donde trabajé tiene bloqueado Maven Central, así que **no pude
resolver dependencias ni correr `mvn package`**. Lo digo de frente porque
cambia qué tan verificado está esto.

Lo que sí verifiqué, abajo está el detalle: el esquema y las consultas contra
MariaDB real, el armado del árbol del menú con los datos de los cuatro
perfiles, los hash de las contraseñas, el XML de todas las vistas, la sintaxis
de todo el Java y el fondo animado en Chromium.

Lo que no: que las versiones de las dependencias resuelvan entre sí y que
Spring levante. La primera compilación te puede pedir algún ajuste de versión.

---

## Cómo arrancarlo

**1. Base de datos**

```bash
mariadb < sql/01_esquema.sql
mariadb < sql/02_datos_iniciales.sql
```

Los dos archivos empiezan con `SET NAMES utf8mb4`. No es adorno: el cliente de
MariaDB usa latin1 por omisión en muchas instalaciones, y sin esa línea
"almacén" se guarda como "almacÃ©n" y así sale en el menú. Me pasó mientras
probaba.

Luego descomenta el bloque final de `01_esquema.sql` para crear el usuario de
la aplicación con permisos acotados.

**2. Credenciales por variables de entorno**

```bash
export BD_URL="jdbc:mariadb://localhost:3306/almacen_med?useUnicode=true&characterEncoding=UTF-8"
export BD_USUARIO=app_almacen
export BD_CLAVE=tu_clave
```

`application.yml` las lee de ahí y solo cae a valores de desarrollo si no
existen. Así el archivo no lleva ningún secreto al repositorio.

**3. Arrancar**

```bash
mvn spring-boot:run
```

Y abrir `http://localhost:8080/login.xhtml`

**4. Entrar**

Cuatro cuentas, todas con la misma contraseña temporal **`Cambiar.2026`** y el
indicador `forzar_cambio_clave` encendido:

| Usuario | Perfil | Qué ve |
|---|---|---|
| `admin` | Administrador | Entradas, Salidas, Impresiones, Administración |
| `recibo` | Recibo | Entradas completas, Impresiones → Marbetes |
| `almacen` | Almacén | Consulta de entradas, Salidas completas, Marbetes y Folios |
| `factura` | Facturación | Consulta de salidas, Impresiones completas |

Cámbialas antes de que esto toque un servidor real. La pantalla de cambio de
contraseña todavía no existe; el indicador ya está en la tabla esperándola.

---

## Cómo funciona el menú dinámico

No hay un solo `if` por rol en el código. El menú sale de dos tablas:

```
menu       árbol de opciones (padre_id NULL = grupo raíz)
rol_menu   qué opción ve cada rol
```

Agregar una opción es capturar un renglón en `menu` y sus permisos en
`rol_menu`. No se recompila nada.

`MenuRepositorio.buscarPorUsuario()` trae raíces e hijos en un solo viaje, ya
ordenados. `MenuServicio.armarArbol()` los cuelga y descarta los grupos que
quedaron sin nada adentro.

Dos detalles que medí y que vale la pena conocer:

**`DISTINCT`, no `GROUP BY`.** Un usuario con dos roles que compartan opciones
las traería repetidas. Con los datos de arranque, un usuario con dos roles pasa
de 11 opciones a 15 renglones sin el `DISTINCT`.

**El menú vive en la sesión.** Se arma al entrar y se guarda. Si un
administrador cambia permisos, el usuario los ve hasta volver a entrar, o
cuando alguien llame a `sesionBean.recargarMenu()`. Para una pantalla que se usa
toda la jornada, repetir la consulta en cada navegación no aporta nada.

**Esconder no es proteger.** El menú oculta lo que no corresponde, pero
cualquiera puede teclear la dirección. Las reglas de `SeguridadConfig`
(`/admin/**` → `hasRole("ADMINISTRADOR")`, etc.) son las que de verdad impiden
el paso.

---

## El fondo del login

Tres capas de CSS y SVG en línea: un degradado quieto, una silueta de anaqueles
que recorre la pantalla en 90 segundos, y cinco piezas (frasco, caja, anaquel,
cápsula, cruz) que flotan. Ni una imagen que descargar, ni una línea de
JavaScript.

Respeta `prefers-reduced-motion`: quien tenga activado "reducir movimiento" ve
el mismo fondo, quieto. No es un detalle estético — el movimiento continuo
molesta a quien tiene vértigo o migraña, y esta pantalla se abre todos los días.

---

## Decisiones que conviene conocer

**El formulario del login es HTML plano, no `<h:form>`.** Spring Security espera
un POST normal a `/login`; un `h:form` manda un postback de JSF con su propio
ViewState y el filtro no lo reconocería. Es la forma estándar de juntar las dos
cosas, y por eso el token CSRF va a mano en esa página.

**CSRF queda encendido en todo.** En el resto de la aplicación el token lo
inserta `WEB-INF/plantillas/csrf.xhtml` dentro de cada `<h:form>`. Es HTML plano
dentro del formulario, así que viaja también en las peticiones AJAX de JSF, que
serializan el formulario completo.

**Cerrar sesión es POST, no un enlace.** Un GET se puede disparar desde una
imagen en otra página.

**Los beans son de Spring, no de CDI.** Spring Boot no trae CDI, así que
`@Named` con `javax.faces.view.ViewScoped` no funcionaría. `SesionBean` es un
`@Component @SessionScope` y el `SpringBeanFacesELResolver` —declarado en
`faces-config.xml`— es lo que hace que `#{sesionBean}` se resuelva.

**La pantalla nunca dice por qué falló el acceso.** Siempre "usuario o
contraseña incorrectos", aunque la cuenta esté bloqueada o inactiva. El motivo
real queda en la bitácora. Distinguirlos le confirma a quien tantea qué cuentas
existen. Por lo mismo, `hideUserNotFoundExceptions` está activo: sin eso, un
usuario inexistente responde notoriamente más rápido.

**La bitácora guarda una etiqueta fija, no el mensaje de la excepción.** Si
alguien se equivoca de campo y teclea su contraseña en el usuario, ese texto no
termina guardado.

**Hibernate no toca el esquema** (`ddl-auto: none`). El esquema lo manda
`sql/01_esquema.sql`. Dejar que la aplicación altere tablas en producción es
cómodo hasta el día en que borra una columna.

**`open-in-view: false`.** Con el valor por omisión, la sesión de JPA queda
abierta durante todo el renderizado y las consultas perezosas se disparan desde
la vista sin que nadie lo note.

---

## Verificación hecha

**Base de datos**, contra MariaDB 10.11 real:

| Caso | Resultado |
|---|---|
| Esquema y datos iniciales | cargan sin error |
| Opciones por perfil | admin 17, almacén 9, recibo 6, facturación 6 |
| Grupos vacíos | ninguno |
| Opciones sin permiso asignado | ninguna |
| `DISTINCT` | confirmado necesario: 11 opciones contra 15 renglones sin él |

**Armado del árbol** (`MenuServicio.armarArbol()` real, con los renglones que
devuelve la consulta real, para los cuatro perfiles):

| Caso | Resultado |
|---|---|
| Grupos y su orden | el esperado en los cuatro perfiles |
| Todas las hojas tienen destino | sí |
| Ningún grupo queda vacío | sí |
| Todos los renglones quedan colgados | sí (hojas + grupos = renglones) |
| Usuario inexistente | árbol vacío, sin excepción |
| Hijo cuyo padre no vino | se descarta, no inventa una raíz |

**Contraseñas**: los cuatro hash verifican `Cambiar.2026`, rechazan cualquier
otra, son costo 10 —el mismo que declara `SeguridadConfig`— y los cuatro son
distintos entre sí, cada uno con su propia sal.

**Vistas**: los 17 XHTML más `faces-config.xml` y `pom.xml` son XML bien
formado. `application.yml` es YAML válido.

**Java**: los 15 archivos compilan sin un solo error de sintaxis. Los 245
errores que quedan son todos de resolución por la falta de los jars; lo
comprobé quitando las anotaciones `@Override` —las únicas tres que no eran
"cannot find symbol"— y el conteo baja a cero.

**Fondo animado**, en Chromium:

| Caso | Resultado |
|---|---|
| El fondo se desplaza | sí, medido a 1.5 s de distancia |
| Con `prefers-reduced-motion` | las animaciones se detienen |
| En 390 px de ancho | sin desbordamiento horizontal |
| Errores de consola | ninguno |
| Contraste del texto | etiquetas 7.4, título 5.5, aviso 6.3, botón 5.2 — todos sobre el mínimo AA de 4.5 |

El pie de página daba 4.76, que pasa pero va al filo para un texto de 0.72 rem;
lo subí a 5.76.

---

## Lo que falta para que sea un sistema

Esto es el andamio: acceso, perfiles y navegación. Las trece pantallas del menú
existen como marcadores de posición para que el menú y los permisos se puedan
probar de punta a punta, pero no hacen nada todavía.

Lo siguiente, en el orden que yo seguiría:

1. **Cambio de contraseña obligatorio** cuando `forzar_cambio_clave` está en 1.
   Hoy el indicador se guarda pero nadie lo revisa.
2. **Manejo de `ViewExpiredException`**: cuando la sesión vence a media captura,
   JSF lanza esa excepción y el usuario ve una pantalla de error cruda.
3. **Administración de usuarios, roles y menús**, que es lo que evita que
   dependas de alguien con acceso a la base para dar de alta a una persona.
4. **Las pantallas de almacén**, que es donde está el trabajo de verdad.

Si quieres, seguimos por cualquiera de ellas.
