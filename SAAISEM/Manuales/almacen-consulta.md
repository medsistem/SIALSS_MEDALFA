# Almacén › Consulta

Opciones para consultar requerimientos, existencias, movimientos y órdenes de compra.

## Concentrado Requerido por día

**Para qué sirve:** Ver el concentrado de lo requerido por día (rurales).

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6.

**Cómo llegar:** Menú › Consulta › Concentrado Requerido por día.

**Paso a paso:**
1. Elige la fecha en **Fechas**.
2. Da clic en **MOSTRAR**.
3. Revisa la lista de **Clave**, **Descripción** y **Cantidad Req.**; al final aparece el **Total**.
4. Si quieres llevarte la información, da clic en **Exportar**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Fechas | Fecha de los requerimientos que quieres sumar (es una sola fecha, no un rango) | Sí |

**Mensajes y errores frecuentes:** Esta pantalla no muestra mensajes. Si la lista sale vacía, no hay requerimientos pendientes en esa fecha; revisa que sea la correcta.

**Notas:** Suma solo los requerimientos que siguen pendientes de surtir. El botón **Exportar** aparece únicamente cuando hay información para esa fecha.

## Concentrados por Ruta

**Para qué sirve:** Consultar el concentrado de surtido por ruta.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6.

**Cómo llegar:** Menú › Consulta › Concentrados por Ruta.

**Paso a paso:**
1. Abre la opción. Se muestra una tabla con cada fecha de entrega de los próximos 7 días que tenga remisiones activas.
2. Para imprimir el concentrado global de una fecha, da clic en el botón de la impresora en la columna **Imprimir Global**. Se abre en una pestaña nueva.
3. Para volver a mandar los requerimientos de las remisiones rurales y de CSU, da clic en el botón de subir en la columna **Enviar Req. Rurales RURAL, CSU** y confirma.
4. Para llevarte el concentrado de esas unidades, da clic en el botón de exportar en la columna **Exportar Global RURAL, CSU**.

**Campos:** Esta pantalla no tiene campos para capturar; solo botones por cada fecha.

**Mensajes y errores frecuentes:**
- "Seguro de Reenviar estas remisiones RURALES y CSU?": confirma solo si de verdad necesitas volver a enviar los requerimientos de esa fecha.

**Notas:** Los botones de enviar y exportar solo aparecen en las fechas que tienen remisiones de unidades rurales o CSU. Si no aparece ninguna fecha, no hay remisiones activas para los próximos 7 días.

## Concepto Movimiento

**Para qué sirve:** Dar de alta los conceptos que se usan en los movimientos al inventario.

**Quién la ve:** Perfiles 100, 1, 2 y 5.

**Cómo llegar:** Menú › Consulta › Concepto Movimiento (se abre en una ventana aparte).

**Paso a paso:**
1. Captura la **CLAVE** del concepto.
2. Captura la **Descripción**.
3. Da clic en **Guardar**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| CLAVE | Clave del concepto (hasta 60 caracteres) | Sí |
| Descripción | Nombre del concepto (hasta 40 caracteres) | Sí |

**Mensajes y errores frecuentes:**
- "TE HA FALTADO INTRODUCIR LOS SIGUIENTES DATOS…" con "El campo Clave no debe de estar vacío" o "El campo Descripcion no debe de estar vacío": captura los dos campos y vuelve a dar clic en **Guardar**.

**Notas:** Los conceptos se usan en **Captura Movimiento**. La tabla de abajo muestra solo los conceptos de uso común (**CLAVE** y **Descripción**), no todos los que existen. Si capturas una clave que ya existe, el sistema no la guarda y no muestra aviso; revisa que aparezca en el catálogo.

## Consulta Existencia en Cedis

**Para qué sirve:** Consultar la existencia de una clave en el Cedis.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6.

**Cómo llegar:** Menú › Consulta › Consulta Existencia en Cedis.

