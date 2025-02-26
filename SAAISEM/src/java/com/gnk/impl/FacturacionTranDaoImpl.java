/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.gnk.impl;

import Correo.CorreoFactIncidente;

import static Develuciones.ConsultaDevoDaoImpl.getFactorEmpaque;
import com.gnk.dao.FacturacionTranDao;
import com.gnk.model.DetalleFactura;
//import com.gnk.model.FacturaTemporal;
import com.gnk.model.FacturacionModel;
import com.medalfa.saa.vo.ParametrosFolio;
import conn.ConectionDBTrans;
import in.co.sneh.service.AbastoService;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Class Implementación de FacturacionTranDao (Facturación automática
 * transaccional)
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */

public class FacturacionTranDaoImpl implements FacturacionTranDao {

    ////////////////fACTURACION GENERAL //////////////////////
    public static String BUSCA_UNIDADESTEMP = "SELECT U.F_ClaUni FROM tb_unireq U INNER JOIN tb_medica M ON U.F_ClaPro=M.F_ClaPro WHERE F_ClaUni IN (%s) and F_Status='2' and F_Solicitado!=0 AND M.F_StsPro='A' GROUP BY U.F_ClaUni order by U.F_ClaUni ASC;";

    //******************************************************************/////
    // ///////////////////////////FACTURACION AUTOMATICA /////////
    public static final String ACTUALIZA_INDICEFACT = "UPDATE tb_indice SET F_IndFactP%s = ?;";
    //public static String BUSCA_UNIDADFACTURA2FOLIO2Controlado = "SELECT f.F_ClaPro, SUM(F_CantReq) AS F_CantReq, SUM(F_CantSur) AS F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, SUM(F_Costo) AS F_Costo, SUM(F_Iva) AS F_Iva, SUM(F_Monto) AS F_Monto, F_OC, f.F_IdFact, o.F_TipOri FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica inner join tb_origen o ON o.F_ClaOri = l.F_Origen WHERE F_ClaCli = ? AND (f.F_Ubicacion LIKE '%CONTROLADO%' AND f.F_ClaPro IN (SELECT F_ClaPro FROM tb_controlados)) AND F_User = ? AND F_CantSur > 0 AND F_FecCad > CURDATE()+7 GROUP BY F_ClaPro, F_Lote, F_Ubicacion order by F_FecCad;";
public static String BUSCA_UNIDADFACTURA2FOLIO2Controlado = "SELECT f.F_ClaPro, SUM(F_CantReq) AS F_CantReq, SUM(F_CantSur) AS F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, SUM(F_Costo) AS F_Costo, SUM(F_Iva) AS F_Iva, SUM(F_Monto) AS F_Monto, F_OC, f.F_IdFact, o.F_TipOri FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica inner join tb_origen o ON o.F_ClaOri = l.F_Origen WHERE F_ClaCli = ? AND f.F_Ubicacion Rlike 'CTRFO|CONTROLADO|CTRL'  AND F_User = ? AND F_CantSur > 0 AND F_FecCad >= CURDATE() GROUP BY F_ClaPro, F_Lote, F_Ubicacion order by F_FecCad;";

    public static String BUSCA_UNIDADFACTURA2FOLIO2 = "SELECT f.F_ClaPro, SUM(F_CantReq) AS F_CantReq, SUM(F_CantSur) AS F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, SUM(F_Costo) AS F_Costo, SUM(F_Iva) AS F_Iva, SUM(F_Monto) AS F_Monto, F_OC, f.F_IdFact, o.F_TipOri FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica inner join tb_origen o ON o.F_ClaOri = l.F_Origen WHERE F_ClaCli = ? AND f.F_DocAnt = 0 AND F_User = ? and F_FecCad >= CURDATE() GROUP BY F_ClaPro, F_Lote, F_Ubicacion order by F_FecCad;";

    //***************************************************************//
    //////////////FACTURACION RECETARIOS
    public static String BUSCA_LOTE = "SELECT F_IdLote,F_ExiLot FROM tb_lote WHERE F_IdLote = ? AND F_ExiLot!=0 ORDER BY F_Origen, F_FecCad ASC;";
    public static String BUSCA_FACTEMPLOTE = "SELECT F_Id, F_IdFact, F_ClaCli,F_FecEnt FROM tb_facttemplote WHERE F_StsFact=3 AND F_User = ? AND F_ClaCli = ? GROUP BY F_IdFact;";
    public static final String ACTUALIZA_REQIdLote = "update tb_unireqlote set F_PiezasReq = ?, F_Obs = ? where F_ClaPro = ? and F_ClaUni = ? and F_IdReq=? and F_Status='0';";
    public static String BUSCA_DATOSFACTLOTE = "SELECT f.F_ClaCli, l.F_FolLot, l.F_IdLote, l.F_ClaPro, l.F_ClaLot, l.F_FecCad, m.F_TipMed, m.F_Costo, p.F_ClaProve, f.F_Cant, l.F_ExiLot, l.F_Ubica, f.F_IdFact, f.F_Id, f.F_FecEnt, f.F_CantSol, l.F_ClaOrg, l.F_FecFab, l.F_Cb, l.F_ClaMar, l.F_Origen, l.F_UniMed FROM tb_facttemplote f INNER JOIN tb_lote l ON f.F_IdLot = l.F_IdLote INNER JOIN tb_medica m ON l.F_ClaPro = m.F_ClaPro INNER JOIN tb_proveedor p ON l.F_ClaOrg = p.F_ClaProve WHERE f.F_IdFact = ? and f.F_StsFact<5 AND f.F_ClaCli = ?;";
    public static final String ACTUALIZA_EXILOTE = "UPDATE tb_lote SET F_ExiLot= (F_ExiLot - (F_ExiLot - ?)) WHERE F_IdLote=?;";
    public static final String ACTUALIZA_FACTTEMLOTE = "UPDATE tb_facttemplote SET F_StsFact='5' WHERE F_Id=?;";
    public static String BUSCA_EXITLOT = "SELECT SUM(L.F_ExiLot) AS F_ExiLot,MOVI.F_CantMov,COUNT(F_Ubica) AS Contar,IF(SUM(L.F_ExiLot)>=IF(IFNULL(MOVI.F_CantMov,0)<0,0,IFNULL(MOVI.F_CantMov,0)),IF(IFNULL(MOVI.F_CantMov,0)<0,0,IFNULL(MOVI.F_CantMov,0)),SUM(L.F_ExiLot)) AS EXILOT FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ProMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv WHERE F_ProMov=%s AND F_UbiMov IN (%s) GROUP BY F_ProMov) AS MOVI ON L.F_ClaPro=MOVI.F_ProMov WHERE L.F_Ubica IN (%s) AND L.F_ExiLot>0 AND M.F_N%d='1' AND M.F_StsPro='A' AND L.F_ClaPro=%s;";

    ////////////////////////////FIN LOTE
    public static final String BUSCAR_LOTE_FACTEM = "SELECT * FROM tb_facttemp WHERE F_IdLot = ? AND F_ClaCli = ? AND F_User = ? AND F_StsFact = 3;";

    public static final String INSERTAR_FACTTEM = "INSERT INTO tb_facttemp VALUES(?,?,?,?,?,3,0,?,?,0);";

    public static final String UPDATE_FACTTEM = "UPDATE tb_facttemp SET F_Cant = F_Cant + ?, F_CantSol = F_CantSol + ? where F_Id = ?;";

    public static String BUSCA_FACTEMP = "SELECT F_Id, F_IdFact, F_ClaCli,F_FecEnt FROM tb_facttemp WHERE F_StsFact=3 AND F_User = ? GROUP BY F_IdFact;";

    public static String BUSCA_INDICEFACT = "SELECT F_IndFactP%s FROM tb_indice;";

    public static String BUSCA_INDICETRANSPRODUCTO = "SELECT F_IndTProducto FROM tb_indice;";

    public static final String ACTUALIZA_INDICEFACTCERO = "UPDATE tb_indice SET F_IndFactP%s = ?;";

    public static final String ACTUALIZA_INDICETRANSPRODUCTO = "UPDATE tb_indice SET F_IndTProducto=?;";

    public static String BUSCA_DATOSFACT = "SELECT f.F_ClaCli, l.F_FolLot, l.F_IdLote, l.F_ClaPro, l.F_ClaLot, l.F_FecCad, m.F_TipMed, m.F_Costo, p.F_ClaProve, f.F_Cant, l.F_ExiLot, l.F_Ubica, f.F_IdFact, f.F_Id, f.F_FecEnt, f.F_CantSol, l.F_ClaOrg, l.F_FecFab, l.F_Cb, l.F_ClaMar, l.F_Origen, l.F_UniMed FROM tb_facttemp f INNER JOIN tb_lote l ON f.F_IdLot = l.F_IdLote INNER JOIN tb_medica m ON l.F_ClaPro = m.F_ClaPro INNER JOIN tb_proveedor p ON l.F_ClaOrg = p.F_ClaProve WHERE f.F_IdFact = ? and f.F_StsFact<5 AND f.F_ClaCli = ?;";

//    public static String BUSCA_DATOSFACTSEMI = "SELECT f.F_ClaCli, l.F_FolLot, l.F_IdLote, l.F_ClaPro, l.F_ClaLot, l.F_FecCad, m.F_TipMed, m.F_Costo, p.F_ClaProve, f.F_Cant, l.F_ExiLot, l.F_Ubica, f.F_IdFact, f.F_Id, f.F_FecEnt, f.F_CantSol, l.F_ClaOrg, l.F_FecFab, l.F_Cb, l.F_ClaMar, l.F_Origen, l.F_UniMed, c.F_Cause FROM tb_facttemp f INNER JOIN tb_lote l ON f.F_IdLot = l.F_IdLote INNER JOIN tb_medica m ON l.F_ClaPro = m.F_ClaPro INNER JOIN tb_proveedor p ON l.F_ClaOrg = p.F_ClaProve INNER JOIN tb_catalogoprecios c ON l.F_ClaPro=c.F_ClaPro AND l.F_Proyecto=c.F_Proyecto WHERE f.F_IdFact = ? and f.F_StsFact<5 AND f.F_ClaCli = ? AND c.F_Cause = ?;";
    public static final String ACTUALIZA_EXILOTEPRODUCTO = "UPDATE tb_lote SET F_ExiLot = (F_ExiLot - (F_ExiLot - ?)) WHERE F_ClaPro = ? AND F_ClaLot = ? AND F_FecCad = ? AND F_Origen = ? AND F_Proyecto = ? AND F_FolLot = ? AND F_Ubica = ?;";

    public static String BUSCA_INDICEMOV = "SELECT F_IndMov FROM tb_indice;";

    public static final String ACTUALIZA_INDICEMOV = "UPDATE tb_indice SET F_IndMov=?;";

    public static final String INSERTA_MOVIMIENTO = "INSERT INTO tb_movinv VALUES(0,curdate(),?,?,?,?,?,?,?,?,?,?,curtime(),?,'');";

    public static final String INSERTA_FACTURA = "INSERT INTO tb_factura VALUES(0,?,?,'A',curdate(),?,?,?,?,?,?,?,?,curtime(),?,?,'',0,?,?,?,?);";

    public static final String INSERTA_FACTURATEMP = "INSERT INTO tb_facturatemp VALUES(0,?,?,'A',curdate(),?,?,?,?,?,?,?,?,curtime(),?,?,?,0,?,?,?,?);";

    public static final String ACTUALIZA_FACTTEM = "UPDATE tb_facttemp SET F_StsFact='5' WHERE F_Id=?;";

    public static final String INSERTA_OBSFACTURA = "INSERT INTO tb_obserfact VALUES (?, ?, 0, 'M', ?, ?);";

    public static String BUSCA_REQ = "SELECT U.F_ClaPro, F_ClaUni FROM tb_unireq U INNER JOIN tb_medica M ON U.F_ClaPro=M.F_ClaPro WHERE F_ClaUni in(?) AND F_Status=0 AND  F_Solicitado != 0 AND M.F_StsPro='A' AND F_N?='1';";

    public static final String ACTUALIZA_REQ = "update tb_unireq set F_PiezasReq = ? where F_ClaPro = ? and F_ClaUni = ? and F_Status='0';";

    //AUTOMATICO PASO 2
    public static final String ACTUALIZA_REQId = "update tb_unireq set F_PiezasReq = ?, F_Obs = ? where F_ClaPro = ? and F_ClaUni = ? and F_IdReq = ? and F_Status = '0';";

//    public static final String ACTUALIZA_REQIdCause = "update tb_unireq set F_PiezasReq = ?,F_Obs=? where F_ClaPro = ? and F_ClaUni = ? and F_IdReq=? and F_Status='0';";
    //  public static String BUSCA_PARAMETRO = "SELECT PU.F_Id,P.F_Id, IFNULL(P.F_DesProy, '') AS Proyecto FROM tb_parametrousuario PU LEFT JOIN ( SELECT F_Id, F_DesProy FROM tb_proyectos ) P ON PU.F_Proyecto = P.F_Id WHERE F_Usuario = ?;";
    public static String BUSCA_PARAMETRO = "SELECT PU.F_Id, P.F_Id AS PROYECTO, UF.F_UbicaSQL, PU.F_Parametro FROM tb_parametrousuario AS PU INNER JOIN tb_proyectos AS P ON PU.F_Proyecto = P.F_Id INNER JOIN tb_ubicafact AS UF ON PU.F_Id = UF.F_idUbicaFac WHERE PU.F_Usuario = ?;";

    public static String BUSCA_UBISOLUCION = "SELECT * FROM tb_ubicasoluciones;";

    public static String BUSCA_DATOSFACTURA = "SELECT U.F_ClaUni,U.F_ClaPro, F_PiezasReq as piezas, F_IdReq,F_Solicitado as F_Solicitado FROM tb_unireq U INNER JOIN tb_medica M ON U.F_ClaPro=M.F_ClaPro WHERE F_ClaUni IN (?) and F_Status='0' and F_Solicitado!=0 AND M.F_StsPro='A' AND F_N?='1' order by U.F_ClaUni ASC,U.F_ClaPro+0;";

    
    /**CONTROLADO*///
    public static String BUSCA_DATOSporFACTURARCONTR = "SELECT U.F_ClaUni,U.F_ClaPro, F_PiezasReq as piezas, F_IdReq,F_Solicitado as F_Solicitado,IFNULL(LOTE.F_ExiLot,0),IFNULL(MOVI.F_CantMov,0),IFNULL(LOTE.F_ExiLot,0) AS EXISTENCIAS,LOTE.F_FolLot,LOTE.F_Ubica,U.F_Obs, ua.F_Tipo FROM tb_unireq U INNER JOIN tb_uniatn ua ON U.F_ClaUni = ua.F_ClaCli INNER JOIN tb_medica M ON U.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT l.F_ClaPro,SUM(F_ExiLot) AS F_ExiLot,F_FolLot,F_Ubica FROM tb_lote l INNER JOIN tb_origen o ON o.F_ClaOri = l.F_Origen %s AND F_Proyecto = ? AND l.F_FecCad > CURDATE() GROUP BY l.F_ClaPro) AS LOTE ON U.F_ClaPro=LOTE.F_ClaPro LEFT JOIN (SELECT F_ProMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica %s AND L.F_Proyecto = ? GROUP BY F_ProMov) AS MOVI ON U.F_ClaPro=MOVI.F_ProMov INNER JOIN tb_controlados ctr on U.F_ClaPro = ctr.F_ClaPro WHERE F_ClaUni IN (%s) and F_Status='0' and F_Solicitado!=0 AND M.F_StsPro='A' AND F_N%d='1' order by U.F_ClaUni ASC,U.F_ClaPro+0;";

    public static String BUSCA_DATOSporFACTURARCONTRCADUCA = "SELECT U.F_ClaUni,U.F_ClaPro, F_PiezasReq as piezas, F_IdReq,F_Solicitado as F_Solicitado,IFNULL(LOTE.F_ExiLot,0),IFNULL(MOVI.F_CantMov,0),IFNULL(LOTE.F_ExiLot,0) AS EXISTENCIAS,LOTE.F_FolLot,LOTE.F_Ubica,U.F_Obs, ua.F_Tipo FROM tb_unireq U INNER JOIN tb_uniatn ua ON U.F_ClaUni = ua.F_ClaCli INNER JOIN tb_medica M ON U.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT l.F_ClaPro,SUM(F_ExiLot) AS F_ExiLot,F_FolLot,F_Ubica FROM tb_lote l INNER JOIN tb_origen o ON o.F_ClaOri = l.F_Origen %s AND F_Proyecto = ? AND l.F_FecCad < CURDATE() GROUP BY l.F_ClaPro) AS LOTE ON U.F_ClaPro=LOTE.F_ClaPro LEFT JOIN (SELECT F_ProMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica %s AND L.F_Proyecto = ? GROUP BY F_ProMov) AS MOVI ON U.F_ClaPro=MOVI.F_ProMov INNER JOIN tb_controlados ctr on U.F_ClaPro = ctr.F_ClaPro WHERE F_ClaUni IN (%s) and F_Status='0' and F_Solicitado!=0 AND M.F_StsPro='A' AND F_N%d='1' order by U.F_ClaUni ASC,U.F_ClaPro+0;";

    
//public static String BUSCA_DATOSporFACTURARCONTR = "SELECT U.F_ClaUni,U.F_ClaPro, F_PiezasReq as piezas, F_IdReq,F_Solicitado as F_Solicitado,IFNULL(LOTE.F_ExiLot,0),IFNULL(MOVI.F_CantMov,0),IFNULL(LOTE.F_ExiLot,0) AS EXISTENCIAS,LOTE.F_FolLot,LOTE.F_Ubica,U.F_Obs, LOTE.F_TipOri FROM tb_unireq U INNER JOIN tb_medica M ON U.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT tb_lote.F_ClaPro,SUM(F_ExiLot) AS F_ExiLot,F_FolLot,F_Ubica, F_TipOri FROM tb_lote, tb_origen %s AND F_Proyecto = ? AND tb_origen.F_ClaOri = tb_lote.F_Origen AND tb_lote.F_FecCad > DATE_ADD(CURDATE(), INTERVAL 40 DAY) GROUP BY tb_lote.F_ClaPro) AS LOTE ON U.F_ClaPro=LOTE.F_ClaPro LEFT JOIN (SELECT F_ProMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica %s AND L.F_Proyecto = ? GROUP BY F_ProMov) AS MOVI ON U.F_ClaPro=MOVI.F_ProMov inner join tb_controlados ctr on U.F_ClaPro = ctr.F_ClaPro WHERE F_ClaUni IN (%s) and F_Status='0' and F_Solicitado!=0 AND M.F_StsPro='A' AND F_N%d='1' order by U.F_ClaUni ASC,U.F_ClaPro+0;";
/*****/////////////
    public static String BUSCA_DATOSporFACTURAR = "SELECT U.F_ClaUni,U.F_ClaPro, F_PiezasReq as piezas, F_IdReq,F_Solicitado as F_Solicitado,IFNULL(LOTE.F_ExiLot,0),IFNULL(MOVI.F_CantMov,0),IFNULL(LOTE.F_ExiLot,0) AS EXISTENCIAS,LOTE.F_FolLot,LOTE.F_Ubica,U.F_Obs, LOTE.F_TipOri, UA.F_Tipo as tipoUnidad FROM tb_unireq U INNER JOIN tb_uniatn UA on U.F_ClaUni = UA.F_ClaCli INNER JOIN tb_medica M ON U.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT l.F_ClaPro,SUM(F_ExiLot) AS F_ExiLot,F_FolLot,F_Ubica, F_TipOri FROM tb_lote l INNER JOIN tb_origen o ON o.F_ClaOri = l.F_Origen %s AND F_Proyecto = ? AND F_ExiLot > 0 GROUP BY l.F_ClaPro) AS LOTE ON U.F_ClaPro=LOTE.F_ClaPro LEFT JOIN (SELECT F_ProMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica %s AND L.F_Proyecto = ? GROUP BY F_ProMov) AS MOVI ON U.F_ClaPro=MOVI.F_ProMov left join tb_controlados ctr on U.F_ClaPro = ctr.F_ClaPro WHERE U.F_ClaUni IN (%s) and F_Status='0' and F_Solicitado!=0 AND M.F_StsPro='A' AND F_N%d='1' AND ctr.F_ClaPro IS NULL order by U.F_ClaUni ASC,U.F_ClaPro+0;";

    //caducos
    public static String BUSCA_DATOSporFACTURAR2 = "SELECT U.F_ClaUni,U.F_ClaPro, F_PiezasReq as piezas, F_IdReq,F_Solicitado as F_Solicitado,IFNULL(LOTE.F_ExiLot,0),IFNULL(MOVI.F_CantMov,0),IFNULL(LOTE.F_ExiLot,0) AS EXISTENCIAS,LOTE.F_FolLot,LOTE.F_Ubica,U.F_Obs, LOTE.F_TipOri, UA.F_Tipo as tipoUnidad FROM tb_unireq U INNER JOIN tb_uniatn UA on U.F_ClaUni = UA.F_ClaCli INNER JOIN tb_medica M ON U.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ClaPro,SUM(F_ExiLot) AS F_ExiLot,F_FolLot,F_Ubica, F_TipOri FROM tb_lote l INNER JOIN tb_origen O ON  O.F_ClaOri = F_Origen %s AND F_Proyecto = ?  AND F_FecCad < CURDATE() and F_ExiLot > 0 GROUP BY F_ClaPro) AS LOTE ON U.F_ClaPro=LOTE.F_ClaPro LEFT JOIN (SELECT F_ProMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica %s AND L.F_Proyecto = ? GROUP BY F_ProMov) AS MOVI ON U.F_ClaPro=MOVI.F_ProMov left join tb_controlados ctr on U.F_ClaPro = ctr.F_ClaPro WHERE F_ClaUni IN (%s) and F_Status='0' and F_Solicitado!=0 AND M.F_StsPro='A' AND F_N%d='1' AND ctr.F_ClaPro IS NULL order by U.F_ClaUni,F_Ubica DESC,U.F_ClaPro+0;";

    
    public static String BUSCA_DATOSporFACTURAR19 = "SELECT U.F_ClaUni,U.F_ClaPro, F_PiezasReq as piezas, F_IdReq,F_Solicitado as F_Solicitado,SUM(IFNULL(LOTE.F_ExiLot, 0)),IFNULL(MOVI.F_CantMov,0),IFNULL(LOTE.F_ExiLot,0) AS EXISTENCIAS,LOTE.F_FolLot,LOTE.F_Ubica,U.F_Obs, LOTE.F_TipOri, UA.F_Tipo as tipoUnidad FROM tb_unireq U INNER JOIN tb_uniatn UA on U.F_ClaUni = UA.F_ClaCli INNER JOIN tb_medica M ON U.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT l.F_ClaPro,SUM(F_ExiLot) AS F_ExiLot,F_FolLot,F_Ubica, F_TipOri FROM tb_lote l INNER JOIN tb_origen o ON o.F_ClaOri = l.F_Origen %s AND F_Proyecto = ? GROUP BY l.F_IdLote) AS LOTE ON U.F_ClaPro=LOTE.F_ClaPro LEFT JOIN (SELECT F_ProMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica %s AND L.F_Proyecto = ? GROUP BY F_ProMov) AS MOVI ON U.F_ClaPro=MOVI.F_ProMov INNER JOIN tb_ubicaatn atn ON atn.No_Unidad = U.F_ClaUni INNER JOIN tb_unidadfonsabi uf on uf.F_FolLot = LOTE.F_FolLot AND uf.F_ClaCli = atn.No_Unidad left join tb_controlados ctr on U.F_ClaPro = ctr.F_ClaPro WHERE U.F_ClaUni IN (%s) and F_Status='0' and F_Solicitado!=0 AND M.F_StsPro='A' AND F_N%d='1' AND ctr.F_ClaPro IS NULL group by LOTE.F_ClaPro order by U.F_ClaUni ASC,U.F_ClaPro+0;";
    ///
    public static String BUSCA_DATOSporFACTURARN1 = "SELECT U.F_ClaUni, U.F_ClaPro, F_PiezasReq AS piezas, F_IdReq, F_Solicitado AS F_Solicitado, IFNULL(LOTE.F_ExiLot, 0), IFNULL(MOVI.F_CantMov, 0), IF ( IFNULL(LOTE.F_ExiLot, 0) >= IFNULL(MOVI.F_CantMov, 0), IFNULL(MOVI.F_CantMov, 0), IFNULL(LOTE.F_ExiLot, 0)) AS EXISTENCIAS, LOTE.F_FolLot, LOTE.F_Ubica, U.F_Obs FROM tb_unireq U INNER JOIN tb_medicatipo M ON U.F_ClaPro = M.F_ClaPro INNER JOIN tb_medica MD ON M.F_ClaPro = MD.F_ClaPro LEFT JOIN ( SELECT F_ClaPro, SUM(F_ExiLot) AS F_ExiLot, F_FolLot, F_Ubica FROM tb_lote %s AND F_Proyecto = ? GROUP BY F_ClaPro ) AS LOTE ON U.F_ClaPro = LOTE.F_ClaPro LEFT JOIN ( SELECT F_ProMov, SUM(F_CantMov * F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov = L.F_ClaPro AND M.F_LotMov = L.F_FolLot AND M.F_UbiMov = L.F_Ubica %s AND L.F_Proyecto = ? GROUP BY F_ProMov ) AS MOVI ON U.F_ClaPro = MOVI.F_ProMov WHERE F_ClaUni IN (%s) AND F_Status = '0' AND F_Solicitado != 0 AND MD.F_StsPro = 'A' AND M.F_Tipo = ? ORDER BY U.F_ClaUni ASC, U.F_ClaPro + 0;";

    public static String BUSCA_DATOSporFACTURARTEMP = "SELECT F_ClaPro, F_CantReq, F_CantSur, F_Lote, F_Ubicacion, F_Proyecto, F_Contrato, F_Costo, F_Iva, F_Monto, F_OC FROM tb_facturatemp WHERE F_ClaCli = ? AND F_Ubicacion NOT LIKE '%CROSS%' AND F_User = ?;";

//    public static String BUSCA_DATOSporFACTEMPANESTESIA = "SELECT F_ClaPro, F_CantReq, F_CantSur, F_Lote, F_Ubicacion, F_Proyecto, F_Contrato, F_Costo, F_Iva, F_Monto, F_OC FROM tb_facturatemp WHERE F_ClaCli = ? AND F_ClaPro NOT IN (%s) AND F_User = ?;";
//    public static String BUSCA_DATOSporFACTEMP5FOLIO = "SELECT f.F_ClaPro, F_CantReq, F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, F_Costo, F_Iva, F_Monto, F_OC FROM tb_facturatemp f WHERE F_ClaCli = ? AND f.F_DocAnt = ? AND F_User = ?;";

//    public static String BUSCA_DATOSporFACTEMP5FOLIOCAUSE = "SELECT f.F_ClaPro, F_CantReq, F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, F_Costo, F_Iva, F_Monto, F_OC FROM tb_facturatemp f WHERE F_ClaCli = ? AND f.F_DocAnt = ? AND F_User = ? AND F_Cause = ?;";
//    public static String BUSCA_DATOSporFACTURARTEMPCross = "SELECT F_ClaPro, F_CantReq, F_CantSur, F_Lote, F_Ubicacion, F_Proyecto, F_Contrato, F_Costo, F_Iva, F_Monto, F_OC FROM tb_facturatemp WHERE F_ClaCli = ? AND F_Ubicacion LIKE '%CROSS%' AND F_User = ?;";
//    public static String BUSCA_DATOSporFACTEMPANESTESIA2 = "SELECT F_ClaPro, F_CantReq, F_CantSur, F_Lote, F_Ubicacion, F_Proyecto, F_Contrato, F_Costo, F_Iva, F_Monto, F_OC FROM tb_facturatemp WHERE F_ClaCli = ? AND F_ClaPro IN (%s) AND F_User = ?;";
    public static String BUSCA_UNIDADFACTURA5FOLIO2 = "SELECT f.F_ClaPro, F_CantReq, F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, F_Costo, F_Iva, F_Monto, F_OC, f.F_IdFact FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica WHERE F_ClaCli = ? AND f.F_ClaPro IN (%s) AND F_User = ? AND l.F_Origen = 1;";

    public static String ELIMINA_DATOSporFACTURARTEMP = "DELETE FROM tb_facturatemp WHERE F_User = ?;";

//    public static String BUSCA_DATOSporFACTURARCause = "SELECT U.F_ClaUni,U.F_ClaPro, F_PiezasReq as piezas, F_IdReq,F_Solicitado as F_Solicitado,IFNULL(LOTE.F_ExiLot,0),IFNULL(MOVI.F_CantMov,0),IF(IFNULL(LOTE.F_ExiLot,0)>=IFNULL(MOVI.F_CantMov,0),IFNULL(MOVI.F_CantMov,0),IFNULL(LOTE.F_ExiLot,0)) AS EXISTENCIAS,LOTE.F_FolLot,LOTE.F_Ubica,U.F_Obs, C.F_Cause FROM tb_unireq U INNER JOIN tb_medica M ON U.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ClaPro,SUM(F_ExiLot) AS F_ExiLot,F_FolLot,F_Ubica FROM tb_lote %s AND F_Proyecto = ? GROUP BY F_ClaPro) AS LOTE ON U.F_ClaPro=LOTE.F_ClaPro LEFT JOIN (SELECT F_ProMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica %s AND L.F_Proyecto = ? GROUP BY F_ProMov) AS MOVI ON U.F_ClaPro=MOVI.F_ProMov INNER JOIN tb_catalogoprecios C ON U.F_ClaPro=C.F_ClaPro WHERE F_ClaUni IN (%s) and F_Status='0' and F_Solicitado!=0 AND M.F_StsPro='A' AND F_N%d='1' AND C.F_Proyecto = ? AND C.F_Cause = ? order by U.F_ClaUni ASC,U.F_ClaPro+0;";
    public static String BUSCA_EXITFOL = "SELECT L.F_IdLote,L.F_ExiLot,L.F_FolLot,M.F_TipMed,M.F_Costo,L.F_Ubica,L.F_ExiLot,L.F_ClaOrg FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro=M.F_ClaPro WHERE L.F_Ubica IN (%s) AND L.F_ExiLot>0 AND M.F_N%d='1' AND M.F_StsPro='A' AND L.F_ClaPro=%s ORDER BY L.F_Origen,L.F_FecCad,L.F_ClaLot ASC;";

    public static String BUSCA_EXITFOLUBI = "SELECT L.F_IdLote,L.F_ExiLot,L.F_FolLot,M.F_TipMed,M.F_Costo,L.F_Ubica,IF(L.F_ExiLot>=IF(IFNULL(MOVI.F_CantMov,0)<0,0,IFNULL(MOVI.F_CantMov,0)),IF(IFNULL(MOVI.F_CantMov,0)<0,0,IFNULL(MOVI.F_CantMov,0)),L.F_ExiLot) AS EXILOT,L.F_ClaOrg  FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ProMov,F_LotMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica %s AND F_ProMov=? AND L.F_Proyecto = ? GROUP BY F_ProMov,F_LotMov) AS MOVI ON L.F_ClaPro=MOVI.F_ProMov AND L.F_FolLot=MOVI.F_LotMov %s AND L.F_ExiLot>0 AND L.F_Proyecto = ? AND M.F_N%d='1' AND M.F_StsPro='A' AND L.F_ClaPro=? HAVING EXILOT>0 ORDER BY L.F_Ubica <>'AF', L.F_Origen,L.F_FecCad,L.F_ClaLot ASC;";
//facturar caducados

    public static String BUSCA_EXITFOLUBI5FOLIO2 = "SELECT L.F_IdLote,L.F_ExiLot,L.F_FolLot,M.F_TipMed,M.F_Costo,L.F_Ubica, L.F_ExiLot AS EXILOT,L.F_ClaOrg  FROM tb_lote L INNER JOIN tb_origen OG ON L.F_Origen = OG.F_ClaOri INNER JOIN tb_medica M ON L.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ProMov,F_LotMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica %s AND F_ProMov=? AND L.F_Proyecto = ? GROUP BY F_ProMov,F_LotMov) AS MOVI ON L.F_ClaPro=MOVI.F_ProMov AND L.F_FolLot=MOVI.F_LotMov %s AND L.F_ExiLot>0 AND L.F_Proyecto = ? AND M.F_N%d='1' AND M.F_StsPro='A' AND L.F_ClaPro=? AND L.F_FecCad < CURDATE() HAVING EXILOT>0 ORDER BY L.F_Ubica <> 'CADUCADO', L.F_FecCad ASC, L.F_ClaLot ASC;";

    public static String BUSCA_EXITFOLUBI19 = "SELECT L.F_IdLote,L.F_ExiLot,L.F_FolLot,M.F_TipMed,M.F_Costo,L.F_Ubica, L.F_ExiLot AS EXILOT,L.F_ClaOrg FROM tb_lote L INNER JOIN tb_unidadFonsabi uf ON uf.F_ClaCli = '%s' AND L.F_FolLot = uf.F_FolLot INNER JOIN tb_origen OG ON L.F_Origen = OG.F_ClaOri INNER JOIN tb_medica M ON L.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ProMov,F_LotMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica %s AND F_ProMov=? AND L.F_Proyecto = ? GROUP BY F_ProMov,F_LotMov) AS MOVI ON L.F_ClaPro=MOVI.F_ProMov AND L.F_FolLot=MOVI.F_LotMov %s AND L.F_ExiLot>0 AND L.F_Proyecto = ? AND M.F_N%d='1' AND M.F_StsPro='A' AND L.F_ClaPro=? AND L.F_FecCad > CURDATE() HAVING EXILOT>0 ORDER BY L.F_Ubica <> 'CADUCADO', L.F_FecCad ASC, L.F_ClaLot ASC;";

    public static String BUSCA_EXITFOLUBI2 = "SELECT L.F_IdLote,L.F_ExiLot,L.F_FolLot,M.F_TipMed,M.F_Costo,L.F_Ubica,IF(L.F_ExiLot>=IF(IFNULL(MOVI.F_CantMov,0)<0,0,IFNULL(MOVI.F_CantMov,0)),IF(IFNULL(MOVI.F_CantMov,0)<0,0,IFNULL(MOVI.F_CantMov,0)),L.F_ExiLot) AS EXILOT,L.F_ClaOrg  FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ProMov,F_LotMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica %s AND F_ProMov=? AND L.F_Proyecto = ? GROUP BY F_ProMov,F_LotMov) AS MOVI ON L.F_ClaPro=MOVI.F_ProMov AND L.F_FolLot=MOVI.F_LotMov %s AND L.F_ExiLot>0 AND L.F_Proyecto = ? AND M.F_N%d='1' AND M.F_StsPro='A' AND L.F_ClaPro=? HAVING EXILOT>0 ORDER BY L.F_Ubica <>'CADUCADO', L.F_Origen,L.F_FecCad,L.F_ClaLot ASC;";

    ////
    public static String BUSCA_EXITFOLUBIN1 = "SELECT L.F_IdLote, L.F_ExiLot, L.F_FolLot, MD.F_TipMed, MD.F_Costo, L.F_Ubica, IF ( L.F_ExiLot >= IF ( IFNULL(MOVI.F_CantMov, 0) < 0, 0, IFNULL(MOVI.F_CantMov, 0)), IF ( IFNULL(MOVI.F_CantMov, 0) < 0, 0, IFNULL(MOVI.F_CantMov, 0)), L.F_ExiLot ) AS EXILOT, L.F_ClaOrg FROM tb_lote L INNER JOIN tb_medicatipo M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_medica MD ON M.F_ClaPro = MD.F_ClaPro LEFT JOIN ( SELECT F_ProMov, F_LotMov, SUM(F_CantMov * F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov = L.F_ClaPro AND M.F_LotMov = L.F_FolLot AND M.F_UbiMov = L.F_Ubica %s AND F_ProMov = ? AND L.F_Proyecto = ? GROUP BY F_ProMov, F_LotMov ) AS MOVI ON L.F_ClaPro = MOVI.F_ProMov AND L.F_FolLot = MOVI.F_LotMov %s AND L.F_ExiLot > 0 AND L.F_Proyecto = ? AND MD.F_StsPro = 'A' AND L.F_ClaPro = ? AND M.F_Tipo = ? HAVING EXILOT > 0 ORDER BY L.F_Ubica <> 'AF', L.F_Origen, L.F_FecCad, L.F_ClaLot ASC;";

    public static String BUSCA_EXITFOLUBI5FOLIO = "SELECT L.F_IdLote,L.F_ExiLot,L.F_FolLot,M.F_TipMed,M.F_Costo,L.F_Ubica, L.F_ExiLot AS EXILOT,L.F_ClaOrg  FROM tb_lote L INNER JOIN tb_origen OG ON L.F_Origen = OG.F_ClaOri INNER JOIN tb_medica M ON L.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ProMov,F_LotMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica %s AND F_ProMov=? AND L.F_Proyecto = ? GROUP BY F_ProMov,F_LotMov) AS MOVI ON L.F_ClaPro=MOVI.F_ProMov AND L.F_FolLot=MOVI.F_LotMov %s AND L.F_ExiLot>0 AND L.F_Proyecto = ? AND M.F_N%d='1' AND M.F_StsPro='A' AND L.F_ClaPro=? AND L.F_FecCad >= curdate() HAVING EXILOT>0 ORDER BY L.F_Ubica <>'AF', L.F_FecCad ASC, L.F_ClaLot ASC;";

    public static String BUSCA_EXITFOLUBI5FOLIORURAL = "SELECT L.F_IdLote,L.F_ExiLot,L.F_FolLot,M.F_TipMed,M.F_Costo,L.F_Ubica, L.F_ExiLot AS EXILOT,L.F_ClaOrg  FROM tb_lote L INNER JOIN tb_origen OG ON L.F_Origen = OG.F_ClaOri INNER JOIN tb_medica M ON L.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ProMov,F_LotMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica %s AND F_ProMov=? AND L.F_Proyecto = ? GROUP BY F_ProMov,F_LotMov) AS MOVI ON L.F_ClaPro=MOVI.F_ProMov AND L.F_FolLot=MOVI.F_LotMov %s AND L.F_ExiLot>0 AND L.F_Proyecto = ? AND M.F_N%d='1' AND M.F_StsPro='A' AND L.F_ClaPro=? AND TIMESTAMPDIFF(MONTH, DATE_SUB(curdate(),INTERVAL DAYOFMONTH(curdate())- 1 DAY), F_FecCad) > 1 ORDER BY L.F_Ubica <>'AF', L.F_FecCad ASC, L.F_ClaLot ASC;";
/*CONTROLADO*/
    //public static String BUSCA_EXITFOLUBI5FOLIOCONTROLADO = "SELECT L.F_IdLote,L.F_ExiLot,L.F_FolLot,M.F_TipMed,M.F_Costo,L.F_Ubica, L.F_ExiLot AS EXILOT,L.F_ClaOrg  FROM tb_lote L INNER JOIN tb_origen OG ON L.F_Origen = OG.F_ClaOri INNER JOIN tb_medica M ON L.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ProMov,F_LotMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica %s AND F_ProMov=? AND L.F_Proyecto = ? GROUP BY F_ProMov,F_LotMov) AS MOVI ON L.F_ClaPro=MOVI.F_ProMov AND L.F_FolLot=MOVI.F_LotMov %s AND L.F_ExiLot>0 AND L.F_Proyecto = ? AND M.F_N%d='1' AND M.F_StsPro='A' AND L.F_ClaPro=? AND L.F_FecCad >= DATE_ADD(curdate(),INTERVAL 41 DAY) HAVING EXILOT>0 ORDER BY L.F_Ubica <>'AF', L.F_FecCad ASC, L.F_ClaLot ASC;";
 public static String BUSCA_EXITFOLUBI5FOLIOCONTROLADO = "SELECT L.F_IdLote,L.F_ExiLot,L.F_FolLot,M.F_TipMed,M.F_Costo,L.F_Ubica, L.F_ExiLot AS EXILOT,L.F_ClaOrg  FROM tb_lote L INNER JOIN tb_origen OG ON L.F_Origen = OG.F_ClaOri INNER JOIN tb_medica M ON L.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ProMov,F_LotMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica %s AND F_ProMov=? AND L.F_Proyecto = ? GROUP BY F_ProMov,F_LotMov) AS MOVI ON L.F_ClaPro=MOVI.F_ProMov AND L.F_FolLot=MOVI.F_LotMov %s AND L.F_ExiLot>0 AND L.F_Proyecto = ? AND M.F_N%d='1' AND M.F_StsPro='A' AND L.F_ClaPro=? AND L.F_FecCad >= curdate() HAVING EXILOT>0 ORDER BY L.F_Ubica <>'AF', L.F_FecCad ASC, L.F_ClaLot ASC;";

    
    
    public static String BUSCA_EXILOTE = "SELECT F_IdLote, F_ExiLot, F_ClaOrg FROM tb_lote WHERE F_ClaPro = ? AND F_FolLot = ? AND F_Ubica = ? AND F_Proyecto = ?;";

    public static String BUSCA_FOLIOLOTE = "SELECT F_FolLot,F_Ubica,F_Costo FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro=M.F_ClaPro WHERE L.F_ClaPro=? ORDER BY F_FolLot DESC;";

    public static String BUSCA_INDICELOTE = "SELECT F_IndLote FROM tb_indice;";

    public final String ACTUALIZA_INDICELOTE = "update tb_indice set F_IndLote=?;";

    public final String INSERTAR_NUEVOLOTE = "INSERT INTO tb_lote VALUES(0,?,'S/L','2015-01-01','0',?,'900000000','SINUBICACION','2013-01-01','111','10372',?,'900000000','131',?)";

    public static String BUSCA_UNIDADES = "SELECT U.F_ClaUni FROM tb_unireq U INNER JOIN tb_medica M ON U.F_ClaPro=M.F_ClaPro WHERE F_ClaUni IN (%s) and F_Status='0' and F_Solicitado!=0 AND M.F_StsPro='A' AND F_N%d='1' GROUP BY U.F_ClaUni order by U.F_ClaUni ASC;";

    public static String BUSCA_UNIDADESN1 = "SELECT U.F_ClaUni, CASE WHEN UN.F_ClaCli IS NULL THEN 'UNIDADES' ELSE 'JURISDICCIONES' END AS TIPO FROM tb_unireq U LEFT JOIN ( SELECT F_ClaCli FROM tb_medicanivelunidad ) UN ON U.F_ClaUni = UN.F_ClaCli INNER JOIN tb_medicatipo MD ON U.F_ClaPro = MD.F_ClaPro INNER JOIN tb_medica M ON U.F_ClaPro = M.F_ClaPro WHERE F_ClaUni IN (%s) AND F_Status = '0' AND F_Solicitado != 0 AND M.F_StsPro = 'A' AND MD.F_Tipo = CASE WHEN UN.F_ClaCli IS NULL THEN 'UNIDADES' ELSE 'JURISDICCIONES' END GROUP BY U.F_ClaUni ORDER BY U.F_ClaUni ASC;";

    public static String BUSCA_UNIDADFACTURA = "SELECT IFNULL(CRL.CantSurCRL, 0) AS CantSurCRL, IFNULL(CR.CantSurCR, 0) AS CantSurCR, F_Obs FROM tb_facturatemp F LEFT JOIN ( SELECT F_ClaCli, SUM(F_CantSur) AS CantSurCR FROM tb_facturatemp WHERE F_ClaCli = ? AND F_Ubicacion LIKE '%CROSS%' AND F_User = ? ) AS CR ON F.F_ClaCli = CR.F_ClaCli LEFT JOIN ( SELECT F_ClaCli, SUM(F_CantSur) AS CantSurCRL FROM tb_facturatemp WHERE F_ClaCli = ? AND F_Ubicacion NOT LIKE '%CROSS%' AND F_User = ? ) AS CRL ON F.F_ClaCli = CRL.F_ClaCli WHERE F.F_ClaCli = ? AND F_User = ? GROUP BY F.F_ClaCli;";

//    public static String BUSCA_UNIDADFACTURAANESTESIA = "SELECT IFNULL(CRL.CantSurCRL, 0) AS CantSurCRL, IFNULL(CR.CantSurCR, 0) AS CantSurCR, F_Obs FROM tb_facturatemp F LEFT JOIN ( SELECT F_ClaCli, SUM(F_CantSur) AS CantSurCR FROM tb_facturatemp WHERE F_ClaCli = ? AND F_ClaPro IN (%s) AND F_User = ? ) AS CR ON F.F_ClaCli = CR.F_ClaCli LEFT JOIN ( SELECT F_ClaCli, SUM(F_CantSur) AS CantSurCRL FROM tb_facturatemp WHERE F_ClaCli = ? AND F_ClaPro NOT IN (%s) AND F_User = ? ) AS CRL ON F.F_ClaCli = CRL.F_ClaCli WHERE F.F_ClaCli = ? AND F_User = ? GROUP BY F.F_ClaCli;";
//    public static String BUSCA_UNIDADFACTURA5FOLIO = "SELECT IFNULL(CRL.CantSurCRL, 0) AS CantSurCRL, IFNULL(CR.CantSurCR, 0) AS CantSurCR, F_Obs FROM tb_facturatemp F LEFT JOIN ( SELECT F_ClaCli, SUM(F_CantSur) AS CantSurCR FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica WHERE F_ClaCli = ? AND f.F_ClaPro IN (%s) AND F_User = ? AND l.F_Origen = 1 ) AS CR ON F.F_ClaCli = CR.F_ClaCli LEFT JOIN ( SELECT F_ClaCli, SUM(F_CantSur) AS CantSurCRL FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica WHERE F_ClaCli = ? AND F_User = ? ) AS CRL ON F.F_ClaCli = CRL.F_ClaCli WHERE F.F_ClaCli = ? AND F_User = ? GROUP BY F.F_ClaCli;";

    public static String BUSCA_UNIDADESLOTE = "SELECT U.F_ClaUni FROM tb_unireqlote U INNER JOIN tb_medica M ON U.F_ClaPro=M.F_ClaPro WHERE F_ClaUni IN (%s) and F_Status='0' and F_Solicitado!=0 AND M.F_StsPro='A' AND F_N%d='1' GROUP BY U.F_ClaUni order by U.F_ClaUni ASC;";

    public final String ACTUALIZA_STSREQ = "update tb_unireq set F_Status='2' where F_ClaUni=? and F_Status='0' and F_Obs != '0'";
    public final String ACTUALIZA_STSREQLOTE = "update tb_unireqlote set F_Status='2' where F_ClaUni=? and F_Status='0';";
    public final String INSERTAR_OBSERVACIONES = "insert into tb_obserfact values (?, ?, 0, 'A', ?, ?)";

    public static String BuscaContrato = "SELECT F_Contrato FROM tb_proyectos WHERE F_Id = ?;";
    public static String DatosFactura = "SELECT F_Contrato, F_OC FROM tb_factura WHERE F_ClaDoc = ? LIMIT 1;";
    public static String BuscaFolioLote = "SELECT F_FolLot FROM tb_lote WHERE F_ClaPro = ? AND F_ClaLot = ? AND F_FecCad = ? AND F_Origen = ? AND F_Proyecto = ? LIMIT 1;";
    public static String BuscaFolioLoteExist = "SELECT F_FolLot, F_ExiLot FROM tb_lote WHERE F_ClaPro = ? AND F_ClaLot = ? AND F_FecCad = ? AND F_Origen = ? AND F_Proyecto = ? AND F_FolLot = ? AND F_Ubica = ?;";
    public static String BuscaFolioLotesinexistencia = "SELECT F_FolLot FROM tb_lote l INNER JOIN tb_origen o on o.F_ClaOri = l.F_Origen WHERE F_ClaPro = ? AND F_Proyecto = ? LIMIT 1;";
    public final String INSERTARLOTE = "INSERT tb_lote VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
    public static String BuscaIndiceLote = "SELECT F_IndLote FROM tb_indice;";
    public final String ActualizaIndiceLote = "UPDATE tb_indice SET F_IndLote = ?;";
    public final String INSERTAtransferenciaproyecto = "INSERT INTO tb_tranferenciaproyecto VALUES (NOW(), ?, ?, ?, ?, ?, 0);";

//    public static String BuscaCause = "SELECT C.F_Cause, CONCAT('( ',T.F_Cause,' )') FROM tb_catalogoprecios C INNER JOIN tb_tipocause T ON C.F_Cause = T.F_Id WHERE F_Proyecto = ? GROUP BY C.F_Cause ORDER BY C.F_Cause + 0;";
//    public static String BuscaCauseTemp = "SELECT F.F_Cause, CONCAT('( ', T.F_Cause, ' )') FROM tb_facturatemp F INNER JOIN tb_tipocause T ON F.F_Cause = T.F_Id WHERE F_DocAnt = 0 AND F_ClaCli = ? AND F_User = ? GROUP BY F.F_Cause;";
//
//    public static String BuscaCauseFactAuto = "SELECT IFNULL(SUM(F_Cause), 0), IFNULL(SUM(F_PiezasReq), 0) FROM tb_unireq U INNER JOIN tb_catalogoprecios C ON U.F_ClaPro = C.F_ClaPro INNER JOIN tb_medica M ON U.F_ClaPro = M.F_ClaPro WHERE C.F_Proyecto = ? AND M.F_StsPro = 'A' AND F_Cause = ? AND U.F_ClaUni = %s AND U.F_Status = 0;";
//    public static String BuscaUbicacionesCross = "SELECT F_Ubi FROM tb_ubicacrosdock;";

//    public static String BuscaUbicaNoFacturar = "SELECT F_Ubi FROM tb_ubicanofacturar;";

//    public static String BuscaAnestesia = "SELECT F_Clave FROM tb_isemanual;";
//    public static final String RegistrarSugerencia = "INSERT INTO tb_sugerenciasaa SET F_Sugerencia = ?, F_Usuario = ?, F_TipoU = ?;";

    public static final String ActualizaIdFactura = "UPDATE tb_facturatemp SET F_DocAnt = ? WHERE F_ClaPro = ? AND F_Lote = ? AND F_Ubicacion = ?;";

//    public static final String ActualizaCause = "UPDATE tb_facturatemp U LEFT JOIN ( SELECT F_Proyecto, F_ClaPro, F_Cause, F_Costo FROM tb_catalogoprecios WHERE F_Proyecto = ? ) AS C ON U.F_ClaPro = C.F_ClaPro SET U.F_Cause = IFNULL(C.F_Cause, 0) WHERE U.F_DocAnt = 0 AND F_ClaCli = ? AND F_User = ?;";
//    public static final String ActualizaFolioB = "UPDATE tb_facturatemp T INNER JOIN ( SELECT F_ClaCli, F_Cause, SUM(F_CantSur) AS CantSur FROM tb_facturatemp WHERE F_ClaCli = ? AND F_User = ? GROUP BY F_ClaCli, F_Cause HAVING CantSur > 0 LIMIT 1 ) C ON T.F_ClaCli = C.F_ClaCli SET T.F_Cause = C.F_Cause WHERE T.F_CantSur = 0 AND T.F_ClaCli = ? AND T.F_User = ?;";

    public static String BUSCA_DATOSporFACTURARLote = "SELECT U.F_ClaUni, U.F_ClaPro, F_PiezasReq AS piezas, F_IdReq, F_Solicitado AS F_Solicitado, U.F_Obs, U.F_Referencia, U.F_Lote, U.F_Caducidad FROM tb_unireqlote U INNER JOIN tb_medica M ON U.F_ClaPro=M.F_ClaPro WHERE F_ClaUni IN (%s) and F_Status='0' and F_Solicitado!=0 AND M.F_StsPro='A' AND F_N%d='1' order by U.F_ClaUni ASC,U.F_ClaPro+0;";

    public static String BUSCA_ExistenciaLote = "SELECT F_ClaPro, SUM(F_ExiLot) AS F_ExiLot, F_FolLot, F_Ubica, F_ClaLot, F_FecCad FROM tb_lote %s AND F_Proyecto = 1 AND F_ClaPro = ? AND F_ExiLot > 0 AND F_ClaLot = ? AND F_FecCad = ? GROUP BY F_ClaPro, F_ClaLot, F_FecCad";

    public static String BUSCA_EXITFOLUBILote = "SELECT L.F_IdLote,L.F_ExiLot,L.F_FolLot,M.F_TipMed,M.F_Costo,L.F_Ubica,L.F_ExiLot AS EXILOT,L.F_ClaOrg  FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro=M.F_ClaPro %s AND L.F_ExiLot>0 AND L.F_Proyecto = ? AND M.F_N%d='1' AND M.F_StsPro='A' AND L.F_ClaPro=? AND L.F_ClaLot = ? AND L.F_FecCad = ? AND L.F_FecCad >= CURDATE() HAVING EXILOT>0 ORDER BY L.F_Origen,L.F_FecCad,L.F_ClaLot ASC;";

    public static String BUSCA_EXITFOLUBILoteP2 = "SELECT L.F_IdLote,L.F_ExiLot,L.F_FolLot,M.F_TipMed,M.F_Costo,L.F_Ubica,IF(L.F_ExiLot>=IF(IFNULL(MOVI.F_CantMov,0)<0,0,IFNULL(MOVI.F_CantMov,0)),IF(IFNULL(MOVI.F_CantMov,0)<0,0,IFNULL(MOVI.F_CantMov,0)),L.F_ExiLot) AS EXILOT,L.F_ClaOrg  FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ProMov,F_LotMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica %s AND F_ProMov=? AND L.F_Proyecto = ? AND L.F_ClaLot = ? AND L.F_FecCad = ? GROUP BY F_ProMov,F_LotMov) AS MOVI ON L.F_ClaPro=MOVI.F_ProMov AND L.F_FolLot=MOVI.F_LotMov %s AND L.F_ExiLot>0 AND L.F_Proyecto = ? AND M.F_N%d='1' AND M.F_StsPro='A' AND L.F_ClaPro=? AND L.F_ClaLot = ? AND L.F_FecCad = ? AND L.F_FecCad >= CURDATE() HAVING EXILOT>0 ORDER BY L.F_Origen,L.F_FecCad,L.F_ClaLot ASC;";

    public static String BUSCA_EXITFOLUBILoteP7 = "SELECT L.F_IdLote,L.F_ExiLot,L.F_FolLot,M.F_TipMed,M.F_Costo,L.F_Ubica,IF(L.F_ExiLot>=IF(IFNULL(MOVI.F_CantMov,0)<0,0,IFNULL(MOVI.F_CantMov,0)),IF(IFNULL(MOVI.F_CantMov,0)<0,0,IFNULL(MOVI.F_CantMov,0)),L.F_ExiLot) AS EXILOT,L.F_ClaOrg  FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro=M.F_ClaPro LEFT JOIN (SELECT F_ProMov,F_LotMov,SUM(F_CantMov*F_SigMov) AS F_CantMov FROM tb_movinv M INNER JOIN tb_lote L ON M.F_ProMov=L.F_ClaPro AND M.F_LotMov=L.F_FolLot AND M.F_UbiMov=L.F_Ubica %s AND F_ProMov=? AND L.F_Proyecto = ? AND L.F_ClaLot = ? AND L.F_FecCad = ? GROUP BY F_ProMov,F_LotMov) AS MOVI ON L.F_ClaPro=MOVI.F_ProMov AND L.F_FolLot=MOVI.F_LotMov %s AND L.F_ExiLot>0 AND L.F_Proyecto = ? AND M.F_N%d='1' AND M.F_StsPro='A' AND L.F_ClaPro=? AND L.F_ClaLot = ? AND L.F_FecCad = ? AND L.F_FecCad >= CURDATE() HAVING EXILOT>0 ORDER BY L.F_Origen,L.F_FecCad,L.F_ClaLot ASC;";

//    public static String DatosAbasto = "SELECT F.F_ClaCli, F.F_Proyecto, F.F_ClaDoc, LTRIM(RTRIM(F.F_ClaPro)), M.F_DesPro, LTRIM(RTRIM(L.F_ClaLot)), DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F.F_CantSur), L.F_Origen, SUBSTR(L.F_Cb, 1, 13) AS F_Cb, NOW(), CASE WHEN ORI.F_TipOri = 'AR' THEN '1' ELSE '0' END AS ORIGEN FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot AND F.F_ClaPro = L.F_ClaPro AND F.F_Ubicacion = L.F_Ubica INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen ORI ON ORI.F_ClaOri = L.F_Origen WHERE F.F_Proyecto = ? AND F_ClaDoc = ? AND F_CantSur > 0 AND F_StsFact = 'A' GROUP BY F.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen;";
    //////////////// GENERAR ABASTO WEB  ////////////////  
    public static String DatosAbasto = "SELECT F.F_ClaCli, F.F_Proyecto, F.F_ClaDoc, LTRIM(RTRIM(F.F_ClaPro)), M.F_DesPro, LTRIM(RTRIM(L.F_ClaLot)), DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F.F_CantSur), L.F_Origen, SUBSTR(L.F_Cb, 1, 13) AS F_Cb, NOW(), CASE WHEN ORI.F_DesOri LIKE 'SC%' THEN '2' WHEN ORI.F_TipOri = 'AR' THEN '1' ELSE '0' END AS ORIGEN, F.F_Lote as LOTE FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot AND F.F_ClaPro = L.F_ClaPro AND F.F_Ubicacion = L.F_Ubica INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen ORI ON ORI.F_ClaOri = L.F_Origen WHERE F.F_Proyecto = ? AND F_ClaDoc = ? AND F_CantSur > 0 AND F_StsFact = 'A' GROUP BY F.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen;";

    public final String InsertAbasto = "INSERT INTO tb_abastoweb VALUES (?,?,?,?,?,?,?,?,?,?,NOW(),?,0,0,?);";

    public static String DatosAbastoByFacturaId = "SELECT F.F_ClaCli, F.F_Proyecto, F.F_ClaDoc, LTRIM(RTRIM(F.F_ClaPro)), M.F_DesPro, LTRIM(RTRIM(L.F_ClaLot)), DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(F.F_CantSur), L.F_Origen, SUBSTR(L.F_Cb, 1, 13) AS F_Cb, NOW(), CASE WHEN ORI.F_DesOri LIKE 'SC%' THEN '2' WHEN ORI.F_TipOri = 'AR' THEN '1' ELSE '0' END AS ORIGEN,F.F_Lote as LOTE FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot AND F.F_ClaPro = L.F_ClaPro AND F.F_Ubicacion = L.F_Ubica INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen ORI ON ORI.F_ClaOri = L.F_Origen WHERE F.F_IdFact = ? GROUP BY F.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen;";

//    public final String InsertAbasto = "INSERT INTO tb_abastoweb VALUES (?,?,?,?,?,?,?,?,?,?,NOW(),?,0,0);";
    //public static String BUSCA_UNIDADFACTURA2FOLIO = "SELECT IFNULL(CRL.CantSurCRL, 0) AS CantSurCRL, IFNULL(CR.CantSurCR, 0) AS CantSurCR, IFNULL(RF.CantSurRF, 0) AS CantSurRF, F_Obs FROM tb_facturatemp F LEFT JOIN ( SELECT F_ClaCli, SUM(F_CantSur) AS CantSurCR FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica WHERE F_ClaCli = ? AND F_CantSur > 0 AND (f.F_Ubicacion LIKE '%CONTROLADO%' AND f.F_ClaPro IN (SELECT F_ClaPro FROM tb_controlados)) AND F_User = ? ) AS CR ON F.F_ClaCli = CR.F_ClaCli LEFT JOIN ( SELECT F_ClaCli, SUM(F_CantSur) AS CantSurCRL FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica WHERE F_ClaCli = ? AND F_User = ? AND F_CantSur > 0  AND f.F_Ubicacion NOT LIKE '%REDFRIA%') AS CRL ON F.F_ClaCli = CRL.F_ClaCli left join (SELECT F_ClaCli, SUM(F_CantSur) AS CantSurRF FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica WHERE F_ClaCli = ? AND F_User = ? AND F_CantSur > 0 AND f.F_Ubicacion LIKE '%REDFRIA%') AS RF ON F.F_ClaCli = RF.F_ClaCli WHERE f.F_ClaCli = ? AND F_User = ? GROUP BY F.F_ClaCli;";

    /*CAMBIOS PARA SALIDA 2024*/
    
    public static String BUSCA_UNIDADFACTURA2FOLIO = "SELECT IFNULL(CRL.CantSurCRL, 0) AS CantSurCRL, IFNULL(CR.CantSurCR, 0) AS CantSurCR, IFNULL(ONCA.CantSurONCAPE, 0) AS CantSurONCAPE,IFNULL(ONCR.CantSurONCRF, 0) AS CantSurONCRF,IFNULL(RF.CantSurRF, 0) AS CantSurRF, F_Obs FROM tb_facturatemp F LEFT JOIN ( SELECT F_ClaCli, SUM(F_CantSur) AS CantSurCR FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica WHERE F_ClaCli = ? AND F_User = ? AND F_CantSur > 0 AND f.F_Ubicacion Rlike 'CTRFO|CONTROLADO|CTRL' ) AS CR ON F.F_ClaCli = CR.F_ClaCli LEFT JOIN ( SELECT F_ClaCli, SUM(F_CantSur) AS CantSurCRL FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica WHERE F_ClaCli = ? AND F_User = ? AND F_CantSur > 0  AND f.F_Ubicacion NOT RLIKE '(REDFRIA|RFFO|RF)' AND F.F_ClaPro NOT IN (SELECT F_ClaPro FROM tb_onco )) AS CRL ON F.F_ClaCli = CRL.F_ClaCli LEFT JOIN ( SELECT F_ClaCli, SUM(F_CantSur) AS CantSurONCAPE FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica WHERE F_ClaCli = ? AND F_User = ? AND F_CantSur > 0 AND F.F_Ubicacion RLIKE '(ONCOAPE)' ) AS ONCA ON F.F_ClaCli = ONCA.F_ClaCli LEFT JOIN ( SELECT F_ClaCli, SUM(F_CantSur) AS CantSurONCRF FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica WHERE F_ClaCli = ? AND F_User = ? AND F_CantSur > 0 AND F.F_Ubicacion RLIKE '(ONCORF)') AS ONCR ON F.F_ClaCli = ONCR.F_ClaCli LEFT JOIN ( SELECT F_ClaCli, SUM(F_CantSur) AS CantSurRF FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica WHERE F_ClaCli = ? AND F_User = ? AND F_CantSur > 0 AND f.F_Ubicacion RLIKE '(REDFRIA|RFFO|RF)' AND F.F_ClaPro NOT IN (SELECT F_ClaPro FROM tb_onco )) AS RF ON F.F_ClaCli = RF.F_ClaCli WHERE f.F_ClaCli = ? AND F_User = ? GROUP BY F.F_ClaCli;";
    
/////////////////////////FACTURACION AUTOMATICA CADUCADOS//////////
    public static String BUSCA_UNIDADFACTURA2FOLIO2Controlado2 = "SELECT f.F_ClaPro, SUM(F_CantReq) AS F_CantReq, SUM(F_CantSur) AS F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, SUM(F_Costo) AS F_Costo, SUM(F_Iva) AS F_Iva, SUM(F_Monto) AS F_Monto, F_OC, f.F_IdFact, o.F_TipOri FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica inner join tb_origen o ON o.F_ClaOri = l.F_Origen WHERE F_ClaCli = ? AND f.F_Ubicacion RLIKE 'CONTROLADO|CTRL' AND F_User = ? AND F_CantSur > 0 AND F_FecCad < CURDATE() GROUP BY F_ClaPro, F_Lote, F_Ubicacion order by F_FecCad;";

    public static String BUSCA_UNIDADFACTURA2FOLIO23 = "SELECT f.F_ClaPro, SUM(F_CantReq) AS F_CantReq, SUM(F_CantSur) AS F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, SUM(F_Costo) AS F_Costo, SUM(F_Iva) AS F_Iva, SUM(F_Monto) AS F_Monto, F_OC, f.F_IdFact, o.F_TipOri FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica inner join tb_origen o ON o.F_ClaOri = l.F_Origen WHERE F_ClaCli = ? AND f.F_DocAnt = 0 AND F_User = ? and F_FecCad < CURDATE() GROUP BY F_ClaPro, F_Lote, F_Ubicacion order by F_FecCad;";

    public static String BUSCA_UNIDADFACTURA2FOLIOCERO = "SELECT f.F_ClaPro, F_CantReq, F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, F_Costo, F_Iva, F_Monto, F_OC, f.F_IdFact FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica WHERE F_ClaCli = ? AND F_User = ? AND F_CantSur = 0;";

    public static String BUSCA_DATOSporFACTEMP2FOLIOControlado = "SELECT f.F_ClaPro, SUM(F_CantReq) AS F_CantReq, SUM(F_CantSur) AS F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, SUM(F_Costo) AS F_Costo, SUM(F_Iva) AS F_Iva, SUM(F_Monto) AS F_Monto, F_OC, f.F_IdFact FROM tb_facturatemp f WHERE F_ClaCli = ? AND f.F_DocAnt = ? AND F_User = ? AND f.F_Ubicacion Rlike 'CTRFO|CONTROLADO|CTRL' AND F_CantSur > 0 GROUP BY F_ClaPro, F_Lote, F_Ubicacion;";

    public static String BUSCA_DATOSporFACTEMP2FOLIO = "SELECT f.F_ClaPro, SUM(F_CantReq) AS F_CantReq, SUM(F_CantSur) AS F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, SUM(F_Costo) AS F_Costo, SUM(F_Iva) AS F_Iva, SUM(F_Monto) AS F_Monto, F_OC, f.F_IdFact FROM tb_facturatemp f WHERE F_ClaCli = ? AND f.F_DocAnt = ? AND F_User = ? GROUP BY F_ClaPro, F_Lote, F_Ubicacion;";

    public static String BUSCA_DATOSporFACTEMP2CERO = "SELECT f.F_ClaPro, F_CantReq, F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, F_Costo, F_Iva, F_Monto, F_OC FROM tb_facturatemp f WHERE F_ClaCli = ? AND f.F_DocAnt = ? AND F_User = ? AND F_CantSur = 0 ;";

    public static String callfolioincidente = "SELECT F.F_ClaDoc as folio,CASE WHEN F.F_Ubicacion RLIKE 'REDFRIA|RF|RFFO'  THEN CONCAT(F.F_ClaDoc,'-RF') WHEN F.F_Ubicacion RLIKE  '(APE|(APE|ES)(1|2|3|PAL|URGENTE|CDist))' THEN CONCAT(F.F_ClaDoc,'-APE') WHEN ((SELECT COUNT(*) from tb_controlados ctr where ctr.F_ClaPro = F.F_ClaPro) > 0 && F.F_Ubicacion Rlike 'CTRFO|CONTROLADO|CTRL') THEN CONCAT(F.F_ClaDoc,'-CTR') ELSE CONCAT(F.F_ClaDoc) END Folio_Tipo,F.F_ClaCli as Clave_Cliente,U.F_NomCli as Nombre_Cliente,F.F_ClaPro as Clave,M.F_ClaProSS as Clave_ISEM,M.F_NomGen,M.F_DesProEsp  AS F_Descripion, M.F_PrePro AS F_Presentacion,L.F_ClaLot as Lote, DATE_FORMAT( L.F_FecCad, '%d/%m/%Y' ) AS Caducidad, F.F_CantReq AS Requerido, F.F_CantSur AS Surtido, F.F_Ubicacion as Ubicacion, CASE WHEN F.F_Ubicacion RLIKE 'REDFRIA|RF|RFFO' && RF.F_ClaPro IS NOT NULL THEN 2 WHEN F.F_Ubicacion NOT RLIKE 'REDFRIA|RF|RFFO' && RF.F_ClaPro IS NOT NULL THEN 'ERROR DE UBICACION' WHEN F.F_Ubicacion RLIKE 'REDFRIA|RF|RFFO' && RF.F_ClaPro IS NULL THEN 'ERROR DE CATALOGO' ELSE 0 END AS RedFria, CASE WHEN F.F_Ubicacion RLIKE '(APE|(APE|ES)(1|2|3|PAL|URGENTE|CDist))' && A.F_ClaPro IS NOT NULL THEN 3 WHEN F.F_Ubicacion NOT RLIKE '(APE|(APE|ES)(1|2|3|PAL|URGENTE|CDist))' && A.F_ClaPro IS NOT NULL THEN 10 WHEN F.F_Ubicacion RLIKE '(APE|(APE|ES)(1|2|3|PAL|URGENTE|CDist))' && A.F_ClaPro IS NULL THEN 11 ELSE 0 END AS APE,CASE WHEN  F.F_Ubicacion RLIKE 'CONTROLADO|CTRFO|CTRL' && CTR.F_ClaPro IS NOT NULL THEN 4 WHEN  F.F_Ubicacion NOT RLIKE 'CONTROLADO|CTRFO|CTRL' && CTR.F_ClaPro IS NOT NULL THEN 10 WHEN  F.F_Ubicacion RLIKE 'CONTROLADO|CTRFO|CTRL' && CTR.F_ClaPro IS NULL THEN 11 ELSE 0 END AS Controlado, CASE WHEN  F.F_Ubicacion RLIKE 'ONCOAPE' && onc.F_ClaPro IS NOT NULL THEN 7 WHEN F.F_Ubicacion NOT RLIKE 'ONCOAPE' && onc.F_ClaPro IS NOT NULL THEN 10 WHEN F.F_Ubicacion RLIKE 'ONCOAPE' && onc.F_ClaPro IS NULL THEN 11  ELSE 0 END AS OncoAPE,CASE WHEN  F.F_Ubicacion RLIKE 'ONCORF' && onc.F_ClaPro IS NOT NULL THEN 8 WHEN F.F_Ubicacion NOT RLIKE 'ONCORF' && onc.F_ClaPro IS NOT NULL THEN 10 WHEN F.F_Ubicacion RLIKE 'ONCORF' && onc.F_ClaPro IS NULL THEN 11 ELSE 0 END AS OncoRF, CASE WHEN F.F_Ubicacion RLIKE 'REDFRIA|RFFO' THEN 2 WHEN  F.F_Ubicacion RLIKE '(FACFO|APE|(APE|ES)(1|2|3|PAL|URGENTE|CDist))' THEN 3 WHEN F.F_Ubicacion RLIKE 'CONTROLADO|CTRFO' THEN 4 WHEN  F.F_Ubicacion RLIKE 'ONCOAPE' THEN 7 WHEN  F.F_Ubicacion RLIKE 'ONCORF' THEN 8	ELSE 1 END tipoMed, CASE WHEN L.F_Origen = '19' THEN 'FONSABI' ELSE 'NORMAL' END AS Tipo_Folio FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot	AND F.F_Ubicacion = L.F_Ubica AND F.F_ClaPro = L.F_ClaPro INNER JOIN tb_uniatn U ON F.F_ClaCli = U.F_ClaCli INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro LEFT JOIN tb_redfria RF ON F.F_ClaPro = RF.F_ClaPro LEFT JOIN tb_ape A ON A.F_ClaPro = F.F_ClaPro LEFT JOIN tb_controlados CTR ON CTR.F_ClaPro = F.F_ClaPro INNER JOIN tb_origen o ON L.F_Origen = o.F_ClaOri LEFT JOIN tb_onco onc ON onc.F_ClaPro = F.F_ClaPro WHERE F_DocAnt != '1' AND F.F_Proyecto = '1' AND F_CantSur > 0 AND F.F_ClaDoc = ? GROUP BY F.F_ClaPro,L.F_ClaLot,L.F_FecCad,tipoMed HAVING RedFria IN (10,11) OR APE IN (10,11) OR Controlado IN (10,11) ORDER BY F.F_ClaPro ASC,L.F_ClaLot ASC,L.F_FecCad ASC;";
    
    /*ONCOLOGICO CADUCOS*/    
public static String BUSCA_UNIDADFACTURA2FOLIO2ONCORF2 = "SELECT f.F_ClaPro, SUM(F_CantReq) AS F_CantReq, SUM(F_CantSur) AS F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, SUM(F_Costo) AS F_Costo, SUM(F_Iva) AS F_Iva, SUM(F_Monto) AS F_Monto, F_OC, f.F_IdFact, o.F_TipOri FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica inner join tb_origen o ON o.F_ClaOri = l.F_Origen WHERE F_ClaCli = ? AND f.F_Ubicacion LIKE '%ONCORF%' AND F_User = ? AND F_CantSur > 0 AND F_FecCad < CURDATE() GROUP BY F_ClaPro, F_Lote, F_Ubicacion order by F_FecCad;";
public static String BUSCA_UNIDADFACTURA2FOLIO2ONCOAPE2 = "SELECT f.F_ClaPro, SUM(F_CantReq) AS F_CantReq, SUM(F_CantSur) AS F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, SUM(F_Costo) AS F_Costo, SUM(F_Iva) AS F_Iva, SUM(F_Monto) AS F_Monto, F_OC, f.F_IdFact, o.F_TipOri FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica inner join tb_origen o ON o.F_ClaOri = l.F_Origen WHERE F_ClaCli = ? AND f.F_Ubicacion LIKE '%ONCOAPE%' AND F_User = ? AND F_CantSur > 0 AND F_FecCad < CURDATE() GROUP BY F_ClaPro, F_Lote, F_Ubicacion order by F_FecCad;";
///ONCO
public static String BUSCA_UNIDADFACTURA2FOLIO2ONCOAPE = "SELECT f.F_ClaPro, SUM(F_CantReq) AS F_CantReq, SUM(F_CantSur) AS F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, SUM(F_Costo) AS F_Costo, SUM(F_Iva) AS F_Iva, SUM(F_Monto) AS F_Monto, F_OC, f.F_IdFact, o.F_TipOri FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica inner join tb_origen o ON o.F_ClaOri = l.F_Origen WHERE F_ClaCli = ? AND (f.F_Ubicacion LIKE '%ONCOAPE%' && f.F_ClaPro IN (SELECT F_ClaPro FROM tb_onco)) AND F_User = ? AND F_CantSur > 0 AND F_FecCad > CURDATE() GROUP BY F_ClaPro, F_Lote, F_Ubicacion order by F_FecCad;";
public static String BUSCA_UNIDADFACTURA2FOLIO2ONCORF = "SELECT f.F_ClaPro, SUM(F_CantReq) AS F_CantReq, SUM(F_CantSur) AS F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, SUM(F_Costo) AS F_Costo, SUM(F_Iva) AS F_Iva, SUM(F_Monto) AS F_Monto, F_OC, f.F_IdFact, o.F_TipOri FROM tb_facturatemp f INNER JOIN tb_lote l ON f.F_ClaPro = l.F_ClaPro AND f.F_Lote = l.F_FolLot AND f.F_Ubicacion = l.F_Ubica inner join tb_origen o ON o.F_ClaOri = l.F_Origen WHERE F_ClaCli = ? AND (f.F_Ubicacion LIKE '%ONCORF%' && f.F_ClaPro IN (SELECT F_ClaPro FROM tb_onco)) AND F_User = ? AND F_CantSur > 0 AND F_FecCad > CURDATE() GROUP BY F_ClaPro, F_Lote, F_Ubicacion order by F_FecCad;";
 public static String BUSCA_DATOSporFACTEMP2FOLIOONCORF = "SELECT f.F_ClaPro, SUM(F_CantReq) AS F_CantReq, SUM(F_CantSur) AS F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, SUM(F_Costo) AS F_Costo, SUM(F_Iva) AS F_Iva, SUM(F_Monto) AS F_Monto, F_OC, f.F_IdFact FROM tb_facturatemp f WHERE F_ClaCli = ? AND f.F_DocAnt = ? AND F_User = ? AND f.F_Ubicacion LIKE 'ONCORF' AND F_CantSur > 0 GROUP BY F_ClaPro, F_Lote, F_Ubicacion;";
 public static String BUSCA_DATOSporFACTEMP2FOLIOONCOAPE = "SELECT f.F_ClaPro, SUM(F_CantReq) AS F_CantReq, SUM(F_CantSur) AS F_CantSur, F_Lote, F_Ubicacion, f.F_Proyecto, F_Contrato, SUM(F_Costo) AS F_Costo, SUM(F_Iva) AS F_Iva, SUM(F_Monto) AS F_Monto, F_OC, f.F_IdFact FROM tb_facturatemp f WHERE F_ClaCli = ? AND f.F_DocAnt = ? AND F_User = ? AND f.F_Ubicacion LIKE 'ONCOAPE' AND F_CantSur > 0 GROUP BY F_ClaPro, F_Lote, F_Ubicacion;";

    
    private final ConectionDBTrans con = new ConectionDBTrans();

    private ResultSet rs;
    private ResultSet rsBuscaUnidad;
    private ResultSet rsBuscaUnidadFactura;
    private ResultSet rsContrato;
    private ResultSet rsContarReg;
    private ResultSet rsBuscaFolioLot;
    private ResultSet rsBuscaExiFol;
    private ResultSet rsBuscaDatosFact;
    private ResultSet rsBuscaExistencia;
//    private ResultSet rsCause;
//    private ResultSet rsCauseFact;
    private ResultSet rsConsulta;
    private ResultSet rsIndice;
    private ResultSet rsIndiceLote;
    private ResultSet rsTemp;
    private ResultSet rsUbicaCross;
//    private ResultSet rsAnestesia;
//    private ResultSet rsUbicaNoFacturar;
    private ResultSet rsAbasto;
    private ResultSet rscorre_error;
    
    
    
    private PreparedStatement psBuscaLote;
//    private PreparedStatement psBuscaAnestesia;
    private PreparedStatement psBuscaContrato;
//    private PreparedStatement psUbicaCrossdock;
//    private PreparedStatement psUbicaNoFacturar;
    private PreparedStatement psBuscaFolioLote;
    private PreparedStatement psContarReg;
    private PreparedStatement psBuscaExiFol;
    private PreparedStatement psBuscaUnidad;
//    private PreparedStatement psBuscaUnidad2;
    private PreparedStatement psAbasto;
    private PreparedStatement psAbastoInsert;
    private PreparedStatement psBuscaUnidadFactura;
    private PreparedStatement psBuscaTemp;
    private PreparedStatement psBuscaDatosFact;
    private PreparedStatement psBuscaExiLote;
    private PreparedStatement psBuscaIndice;
    private PreparedStatement psBuscaIndiceLote;
    private PreparedStatement psINSERTLOTE;
    private PreparedStatement psActualizaIndice;
    private PreparedStatement psActualizaIndiceCero;
    private PreparedStatement psActualizaIndiceLote;
    private PreparedStatement psActualizaLote;
    private PreparedStatement psActualizaTemp;
    private PreparedStatement psActualizaReq;
    private PreparedStatement psInsertar;
    private PreparedStatement psInsertarMov;
    private PreparedStatement psInsertarFact;
    private PreparedStatement psInsertarFactTemp;
    private PreparedStatement psInsertarObs;
    private PreparedStatement psConsulta;
//    private PreparedStatement psCause;
//    private PreparedStatement psCauseFact;
    private PreparedStatement psActualiza;
    private PreparedStatement pscorre_error;
  //CallableStatement cstmt;
     CorreoFactIncidente cfactinc = new CorreoFactIncidente();

//********************************************************************************************************************************************//    
    @Override
    public JSONArray getRegistro(String ClaPro) {

        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;

        String query = "SELECT F_IdLote,F_ExiLot FROM tb_lote WHERE F_ClaPro = ? AND F_ExiLot!=0 ORDER BY F_Origen, F_FecCad ASC;";
        PreparedStatement ps;
        ResultSet rs;
        try {
            con.conectar();
            ps = con.getConn().prepareStatement(query);
            ps.setString(1, ClaPro);
            rs = ps.executeQuery();

            while (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("IdReg", rs.getString(1));
                jsonObj.put("Existencia", rs.getString(2));
                jsonArray.add(jsonObj);
            }

            con.cierraConexion();
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public JSONArray getRegistroFact(String ClaUni, int Catalogo) {

        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();
        JSONObject jsonObj;
        JSONObject jsonObj2;
        System.out.println("Unidades:" + ClaUni);
        String query = "SELECT U.F_ClaPro, F_ClaUni FROM tb_unireq U INNER JOIN tb_medica M ON U.F_ClaPro=M.F_ClaPro WHERE F_ClaUni in ('" + ClaUni + "') AND F_Status=0 AND  F_Solicitado != 0 AND M.F_StsPro='A' AND F_N?='1';";
        PreparedStatement ps;
        ResultSet rs;
        try {
            int Contar = 0;
            con.conectar();
            ps = con.getConn().prepareStatement(query);
            ps.setInt(1, Catalogo);
            System.out.println("UNidades=" + ps);
            rs = ps.executeQuery();

            while (rs.next()) {
                String Clave = rs.getString(1);
                Clave = Clave.replace(".", "");
                jsonObj = new JSONObject();
                jsonObj.put("IdReg", Clave);
                jsonObj.put("ClaPro", rs.getString(1));
                jsonObj.put("ClaUni", rs.getString(2));
                Contar = Contar + 1;
                jsonArray.add(jsonObj);

            }
            ps.close();
            ps = null;
            rs.close();
            rs = null;
            jsonObj2 = new JSONObject();
            jsonArray.add(jsonObj2);
            con.cierraConexion();

        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public boolean RegistraDatosFactTemp(String Folio, String ClaUni, String IdLote, int CantMov, String FechaE, String Usuario) {
        boolean save = false;
        int ExiLot = 0;

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psBuscaLote = con.getConn().prepareStatement(BUSCA_LOTE);
            psBuscaLote.setString(1, IdLote);
            rs = psBuscaLote.executeQuery();
            if (rs.next()) {
                ExiLot = rs.getInt(2);
            }
            psBuscaLote.clearParameters();
            psBuscaLote.close();
            rs.close();

            if (CantMov > ExiLot) {
                save = false;
                return save;
            } else {

                PreparedStatement loteRepetido = con.getConn().prepareStatement(BUSCAR_LOTE_FACTEM);
                loteRepetido.setString(1, IdLote);
                loteRepetido.setString(2, ClaUni);
                loteRepetido.setString(3, Usuario);

                ResultSet loteRepetidoRS = loteRepetido.executeQuery();

                if (loteRepetidoRS.next()) {
                    Integer idFact = loteRepetidoRS.getInt("F_Id");
                    loteRepetido = con.getConn().prepareStatement(UPDATE_FACTTEM);
                    loteRepetido.setInt(1, CantMov);
                    loteRepetido.setInt(2, CantMov);
                    loteRepetido.setInt(3, idFact);
                    loteRepetido.executeUpdate();

                } else {

                    psINSERTLOTE = con.getConn().prepareStatement(INSERTAR_FACTTEM);
                    psINSERTLOTE.setString(1, Folio);
                    psINSERTLOTE.setString(2, ClaUni);
                    psINSERTLOTE.setString(3, IdLote);
                    psINSERTLOTE.setInt(4, CantMov);
                    psINSERTLOTE.setString(5, FechaE);
                    psINSERTLOTE.setString(6, Usuario);
                    psINSERTLOTE.setInt(7, CantMov);
                    psINSERTLOTE.execute();
                    psINSERTLOTE.clearParameters();
                    psINSERTLOTE.close();
                }
                save = true;
                con.getConn().commit();
                return save;
            }

        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;

    }

    /**
     * *********************************************
     */
    @Override
    public boolean ConfirmarFactTemp(String Usuario, String Observaciones, String Tipo, int Proyecto, String OC) {
        boolean save = false;
        int ExiLot = 0, FolFact = 0, FolioFactura = 0, FolioMovi = 0, FolMov = 0, TipoMed = 0;
        int existencia = 0, cantidad = 0;
        double Costo = 0.0, IVA = 0.0, Monto = 0.0, IVAPro = 0.0, MontoIva = 0.0;
        String IdFact = "", ClaCli = "", FechaE = "", Contrato = "";
        List<FacturacionModel> Factura = new ArrayList<>();

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psBuscaContrato = con.getConn().prepareStatement(BuscaContrato);
            psBuscaContrato.setInt(1, Proyecto);
            rsContrato = psBuscaContrato.executeQuery();
            if (rsContrato.next()) {
                Contrato = rsContrato.getString(1);
            }

            psBuscaTemp = con.getConn().prepareStatement(BUSCA_FACTEMP);
            psBuscaTemp.setString(1, Usuario);
            rsTemp = psBuscaTemp.executeQuery();

            while (rsTemp.next()) {

                IdFact = rsTemp.getString(2);
                ClaCli = rsTemp.getString(3);
                FechaE = rsTemp.getString(4);

                psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSFACT);
                psBuscaDatosFact.setString(1, IdFact);
                psBuscaDatosFact.setString(2, ClaCli);
                rs = psBuscaDatosFact.executeQuery();
                while (rs.next()) {
                    FacturacionModel facturacion = new FacturacionModel();
                    facturacion.setClaCli(rs.getString(1));
                    facturacion.setFolLot(rs.getString(2));
                    facturacion.setIdLote(rs.getString(3));
                    facturacion.setClaPro(rs.getString(4));
                    facturacion.setTipMed(rs.getInt(7));
                    facturacion.setCosto(rs.getDouble(8));
                    facturacion.setClaProve(rs.getString(9));
                    facturacion.setCant(rs.getInt(10));
                    facturacion.setExiLot(rs.getInt(11));
                    facturacion.setUbica(rs.getString(12));
                    facturacion.setId(rs.getString(14));
                    facturacion.setCantSol(rs.getString(16));
                    Factura.add(facturacion);
                }
            }
            psBuscaDatosFact.clearParameters();
            psBuscaDatosFact.close();
            psBuscaTemp.clearParameters();
            psBuscaTemp.close();
            rsTemp.close();

            psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
            rsIndice = psBuscaIndice.executeQuery();
            if (rsIndice.next()) {
                FolioFactura = rsIndice.getInt(1);
            }
            FolFact = FolioFactura + 1;

            psActualizaIndice = con.getConn().prepareStatement(String.format(ACTUALIZA_INDICEFACT, Proyecto));
            psActualizaIndice.setInt(1, FolFact);
            psActualizaIndice.execute();

            psActualizaIndice.clearParameters();
            psBuscaIndice.clearParameters();

            for (FacturacionModel f : Factura) {

                TipoMed = f.getTipMed();
                existencia = f.getExiLot();
                cantidad = f.getCant();

                if (TipoMed == 2504) {
                    IVA = 0.0;
                } else {
                    IVA = 0.16;
                }

                Costo = f.getCosto();
                Costo = 0.0;

                int Diferencia = existencia - cantidad;

                if (Diferencia >= 0) {
                    if (Diferencia == 0) {
                        psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
                        psActualizaLote.setInt(1, 0);
                        psActualizaLote.setString(2, f.getIdLote());
                        psActualizaLote.execute();
                        psActualizaLote.clearParameters();
                        psActualizaLote.close();
                    } else {
                        psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
                        psActualizaLote.setInt(1, Diferencia);
                        psActualizaLote.setString(2, f.getIdLote());
                        psActualizaLote.execute();
                        psActualizaLote.clearParameters();
                        psActualizaLote.close();
                    }

                    IVAPro = (cantidad * Costo) * IVA;
                    Monto = cantidad * Costo;
                    MontoIva = Monto + IVAPro;

                    psBuscaIndice = con.getConn().prepareStatement(BUSCA_INDICEMOV);
                    rsIndice = psBuscaIndice.executeQuery();
                    if (rsIndice.next()) {
                        FolioMovi = rsIndice.getInt(1);
                    }

                    FolMov = FolioMovi + 1;

                    psActualizaIndice = con.getConn().prepareStatement(ACTUALIZA_INDICEMOV);
                    psActualizaIndice.setInt(1, FolMov);
                    psActualizaIndice.execute();
                    psActualizaIndice.clearParameters();
                    psBuscaIndice.clearParameters();
                    psBuscaIndice.close();

                    psInsertar = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
                    psInsertar.setInt(1, FolioFactura);
                    psInsertar.setInt(2, 51);
                    psInsertar.setString(3, f.getClaPro());
                    psInsertar.setInt(4, cantidad);
                    psInsertar.setDouble(5, Costo);
                    psInsertar.setDouble(6, MontoIva);
                    psInsertar.setString(7, "-1");
                    psInsertar.setString(8, f.getFolLot());
                    psInsertar.setString(9, f.getUbica());
                    psInsertar.setString(10, f.getClaProve());
                    psInsertar.setString(11, Usuario);
                    psInsertar.execute();
                    psInsertar.clearParameters();
                    psInsertar.close();

                    psInsertarFact = con.getConn().prepareStatement(INSERTA_FACTURA);
                    psInsertarFact.setInt(1, FolioFactura);
                    psInsertarFact.setString(2, f.getClaCli());
                    psInsertarFact.setString(3, f.getClaPro());
                    psInsertarFact.setString(4, f.getCantSol());
                    psInsertarFact.setInt(5, cantidad);
                    psInsertarFact.setDouble(6, Costo);
                    psInsertarFact.setDouble(7, IVAPro);
                    psInsertarFact.setDouble(8, MontoIva);
                    psInsertarFact.setString(9, f.getFolLot());
                    psInsertarFact.setString(10, FechaE);
                    psInsertarFact.setString(11, Usuario);
                    psInsertarFact.setString(12, f.getUbica());
                    psInsertarFact.setInt(13, Proyecto);
                    psInsertarFact.setString(14, Contrato);
                    psInsertarFact.setString(15, OC);
                    psInsertarFact.setInt(16, 0);
                    psInsertarFact.execute();
                    psInsertarFact.clearParameters();
                    psInsertarFact.close();

                    psActualizaTemp = con.getConn().prepareStatement(ACTUALIZA_FACTTEM);
                    psActualizaTemp.setString(1, f.getId());
                    psActualizaTemp.execute();
                    psActualizaTemp.clearParameters();
                    psActualizaTemp.close();

                } else {
                    save = false;
                    con.getConn().rollback();
                    return save;
                }

            }

            psInsertarObs = con.getConn().prepareStatement(INSERTA_OBSFACTURA);
            psInsertarObs.setInt(1, FolioFactura);
            psInsertarObs.setString(2, Observaciones);
            psInsertarObs.setString(3, Tipo);
            psInsertarObs.setInt(4, Proyecto);
            psInsertarObs.execute();
            psInsertarObs.clearParameters();

            psInsertarObs.close();


/*AbastoService abasto = null;
abasto.crearAbastoWeb(FolioFactura,  Proyecto, Usuario);*/

            psAbastoInsert = con.getConn().prepareStatement(InsertAbasto);
            psAbasto = con.getConn().prepareStatement(DatosAbasto);
            psAbasto.setInt(1, Proyecto);
            psAbasto.setInt(2, FolioFactura);
            rsAbasto = psAbasto.executeQuery();
            while (rsAbasto.next()) {
                int factorEmpaque = 1;
                int folLot = rsAbasto.getInt("LOTE");
                PreparedStatement psfe = con.getConn().prepareStatement(getFactorEmpaque);
                psfe.setInt(1, folLot);
                ResultSet rsfe = psfe.executeQuery();
                if (rsfe.next()) {
                    factorEmpaque = rsfe.getInt("factor");
                }
                psAbastoInsert.setString(1, rsAbasto.getString(1));
                psAbastoInsert.setString(2, rsAbasto.getString(2));
                psAbastoInsert.setString(3, rsAbasto.getString(3));
                psAbastoInsert.setString(4, rsAbasto.getString(4));
                psAbastoInsert.setString(5, rsAbasto.getString(5));
                psAbastoInsert.setString(6, rsAbasto.getString(6));
                psAbastoInsert.setString(7, rsAbasto.getString(7));
                psAbastoInsert.setString(8, rsAbasto.getString(8));
                psAbastoInsert.setString(9, rsAbasto.getString(12));
                psAbastoInsert.setString(10, rsAbasto.getString(10));
                psAbastoInsert.setString(11, Usuario);
                psAbastoInsert.setInt(12, factorEmpaque);
                System.out.println("fact abasto 3" + psAbastoInsert);
                psAbastoInsert.addBatch();
            }

            psAbastoInsert.executeBatch();

            save = true;
            con.getConn().commit();
            return save;
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;

    }

    //AUTOMATICO PASO 2
    @Override
    public boolean ActualizaREQ(String ClaUni, String ClaPro, int Cantidad, int Catalogo, int Idreg, String Obs, int CantidadReq) {
        System.out.println("ClaUni=" + ClaUni + " ClaPro=" + ClaPro + " Cantidad=" + Cantidad + " Catalogo=" + Catalogo);
        boolean save = false;
        int ExiLot = 0;

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psBuscaLote = con.getConn().prepareStatement(ACTUALIZA_REQId);
            psBuscaLote.setInt(1, Cantidad);

            psBuscaLote.setString(2, Obs);
            psBuscaLote.setString(3, ClaPro);
            psBuscaLote.setString(4, ClaUni);
            psBuscaLote.setInt(5, Idreg);

            psBuscaLote.executeUpdate();
            psBuscaLote.clearParameters();
            psBuscaLote.close();
            psBuscaLote = null;
            save = true;
            con.getConn().commit();
            return save;

        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;

    }

//paso 1 de facturacion
    @Override
    public JSONArray getRegistroFactAuto(String ClaUni, String Catalogo) {
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();
        JSONObject jsonObj;
        JSONObject jsonObj2;
        System.out.println("Unidades:" + ClaUni);
        String query = "SELECT U.F_ClaPro, F_ClaUni,CONCAT(F_ClaUni,'_',REPLACE(U.F_ClaPro,'.','')) AS DATOS,F_IdReq FROM tb_unireq U INNER JOIN tb_medica M ON U.F_ClaPro=M.F_ClaPro WHERE F_ClaUni in (%s) AND F_Status=0 AND  F_Solicitado != 0 AND M.F_StsPro='A' AND F_N%s='1';";
        PreparedStatement ps;
        ResultSet rs;
        try {
            int Contar = 0;
            con.conectar();
            ps = con.getConn().prepareStatement(String.format(query, ClaUni, Catalogo));
            System.out.println("UNidades=" + ps);
            rs = ps.executeQuery();

            while (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("ClaPro", rs.getString(1));
                jsonObj.put("ClaUni", rs.getString(2));
                jsonObj.put("Datos", rs.getString(3));
                jsonObj.put("IdReg", rs.getString(4));
                jsonArray.add(jsonObj);

            }
            ps.close();
            ps = null;
            rs.close();
            rs = null;
            jsonObj2 = new JSONObject();
            jsonArray.add(jsonObj2);
            con.cierraConexion();

        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public boolean RegistrarFolios(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC) {
        boolean save = false;
        int UbicaModu = 0, piezas = 0, F_Solicitado = 0, ContarV = 0, Existencia = 0, FolioFactura = 0, DifeSol = 0, FolioLote = 0;
        String UbicaDesc = "", Clave = "", Unidad = "", Unidad2 = "", UbicaLote = "", Contrato = "", Ubicaciones = "", UbicaNofacturar = "";

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psBuscaContrato = con.getConn().prepareStatement(BuscaContrato);
            psBuscaContrato.setInt(1, Proyecto);
            rsContrato = psBuscaContrato.executeQuery();
            if (rsContrato.next()) {
                Contrato = rsContrato.getString(1);
            }

//            psUbicaCrossdock = con.getConn().prepareStatement(BuscaUbicacionesCross);
//            rsUbicaCross = psUbicaCrossdock.executeQuery();
//            if (rsUbicaCross.next()) {
//                Ubicaciones = rsUbicaCross.getString(1);
//            }
//           
            if (Catalogo > 0) {
                psConsulta = con.getConn().prepareStatement(BUSCA_PARAMETRO);
                psConsulta.setString(1, Usuario);
                rsConsulta = psConsulta.executeQuery();
                rsConsulta.next();
                UbicaModu = rsConsulta.getInt(1); // id de parametro
                Proyecto = rsConsulta.getInt(2);
                UbicaDesc = rsConsulta.getString(3);
                psConsulta.close();
                psConsulta = null;
//             
            }

            psActualizaIndice = con.getConn().prepareStatement(String.format(ACTUALIZA_INDICEFACT, Proyecto));
            psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
            psInsertarMov = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
            psInsertarFact = con.getConn().prepareStatement(INSERTA_FACTURA);
            psActualizaIndiceLote = con.getConn().prepareStatement(ACTUALIZA_INDICELOTE);
            psINSERTLOTE = con.getConn().prepareStatement(INSERTAR_NUEVOLOTE);
            psActualizaReq = con.getConn().prepareStatement(ACTUALIZA_STSREQ);
            psInsertarObs = con.getConn().prepareStatement(INSERTAR_OBSERVACIONES);
            psAbastoInsert = con.getConn().prepareStatement(InsertAbasto);

            psBuscaUnidad = con.getConn().prepareStatement(String.format(BUSCA_UNIDADES, ClaUnidad, Catalogo));
            rsBuscaUnidad = psBuscaUnidad.executeQuery();
            while (rsBuscaUnidad.next()) {
                Unidad = rsBuscaUnidad.getString(1);
                Unidad2 = rsBuscaUnidad.getString(1);
                Unidad = "'" + Unidad + "'";
                psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
                rsIndice = psBuscaIndice.executeQuery();
                if (rsIndice.next()) {
                    FolioFactura = rsIndice.getInt(1);
                }

                //psActualizaIndice=con.getConn().prepareStatement(ACTUALIZA_INDICEFACT);
                psActualizaIndice.setInt(1, FolioFactura + 1);
                psActualizaIndice.addBatch();

                /*psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSFACTURA);
                psBuscaDatosFact.setString(1, Unidad);
                psBuscaDatosFact.setInt(2, Catalogo);*/
                if (UbicaModu == 14 || UbicaModu == 40 || UbicaModu == 41) {
                    System.out.println("si entre al parametro de caducados");
                    psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTURAR2, UbicaDesc, UbicaDesc, Unidad, Catalogo));
                } else {
                    System.out.println("NO entre caducados");
                    psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTURAR, UbicaDesc, UbicaDesc, Unidad, Catalogo));
                }
//                psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTURAR, UbicaDesc, UbicaDesc, Unidad, Catalogo));
                psBuscaDatosFact.setInt(1, Proyecto);
                psBuscaDatosFact.setInt(2, Proyecto);
                //System.out.println("Datos Facturas" + psBuscaDatosFact);
                rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                while (rsBuscaDatosFact.next()) {
                    Clave = rsBuscaDatosFact.getString(2);
                    piezas = rsBuscaDatosFact.getInt(3);
                    F_Solicitado = rsBuscaDatosFact.getInt(5);
                    Existencia = rsBuscaDatosFact.getInt(8);
                    FolioLote = rsBuscaDatosFact.getInt(9);
                    UbicaLote = rsBuscaDatosFact.getString(10);
                    Observaciones = rsBuscaDatosFact.getString(11);

                    if ((piezas > 0) && (Existencia > 0)) {

                        int F_IdLote = 0, F_FolLot = 0, Tipo = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;
                        int Facturado = 0, Contar = 0;
                        String Ubicacion = "";
                        double Costo = 0.0, IVA = 0.0, IVAPro = 0.0, Monto = 0.0, MontoIva = 0.0;

                        if (UbicaModu == 14 || UbicaModu == 40 || UbicaModu == 41) {
                            System.out.println("si entre a existencia de caducados");
                            psContarReg = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI2, UbicaDesc, UbicaDesc, Catalogo));
                        } else {
                            System.out.println("NO entre a existencia de caducados");
                            psContarReg = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI, UbicaDesc, UbicaDesc, Catalogo));

                        }
                        psContarReg.setString(1, Clave);
                        psContarReg.setInt(2, Proyecto);
                        psContarReg.setInt(3, Proyecto);
                        psContarReg.setString(4, Clave);

                        rsContarReg = psContarReg.executeQuery();
                        while (rsContarReg.next()) {
                            Contar++;
                        }
                        if (UbicaModu == 14 || UbicaModu == 40 || UbicaModu == 41) {
                            System.out.println("si entre a existencia de caducados2");
                            psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI2, UbicaDesc, UbicaDesc, Catalogo));
                        } else {
                            System.out.println("NO entre a existencia de caducados2");
                            psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI, UbicaDesc, UbicaDesc, Catalogo));

                        }
                        //psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI, UbicaDesc, UbicaDesc, Catalogo));
                        psBuscaExiFol.setString(1, Clave);
                        psBuscaExiFol.setInt(2, Proyecto);
                        psBuscaExiFol.setInt(3, Proyecto);
                        psBuscaExiFol.setString(4, Clave);
                        //psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOL, UbicaDesc, Catalogo, Clave));

                        System.out.println("BuscaExistenciaDetalle =" + psBuscaExiFol);

                        rsBuscaExiFol = psBuscaExiFol.executeQuery();
                        while (rsBuscaExiFol.next()) {
                            F_IdLote = rsBuscaExiFol.getInt(1);
                            F_ExiLot = rsBuscaExiFol.getInt(7);
                            F_FolLot = rsBuscaExiFol.getInt(3);
                            Tipo = rsBuscaExiFol.getInt(4);
                            Costo = rsBuscaExiFol.getDouble(5);
                            Ubicacion = rsBuscaExiFol.getString(6);
                            ClaProve = rsBuscaExiFol.getInt(8);
                            if (Tipo == 2504) {
                                IVA = 0.0;
                            } else {
                                IVA = 0.16;
                            }

                            Costo = 0.0;

                            if ((F_ExiLot >= piezas) && (piezas > 0)) {
                                Contar = Contar - 1;
                                diferencia = F_ExiLot - piezas;
                                CanSur = piezas;
                                System.out.println("ActualizaLote = " + psActualizaLote + " Clave = " + Clave);
                                psActualizaLote.setInt(1, diferencia);
                                psActualizaLote.setInt(2, F_IdLote);

                                psActualizaLote.addBatch();

                                IVAPro = (CanSur * Costo) * IVA;
                                Monto = CanSur * Costo;
                                MontoIva = Monto + IVAPro;

                                psInsertarMov.setInt(1, FolioFactura);
                                psInsertarMov.setInt(2, 51);
                                psInsertarMov.setString(3, Clave);
                                psInsertarMov.setInt(4, CanSur);
                                psInsertarMov.setDouble(5, Costo);
                                psInsertarMov.setDouble(6, MontoIva);
                                psInsertarMov.setString(7, "-1");
                                psInsertarMov.setInt(8, F_FolLot);
                                psInsertarMov.setString(9, Ubicacion);
                                psInsertarMov.setInt(10, ClaProve);
                                psInsertarMov.setString(11, Usuario);
                                System.out.println("Mov1: " + psInsertarMov);
                                psInsertarMov.addBatch();

                                psInsertarFact.setInt(1, FolioFactura);
                                psInsertarFact.setString(2, Unidad2);
                                psInsertarFact.setString(3, Clave);
                                psInsertarFact.setInt(4, F_Solicitado);
                                psInsertarFact.setInt(5, CanSur);
                                psInsertarFact.setDouble(6, Costo);
                                psInsertarFact.setDouble(7, IVAPro);
                                psInsertarFact.setDouble(8, MontoIva);
                                psInsertarFact.setInt(9, F_FolLot);
                                psInsertarFact.setString(10, FecEnt);
                                psInsertarFact.setString(11, Usuario);
                                psInsertarFact.setString(12, Ubicacion);
                                psInsertarFact.setInt(13, Proyecto);
                                psInsertarFact.setString(14, Contrato);
                                psInsertarFact.setString(15, OC);
                                psInsertarFact.setInt(16, 0);
                                System.out.println("fact1: " + psInsertarFact);
                                psInsertarFact.addBatch();

                                piezas = 0;
                                F_Solicitado = 0;
                                break;

                            } else if ((piezas > 0) && (F_ExiLot > 0)) {
                                Contar = Contar - 1;
                                diferencia = piezas - F_ExiLot;
                                CanSur = F_ExiLot;
                                if (F_ExiLot >= F_Solicitado) {
                                    DifeSol = F_Solicitado;
                                } else if (Contar > 0) {
                                    DifeSol = F_ExiLot;
                                } else {
                                    DifeSol = F_Solicitado - F_ExiLot;
                                }

                                psActualizaLote.setInt(1, 0);
                                psActualizaLote.setInt(2, F_IdLote);
                                System.out.println("ActualizaLote2=" + psActualizaLote + " Clave=" + Clave);
                                psActualizaLote.addBatch();

                                IVAPro = (CanSur * Costo) * IVA;
                                Monto = CanSur * Costo;
                                MontoIva = Monto + IVAPro;

                                psInsertarMov.setInt(1, FolioFactura);
                                psInsertarMov.setInt(2, 51);
                                psInsertarMov.setString(3, Clave);
                                psInsertarMov.setInt(4, CanSur);
                                psInsertarMov.setDouble(5, Costo);
                                psInsertarMov.setDouble(6, MontoIva);
                                psInsertarMov.setString(7, "-1");
                                psInsertarMov.setInt(8, F_FolLot);
                                psInsertarMov.setString(9, Ubicacion);
                                psInsertarMov.setInt(10, ClaProve);
                                psInsertarMov.setString(11, Usuario);
                                System.out.println("Mov2" + psInsertarMov);
                                psInsertarMov.addBatch();

                                psInsertarFact.setInt(1, FolioFactura);
                                psInsertarFact.setString(2, Unidad2);
                                psInsertarFact.setString(3, Clave);
                                psInsertarFact.setInt(4, F_Solicitado);
                                psInsertarFact.setInt(5, CanSur);
                                psInsertarFact.setDouble(6, Costo);
                                psInsertarFact.setDouble(7, IVAPro);
                                psInsertarFact.setDouble(8, MontoIva);
                                psInsertarFact.setInt(9, F_FolLot);
                                psInsertarFact.setString(10, FecEnt);
                                psInsertarFact.setString(11, Usuario);
                                psInsertarFact.setString(12, Ubicacion);
                                psInsertarFact.setInt(13, Proyecto);
                                psInsertarFact.setString(14, Contrato);
                                psInsertarFact.setString(15, OC);
                                psInsertarFact.setInt(16, 0);
                                System.out.println("fact2: " + psInsertarFact);
                                psInsertarFact.addBatch();

                                F_Solicitado = F_Solicitado - CanSur;

                                piezas = piezas - CanSur;
                                F_ExiLot = 0;

                            }
                            if (Contar == 0) {
                                if (F_Solicitado > 0) {
                                    psInsertarFact.setInt(1, FolioFactura);
                                    psInsertarFact.setString(2, Unidad2);
                                    psInsertarFact.setString(3, Clave);
                                    psInsertarFact.setInt(4, F_Solicitado);
                                    psInsertarFact.setInt(5, 0);
                                    psInsertarFact.setDouble(6, Costo);
                                    psInsertarFact.setDouble(7, IVAPro);
                                    psInsertarFact.setDouble(8, MontoIva);
                                    psInsertarFact.setInt(9, F_FolLot);
                                    psInsertarFact.setString(10, FecEnt);
                                    psInsertarFact.setString(11, Usuario);
                                    psInsertarFact.setString(12, Ubicacion);
                                    psInsertarFact.setInt(13, Proyecto);
                                    psInsertarFact.setString(14, Contrato);
                                    psInsertarFact.setString(15, OC);
                                    psInsertarFact.setInt(16, 0);
                                    System.out.println("fact3" + psInsertarFact);
                                    psInsertarFact.addBatch();
                                    F_Solicitado = 0;
                                }
                            }

                        }

                    } else if ((FolioLote > 0) && (UbicaLote != "")) {
                        psInsertarFact.setInt(1, FolioFactura);
                        psInsertarFact.setString(2, Unidad2);
                        psInsertarFact.setString(3, Clave);
                        psInsertarFact.setInt(4, F_Solicitado);
                        psInsertarFact.setInt(5, 0);
                        psInsertarFact.setDouble(6, 0);
                        psInsertarFact.setDouble(7, 0);
                        psInsertarFact.setDouble(8, 0);
                        psInsertarFact.setInt(9, FolioLote);
                        psInsertarFact.setString(10, FecEnt);
                        psInsertarFact.setString(11, Usuario);
                        psInsertarFact.setString(12, UbicaLote);
                        psInsertarFact.setInt(13, Proyecto);
                        psInsertarFact.setString(14, Contrato);
                        psInsertarFact.setString(15, OC);
                        psInsertarFact.setInt(16, 0);
                        System.out.println("fact4" + psInsertarFact);
                        psInsertarFact.addBatch();
                    } else {
                        int FolioL = 0, IndiceLote = 0;
                        String Ubicacion = "";
                        double Costo = 0.0;

                        psBuscaIndiceLote = con.getConn().prepareStatement(BUSCA_INDICELOTE);
                        rsIndiceLote = psBuscaIndiceLote.executeQuery();
                        rsIndiceLote.next();
                        FolioL = rsIndiceLote.getInt(1);

                        IndiceLote = FolioL + 1;

                        psActualizaIndiceLote.setInt(1, IndiceLote);
                        psActualizaIndiceLote.addBatch();

                        psINSERTLOTE.setString(1, Clave);
                        psINSERTLOTE.setInt(2, FolioL);
                        psINSERTLOTE.setInt(3, Proyecto);
                        System.out.println("InsertarLote" + psINSERTLOTE);
                        psINSERTLOTE.addBatch();

                        psInsertarFact.setInt(1, FolioFactura);
                        psInsertarFact.setString(2, Unidad2);
                        psInsertarFact.setString(3, Clave);
                        psInsertarFact.setInt(4, F_Solicitado);
                        psInsertarFact.setInt(5, 0);
                        psInsertarFact.setDouble(6, Costo);
                        psInsertarFact.setDouble(7, 0);
                        psInsertarFact.setDouble(8, 0);
                        psInsertarFact.setInt(9, FolioL);
                        psInsertarFact.setString(10, FecEnt);
                        psInsertarFact.setString(11, Usuario);
                        psInsertarFact.setString(12, "NUEVA");
                        psInsertarFact.setInt(13, Proyecto);
                        psInsertarFact.setString(14, Contrato);
                        psInsertarFact.setString(15, OC);
                        psInsertarFact.setInt(16, 0);
                        System.out.println(" psInsertarFact1:" + psInsertarFact);
                        psInsertarFact.addBatch();
                    }

                }

                psActualizaReq.setString(1, Unidad2);
                psActualizaReq.addBatch();

                psInsertarObs.setInt(1, FolioFactura);
                psInsertarObs.setString(2, Observaciones);
                psInsertarObs.setString(3, Tipos);
                psInsertarObs.setInt(4, Proyecto);
                psInsertarObs.addBatch();

                psActualizaIndice.executeBatch();
                psActualizaLote.executeBatch();
                psINSERTLOTE.executeBatch();
                psInsertarMov.executeBatch();
                psInsertarFact.executeBatch();
                psActualizaIndiceLote.executeBatch();
                psActualizaReq.executeBatch();
                psInsertarObs.executeBatch();
/*
AbastoService abasto = null;
abasto.crearAbastoWeb(FolioFactura,  Proyecto, Usuario);*/
                psAbasto = con.getConn().prepareStatement(DatosAbasto);
                psAbasto.setInt(1, Proyecto);
                psAbasto.setInt(2, FolioFactura);
                rsAbasto = psAbasto.executeQuery();
                while (rsAbasto.next()) {
                    int factorEmpaque = 1;
                    int folLot = rsAbasto.getInt("LOTE");
                    PreparedStatement psfe = con.getConn().prepareStatement(getFactorEmpaque);
                    psfe.setInt(1, folLot);
                    ResultSet rsfe = psfe.executeQuery();
                    if (rsfe.next()) {
                        factorEmpaque = rsfe.getInt("factor");
                    }
                    psAbastoInsert.setString(1, rsAbasto.getString(1));
                    psAbastoInsert.setString(2, rsAbasto.getString(2));
                    psAbastoInsert.setString(3, rsAbasto.getString(3));
                    psAbastoInsert.setString(4, rsAbasto.getString(4));
                    psAbastoInsert.setString(5, rsAbasto.getString(5));
                    psAbastoInsert.setString(6, rsAbasto.getString(6));
                    psAbastoInsert.setString(7, rsAbasto.getString(7));
                    psAbastoInsert.setString(8, rsAbasto.getString(8));
                    psAbastoInsert.setString(9, rsAbasto.getString(12));
                    psAbastoInsert.setString(10, rsAbasto.getString(10));
                    psAbastoInsert.setString(11, Usuario);
                    psAbastoInsert.setInt(12, factorEmpaque);
                    System.out.println("fact abastos" + psAbastoInsert);
                    psAbastoInsert.addBatch();
                }

                psAbastoInsert.executeBatch();

                save = true;
                con.getConn().commit();
                System.out.println("Terminó Unidad= " + Unidad + " Con el Folio= " + FolioFactura);

            }
            psBuscaExiFol.close();
            psBuscaExiFol = null;
            rsBuscaExiFol.close();
            rsBuscaExiFol = null;
            psBuscaDatosFact.close();
            psBuscaDatosFact = null;
            rsBuscaDatosFact.close();
            rsBuscaDatosFact = null;
//            psUbicaCrossdock.close();
//            psUbicaCrossdock = null;
            rsUbicaCross.close();
            rsUbicaCross = null;
//            rsUbicaNoFacturar.close();
//            rsUbicaNoFacturar = null;
            return save;
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;
    }

    @Override
    public boolean ConfirmarFactTempFOLIO(String Usuario, int Proyecto) {
        boolean save = false;
        int ExiLot = 0, FolFact = 0, FolioFactura = 0, FolioMovi = 0, FolMov = 0, TipoMed = 0;
        int existencia = 0, cantidad = 0;
        double Costo = 0.0, IVA = 0.0, Monto = 0.0, IVAPro = 0.0, MontoIva = 0.0;
        String IdFact = "", ClaCli = "", FechaE = "", Contrato = "", OC = "";
        List<FacturacionModel> Factura = new ArrayList<>();

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psBuscaTemp = con.getConn().prepareStatement(BUSCA_FACTEMP);
            psBuscaTemp.setString(1, Usuario);
            rsTemp = psBuscaTemp.executeQuery();

            while (rsTemp.next()) {

                psBuscaContrato = con.getConn().prepareStatement(DatosFactura);
                psBuscaContrato.setString(1, rsTemp.getString(2));
                rsContrato = psBuscaContrato.executeQuery();
                if (rsContrato.next()) {
                    Contrato = rsContrato.getString(1);
                    OC = rsContrato.getString(2);
                }

                IdFact = rsTemp.getString(2);
                ClaCli = rsTemp.getString(3);
                FechaE = rsTemp.getString(4);

                psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSFACT);
                psBuscaDatosFact.setString(1, IdFact);
                psBuscaDatosFact.setString(2, ClaCli);
                rs = psBuscaDatosFact.executeQuery();
                while (rs.next()) {
                    FacturacionModel facturacion = new FacturacionModel();
                    facturacion.setClaCli(rs.getString(1));
                    facturacion.setFolLot(rs.getString(2));
                    facturacion.setIdLote(rs.getString(3));
                    facturacion.setClaPro(rs.getString(4));
                    facturacion.setTipMed(rs.getInt(7));
                    facturacion.setCosto(rs.getDouble(8));
                    facturacion.setClaProve(rs.getString(9));
                    facturacion.setCant(rs.getInt(10));
                    facturacion.setExiLot(rs.getInt(11));
                    facturacion.setUbica(rs.getString(12));
                    facturacion.setId(rs.getString(14));
                    facturacion.setCantSol(rs.getString(16));
                    Factura.add(facturacion);
                }
            }
            psBuscaDatosFact.clearParameters();
            psBuscaDatosFact.close();
            psBuscaTemp.clearParameters();
            psBuscaTemp.close();
            rsTemp.close();

            FolioFactura = Integer.parseInt(IdFact);
            for (FacturacionModel f : Factura) {

                TipoMed = f.getTipMed();
                existencia = f.getExiLot();
                cantidad = f.getCant();

                if (TipoMed == 2504) {
                    IVA = 0.0;
                } else {
                    IVA = 0.16;
                }

                Costo = f.getCosto();
                Costo = 0.0;

                int Diferencia = existencia - cantidad;

                if (Diferencia >= 0) {
                    if (Diferencia == 0) {
                        psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
                        psActualizaLote.setInt(1, 0);
                        psActualizaLote.setString(2, f.getIdLote());
                        psActualizaLote.execute();
                        psActualizaLote.clearParameters();
                        psActualizaLote.close();
                    } else {
                        psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
                        psActualizaLote.setInt(1, Diferencia);
                        psActualizaLote.setString(2, f.getIdLote());
                        psActualizaLote.execute();
                        psActualizaLote.clearParameters();
                        psActualizaLote.close();
                    }

                    IVAPro = (cantidad * Costo) * IVA;
                    Monto = cantidad * Costo;
                    MontoIva = Monto + IVAPro;

                    psBuscaIndice = con.getConn().prepareStatement(BUSCA_INDICEMOV);
                    rsIndice = psBuscaIndice.executeQuery();
                    if (rsIndice.next()) {
                        FolioMovi = rsIndice.getInt(1);
                    }

                    FolMov = FolioMovi + 1;

                    psActualizaIndice = con.getConn().prepareStatement(ACTUALIZA_INDICEMOV);
                    psActualizaIndice.setInt(1, FolMov);
                    psActualizaIndice.execute();
                    psActualizaIndice.clearParameters();
                    psBuscaIndice.clearParameters();
                    psBuscaIndice.close();

                    psInsertar = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
                    psInsertar.setInt(1, FolioFactura);
                    psInsertar.setInt(2, 51);
                    psInsertar.setString(3, f.getClaPro());
                    psInsertar.setInt(4, cantidad);
                    psInsertar.setDouble(5, Costo);
                    psInsertar.setDouble(6, MontoIva);
                    psInsertar.setString(7, "-1");
                    psInsertar.setString(8, f.getFolLot());
                    psInsertar.setString(9, f.getUbica());
                    psInsertar.setString(10, f.getClaProve());
                    psInsertar.setString(11, Usuario);
                    psInsertar.execute();
                    psInsertar.clearParameters();
                    psInsertar.close();

                    psInsertarFact = con.getConn().prepareStatement(INSERTA_FACTURA, Statement.RETURN_GENERATED_KEYS);
                    psInsertarFact.setInt(1, FolioFactura);
                    psInsertarFact.setString(2, f.getClaCli());
                    psInsertarFact.setString(3, f.getClaPro());
                    psInsertarFact.setString(4, "0");
                    psInsertarFact.setInt(5, cantidad);
                    psInsertarFact.setDouble(6, Costo);
                    psInsertarFact.setDouble(7, IVAPro);
                    psInsertarFact.setDouble(8, MontoIva);
                    psInsertarFact.setString(9, f.getFolLot());
                    psInsertarFact.setString(10, FechaE);
                    psInsertarFact.setString(11, Usuario);
                    psInsertarFact.setString(12, f.getUbica());
                    psInsertarFact.setInt(13, Proyecto);
                    psInsertarFact.setString(14, Contrato);
                    psInsertarFact.setString(15, OC);
                    psInsertarFact.setInt(16, 0);
                    psInsertarFact.executeUpdate();
                    ResultSet rsIndex = psInsertarFact.getGeneratedKeys();

                    if (rsIndex.next()) {
                        this.insertarAbasto(rsIndex.getInt(1), Usuario);
                    }
                    psInsertarFact.clearParameters();
                    psInsertarFact.close();

                    psActualizaTemp = con.getConn().prepareStatement(ACTUALIZA_FACTTEM);
                    psActualizaTemp.setString(1, f.getId());
                    psActualizaTemp.execute();
                    psActualizaTemp.clearParameters();
                    psActualizaTemp.close();

                } else {
                    save = false;
                    con.getConn().rollback();
                    return save;
                }

            }

            save = true;
            con.getConn().commit();
            return save;
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;

    }
/*
    @Override
    public boolean ConfirmarTranferenciaProyecto(String Usuario, String Observaciones, int Proyecto, int ProyectoFinal) {
        boolean save = false;
        int ExiLot = 0, FolFact = 0, FolioFactura = 0, FolioMovi = 0, FolMov = 0, TipoMed = 0, FolioLoteT = 0, FolioLoteT2 = 0, Existencia = 0, ExisTotal = 0;
        int existencia = 0, cantidad = 0, IndLote = 0;
        double Costo = 0.0, IVA = 0.0, Monto = 0.0, IVAPro = 0.0, MontoIva = 0.0;
        String IdFact = "", ClaCli = "", FechaE = "";
        List<FacturacionModel> Factura = new ArrayList<>();

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psBuscaTemp = con.getConn().prepareStatement(BUSCA_FACTEMP);
            psBuscaTemp.setString(1, Usuario);
            rsTemp = psBuscaTemp.executeQuery();

            while (rsTemp.next()) {

                IdFact = rsTemp.getString(2);
                ClaCli = rsTemp.getString(3);
                FechaE = rsTemp.getString(4);

                psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSFACT);
                psBuscaDatosFact.setString(1, IdFact);
                psBuscaDatosFact.setString(2, ClaCli);
                rs = psBuscaDatosFact.executeQuery();
                while (rs.next()) {
                    FacturacionModel facturacion = new FacturacionModel();
                    facturacion.setClaCli(rs.getString(1));
                    facturacion.setFolLot(rs.getString(2));
                    facturacion.setIdLote(rs.getString(3));
                    facturacion.setClaPro(rs.getString(4));
                    facturacion.setLote(rs.getString(5));
                    facturacion.setCaducidad(rs.getString(6));
                    facturacion.setTipMed(rs.getInt(7));
                    facturacion.setCosto(rs.getDouble(8));
                    facturacion.setClaProve(rs.getString(9));
                    facturacion.setCant(rs.getInt(10));
                    facturacion.setExiLot(rs.getInt(11));
                    facturacion.setUbica(rs.getString(12));
                    facturacion.setId(rs.getString(14));
                    facturacion.setCantSol(rs.getString(16));
                    facturacion.setClaOrg(rs.getString(17));
                    facturacion.setFecFab(rs.getString(18));
                    facturacion.setCb(rs.getString(19));
                    facturacion.setClaMar(rs.getString(20));
                    facturacion.setOrigen(rs.getString(21));
                    facturacion.setUniMed(rs.getString(22));
                    Factura.add(facturacion);
                }
            }
            psBuscaDatosFact.clearParameters();
            psBuscaDatosFact.close();
            psBuscaTemp.clearParameters();
            psBuscaTemp.close();
            rsTemp.close();

            psBuscaIndice = con.getConn().prepareStatement(BUSCA_INDICETRANSPRODUCTO);
            rsIndice = psBuscaIndice.executeQuery();
            if (rsIndice.next()) {
                FolioFactura = rsIndice.getInt(1);
            }
            FolFact = FolioFactura + 1;

            psActualizaIndice = con.getConn().prepareStatement(ACTUALIZA_INDICETRANSPRODUCTO);
            psActualizaIndice.setInt(1, FolFact);
            psActualizaIndice.execute();

            psActualizaIndice.clearParameters();
            psBuscaIndice.clearParameters();

            for (FacturacionModel f : Factura) {

                TipoMed = f.getTipMed();
                existencia = f.getExiLot();
                cantidad = f.getCant();

                if (TipoMed == 2504) {
                    IVA = 0.0;
                } else {
                    IVA = 0.16;
                }

                Costo = f.getCosto();
                Costo = 0.0;

                int Diferencia = existencia - cantidad;

                if (Diferencia >= 0) {
                    if (Diferencia == 0) {
                        psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
                        psActualizaLote.setInt(1, 0);
                        psActualizaLote.setString(2, f.getIdLote());
                        psActualizaLote.execute();
                        psActualizaLote.clearParameters();
                        psActualizaLote.close();
                    } else {
                        psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
                        psActualizaLote.setInt(1, Diferencia);
                        psActualizaLote.setString(2, f.getIdLote());
                        psActualizaLote.execute();
                        psActualizaLote.clearParameters();
                        psActualizaLote.close();
                    }

                    IVAPro = (cantidad * Costo) * IVA;
                    Monto = cantidad * Costo;
                    MontoIva = Monto + IVAPro;

                    psBuscaIndice = con.getConn().prepareStatement(BUSCA_INDICEMOV);
                    rsIndice = psBuscaIndice.executeQuery();
                    if (rsIndice.next()) {
                        FolioMovi = rsIndice.getInt(1);
                    }

                    FolMov = FolioMovi + 1;

                    psActualizaIndice = con.getConn().prepareStatement(ACTUALIZA_INDICEMOV);
                    psActualizaIndice.setInt(1, FolMov);
                    psActualizaIndice.execute();
                    psActualizaIndice.clearParameters();
                    psBuscaIndice.clearParameters();
                    psBuscaIndice.close();

                    psInsertar = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
                    psInsertar.setInt(1, FolioFactura);
                    psInsertar.setInt(2, 60);
                    psInsertar.setString(3, f.getClaPro());
                    psInsertar.setInt(4, cantidad);
                    psInsertar.setDouble(5, Costo);
                    psInsertar.setDouble(6, MontoIva);
                    psInsertar.setString(7, "-1");
                    psInsertar.setString(8, f.getFolLot());
                    psInsertar.setString(9, f.getUbica());
                    psInsertar.setString(10, f.getClaProve());
                    psInsertar.setString(11, Usuario);
                    psInsertar.execute();
                    psInsertar.clearParameters();
                    psInsertar.close();

                    psBuscaFolioLote = con.getConn().prepareStatement(BuscaFolioLote);
                    psBuscaFolioLote.setString(1, f.getClaPro());
                    psBuscaFolioLote.setString(2, f.getLote());
                    psBuscaFolioLote.setString(3, f.getCaducidad());
                    psBuscaFolioLote.setString(4, f.getOrigen());
                    psBuscaFolioLote.setInt(5, ProyectoFinal);
                    rsBuscaFolioLot = psBuscaFolioLote.executeQuery();
                    if (rsBuscaFolioLot.next()) {
                        FolioLoteT = rsBuscaFolioLot.getInt(1);
                    }

                    psBuscaFolioLote.clearParameters();

                    if (FolioLoteT != 0) {
                        psBuscaFolioLote = con.getConn().prepareStatement(BuscaFolioLoteExist);
                        psBuscaFolioLote.setString(1, f.getClaPro());
                        psBuscaFolioLote.setString(2, f.getLote());
                        psBuscaFolioLote.setString(3, f.getCaducidad());
                        psBuscaFolioLote.setString(4, f.getOrigen());
                        psBuscaFolioLote.setInt(5, ProyectoFinal);
                        psBuscaFolioLote.setInt(6, FolioLoteT);
                        psBuscaFolioLote.setString(7, f.getUbica());
                        rsBuscaFolioLot = psBuscaFolioLote.executeQuery();
                        if (rsBuscaFolioLot.next()) {
                            FolioLoteT2 = rsBuscaFolioLot.getInt(1);
                            Existencia = rsBuscaFolioLot.getInt(2);
                        }
                        if (FolioLoteT2 != 0) {
                            psBuscaFolioLote.clearParameters();
                            ExisTotal = Existencia + cantidad;

                            psBuscaFolioLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTEPRODUCTO);
                            psBuscaFolioLote.setInt(1, ExisTotal);
                            psBuscaFolioLote.setString(2, f.getClaPro());
                            psBuscaFolioLote.setString(3, f.getLote());
                            psBuscaFolioLote.setString(4, f.getCaducidad());
                            psBuscaFolioLote.setString(5, f.getOrigen());
                            psBuscaFolioLote.setInt(6, ProyectoFinal);
                            psBuscaFolioLote.setInt(7, FolioLoteT);
                            psBuscaFolioLote.setString(8, f.getUbica());
                            psBuscaFolioLote.execute();
                        } else {
                            psBuscaFolioLote.clearParameters();
                            psBuscaFolioLote = con.getConn().prepareStatement(INSERTARLOTE);
                            psBuscaFolioLote.setInt(1, 0);
                            psBuscaFolioLote.setString(2, f.getClaPro());
                            psBuscaFolioLote.setString(3, f.getLote());
                            psBuscaFolioLote.setString(4, f.getCaducidad());
                            psBuscaFolioLote.setInt(5, cantidad);
                            psBuscaFolioLote.setInt(6, FolioLoteT);
                            psBuscaFolioLote.setString(7, f.getClaOrg());
                            psBuscaFolioLote.setString(8, f.getUbica());
                            psBuscaFolioLote.setString(9, f.getFecFab());
                            psBuscaFolioLote.setString(10, f.getCb());
                            psBuscaFolioLote.setString(11, f.getClaMar());
                            psBuscaFolioLote.setString(12, f.getOrigen());
                            psBuscaFolioLote.setString(13, f.getClaOrg());
                            psBuscaFolioLote.setInt(14, 131);
                            psBuscaFolioLote.setInt(15, ProyectoFinal);
                            psBuscaFolioLote.execute();
                        }

                        psInsertar = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
                        psInsertar.setInt(1, FolioFactura);
                        psInsertar.setInt(2, 9);
                        psInsertar.setString(3, f.getClaPro());
                        psInsertar.setInt(4, cantidad);
                        psInsertar.setDouble(5, Costo);
                        psInsertar.setDouble(6, MontoIva);
                        psInsertar.setString(7, "1");
                        psInsertar.setInt(8, FolioLoteT);
                        psInsertar.setString(9, f.getUbica());
                        psInsertar.setString(10, f.getClaProve());
                        psInsertar.setString(11, Usuario);
                        psInsertar.execute();
                        psInsertar.clearParameters();
                        psInsertar.close();

                    } else {

                        psBuscaFolioLote.clearParameters();
                        psBuscaFolioLote = con.getConn().prepareStatement(BuscaIndiceLote);
                        rs = psBuscaFolioLote.executeQuery();
                        if (rs.next()) {
                            IndLote = rs.getInt(1);
                        }

                        psBuscaFolioLote.clearParameters();

                        psBuscaFolioLote = con.getConn().prepareStatement(ActualizaIndiceLote);
                        psBuscaFolioLote.setInt(1, IndLote + 1);
                        psBuscaFolioLote.execute();

                        if (f.getUbica().equals("NUEVA")) {
                            psBuscaFolioLote.clearParameters();
                            psBuscaFolioLote = con.getConn().prepareStatement(INSERTARLOTE);
                            psBuscaFolioLote.setInt(1, 0);
                            psBuscaFolioLote.setString(2, f.getClaPro());
                            psBuscaFolioLote.setString(3, f.getLote());
                            psBuscaFolioLote.setString(4, f.getCaducidad());
                            psBuscaFolioLote.setInt(5, cantidad);
                            psBuscaFolioLote.setInt(6, IndLote);
                            psBuscaFolioLote.setString(7, f.getClaOrg());
                            psBuscaFolioLote.setString(8, f.getUbica());
                            psBuscaFolioLote.setString(9, f.getFecFab());
                            psBuscaFolioLote.setString(10, f.getCb());
                            psBuscaFolioLote.setString(11, f.getClaMar());
                            psBuscaFolioLote.setString(12, f.getOrigen());
                            psBuscaFolioLote.setString(13, f.getClaOrg());
                            psBuscaFolioLote.setInt(14, 131);
                            psBuscaFolioLote.setInt(15, ProyectoFinal);
                            psBuscaFolioLote.execute();
                        } else {
                            psBuscaFolioLote.clearParameters();
                            psBuscaFolioLote = con.getConn().prepareStatement(INSERTARLOTE);
                            psBuscaFolioLote.setInt(1, 0);
                            psBuscaFolioLote.setString(2, f.getClaPro());
                            psBuscaFolioLote.setString(3, f.getLote());
                            psBuscaFolioLote.setString(4, f.getCaducidad());
                            psBuscaFolioLote.setInt(5, 0);
                            psBuscaFolioLote.setInt(6, IndLote);
                            psBuscaFolioLote.setString(7, f.getClaOrg());
                            psBuscaFolioLote.setString(8, "NUEVA");
                            psBuscaFolioLote.setString(9, f.getFecFab());
                            psBuscaFolioLote.setString(10, f.getCb());
                            psBuscaFolioLote.setString(11, f.getClaMar());
                            psBuscaFolioLote.setString(12, f.getOrigen());
                            psBuscaFolioLote.setString(13, f.getClaOrg());
                            psBuscaFolioLote.setInt(14, 131);
                            psBuscaFolioLote.setInt(15, ProyectoFinal);
                            psBuscaFolioLote.execute();

                            psBuscaFolioLote.clearParameters();
                            psBuscaFolioLote = con.getConn().prepareStatement(INSERTARLOTE);
                            psBuscaFolioLote.setInt(1, 0);
                            psBuscaFolioLote.setString(2, f.getClaPro());
                            psBuscaFolioLote.setString(3, f.getLote());
                            psBuscaFolioLote.setString(4, f.getCaducidad());
                            psBuscaFolioLote.setInt(5, cantidad);
                            psBuscaFolioLote.setInt(6, IndLote);
                            psBuscaFolioLote.setString(7, f.getClaOrg());
                            psBuscaFolioLote.setString(8, f.getUbica());
                            psBuscaFolioLote.setString(9, f.getFecFab());
                            psBuscaFolioLote.setString(10, f.getCb());
                            psBuscaFolioLote.setString(11, f.getClaMar());
                            psBuscaFolioLote.setString(12, f.getOrigen());
                            psBuscaFolioLote.setString(13, f.getClaOrg());
                            psBuscaFolioLote.setInt(14, 131);
                            psBuscaFolioLote.setInt(15, ProyectoFinal);
                            psBuscaFolioLote.execute();
                        }

                        psInsertar = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
                        psInsertar.setInt(1, FolioFactura);
                        psInsertar.setInt(2, 9);
                        psInsertar.setString(3, f.getClaPro());
                        psInsertar.setInt(4, cantidad);
                        psInsertar.setDouble(5, Costo);
                        psInsertar.setDouble(6, MontoIva);
                        psInsertar.setString(7, "1");
                        psInsertar.setInt(8, IndLote);
                        psInsertar.setString(9, f.getUbica());
                        psInsertar.setString(10, f.getClaProve());
                        psInsertar.setString(11, Usuario);
                        psInsertar.execute();

                    }

                    psActualizaTemp = con.getConn().prepareStatement(ACTUALIZA_FACTTEM);
                    psActualizaTemp.setString(1, f.getId());
                    psActualizaTemp.execute();
                    psActualizaTemp.clearParameters();
                    psActualizaTemp.close();
                    psBuscaFolioLote.close();

                } else {
                    save = false;
                    con.getConn().rollback();
                    return save;
                }

            }

            psInsertar.clearParameters();
            psInsertar = con.getConn().prepareStatement(INSERTAtransferenciaproyecto);
            psInsertar.setInt(1, FolioFactura);
            psInsertar.setInt(2, Proyecto);
            psInsertar.setInt(3, ProyectoFinal);
            psInsertar.setString(4, Observaciones);
            psInsertar.setString(5, Usuario);
            psInsertar.execute();

            save = true;
            con.getConn().commit();
            return save;
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;

    }
*/
/*//    @Override
    public boolean ConfirmarFactTempCause(String Usuario, String Observaciones, String Tipo, int Proyecto, String OC, String Cause) {
        boolean save = false;
        int ExiLot = 0, FolFact = 0, FolioFactura = 0, FolioMovi = 0, FolMov = 0, TipoMed = 0;
        int existencia = 0, cantidad = 0;
        double Costo = 0.0, IVA = 0.0, Monto = 0.0, IVAPro = 0.0, MontoIva = 0.0;
        String IdFact = "", ClaCli = "", FechaE = "", Contrato = "";
        List<FacturacionModel> Factura = new ArrayList<>();

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psBuscaContrato = con.getConn().prepareStatement(BuscaContrato);
            psBuscaContrato.setInt(1, Proyecto);
            rsContrato = psBuscaContrato.executeQuery();
            if (rsContrato.next()) {
                Contrato = rsContrato.getString(1);
            }

            psBuscaTemp = con.getConn().prepareStatement(BUSCA_FACTEMP);
            psBuscaTemp.setString(1, Usuario);
            rsTemp = psBuscaTemp.executeQuery();

            while (rsTemp.next()) {

                IdFact = rsTemp.getString(2);
                ClaCli = rsTemp.getString(3);
                FechaE = rsTemp.getString(4);

                psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSFACT);
                psBuscaDatosFact.setString(1, IdFact);
                psBuscaDatosFact.setString(2, ClaCli);
                rs = psBuscaDatosFact.executeQuery();
                while (rs.next()) {
                    FacturacionModel facturacion = new FacturacionModel();
                    facturacion.setClaCli(rs.getString(1));
                    facturacion.setFolLot(rs.getString(2));
                    facturacion.setIdLote(rs.getString(3));
                    facturacion.setClaPro(rs.getString(4));
                    facturacion.setTipMed(rs.getInt(7));
                    facturacion.setCosto(rs.getDouble(8));
                    facturacion.setClaProve(rs.getString(9));
                    facturacion.setCant(rs.getInt(10));
                    facturacion.setExiLot(rs.getInt(11));
                    facturacion.setUbica(rs.getString(12));
                    facturacion.setId(rs.getString(14));
                    facturacion.setCantSol(rs.getString(16));
                    Factura.add(facturacion);
                }
            }
            psBuscaDatosFact.clearParameters();
            psBuscaDatosFact.close();
            psBuscaTemp.clearParameters();
            psBuscaTemp.close();
            rsTemp.close();

            psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
            rsIndice = psBuscaIndice.executeQuery();
            if (rsIndice.next()) {
                FolioFactura = rsIndice.getInt(1);
            }
            FolFact = FolioFactura + 1;

            psActualizaIndice = con.getConn().prepareStatement(String.format(ACTUALIZA_INDICEFACT, Proyecto));
            psActualizaIndice.setInt(1, FolFact);
            psActualizaIndice.execute();

            psActualizaIndice.clearParameters();
            psBuscaIndice.clearParameters();

            for (FacturacionModel f : Factura) {

                TipoMed = f.getTipMed();
                existencia = f.getExiLot();
                cantidad = f.getCant();

                if (TipoMed == 2504) {
                    IVA = 0.0;
                } else {
                    IVA = 0.16;
                }

                Costo = f.getCosto();
                Costo = 0.0;

                int Diferencia = existencia - cantidad;

                if (Diferencia >= 0) {
                    if (Diferencia == 0) {
                        psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
                        psActualizaLote.setInt(1, 0);
                        psActualizaLote.setString(2, f.getIdLote());
                        psActualizaLote.execute();
                        psActualizaLote.clearParameters();
                        psActualizaLote.close();
                    } else {
                        psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
                        psActualizaLote.setInt(1, Diferencia);
                        psActualizaLote.setString(2, f.getIdLote());
                        psActualizaLote.execute();
                        psActualizaLote.clearParameters();
                        psActualizaLote.close();
                    }

                    IVAPro = (cantidad * Costo) * IVA;
                    Monto = cantidad * Costo;
                    MontoIva = Monto + IVAPro;

                    psBuscaIndice = con.getConn().prepareStatement(BUSCA_INDICEMOV);
                    rsIndice = psBuscaIndice.executeQuery();
                    if (rsIndice.next()) {
                        FolioMovi = rsIndice.getInt(1);
                    }

                    FolMov = FolioMovi + 1;

                    psActualizaIndice = con.getConn().prepareStatement(ACTUALIZA_INDICEMOV);
                    psActualizaIndice.setInt(1, FolMov);
                    psActualizaIndice.execute();
                    psActualizaIndice.clearParameters();
                    psBuscaIndice.clearParameters();
                    psBuscaIndice.close();

                    psInsertar = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
                    psInsertar.setInt(1, FolioFactura);
                    psInsertar.setInt(2, 51);
                    psInsertar.setString(3, f.getClaPro());
                    psInsertar.setInt(4, cantidad);
                    psInsertar.setDouble(5, Costo);
                    psInsertar.setDouble(6, MontoIva);
                    psInsertar.setString(7, "-1");
                    psInsertar.setString(8, f.getFolLot());
                    psInsertar.setString(9, f.getUbica());
                    psInsertar.setString(10, f.getClaProve());
                    psInsertar.setString(11, Usuario);
                    psInsertar.execute();
                    psInsertar.clearParameters();
                    psInsertar.close();

                    psInsertarFact = con.getConn().prepareStatement(INSERTA_FACTURA);
                    psInsertarFact.setInt(1, FolioFactura);
                    psInsertarFact.setString(2, f.getClaCli());
                    psInsertarFact.setString(3, f.getClaPro());
                    psInsertarFact.setString(4, f.getCantSol());
                    psInsertarFact.setInt(5, cantidad);
                    psInsertarFact.setDouble(6, Costo);
                    psInsertarFact.setDouble(7, IVAPro);
                    psInsertarFact.setDouble(8, MontoIva);
                    psInsertarFact.setString(9, f.getFolLot());
                    psInsertarFact.setString(10, FechaE);
                    psInsertarFact.setString(11, Usuario);
                    psInsertarFact.setString(12, f.getUbica());
                    psInsertarFact.setInt(13, Proyecto);
                    psInsertarFact.setString(14, Contrato);
                    psInsertarFact.setString(15, OC);
                    psInsertarFact.setString(16, Cause);
                    psInsertarFact.execute();
                    psInsertarFact.clearParameters();
                    psInsertarFact.close();

                    psActualizaTemp = con.getConn().prepareStatement(ACTUALIZA_FACTTEM);
                    psActualizaTemp.setString(1, f.getId());
                    psActualizaTemp.execute();
                    psActualizaTemp.clearParameters();
                    psActualizaTemp.close();

                } else {
                    save = false;
                    con.getConn().rollback();
                    return save;
                }

            }

            psInsertarObs = con.getConn().prepareStatement(INSERTA_OBSFACTURA);
            psInsertarObs.setInt(1, FolioFactura);
            psInsertarObs.setString(2, Observaciones);
            psInsertarObs.setString(3, Tipo);
            psInsertarObs.setInt(4, Proyecto);
            psInsertarObs.execute();
            psInsertarObs.clearParameters();
            psInsertarObs.close();

            save = true;
            con.getConn().commit();
            return save;
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;

    }
*///
/*//    @Override
    public boolean ConfirmarFactTempSemiCause(String Usuario, String Observaciones, String Tipo, int Proyecto, String OC) {
        boolean save = false;
        int ExiLot = 0, FolFact = 0, FolioFactura = 0, FolioMovi = 0, FolMov = 0, TipoMed = 0;
        int existencia = 0, cantidad = 0;
        double Costo = 0.0, IVA = 0.0, Monto = 0.0, IVAPro = 0.0, MontoIva = 0.0;
        String IdFact = "", ClaCli = "", FechaE = "", Contrato = "";
        List<FacturacionModel> Factura = new ArrayList<>();

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psBuscaContrato = con.getConn().prepareStatement(BuscaContrato);
            psBuscaContrato.setInt(1, Proyecto);
            rsContrato = psBuscaContrato.executeQuery();
            if (rsContrato.next()) {
                Contrato = rsContrato.getString(1);
            }

            psCause = con.getConn().prepareStatement(BuscaCause);
            psCause.setInt(1, Proyecto);
            rsCause = psCause.executeQuery();
            while (rsCause.next()) {

                psBuscaTemp = con.getConn().prepareStatement(BUSCA_FACTEMP);
                psBuscaTemp.setString(1, Usuario);
                rsTemp = psBuscaTemp.executeQuery();

                while (rsTemp.next()) {

                    IdFact = rsTemp.getString(2);
                    ClaCli = rsTemp.getString(3);
                    FechaE = rsTemp.getString(4);

                    psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSFACTSEMI);
                    psBuscaDatosFact.setString(1, IdFact);
                    psBuscaDatosFact.setString(2, ClaCli);
                    psBuscaDatosFact.setString(3, rsCause.getString(1));
                    rs = psBuscaDatosFact.executeQuery();
                    while (rs.next()) {
                        FacturacionModel facturacion = new FacturacionModel();
                        facturacion.setClaCli(rs.getString(1));
                        facturacion.setFolLot(rs.getString(2));
                        facturacion.setIdLote(rs.getString(3));
                        facturacion.setClaPro(rs.getString(4));
                        facturacion.setTipMed(rs.getInt(7));
                        facturacion.setCosto(rs.getDouble(8));
                        facturacion.setClaProve(rs.getString(9));
                        facturacion.setCant(rs.getInt(10));
                        facturacion.setExiLot(rs.getInt(11));
                        facturacion.setUbica(rs.getString(12));
                        facturacion.setId(rs.getString(14));
                        facturacion.setCantSol(rs.getString(16));
                        facturacion.setCause(rs.getString(23));
                        Factura.add(facturacion);
                    }
                }
                psBuscaDatosFact.clearParameters();
                psBuscaDatosFact.close();
                psBuscaTemp.clearParameters();
                psBuscaTemp.close();
                rsTemp.close();
                if (Factura.size() > 0) {
                    psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
                    rsIndice = psBuscaIndice.executeQuery();
                    if (rsIndice.next()) {
                        FolioFactura = rsIndice.getInt(1);
                    }
                    FolFact = FolioFactura + 1;

                    psActualizaIndice = con.getConn().prepareStatement(String.format(ACTUALIZA_INDICEFACT, Proyecto));
                    psActualizaIndice.setInt(1, FolFact);
                    psActualizaIndice.execute();

                    psActualizaIndice.clearParameters();
                    psBuscaIndice.clearParameters();
                    for (FacturacionModel f : Factura) {

                        TipoMed = f.getTipMed();
                        existencia = f.getExiLot();
                        cantidad = f.getCant();

                        if (TipoMed == 2504) {
                            IVA = 0.0;
                        } else {
                            IVA = 0.16;
                        }

                        Costo = f.getCosto();
                        Costo = 0.0;

                        int Diferencia = existencia - cantidad;

                        if (Diferencia >= 0) {
                            if (Diferencia == 0) {
                                psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
                                psActualizaLote.setInt(1, 0);
                                psActualizaLote.setString(2, f.getIdLote());
                                psActualizaLote.execute();
                                psActualizaLote.clearParameters();
                                psActualizaLote.close();
                            } else {
                                psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
                                psActualizaLote.setInt(1, Diferencia);
                                psActualizaLote.setString(2, f.getIdLote());
                                psActualizaLote.execute();
                                psActualizaLote.clearParameters();
                                psActualizaLote.close();
                            }

                            IVAPro = (cantidad * Costo) * IVA;
                            Monto = cantidad * Costo;
                            MontoIva = Monto + IVAPro;

                            psBuscaIndice = con.getConn().prepareStatement(BUSCA_INDICEMOV);
                            rsIndice = psBuscaIndice.executeQuery();
                            if (rsIndice.next()) {
                                FolioMovi = rsIndice.getInt(1);
                            }

                            FolMov = FolioMovi + 1;

                            psActualizaIndice = con.getConn().prepareStatement(ACTUALIZA_INDICEMOV);
                            psActualizaIndice.setInt(1, FolMov);
                            psActualizaIndice.execute();
                            psActualizaIndice.clearParameters();
                            psBuscaIndice.clearParameters();
                            psBuscaIndice.close();

                            psInsertar = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
                            psInsertar.setInt(1, FolioFactura);
                            psInsertar.setInt(2, 51);
                            psInsertar.setString(3, f.getClaPro());
                            psInsertar.setInt(4, cantidad);
                            psInsertar.setDouble(5, Costo);
                            psInsertar.setDouble(6, MontoIva);
                            psInsertar.setString(7, "-1");
                            psInsertar.setString(8, f.getFolLot());
                            psInsertar.setString(9, f.getUbica());
                            psInsertar.setString(10, f.getClaProve());
                            psInsertar.setString(11, Usuario);
                            psInsertar.execute();
                            psInsertar.clearParameters();
                            psInsertar.close();

                            psInsertarFact = con.getConn().prepareStatement(INSERTA_FACTURA);
                            psInsertarFact.setInt(1, FolioFactura);
                            psInsertarFact.setString(2, f.getClaCli());
                            psInsertarFact.setString(3, f.getClaPro());
                            psInsertarFact.setString(4, f.getCantSol());
                            psInsertarFact.setInt(5, cantidad);
                            psInsertarFact.setDouble(6, Costo);
                            psInsertarFact.setDouble(7, IVAPro);
                            psInsertarFact.setDouble(8, MontoIva);
                            psInsertarFact.setString(9, f.getFolLot());
                            psInsertarFact.setString(10, FechaE);
                            psInsertarFact.setString(11, Usuario);
                            psInsertarFact.setString(12, f.getUbica());
                            psInsertarFact.setInt(13, Proyecto);
                            psInsertarFact.setString(14, Contrato);
                            psInsertarFact.setString(15, OC);
                            psInsertarFact.setString(16, f.getCause());
                            psInsertarFact.execute();
                            psInsertarFact.clearParameters();
                            psInsertarFact.close();

                            psActualizaTemp = con.getConn().prepareStatement(ACTUALIZA_FACTTEM);
                            psActualizaTemp.setString(1, f.getId());
                            psActualizaTemp.execute();
                            psActualizaTemp.clearParameters();
                            psActualizaTemp.close();

                        } else {
                            save = false;
                            con.getConn().rollback();
                            return save;
                        }

                    }

                    Factura.clear();
                    psInsertarObs = con.getConn().prepareStatement(INSERTA_OBSFACTURA);
                    psInsertarObs.setInt(1, FolioFactura);
                    psInsertarObs.setString(2, Observaciones);
                    psInsertarObs.setString(3, Tipo);
                    psInsertarObs.setInt(4, Proyecto);
                    psInsertarObs.execute();
                    psInsertarObs.clearParameters();
                    psInsertarObs.close();
                    save = true;
                    con.getConn().commit();
                }
            }
            return save;
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;

    }
*///
/*//    @Override
    public JSONArray RegistroFactAutoCause(String ClaUni, String Catalogo, int Proyecto) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObj;
        JSONObject jsonObj2;
        System.out.println("Unidades:" + ClaUni);
        String query = "SELECT U.F_ClaPro, F_ClaUni,CONCAT(F_ClaUni,'_',REPLACE(U.F_ClaPro,'.','')) AS DATOS,F_IdReq FROM tb_unireq U INNER JOIN tb_medica M ON U.F_ClaPro=M.F_ClaPro INNER JOIN tb_catalogoprecios C ON U.F_ClaPro=C.F_ClaPro WHERE F_ClaUni in (%s) AND F_Status=0 AND  F_Solicitado != 0 AND M.F_StsPro='A' AND F_N%s='1' AND C.F_Proyecto=%s;";
        String ActualizaReq = "UPDATE tb_unireq SET F_Obs = 0 WHERE F_ClaUni in (%s);";
        PreparedStatement ps;
        ResultSet rs;
        try {
            int Contar = 0;
            con.conectar();
            ps = con.getConn().prepareStatement(String.format(ActualizaReq, ClaUni));
            ps.execute();
            ps.clearParameters();

            ps = con.getConn().prepareStatement(String.format(query, ClaUni, Catalogo, Proyecto));
            System.out.println("UNidades=" + ps);
            rs = ps.executeQuery();

            while (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("ClaPro", rs.getString(1));
                jsonObj.put("ClaUni", rs.getString(2));
                jsonObj.put("Datos", rs.getString(3));
                jsonObj.put("IdReg", rs.getString(4));
                jsonArray.add(jsonObj);

            }
            ps.close();
            ps = null;
            rs.close();
            rs = null;
            jsonObj2 = new JSONObject();
            jsonArray.add(jsonObj2);
            con.cierraConexion();

        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public boolean RegistrarFoliosCause(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC) {
        boolean save = false;
        int UbicaModu = 0, piezas = 0, F_Solicitado = 0, ContarV = 0, Existencia = 0, FolioFactura = 0, DifeSol = 0, FolioLote = 0, Cause = 0, ContarCause = 0, SumaReq = 0;
        String UbicaDesc = "", Clave = "", Unidad = "", Unidad2 = "", UbicaLote = "", Contrato = "", Ubicaciones = "", UbicaNofacturar = "";

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psBuscaContrato = con.getConn().prepareStatement(BuscaContrato);
            psBuscaContrato.setInt(1, Proyecto);
            rsContrato = psBuscaContrato.executeQuery();
            if (rsContrato.next()) {
                Contrato = rsContrato.getString(1);
            }

            psUbicaCrossdock = con.getConn().prepareStatement(BuscaUbicacionesCross);
            rsUbicaCross = psUbicaCrossdock.executeQuery();
            if (rsUbicaCross.next()) {
                Ubicaciones = rsUbicaCross.getString(1);
            }

//            if (!(Usuario.equals("Francisco"))) {
//                psUbicaNoFacturar = con.getConn().prepareStatement(BuscaUbicaNoFacturar);
//                rsUbicaNoFacturar = psUbicaNoFacturar.executeQuery();
//                if (rsUbicaNoFacturar.next()) {
//                    UbicaNofacturar = "," + rsUbicaNoFacturar.getString(1);
//                }
//            }
//
//            Ubicaciones = Ubicaciones + UbicaNofacturar;

            if (Catalogo > 0) {
                psConsulta = con.getConn().prepareStatement(BUSCA_PARAMETRO);
                psConsulta.setString(1, Usuario);
                rsConsulta = psConsulta.executeQuery();
                rsConsulta.next();
                UbicaModu = rsConsulta.getInt(1);
                Proyecto = rsConsulta.getInt(2);
                 UbicaDesc = rsConsulta.getString(3);
                psConsulta.close();
                psConsulta = null;
//                switch (UbicaModu) {
//                    case 1:
//                        UbicaDesc = " WHERE F_Ubica IN ('MODULA','A0S','APE','DENTAL','REDFRIA')";
//                        break;
//                    case 2:
//                        UbicaDesc = " WHERE F_Ubica IN ('MODULA2','A0S','APE','DENTAL','REDFRIA')";
//                        break;
//                    case 3:
//                        UbicaDesc = " WHERE F_Ubica IN ('AF','APE','DENTAL','REDFRIA','CAMARAFRIA01','CAMARAFRIA02')";
//                        break;
//                    case 4:
//                        UbicaDesc = " WHERE F_Ubica NOT IN ('A0S','MODULA','MODULA2','CADUCADOS','PROXACADUCAR','MERMA','CROSSDOCKMORELIA','INGRESOS_V','CUARENTENA','LERMA'," + Ubicaciones + ")";
//                        break;
//                   case 5:
//                        UbicaDesc = " WHERE F_Ubica IN ('AF2','APE','DENTAL','REDFRIA','CONTROLADO')";
//                        break;
//                        
//                    default:
//                        UbicaDesc = " WHERE F_Ubica IN ('AF3','APE','DENTAL','REDFRIA','CONTROLADO')";
//                        break;
//                }
            }

            psActualizaIndice = con.getConn().prepareStatement(String.format(ACTUALIZA_INDICEFACT, Proyecto));
            psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
            psInsertarMov = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
            psInsertarFact = con.getConn().prepareStatement(INSERTA_FACTURA);
            psActualizaIndiceLote = con.getConn().prepareStatement(ACTUALIZA_INDICELOTE);
            psINSERTLOTE = con.getConn().prepareStatement(INSERTAR_NUEVOLOTE);
            psActualizaReq = con.getConn().prepareStatement(ACTUALIZA_STSREQCause);
            psInsertarObs = con.getConn().prepareStatement(INSERTAR_OBSERVACIONES);

            psBuscaUnidad = con.getConn().prepareStatement(String.format(BUSCA_UNIDADES, ClaUnidad, Catalogo));
            rsBuscaUnidad = psBuscaUnidad.executeQuery();
            while (rsBuscaUnidad.next()) {
                Unidad = rsBuscaUnidad.getString(1);
                Unidad2 = rsBuscaUnidad.getString(1);
                Unidad = "'" + Unidad + "'";

                psCause = con.getConn().prepareStatement(BuscaCause);
                psCause.setInt(1, Proyecto);
                rsCause = psCause.executeQuery();
                while (rsCause.next()) {

                    psCauseFact = con.getConn().prepareStatement(String.format(BuscaCauseFactAuto, Unidad));
                    psCauseFact.setInt(1, Proyecto);
                    psCauseFact.setInt(2, rsCause.getInt(1));
                    rsCauseFact = psCauseFact.executeQuery();
                    while (rsCauseFact.next()) {
                        ContarCause = rsCauseFact.getInt(1);
                        SumaReq = rsCauseFact.getInt(2);
                        if ((rsCause.getInt(1) == 0) && (ContarCause == 0) && (SumaReq > 0)) {
                            ContarCause = 1;
                        }

                        if ((ContarCause > 0) && (SumaReq > 0)) {

                            psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
                            rsIndice = psBuscaIndice.executeQuery();
                            if (rsIndice.next()) {
                                FolioFactura = rsIndice.getInt(1);
                            }

                            //psActualizaIndice=con.getConn().prepareStatement(ACTUALIZA_INDICEFACT);
                            psActualizaIndice.setInt(1, FolioFactura + 1);
                            psActualizaIndice.addBatch();

                            /*psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSFACTURA);
                psBuscaDatosFact.setString(1, Unidad);
                psBuscaDatosFact.setInt(2, Catalogo);
*/
/*
                            psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTURARCause, UbicaDesc, UbicaDesc, Unidad, Catalogo));
                            psBuscaDatosFact.setInt(1, Proyecto);
                            psBuscaDatosFact.setInt(2, Proyecto);
                            psBuscaDatosFact.setInt(3, Proyecto);
                            psBuscaDatosFact.setInt(4, rsCause.getInt(1));
                            System.out.println("Datos Facturas" + psBuscaDatosFact);
                            rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                            while (rsBuscaDatosFact.next()) {
                                Clave = rsBuscaDatosFact.getString(2);
                                piezas = rsBuscaDatosFact.getInt(3);
                                F_Solicitado = rsBuscaDatosFact.getInt(5);
                                Existencia = rsBuscaDatosFact.getInt(8);
                                FolioLote = rsBuscaDatosFact.getInt(9);
                                UbicaLote = rsBuscaDatosFact.getString(10);
                                Observaciones = rsBuscaDatosFact.getString(11);
                                Cause = rsBuscaDatosFact.getInt(12);

                                if ((piezas > 0) && (Existencia > 0)) {

                                    int F_IdLote = 0, F_FolLot = 0, Tipo = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;
                                    int Facturado = 0, Contar = 0;
                                    String Ubicacion = "";
                                    double Costo = 0.0, IVA = 0.0, IVAPro = 0.0, Monto = 0.0, MontoIva = 0.0;
                                    if (UbicaModu == 14) {
                                        psContarReg = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI2, UbicaDesc, UbicaDesc, Catalogo));
                                    } else {
                                       psContarReg = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI, UbicaDesc, UbicaDesc, Catalogo));

                                    }
                                   // psContarReg = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI, UbicaDesc, UbicaDesc, Catalogo));
                                    psContarReg.setString(1, Clave);
                                    psContarReg.setInt(2, Proyecto);
                                    psContarReg.setInt(3, Proyecto);
                                    psContarReg.setString(4, Clave);
                                    rsContarReg = psContarReg.executeQuery();
                                    while (rsContarReg.next()) {
                                        Contar++;
                                    }
                                    if (UbicaModu == 14) {
                                        psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI2, UbicaDesc, UbicaDesc, Catalogo));
                                    } else {
                                        psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI, UbicaDesc, UbicaDesc, Catalogo));

                                    }
                                    //psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI, UbicaDesc, UbicaDesc, Catalogo));
                                    psBuscaExiFol.setString(1, Clave);
                                    psBuscaExiFol.setInt(2, Proyecto);
                                    psBuscaExiFol.setInt(3, Proyecto);
                                    psBuscaExiFol.setString(4, Clave);
                                    //psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOL, UbicaDesc, Catalogo, Clave));

                                    System.out.println("BuscaExistenciaDetalle=" + psBuscaExiFol);
                                    rsBuscaExiFol = psBuscaExiFol.executeQuery();
                                    while (rsBuscaExiFol.next()) {
                                        F_IdLote = rsBuscaExiFol.getInt(1);
                                        F_ExiLot = rsBuscaExiFol.getInt(7);
                                        F_FolLot = rsBuscaExiFol.getInt(3);
                                        Tipo = rsBuscaExiFol.getInt(4);
                                        Costo = rsBuscaExiFol.getDouble(5);
                                        Ubicacion = rsBuscaExiFol.getString(6);
                                        ClaProve = rsBuscaExiFol.getInt(8);
                                        if (Tipo == 2504) {
                                            IVA = 0.0;
                                        } else {
                                            IVA = 0.16;
                                        }

                                        Costo = 0.0;

                                        if ((F_ExiLot >= piezas) && (piezas > 0)) {
                                            Contar = Contar - 1;
                                            diferencia = F_ExiLot - piezas;
                                            CanSur = piezas;

                                            psActualizaLote.setInt(1, diferencia);
                                            psActualizaLote.setInt(2, F_IdLote);
                                            System.out.println("ActualizaLote=" + psActualizaLote + " Clave=" + Clave);
                                            psActualizaLote.addBatch();

                                            IVAPro = (CanSur * Costo) * IVA;
                                            Monto = CanSur * Costo;
                                            MontoIva = Monto + IVAPro;

                                            psInsertarMov.setInt(1, FolioFactura);
                                            psInsertarMov.setInt(2, 51);
                                            psInsertarMov.setString(3, Clave);
                                            psInsertarMov.setInt(4, CanSur);
                                            psInsertarMov.setDouble(5, Costo);
                                            psInsertarMov.setDouble(6, MontoIva);
                                            psInsertarMov.setString(7, "-1");
                                            psInsertarMov.setInt(8, F_FolLot);
                                            psInsertarMov.setString(9, Ubicacion);
                                            psInsertarMov.setInt(10, ClaProve);
                                            psInsertarMov.setString(11, Usuario);
                                            System.out.println("Mov1" + psInsertarMov);
                                            psInsertarMov.addBatch();

                                            psInsertarFact.setInt(1, FolioFactura);
                                            psInsertarFact.setString(2, Unidad2);
                                            psInsertarFact.setString(3, Clave);
                                            psInsertarFact.setInt(4, F_Solicitado);
                                            psInsertarFact.setInt(5, CanSur);
                                            psInsertarFact.setDouble(6, Costo);
                                            psInsertarFact.setDouble(7, IVAPro);
                                            psInsertarFact.setDouble(8, MontoIva);
                                            psInsertarFact.setInt(9, F_FolLot);
                                            psInsertarFact.setString(10, FecEnt);
                                            psInsertarFact.setString(11, Usuario);
                                            psInsertarFact.setString(12, Ubicacion);
                                            psInsertarFact.setInt(13, Proyecto);
                                            psInsertarFact.setString(14, Contrato);
                                            psInsertarFact.setString(15, OC);
                                            psInsertarFact.setInt(16, Cause);
                                            System.out.println("fact1" + psInsertarFact);
                                            psInsertarFact.addBatch();

                                            piezas = 0;
                                            F_Solicitado = 0;
                                            break;

                                        } else if ((piezas > 0) && (F_ExiLot > 0)) {
                                            Contar = Contar - 1;
                                            diferencia = piezas - F_ExiLot;
                                            CanSur = F_ExiLot;
                                            if (F_ExiLot >= F_Solicitado) {
                                                DifeSol = F_Solicitado;
                                            } else if (Contar > 0) {
                                                DifeSol = F_ExiLot;
                                            } else {
                                                DifeSol = F_Solicitado - F_ExiLot;
                                            }

                                            psActualizaLote.setInt(1, 0);
                                            psActualizaLote.setInt(2, F_IdLote);
                                            System.out.println("ActualizaLote2=" + psActualizaLote + " Clave=" + Clave);
                                            psActualizaLote.addBatch();

                                            IVAPro = (CanSur * Costo) * IVA;
                                            Monto = CanSur * Costo;
                                            MontoIva = Monto + IVAPro;

                                            psInsertarMov.setInt(1, FolioFactura);
                                            psInsertarMov.setInt(2, 51);
                                            psInsertarMov.setString(3, Clave);
                                            psInsertarMov.setInt(4, CanSur);
                                            psInsertarMov.setDouble(5, Costo);
                                            psInsertarMov.setDouble(6, MontoIva);
                                            psInsertarMov.setString(7, "-1");
                                            psInsertarMov.setInt(8, F_FolLot);
                                            psInsertarMov.setString(9, Ubicacion);
                                            psInsertarMov.setInt(10, ClaProve);
                                            psInsertarMov.setString(11, Usuario);
                                            System.out.println("Mov2" + psInsertarMov);
                                            psInsertarMov.addBatch();

                                            psInsertarFact.setInt(1, FolioFactura);
                                            psInsertarFact.setString(2, Unidad2);
                                            psInsertarFact.setString(3, Clave);
                                            psInsertarFact.setInt(4, DifeSol);
                                            psInsertarFact.setInt(5, CanSur);
                                            psInsertarFact.setDouble(6, Costo);
                                            psInsertarFact.setDouble(7, IVAPro);
                                            psInsertarFact.setDouble(8, MontoIva);
                                            psInsertarFact.setInt(9, F_FolLot);
                                            psInsertarFact.setString(10, FecEnt);
                                            psInsertarFact.setString(11, Usuario);
                                            psInsertarFact.setString(12, Ubicacion);
                                            psInsertarFact.setInt(13, Proyecto);
                                            psInsertarFact.setString(14, Contrato);
                                            psInsertarFact.setString(15, OC);
                                            psInsertarFact.setInt(16, Cause);
                                            System.out.println("fact2" + psInsertarFact);
                                            psInsertarFact.addBatch();

                                            F_Solicitado = F_Solicitado - CanSur;

                                            piezas = piezas - CanSur;
                                            F_ExiLot = 0;

                                        }
                                        if (Contar == 0) {
                                            if (F_Solicitado > 0) {
                                                psInsertarFact.setInt(1, FolioFactura);
                                                psInsertarFact.setString(2, Unidad2);
                                                psInsertarFact.setString(3, Clave);
                                                psInsertarFact.setInt(4, F_Solicitado);
                                                psInsertarFact.setInt(5, 0);
                                                psInsertarFact.setDouble(6, Costo);
                                                psInsertarFact.setDouble(7, IVAPro);
                                                psInsertarFact.setDouble(8, MontoIva);
                                                psInsertarFact.setInt(9, F_FolLot);
                                                psInsertarFact.setString(10, FecEnt);
                                                psInsertarFact.setString(11, Usuario);
                                                psInsertarFact.setString(12, Ubicacion);
                                                psInsertarFact.setInt(13, Proyecto);
                                                psInsertarFact.setString(14, Contrato);
                                                psInsertarFact.setString(15, OC);
                                                psInsertarFact.setInt(16, Cause);
                                                System.out.println("fact3" + psInsertarFact);
                                                psInsertarFact.addBatch();
                                                F_Solicitado = 0;
                                            }
                                        }

                                    }

                                } else if ((FolioLote > 0) && (UbicaLote != "")) {
                                    psInsertarFact.setInt(1, FolioFactura);
                                    psInsertarFact.setString(2, Unidad2);
                                    psInsertarFact.setString(3, Clave);
                                    psInsertarFact.setInt(4, F_Solicitado);
                                    psInsertarFact.setInt(5, 0);
                                    psInsertarFact.setDouble(6, 0);
                                    psInsertarFact.setDouble(7, 0);
                                    psInsertarFact.setDouble(8, 0);
                                    psInsertarFact.setInt(9, FolioLote);
                                    psInsertarFact.setString(10, FecEnt);
                                    psInsertarFact.setString(11, Usuario);
                                    psInsertarFact.setString(12, UbicaLote);
                                    psInsertarFact.setInt(13, Proyecto);
                                    psInsertarFact.setString(14, Contrato);
                                    psInsertarFact.setString(15, OC);
                                    psInsertarFact.setInt(16, Cause);
                                    System.out.println("fact4" + psInsertarFact);
                                    psInsertarFact.addBatch();
                                } else {
                                    int FolioL = 0, IndiceLote = 0;
                                    String Ubicacion = "";
                                    double Costo = 0.0;

                                    psBuscaIndiceLote = con.getConn().prepareStatement(BUSCA_INDICELOTE);
                                    rsIndiceLote = psBuscaIndiceLote.executeQuery();
                                    rsIndiceLote.next();
                                    FolioL = rsIndiceLote.getInt(1);

                                    IndiceLote = FolioL + 1;

                                    psActualizaIndiceLote.setInt(1, IndiceLote);
                                    psActualizaIndiceLote.addBatch();

                                    psINSERTLOTE.setString(1, Clave);
                                    psINSERTLOTE.setInt(2, FolioL);
                                    psINSERTLOTE.setInt(3, Proyecto);
                                    System.out.println("InsertarLote" + psINSERTLOTE);
                                    psINSERTLOTE.addBatch();

                                    psInsertarFact.setInt(1, FolioFactura);
                                    psInsertarFact.setString(2, Unidad2);
                                    psInsertarFact.setString(3, Clave);
                                    psInsertarFact.setInt(4, F_Solicitado);
                                    psInsertarFact.setInt(5, 0);
                                    psInsertarFact.setDouble(6, Costo);
                                    psInsertarFact.setDouble(7, 0);
                                    psInsertarFact.setDouble(8, 0);
                                    psInsertarFact.setInt(9, FolioL);
                                    psInsertarFact.setString(10, FecEnt);
                                    psInsertarFact.setString(11, Usuario);
                                    psInsertarFact.setString(12, "NUEVA");
                                    psInsertarFact.setInt(13, Proyecto);
                                    psInsertarFact.setString(14, Contrato);
                                    psInsertarFact.setString(15, OC);
                                    psInsertarFact.setInt(16, Cause);
                                    System.out.println("fact5" + psInsertarFact);
                                    psInsertarFact.addBatch();
                                }

                            }

                            psInsertarObs.setInt(1, FolioFactura);
                            psInsertarObs.setString(2, Observaciones + rsCause.getString(2));
                            psInsertarObs.setString(3, Tipos);
                            psInsertarObs.setInt(4, Proyecto);
                            psInsertarObs.addBatch();

                            psActualizaIndice.executeBatch();
                            psActualizaLote.executeBatch();
                            psINSERTLOTE.executeBatch();
                            psInsertarMov.executeBatch();
                            psInsertarFact.executeBatch();
                            psActualizaIndiceLote.executeBatch();
                            psInsertarObs.executeBatch();
                        }
                    }
                }
                psActualizaReq.setString(1, Unidad2);
                psActualizaReq.addBatch();;
                psActualizaReq.executeBatch();
                save = true;
                con.getConn().commit();
                System.out.println("Terminó Unidad= " + Unidad + " Con el Folio= " + FolioFactura);

            }
            psBuscaExiFol.close();
            psBuscaExiFol = null;
            rsBuscaExiFol.close();
            rsBuscaExiFol = null;
            psBuscaDatosFact.close();
            psBuscaDatosFact = null;
            rsBuscaDatosFact.close();
            rsBuscaDatosFact = null;
            psCause.close();
            psCause = null;
            psCauseFact.close();
            psCauseFact = null;
            rsCause.close();
            rsCause = null;
            rsCauseFact.close();
            rsCauseFact = null;
            psUbicaCrossdock.close();
            psUbicaCrossdock = null;
            rsUbicaCross.close();
            rsUbicaCross = null;
//            rsUbicaNoFacturar.close();
//            rsUbicaNoFacturar = null;

            return save;
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;
    }
*/
/*    @Override
    public boolean ActualizaREQCause(String ClaUni, String ClaPro, int Cantidad, int Catalogo, int Idreg, String Obs) {
        System.out.println("ClaUni=" + ClaUni + " ClaPro=" + ClaPro + " Cantidad=" + Cantidad + " Catalogo=" + Catalogo);
        boolean save = false;
        int ExiLot = 0;

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psBuscaLote = con.getConn().prepareStatement(ACTUALIZA_REQIdCause);
            psBuscaLote.setInt(1, Cantidad);
            psBuscaLote.setString(2, "1");
            psBuscaLote.setString(3, ClaPro);
            psBuscaLote.setString(4, ClaUni);
            psBuscaLote.setInt(5, Idreg);
            System.out.println("ActualizaReq=" + psBuscaLote);
            psBuscaLote.execute();
            psBuscaLote.clearParameters();
            psBuscaLote.close();
            psBuscaLote = null;
            save = true;
            con.getConn().commit();
            return save;

        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;

    }
*/
    @Override
    public boolean RegistrarFoliosApartarMich(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC) {
        boolean save = false;
        int UbicaModu = 0, piezas = 0, F_Solicitado = 0, ContarV = 0, Existencia = 0, FolioFactura = 0, DifeSol = 0, FolioLote = 0;
        String UbicaDesc = "", Clave = "", Unidad = "", Unidad2 = "", UbicaLote = "", Contrato = "", Ubicaciones = "", UbicaNofacturar = "";

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psActualizaTemp = con.getConn().prepareStatement(ELIMINA_DATOSporFACTURARTEMP);
            psActualizaTemp.setString(1, Usuario);
            psActualizaTemp.execute();
            psActualizaTemp.clearParameters();

            psBuscaContrato = con.getConn().prepareStatement(BuscaContrato);
            psBuscaContrato.setInt(1, Proyecto);
            rsContrato = psBuscaContrato.executeQuery();
            psBuscaContrato.clearParameters();
            if (rsContrato.next()) {
                Contrato = rsContrato.getString(1);
            }

//            if (!(Usuario.equals("Francisco"))) {
//                psUbicaNoFacturar = con.getConn().prepareStatement(BuscaUbicaNoFacturar);
//                rsUbicaNoFacturar = psUbicaNoFacturar.executeQuery();
//                if (rsUbicaNoFacturar.next()) {
//                    UbicaNofacturar = "," + rsUbicaNoFacturar.getString(1);
//                }
//            }
//
//            Ubicaciones = UbicaNofacturar;
            if (Catalogo > 0) {
                psConsulta = con.getConn().prepareStatement(BUSCA_PARAMETRO);
                psConsulta.setString(1, Usuario);
                rsConsulta = psConsulta.executeQuery();
                rsConsulta.next();
                UbicaModu = rsConsulta.getInt(1);
                Proyecto = rsConsulta.getInt(2);
                UbicaDesc = rsConsulta.getString(3);
                psConsulta.close();
                psConsulta = null;
//                switch (UbicaModu) {
//                    case 1:
//                        UbicaDesc = " WHERE F_Ubica IN ('MODULA','A0S','APE','DENTAL','REDFRIA')";
//                        break;
//                    case 2:
//                        UbicaDesc = " WHERE F_Ubica IN ('MODULA2','A0S','APE','DENTAL','REDFRIA')";
//                        break;
//                    case 3:
//                        UbicaDesc = " WHERE F_Ubica IN ('AF','APE','DENTAL','REDFRIA')";
//                        break;
//                    case 4:
//                        UbicaDesc = " WHERE F_Ubica NOT IN ('A0S','MODULA','MODULA2','CADUCADOS','PROXACADUCAR','MERMA','INGRESOS_V','CUARENTENA','LERMA'" + Ubicaciones + ")";
//                        break;
//                   case 5:
//                        UbicaDesc = " WHERE F_Ubica IN ('AF2','APE','DENTAL','REDFRIA','CONTROLADO')";
//                        break;
//                        
//                    default:
//                        UbicaDesc = " WHERE F_Ubica IN ('AF3','APE','DENTAL','REDFRIA','CONTROLADO')";
//                        break;
//                }
            }

            psActualizaIndice = con.getConn().prepareStatement(String.format(ACTUALIZA_INDICEFACT, Proyecto));
            psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
            psInsertarMov = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
            psInsertarFactTemp = con.getConn().prepareStatement(INSERTA_FACTURATEMP);
            psInsertarFact = con.getConn().prepareStatement(INSERTA_FACTURA);
            psActualizaIndiceLote = con.getConn().prepareStatement(ACTUALIZA_INDICELOTE);
            psINSERTLOTE = con.getConn().prepareStatement(INSERTAR_NUEVOLOTE);
//            psActualizaReq = con.getConn().prepareStatement(ACTUALIZA_STSREQ);
            psInsertarObs = con.getConn().prepareStatement(INSERTAR_OBSERVACIONES);

            psBuscaUnidad = con.getConn().prepareStatement(String.format(BUSCA_UNIDADES, ClaUnidad, Catalogo));
            rsBuscaUnidad = psBuscaUnidad.executeQuery();
            while (rsBuscaUnidad.next()) {
                Unidad = rsBuscaUnidad.getString(1);
                Unidad2 = rsBuscaUnidad.getString(1);
                Unidad = "'" + Unidad + "'";
                if (UbicaModu == 14 || UbicaModu == 40 || UbicaModu == 41) {

                    psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTURAR2, UbicaDesc, UbicaDesc, Unidad, Catalogo));
                } else {
                    psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTURAR, UbicaDesc, UbicaDesc, Unidad, Catalogo));
                }

                psBuscaDatosFact.setInt(1, Proyecto);
                psBuscaDatosFact.setInt(2, Proyecto);
                rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                while (rsBuscaDatosFact.next()) {
                    Clave = rsBuscaDatosFact.getString(2);
                    piezas = rsBuscaDatosFact.getInt(3);
                    F_Solicitado = rsBuscaDatosFact.getInt(5);
                    Existencia = rsBuscaDatosFact.getInt(8);
                    FolioLote = rsBuscaDatosFact.getInt(9);
                    UbicaLote = rsBuscaDatosFact.getString(10);
                    Observaciones = rsBuscaDatosFact.getString(11);

                    if ((piezas > 0) && (Existencia > 0)) {

                        int F_IdLote = 0, F_FolLot = 0, Tipo = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;
                        int Facturado = 0, Contar = 0;
                        String Ubicacion = "";
                        double Costo = 0.0, IVA = 0.0, IVAPro = 0.0, Monto = 0.0, MontoIva = 0.0;
                        if (UbicaModu == 14 || UbicaModu == 40 || UbicaModu == 41) {
                            psContarReg = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI2, UbicaDesc, UbicaDesc, Catalogo));
                        } else {
                            psContarReg = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI, UbicaDesc, UbicaDesc, Catalogo));

                        }
                        //psContarReg = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI, UbicaDesc, UbicaDesc, Catalogo));
                        psContarReg.setString(1, Clave);
                        psContarReg.setInt(2, Proyecto);
                        psContarReg.setInt(3, Proyecto);
                        psContarReg.setString(4, Clave);
                        rsContarReg = psContarReg.executeQuery();
                        while (rsContarReg.next()) {
                            Contar++;
                        }
                        if (UbicaModu == 14 || UbicaModu == 40 || UbicaModu == 41) {
                            psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI2, UbicaDesc, UbicaDesc, Catalogo));
                        } else {
                            psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI, UbicaDesc, UbicaDesc, Catalogo));

                        }
                        //psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI, UbicaDesc, UbicaDesc, Catalogo));
                        psBuscaExiFol.setString(1, Clave);
                        psBuscaExiFol.setInt(2, Proyecto);
                        psBuscaExiFol.setInt(3, Proyecto);
                        psBuscaExiFol.setString(4, Clave);
                        rsBuscaExiFol = psBuscaExiFol.executeQuery();
                        while (rsBuscaExiFol.next()) {
                            F_IdLote = rsBuscaExiFol.getInt(1);
                            F_ExiLot = rsBuscaExiFol.getInt(7);
                            F_FolLot = rsBuscaExiFol.getInt(3);
                            Tipo = rsBuscaExiFol.getInt(4);
                            Costo = rsBuscaExiFol.getDouble(5);
                            Ubicacion = rsBuscaExiFol.getString(6);
                            ClaProve = rsBuscaExiFol.getInt(8);
                            if (Tipo == 2504) {
                                IVA = 0.0;
                            } else {
                                IVA = 0.16;
                            }

                            Costo = 0.0;

                            if ((F_ExiLot >= piezas) && (piezas > 0)) {
                                Contar = Contar - 1;
                                diferencia = F_ExiLot - piezas;
                                CanSur = piezas;
                                IVAPro = (CanSur * Costo) * IVA;
                                Monto = CanSur * Costo;
                                MontoIva = Monto + IVAPro;
                                psInsertarFactTemp.setInt(1, FolioFactura);
                                psInsertarFactTemp.setString(2, Unidad2);
                                psInsertarFactTemp.setString(3, Clave);
                                psInsertarFactTemp.setInt(4, F_Solicitado);
                                psInsertarFactTemp.setInt(5, CanSur);
                                psInsertarFactTemp.setDouble(6, Costo);
                                psInsertarFactTemp.setDouble(7, IVAPro);
                                psInsertarFactTemp.setDouble(8, MontoIva);
                                psInsertarFactTemp.setInt(9, F_FolLot);
                                psInsertarFactTemp.setString(10, FecEnt);
                                psInsertarFactTemp.setString(11, Usuario);
                                psInsertarFactTemp.setString(12, Ubicacion);
                                psInsertarFactTemp.setString(13, Observaciones);
                                psInsertarFactTemp.setInt(14, Proyecto);
                                psInsertarFactTemp.setString(15, Contrato);
                                psInsertarFactTemp.setString(16, OC);
                                psInsertarFactTemp.setInt(17, 0);
                                psInsertarFactTemp.addBatch();

                                piezas = 0;
                                F_Solicitado = 0;
                                break;

                            } else if ((piezas > 0) && (F_ExiLot > 0)) {
                                Contar = Contar - 1;
                                diferencia = piezas - F_ExiLot;
                                CanSur = F_ExiLot;
                                if (F_ExiLot >= F_Solicitado) {
                                    DifeSol = F_Solicitado;
                                } else if (Contar > 0) {
                                    DifeSol = F_ExiLot;
                                } else {
                                    DifeSol = F_Solicitado - F_ExiLot;
                                }

                                IVAPro = (CanSur * Costo) * IVA;
                                Monto = CanSur * Costo;
                                MontoIva = Monto + IVAPro;

                                psInsertarFactTemp.setInt(1, FolioFactura);
                                psInsertarFactTemp.setString(2, Unidad2);
                                psInsertarFactTemp.setString(3, Clave);
                                psInsertarFactTemp.setInt(4, DifeSol);
                                psInsertarFactTemp.setInt(5, CanSur);
                                psInsertarFactTemp.setDouble(6, Costo);
                                psInsertarFactTemp.setDouble(7, IVAPro);
                                psInsertarFactTemp.setDouble(8, MontoIva);
                                psInsertarFactTemp.setInt(9, F_FolLot);
                                psInsertarFactTemp.setString(10, FecEnt);
                                psInsertarFactTemp.setString(11, Usuario);
                                psInsertarFactTemp.setString(12, Ubicacion);
                                psInsertarFactTemp.setString(13, Observaciones);
                                psInsertarFactTemp.setInt(14, Proyecto);
                                psInsertarFactTemp.setString(15, Contrato);
                                psInsertarFactTemp.setString(16, OC);
                                psInsertarFactTemp.setInt(17, 0);
                                psInsertarFactTemp.addBatch();

                                F_Solicitado = F_Solicitado - CanSur;

                                piezas = piezas - CanSur;
                                F_ExiLot = 0;

                            }
                            if (Contar == 0) {
                                if (F_Solicitado > 0) {
                                    psInsertarFactTemp.setInt(1, FolioFactura);
                                    psInsertarFactTemp.setString(2, Unidad2);
                                    psInsertarFactTemp.setString(3, Clave);
                                    psInsertarFactTemp.setInt(4, F_Solicitado);
                                    psInsertarFactTemp.setInt(5, 0);
                                    psInsertarFactTemp.setDouble(6, Costo);
                                    psInsertarFactTemp.setDouble(7, IVAPro);
                                    psInsertarFactTemp.setDouble(8, MontoIva);
                                    psInsertarFactTemp.setInt(9, F_FolLot);
                                    psInsertarFactTemp.setString(10, FecEnt);
                                    psInsertarFactTemp.setString(11, Usuario);
                                    psInsertarFactTemp.setString(12, Ubicacion);
                                    psInsertarFactTemp.setString(13, Observaciones);
                                    psInsertarFactTemp.setInt(14, Proyecto);
                                    psInsertarFactTemp.setString(15, Contrato);
                                    psInsertarFactTemp.setString(16, OC);
                                    psInsertarFactTemp.setInt(17, 0);
                                    psInsertarFactTemp.addBatch();
                                    F_Solicitado = 0;
                                }
                            }

                        }

                    } else if ((FolioLote > 0) && (UbicaLote != "")) {
                        psInsertarFactTemp.setInt(1, FolioFactura);
                        psInsertarFactTemp.setString(2, Unidad2);
                        psInsertarFactTemp.setString(3, Clave);
                        psInsertarFactTemp.setInt(4, F_Solicitado);
                        psInsertarFactTemp.setInt(5, 0);
                        psInsertarFactTemp.setDouble(6, 0);
                        psInsertarFactTemp.setDouble(7, 0);
                        psInsertarFactTemp.setDouble(8, 0);
                        psInsertarFactTemp.setInt(9, FolioLote);
                        psInsertarFactTemp.setString(10, FecEnt);
                        psInsertarFactTemp.setString(11, Usuario);
                        psInsertarFactTemp.setString(12, UbicaLote);
                        psInsertarFactTemp.setString(13, Observaciones);
                        psInsertarFactTemp.setInt(14, Proyecto);
                        psInsertarFactTemp.setString(15, Contrato);
                        psInsertarFactTemp.setString(16, OC);
                        psInsertarFactTemp.setInt(17, 0);
                        psInsertarFactTemp.addBatch();
                    } else {
                        int FolioL = 0, IndiceLote = 0;
                        String Ubicacion = "";
                        double Costo = 0.0;

                        psBuscaIndiceLote = con.getConn().prepareStatement(BUSCA_INDICELOTE);
                        rsIndiceLote = psBuscaIndiceLote.executeQuery();
                        rsIndiceLote.next();
                        FolioL = rsIndiceLote.getInt(1);

                        IndiceLote = FolioL + 1;

                        psActualizaIndiceLote.setInt(1, IndiceLote);
                        psActualizaIndiceLote.addBatch();

                        psINSERTLOTE.setString(1, Clave);
                        psINSERTLOTE.setInt(2, FolioL);
                        psINSERTLOTE.setInt(3, Proyecto);
                        System.out.println("InsertarLote" + psINSERTLOTE);
                        psINSERTLOTE.addBatch();

                        psInsertarFactTemp.setInt(1, FolioFactura);
                        psInsertarFactTemp.setString(2, Unidad2);
                        psInsertarFactTemp.setString(3, Clave);
                        psInsertarFactTemp.setInt(4, F_Solicitado);
                        psInsertarFactTemp.setInt(5, 0);
                        psInsertarFactTemp.setDouble(6, Costo);
                        psInsertarFactTemp.setDouble(7, 0);
                        psInsertarFactTemp.setDouble(8, 0);
                        psInsertarFactTemp.setInt(9, FolioL);
                        psInsertarFactTemp.setString(10, FecEnt);
                        psInsertarFactTemp.setString(11, Usuario);
                        psInsertarFactTemp.setString(12, "NUEVA");
                        psInsertarFactTemp.setString(13, Observaciones);
                        psInsertarFactTemp.setInt(14, Proyecto);
                        psInsertarFactTemp.setString(15, Contrato);
                        psInsertarFactTemp.setString(16, OC);
                        psInsertarFactTemp.setInt(17, 0);
                        psInsertarFactTemp.addBatch();
                    }

                }

                psActualizaReq.setString(1, Unidad2);
                psActualizaReq.addBatch();
                psINSERTLOTE.executeBatch();
                psInsertarFactTemp.executeBatch();
                psActualizaIndiceLote.executeBatch();
                psActualizaReq.executeBatch();
                save = true;
                con.getConn().commit();

            }

            psBuscaExiFol.close();
            psBuscaExiFol = null;
            rsBuscaExiFol.close();
            rsBuscaExiFol = null;
            psBuscaDatosFact.close();
            psBuscaDatosFact = null;
            rsBuscaDatosFact.close();
            rsBuscaDatosFact = null;
//            rsUbicaNoFacturar.close();
//            rsUbicaNoFacturar = null;
            psActualizaReq.close();
            psActualizaReq = null;
            psINSERTLOTE.close();
            psINSERTLOTE = null;
            psInsertarFactTemp.close();
            psInsertarFactTemp = null;
            psActualizaIndiceLote.close();
            psActualizaIndiceLote = null;
            psBuscaUnidad.close();
            psBuscaUnidad = null;
            rsBuscaUnidad.close();
            rsBuscaUnidad = null;
            return save;
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                save = false;
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;
    }

    @Override
    public boolean RegistrarFoliosMich(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC) {
        boolean save = false;
        int UbicaModu = 0, piezas = 0, F_Solicitado = 0, ContarV = 0, Existencia = 0, FolioFactura = 0, DifeSol = 0, FolioLote = 0;
        String UbicaDesc = "", Clave = "", Unidad = "", Unidad2 = "", UbicaLote = "", Contrato = "", Ubicaciones = "", UbicaNofacturar = "";

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psActualizaIndice = con.getConn().prepareStatement(String.format(ACTUALIZA_INDICEFACT, Proyecto));
            psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
            psInsertarMov = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
            psInsertarFact = con.getConn().prepareStatement(INSERTA_FACTURA);
            psInsertarObs = con.getConn().prepareStatement(INSERTAR_OBSERVACIONES);

            /*psBuscaUnidad = con.getConn().prepareStatement(String.format(BUSCA_UNIDADESTEMP, ClaUnidad));
            psBuscaUnidad.setString(1, Usuario);*/
            psBuscaUnidad = con.getConn().prepareStatement(String.format(BUSCA_UNIDADESTEMP, ClaUnidad));
            rsBuscaUnidad = psBuscaUnidad.executeQuery();
            while (rsBuscaUnidad.next()) {
                Unidad = rsBuscaUnidad.getString(1);
                Unidad2 = rsBuscaUnidad.getString(1);
                Unidad = "'" + Unidad + "'";
                String Obs = "", ContratoSelect = "";
                double Costo = 0.0, IVA = 0.0, IVAPro = 0.0, Monto = 0.0, MontoIva = 0.0;
                int CantSur = 0, CantSurCR = 0, ProyectoSelect = 0;
                psBuscaUnidadFactura = con.getConn().prepareStatement(BUSCA_UNIDADFACTURA);
                psBuscaUnidadFactura.setString(1, Unidad2);
                psBuscaUnidadFactura.setString(2, Usuario);
                psBuscaUnidadFactura.setString(3, Unidad2);
                psBuscaUnidadFactura.setString(4, Usuario);
                psBuscaUnidadFactura.setString(5, Unidad2);
                psBuscaUnidadFactura.setString(6, Usuario);
                rsBuscaUnidadFactura = psBuscaUnidadFactura.executeQuery();
                if (rsBuscaUnidadFactura.next()) {
                    CantSur = rsBuscaUnidadFactura.getInt(1);
                    CantSurCR = rsBuscaUnidadFactura.getInt(2);
                    Obs = rsBuscaUnidadFactura.getString(3);
                }

                if (CantSur > 0) {

                    psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
                    rsIndice = psBuscaIndice.executeQuery();
                    if (rsIndice.next()) {
                        FolioFactura = rsIndice.getInt(1);
                    }

                    psActualizaIndice.setInt(1, FolioFactura + 1);
                    psActualizaIndice.addBatch();

                    psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSporFACTURARTEMP);
                    psBuscaDatosFact.setString(1, Unidad2);
                    psBuscaDatosFact.setString(2, Usuario);
                    rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                    while (rsBuscaDatosFact.next()) {
                        Clave = rsBuscaDatosFact.getString(1);
                        F_Solicitado = rsBuscaDatosFact.getInt(2);
                        piezas = rsBuscaDatosFact.getInt(3);
                        FolioLote = rsBuscaDatosFact.getInt(4);
                        UbicaLote = rsBuscaDatosFact.getString(5);
                        ProyectoSelect = rsBuscaDatosFact.getInt(6);
                        ContratoSelect = rsBuscaDatosFact.getString(7);
                        Costo = rsBuscaDatosFact.getDouble(8);
                        IVA = rsBuscaDatosFact.getDouble(9);
                        Monto = rsBuscaDatosFact.getDouble(10);
                        OC = rsBuscaDatosFact.getString(11);

                        int F_IdLote = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;

                        psBuscaExiFol = con.getConn().prepareStatement(BUSCA_EXILOTE);
                        psBuscaExiFol.setString(1, Clave);
                        psBuscaExiFol.setInt(2, FolioLote);
                        psBuscaExiFol.setString(3, UbicaLote);
                        psBuscaExiFol.setInt(4, ProyectoSelect);

                        System.out.println("BuscaExistenciaDetalle=" + psBuscaExiFol);
                        rsBuscaExiFol = psBuscaExiFol.executeQuery();
                        while (rsBuscaExiFol.next()) {
                            F_IdLote = rsBuscaExiFol.getInt(1);
                            F_ExiLot = rsBuscaExiFol.getInt(2);
                            ClaProve = rsBuscaExiFol.getInt(3);

                            if ((F_ExiLot >= piezas) && (piezas > 0)) {
                                diferencia = F_ExiLot - piezas;
                                CanSur = piezas;

                                psActualizaLote.setInt(1, diferencia);
                                psActualizaLote.setInt(2, F_IdLote);
                                System.out.println("ActualizaLote=" + psActualizaLote + " Clave=" + Clave);
                                psActualizaLote.addBatch();

                                psInsertarMov.setInt(1, FolioFactura);
                                psInsertarMov.setInt(2, 51);
                                psInsertarMov.setString(3, Clave);
                                psInsertarMov.setInt(4, CanSur);
                                psInsertarMov.setDouble(5, Costo);
                                psInsertarMov.setDouble(6, Monto);
                                psInsertarMov.setString(7, "-1");
                                psInsertarMov.setInt(8, FolioLote);
                                psInsertarMov.setString(9, UbicaLote);
                                psInsertarMov.setInt(10, ClaProve);
                                psInsertarMov.setString(11, Usuario);
                                System.out.println("Mov1" + psInsertarMov);
                                psInsertarMov.addBatch();

                                psInsertarFact.setInt(1, FolioFactura);
                                psInsertarFact.setString(2, Unidad2);
                                psInsertarFact.setString(3, Clave);
                                psInsertarFact.setInt(4, F_Solicitado);
                                psInsertarFact.setInt(5, CanSur);
                                psInsertarFact.setDouble(6, Costo);
                                psInsertarFact.setDouble(7, IVA);
                                psInsertarFact.setDouble(8, Monto);
                                psInsertarFact.setInt(9, FolioLote);
                                psInsertarFact.setString(10, FecEnt);
                                psInsertarFact.setString(11, Usuario);
                                psInsertarFact.setString(12, UbicaLote);
                                psInsertarFact.setInt(13, ProyectoSelect);
                                psInsertarFact.setString(14, ContratoSelect);
                                psInsertarFact.setString(15, OC);
                                psInsertarFact.setInt(16, 0);
                                System.out.println("fact1" + psInsertarFact);
                                psInsertarFact.addBatch();

                                piezas = 0;
                                F_Solicitado = 0;
                                break;

                            } else if ((piezas > 0) && (F_ExiLot > 0)) {
                                diferencia = piezas - F_ExiLot;
                                CanSur = F_ExiLot;

                                psActualizaLote.setInt(1, 0);
                                psActualizaLote.setInt(2, F_IdLote);
                                System.out.println("ActualizaLote2=" + psActualizaLote + " Clave=" + Clave);
                                psActualizaLote.addBatch();

                                psInsertarMov.setInt(1, FolioFactura);
                                psInsertarMov.setInt(2, 51);
                                psInsertarMov.setString(3, Clave);
                                psInsertarMov.setInt(4, CanSur);
                                psInsertarMov.setDouble(5, Costo);
                                psInsertarMov.setDouble(6, Monto);
                                psInsertarMov.setString(7, "-1");
                                psInsertarMov.setInt(8, FolioLote);
                                psInsertarMov.setString(9, UbicaLote);
                                psInsertarMov.setInt(10, ClaProve);
                                psInsertarMov.setString(11, Usuario);
                                System.out.println("Mov2" + psInsertarMov);
                                psInsertarMov.addBatch();

                                psInsertarFact.setInt(1, FolioFactura);
                                psInsertarFact.setString(2, Unidad2);
                                psInsertarFact.setString(3, Clave);
                                psInsertarFact.setInt(4, F_Solicitado);
                                psInsertarFact.setInt(5, CanSur);
                                psInsertarFact.setDouble(6, Costo);
                                psInsertarFact.setDouble(7, IVA);
                                psInsertarFact.setDouble(8, Monto);
                                psInsertarFact.setInt(9, FolioLote);
                                psInsertarFact.setString(10, FecEnt);
                                psInsertarFact.setString(11, Usuario);
                                psInsertarFact.setString(12, UbicaLote);
                                psInsertarFact.setInt(13, ProyectoSelect);
                                psInsertarFact.setString(14, ContratoSelect);
                                psInsertarFact.setString(15, OC);
                                psInsertarFact.setInt(16, 0);
                                System.out.println("fact2" + psInsertarFact);
                                psInsertarFact.addBatch();

                                F_Solicitado = F_Solicitado - CanSur;

                                piezas = piezas - CanSur;
                                F_ExiLot = 0;

                            } else if ((piezas == 0) && (F_ExiLot == 0)) {

                                psInsertarFact.setInt(1, FolioFactura);
                                psInsertarFact.setString(2, Unidad2);
                                psInsertarFact.setString(3, Clave);
                                psInsertarFact.setInt(4, F_Solicitado);
                                psInsertarFact.setInt(5, 0);
                                psInsertarFact.setDouble(6, 0.00);
                                psInsertarFact.setDouble(7, 0.00);
                                psInsertarFact.setDouble(8, 0.00);
                                psInsertarFact.setInt(9, FolioLote);
                                psInsertarFact.setString(10, FecEnt);
                                psInsertarFact.setString(11, Usuario);
                                psInsertarFact.setString(12, UbicaLote);
                                psInsertarFact.setInt(13, ProyectoSelect);
                                psInsertarFact.setString(14, ContratoSelect);
                                psInsertarFact.setString(15, OC);
                                psInsertarFact.setInt(16, 0);
                                System.out.println("fact2" + psInsertarFact);
                                psInsertarFact.addBatch();
                            }

                        }

                    }

                    if (CantSurCR == 0) {
//                        psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSporFACTURARTEMPCross);
//                        psBuscaDatosFact.setString(1, Unidad2);
//                        psBuscaDatosFact.setString(2, Usuario);
//                        rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
//                        while (rsBuscaDatosFact.next()) {
//                            Clave = rsBuscaDatosFact.getString(1);
//                            F_Solicitado = rsBuscaDatosFact.getInt(2);
//                            piezas = rsBuscaDatosFact.getInt(3);
//                            FolioLote = rsBuscaDatosFact.getInt(4);
//                            UbicaLote = rsBuscaDatosFact.getString(5);
//                            ProyectoSelect = rsBuscaDatosFact.getInt(6);
//                            ContratoSelect = rsBuscaDatosFact.getString(7);
//                            Costo = rsBuscaDatosFact.getDouble(8);
//                            IVA = rsBuscaDatosFact.getDouble(9);
//                            Monto = rsBuscaDatosFact.getDouble(10);
//                            OC = rsBuscaDatosFact.getString(11);
//
//                            psInsertarFact.setInt(1, FolioFactura);
//                            psInsertarFact.setString(2, Unidad2);
//                            psInsertarFact.setString(3, Clave);
//                            psInsertarFact.setInt(4, F_Solicitado);
//                            psInsertarFact.setInt(5, 0);
//                            psInsertarFact.setDouble(6, 0.00);
//                            psInsertarFact.setDouble(7, 0.00);
//                            psInsertarFact.setDouble(8, 0.00);
//                            psInsertarFact.setInt(9, FolioLote);
//                            psInsertarFact.setString(10, FecEnt);
//                            psInsertarFact.setString(11, Usuario);
//                            psInsertarFact.setString(12, UbicaLote);
//                            psInsertarFact.setInt(13, ProyectoSelect);
//                            psInsertarFact.setString(14, ContratoSelect);
//                            psInsertarFact.setString(15, OC);
//                            psInsertarFact.setInt(16, 0);
//                            System.out.println("fact1" + psInsertarFact);
//                            psInsertarFact.addBatch();

//                        }
                    }

                    psInsertarObs.setInt(1, FolioFactura);
                    psInsertarObs.setString(2, Obs);
                    psInsertarObs.setString(3, Tipos);
                    psInsertarObs.setInt(4, Proyecto);
                    psInsertarObs.addBatch();

                    psActualizaIndice.executeBatch();
                    psActualizaLote.executeBatch();
                    psInsertarMov.executeBatch();
                    psInsertarFact.executeBatch();
                    psInsertarObs.executeBatch();
                    save = true;
                    con.getConn().commit();
                    System.out.println("Terminó Unidad= " + Unidad + " Con el Folio= " + FolioFactura);
                }

                if (CantSurCR > 0) {

                    psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
                    rsIndice = psBuscaIndice.executeQuery();
                    if (rsIndice.next()) {
                        FolioFactura = rsIndice.getInt(1);
                    }

                    psActualizaIndice.setInt(1, FolioFactura + 1);
                    psActualizaIndice.addBatch();

//                    psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSporFACTURARTEMPCross);
//                    psBuscaDatosFact.setString(1, Unidad2);
//                    psBuscaDatosFact.setString(2, Usuario);
//                    rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
//                    while (rsBuscaDatosFact.next()) {
//                        Clave = rsBuscaDatosFact.getString(1);
//                        F_Solicitado = rsBuscaDatosFact.getInt(2);
//                        piezas = rsBuscaDatosFact.getInt(3);
//                        FolioLote = rsBuscaDatosFact.getInt(4);
//                        UbicaLote = rsBuscaDatosFact.getString(5);
//                        ProyectoSelect = rsBuscaDatosFact.getInt(6);
//                        ContratoSelect = rsBuscaDatosFact.getString(7);
//                        Costo = rsBuscaDatosFact.getDouble(8);
//                        IVA = rsBuscaDatosFact.getDouble(9);
//                        Monto = rsBuscaDatosFact.getDouble(10);
//                        OC = rsBuscaDatosFact.getString(11);
//
//                        int F_IdLote = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;
//
//                        psBuscaExiFol = con.getConn().prepareStatement(BUSCA_EXILOTE);
//                        psBuscaExiFol.setString(1, Clave);
//                        psBuscaExiFol.setInt(2, FolioLote);
//                        psBuscaExiFol.setString(3, UbicaLote);
//                        psBuscaExiFol.setInt(4, ProyectoSelect);
//
//                        System.out.println("BuscaExistenciaDetalle=" + psBuscaExiFol);
//                        rsBuscaExiFol = psBuscaExiFol.executeQuery();
//                        while (rsBuscaExiFol.next()) {
//                            F_IdLote = rsBuscaExiFol.getInt(1);
//                            F_ExiLot = rsBuscaExiFol.getInt(2);
//                            ClaProve = rsBuscaExiFol.getInt(3);
//
//                            if ((F_ExiLot >= piezas) && (piezas > 0)) {
//                                diferencia = F_ExiLot - piezas;
//                                CanSur = piezas;
//
//                                psActualizaLote.setInt(1, diferencia);
//                                psActualizaLote.setInt(2, F_IdLote);
//                                System.out.println("ActualizaLote=" + psActualizaLote + " Clave=" + Clave);
//                                psActualizaLote.addBatch();
//
//                                psInsertarMov.setInt(1, FolioFactura);
//                                psInsertarMov.setInt(2, 51);
//                                psInsertarMov.setString(3, Clave);
//                                psInsertarMov.setInt(4, CanSur);
//                                psInsertarMov.setDouble(5, Costo);
//                                psInsertarMov.setDouble(6, Monto);
//                                psInsertarMov.setString(7, "-1");
//                                psInsertarMov.setInt(8, FolioLote);
//                                psInsertarMov.setString(9, UbicaLote);
//                                psInsertarMov.setInt(10, ClaProve);
//                                psInsertarMov.setString(11, Usuario);
//                                System.out.println("Mov1" + psInsertarMov);
//                                psInsertarMov.addBatch();
//
//                                psInsertarFact.setInt(1, FolioFactura);
//                                psInsertarFact.setString(2, Unidad2);
//                                psInsertarFact.setString(3, Clave);
//                                psInsertarFact.setInt(4, F_Solicitado);
//                                psInsertarFact.setInt(5, CanSur);
//                                psInsertarFact.setDouble(6, Costo);
//                                psInsertarFact.setDouble(7, IVA);
//                                psInsertarFact.setDouble(8, Monto);
//                                psInsertarFact.setInt(9, FolioLote);
//                                psInsertarFact.setString(10, FecEnt);
//                                psInsertarFact.setString(11, Usuario);
//                                psInsertarFact.setString(12, UbicaLote);
//                                psInsertarFact.setInt(13, ProyectoSelect);
//                                psInsertarFact.setString(14, ContratoSelect);
//                                psInsertarFact.setString(15, OC);
//                                psInsertarFact.setInt(16, 0);
//                                System.out.println("fact1" + psInsertarFact);
//                                psInsertarFact.addBatch();
//
//                                piezas = 0;
//                                F_Solicitado = 0;
//                                break;
//
//                            } else if ((piezas > 0) && (F_ExiLot > 0)) {
//                                diferencia = piezas - F_ExiLot;
//                                CanSur = F_ExiLot;
//
//                                psActualizaLote.setInt(1, 0);
//                                psActualizaLote.setInt(2, F_IdLote);
//                                System.out.println("ActualizaLote2=" + psActualizaLote + " Clave=" + Clave);
//                                psActualizaLote.addBatch();
//
//                                psInsertarMov.setInt(1, FolioFactura);
//                                psInsertarMov.setInt(2, 51);
//                                psInsertarMov.setString(3, Clave);
//                                psInsertarMov.setInt(4, CanSur);
//                                psInsertarMov.setDouble(5, Costo);
//                                psInsertarMov.setDouble(6, Monto);
//                                psInsertarMov.setString(7, "-1");
//                                psInsertarMov.setInt(8, FolioLote);
//                                psInsertarMov.setString(9, UbicaLote);
//                                psInsertarMov.setInt(10, ClaProve);
//                                psInsertarMov.setString(11, Usuario);
//                                System.out.println("Mov2" + psInsertarMov);
//                                psInsertarMov.addBatch();
//
//                                psInsertarFact.setInt(1, FolioFactura);
//                                psInsertarFact.setString(2, Unidad2);
//                                psInsertarFact.setString(3, Clave);
//                                psInsertarFact.setInt(4, F_Solicitado);
//                                psInsertarFact.setInt(5, CanSur);
//                                psInsertarFact.setDouble(6, Costo);
//                                psInsertarFact.setDouble(7, IVA);
//                                psInsertarFact.setDouble(8, Monto);
//                                psInsertarFact.setInt(9, FolioLote);
//                                psInsertarFact.setString(10, FecEnt);
//                                psInsertarFact.setString(11, Usuario);
//                                psInsertarFact.setString(12, UbicaLote);
//                                psInsertarFact.setInt(13, ProyectoSelect);
//                                psInsertarFact.setString(14, ContratoSelect);
//                                psInsertarFact.setString(15, OC);
//                                psInsertarFact.setInt(16, 0);
//                                System.out.println("fact2" + psInsertarFact);
//                                psInsertarFact.addBatch();
//
//                                F_Solicitado = F_Solicitado - CanSur;
//
//                                piezas = piezas - CanSur;
//                                F_ExiLot = 0;
//
//                            } else if ((piezas == 0) && (F_ExiLot == 0)) {
//
//                                psInsertarFact.setInt(1, FolioFactura);
//                                psInsertarFact.setString(2, Unidad2);
//                                psInsertarFact.setString(3, Clave);
//                                psInsertarFact.setInt(4, F_Solicitado);
//                                psInsertarFact.setInt(5, 0);
//                                psInsertarFact.setDouble(6, 0.00);
//                                psInsertarFact.setDouble(7, 0.00);
//                                psInsertarFact.setDouble(8, 0.00);
//                                psInsertarFact.setInt(9, FolioLote);
//                                psInsertarFact.setString(10, FecEnt);
//                                psInsertarFact.setString(11, Usuario);
//                                psInsertarFact.setString(12, UbicaLote);
//                                psInsertarFact.setInt(13, ProyectoSelect);
//                                psInsertarFact.setString(14, ContratoSelect);
//                                psInsertarFact.setString(15, OC);
//                                psInsertarFact.setInt(16, 0);
//                                System.out.println("fact2" + psInsertarFact);
//                                psInsertarFact.addBatch();
//
//                                F_Solicitado = F_Solicitado - CanSur;
//
//                                piezas = piezas - CanSur;
//                                F_ExiLot = 0;
//
//                            }
//
//                        }
//
//                    }
                    if (CantSur == 0) {
                        psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSporFACTURARTEMP);
                        psBuscaDatosFact.setString(1, Unidad2);
                        psBuscaDatosFact.setString(2, Usuario);
                        rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                        while (rsBuscaDatosFact.next()) {
                            Clave = rsBuscaDatosFact.getString(1);
                            F_Solicitado = rsBuscaDatosFact.getInt(2);
                            piezas = rsBuscaDatosFact.getInt(3);
                            FolioLote = rsBuscaDatosFact.getInt(4);
                            UbicaLote = rsBuscaDatosFact.getString(5);
                            ProyectoSelect = rsBuscaDatosFact.getInt(6);
                            ContratoSelect = rsBuscaDatosFact.getString(7);
                            Costo = rsBuscaDatosFact.getDouble(8);
                            IVA = rsBuscaDatosFact.getDouble(9);
                            Monto = rsBuscaDatosFact.getDouble(10);
                            OC = rsBuscaDatosFact.getString(11);

                            psInsertarFact.setInt(1, FolioFactura);
                            psInsertarFact.setString(2, Unidad2);
                            psInsertarFact.setString(3, Clave);
                            psInsertarFact.setInt(4, F_Solicitado);
                            psInsertarFact.setInt(5, 0);
                            psInsertarFact.setDouble(6, 0.00);
                            psInsertarFact.setDouble(7, 0.00);
                            psInsertarFact.setDouble(8, 0.00);
                            psInsertarFact.setInt(9, FolioLote);
                            psInsertarFact.setString(10, FecEnt);
                            psInsertarFact.setString(11, Usuario);
                            psInsertarFact.setString(12, UbicaLote);
                            psInsertarFact.setInt(13, ProyectoSelect);
                            psInsertarFact.setString(14, ContratoSelect);
                            psInsertarFact.setString(15, OC);
                            psInsertarFact.setInt(16, 0);
                            System.out.println("fact1" + psInsertarFact);
                            psInsertarFact.addBatch();

                        }
                    }

                    psInsertarObs.setInt(1, FolioFactura);
                    psInsertarObs.setString(2, Obs);
                    psInsertarObs.setString(3, Tipos);
                    psInsertarObs.setInt(4, Proyecto);
                    psInsertarObs.addBatch();

                    psActualizaIndice.executeBatch();
                    psActualizaLote.executeBatch();
                    psInsertarMov.executeBatch();
                    psInsertarFact.executeBatch();
                    psInsertarObs.executeBatch();
                    save = true;
                    con.getConn().commit();
                    System.out.println("Terminó Unidad= " + Unidad + " Con el Folio= " + FolioFactura);
                }
            }

            psBuscaExiFol.close();
            psBuscaExiFol = null;
            rsBuscaExiFol.close();
            rsBuscaExiFol = null;
            psBuscaDatosFact.close();
            psBuscaDatosFact = null;
            rsBuscaDatosFact.close();
            rsBuscaDatosFact = null;
            return save;
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                save = false;
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;
    }

//    @Override
//    public boolean RegistrarFoliosApartarAnestesia(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC) {
//        boolean save = false;
//        int UbicaModu = 0, piezas = 0, F_Solicitado = 0, ContarV = 0, Existencia = 0, FolioFactura = 0, DifeSol = 0, FolioLote = 0;
//        String UbicaDesc = "", Clave = "", Unidad = "", Unidad2 = "", UbicaLote = "", Contrato = "", Ubicaciones = "", UbicaNofacturar = "";
//
//        try {
//            con.conectar();
//            con.getConn().setAutoCommit(false);
//
//            psActualizaTemp = con.getConn().prepareStatement(ELIMINA_DATOSporFACTURARTEMP);
//            psActualizaTemp.setString(1, Usuario);
//            psActualizaTemp.execute();
//            psActualizaTemp.clearParameters();
//
//            psBuscaContrato = con.getConn().prepareStatement(BuscaContrato);
//            psBuscaContrato.setInt(1, Proyecto);
//            rsContrato = psBuscaContrato.executeQuery();
//            psBuscaContrato.clearParameters();
//            if (rsContrato.next()) {
//                Contrato = rsContrato.getString(1);
//            }
//
//            psUbicaCrossdock = con.getConn().prepareStatement(BuscaUbicacionesCross);
//            rsUbicaCross = psUbicaCrossdock.executeQuery();
//            if (rsUbicaCross.next()) {
//                Ubicaciones = rsUbicaCross.getString(1);
//            }
//
////            if (!(Usuario.equals("Francisco"))) {
////                psUbicaNoFacturar = con.getConn().prepareStatement(BuscaUbicaNoFacturar);
////                rsUbicaNoFacturar = psUbicaNoFacturar.executeQuery();
////                if (rsUbicaNoFacturar.next()) {
////                    UbicaNofacturar = "," + rsUbicaNoFacturar.getString(1);
////                }
////            }
////
////            Ubicaciones = Ubicaciones + UbicaNofacturar;
//
//            if (Catalogo > 0) {
//                psConsulta = con.getConn().prepareStatement(BUSCA_PARAMETRO);
//                psConsulta.setString(1, Usuario);
//                rsConsulta = psConsulta.executeQuery();
//                rsConsulta.next();
//                UbicaModu = rsConsulta.getInt(1);
//                Proyecto = rsConsulta.getInt(2);
//                 UbicaDesc = rsConsulta.getString(3);
//                psConsulta.close();
//                psConsulta = null;
////                switch (UbicaModu) {
////                    case 1:
////                        UbicaDesc = " WHERE F_Ubica IN ('MODULA','A0S','APE','DENTAL','REDFRIA')";
////                        break;
////                    case 2:
////                        UbicaDesc = " WHERE F_Ubica IN ('MODULA2','A0S','APE','DENTAL','REDFRIA')";
////                        break;
////                    case 3:
////                        UbicaDesc = " WHERE F_Ubica IN ('AF','APE','DENTAL','REDFRIA')";
////                        break;
////                    case 4:
////                        UbicaDesc = " WHERE F_Ubica NOT IN ('A0S','MODULA','MODULA2','CADUCADOS','PROXACADUCAR','MERMA','CROSSDOCKMORELIA','INGRESOS_V','CUARENTENA','LERMA'," + Ubicaciones + ")";
////                        break;
////                   case 5:
////                        UbicaDesc = " WHERE F_Ubica IN ('AF2','APE','DENTAL','REDFRIA','CONTROLADO')";
////                        break;
////                        
////                    default:
////                        UbicaDesc = " WHERE F_Ubica IN ('AF3','APE','DENTAL','REDFRIA','CONTROLADO')";
////                        break;
////                }
//            }
//
//            psActualizaIndice = con.getConn().prepareStatement(String.format(ACTUALIZA_INDICEFACT, Proyecto));
//            psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
//            psInsertarMov = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
//            psInsertarFactTemp = con.getConn().prepareStatement(INSERTA_FACTURATEMP);
//            psInsertarFact = con.getConn().prepareStatement(INSERTA_FACTURA);
//            psActualizaIndiceLote = con.getConn().prepareStatement(ACTUALIZA_INDICELOTE);
//            psINSERTLOTE = con.getConn().prepareStatement(INSERTAR_NUEVOLOTE);
//            psActualizaReq = con.getConn().prepareStatement(ACTUALIZA_STSREQ);
//            psInsertarObs = con.getConn().prepareStatement(INSERTAR_OBSERVACIONES);
//
//            psBuscaUnidad = con.getConn().prepareStatement(String.format(BUSCA_UNIDADES, ClaUnidad, Catalogo));
//            rsBuscaUnidad = psBuscaUnidad.executeQuery();
//            while (rsBuscaUnidad.next()) {
//                Unidad = rsBuscaUnidad.getString(1);
//                Unidad2 = rsBuscaUnidad.getString(1);
//                Unidad = "'" + Unidad + "'";
//                if (UbicaModu == 14) {
//
//                    psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTURAR2, UbicaDesc, UbicaDesc, Unidad, Catalogo));
//                } else {
//                    psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTURAR, UbicaDesc, UbicaDesc, Unidad, Catalogo));
//                }
//                psBuscaDatosFact.setInt(1, Proyecto);
//                psBuscaDatosFact.setInt(2, Proyecto);
//                rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
//                while (rsBuscaDatosFact.next()) {
//                    Clave = rsBuscaDatosFact.getString(2);
//                    piezas = rsBuscaDatosFact.getInt(3);
//                    F_Solicitado = rsBuscaDatosFact.getInt(5);
//                    Existencia = rsBuscaDatosFact.getInt(8);
//                    FolioLote = rsBuscaDatosFact.getInt(9);
//                    UbicaLote = rsBuscaDatosFact.getString(10);
//                    Observaciones = rsBuscaDatosFact.getString(11);
//
//                    if ((piezas > 0) && (Existencia > 0)) {
//
//                        int F_IdLote = 0, F_FolLot = 0, Tipo = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;
//                        int Facturado = 0, Contar = 0;
//                        String Ubicacion = "";
//                        double Costo = 0.0, IVA = 0.0, IVAPro = 0.0, Monto = 0.0, MontoIva = 0.0;
//                        
//                        psContarReg = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI, UbicaDesc, UbicaDesc, Catalogo));
//                        psContarReg.setString(1, Clave);
//                        psContarReg.setInt(2, Proyecto);
//                        psContarReg.setInt(3, Proyecto);
//                        psContarReg.setString(4, Clave);
//                        rsContarReg = psContarReg.executeQuery();
//                        while (rsContarReg.next()) {
//                            Contar++;
//                        }
//
//                        psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI, UbicaDesc, UbicaDesc, Catalogo));
//                        psBuscaExiFol.setString(1, Clave);
//                        psBuscaExiFol.setInt(2, Proyecto);
//                        psBuscaExiFol.setInt(3, Proyecto);
//                        psBuscaExiFol.setString(4, Clave);
//                        rsBuscaExiFol = psBuscaExiFol.executeQuery();
//                        while (rsBuscaExiFol.next()) {
//                            F_IdLote = rsBuscaExiFol.getInt(1);
//                            F_ExiLot = rsBuscaExiFol.getInt(7);
//                            F_FolLot = rsBuscaExiFol.getInt(3);
//                            Tipo = rsBuscaExiFol.getInt(4);
//                            Costo = rsBuscaExiFol.getDouble(5);
//                            Ubicacion = rsBuscaExiFol.getString(6);
//                            ClaProve = rsBuscaExiFol.getInt(8);
//                            if (Tipo == 2504) {
//                                IVA = 0.0;
//                            } else {
//                                IVA = 0.16;
//                            }
//
//                            Costo = 0.0;
//
//                            if ((F_ExiLot >= piezas) && (piezas > 0)) {
//                                Contar = Contar - 1;
//                                diferencia = F_ExiLot - piezas;
//                                CanSur = piezas;
//                                IVAPro = (CanSur * Costo) * IVA;
//                                Monto = CanSur * Costo;
//                                MontoIva = Monto + IVAPro;
//                                psInsertarFactTemp.setInt(1, FolioFactura);
//                                psInsertarFactTemp.setString(2, Unidad2);
//                                psInsertarFactTemp.setString(3, Clave);
//                                psInsertarFactTemp.setInt(4, F_Solicitado);
//                                psInsertarFactTemp.setInt(5, CanSur);
//                                psInsertarFactTemp.setDouble(6, Costo);
//                                psInsertarFactTemp.setDouble(7, IVAPro);
//                                psInsertarFactTemp.setDouble(8, MontoIva);
//                                psInsertarFactTemp.setInt(9, F_FolLot);
//                                psInsertarFactTemp.setString(10, FecEnt);
//                                psInsertarFactTemp.setString(11, Usuario);
//                                psInsertarFactTemp.setString(12, Ubicacion);
//                                psInsertarFactTemp.setString(13, Observaciones);
//                                psInsertarFactTemp.setInt(14, Proyecto);
//                                psInsertarFactTemp.setString(15, Contrato);
//                                psInsertarFactTemp.setString(16, OC);
//                                psInsertarFactTemp.setInt(17, 0);
//                                psInsertarFactTemp.addBatch();
//
//                                piezas = 0;
//                                F_Solicitado = 0;
//                                break;
//
//                            } else if ((piezas > 0) && (F_ExiLot > 0)) {
//                                Contar = Contar - 1;
//                                diferencia = piezas - F_ExiLot;
//                                CanSur = F_ExiLot;
//                                if (F_ExiLot >= F_Solicitado) {
//                                    DifeSol = F_Solicitado;
//                                } else if (Contar > 0) {
//                                    DifeSol = F_ExiLot;
//                                } else {
//                                    DifeSol = F_Solicitado - F_ExiLot;
//                                }
//
//                                IVAPro = (CanSur * Costo) * IVA;
//                                Monto = CanSur * Costo;
//                                MontoIva = Monto + IVAPro;
//
//                                psInsertarFactTemp.setInt(1, FolioFactura);
//                                psInsertarFactTemp.setString(2, Unidad2);
//                                psInsertarFactTemp.setString(3, Clave);
//                                psInsertarFactTemp.setInt(4, DifeSol);
//                                psInsertarFactTemp.setInt(5, CanSur);
//                                psInsertarFactTemp.setDouble(6, Costo);
//                                psInsertarFactTemp.setDouble(7, IVAPro);
//                                psInsertarFactTemp.setDouble(8, MontoIva);
//                                psInsertarFactTemp.setInt(9, F_FolLot);
//                                psInsertarFactTemp.setString(10, FecEnt);
//                                psInsertarFactTemp.setString(11, Usuario);
//                                psInsertarFactTemp.setString(12, Ubicacion);
//                                psInsertarFactTemp.setString(13, Observaciones);
//                                psInsertarFactTemp.setInt(14, Proyecto);
//                                psInsertarFactTemp.setString(15, Contrato);
//                                psInsertarFactTemp.setString(16, OC);
//                                psInsertarFactTemp.setInt(17, 0);
//                                psInsertarFactTemp.addBatch();
//
//                                F_Solicitado = F_Solicitado - CanSur;
//
//                                piezas = piezas - CanSur;
//                                F_ExiLot = 0;
//
//                            }
//                            if (Contar == 0) {
//                                if (F_Solicitado > 0) {
//                                    psInsertarFactTemp.setInt(1, FolioFactura);
//                                    psInsertarFactTemp.setString(2, Unidad2);
//                                    psInsertarFactTemp.setString(3, Clave);
//                                    psInsertarFactTemp.setInt(4, F_Solicitado);
//                                    psInsertarFactTemp.setInt(5, 0);
//                                    psInsertarFactTemp.setDouble(6, Costo);
//                                    psInsertarFactTemp.setDouble(7, IVAPro);
//                                    psInsertarFactTemp.setDouble(8, MontoIva);
//                                    psInsertarFactTemp.setInt(9, F_FolLot);
//                                    psInsertarFactTemp.setString(10, FecEnt);
//                                    psInsertarFactTemp.setString(11, Usuario);
//                                    psInsertarFactTemp.setString(12, Ubicacion);
//                                    psInsertarFactTemp.setString(13, Observaciones);
//                                    psInsertarFactTemp.setInt(14, Proyecto);
//                                    psInsertarFactTemp.setString(15, Contrato);
//                                    psInsertarFactTemp.setString(16, OC);
//                                    psInsertarFactTemp.setInt(17, 0);
//                                    psInsertarFactTemp.addBatch();
//                                    F_Solicitado = 0;
//                                }
//                            }
//
//                        }
//
//                    } else if ((FolioLote > 0) && (UbicaLote != "")) {
//                        psInsertarFactTemp.setInt(1, FolioFactura);
//                        psInsertarFactTemp.setString(2, Unidad2);
//                        psInsertarFactTemp.setString(3, Clave);
//                        psInsertarFactTemp.setInt(4, F_Solicitado);
//                        psInsertarFactTemp.setInt(5, 0);
//                        psInsertarFactTemp.setDouble(6, 0);
//                        psInsertarFactTemp.setDouble(7, 0);
//                        psInsertarFactTemp.setDouble(8, 0);
//                        psInsertarFactTemp.setInt(9, FolioLote);
//                        psInsertarFactTemp.setString(10, FecEnt);
//                        psInsertarFactTemp.setString(11, Usuario);
//                        psInsertarFactTemp.setString(12, UbicaLote);
//                        psInsertarFactTemp.setString(13, Observaciones);
//                        psInsertarFactTemp.setInt(14, Proyecto);
//                        psInsertarFactTemp.setString(15, Contrato);
//                        psInsertarFactTemp.setString(16, OC);
//                        psInsertarFactTemp.setInt(17, 0);
//                        psInsertarFactTemp.addBatch();
//                    } else {
//                        int FolioL = 0, IndiceLote = 0;
//                        String Ubicacion = "";
//                        double Costo = 0.0;
//
//                        psBuscaIndiceLote = con.getConn().prepareStatement(BUSCA_INDICELOTE);
//                        rsIndiceLote = psBuscaIndiceLote.executeQuery();
//                        rsIndiceLote.next();
//                        FolioL = rsIndiceLote.getInt(1);
//
//                        IndiceLote = FolioL + 1;
//
//                        psActualizaIndiceLote.setInt(1, IndiceLote);
//                        psActualizaIndiceLote.addBatch();
//
//                        psINSERTLOTE.setString(1, Clave);
//                        psINSERTLOTE.setInt(2, FolioL);
//                        psINSERTLOTE.setInt(3, Proyecto);
//                        System.out.println("InsertarLote" + psINSERTLOTE);
//                        psINSERTLOTE.addBatch();
//
//                        psInsertarFactTemp.setInt(1, FolioFactura);
//                        psInsertarFactTemp.setString(2, Unidad2);
//                        psInsertarFactTemp.setString(3, Clave);
//                        psInsertarFactTemp.setInt(4, F_Solicitado);
//                        psInsertarFactTemp.setInt(5, 0);
//                        psInsertarFactTemp.setDouble(6, Costo);
//                        psInsertarFactTemp.setDouble(7, 0);
//                        psInsertarFactTemp.setDouble(8, 0);
//                        psInsertarFactTemp.setInt(9, FolioL);
//                        psInsertarFactTemp.setString(10, FecEnt);
//                        psInsertarFactTemp.setString(11, Usuario);
//                        psInsertarFactTemp.setString(12, "NUEVA");
//                        psInsertarFactTemp.setString(13, Observaciones);
//                        psInsertarFactTemp.setInt(14, Proyecto);
//                        psInsertarFactTemp.setString(15, Contrato);
//                        psInsertarFactTemp.setString(16, OC);
//                        psInsertarFactTemp.setInt(17, 0);
//                        psInsertarFactTemp.addBatch();
//                    }
//
//                }
//
//                psActualizaReq.setString(1, Unidad2);
//                psActualizaReq.addBatch();
//                psINSERTLOTE.executeBatch();
//                psInsertarFactTemp.executeBatch();
//                psActualizaIndiceLote.executeBatch();
//                psActualizaReq.executeBatch();
//                save = true;
//                con.getConn().commit();
//
//            }
//
//            psBuscaExiFol.close();
//            psBuscaExiFol = null;
//            rsBuscaExiFol.close();
//            rsBuscaExiFol = null;
//            psBuscaDatosFact.close();
//            psBuscaDatosFact = null;
//            rsBuscaDatosFact.close();
//            rsBuscaDatosFact = null;
////            rsUbicaNoFacturar.close();
////            rsUbicaNoFacturar = null;
//            psActualizaReq.close();
//            psActualizaReq = null;
//            psINSERTLOTE.close();
//            psINSERTLOTE = null;
//            psInsertarFactTemp.close();
//            psInsertarFactTemp = null;
//            psActualizaIndiceLote.close();
//            psActualizaIndiceLote = null;
//            psBuscaUnidad.close();
//            psBuscaUnidad = null;
//            rsBuscaUnidad.close();
//            rsBuscaUnidad = null;
//            return save;
//        } catch (SQLException ex) {
//            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
//            try {
//                save = false;
//                con.getConn().rollback();
//                con.cierraConexion();
//            } catch (SQLException ex1) {
//                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
//            }
//        } finally {
//            try {
//                con.cierraConexion();
//            } catch (Exception ex) {
//                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
//            }
//        }
//        return save;
//    }
//
//    @Override
//    public boolean RegistrarFoliosAnestesia(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC) {
//        boolean save = false;
//        int UbicaModu = 0, piezas = 0, F_Solicitado = 0, ContarV = 0, Existencia = 0, FolioFactura = 0, DifeSol = 0, FolioLote = 0;
//        String UbicaDesc = "", Clave = "", Unidad = "", Unidad2 = "", UbicaLote = "", Contrato = "", Ubicaciones = "", UbicaNofacturar = "", Anestesia = "";
//
//        try {
//            con.conectar();
//            con.getConn().setAutoCommit(false);
//
//            psActualizaIndice = con.getConn().prepareStatement(String.format(ACTUALIZA_INDICEFACT, Proyecto));
//            psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
//            psInsertarMov = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
//            psInsertarFact = con.getConn().prepareStatement(INSERTA_FACTURA);
//            psInsertarObs = con.getConn().prepareStatement(INSERTAR_OBSERVACIONES);
//
//            psBuscaAnestesia = con.getConn().prepareStatement(BuscaAnestesia);
//            rsAnestesia = psBuscaAnestesia.executeQuery();
//            if (rsAnestesia.next()) {
//                Anestesia = rsAnestesia.getString(1);
//            }
//            psBuscaUnidad = con.getConn().prepareStatement(String.format(BUSCA_UNIDADESTEMP, ClaUnidad));
//            rsBuscaUnidad = psBuscaUnidad.executeQuery();
//            while (rsBuscaUnidad.next()) {
//                Unidad = rsBuscaUnidad.getString(1);
//                Unidad2 = rsBuscaUnidad.getString(1);
//                Unidad = "'" + Unidad + "'";
//                String Obs = "", ContratoSelect = "";
//                double Costo = 0.0, IVA = 0.0, IVAPro = 0.0, Monto = 0.0, MontoIva = 0.0;
//                int CantSur = 0, CantSurCR = 0, ProyectoSelect = 0;
//                psBuscaUnidadFactura = con.getConn().prepareStatement(String.format(BUSCA_UNIDADFACTURAANESTESIA, Anestesia, Anestesia));
//                psBuscaUnidadFactura.setString(1, Unidad2);
//                psBuscaUnidadFactura.setString(2, Usuario);
//                psBuscaUnidadFactura.setString(3, Unidad2);
//                psBuscaUnidadFactura.setString(4, Usuario);
//                psBuscaUnidadFactura.setString(5, Unidad2);
//                psBuscaUnidadFactura.setString(6, Usuario);
//                rsBuscaUnidadFactura = psBuscaUnidadFactura.executeQuery();
//                if (rsBuscaUnidadFactura.next()) {
//                    CantSur = rsBuscaUnidadFactura.getInt(1);
//                    CantSurCR = rsBuscaUnidadFactura.getInt(2);
//                    Obs = rsBuscaUnidadFactura.getString(3);
//                }
//
//                if (CantSur > 0) {
//
//                    psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
//                    rsIndice = psBuscaIndice.executeQuery();
//                    if (rsIndice.next()) {
//                        FolioFactura = rsIndice.getInt(1);
//                    }
//
//                    psActualizaIndice.setInt(1, FolioFactura + 1);
//                    psActualizaIndice.addBatch();
//
//                    psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTEMPANESTESIA, Anestesia));
//                    psBuscaDatosFact.setString(1, Unidad2);
//                    psBuscaDatosFact.setString(2, Usuario);
//                    rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
//                    while (rsBuscaDatosFact.next()) {
//                        Clave = rsBuscaDatosFact.getString(1);
//                        F_Solicitado = rsBuscaDatosFact.getInt(2);
//                        piezas = rsBuscaDatosFact.getInt(3);
//                        FolioLote = rsBuscaDatosFact.getInt(4);
//                        UbicaLote = rsBuscaDatosFact.getString(5);
//                        ProyectoSelect = rsBuscaDatosFact.getInt(6);
//                        ContratoSelect = rsBuscaDatosFact.getString(7);
//                        Costo = rsBuscaDatosFact.getDouble(8);
//                        IVA = rsBuscaDatosFact.getDouble(9);
//                        Monto = rsBuscaDatosFact.getDouble(10);
//                        OC = rsBuscaDatosFact.getString(11);
//
//                        int F_IdLote = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;
//                        Costo = 0.0;
//                        Monto = 0.0;
//                        IVA = 0.0;
//
//                        psBuscaExiFol = con.getConn().prepareStatement(BUSCA_EXILOTE);
//                        psBuscaExiFol.setString(1, Clave);
//                        psBuscaExiFol.setInt(2, FolioLote);
//                        psBuscaExiFol.setString(3, UbicaLote);
//                        psBuscaExiFol.setInt(4, ProyectoSelect);
//
//                        System.out.println("BuscaExistenciaDetalle=" + psBuscaExiFol);
//                        rsBuscaExiFol = psBuscaExiFol.executeQuery();
//                        while (rsBuscaExiFol.next()) {
//                            F_IdLote = rsBuscaExiFol.getInt(1);
//                            F_ExiLot = rsBuscaExiFol.getInt(2);
//                            ClaProve = rsBuscaExiFol.getInt(3);
//
//                            if ((F_ExiLot >= piezas) && (piezas > 0)) {
//                                diferencia = F_ExiLot - piezas;
//                                CanSur = piezas;
//
//                                psActualizaLote.setInt(1, diferencia);
//                                psActualizaLote.setInt(2, F_IdLote);
//                                System.out.println("ActualizaLote=" + psActualizaLote + " Clave=" + Clave);
//                                psActualizaLote.addBatch();
//
//                                psInsertarMov.setInt(1, FolioFactura);
//                                psInsertarMov.setInt(2, 51);
//                                psInsertarMov.setString(3, Clave);
//                                psInsertarMov.setInt(4, CanSur);
//                                psInsertarMov.setDouble(5, Costo);
//                                psInsertarMov.setDouble(6, Monto);
//                                psInsertarMov.setString(7, "-1");
//                                psInsertarMov.setInt(8, FolioLote);
//                                psInsertarMov.setString(9, UbicaLote);
//                                psInsertarMov.setInt(10, ClaProve);
//                                psInsertarMov.setString(11, Usuario);
//                                System.out.println("Mov1" + psInsertarMov);
//                                psInsertarMov.addBatch();
//
//                                psInsertarFact.setInt(1, FolioFactura);
//                                psInsertarFact.setString(2, Unidad2);
//                                psInsertarFact.setString(3, Clave);
//                                psInsertarFact.setInt(4, F_Solicitado);
//                                psInsertarFact.setInt(5, CanSur);
//                                psInsertarFact.setDouble(6, Costo);
//                                psInsertarFact.setDouble(7, IVA);
//                                psInsertarFact.setDouble(8, Monto);
//                                psInsertarFact.setInt(9, FolioLote);
//                                psInsertarFact.setString(10, FecEnt);
//                                psInsertarFact.setString(11, Usuario);
//                                psInsertarFact.setString(12, UbicaLote);
//                                psInsertarFact.setInt(13, ProyectoSelect);
//                                psInsertarFact.setString(14, ContratoSelect);
//                                psInsertarFact.setString(15, OC);
//                                psInsertarFact.setInt(16, 0);
//                                System.out.println("fact1" + psInsertarFact);
//                                psInsertarFact.addBatch();
//
//                                piezas = 0;
//                                F_Solicitado = 0;
//                                break;
//
//                            } else if ((piezas > 0) && (F_ExiLot > 0)) {
//                                diferencia = piezas - F_ExiLot;
//                                CanSur = F_ExiLot;
//
//                                psActualizaLote.setInt(1, 0);
//                                psActualizaLote.setInt(2, F_IdLote);
//                                System.out.println("ActualizaLote2=" + psActualizaLote + " Clave=" + Clave);
//                                psActualizaLote.addBatch();
//
//                                psInsertarMov.setInt(1, FolioFactura);
//                                psInsertarMov.setInt(2, 51);
//                                psInsertarMov.setString(3, Clave);
//                                psInsertarMov.setInt(4, CanSur);
//                                psInsertarMov.setDouble(5, Costo);
//                                psInsertarMov.setDouble(6, Monto);
//                                psInsertarMov.setString(7, "-1");
//                                psInsertarMov.setInt(8, FolioLote);
//                                psInsertarMov.setString(9, UbicaLote);
//                                psInsertarMov.setInt(10, ClaProve);
//                                psInsertarMov.setString(11, Usuario);
//                                System.out.println("Mov2" + psInsertarMov);
//                                psInsertarMov.addBatch();
//
//                                psInsertarFact.setInt(1, FolioFactura);
//                                psInsertarFact.setString(2, Unidad2);
//                                psInsertarFact.setString(3, Clave);
//                                psInsertarFact.setInt(4, F_Solicitado);
//                                psInsertarFact.setInt(5, CanSur);
//                                psInsertarFact.setDouble(6, Costo);
//                                psInsertarFact.setDouble(7, IVA);
//                                psInsertarFact.setDouble(8, Monto);
//                                psInsertarFact.setInt(9, FolioLote);
//                                psInsertarFact.setString(10, FecEnt);
//                                psInsertarFact.setString(11, Usuario);
//                                psInsertarFact.setString(12, UbicaLote);
//                                psInsertarFact.setInt(13, ProyectoSelect);
//                                psInsertarFact.setString(14, ContratoSelect);
//                                psInsertarFact.setString(15, OC);
//                                psInsertarFact.setInt(16, 0);
//                                System.out.println("fact2" + psInsertarFact);
//                                psInsertarFact.addBatch();
//
//                                F_Solicitado = F_Solicitado - CanSur;
//
//                                piezas = piezas - CanSur;
//                                F_ExiLot = 0;
//
//                            } else if ((piezas == 0) && (F_ExiLot == 0)) {
//
//                                psInsertarFact.setInt(1, FolioFactura);
//                                psInsertarFact.setString(2, Unidad2);
//                                psInsertarFact.setString(3, Clave);
//                                psInsertarFact.setInt(4, F_Solicitado);
//                                psInsertarFact.setInt(5, 0);
//                                psInsertarFact.setDouble(6, 0.00);
//                                psInsertarFact.setDouble(7, 0.00);
//                                psInsertarFact.setDouble(8, 0.00);
//                                psInsertarFact.setInt(9, FolioLote);
//                                psInsertarFact.setString(10, FecEnt);
//                                psInsertarFact.setString(11, Usuario);
//                                psInsertarFact.setString(12, UbicaLote);
//                                psInsertarFact.setInt(13, ProyectoSelect);
//                                psInsertarFact.setString(14, ContratoSelect);
//                                psInsertarFact.setString(15, OC);
//                                psInsertarFact.setInt(16, 0);
//                                System.out.println("fact2" + psInsertarFact);
//                                psInsertarFact.addBatch();
//                            }
//
//                        }
//
//                    }
//
//                    if (CantSurCR == 0) {
//                        psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTEMPANESTESIA2, Anestesia));
//                        psBuscaDatosFact.setString(1, Unidad2);
//                        psBuscaDatosFact.setString(2, Usuario);
//                        rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
//                        while (rsBuscaDatosFact.next()) {
//                            Clave = rsBuscaDatosFact.getString(1);
//                            F_Solicitado = rsBuscaDatosFact.getInt(2);
//                            piezas = rsBuscaDatosFact.getInt(3);
//                            FolioLote = rsBuscaDatosFact.getInt(4);
//                            UbicaLote = rsBuscaDatosFact.getString(5);
//                            ProyectoSelect = rsBuscaDatosFact.getInt(6);
//                            ContratoSelect = rsBuscaDatosFact.getString(7);
//                            Costo = rsBuscaDatosFact.getDouble(8);
//                            IVA = rsBuscaDatosFact.getDouble(9);
//                            Monto = rsBuscaDatosFact.getDouble(10);
//                            OC = rsBuscaDatosFact.getString(11);
//
//                            Costo = 0.0;
//                            Monto = 0.0;
//                            IVA = 0.0;
//
//                            psInsertarFact.setInt(1, FolioFactura);
//                            psInsertarFact.setString(2, Unidad2);
//                            psInsertarFact.setString(3, Clave);
//                            psInsertarFact.setInt(4, F_Solicitado);
//                            psInsertarFact.setInt(5, 0);
//                            psInsertarFact.setDouble(6, 0.00);
//                            psInsertarFact.setDouble(7, 0.00);
//                            psInsertarFact.setDouble(8, 0.00);
//                            psInsertarFact.setInt(9, FolioLote);
//                            psInsertarFact.setString(10, FecEnt);
//                            psInsertarFact.setString(11, Usuario);
//                            psInsertarFact.setString(12, UbicaLote);
//                            psInsertarFact.setInt(13, ProyectoSelect);
//                            psInsertarFact.setString(14, ContratoSelect);
//                            psInsertarFact.setString(15, OC);
//                            psInsertarFact.setInt(16, 0);
//                            System.out.println("fact1" + psInsertarFact);
//                            psInsertarFact.addBatch();
//
//                        }
//                    }
//
//                    psInsertarObs.setInt(1, FolioFactura);
//                    psInsertarObs.setString(2, Obs);
//                    psInsertarObs.setString(3, Tipos);
//                    psInsertarObs.setInt(4, Proyecto);
//                    psInsertarObs.addBatch();
//
//                    psActualizaIndice.executeBatch();
//                    psActualizaLote.executeBatch();
//                    psInsertarMov.executeBatch();
//                    psInsertarFact.executeBatch();
//                    psInsertarObs.executeBatch();
//                    save = true;
//                    con.getConn().commit();
//                    System.out.println("Terminó Unidad= " + Unidad + " Con el Folio= " + FolioFactura);
//                }
//
//                if (CantSurCR > 0) {
//
//                    psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
//                    rsIndice = psBuscaIndice.executeQuery();
//                    if (rsIndice.next()) {
//                        FolioFactura = rsIndice.getInt(1);
//                    }
//
//                    psActualizaIndice.setInt(1, FolioFactura + 1);
//                    psActualizaIndice.addBatch();
//
//                    psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTEMPANESTESIA2, Anestesia));
//                    psBuscaDatosFact.setString(1, Unidad2);
//                    psBuscaDatosFact.setString(2, Usuario);
//                    rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
//                    while (rsBuscaDatosFact.next()) {
//                        Clave = rsBuscaDatosFact.getString(1);
//                        F_Solicitado = rsBuscaDatosFact.getInt(2);
//                        piezas = rsBuscaDatosFact.getInt(3);
//                        FolioLote = rsBuscaDatosFact.getInt(4);
//                        UbicaLote = rsBuscaDatosFact.getString(5);
//                        ProyectoSelect = rsBuscaDatosFact.getInt(6);
//                        ContratoSelect = rsBuscaDatosFact.getString(7);
//                        Costo = rsBuscaDatosFact.getDouble(8);
//                        IVA = rsBuscaDatosFact.getDouble(9);
//                        Monto = rsBuscaDatosFact.getDouble(10);
//                        OC = rsBuscaDatosFact.getString(11);
//
//                        int F_IdLote = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;
//
//                        Costo = 0.0;
//                        Monto = 0.0;
//                        IVA = 0.0;
//
//                        psBuscaExiFol = con.getConn().prepareStatement(BUSCA_EXILOTE);
//                        psBuscaExiFol.setString(1, Clave);
//                        psBuscaExiFol.setInt(2, FolioLote);
//                        psBuscaExiFol.setString(3, UbicaLote);
//                        psBuscaExiFol.setInt(4, ProyectoSelect);
//
//                        System.out.println("BuscaExistenciaDetalle=" + psBuscaExiFol);
//                        rsBuscaExiFol = psBuscaExiFol.executeQuery();
//                        while (rsBuscaExiFol.next()) {
//                            F_IdLote = rsBuscaExiFol.getInt(1);
//                            F_ExiLot = rsBuscaExiFol.getInt(2);
//                            ClaProve = rsBuscaExiFol.getInt(3);
//
//                            if ((F_ExiLot >= piezas) && (piezas > 0)) {
//                                diferencia = F_ExiLot - piezas;
//                                CanSur = piezas;
//
//                                psActualizaLote.setInt(1, diferencia);
//                                psActualizaLote.setInt(2, F_IdLote);
//                                System.out.println("ActualizaLote=" + psActualizaLote + " Clave=" + Clave);
//                                psActualizaLote.addBatch();
//
//                                psInsertarMov.setInt(1, FolioFactura);
//                                psInsertarMov.setInt(2, 51);
//                                psInsertarMov.setString(3, Clave);
//                                psInsertarMov.setInt(4, CanSur);
//                                psInsertarMov.setDouble(5, Costo);
//                                psInsertarMov.setDouble(6, Monto);
//                                psInsertarMov.setString(7, "-1");
//                                psInsertarMov.setInt(8, FolioLote);
//                                psInsertarMov.setString(9, UbicaLote);
//                                psInsertarMov.setInt(10, ClaProve);
//                                psInsertarMov.setString(11, Usuario);
//                                System.out.println("Mov1" + psInsertarMov);
//                                psInsertarMov.addBatch();
//
//                                psInsertarFact.setInt(1, FolioFactura);
//                                psInsertarFact.setString(2, Unidad2);
//                                psInsertarFact.setString(3, Clave);
//                                psInsertarFact.setInt(4, F_Solicitado);
//                                psInsertarFact.setInt(5, CanSur);
//                                psInsertarFact.setDouble(6, Costo);
//                                psInsertarFact.setDouble(7, IVA);
//                                psInsertarFact.setDouble(8, Monto);
//                                psInsertarFact.setInt(9, FolioLote);
//                                psInsertarFact.setString(10, FecEnt);
//                                psInsertarFact.setString(11, Usuario);
//                                psInsertarFact.setString(12, UbicaLote);
//                                psInsertarFact.setInt(13, ProyectoSelect);
//                                psInsertarFact.setString(14, ContratoSelect);
//                                psInsertarFact.setString(15, OC);
//                                psInsertarFact.setInt(16, 5);
//                                System.out.println("fact1" + psInsertarFact);
//                                psInsertarFact.addBatch();
//
//                                piezas = 0;
//                                F_Solicitado = 0;
//                                break;
//
//                            } else if ((piezas > 0) && (F_ExiLot > 0)) {
//                                diferencia = piezas - F_ExiLot;
//                                CanSur = F_ExiLot;
//
//                                psActualizaLote.setInt(1, 0);
//                                psActualizaLote.setInt(2, F_IdLote);
//                                System.out.println("ActualizaLote2=" + psActualizaLote + " Clave=" + Clave);
//                                psActualizaLote.addBatch();
//
//                                psInsertarMov.setInt(1, FolioFactura);
//                                psInsertarMov.setInt(2, 51);
//                                psInsertarMov.setString(3, Clave);
//                                psInsertarMov.setInt(4, CanSur);
//                                psInsertarMov.setDouble(5, Costo);
//                                psInsertarMov.setDouble(6, Monto);
//                                psInsertarMov.setString(7, "-1");
//                                psInsertarMov.setInt(8, FolioLote);
//                                psInsertarMov.setString(9, UbicaLote);
//                                psInsertarMov.setInt(10, ClaProve);
//                                psInsertarMov.setString(11, Usuario);
//                                System.out.println("Mov2" + psInsertarMov);
//                                psInsertarMov.addBatch();
//
//                                psInsertarFact.setInt(1, FolioFactura);
//                                psInsertarFact.setString(2, Unidad2);
//                                psInsertarFact.setString(3, Clave);
//                                psInsertarFact.setInt(4, F_Solicitado);
//                                psInsertarFact.setInt(5, CanSur);
//                                psInsertarFact.setDouble(6, Costo);
//                                psInsertarFact.setDouble(7, IVA);
//                                psInsertarFact.setDouble(8, Monto);
//                                psInsertarFact.setInt(9, FolioLote);
//                                psInsertarFact.setString(10, FecEnt);
//                                psInsertarFact.setString(11, Usuario);
//                                psInsertarFact.setString(12, UbicaLote);
//                                psInsertarFact.setInt(13, ProyectoSelect);
//                                psInsertarFact.setString(14, ContratoSelect);
//                                psInsertarFact.setString(15, OC);
//                                psInsertarFact.setInt(16, 5);
//                                System.out.println("fact2" + psInsertarFact);
//                                psInsertarFact.addBatch();
//
//                                F_Solicitado = F_Solicitado - CanSur;
//
//                                piezas = piezas - CanSur;
//                                F_ExiLot = 0;
//
//                            } else if ((piezas == 0) && (F_ExiLot == 0)) {
//
//                                psInsertarFact.setInt(1, FolioFactura);
//                                psInsertarFact.setString(2, Unidad2);
//                                psInsertarFact.setString(3, Clave);
//                                psInsertarFact.setInt(4, F_Solicitado);
//                                psInsertarFact.setInt(5, 0);
//                                psInsertarFact.setDouble(6, 0.00);
//                                psInsertarFact.setDouble(7, 0.00);
//                                psInsertarFact.setDouble(8, 0.00);
//                                psInsertarFact.setInt(9, FolioLote);
//                                psInsertarFact.setString(10, FecEnt);
//                                psInsertarFact.setString(11, Usuario);
//                                psInsertarFact.setString(12, UbicaLote);
//                                psInsertarFact.setInt(13, ProyectoSelect);
//                                psInsertarFact.setString(14, ContratoSelect);
//                                psInsertarFact.setString(15, OC);
//                                psInsertarFact.setInt(16, 5);
//                                System.out.println("fact2" + psInsertarFact);
//                                psInsertarFact.addBatch();
//
//                                F_Solicitado = F_Solicitado - CanSur;
//
//                                piezas = piezas - CanSur;
//                                F_ExiLot = 0;
//
//                            }
//
//                        }
//
//                    }
//
//                    if (CantSur == 0) {
//                        psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTEMPANESTESIA, Anestesia));
//                        psBuscaDatosFact.setString(1, Unidad2);
//                        psBuscaDatosFact.setString(2, Usuario);
//                        rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
//                        while (rsBuscaDatosFact.next()) {
//                            Clave = rsBuscaDatosFact.getString(1);
//                            F_Solicitado = rsBuscaDatosFact.getInt(2);
//                            piezas = rsBuscaDatosFact.getInt(3);
//                            FolioLote = rsBuscaDatosFact.getInt(4);
//                            UbicaLote = rsBuscaDatosFact.getString(5);
//                            ProyectoSelect = rsBuscaDatosFact.getInt(6);
//                            ContratoSelect = rsBuscaDatosFact.getString(7);
//                            Costo = rsBuscaDatosFact.getDouble(8);
//                            IVA = rsBuscaDatosFact.getDouble(9);
//                            Monto = rsBuscaDatosFact.getDouble(10);
//                            OC = rsBuscaDatosFact.getString(11);
//
//                            psInsertarFact.setInt(1, FolioFactura);
//                            psInsertarFact.setString(2, Unidad2);
//                            psInsertarFact.setString(3, Clave);
//                            psInsertarFact.setInt(4, F_Solicitado);
//                            psInsertarFact.setInt(5, 0);
//                            psInsertarFact.setDouble(6, 0.00);
//                            psInsertarFact.setDouble(7, 0.00);
//                            psInsertarFact.setDouble(8, 0.00);
//                            psInsertarFact.setInt(9, FolioLote);
//                            psInsertarFact.setString(10, FecEnt);
//                            psInsertarFact.setString(11, Usuario);
//                            psInsertarFact.setString(12, UbicaLote);
//                            psInsertarFact.setInt(13, ProyectoSelect);
//                            psInsertarFact.setString(14, ContratoSelect);
//                            psInsertarFact.setString(15, OC);
//                            psInsertarFact.setInt(16, 0);
//                            System.out.println("fact1" + psInsertarFact);
//                            psInsertarFact.addBatch();
//
//                        }
//                    }
//
//                    psInsertarObs.setInt(1, FolioFactura);
//                    psInsertarObs.setString(2, Obs);
//                    psInsertarObs.setString(3, Tipos);
//                    psInsertarObs.setInt(4, Proyecto);
//                    psInsertarObs.addBatch();
//
//                    psActualizaIndice.executeBatch();
//                    psActualizaLote.executeBatch();
//                    psInsertarMov.executeBatch();
//                    psInsertarFact.executeBatch();
//                    psInsertarObs.executeBatch();
//                    save = true;
//                    con.getConn().commit();
//                    System.out.println("Terminó Unidad= " + Unidad + " Con el Folio= " + FolioFactura);
//                }
//            }
//
//            psBuscaExiFol.close();
//            psBuscaExiFol = null;
//            rsBuscaExiFol.close();
//            rsBuscaExiFol = null;
//            psBuscaDatosFact.close();
//            psBuscaDatosFact = null;
//            rsBuscaDatosFact.close();
//            rsBuscaDatosFact = null;
//            return save;
//        } catch (SQLException ex) {
//            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
//            try {
//                save = false;
//                con.getConn().rollback();
//                con.cierraConexion();
//            } catch (SQLException ex1) {
//                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
//            }
//        } finally {
//            try {
//                con.cierraConexion();
//            } catch (Exception ex) {
//                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
//            }
//        }
//        return save;
//    }
//
//    @Override
//    public boolean ConfirmarSugerencia(String ObsGral, String Solicitante) {
//
//        boolean save = false;
//        try {
//            con.conectar();
//            con.getConn().setAutoCommit(false);
//            psInsertar = con.getConn().prepareStatement(String.format(RegistrarSugerencia), PreparedStatement.RETURN_GENERATED_KEYS);
//            psInsertar.setString(1, ObsGral);
//            psInsertar.setString(2, Solicitante);
//            psInsertar.setString(3, "SAA");
//            boolean ok = psInsertar.executeUpdate() == 1;
//            if (!ok) {
//                psInsertar.close();
//                save = false;
//                String mensajeError = String.format("NO creo el registro");
//                throw new SQLException(mensajeError);
//            } else {
//                rs = psInsertar.getGeneratedKeys();
//                rs.next();
//                int id = rs.getInt(1);
//                psInsertar.close();
//                psInsertar = null;
//                rs.close();
//                rs = null;
//                save = true;
//                con.getConn().commit();
//                return save;
//            }
//
//        } catch (SQLException ex) {
//            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
//            try {
//                save = false;
//                con.getConn().rollback();
//                con.cierraConexion();
//            } catch (SQLException ex1) {
//                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
//            }
//        } finally {
//            try {
//                con.cierraConexion();
//            } catch (Exception ex) {
//                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
//            }
//        }
//        return save;
//    }
//
//    @Override
//    public boolean ConfirmarSugerenciaCompra(String ObsGral, String Solicitante) {
//
//        boolean save = false;
//        try {
//            con.conectar();
//            con.getConn().setAutoCommit(false);
//            psInsertar = con.getConn().prepareStatement(String.format(RegistrarSugerencia), PreparedStatement.RETURN_GENERATED_KEYS);
//            psInsertar.setString(1, ObsGral);
//            psInsertar.setString(2, Solicitante);
//            psInsertar.setString(3, "COMPRA");
//            boolean ok = psInsertar.executeUpdate() == 1;
//            if (!ok) {
//                psInsertar.close();
//                save = false;
//                String mensajeError = String.format("NO creo el registro");
//                throw new SQLException(mensajeError);
//            } else {
//                rs = psInsertar.getGeneratedKeys();
//                rs.next();
//                int id = rs.getInt(1);
//                psInsertar.close();
//                psInsertar = null;
//                rs.close();
//                rs = null;
//                save = true;
//                con.getConn().commit();
//                return save;
//            }
//
//        } catch (SQLException ex) {
//            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
//            try {
//                save = false;
//                con.getConn().rollback();
//                con.cierraConexion();
//            } catch (SQLException ex1) {
//                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
//            }
//        } finally {
//            try {
//                con.cierraConexion();
//            } catch (Exception ex) {
//                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
//            }
//        }
//        return save;
//    }
/*
    @Override
    public boolean RegistrarFoliosApartar5Folio(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC) {
        boolean save = false;
        int UbicaModu = 0, piezas = 0, F_Solicitado = 0, ContarV = 0, Existencia = 0, FolioFactura = 0, DifeSol = 0, FolioLote = 0, Parametro = 0;
        String UbicaDesc = "", Clave = "", Unidad = "", Unidad2 = "", UbicaLote = "", Contrato = "", Ubicaciones = "", UbicaNofacturar = "";

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psActualizaTemp = con.getConn().prepareStatement(ELIMINA_DATOSporFACTURARTEMP);
            psActualizaTemp.setString(1, Usuario);
            psActualizaTemp.execute();
            psActualizaTemp.clearParameters();

            psBuscaContrato = con.getConn().prepareStatement(BuscaContrato);
            psBuscaContrato.setInt(1, Proyecto);
            rsContrato = psBuscaContrato.executeQuery();
            psBuscaContrato.clearParameters();
            if (rsContrato.next()) {
                Contrato = rsContrato.getString(1);
            }

//            psUbicaCrossdock = con.getConn().prepareStatement(BuscaUbicacionesCross);
//            rsUbicaCross = psUbicaCrossdock.executeQuery();
//            if (rsUbicaCross.next()) {
//                Ubicaciones = rsUbicaCross.getString(1);
//            }
//            if (!(Usuario.equals("Francisco"))) {
//                psUbicaNoFacturar = con.getConn().prepareStatement(BuscaUbicaNoFacturar);
//                rsUbicaNoFacturar = psUbicaNoFacturar.executeQuery();
//                if (rsUbicaNoFacturar.next()) {
//                    UbicaNofacturar = "," + rsUbicaNoFacturar.getString(1);
//                }
//            }
//
//            Ubicaciones = Ubicaciones + UbicaNofacturar;
            if (Catalogo > 0) {
                psConsulta = con.getConn().prepareStatement(BUSCA_PARAMETRO);
                psConsulta.setString(1, Usuario);
                rsConsulta = psConsulta.executeQuery();
                rsConsulta.next();
                UbicaModu = rsConsulta.getInt(1);
                Proyecto = rsConsulta.getInt(2);
                UbicaDesc = rsConsulta.getString(3);
                psConsulta.close();
                psConsulta = null;
//                switch (UbicaModu) {
//                    case 1:
//                        UbicaDesc = " WHERE F_Ubica IN ('MODULA','A0S','APE','DENTAL','REDFRIA')";
//                        break;
//                    case 2:
//                        UbicaDesc = " WHERE F_Ubica IN ('MODULA2','A0S','APE','DENTAL','REDFRIA')";
//                        break;
//                    case 3:
//                        UbicaDesc = " WHERE F_Ubica IN ('AF','APE','DENTAL','REDFRIA')";
//                        break;
//                    case 4:
//                        UbicaDesc = " WHERE F_Ubica NOT IN ('A0S','MODULA','MODULA2','CADUCADOS','PROXACADUCAR','MERMA','CROSSDOCKMORELIA','INGRESOS_V','CUARENTENA','LERMA'," + Ubicaciones + ")";
//                        break;
//                   case 5:
//                        UbicaDesc = " WHERE F_Ubica IN ('AF2','APE','DENTAL','REDFRIA','CONTROLADO')";
//                        break;
//                        
//                    default:
//                        UbicaDesc = " WHERE F_Ubica IN ('AF3','APE','DENTAL','REDFRIA','CONTROLADO')";
//                        break;
//                }
            }

            psActualizaIndice = con.getConn().prepareStatement(String.format(ACTUALIZA_INDICEFACT, Proyecto));
            psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
            psInsertarMov = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
            psInsertarFactTemp = con.getConn().prepareStatement(INSERTA_FACTURATEMP);
            psInsertarFact = con.getConn().prepareStatement(INSERTA_FACTURA);
            psActualizaIndiceLote = con.getConn().prepareStatement(ACTUALIZA_INDICELOTE);
            psINSERTLOTE = con.getConn().prepareStatement(INSERTAR_NUEVOLOTE);
            psActualizaReq = con.getConn().prepareStatement(ACTUALIZA_STSREQ);
            psInsertarObs = con.getConn().prepareStatement(INSERTAR_OBSERVACIONES);

            psBuscaUnidad = con.getConn().prepareStatement(String.format(BUSCA_UNIDADES, ClaUnidad, Catalogo));
            rsBuscaUnidad = psBuscaUnidad.executeQuery();
            while (rsBuscaUnidad.next()) {
                Unidad = rsBuscaUnidad.getString(1);
                Unidad2 = rsBuscaUnidad.getString(1);
                Unidad = "'" + Unidad + "'";

                if (UbicaModu == 14) {

                    psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTURAR2, UbicaDesc, UbicaDesc, Unidad, Catalogo));
                } else {
                    psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTURAR, UbicaDesc, UbicaDesc, Unidad, Catalogo));
                }
                psBuscaDatosFact.setInt(1, Proyecto);
                psBuscaDatosFact.setInt(2, Proyecto);
                rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                while (rsBuscaDatosFact.next()) {
                    Clave = rsBuscaDatosFact.getString(2);
                    piezas = rsBuscaDatosFact.getInt(3);
                    F_Solicitado = rsBuscaDatosFact.getInt(5);
                    Existencia = rsBuscaDatosFact.getInt(8);
                    FolioLote = rsBuscaDatosFact.getInt(9);
                    UbicaLote = rsBuscaDatosFact.getString(10);
                    Observaciones = rsBuscaDatosFact.getString(11);

                    if ((piezas > 0) && (Existencia > 0)) {

                        int F_IdLote = 0, F_FolLot = 0, Tipo = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;
                        int Facturado = 0, Contar = 0;
                        String Ubicacion = "";
                        double Costo = 0.0, IVA = 0.0, IVAPro = 0.0, Monto = 0.0, MontoIva = 0.0;
                        if (UbicaModu == 14) {
                            psContarReg = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI5FOLIO2, UbicaDesc, UbicaDesc, Catalogo));
                        } else {
                            psContarReg = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI5FOLIO, UbicaDesc, UbicaDesc, Catalogo));

                        }
                        psContarReg.setString(1, Clave);
                        psContarReg.setInt(2, Proyecto);
                        psContarReg.setInt(3, Proyecto);
                        psContarReg.setString(4, Clave);
                        rsContarReg = psContarReg.executeQuery();
                        while (rsContarReg.next()) {
                            Contar++;
                        }
                        if (UbicaModu == 14) {
                            psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI5FOLIO2, UbicaDesc, UbicaDesc, Catalogo));

                        } else {
                            psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI5FOLIO, UbicaDesc, UbicaDesc, Catalogo));

                        }
                        psBuscaExiFol.setString(1, Clave);
                        psBuscaExiFol.setInt(2, Proyecto);
                        psBuscaExiFol.setInt(3, Proyecto);
                        psBuscaExiFol.setString(4, Clave);
                        rsBuscaExiFol = psBuscaExiFol.executeQuery();
                        while (rsBuscaExiFol.next()) {
                            F_IdLote = rsBuscaExiFol.getInt(1);
                            F_ExiLot = rsBuscaExiFol.getInt(7);
                            F_FolLot = rsBuscaExiFol.getInt(3);
                            Tipo = rsBuscaExiFol.getInt(4);
                            Costo = rsBuscaExiFol.getDouble(5);
                            Ubicacion = rsBuscaExiFol.getString(6);
                            ClaProve = rsBuscaExiFol.getInt(8);
                            if (Tipo == 2504) {
                                IVA = 0.0;
                            } else {
                                IVA = 0.16;
                            }

                            Costo = 0.0;

                            if ((F_ExiLot >= piezas) && (piezas > 0)) {
                                Contar = Contar - 1;
                                diferencia = F_ExiLot - piezas;
                                CanSur = piezas;
                                IVAPro = (CanSur * Costo) * IVA;
                                Monto = CanSur * Costo;
                                MontoIva = Monto + IVAPro;
                                psInsertarFactTemp.setInt(1, FolioFactura);
                                psInsertarFactTemp.setString(2, Unidad2);
                                psInsertarFactTemp.setString(3, Clave);
                                psInsertarFactTemp.setInt(4, F_Solicitado);
                                psInsertarFactTemp.setInt(5, CanSur);
                                psInsertarFactTemp.setDouble(6, Costo);
                                psInsertarFactTemp.setDouble(7, IVAPro);
                                psInsertarFactTemp.setDouble(8, MontoIva);
                                psInsertarFactTemp.setInt(9, F_FolLot);
                                psInsertarFactTemp.setString(10, FecEnt);
                                psInsertarFactTemp.setString(11, Usuario);
                                psInsertarFactTemp.setString(12, Ubicacion);
                                psInsertarFactTemp.setString(13, Observaciones);
                                psInsertarFactTemp.setInt(14, Proyecto);
                                psInsertarFactTemp.setString(15, Contrato);
                                psInsertarFactTemp.setString(16, OC);
                                psInsertarFactTemp.setInt(17, 0);
                                psInsertarFactTemp.addBatch();

                                piezas = 0;
                                F_Solicitado = 0;
                                break;

                            } else if ((piezas > 0) && (F_ExiLot > 0)) {
                                Contar = Contar - 1;
                                diferencia = piezas - F_ExiLot;
                                CanSur = F_ExiLot;
                                if (F_ExiLot >= F_Solicitado) {
                                    DifeSol = F_Solicitado;
                                } else if (Contar > 0) {
                                    DifeSol = F_ExiLot;
                                } else {
                                    DifeSol = F_Solicitado - F_ExiLot;
                                }

                                IVAPro = (CanSur * Costo) * IVA;
                                Monto = CanSur * Costo;
                                MontoIva = Monto + IVAPro;

                                psInsertarFactTemp.setInt(1, FolioFactura);
                                psInsertarFactTemp.setString(2, Unidad2);
                                psInsertarFactTemp.setString(3, Clave);
                                psInsertarFactTemp.setInt(4, DifeSol);
                                psInsertarFactTemp.setInt(5, CanSur);
                                psInsertarFactTemp.setDouble(6, Costo);
                                psInsertarFactTemp.setDouble(7, IVAPro);
                                psInsertarFactTemp.setDouble(8, MontoIva);
                                psInsertarFactTemp.setInt(9, F_FolLot);
                                psInsertarFactTemp.setString(10, FecEnt);
                                psInsertarFactTemp.setString(11, Usuario);
                                psInsertarFactTemp.setString(12, Ubicacion);
                                psInsertarFactTemp.setString(13, Observaciones);
                                psInsertarFactTemp.setInt(14, Proyecto);
                                psInsertarFactTemp.setString(15, Contrato);
                                psInsertarFactTemp.setString(16, OC);
                                psInsertarFactTemp.setInt(17, 0);
                                psInsertarFactTemp.addBatch();

                                F_Solicitado = F_Solicitado - CanSur;

                                piezas = piezas - CanSur;
                                F_ExiLot = 0;

                            }
                            if (Contar == 0) {
                                if (F_Solicitado > 0) {
                                    psInsertarFactTemp.setInt(1, FolioFactura);
                                    psInsertarFactTemp.setString(2, Unidad2);
                                    psInsertarFactTemp.setString(3, Clave);
                                    psInsertarFactTemp.setInt(4, F_Solicitado);
                                    psInsertarFactTemp.setInt(5, 0);
                                    psInsertarFactTemp.setDouble(6, Costo);
                                    psInsertarFactTemp.setDouble(7, IVAPro);
                                    psInsertarFactTemp.setDouble(8, MontoIva);
                                    psInsertarFactTemp.setInt(9, F_FolLot);
                                    psInsertarFactTemp.setString(10, FecEnt);
                                    psInsertarFactTemp.setString(11, Usuario);
                                    psInsertarFactTemp.setString(12, Ubicacion);
                                    psInsertarFactTemp.setString(13, Observaciones);
                                    psInsertarFactTemp.setInt(14, Proyecto);
                                    psInsertarFactTemp.setString(15, Contrato);
                                    psInsertarFactTemp.setString(16, OC);
                                    psInsertarFactTemp.setInt(17, 0);
                                    psInsertarFactTemp.addBatch();
                                    F_Solicitado = 0;
                                }
                            }

                        }

                    } else if ((FolioLote > 0) && (UbicaLote != "")) {
                        psInsertarFactTemp.setInt(1, FolioFactura);
                        psInsertarFactTemp.setString(2, Unidad2);
                        psInsertarFactTemp.setString(3, Clave);
                        psInsertarFactTemp.setInt(4, F_Solicitado);
                        psInsertarFactTemp.setInt(5, 0);
                        psInsertarFactTemp.setDouble(6, 0);
                        psInsertarFactTemp.setDouble(7, 0);
                        psInsertarFactTemp.setDouble(8, 0);
                        psInsertarFactTemp.setInt(9, FolioLote);
                        psInsertarFactTemp.setString(10, FecEnt);
                        psInsertarFactTemp.setString(11, Usuario);
                        psInsertarFactTemp.setString(12, UbicaLote);
                        psInsertarFactTemp.setString(13, Observaciones);
                        psInsertarFactTemp.setInt(14, Proyecto);
                        psInsertarFactTemp.setString(15, Contrato);
                        psInsertarFactTemp.setString(16, OC);
                        psInsertarFactTemp.setInt(17, 0);
                        psInsertarFactTemp.addBatch();
                    } else {
                        int FolioL = 0, IndiceLote = 0;
                        String Ubicacion = "";
                        double Costo = 0.0;

                        psBuscaIndiceLote = con.getConn().prepareStatement(BUSCA_INDICELOTE);
                        rsIndiceLote = psBuscaIndiceLote.executeQuery();
                        rsIndiceLote.next();
                        FolioL = rsIndiceLote.getInt(1);

                        IndiceLote = FolioL + 1;

                        psActualizaIndiceLote.setInt(1, IndiceLote);
                        psActualizaIndiceLote.addBatch();

                        psINSERTLOTE.setString(1, Clave);
                        psINSERTLOTE.setInt(2, FolioL);
                        psINSERTLOTE.setInt(3, Proyecto);
                        System.out.println("InsertarLote" + psINSERTLOTE);
                        psINSERTLOTE.addBatch();

                        psInsertarFactTemp.setInt(1, FolioFactura);
                        psInsertarFactTemp.setString(2, Unidad2);
                        psInsertarFactTemp.setString(3, Clave);
                        psInsertarFactTemp.setInt(4, F_Solicitado);
                        psInsertarFactTemp.setInt(5, 0);
                        psInsertarFactTemp.setDouble(6, Costo);
                        psInsertarFactTemp.setDouble(7, 0);
                        psInsertarFactTemp.setDouble(8, 0);
                        psInsertarFactTemp.setInt(9, FolioL);
                        psInsertarFactTemp.setString(10, FecEnt);
                        psInsertarFactTemp.setString(11, Usuario);
                        psInsertarFactTemp.setString(12, "NUEVA");
                        psInsertarFactTemp.setString(13, Observaciones);
                        psInsertarFactTemp.setInt(14, Proyecto);
                        psInsertarFactTemp.setString(15, Contrato);
                        psInsertarFactTemp.setString(16, OC);
                        psInsertarFactTemp.setInt(17, 0);
                        psInsertarFactTemp.addBatch();
                    }

                }

                psActualizaReq.setString(1, Unidad2);
                psActualizaReq.addBatch();
                psINSERTLOTE.executeBatch();
                psInsertarFactTemp.executeBatch();
                psActualizaIndiceLote.executeBatch();
                psActualizaReq.executeBatch();
                save = true;
                con.getConn().commit();

            }

            psBuscaExiFol.close();
            psBuscaExiFol = null;
            rsBuscaExiFol.close();
            rsBuscaExiFol = null;
            psBuscaDatosFact.close();
            psBuscaDatosFact = null;
            rsBuscaDatosFact.close();
            rsBuscaDatosFact = null;
//            rsUbicaNoFacturar.close();
//            rsUbicaNoFacturar = null;
            psActualizaReq.close();
            psActualizaReq = null;
            psINSERTLOTE.close();
            psINSERTLOTE = null;
            psInsertarFactTemp.close();
            psInsertarFactTemp = null;
            psActualizaIndiceLote.close();
            psActualizaIndiceLote = null;
            psBuscaUnidad.close();
            psBuscaUnidad = null;
            rsBuscaUnidad.close();
            rsBuscaUnidad = null;
            return save;
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                save = false;
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;
    }
*/

/*
    @Override
    public boolean Registro5Folio(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC) {
        boolean save = false;
        int UbicaModu = 0, piezas = 0, F_Solicitado = 0, ContarV = 0, Existencia = 0, FolioFactura = 0, DifeSol = 0, FolioLote = 0, IdFact = 0;
        String UbicaDesc = "", Clave = "", Unidad = "", Unidad2 = "", UbicaLote = "", Contrato = "", Ubicaciones = "", UbicaNofacturar = "", Anestesia = "";

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psActualizaIndice = con.getConn().prepareStatement(String.format(ACTUALIZA_INDICEFACT, Proyecto));
            psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
            psInsertarMov = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
            psInsertarFact = con.getConn().prepareStatement(INSERTA_FACTURA);
            psInsertarObs = con.getConn().prepareStatement(INSERTAR_OBSERVACIONES);

//            psBuscaAnestesia = con.getConn().prepareStatement(BuscaAnestesia);
//            rsAnestesia = psBuscaAnestesia.executeQuery();
//            if (rsAnestesia.next()) {
//                Anestesia = rsAnestesia.getString(1);
//            }
            psBuscaUnidad = con.getConn().prepareStatement(String.format(BUSCA_UNIDADESTEMP, ClaUnidad));
            rsBuscaUnidad = psBuscaUnidad.executeQuery();
            while (rsBuscaUnidad.next()) {
                Unidad = rsBuscaUnidad.getString(1);
                Unidad2 = rsBuscaUnidad.getString(1);
                Unidad = "'" + Unidad + "'";
                String Obs = "", ContratoSelect = "";
                double Costo = 0.0, IVA = 0.0, IVAPro = 0.0, Monto = 0.0, MontoIva = 0.0;
                int CantSur = 0, CantSurCR = 0, ProyectoSelect = 0, CantSurT = 0;
                psBuscaUnidadFactura = con.getConn().prepareStatement(String.format(BUSCA_UNIDADFACTURA5FOLIO, Anestesia));
                psBuscaUnidadFactura.setString(1, Unidad2);
                psBuscaUnidadFactura.setString(2, Usuario);
                psBuscaUnidadFactura.setString(3, Unidad2);
                psBuscaUnidadFactura.setString(4, Usuario);
                psBuscaUnidadFactura.setString(5, Unidad2);
                psBuscaUnidadFactura.setString(6, Usuario);
                rsBuscaUnidadFactura = psBuscaUnidadFactura.executeQuery();
                if (rsBuscaUnidadFactura.next()) {
                    CantSurT = rsBuscaUnidadFactura.getInt(1);
                    CantSurCR = rsBuscaUnidadFactura.getInt(2);
                    Obs = rsBuscaUnidadFactura.getString(3);
                }

                CantSur = CantSurT - CantSurCR;

                if (CantSurCR > 0) {

                    psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
                    rsIndice = psBuscaIndice.executeQuery();
                    if (rsIndice.next()) {
                        FolioFactura = rsIndice.getInt(1);
                    }

                    psActualizaIndice.setInt(1, FolioFactura + 1);
                    psActualizaIndice.addBatch();

                    psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_UNIDADFACTURA5FOLIO2, Anestesia));
                    psBuscaDatosFact.setString(1, Unidad2);
                    psBuscaDatosFact.setString(2, Usuario);
                    rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                    while (rsBuscaDatosFact.next()) {
                        Clave = rsBuscaDatosFact.getString(1);
                        F_Solicitado = rsBuscaDatosFact.getInt(2);
                        piezas = rsBuscaDatosFact.getInt(3);
                        FolioLote = rsBuscaDatosFact.getInt(4);
                        UbicaLote = rsBuscaDatosFact.getString(5);
                        ProyectoSelect = rsBuscaDatosFact.getInt(6);
                        ContratoSelect = rsBuscaDatosFact.getString(7);
                        Costo = rsBuscaDatosFact.getDouble(8);
                        IVA = rsBuscaDatosFact.getDouble(9);
                        Monto = rsBuscaDatosFact.getDouble(10);
                        OC = rsBuscaDatosFact.getString(11);
                        IdFact = rsBuscaDatosFact.getInt(12);
                        int F_IdLote = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;

                        Costo = 0.0;
                        Monto = 0.0;
                        IVA = 0.0;

                        psBuscaExiFol = con.getConn().prepareStatement(BUSCA_EXILOTE);
                        psBuscaExiFol.setString(1, Clave);
                        psBuscaExiFol.setInt(2, FolioLote);
                        psBuscaExiFol.setString(3, UbicaLote);
                        psBuscaExiFol.setInt(4, ProyectoSelect);

                        System.out.println("BuscaExistenciaDetalle=" + psBuscaExiFol);
                        rsBuscaExiFol = psBuscaExiFol.executeQuery();
                        while (rsBuscaExiFol.next()) {
                            F_IdLote = rsBuscaExiFol.getInt(1);
                            F_ExiLot = rsBuscaExiFol.getInt(2);
                            ClaProve = rsBuscaExiFol.getInt(3);

                            if ((F_ExiLot >= piezas) && (piezas > 0)) {
                                diferencia = F_ExiLot - piezas;
                                CanSur = piezas;

                                psActualizaLote.setInt(1, diferencia);
                                psActualizaLote.setInt(2, F_IdLote);
                                System.out.println("ActualizaLote=" + psActualizaLote + " Clave=" + Clave);
                                psActualizaLote.addBatch();

                                psInsertarMov.setInt(1, FolioFactura);
                                psInsertarMov.setInt(2, 51);
                                psInsertarMov.setString(3, Clave);
                                psInsertarMov.setInt(4, CanSur);
                                psInsertarMov.setDouble(5, Costo);
                                psInsertarMov.setDouble(6, Monto);
                                psInsertarMov.setString(7, "-1");
                                psInsertarMov.setInt(8, FolioLote);
                                psInsertarMov.setString(9, UbicaLote);
                                psInsertarMov.setInt(10, ClaProve);
                                psInsertarMov.setString(11, Usuario);
                                System.out.println("Mov1" + psInsertarMov);
                                psInsertarMov.addBatch();

                                psInsertarFact.setInt(1, FolioFactura);
                                psInsertarFact.setString(2, Unidad2);
                                psInsertarFact.setString(3, Clave);
                                psInsertarFact.setInt(4, F_Solicitado);
                                psInsertarFact.setInt(5, CanSur);
                                psInsertarFact.setDouble(6, Costo);
                                psInsertarFact.setDouble(7, IVA);
                                psInsertarFact.setDouble(8, Monto);
                                psInsertarFact.setInt(9, FolioLote);
                                psInsertarFact.setString(10, FecEnt);
                                psInsertarFact.setString(11, Usuario);
                                psInsertarFact.setString(12, UbicaLote);
                                psInsertarFact.setInt(13, ProyectoSelect);
                                psInsertarFact.setString(14, ContratoSelect);
                                psInsertarFact.setString(15, OC);
                                psInsertarFact.setInt(16, 5);
                                System.out.println("fact1" + psInsertarFact);
                                psInsertarFact.addBatch();

                                piezas = 0;
                                F_Solicitado = 0;
                                break;

                            } else if ((piezas > 0) && (F_ExiLot > 0)) {
                                diferencia = piezas - F_ExiLot;
                                CanSur = F_ExiLot;

                                psActualizaLote.setInt(1, 0);
                                psActualizaLote.setInt(2, F_IdLote);
                                System.out.println("ActualizaLote2=" + psActualizaLote + " Clave=" + Clave);
                                psActualizaLote.addBatch();

                                psInsertarMov.setInt(1, FolioFactura);
                                psInsertarMov.setInt(2, 51);
                                psInsertarMov.setString(3, Clave);
                                psInsertarMov.setInt(4, CanSur);
                                psInsertarMov.setDouble(5, Costo);
                                psInsertarMov.setDouble(6, Monto);
                                psInsertarMov.setString(7, "-1");
                                psInsertarMov.setInt(8, FolioLote);
                                psInsertarMov.setString(9, UbicaLote);
                                psInsertarMov.setInt(10, ClaProve);
                                psInsertarMov.setString(11, Usuario);
                                System.out.println("Mov2" + psInsertarMov);
                                psInsertarMov.addBatch();

                                psInsertarFact.setInt(1, FolioFactura);
                                psInsertarFact.setString(2, Unidad2);
                                psInsertarFact.setString(3, Clave);
                                psInsertarFact.setInt(4, F_Solicitado);
                                psInsertarFact.setInt(5, CanSur);
                                psInsertarFact.setDouble(6, Costo);
                                psInsertarFact.setDouble(7, IVA);
                                psInsertarFact.setDouble(8, Monto);
                                psInsertarFact.setInt(9, FolioLote);
                                psInsertarFact.setString(10, FecEnt);
                                psInsertarFact.setString(11, Usuario);
                                psInsertarFact.setString(12, UbicaLote);
                                psInsertarFact.setInt(13, ProyectoSelect);
                                psInsertarFact.setString(14, ContratoSelect);
                                psInsertarFact.setString(15, OC);
                                psInsertarFact.setInt(16, 5);
                                System.out.println("fact2" + psInsertarFact);
                                psInsertarFact.addBatch();

                                F_Solicitado = F_Solicitado - CanSur;

                                piezas = piezas - CanSur;
                                F_ExiLot = 0;

                            } else if ((piezas == 0) && (F_ExiLot == 0)) {

                                psInsertarFact.setInt(1, FolioFactura);
                                psInsertarFact.setString(2, Unidad2);
                                psInsertarFact.setString(3, Clave);
                                psInsertarFact.setInt(4, F_Solicitado);
                                psInsertarFact.setInt(5, 0);
                                psInsertarFact.setDouble(6, 0.00);
                                psInsertarFact.setDouble(7, 0.00);
                                psInsertarFact.setDouble(8, 0.00);
                                psInsertarFact.setInt(9, FolioLote);
                                psInsertarFact.setString(10, FecEnt);
                                psInsertarFact.setString(11, Usuario);
                                psInsertarFact.setString(12, UbicaLote);
                                psInsertarFact.setInt(13, ProyectoSelect);
                                psInsertarFact.setString(14, ContratoSelect);
                                psInsertarFact.setString(15, OC);
                                psInsertarFact.setInt(16, 5);
                                System.out.println("fact2" + psInsertarFact);
                                psInsertarFact.addBatch();

                                F_Solicitado = F_Solicitado - CanSur;

                                piezas = piezas - CanSur;
                                F_ExiLot = 0;

                            }

                        }
                        psActualiza = con.getConn().prepareStatement(ActualizaIdFactura);
                        psActualiza.setInt(1, 1);
                        psActualiza.setInt(2, IdFact);
                        psActualiza.execute();
                        psActualiza.clearParameters();

                    }

                    if (CantSur == 0) {
                        psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSporFACTEMP5FOLIO);
                        psBuscaDatosFact.setString(1, Unidad2);
                        psBuscaDatosFact.setInt(2, 0);
                        psBuscaDatosFact.setString(3, Usuario);
                        rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                        while (rsBuscaDatosFact.next()) {
                            Clave = rsBuscaDatosFact.getString(1);
                            F_Solicitado = rsBuscaDatosFact.getInt(2);
                            piezas = rsBuscaDatosFact.getInt(3);
                            FolioLote = rsBuscaDatosFact.getInt(4);
                            UbicaLote = rsBuscaDatosFact.getString(5);
                            ProyectoSelect = rsBuscaDatosFact.getInt(6);
                            ContratoSelect = rsBuscaDatosFact.getString(7);
                            Costo = rsBuscaDatosFact.getDouble(8);
                            IVA = rsBuscaDatosFact.getDouble(9);
                            Monto = rsBuscaDatosFact.getDouble(10);
                            OC = rsBuscaDatosFact.getString(11);

                            psInsertarFact.setInt(1, FolioFactura);
                            psInsertarFact.setString(2, Unidad2);
                            psInsertarFact.setString(3, Clave);
                            psInsertarFact.setInt(4, F_Solicitado);
                            psInsertarFact.setInt(5, 0);
                            psInsertarFact.setDouble(6, 0.00);
                            psInsertarFact.setDouble(7, 0.00);
                            psInsertarFact.setDouble(8, 0.00);
                            psInsertarFact.setInt(9, FolioLote);
                            psInsertarFact.setString(10, FecEnt);
                            psInsertarFact.setString(11, Usuario);
                            psInsertarFact.setString(12, UbicaLote);
                            psInsertarFact.setInt(13, ProyectoSelect);
                            psInsertarFact.setString(14, ContratoSelect);
                            psInsertarFact.setString(15, OC);
                            psInsertarFact.setInt(16, 0);
                            System.out.println("fact1" + psInsertarFact);
                            psInsertarFact.addBatch();

                        }
                    }

                    psInsertarObs.setInt(1, FolioFactura);
                    psInsertarObs.setString(2, Obs + " - ( Claves Administración )");
                    psInsertarObs.setString(3, Tipos);
                    psInsertarObs.setInt(4, Proyecto);
                    psInsertarObs.addBatch();

                    psActualizaIndice.executeBatch();
                    psActualizaLote.executeBatch();
                    psInsertarMov.executeBatch();
                    psInsertarFact.executeBatch();
                    psInsertarObs.executeBatch();
                    save = true;
                    con.getConn().commit();
                    System.out.println("Terminó Unidad= " + Unidad + " Con el Folio= " + FolioFactura);
                }

                if (CantSur > 0) {

//                    psActualiza = con.getConn().prepareStatement(ActualizaCause);
//                    psActualiza.setInt(1, Proyecto);
//                    psActualiza.setString(2, Unidad2);
//                    psActualiza.setString(3, Usuario);
//                    psActualiza.execute();
//                    psActualiza.clearParameters();
                    psActualiza = con.getConn().prepareStatement(ActualizaFolioB);
                    psActualiza.setString(1, Unidad2);
                    psActualiza.setString(2, Usuario);
                    psActualiza.setString(3, Unidad2);
                    psActualiza.setString(4, Usuario);
                    psActualiza.execute();
                    psActualiza.clearParameters();
//
//                    psCause = con.getConn().prepareStatement(BuscaCauseTemp);
//                    psCause.setString(1, Unidad2);
//                    psCause.setString(2, Usuario);
//                    rsCause = psCause.executeQuery();
//                    while (rsCause.next()) {
//
//                        psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
//                        rsIndice = psBuscaIndice.executeQuery();
//                        if (rsIndice.next()) {
//                            FolioFactura = rsIndice.getInt(1);
//                        }
//
//                        psActualizaIndice.setInt(1, FolioFactura + 1);
//                        psActualizaIndice.addBatch();
//
//                        psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSporFACTEMP5FOLIOCAUSE);
//                        psBuscaDatosFact.setString(1, Unidad2);
//                        psBuscaDatosFact.setInt(2, 0);
//                        psBuscaDatosFact.setString(3, Usuario);
//                        psBuscaDatosFact.setString(4, rsCause.getString(1));
//                        rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
//                        while (rsBuscaDatosFact.next()) {
//                            Clave = rsBuscaDatosFact.getString(1);
//                            F_Solicitado = rsBuscaDatosFact.getInt(2);
//                            piezas = rsBuscaDatosFact.getInt(3);
//                            FolioLote = rsBuscaDatosFact.getInt(4);
//                            UbicaLote = rsBuscaDatosFact.getString(5);
//                            ProyectoSelect = rsBuscaDatosFact.getInt(6);
//                            ContratoSelect = rsBuscaDatosFact.getString(7);
//                            Costo = rsBuscaDatosFact.getDouble(8);
//                            IVA = rsBuscaDatosFact.getDouble(9);
//                            Monto = rsBuscaDatosFact.getDouble(10);
//                            OC = rsBuscaDatosFact.getString(11);
//
//                            int F_IdLote = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;
//                            Costo = 0.0;
//                            Monto = 0.0;
//                            IVA = 0.0;
//
//                            psBuscaExiFol = con.getConn().prepareStatement(BUSCA_EXILOTE);
//                            psBuscaExiFol.setString(1, Clave);
//                            psBuscaExiFol.setInt(2, FolioLote);
//                            psBuscaExiFol.setString(3, UbicaLote);
//                            psBuscaExiFol.setInt(4, ProyectoSelect);
//
//                            System.out.println("BuscaExistenciaDetalle=" + psBuscaExiFol);
//                            rsBuscaExiFol = psBuscaExiFol.executeQuery();
//                            while (rsBuscaExiFol.next()) {
//                                F_IdLote = rsBuscaExiFol.getInt(1);
//                                F_ExiLot = rsBuscaExiFol.getInt(2);
//                                ClaProve = rsBuscaExiFol.getInt(3);
//
//                                if ((F_ExiLot >= piezas) && (piezas > 0)) {
//                                    diferencia = F_ExiLot - piezas;
//                                    CanSur = piezas;
//
//                                    psActualizaLote.setInt(1, diferencia);
//                                    psActualizaLote.setInt(2, F_IdLote);
//                                    System.out.println("ActualizaLote=" + psActualizaLote + " Clave=" + Clave);
//                                    psActualizaLote.addBatch();
//
//                                    psInsertarMov.setInt(1, FolioFactura);
//                                    psInsertarMov.setInt(2, 51);
//                                    psInsertarMov.setString(3, Clave);
//                                    psInsertarMov.setInt(4, CanSur);
//                                    psInsertarMov.setDouble(5, Costo);
//                                    psInsertarMov.setDouble(6, Monto);
//                                    psInsertarMov.setString(7, "-1");
//                                    psInsertarMov.setInt(8, FolioLote);
//                                    psInsertarMov.setString(9, UbicaLote);
//                                    psInsertarMov.setInt(10, ClaProve);
//                                    psInsertarMov.setString(11, Usuario);
//                                    System.out.println("Mov1" + psInsertarMov);
//                                    psInsertarMov.addBatch();
//
//                                    psInsertarFact.setInt(1, FolioFactura);
//                                    psInsertarFact.setString(2, Unidad2);
//                                    psInsertarFact.setString(3, Clave);
//                                    psInsertarFact.setInt(4, F_Solicitado);
//                                    psInsertarFact.setInt(5, CanSur);
//                                    psInsertarFact.setDouble(6, Costo);
//                                    psInsertarFact.setDouble(7, IVA);
//                                    psInsertarFact.setDouble(8, Monto);
//                                    psInsertarFact.setInt(9, FolioLote);
//                                    psInsertarFact.setString(10, FecEnt);
//                                    psInsertarFact.setString(11, Usuario);
//                                    psInsertarFact.setString(12, UbicaLote);
//                                    psInsertarFact.setInt(13, ProyectoSelect);
//                                    psInsertarFact.setString(14, ContratoSelect);
//                                    psInsertarFact.setString(15, OC);
//                                    psInsertarFact.setInt(16, rsCause.getInt(1));
//                                    System.out.println("fact1" + psInsertarFact);
//                                    psInsertarFact.addBatch();
//
//                                    piezas = 0;
//                                    F_Solicitado = 0;
//                                    break;
//
//                                } else if ((piezas > 0) && (F_ExiLot > 0)) {
//                                    diferencia = piezas - F_ExiLot;
//                                    CanSur = F_ExiLot;
//
//                                    psActualizaLote.setInt(1, 0);
//                                    psActualizaLote.setInt(2, F_IdLote);
//                                    System.out.println("ActualizaLote2=" + psActualizaLote + " Clave=" + Clave);
//                                    psActualizaLote.addBatch();
//
//                                    psInsertarMov.setInt(1, FolioFactura);
//                                    psInsertarMov.setInt(2, 51);
//                                    psInsertarMov.setString(3, Clave);
//                                    psInsertarMov.setInt(4, CanSur);
//                                    psInsertarMov.setDouble(5, Costo);
//                                    psInsertarMov.setDouble(6, Monto);
//                                    psInsertarMov.setString(7, "-1");
//                                    psInsertarMov.setInt(8, FolioLote);
//                                    psInsertarMov.setString(9, UbicaLote);
//                                    psInsertarMov.setInt(10, ClaProve);
//                                    psInsertarMov.setString(11, Usuario);
//                                    System.out.println("Mov2" + psInsertarMov);
//                                    psInsertarMov.addBatch();
//
//                                    psInsertarFact.setInt(1, FolioFactura);
//                                    psInsertarFact.setString(2, Unidad2);
//                                    psInsertarFact.setString(3, Clave);
//                                    psInsertarFact.setInt(4, F_Solicitado);
//                                    psInsertarFact.setInt(5, CanSur);
//                                    psInsertarFact.setDouble(6, Costo);
//                                    psInsertarFact.setDouble(7, IVA);
//                                    psInsertarFact.setDouble(8, Monto);
//                                    psInsertarFact.setInt(9, FolioLote);
//                                    psInsertarFact.setString(10, FecEnt);
//                                    psInsertarFact.setString(11, Usuario);
//                                    psInsertarFact.setString(12, UbicaLote);
//                                    psInsertarFact.setInt(13, ProyectoSelect);
//                                    psInsertarFact.setString(14, ContratoSelect);
//                                    psInsertarFact.setString(15, OC);
//                                    psInsertarFact.setInt(16, rsCause.getInt(1));
//                                    System.out.println("fact2" + psInsertarFact);
//                                    psInsertarFact.addBatch();
//
//                                    F_Solicitado = F_Solicitado - CanSur;
//
//                                    piezas = piezas - CanSur;
//                                    F_ExiLot = 0;
//
//                                } else if ((piezas == 0) && (F_ExiLot == 0)) {
//
//                                    psInsertarFact.setInt(1, FolioFactura);
//                                    psInsertarFact.setString(2, Unidad2);
//                                    psInsertarFact.setString(3, Clave);
//                                    psInsertarFact.setInt(4, F_Solicitado);
//                                    psInsertarFact.setInt(5, 0);
//                                    psInsertarFact.setDouble(6, 0.00);
//                                    psInsertarFact.setDouble(7, 0.00);
//                                    psInsertarFact.setDouble(8, 0.00);
//                                    psInsertarFact.setInt(9, FolioLote);
//                                    psInsertarFact.setString(10, FecEnt);
//                                    psInsertarFact.setString(11, Usuario);
//                                    psInsertarFact.setString(12, UbicaLote);
//                                    psInsertarFact.setInt(13, ProyectoSelect);
//                                    psInsertarFact.setString(14, ContratoSelect);
//                                    psInsertarFact.setString(15, OC);
//                                    psInsertarFact.setInt(16, rsCause.getInt(1));
//                                    System.out.println("fact2" + psInsertarFact);
//                                    psInsertarFact.addBatch();
//                                }
//
//                            }
//
//                        }
//                        psInsertarObs.setInt(1, FolioFactura);
//                        psInsertarObs.setString(2, Obs + " - " + rsCause.getString(2));
//                        psInsertarObs.setString(3, Tipos);
//                        psInsertarObs.setInt(4, Proyecto);
//                        psInsertarObs.addBatch();
//
//                        psActualizaIndice.executeBatch();
//                        psActualizaLote.executeBatch();
//                        psInsertarMov.executeBatch();
//                        psInsertarFact.executeBatch();
//                        psInsertarObs.executeBatch();
//                        System.out.println("Terminó Unidad= " + Unidad + " Con el Folio= " + FolioFactura);
//
//                    }

                    if (CantSurCR == 0) {
                        psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_UNIDADFACTURA5FOLIO2, Anestesia));
                        psBuscaDatosFact.setString(1, Unidad2);
                        psBuscaDatosFact.setString(2, Usuario);
                        rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                        while (rsBuscaDatosFact.next()) {
                            Clave = rsBuscaDatosFact.getString(1);
                            F_Solicitado = rsBuscaDatosFact.getInt(2);
                            piezas = rsBuscaDatosFact.getInt(3);
                            FolioLote = rsBuscaDatosFact.getInt(4);
                            UbicaLote = rsBuscaDatosFact.getString(5);
                            ProyectoSelect = rsBuscaDatosFact.getInt(6);
                            ContratoSelect = rsBuscaDatosFact.getString(7);
                            Costo = rsBuscaDatosFact.getDouble(8);
                            IVA = rsBuscaDatosFact.getDouble(9);
                            Monto = rsBuscaDatosFact.getDouble(10);
                            OC = rsBuscaDatosFact.getString(11);

                            Costo = 0.0;
                            Monto = 0.0;
                            IVA = 0.0;

                            psInsertarFact.setInt(1, FolioFactura);
                            psInsertarFact.setString(2, Unidad2);
                            psInsertarFact.setString(3, Clave);
                            psInsertarFact.setInt(4, F_Solicitado);
                            psInsertarFact.setInt(5, 0);
                            psInsertarFact.setDouble(6, 0.00);
                            psInsertarFact.setDouble(7, 0.00);
                            psInsertarFact.setDouble(8, 0.00);
                            psInsertarFact.setInt(9, FolioLote);
                            psInsertarFact.setString(10, FecEnt);
                            psInsertarFact.setString(11, Usuario);
                            psInsertarFact.setString(12, UbicaLote);
                            psInsertarFact.setInt(13, ProyectoSelect);
                            psInsertarFact.setString(14, ContratoSelect);
                            psInsertarFact.setString(15, OC);
                            psInsertarFact.setInt(16, 0);
                            System.out.println("fact1" + psInsertarFact);
                            psInsertarFact.addBatch();

                        }
                    }
                    psInsertarFact.executeBatch();
                    save = true;
                    con.getConn().commit();
                    System.out.println("Terminó Unidad= " + Unidad + " Con el Folio= " + FolioFactura);
                }

            }

            psBuscaExiFol.close();
            psBuscaExiFol = null;
            rsBuscaExiFol.close();
            rsBuscaExiFol = null;
            psBuscaDatosFact.close();
            psBuscaDatosFact = null;
            rsBuscaDatosFact.close();
            rsBuscaDatosFact = null;
            rsCause.close();
            rsCause = null;
//            psCause.close();
//            psCause = null;
            psActualiza.close();
            psActualiza = null;
            return save;
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                save = false;
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;
    }
*/
    @Override
    public JSONArray getRegistroFactAutoLote(String ClaUni, String Catalogo) {
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();
        JSONObject jsonObj;
        JSONObject jsonObj2;
        System.out.println("Unidades:" + ClaUni);
        String query = "SELECT U.F_ClaPro, F_ClaUni,CONCAT( F_ClaUni, '_', REPLACE (U.F_ClaPro, '.', ''), '_', F_IdReq ) AS DATOS,F_IdReq FROM tb_unireqlote U INNER JOIN tb_medica M ON U.F_ClaPro=M.F_ClaPro WHERE F_ClaUni in (%s) AND F_Status=0 AND  F_Solicitado != 0 AND M.F_StsPro='A' AND F_N%s='1';";
        PreparedStatement ps;
        ResultSet rs;
        try {
            int Contar = 0;
            con.conectar();
            ps = con.getConn().prepareStatement(String.format(query, ClaUni, Catalogo));
            System.out.println("UNidades=" + ps);
            rs = ps.executeQuery();

            while (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("ClaPro", rs.getString(1));
                jsonObj.put("ClaUni", rs.getString(2));
                jsonObj.put("Datos", rs.getString(3));
                jsonObj.put("IdReg", rs.getString(4));
                jsonArray.add(jsonObj);

            }
            ps.close();
            ps = null;
            rs.close();
            rs = null;
            jsonObj2 = new JSONObject();
            jsonArray.add(jsonObj2);
            con.cierraConexion();

        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public boolean ActualizaREQLote(String ClaUni, String ClaPro, int Cantidad, int Catalogo, int Idreg, String Obs) {
        System.out.println("ClaUni=" + ClaUni + " ClaPro=" + ClaPro + " Cantidad=" + Cantidad + " Catalogo=" + Catalogo);
        boolean save = false;
        int ExiLot = 0;

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psBuscaLote = con.getConn().prepareStatement(ACTUALIZA_REQIdLote);
            psBuscaLote.setInt(1, Cantidad);
            psBuscaLote.setString(2, Obs);
            psBuscaLote.setString(3, ClaPro);
            psBuscaLote.setString(4, ClaUni);
            psBuscaLote.setInt(5, Idreg);
            System.out.println("ActualizaReq=" + psBuscaLote);
            psBuscaLote.execute();
            psBuscaLote.clearParameters();
            psBuscaLote.close();
            psBuscaLote = null;
            save = true;
            con.getConn().commit();
            return save;

        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;

    }

    @Override
    public boolean RegistrarFoliosLote(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto) {
        boolean save = false;
        int UbicaModu = 0, piezas = 0, F_Solicitado = 0, ContarV = 0, Existencia = 0, FolioFactura = 0, DifeSol = 0, FolioLote = 0;
        String UbicaDesc = "", Clave = "", Unidad = "", Unidad2 = "", UbicaLote = "", Contrato = "", Referencia = "", Lote = "", Caducidad = "";

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psBuscaContrato = con.getConn().prepareStatement(BuscaContrato);
            psBuscaContrato.setInt(1, Proyecto);
            rsContrato = psBuscaContrato.executeQuery();
            if (rsContrato.next()) {
                Contrato = rsContrato.getString(1);
            }

            if (Catalogo > 0) {
                psConsulta = con.getConn().prepareStatement(BUSCA_PARAMETRO);
                psConsulta.setString(1, Usuario);
                rsConsulta = psConsulta.executeQuery();
                rsConsulta.next();
                UbicaModu = rsConsulta.getInt(1);
                Proyecto = rsConsulta.getInt(2);
                psConsulta.close();
                psConsulta = null;
                switch (UbicaModu) {
                    case 1:
                        UbicaDesc = " WHERE F_Ubica IN ('MODULA','A0S','APE','DENTAL','REDFRIA')";
                        break;
                    case 2:
                        UbicaDesc = " WHERE F_Ubica IN ('MODULA2','A0S','APE','DENTAL','REDFRIA')";
                        break;
                    case 3:
                        UbicaDesc = " WHERE F_Ubica IN ('AF','APE','DENTAL','REDFRIA')";
                        break;
                    case 4:
                        UbicaDesc = " WHERE F_Ubica NOT IN ('MODULA','MODULA2','CADUCADOS','PROX_CADUCA','MERMA','CUARENTENA')";
                        break;
                    case 5:
                        UbicaDesc = " WHERE F_Ubica IN ('AF2','APE','DENTAL','REDFRIA','CONTROLADO')";
                        break;

                    default:
                        UbicaDesc = " WHERE F_Ubica IN ('AF3','APE','DENTAL','REDFRIA','CONTROLADO')";
                        break;
                }
            }

            psActualizaIndice = con.getConn().prepareStatement(String.format(ACTUALIZA_INDICEFACT, Proyecto));
            psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
            psInsertarMov = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
            psInsertarFact = con.getConn().prepareStatement(INSERTA_FACTURA);
            psActualizaIndiceLote = con.getConn().prepareStatement(ACTUALIZA_INDICELOTE);
            psINSERTLOTE = con.getConn().prepareStatement(INSERTAR_NUEVOLOTE);
            psActualizaReq = con.getConn().prepareStatement(ACTUALIZA_STSREQLOTE);
            psInsertarObs = con.getConn().prepareStatement(INSERTAR_OBSERVACIONES);

            psBuscaUnidad = con.getConn().prepareStatement(String.format(BUSCA_UNIDADESLOTE, ClaUnidad, Catalogo));
            rsBuscaUnidad = psBuscaUnidad.executeQuery();
            while (rsBuscaUnidad.next()) {
                Unidad = rsBuscaUnidad.getString(1);
                Unidad2 = rsBuscaUnidad.getString(1);
                Unidad = "'" + Unidad + "'";
                psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
                rsIndice = psBuscaIndice.executeQuery();
                if (rsIndice.next()) {
                    FolioFactura = rsIndice.getInt(1);
                }

                //psActualizaIndice=con.getConn().prepareStatement(ACTUALIZA_INDICEFACT);
                psActualizaIndice.setInt(1, FolioFactura + 1);
                psActualizaIndice.addBatch();

                /*psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSFACTURA);
                psBuscaDatosFact.setString(1, Unidad);
                psBuscaDatosFact.setInt(2, Catalogo);*/
                if (UbicaModu == 14 || UbicaModu == 40 || UbicaModu == 41) {

                    psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTURAR2, UbicaDesc, UbicaDesc, Unidad, Catalogo));
                } else {
                    psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTURAR, UbicaDesc, UbicaDesc, Unidad, Catalogo));
                }
                System.out.println("Datos Facturas" + psBuscaDatosFact);
                rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                while (rsBuscaDatosFact.next()) {
                    Clave = rsBuscaDatosFact.getString(2);
                    piezas = rsBuscaDatosFact.getInt(3);
                    F_Solicitado = rsBuscaDatosFact.getInt(5);
                    Observaciones = rsBuscaDatosFact.getString(6);
                    Referencia = rsBuscaDatosFact.getString(7);
                    Lote = rsBuscaDatosFact.getString(8);
                    Caducidad = rsBuscaDatosFact.getString(9);

                    psBuscaExiLote = con.getConn().prepareStatement(String.format(BUSCA_ExistenciaLote, UbicaDesc));
                    psBuscaExiLote.setString(1, Clave);
                    psBuscaExiLote.setString(2, Lote);
                    psBuscaExiLote.setString(3, Caducidad);
                    rsBuscaExistencia = psBuscaExiLote.executeQuery();
                    while (rsBuscaExistencia.next()) {
                        Existencia = rsBuscaExistencia.getInt(2);
                        FolioLote = rsBuscaDatosFact.getInt(3);
                        UbicaLote = rsBuscaDatosFact.getString(4);
                    }

                    if ((piezas > 0) && (Existencia > 0)) {

                        int F_IdLote = 0, F_FolLot = 0, Tipo = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;
                        int Facturado = 0, Contar = 0;
                        String Ubicacion = "";
                        double Costo = 0.0, IVA = 0.0, IVAPro = 0.0, Monto = 0.0, MontoIva = 0.0;

                        if (Proyecto == 2) {
                            psContarReg = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBILoteP2, UbicaDesc, UbicaDesc, Catalogo));
                        } else if (Proyecto == 7) {
                            psContarReg = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBILoteP7, UbicaDesc, UbicaDesc, Catalogo));
                        } else {
                            psContarReg = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBILote, UbicaDesc, Catalogo));
                        }
                        //psContarReg = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBILote, UbicaDesc, UbicaDesc, Catalogo));
                        psContarReg.setInt(1, Proyecto);
                        psContarReg.setString(2, Clave);
                        psContarReg.setString(3, Lote);
                        psContarReg.setString(4, Caducidad);
                        rsContarReg = psContarReg.executeQuery();
                        while (rsContarReg.next()) {
                            Contar++;
                        }

                        if (Proyecto == 2) {
                            psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBILoteP2, UbicaDesc, UbicaDesc, Catalogo));
                        } else if (Proyecto == 7) {
                            psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBILoteP7, UbicaDesc, UbicaDesc, Catalogo));
                        } else {
                            psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBILote, UbicaDesc, Catalogo));
                        }
                        //psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBILote, UbicaDesc, UbicaDesc, Catalogo));
                        psBuscaExiFol.setInt(1, Proyecto);
                        psBuscaExiFol.setString(2, Clave);
                        psBuscaExiFol.setString(3, Lote);
                        psBuscaExiFol.setString(4, Caducidad);

                        System.out.println("BuscaExistenciaDetalle=" + psBuscaExiFol);
                        rsBuscaExiFol = psBuscaExiFol.executeQuery();
                        while (rsBuscaExiFol.next()) {
                            F_IdLote = rsBuscaExiFol.getInt(1);
                            F_ExiLot = rsBuscaExiFol.getInt(7);
                            F_FolLot = rsBuscaExiFol.getInt(3);
                            Tipo = rsBuscaExiFol.getInt(4);
                            Costo = rsBuscaExiFol.getDouble(5);
                            Ubicacion = rsBuscaExiFol.getString(6);
                            ClaProve = rsBuscaExiFol.getInt(8);
                            if (Tipo == 2504) {
                                IVA = 0.0;
                            } else {
                                IVA = 0.16;
                            }

                            Costo = 0.0;

                            if ((F_ExiLot >= piezas) && (piezas > 0)) {
                                Contar = Contar - 1;
                                diferencia = F_ExiLot - piezas;
                                CanSur = piezas;

                                psActualizaLote.setInt(1, diferencia);
                                psActualizaLote.setInt(2, F_IdLote);
                                System.out.println("ActualizaLote=" + psActualizaLote + " Clave=" + Clave);
                                psActualizaLote.addBatch();

                                IVAPro = (CanSur * Costo) * IVA;
                                Monto = CanSur * Costo;
                                MontoIva = Monto + IVAPro;

                                psInsertarMov.setInt(1, FolioFactura);
                                psInsertarMov.setInt(2, 51);
                                psInsertarMov.setString(3, Clave);
                                psInsertarMov.setInt(4, CanSur);
                                psInsertarMov.setDouble(5, Costo);
                                psInsertarMov.setDouble(6, MontoIva);
                                psInsertarMov.setString(7, "-1");
                                psInsertarMov.setInt(8, F_FolLot);
                                psInsertarMov.setString(9, Ubicacion);
                                psInsertarMov.setInt(10, ClaProve);
                                psInsertarMov.setString(11, Usuario);
                                System.out.println("Mov1" + psInsertarMov);
                                psInsertarMov.addBatch();

                                psInsertarFact.setInt(1, FolioFactura);
                                psInsertarFact.setString(2, Unidad2);
                                psInsertarFact.setString(3, Clave);
                                psInsertarFact.setInt(4, F_Solicitado);
                                psInsertarFact.setInt(5, CanSur);
                                psInsertarFact.setDouble(6, Costo);
                                psInsertarFact.setDouble(7, IVAPro);
                                psInsertarFact.setDouble(8, MontoIva);
                                psInsertarFact.setInt(9, F_FolLot);
                                psInsertarFact.setString(10, FecEnt);
                                psInsertarFact.setString(11, Usuario);
                                psInsertarFact.setString(12, Ubicacion);
                                psInsertarFact.setInt(13, Proyecto);
                                psInsertarFact.setString(14, Contrato);
                                psInsertarFact.setString(15, Referencia);
                                psInsertarFact.setInt(16, 0);
                                System.out.println("fact1" + psInsertarFact);
                                psInsertarFact.addBatch();

                                piezas = 0;
                                F_Solicitado = 0;
                                break;

                            } else if ((piezas > 0) && (F_ExiLot > 0)) {
                                Contar = Contar - 1;
                                diferencia = piezas - F_ExiLot;
                                CanSur = F_ExiLot;
                                if (F_ExiLot >= F_Solicitado) {
                                    DifeSol = F_Solicitado;
                                } else if (Contar > 0) {
                                    DifeSol = F_ExiLot;
                                } else {
                                    DifeSol = F_Solicitado - F_ExiLot;
                                }

                                psActualizaLote.setInt(1, 0);
                                psActualizaLote.setInt(2, F_IdLote);
                                System.out.println("ActualizaLote2=" + psActualizaLote + " Clave=" + Clave);
                                psActualizaLote.addBatch();

                                IVAPro = (CanSur * Costo) * IVA;
                                Monto = CanSur * Costo;
                                MontoIva = Monto + IVAPro;

                                psInsertarMov.setInt(1, FolioFactura);
                                psInsertarMov.setInt(2, 51);
                                psInsertarMov.setString(3, Clave);
                                psInsertarMov.setInt(4, CanSur);
                                psInsertarMov.setDouble(5, Costo);
                                psInsertarMov.setDouble(6, MontoIva);
                                psInsertarMov.setString(7, "-1");
                                psInsertarMov.setInt(8, F_FolLot);
                                psInsertarMov.setString(9, Ubicacion);
                                psInsertarMov.setInt(10, ClaProve);
                                psInsertarMov.setString(11, Usuario);
                                System.out.println("Mov2" + psInsertarMov);
                                psInsertarMov.addBatch();

                                psInsertarFact.setInt(1, FolioFactura);
                                psInsertarFact.setString(2, Unidad2);
                                psInsertarFact.setString(3, Clave);
                                psInsertarFact.setInt(4, DifeSol);
                                psInsertarFact.setInt(5, CanSur);
                                psInsertarFact.setDouble(6, Costo);
                                psInsertarFact.setDouble(7, IVAPro);
                                psInsertarFact.setDouble(8, MontoIva);
                                psInsertarFact.setInt(9, F_FolLot);
                                psInsertarFact.setString(10, FecEnt);
                                psInsertarFact.setString(11, Usuario);
                                psInsertarFact.setString(12, Ubicacion);
                                psInsertarFact.setInt(13, Proyecto);
                                psInsertarFact.setString(14, Contrato);
                                psInsertarFact.setString(15, Referencia);
                                psInsertarFact.setInt(16, 0);
                                System.out.println("fact2" + psInsertarFact);
                                psInsertarFact.addBatch();

                                F_Solicitado = F_Solicitado - CanSur;

                                piezas = piezas - CanSur;
                                F_ExiLot = 0;

                            }
                            if (Contar == 0) {
                                if (F_Solicitado > 0) {
                                    psInsertarFact.setInt(1, FolioFactura);
                                    psInsertarFact.setString(2, Unidad2);
                                    psInsertarFact.setString(3, Clave);
                                    psInsertarFact.setInt(4, F_Solicitado);
                                    psInsertarFact.setInt(5, 0);
                                    psInsertarFact.setDouble(6, Costo);
                                    psInsertarFact.setDouble(7, IVAPro);
                                    psInsertarFact.setDouble(8, MontoIva);
                                    psInsertarFact.setInt(9, F_FolLot);
                                    psInsertarFact.setString(10, FecEnt);
                                    psInsertarFact.setString(11, Usuario);
                                    psInsertarFact.setString(12, Ubicacion);
                                    psInsertarFact.setInt(13, Proyecto);
                                    psInsertarFact.setString(14, Contrato);
                                    psInsertarFact.setString(15, Referencia);
                                    psInsertarFact.setInt(16, 0);
                                    System.out.println("fact3" + psInsertarFact);
                                    psInsertarFact.addBatch();
                                    F_Solicitado = 0;
                                }
                            }

                        }

                    } else if ((FolioLote > 0) && (UbicaLote != "")) {
                        psInsertarFact.setInt(1, FolioFactura);
                        psInsertarFact.setString(2, Unidad2);
                        psInsertarFact.setString(3, Clave);
                        psInsertarFact.setInt(4, F_Solicitado);
                        psInsertarFact.setInt(5, 0);
                        psInsertarFact.setDouble(6, 0);
                        psInsertarFact.setDouble(7, 0);
                        psInsertarFact.setDouble(8, 0);
                        psInsertarFact.setInt(9, FolioLote);
                        psInsertarFact.setString(10, FecEnt);
                        psInsertarFact.setString(11, Usuario);
                        psInsertarFact.setString(12, UbicaLote);
                        psInsertarFact.setInt(13, Proyecto);
                        psInsertarFact.setString(14, Contrato);
                        psInsertarFact.setString(15, Referencia);
                        psInsertarFact.setInt(16, 0);
                        System.out.println("fact4" + psInsertarFact);
                        psInsertarFact.addBatch();
                    } else {
                        int FolioL = 0, IndiceLote = 0;
                        String Ubicacion = "";
                        double Costo = 0.0;

                        psBuscaIndiceLote = con.getConn().prepareStatement(BUSCA_INDICELOTE);
                        rsIndiceLote = psBuscaIndiceLote.executeQuery();
                        rsIndiceLote.next();
                        FolioL = rsIndiceLote.getInt(1);

                        IndiceLote = FolioL + 1;

                        psActualizaIndiceLote.setInt(1, IndiceLote);
                        psActualizaIndiceLote.addBatch();

                        psINSERTLOTE.setString(1, Clave);
                        psINSERTLOTE.setInt(2, FolioL);
                        psINSERTLOTE.setInt(3, Proyecto);
                        System.out.println("InsertarLote" + psINSERTLOTE);
                        psINSERTLOTE.addBatch();

                        psInsertarFact.setInt(1, FolioFactura);
                        psInsertarFact.setString(2, Unidad2);
                        psInsertarFact.setString(3, Clave);
                        psInsertarFact.setInt(4, F_Solicitado);
                        psInsertarFact.setInt(5, 0);
                        psInsertarFact.setDouble(6, Costo);
                        psInsertarFact.setDouble(7, 0);
                        psInsertarFact.setDouble(8, 0);
                        psInsertarFact.setInt(9, FolioL);
                        psInsertarFact.setString(10, FecEnt);
                        psInsertarFact.setString(11, Usuario);
                        psInsertarFact.setString(12, "NUEVA");
                        psInsertarFact.setInt(13, Proyecto);
                        psInsertarFact.setString(14, Contrato);
                        psInsertarFact.setString(15, Referencia);
                        psInsertarFact.setInt(16, 0);
                        System.out.println(" psInsertarFact 2" + psInsertarFact);
                        psInsertarFact.addBatch();
                    }

                }

                psActualizaReq.setString(1, Unidad2);
                psActualizaReq.addBatch();;

                psInsertarObs.setInt(1, FolioFactura);
                psInsertarObs.setString(2, Observaciones);
                psInsertarObs.setString(3, Tipos);
                psInsertarObs.setInt(4, Proyecto);
                psInsertarObs.addBatch();

                psActualizaIndice.executeBatch();
                psActualizaLote.executeBatch();
                psINSERTLOTE.executeBatch();
                psInsertarMov.executeBatch();
                psInsertarFact.executeBatch();
                psActualizaIndiceLote.executeBatch();
                psActualizaReq.executeBatch();
                psInsertarObs.executeBatch();
                save = true;
                con.getConn().commit();
                System.out.println("Terminó Unidad= " + Unidad + " Con el Folio= " + FolioFactura);

            }
            psBuscaExiFol.close();
            psBuscaExiFol = null;
            rsBuscaExiFol.close();
            rsBuscaExiFol = null;
            psBuscaDatosFact.close();
            psBuscaDatosFact = null;
            rsBuscaDatosFact.close();
            rsBuscaDatosFact = null;
            return save;
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;
    }

    @Override
    public boolean ConfirmarFactTempL(String Usuario, String Observaciones, String Tipo, int Proyecto, String OC, String ClaCliSelect) {
        boolean save = false;
        int ExiLot = 0, FolFact = 0, FolioFactura = 0, FolioMovi = 0, FolMov = 0, TipoMed = 0;
        int existencia = 0, cantidad = 0;
        double Costo = 0.0, IVA = 0.0, Monto = 0.0, IVAPro = 0.0, MontoIva = 0.0;
        String IdFact = "", ClaCli = "", FechaE = "", Contrato = "";
        List<FacturacionModel> Factura = new ArrayList<>();

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psBuscaContrato = con.getConn().prepareStatement(BuscaContrato);
            psBuscaContrato.setInt(1, Proyecto);
            rsContrato = psBuscaContrato.executeQuery();
            if (rsContrato.next()) {
                Contrato = rsContrato.getString(1);
            }

            psBuscaTemp = con.getConn().prepareStatement(BUSCA_FACTEMPLOTE);
            psBuscaTemp.setString(1, Usuario);
            psBuscaTemp.setString(2, ClaCliSelect);
            rsTemp = psBuscaTemp.executeQuery();

            while (rsTemp.next()) {

                IdFact = rsTemp.getString(2);
                ClaCli = rsTemp.getString(3);
                FechaE = rsTemp.getString(4);

                psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSFACTLOTE);
                psBuscaDatosFact.setString(1, IdFact);
                psBuscaDatosFact.setString(2, ClaCli);
                rs = psBuscaDatosFact.executeQuery();
                while (rs.next()) {
                    FacturacionModel facturacion = new FacturacionModel();
                    facturacion.setClaCli(rs.getString(1));
                    facturacion.setFolLot(rs.getString(2));
                    facturacion.setIdLote(rs.getString(3));
                    facturacion.setClaPro(rs.getString(4));
                    facturacion.setTipMed(rs.getInt(7));
                    facturacion.setCosto(rs.getDouble(8));
                    facturacion.setClaProve(rs.getString(9));
                    facturacion.setCant(rs.getInt(10));
                    facturacion.setExiLot(rs.getInt(11));
                    facturacion.setUbica(rs.getString(12));
                    facturacion.setId(rs.getString(14));
                    facturacion.setCantSol(rs.getString(16));
                    Factura.add(facturacion);
                }
            }
            psBuscaDatosFact.clearParameters();
            psBuscaDatosFact.close();
            psBuscaTemp.clearParameters();
            psBuscaTemp.close();
            rsTemp.close();

            psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
            rsIndice = psBuscaIndice.executeQuery();
            if (rsIndice.next()) {
                FolioFactura = rsIndice.getInt(1);
            }
            FolFact = FolioFactura + 1;

            psActualizaIndice = con.getConn().prepareStatement(String.format(ACTUALIZA_INDICEFACT, Proyecto));
            psActualizaIndice.setInt(1, FolFact);
            psActualizaIndice.execute();

            psActualizaIndice.clearParameters();
            psBuscaIndice.clearParameters();

            for (FacturacionModel f : Factura) {

                TipoMed = f.getTipMed();
                existencia = f.getExiLot();
                cantidad = f.getCant();

                if (TipoMed == 2504) {
                    IVA = 0.0;
                } else {
                    IVA = 0.16;
                }

                Costo = f.getCosto();
                int Diferencia = existencia - cantidad;

                if (Diferencia >= 0) {
                    if (Diferencia == 0) {
                        psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
                        psActualizaLote.setInt(1, 0);
                        psActualizaLote.setString(2, f.getIdLote());
                        psActualizaLote.execute();
                        psActualizaLote.clearParameters();
                        psActualizaLote.close();
                    } else {
                        psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
                        psActualizaLote.setInt(1, Diferencia);
                        psActualizaLote.setString(2, f.getIdLote());
                        psActualizaLote.execute();
                        psActualizaLote.clearParameters();
                        psActualizaLote.close();
                    }

                    IVAPro = (cantidad * Costo) * IVA;
                    Monto = cantidad * Costo;
                    MontoIva = Monto + IVAPro;

                    psBuscaIndice = con.getConn().prepareStatement(BUSCA_INDICEMOV);
                    rsIndice = psBuscaIndice.executeQuery();
                    if (rsIndice.next()) {
                        FolioMovi = rsIndice.getInt(1);
                    }

                    FolMov = FolioMovi + 1;

                    psActualizaIndice = con.getConn().prepareStatement(ACTUALIZA_INDICEMOV);
                    psActualizaIndice.setInt(1, FolMov);
                    psActualizaIndice.execute();
                    psActualizaIndice.clearParameters();
                    psBuscaIndice.clearParameters();
                    psBuscaIndice.close();

                    psInsertar = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
                    psInsertar.setInt(1, FolioFactura);
                    psInsertar.setInt(2, 51);
                    psInsertar.setString(3, f.getClaPro());
                    psInsertar.setInt(4, cantidad);
                    psInsertar.setDouble(5, Costo);
                    psInsertar.setDouble(6, MontoIva);
                    psInsertar.setString(7, "-1");
                    psInsertar.setString(8, f.getFolLot());
                    psInsertar.setString(9, f.getUbica());
                    psInsertar.setString(10, f.getClaProve());
                    psInsertar.setString(11, Usuario);
                    psInsertar.execute();
                    psInsertar.clearParameters();
                    psInsertar.close();

                    psInsertarFact = con.getConn().prepareStatement(INSERTA_FACTURA);
                    psInsertarFact.setInt(1, FolioFactura);
                    psInsertarFact.setString(2, f.getClaCli());
                    psInsertarFact.setString(3, f.getClaPro());
                    psInsertarFact.setString(4, f.getCantSol());
                    psInsertarFact.setInt(5, cantidad);
                    psInsertarFact.setDouble(6, Costo);
                    psInsertarFact.setDouble(7, IVAPro);
                    psInsertarFact.setDouble(8, MontoIva);
                    psInsertarFact.setString(9, f.getFolLot());
                    psInsertarFact.setString(10, FechaE);
                    psInsertarFact.setString(11, Usuario);
                    psInsertarFact.setString(12, f.getUbica());
                    psInsertarFact.setInt(13, Proyecto);
                    psInsertarFact.setString(14, Contrato);
                    psInsertarFact.setString(15, OC);
                    psInsertarFact.setInt(16, 0);
                    psInsertarFact.execute();
                    psInsertarFact.clearParameters();
                    psInsertarFact.close();

                    psActualizaTemp = con.getConn().prepareStatement(ACTUALIZA_FACTTEMLOTE);
                    psActualizaTemp.setString(1, f.getId());
                    psActualizaTemp.execute();
                    psActualizaTemp.clearParameters();
                    psActualizaTemp.close();

                } else {
                    save = false;
                    con.getConn().rollback();
                    return save;
                }

            }

            psInsertarObs = con.getConn().prepareStatement(INSERTA_OBSFACTURA);
            psInsertarObs.setInt(1, FolioFactura);
            psInsertarObs.setString(2, Observaciones);
            psInsertarObs.setString(3, Tipo);
            psInsertarObs.setInt(4, Proyecto);
            psInsertarObs.execute();
            psInsertarObs.clearParameters();
            psInsertarObs.close();

            save = true;
            con.getConn().commit();
            return save;
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;

    }

    @Override
    public boolean RegistrarFoliosN1(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC) {
        boolean save = false;
        int UbicaModu = 0, piezas = 0, F_Solicitado = 0, ContarV = 0, Existencia = 0, FolioFactura = 0, DifeSol = 0, FolioLote = 0;
        String UbicaDesc = "", Clave = "", Unidad = "", Unidad2 = "", UbicaLote = "", Contrato = "", Ubicaciones = "", UbicaNofacturar = "", TipoNivel = "";

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psBuscaContrato = con.getConn().prepareStatement(BuscaContrato);
            psBuscaContrato.setInt(1, Proyecto);
            rsContrato = psBuscaContrato.executeQuery();
            if (rsContrato.next()) {
                Contrato = rsContrato.getString(1);
            }

//            if (!(Usuario.equals("Francisco"))) {
//                psUbicaNoFacturar = con.getConn().prepareStatement(BuscaUbicaNoFacturar);
//                rsUbicaNoFacturar = psUbicaNoFacturar.executeQuery();
//                if (rsUbicaNoFacturar.next()) {
//                    UbicaNofacturar = "," + rsUbicaNoFacturar.getString(1);
//                }
//            }
//
//            Ubicaciones = UbicaNofacturar;
            if (Catalogo > 0) {
                psConsulta = con.getConn().prepareStatement(BUSCA_PARAMETRO);
                psConsulta.setString(1, Usuario);
                rsConsulta = psConsulta.executeQuery();
                rsConsulta.next();
                UbicaModu = rsConsulta.getInt(1);
                Proyecto = rsConsulta.getInt(2);
                UbicaDesc = rsConsulta.getString(3);
                psConsulta.close();
                psConsulta = null;
//                switch (UbicaModu) {
//                    case 1:
//                        UbicaDesc = " WHERE F_Ubica IN ('MODULA','A0S','APE','DENTAL','REDFRIA')";
//                        break;
//                    case 2:
//                        UbicaDesc = " WHERE F_Ubica IN ('MODULA2','A0S','APE','DENTAL','REDFRIA')";
//                        break;
//                    case 3:
//                        UbicaDesc = " WHERE F_Ubica IN ('AF','APE','DENTAL','REDFRIA','CONTROLADO')";
//                        break;
//                    case 4:
//                        UbicaDesc = " WHERE F_Ubica NOT IN ('A0S','MODULA','MODULA2','CADUCADOS','PROXACADUCAR','MERMA','CROSSDOCKMORELIA','INGRESOS_V','CUARENTENA','LERMA'" + Ubicaciones + ")";
//                        break;
//                    case 5:
//                        UbicaDesc = " WHERE F_Ubica IN ('AF2','APE','DENTAL','REDFRIA','CONTROLADO')";
//                        break;
//                        
//                    default:
//                        UbicaDesc = " WHERE F_Ubica IN ('AF3','APE','DENTAL','REDFRIA','CONTROLADO')";
//                        break;
//                }
            }
            psActualizaIndice = con.getConn().prepareStatement(String.format(ACTUALIZA_INDICEFACT, Proyecto));
            psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
            psInsertarMov = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
            psInsertarFact = con.getConn().prepareStatement(INSERTA_FACTURA);
            psActualizaIndiceLote = con.getConn().prepareStatement(ACTUALIZA_INDICELOTE);
            psINSERTLOTE = con.getConn().prepareStatement(INSERTAR_NUEVOLOTE);
            psActualizaReq = con.getConn().prepareStatement(ACTUALIZA_STSREQ);
            psInsertarObs = con.getConn().prepareStatement(INSERTAR_OBSERVACIONES);
            psAbastoInsert = con.getConn().prepareStatement(InsertAbasto);

            psBuscaUnidad = con.getConn().prepareStatement(String.format(BUSCA_UNIDADESN1, ClaUnidad, Catalogo));
            rsBuscaUnidad = psBuscaUnidad.executeQuery();
            while (rsBuscaUnidad.next()) {
                Unidad = rsBuscaUnidad.getString(1);
                Unidad2 = rsBuscaUnidad.getString(1);
                TipoNivel = rsBuscaUnidad.getString(2);
                Unidad = "'" + Unidad + "'";
                psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
                rsIndice = psBuscaIndice.executeQuery();
                if (rsIndice.next()) {
                    FolioFactura = rsIndice.getInt(1);
                }

                psActualizaIndice.setInt(1, FolioFactura + 1);
                psActualizaIndice.addBatch();

                psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTURARN1, UbicaDesc, UbicaDesc, Unidad, Catalogo));
                psBuscaDatosFact.setInt(1, Proyecto);
                psBuscaDatosFact.setInt(2, Proyecto);
                psBuscaDatosFact.setString(3, TipoNivel);
                rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                while (rsBuscaDatosFact.next()) {
                    Clave = rsBuscaDatosFact.getString(2);
                    piezas = rsBuscaDatosFact.getInt(3);
                    F_Solicitado = rsBuscaDatosFact.getInt(5);
                    Existencia = rsBuscaDatosFact.getInt(8);
                    FolioLote = rsBuscaDatosFact.getInt(9);
                    UbicaLote = rsBuscaDatosFact.getString(10);
                    Observaciones = rsBuscaDatosFact.getString(11);

                    if ((piezas > 0) && (Existencia > 0)) {

                        int F_IdLote = 0, F_FolLot = 0, Tipo = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;
                        int Facturado = 0, Contar = 0;
                        String Ubicacion = "";
                        double Costo = 0.0, IVA = 0.0, IVAPro = 0.0, Monto = 0.0, MontoIva = 0.0;

                        psContarReg = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBIN1, UbicaDesc, UbicaDesc, Catalogo));
                        psContarReg.setString(1, Clave);
                        psContarReg.setInt(2, Proyecto);
                        psContarReg.setInt(3, Proyecto);
                        psContarReg.setString(4, Clave);
                        psContarReg.setString(5, TipoNivel);
                        rsContarReg = psContarReg.executeQuery();
                        while (rsContarReg.next()) {
                            Contar++;
                        }

                        psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBIN1, UbicaDesc, UbicaDesc, Catalogo));
                        psBuscaExiFol.setString(1, Clave);
                        psBuscaExiFol.setInt(2, Proyecto);
                        psBuscaExiFol.setInt(3, Proyecto);
                        psBuscaExiFol.setString(4, Clave);
                        psBuscaExiFol.setString(5, TipoNivel);

                        System.out.println("BuscaExistenciaDetalle=" + psBuscaExiFol);
                        rsBuscaExiFol = psBuscaExiFol.executeQuery();
                        while (rsBuscaExiFol.next()) {
                            F_IdLote = rsBuscaExiFol.getInt(1);
                            F_ExiLot = rsBuscaExiFol.getInt(7);
                            F_FolLot = rsBuscaExiFol.getInt(3);
                            Tipo = rsBuscaExiFol.getInt(4);
                            Costo = rsBuscaExiFol.getDouble(5);
                            Ubicacion = rsBuscaExiFol.getString(6);
                            ClaProve = rsBuscaExiFol.getInt(8);
                            if (Tipo == 2504) {
                                IVA = 0.0;
                            } else {
                                IVA = 0.16;
                            }

                            Costo = 0.0;

                            if ((F_ExiLot >= piezas) && (piezas > 0)) {
                                Contar = Contar - 1;
                                diferencia = F_ExiLot - piezas;
                                CanSur = piezas;

                                psActualizaLote.setInt(1, diferencia);
                                psActualizaLote.setInt(2, F_IdLote);
                                System.out.println("ActualizaLote=" + psActualizaLote + " Clave=" + Clave);
                                psActualizaLote.addBatch();

                                IVAPro = (CanSur * Costo) * IVA;
                                Monto = CanSur * Costo;
                                MontoIva = Monto + IVAPro;

                                psInsertarMov.setInt(1, FolioFactura);
                                psInsertarMov.setInt(2, 51);
                                psInsertarMov.setString(3, Clave);
                                psInsertarMov.setInt(4, CanSur);
                                psInsertarMov.setDouble(5, Costo);
                                psInsertarMov.setDouble(6, MontoIva);
                                psInsertarMov.setString(7, "-1");
                                psInsertarMov.setInt(8, F_FolLot);
                                psInsertarMov.setString(9, Ubicacion);
                                psInsertarMov.setInt(10, ClaProve);
                                psInsertarMov.setString(11, Usuario);
                                System.out.println("Mov1" + psInsertarMov);
                                psInsertarMov.addBatch();

                                psInsertarFact.setInt(1, FolioFactura);
                                psInsertarFact.setString(2, Unidad2);
                                psInsertarFact.setString(3, Clave);
                                psInsertarFact.setInt(4, F_Solicitado);
                                psInsertarFact.setInt(5, CanSur);
                                psInsertarFact.setDouble(6, Costo);
                                psInsertarFact.setDouble(7, IVAPro);
                                psInsertarFact.setDouble(8, MontoIva);
                                psInsertarFact.setInt(9, F_FolLot);
                                psInsertarFact.setString(10, FecEnt);
                                psInsertarFact.setString(11, Usuario);
                                psInsertarFact.setString(12, Ubicacion);
                                psInsertarFact.setInt(13, Proyecto);
                                psInsertarFact.setString(14, Contrato);
                                psInsertarFact.setString(15, OC);
                                psInsertarFact.setInt(16, 0);
                                System.out.println("fact1" + psInsertarFact);
                                psInsertarFact.addBatch();

                                piezas = 0;
                                F_Solicitado = 0;
                                break;

                            } else if ((piezas > 0) && (F_ExiLot > 0)) {
                                Contar = Contar - 1;
                                diferencia = piezas - F_ExiLot;
                                CanSur = F_ExiLot;
                                if (F_ExiLot >= F_Solicitado) {
                                    DifeSol = F_Solicitado;
                                } else if (Contar > 0) {
                                    DifeSol = F_ExiLot;
                                } else {
                                    DifeSol = F_Solicitado - F_ExiLot;
                                }

                                psActualizaLote.setInt(1, 0);
                                psActualizaLote.setInt(2, F_IdLote);
                                System.out.println("ActualizaLote2=" + psActualizaLote + " Clave=" + Clave);
                                psActualizaLote.addBatch();

                                IVAPro = (CanSur * Costo) * IVA;
                                Monto = CanSur * Costo;
                                MontoIva = Monto + IVAPro;

                                psInsertarMov.setInt(1, FolioFactura);
                                psInsertarMov.setInt(2, 51);
                                psInsertarMov.setString(3, Clave);
                                psInsertarMov.setInt(4, CanSur);
                                psInsertarMov.setDouble(5, Costo);
                                psInsertarMov.setDouble(6, MontoIva);
                                psInsertarMov.setString(7, "-1");
                                psInsertarMov.setInt(8, F_FolLot);
                                psInsertarMov.setString(9, Ubicacion);
                                psInsertarMov.setInt(10, ClaProve);
                                psInsertarMov.setString(11, Usuario);
                                System.out.println("Mov2" + psInsertarMov);
                                psInsertarMov.addBatch();

                                psInsertarFact.setInt(1, FolioFactura);
                                psInsertarFact.setString(2, Unidad2);
                                psInsertarFact.setString(3, Clave);
                                psInsertarFact.setInt(4, DifeSol);
                                psInsertarFact.setInt(5, CanSur);
                                psInsertarFact.setDouble(6, Costo);
                                psInsertarFact.setDouble(7, IVAPro);
                                psInsertarFact.setDouble(8, MontoIva);
                                psInsertarFact.setInt(9, F_FolLot);
                                psInsertarFact.setString(10, FecEnt);
                                psInsertarFact.setString(11, Usuario);
                                psInsertarFact.setString(12, Ubicacion);
                                psInsertarFact.setInt(13, Proyecto);
                                psInsertarFact.setString(14, Contrato);
                                psInsertarFact.setString(15, OC);
                                psInsertarFact.setInt(16, 0);
                                System.out.println("fact2" + psInsertarFact);
                                psInsertarFact.addBatch();

                                F_Solicitado = F_Solicitado - CanSur;

                                piezas = piezas - CanSur;
                                F_ExiLot = 0;

                            }
                            if (Contar == 0) {
                                if (F_Solicitado > 0) {
                                    psInsertarFact.setInt(1, FolioFactura);
                                    psInsertarFact.setString(2, Unidad2);
                                    psInsertarFact.setString(3, Clave);
                                    psInsertarFact.setInt(4, F_Solicitado);
                                    psInsertarFact.setInt(5, 0);
                                    psInsertarFact.setDouble(6, Costo);
                                    psInsertarFact.setDouble(7, IVAPro);
                                    psInsertarFact.setDouble(8, MontoIva);
                                    psInsertarFact.setInt(9, F_FolLot);
                                    psInsertarFact.setString(10, FecEnt);
                                    psInsertarFact.setString(11, Usuario);
                                    psInsertarFact.setString(12, Ubicacion);
                                    psInsertarFact.setInt(13, Proyecto);
                                    psInsertarFact.setString(14, Contrato);
                                    psInsertarFact.setString(15, OC);
                                    psInsertarFact.setInt(16, 0);
                                    System.out.println("fact3" + psInsertarFact);
                                    psInsertarFact.addBatch();
                                    F_Solicitado = 0;
                                }
                            }

                        }

                    } else if ((FolioLote > 0) && (UbicaLote != "")) {
                        psInsertarFact.setInt(1, FolioFactura);
                        psInsertarFact.setString(2, Unidad2);
                        psInsertarFact.setString(3, Clave);
                        psInsertarFact.setInt(4, F_Solicitado);
                        psInsertarFact.setInt(5, 0);
                        psInsertarFact.setDouble(6, 0);
                        psInsertarFact.setDouble(7, 0);
                        psInsertarFact.setDouble(8, 0);
                        psInsertarFact.setInt(9, FolioLote);
                        psInsertarFact.setString(10, FecEnt);
                        psInsertarFact.setString(11, Usuario);
                        psInsertarFact.setString(12, UbicaLote);
                        psInsertarFact.setInt(13, Proyecto);
                        psInsertarFact.setString(14, Contrato);
                        psInsertarFact.setString(15, OC);
                        psInsertarFact.setInt(16, 0);
                        System.out.println("fact4" + psInsertarFact);
                        psInsertarFact.addBatch();
                    } else {
                        int FolioL = 0, IndiceLote = 0;
                        String Ubicacion = "";
                        double Costo = 0.0;

                        psBuscaIndiceLote = con.getConn().prepareStatement(BUSCA_INDICELOTE);
                        rsIndiceLote = psBuscaIndiceLote.executeQuery();
                        rsIndiceLote.next();
                        FolioL = rsIndiceLote.getInt(1);

                        IndiceLote = FolioL + 1;

                        psActualizaIndiceLote.setInt(1, IndiceLote);
                        psActualizaIndiceLote.addBatch();

                        psINSERTLOTE.setString(1, Clave);
                        psINSERTLOTE.setInt(2, FolioL);
                        psINSERTLOTE.setInt(3, Proyecto);
                        System.out.println("InsertarLote" + psINSERTLOTE);
                        psINSERTLOTE.addBatch();

                        psInsertarFact.setInt(1, FolioFactura);
                        psInsertarFact.setString(2, Unidad2);
                        psInsertarFact.setString(3, Clave);
                        psInsertarFact.setInt(4, F_Solicitado);
                        psInsertarFact.setInt(5, 0);
                        psInsertarFact.setDouble(6, Costo);
                        psInsertarFact.setDouble(7, 0);
                        psInsertarFact.setDouble(8, 0);
                        psInsertarFact.setInt(9, FolioL);
                        psInsertarFact.setString(10, FecEnt);
                        psInsertarFact.setString(11, Usuario);
                        psInsertarFact.setString(12, "NUEVA");
                        psInsertarFact.setInt(13, Proyecto);
                        psInsertarFact.setString(14, Contrato);
                        psInsertarFact.setString(15, OC);
                        psInsertarFact.setInt(16, 0);
                        System.out.println(" psInsertarFact 3" + psInsertarFact);
                        psInsertarFact.addBatch();
                    }

                }

                psActualizaReq.setString(1, Unidad2);
                psActualizaReq.addBatch();;

                psInsertarObs.setInt(1, FolioFactura);
                psInsertarObs.setString(2, Observaciones);
                psInsertarObs.setString(3, Tipos);
                psInsertarObs.setInt(4, Proyecto);
                psInsertarObs.addBatch();

                psActualizaIndice.executeBatch();
                psActualizaLote.executeBatch();
                psINSERTLOTE.executeBatch();
                psInsertarMov.executeBatch();
                psInsertarFact.executeBatch();
                psActualizaIndiceLote.executeBatch();
                psActualizaReq.executeBatch();
                psInsertarObs.executeBatch();

/*AbastoService abasto = null;
abasto.crearAbastoWeb(FolioFactura,  Proyecto, Usuario);*/

      psAbasto = con.getConn().prepareStatement(DatosAbasto);
                psAbasto.setInt(1, Proyecto);
                psAbasto.setInt(2, FolioFactura);
                rsAbasto = psAbasto.executeQuery();
                while (rsAbasto.next()) {
                    int factorEmpaque = 1;
                    int folLot = rsAbasto.getInt("LOTE");
                    PreparedStatement psfe = con.getConn().prepareStatement(getFactorEmpaque);
                    psfe.setInt(1, folLot);
                    ResultSet rsfe = psfe.executeQuery();
                    if (rsfe.next()) {
                        factorEmpaque = rsfe.getInt("factor");
                    }
                    psAbastoInsert.setString(1, rsAbasto.getString(1));
                    psAbastoInsert.setString(2, rsAbasto.getString(2));
                    psAbastoInsert.setString(3, rsAbasto.getString(3));
                    psAbastoInsert.setString(4, rsAbasto.getString(4));
                    psAbastoInsert.setString(5, rsAbasto.getString(5));
                    psAbastoInsert.setString(6, rsAbasto.getString(6));
                    psAbastoInsert.setString(7, rsAbasto.getString(7));
                    psAbastoInsert.setString(8, rsAbasto.getString(8));
                    psAbastoInsert.setString(9, rsAbasto.getString(12));
                    psAbastoInsert.setString(10, rsAbasto.getString(10));
                    psAbastoInsert.setString(11, Usuario);
                    psAbastoInsert.setInt(12, factorEmpaque);
                    System.out.println("fact abasto 2" + psAbastoInsert);
                    psAbastoInsert.addBatch();
                }

                psAbastoInsert.executeBatch();

                save = true;
                con.getConn().commit();
                System.out.println("Terminó Unidad= " + Unidad + " Con el Folio= " + FolioFactura);

            }

            psBuscaExiFol.close();
            psBuscaExiFol = null;
            rsBuscaExiFol.close();
            rsBuscaExiFol = null;
            psBuscaDatosFact.close();
            psBuscaDatosFact = null;
            rsBuscaDatosFact.close();
            rsBuscaDatosFact = null;
//            rsUbicaNoFacturar.close();
//            rsUbicaNoFacturar = null;
            //return save;
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                save = false;
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
                return save;
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;
    }

    @Override
    public JSONArray getRegistroFactAutoNivel(String ClaUni, String Catalogo) {
        JSONArray jsonArray = new JSONArray();
        JSONArray jsonArray2 = new JSONArray();
        JSONObject jsonObj;
        JSONObject jsonObj2;
        System.out.println("Unidades Nivel:" + ClaUni);
        String query = "SELECT U.F_ClaPro, F_ClaUni, CONCAT( F_ClaUni, '_', REPLACE (U.F_ClaPro, '.', '')) AS DATOS, F_IdReq, CASE WHEN UN.F_ClaCli IS NULL THEN 'UNIDADES' ELSE 'JURISDICCIONES' END AS TIPO FROM tb_unireq U INNER JOIN tb_medicatipo M ON U.F_ClaPro = M.F_ClaPro INNER JOIN tb_medica MD ON M.F_ClaPro = MD.F_ClaPro LEFT JOIN ( SELECT F_ClaCli FROM tb_medicanivelunidad ) UN ON U.F_ClaUni = UN.F_ClaCli WHERE F_ClaUni IN (%s) AND F_Status = 0 AND F_Solicitado != 0 AND MD.F_StsPro = 'A' AND M.F_Tipo = CASE WHEN UN.F_ClaCli IS NULL THEN 'UNIDADES' ELSE 'JURISDICCIONES' END;";
        PreparedStatement ps;
        ResultSet rs;
        try {
            int Contar = 0;
            con.conectar();
            ps = con.getConn().prepareStatement(String.format(query, ClaUni));
            System.out.println("UNidades Nivel=" + ps);
            rs = ps.executeQuery();

            while (rs.next()) {
                jsonObj = new JSONObject();
                jsonObj.put("ClaPro", rs.getString(1));
                jsonObj.put("ClaUni", rs.getString(2));
                jsonObj.put("Datos", rs.getString(3));
                jsonObj.put("IdReg", rs.getString(4));
                jsonArray.add(jsonObj);

            }
            ps.close();
//            ps = null;
            rs.close();
//            rs = null;
            jsonObj2 = new JSONObject();
            jsonArray.add(jsonObj2);
            con.cierraConexion();

        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return jsonArray;
    }

    @Override
    public boolean RegistrarFoliosApartar2Folio(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC, String agrupacion) {
        boolean save = false;
        int UbicaModu = 0, piezas = 0, F_Solicitado = 0, ContarV = 0, Existencia = 0, FolioFactura = 0, DifeSol = 0, FolioLote = 0;
        String UbicaDesc = "", Clave = "", Unidad = "", Unidad2 = "", UbicaLote = "", Contrato = "", Ubicaciones = "", UbicaNofacturar = "";
        ParametrosFolio params = new ParametrosFolio();
        params.setProyecto(Proyecto);
        params.setOc(OC);
        params.setObservaciones(Observaciones);
        params.setUsuario(Usuario);
        params.setFecEnt(FecEnt);
        params.setCatalogo(Catalogo);
        params.setUnidad(ClaUnidad);
        params.setFolioFactura(0);
        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psActualizaTemp = con.getConn().prepareStatement(ELIMINA_DATOSporFACTURARTEMP);
            psActualizaTemp.setString(1, Usuario);
            psActualizaTemp.execute();
            psActualizaTemp.clearParameters();

            psBuscaContrato = con.getConn().prepareStatement(BuscaContrato);
            psBuscaContrato.setInt(1, Proyecto);
            rsContrato = psBuscaContrato.executeQuery();
            psBuscaContrato.clearParameters();
            if (rsContrato.next()) {
                Contrato = rsContrato.getString(1);
                params.setContrato(Contrato);
            }

//            psUbicaCrossdock = con.getConn().prepareStatement(BuscaUbicacionesCross);
//            rsUbicaCross = psUbicaCrossdock.executeQuery();
//            if (rsUbicaCross.next()) {
//                Ubicaciones = rsUbicaCross.getString(1);
//            }
//      
            if (Catalogo > 0) {
                psConsulta = con.getConn().prepareStatement(BUSCA_PARAMETRO);
                psConsulta.setString(1, Usuario);
                rsConsulta = psConsulta.executeQuery();
                rsConsulta.next();
                UbicaModu = rsConsulta.getInt(1);
                params.setUbicaModu(UbicaModu);
                Proyecto = rsConsulta.getInt(2);
                UbicaDesc = rsConsulta.getString(3);

                String parametro = rsConsulta.getString(4);
                if (parametro.equals("AF2") && Tipos.equals("Urgente")) {
                    UbicaDesc = "WHERE F_Ubica IN ('APEURGENTE','ORDINARIOURGENTE','REDFRIAURGENTE','CONTROLADOURGENTE')";
                }
                psConsulta.close();
                psConsulta = null;
                params.setUbicaDesc(UbicaDesc);
//               
            }

            psActualizaIndice = con.getConn().prepareStatement(String.format(ACTUALIZA_INDICEFACT, Proyecto));
            psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
            psInsertarMov = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
            psInsertarFactTemp = con.getConn().prepareStatement(INSERTA_FACTURATEMP);
            psInsertarFact = con.getConn().prepareStatement(INSERTA_FACTURA);
            psActualizaIndiceLote = con.getConn().prepareStatement(ACTUALIZA_INDICELOTE);
            psINSERTLOTE = con.getConn().prepareStatement(INSERTAR_NUEVOLOTE);
            psActualizaReq = con.getConn().prepareStatement(ACTUALIZA_STSREQ);
            psInsertarObs = con.getConn().prepareStatement(INSERTAR_OBSERVACIONES);

            psBuscaUnidad = con.getConn().prepareStatement(String.format(BUSCA_UNIDADES, ClaUnidad, Catalogo));
            System.out.println(psBuscaUnidad);
            rsBuscaUnidad = psBuscaUnidad.executeQuery();
            while (rsBuscaUnidad.next()) {
                Unidad = rsBuscaUnidad.getString(1);
                Unidad2 = rsBuscaUnidad.getString(1);
                Unidad = "'" + Unidad + "'";
                params.setUnidad(Unidad2);

                String query = "";
                System.out.println("UbicaModu: "+UbicaModu);
                if(UbicaModu == 14 || UbicaModu == 40 || UbicaModu == 41) {
                    
                         query = String.format(BUSCA_DATOSporFACTURARCONTRCADUCA, UbicaDesc, UbicaDesc, Unidad, Catalogo);
                         
                }else{         
                        query = String.format(BUSCA_DATOSporFACTURARCONTR, UbicaDesc, UbicaDesc, Unidad, Catalogo);
                 }
                System.out.println("PARA CONTROLADOS: "+query );
                psBuscaDatosFact = con.getConn().prepareStatement(query);
                psBuscaDatosFact.setInt(1, Proyecto);
                psBuscaDatosFact.setInt(2, Proyecto);
                System.out.println(psBuscaDatosFact);
                rsBuscaDatosFact = psBuscaDatosFact.executeQuery();

                while (rsBuscaDatosFact.next()) {
                    System.out.println("si hay controlado");
                    this.insertaFactTemp(params, true);
                }
                switch (UbicaModu) {
                    case 14:
                    case 40:
                    case 41:    
                     System.out.println("UbicaModu2: "+UbicaModu);
                        psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTURAR2, UbicaDesc, UbicaDesc, Unidad, Catalogo));
                        break;
                    
                    case 19: 
                        psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTURAR19, UbicaDesc, UbicaDesc, Unidad, Catalogo));
                        break;
                    
                    default: 
                        psBuscaDatosFact = con.getConn().prepareStatement(String.format(BUSCA_DATOSporFACTURAR, UbicaDesc, UbicaDesc, Unidad, Catalogo));
                    
                }
                psBuscaDatosFact.setInt(1, Proyecto);
                psBuscaDatosFact.setInt(2, Proyecto);
                System.out.println(psBuscaDatosFact);
                rsBuscaDatosFact = psBuscaDatosFact.executeQuery();

                while (rsBuscaDatosFact.next()) {
                    System.out.println("si hay normal");
                    this.insertaFactTemp(params, false);
                }

                psActualizaReq.setString(1, Unidad2);
                psActualizaReq.addBatch();
                psINSERTLOTE.executeBatch();
                System.out.println("inserta factura temp: "+ psInsertarFactTemp.executeBatch());
                psInsertarFactTemp.executeBatch();
                psActualizaIndiceLote.executeBatch();
                psActualizaReq.executeBatch();
                save = true;
                con.getConn().commit();

            }

            psBuscaDatosFact.close();
            psBuscaDatosFact = null;
            rsBuscaDatosFact.close();
            rsBuscaDatosFact = null;
//            rsUbicaNoFacturar.close();
//            rsUbicaNoFacturar = null;
            psActualizaReq.close();
            psActualizaReq = null;
            psINSERTLOTE.close();
            psINSERTLOTE = null;
            psInsertarFactTemp.close();
            psInsertarFactTemp = null;
            psActualizaIndiceLote.close();
            psActualizaIndiceLote = null;
            psBuscaUnidad.close();
            psBuscaUnidad = null;
            rsBuscaUnidad.close();
            rsBuscaUnidad = null;
            return save;
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                save = false;
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;
    }

    private void insertaFactTemp(ParametrosFolio params, boolean controlado) throws SQLException {
        String Clave = rsBuscaDatosFact.getString(2);
        int piezas = rsBuscaDatosFact.getInt(3);
        int F_Solicitado = rsBuscaDatosFact.getInt(5);
        int Existencia = rsBuscaDatosFact.getInt(8);
        int FolioLote = rsBuscaDatosFact.getInt(9);
        String UbicaLote = rsBuscaDatosFact.getString(10);
//      Observaciones = rsBuscaDatosFact.getString(11);
        String tipoUnidad = rsBuscaDatosFact.getString("F_Tipo");
        String origen = rsBuscaDatosFact.getString(12);
        
        String queryExistencia = BUSCA_EXITFOLUBI5FOLIO;
        if (tipoUnidad.equals("RURAL"))
        {
            queryExistencia = BUSCA_EXITFOLUBI5FOLIORURAL;
        }
        if (controlado) {
            queryExistencia = BUSCA_EXITFOLUBI5FOLIOCONTROLADO;
        }
        
        if ((piezas > 0) && (Existencia > 0)) 
        {

            int F_IdLote = 0, F_FolLot = 0, Tipo = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;
            int Facturado = 0, Contar = 0;
            String Ubicacion = "";
            double Costo = 0.0, IVA = 0.0, IVAPro = 0.0, Monto = 0.0, MontoIva = 0.0;

            /**remisioanr caducados*/
            if (params.getUbicaModu() == 14 || params.getUbicaModu() == 40 || params.getUbicaModu() == 41) {
                System.out.println("caduco controlado factura temporal");
                psContarReg = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI5FOLIO2, params.getUbicaDesc(), params.getUbicaDesc(), params.getCatalogo()));
            } else if (params.getUbicaModu() == 19) {
                String query = String.format(BUSCA_EXITFOLUBI19, params.getUnidad(), params.getUbicaDesc(), params.getUbicaDesc(), params.getCatalogo());
                System.out.println(query);
                psContarReg = con.getConn().prepareStatement(query);
            } else {
                psContarReg = con.getConn().prepareStatement(String.format(queryExistencia, params.getUbicaDesc(), params.getUbicaDesc(), params.getCatalogo()));

            }

            psContarReg.setString(1, Clave);
            psContarReg.setInt(2, params.getProyecto());
            psContarReg.setInt(3, params.getProyecto());
            psContarReg.setString(4, Clave);
            rsContarReg = psContarReg.executeQuery();
            while (rsContarReg.next()) {
                Contar++;
            }
            System.out.println("conta de facttemp");
            if (Contar > 0) {
                if (params.getUbicaModu() == 14 || params.getUbicaModu() == 40 || params.getUbicaModu() == 41) {
                    System.out.println("caduco normal factura temporal");
                    psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI5FOLIO2, params.getUbicaDesc(), params.getUbicaDesc(), params.getCatalogo()));

                } else if (params.getUbicaModu() == 19) {
                    psBuscaExiFol = con.getConn().prepareStatement(String.format(BUSCA_EXITFOLUBI19, params.getUnidad(), params.getUbicaDesc(), params.getUbicaDesc(), params.getCatalogo()));
                } else {
                    psBuscaExiFol = con.getConn().prepareStatement(String.format(queryExistencia, params.getUbicaDesc(), params.getUbicaDesc(), params.getCatalogo()));
                }

                psBuscaExiFol.setString(1, Clave);
                psBuscaExiFol.setInt(2, params.getProyecto());
                psBuscaExiFol.setInt(3, params.getProyecto());
                psBuscaExiFol.setString(4, Clave);
                rsBuscaExiFol = psBuscaExiFol.executeQuery();

                this.insertarFacturaTemp(rsBuscaExiFol, piezas, params.getFolioFactura(), params.getUnidad(), Clave, F_Solicitado, params.getFecEnt(), params.getUsuario(), params.getObservaciones(), params.getProyecto(), params.getContrato(), params.getOc(), Contar, true);

            } else {
                int FolioL = 0, IndiceLote = 0;
                Costo = 0.0;

                psBuscaIndiceLote = con.getConn().prepareStatement(BuscaFolioLotesinexistencia);
                psBuscaIndiceLote.setString(1, Clave);
                psBuscaIndiceLote.setInt(2, params.getProyecto());
                rsIndiceLote = psBuscaIndiceLote.executeQuery();
                if (rsIndiceLote.next()) {
                    FolioL = rsIndiceLote.getInt(1);
                }

                if (FolioL == 0) {

                    psBuscaIndiceLote.clearParameters();
                    psBuscaIndiceLote = con.getConn().prepareStatement(BUSCA_INDICELOTE);
                    rsIndiceLote = psBuscaIndiceLote.executeQuery();
                    rsIndiceLote.next();
                    FolioL = rsIndiceLote.getInt(1);

                    IndiceLote = FolioL + 1;

                    psActualizaIndiceLote.setInt(1, IndiceLote);
                    psActualizaIndiceLote.executeUpdate();
                    psActualizaIndiceLote.clearParameters();

                    psINSERTLOTE.setString(1, Clave);
                    psINSERTLOTE.setInt(2, FolioL);
                    psINSERTLOTE.setInt(3, 1);
                    psINSERTLOTE.setInt(4, params.getProyecto());
                    System.out.println("InsertarLote" + psINSERTLOTE);
                    psINSERTLOTE.addBatch();
                }

                psInsertarFactTemp.setInt(1, params.getFolioFactura());
                psInsertarFactTemp.setString(2, params.getUnidad());
                psInsertarFactTemp.setString(3, Clave);
                psInsertarFactTemp.setInt(4, F_Solicitado);
                psInsertarFactTemp.setInt(5, 0);
                psInsertarFactTemp.setDouble(6, Costo);
                psInsertarFactTemp.setDouble(7, 0);
                psInsertarFactTemp.setDouble(8, 0);
                psInsertarFactTemp.setInt(9, FolioL);
                psInsertarFactTemp.setString(10, params.getFecEnt());
                psInsertarFactTemp.setString(11, params.getUsuario());
                psInsertarFactTemp.setString(12, "NUEVA");
                psInsertarFactTemp.setString(13, params.getObservaciones());
                psInsertarFactTemp.setInt(14, params.getProyecto());
                psInsertarFactTemp.setString(15, params.getContrato());
                psInsertarFactTemp.setString(16, params.getOc());
                psInsertarFactTemp.setInt(17, 0);
                psInsertarFactTemp.addBatch();
            }

            if (Contar > 0) {
                psBuscaExiFol.close();
                psBuscaExiFol = null;
                rsBuscaExiFol.close();
                rsBuscaExiFol = null;
            }
        } else if ((FolioLote > 0) && (UbicaLote != "")) {
            psInsertarFactTemp.setInt(1, params.getFolioFactura());
            psInsertarFactTemp.setString(2, params.getUnidad());
            psInsertarFactTemp.setString(3, Clave);
            psInsertarFactTemp.setInt(4, F_Solicitado);
            psInsertarFactTemp.setInt(5, 0);
            psInsertarFactTemp.setDouble(6, 0);
            psInsertarFactTemp.setDouble(7, 0);
            psInsertarFactTemp.setDouble(8, 0);
            psInsertarFactTemp.setInt(9, FolioLote);
            psInsertarFactTemp.setString(10, params.getFecEnt());
            psInsertarFactTemp.setString(11, params.getUsuario());
            psInsertarFactTemp.setString(12, UbicaLote);
            psInsertarFactTemp.setString(13, params.getObservaciones());
            psInsertarFactTemp.setInt(14, params.getProyecto());
            psInsertarFactTemp.setString(15, params.getContrato());
            psInsertarFactTemp.setString(16, params.getOc());
            psInsertarFactTemp.setInt(17, 0);
            psInsertarFactTemp.addBatch();
        } else {
            int FolioL = 0, IndiceLote = 0;
            double Costo = 0.0;

            psBuscaIndiceLote = con.getConn().prepareStatement(BuscaFolioLotesinexistencia);
            psBuscaIndiceLote.setString(1, Clave);
            psBuscaIndiceLote.setInt(2, params.getProyecto());
            rsIndiceLote = psBuscaIndiceLote.executeQuery();
            if (rsIndiceLote.next()) {
                FolioL = rsIndiceLote.getInt(1);
            }

            if (FolioL == 0) {

                psBuscaIndiceLote.clearParameters();
                psBuscaIndiceLote = con.getConn().prepareStatement(BUSCA_INDICELOTE);
                rsIndiceLote = psBuscaIndiceLote.executeQuery();
                rsIndiceLote.next();
                FolioL = rsIndiceLote.getInt(1);

                IndiceLote = FolioL + 1;

                psActualizaIndiceLote.setInt(1, IndiceLote);
                psActualizaIndiceLote.executeUpdate();
                psActualizaIndiceLote.clearParameters();

                int ori = 1;
                psINSERTLOTE.setString(1, Clave);
                psINSERTLOTE.setInt(2, FolioL);
                psINSERTLOTE.setInt(3, ori);
                psINSERTLOTE.setInt(4, params.getProyecto());
                System.out.println("InsertarLote" + psINSERTLOTE);
                psINSERTLOTE.addBatch();
            }

            psInsertarFactTemp.setInt(1, params.getFolioFactura());
            psInsertarFactTemp.setString(2, params.getUnidad());
            psInsertarFactTemp.setString(3, Clave);
            psInsertarFactTemp.setInt(4, F_Solicitado);
            psInsertarFactTemp.setInt(5, 0);
            psInsertarFactTemp.setDouble(6, Costo);
            psInsertarFactTemp.setDouble(7, 0);
            psInsertarFactTemp.setDouble(8, 0);
            psInsertarFactTemp.setInt(9, FolioL);
            psInsertarFactTemp.setString(10, params.getFecEnt());
            psInsertarFactTemp.setString(11, params.getUsuario());
            psInsertarFactTemp.setString(12, "NUEVA");
            psInsertarFactTemp.setString(13, params.getObservaciones());
            psInsertarFactTemp.setInt(14, params.getProyecto());
            psInsertarFactTemp.setString(15, params.getContrato());
            psInsertarFactTemp.setString(16, params.getOc());
            psInsertarFactTemp.setInt(17, 0);
            psInsertarFactTemp.addBatch();
        }

    }

    //facturacion automatica******************************************************///////

    @Override
    public boolean Registro2Folio(String ClaUnidad, int Catalogo, String Tipos, String FecEnt, String Usuario, String Observaciones, int Proyecto, String OC) {
        boolean save = false;
        int UbicaModu = 0, piezas = 0, F_Solicitado = 0, ContarV = 0, Existencia = 0, FolioFactura = 0, DifeSol = 0, FolioLote = 0, IdFact = 0, FolioCero = 0,rf=0,ctr=0,ape=0,oncape=0,oncrf=0;
        String UbicaDesc = "", Clave = "", Unidad = "", Unidad2 = "", UbicaLote = "", Contrato = "", Ubicaciones = "", UbicaNofacturar = "";

        List<DetalleFactura> controlado = new ArrayList<DetalleFactura>();
        List<DetalleFactura> normal = new ArrayList<DetalleFactura>();
        List<DetalleFactura> oncoape = new ArrayList<DetalleFactura>();
        List<DetalleFactura> oncoredfria = new ArrayList<DetalleFactura>();

        try {
            con.conectar();
            con.getConn().setAutoCommit(false);

            psActualizaIndice = con.getConn().prepareStatement(String.format(ACTUALIZA_INDICEFACT, Proyecto));
            psActualizaIndiceCero = con.getConn().prepareStatement(String.format(ACTUALIZA_INDICEFACTCERO, FolioCero));
            psActualizaLote = con.getConn().prepareStatement(ACTUALIZA_EXILOTE);
            psInsertarMov = con.getConn().prepareStatement(INSERTA_MOVIMIENTO);
            psInsertarFact = con.getConn().prepareStatement(INSERTA_FACTURA);
            psInsertarObs = con.getConn().prepareStatement(INSERTAR_OBSERVACIONES);

            psConsulta = con.getConn().prepareStatement(BUSCA_PARAMETRO);
            psConsulta.setString(1, Usuario);
            rsConsulta = psConsulta.executeQuery();
            rsConsulta.next();
            UbicaModu = rsConsulta.getInt(1); // id de parametro
            Proyecto = rsConsulta.getInt(2);
            UbicaDesc = rsConsulta.getString(3);
            psConsulta.close();
            psConsulta = null;

            psBuscaUnidad = con.getConn().prepareStatement(String.format(BUSCA_UNIDADESTEMP, ClaUnidad));
            rsBuscaUnidad = psBuscaUnidad.executeQuery();
            List<Integer> foliosCreados = new ArrayList<>();
            while (rsBuscaUnidad.next()) {
                Unidad = rsBuscaUnidad.getString(1);
                Unidad2 = rsBuscaUnidad.getString(1);
                Unidad = "'" + Unidad + "'";
                String Obs = "", ContratoSelect = "";
                double Costo = 0.0, IVA = 0.0, IVAPro = 0.0, Monto = 0.0, MontoIva = 0.0;
                int CantSur = 0, CantSurCR = 0, ProyectoSelect = 0, CantSurT = 0, ContarCero = 0, cantSurRF = 0,cantSurONCAPE = 0,cantSurONCRF = 0;

                psBuscaUnidadFactura = con.getConn().prepareStatement(BUSCA_UNIDADFACTURA2FOLIO);
                psBuscaUnidadFactura.setString(1, Unidad2);
                psBuscaUnidadFactura.setString(2, Usuario);
                psBuscaUnidadFactura.setString(3, Unidad2);
                psBuscaUnidadFactura.setString(4, Usuario);
                psBuscaUnidadFactura.setString(5, Unidad2);
                psBuscaUnidadFactura.setString(6, Usuario);
                psBuscaUnidadFactura.setString(7, Unidad2);
                psBuscaUnidadFactura.setString(8, Usuario);
                psBuscaUnidadFactura.setString(9, Unidad2);
                psBuscaUnidadFactura.setString(10, Usuario);
                psBuscaUnidadFactura.setString(11, Unidad2);
                psBuscaUnidadFactura.setString(12, Usuario);
                rsBuscaUnidadFactura = psBuscaUnidadFactura.executeQuery();
                if (rsBuscaUnidadFactura.next()) {
                    CantSurT = rsBuscaUnidadFactura.getInt(1);
                    CantSurCR = rsBuscaUnidadFactura.getInt(2);
                    cantSurRF = rsBuscaUnidadFactura.getInt(5);
                    cantSurONCAPE = rsBuscaUnidadFactura.getInt(3);
                    cantSurONCRF = rsBuscaUnidadFactura.getInt(4);
                    CantSurT = CantSurT + cantSurRF;
                    Obs = rsBuscaUnidadFactura.getString(4);
                }

                System.out.println("CantSurT: "+CantSurT+"  CantSurCR: "+ CantSurCR+" cantSurRF: "+cantSurRF);

                CantSur = CantSurT - CantSurCR;

                //controlado//////////
                if (CantSurCR > 0) {
                    System.out.println("si tengo controlado");
                    psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
                    rsIndice = psBuscaIndice.executeQuery();
                    if (rsIndice.next()) {
                        FolioFactura = rsIndice.getInt(1);
                    }

                    if (UbicaModu == 14 || UbicaModu == 40 || UbicaModu == 41) {

                        psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_UNIDADFACTURA2FOLIO2Controlado2);
                    } else {
                        psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_UNIDADFACTURA2FOLIO2Controlado);
                    }
                    System.out.println("hola controlado");
                    psBuscaDatosFact.setString(1, Unidad2);
                    psBuscaDatosFact.setString(2, Usuario);

                    System.out.println("datos a facturar controlado: " + psBuscaDatosFact);
                    rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                    while (rsBuscaDatosFact.next()) {
                        Clave = rsBuscaDatosFact.getString(1);
                        F_Solicitado = rsBuscaDatosFact.getInt(2);
                        piezas = rsBuscaDatosFact.getInt(3);
                        FolioLote = rsBuscaDatosFact.getInt(4);
                        UbicaLote = rsBuscaDatosFact.getString(5);
                        ProyectoSelect = rsBuscaDatosFact.getInt(6);
                        ContratoSelect = rsBuscaDatosFact.getString(7);
                        Costo = rsBuscaDatosFact.getDouble(8);
                        IVA = rsBuscaDatosFact.getDouble(9);
                        Monto = rsBuscaDatosFact.getDouble(10);
                        OC = rsBuscaDatosFact.getString(11);
                        IdFact = rsBuscaDatosFact.getInt(12);
                        String tipOri = rsBuscaDatosFact.getString(13);
                        int F_IdLote = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;

                        Costo = 0.0;
                        Monto = 0.0;
                        IVA = 0.0;

                        DetalleFactura f = DetalleFactura.buildDetalleFactura(rsBuscaDatosFact);
                        f.setFecEnt(FecEnt);
                        controlado.add(f);
                        psActualiza = con.getConn().prepareStatement(ActualizaIdFactura);
                        psActualiza.setInt(1, 1);
                        psActualiza.setString(2, Clave);
                        psActualiza.setInt(3, FolioLote);
                        psActualiza.setString(4, UbicaLote);
                        psActualiza.execute();
                        psActualiza.clearParameters();

                    }

                    //CREACION DE FOLIO CONTROLADOS
                    if (controlado.size() > 0) {
                        System.out.println("Creando folio controlado, claves:" + controlado.size());
                        psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
                        System.out.println("Consulta folioFactura " + psBuscaIndice);
                        rsIndice = psBuscaIndice.executeQuery();
                        if (rsIndice.next()) {
                            FolioFactura = rsIndice.getInt(1);
                        }
                        System.out.println("Indice Factura: " + FolioFactura);
                        this.crearFolio(controlado, UbicaDesc, Contrato, Usuario, FolioFactura, Unidad2, Tipos, Observaciones + " - CONTROLADO");
                        psActualizaIndice.setInt(1, FolioFactura + 1);
                        psActualizaIndice.execute();
                        foliosCreados.add(FolioFactura);
                    }

                    psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSporFACTEMP2FOLIOControlado);
                    psBuscaDatosFact.setString(1, Unidad2);
                    psBuscaDatosFact.setInt(2, 0);
                    psBuscaDatosFact.setString(3, Usuario);
                    rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                    while (rsBuscaDatosFact.next()) {
                        Clave = rsBuscaDatosFact.getString(1);
                        F_Solicitado = rsBuscaDatosFact.getInt(2);
                        piezas = rsBuscaDatosFact.getInt(3);
                        FolioLote = rsBuscaDatosFact.getInt(4);
                        UbicaLote = rsBuscaDatosFact.getString(5);
                        ProyectoSelect = rsBuscaDatosFact.getInt(6);
                        ContratoSelect = rsBuscaDatosFact.getString(7);
                        Costo = rsBuscaDatosFact.getDouble(8);
                        IVA = rsBuscaDatosFact.getDouble(9);
                        Monto = rsBuscaDatosFact.getDouble(10);
                        OC = rsBuscaDatosFact.getString(11);

                        psInsertarFact.setInt(1, FolioFactura);
                        psInsertarFact.setString(2, Unidad2);
                        psInsertarFact.setString(3, Clave);
                        psInsertarFact.setInt(4, F_Solicitado);
                        psInsertarFact.setInt(5, 0);
                        psInsertarFact.setDouble(6, 0.00);
                        psInsertarFact.setDouble(7, 0.00);
                        psInsertarFact.setDouble(8, 0.00);
                        psInsertarFact.setInt(9, FolioLote);
                        psInsertarFact.setString(10, FecEnt);
                        psInsertarFact.setString(11, Usuario);
                        psInsertarFact.setString(12, UbicaLote);
                        psInsertarFact.setInt(13, ProyectoSelect);
                        psInsertarFact.setString(14, ContratoSelect);
                        psInsertarFact.setString(15, OC);
                        psInsertarFact.setInt(16, 0);

                        System.out.println("fact1" + psInsertarFact);
                        psInsertarFact.addBatch();

                        psActualiza = con.getConn().prepareStatement(ActualizaIdFactura);
                        psActualiza.setInt(1, 1);
                        psActualiza.setString(2, Clave);
                        psActualiza.setInt(3, FolioLote);
                        psActualiza.setString(4, UbicaLote);
                        psActualiza.execute();
                        psActualiza.clearParameters();

                    }

                
                    
                    
                    /**/
                    psActualizaIndice.executeBatch();
                    psActualizaLote.executeBatch();
                    psInsertarMov.executeBatch();
                    psInsertarFact.executeBatch();
                    psInsertarObs.executeBatch();

                      save = true;
                    con.getConn().commit();
                    
                   /*enviar correo de error en controlado*/
                   System.out.println("llamado controlado");
//                   pscorre_error.clearParameters();
                   pscorre_error = con.getConn().prepareStatement(callfolioincidente);
                   pscorre_error.setInt(1, FolioFactura);
                   rscorre_error = pscorre_error.executeQuery();
                    System.out.println(pscorre_error+"   "+ rscorre_error);
                    while (rscorre_error.next()) { 
                        System.out.println("comienza el ciclo controlado");
                        rf = rscorre_error.getInt(15);
                        ape = rscorre_error.getInt(16);
                        ctr = rscorre_error.getInt(17);
                        oncape = rscorre_error.getInt(18);
                        oncrf = rscorre_error.getInt(19);
                        System.out.println("redfria: "+rf + " /ape: " + ape + " /controlado: " + ctr);
                    if (ctr == 10 || ctr == 11 ) {
                          ContarV = ContarV + 1;
                    }else
                    if(rf == 10 || rf == 11){
                        ContarV = ContarV + 1;
                    }
                    else if(ape == 10 || ape == 11){
                        ContarV = ContarV + 1;
                    }
                   }
                    pscorre_error.close();
                    
                    System.out.println("ContarV: "+ ContarV);
                    if (ContarV > 0) {
                        System.out.println("si entre a correo PARA CONTROLADO");   
                        System.out.println("Folio que se manda: "+FolioFactura);
                    cfactinc.enviaCorreoFactIncidente(FolioFactura);   
                   
                    }
                    
                  
                    System.out.println("Terminó Folio Controlado Unidad= " + Unidad + " Con el Folio= " + FolioFactura);
                }
                
                ///////////////////***********ONCOAPE************************////////////////////
                if (cantSurONCAPE > 0) {
                   ContarV = 0;
                    System.out.println("si tengo oncoAPE");
                    psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
                    rsIndice = psBuscaIndice.executeQuery();
                    if (rsIndice.next()) {
                        FolioFactura = rsIndice.getInt(1);
                    }
                    if (UbicaModu == 14 || UbicaModu == 40 || UbicaModu == 41) {
                        psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_UNIDADFACTURA2FOLIO2ONCOAPE2);
                    } else {
                        psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_UNIDADFACTURA2FOLIO2ONCOAPE);
                    }
                    System.out.println("hola ONCO APE");
                    psBuscaDatosFact.setString(1, Unidad2);
                    psBuscaDatosFact.setString(2, Usuario);

                    System.out.println("datos a facturar onco APE: " + psBuscaDatosFact);
                    rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                    while (rsBuscaDatosFact.next()) {
                        Clave = rsBuscaDatosFact.getString(1);
                        F_Solicitado = rsBuscaDatosFact.getInt(2);
                        piezas = rsBuscaDatosFact.getInt(3);
                        FolioLote = rsBuscaDatosFact.getInt(4);
                        UbicaLote = rsBuscaDatosFact.getString(5);
                        ProyectoSelect = rsBuscaDatosFact.getInt(6);
                        ContratoSelect = rsBuscaDatosFact.getString(7);
                        Costo = rsBuscaDatosFact.getDouble(8);
                        IVA = rsBuscaDatosFact.getDouble(9);
                        Monto = rsBuscaDatosFact.getDouble(10);
                        OC = rsBuscaDatosFact.getString(11);
                        IdFact = rsBuscaDatosFact.getInt(12);
                        String tipOri = rsBuscaDatosFact.getString(13);
                        int F_IdLote = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;

                        Costo = 0.0;
                        Monto = 0.0;
                        IVA = 0.0;

                        DetalleFactura f = DetalleFactura.buildDetalleFactura(rsBuscaDatosFact);
                        f.setFecEnt(FecEnt);
                        oncoape.add(f);
                        psActualiza = con.getConn().prepareStatement(ActualizaIdFactura);
                        psActualiza.setInt(1, 1);
                        psActualiza.setString(2, Clave);
                        psActualiza.setInt(3, FolioLote);
                        psActualiza.setString(4, UbicaLote);
                        psActualiza.execute();
                        psActualiza.clearParameters();

                    }

                    //CREACION DE FOLIO ONCOAPE
                    if (oncoape.size() > 0) {
                        System.out.println("Creando folio onco APE, claves:" + oncoredfria.size());
                        psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
                        System.out.println("Consulta folioFactura " + psBuscaIndice);
                        rsIndice = psBuscaIndice.executeQuery();
                        if (rsIndice.next()) {
                            FolioFactura = rsIndice.getInt(1);
                        }
                        System.out.println("Indice Factura ONCOAPE: " + FolioFactura);
                        this.crearFolio(oncoape, UbicaDesc, Contrato, Usuario, FolioFactura, Unidad2, Tipos, Observaciones + " - ONCOAPE");
                        psActualizaIndice.setInt(1, FolioFactura + 1);
                        psActualizaIndice.execute();
                        foliosCreados.add(FolioFactura);
                    }

                    psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSporFACTEMP2FOLIOONCOAPE);
                    psBuscaDatosFact.setString(1, Unidad2);
                    psBuscaDatosFact.setInt(2, 0);
                    psBuscaDatosFact.setString(3, Usuario);
                    rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                    while (rsBuscaDatosFact.next()) {
                        Clave = rsBuscaDatosFact.getString(1);
                        F_Solicitado = rsBuscaDatosFact.getInt(2);
                        piezas = rsBuscaDatosFact.getInt(3);
                        FolioLote = rsBuscaDatosFact.getInt(4);
                        UbicaLote = rsBuscaDatosFact.getString(5);
                        ProyectoSelect = rsBuscaDatosFact.getInt(6);
                        ContratoSelect = rsBuscaDatosFact.getString(7);
                        Costo = rsBuscaDatosFact.getDouble(8);
                        IVA = rsBuscaDatosFact.getDouble(9);
                        Monto = rsBuscaDatosFact.getDouble(10);
                        OC = rsBuscaDatosFact.getString(11);

                        psInsertarFact.setInt(1, FolioFactura);
                        psInsertarFact.setString(2, Unidad2);
                        psInsertarFact.setString(3, Clave);
                        psInsertarFact.setInt(4, F_Solicitado);
                        psInsertarFact.setInt(5, 0);
                        psInsertarFact.setDouble(6, 0.00);
                        psInsertarFact.setDouble(7, 0.00);
                        psInsertarFact.setDouble(8, 0.00);
                        psInsertarFact.setInt(9, FolioLote);
                        psInsertarFact.setString(10, FecEnt);
                        psInsertarFact.setString(11, Usuario);
                        psInsertarFact.setString(12, UbicaLote);
                        psInsertarFact.setInt(13, ProyectoSelect);
                        psInsertarFact.setString(14, ContratoSelect);
                        psInsertarFact.setString(15, OC);
                        psInsertarFact.setInt(16, 0);

                        System.out.println("fact1" + psInsertarFact);
                        psInsertarFact.addBatch();

                        psActualiza = con.getConn().prepareStatement(ActualizaIdFactura);
                        psActualiza.setInt(1, 1);
                        psActualiza.setString(2, Clave);
                        psActualiza.setInt(3, FolioLote);
                        psActualiza.setString(4, UbicaLote);
                        psActualiza.execute();
                        psActualiza.clearParameters();

                    }

                    psActualizaIndice.executeBatch();
                    psActualizaLote.executeBatch();
                    psInsertarMov.executeBatch();
                    psInsertarFact.executeBatch();
                    psInsertarObs.executeBatch();

                    save = true;
                    con.getConn().commit();
                    
                         System.out.println("llamado oncoape");
                   pscorre_error = con.getConn().prepareStatement(callfolioincidente);
                   pscorre_error.setInt(1, FolioFactura);
                   rscorre_error = pscorre_error.executeQuery();
                    System.out.println(pscorre_error+"   "+ rscorre_error);
                    while (rscorre_error.next()) {
                        System.out.println("comienza el ciclo oncoape");
                        rf = rscorre_error.getInt(15);
                        ape = rscorre_error.getInt(16);
                        ctr = rscorre_error.getInt(17);
                        oncape = rscorre_error.getInt(18);
                        oncrf = rscorre_error.getInt(19);
                        System.out.println("redfria: "+rf + " /ape: " + ape + " /controlado: " + ctr);
                        if ( ctr == 10 || ctr == 11) {
                            ContarV = ContarV + 1 ;
                        }else if(rf == 10 || rf == 11){
                            ContarV = ContarV + 1 ;
                        }else if(ape == 10 || ape == 11){
                            ContarV = ContarV + 1 ;
                        }
                    }
                    pscorre_error.close();
                    System.out.println("ContarV: "+ ContarV);
                    if (ContarV > 0) {
                        System.out.println("si entre a correo PARA oncoape");   
                        System.out.println("Folio que se manda: "+FolioFactura);
                    cfactinc.enviaCorreoFactIncidente(FolioFactura);    
                    }
                    
                    System.out.println("Terminó Folio ONCOAPE Unidad= " + Unidad + " Con el Folio= " + FolioFactura);

                }
                
                  ///////////////////***********ONCORF************************////////////////////
                if (cantSurONCRF > 0) {
                    ContarV = 0;
                    System.out.println("si tengo oncoredfria");
                    psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
                    rsIndice = psBuscaIndice.executeQuery();
                    if (rsIndice.next()) {
                        FolioFactura = rsIndice.getInt(1);
                    }
                    if (UbicaModu == 14 || UbicaModu == 40 || UbicaModu == 41) {
                        psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_UNIDADFACTURA2FOLIO2ONCORF2);
                    } else {
                        psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_UNIDADFACTURA2FOLIO2ONCORF);
                    }
                    System.out.println("hola ONCO REDFRIA");
                    psBuscaDatosFact.setString(1, Unidad2);
                    psBuscaDatosFact.setString(2, Usuario);

                    System.out.println("datos a facturar onco rf: " + psBuscaDatosFact);
                    rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                    while (rsBuscaDatosFact.next()) {
                        Clave = rsBuscaDatosFact.getString(1);
                        F_Solicitado = rsBuscaDatosFact.getInt(2);
                        piezas = rsBuscaDatosFact.getInt(3);
                        FolioLote = rsBuscaDatosFact.getInt(4);
                        UbicaLote = rsBuscaDatosFact.getString(5);
                        ProyectoSelect = rsBuscaDatosFact.getInt(6);
                        ContratoSelect = rsBuscaDatosFact.getString(7);
                        Costo = rsBuscaDatosFact.getDouble(8);
                        IVA = rsBuscaDatosFact.getDouble(9);
                        Monto = rsBuscaDatosFact.getDouble(10);
                        OC = rsBuscaDatosFact.getString(11);
                        IdFact = rsBuscaDatosFact.getInt(12);
                        String tipOri = rsBuscaDatosFact.getString(13);
                        int F_IdLote = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;

                        Costo = 0.0;
                        Monto = 0.0;
                        IVA = 0.0;

                        DetalleFactura f = DetalleFactura.buildDetalleFactura(rsBuscaDatosFact);
                        f.setFecEnt(FecEnt);
                        oncoredfria.add(f);
                        psActualiza = con.getConn().prepareStatement(ActualizaIdFactura);
                        psActualiza.setInt(1, 1);
                        psActualiza.setString(2, Clave);
                        psActualiza.setInt(3, FolioLote);
                        psActualiza.setString(4, UbicaLote);
                        psActualiza.execute();
                        psActualiza.clearParameters();

                    }

                    //CREACION DE FOLIO ONCORF
                    if (oncoredfria.size() > 0) {
                        System.out.println("Creando folio onco, claves:" + oncoredfria.size());
                        psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
                        System.out.println("Consulta folioFactura " + psBuscaIndice);
                        rsIndice = psBuscaIndice.executeQuery();
                        if (rsIndice.next()) {
                            FolioFactura = rsIndice.getInt(1);
                        }
                        System.out.println("Indice Factura: " + FolioFactura);
                        this.crearFolio(oncoredfria, UbicaDesc, Contrato, Usuario, FolioFactura, Unidad2, Tipos, Observaciones + " - ONCORF");
                        psActualizaIndice.setInt(1, FolioFactura + 1);
                        psActualizaIndice.execute();
                        foliosCreados.add(FolioFactura);
                    }

                    psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSporFACTEMP2FOLIOONCORF);
                    psBuscaDatosFact.setString(1, Unidad2);
                    psBuscaDatosFact.setInt(2, 0);
                    psBuscaDatosFact.setString(3, Usuario);
                    rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                    while (rsBuscaDatosFact.next()) {
                        Clave = rsBuscaDatosFact.getString(1);
                        F_Solicitado = rsBuscaDatosFact.getInt(2);
                        piezas = rsBuscaDatosFact.getInt(3);
                        FolioLote = rsBuscaDatosFact.getInt(4);
                        UbicaLote = rsBuscaDatosFact.getString(5);
                        ProyectoSelect = rsBuscaDatosFact.getInt(6);
                        ContratoSelect = rsBuscaDatosFact.getString(7);
                        Costo = rsBuscaDatosFact.getDouble(8);
                        IVA = rsBuscaDatosFact.getDouble(9);
                        Monto = rsBuscaDatosFact.getDouble(10);
                        OC = rsBuscaDatosFact.getString(11);

                        psInsertarFact.setInt(1, FolioFactura);
                        psInsertarFact.setString(2, Unidad2);
                        psInsertarFact.setString(3, Clave);
                        psInsertarFact.setInt(4, F_Solicitado);
                        psInsertarFact.setInt(5, 0);
                        psInsertarFact.setDouble(6, 0.00);
                        psInsertarFact.setDouble(7, 0.00);
                        psInsertarFact.setDouble(8, 0.00);
                        psInsertarFact.setInt(9, FolioLote);
                        psInsertarFact.setString(10, FecEnt);
                        psInsertarFact.setString(11, Usuario);
                        psInsertarFact.setString(12, UbicaLote);
                        psInsertarFact.setInt(13, ProyectoSelect);
                        psInsertarFact.setString(14, ContratoSelect);
                        psInsertarFact.setString(15, OC);
                        psInsertarFact.setInt(16, 0);

                        System.out.println("fact1" + psInsertarFact);
                        psInsertarFact.addBatch();

                        psActualiza = con.getConn().prepareStatement(ActualizaIdFactura);
                        psActualiza.setInt(1, 1);
                        psActualiza.setString(2, Clave);
                        psActualiza.setInt(3, FolioLote);
                        psActualiza.setString(4, UbicaLote);
                        psActualiza.execute();
                        psActualiza.clearParameters();

                    }

                    psActualizaIndice.executeBatch();
                    psActualizaLote.executeBatch();
                    psInsertarMov.executeBatch();
                    psInsertarFact.executeBatch();
                    psInsertarObs.executeBatch();

                    save = true;
                    con.getConn().commit();
                    
                     System.out.println("llamado oncorf");
                   pscorre_error = con.getConn().prepareStatement(callfolioincidente);
                   pscorre_error.setInt(1, FolioFactura);
                   rscorre_error = pscorre_error.executeQuery();
                    System.out.println(pscorre_error+"   "+ rscorre_error);
                    while (rscorre_error.next()) {
                        System.out.println("comienza el ciclo oncorf");
                        rf = rscorre_error.getInt(15);
                        ape = rscorre_error.getInt(16);
                        ctr = rscorre_error.getInt(17);
                        oncape = rscorre_error.getInt(18);
                        oncrf = rscorre_error.getInt(19);
                        System.out.println("redfria: "+rf + " /ape: " + ape + " /controlado: " + ctr);
                        if ( ctr == 10 || ctr == 11) {
                            ContarV = ContarV + 1 ;
                        }else if(rf == 10 || rf == 11){
                            ContarV = ContarV + 1 ;
                        }else if(ape == 10 || ape == 11){
                            ContarV = ContarV + 1 ;
                        }
                    }
                    pscorre_error.close();
                    System.out.println("ContarV: "+ ContarV);
                    if (ContarV > 0) {
                        System.out.println("si entre a correo PARA oncorf");   
                        System.out.println("Folio que se manda: "+FolioFactura);
                    cfactinc.enviaCorreoFactIncidente(FolioFactura);    
                    }
                    System.out.println("Terminó Folio ONCORF Unidad= " + Unidad + " Con el Folio= " + FolioFactura);

                }

                //////////////////**********normal*******************************///////////////////
                if (CantSurT > 0) {
                System.out.println("si tengo NORMAL");
                ContarV = 0;
                    psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
                    rsIndice = psBuscaIndice.executeQuery();
//                    if (rsIndice.next()) {
//                        FolioFactura = rsIndice.getInt(1);
//                    }
//
//                    psActualizaIndice.setInt(1, FolioFactura + 1);
//                    psActualizaIndice.addBatch();
                    if (UbicaModu == 14 || UbicaModu == 40 || UbicaModu == 41) {

                        psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_UNIDADFACTURA2FOLIO23);
                    } else {
                        psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_UNIDADFACTURA2FOLIO2);
                    }
//                    psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_UNIDADFACTURA2FOLIO2);
                    psBuscaDatosFact.setString(1, Unidad2);
                    psBuscaDatosFact.setString(2, Usuario);
                    System.out.println(psBuscaDatosFact);
                    rsBuscaDatosFact = psBuscaDatosFact.executeQuery();

                    while (rsBuscaDatosFact.next()) {
                        Clave = rsBuscaDatosFact.getString(1);
                        F_Solicitado = rsBuscaDatosFact.getInt(2);
                        piezas = rsBuscaDatosFact.getInt(3);
                        FolioLote = rsBuscaDatosFact.getInt(4);
                        UbicaLote = rsBuscaDatosFact.getString(5);
                        ProyectoSelect = rsBuscaDatosFact.getInt(6);
                        ContratoSelect = rsBuscaDatosFact.getString(7);
                        Costo = rsBuscaDatosFact.getDouble(8);
                        IVA = rsBuscaDatosFact.getDouble(9);
                        Monto = rsBuscaDatosFact.getDouble(10);
                        OC = rsBuscaDatosFact.getString(11);
                        IdFact = rsBuscaDatosFact.getInt(12);
                        String tipOri = rsBuscaDatosFact.getString(13);
                        int F_IdLote = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;

                        Costo = 0.0;
                        Monto = 0.0;
                        IVA = 0.0;

                        DetalleFactura f = DetalleFactura.buildDetalleFactura(rsBuscaDatosFact);
                        f.setFecEnt(FecEnt);
                        normal.add(f);

                        psActualiza = con.getConn().prepareStatement(ActualizaIdFactura);
                        psActualiza.setInt(1, 1);
                        psActualiza.setString(2, Clave);
                        psActualiza.setInt(3, FolioLote);
                        psActualiza.setString(4, UbicaLote);
                        psActualiza.execute();
                        psActualiza.clearParameters();

                    }

                    //CREACION DE FOLIOS NORMAL
                    if (normal.size() > 0) {
                        System.out.println("Creando folio controlado, claves:" + normal.size());
                        psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, Proyecto));
                        System.out.println("Consulta folioFactura " + psBuscaIndice);
                        rsIndice = psBuscaIndice.executeQuery();
                        if (rsIndice.next()) {
                            FolioFactura = rsIndice.getInt(1);
                        }
                        System.out.println("Indice Factura: " + FolioFactura);
                        this.crearFolio(normal, UbicaDesc, Contrato, Usuario, FolioFactura, Unidad2, Tipos, Observaciones + " - NORMAL");
                        psActualizaIndice.setInt(1, FolioFactura + 1);
                        psActualizaIndice.execute();
                        foliosCreados.add(FolioFactura);
                    }

                    psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_DATOSporFACTEMP2FOLIO);
                    psBuscaDatosFact.setString(1, Unidad2);
                    psBuscaDatosFact.setInt(2, 0);
                    psBuscaDatosFact.setString(3, Usuario);
                    rsBuscaDatosFact = psBuscaDatosFact.executeQuery();
                    while (rsBuscaDatosFact.next()) {
                        Clave = rsBuscaDatosFact.getString(1);
                        F_Solicitado = rsBuscaDatosFact.getInt(2);
                        piezas = rsBuscaDatosFact.getInt(3);
                        FolioLote = rsBuscaDatosFact.getInt(4);
                        UbicaLote = rsBuscaDatosFact.getString(5);
                        ProyectoSelect = rsBuscaDatosFact.getInt(6);
                        ContratoSelect = rsBuscaDatosFact.getString(7);
                        Costo = rsBuscaDatosFact.getDouble(8);
                        IVA = rsBuscaDatosFact.getDouble(9);
                        Monto = rsBuscaDatosFact.getDouble(10);
                        OC = rsBuscaDatosFact.getString(11);

                        psInsertarFact.setInt(1, FolioFactura);
                        psInsertarFact.setString(2, Unidad2);
                        psInsertarFact.setString(3, Clave);
                        psInsertarFact.setInt(4, F_Solicitado);
                        psInsertarFact.setInt(5, 0);
                        psInsertarFact.setDouble(6, 0.00);
                        psInsertarFact.setDouble(7, 0.00);
                        psInsertarFact.setDouble(8, 0.00);
                        psInsertarFact.setInt(9, FolioLote);
                        psInsertarFact.setString(10, FecEnt);
                        psInsertarFact.setString(11, Usuario);
                        psInsertarFact.setString(12, UbicaLote);
                        psInsertarFact.setInt(13, ProyectoSelect);
                        psInsertarFact.setString(14, ContratoSelect);
                        psInsertarFact.setString(15, OC);
                        psInsertarFact.setInt(16, 0);
                        System.out.println("facttemp1: " + psInsertarFact);
                        psInsertarFact.addBatch();

                        psActualiza = con.getConn().prepareStatement(ActualizaIdFactura);
                        psActualiza.setInt(1, 1);
                        psActualiza.setString(2, Clave);
                        psActualiza.setInt(3, FolioLote);
                        psActualiza.setString(4, UbicaLote);
                        psActualiza.execute();
                        psActualiza.clearParameters();

                    }
                    
                     
                    /**/

//                    psActualizaIndice.executeBatch();
                    psActualizaLote.executeBatch();
                    psInsertarMov.executeBatch();
                    psInsertarFact.executeBatch();
                    psInsertarObs.executeBatch();

                     save = true;
                     con.getConn().commit();
                     
                    /*enviar correo de error en ape,rf,normal*/
                   System.out.println("llamado ape rdfria normal");
                   pscorre_error = con.getConn().prepareStatement(callfolioincidente);
                   pscorre_error.setInt(1, FolioFactura);
                   rscorre_error = pscorre_error.executeQuery();
                    System.out.println(pscorre_error+"   "+ rscorre_error);
                    while (rscorre_error.next()) {
                        System.out.println("comienza el ciclo no controlado");
                        rf = rscorre_error.getInt(15);
                        ape = rscorre_error.getInt(16);
                        ctr = rscorre_error.getInt(17);
                        oncape = rscorre_error.getInt(18);
                        oncrf = rscorre_error.getInt(19);
                        System.out.println("redfria: "+rf + " /ape: " + ape + " /controlado: " + ctr);
                        if ( ctr == 10 || ctr == 11) {
                            ContarV = ContarV + 1 ;
                        }else if(rf == 10 || rf == 11){
                            ContarV = ContarV + 1 ;
                        }else if(ape == 10 || ape == 11){
                            ContarV = ContarV + 1 ;
                        }
                    }
                    pscorre_error.close();
                    System.out.println("ContarV: "+ ContarV);
                    if (ContarV > 0) {
                        System.out.println("si entre a correo PARA APE O RF O NORMAL");   
                        System.out.println("Folio que se manda: "+FolioFactura);
                    cfactinc.enviaCorreoFactIncidente(FolioFactura);    
                    }

                   

                    System.out.println("Terminó Folio Normal Unidad= " + Unidad + " Con el Folio= " + FolioFactura);
                }

            }
            for (Integer f : foliosCreados) {
             this.insertarAbasto(Proyecto, f, Usuario);
/*AbastoService abasto = null;
abasto.crearAbastoWeb(FolioFactura,  Proyecto, Usuario);*/
            }

            con.getConn().commit();
            return save;
        } catch (SQLException ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex.getSQLState()), ex);
            try {
                save = false;
                con.getConn().rollback();
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex1);
            }
        } catch (Exception ex) {
            Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(FacturacionTranDaoImpl.class.getName()).log(Level.SEVERE, String.format("m: %s, sql: %s", ex.getMessage(), ex), ex);
            }
        }
        return save;
    }

    /**
     * **************************************************************************************************************************************************************
     */
    private int insertarFacturaTemp(ResultSet rsBuscaExiFol, int piezas, int FolioFactura, String Unidad2, String clave, int F_Solicitado, String fecEnt, String usuario,
            String observaciones, int proyecto, String contrato, String oc, int contar, boolean ceros) throws SQLException {
        int F_IdLote, F_ExiLot, F_FolLot, Tipo, ClaProve;
        double Costo, IVA;
        String Ubicacion;
        while (rsBuscaExiFol.next()) {
            F_IdLote = rsBuscaExiFol.getInt(1);
            F_ExiLot = rsBuscaExiFol.getInt(7);
            F_FolLot = rsBuscaExiFol.getInt(3);
            Tipo = rsBuscaExiFol.getInt(4);
            Costo = rsBuscaExiFol.getDouble(5);
            Ubicacion = rsBuscaExiFol.getString(6);
            ClaProve = rsBuscaExiFol.getInt(8);
            if (Tipo == 2504) {
                IVA = 0.0;
            } else {
                IVA = 0.16;
            }

            Costo = 0.0;

            int diferencia = 0;
            double IVAPro = 0, Monto = 0, MontoIva = 0;
            if ((F_ExiLot >= piezas) && (piezas > 0)) {
                contar = contar - 1;
                diferencia = F_ExiLot - piezas;
//                CanSur = piezas;
                IVAPro = (piezas * Costo) * IVA;
                Monto = piezas * Costo;
                MontoIva = Monto + IVAPro;
                psInsertarFactTemp.setInt(1, FolioFactura);
                psInsertarFactTemp.setString(2, Unidad2);
                psInsertarFactTemp.setString(3, clave);
                psInsertarFactTemp.setInt(4, F_Solicitado);
                psInsertarFactTemp.setInt(5, piezas);
                psInsertarFactTemp.setDouble(6, Costo);
                psInsertarFactTemp.setDouble(7, IVAPro);
                psInsertarFactTemp.setDouble(8, MontoIva);
                psInsertarFactTemp.setInt(9, F_FolLot);
                psInsertarFactTemp.setString(10, fecEnt);
                psInsertarFactTemp.setString(11, usuario);
                psInsertarFactTemp.setString(12, Ubicacion);
                psInsertarFactTemp.setString(13, observaciones);
                psInsertarFactTemp.setInt(14, proyecto);
                psInsertarFactTemp.setString(15, contrato);
                psInsertarFactTemp.setString(16, oc);
                psInsertarFactTemp.setInt(17, 0);
                psInsertarFactTemp.addBatch();

                piezas = 0;
                F_Solicitado = 0;
                return F_Solicitado;

            } else if ((piezas > 0) && (F_ExiLot > 0)) {
                int DifeSol = 0;
                contar = contar - 1;
                diferencia = piezas - F_ExiLot;
                int surtido = F_ExiLot;
                if (F_ExiLot >= F_Solicitado) {
                    DifeSol = F_Solicitado;
                } else if (contar >= 0) {
                    DifeSol = F_ExiLot;
                } else {
                    DifeSol = F_Solicitado - F_ExiLot;
                }

                IVAPro = (surtido * Costo) * IVA;
                Monto = surtido * Costo;
                MontoIva = Monto + IVAPro;

                psInsertarFactTemp.setInt(1, FolioFactura);
                psInsertarFactTemp.setString(2, Unidad2);
                psInsertarFactTemp.setString(3, clave);
                psInsertarFactTemp.setInt(4, DifeSol);
                psInsertarFactTemp.setInt(5, surtido);
                psInsertarFactTemp.setDouble(6, Costo);
                psInsertarFactTemp.setDouble(7, IVAPro);
                psInsertarFactTemp.setDouble(8, MontoIva);
                psInsertarFactTemp.setInt(9, F_FolLot);
                psInsertarFactTemp.setString(10, fecEnt);
                psInsertarFactTemp.setString(11, usuario);
                psInsertarFactTemp.setString(12, Ubicacion);
                psInsertarFactTemp.setString(13, observaciones);
                psInsertarFactTemp.setInt(14, proyecto);
                psInsertarFactTemp.setString(15, contrato);
                psInsertarFactTemp.setString(16, oc);
                psInsertarFactTemp.setInt(17, 0);
                psInsertarFactTemp.addBatch();

                F_Solicitado = F_Solicitado - surtido;

                piezas = piezas - surtido;
            }
            if (contar == 0 && ceros) {
                if (F_Solicitado > 0) {
                    psInsertarFactTemp.setInt(1, FolioFactura);
                    psInsertarFactTemp.setString(2, Unidad2);
                    psInsertarFactTemp.setString(3, clave);
                    psInsertarFactTemp.setInt(4, F_Solicitado);
                    psInsertarFactTemp.setInt(5, 0);
                    psInsertarFactTemp.setDouble(6, Costo);
                    psInsertarFactTemp.setDouble(7, IVAPro);
                    psInsertarFactTemp.setDouble(8, MontoIva);
                    psInsertarFactTemp.setInt(9, F_FolLot);
                    psInsertarFactTemp.setString(10, fecEnt);
                    psInsertarFactTemp.setString(11, usuario);
                    psInsertarFactTemp.setString(12, Ubicacion);
                    psInsertarFactTemp.setString(13, observaciones);
                    psInsertarFactTemp.setInt(14, proyecto);
                    psInsertarFactTemp.setString(15, contrato);
                    psInsertarFactTemp.setString(16, oc);
                    psInsertarFactTemp.setInt(17, 0);
                    psInsertarFactTemp.addBatch();
                    return F_Solicitado;
                }
            }

        }
        return F_Solicitado;
    }

    private void crearFolio(List<DetalleFactura> detalles, String ubicaDesc, String catalogo, String usuario, int folioFactura, String unidad2, String tipos, String observaciones) throws Exception {
        int proyecto = 0;
        for (DetalleFactura detalle : detalles) {
            String clave = detalle.getClave();
            int folioLote = detalle.getFolioLote();
            int piezas = detalle.getPiezas();
            String ubicaLote = detalle.getUbicaLote();
            int proyectoSelect = detalle.getProyectoSelect();
            proyecto = proyectoSelect;
            double costo = detalle.getCosto();
            double monto = detalle.getMonto();
            double iva = detalle.getIva();
            String fecEnt = detalle.getFecEnt();
            String contratoSelect = detalle.getContratoSelect();
            String oc = detalle.getOc();
            int solicitado = detalle.getSolicitado();
            int F_IdLote = 0, F_ExiLot = 0, diferencia = 0, CanSur = 0, ClaProve = 0;
            System.out.println("Folio= " + folioFactura + " Clave: " + clave);
            psBuscaExiFol = con.getConn().prepareStatement(BUSCA_EXILOTE);
            psBuscaExiFol.setString(1, clave);
            psBuscaExiFol.setInt(2, folioLote);
            psBuscaExiFol.setString(3, ubicaLote);
            psBuscaExiFol.setInt(4, proyectoSelect);

            System.out.println("BuscaExistenciaDetalle=" + psBuscaExiFol);
            rsBuscaExiFol = psBuscaExiFol.executeQuery();
            while (rsBuscaExiFol.next()) {
                F_IdLote = rsBuscaExiFol.getInt(1);
                F_ExiLot = rsBuscaExiFol.getInt(2);
                ClaProve = rsBuscaExiFol.getInt(3);

                if ((F_ExiLot >= piezas) && (piezas > 0)) {
                    diferencia = F_ExiLot - piezas;
                    CanSur = piezas;

                    psActualizaLote.setInt(1, diferencia);
                    psActualizaLote.setInt(2, F_IdLote);
                    System.out.println("ActualizaLote=" + psActualizaLote + " Clave=" + clave);
                    psActualizaLote.addBatch();

                    psInsertarMov.setInt(1, folioFactura);
                    psInsertarMov.setInt(2, 51);
                    psInsertarMov.setString(3, clave);
                    psInsertarMov.setInt(4, CanSur);
                    psInsertarMov.setDouble(5, costo);
                    psInsertarMov.setDouble(6, monto);
                    psInsertarMov.setString(7, "-1");
                    psInsertarMov.setInt(8, folioLote);
                    psInsertarMov.setString(9, ubicaLote);
                    psInsertarMov.setInt(10, ClaProve);
                    psInsertarMov.setString(11, usuario);
                    System.out.println("Mov1" + psInsertarMov);
                    psInsertarMov.addBatch();

                    psInsertarFact.setInt(1, folioFactura);
                    psInsertarFact.setString(2, unidad2);
                    psInsertarFact.setString(3, clave);
                    psInsertarFact.setInt(4, solicitado);
                    psInsertarFact.setInt(5, CanSur);
                    psInsertarFact.setDouble(6, costo);
                    psInsertarFact.setDouble(7, iva);
                    psInsertarFact.setDouble(8, monto);
                    psInsertarFact.setInt(9, folioLote);
                    psInsertarFact.setString(10, fecEnt);
                    psInsertarFact.setString(11, usuario);
                    psInsertarFact.setString(12, ubicaLote);
                    psInsertarFact.setInt(13, proyectoSelect);
                    psInsertarFact.setString(14, contratoSelect);
                    psInsertarFact.setString(15, oc);
                    psInsertarFact.setInt(16, 0);
                    System.out.println("fact1" + psInsertarFact);
                    psInsertarFact.addBatch();

                    piezas = 0;
                    solicitado = 0;
                    break;

                } else if ((piezas > 0) && (F_ExiLot > 0)) {
                    diferencia = piezas - F_ExiLot;
                    CanSur = F_ExiLot;

                    psActualizaLote.setInt(1, 0);
                    psActualizaLote.setInt(2, F_IdLote);
                    System.out.println("ActualizaLote2=" + psActualizaLote + " Clave=" + clave);
                    psActualizaLote.addBatch();

                    psInsertarMov.setInt(1, folioFactura);
                    psInsertarMov.setInt(2, 51);
                    psInsertarMov.setString(3, clave);
                    psInsertarMov.setInt(4, CanSur);
                    psInsertarMov.setDouble(5, costo);
                    psInsertarMov.setDouble(6, monto);
                    psInsertarMov.setString(7, "-1");
                    psInsertarMov.setInt(8, folioLote);
                    psInsertarMov.setString(9, ubicaLote);
                    psInsertarMov.setInt(10, ClaProve);
                    psInsertarMov.setString(11, usuario);
                    System.out.println("Mov2" + psInsertarMov);
                    psInsertarMov.addBatch();

                    psInsertarFact.setInt(1, folioFactura);
                    psInsertarFact.setString(2, unidad2);
                    psInsertarFact.setString(3, clave);
                    psInsertarFact.setInt(4, solicitado);
                    psInsertarFact.setInt(5, CanSur);
                    psInsertarFact.setDouble(6, costo);
                    psInsertarFact.setDouble(7, iva);
                    psInsertarFact.setDouble(8, monto);
                    psInsertarFact.setInt(9, folioLote);
                    psInsertarFact.setString(10, fecEnt);
                    psInsertarFact.setString(11, usuario);
                    psInsertarFact.setString(12, ubicaLote);
                    psInsertarFact.setInt(13, proyectoSelect);
                    psInsertarFact.setString(14, contratoSelect);
                    psInsertarFact.setString(15, oc);
                    psInsertarFact.setInt(16, 0);
                    System.out.println("fact2" + psInsertarFact);
                    psInsertarFact.addBatch();

                    solicitado = solicitado - CanSur;

                    piezas = piezas - CanSur;
                    F_ExiLot = 0;

                } else if ((piezas == 0) && (F_ExiLot == 0)) {

                    psInsertarFact.setInt(1, folioFactura);
                    psInsertarFact.setString(2, unidad2);
                    psInsertarFact.setString(3, clave);
                    psInsertarFact.setInt(4, solicitado);
                    psInsertarFact.setInt(5, 0);
                    psInsertarFact.setDouble(6, 0.00);
                    psInsertarFact.setDouble(7, 0.00);
                    psInsertarFact.setDouble(8, 0.00);
                    psInsertarFact.setInt(9, folioLote);
                    psInsertarFact.setString(10, fecEnt);
                    psInsertarFact.setString(11, usuario);
                    psInsertarFact.setString(12, ubicaLote);
                    psInsertarFact.setInt(13, proyectoSelect);
                    psInsertarFact.setString(14, contratoSelect);
                    psInsertarFact.setString(15, oc);
                    psInsertarFact.setInt(16, 0);
                    System.out.println("fact2" + psInsertarFact);
                    psInsertarFact.addBatch();
                }

            }

        }
        psInsertarObs.setInt(1, folioFactura);
        psInsertarObs.setString(2, observaciones);
        psInsertarObs.setString(3, tipos);
        psInsertarObs.setInt(4, proyecto);
        psInsertarObs.addBatch();
    }

    /**
     * Inserta el abasto del folio de remisión.
     *
     * @param proyecto proyecto de la remision
     * @param folioFactura folio de la remision
     * @param usuario usuario que remisiona
     */
    private void insertarAbasto(int proyecto, int folioFactura, String usuario) throws SQLException {
        psAbastoInsert = con.getConn().prepareStatement(InsertAbasto);
        psAbasto = con.getConn().prepareStatement(DatosAbasto);
        psAbasto.setInt(1, proyecto);
        psAbasto.setInt(2, folioFactura);
        rsAbasto = psAbasto.executeQuery();
        while (rsAbasto.next()) {
            int factorEmpaque = 1;
            int folLot = rsAbasto.getInt("LOTE");
            PreparedStatement psfe = con.getConn().prepareStatement(getFactorEmpaque);
            psfe.setInt(1, folLot);
            ResultSet rsfe = psfe.executeQuery();
            if (rsfe.next()) {
                factorEmpaque = rsfe.getInt("factor");
            }
            psAbastoInsert.setString(1, rsAbasto.getString(1));
            psAbastoInsert.setString(2, rsAbasto.getString(2));
            psAbastoInsert.setString(3, rsAbasto.getString(3));
            psAbastoInsert.setString(4, rsAbasto.getString(4));
            psAbastoInsert.setString(5, rsAbasto.getString(5));
            psAbastoInsert.setString(6, rsAbasto.getString(6));
            psAbastoInsert.setString(7, rsAbasto.getString(7));
            psAbastoInsert.setString(8, rsAbasto.getString(8));
            psAbastoInsert.setString(9, rsAbasto.getString(12));
            psAbastoInsert.setString(10, rsAbasto.getString(10));
            psAbastoInsert.setString(11, usuario);
            psAbastoInsert.setInt(12, factorEmpaque);
            psAbastoInsert.addBatch();
        }

        psAbastoInsert.executeBatch();
    }

    /**
     * Inserta el abasto del registro de tb_factura que se inserta
     *
     * @param idFactura Id del registro de factura del que se va a generar el
     * abasto
     * @param usuario usuario que remisiona
     */
    private void insertarAbasto(int idFactura, String usuario) throws SQLException {
        psAbastoInsert = con.getConn().prepareStatement(InsertAbasto);
        psAbasto = con.getConn().prepareStatement(DatosAbastoByFacturaId);
        psAbasto.setInt(1, idFactura);
        rsAbasto = psAbasto.executeQuery();
        while (rsAbasto.next()) {
            int factorEmpaque = 1;
            int folLot = rsAbasto.getInt("LOTE");
            PreparedStatement psfe = con.getConn().prepareStatement(getFactorEmpaque);
            psfe.setInt(1, folLot);
            ResultSet rsfe = psfe.executeQuery();
            if (rsfe.next()) {
                factorEmpaque = rsfe.getInt("factor");
            }
            psAbastoInsert.setString(1, rsAbasto.getString(1));
            psAbastoInsert.setString(2, rsAbasto.getString(2));
            psAbastoInsert.setString(3, rsAbasto.getString(3));
            psAbastoInsert.setString(4, rsAbasto.getString(4));
            psAbastoInsert.setString(5, rsAbasto.getString(5));
            psAbastoInsert.setString(6, rsAbasto.getString(6));
            psAbastoInsert.setString(7, rsAbasto.getString(7));
            psAbastoInsert.setString(8, rsAbasto.getString(8));
            psAbastoInsert.setString(9, rsAbasto.getString(12));
            psAbastoInsert.setString(10, rsAbasto.getString(10));
            psAbastoInsert.setString(11, usuario);
            psAbastoInsert.setInt(12, factorEmpaque);
            psAbastoInsert.addBatch();
        }

        psAbastoInsert.executeBatch();
    }

    private boolean crearFolioFonsabi(String ubicaciones, String unidad, String usuario, Integer proyecto, String FecEnt, String Contrato, String Observaciones, String Tipos, List<Integer> foliosCreados) throws SQLException, Exception {
        psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, proyecto));
        rsIndice = psBuscaIndice.executeQuery();

        psBuscaDatosFact = con.getConn().prepareStatement(BUSCA_UNIDADFACTURA2FOLIO2);
        psBuscaDatosFact.setString(1, unidad);
        psBuscaDatosFact.setString(2, usuario);
        System.out.println(psBuscaDatosFact);
        rsBuscaDatosFact = psBuscaDatosFact.executeQuery();

        String Clave, UbicaLote, ContratoSelect, OC;
        Integer F_Solicitado, FolioLote, ProyectoSelect, FolioFactura = 0;
        List<DetalleFactura> normal = new ArrayList<DetalleFactura>();

        while (rsBuscaDatosFact.next()) {
            Clave = rsBuscaDatosFact.getString(1);
            F_Solicitado = rsBuscaDatosFact.getInt(2);
            FolioLote = rsBuscaDatosFact.getInt(4);
            UbicaLote = rsBuscaDatosFact.getString(5);
            ProyectoSelect = rsBuscaDatosFact.getInt(6);
            ContratoSelect = rsBuscaDatosFact.getString(7);
            OC = rsBuscaDatosFact.getString(11);

            DetalleFactura f = DetalleFactura.buildDetalleFactura(rsBuscaDatosFact);
            f.setFecEnt(FecEnt);
            normal.add(f);

            psActualiza = con.getConn().prepareStatement(ActualizaIdFactura);
            psActualiza.setInt(1, 1);
            psActualiza.setString(2, Clave);
            psActualiza.setInt(3, FolioLote);
            psActualiza.setString(4, UbicaLote);
            psActualiza.execute();
            psActualiza.clearParameters();

        }

        //CREACION DE FOLIOS NORMAL
        if (normal.size() > 0) {
            System.out.println("Creando folio FONSABI, claves:" + normal.size());
            psBuscaIndice = con.getConn().prepareStatement(String.format(BUSCA_INDICEFACT, proyecto));
            System.out.println("Consulta folioFactura " + psBuscaIndice);
            rsIndice = psBuscaIndice.executeQuery();
            if (rsIndice.next()) {
                FolioFactura = rsIndice.getInt(1);
            }
            System.out.println("Indice Factura: " + FolioFactura);
            this.crearFolio(normal, ubicaciones, Contrato, usuario, FolioFactura, unidad, Tipos, Observaciones + " - FONSABI");
            psActualizaIndice.setInt(1, FolioFactura + 1);
            psActualizaIndice.execute();
            foliosCreados.add(FolioFactura);
        }

//                    psActualizaIndice.executeBatch();
        psActualizaLote.executeBatch();
        psInsertarMov.executeBatch();
        psInsertarFact.executeBatch();
        psInsertarObs.executeBatch();

        System.out.println("Terminó Folio FONSABI Unidad= " + unidad + " Con el Folio= " + FolioFactura);
        return true;
    }
}
