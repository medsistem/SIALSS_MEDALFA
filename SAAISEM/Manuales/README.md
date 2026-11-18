# Manual de usuario de SIALSS_MDF

## Qué es SIALSS_MDF

SIALSS (Sistema Integral de Administración y Logística para Servicios de Salud) es el sistema de MEDALFA para el almacén y la distribución de insumos para la salud. Con él recibes mercancía, surtes remisiones a las unidades, consultas existencias y generas reportes.

## Cómo entrar

1. Abre la página de inicio (**¿A qué módulo quieres entrar?**) y elige tu acceso: **SIALSS Almacén**, **SIALSS Compras** o **SIALSS Auditoría**. Cada opción trae una breve descripción y el botón **Entrar**. En la pantalla de inicio de sesión, arriba de **Iniciar sesión**, hay un selector **Almacén · Compras · Auditoría** para cambiar de módulo.
2. Escribe tu **Usuario**.
3. En **Entrar con**, elige **Contraseña** o **PIN**.
4. Escribe tu contraseña o tu PIN. Usa **Mostrar** para ver lo que escribes.
5. Da clic en **Entrar**.

Reglas que debes conocer:
- Después de varios intentos fallidos (3 de forma predeterminada) tu usuario se **bloquea**. Pide al administrador que lo desbloquee.
- Si dejas el sistema sin actividad, la pantalla se **bloquea** (5 minutos de forma predeterminada) y debes escribir tu contraseña o PIN. Tras un tiempo más largo (60 minutos de forma predeterminada) la **sesión se cierra**.
- Si tu perfil está suspendido no podrás entrar: "Su perfil de usuario está suspendido. Comuníquese con el administrador del sistema."
- Cada acceso solo deja entrar a ciertos perfiles. Si entras por el acceso equivocado ves "Su usuario no tiene el perfil de este acceso. Entre por el acceso que le corresponde."; usa el selector para cambiar de módulo (no cuenta como intento fallido).
- Auditoría usa los mismos usuarios que Almacén (solo entran los perfiles 28, 1 y 100): los intentos fallidos cuentan en conjunto, solo se permite una sesión a la vez y la contraseña se cambia cada 90 días.
- Para salir, usa la opción de cerrar sesión del menú; la pantalla de inicio de sesión cierra cualquier sesión anterior.

### Tu PIN

El PIN es un número corto que sustituye a la contraseña al entrar o desbloquear la pantalla. Lo creas, cambias o quitas en **Menú › Usuario › Mi PIN de Acceso** (ver [Almacén › Usuario](almacen-usuario.md#mi-pin-de-acceso)). No puede tener números repetidos (111111) ni seguidos (123456).

## Cómo leer el menú

El menú se organiza en **Módulo › Opción** dentro de tu acceso (Almacén, Compras o Auditoría). Cada persona ve solo las opciones que le corresponden según su perfil y los ajustes que haga el superusuario. En este manual cada sección indica:

- **Quién la ve:** los números de perfil que tienen la opción.
- **Cómo llegar:** Menú › Módulo › Opción.
- Algunas opciones se abren en una **ventana aparte**; la sección lo indica.

Las secciones marcadas como "Pendiente de confirmar" tienen información que aún no se ha revisado en pantalla.

## Perfiles de usuario

El perfil de cada usuario (número de tipo de usuario) determina qué opciones del menú ve.

| Perfil | Nombre | Notas |
|---|---|---|
| 100 | SUPERUSUARIO | Todo lo del administrador, más perfiles y menús por perfil. También entra a Auditoría |
| 1 | ADMINISTRADOR | Administra usuarios y proyectos. También entra a Auditoría |
| 5 | ADMIN ALMACEN | Administra usuarios y proyectos de Almacén |
| 6 | ADMIN CONSULTAS | Consultas y reportes |
| 2 | JEFE DE C.S. | Acceso Almacén |
| 3 | COORDINADOR DE C.S. | Acceso Almacén |
| 4 | CONSULTAS C.S. | Acceso Almacén, solo consulta |
| 17 | CLIENTES (usuario de consultas de diferentes empresas) | Acceso Almacén con vistas para cliente |
| 13, 14, 15 | Pendiente de confirmar nombre | Acceso Compras. Los nombres no aparecen en el código ni en los scripts de despliegue; se buscaron en los menús anteriores, en el módulo de usuarios, en la consulta de perfiles de la pantalla de alta de usuario y en los scripts de la carpeta de despliegue. Se registran solo en la tabla de perfiles de la base de datos |
| 28 | Pendiente de confirmar nombre | Acceso Auditoría. El nombre no aparece en el código ni en los scripts de despliegue (misma búsqueda que los perfiles 13, 14 y 15); se ve en la pantalla **Perfiles de Usuario** o en el campo **Tipo Usuario** del alta de usuarios |

## Índice

### Almacén
- [Entrada](almacen-entrada.md)
- [Remisión](almacen-remision.md)
- [Inventario](almacen-inventario.md)
- [PDF](almacen-pdf.md)
- [Consulta](almacen-consulta.md)
- [Reporte](almacen-reporte.md)
- [Catálogos](almacen-catalogos.md)
- [Usuario](almacen-usuario.md)
- [Carga de Archivos](almacen-carga-de-archivos.md)

### Compras
- [Entrada](compras-entrada.md)
- [Órdenes de Compra](compras-ordenes-de-compra.md)
- [Inventario](compras-inventario.md)
- [Consulta](compras-consulta.md)
- [Catálogos](compras-catalogos.md)
- [Usuario](compras-usuario.md)
- [Carga de Archivos](compras-carga-de-archivos.md)

### Auditoría
- [Órdenes de Compra](auditoria-ordenes-de-compra.md)
- [Consulta](auditoria-consulta.md)
- [Usuario](auditoria-usuario.md)

Consulta también el [historial de cambios del manual](historial.md).
