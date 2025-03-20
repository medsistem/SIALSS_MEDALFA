/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gnk.impl;

import com.gnk.dao.ExistenciaProyectoDao;
//import com.sun.xml.internal.bind.v2.runtime;
import conn.ConectionDBTrans;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author CEDIS TOLUCA3
 */
public class ExistenciaProyectoDaoImpl implements ExistenciaProyectoDao {

    /*almacen*/
    public static String BuscaProyecto = "SELECT * FROM tb_proyectos order by F_DesProy;";

    public static String BuscaProyectoConsulta = "SELECT * FROM tb_proyectos WHERE F_Id IN (%s);";

   public static String ExistenciaProyectoGlobal = "SELECT P.F_DesProy, L.F_ClaPro, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica,M.F_PrePro,tb_proveedor.F_ClaProve,tb_proveedor.F_NomPro,b.Nombre_Bodega as lugar, C.F_FuenteFinanza, IFNULL(M.F_NomGen,'') AS NomGen  FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id INNER JOIN tb_proveedor ON tb_proveedor.F_ClaProve = L.F_ClaPrv LEFT JOIN tb_compra C ON C.F_Lote = L.F_FolLot AND C.F_ClaPro = L.F_ClaPro LEFT JOIN tb_ubica as u ON u.F_ClaUbi=L.F_Ubica LEFT JOIN tb_bodegas AS b ON b.Id_Bodega = u.Id_Bodega WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0  AND L.F_Origen <> '19' AND u.Estatus=1 AND b.Estatus=1 GROUP BY L.F_ClaPro, L.F_Proyecto;";
   public static String ExistenciaProyectoGlobalFonsabi = "SELECT P.F_DesProy, L.F_ClaPro, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica,M.F_PrePro,tb_proveedor.F_ClaProve,tb_proveedor.F_NomPro, b.Nombre_Bodega as lugar, '' AS F_OrdenSuministro, '' AS F_FolRemi, '' AS OC, C.F_FuenteFinanza, '' AS F_IdLote, M.F_NomGen  FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id INNER JOIN tb_proveedor ON tb_proveedor.F_ClaProve = L.F_ClaPrv LEFT JOIN tb_compra C ON C.F_Lote = L.F_FolLot AND C.F_ClaPro = L.F_ClaPro LEFT JOIN tb_ubica as u ON u.F_ClaUbi=L.F_Ubica LEFT JOIN tb_bodegas AS b ON b.Id_Bodega = u.Id_Bodega WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0  AND L.F_Origen = '19' AND u.Estatus=1 AND b.Estatus=1 GROUP BY L.F_ClaPro, L.F_Proyecto;";

    public static String ExistenciaProyectoLote = "SELECT P.F_DesProy, L.F_ClaPro, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica,M.F_PrePro,tb_proveedor.F_ClaProve,tb_proveedor.F_NomPro,b.Nombre_Bodega as lugar, C.F_FuenteFinanza, IFNULL(M.F_NomGen,'') AS NomGen  FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id INNER JOIN tb_proveedor ON tb_proveedor.F_ClaProve = L.F_ClaPrv LEFT JOIN tb_compra C ON C.F_Lote = C.F_Lote = L.F_FolLot AND C.F_ClaPro = L.F_ClaPro LEFT JOIN tb_ubica as u ON u.F_ClaUbi=L.F_Ubica LEFT JOIN tb_bodegas AS b ON b.Id_Bodega = u.Id_Bodega WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0  AND L.F_Origen <> '19' AND u.Estatus=1 AND b.Estatus=1 GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto,tb_proveedor.F_NomPro ;";
    public static String ExistenciaProyectoLoteFonsabi = "SELECT P.F_DesProy, L.F_ClaPro, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica,M.F_PrePro,tb_proveedor.F_ClaProve,tb_proveedor.F_NomPro, b.Nombre_Bodega as lugar, '' AS F_OrdenSuministro, '' AS F_FolRemi, '' AS OC, C.F_FuenteFinanza, '' AS F_IdLote, M.F_NomGen FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id INNER JOIN tb_proveedor ON tb_proveedor.F_ClaProve = L.F_ClaPrv LEFT JOIN tb_compra C ON C.F_Lote = C.F_Lote = L.F_FolLot AND C.F_ClaPro = L.F_ClaPro LEFT JOIN tb_ubica as u ON u.F_ClaUbi=L.F_Ubica LEFT JOIN tb_bodegas AS b ON b.Id_Bodega = u.Id_Bodega WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0  AND L.F_Origen = '19' AND u.Estatus=1 AND b.Estatus=1 GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto,tb_proveedor.F_NomPro ;";

    public static String ExistenciaProyectoUbicacion = "SELECT P.F_DesProy, L.F_ClaPro, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica,M.F_PrePro,tb_proveedor.F_ClaProve,tb_proveedor.F_NomPro, b.Nombre_Bodega as lugar, '' AS F_FuenteFinanza, IFNULL(M.F_NomGen,'') AS NomGen FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id INNER JOIN tb_proveedor ON tb_proveedor.F_ClaProve = L.F_ClaPrv LEFT JOIN tb_ubica as u ON u.F_ClaUbi=L.F_Ubica LEFT JOIN tb_bodegas AS b ON b.Id_Bodega = u.Id_Bodega WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0  AND L.F_Origen <> '19' AND u.Estatus=1 AND b.Estatus=1 GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto, L.F_Ubica;";
    public static String ExistenciaProyectoUbicacionFonsabi = "SELECT P.F_DesProy, L.F_ClaPro, SUBSTR( M.F_DesPro, 1, 40 ) AS F_DesPro, L.F_ClaLot, DATE_FORMAT( L.F_FecCad, '%d/%m/%Y' ) AS F_FecCad, SUM( L.F_ExiLot ) AS F_ExiLot, O.F_DesOri, L.F_Ubica, M.F_PrePro, tb_proveedor.F_ClaProve, tb_proveedor.F_NomPro, b.Nombre_Bodega as lugar, C.F_OrdenSuministro, C.F_FolRemi, C.F_OrdCom, C.F_FuenteFinanza, L.F_IdLote, M.F_NomGen FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id INNER JOIN tb_proveedor ON tb_proveedor.F_ClaProve = L.F_ClaPrv LEFT JOIN tb_compra C ON C.F_Lote = L.F_FolLot AND C.F_ClaPro = L.F_ClaPro LEFT JOIN tb_ubica as u ON u.F_ClaUbi=L.F_Ubica LEFT JOIN tb_bodegas AS b ON b.Id_Bodega = u.Id_Bodega WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0 AND L.F_ClaPro NOT IN ( '9999', '9998', '9996', '9995' ) AND L.F_Origen = '19' AND u.Estatus=1 AND b.Estatus=1 GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto, L.F_Ubica, L.F_FolLot;";

    public static String ContarClaveProyecto = "SELECT COUNT(DISTINCT(F_ClaPro)) AS CONTAR FROM tb_lote L WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0 ;";

    public static String ContarClaveTodos = "SELECT COUNT(DISTINCT(F_ClaPro)) AS CONTAR FROM tb_lote L WHERE L.F_ExiLot > 0 ;";

    public static String ContarClaveTodosCosulta = "SELECT COUNT(DISTINCT(F_ClaPro)) AS CONTAR FROM tb_lote L WHERE L.F_ExiLot > 0 AND L.F_Proyecto IN (%s) ;";

