# Historial de cambios del manual

## 2026-11-18 · 9bb755d

Mensaje del commit: Manual: nombre del perfil en el selector en lugar de Perfil N.

- `almacen-usuario.md`, sección **Manual de Usuario**: la lista para elegir perfil muestra el nombre del perfil; el encabezado muestra número y nombre.
- `compras-usuario.md` y `auditoria-usuario.md`, sección **Manual de Usuario**: el encabezado muestra número y nombre del perfil.

## 2026-11-18 · 4b6ce2d

Mensaje del commit: Cargas: no aceptar archivos vacíos ni la plantilla sin llenar.

- `almacen-carga-de-archivos.md`: nuevo apartado general **Archivos vacíos y renglones de ejemplo** (archivo de 0 bytes, sin hojas, sin renglones o solo con el ejemplo de la plantilla; no se guarda nada); ajuste del aviso en **Carga de Inventario** y nota sobre los ejemplos en **Carga de Equipos**.
- `compras-carga-de-archivos.md`, sección **Carga de OC**: mismos avisos de archivo vacío y de renglón de ejemplo.
- Los cambios de versión de scripts no afectan lo que ve el usuario.

## 2026-11-18 · 78eddce

Mensaje del commit: Cargas sin repetidos, carga de catálogos y nuevo inicio.

- `almacen-carga-de-archivos.md`: aviso general de **renglones repetidos** (se omiten con aviso y lista, no son error); cambios en **Carga de Inventario**, **Carga de Requerimiento** (ya no cierra pendientes), **Carga de Caducos**, **Carga de OC** (ya no reemplaza órdenes) y **Carga de Equipos**; secciones nuevas **Carga de Marcas**, **Carga de Proveedores**, **Carga de Unidades** y **Carga de Productos** (perfiles 100, 1 y 5).
- `compras-carga-de-archivos.md`, sección **Carga de OC**: las órdenes ya no se reemplazan; los repetidos se omiten con aviso.
- `README.md`, **Cómo entrar**: nueva página de inicio con los tres accesos.
- Los demás cambios de pantallas (versión de estilos) no afectan lo que ve el usuario.

## 2026-11-18 · b4922c5

Mensaje del commit: Login con selector Almacén · Compras · Auditoría y errores claros en las cargas.

- `README.md`: sección **Cómo entrar** (selector de módulo, aviso de perfil incorrecto, Auditoría con usuarios de Almacén) y tabla de perfiles (100 y 1 también entran a Auditoría).
- `auditoria-consulta.md`, `auditoria-ordenes-de-compra.md`, `auditoria-usuario.md`: las opciones ahora las ven los perfiles 100, 1 y 28.
- `almacen-carga-de-archivos.md`: **Carga de Caducos** (lote hasta 30 caracteres) y **Carga de Equipos** (los equipos repetidos se omiten con aviso, serie o lote hasta 30 caracteres, mensajes de error claros).
- `almacen-entrada.md`, sección **Entrada de Equipos**: **No. de serie / Lote** hasta 30 caracteres y mensajes de error de guardado más claros.

## 2026-11-17 · da6c2f8

Mensaje del commit: Carga de Equipos por Excel, ubicaciones de ingreso y etiquetas de poste en 3 columnas.

- `almacen-carga-de-archivos.md`: sección nueva **Carga de Equipos** (perfiles 100, 1, 2, 3 y 5).
- `almacen-entrada.md`, sección **Entrada de Equipos**: **Ubicación** ahora es una lista de ubicaciones de ingreso que cambia según el proyecto; mensajes nuevos de ubicación.
- `almacen-inventario.md`: se quitó **Etiquetas de Ubicación**, que pasó al módulo PDF.
- `almacen-pdf.md`: sección **Etiquetas de Ubicación**, ahora con etiquetas de poste en 3 columnas por hoja.

## 2026-11-17 · 1c9c276

Mensaje del commit: Entrada de Equipos: empaque por tarimas, cajas y piezas, acuse y marbetes.

Se actualizó `almacen-entrada.md`, sección **Entrada de Equipos**: nuevo bloque **Empaque** con total calculado, campos **Orden de compra** y **No. de tarima**, serie o lote según las piezas, mensajes nuevos de empaque, botones de formatos (acuse y marbetes) y columnas **Piezas** y **Formatos** en la tabla de últimas entradas. El cambio en `pom.xml` no afecta lo que ve el usuario.

## 2026-11-17 · 16b98f5

Mensaje del commit: Carga de Archivos, Entrada de Equipos, Perfiles/Superusuario, Manual de Usuario y ajustes de pantallas.

Se comparó el catálogo del menú del rango contra el manual. Los cambios del menú ya estaban reflejados, por lo que no se modificaron los módulos:
- Perfil 100 (SUPERUSUARIO) en las opciones de Almacén.
- Módulo Carga de Archivos (Almacén y Compras), y Cargar OC fuera de Compras › Órdenes de Compra.
- Entrada de Equipos, Perfiles de Usuario y Manual de Usuario (Almacén, Compras y Auditoría).
- Ajustes de Menús por Perfil, Alta de Proyecto, Parámetro Facturación y Cambio Contraseña.

Los demás archivos del rango (ajustes internos, despliegue, estilos y pantallas sin cambio en el menú) no se revisaron pantalla por pantalla. Pendiente de confirmar: si alguna pantalla ajustada cambió campos o mensajes visibles.

## 2026-11-17 · 4659268

Mensaje del commit: no se revisó (generación completa del manual).

Se creó el manual completo a partir del catálogo del menú: `README.md` y los 18 archivos de módulos de Almacén, Compras y Auditoría. Varias secciones quedaron con "Pendiente de confirmar" porque su pantalla no se revisó en detalle.
