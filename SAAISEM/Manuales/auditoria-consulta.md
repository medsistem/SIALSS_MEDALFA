# Auditoría › Consulta

## Existencia

**Para qué sirve:** Consultar las existencias por proyecto.

**Quién la ve:** Perfil 28.

**Cómo llegar:** Menú › Consulta › Existencia.

**Paso a paso:**

1. Entra a **Consulta Existencias** desde el menú.
2. En **Seleccione Proyecto**, elige el proyecto (por ahora aparece MDF).
3. En **Consulta**, elige **Disponible** o **Lote**. La tabla se llena sola al elegir el tipo de consulta.
4. Revisa la tabla. Puedes ordenarla, paginarla y moverte con las barras de desplazamiento.
5. Abajo de la tabla verás **No. Claves** y **Total** (suma de la columna Cantidad).
6. Si necesitas el archivo, da clic en **Descargar** y se baja un Excel con el mismo tipo de consulta.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| **Seleccione Proyecto** | Elige el proyecto de la lista. | Sí (aparece marcado con *) |
| **Consulta** | **Disponible** (incluye la columna Estatus) o **Lote** (sin la columna Estatus). | Sí (aparece marcado con *) |

La tabla muestra: Proyecto, Clave, ClaveSS, Descripción, Lote, Caducidad, Cantidad, Origen, Orden de compra y, en la consulta **Disponible**, también Estatus.

**Mensajes y errores frecuentes:**

- **Error grave contactar con el departamento de sistemas:** no se pudo traer la información. Vuelve a elegir la consulta y, si se repite, avisa a Sistemas.
- Si la tabla sale vacía, no hay existencias que mostrar y no aparecen **No. Claves** ni **Total**.
- Si da clic en **Descargar** sin haber elegido primero **Consulta**, el archivo no se genera correctamente. Elige el tipo de consulta antes de descargar.

**Notas:**

- El Excel se llama "Reporte_Existencia_Disponible" o "Reporte_Existencia_Lote", según lo que elegiste.
- En el Excel de la consulta **Lote** no viene la columna Origen ni Estatus.
- Esta pantalla solo consulta; no modifica existencias.

## Reporte Fecha Proveedor

**Para qué sirve:** Consultar lo entregado por proveedor en un periodo.

**Quién la ve:** Perfil 28.

**Cómo llegar:** Menú › Consulta › Reporte Fecha Proveedor.

**Paso a paso:**

1. Entra a **Reporte por Fecha Proveedor** desde el menú.
2. Si quieres acotar el resultado, captura uno o varios filtros (todos son opcionales).
3. Da clic en **Buscar**. La tabla se llena con las entregas que cumplen los filtros.
4. Para obtener el archivo con esos mismos filtros, da clic en **Descargar**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| **Proveedor** | Elige un proveedor de la lista (**--Proveedor--** muestra todos). | No |
| **Fecha Inicial** | Fecha desde la que quieres consultar. | No (úsala junto con Fecha Final) |
| **Fecha Final** | Fecha hasta la que quieres consultar. | No (úsala junto con Fecha Inicial) |
| **Clave** | Clave del insumo. | No |
| **Lote** | Lote del insumo. | No |
| **Remisión** | Folio de la remisión. | No |
| **OC** | Número de orden de compra. | No |

La tabla muestra: Proyecto, Proveedor, Clave, Marca, Lote, Caducidad, Piezas, Fecha, OC, Folio Medalfa, Remisión, Usuario, Costo U, Monto y O.S.

**Mensajes y errores frecuentes:**

- Si ninguna entrega cumple los filtros, la tabla aparece vacía. Revisa que la clave, el lote o la OC estén bien escritos y amplía el rango de fechas.
- Las fechas solo filtran cuando capturas las dos; si llenas una sola, se ignora.

**Notas:**