**Paso a paso:**
1. Captura la **Clave** o parte de la **Descripción** del insumo (si lo dejas vacío se consultan todos).
2. Deja la **Fecha** del día o elige otra: se calcula la existencia a esa fecha.
3. Elige el tipo de **Consulta**: **Clave** (una línea por insumo) o **Lote** (una línea por lote, con su caducidad).
4. Da clic en **MOSTRAR**. Al final se muestra la **Existencia** total.
5. Si quieres llevarte la información, da clic en **Exportar Todo** o en **Exportar Soluciones**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Clave | Clave del insumo | No |
| Descripción | Parte de la descripción del insumo | No |
| Fecha | Fecha a la que quieres la existencia; aparece la del día | No |
| Consulta | **Clave** o **Lote**; viene marcada **Clave** | No (ya viene marcada una opción) |

**Mensajes y errores frecuentes:** Esta pantalla no muestra mensajes. Si la tabla sale vacía, no hay existencia con esos datos.

**Notas:** Al consultar por **Lote** solo se listan los lotes con existencia mayor a cero y se muestran **Clave**, **Descripción**, **Lote**, **Caducidad** y **Cantidad**; por **Clave** se muestran **Clave**, **Descripción** y **Cantidad**. **Exportar Todo** aparece solo cuando hay resultados; **Exportar Soluciones** siempre está disponible. Las claves de ajuste no se incluyen.

## Consulta Movimiento

**Para qué sirve:** Consultar los movimientos al inventario por clave y concepto.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6.

**Cómo llegar:** Menú › Consulta › Consulta Movimiento.

**Paso a paso:**
1. Captura la **Clave** del insumo, si quieres ver uno solo.
2. Elige las dos fechas (inicial y final) si quieres limitar el periodo.
3. Si quieres, elige el **Proyecto** y el **Origen**.
4. En la lista de **Conceptos**, elige un concepto; se agrega al recuadro de conceptos. Repite para agregar más. Para empezar de nuevo, da clic en **Limpiar**.
5. Da clic en **MOSTRAR**.
6. Si quieres llevarte la información, da clic en **Exportar**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Clave | Clave del insumo | No |
| Fechas | Fecha inicial y fecha final del periodo; se usan solo si capturas las dos | No |
| Proyecto | Proyecto del lote (**-Seleccione Proyecto-** significa todos) | No |
| Origen | Origen del lote (**-Selec Origen-** significa todos) | No |
| Conceptos | Uno o varios conceptos de movimiento elegidos de la lista | No |

**Mensajes y errores frecuentes:** Esta pantalla no muestra mensajes. Si la tabla sale vacía, no hay movimientos con esos datos, o no capturaste ni la clave, ni las dos fechas, ni un concepto.

**Notas:** Debes dar al menos uno de estos datos: **Clave**, las dos fechas o un concepto; sin ninguno no se muestra nada. La tabla muestra **Proyecto**, **Fecha Mov.**, **No.Documento**, **Con/Mov**, **Des/Mov**, **Clave**, **Descripción**, **Lote**, **Caducidad**, **Cantidad**, **Origen**, **Proveedor** y **Marca**. El botón **Exportar** aparece solo cuando hay resultados.

## Inventario por mes Cedis

**Para qué sirve:** Consultar el inventario del Cedis mes por mes.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6.

**Cómo llegar:** Menú › Consulta › Inventario por mes Cedis.

**Paso a paso:**
1. Abre la opción. El sistema calcula el inventario y muestra una fila por cada año anterior y una por cada mes del año en curso.
2. Revisa las columnas **Mes**, **Inv. Inicio Mes**, **Entrada Por Inv. Inicial**, **Entrada Por Compra**, **Entrada Por Devolución**, **Entrada Por Otros Movimientos**, **Salida Por Facturación**, **Salida Por Otros Movimientos** e **Inv. Final Mes**.
3. Para llevarte todo el reporte, da clic en **Exportar Global**.
4. Para llevarte el detalle de un mes, da clic en el botón de la columna **Descargar** de ese renglón.

**Campos:** Esta pantalla no tiene campos para capturar.

**Mensajes y errores frecuentes:** Esta pantalla no muestra mensajes. Como calcula todo al abrirla, puede tardar unos segundos en mostrar la tabla; espera sin cerrarla.

