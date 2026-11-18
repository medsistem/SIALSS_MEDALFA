# Almacén › Carga de Archivos

Todas las cargas se hacen con un archivo de Excel y siguen el mismo modelo: primero se **revisa** el archivo sin guardar nada y después se **confirma** con tu contraseña.

**Renglones repetidos:** ninguna carga duplica información. Si un renglón ya está cargado, o se repite más arriba en el mismo archivo, **no es un error**: la revisión lo omite y carga solo lo nuevo. Verás el aviso "Se omite 1 renglón repetido" (o "Se omiten N renglones repetidos") con la opción **Ver la lista**, que indica el renglón y el motivo. Al confirmar, el mensaje final dice cuántos renglones repetidos no se cargaron. Si todos los renglones están repetidos, verás un aviso "No hay … nuevos … No se guardó nada." y no hay nada que confirmar.

## Carga de Inventario

**Para qué sirve:** Cargar lotes y existencias desde un Excel.

**Quién la ve:** Perfiles 100 y 1.

**Cómo llegar:** Menú › Carga de Archivos › Carga de Inventario.

**Paso a paso:**
1. Da clic en **Descargar plantilla** si necesitas el formato.
2. Elige tu archivo en **Excel (.xlsx o .xls)**.
3. Da clic en **Revisar archivo**. Consulta la **Vista previa** y corrige tu Excel si hay observaciones.
4. Escribe tu contraseña en **2. Para confirmar, escribe tu contraseña**.
5. Da clic en **Confirmar carga**. Si ya no quieres seguir, da clic en **Cancelar carga**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Excel (.xlsx o .xls) | Archivo con lotes y existencias. Columnas de la plantilla: Clave, Lote, Caducidad, Cantidad, Ubicación, Proyecto, Origen, Proveedor, Marca, Código de barras, Fabricación y Tarima | Sí |
| Contraseña | Tu contraseña, para confirmar | Sí |

En el Excel son obligatorias Clave, Lote, Caducidad, Cantidad, Ubicación, Proyecto y Origen. Proveedor, Marca, Código de barras, Fabricación y Tarima son opcionales.

**Mensajes y errores frecuentes:**
- "Renglón N: falta la clave; …": el renglón tiene datos faltantes o incorrectos. Las observaciones pueden ser "la clave … no existe", "falta el lote", "la cantidad debe ser un entero mayor a 0", "la cantidad no es un número", "falta la ubicación", "la ubicación … no existe", "el proyecto … no existe", "el origen … no existe", "la caducidad no es una fecha (dd/mm/aaaa)". Corrige el Excel y vuelve a subirlo.
- "No se pudo leer el archivo como Excel (.xlsx o .xls)": el archivo está dañado o no es de Excel.
- "El archivo no tiene renglones con datos (la primera fila son los títulos).": captura al menos un renglón debajo de los títulos.
- "El archivo tiene N renglones; el máximo es 5000.": divide el archivo en partes.
- "Elige el archivo de Excel.": falta seleccionar el archivo antes de **Revisar archivo**.
- "Escribe tu contraseña para confirmar.": captura tu contraseña.
- "Contraseña incorrecta. Le quedan N intentos.": vuelve a escribirla. A la tercera falla se cancela la carga y debes subir el archivo otra vez.
- "No hay una revisión pendiente: vuelva a subir el archivo.": la revisión se perdió; revisa el archivo de nuevo.
- "Su sesión terminó o no es de Almacén. Vuelva a iniciar sesión.": entra de nuevo al sistema.
- "No se guardó nada: …": ocurrió un error al guardar; no se aplicó ningún cambio. Avisa al departamento de sistemas.

**Notas:** La revisión no guarda nada; solo **Confirmar carga** aplica los cambios. Si un renglón tiene error no se carga ninguno. Se permiten hasta 5,000 renglones. Un lote es el mismo cuando coinciden clave, lote, caducidad, proyecto y origen: conserva su folio. Si la clave, el lote y la caducidad ya tienen existencia en esa ubicación, o se repiten en el archivo, el renglón se omite con aviso y solo se cargan los lotes nuevos; la revisión indica "renglones nuevos". Si dejas vacío el código de barras, se usa el del lote existente o se genera uno nuevo. Si no capturas la fabricación se usa la fecha de la carga. Los movimientos quedan como alta de inventario a nombre de tu usuario. Caducidad y Fabricación pueden ser fecha de Excel o texto dd/mm/aaaa.

