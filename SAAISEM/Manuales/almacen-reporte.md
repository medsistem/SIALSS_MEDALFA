# Almacén › Reporte

Reportes de claves, facturación, auditoría, órdenes de compra, caducidad y más.

## Reporte Claves Fuera de Catálogo

**Para qué sirve:** Listar las claves con existencia que ya no están en el catálogo.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6.

**Cómo llegar:** Menú › Reporte › Reporte Claves Fuera de Catálogo.

**Paso a paso:**
1. Abre la opción.
2. Revisa el reporte **Clave FUERA DE CATALOGO** y el **Total Existencia**.
3. Si lo necesitas en archivo, da clic en **Descargar**.

**Campos:** Sin campos de captura; la pantalla solo muestra el reporte.

**Mensajes y errores frecuentes:** Ninguno; la pantalla no muestra avisos propios.

**Notas:** Ninguna.

## Reporte Claves Nulo y Bajo Movimiento

**Para qué sirve:** Identificar claves sin movimiento (nulo) o con poco movimiento (bajo).

**Quién la ve:** Perfiles 100, 1, 2, 3, 5, 6 y 17.

**Cómo llegar:** Menú › Reporte › Reporte Claves Nulo y Bajo Movimiento.

**Paso a paso:**
1. Abre la opción.
2. Revisa **Reporte Clave Nulo Movimiento** y **Reporte Clave Bajo Movimiento**, cada uno con su **Total Existencia**. Ambos muestran Proyecto, Clave, Descripción, Presentación, Entrada Compra, Requerido y Existencia.
3. Si lo necesitas en archivo, da clic en **Descargar**.

**Campos:** Sin campos de captura; la pantalla solo muestra el reporte.

**Mensajes y errores frecuentes:** Ninguno; la pantalla no muestra avisos propios.

**Notas:** **Nulo Movimiento** lista las claves con existencia que no han tenido ninguna salida. **Bajo Movimiento** lista las claves con existencia cuyo requerido equivale a 10 % o menos de lo que ha entrado por compra. Las ubicaciones excluidas del inventario no se consideran.

## Reporte Concentrado Facturado

**Para qué sirve:** Consultar y descargar lo facturado en concentrado.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6.

**Cómo llegar:** Menú › Reporte › Reporte Concentrado Facturado.

**Paso a paso:**
1. Da clic en **Mostrar Todos** o filtra en la tabla.
2. Da clic en **Descargar**.

**Campos:** Sin campos de captura; solo filtros de la tabla.

**Mensajes y errores frecuentes:** Ninguno identificado.

**Notas:** Ninguna.

## Reporte de Auditoría

**Para qué sirve:** Consultar el reporte de auditoría.

**Quién la ve:** Perfiles 100, 1, 3, 5 y 6.

**Cómo llegar:** Menú › Reporte › Reporte de Auditoría.

**Paso a paso:**
1. Abre la opción. Al inicio aparecen todos los registros de validación.
2. Si quieres filtrar, elige el **Auditor**, captura la **Fecha Validación** o usa ambos. La tabla se actualiza al elegir.
3. Revisa la tabla (Fecha, Usuario, Proveedor, OC, Remisión, Folio Int, Cantidad, Claves, Lotes, Proyecto).
4. Da clic en el ojo de la columna **Ver** para abrir el detalle de esa validación.
5. Da clic en **Todo** para quitar los filtros o en **Descargar** para bajar el reporte con los filtros elegidos.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Auditor | Auditor que validó | No |
| Fecha Validación | Fecha en formato dd/mm/aaaa | No |

**Mensajes y errores frecuentes:** Ninguno; la pantalla no muestra avisos propios. Si no hay registros con los filtros elegidos, la tabla aparece vacía.

**Notas:** El reporte solo lista a los auditores que ya han validado recibos.

## Reporte Insumo Especial

**Para qué sirve:** Consultar el reporte de insumos especiales y agregar nuevos.

**Quién la ve:** Perfiles 100, 1 y 5.

**Cómo llegar:** Menú › Reporte › Reporte Insumo Especial.

**Paso a paso:**
1. En **Insumo** elige el tipo (**Ape**, **Controlado** o **Red Fría**). Aparece el catálogo con Clave, Descripción y Presentación.
2. Si lo necesitas en archivo, da clic en **Descargar**.
3. Para añadir una clave, da clic en **Clave**, elige la clave y el tipo de insumo y da clic en **Agregar**. Si el tipo es Oncologico, elige también si es Red Fría o APE.
4. Para quitar una clave del catálogo, da clic en **Eliminar** en su renglón.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Insumo | Tipo de insumo que quieres consultar | Sí, para ver el catálogo |
| Seleccionar Clave | Clave que vas a agregar (solo claves habilitadas del catálogo) | Sí, al agregar |
| Seleccionar el tipo de insumo | Controlado, Red Fría, APE u Oncologico | Sí, al agregar |
| Tipo de insumo oncologico | Red Fría o APE | Solo si el tipo es Oncologico |

