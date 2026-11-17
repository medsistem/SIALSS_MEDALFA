# Compras › Consulta

## Acuse de Recibo

**Para qué sirve:** Buscar y descargar los documentos de acuse de recibo cargados al sistema.

**Quién la ve:** Perfil 13.

**Cómo llegar:** Menú › Consulta › Acuse de Recibo.

**Paso a paso:**
1. Captura en **Nombre** parte del nombre del archivo (mínimo 4 caracteres).
2. Da clic en la lupa. Aparece la lista **Documentos**, con **Nombre** y **Descargar**.
3. Da clic en el botón de descarga del documento que necesitas. Mientras se prepara, verás una ventana de espera.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Nombre | Parte del nombre del documento, mínimo 4 caracteres | Sí |

**Mensajes y errores frecuentes:**
- "Favor de ingresar un nombre válido (longitud mínima de 4)": captura al menos 4 caracteres.
- "Error Contactar al departamento de sistemas": falló la búsqueda o la descarga; inténtalo de nuevo y, si persiste, avisa a sistemas.

**Notas:** Es la misma pantalla que usa Almacén, con el menú de Compras. La lista se arma con los archivos que ya están cargados en el servidor; si no aparece el que buscas, pide que lo carguen.

## Consulta Global Unidad

**Para qué sirve:** Consultar lo surtido por unidad de atención: qué claves y cuántas piezas recibió cada unidad.

**Quién la ve:** Perfil 13.

**Cómo llegar:** Menú › Consulta › Consulta Global Unidad.

**Paso a paso:**
1. Si quieres limitar el periodo, elige la fecha inicial y la fecha final (son las dos cajas de fecha, sin etiqueta).
2. Si quieres, elige el **Proyecto** (**-Seleccione Proyecto-**), el **Origen** (**-Selec Origen-**) y la **Unidad**.
3. Da clic en **Buscar**.
4. Revisa la tabla **Global de Insumo por Unidad**: ClaveU, Unidad, Clave, Clave SS, Nombre genérico, Descripción, Presentación, Cantidad, Proyecto y Origen. Puedes buscar y ordenar con los controles de la tabla.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Fecha inicial y fecha final | Periodo de aplicación; se usan solo si capturas las dos | No |
| Proyecto | Proyecto a consultar | No |
| Origen | Origen del insumo | No |
| Unidad | Unidad de atención | No |

**Mensajes y errores frecuentes:** Esta pantalla no muestra mensajes. Si la tabla sale vacía, no hubo surtido con esos filtros.

**Notas:** Se suman las piezas surtidas por unidad y clave. Si no eliges ningún filtro se consulta todo, por lo que la carga puede tardar.

## Consulta Movimientos

**Para qué sirve:** Consultar las entradas por compra y las salidas surtidas a unidades.

**Quién la ve:** Perfil 13.

**Cómo llegar:** Menú › Consulta › Consulta Movimientos.

**Paso a paso:**
1. Elige el concepto en **Conceptos** (es obligatorio): **[1]** (entrada por compra) o **[51]** (surtido a unidades).
2. Captura la **Clave** si quieres ver un solo insumo.
3. Elige las dos fechas de **Fecha** (inicial y final) para limitar el periodo.
4. Si quieres, elige el **Origen**.
5. Da clic en **Mostrar**. Para empezar de nuevo, da clic en **Limpiar**.
6. Revisa la tabla. Con el concepto 1 verás Fecha, Des/Mov, Clave, ClaveSS, Nombre genérico, Descripción, Presentación, Lote, Caducidad, Cantidad, Origen, Remisión, Proveedor, OC y Contrato. Con el 51 se muestran el folio y la unidad que recibió.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Conceptos | **[1]** o **[51]** | Sí |
| Clave | Clave del insumo | No |
| Fecha | Fecha inicial y final; se usan solo si capturas las dos | No |
| Origen | Origen del insumo | No |

**Mensajes y errores frecuentes:** Esta pantalla no muestra mensajes. Si la tabla sale vacía, no hay movimientos con esos datos.

**Notas:**
- Diferencia con Almacén: en Compras solo se ofrecen los conceptos 1 y 51, se elige uno a la vez y no hay filtro de **Proyecto**.
- Conviene capturar al menos la **Clave**, las fechas o el **Origen**. En el concepto 1, la combinación de **Clave** y **Origen** sin fechas no da resultados.
- No se incluyen las claves excluidas de reportes.