## Carga de Requerimiento

**Para qué sirve:** Cargar requerimientos de unidades desde un Excel.

**Quién la ve:** Perfiles 100, 1, 2, 3 y 5.

**Cómo llegar:** Menú › Carga de Archivos › Carga de Requerimiento.

**Paso a paso:** Igual que en **Carga de Inventario**: da clic en **Descargar plantilla**, llénala, elige el archivo, da clic en **Revisar archivo**, escribe tu contraseña y da clic en **Confirmar carga**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Excel (.xlsx o .xls) | Archivo con las columnas Unidad, Clave, Fecha de entrega y Cantidad, un renglón por unidad, clave y fecha | Sí |
| Contraseña | Tu contraseña, para confirmar | Sí |

Las cuatro columnas del Excel son obligatorias. Unidad es la clave de la unidad (la hoja de catálogos de la plantilla trae la lista con su nivel). La fecha puede ser fecha de Excel o texto dd/mm/aaaa. La cantidad son piezas, un entero mayor a 0.

**Mensajes y errores frecuentes:**
- "Renglón N: falta la unidad", "la unidad … no existe", "la unidad … está suspendida": usa una unidad activa del catálogo.
- "la clave … no existe", "la clave … no está activa": usa una clave activa del catálogo.
- "la clave … no está autorizada para el nivel 17 de la unidad" (o nivel 18): esa clave no corresponde al nivel de la unidad.
- "falta la fecha de entrega" o "la fecha de entrega (…) no es una fecha dd/mm/aaaa": corrige la fecha.
- "la cantidad debe ser un entero mayor a 0" o "la cantidad (…) no es un número": corrige la cantidad.
- Los mensajes de archivo, contraseña y sesión son los mismos que en **Carga de Inventario**.

**Notas:** Si un renglón tiene error no se carga nada. Los requerimientos pendientes se conservan: un renglón con la misma unidad, clave y fecha de entrega que uno pendiente, o repetido en el archivo, se omite con aviso y solo se agregan los nuevos. Al terminar, el sistema indica cuántos renglones se cargaron y cuántos repetidos no se cargaron. Se permiten hasta 5,000 renglones.

## Carga de Caducos

**Para qué sirve:** Cargar desde un Excel los productos caducos.

**Quién la ve:** Perfiles 100, 1, 2, 3 y 5.

**Cómo llegar:** Menú › Carga de Archivos › Carga de Caducos.

**Paso a paso:** Igual que en **Carga de Inventario**: descarga la plantilla, llénala, revisa el archivo, escribe tu contraseña y confirma.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Excel (.xlsx o .xls) | Archivo con las columnas Unidad, Clave, Lote, Caducidad y Cantidad | Sí |
| Contraseña | Tu contraseña, para confirmar | Sí |

Las cinco columnas del Excel son obligatorias. El lote puede tener hasta 30 caracteres. La caducidad puede ser fecha de Excel o texto dd/mm/aaaa y la cantidad un entero mayor a 0.

**Mensajes y errores frecuentes:**
- "Renglón N: falta la unidad" o "la unidad … no existe": usa una unidad del catálogo.
- "falta la clave" o "la clave … no existe": usa una clave del catálogo.
- "falta el lote" o "el lote pasa de 30 caracteres": corrige el lote.
- "falta la caducidad", "la caducidad (…) no es una fecha dd/mm/aaaa": corrige la fecha.
- "la cantidad debe ser un entero mayor a 0": corrige la cantidad.
- "No existe la ubicación CADUCADOSFARMACIA …": la ubicación de destino no está dada de alta; avisa al departamento de sistemas.
- Los mensajes de archivo, contraseña y sesión son los mismos que en **Carga de Inventario**.

