# Almacén › Usuario

## Cambio Contraseña

**Para qué sirve:** Consultar la lista de usuarios y cambiar la contraseña o el estatus de un usuario.

**Quién la ve:** Perfiles 100, 1, 2, 3, 4, 5, 6 y 17.

**Cómo llegar:** Menú › Usuario › Cambio Contraseña.

**Paso a paso:**
1. Abre la opción; se muestra la lista de usuarios con **Id**, **Nombre**, **Sts** (estatus) y **Acciones**. Si tu perfil no es 100, 1 o 5, solo ves tu propio usuario.
2. Da clic en el lápiz (**Modificar Password**) del usuario que quieres cambiar. Se abre la pantalla **Editar Usuario**.
3. Para cambiar la contraseña, captura **Ingrese Contraseña** y repítela en **Repita Contraseña**. Si hace falta, corrige el **Correo**.
4. Da clic en **Actualizar**.
5. Para suspender o reactivar al usuario (solo perfiles 100, 1 y 5), mueve el interruptor **Estatus** entre **Activo** y **Suspendido** y da clic en **Modificar Sts**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Estatus | **Activo** o **Suspendido** (solo perfiles 100, 1 y 5) | No |
| Nombre, Apellido Paterno, Apellido Materno | Solo se muestran; no se pueden cambiar | No |
| Ingrese Correo | Correo electrónico del usuario | Sí |
| Ingrese Contraseña | Contraseña nueva: de 8 a 16 caracteres, con una mayúscula y un número | Sí |
| Repita Contraseña | La misma contraseña | Sí |

**Mensajes y errores frecuentes:**
- "TE HA FALTADO INTRODUCIR LOS SIGUIENTES DATOS PARA ACTUALIZAR INFORMACIÓN…": lista los campos vacíos; complétalos y vuelve a intentar.
- El botón **Actualizar** permanece desactivado hasta que el cuadro "Requerimiento del password" marca en verde **Una Letra Mayuscula**, **Un Número**, **8 min - 16 max Caracteres** y **Contraseña Igual**.
- "Usuario Modificado Password" o "Usuario Modificado Estatus": el cambio se guardó (aparece en la lista).
- "Solo puede modificar su propio usuario.": con tu perfil solo puedes cambiar tu propia contraseña y no el estatus.

**Notas:** Al reactivar a un usuario suspendido se limpian sus intentos fallidos de entrada. La contraseña del alta de usuario lleva hasta 8 caracteres (ver **Nuevo Usuario**); aquí al cambiarla se aceptan de 8 a 16.

## Nuevo Usuario

**Para qué sirve:** Dar de alta usuarios del sistema.

**Quién la ve:** Perfiles 100, 1 y 5.

**Cómo llegar:** Menú › Usuario › Nuevo Usuario. La pantalla tiene pestañas: **Nuevo usuario**, **Perfiles de usuario** (solo perfil 100) y **Menús por perfil y usuario** (solo quien tenga esa opción).

**Paso a paso:**
1. Captura **Nombre**, **Apellido Paterno**, **Apellido Materno** y **Correo**.
2. Captura el **Usuario** con el que va a entrar.
3. Captura la contraseña en **Ingrese Contraseña** y repítela en **Repita Contraseña**.
4. Elige el **Tipo Usuario** (perfil).
5. Elige uno o más proyectos en la lista **--Seleccione Proyecto--**; se van sumando en el campo **Proyecto**.
6. Da clic en **AltaUsuario**. Para borrar lo capturado, da clic en **Limpiar**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Nombre | Nombre(s) | Sí |
| Apellido Paterno | Apellido paterno | Sí |
| Apellido Materno | Apellido materno | Sí |
| Correo | Correo electrónico | Sí |
| Usuario | Nombre de usuario | Sí |
| Contraseña / Repita Contraseña | Máximo 8 caracteres, con una mayúscula y un número; deben ser iguales | Sí |
| Tipo Usuario | Perfil del usuario | Sí |
| Proyecto | Proyectos a los que tendrá acceso | Sí |

