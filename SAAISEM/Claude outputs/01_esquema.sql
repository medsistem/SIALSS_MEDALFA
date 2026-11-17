-- ---------------------------------------------------------------------------
-- Almacén de medicamentos e insumos médicos — esquema de seguridad y menús
-- MariaDB 10.4+
--
-- Base nueva, independiente de sialss_mdf. Solo contiene usuarios, roles,
-- menús, permisos y la bitácora de accesos.
-- ---------------------------------------------------------------------------

CREATE DATABASE IF NOT EXISTS almacen_med
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE almacen_med;
-- El SET NAMES no es decorativo: el cliente de MariaDB usa latin1 por omisión
-- en muchas instalaciones (incluida la de línea de comandos en Windows). Sin
-- esta línea, al cargar este archivo los acentos quedan doblemente
-- codificados: "almacén" se guarda como "almacÃ©n" y así sale en el menú.
SET NAMES utf8mb4;



-- ---------------------------------------------------------------------------
-- Usuarios
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuario (
    id                  INT           NOT NULL AUTO_INCREMENT,
    usuario             VARCHAR(50)   NOT NULL,
    clave_hash          VARCHAR(100)  NOT NULL COMMENT 'BCrypt. 60 caracteres, se deja holgado por si cambia el algoritmo',
    nombre              VARCHAR(150)  NOT NULL,
    correo              VARCHAR(150)  NULL,
    activo              TINYINT(1)    NOT NULL DEFAULT 1,
    bloqueado           TINYINT(1)    NOT NULL DEFAULT 0,
    forzar_cambio_clave TINYINT(1)    NOT NULL DEFAULT 1,
    intentos_fallidos   INT           NOT NULL DEFAULT 0,
    ultimo_acceso       DATETIME      NULL,
    fecha_alta          DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_usuario_usuario (usuario),
    KEY ix_usuario_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ---------------------------------------------------------------------------
-- Roles
--
-- La clave se guarda sin el prefijo ROLE_; Spring Security lo antepone en
-- DetallesUsuarioServicio. Así la base queda legible.
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS rol (
    id          INT          NOT NULL AUTO_INCREMENT,
    clave       VARCHAR(40)  NOT NULL,
    nombre      VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) NULL,
    activo      TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uq_rol_clave (clave)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS usuario_rol (
    usuario_id INT NOT NULL,
    rol_id     INT NOT NULL,
    PRIMARY KEY (usuario_id, rol_id),
    KEY ix_usuario_rol_rol (rol_id),
    CONSTRAINT fk_ur_usuario FOREIGN KEY (usuario_id) REFERENCES usuario (id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_rol     FOREIGN KEY (rol_id)     REFERENCES rol (id)     ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ---------------------------------------------------------------------------
-- Menú
--
-- Árbol de un solo nivel de anidamiento (padre e hijos). padre_id NULL = raíz.
-- El menú de la pantalla se arma leyendo esta tabla cruzada con rol_menu, así
-- que agregar una opción nueva no requiere tocar Java ni recompilar.
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS menu (
    id       INT          NOT NULL AUTO_INCREMENT,
    padre_id INT          NULL,
    clave    VARCHAR(60)  NOT NULL,
    etiqueta VARCHAR(100) NOT NULL,
    icono    VARCHAR(60)  NULL COMMENT 'Clase de icono PrimeFaces, ej. pi pi-download',
    destino  VARCHAR(200) NULL COMMENT 'Ruta de la vista; NULL en los nodos que solo agrupan',
    orden    INT          NOT NULL DEFAULT 0,
    activo   TINYINT(1)   NOT NULL DEFAULT 1,
    PRIMARY KEY (id),
    UNIQUE KEY uq_menu_clave (clave),
    KEY ix_menu_padre (padre_id),
    KEY ix_menu_orden (padre_id, orden),
    CONSTRAINT fk_menu_padre FOREIGN KEY (padre_id) REFERENCES menu (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS rol_menu (
    rol_id  INT NOT NULL,
    menu_id INT NOT NULL,
    PRIMARY KEY (rol_id, menu_id),
    KEY ix_rol_menu_menu (menu_id),
    CONSTRAINT fk_rm_rol  FOREIGN KEY (rol_id)  REFERENCES rol (id)  ON DELETE CASCADE,
    CONSTRAINT fk_rm_menu FOREIGN KEY (menu_id) REFERENCES menu (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ---------------------------------------------------------------------------
-- Bitácora de accesos
--
-- Quién entró, cuándo, desde dónde y si lo logró. Es lo que te van a pedir en
-- una auditoría, y es barato tenerlo desde el primer día.
--
-- No guarda la contraseña ni intentos de contraseña: solo el nombre de usuario
-- que se intentó y el motivo del rechazo.
-- ---------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS bitacora_acceso (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    usuario    VARCHAR(50)  NOT NULL COMMENT 'El capturado, exista o no',
    exito      TINYINT(1)   NOT NULL,
    motivo     VARCHAR(120) NULL COMMENT 'Causa del rechazo cuando exito = 0',
    ip         VARCHAR(45)  NULL COMMENT '45 caracteres para que quepa IPv6',
    agente     VARCHAR(255) NULL,
    fecha      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY ix_bitacora_fecha (fecha),
    KEY ix_bitacora_usuario (usuario, fecha)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ---------------------------------------------------------------------------
-- Usuario de la aplicación
-- Ajusta host y contraseña. Sin DROP ni ALTER: la aplicación no cambia el esquema.
-- ---------------------------------------------------------------------------
-- CREATE USER IF NOT EXISTS 'app_almacen'@'localhost' IDENTIFIED BY 'CAMBIA_ESTA_CLAVE';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON almacen_med.* TO 'app_almacen'@'localhost';
-- FLUSH PRIVILEGES;