**Mensajes y errores frecuentes:**
- "Por favor, completar el campo de clave": falta elegir la clave.
- "Por favor, completar el campo de tipo de insumo": falta elegir el tipo.
- "Por favor, completar el campo de tipo insumo oncologico": elige Red Fría o APE.
- "La clave … ya existe en el reporte de …": esa clave ya estaba en el catálogo.
- "La clave … se ingreso en el catálogo …": la clave se agregó.
- "La clave … fue eliminada del reporte de …": la clave se quitó.

**Notas:** **Eliminar** quita la clave de inmediato, sin pedir confirmación. Estos catálogos son los que identifican los insumos controlados, de red fría y APE en el resto del sistema.

## Reporte OC

**Para qué sirve:** Comparar lo solicitado contra lo recibido de una orden de compra.

**Quién la ve:** Perfiles 100, 1, 2, 3, 4, 5 y 6.

**Cómo llegar:** Menú › Reporte › Reporte OC.

**Paso a paso:**
1. Elige el **No. OC**, el **Proveedor**, un rango de fechas (inicial y final) o combina los filtros que necesites.
2. Da clic en **MOSTRAR**.
3. Revisa **Total Sol**, **Total Recibido** y **Total Diferencia**, y la tabla (No OC, Proveedor, Clave, Nombre genérico, Descripción, Presentación, Grupo terapéutico, Solicitado, Recibido, Monto, Diferencia, Contrato, Fecha programada, Fecha ingreso, Fecha creacion).
4. Da clic en **Exportar** para bajar el resultado.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| No. OC | Número de orden de compra | No |
| Proveedor | Proveedor | No |
| Fechas | Fecha inicial y final de la orden; se usan solo si capturas las dos | No |

**Mensajes y errores frecuentes:** Ninguno; la pantalla no muestra avisos propios. Si no hay resultados, la tabla aparece vacía y no se muestra **Exportar**.

**Notas:** Si no eliges ningún filtro, el reporte incluye todas las órdenes. Compras tiene la misma opción.

## Reporte por Fecha Proveedor

**Para qué sirve:** Consultar lo entregado por proveedor en un periodo.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6.

**Cómo llegar:** Menú › Reporte › Reporte por Fecha Proveedor.

**Paso a paso:**
1. Elige el **Proveedor** o captura la **Fecha de Recibo** (dd/mm/aaaa). La tabla se actualiza al elegir.
2. Revisa la tabla (Proveedor, Clave, Lote, Caducidad, Piezas, OC, Folio Medalfa, Remisión, Usuario, Fecha-Hora de Captura, Usuario Ingreso, Fecha-Hora de Ingreso, O.S.).
3. Da clic en **Descargar** para bajar el reporte con el filtro elegido.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Proveedor | Proveedor que entregó | Una de las dos opciones |
| Fecha de Recibo | Fecha en que se recibió, dd/mm/aaaa | Una de las dos opciones |

**Mensajes y errores frecuentes:** Ninguno; la pantalla no muestra avisos propios. Sin proveedor ni fecha, la tabla aparece vacía.

**Notas:** Si eliges proveedor y fecha a la vez, el reporte se filtra solo por la fecha. Elige uno u otro para evitar confusiones.

## Reporte Próximos a Caducar

**Para qué sirve:** Ver los productos próximos a caducar por proyecto.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6.

**Cómo llegar:** Menú › Reporte › Reporte Próximos a Caducar.

**Paso a paso:**
1. En **Proyectos** elige el proyecto (solo aparecen los que tienen existencia).
2. Da clic en **Buscar**.
3. Revisa la tabla (CLAVE, DESCRIPCION, LOTE, CADUCIDAD, PRESENTACION, EXISTENCIA, UBICACION, ORIGEN, RE-UBICAR).
4. Da clic en **Descargar** para bajar el reporte.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Proyectos | Un proyecto de la lista | Sí |

**Mensajes y errores frecuentes:**
- "DEBES SELECCIONAR UN PROYECTO": elige un proyecto antes de dar clic en **Buscar**.

**Notas:** El reporte muestra los lotes con existencia que caducan en los próximos 3 meses. No incluye las ubicaciones de caducados, merma, cuarentena, ingresos ni las que ya son de próximos a caducar. La columna **RE-UBICAR** indica el nombre de la ubicación de próximos a caducar a la que corresponde cada lote; es solo informativa.

## Reporte Red Fría

**Para qué sirve:** Consultar y corregir las temperaturas de red fría.