**Mensajes y errores frecuentes:**
- "TE HA FALTADO INTRODUCIR LOS SIGUIENTES DATOS…": lista los campos vacíos; complétalos.
- Si no tienes permiso, la pantalla marca "sin permiso".

**Notas:**
- Los perfiles **ADMINISTRADOR** y **SUPERUSUARIO** solo los asigna el superusuario.
- No aparecen perfiles suspendidos.
- El cuadro "Requerimiento del password" te muestra qué falta (mayúscula, número, 8 caracteres, contraseña igual).

## Parámetro Facturación

**Para qué sirve:** Cambiar el parámetro de facturación y el proyecto con los que trabaja tu usuario.

**Quién la ve:** Perfiles 100, 1, 2, 3, 4, 5, 6 y 17.

**Cómo llegar:** Menú › Usuario › Parámetro Facturación.

**Paso a paso:**
1. Revisa el **Parámetro actual** y el **Proyecto actual**.
2. Elige el **Nuevo parámetro** y el **Nuevo proyecto**.
3. Da clic en **ACTUALIZAR** y confirma.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Parámetro actual | Solo lectura | No |
| Nuevo parámetro | Parámetro de facturación (lista de ubicaciones con las que se surte) | Sí; con **-- Seleccione --** el parámetro no cambia |
| Proyecto actual | Solo lectura | No |
| Nuevo proyecto | Proyecto de trabajo | Sí |

**Mensajes y errores frecuentes:**
- "Datos Actualizados": el cambio se guardó.
- "Seleccione Proyecto": elige un **Nuevo proyecto** antes de dar clic en **ACTUALIZAR**.

**Notas:** Abajo de la pantalla se listan las ubicaciones que incluye el parámetro actual (con su descripción y código de barras). El cambio es solo para tu usuario.

## Menús por Perfil y Usuario

**Para qué sirve:** Definir qué opciones del menú ve cada perfil o cada usuario.

**Quién la ve:** Perfil 100.

**Cómo llegar:** Menú › Usuario › Menús por Perfil y Usuario.

**Paso a paso:**
1. Elige el **Acceso** (Almacén, Compras, etc.).
2. En la pestaña **Por perfil**, elige el **Perfil**, marca u omite las opciones (**Marcar todas**, **Quitar todas**, **Deshacer cambios**) y da clic en **Guardar menú del perfil**.
3. En la pestaña **Por usuario**, busca al usuario, ajusta sus opciones y da clic en **Guardar permisos del usuario**. **Todo según su perfil** quita los ajustes propios.
4. En **Historial de cambios** consulta qué se modificó.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Acceso | Almacén, Compras u otro | Sí |
| Perfil | Perfil a configurar | Sí (pestaña Por perfil) |
| Buscar usuario / Usuario | Usuario a configurar | Sí (pestaña Por usuario) |

**Mensajes y errores frecuentes:**
- "Se guardó el menú del perfil …" y "Se guardaron los permisos de …": el cambio quedó registrado.
- "No había cambios que guardar.": no modificaste nada.
- "Hay cambios sin guardar. ¿Descartarlos?": confirma solo si quieres perder lo marcado.
- "No puede quitarle a su propio perfil la opción "Menús por Perfil y Usuario"" y "No puede bloquearse a sí mismo la opción "Menús por Perfil y Usuario".": el sistema evita que te quedes sin esta pantalla.
- "La página caducó. Vuelva a abrirla.": recarga la pantalla y repite el cambio.
- "No se pudo guardar. No se cambió nada.": inténtalo de nuevo; si persiste, avisa a sistemas.
- Aviso "Todavía no se pueden guardar cambios.": el menú se toma del archivo del sistema porque faltan las tablas de menú en la base; pide al administrador de la base que las cree.

