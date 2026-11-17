# Compras › Catálogos

## Alta Proveedor

**Para qué sirve:** Dar de alta proveedores y consultar los que ya existen.

**Quién la ve:** Perfiles 13 y 14.

**Cómo llegar:** Menú › Catálogos › Alta Proveedor.

**Paso a paso:**
1. Captura el **Nombre** y el **Teléfono** del proveedor (son obligatorios).
2. Si los tienes, captura **Dirección**, **Colonia**, **C.P.**, **RFC**, **FAX** y **Mail**.
3. Da clic en **Guardar**. El proveedor aparece en la tabla de abajo (Clave, Nombre, Dirección, Teléfono), que puedes buscar y ordenar.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Nombre | Nombre o razón social (hasta 60 caracteres) | Sí (*) |
| Teléfono | Solo números, con formato (XXX) XXX-XXXX | Sí (*) |
| Dirección | Calle y número (hasta 50 caracteres) | No |
| Colonia | Colonia (hasta 40 caracteres) | No |
| C.P. | Código postal, 5 números | No |
| RFC | RFC (hasta 15 caracteres) | No |
| FAX | Solo números | No |
| Mail | Correo electrónico (hasta 60 caracteres) | No |

**Mensajes y errores frecuentes:**
- "Tiene campos vacíos, verifique.": falta el **Nombre** o el **Teléfono**.
- "Ya esta registrado ese proveedor": ya existe un proveedor con ese nombre; no se duplica. Después de este aviso el sistema puede mostrar también "Proveedor capturado correctamente.", pero no se guarda un segundo registro.
- "Proveedor capturado correctamente.": el alta quedó guardada.

**Notas:**
- Los datos se guardan en mayúsculas.
- La clave del proveedor la asigna el sistema.
- La pantalla es la misma para los perfiles 13 y 14; en ambos se puede capturar.

## Catálogo de Marcas

**Para qué sirve:** Consultar las marcas de insumos y, si tu perfil lo permite, dar de alta marcas nuevas.

**Quién la ve:** Perfiles 13 y 14. Se abre en una ventana aparte.

**Cómo llegar:** Menú › Catálogos › Catálogo de Marcas.

**Paso a paso:**
1. Consulta la tabla de marcas (Clave y Descripción Marca).
2. Para dar de alta una marca, captura el **Nombre** y da clic en **Guardar**. Este formulario solo se muestra a ciertos perfiles (ver Notas).
3. Da clic en **SALIR** para cerrar la ventana.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Nombre | Nombre de la marca (hasta 60 caracteres) | Sí (*) |

**Mensajes y errores frecuentes:**
- "Tiene campos vacíos, verifique.": falta el **Nombre**.
- "Ya esta registrado ésta Marca": la marca ya existe.
- "Marca capturado correctamente.": el alta quedó guardada.

**Notas:**
- Diferencia por perfil: el formulario de alta (**Nombre** y **Guardar**) solo aparece para los perfiles 1, 4 y 11. Con los perfiles 13 y 14 la pantalla es solo de consulta: ves la lista de marcas, pero no puedes agregar.
- El nombre se guarda en mayúsculas.

## Catálogo de Medicamento

**Para qué sirve:** Buscar insumos para la salud por clave o descripción.

**Quién la ve:** Perfiles 13 y 14. Se abre en una ventana aparte.

**Cómo llegar:** Menú › Catálogos › Catálogo de Medicamento.

**Paso a paso:**
1. Captura la **CLAVE** o parte de la **Descripción**. Si no capturas nada, se muestra todo el catálogo.
2. Da clic en **Buscar**.
3. Revisa la tabla: CLAVE, CLAVE SS, Descripción, Presentación, Sts, Tipo Medicamento y Origen.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| CLAVE | Clave exacta del insumo (hasta 10 caracteres) | No |
| Descripción | Parte de la descripción (hasta 80 caracteres) | No |

**Mensajes y errores frecuentes:** Ninguno identificado; si no hay coincidencias la tabla queda vacía.

**Notas:**
- Si capturas las dos, la búsqueda usa la **Descripción**.
- Diferencia por perfil: las columnas de costo y el botón de modificar solo se muestran a los perfiles 1 y 12, y **Nuevo Insumo** solo al perfil 1. Con los perfiles 13 y 14 la pantalla es solo de consulta.
- En **Tipo Medicamento** verás "MEDICAMENTO" o "MAT. CURACIÓN".

## Puntos de Entregas

**Para qué sirve:** Consultar los puntos de entrega (unidades de atención).

**Quién la ve:** Perfil 13.

**Cómo llegar:** Menú › Catálogos › Puntos de Entregas.

**Paso a paso:**
1. Abre la opción.
2. Consulta la tabla con Clave, Nombre, Sts, Dirección y Tipo Unidad. Puedes buscar y ordenar con los controles de la tabla.

**Campos:** Sin campos de captura; solo la búsqueda de la tabla.

**Mensajes y errores frecuentes:** Ninguno identificado.

**Notas:**
- En la pantalla de Compras no hay un botón por renglón para abrir la modificación; la ventana **Modificar Unidad** (Nombre, Dirección, Sts, Tipo Unidad y Dispensador, con **Guardar** y **Cancelar**) existe en el código pero no se puede abrir desde esta pantalla. Para cambiar datos de una unidad usa el Catálogo de Unidades de Almacén, con un perfil que lo tenga.
