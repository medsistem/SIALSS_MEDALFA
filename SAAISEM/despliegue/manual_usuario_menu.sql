-- Opción "Manual de Usuario" (ManualUsuario.jsp) en el módulo USUARIO de los tres accesos, para todos los
-- perfiles que ya tienen alguna opción en ese acceso. Solo hace falta si ya se ejecutó menu_roles_tablas.sql
-- (sin esas tablas el menú sale de menu-roles.csv, que ya trae la opción). Es repetible.
-- Después de ejecutarlo, reiniciar la aplicación o guardar cualquier cambio en "Menús por Perfil y Usuario".
USE `sialss_mdf`;
SET NAMES utf8mb4;

INSERT INTO tb_menu_opcion (F_Acceso, F_Modulo, F_Opcion, F_Url, F_Ventana) VALUES ('ALMACEN', 'USUARIO', 'Manual de Usuario', 'ManualUsuario.jsp', 0) ON DUPLICATE KEY UPDATE F_IdOpcion = F_IdOpcion;
INSERT INTO tb_menu_opcion (F_Acceso, F_Modulo, F_Opcion, F_Url, F_Ventana) VALUES ('COMPRAS', 'USUARIO', 'Manual de Usuario', 'ManualUsuario.jsp', 0) ON DUPLICATE KEY UPDATE F_IdOpcion = F_IdOpcion;
INSERT INTO tb_menu_opcion (F_Acceso, F_Modulo, F_Opcion, F_Url, F_Ventana) VALUES ('AUDITORIA', 'USUARIO', 'Manual de Usuario', 'ManualUsuario.jsp', 0) ON DUPLICATE KEY UPDATE F_IdOpcion = F_IdOpcion;

-- Todos los perfiles con al menos una opción activa en el mismo acceso.
INSERT IGNORE INTO tb_menu_perfil (F_IdOpcion, F_TipUsu)
SELECT DISTINCT m.F_IdOpcion, p.F_TipUsu
  FROM tb_menu_opcion m
  JOIN tb_menu_opcion o ON o.F_Acceso = m.F_Acceso AND o.F_Status = 'A' AND o.F_IdOpcion <> m.F_IdOpcion
  JOIN tb_menu_perfil p ON p.F_IdOpcion = o.F_IdOpcion
 WHERE m.F_Url = 'ManualUsuario.jsp';

SELECT m.F_Acceso, GROUP_CONCAT(p.F_TipUsu ORDER BY p.F_TipUsu) perfiles
  FROM tb_menu_opcion m LEFT JOIN tb_menu_perfil p ON p.F_IdOpcion = m.F_IdOpcion
 WHERE m.F_Url = 'ManualUsuario.jsp' GROUP BY m.F_Acceso;

-- Reversa:
-- DELETE p FROM tb_menu_perfil p JOIN tb_menu_opcion m ON m.F_IdOpcion = p.F_IdOpcion WHERE m.F_Url = 'ManualUsuario.jsp';
-- DELETE u FROM tb_menu_usuario u JOIN tb_menu_opcion m ON m.F_IdOpcion = u.F_IdOpcion WHERE m.F_Url = 'ManualUsuario.jsp';
-- DELETE FROM tb_menu_opcion WHERE F_Url = 'ManualUsuario.jsp';