- Sin filtros, la consulta trae las entregas desde el 1 de enero de 2021 (se excluye la clave 9999).
- **Descargar** respeta los filtros que usaste en la última búsqueda; por eso da clic en **Buscar** antes de descargar.
- **Costo U** es el costo por pieza y **Monto** es piezas por costo.
- Esta pantalla solo consulta; no modifica información.

## Reporteador Estadística de Entrega

**Para qué sirve:** Generar el reporte estadístico de entregas.

**Quién la ve:** Perfil 28.

**Cómo llegar:** Menú › Consulta › Reporteador Estadística de Entrega.

**Paso a paso:**

1. Entra a **Reporteador Estadística de Entrega** desde el menú.
2. En el recuadro **Cifras**, marca al menos una: **SURTIDO**, **REQUERIDO** o **COSTO**.
3. Marca al menos una categoría más para agrupar el reporte, en cualquiera de los recuadros: **Clasificación Unidad de Atención** (**JURISDICIÓN/HOSP**, **NIVEL**, **MUNICIPIO**), **Categoría** (**UNIDAD**, **INSUMO**), **Facturas** (**DOCT**, **LOTE**) o **Clasificación medicamentos** (**ORIGEN**, **PROYECTO**, **TIPO CAUSES**).
4. En **Búsqueda**, captura las **Fechas** (inicial y final).
5. Si quieres limitar el reporte, elige valores en **Proyecto**, **Jurisdicción**, **Municipio**, **Unidad**, **Insumo** y **Tipo Unidad**. Cada valor que eliges se agrega a la caja de su lado; puedes elegir varios y se acumulan.
6. Da clic en **Generar**. El botón cambia a "...Generando..." mientras se arma el reporte.
7. En la pantalla de resultados revisa el rango de fechas y la tabla. Da clic en **Exportar** para bajar el archivo o en **Regresar** para volver a los filtros.
8. Para empezar de nuevo, da clic en **Limpiar**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| **Cifras** (**SURTIDO**, **REQUERIDO**, **COSTO**) | Marca lo que quieres medir. | Sí, al menos una |
| Categorías de agrupación | Marca cómo quieres agrupar el reporte (unidad, insumo, jurisdicción, nivel, municipio, documento, lote, origen, proyecto, tipo de causes). | Sí, al menos una |
| **Fechas** | Fecha inicial y fecha final del periodo. | Sí, al menos una de las dos |
| **Proyecto** | Elige uno o varios proyectos. Marca solo la casilla **PROYECTO**. | No |
| **Jurisdicción** | Elige una o varias jurisdicciones. Marca solo la casilla **JURISDICIÓN/HOSP**. | No |
| **Municipio** | Se llena según la jurisdicción elegida. Marca solo la casilla **MUNICIPIO**. | No |
| **Unidad** | Se llena según jurisdicción y municipio. Marca solo la casilla **UNIDAD**. | No |
| **Insumo** | Elige claves de la lista (clave y descripción). Marca solo la casilla **INSUMO**. | No |
| **Tipo Unidad** | Elige el tipo de unidad. | No |

**Mensajes y errores frecuentes:**

- **Favor de seleccionar cifras:** no marcaste **SURTIDO**, **REQUERIDO** ni **COSTO**. Regresa y marca al menos una.
- **Favor de seleccionar al menos otra categaria además de "Cifras":** falta marcar una categoría de agrupación. Regresa y marca una.
- **Favor de seleccionar Fechas:** dejaste vacías las dos fechas. Captura al menos una.

**Notas:**

- Al elegir un valor en **Proyecto**, **Jurisdicción**, **Municipio**, **Unidad** o **Insumo**, la casilla de su categoría se marca sola.
- Las cajas de texto junto a cada lista son de solo lectura; se llenan con lo que eliges. Si te equivocas, da clic en **Limpiar** y empieza de nuevo.
- El reporte se muestra en pantalla; el archivo se obtiene con **Exportar**.