    public static String ExistenciaProyectoTodosGlobal = "SELECT P.F_DesProy, L.F_ClaPro, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica,M.F_PrePro,tb_proveedor.F_ClaProve,tb_proveedor.F_NomPro,b.Nombre_Bodega as lugar FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id INNER JOIN tb_proveedor ON tb_proveedor.F_ClaProve = L.F_ClaPrv LEFT JOIN tb_ubica as u ON u.F_ClaUbi=L.F_Ubica LEFT JOIN tb_bodegas AS b ON b.Id_Bodega = u.Id_Bodega WHERE L.F_ExiLot>0  AND u.Estatus=1 AND b.Estatus=1 GROUP BY L.F_ClaPro, L.F_Proyecto;";

    public static String ExistenciaProyectoTodosLote = "SELECT P.F_DesProy, L.F_ClaPro, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica,M.F_PrePro,tb_proveedor.F_ClaProve,tb_proveedor.F_NomPro,b.Nombre_Bodega as lugar FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id INNER JOIN tb_proveedor ON tb_proveedor.F_ClaProve = L.F_ClaPrv LEFT JOIN tb_ubica as u ON u.F_ClaUbi=L.F_Ubica LEFT JOIN tb_bodegas AS b ON b.Id_Bodega = u.Id_Bodega WHERE L.F_ExiLot>0  AND u.Estatus=1 AND b.Estatus=1 GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto,tb_proveedor.F_NomPro;";

    public static String ExistenciaProyectoTodosUbicacion = "SELECT P.F_DesProy, L.F_ClaPro, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica,M.F_PrePro,tb_proveedor.F_ClaProve,tb_proveedor.F_NomPro, b.Nombre_Bodega as lugar, C.F_FuenteFinanza  FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id INNER JOIN tb_proveedor ON tb_proveedor.F_ClaProve = L.F_ClaPrv LEFT JOIN tb_compra C ON C.F_Lote = L.F_FolLot AND C.F_ClaPro = L.F_ClaPro LEFT JOIN tb_ubica as u ON u.F_ClaUbi=L.F_Ubica LEFT JOIN tb_bodegas AS b ON b.Id_Bodega = u.Id_Bodega WHERE L.F_ExiLot>0  AND u.Estatus=1 AND b.Estatus=1 GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto, L.F_Ubica;";

    public static String ExistenciaProyectoTodosClientes = "SELECT P.F_DesProy, L.F_ClaPro, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ExiLot>0 AND L.F_Proyecto IN (%s)   AND L.F_Ubica NOT LIKE '%AT%' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";

    public static String ExistenciaProyectoConsulta = "SELECT P.F_DesProy, L.F_ClaPro, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ExiLot>0 AND L.F_Proyecto IN ( %d )   GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";

//    public static String BuscaUbiNomostrar = "SELECT CONCAT(GROUP_CONCAT(\"'\",ue.ubicacion,\"'\"),\"'\") AS cadena_ubicacion FROM ubicaciones_excluidas AS ue ;";
    public static String BuscaUbiNomostrar = "SELECT ubicacion FROM ubicaciones_excluidas;";

public static String BuscaclavePrograma = "SELECT F_ClaPro FROM tb_medicaprograma;";

    public static String BuscaUbiNomostrarContar = "SELECT COUNT(*) FROM ubicaciones_excluidas;";

    public static String BuscaTipoUnidad = "SELECT F_Tipo FROM tb_uniatn GROUP BY F_Tipo;";

    public static String FacturadoFechaTipo = "SELECT F_ClaPro, SUM(F_Solicitado) AS SURTIDO, U.F_Tipo FROM tb_unireq F INNER JOIN tb_uniatn U ON F.F_ClaUni = U.F_ClaCli WHERE F_FecCarg = ? AND F_Tipo = ? AND F_Status = 0 GROUP BY F_ClaPro HAVING SURTIDO > 0;";

    public static String FacturadoFecha = "SELECT F_ClaPro, SUM(F_Solicitado) AS SURTIDO, U.F_Tipo FROM tb_unireq F INNER JOIN tb_uniatn U ON F.F_ClaUni = U.F_ClaCli WHERE F_FecCarg = ? AND F_Status = 0 GROUP BY F_ClaPro HAVING SURTIDO > 0;";

    /*Compras*/
    public static String ExistenciaProyectoCompraGlobalPrograma = "SELECT P.F_DesProy, M.F_ClaPro, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica, CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza FROM tb_lote L LEFT JOIN (SELECT F_Lote,GROUP_CONCAT(F_OrdenSuministro  SEPARATOR' // ') as F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0  AND F_Ubica NOT IN (%s)  AND L.F_ClaPro IN (%s) AND L.F_FecCad > CURDATE() AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_Proyecto;";
    public static String ExistenciaProyectoCompraLotePrograma = "SELECT P.F_DesProy, M.F_ClaPro, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica , CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza FROM tb_lote L LEFT JOIN (SELECT F_Lote,GROUP_CONCAT(F_OrdenSuministro  SEPARATOR' // ') as F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0  AND F_Ubica NOT IN (%s)  AND L.F_ClaPro IN (%s) AND L.F_FecCad > CURDATE() AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";
     // public static String ExistenciaProyectoCompraGlobalPrograma = "SELECT P.F_DesProy, M.F_ClaPro, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica, CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, M.F_PrePro FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0  AND F_Ubica NOT IN (%s)  AND L.F_ClaPro IN (%s) AND L.F_FecCad > CURDATE()  GROUP BY L.F_ClaPro, L.F_Proyecto;";
   //   public static String ExistenciaProyectoCompraLotePrograma = "SELECT P.F_DesProy, M.F_ClaPro, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica , CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, M.F_PrePro FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0  AND F_Ubica NOT IN (%s)  AND L.F_ClaPro IN (%s) AND L.F_FecCad > CURDATE()  GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";


    public static String ExistenciaProyectoCompraGlobal = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica, CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0  AND F_Ubica NOT IN (%s)  AND L.F_FecCad > CURDATE() AND L.F_Origen <> '19' AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_Proyecto;";

    public static String ExistenciaProyectoCompraLote = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica , CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0  AND F_Ubica NOT IN (%s)  AND L.F_FecCad > CURDATE() AND L.F_Origen <> '19' AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";

    public static String ExistenciaProyectoCompraUbicacion = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri,L.F_Ubica  ,CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0  AND F_Ubica NOT IN (%s)  AND L.F_FecCad > CURDATE() AND L.F_Origen <> '19' AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";

