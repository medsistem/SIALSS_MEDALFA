# Almacén › Catálogos

## Catálogo

**Para qué sirve:** Consultar el catálogo de insumos para clientes.

**Quién la ve:** Perfil 17.

**Cómo llegar:** Menú › Catálogos › Catálogo.

**Paso a paso:**
1. Abre la opción. Se muestra la tabla con los insumos de tu proyecto, ordenados por clave.
2. Para encontrar un insumo, escribe en el cuadro de búsqueda de la tabla (clave o parte de la descripción).
3. Si hay muchos renglones, usa la paginación al pie de la tabla.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Búsqueda de la tabla | Clave o parte de la descripción del insumo | No |

La tabla muestra las columnas **CLAVE**, **Descripción** y **Sts** (estatus del insumo).

**Mensajes y errores frecuentes:** Esta pantalla no genera mensajes. Si la tabla aparece vacía, tu perfil no tiene insumos ligados a ningún proyecto; avisa a tu supervisor.

**Notas:** Es solo de consulta: no se puede capturar ni modificar nada. Solo ves los insumos de los proyectos asignados a tu usuario.

## Catálogo de CB

**Para qué sirve:** Consultar y dar mantenimiento a los códigos de barras de los insumos.

**Quién la ve:** Perfiles 100, 1, 2, 3, 4, 5 y 6. Se abre en una ventana aparte.

**Cómo llegar:** Menú › Catálogos › Catálogo de CB.

**Paso a paso:**
1. Captura la **CLAVE**, la **Descripción** o el **CB** que buscas (con uno basta).
2. Da clic en el botón de la lupa.
3. Revisa en la tabla la **Clave**, **Descripcion**, **Lote**, **Caducidad** y el código de barras impreso de cada lote.
4. Para cerrar la ventana, da clic en el botón rojo de salida.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| CLAVE | Clave del insumo (hasta 10 caracteres); al escribir se sugieren claves | No |
| Descripción | Parte de la descripción del insumo (hasta 80 caracteres); al escribir se sugieren descripciones | No |
| CB | Inicio del código de barras (hasta 14 caracteres); al escribir se sugieren códigos | No |

**Mensajes y errores frecuentes:** Esta pantalla no muestra mensajes de aviso. Si la tabla sale vacía, no hay lotes con esa clave, descripción o código de barras.

**Notas:** Es solo de consulta: no permite dar de alta ni modificar códigos de barras. Si capturas varios criterios se usa solo uno, en este orden: clave, descripción y código de barras. Sin criterios muestra los primeros 100 lotes. Los lotes con lote "X" y las claves de ajuste no aparecen.

## Catálogo de Marcas

**Para qué sirve:** Dar de alta marcas de insumos.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6. Se abre en una ventana aparte.

**Cómo llegar:** Menú › Catálogos › Catálogo de Marcas.

**Paso a paso:**
1. Captura el **Nombre** de la marca.
2. Da clic en **Guardar**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Nombre | Nombre de la marca | Sí (*) |

**Mensajes y errores frecuentes:**
- "Tiene campos vacíos, verifique.": captura el **Nombre** antes de guardar.
- "Ya esta registrado ésta Marca": la marca ya existe; no se duplica.
- "Marca capturado correctamente.": la marca quedó dada de alta.

**Notas:** El nombre se guarda en mayúsculas. Abajo aparece la lista de marcas existentes, con buscador. El formulario para guardar solo lo ven los perfiles 1, 4 y 11; los demás perfiles solo consultan la lista.

## Catálogo de Medicamento

**Para qué sirve:** Buscar insumos para la salud por clave o descripción.

**Quién la ve:** Perfiles 100, 1, 2, 3, 4, 5 y 6. Se abre en una ventana aparte.

**Cómo llegar:** Menú › Catálogos › Catálogo de Medicamento.

**Paso a paso:**
1. Captura la **CLAVE** o la **Descripción**.
2. Da clic en **Buscar**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| CLAVE | Clave del insumo | No |
| Descripción | Parte de la descripción | No |

**Mensajes y errores frecuentes:** Esta pantalla no muestra mensajes. Si la tabla sale vacía, no hay insumos con esa clave o descripción.

**Notas:** La **CLAVE** debe ser exacta; la **Descripción** busca por cualquier parte del texto. Si capturas ambas se usa solo la descripción. Sin datos se listan todos los insumos. La tabla muestra **CLAVE**, **CLAVE SS**, **Descripción**, **Presentación**, **Sts**, **Tipo Medicamento** y **Origen**. El perfil 1 también ve el botón **Nuevo Insumo**; los perfiles 1 y 12 ven el **Costo** y el botón de lápiz para modificar cada insumo.

## Catálogo de Proveedores

**Para qué sirve:** Dar de alta proveedores.

**Quién la ve:** Perfiles 100, 1, 2, 3, 4, 5 y 6. Se abre en una ventana aparte.

**Cómo llegar:** Menú › Catálogos › Catálogo de Proveedores.

**Paso a paso:**
1. Captura los datos del proveedor.
2. Da clic en **Guardar**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Nombre | Nombre o razón social | Sí (*) |
| Teléfono | Teléfono | Sí (*) |
| Dirección, Colonia, C.P. | Domicilio | No |
| RFC, FAX, Mail | Datos fiscales y de contacto | No |

**Mensajes y errores frecuentes:**
- "Tiene campos vacíos, verifique.": captura **Nombre** y **Teléfono**.
- "Ya esta registrado ese proveedor": ya existe un proveedor con ese nombre.
- "Proveedor capturado correctamente.": el proveedor quedó dado de alta.

