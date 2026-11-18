# Almacén › Carga de Archivos

Todas las cargas se hacen con un archivo de Excel y siguen el mismo modelo: primero se **revisa** el archivo sin guardar nada y después se **confirma** con tu contraseña.

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

**Notas:** La revisión no guarda nada; solo **Confirmar carga** aplica los cambios. Si un renglón tiene error no se carga ninguno. Se permiten hasta 5,000 renglones. Un lote es el mismo cuando coinciden clave, lote, caducidad, proyecto y origen: conserva su folio y, si ya está en esa ubicación, se suma la cantidad. Si dejas vacío el código de barras, se usa el del lote existente o se genera uno nuevo. Si no capturas la fabricación se usa la fecha de la carga. Los movimientos quedan como alta de inventario a nombre de tu usuario. Caducidad y Fabricación pueden ser fecha de Excel o texto dd/mm/aaaa.

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

**Notas:** Si un renglón tiene error no se carga nada. Al confirmar, los requerimientos pendientes anteriores de cada unidad del archivo se cierran y queda el nuevo; si una unidad repite clave y fecha, se suman las cantidades. Al terminar, el sistema indica cuántas unidades y claves se cargaron y cuántos pendientes anteriores se cerraron. Se permiten hasta 5,000 renglones.

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

Las cinco columnas del Excel son obligatorias. El lote puede tener hasta 150 caracteres. La caducidad puede ser fecha de Excel o texto dd/mm/aaaa y la cantidad un entero mayor a 0.

**Mensajes y errores frecuentes:**
- "Renglón N: falta la unidad" o "la unidad … no existe": usa una unidad del catálogo.
- "falta la clave" o "la clave … no existe": usa una clave del catálogo.
- "falta el lote" o "el lote pasa de 150 caracteres": corrige el lote.
- "falta la caducidad", "la caducidad (…) no es una fecha dd/mm/aaaa": corrige la fecha.
- "la cantidad debe ser un entero mayor a 0": corrige la cantidad.
- "No existe la ubicación CADUCADOSFARMACIA …": la ubicación de destino no está dada de alta; avisa al departamento de sistemas.
- Los mensajes de archivo, contraseña y sesión son los mismos que en **Carga de Inventario**.

**Notas:** Los productos entran a la ubicación CADUCADOSFARMACIA con un documento por unidad. Si el lote ya estaba en esa ubicación, se suma la cantidad. Al terminar, el sistema muestra las piezas cargadas, los renglones nuevos y sumados, y los documentos generados. Si un renglón tiene error no se carga nada. Se permiten hasta 5,000 renglones.

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
- "La orden … ya tiene piezas recibidas: no se puede reemplazar por carga.": esa orden ya recibió mercancía y no se puede volver a cargar.
- "Su usuario (…) no está dado de alta en los usuarios de compras…": pide al administrador que te dé de alta como usuario de compras.
- Los mensajes de archivo, contraseña y sesión son los mismos que en **Carga de Inventario**.

**Notas:** Si una orden ya estaba cargada para ese proveedor y proyecto, se reemplaza (la revisión te avisa cuántas se reemplazarán). Si un renglón tiene error no se carga nada. Se permiten hasta 5,000 renglones. Compras tiene la misma opción (perfil 13).

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
- "la serie … se repite en el archivo", "la serie … ya tiene existencia con la clave …", "el código de barras … se repite en el archivo" o "ya existe": cambia la serie o el código de barras.
- "pasa de 10,000,000 de piezas": revisa el empaque.
- También pueden salir avisos de números inválidos (costo, tarimas, cajas, piezas) o de textos demasiado largos; corrige el dato indicado.
- Los mensajes de archivo, contraseña y sesión son los mismos que en **Carga de Inventario**.

**Notas:** Se permiten hasta 5,000 renglones. Si un renglón tiene error no se carga nada. La cantidad sale del empaque, como en la Entrada Manual: cajas = tarimas completas × cajas por tarima + cajas incompletas; piezas = cajas × piezas por caja + resto. Sin empaque entra 1 pieza. Con 1 pieza la columna No. de serie o lote es la serie (no puede repetirse ni tener existencia); con varias piezas es el lote. Si la dejas vacía se usa el código de barras, y si también va vacío se genera uno. Las fechas pueden ser fecha de Excel o texto dd/mm/aaaa. Si dejas vacías la orden de compra y la remisión se usan "EQUIPOS" y "S/R". Los renglones con la misma orden de compra y remisión comparten documento. Al confirmar, el sistema indica los equipos, las piezas y los documentos generados; el acuse y los marbetes están en **Reimpresión de compras** y en **Entrada de Equipos › Últimas entradas**. La plantilla trae dos ejemplos (una pieza con serie y un lote con empaque).