    public static String ExistenciaCompraLoteFonsabi = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR( M.F_DesPro, 1, 40 ) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, Sum(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica, IFNULL(c.F_OrdenSuministro, '') AS suministro, IFNULL(c.F_NomCli, '') AS F_NomCli, M.F_NomGen, M.F_PrePro  FROM tb_lote AS L INNER JOIN tb_medica AS M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen AS O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos AS P ON L.F_Proyecto = P.F_Id LEFT JOIN (SELECT c.F_Lote, c.F_OrdenSuministro, u.F_NomCli, c.F_ClaPro FROM tb_compra AS c LEFT JOIN tb_unidadfonsabi AS uf ON c.F_Lote = uf.F_FolLot LEFT JOIN tb_uniatn AS u ON uf.F_ClaCli = u.F_ClaCli WHERE c.F_OrdenSuministro IS NOT NULL GROUP BY c.F_Lote ) AS c ON L.F_FolLot = c.F_Lote AND L.F_ClaPro = c.F_ClaPro WHERE L.F_Proyecto = ? AND L.F_Ubica NOT IN (%s) AND L.F_ExiLot > 0  AND L.F_FecCad > CURDATE( ) AND L.F_Origen = '19' AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto, L.F_FolLot;";

    public static String ExistenciaProyectoCompraCovidIsem = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri,L.F_Ubica  ,CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0 AND F_Ubica NOT IN (%s) AND  L.F_Origen = '3'  AND L.F_FecCad > CURDATE() AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";

    public static String ExistenciaProyectoCompraInsabi = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri,L.F_Ubica  ,CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0 AND F_Ubica NOT IN (%s) AND  L.F_Origen = '4'  AND L.F_FecCad > CURDATE() AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";

    public static String ExistenciaProyectoCompraCovidInsabi = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri,L.F_Ubica  ,CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0 AND F_Ubica NOT IN (%s) AND  L.F_Origen = '5'  AND L.F_FecCad > CURDATE() AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";

    public static String ExistenciaProyectoCompraIsem = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri,L.F_Ubica  ,CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0 AND F_Ubica NOT IN (%s) AND  L.F_Origen IN ('1','2','8')  AND L.F_FecCad > CURDATE() AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";

    public static String ContarClaveProyectoCompra = "SELECT COUNT(DISTINCT(F_ClaPro)) AS CONTAR FROM tb_lote L WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0 AND L.F_Origen <> '19' AND F_Ubica NOT IN ('CADUCADOS', 'PROXACADUCAR', 'MERMA', 'CROSSDOCKMORELIA', 'INGRESOS_V', 'CUARENTENA',  'ATI','NUEVA', 'AT','duplicado','CADUCADOSFARMACIA') ;";

//    public static String ContarClaveProyectoCompraDis = "SELECT COUNT(DISTINCT(F_ClaPro)) AS CONTAR FROM tb_lote L WHERE L.F_Proyecto = ?  AND L.F_ExiLot > 0 AND F_Ubica NOT IN (%s) AND L.F_ClaPro NOT IN ('9999', '9998');";
    public static String ContarClaveProyectoCompraDis = "SELECT COUNT(DISTINCT(F_ClaPro)) AS CONTAR FROM v_existencias L WHERE L.F_ExiLot > 0 AND L.F_FecCad > DATE_ADD(CURDATE(),INTERVAL 7 DAY) AND F_Ubica NOT IN (%s) ;";

    public static String ContarClaveTodosCompra = "SELECT COUNT(DISTINCT(F_ClaPro)) AS CONTAR FROM tb_lote L WHERE L.F_ExiLot > 0 AND F_Ubica NOT IN ('CADUCADOS', 'PROXACADUCAR', 'MERMA', 'CROSSDOCKMORELIA', 'INGRESOS_V', 'CUARENTENA',  'ATI','NUEVA', 'AT','duplicado','CADUCADOSFARMACIA') ;";

    public static String ExistenciaProyectoTodosCompraGlobal = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica, CASE  WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  ESTATUS, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ExiLot>0 AND L.F_Ubica NOT IN (%s)  AND M.F_StsPro = 'A'  GROUP BY L.F_ClaPro, L.F_Proyecto;";

    public static String ExistenciaProyectoTodosCompraLote = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica, CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ExiLot>0 AND L.F_Ubica NOT IN (%s)  AND M.F_StsPro = 'A'  GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";

    public static String ExistenciaProyectoTodosCompraUbicacion = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica, CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ExiLot>0 AND L.F_Ubica NOT IN (%s)  AND M.F_StsPro = 'A'  GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto, L.F_Ubica;";

    public static String ExistenciaProyectoCompraOriCovidIsem = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri ,L.F_Ubica , CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ExiLot>0 AND F_Ubica NOT IN (%s) AND  L.F_Origen = '3'  AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";

    public static String ExistenciaProyectoCompraOriInsabi = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri ,L.F_Ubica , CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ExiLot>0 AND F_Ubica NOT IN (%s) AND  L.F_Origen = '4'  AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";

    public static String ExistenciaProyectoCompraOriCovidInsabi = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri ,L.F_Ubica , CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ExiLot>0 AND F_Ubica NOT IN (%s) AND  L.F_Origen = '5'  AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";

    public static String ExistenciaProyectoCompraOriIsem = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri ,L.F_Ubica , CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ExiLot>0 AND F_Ubica NOT IN (%s) AND  L.F_Origen IN ('1','2','8')  AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";

    public static String ExistenciaProyectoTodosNuevaLote = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica, CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id INNER JOIN tb_ubicanueva U ON L.F_Ubica = U.DescUbi COLLATE utf8_general_ci WHERE L.F_ExiLot>0 AND L.F_Origen <> '19'  AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";

    public static String ExistenciaProyectoNuevaLote = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica ,CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id INNER JOIN tb_ubicanueva U ON L.F_Ubica = U.DescUbi COLLATE utf8_general_ci WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0 AND L.F_Origen <> '19'  AND L.F_FecCad > CURDATE() AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";

    public static String ExistenciaProyectoComTexcoco = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri ,L.F_Ubica , CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE  L.F_Proyecto = ? AND L.F_ExiLot>0 AND F_Ubica NOT IN (%s) AND  L.F_Origen = '14'  AND L.F_FecCad > CURDATE() AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";
   /*fuente de financiamiento*/
    public static String ExistenciaProyectoComFuenteFinan = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri ,L.F_Ubica , CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L INNER JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza  FROM tb_compra where  F_FuenteFinanza != '' AND F_FuenteFinanza IS NOT NULL  group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE  L.F_Proyecto = ? AND L.F_ExiLot>0 AND F_Ubica NOT IN (%s)   AND L.F_FecCad > CURDATE() AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";
   
 ///Disponibles
    public static String ExistenciaProyectoCompraGlobalDis = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM( L.F_ExiLot ) AS F_ExiLot, O.F_DesOri, L.F_Ubica, CASE  WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  ESTATUS, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id  WHERE L.F_Proyecto = ? AND L.F_ExiLot > 0 AND F_Ubica NOT IN (%s)  AND L.F_FecCad > CURDATE() AND M.F_StsPro = 'A'  GROUP BY L.F_ClaPro, L.F_ClaLot, O.F_DesOri ORDER BY L.F_ClaPro, L.F_ClaLot, O.F_DesOri;";

    public static String ExistenciaProyectoCompraDis = "SELECT v.F_DesProy AS proyecto, v.F_ClaPro AS clave, SUBSTR(v.F_DesPro,1,40) AS Descrip, CASE WHEN IFNULL(a.F_ClaPro ,'') THEN 'APE' WHEN IFNULL(ct.F_ClaPro ,'') THEN 'CONTROLADO' WHEN IFNULL(rf.F_ClaPro ,'') THEN 'RED FRIA' ELSE 'SECO' END AS TipIns,v.F_ClaLot AS lote,v.F_FecCad AS caducidad, v.F_ExiLot AS cantidad,v.F_DesOri, CASE WHEN v.F_FecCad <= DATE_ADD(curdate(), INTERVAL 40 DAY) and ct.F_ClaPro THEN 'Caduco' WHEN v.F_FecCad BETWEEN DATE_ADD(curdate(), INTERVAL 40 DAY) AND DATE_ADD(curdate(), INTERVAL 180 DAY) AND ct.F_ClaPro THEN 'Disponible Insumo próximo a caducar' WHEN v.F_Origen = 14 THEN 'Disponible para Unidad Materno Infantil Texcoco' WHEN v.F_Ubica LIKE '%ACOPIO%' THEN 'ACOPIO' ELSE 'Disponible' END AS Sts, IFNULL(C.F_OrdenSuministro,'') as ordsum, v.F_DesUbi FROM v_existencias AS v LEFT JOIN tb_compra AS C ON v.F_FolLot = C.F_Lote AND v.F_ClaPro = C.F_ClaPro LEFT JOIN tb_ape AS a ON v.F_ClaPro = a.F_ClaPro LEFT JOIN tb_controlados AS ct ON v.F_ClaPro = ct.F_ClaPro LEFT JOIN tb_redfria AS rf ON v.F_ClaPro = rf.F_ClaPro WHERE v.F_FecCad > DATE_ADD(curdate(), INTERVAL 7 DAY) AND v.F_Ubica NOT IN (:ubicaciones) AND v.F_ClaPro NOT IN ('9999', '9998', '9996', '9995') GROUP BY v.F_FolLot,v.F_Ubica HAVING Sts NOT IN ('Caduco') ORDER BY clave ASC, lote ASC, caducidad ASC;";
   
 //SOLUGLOB
    public static String ExistenciaSoluglob = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri ,L.F_Ubica , CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ExiLot>0 AND L.F_Origen = '9'  AND L.F_Proyecto = ? AND F_Ubica NOT IN (%s) AND L.F_FecCad > CURDATE()  GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";
    public static String ExistenciaAbastoRegular = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri ,L.F_Ubica , CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ExiLot>0 AND L.F_Origen = '10'  AND L.F_Proyecto = ? AND F_Ubica NOT IN (%s) AND L.F_FecCad > CURDATE() GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";
    public static String ExistenciaUNOPS = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri ,L.F_Ubica , CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ExiLot>0 AND L.F_Origen = '11'  AND L.F_Proyecto = ? AND F_Ubica NOT IN (%s) AND L.F_FecCad > CURDATE() GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";
    public static String ExistenciaSCEstatal = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri ,L.F_Ubica , CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ExiLot>0 AND L.F_Origen = '13'  AND L.F_Proyecto = ? AND F_Ubica NOT IN (%s) AND L.F_FecCad > CURDATE() GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";
    public static String ExistenciaSCFederal = "SELECT P.F_DesProy, M.F_ClaProSS, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri ,L.F_Ubica , CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus, c.F_OrdenSuministro as suministro, M.F_NomGen, M.F_FormaFarm, M.F_Concentracion, IFNULL(c.F_FuenteFinanza,'') as FuenteFinanza, M.F_PrePro FROM tb_lote L LEFT JOIN (SELECT F_Lote, F_OrdenSuministro,F_FuenteFinanza FROM tb_compra where F_OrdenSuministro IS NOT NULL group by F_Lote) c on L.F_FolLot = c.F_Lote INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ExiLot>0 AND L.F_Origen = '12'  AND L.F_Proyecto = ? AND F_Ubica NOT IN (%s) AND L.F_FecCad > CURDATE() GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";

    //Consulta SC
    
    // Reporte Auditoria
    public static String ExistenciaAuditoria = "SELECT * FROM tb_reporteauditoria";
    public static String ContarClavesAuditoria = "SELECT COUNT(DISTINCT(clave)) AS CONTAR FROM tb_reporteauditoria";
    
    
    
//    public static String ExistenciaSoluglob = "SELECT P.F_DesProy, L.F_ClaPro, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri ,L.F_Ubica , M.F_ClaProSS,CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ExiLot>0 AND L.F_Origen = '9' AND L.F_ClaPro != '9999' AND L.F_Proyecto = ? GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";
//    
//    public static String ExistenciaAbastoRegular = "SELECT P.F_DesProy, L.F_ClaPro, SUBSTR(M.F_DesPro,1,40) AS F_DesPro, L.F_ClaLot, L.F_FecCad AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri ,L.F_Ubica , M.F_ClaProSS,CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ExiLot>0 AND L.F_Origen = '10' AND L.F_ClaPro != '9999' AND L.F_Proyecto = ? AND F_Ubica NOT IN (%s) GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";
    /*cliente*/
    //public static String BuscaProyectoCliente = "SELECT * FROM tb_proyectos WHERE F_Id IN (%s);";
    public static String BuscaProyectoCliente = "SELECT * FROM tb_proyectos order by F_DesProy;";
    public static String ContarClaveTodosClientes = "SELECT COUNT(DISTINCT(F_ClaPro)) AS CONTAR FROM tb_lote L WHERE L.F_ExiLot > 0 AND L.F_Proyecto IN (%s) AND L.F_ClaPro NOT IN ('9999','72722','9998','9996','9995');";

    //public static String ContarClaveTodosCompra = "SELECT COUNT(DISTINCT(F_ClaPro)) AS CONTAR FROM tb_lote L WHERE L.F_ExiLot > 0 AND F_Ubica NOT IN ('CADUCADOS','PROXACADUCAR','MERMA') AND L.F_FecCad > DATE_ADD(CURDATE(), INTERVAL 6 MONTH);";
    private final ConectionDBTrans con = new ConectionDBTrans();
    private PreparedStatement psBuscaProyecto;
    private PreparedStatement psConsulta;
    private ResultSet rs;

    @Override
    public JSONArray ObtenerProyectos() {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        try {
            con.conectar();
            psBuscaProyecto = con.getConn().prepareStatement(BuscaProyecto);
            rs = psBuscaProyecto.executeQuery();

            while (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("Id", rs.getString(1));
                jsonObj.put("Nombre", rs.getString(2));
                jsonArray.add(jsonObj);
            }
            //psBuscaProyecto.close();
            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

     public JSONArray MostrarRegistros(String Proyecto, String Tipo) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        String ConsultaExi = "";
        int Contar = 0, CantidadT = 0, ContarClave = 0;
        try {
            con.conectar();
            switch (Tipo) {
                case "1":
                    ConsultaExi = ExistenciaProyectoGlobal;
                    break;
                case "2":
                    ConsultaExi = ExistenciaProyectoLote;
                    break;
                case "3":
                    ConsultaExi = ExistenciaProyectoUbicacion;
                    break;
                default:
                    break;
            }

            psConsulta = con.getConn().prepareStatement(ContarClaveProyecto);
            psConsulta.setString(1, Proyecto);
            rs = psConsulta.executeQuery();
            if (rs.next()) {
                ContarClave = rs.getInt(1);
            }

            psConsulta.clearParameters();

            psConsulta = con.getConn().prepareStatement(ConsultaExi);
            psConsulta.setString(1, Proyecto);
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                Contar++;
                CantidadT = CantidadT + rs.getInt(6);
                jsonObj = new JSONObject();
                jsonObj.put("Proyecto", rs.getString(1));
                jsonObj.put("ClaPro", rs.getString(2));
                jsonObj.put("Descripcion", rs.getString(3));
                jsonObj.put("Lote", rs.getString(4));
                jsonObj.put("Caducidad", rs.getString(5));
                jsonObj.put("Cantidad", rs.getString(6));
                jsonObj.put("Origen", rs.getString(7));
                jsonObj.put("Ubicacion", rs.getString(8));
                jsonObj.put("Presentacion", rs.getString(9));
                jsonObj.put("Proveedor", rs.getString(11));
                jsonObj.put("Lugar", rs.getString(12));
                jsonObj.put("fuenteF", rs.getString(13));
                jsonObj.put("Tipo", Tipo);
                jsonObj.put("Contar", Contar);
                jsonObj.put("CantidadT", formatter.format(CantidadT));
                jsonObj.put("ContarClave", formatter.format(ContarClave));
                jsonObj.put("NomGen", rs.getString("NomGen"));
                jsonArray.add(jsonObj);
            }

            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }
     public JSONArray MostrarRegistrosFonsabi(String Proyecto, String Tipo) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        String ConsultaExi = "";
        int Contar = 0, CantidadT = 0, ContarClave = 0;
        try {
            con.conectar();
            switch (Tipo) {
                case "1":
                    ConsultaExi = ExistenciaProyectoGlobalFonsabi;
                    break;
                case "2":
                    ConsultaExi = ExistenciaProyectoLoteFonsabi;
                    break;
                case "3":
                    ConsultaExi = ExistenciaProyectoUbicacionFonsabi;
                    break;
                default:
                    break;
            }

            psConsulta = con.getConn().prepareStatement(ContarClaveProyecto);
            psConsulta.setString(1, Proyecto);
            rs = psConsulta.executeQuery();
            if (rs.next()) {
                ContarClave = rs.getInt(1);
            }

            psConsulta.clearParameters();

            psConsulta = con.getConn().prepareStatement(ConsultaExi);
            psConsulta.setString(1, Proyecto);
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                Contar++;
                CantidadT = CantidadT + rs.getInt(6);
                jsonObj = new JSONObject();
                jsonObj.put("Proyecto", rs.getString(1));
                jsonObj.put("ClaPro", rs.getString(2));
                jsonObj.put("Descripcion", rs.getString(3));
                jsonObj.put("Lote", rs.getString(4));
                jsonObj.put("Caducidad", rs.getString(5));
                jsonObj.put("Cantidad", rs.getString(6));
                jsonObj.put("Origen", rs.getString(7));
                jsonObj.put("Ubicacion", rs.getString(8));
                jsonObj.put("Presentacion", rs.getString(9));
                jsonObj.put("Proveedor", rs.getString(11));
                jsonObj.put("Lugar", rs.getString(12));
                jsonObj.put("OrdSuministro", rs.getString(13));
                jsonObj.put("Remision", rs.getString(14));
                jsonObj.put("OC", rs.getString(15));
                jsonObj.put("fuenteF", rs.getString(16));
                jsonObj.put("IdLote", rs.getString(17));
                jsonObj.put("NomGenerico", rs.getString(18));
                jsonObj.put("Tipo", Tipo);
                jsonObj.put("Contar", Contar);
                jsonObj.put("CantidadT", formatter.format(CantidadT));
                jsonObj.put("ContarClave", formatter.format(ContarClave));
                
                jsonArray.add(jsonObj);
            }

            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public JSONArray MostrarTodos(String Tipo) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        String ConsultaExiTodo = "";
        int Contar = 0, CantidadT = 0, ContarClave = 0;
        try {
            con.conectar();

            switch (Tipo) {
                case "1":
                    ConsultaExiTodo = ExistenciaProyectoTodosGlobal;
                    break;
                case "2":
                    ConsultaExiTodo = ExistenciaProyectoTodosLote;
                    break;
                case "3":
                    ConsultaExiTodo = ExistenciaProyectoTodosUbicacion;
                    break;
                default:
                    break;
            }

            psConsulta = con.getConn().prepareStatement(ContarClaveTodos);
            rs = psConsulta.executeQuery();
            if (rs.next()) {
                ContarClave = rs.getInt(1);
            }

            psConsulta.clearParameters();

            psConsulta = con.getConn().prepareStatement(ConsultaExiTodo);
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                Contar++;
                CantidadT = CantidadT + rs.getInt(6);
                jsonObj = new JSONObject();
                jsonObj.put("Proyecto", rs.getString(1));
                jsonObj.put("ClaPro", rs.getString(2));
                jsonObj.put("Descripcion", rs.getString(3));
                jsonObj.put("Lote", rs.getString(4));
                jsonObj.put("Caducidad", rs.getString(5));
                jsonObj.put("Cantidad", rs.getString(6));
                jsonObj.put("Origen", rs.getString(7));
                jsonObj.put("Ubicacion", rs.getString(8));
                jsonObj.put("Presentacion", rs.getString(9));
                jsonObj.put("Proveedor", rs.getString(11));
                jsonObj.put("Lugar", rs.getString(12));
                jsonObj.put("Tipo", Tipo);
                jsonObj.put("fuenteF", rs.getString(13));
                jsonObj.put("Contar", Contar);
                jsonObj.put("CantidadT", formatter.format(CantidadT));
                jsonObj.put("ContarClave", formatter.format(ContarClave));
                jsonArray.add(jsonObj);
            }

            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    
    }

    @Override
    public JSONArray ObtenerProyectosConsulta(String Proyecto) {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        try {
            con.conectar();
            psBuscaProyecto = con.getConn().prepareStatement(String.format(BuscaProyectoConsulta, Proyecto));
            rs = psBuscaProyecto.executeQuery();

            while (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("Id", rs.getString(1));
                jsonObj.put("Nombre", rs.getString(2));
                jsonArray.add(jsonObj);
            }
            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public JSONArray MostrarTodosConsulta(String Proyecto) {
        DecimalFormat formatters = new DecimalFormat("#,###,###");
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        int Contar = 0, CantidadT = 0, ContarClave = 0;
        try {
            con.conectar();
            psConsulta = con.getConn().prepareStatement(String.format(ContarClaveTodosCosulta, Proyecto));
            rs = psConsulta.executeQuery();
            if (rs.next()) {
                ContarClave = rs.getInt(1);
            }

            psConsulta.clearParameters();

            psConsulta = con.getConn().prepareStatement(String.format(ExistenciaProyectoConsulta, Proyecto));
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                Contar++;
                CantidadT = CantidadT + rs.getInt(6);
                jsonObj = new JSONObject();
                jsonObj.put("Proyecto", rs.getString(1));
                jsonObj.put("ClaPro", rs.getString(2));
                jsonObj.put("Descripcion", rs.getString(3));
                jsonObj.put("Lote", rs.getString(4));
                jsonObj.put("Caducidad", rs.getString(5));
                jsonObj.put("Cantidad", rs.getString(6));
                jsonObj.put("Origen", rs.getString(7));
                jsonObj.put("Contar", Contar);
                jsonObj.put("CantidadT", formatters.format(CantidadT));
                jsonObj.put("ContarClave", formatters.format(ContarClave));
                jsonArray.add(jsonObj);
            }

            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    /*COMPRAS TODOS*/
    
    @Override
    public JSONArray MostrarTodosCompra(String Tipo) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        JSONArray jsonArray = new JSONArray();
        ArrayList lista = new ArrayList();
        JSONObject jsonObj;
        String ConsultaExiTodo = "", Ubicaciones = "";
        int Contar = 0, CantidadT = 0, ContarClave = 0;
        try {
            con.conectar();

            switch (Tipo) {
                case "1":
                    ConsultaExiTodo = ExistenciaProyectoTodosCompraGlobal;
                    break;
                case "2":
                    ConsultaExiTodo = ExistenciaProyectoTodosCompraLote;
                    break;
                case "3":
                    ConsultaExiTodo = ExistenciaProyectoCompraOriCovidIsem;
                    break;
                case "4":
                    ConsultaExiTodo = ExistenciaProyectoCompraOriInsabi;
                    break;
                case "5":
                    ConsultaExiTodo = ExistenciaProyectoCompraOriCovidInsabi;
                    break;
                case "6":
                    ConsultaExiTodo = ExistenciaProyectoCompraOriIsem;
                    break;
                case "7":
                    ConsultaExiTodo = ExistenciaProyectoTodosNuevaLote;
                    break;
                case "8":
                    ConsultaExiTodo = ExistenciaProyectoCompraGlobalDis;
                    break;
                default:
                    ConsultaExiTodo = ExistenciaProyectoTodosCompraLote;
                    break;
            }

            psConsulta = con.getConn().prepareStatement(BuscaUbiNomostrar);
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                for (int i = 0; i < 1; i++) {
                    for (int j = 0; j < 1; j++) {
                        Ubicaciones = rs.getString(1);
//                            System.out.println(Ubicaciones);
                        lista.add(i, "'" + Ubicaciones + "'");
                    }

                }
            }
            Ubicaciones = lista.toString().replace("[", "").replace("]", "");
//                }

            System.out.println(Ubicaciones);
            psConsulta.clearParameters();
            psConsulta = con.getConn().prepareStatement(ContarClaveTodosCompra);
            rs = psConsulta.executeQuery();
            if (rs.next()) {
                ContarClave = rs.getInt(1);
            }

            psConsulta.clearParameters();

            psConsulta = con.getConn().prepareStatement(String.format(ConsultaExiTodo, Ubicaciones));
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                Contar++;
                CantidadT = CantidadT + rs.getInt(6);
                jsonObj = new JSONObject();
                jsonObj.put("Proyecto", rs.getString(1));
                jsonObj.put("ClaPro", rs.getString(2));
                jsonObj.put("Descripcion", rs.getString(3));
                jsonObj.put("Lote", rs.getString(4));
                jsonObj.put("Caducidad", rs.getString(5));
                jsonObj.put("Cantidad", rs.getString(6));
                jsonObj.put("Origen", rs.getString(7));
                jsonObj.put("Ubicacion", rs.getString(8));
                jsonObj.put("Tipo", Tipo);
                jsonObj.put("Contar", Contar);
                jsonObj.put("CantidadT", formatter.format(CantidadT));
                jsonObj.put("ContarClave", formatter.format(ContarClave));
                jsonObj.put("ClaProSS", rs.getString(9));
                jsonObj.put("Estatus", rs.getString(10));
                jsonArray.add(jsonObj);
            }

            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }
    
    
    /*COMPRAS POR PROYECTO*/
    @Override
    public JSONArray MostrarRegistrosCompra(String Proyecto, String Tipo) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        JSONArray jsonArray = new JSONArray();
        ArrayList lista = new ArrayList();
        JSONObject jsonObj;
        String ConsultaExi = "", Ubicaciones = "", Ubica = "";

        int Contar = 0, CantidadT = 0, ContarClave = 0, Contador = 0;
        try {
            con.conectar();
//////Tipo Existencias
            switch (Tipo) {
                case "1":
                    ConsultaExi = ExistenciaProyectoCompraGlobal;
                    break;
                case "2":
                    ConsultaExi = ExistenciaProyectoCompraLote;
                    break;
                case "3":
                    ConsultaExi = ExistenciaProyectoCompraCovidIsem;
                    break;
                case "4":
                    ConsultaExi = ExistenciaProyectoCompraInsabi;
                    break;
                case "5":
                    ConsultaExi = ExistenciaProyectoCompraCovidInsabi;
                    break;
                case "6":
                    ConsultaExi = ExistenciaProyectoCompraIsem;
                    break;
                case "7":
                    ConsultaExi = ExistenciaProyectoNuevaLote;
                    break;
                case "8":
                    ConsultaExi = ExistenciaProyectoCompraGlobalDis;
                    break;
                case "9":
                    ConsultaExi = ExistenciaSoluglob;
                    break;
                case "10":
                    ConsultaExi = ExistenciaAbastoRegular;
                    break;
                case "11":
                    ConsultaExi = ExistenciaUNOPS;
                    break;
                case "12":
                    ConsultaExi = ExistenciaSCEstatal;
                    break;
                case "13":
                    ConsultaExi = ExistenciaSCFederal;
                    break;    
                case "14":
                    ConsultaExi = ExistenciaProyectoComTexcoco;
                    break; 
                case "15":
                    ConsultaExi = ExistenciaProyectoComFuenteFinan;
                    break;    
                default:
                    ConsultaExi = ExistenciaProyectoCompraLote;
                    break;
            }

            psConsulta = con.getConn().prepareStatement(BuscaUbiNomostrar);
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                for (int i = 0; i < 1; i++) {
                    for (int j = 0; j < 1; j++) {
                        Ubicaciones = rs.getString(1);
//                            System.out.println(Ubicaciones);
                        lista.add(i, "'" + Ubicaciones + "'");
                    }

                }
            }
            Ubicaciones = lista.toString().replace("[", "").replace("]", "");
//                }

            System.out.println(Ubicaciones);
            psConsulta.clearParameters();

            psConsulta = con.getConn().prepareStatement(ContarClaveProyectoCompra);
            psConsulta.setString(1, Proyecto);
            rs = psConsulta.executeQuery();
            if (rs.next()) {
                ContarClave = rs.getInt(1);
            }

            psConsulta.clearParameters();

            psConsulta = con.getConn().prepareStatement(String.format(ConsultaExi, Ubicaciones));
            psConsulta.setString(1, Proyecto);
            rs = psConsulta.executeQuery();
            System.out.println( psConsulta);
            while (rs.next()) {
                Contar++;
                CantidadT = CantidadT + rs.getInt(6);
                jsonObj = new JSONObject();
                jsonObj.put("Proyecto", rs.getString(1));
                jsonObj.put("ClaPro", rs.getString(2));
                jsonObj.put("Descripcion", rs.getString(3));
                jsonObj.put("Lote", rs.getString(4));
                jsonObj.put("Caducidad", rs.getString(5));
                jsonObj.put("Cantidad", rs.getString(6));
                jsonObj.put("Origen", rs.getString(7));
                jsonObj.put("Ubicacion", rs.getString(8));
                jsonObj.put("Tipo", Tipo);
                jsonObj.put("Contar", Contar);
                jsonObj.put("CantidadT", formatter.format(CantidadT));
                jsonObj.put("ContarClave", formatter.format(ContarClave));
                jsonObj.put("Estatus", rs.getString(9));
                jsonObj.put("OrdenSuministro", rs.getString(10));
                jsonObj.put("NomGen", rs.getString(11));
                jsonObj.put("ForFar", rs.getString(12));
                jsonObj.put("concentracion", rs.getString(13));
                jsonObj.put("fuentefinanciamento", rs.getString(14));
                jsonObj.put("Presentacion", rs.getString(15));
                jsonArray.add(jsonObj);
            }

            con.cierraConexion();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }
    
    //COMPRAS FONSABI
    
    @Override
    public JSONArray MostrarRegistrosCompraFonsabi(String Proyecto, String Tipo) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        JSONArray jsonArray = new JSONArray();
        ArrayList lista = new ArrayList();
        JSONObject jsonObj;
        String ConsultaExi = "", Ubicaciones = "", Ubica = "";

        int Contar = 0, CantidadT = 0, ContarClave = 0, Contador = 0;
        try {
            con.conectar();
//////Tipo Existencias
System.out.println("tipo" + Tipo);
            switch (Tipo) {                
                
                case "2":
                    ConsultaExi = ExistenciaCompraLoteFonsabi;                   
                    break;
                 
                default:
                    ConsultaExi = ExistenciaCompraLoteFonsabi;
                    break;
            }

            psConsulta = con.getConn().prepareStatement(BuscaUbiNomostrar);
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                for (int i = 0; i < 1; i++) {
                    for (int j = 0; j < 1; j++) {
                        Ubicaciones = rs.getString(1);
//                            System.out.println(Ubicaciones);
                        lista.add(i, "'" + Ubicaciones + "'");
                    }

                }
            }
            Ubicaciones = lista.toString().replace("[", "").replace("]", "");
//                }

            
            psConsulta.clearParameters();

            psConsulta = con.getConn().prepareStatement(ContarClaveProyectoCompra);
            psConsulta.setString(1, Proyecto);
            rs = psConsulta.executeQuery();
            if (rs.next()) {
                ContarClave = rs.getInt(1);
            }

            psConsulta.clearParameters();
            System.out.println("aun no");
            psConsulta = con.getConn().prepareStatement(String.format(ConsultaExi, Ubicaciones));
            psConsulta.setString(1, Proyecto);
            rs = psConsulta.executeQuery();
            System.out.println("entre");
            System.out.println(Tipo);
            System.out.println( psConsulta);
            while (rs.next()) {
                Contar++;
                CantidadT = CantidadT + rs.getInt(6);
                jsonObj = new JSONObject();
                jsonObj.put("Proyecto", rs.getString(1));
                jsonObj.put("ClaPro", rs.getString(2));
                jsonObj.put("Descripcion", rs.getString(3));
                jsonObj.put("Lote", rs.getString(4));
                jsonObj.put("Caducidad", rs.getString(5));
                jsonObj.put("Cantidad", rs.getString(6));
                jsonObj.put("Origen", rs.getString(7));
                jsonObj.put("Ubicacion", rs.getString(8));
                jsonObj.put("OrdenSuministro", rs.getString(9));
                jsonObj.put("UnidadEntrega", rs.getString(10));               
                jsonObj.put("NomGenerico", rs.getString(11));               
                jsonObj.put("Presentacion", rs.getString(12));               
                jsonObj.put("Tipo", Tipo);
                jsonObj.put("Contar", Contar);
                jsonObj.put("CantidadT", formatter.format(CantidadT));
                jsonObj.put("ContarClave", formatter.format(ContarClave));
                
                
                jsonArray.add(jsonObj);
            }

            con.cierraConexion();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }
    