**Notas:** Los productos entran a la ubicación CADUCADOSFARMACIA con un documento por unidad. Un renglón con la misma unidad, clave, lote y caducidad que ya se cargó a CADUCADOSFARMACIA, o repetido en el archivo, se omite con aviso: solo entran los caducos nuevos. Al terminar, el sistema muestra las piezas cargadas, los renglones nuevos y sumados, y los documentos generados. Si un renglón tiene error no se carga nada. Se permiten hasta 5,000 renglones.

## Carga de OC

**Para qué sirve:** Cargar órdenes de compra desde un Excel.

**Quién la ve:** Perfiles 100, 1, 2, 3 y 5.

**Cómo llegar:** Menú › Carga de Archivos › Carga de OC.

**Paso a paso:** Igual que en **Carga de Inventario**: descarga la plantilla, llénala, revisa el archivo, escribe tu contraseña y confirma.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Excel (.xlsx o .xls) | Archivo con las columnas Orden de compra, Proveedor, Nombre del proveedor, Clave, Cantidad, Proyecto y Fuente de financiamiento | Sí |
| Contraseña | Tu contraseña, para confirmar | Sí |

En el Excel son obligatorias Orden de compra, Proveedor, Clave, Cantidad y Proyecto. El nombre del proveedor es solo de referencia y la fuente de financiamiento es opcional (si va vacía se guarda "Pendiente"). Proveedor y Proyecto llevan el número que aparece en la hoja de catálogos de la plantilla.

**Mensajes y errores frecuentes:**
- "Renglón N: falta la orden de compra" o "la orden de compra pasa de 50 caracteres": corrige el número de orden.
- "falta el proveedor" o "el proveedor … no existe": usa un proveedor del catálogo.
- "falta la clave", "la clave … no existe", "la clave … está suspendida": usa una clave activa.
- "falta el proyecto" o "el proyecto … no existe": usa un proyecto del catálogo.
- "la fuente de financiamiento pasa de 50 caracteres": acorta el texto.
- "Su usuario (…) no está dado de alta en los usuarios de compras…": pide al administrador que te dé de alta como usuario de compras.
- Los mensajes de archivo, contraseña y sesión son los mismos que en **Carga de Inventario**.

**Notas:** Las órdenes ya no se reemplazan: si una orden ya está cargada, sus claves se conservan. Un renglón con la misma orden y clave que ya está cargada, o repetido en el archivo, se omite con aviso y solo se agregan las claves nuevas; la revisión indica "claves nuevas". Si un renglón tiene error no se carga nada. Se permiten hasta 5,000 renglones. Compras tiene la misma opción (perfil 13).

## Carga de Equipos

**Para qué sirve:** Registrar desde un Excel la entrada de muchos equipos a la vez, igual que en **Entrada de Equipos**.

**Quién la ve:** Perfiles 100, 1, 2, 3 y 5.

**Cómo llegar:** Menú › Carga de Archivos › Carga de Equipos.

**Paso a paso:** Igual que en **Carga de Inventario**: da clic en **Descargar plantilla**, llénala (un renglón por entrada), elige el archivo, da clic en **Revisar archivo**, escribe tu contraseña y da clic en **Confirmar carga**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Excel (.xlsx o .xls) | Archivo con las columnas de la plantilla (abajo) | Sí |
| Contraseña | Tu contraseña, para confirmar | Sí |

Columnas del Excel: Clave, Marca, Proyecto, Origen, Proveedor, Ubicación, No. de serie o lote, Código de barras, Modelo, Categoría, Condición, Costo, Fecha de compra, Garantía hasta, Accesorios, Notas, No. de tarima, Tarimas completas, Cajas por tarima, Piezas por caja, Tarimas incompletas, Cajas incompletas, Resto, Orden de compra y Remisión o factura.

Las primeras seis (Clave, Marca, Proyecto, Origen, Proveedor y Ubicación) son obligatorias; las demás son opcionales. Marca y Modelo se capturan con el número o el nombre exacto de la hoja de catálogos (no se dan de alta desde el archivo). Proyecto, Origen y Proveedor llevan el número de esa hoja. La Ubicación debe ser una de ingreso, general o del mismo proyecto del renglón.

