# Almacén › Entrada

Opciones para registrar la mercancía que llega al almacén y para cerrar órdenes de compra.

## Cerrar OC Manual

**Para qué sirve:** Cerrar una orden de compra que ya no recibirá más mercancía.

**Quién la ve:** Perfiles 100, 1, 2, 3, 5 y 6.

**Cómo llegar:** Menú › Entrada › Cerrar OC Manual.

**Paso a paso:**
1. Abre la opción.
2. Localiza la orden en **Ordenes de compra** (elige el número y da clic en la lupa) o en **Proveedor** (elige el proveedor y da clic en la lupa). Las órdenes aparecen en la tabla con **No. Ori**, **Fecha** y **Proveedor**.
3. Da clic en el botón rojo de la columna **Cerrar** del renglón.
4. En el aviso "Cerrar orden de compra", da clic en **Aceptar**. Al terminar aparece "Proceso exitoso".

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Ordenes de compra | Número de la orden por cerrar | Una de las dos búsquedas |
| Proveedor | Proveedor, para ver sus órdenes abiertas | Una de las dos búsquedas |

Abajo, la tabla **Ordenes cerradas** muestra las órdenes ya cerradas con **No. Ori**, **Fecha** y **Usuario** que las cerró.

**Mensajes y errores frecuentes:**
- "¿Estas seguro de cerrar esta orden de compra? Esta acción no se puede revertir": confirma solo si ya no recibirás más mercancía de esa orden.
- "Proceso exitoso – Se cerró la orden correctamente": la orden quedó cerrada.
- "Error al cerrar la orden": la orden no se cerró; avisa al departamento de sistemas.
- "Error Contactar al departamento de sistemas": falló la consulta; avisa a sistemas.

**Notas:** Cerrar una orden no se puede revertir desde esta pantalla. La pantalla solo lista las órdenes del proyecto 1 que aún no están cerradas. Para cerrar o reactivar órdenes también puedes usar **Órdenes de Compras** (módulo Consulta).

## Entrada Automática ORI

**Para qué sirve:** Registrar la recepción de una orden de compra (ORI) clave por clave y lote por lote, y después ingresarla al inventario.

**Quién la ve:** Perfiles 100, 1, 2, 3 y 5.

**Cómo llegar:** Menú › Entrada › Entrada Automática ORI.

**Paso a paso:**
1. Da clic en **Buscar** para localizar la orden de compra.
2. Elige la clave que vas a recibir (botón **CLAVE**).
3. Captura el lote, la caducidad, la marca, el código de barras, el folio de remisión, la orden de suministro y el costo.
4. Captura el empaque: tarimas, cajas por tarima, piezas por caja, cajas incompletas y resto. El sistema calcula cajas y piezas.
5. Da clic en **Guardar Lote**.
6. Repite para cada lote. Revisa la lista de lo capturado (Remisión, Clave, Lote, Caducidad, Cantidad, Costo U., IVA, Importe).
7. Da clic en **Ingresar OC** para pasar la compra a verificación, o en **Cancelar Compra** para borrarla.
8. Si no vas a aceptar la entrega, da clic en **Rechazar OC**, captura las observaciones, la nueva fecha y hora de recepción y el correo del proveedor.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Folio de remisión | Folio de la remisión del proveedor | Sí |
| Código de barras | Código de barras del producto | Sí |
| Lote | Lote del producto | Sí |
| Caducidad | Fecha de caducidad | Sí |
| Marca / Nombre comercial | Marca del producto | Sí |
| Costo | Costo unitario | Sí |
| Orden de suministro | Número de la orden de suministro | Sí |
| Carta canje | Dato de carta canje, cuando aplica | Según el caso |
| Proyecto y unidad | Proyecto y unidad de destino | Sí |
| Tarimas, Cajas x Tarima, Piezas x Caja, Resto, Factor de Empaque | Datos de empaque, con confirmación | Sí |

**Mensajes y errores frecuentes:**
- "Falta Caducidad", "Caducidad Incorrecta", "Verifique la caducidad": revisa la fecha de caducidad.
- "Falta Orden de Suministro" o "No coinciden los datos de la orden de suministro": captura o corrige la orden de suministro.
- "Falta seleccionar unidad", "Seleccione Proyecto": elige el dato faltante.
- "Falta Folio de Remisión", "Falta Código de Barras", "Falta Lote", "Falta Marca", "Falta Costo": captura el dato.
- "Ingresar Carta Canje": captura la carta canje.
- "Excede la cantidad a recibir, favor de verificar": la cantidad es mayor a la pendiente de la orden.
- "Seleccionar nombre comercial": elige el nombre comercial.
- "Favor de llenar todos los datos": hay campos vacíos.
- "Ingrese las observaciones del rechazo", "Ingrese nueva fecha de recepción", "Ingrese nueva hora de recepción", "Ingrese correo de proveedor": completa los datos al rechazar.

