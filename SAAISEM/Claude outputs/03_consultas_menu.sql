-- ---------------------------------------------------------------------------
-- Las consultas que arman el menú dinámico, aisladas para poder probarlas
-- sin levantar la aplicación.
-- ---------------------------------------------------------------------------

USE almacen_med;


-- 1. Opciones visibles para un usuario -------------------------------------
--    Es la consulta que usa MenuRepositorio.buscarPorUsuario().
--    Devuelve raíces e hijos en un solo resultado, ya ordenados; el servicio
--    solo los acomoda en árbol.

SELECT m.id, m.padre_id, m.clave, m.etiqueta, m.icono, m.destino, m.orden
FROM   menu m
JOIN   rol_menu rm     ON rm.menu_id = m.id
JOIN   usuario_rol ur  ON ur.rol_id  = rm.rol_id
JOIN   usuario u       ON u.id       = ur.usuario_id
JOIN   rol r           ON r.id       = rm.rol_id
WHERE  u.usuario = 'almacen'
  AND  u.activo = 1
  AND  r.activo = 1
  AND  m.activo = 1
GROUP  BY m.id, m.padre_id, m.clave, m.etiqueta, m.icono, m.destino, m.orden
ORDER  BY COALESCE(m.padre_id, m.id), m.padre_id IS NOT NULL, m.orden;

-- El GROUP BY es necesario: un usuario con dos roles que compartan una opción
-- la traería duplicada.
--
-- El ORDER BY ordena primero por la raíz a la que pertenece cada renglón, luego
-- pone la raíz antes que sus hijos, y al final respeta el orden capturado.


-- 2. Comprobación: qué ve cada perfil --------------------------------------

SELECT u.usuario,
       COUNT(DISTINCT m.id)                                      AS opciones,
       SUM(CASE WHEN m.padre_id IS NULL THEN 1 ELSE 0 END)       AS grupos,
       GROUP_CONCAT(DISTINCT CASE WHEN m.padre_id IS NULL
                                  THEN m.etiqueta END ORDER BY m.orden SEPARATOR ' | ') AS menus
FROM   usuario u
JOIN   usuario_rol ur ON ur.usuario_id = u.id
JOIN   rol_menu rm    ON rm.rol_id     = ur.rol_id
JOIN   menu m         ON m.id          = rm.menu_id AND m.activo = 1
GROUP  BY u.usuario
ORDER  BY u.usuario;


-- 3. Grupos que quedarían vacíos -------------------------------------------
--    Un grupo asignado a un rol pero sin ningún hijo asignado se vería como
--    un menú que no abre nada. El servicio los descarta; esta consulta sirve
--    para detectarlos al capturar permisos.

SELECT r.clave AS rol, p.etiqueta AS grupo_sin_hijos
FROM   rol r
JOIN   rol_menu rm ON rm.rol_id = r.id
JOIN   menu p      ON p.id = rm.menu_id AND p.padre_id IS NULL
WHERE  NOT EXISTS (
         SELECT 1 FROM menu h
         JOIN rol_menu rh ON rh.menu_id = h.id AND rh.rol_id = r.id
         WHERE h.padre_id = p.id AND h.activo = 1 )
ORDER  BY r.clave, p.orden;


-- 4. Opciones huérfanas ----------------------------------------------------
--    Hojas que ningún rol puede ver: o falta capturar el permiso, o la opción
--    ya no se usa.

SELECT m.clave, m.etiqueta, m.destino
FROM   menu m
WHERE  m.padre_id IS NOT NULL
  AND  m.activo = 1
  AND  NOT EXISTS (SELECT 1 FROM rol_menu rm WHERE rm.menu_id = m.id)
ORDER  BY m.clave;


-- 5. Últimos accesos y rechazos --------------------------------------------

SELECT usuario, exito, motivo, ip, fecha
FROM   bitacora_acceso
ORDER  BY fecha DESC
LIMIT  50;

SELECT usuario, COUNT(*) AS intentos_fallidos, MAX(fecha) AS ultimo
FROM   bitacora_acceso
WHERE  exito = 0 AND fecha >= NOW() - INTERVAL 1 DAY
GROUP  BY usuario
HAVING COUNT(*) >= 5
ORDER  BY intentos_fallidos DESC;
