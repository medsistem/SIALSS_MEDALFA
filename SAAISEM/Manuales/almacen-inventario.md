# Almacén › Inventario

Opciones para mover, consultar y ubicar el inventario.

## Captura Movimiento

**Para qué sirve:** Registrar un movimiento al inventario (entrada o salida por concepto) de claves y lotes.

**Quién la ve:** Perfiles 100, 1, 2, 3 y 5.

**Cómo llegar:** Menú › Inventario › Captura Movimiento.

**Paso a paso:**
1. Da clic en **Nuevo Folio**.
2. Elige el **Movimiento** (concepto) y la **Fecha de Entrega**.
3. Captura la clave en **Ingrese CLAVE** y da clic en **Buscar**. Se muestran la **Clave** y su **Descripción**.
4. Captura la cantidad en **Ingresé Cantidad** y da clic en el segundo **Buscar** para elegir el lote.
5. Elige el lote; la clave se agrega a la tabla (**Clave**, **Lote**, **Caducidad**, **Ubicación**, **Marca**, **Cantidad**, **Remover**). Repite con cada clave.
6. Para quitar una clave, da clic en el botón de eliminar del renglón y confirma.
7. Captura las **Observaciones**.
8. Da clic en **Confirmar Movimiento**, o en **Cancelar Movimiento** para anularlo.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Movimiento | Concepto del movimiento (entrada o salida) | Sí |
| Fecha de Entrega | Fecha del movimiento; no puede ser anterior a hoy | Sí |
| Ingrese CLAVE | Clave del insumo | Sí |
| Ingresé Cantidad | Piezas a mover; solo números, de 1 a 10000 | Sí |
| Observaciones | Motivo o comentario del movimiento | Sí, para confirmar |

**Mensajes y errores frecuentes:**
- "Seleccione Movimiento", "Seleccione Fecha de Entrega", "Escriba una Clave", "Escriba una cantidad": captura o elige el dato que falta.
- "Favor de Capturar Toda la información": faltan datos.
- "La cantidad debe ser mayor a cero": captura una cantidad válida.
- "Insumo sin Existencias": la clave no existe en el catálogo o no tiene existencia.
- "Favor de agregar Observación": captura las **Observaciones** antes de confirmar.
- "Seguro que desea eliminar esta clave?" y "Seguro de CANCELAR?": confirma solo si estás seguro.
- "Movimiento Eliminada Correctamente": se canceló el movimiento.
- "Error al Generar la remisión, Contacte con el área de Sistemas": repórtalo.

**Notas:** La pantalla muestra el proyecto con el que trabajas. Los conceptos se dan de alta en **Concepto Movimiento** (módulo Consulta).

## Existencia Disponible

**Para qué sirve:** Consultar la existencia disponible por proyecto.

**Quién la ve:** Perfil 17.

**Cómo llegar:** Menú › Inventario › Existencia Disponible.

**Paso a paso:**
1. Elige el proyecto en **Seleccione Proyecto**.
2. En **Consulta**, elige **Existencias Disponibles**. La tabla se llena sola.
3. Revisa los totales **No. Claves** y **Total**.
4. Da clic en **Descargar** para obtener el archivo.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Seleccione Proyecto | Proyecto a consultar | Sí |
| Consulta | **Existencias Disponibles** | Sí |

**Mensajes y errores frecuentes:**
- "Error grave contactar con el departamento de sistemas": repórtalo.

**Notas:** La tabla muestra proyecto, clave, descripción, tipo de insumo, lote, caducidad, cantidad, origen, estatus, orden de suministro y ubicación. "Disponible" significa que no se incluyen las ubicaciones que el sistema tiene marcadas como excluidas; no se encontró que se descuente lo apartado por remisiones en proceso.

## Existencias por Proyecto

**Para qué sirve:** Consultar las existencias por proyecto, clave y lote.

**Quién la ve:** Perfiles 100, 1, 2, 3, 4, 5, 6 y 17.

**Cómo llegar:** Menú › Inventario › Existencias por Proyecto.

**Paso a paso:**
1. Elige el proyecto en **Seleccione Proyecto**.
2. Elige cómo ver la existencia: **Global**, **Lote** o **Ubicación**.
3. Da clic en **Mostrar Todos** para ver todo el inventario o filtra en la tabla.
4. Revisa el **Total** y da clic en **Descargar** para exportar.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Seleccione Proyecto | Proyecto a consultar | Sí |
| Global / Lote / Ubicación | Nivel de detalle de la consulta (trae **Global** de inicio) | Sí |

**Mensajes y errores frecuentes:**
- "Error grave contactar con el departamento de sistemas": repórtalo.

**Notas:** Ninguna.

## Kardex Claves

**Para qué sirve:** Ver todos los movimientos de una clave (ingresos, egresos y redistribución) en un periodo.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5, 6 y 17.

**Cómo llegar:** Menú › Inventario › Kardex Claves.

**Paso a paso:**
1. Captura la **Clave** o la **Descripción** (al escribir se sugieren coincidencias) y da clic en el botón de buscar.
2. Si quieres acotar el periodo, captura **Fecha Inicial** y **Fecha Final**.
3. Para afinar, elige el **Lote** (la **Caducidad** y el **Origen** se llenan al elegirlo) y da clic en el botón de buscar de esa fila.
4. Consulta las tablas **Ingresos / Egresos** y **Redistribución en Almacén**, y la **Existencia Actual**.
5. Para obtener el kardex en un archivo, da clic en el botón de descarga (el de guardar).

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Clave | Clave del insumo | Sí, si no capturas la descripción |
| Descripción | Descripción del insumo | Sí, si no capturas la clave |
| Fecha Inicial / Fecha Final | Periodo a consultar | No; la pantalla no las exige |
| Lote, Caducidad | Lote del insumo; la caducidad corresponde a ese lote | No |
| Origen | Origen del lote (solo lectura) | No |

