# Almacén › Remisión

Opciones para surtir a las unidades: generar, modificar y devolver remisiones.

## Devoluciones Facturas

**Para qué sirve:** Registrar la devolución de una remisión ya facturada.

**Quién la ve:** Perfiles 100, 1, 2, 3 y 5.

**Cómo llegar:** Menú › Remisión › Devoluciones Facturas.

**Paso a paso:**
1. Captura el número de **Folios** de la remisión y, si lo necesitas, elige el **Proyecto**.
2. Da clic en **MOSTRAR TODO** para ver todas las claves del folio, o en **MOSTRAR POR CLAVE** para trabajar clave por clave.
3. Revisa los datos del folio (**Documento**, **Unidad**, **Entrega**) y la tabla de claves: **Clave**, **Descripción**, **Lote**, **Caducidad**, **Req.**, **Ubicación**, **Ent.**, **Devolución**, **Costo U** e **Importe**.
4. Para devolver solo parte de una clave, da clic en el lápiz de la columna **Modificar**, captura la **Cantidad a Devolver** y da clic en **Guardar** (o en **Cancelar** para salir).
5. Si una clave no se devuelve, da clic en el bote de basura de la columna **Eliminar**.
6. Captura las **Observaciones** y da clic en **Aplicar Devolución**. Confirma en el aviso.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Folios | Número de la remisión que se devuelve | Sí |
| Proyecto | Proyecto de la remisión | No |
| Cantidad a Devolver | Piezas a devolver; solo números | Sí, al modificar una clave |
| Observaciones | Motivo de la devolución | Sí |

**Mensajes y errores frecuentes:**
- "Cantidad a Devolver debe ser Mayor a Cero": captura una cantidad mayor que cero.
- "Cantidad a Devolver es Mayor a lo Surtido": la cantidad no puede pasar de lo entregado.
- "Ingrese Datos": falta capturar la cantidad.
- "Favor de llenar campo de observaciones": captura las **Observaciones**.
- "¿Seguro que deseas aplicar la Devolución?" con el texto "No podrás deshacer este paso...": confirma con **Adelante!** solo si estás seguro.
- "Devolución aplicado correctamente!": la devolución quedó registrada.
- "Datos no Registrados no remisionado" o "Registro no eliminado": no se pudo completar; revisa los datos o avisa a sistemas.

**Notas:** Una vez aplicada, la devolución no se puede deshacer. Para perfiles con ubicación de devolución (no es el caso de los perfiles de este módulo), la pantalla pide además la **Ubicación destino**. Las devoluciones ya hechas se consultan en **Administrar Devoluciones** (módulo PDF).

## Facturación Automática

**Para qué sirve:** Generar remisiones (folios) de una unidad a partir de su requerimiento, de forma automática.

**Quién la ve:** Perfiles 100, 1, 2, 3 y 5.

**Cómo llegar:** Menú › Remisión › Facturación Automática.

**Paso a paso:**
1. Captura o elige la **Clave Unidad**.
2. Da clic en **Consultar**.
3. Revisa el requerimiento mostrado. Si una clave no debe surtirse, quítala con el botón de eliminar del renglón.
4. Da clic en **Generar Folio(s)** para crear las remisiones.
5. Si necesitas anular lo generado, da clic en **Cancelar Folio(s)**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Clave Unidad | Clave de la unidad a surtir | Sí |
| Catálogo | Catálogo de insumos a surtir | Sí |

**Mensajes y errores frecuentes:**
- "Favor de Seleccionar Unidad Activa": elige una unidad que esté activa.
- "Favor de Seleccionar Catálogo": elige el catálogo.

**Notas:** La pantalla muestra el proyecto con el que estás trabajando.

## Facturación Manual

**Para qué sirve:** Armar una remisión a mano, clave por clave y lote por lote.

**Quién la ve:** Perfiles 100, 1, 2, 3 y 5.

**Cómo llegar:** Menú › Remisión › Facturación Manual.

