# Almacén › PDF

Opciones para reimprimir y administrar documentos: remisiones, movimientos, devoluciones y marbetes.

## Administrar Devoluciones

**Para qué sirve:** Consultar las devoluciones registradas y reimprimir sus documentos.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6.

**Cómo llegar:** Menú › PDF › Administrar Devoluciones.

**Paso a paso:**
1. Captura el rango de **Fechas**.
2. Da clic en **MOSTRAR**.
3. Revisa la tabla: **Fecha Dev.**, **Doc. Dev.**, **Clave Cli.**, **Nombre**, **Fec. Ent.** y **Doc. Ref.**
4. Da clic en el botón de impresora de la columna **Devolución** para abrir el documento de la devolución, o en el de la columna **Marbete** para abrir su marbete. Ambos se abren en una pestaña nueva.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Fechas | Fecha inicial y final de las devoluciones | Sí |
| Folios | Folio inicial y final | No; la búsqueda se hace solo por fechas |

**Mensajes y errores frecuentes:** La pantalla no muestra avisos propios. Si la tabla queda vacía, no hay devoluciones en esas fechas.

**Notas:** Al abrir la opción la tabla aparece vacía hasta que das clic en **MOSTRAR**. La tabla permite buscar y ordenar.

## Administrar Movimientos

**Para qué sirve:** Consultar y reimprimir los movimientos al inventario.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6.

**Cómo llegar:** Menú › PDF › Administrar Movimientos.

**Paso a paso:**
1. Captura el rango de **Folios** o de **Fechas**.
2. Da clic en **MOSTRAR**.
3. Revisa la tabla: **No. Folio**, **Punto de Entrega** (el concepto del movimiento), **Fecha de Entrega** y **Folio** (proyecto).
4. Da clic en el botón de impresora del renglón para obtener el documento del movimiento, o en el botón de la columna **Exportar Ubicación** para el detalle por ubicación.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Folios | Folio inicial y final | No |
| Fechas | Fecha inicial y final | No |

**Mensajes y errores frecuentes:** La pantalla no muestra avisos propios. Si la tabla queda vacía, no hay movimientos con esos datos.

**Notas:** Si das clic en **MOSTRAR** sin capturar nada, se listan los movimientos de los últimos 7 días. No aparecen los movimientos de ciertos conceptos de captura inicial (por ejemplo, alta de inventario). Si capturas folios y fechas, se usan ambos criterios.

## Administrar Remisiones

**Para qué sirve:** Consultar remisiones por folio, fecha y proyecto, reimprimirlas y exportarlas.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6. El perfil 17 ve una versión propia de esta pantalla (Administrar Remisiones de clientes), con el mismo nombre en el menú.

**Cómo llegar:** Menú › PDF › Administrar Remisiones.

**Paso a paso:**
1. Captura los **Folios**, las **Fechas** o elige el **Proyecto**.
2. Da clic en **MOSTRAR**.
3. Revisa la tabla: **No. Folio**, **Punto de Entrega**, **Proyecto Unidad**, **Estatus**, **Factura**, **Fec Ent** y **Tipo**. En los folios activos, usa el botón de la columna **Folio** para ver o reimprimir la remisión y el de la columna **Excel** para obtenerla en Excel.
4. Da clic en **Exportar Abasto** y confirma para descargar el abasto de lo mostrado (este botón no se muestra a todos los perfiles).

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Folios | Folio inicial y final | No |
| Fechas | Fecha inicial y final | No |
| Proyecto | Proyecto de las remisiones | No |

**Mensajes y errores frecuentes:**
- "Seguro que desea exportar los Abastos?": confirma para continuar.
- "Seguro de Reenviar este concentrado?": confirma solo si de verdad necesitas reenviarlo.

**Notas:** Al abrir la opción se muestran las remisiones sin filtros; para acotar captura folios o fechas, o elige el proyecto, y da clic en **MOSTRAR**. Los folios cancelados no se pueden reimprimir. Algunas columnas (por ejemplo la de cancelación) solo aparecen a ciertos perfiles.

## Costos Remisiones

**Para qué sirve:** Consultar remisiones con costo y exportarlas.

**Quién la ve:** Perfiles 100 y 1.

**Cómo llegar:** Menú › PDF › Costos Remisiones.

**Paso a paso:**
1. Captura **Folios**, **Fechas** o **Proyecto**.
2. Da clic en **MOSTRAR**.
3. Revisa la tabla de remisiones con costo.
4. Da clic en **Exportar Abasto** o en **Exportar por Dispensador** y confirma.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Folios | Folio inicial y final | No |
| Fechas | Fecha inicial y final | No |
| Proyecto | Proyecto | No |

**Mensajes y errores frecuentes:**
- "Seguro que desea exportar los Abastos?" / "Seguro que desea exportar los Folios por Dispensador?": confirma para continuar.

**Notas:** Ninguna.

## Marbete Manual

**Para qué sirve:** Generar un marbete a mano.

**Quién la ve:** Perfiles 100 y 1.

**Cómo llegar:** Menú › PDF › Marbete Manual.

**Paso a paso:**
1. En **Criterios de Búsqueda**, elige el **Proyecto**.
2. Elige el **Producto** (la clave).
3. Elige el **Lote**.
4. Da clic en **Generar Marbetes**. El marbete se abre en una pestaña nueva.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Proyecto | Proyecto del insumo | Sí |
| Producto | Clave del insumo | Sí |
| Lote | Lote del insumo | Sí |

