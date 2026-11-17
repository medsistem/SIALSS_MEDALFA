# Compras › Inventario

## Captura Movimiento

**Para qué sirve:** Registrar un movimiento al inventario.

**Quién la ve:** Perfil 14.

**Cómo llegar:** Menú › Inventario › Captura Movimiento.

**Paso a paso:** Igual que en [Captura Movimiento de Almacén](almacen-inventario.md#captura-movimiento).

**Campos:** Los mismos que en Almacén.

**Mensajes y errores frecuentes:** Los mismos que en Almacén.

**Notas:** Es la misma pantalla que en Almacén, con el menú de Compras. Diferencia por perfil: en la lista de conceptos del movimiento tu perfil (14) solo ve el concepto 71; en Almacén se ven todos los conceptos. Pendiente de confirmar: el nombre del concepto 71 (está en un catálogo de la base de datos, no en el código).

## Existencias

**Para qué sirve:** Consultar las existencias por proyecto. Son dos pantallas distintas con el mismo nombre en el menú, según tu perfil.

**Quién la ve:** Perfil 13 (consulta con varias vistas) y perfil 14 (consulta por proyecto con lotes).

**Cómo llegar:** Menú › Inventario › Existencias.

**Paso a paso (perfil 13):**
1. Elige el **Seleccione Proyecto**.
2. Elige el tipo de **Consulta**: **Global**, **Lote**, **Nueva** o **Disponibilidad**. La tabla se muestra en cuanto eliges proyecto y consulta.
3. Revisa la tabla y, abajo, **No. Claves** y **Total**. Puedes buscar y ordenar con los controles de la tabla.
4. Da clic en **Descargar** para obtener el archivo con la misma consulta.

**Paso a paso (perfil 14):**
1. Elige el **Seleccione Proyecto**; la lista trae solo los proyectos que tienes asignados.
2. La tabla muestra de inmediato Clave, Descripción, Lote, Caducidad, Cantidad y Origen.
3. Revisa **Total** y, si lo necesitas, da clic en **Descargar**.

**Campos:**

| Campo | Qué capturar | Obligatorio |
|---|---|---|
| Seleccione Proyecto | Proyecto a consultar | Sí (*) |
| Consulta (solo perfil 13) | Global, Lote, Nueva o Disponibilidad | Sí (*) |

**Mensajes y errores frecuentes:**
- "Selecciona un proyecto": lo ve el perfil 14 al dar **Descargar** sin elegir proyecto.
- "Error grave contactar con el departamento de sistemas": falló la consulta; avisa a sistemas.
- Si no eliges proyecto y consulta (perfil 13) la tabla no se muestra.

**Notas:**
- Diferencias entre perfiles: el perfil 13 ve las vistas por clave (**Global**) y por lote (**Lote**), con Nombre Genérico, Descripción Específica, Presentación y, en las vistas por lote, Lote, Caducidad, Cantidad y Origen; el perfil 14 ve siempre el detalle por lote sin elegir vista.
- **Global** suma las piezas por clave; **Lote** muestra cada lote con su caducidad; **Nueva** limita la consulta a las ubicaciones nuevas; **Disponibilidad** agrega una columna de Estatus (DISPONIBLE o NO DISPONIBLE; lo que está en cuarentena aparece como no disponible).
- En las vistas del perfil 13 solo se consideran lotes con existencia, no caducados y con la clave activa; en la del perfil 14 se muestran los lotes con existencia del proyecto.

## Existencias Disponibles

**Para qué sirve:** Consultar la existencia disponible.

**Quién la ve:** Perfil 13.

**Cómo llegar:** Menú › Inventario › Existencias Disponibles.

**Paso a paso:**
1. Abre la opción.
2. Da clic en **Descargar** para obtener el archivo.

**Campos:** Sin campos de captura.

**Mensajes y errores frecuentes:** Ninguno identificado.

**Notas:** Es la misma pantalla que **Existencia Disponible** de Almacén.
