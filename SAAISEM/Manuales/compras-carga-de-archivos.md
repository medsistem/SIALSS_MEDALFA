# Compras › Carga de Archivos

## Carga de OC

**Para qué sirve:** Cargar órdenes de compra desde un Excel. Es la misma pantalla que en [Carga de OC de Almacén](almacen-carga-de-archivos.md#carga-de-oc).

**Quién la ve:** Perfil 13.

**Cómo llegar:** Menú › Carga de Archivos › Carga de OC.

**Paso a paso:**
1. Da clic en **Descargar plantilla**. Trae las columnas, un ejemplo y los catálogos de proveedores y proyectos.
2. Llena tu Excel con un renglón por orden y clave, y elígelo en **Excel (.xlsx o .xls)**.
3. Da clic en **Revisar archivo**. Consulta la **Vista previa** (primeros 200 renglones válidos) y corrige tu Excel si aparecen errores.
4. Escribe tu contraseña en **2. Para confirmar, escribe tu contraseña**.
5. Da clic en **Confirmar carga**. Si ya no quieres seguir, da clic en **Cancelar carga**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Excel (.xlsx o .xls) | Archivo con las órdenes de compra | Sí |
| Contraseña | Tu contraseña, para confirmar | Sí |

Columnas del Excel:

| Columna | Qué capturar | Obligatorio |
|---|---|---|
| Orden de compra | Número de la orden (máximo 50 caracteres) | Sí |
| Proveedor | Número del proveedor, como aparece en la hoja de catálogos | Sí |
| Nombre del proveedor | Solo de referencia | No |
| Clave | Clave del insumo; debe existir y no estar suspendida | Sí |
| Cantidad | Piezas pedidas | Sí |
| Proyecto | Número del proyecto, como aparece en la hoja de catálogos | Sí |
| Fuente de financiamiento | Fuente (máximo 50 caracteres); si la dejas vacía se guarda como "Pendiente" | No |

**Mensajes y errores frecuentes:**
- "Renglón N: falta la orden de compra; el proveedor X no existe; la clave X no existe…": corrige esos renglones en tu Excel y vuelve a subirlo. Si un renglón tiene error no se carga nada.
- "la clave X está suspendida": usa otra clave o pide que la reactiven.
- "la orden X (proveedor Y, proyecto Z) ya tiene piezas recibidas: no se puede reemplazar por carga": esa orden ya recibió mercancía; no se puede volver a cargar.
- "Su usuario no está dado de alta en los usuarios de compras…": pide al administrador que te dé de alta.
- "Elige el archivo de Excel." y "Escribe tu contraseña para confirmar.": faltó el archivo o la contraseña.
- "Contraseña incorrecta. Te quedan N intentos.": al tercer error se cancela la carga y debes volver a subir el archivo.
- "No hay una revisión pendiente: vuelva a subir el archivo.": vuelve a dar clic en **Revisar archivo**.

**Notas:**
- La revisión no guarda nada; solo **Confirmar carga** aplica la carga, y se guarda a tu nombre.
- Si una orden ya estaba cargada para ese proveedor y proyecto (y sin piezas recibidas), se reemplaza. Las filas repetidas de la misma clave se suman.
- Las órdenes quedan confirmadas, con entrega a 7 días.
- Diferencia con Almacén: ninguna en pantalla; en Compras tu usuario ya es el usuario de compras que se registra en la orden.