**Mensajes y errores frecuentes:**
- "Atención: Favor de verificar criterios de búsqueda": captura la clave o la descripción, o busca primero la clave antes de buscar por lote o descargar.
- "Error Contactar al departamento de sistemas": repórtalo.

**Notas:** Ninguna.

## Kardex por día

**Para qué sirve:** Ver los movimientos de todas las claves en un día.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6.

**Cómo llegar:** Menú › Inventario › Kardex por día.

**Paso a paso:**
1. Captura la **Fecha**.
2. Consulta **Ingresos / Egresos** y **Redistribución en Almacén**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Fecha | Día a consultar | Sí |

**Mensajes y errores frecuentes:** Ninguno identificado.

**Notas:** Ninguna.

## Redistribución HH

**Para qué sirve:** Mover el inventario entre ubicaciones con el equipo de mano (HH).

**Quién la ve:** Perfiles 100 y 1.

**Cómo llegar:** Menú › Inventario › Redistribución HH.

**Paso a paso:**
1. Elige una de las tres opciones: **Insumo Recién Ingresado** (lo que acaba de entrar), **Insumo Ya Ubicado** (lo que ya está en una ubicación) o **Por Clave de Insumo**.
2. Identifica el insumo a mover:
   - En **Insumo Recién Ingresado**, captura el **CB del Insumo a Mover** y da clic en **Leer Insumo**; o da clic en **Por Ubicar** para ver lo que aún no tiene ubicación.
   - En **Insumo Ya Ubicado**, captura la **Ubicación** o el **CB Ubicación** y da clic en **Leer Ubicación**.
   - En **Por Clave de Insumo**, captura la **Clave del Insumo a Mover** y da clic en **Leer Insumo**.
3. En la tabla (**Proyecto**, **Clave**, **Lote**, **Caducidad**, **Ubicación**, **Cantidad**, **Origen**), da clic en **Seleccionar** en el renglón que vas a mover. **Regresar** vuelve a las opciones.
4. Revisa los datos del insumo, captura la **Cantidad a Mover** y el **CB de Nueva Ubicación**.
5. Da clic en **Redistribuir** y confirma.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| CB del Insumo a Mover | Código de barras del insumo | Sí, en **Insumo Recién Ingresado** |
| Ubicación / CB Ubicación | Ubicación origen o su código de barras | Una de las dos, en **Insumo Ya Ubicado** |
| Clave del Insumo a Mover | Clave del insumo | Sí, en **Por Clave de Insumo** |
| Cantidad a Mover | Piezas a mover; solo números, de 1 hasta lo disponible | Sí |
| CB de Nueva Ubicación | Código de barras de la ubicación destino | Sí |

**Mensajes y errores frecuentes:**
- "La Cantidad a mover no puede ser mayor a la cantidad en existencia.": captura una cantidad menor o igual a la existente.
- "La Cantidad a mover no puede ser vacio": captura la cantidad.
- "La Ubicación no puede ser vacio": captura el código de la ubicación destino.
- "La Ubicación no puede ser igual a la anterior": elige otra ubicación destino.
- "Seguro que desea hacer la redistribución?": confirma solo si estás seguro.
- "Ubicación no válida": el código de la ubicación destino no existe.
- "Este insumo está apartado con N piezas": parte de la existencia está comprometida en remisiones en proceso; la cantidad máxima a mover es la diferencia.
- "No se puede mover este insumo.": el insumo no se puede redistribuir.

**Notas:** Algunas ubicaciones destino están restringidas (por ejemplo, de conciliación o de caducos) y bloquean el botón **Redistribuir**. Algunos renglones (como los de ubicaciones de acopio o de merma de red fría) aparecen con **Seleccionar** desactivado.

## Ubicaciones

**Para qué sirve:** Consultar dónde está cada insumo en el almacén y ver lo que falta por ubicar.

**Quién la ve:** Perfiles 100, 1, 2, 3, 4, 5, 6 y 17.

**Cómo llegar:** Menú › Inventario › Ubicaciones.

**Paso a paso:**
1. Da clic en **Buscar** para consultar por clave o ubicación.
2. Usa **Por Ubicar** o **Por Ubicar Crossdock** para ver lo que aún no tiene ubicación.
3. Usa **Mostrar Todas** para ver todas las ubicaciones.
4. Revisa los totales: **Total de Piezas** y, para Modula, **Total Modula**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Clave | Clave del insumo | No |
| Lote | Lote del insumo | No |
| Ubicación | Ubicación a consultar | No |
| CB Med | Código de barras del insumo | No |

**Mensajes y errores frecuentes:** La pantalla no muestra avisos propios. Si la tabla queda vacía, no hay existencia con esos criterios.

**Notas:** La tabla muestra **Proyecto**, **CLAVE**, **Presentacion**, **Origen**, **Lote**, **Caducidad**, **Ubicación**, **Piezas**, **Proveedor** y **Bodega**. Con **Descargar Inventario** obtienes el inventario completo en un archivo.

