-- PIN de inicio de sesión (skill seguridad-sesion-sialss).
-- Agrega a las tablas de usuarios el PIN cifrado (PBKDF2, nunca el PIN en claro) y su fecha.
-- Antes de aplicarlo en otra base: buscar INSERT INTO tb_usuario / tb_usuariocompra sin lista de columnas
-- (en SIALSS_MDF estaban en servlets/ModificacionPass.java y ya llevan la lista).
-- Repetible: solo agrega las columnas si faltan. Lo puede ejecutar el usuario de la aplicación (permiso ALTER).
ALTER TABLE tb_usuario
  ADD COLUMN IF NOT EXISTS F_Pin VARCHAR(120) NULL COMMENT 'PIN cifrado (pbkdf2$iteraciones$sal$hash); NULL = sin PIN',
  ADD COLUMN IF NOT EXISTS F_PinFecha DATETIME NULL COMMENT 'Cuándo se creó o cambió el PIN';
ALTER TABLE tb_usuariocompra
  ADD COLUMN IF NOT EXISTS F_Pin VARCHAR(120) NULL COMMENT 'PIN cifrado (pbkdf2$iteraciones$sal$hash); NULL = sin PIN',
  ADD COLUMN IF NOT EXISTS F_PinFecha DATETIME NULL COMMENT 'Cuándo se creó o cambió el PIN';