**Mensajes y errores frecuentes:**
- "Renglón N: falta la clave", "la clave … no existe", "falta la marca", "la marca … no existe (hoja Catálogos)", "falta el proyecto", "el proyecto … no existe", "falta el origen", "falta el proveedor": corrige el dato con los catálogos de la plantilla.
- "la ubicación … no es de ingreso" o "la ubicación … es del proyecto …": usa una ubicación de ingreso general o del proyecto del renglón.
- "el modelo … no existe (hoja Catálogos)" o "la condición … no es …": usa un valor de la hoja de catálogos.
- "la fecha de compra no puede ser futura" o "la garantía termina antes de la compra": corrige las fechas.
- "faltan las cajas por tarima", "faltan las piezas por caja", "faltan las cajas o el resto de las tarimas incompletas": completa el empaque del renglón.
- "la serie … ya tiene existencia con la clave … (no es un equipo de esta carga)" o "el código de barras … ya es de otro lote": cambia la serie o el código de barras.
- "No hay equipos nuevos: los N renglones ya están cargados o se repiten. No se guardó nada.": todos los renglones se omitieron; no hay nada que cargar.
- "Se omiten N renglones repetidos (ya cargados o repetidos en el archivo)": aviso informativo; da clic en **Ver la lista** para ver el renglón, la clave, la serie o código de barras y el motivo. Los demás sí se cargan.
- "el lote o número de serie pasa de 30 caracteres" o "Un dato es más largo de lo que admite el sistema … (hasta 30 caracteres)": acorta la serie o el lote.
- Si falla el guardado, el mensaje ya no muestra textos técnicos: dice qué dato falló, indica que no se guardó ningún cambio y, si el problema sigue, una referencia para dar a sistemas.
- "pasa de 10,000,000 de piezas": revisa el empaque.
- También pueden salir avisos de números inválidos (costo, tarimas, cajas, piezas) o de textos demasiado largos; corrige el dato indicado.
- Los mensajes de archivo, contraseña y sesión son los mismos que en **Carga de Inventario**.

**Notas:** Se permiten hasta 5,000 renglones. Si un renglón tiene error no se carga nada. La cantidad sale del empaque, como en la Entrada Manual: cajas = tarimas completas × cajas por tarima + cajas incompletas; piezas = cajas × piezas por caja + resto. Sin empaque entra 1 pieza. Con 1 pieza la columna No. de serie o lote es la serie; con varias piezas es el lote (hasta 30 caracteres). Los equipos que ya están cargados (misma clave y misma serie, lote o código de barras) y los renglones repetidos dentro del archivo se omiten con aviso: solo se suben los nuevos, y la revisión indica "equipos nuevos". Al confirmar, el mensaje también dice cuántos renglones repetidos no se volvieron a cargar. Si la dejas vacía se usa el código de barras, y si también va vacío se genera uno. Las fechas pueden ser fecha de Excel o texto dd/mm/aaaa. Si dejas vacías la orden de compra y la remisión se usan "EQUIPOS" y "S/R". Los renglones con la misma orden de compra y remisión comparten documento. Al confirmar, el sistema indica los equipos, las piezas y los documentos generados; el acuse y los marbetes están en **Reimpresión de compras** y en **Entrada de Equipos › Últimas entradas**. La plantilla trae dos ejemplos (una pieza con serie y un lote con empaque).

## Carga de Marcas

**Para qué sirve:** Dar de alta muchas marcas a la vez desde un Excel.

**Quién la ve:** Perfiles 100, 1 y 5.

**Cómo llegar:** Menú › Carga de Archivos › Carga de Marcas.

**Paso a paso:** Igual que en **Carga de Inventario**: da clic en **Descargar plantilla**, llénala, elige el archivo, da clic en **Revisar archivo**, escribe tu contraseña y da clic en **Confirmar carga**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Excel (.xlsx o .xls) | Archivo con la columna Marca, un renglón por marca (hasta 80 caracteres) | Sí |
| Contraseña | Tu contraseña, para confirmar | Sí |

**Mensajes y errores frecuentes:**
- "Renglón N: falta la marca" o "la marca pasa de 80 caracteres": corrige el renglón.
- "Se omite N renglón repetido": la marca ya existe, se parece a una registrada ("ya existe la marca …" o "se parece a la marca …") o se repite en el archivo. No es error.
- "No hay marcas nuevas: … No se guardó nada.": todas las marcas del archivo ya existían o se repetían.
- Los mensajes de archivo, contraseña y sesión son los mismos que en **Carga de Inventario**.