**Quién la ve:** Perfiles 100 y 1.

**Cómo llegar:** Menú › Reporte › Reporte Red Fría.

**Paso a paso:**
1. Captura el rango de **Fechas** (inicial y final). Al entrar aparece la fecha de hoy.
2. Da clic en **MOSTRAR**. La tabla lista los folios con No. Folio, Punto de Entrega, Estatus y Fec Ent.
3. Da clic en **Exportar** para bajar el concentrado.
4. Para cambiar la temperatura, da clic en **Temperatura °C**, captura el valor y da clic en **Guardar** (o en **Cancelar** para salir).

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Fechas | Fecha inicial y final | Sí |
| Temperatura °C | Temperatura que se registra para red fría | Sí, al guardar |

**Mensajes y errores frecuentes:** Ninguno; la pantalla no muestra avisos propios. Si no capturas la temperatura, el navegador te pide llenar el campo.

**Notas:** El valor de **Temperatura °C** es único: al guardarlo reemplaza el valor anterior para todos los folios. Cada renglón tiene un botón con flecha para abrir el detalle del folio.

> Pendiente de confirmar: qué muestra y qué permite corregir el detalle del folio (botón de flecha). La pantalla de destino no está en la carpeta principal de la aplicación (solo en respaldos), así que no se puede describir con certeza.

## Reporte Usuario Suspendidos

**Para qué sirve:** Ver los usuarios suspendidos.

**Quién la ve:** Perfiles 100, 1, 5 y 6.

**Cómo llegar:** Menú › Reporte › Reporte Usuario Suspendidos.

**Paso a paso:**
1. Abre la opción.
2. Revisa la tabla **Reporte Usuarios Suspendidos** con las columnas Id, Nombre, Fecha Suspendido y Fecha Activación.

**Campos:** Sin campos de captura; la pantalla solo muestra la lista.

**Mensajes y errores frecuentes:** Ninguno; la pantalla no muestra avisos propios.

**Notas:** Si el usuario ya fue reactivado, aparece su **Fecha Activación**; si sigue suspendido, esa columna queda vacía. Desde este reporte no se puede suspender ni reactivar a nadie.

## Reporteador Estadística de Entrega

**Para qué sirve:** Generar el reporte estadístico de entregas por proyecto, jurisdicción, municipio y unidad.

**Quién la ve:** Perfiles 100, 1, 3, 4, 5 y 6.

**Cómo llegar:** Menú › Reporte › Reporteador Estadística de Entrega.

**Paso a paso:**
1. En **Cifras** marca lo que quieres medir: **SURTIDO**, **REQUERIDO** o **COSTO**.
2. Marca cómo quieres agrupar el reporte, al menos una opción de **Clasificación Unidad de Atención** (**JURISDICCIÓN/HOSP**, **NIVEL**, **MUNICIPIO**), **Categoría** (**UNIDAD**, **INSUMO**), **Facturas** (**DOCT**, **LOTE**) o **Clasificación medicamentos** (**ORIGEN**, **TIPO REM**, **PROYECTO**, **TIPO CAUSES**).
3. En **Búsqueda** captura las **Fechas** (inicial y final).
4. Si lo necesitas, elige filtros en **Proyecto**, **Jurisdicción**, **Municipio**, **Unidad**, **Insumo** y **Tipo Unidad**. Cada vez que eliges un valor se agrega a la lista de arriba, así que puedes elegir varios, y la casilla de esa categoría se marca sola.
5. Da clic en **Generar**. El botón muestra "...Generando..." mientras se prepara.
6. En el resultado, da clic en **Exportar** para bajarlo o en **Regresar** para cambiar los parámetros. Para dejar todo en blanco, da clic en **Limpiar**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Cifras | SURTIDO, REQUERIDO y/o COSTO | Sí, al menos una |
| Casillas de agrupación | Cómo se agrupa el reporte | Sí, al menos una además de Cifras |
| Fechas | Fecha inicial y final | Sí |
| Proyecto | Proyecto o proyectos | No |
| Jurisdicción, Municipio, Unidad, Insumo, Tipo Unidad | Filtros; Municipio y Unidad dependen de lo que elijas antes | No |

**Mensajes y errores frecuentes:**
- "Favor de seleccionar cifras": marca al menos una cifra.
- "Favor de seleccionar al menos otra categaria además de "Cifras"": marca al menos una casilla de agrupación.
- "Favor de seleccionar Fechas": captura el rango de fechas.

**Notas:** Al elegir una **Jurisdicción** se cargan sus municipios, y al elegir un **Municipio** se cargan sus unidades. El resultado muestra el rango de fechas consultado. Otros perfiles tienen su propia versión de este reporteador (Compras y Auditoría).