**Notas:** Después de **Ingresar OC**, valida la remisión en **Verificar OC (ORI)**. En esta pantalla las pestañas de peso y dimensiones (volumetría) existen pero están desactivadas, por lo que no se captura peso ni medidas aquí.

## Entrada Manual

**Para qué sirve:** Capturar una entrada de insumo sin orden automática, con sus datos de remisión, lote y caducidad.

**Quién la ve:** Perfiles 100, 1, 2, 3 y 5.

**Cómo llegar:** Menú › Entrada › Entrada Manual.

**Paso a paso:**
1. Captura el **Folio Remisión**, la **Orden de Compra** y el **Proveedor**.
2. Captura **Recibido por**.
3. Busca el producto por **C.B**, **CLAVE** o **Descripción**.
4. Captura **Código de Barras** (y su confirmación), **Lote** y **Caducidad** (día, mes, año; y su confirmación).
5. Captura el empaque (Tarimas, Cajas x Tarima, Piezas x Caja, Resto, Factor de Empaque) y confírmalo.
6. Escribe **Observaciones** si hace falta y guarda la captura.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| No. Folio MEDALFA, Fecha, Hora | Los pone el sistema | No |
| Folio Remisión | Folio de la remisión | Sí |
| Orden de Compra | Número de la orden | Sí |
| Proveedor | Proveedor que entrega | Sí |
| Recibido por | Quién recibe | Sí |
| Clave / Descripción / Marca | Producto recibido | Sí |
| Código de Barras y Confirmar Código de Barras | Mismo código dos veces | Sí |
| Lote | Lote del producto | Sí |
| Caducidad y Confirmar Caducidad | Día, mes y año, dos veces | Sí |
| Tarimas, Cajas x Tarima, Piezas x Caja, Resto, Factor de Empaque | Empaque, con confirmación | Sí |
| Observaciones | Comentarios | No |

**Mensajes y errores frecuentes:**
- "Tiene campos vacíos, verifique.": completa los campos faltantes.
- "DIA Fuera de Rango", "MES Fuera de Rango", "Año Fuera de Rango": corrige la fecha.
- "La fecha de caducidad no puede ser menor a 9 meses próximos": el producto debe caducar dentro de más de 9 meses.
- "Fecha Caduca": la fecha ya venció.
- "Falta Orden de Suministro" / "No coinciden los datos de la orden de suministro": revisa la orden de suministro.
- "Falta seleccionar unidad": elige la unidad.

**Notas:** Los datos de confirmación (código de barras, caducidad, empaque) deben coincidir con los primeros.

## Entrada de Equipos

**Para qué sirve:** Registrar la entrada de equipos al almacén, de una pieza con su serie o de varias piezas como lote. La cantidad sale del empaque (tarimas, cajas y piezas), igual que en la Entrada Manual.

**Quién la ve:** Perfiles 100, 1, 2, 3 y 5.

**Cómo llegar:** Menú › Entrada › Entrada de Equipos.