## Existencias por programa

**Para qué sirve:** Consultar las existencias de las claves que pertenecen a un programa, por proyecto.

**Quién la ve:** Perfil 15.

**Cómo llegar:** Menú › Consulta › Existencias por programa.

**Paso a paso:**
1. Elige el **Seleccione Proyecto**.
2. Elige el tipo de **Consulta**: **Global** (por clave) o **Lote**. La tabla se muestra en cuanto eliges ambos.
3. Revisa la tabla (Clave, Descripción Específica y Cantidad; en **Lote** también Lote y Caducidad) y, abajo, **No. Claves** y **Total**.
4. Da clic en **Descargar** para obtener el archivo.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Seleccione Proyecto | Proyecto a consultar | Sí (*) |
| Consulta | **Global** o **Lote** | Sí (*) |

**Mensajes y errores frecuentes:**
- "Error grave contactar con el departamento de sistemas": falló la consulta; avisa a sistemas.
- Si no eliges proyecto y consulta, la tabla no se muestra.

**Notas:** Solo se consideran lotes con existencia, no caducados, con la clave activa y que estén en ubicaciones visibles. Solo entran las claves definidas para el programa en el sistema.

## Reporte Claves Fuera de Catálogo

**Para qué sirve:** Listar las claves con existencia que ya no están en el catálogo.

**Quién la ve:** Perfil 13.

**Cómo llegar:** Menú › Consulta › Reporte Claves Fuera de Catálogo.