**Notas:** Las cifras se actualizan cada vez que abres la pantalla. Las claves de ajuste no se incluyen.

## Nivel Abasto Diario

**Para qué sirve:** Ver los productos negados (requerido contra surtido) por tipo de unidad.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6.

**Cómo llegar:** Menú › Consulta › Nivel Abasto Diario.

**Paso a paso:**
1. Captura la fecha inicial y la fecha final en **Fechas**.
2. Da clic en **MOSTRAR**.
3. Revisa por cada tipo de unidad el **Nivel**, **No. Unidades**, **Requerido**, **Surtido**, **No. Sertido** (lo no surtido) y **Fill Ride** (porcentaje surtido). Al final aparece el **Total**.
4. Si quieres llevarte la información, da clic en **Exportar Nivel Abasto** o en **Exportar Reporte Abasto**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Fechas | Fecha inicial y fecha final de entrega | Sí |

**Mensajes y errores frecuentes:** Esta pantalla no muestra mensajes. Si la tabla sale vacía, falta una de las dos fechas o no hubo remisiones en el periodo.

**Notas:** Toma las remisiones activas del proyecto 1 según su fecha de entrega. Los botones de exportar aparecen solo cuando hay información.

## OC Entregadas

**Para qué sirve:** Consultar las órdenes de compra ya entregadas (cerradas).

**Quién la ve:** Perfiles 100, 1, 2, 3, 4, 5 y 6.

**Cómo llegar:** Menú › Consulta › OC Entregadas.

**Paso a paso:**
1. Captura la fecha inicial y la fecha final en **Fechas**.
2. Si lo necesitas, elige un **Proyecto**.
3. Da clic en **Buscar**.
4. Revisa la tabla con **Fecha**, **Claves**, **Ingresos**, **Orden de Compra**, **Solicitado**, **Faltante/sobrante**, **Status OC** y **Proveedor**.
5. Para llevarte la información, da clic en **Descargar**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Fechas | Fecha inicial y fecha final | Sí |
| Proyecto | Proyecto de las órdenes (**--Seleccione--** significa sin proyecto) | No |

**Mensajes y errores frecuentes:** Esta pantalla no muestra mensajes. Si la tabla sale vacía, no hay órdenes cerradas en esas fechas.

**Notas:** En pantalla la sección se titula **OC CERRADAS**. La tabla se arma con las fechas; el **Proyecto** se aplica en la descarga. **Descargar** usa las fechas y el proyecto de la última búsqueda, así que da clic primero en **Buscar**.

## Órdenes de Compras

**Para qué sirve:** Consultar órdenes de compra, ver su detalle, cerrarlas o reactivarlas y validar o eliminar remisiones.

**Quién la ve:** Perfiles 100, 1, 2, 3, 4, 5 y 6.

**Cómo llegar:** Menú › Consulta › Órdenes de Compras (se abre en una ventana aparte).

**Paso a paso:**
1. Elige el **Proveedor**, la **Fecha de Entrega a MEDALFA** o el **Usuario** que capturó la orden.
2. Da clic en **Buscar**. Abajo a la izquierda aparece la lista de **Órdenes de Compra**.
3. Da clic en la lupa de la orden que quieres ver. A la derecha se muestra su detalle: **Orden No.**, **Capturó**, **Proveedor**, **Fecha de Entrega** y **Hora de Entrega**, y la tabla de **Clave**, **Descripción** y **Cantidad**.
4. Para imprimir la orden, da clic en el botón de la impresora.
5. Si la orden está pendiente, da clic en **Validar** para validarla. Para quitarla, da clic en **Eliminar**.
6. Para volver a abrir una clave ya recibida, da clic en **Abrir** en su renglón y confirma. Si la clave ya está abierta, el botón dice **Abierta**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Proveedor | Proveedor de la orden | No (basta uno de los tres filtros) |
| Fecha de Entrega a MEDALFA | Fecha en formato dd/mm/aaaa, con calendario | No (basta uno de los tres filtros) |
| Usuario | Quien capturó la orden | No (basta uno de los tres filtros) |