**Notas:** Los campos marcados con * son obligatorios. El teléfono y el fax se escriben con formato (XXX) XXX-XXXX y el C.P. solo acepta números (5 dígitos). Los datos se guardan en mayúsculas. Abajo aparece la lista de proveedores con **Clave**, **Nombre**, **Dirección** y **Teléfono**. El formulario para guardar solo aparece a los perfiles 1, 11 y 13; los demás solo consultan la lista.

## Catálogo de Unidades

**Para qué sirve:** Dar de alta y modificar las unidades a las que se surte.

**Quién la ve:** Perfiles 100, 1, 2, 3, 4, 5 y 6. Se abre en una ventana aparte.

**Cómo llegar:** Menú › Catálogos › Catálogo de Unidades.

**Paso a paso:**
1. Captura **Clave**, **Juris - Muni**, **Nombre**, **Tipo Unidad** y **Dirección** (y el **IdReporte** si lo tienes).
2. Da clic en **Guardar**. La unidad nueva queda activa y aparece en la tabla.
3. Para modificar una existente, da clic en el lápiz de su renglón. Se abre la ventana **Modificar Unidad**.
4. Cambia el **Nombre**, la **Dirección**, el **Estatus** (**Activo** o **Suspendido**) o la **Clues** y da clic en **Guardar**. Para no hacer cambios, da clic en **Cancelar**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Clave | Clave de la unidad | Sí (*) |
| Juris - Muni | Jurisdicción y municipio | Sí (*) |
| Nombre | Nombre de la unidad | Sí (*) |
| Tipo Unidad | Tipo de unidad | Sí (*) |
| Dirección | Domicilio | Sí (*) |
| IdReporte | Número entero con el identificador para reportes | No |
| Estatus | Solo al modificar: **Activo** o **Suspendido** | Sí, al modificar |
| Clues | Solo al modificar: clave CLUES de la unidad | No |

**Mensajes y errores frecuentes:**
- "Clave de la Unidad Existente": ya hay una unidad con esa clave; usa otra.
- "Seleccione Tipo Unidad": falta elegir el tipo de unidad.
- "Ingrese Datos En el campo Nombre" o "Ingrese Datos En el campo Dirección": al modificar, esos campos no pueden ir vacíos.

**Notas:** El formulario de alta y el lápiz para modificar los ven los perfiles 1 y 7 (el encabezado de la columna de modificar solo aparece al perfil 1). Los demás perfiles solo consultan la tabla (**Clave**, **Nombre**, **Sts**, **Clues**, **Dirección**, **Tipo Unidad**, **Dispensador**). La **Clave** de una unidad no se puede cambiar. En la ventana de modificar se muestra **Tipo Unidad**, pero ese dato no se guarda. Una unidad inactiva no se puede surtir en **Facturación Automática**.

## Alta de Proyecto

**Para qué sirve:** Dar de alta un proyecto nuevo.

**Quién la ve:** Perfiles 100, 1 y 5.

**Cómo llegar:** Menú › Catálogos › Alta de Proyecto.

**Paso a paso:**
1. Captura los datos del proyecto.
2. Revisa el recuadro **Lo que se va a crear**.
3. Da clic en **Dar de alta el proyecto**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Nombre del proyecto | Nombre (hasta 30 caracteres) | Sí |
| Abreviatura | De 2 a 6 letras o números, sin espacios ni guiones; se sugiere sola al escribir el nombre | Sí |
| Dirección | Domicilio (hasta 255 caracteres) | Sí |
| RFC | RFC de 12 caracteres (persona moral) o 13 (persona física) | Sí |
| Encabezado | Texto del encabezado de los documentos (hasta 150 caracteres) | Sí |
| Contrato | Número o nombre del contrato (hasta 255 caracteres) | No |
| Responsable sanitario de medicamento (2504) | Nombre; viene copiado del proyecto 1 | Sí |
| Responsable sanitario de dispositivos médicos (2505) | Nombre; viene copiado del proyecto 1 | Sí |

**Mensajes y errores frecuentes:**
- El botón **Dar de alta el proyecto** está desactivado hasta que los datos estén completos y correctos. En **Lo que se va a crear**, la lista **Falta corregir** te dice qué ajustar, por ejemplo: "El nombre es obligatorio y admite hasta 30 caracteres.", "La abreviatura lleva de 2 a 6 letras o números, sin espacios ni guiones." o "El RFC no tiene formato válido (12 caracteres persona moral, 13 persona física)."
- "Ya existe el proyecto…" o "La abreviatura … ya la usa el proyecto…": cambia el nombre o la abreviatura.
- "¿Dar de alta el proyecto …?": el sistema pide confirmación; acepta solo si los datos son correctos.
- "Se dio de alta el proyecto …": el alta terminó bien.
- "No se pudo dar de alta el proyecto; no se guardaron sus datos.": no se guardó nada; avisa a soporte.
- "La página caducó. Vuelva a abrirla.": entra de nuevo a la opción.
- "No tiene permiso para dar de alta proyectos.": tu usuario no tiene acceso a esta opción.

**Notas:** El recuadro **Lo que se va a crear** se actualiza mientras capturas y muestra la nomenclatura y las ubicaciones de ingreso y salida del proyecto. El alta no se puede deshacer desde esta pantalla. Abajo aparece la lista **Proyectos existentes**.