**Notas:** Una opción bloqueada a un usuario tampoco se puede abrir escribiendo su dirección. El menú se lee al abrir cada pantalla; pide a la persona afectada que recargue o vuelva a entrar para ver el cambio. Pendiente de confirmar: no se encontró en el código si una sesión ya abierta conserva el menú anterior.

## Mi PIN de Acceso

**Para qué sirve:** Crear, cambiar o quitar tu PIN numérico para entrar al sistema o desbloquear la pantalla sin escribir la contraseña.

**Quién la ve:** Perfiles 100, 1, 2, 3, 4, 5, 6 y 17.

**Cómo llegar:** Menú › Usuario › Mi PIN de Acceso.

**Paso a paso (crear o cambiar):**
1. Captura el **PIN nuevo**.
2. Repítelo en **Repita el PIN**.
3. Captura **Su contraseña actual**.
4. Da clic en **Guardar PIN**.

**Paso a paso (quitar):**
1. En **Quitar PIN**, captura **Su contraseña actual**.
2. Da clic en **Quitar PIN**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| PIN nuevo | Solo números, la cantidad exacta que indica la pantalla | Sí |
| Repita el PIN | El mismo PIN | Sí |
| Su contraseña actual | Tu contraseña | Sí |

**Mensajes y errores frecuentes:**
- "El PIN debe tener exactamente N números.": captura la cantidad indicada.
- "Los dos PIN no son iguales.": vuelve a escribirlos.
- "Escriba su contraseña actual.": captura tu contraseña.

**Notas:** No se permiten PIN con números repetidos (111111) ni seguidos (123456). Tu contraseña sigue funcionando. Los intentos fallidos con PIN o con contraseña cuentan igual para el bloqueo.

## Perfiles de Usuario

**Para qué sirve:** Dar de alta perfiles nuevos y suspender o reactivar los existentes.

**Quién la ve:** Perfil 100 (SUPERUSUARIO).

**Cómo llegar:** Menú › Usuario › Perfiles de Usuario.

**Paso a paso:**
1. Captura el **Nombre del perfil**.
2. Da clic en **Dar de alta**.
3. En la lista **Perfiles** puedes suspender o reactivar un perfil.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Nombre del perfil | De 3 a 30 caracteres: letras, números, espacios, punto y guion | Sí |

**Mensajes y errores frecuentes:**
- "El nombre del perfil debe tener de 3 a 30 caracteres."
- "El nombre solo puede llevar letras, números, espacios, punto y guion."
- "Ya existe el perfil …": usa otro nombre.
- "El perfil SUPERUSUARIO no se puede suspender."
- "No puede suspender su propio perfil."

**Notas:** Un perfil suspendido no puede iniciar sesión y no se ofrece al dar de alta usuarios. El nombre se guarda en mayúsculas.

## Manual de Usuario

**Para qué sirve:** Consultar este manual dentro del sistema, solo con los módulos y opciones que tienes en tu menú, y descargarlo en PDF.

**Quién la ve:** Perfiles 100, 1, 2, 3, 4, 5, 6 y 17.

**Cómo llegar:** Menú › Usuario › Manual de Usuario.

**Paso a paso:**
1. Abre la opción. Arriba ves tu acceso, tu perfil y la versión del manual.
2. Usa el **Contenido** para ir al módulo u opción que necesitas.
3. Da clic en **Descargar PDF** para obtener el mismo manual en un archivo que puedes guardar o imprimir.
4. Si eres administrador (perfiles 100, 1 o 5), elige otro perfil en la lista de arriba para ver el manual de ese perfil.

**Campos:** No tiene campos de captura.

**Mensajes y errores frecuentes:**
- "Esta opción todavía no está documentada en el manual.": la opción está en tu menú, pero aún no tiene instrucciones; avisa a sistemas.
- "Este perfil no tiene opciones en el menú, por eso el manual no tiene módulos.": tu perfil no tiene opciones asignadas.

**Notas:** El manual se actualiza solo cuando sistemas publica cambios. Si te bloquearon o te permitieron alguna opción en particular, tu manual también lo refleja.