   /*COMPRAS POR PROGRAMA*/
    @Override
    public JSONArray MostrarRegistrosCompraPrograma(String Proyecto, String Tipo) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        JSONArray jsonArray = new JSONArray();
        ArrayList lista = new ArrayList();
        JSONObject jsonObj;
        String ConsultaExi = "", Ubicaciones = "",clapro = "";

        int Contar = 0, CantidadT = 0, ContarClave = 0, Contador = 0;
        try {
            con.conectar();
//////Tipo Existencias
            switch (Tipo) {
                case "1":
                    ConsultaExi = ExistenciaProyectoCompraGlobalPrograma;
                    break;
                case "2":
                    ConsultaExi = ExistenciaProyectoCompraLotePrograma;
                    break;
                    
                default:
                    ConsultaExi = ExistenciaProyectoCompraGlobalPrograma;
                    break;
            }

            psConsulta = con.getConn().prepareStatement(BuscaUbiNomostrar);
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                for (int i = 0; i < 1; i++) {
                    for (int j = 0; j < 1; j++) {
                        Ubicaciones = rs.getString(1);
                        lista.add(i, "'" + Ubicaciones + "'");
                    }

                }
            }
            Ubicaciones  = lista.toString().replace("[", "").replace("]", "");


          //  System.out.println(Ubicaciones );
            psConsulta.clearParameters();

/*buscar claves consulta*/
            psConsulta = con.getConn().prepareStatement(BuscaclavePrograma);
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                for (int i = 0; i < 1; i++) {
                    for (int j = 0; j < 1; j++) {
                        clapro = rs.getString(1);
                        lista.add(i, "'" + clapro + "'");
                    }

                }
            }
           clapro = lista.toString().replace("[", "").replace("]", "");
            System.out.println(clapro);
            psConsulta.clearParameters();

            psConsulta = con.getConn().prepareStatement(ContarClaveProyectoCompra);
            psConsulta.setString(1, Proyecto);
            rs = psConsulta.executeQuery();
            if (rs.next()) {
                ContarClave = rs.getInt(1);
            }

            psConsulta.clearParameters();

            psConsulta = con.getConn().prepareStatement(String.format(ConsultaExi, Ubicaciones, clapro));
            psConsulta.setString(1, Proyecto);
            rs = psConsulta.executeQuery();
            System.out.println("consulta: "+ psConsulta);
            while (rs.next()) {
                Contar++;
                CantidadT = CantidadT + rs.getInt(6);
                jsonObj = new JSONObject();
                jsonObj.put("Proyecto", rs.getString(1));
                jsonObj.put("ClaPro", rs.getString(2));
                jsonObj.put("Descripcion", rs.getString(3));
                jsonObj.put("Lote", rs.getString(4));
                jsonObj.put("Caducidad", rs.getString(5));
                jsonObj.put("Cantidad", rs.getString(6));
                jsonObj.put("Origen", rs.getString(7));
                jsonObj.put("Ubicacion", rs.getString(8));
                jsonObj.put("Tipo", Tipo);
                jsonObj.put("Contar", Contar);
                jsonObj.put("CantidadT", formatter.format(CantidadT));
                jsonObj.put("ContarClave", formatter.format(ContarClave));
                jsonObj.put("Estatus", rs.getString(9));
                jsonObj.put("OrdenSuministro", rs.getString(10));
                jsonObj.put("NomGen", rs.getString(11));
                jsonObj.put("ForFar", rs.getString(12));
                jsonObj.put("concentracion", rs.getString(13));
                jsonObj.put("fuentefinanciamento", rs.getString(14));
                jsonArray.add(jsonObj);
            }

            con.cierraConexion();
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    ///CLIENTES
    @Override
    public JSONArray ObtenerProyectosClientes(String ProyectoCL) {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        try {
            con.conectar();
            psBuscaProyecto = con.getConn().prepareStatement(String.format(BuscaProyectoCliente, ProyectoCL));
            rs = psBuscaProyecto.executeQuery();

            while (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("Id", rs.getString(1));
                jsonObj.put("Nombre", rs.getString(2));
                jsonArray.add(jsonObj);
            }
            //psBuscaProyecto.close();
            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    ///cliente
    @Override
    public JSONArray MostrarTodosClientes(String ProyectoCL) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        int Contar = 0, CantidadT = 0, ContarClave = 0;
        try {
            con.conectar();

            /*psConsulta = con.getConn().prepareStatement(String.format(ContarClaveTodosClientes, ProyectoCL));
            rs = psConsulta.executeQuery();
            if (rs.next()) {
                ContarClave = rs.getInt(1);
            }*/
            //psConsulta.clearParameters();
            psConsulta = con.getConn().prepareStatement(String.format(ExistenciaProyectoTodosClientes, ProyectoCL));
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                Contar++;
                CantidadT = CantidadT + rs.getInt(6);
                jsonObj = new JSONObject();
                jsonObj.put("Proyecto", rs.getString(1));
                jsonObj.put("ClaPro", rs.getString(2));
                jsonObj.put("Descripcion", rs.getString(3));
                jsonObj.put("Lote", rs.getString(4));
                jsonObj.put("Caducidad", rs.getString(5));
                jsonObj.put("Cantidad", rs.getString(6));
                jsonObj.put("Origen", rs.getString(7));
                jsonObj.put("Contar", Contar);
                jsonObj.put("CantidadT", formatter.format(CantidadT));
                jsonObj.put("ContarClave", formatter.format(ContarClave));
                jsonArray.add(jsonObj);
            }

            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public JSONArray ObtenerTipoUnidad() {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        try {
            con.conectar();
            psBuscaProyecto = con.getConn().prepareStatement(BuscaTipoUnidad);
            rs = psBuscaProyecto.executeQuery();

            while (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("Nombre", rs.getString(1));
                jsonObj.put("Id", rs.getString(1));
                jsonArray.add(jsonObj);
            }
            //psBuscaProyecto.close();
            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public JSONArray MostrarRegistrosFact(String TipoUnidad, String Fecha) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        String ConsultaExi = "";
        int Contar = 0, CantidadT = 0, ContarClave = 0;
        try {
            con.conectar();

            psConsulta = con.getConn().prepareStatement(FacturadoFechaTipo);
            psConsulta.setString(1, Fecha);
            psConsulta.setString(2, TipoUnidad);
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                CantidadT = CantidadT + rs.getInt(2);
                jsonObj = new JSONObject();
                jsonObj.put("Clave", rs.getString(1));
                jsonObj.put("Cantidad", rs.getString(2));
                jsonObj.put("Tipo", rs.getString(3));
                jsonObj.put("CantidadT", formatter.format(CantidadT));
                jsonObj.put("TipoConsulta", "1");
                jsonArray.add(jsonObj);
            }

            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public JSONArray MostrarTodosFact(String Fecha) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        String ConsultaExiTodo = "";
        int Contar = 0, CantidadT = 0, ContarClave = 0;
        try {
            con.conectar();

            psConsulta = con.getConn().prepareStatement(FacturadoFecha);
            psConsulta.setString(1, Fecha);
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                CantidadT = CantidadT + rs.getInt(2);
                jsonObj = new JSONObject();
                jsonObj.put("Clave", rs.getString(1));
                jsonObj.put("Cantidad", rs.getString(2));
                jsonObj.put("Tipo", rs.getString(3));
                jsonObj.put("CantidadT", formatter.format(CantidadT));
                jsonObj.put("TipoConsulta", "2");
                jsonArray.add(jsonObj);
            }
            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public JSONArray MostrarAuditoria(String Tipo) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        JSONArray jsonArray = new JSONArray();
        ArrayList lista = new ArrayList();
        JSONObject jsonObj;
        String ConsultaExiTodo = "", Ubicaciones = "";
        int Contar = 0, CantidadT = 0, ContarClave = 0;
        try {
            con.conectar();

            switch (Tipo) {
                case "1":
                    ConsultaExiTodo = ExistenciaAuditoria;
                    break;
                    default:
                    ConsultaExiTodo = ExistenciaAuditoria;
                    break;
                
            }
            
            psConsulta = con.getConn().prepareStatement(ContarClavesAuditoria);
            rs = psConsulta.executeQuery();
            if (rs.next()) {
                ContarClave = rs.getInt(1);
            }

            psConsulta.clearParameters();

            psConsulta = con.getConn().prepareStatement(String.format(ConsultaExiTodo));
            rs = psConsulta.executeQuery();
            while (rs.next()) {
                
                Contar++;
                CantidadT = CantidadT + rs.getInt(8);
                jsonObj = new JSONObject();
                jsonObj.put("Proyecto", rs.getString(2));
                jsonObj.put("ClaPro", rs.getString(3));
                jsonObj.put("ClaProSS", rs.getString(4));
                jsonObj.put("Descripcion", rs.getString(5));
                jsonObj.put("Lote", rs.getString(6));
                jsonObj.put("Caducidad", rs.getString(7));
                jsonObj.put("Cantidad", rs.getString(8));
                jsonObj.put("Origen", rs.getString(9));
                jsonObj.put("Estatus", rs.getString(10));
                jsonObj.put("OrdenCompra", rs.getString(11));
                jsonObj.put("Contar", Contar);
                jsonObj.put("CantidadT", formatter.format(CantidadT));
                jsonObj.put("ContarClave", formatter.format(ContarClave));
                 jsonObj.put("Tipo", Tipo);
                jsonArray.add(jsonObj);
            }

            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

 @Override
    public JSONArray MostrarRegistrosCompraDis(String Proyecto, String Tipo) {
        DecimalFormat formatter = new DecimalFormat("#,###,###");
        JSONArray jsonArray = new JSONArray();
        ArrayList lista = new ArrayList();
        JSONObject jsonObj;
        String ConsultaExi = "", Ubicaciones = "";

//     System.out.println("si entro en reporte");
        int Contar = 0, CantidadT = 0, ContarClave = 0;
        try {
            con.conectar();
            //////Tipo Existencias
            switch (Tipo) {
                case "1":
                    ConsultaExi = ExistenciaProyectoCompraDis;
                    System.out.println(ExistenciaProyectoCompraDis);
                    break;
                default:
                    ConsultaExi = ExistenciaProyectoCompraDis;
                    System.out.println(ExistenciaProyectoCompraDis);
                    break;
            }

            psConsulta = con.getConn().prepareStatement(BuscaUbiNomostrar);
            rs = psConsulta.executeQuery();
            
            while (rs.next()) {
                for (int i = 0; i < 1; i++) {
                    for (int j = 0; j < 1; j++) {
                        Ubicaciones = rs.getString(1);
                        lista.add(i, "'" + Ubicaciones + "'");
                    }

                }
            }
            Ubicaciones = lista.toString().replace("[", "").replace("]", "");

            System.out.println(Ubicaciones);

            psConsulta.clearParameters();

            psConsulta = con.getConn().prepareStatement(String.format(ContarClaveProyectoCompraDis , Ubicaciones));
//            psConsulta.setString(1, "ISEM');
            rs = psConsulta.executeQuery();
            if (rs.next()) {
                ContarClave = rs.getInt(1);
            }
            psConsulta.clearParameters();
            System.out.println("ConsultaExi: "+ConsultaExi);
           psConsulta = con.getConn().prepareStatement(ConsultaExi.replace(":ubicaciones", Ubicaciones));
            rs = psConsulta.executeQuery();
        
            System.out.println( psConsulta);
            while (rs.next()) {
                Contar++;
                CantidadT = CantidadT + rs.getInt(7);
                jsonObj = new JSONObject();
                jsonObj.put("Proyecto", rs.getString(1));
                jsonObj.put("ClaPro", rs.getString(2));
                jsonObj.put("Descripcion", rs.getString(3));
                jsonObj.put("Tipo", rs.getString(4));
                jsonObj.put("Lote", rs.getString(5));
                jsonObj.put("Caducidad", rs.getString(6));
                jsonObj.put("Cantidad", rs.getString(7));
                jsonObj.put("Origen", rs.getString(8));
                jsonObj.put("Estatus", rs.getString(9));
                jsonObj.put("OrdenSuministro", rs.getString(10));
                jsonObj.put("Ubicacion", rs.getString(11));
                jsonObj.put("Contar", Contar);
                jsonObj.put("CantidadT", formatter.format(CantidadT));
                jsonObj.put("ContarClave", formatter.format(ContarClave));
                jsonArray.add(jsonObj);
            }

            con.cierraConexion();
        } catch (SQLException ex) {
ex.getStackTrace();
ex.getMessage();
            Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (SQLException ex) {
                Logger.getLogger(ExistenciaProyectoDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }


}