**Paso a paso:** Igual que en [Almacén](almacen-reporte.md#reporte-claves-fuera-de-catálogo).

**Campos:** Sin campos de captura identificados.

**Mensajes y errores frecuentes:** Ninguno identificado.

**Notas:** Es la misma pantalla que en Almacén; para el perfil 13 se muestra con el menú de Compras.

## Reporte Claves Nulo y Bajo Movimiento

**Para qué sirve:** Identificar claves sin movimiento o con poco movimiento.

**Quién la ve:** Perfil 13.

**Cómo llegar:** Menú › Consulta › Reporte Claves Nulo y Bajo Movimiento.

**Paso a paso:** Igual que en [Almacén](almacen-reporte.md#reporte-claves-nulo-y-bajo-movimiento).

**Campos:** Sin campos de captura identificados.

**Mensajes y errores frecuentes:** Ninguno identificado.

**Notas:** Es la misma pantalla que en Almacén, con el menú de Compras. Los dos reportes comparan lo comprado (**Entrada Compra**) contra lo **Requerido** y la **Existencia**. En **Reporte Clave Bajo Movimiento** aparecen las claves cuyo requerido es el 10 % o menos de lo comprado.

## Reporte Fecha Proveedor

**Para qué sirve:** Consultar lo comprado y recibido por proveedor, fechas, remisión u orden de compra.

**Quién la ve:** Perfil 13.

**Cómo llegar:** Menú › Consulta › Reporte Fecha Proveedor.

**Paso a paso:**
1. Elige el **Proveedor**, el rango **Fecha Inicial** y **Fecha Final**, la **Remisión** o la **OC**; usa los que necesites.
2. Da clic en **Buscar**.
3. Revisa la tabla (Clave, Nombre Genérico, Descripción, Presentación, Proveedor, Marca, Lote, Caducidad, Piezas, Costo U, Monto, Fecha, OC, Fuente de Financiamiento, Folio Medalfa, Remisión, Usuario, Ord. Sum., Forma Farmacéutica, Concentración y Contrato).
4. Da clic en **Descargar** para bajar lo filtrado, o en **Descargar Todo** para bajar todo lo comprado.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Proveedor | Proveedor con compras registradas | No |
| Fecha Inicial y Fecha Final | Periodo de la entrada; se usan solo si capturas las dos | No |
| Remisión | Número de remisión | No |
| OC | Número de orden de compra | No |

**Mensajes y errores frecuentes:** Esta pantalla no muestra mensajes. Si la tabla sale vacía, no hay compras con esos filtros.

**Notas:** Diferencia con la opción de Almacén (Reporte por Fecha Proveedor): la de Compras incluye costo, monto, fuente de financiamiento y contrato, y filtra también por fechas, remisión y OC. No se incluyen las claves excluidas de reportes.

## Reporte OC

**Para qué sirve:** Comparar lo solicitado contra lo recibido de una orden de compra.

**Quién la ve:** Perfil 13.

**Cómo llegar:** Menú › Consulta › Reporte OC.

**Paso a paso:** Igual que en [Reporte OC de Almacén](almacen-reporte.md#reporte-oc): elige el **No. OC**, el **Proveedor** o un rango de fechas, da clic en **MOSTRAR** y revisa **Total Sol**, **Total Recibido** y **Total Diferencia**.

**Campos:** No. OC, Proveedor y fechas inicial y final (todos opcionales).

**Mensajes y errores frecuentes:** Ninguno; la pantalla no muestra avisos propios. Si no hay resultados, la tabla aparece vacía.

**Notas:** Es la misma pantalla que en Almacén; para el perfil 13 se muestra con el menú de Compras.

## Reporteador Estadística de Entrega

**Para qué sirve:** Generar un reporte estadístico de lo surtido, requerido y su costo, agrupado como tú lo elijas.

**Quién la ve:** Perfil 13.

**Cómo llegar:** Menú › Consulta › Reporteador Estadística de Entrega.

**Paso a paso:**
1. En **Cifras** marca al menos una: **SURTIDO**, **REQUERIDO** o **COSTO**.
2. Marca al menos una agrupación más: en **Clasificación Unidad de Atención** (**JURISDICCIÓN/HOSP**, **NIVEL**, **MUNICIPIO**), **Categoría** (**UNIDAD**, **INSUMO**), **Facturas** (**DOCT**, **LOTE**), **Fuente Financiamiento** o **Clasificación medicamentos** (**ORIGEN**, **TIPO REM**, **PROYECTO**, **TIPO CAUSES**).
3. En **Búsqueda**, captura las dos **Fechas**.
4. Si quieres limitar el reporte, elige valores en **Proyecto**, **Jurisdicción**, **Municipio**, **Unidad**, **Insumo** y **Tipo Unidad**. Cada valor se agrega al recuadro de su lista; puedes agregar varios. Al elegir un proyecto se marca solo la agrupación **PROYECTO**, y así con los demás.
5. Da clic en **Generar**. El botón dirá "...Generando..." mientras trabaja.
6. En el resultado revisa la tabla del rango de fechas. Da clic en **Exportar** para bajarla o en **Regresar** para cambiar los filtros. Para limpiar la pantalla, da clic en **Limpiar**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Cifras | **SURTIDO**, **REQUERIDO** y/o **COSTO** | Sí, al menos una |
| Agrupaciones | Unidad de atención, categoría, facturas, fuente de financiamiento o clasificación de medicamentos | Sí, al menos una además de las cifras |
| Fechas | Fecha inicial y final | Sí |
| Proyecto, Jurisdicción, Municipio, Unidad, Insumo, Tipo Unidad | Filtros del reporte | No |

**Mensajes y errores frecuentes:**
- "Favor de seleccionar cifras": marca **SURTIDO**, **REQUERIDO** o **COSTO**.
- "Favor de seleccionar al menos otra categaria además de "Cifras"": marca al menos una agrupación.
- "Favor de seleccionar Fechas": captura las fechas.

**Notas:** Es el mismo reporteador que usa Almacén en **Requerido Vs. Surtido**, pero con el resultado y la exportación de Compras (incluye costo).

## Semaforización

**Para qué sirve:** Ver las existencias según su cercanía a la caducidad.

**Quién la ve:** Perfil 13.

**Cómo llegar:** Menú › Consulta › Semaforización.

**Paso a paso:**
1. Marca **Menor a 6 Meses** o **Mayor a 6 Meses**. La pantalla se actualiza sola.
2. Revisa la tabla: Clave, Nombre genérico, Descripción, Presentación, Lote, Caducidad, Cantidad, Origen, Proyecto y **Semaforización**; al final aparece **Total Piezas**.
3. Da clic en **Descargar** para llevarte lo del grupo elegido. Da clic en **Actualizar** para volver a empezar.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Menor a 6 Meses | Lotes que caducan dentro de los próximos 6 meses (etiqueta verde) | No |
| Mayor a 6 Meses | Lotes que caducan después de 6 meses (etiqueta amarilla) | No |

**Mensajes y errores frecuentes:** Esta pantalla no muestra mensajes. Mientras no marques una opción, la tabla aparece vacía.

**Notas:** Diferencia con Almacén: en Compras no existe la opción **Caducados** y la pantalla no muestra nada hasta que eliges un grupo. En la columna **Semaforización** la etiqueta repite el grupo elegido.