**Mensajes y errores frecuentes:**
- "Se validó la orden … corrercetamente": la orden quedó validada.
- "Se eliminó la orden … corrercetamente": la orden se eliminó.
- "¿Desea realizar el Reabir el insumo?": confirma solo si estás seguro de reabrir esa clave.
- "Clave reabierta": la clave quedó abierta de nuevo.
- "Ocurrio un irreor intente de nuevo": no se pudo reabrir la clave; inténtalo otra vez y, si sigue, avisa a soporte.

**Notas:** Si eliges varios filtros se usa solo uno, en este orden: **Proveedor**, **Fecha de Entrega a MEDALFA** y **Usuario**. **Validar** y **Eliminar** no piden confirmación, así que revisa la orden antes de dar clic. El botón **Abrir** de cada clave solo lo ve el perfil 11 y algunos usuarios autorizados. Si la orden fue cancelada o recalendarizada, el detalle lo indica con **FOLIO CANCELADO** o **FOLIO RECALENDARIZADO** y muestra las observaciones. Los botones **Cerrar** y **Reactivar** forman parte de la pantalla, pero con la configuración actual no se muestran a ningún perfil.

## Requerido Vs. Surtido

**Para qué sirve:** Comparar lo requerido contra lo surtido.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6.

**Cómo llegar:** Menú › Consulta › Requerido Vs. Surtido.

**Paso a paso:**
1. Captura la fecha inicial y la fecha final en **Fechas**.
2. Si quieres, elige el origen en **--Selecciona Origen--** (**ADMON** o **VD**).
3. Da clic en **MOSTRAR**.
4. Revisa por clave la **Cantidad Req.** y la **Cantidad Sur.**; al final aparece el **Total**.
5. Si quieres llevarte la información, da clic en **Exportar**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Fechas | Fecha inicial y fecha final de entrega | Sí |
| Origen | **ADMON** o **VD**; sin elegir se incluyen ambos | No |

**Mensajes y errores frecuentes:** Esta pantalla no muestra mensajes. Si la tabla sale vacía, falta una de las dos fechas o no hubo remisiones en el periodo.

**Notas:** Se toman las remisiones activas según su fecha de entrega. La tabla muestra **Clave**, **Descripción**, **Origen**, **Cantidad Req.** y **Cantidad Sur.**. El botón **Exportar** aparece solo cuando hay información.

## Semaforización

**Para qué sirve:** Ver el estado de las existencias con semáforo.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5, 6 y 17.

**Cómo llegar:** Menú › Consulta › Semaforización (se abre en una ventana aparte).

**Paso a paso:**
1. Abre la opción. Se muestran los lotes con existencia, con su **Clave**, **Descripción**, **Lote**, **Caducidad**, **Cantidad**, **Origen** y **Proyecto**.
2. Para ver solo un grupo, marca **Caducados**, **Menor a 6 Meses** o **Mayor a 6 Meses**. La pantalla se actualiza sola.
3. Revisa la columna **Semaforización** y el **Total Piezas** al final.
4. Para llevarte la información del grupo elegido, da clic en **Descargar**. Para volver a la lista completa, da clic en **Actualizar**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Caducados | Marca para ver los lotes que ya caducaron | No |
| Menor a 6 Meses | Marca para ver los lotes que caducan dentro de los próximos 6 meses | No |
| Mayor a 6 Meses | Marca para ver los lotes que caducan después de 6 meses | No |

**Mensajes y errores frecuentes:** Esta pantalla no muestra mensajes.

**Notas:** Los colores de la columna **Semaforización** son: rojo para **Caducados** y **Menor 6 Meses**, amarillo para **Mayor a 6 Meses** y verde para **Mayor a 12 Meses**. Sin marcar ninguna casilla se listan todas las existencias con la etiqueta verde, aunque caduquen antes; usa las casillas para clasificarlas. **Descargar** aparece solo al marcar una casilla. No se incluyen las existencias en ubicaciones de caducados, merma, extraordinaria, MRM, CDC ni nuevas.
