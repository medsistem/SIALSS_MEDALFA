-- Alta de proyecto (skill proyecto-nuevo-sialss): columnas de dirección y RFC en tb_proyectos.
-- La pantalla también las crea si faltan (permiso ALTER). Repetible.
ALTER TABLE tb_proyectos
  ADD COLUMN IF NOT EXISTS F_Direccion VARCHAR(255) NULL COMMENT 'Dirección del proyecto',
  ADD COLUMN IF NOT EXISTS F_Rfc VARCHAR(13) NULL COMMENT 'RFC del proyecto';
-- Cada alta de proyecto puede agregar además (desde la aplicación, con ALTER):
--   tb_medica.F_Project<id> INT NOT NULL DEFAULT 0   (si no hay columna de proyecto libre)
--   tb_indice.F_IndFactP<id> INT NOT NULL DEFAULT 1  (si no existe)
