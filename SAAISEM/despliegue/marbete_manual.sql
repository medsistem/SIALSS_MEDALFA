-- =============================================================================
-- SIALSS (sialss_mdf) · Procedimiento MarbeteManual (Marbetes > Marbete Manual, reporte MarbeteManual.jasper)
-- =============================================================================
-- Tomado de producción (19/11/2026) con cuatro ajustes:
--   1. l2.F_UniMed -> l2.F_tarima   (el script 01 renombra F_UniMed a F_tarima).
--   2. l.F_Tarima  -> l.F_tarima    (misma columna; antes F_UniMed).
--   3. AS row_number -> AS `row_number`: en MariaDB 10.7+ ROW_NUMBER es palabra reservada y sin comillas da
--      error de sintaxis.
--   4. L.F_ClaLot -> l.F_ClaLot: en Linux los alias distinguen mayúsculas y "L" no existe.
-- Qué devuelve: el equipo principal (clave + lote + proyecto) y, en el detalle, todos los lotes del mismo
-- proyecto cuya clave y lote contienen los del principal (por ejemplo RXF / 29 -> RXF-BP / 18-29, RXF-MS / 95-29).
-- Requiere CREATE ROUTINE (usuario administrador). Correr DESPUÉS del script 01. Se puede correr más de una vez.
--   mariadb -h <servidor> -u <usuario_admin> -p --default-character-set=utf8mb4 sialss_mdf < 07_marbete_manual.sql
-- Reversa: reversa/07_reversa_marbete_manual.sql (deja la versión anterior de producción).
-- =============================================================================
SET NAMES utf8mb4;
USE sialss_mdf;

DROP PROCEDURE IF EXISTS MarbeteManual;

DELIMITER //
CREATE PROCEDURE MarbeteManual(IN Clave VARCHAR(30), IN Lote VARCHAR(50), IN Proyecto INT)
BEGIN
    SET @PROY = Proyecto;
    SET @Clave = Clave;
    SET @Lote = Lote;

    SELECT
        row_number() OVER () AS `row_number`,
        l2.F_tarima        AS F_TarimaDad,
        l2.F_ClaPro        AS F_ClaProclaveDad,
        m2.F_DesPro        AS F_DesProDesProDad,
        mdl2.F_DesModelo   AS F_DesModeloDad,
        l2.F_ClaLot        AS F_ClaLotDad,
        l2.F_observacion   AS F_observacionDad,
        pp.F_ClaPro        AS clave,
        pp.F_DesProEsp     AS nombre,
        pp.F_ClaLot        AS lote,
        pp.F_FecCad        AS caducidad,
        pp.F_ExiLot        AS cantidad,
        pp.F_Ubica         AS ubicacion,
        pp.F_FecFab        AS fabrica,
        pp.F_Cb,
        pp.F_ClaMar        AS marca,
        pp.F_Origen        AS origen,
        pp.F_Tarima        AS tarima,
        pp.F_DesProy       AS proyecto,
        pp.F_Modelo,
        pp.F_observa       AS observa
    FROM tb_lote AS l2
    CROSS JOIN (
        SELECT l.F_ClaPro, m.F_DesProEsp, l.F_ClaLot, l.F_FecCad, l.F_ExiLot, l.F_Ubica, l.F_FecFab, l.F_Cb,
               mr.F_DesMar AS F_ClaMar, o.F_DesOri AS F_Origen, l.F_tarima AS F_Tarima, py.F_DesProy,
               mdl.F_DesModelo AS F_Modelo, l.F_observacion AS F_Observa
        FROM tb_lote AS l
        INNER JOIN tb_medica    m   ON l.F_ClaPro = m.F_ClaPro
        INNER JOIN tb_proyectos py  ON l.F_Proyecto = py.F_Id
        INNER JOIN tb_modelo    mdl ON l.F_Modelo = mdl.F_ClaModelo
        INNER JOIN tb_marca     mr  ON l.F_ClaMar = mr.F_ClaMar
        INNER JOIN tb_origen    o   ON l.F_Origen = o.F_ClaOri
        WHERE l.F_ClaPro = @Clave
          AND l.F_ClaLot = @Lote
          AND l.F_Proyecto = @PROY
    ) AS pp
    INNER JOIN tb_medica    m2   ON l2.F_ClaPro = m2.F_ClaPro
    INNER JOIN tb_proyectos py2  ON l2.F_Proyecto = py2.F_Id
    INNER JOIN tb_modelo    mdl2 ON l2.F_Modelo = mdl2.F_ClaModelo
    WHERE l2.F_ClaPro RLIKE @Clave
      AND l2.F_ClaLot RLIKE @Lote
      AND l2.F_Proyecto = @PROY
    ORDER BY m2.F_DesProEsp;
END //
DELIMITER ;

-- Prueba (equipo de la captura del 19/11/2026): debe devolver 8 renglones.
CALL MarbeteManual('RXF', '29', 8);
