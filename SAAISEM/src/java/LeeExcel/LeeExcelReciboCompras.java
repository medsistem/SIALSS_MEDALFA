/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LeeExcel;

import conn.ConectionDB;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author MEDALFA
 */
public class LeeExcelReciboCompras {

    private Vector vectorDataExcelXLSX = new Vector();

    public boolean obtieneArchivo(String path, String file, String Usuario, String Proyecto, String Campo, String IdUsu) {

        String excelXLSXFileName = path + "/exceles/" + file;
        vectorDataExcelXLSX = readDataExcelXLSX(excelXLSXFileName);
        displayDataExcelXLSX(vectorDataExcelXLSX, Usuario, Proyecto, Campo, IdUsu);
        return true;
    }

    public Vector readDataExcelXLSX(String fileName) {
        Vector vectorData = new Vector();

        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);

            XSSFWorkbook xssfWorkBook = new XSSFWorkbook(fileInputStream);

            // Read data at sheet 0
            XSSFSheet xssfSheet = xssfWorkBook.getSheetAt(0);

            Iterator rowIteration = xssfSheet.rowIterator();

            // Looping every row at sheet 0
            while (rowIteration.hasNext()) {
                XSSFRow xssfRow = (XSSFRow) rowIteration.next();
                Iterator cellIteration = xssfRow.cellIterator();

                Vector vectorCellEachRowData = new Vector();

                // Looping every cell in each row at sheet 0
                while (cellIteration.hasNext()) {
                    XSSFCell xssfCell = (XSSFCell) cellIteration.next();
                    vectorCellEachRowData.addElement(xssfCell);
                }

                vectorData.addElement(vectorCellEachRowData);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return vectorData;
    }

