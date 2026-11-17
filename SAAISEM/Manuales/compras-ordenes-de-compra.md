# Compras › Órdenes de Compra

## Crear OC

**Para qué sirve:** Crear una orden de compra a un proveedor, capturando las claves, cantidades y precios.

**Quién la ve:** Perfiles 13 y 14. La pantalla es la misma para ambos.

**Cómo llegar:** Menú › Órdenes de Compra › Crear OC.

**Paso a paso:**
1. En **Selecciona Proyecto** da clic en el botón del proyecto para el que vas a comprar. Solo aparecen los proyectos asignados a tu usuario.
2. En **Captura OC Proyecto**, escribe el **Número de Orden de Compra** (si el proyecto tiene numeración automática, ya viene escrito).
3. Elige el **Proveedor**, el **Lugar de Entrega**, la **Fecha de Entrega**, la **Hora de Entrega**, la **Fecha de Vencimiento**, la **Moneda**, el **CFDI** y captura la **Condición de pago**.
4. Da clic en **Validar OC**. El sistema avisa si la orden es nueva ("OC Nueva") o si ya existe ("Ya existe OC"). Si es nueva, los datos generales quedan bloqueados; para empezar de nuevo da clic en **Limpiar NO OC**.
5. Elige la **Clave** en la lista y da clic en **Clave**. La pantalla muestra la descripción, la **Cantidad Enviada**, la **Cantidad Máxima**, la **Cantidad Restante**, la **Presentación** y las **Exist. en Almacén**.
6. Elige el **No. de Entrega** (si aplica), captura las **Pzs a Entregar**, el **Precio**, el **Descuento %** y el **IVA %**. El sistema calcula **Subtotal**, **Monto Descuento**, **Monto IVA** y **Total**.
7. Da clic en **Capturar**. La clave aparece en la lista de la orden (Clave, Descripción, Fecha, Hora, Cantidad, Precio, Descuento, IVA). Para quitar una clave da clic en la **X** de su renglón.
8. Repite los pasos 5 a 7 con cada clave.
9. Si lo necesitas, escribe **Observaciones** (hasta 150 caracteres).
10. Da clic en **Confirmar Orden de Compra** y acepta el aviso. Para anular todo lo capturado da clic en **Cancelar**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Número de Orden de Compra | Número de la orden | Sí |
| Proveedor | Proveedor de la orden | Sí |
| Lugar de Entrega | Punto de entrega | Sí |
| Fecha de Entrega | Día de entrega | Sí |
| Hora de Entrega | Hora de entrega (cada 30 minutos, desde las 7:00) | Sí |
| Fecha de Vencimiento | Fecha de vencimiento; debe ser posterior a la de entrega | Sí |
| Moneda | Tipo de moneda | Sí |
| CFDI | Uso de CFDI | Sí |
| Condición de pago | Días de pago | Sí |
| Clave | Insumo a comprar | Sí |
| No. de Entrega | Número de entrega | No |
| Pzs a Entregar | Piezas, mínimo 1 | Sí |
| Precio | Precio unitario | Sí |
| Descuento % | Porcentaje de descuento (inicia en 0) | No |
| IVA % | Porcentaje de IVA (inicia en 0) | No |
| Observaciones | Comentarios de la orden | No |

**Mensajes y errores frecuentes:**
- "Complete los datos de la captura !": falta algún dato general al dar **Validar OC**.
- "La fecha de vencimiento es menor a la fecha de entrega desea cambiarla?.": corrige las fechas; la de vencimiento debe ser posterior a la de entrega.
- "OC Nueva: …": el número de orden está libre; puedes continuar.
- "Ya existe OC: … Repetida": ese número ya se usó; usa otro.
- "Faltan campos por ingresar": faltan datos generales; complétalos y vuelve a validar.
- "Complete los datos de capturar de cantidades ": falta la clave, las piezas o el precio.
- "El numero de piezas como minimo es 1": captura al menos una pieza.
- "La Cantidad Solicitada no puede ser mayor a la Cantidad Restante": reduce las piezas.
- "¿Seguro que desea CONFIRMAR el pedido?" y "¿Seguro que desea CANCELAR el pedido?": confirma solo si estás seguro.

**Notas:**
- La orden no queda confirmada hasta que das clic en **Confirmar Orden de Compra**.
- Con **Cancelar** se borran las claves capturadas en tu orden que aún no se han confirmado.
- **Cantidad Máxima** es lo autorizado para esa clave con ese proveedor; **Cantidad Enviada** es lo ya pedido en otras órdenes no canceladas, y **Cantidad Restante** es la diferencia. No puedes pedir más que lo restante. **No. de Entrega** se elige de un catálogo de prioridades del sistema.

## Ver OC

**Para qué sirve:** Consultar las órdenes de compra, cancelarlas o cambiar su fecha y hora de entrega.

**Quién la ve:** Perfiles 13 y 14. La pantalla es la misma para ambos.

**Cómo llegar:** Menú › Órdenes de Compra › Ver OC.

**Paso a paso:**
1. Filtra por **Proveedor**, **Fecha de Entrega** o **Usuario** (quien capturó la orden) y da clic en **Buscar**. Si llenas más de un filtro, el sistema usa primero el proveedor, luego la fecha y al final el usuario.
2. En **Órdenes de Compra** (No. Orden, Capturó, Proveedor, Fecha Entrega, Hora entrega) da clic en la lupa de la orden que necesitas.
3. Revisa el detalle: proveedor, fecha y hora de entrega, observaciones y las claves con su descripción y cantidad.
4. Para anularla, escribe las **Observaciones para cancelar** y da clic en **CANCELAR ORDEN DE COMPRA**.
5. Para reprogramarla, da clic en **CAMBIO DE FECHA Y HORA ENTREGA**, elige la nueva fecha y hora y da clic en **Confirmar** (o en **Cerrar** para salir).

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Proveedor | Proveedor de la orden | No |
| Fecha de Entrega | Día de entrega | No |
| Usuario | Persona que capturó | No |
| Observaciones para cancelar | Motivo de la cancelación | Sí, al cancelar |
| Nueva fecha y hora de entrega | Al reprogramar | Sí |

**Mensajes y errores frecuentes:**
- "¿Seguro que desea cancelar la orden? ": confirma solo si estás seguro.
- "Favor de llenar el campo de observaciones": escribe el motivo antes de cancelar.
- "¿Seguro que desea cambiar la fecha y hora?": confirma solo si estás seguro.
- "Actualización correcta": la nueva fecha y hora quedó guardada.
- "Error al actualizar": no se guardó el cambio; inténtalo de nuevo.

**Notas:**
- Los botones de cancelar y cambiar fecha solo aparecen en las órdenes que tú capturaste, que no están canceladas y que no tienen piezas recibidas.
- Al cancelar una orden el sistema avisa por correo de la cancelación.