**Notas:** Las marcas se guardan en mayúsculas. Dos nombres se consideran la misma marca si, sin importar acentos, mayúsculas, signos o espacios de más, quedan iguales (por ejemplo "BAXTER" y "BAX TER"), o si difieren en una letra. La plantilla trae en otra hoja las marcas ya registradas. Se permiten hasta 5,000 renglones y, si un renglón tiene error, no se carga nada.

## Carga de Proveedores

**Para qué sirve:** Dar de alta muchos proveedores a la vez desde un Excel.

**Quién la ve:** Perfiles 100, 1 y 5.

**Cómo llegar:** Menú › Carga de Archivos › Carga de Proveedores.

**Paso a paso:** Igual que en **Carga de Inventario**: da clic en **Descargar plantilla**, llénala, elige el archivo, da clic en **Revisar archivo**, escribe tu contraseña y da clic en **Confirmar carga**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Excel (.xlsx o .xls) | Archivo con las columnas de la plantilla (abajo) | Sí |
| Contraseña | Tu contraseña, para confirmar | Sí |

Columnas del Excel: Nombre o razón social (hasta 150 caracteres, obligatoria), RFC (hasta 20), Dirección (150), Colonia (150), Código postal (10), Teléfono (20), Fax (20), Correo (80), Población (80) y Clave SAP (20). Solo el nombre es obligatorio.

**Mensajes y errores frecuentes:**
- "Renglón N: falta el nombre" o "el campo … pasa de N caracteres": corrige el renglón.
- "el RFC … no tiene el formato de 12 o 13 caracteres": corrige el RFC.
- "el correo … no es válido": escribe un correo con formato nombre@dominio.mx.
- "Se omite N renglón repetido": el nombre ya existe o se parece a un proveedor registrado, el RFC ya es de otro proveedor, o se repite en el archivo. No es error.
- "No hay proveedores nuevos: … No se guardó nada.": todos los renglones ya existían o se repetían.
- Los mensajes de archivo, contraseña y sesión son los mismos que en **Carga de Inventario**.

**Notas:** El nombre se guarda en mayúsculas y los datos que dejes vacíos se guardan con "-". Al comparar nombres no cuentan acentos, signos, espacios ni la razón social (S.A. de C.V., S. de R.L., etc.). Se permiten hasta 5,000 renglones y, si un renglón tiene error, no se carga nada.

## Carga de Unidades

**Para qué sirve:** Dar de alta muchas unidades de atención a la vez desde un Excel, con las mismas reglas que el alta de unidades.

**Quién la ve:** Perfiles 100, 1 y 5.

**Cómo llegar:** Menú › Carga de Archivos › Carga de Unidades.

**Paso a paso:** Igual que en **Carga de Inventario**: da clic en **Descargar plantilla**, llénala, elige el archivo, da clic en **Revisar archivo**, escribe tu contraseña y da clic en **Confirmar carga**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Excel (.xlsx o .xls) | Archivo con las columnas de la plantilla (abajo) | Sí |
| Contraseña | Tu contraseña, para confirmar | Sí |

Columnas del Excel: Clave de la unidad (hasta 10 caracteres), Nombre (150), Tipo de unidad, Municipio, Proyecto, Dirección (250), CLUES (100), Nivel de unidad y Ruta (20). Son obligatorias Clave, Nombre, Tipo de unidad, Municipio y Proyecto; Tipo de unidad, Municipio y Proyecto llevan el número de la hoja de catálogos de la plantilla. Si dejas vacío Nivel de unidad se usa el del tipo de unidad (1 a 99 si lo capturas); si dejas vacía la Ruta se usa R1001.