    @SuppressWarnings("empty-statement")
    public void displayDataExcelXLSX(Vector vectorData, String User, String Proyecto, String Campo, String IdUsu) {
        // Looping every row data in vector
        DateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df3 = new SimpleDateFormat("dd/MM/yyyy");

        ConectionDB con = new ConectionDB();
        try {
            con.conectar();
            con.actualizar("DELETE FROM tb_cargaocisem WHERE F_Usu='" + User + "'");
            con.cierraConexion();
        } catch (Exception e) {
            Logger.getLogger("LeeExcelReciboCompras").log(Level.SEVERE, null, e);
        }

        for (Object vectorData1 : vectorData) {
            Vector vectorCellEachRowData = (Vector) vectorData1;
            String qry = "INSERT INTO tb_cargaocisem VALUES (";
            // looping every cell in each row
            try {
                con.conectar();
                String NoOc = "";
                ResultSet Consulta = con.consulta("SELECT CONCAT('OC-',SUBSTR( F_NoCompra, 4, LENGTH(F_NoCompra)) + 1) FROM tb_pedidoisem2017 ORDER BY F_IdIsem DESC LIMIT 1;");
                if (Consulta.next()) {
                    NoOc = Consulta.getString(1);
                }
                for (int j = 0; j < 3; j++) {

                    switch (j) {
                        /*case 0:
                            try {
                                String NoOrden = (vectorCellEachRowData.get(j).toString().trim() + "");

                                NoOrden = NoOrden.replaceAll("^\\s*", "");
                                NoOrden = NoOrden.replaceAll(" ", "");
                                NoOrden = NoOrden.replaceAll("&nbsp;", "");

                                qry = qry + "'" + NoOrden + "', ";
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                            break;*/
                        case 0:
                            try {
                                String Cliente = (vectorCellEachRowData.get(j).toString() + "");

                                Cliente = Cliente.replaceAll("^\\s*", "");
                                Cliente = Cliente.replaceAll(" ", "");
                                Cliente = Cliente.replaceAll("&nbsp;", "");

                                qry = qry + "'" + Cliente + "', ";
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        case 1:
                            try {
                                String Nombre = (vectorCellEachRowData.get(j).toString() + "").trim();
                                qry = qry + "'" + Nombre + "', ";
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        case 2:
                            try {
                                String ClaPro = ((vectorCellEachRowData.get(j).toString()) + "");
                                qry = qry + "'" + ClaPro + "' ,'" + ClaPro + "' ";

                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                            break;

                        default:
                            break;
                    }
                }
                String Solicitado = "", FechaCap = "", FechaEnt = "", Evento = "", Zona = "", Tipo = "";
                Solicitado = ((vectorCellEachRowData.get(3).toString()) + "");
                if (Solicitado.equals("Cantidad")) {
                    Solicitado = "0";
                }

                try {
                    FechaCap = (vectorCellEachRowData.get(4).toString());
                    if (FechaCap.equals("Fecha captura")) {
                        FechaCap = "01/01/2000";
                    }
                    FechaCap = df2.format(df1.parse(FechaCap));

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    try {
                        FechaCap = df2.format(df3.parse(FechaCap));
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }

                try {
                    FechaEnt = (vectorCellEachRowData.get(5).toString());
                    if (FechaEnt.equals("Fecha Entrega")) {
                        FechaEnt = "01/01/2000";
                    }
                    FechaEnt = df2.format(df1.parse(FechaEnt));

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    try {
                        FechaEnt = df2.format(df3.parse(FechaEnt));
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }

                /*FechaCap = ((vectorCellEachRowData.get(5).toString()) + "");
                FechaEnt = ((vectorCellEachRowData.get(6).toString()) + "");*/
                Evento = ((vectorCellEachRowData.get(6).toString()) + "");
                Zona = ((vectorCellEachRowData.get(7).toString()) + "");
                Tipo = ((vectorCellEachRowData.get(8).toString()) + "");

                qry = qry + ",'" + Solicitado + "','" + User + "','" + Proyecto + "','" + FechaCap + "','" + FechaEnt + "','" + Evento + "','" + Zona + "','" + Tipo + "','" + NoOc + "',0,0,0,0,0)"; // agregar campos fuera del excel
                System.out.println(qry);
                con.insertar(qry);
                con.cierraConexion();
            } catch (Exception e) {
                //System.out.println(qry);
                Logger.getLogger("LeeExcelReciboCompras").log(Level.SEVERE, null, e);
            }
        }

        try {
            con.conectar();
            int Contar = 0, Contar3 = 0, Contar4 = 0, ContarRLP = 0, ContarLP = 0, DIFMAX = 0, DIFENT = 0, BanMax = 0, BanEnt = 0, IdProveedor = 0;
            String Proveedor = "";
            con.actualizar("DELETE FROM tb_cargaocisem WHERE F_Clave='clave' AND F_Usu='" + User + "';");

            ResultSet ConsultaP = con.consulta("SELECT F_NombreProvee FROM tb_cargaocisem WHERE F_Usu= '" + User + "' AND F_Proveedor='' GROUP BY F_NombreProvee;");
            while (ConsultaP.next()) {
                Proveedor = ConsultaP.getString(1);

                if (Proveedor != "") {
                    ConsultaP = con.consulta("SELECT F_ClaProve FROM tb_proveedor WHERE F_NomPro='" + Proveedor + "';");
                    if (ConsultaP.next()) {
                        IdProveedor = ConsultaP.getInt(1);
                    }
                    if (IdProveedor > 0) {
                        con.actualizar("UPDATE tb_cargaocisem SET F_Proveedor = '" + IdProveedor + "' WHERE F_NombreProvee='" + Proveedor + "' AND F_Usu='" + User + "' AND F_Proveedor='';");
                    } else {
                        con.insertar("INSERT INTO tb_proveedor VALUES (0,'" + Proveedor + "','','','','','','','','','');");
                        ConsultaP = con.consulta("SELECT F_ClaProve FROM tb_proveedor WHERE F_NomPro='" + Proveedor + "';");
                        if (ConsultaP.next()) {
                            IdProveedor = ConsultaP.getInt(1);
                        }
                        con.actualizar("UPDATE tb_cargaocisem SET F_Proveedor = '" + IdProveedor + "' WHERE F_NombreProvee='" + Proveedor + "' AND F_Usu='" + User + "' AND F_Proveedor='';");
                    }
                }
            }

            ResultSet Consulta2 = con.consulta("SELECT COUNT(*) AS contar FROM tb_cargaocisem WHERE ( F_Tipo != 'CC' AND F_Tipo != 'LP' ) AND F_Usu = '" + User + "';");
            if (Consulta2.next()) {
                Contar3 = Consulta2.getInt(1);
            }

            if (Contar3 == 0) {

                Consulta2 = con.consulta("SELECT COUNT(*) AS contar FROM tb_cargaocisem WHERE F_Tipo = 'LP'  AND F_Usu = '" + User + "';");
                if (Consulta2.next()) {
                    Contar4 = Consulta2.getInt(1);
                }
                if (Contar4 > 0) {
                    Consulta2 = con.consulta("SELECT COUNT(*) AS contar FROM tb_cargaocisem WHERE ( F_Zona != 'ZONA NORTE' AND F_Zona != 'ZONA ORIENTE' AND F_Zona != 'ZONA PONIENTE' AND F_Zona != 'SOLUCIONES' ) AND F_Tipo = 'LP'  AND F_Usu = '" + User + "';");
                    if (Consulta2.next()) {
                        Contar = Consulta2.getInt(1);
                    }
                }
                if (Contar == 0) {
                    con.actualizar("DELETE FROM tb_cargaocisem WHERE F_NoOc='Oc' AND F_Usu = '" + User + "';");

                    if (Contar4 > 0) {
                        con.actualizar("UPDATE tb_cargaocisem C INNER JOIN tb_prodprov P ON C.F_Proveedor = P.F_ClaProve AND C.F_Zona = P.F_Zona AND C.F_Clave = P.F_ClaPro SET C.F_ProblemaLP = 1 WHERE C.F_Tipo = 'LP' AND C.F_Usu = '" + User + "';");
                        Consulta2 = con.consulta("SELECT COUNT(*), SUM(F_ProblemaLP) FROM tb_cargaocisem O WHERE F_Tipo = 'LP' AND F_Usu = '" + User + "';");
                        while (Consulta2.next()) {
                            ContarRLP = Consulta2.getInt(1);
                            ContarLP = Consulta2.getInt(2);
                        }
                    }

                    if (ContarRLP == ContarLP) {

                        Consulta2 = con.consulta("SELECT C.F_Proveedor, C.F_Clave, C.F_Cant, C.F_Zona, P.F_CantMin, P.F_CantMax, P.F_CantMax - C.F_Cant AS DIFMAX, IFNULL(PD.F_Cant, 0) AS CANTPD, (( P.F_CantMax - IFNULL(PD.F_Cant, 0)) - C.F_Cant ) AS DIFENT FROM tb_cargaocisem C INNER JOIN tb_prodprov P ON C.F_Proveedor = P.F_ClaProve AND C.F_Clave = P.F_ClaPro AND C.F_Zona = P.F_Zona LEFT JOIN ( SELECT F_Provee, F_Clave, F_Zona, SUM(F_Cant) AS F_Cant FROM tb_pedidoisem2017 WHERE F_Tipo = 'LP' AND F_StsPed != 2 GROUP BY F_Provee, F_Clave, F_Zona ) AS PD ON C.F_Proveedor = PD.F_Provee AND C.F_Clave = PD.F_Clave AND C.F_Zona = PD.F_Zona WHERE C.F_Tipo = 'LP' AND C.F_Usu = '" + User + "' GROUP BY C.F_Proveedor, C.F_Clave, C.F_Zona;");
                        while (Consulta2.next()) {
                            DIFMAX = Consulta2.getInt(7);
                            DIFENT = Consulta2.getInt(9);

                            if (DIFMAX < 0) {
                                BanMax++;
                            }
                            if (DIFENT < 0) {
                                BanEnt++;
                            }
                        }

                        if (BanEnt == 0) {

                            con.actualizar("UPDATE tb_cargaocisem O INNER JOIN tb_proveedor P ON O.F_Proveedor = P.F_ClaProve SET O.F_ProblemaProvee=1 WHERE O.F_UsU='" + User + "';");
                            con.actualizar("UPDATE tb_cargaocisem O INNER JOIN tb_medica M ON O.F_Clave = M.F_ClaPro SET O.F_Clavess = M.F_ClaProSS WHERE O.F_Usu='" + User + "' AND " + Campo + " = 1;");
                            con.actualizar("UPDATE tb_cargaocisem O INNER JOIN tb_medica M ON O.F_Clave = M.F_ClaPro AND O.F_Clavess = M.F_ClaProSS SET O.F_ProblemaClave=1 WHERE O.F_Usu='" + User + "' AND " + Campo + " = 1;");
                            con.actualizar("UPDATE tb_cargaocisem O INNER JOIN tb_proyectos P ON O.F_Proyecto = P.F_Id SET O.F_ProblemaProyecto = 1 WHERE O.F_Usu='" + User + "';");

                            ResultSet Consulta = con.consulta("SELECT F_NoOc, F_Proveedor, P.F_ClaProve, COUNT(*), SUM(F_ProblemaProvee), SUM(F_ProblemaClave),SUM(O.F_ProblemaProyecto),O.F_Proyecto FROM tb_cargaocisem O INNER JOIN tb_proveedor P ON O.F_Proveedor = P.F_ClaProve INNER JOIN tb_proyectos PR ON O.F_Proyecto=PR.F_Id WHERE F_Usu = '" + User + "' GROUP BY F_NoOc, F_Proveedor,O.F_Proyecto;");
                            while (Consulta.next()) {
                                int NoReg = Consulta.getInt(4);
                                int NoProvee = Consulta.getInt(5);
                                int NoClave = Consulta.getInt(6);
                                int NoProyecto = Consulta.getInt(7);

                                if ((NoReg == NoClave) && (NoReg == NoProvee) && (NoReg == NoProyecto)) {
                                    con.actualizar("DELETE FROM tb_pedidoisem2017 WHERE F_NoCompra='" + Consulta.getString(1) + "' AND F_Provee='" + Consulta.getString(3) + "' AND F_Proyecto='" + Consulta.getString(8) + "';");

                                    ResultSet Claves = con.consulta("SELECT F_NoOc, P.F_ClaProve, F_Clave, F_Clavess, SUM(F_Cant) AS F_Cant, F_Proyecto, O.F_FecCap, O.F_FecEnt, O.F_Evento, O.F_Zona, O.F_Tipo FROM tb_cargaocisem O INNER JOIN tb_proveedor P ON O.F_Proveedor = P.F_ClaProve INNER JOIN tb_proyectos PR ON O.F_Proyecto = PR.F_Id WHERE F_Usu = '" + User + "' AND F_NoOc ='" + Consulta.getString(1) + "' AND P.F_ClaProve = '" + Consulta.getString(3) + "' AND F_Proyecto='" + Consulta.getString(8) + "' GROUP BY F_NoOc, P.F_ClaProve, F_Clave, F_Clavess,F_Proyecto;");
                                    while (Claves.next()) {
                                        con.insertar("INSERT INTO tb_pedidoisem2017 VALUES(0,'" + Claves.getString(1) + "','" + Claves.getString(2) + "','" + Claves.getString(3) + "','" + Claves.getString(4) + "','-','-','-',CURDATE(),'" + Claves.getString(5) + "','" + Claves.getString(9) + "','" + Claves.getString(7) + "','" + Claves.getString(8) + "',CURTIME(),'" + IdUsu + "',1,0,'" + Claves.getString(6) + "','" + Claves.getString(10) + "','" + Claves.getString(11) + "');");
                                    }

                                }
                            }
                            Contar = 0;
                            Contar3 = 0;
                            Contar4 = 0;
                            ContarRLP = 0;
                            ContarLP = 0;
                        } else {
                            Contar = 0;
                            Contar3 = 0;
                            Contar4 = 0;
                            ContarRLP = 0;
                            ContarLP = 0;
                            return;
                        }
                    } else {
                        Contar = 0;
                        Contar3 = 0;
                        Contar4 = 0;
                        ContarRLP = 0;
                        ContarLP = 0;
                        return;
                    }
                } else {
                    Contar = 0;
                    Contar3 = 0;
                    Contar4 = 0;
                    ContarRLP = 0;
                    ContarLP = 0;
                    return;
                }
            } else {
                Contar = 0;
                Contar3 = 0;
                Contar4 = 0;
                ContarRLP = 0;
                ContarLP = 0;
                return;
            }

            con.cierraConexion();
        } catch (Exception e) {
            Logger.getLogger("LeeExcelRecibo").log(Level.SEVERE, null, e);
        }

    }
}