**Paso a paso:**
1. Busca el equipo con **Buscar por clave o descripción**.
2. Captura **Marca**, **Modelo**, **Categoría**, **Condición** y **No. de serie** (con varias piezas el campo cambia a **Lote**).
3. Captura el **Código de barras** o da clic en **Generar CB**.
4. Captura costo, **No. de tarima**, fecha de compra, garantía, accesorios y notas.
5. En **Empaque**, captura las tarimas completas, las incompletas y el resto. Si lo dejas vacío, entra una pieza. Revisa el **Total de la entrada** (Tarimas, Cajas y Piezas).
6. Elige **Proyecto**, **Origen**, **Proveedor**, **Ubicación** (lista de ubicaciones de ingreso; cambia según el proyecto) y escribe la **Orden de compra** y la **Remisión o factura**.
7. Da clic en **Guardar entrada**. Para empezar de nuevo, da clic en **Limpiar equipo**.
8. Al guardar aparecen los botones de formatos de la entrada: **Acuse de entrada**, **Marbetes tarimas completas**, **Marbetes todas las tarimas** y **Marbetes de cajas**. Se abren en una ventana aparte.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Buscar por clave o descripción | Escribe parte de la clave o la descripción y elige el equipo de la lista de resultados | Sí |
| Marca | Elige la marca; si no está, elige "Otra (escribir)" y captura el nombre de la marca nueva | Sí |
| Modelo | Elige el modelo; si no está, elige "Otra (escribir)" y captura el modelo nuevo (hasta 80 caracteres) | No |
| Categoría | Texto libre, con sugerencias de las ya usadas (hasta 80 caracteres) | No |
| Condición | Nuevo, Usado, Reacondicionado o Dañado | No |
| No. de serie / Lote | Con una pieza se llama **No. de serie**; con varias, **Lote** (hasta 30 caracteres). Si lo dejas vacío, se usa el código de barras | No |
| Código de barras | Código del equipo, o da clic en **Generar CB** para que el sistema lo genere. Si lo dejas vacío, se genera uno al guardar | No |
| Costo | Costo del equipo, número sin signo negativo | No |
| No. de tarima | Tarima donde se coloca (hasta 100 caracteres) | No |
| Fecha de compra | Fecha de compra; no puede ser futura | No |
| Garantía hasta | Fecha en que termina la garantía; no puede ser anterior a la fecha de compra | No |
| Accesorios | Accesorios que incluye (hasta 500 caracteres) | No |
| Notas | Comentarios (hasta 1,000 caracteres) | No |
| Proyecto | Proyecto de la entrada | Sí |
| Origen | Origen del equipo | Sí |
| Proveedor | Proveedor que entrega | Sí |
| Ubicación | Elige una ubicación de ingreso de la lista. Salen las generales y las del **Proyecto** elegido; al cambiar el proyecto la lista se actualiza | Sí |
| Tarimas (completas) | Número de tarimas completas | No |
| Cajas por tarima | Cajas que trae cada tarima completa | Solo si hay tarimas completas |
| Piezas por caja | Piezas que trae cada caja | Solo si hay cajas |
| Tarimas (incompletas) | Número de tarimas incompletas | No |
| Cajas (total de las incompletas) | Cajas de todas las tarimas incompletas | No |
| Resto (piezas sueltas) | Piezas sueltas | No |
| Orden de compra | Número de la orden (hasta 40 caracteres). Si lo dejas vacío se usa "EQUIPOS" | No |
| Remisión o factura | Folio del documento del proveedor (hasta 40 caracteres). Si lo dejas vacío se usa "S/R" | No |

Los campos de empaque aceptan números enteros de 0 a 9,999,999. El **Total de la entrada** se calcula solo: Cajas = tarimas completas × cajas por tarima + cajas de las incompletas; Piezas = cajas × piezas por caja + resto.

**Mensajes y errores frecuentes:**
- "Indique cuántas cajas trae cada tarima completa.": captura **Cajas por tarima**.
- "Indique cuántas piezas trae cada caja.": captura **Piezas por caja**.
- "Indique las cajas o el resto de las tarimas incompletas.": captura **Cajas** o **Resto** de las incompletas.
- "La entrada debe tener al menos una pieza.": revisa el empaque.
- "La entrada pasa de 10,000,000 de piezas: revise tarimas, cajas y piezas.": corrige el empaque.
- "En … escriba un número entero de 0 a 9,999,999.": corrige el campo de empaque que menciona.
- "Falta: el equipo, la marca, proyecto, origen, proveedor, ubicación.": completa los campos que menciona.
- "Busque y elija el equipo (clave o descripción).": elige el equipo de la lista de resultados; no basta con escribir.
- "Elija la marca o escriba una nueva.": elige una marca o captura la nueva.
- "Elija proyecto, origen y proveedor.": completa los tres.
- "Elija la ubicación de ingreso.": elige una ubicación de la lista.
- "La ubicación … no es una ubicación de ingreso activa" o "… es del proyecto …, no del proyecto elegido": elige una ubicación general o del mismo proyecto.
- "El costo no es un número." / "El costo no puede ser negativo.": corrige el costo.
- "La fecha de compra no puede ser futura." / "La garantía no puede terminar antes de la fecha de compra.": corrige las fechas.
- "La serie … ya tiene existencia con la clave …": ese equipo ya está en el almacén; no se puede repetir la serie.
- "El código de barras … ya está en la clave …": el código ya lo usa otro lote; captura otro o da clic en **Generar CB**.
- "Código generado: …": se llenó el campo con el código nuevo.
- "Entrada guardada: … pieza, serie … / … piezas, lote …, CB …, folio de lote …, documento … (OC …, remisión …)": la entrada quedó registrada y aparecen los botones de formatos.
- "Su sesión terminó o no es de Almacén. Vuelva a iniciar sesión.": entra de nuevo al sistema.
- Si el guardado falla, el mensaje explica qué dato falló (por ejemplo, "Un dato es más largo de lo que admite el sistema: el lote o número de serie (hasta 30 caracteres)"), indica que no se guardó ningún cambio y da una referencia para avisar a sistemas si el problema sigue.