**Paso a paso:**
1. Da clic en **Nuevo Folio**.
2. Captura la **Unidad** (al escribir se sugieren unidades) y elige la **Fecha de entrega**. El **Proyecto** se muestra solo para consulta.
3. Captura la clave en **Ingrese CLAVE** y da clic en **Buscar**.
4. Captura la **Cantidad a Facturar** y da clic en **Seleccionar** para elegir el lote.
5. Repite con cada clave. Para quitar una clave, da clic en el botón de eliminar del renglón y confirma.
6. Captura las **Observaciones**, el número de **OC** (si lo tienes) y elige el **Tipo** (Ordinario, Complemento, Apoyo, Urgente, etcétera).
7. Da clic en **Confirmar Factura** para terminar, o en **Cancelar Factura** para anularla.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Unidad | Unidad a la que se surte | Sí |
| Fecha de entrega | Fecha en que se entrega | Sí |
| Clave | Clave del insumo | Sí |
| Cantidad a Facturar | Piezas a surtir; solo números | Sí |
| Observaciones | Comentarios de la remisión | Sí |
| OC | Orden de compra o de suministro | No |
| Tipo | Tipo de remisión | Sí (trae **Ordinario** de inicio) |

**Mensajes y errores frecuentes:**
- "Favor de Capturar Toda la información": faltan datos.
- "Escriba una cantidad", "Escriba una Clave": captura el dato.
- "Seleccione Unidad", "Seleccione Fecha de Entrega": elige el dato.
- "Capture Unidad válida": la unidad no existe; elígela de la lista de sugerencias.
- "Insumo sin Existencias": la clave no tiene existencia disponible.
- "Seguro que desea eliminar esta clave?" / "Seguro de CANCELAR la Factura?": confirma solo si estás seguro.
- "Favor de llenar campo de observaciones": captura las **Observaciones** antes de confirmar.
- "¿Seguro de confirmar la Factura?" con el texto "No podrás deshacer este paso...": confirma con **Continuar!**.
- "Facturación Realizado correctamente!": la remisión quedó generada.
- "Error al Generar la remisión, Contacte con el área de Sistemas": repórtalo.

**Notas:** Nada queda registrado hasta que das clic en **Confirmar Factura**. La fecha de entrega no puede ser anterior a hoy. Esta pantalla no tiene campo de diagnóstico.

## Modificar Folio

**Para qué sirve:** Cambiar los datos de una remisión ya generada o agregar claves a un folio.

**Quién la ve:** Perfiles 100, 1, 2, 3 y 5.

**Cómo llegar:** Menú › Remisión › Modificar Folio.

**Paso a paso:**
1. Captura en **Folios** el número de la remisión y elige el **Proyecto**.
2. Da clic en **MOSTRAR** para ver las claves del folio, o en **UNIDAD** para cambiar la unidad a la que se surte.
3. Revisa **Documento**, **Unidad** y **Entrega**, y la tabla de claves.
4. Para cambiar el lote o la caducidad de una clave, da clic en su lápiz de la columna **Modificar**, captura el **Lote** y la **Caducidad** nuevos y da clic en **Guardar**. **Cancelar** cierra la ventana sin cambios.
5. Para quitar una clave del folio, da clic en el bote de basura.
6. Para añadir una clave, da clic en **Agregar**.
7. Si diste clic en **UNIDAD**, elige la **Unidad nueva** y da clic en **Guardar**; **Regresar** vuelve sin cambios.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Folios | Número del folio (uno solo) | Sí |
| Proyecto | Proyecto de la remisión | Sí |
| Lote y Caducidad (al modificar) | Datos nuevos de la clave | Sí |
| Unidad nueva (al cambiar unidad) | Unidad a la que pasa el folio | Sí |

**Mensajes y errores frecuentes:**
- "No Existe el Folio o Proyecto": revisa el número de folio y el proyecto elegido.
- "Datos Iguales agregar lote o caducidad diferentes": cambia el lote o la caducidad; no puedes guardar los mismos.
- "Ingrese Datos": falta capturar el lote o la caducidad.
- "Unidad cambiada existosamente": el cambio de unidad se guardó.
- "Registro no eliminado": no se pudo quitar la clave; vuelve a intentarlo o avisa a sistemas.

**Notas:** Para reimprimir la remisión usa **Administrar Remisiones** (módulo PDF).