**Mensajes y errores frecuentes:**
- "Renglón N: falta la clave", "falta el nombre", "falta el tipo de unidad", "falta el municipio", "falta el proyecto": completa el dato.
- "el tipo de unidad … no existe (hoja Catálogos)", "el municipio … no existe (hoja Catálogos)", "el proyecto … no existe": usa un número de la hoja de catálogos.
- "el nivel de unidad debe ser de 1 a 99" o "el nivel de unidad (…) no es un número": corrige el nivel.
- "Se omite N renglón repetido": la clave o la CLUES ya son de otra unidad, el nombre ya existe o se parece a una unidad registrada, o se repite en el archivo. No es error.
- "No hay unidades nuevas: … No se guardó nada.": todos los renglones ya existían o se repetían.
- Los mensajes de archivo, contraseña y sesión son los mismos que en **Carga de Inventario**.

**Notas:** Las unidades se dan de alta activas; la jurisdicción sale del municipio. La plantilla trae las listas de tipos de unidad, municipios, proyectos y las unidades ya registradas. Se permiten hasta 5,000 renglones y, si un renglón tiene error, no se carga nada.

## Carga de Productos

**Para qué sirve:** Dar de alta muchas claves de insumos (medicamento, material de curación y demás) a la vez desde un Excel, como en **Alta de Claves**.

**Quién la ve:** Perfiles 100, 1 y 5.

**Cómo llegar:** Menú › Carga de Archivos › Carga de Productos.

**Paso a paso:** Igual que en **Carga de Inventario**: da clic en **Descargar plantilla**, llénala (un renglón por clave), elige el archivo, da clic en **Revisar archivo**, escribe tu contraseña y da clic en **Confirmar carga**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Excel (.xlsx o .xls) | Archivo con las columnas de la plantilla (abajo) | Sí |
| Contraseña | Tu contraseña, para confirmar | Sí |

Columnas del Excel: Clave (hasta 20 caracteres), Descripción, Tipo de insumo, Origen, Precio, Proyectos, Clave SS, Presentación, Origen NIM, Grupo terapéutico, Cantidad máxima a recibir, Catálogo (año), Niveles de unidad y Estatus.

Son obligatorias Clave, Descripción, Tipo de insumo, Origen, Precio y Proyectos. Tipo de insumo, Origen y Proyectos llevan el número de la hoja de catálogos; en Proyectos puedes poner varios separados por coma (1,2). Las demás son opcionales:
- **Clave SS** vacía: se usa la misma clave.
- **Origen NIM**: LP, CC o CC/LP.
- **Grupo terapéutico**: el nombre de la hoja de catálogos; vacío = POR DEFINIR.
- **Cantidad máxima a recibir** vacía: 100,000.
- **Catálogo (año)** vacío: el año actual.
- **Niveles de unidad** vacío: todos; o los números separados por coma.
- **Estatus**: A (activo) o S (suspendido); vacío = A.

**Mensajes y errores frecuentes:**
- "Renglón N: falta la clave", "falta la descripción", "falta el tipo de insumo", "falta el origen", "falta el precio", "faltan los proyectos": completa el dato.
- "el tipo de insumo … no existe (hoja Catálogos)", "el origen … no existe (hoja Catálogos)", "el grupo terapéutico … no existe (hoja Catálogos)": usa un valor de la hoja de catálogos.
- "el precio no puede ser negativo", "el precio (…) no es un número", "el precio pasa de 11 caracteres": corrige el precio.
- "el origen NIM … no es LP, CC ni CC/LP", "el estatus debe ser A (activo) o S (suspendido)", "el catálogo (año) … no es válido", "el nivel de unidad … no existe": corrige el dato.
- "el proyecto … no existe o no tiene columna": el proyecto no puede usarse para productos; avisa al departamento de sistemas.
- "Se omite N renglón repetido": la clave ya existe (también si solo cambian los ceros a la izquierda, 104 y 0104), la descripción ya existe o se parece a la de otro producto, o se repite en el archivo. No es error.
- "No hay productos nuevos: … No se guardó nada.": todos los renglones ya existían o se repetían.
- Los mensajes de archivo, contraseña y sesión son los mismos que en **Carga de Inventario**.

**Notas:** Dos descripciones se consideran parecidas solo si tienen los mismos números: "PARACETAMOL 500 MG" y "PARACETAMOL 750 MG" son productos distintos. La plantilla trae un ejemplo y las listas de tipo de insumo, origen, proyectos y grupo terapéutico. Se permiten hasta 5,000 renglones y, si un renglón tiene error, no se carga nada.