**Notas:** Con una sola pieza no se permite repetir la serie de un equipo que ya tiene existencia; con varias piezas se captura un lote y esa validación de serie no aplica. El código de barras no se puede repetir. Los formatos son los mismos de **Reimpresión de compras**. Abajo de la pantalla aparece la tabla **Últimas entradas de equipos** (Fecha, Clave, Equipo, Serie, CB, Marca, Modelo, Categoría, Condición, Costo, Piezas, Ubicación, Folio y Formatos). En **Formatos** de cada renglón están los vínculos **Acuse**, **Tarimas C**, **Tarimas A** y **Cajas**; si el renglón es una entrada anterior sin compra, dice "Sin compra".

## Verificar OC (ORI)

**Para qué sirve:** Validar la recepción de una orden de compra antes de que quede en el inventario.

**Quién la ve:** Perfiles 100, 1, 2, 3 y 5.

**Cómo llegar:** Menú › Entrada › Verificar OC (ORI).

**Paso a paso:**
1. En **Elegir Remisión** selecciona la remisión (se muestra proveedor, orden de compra, remisión y origen). Aparecen el folio, la remisión, el proveedor y la fecha y hora de entrega.
2. Revisa la tabla **Validación Recibo** (Remisión, Clave, Descripción, Origen, Lote, Cantidad, Costo U, IVA, Importe, Caducidad, Marca, Nombre Comercial). Si necesitas el detalle en archivo, da clic en **Exportar**.
3. Si hay un error, da clic en **Editar** en el renglón, corrige los datos en la ventana y da clic en **Guardar** (o en **Cancelar** para salir sin cambios).
4. En **Elegir Ubicacion** selecciona la ubicación donde se guardará la mercancía.
5. Da clic en el botón de palomita (**Validar**) del renglón para validar solo ese renglón, o en **Confirmar Remisión** para validar toda la remisión. Confirma en el aviso que aparece.
6. Si la remisión no procede, da clic en **Cancelar Remisión** y confirma.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Elegir Remisión | Remisión por verificar | Sí |
| Elegir Ubicacion | Ubicación de destino de la mercancía | Sí, para validar |
| Lote | Lote corregido | Sí |
| Caducidad | Fecha de caducidad | Sí |
| Código de Barras | Código de barras del producto | Sí |
| Orden de Suministro | Orden de suministro; solo se habilita en los orígenes que la piden | Según el origen |
| Carta Canje | Solo se habilita en insumos controlados con caducidad menor a un año | Solo en ese caso |
| Marca y Nombre Comercial | Marca del producto y su nombre comercial | Marca sí; nombre comercial según el producto |
| Unidad | Unidad destino; solo se habilita en el origen que la pide | Según el origen |
| Cantidad y Costo U | Piezas y costo unitario | Cantidad sí (mayor a 0) |
| Tarimas, Cajas x Tarima, Piezas x Caja, Cajas x Tarima Incompleta, Resto, Factor de Empaque | Empaque | Sí |

La ventana también muestra las pestañas de peso y dimensiones solo cuando el renglón ya trae esos datos.

**Mensajes y errores frecuentes:**
- "Seleccionar Una Ubicacion Valida": elige una ubicación antes de validar.
- "Excede la cantidad a recibir, Verificar cantidad": las piezas capturadas rebasan lo pendiente de la orden.
- "Ingresar Carta Canje": el insumo controlado caduca en menos de un año y falta la carta canje.
- "Ingresar un lote válido por favor.", "ingresar una caducidad válida por favor.", "ingresar una cantidad válida por favor.", "ingresar una cb válido por favor.", "ingresar una marca válida por favor.": completa el dato indicado.
- "ingresar Cantidad en Tarima válida por favor." y avisos parecidos para Cajas, Piezas x Cajas, Cajas x Tarimas Incompletas, Resto y Factor de Empaque: captura el dato de empaque que falta.
- "Modificación realizada con éxito": el renglón se guardó.
- "¿Seguro de Validar la Compra?": avisa que no se puede deshacer; confirma con **Continuar!** solo si la ubicación es la correcta. Al terminar aparece "Compra validada correctamente!".
- "Seguro que desea eliminar la compra?": confirma solo si quieres cancelar la remisión.
- "Error en sistema", "Error en la modificación contactar al departamento de sistemas." o "Error en la Validación contactar al departamento de sistemas.": avisa al departamento de sistemas.

**Notas:** Un renglón sin validar no entra al inventario. La validación no se puede deshacer. Los perfiles de Compras (13 y 14) solo ven sus propias remisiones y las ubicaciones de su unidad.
