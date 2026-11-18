# Auditoría › Órdenes de Compra

## Ver OC

**Para qué sirve:** Consultar las órdenes de compra para auditoría.

**Quién la ve:** Perfiles 100, 1 y 28.

**Cómo llegar:** Menú › Órdenes de Compra › Ver OC (se abre en una ventana aparte).

**Paso a paso:**

1. Entra a **Ver OC** desde el menú; se abre en una ventana aparte.
2. Elige un filtro: **Proveedor**, **Fecha de Entrega** o **Usuario**.
3. Da clic en **Buscar**. En el panel **Órdenes de Compra** aparecen las órdenes con las columnas No. Orden, Capturó, Proveedor, Fecha Entrega, Hora entrega y Ver.
4. En la orden que te interese, da clic en el botón de lupa de la columna **Ver**.
5. A la derecha se muestra el detalle: Orden No., Capturó, Proveedor, Fecha de Captura, Fecha de Entrega, Zona y Tipo OC. Abajo aparece la tabla con Clave, Descripción, Evento, Cantidad, Recibido y Pendiente.
6. Para imprimir la orden, da clic en el botón de la impresora junto al número de orden; se abre en otra pestaña.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| **Proveedor** | Elige un proveedor que tenga órdenes de compra. | No (usa al menos un filtro) |
| **Fecha de Entrega** | Fecha de entrega de la orden. | No (usa al menos un filtro) |
| **Usuario** | Elige quién capturó la orden. | No (usa al menos un filtro) |

**Mensajes y errores frecuentes:**

- Si no eliges ningún filtro y das clic en **Buscar**, la lista queda vacía. Elige al menos uno.
- Si usas varios filtros a la vez, la búsqueda toma uno solo, en este orden de prioridad: **Proveedor**, luego **Fecha de Entrega** y luego **Usuario**.
- Una orden cancelada muestra el aviso **FOLIO CANCELADO** y el texto con las observaciones de la cancelación.
- Las órdenes canceladas también aparecen en la lista; al abrirlas verás el aviso de cancelación.

**Notas:**

- El acceso de Auditoría tiene su propio inicio de sesión.
- Los botones para agregar, modificar o quitar claves de una orden solo aparecen para el perfil 13, no para Auditoría.
- La pantalla del detalle también ofrece **CANCELAR ORDEN DE COMPRA**, **CAMBIO DE FECHA Y HORA ENTREGA** y **ABRIR ORDEN DE COMPRA**, que modifican la orden.

> Pendiente de confirmar: el archivo de esta pantalla (verFoliosMedalfaAud.jsp) solo existe en la carpeta de respaldos del sistema y no en la raíz de la aplicación, que es donde apunta el menú. No se puede saber leyendo el código si hoy abre correctamente para el perfil 28, ni si ese perfil debe poder cancelar, abrir o reprogramar órdenes. Además, al terminar esas acciones el sistema regresa a la pantalla de Ver OC de Almacén y no a la de Auditoría.
