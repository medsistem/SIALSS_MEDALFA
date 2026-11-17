-- ---------------------------------------------------------------------------
-- Datos iniciales: roles, menú, permisos y cuatro usuarios de arranque.
--
-- Las cuatro cuentas traen la misma contraseña temporal y el indicador
-- forzar_cambio_clave en 1. Cámbialas antes de que esto toque un servidor real.
--
--     contraseña temporal:  Cambiar.2026
--
-- Los hash son BCrypt con costo 10, generados para este archivo. Spring
-- Security los verifica con BCryptPasswordEncoder sin configuración extra.
-- ---------------------------------------------------------------------------

USE almacen_med;
-- El SET NAMES no es decorativo: el cliente de MariaDB usa latin1 por omisión
-- en muchas instalaciones (incluida la de línea de comandos en Windows). Sin
-- esta línea, al cargar este archivo los acentos quedan doblemente
-- codificados: "almacén" se guarda como "almacÃ©n" y así sale en el menú.
SET NAMES utf8mb4;



-- ---------------------------------------------------------------------------
-- Roles
-- ---------------------------------------------------------------------------
INSERT INTO rol (id, clave, nombre, descripcion) VALUES
  (1, 'ADMINISTRADOR', 'Administrador', 'Acceso total, incluida la configuración y la bitácora'),
  (2, 'RECIBO',        'Recibo',        'Recepción de mercancía y entradas al almacén'),
  (3, 'ALMACEN',       'Almacén',       'Movimientos de almacén, salidas y surtido'),
  (4, 'FACTURACION',   'Facturación',   'Consulta de salidas e impresión de documentos')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), descripcion = VALUES(descripcion);


-- ---------------------------------------------------------------------------
-- Menú: tres grupos pedidos más la administración
-- ---------------------------------------------------------------------------
INSERT INTO menu (id, padre_id, clave, etiqueta, icono, destino, orden) VALUES
  -- Raíces
  (10, NULL, 'ENTRADAS',    'Entradas almacén', 'pi pi-download',  NULL, 10),
  (20, NULL, 'SALIDAS',     'Salidas',          'pi pi-upload',    NULL, 20),
  (30, NULL, 'IMPRESIONES', 'Impresiones',      'pi pi-print',     NULL, 30),
  (90, NULL, 'ADMIN',       'Administración',   'pi pi-cog',       NULL, 90),

  -- Entradas
  (11, 10, 'ENT_RECEPCION', 'Recepción de compra',    'pi pi-box',           '/entradas/recepcion.xhtml', 10),
  (12, 10, 'ENT_DEVOLUCION','Entrada por devolución', 'pi pi-replay',        '/entradas/devolucion.xhtml', 20),
  (13, 10, 'ENT_CONSULTA',  'Consulta de entradas',   'pi pi-search',        '/entradas/consulta.xhtml',  30),

  -- Salidas
  (21, 20, 'SAL_SURTIDO',   'Surtido a unidad',       'pi pi-send',          '/salidas/surtido.xhtml',    10),
  (22, 20, 'SAL_CADUCIDAD', 'Salida por caducidad',   'pi pi-exclamation-triangle', '/salidas/caducidad.xhtml', 20),
  (23, 20, 'SAL_CONSULTA',  'Consulta de salidas',    'pi pi-search',        '/salidas/consulta.xhtml',   30),

  -- Impresiones
  (31, 30, 'IMP_MARBETES',  'Marbetes',               'pi pi-tag',           '/impresiones/marbetes.xhtml',  10),
  (32, 30, 'IMP_FOLIOS',    'Folios',                 'pi pi-file',          '/impresiones/folios.xhtml',    20),
  (33, 30, 'IMP_EXISTENCIAS','Reporte de existencias','pi pi-chart-bar',     '/impresiones/existencias.xhtml', 30),

  -- Administración
  (91, 90, 'ADM_USUARIOS',  'Usuarios',               'pi pi-users',         '/admin/usuarios.xhtml',    10),
  (92, 90, 'ADM_ROLES',     'Roles y permisos',       'pi pi-key',           '/admin/roles.xhtml',       20),
  (93, 90, 'ADM_MENUS',     'Menús',                  'pi pi-sitemap',       '/admin/menus.xhtml',       30),
  (94, 90, 'ADM_BITACORA',  'Bitácora de accesos',    'pi pi-history',       '/admin/bitacora.xhtml',    40)
ON DUPLICATE KEY UPDATE etiqueta = VALUES(etiqueta), icono = VALUES(icono),
                        destino = VALUES(destino), orden = VALUES(orden);


-- ---------------------------------------------------------------------------
-- Permisos por rol
--
-- Se asignan los nodos hoja. Los grupos (10, 20, 30, 90) se asignan también
-- porque el servicio solo muestra un grupo si el rol lo tiene y además tiene
-- al menos un hijo visible.
-- ---------------------------------------------------------------------------
DELETE FROM rol_menu;

-- Administrador: todo
INSERT INTO rol_menu (rol_id, menu_id) SELECT 1, id FROM menu;

-- Recibo: entradas completas, y de impresiones solo marbetes
INSERT INTO rol_menu (rol_id, menu_id) VALUES
  (2, 10), (2, 11), (2, 12), (2, 13),
  (2, 30), (2, 31);

-- Almacén: consulta de entradas, salidas completas, marbetes y folios
INSERT INTO rol_menu (rol_id, menu_id) VALUES
  (3, 10), (3, 13),
  (3, 20), (3, 21), (3, 22), (3, 23),
  (3, 30), (3, 31), (3, 32);

-- Facturación: consulta de salidas e impresiones completas
INSERT INTO rol_menu (rol_id, menu_id) VALUES
  (4, 20), (4, 23),
  (4, 30), (4, 31), (4, 32), (4, 33);


-- ---------------------------------------------------------------------------
-- Usuarios de arranque — contraseña temporal: Cambiar.2026
-- ---------------------------------------------------------------------------
INSERT INTO usuario (id, usuario, clave_hash, nombre, correo, activo, forzar_cambio_clave) VALUES
  (1, 'admin',   '$2b$10$ADECyqlfJymXtd22xoWLeuomlUeUXmJ0HzgrGSsMuM.0DoYVKaX7q',
      'Administrador del sistema', NULL, 1, 1),
  (2, 'recibo',  '$2b$10$Jh.9Y.oJIQmWaSUPXg.nEufoIF4x2ej9I5qO5Ok5W/MDEJilRroke',
      'Usuario de recibo',         NULL, 1, 1),
  (3, 'almacen', '$2b$10$AGN0wFML0/chziX9cy45SOT3XLcNKbQ9hoBGREsxxuX1xElzVySWK',
      'Usuario de almacén',        NULL, 1, 1),
  (4, 'factura', '$2b$10$MwvC9wZ3YzaQLnG4vKk6nOnEYyCNm.6l4dNH81OjsvB9q7/hUhJ2q',
      'Usuario de facturación',    NULL, 1, 1)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

INSERT INTO usuario_rol (usuario_id, rol_id) VALUES
  (1, 1), (2, 2), (3, 3), (4, 4)
ON DUPLICATE KEY UPDATE rol_id = VALUES(rol_id);