**Mensajes y errores frecuentes:**
- "Selecciona proyecto, producto y lote antes de generar el marbete.": elige los tres datos.
- Antes de generar el marbete puede aparecer un aviso con el proyecto, la clave y el lote elegidos; ciérralo para continuar.

**Notas:** La lista de **Lote** muestra todos los lotes, no solo los de la clave elegida; asegúrate de elegir el lote que corresponde al producto.

## Múltiples Remisiones/Recalendarizar

**Para qué sirve:** Imprimir varias remisiones a la vez y cambiar su fecha de entrega.

**Quién la ve:** Perfiles 100, 1, 2, 3 y 5.

**Cómo llegar:** Menú › PDF › Múltiples Remisiones/Recalendarizar.

**Paso a paso:**
1. Elige **Unidad** y **Proyecto**, o captura **Folios** y **Fechas**.
2. Da clic en **MOSTRAR**.
3. Marca las remisiones.
4. Para imprimir, elige **Seleccione Impresora**, captura **No. Copias** y da clic en **Impresiones Múltiples**, **Impresiones Surtido** o **Imprime Relación**.
5. Para cambiar la fecha, da clic en **Recalendarizar**, captura la nueva fecha y confirma.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Unidad | Unidad | No |
| Proyecto | Proyecto | No |
| Folios | Folio inicial y final | No |
| Fechas | Fecha inicial y final | No |
| Seleccione Impresora | Impresora | Para imprimir |
| No. Copias | Número de copias (de 1 a 4) | Para imprimir |
| Tipo de Remisión | Tipo de insumo de las remisiones | Para imprimir |
| Seleccionar fecha (ventana de **Recalendarizar**) | Nueva fecha de entrega | Para recalendarizar |

**Mensajes y errores frecuentes:**
- "FOLIO NO EXISTE O ESTA CANCELADO": no hay remisiones activas con esos filtros; cámbialos.
- "Seguro que desea cambiar la fecha de entrega?": confirma para guardar la nueva fecha.
- "Falta la fecha": captura la nueva fecha en la ventana de **Recalendarizar**.

**Notas:** El botón **Surtido MDF** aparece desactivado. También puedes dar clic en **Exportar** para obtener la lista mostrada. En la ventana de recalendarización da clic en **Recalendarizar** para aplicar o en **Cerrar** para salir sin cambios.

## Reimpresión de Remisiones

**Para qué sirve:** Volver a imprimir remisiones y corregir sus datos.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6 (se abre en una ventana aparte). El perfil 17 ve una versión propia, sin ventana aparte.

**Cómo llegar:** Menú › PDF › Reimpresión de Remisiones.

**Paso a paso:**
1. Captura el **Rango de Fecha** (inicial y final).
2. Da clic en **Por Fecha**. Se listan las compras ingresadas en ese periodo con **No. Folio**, **Folio Remisión**, **Orden de Compra**, **Fecha Ingreso**, **Usuario** y **Proveedor**.
3. En el renglón que necesitas, da clic en **ACUSE MDF** para reimprimir el acuse, en **TARIMAS C** o **TARIMAS A** para los marbetes de tarimas completas o adicionales, o en **CAJAS** para los marbetes de cajas. Cada documento se abre en una pestaña nueva.
4. Da clic en **Ver Compra** para consultar el detalle de la compra.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Rango de Fecha | Fecha inicial y final del ingreso | Sí |

**Mensajes y errores frecuentes:** La pantalla no muestra avisos propios. Si la tabla queda vacía, no hay compras ingresadas en ese periodo.

**Notas:** Esta opción reimprime documentos de las compras (entradas), no de las remisiones de salida; para reimprimir una remisión usa **Administrar Remisiones**. La ventana de corrección de la remisión de la compra existe en la pantalla, pero no tiene un botón que la abra.

## Etiquetas de Ubicación

**Para qué sirve:** Imprimir en PDF las etiquetas de las ubicaciones. Las de poste miden 5 × 4 cm y salen en 3 columnas por hoja.

**Quién la ve:** Perfiles 100, 1, 2, 3 y 5.

**Cómo llegar:** Menú › PDF › Etiquetas de Ubicación.

**Paso a paso:**
1. Elige la **Hoja para etiquetas de poste**.
2. Elige la **Sección** o usa **Buscar clave o ubicación** y da clic en **Buscar**.
3. Marca las ubicaciones, o usa **Seleccionar todas** / **Quitar selección**.
4. Da clic en **Generar PDF**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Hoja para etiquetas de poste | Tipo de hoja | Sí |
| Sección | Sección del almacén | No |
| Buscar clave o ubicación | Texto a buscar | No |

**Mensajes y errores frecuentes:** **Generar PDF** está desactivado hasta que marcas al menos una ubicación.

**Notas:** Las etiquetas de poste se acomodan en 3 columnas, con espacio entre ellas y guías de corte. La lista se llena de arriba abajo en la primera columna, luego la segunda y la tercera, así que se lee en orden por columna.

## Opciones apagadas

Estas opciones aparecen en el catálogo del menú pero hoy no están disponibles:

- Marbetes Surtido (APAGADO)
- Marbetes Surtido Juris (APAGADO)
