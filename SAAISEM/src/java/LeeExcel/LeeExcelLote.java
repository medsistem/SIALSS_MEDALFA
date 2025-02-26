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
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Vector;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Anibal GNKL
 */
public class LeeExcelLote {

    private Vector vectorDataExcelXLSX = new Vector();

    public boolean obtieneArchivo(String path, String file, String Usuario) {

        String excelXLSXFileName = path + "/exceles/" + file;
        vectorDataExcelXLSX = readDataExcelXLSX(excelXLSXFileName);
        displayDataExcelXLSX(vectorDataExcelXLSX, Usuario);
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

    public void displayDataExcelXLSX(Vector vectorData, String User) {
        System.out.println("User: " + User);
        // Looping every row data in vector
        DateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df3 = new SimpleDateFormat("dd/MM/yyyy");

        ConectionDB con = new ConectionDB();
        try {
            con.conectar();
            con.actualizar("DELETE FROM tb_cargareqlote WHERE F_User='" + User + "'");
            System.out.println("Registros Eliminados");
            con.cierraConexion();
        } catch (Exception e) {

        }

        for (int i = 0; i < vectorData.size(); i++) {            
            Vector vectorCellEachRowData = (Vector) vectorData.get(i);            
            String qry = "insert into tb_cargareqlote values (";
            String qryElimina = "";
            // looping every cell in each row

            try {
                con.conectar();
                for (int j = 0; j < 4; j++) {

                    if (j == 0) {
                        try {
                            String Clave = (vectorCellEachRowData.get(j).toString() + "").trim();
                            /*NumberFormat formatter = new DecimalFormat("0000");
                             Clave = formatter.format(Double.parseDouble(Clave));*/
                            System.out.println(Clave);
                            
                                
                            
                            Clave.replaceAll("^\\s*", "");
                            Clave.replaceAll(" ", "");
                            Clave.replaceAll("&nbsp;", "");
                            for (int x = 0; x < Clave.length(); x++) {

                                System.out.println(Clave.charAt(x) + " = " + Clave.codePointAt(x));
                            };
                           
                           
                            qry = qry + "'" + Clave + "', ";
                            
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    } else if (j == 1) {
                        System.out.println("algo");
                        try {
                            String ClaPro = ((vectorCellEachRowData.get(j).toString()) + "");

                            qry = qry + "'" + ClaPro + "' , ";

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    } else if (j == 2) {
                        qry = qry + "'0' , ";
                    } else if (j == 3) {
                        try {
                            String Clave = ((int) Double.parseDouble(vectorCellEachRowData.get(j).toString()) + "");
                            qry = qry + "'" + Clave.trim() + "' , ";
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
                String F_Fecha = "", Solicitado = "", Referencia = "", Lote = "", Caducidad = "";
                try {
                    F_Fecha = (vectorCellEachRowData.get(2).toString());
                    F_Fecha = df2.format(df1.parse(F_Fecha));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    try {
                        F_Fecha = df2.format(df3.parse(F_Fecha));
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }

                try {
                    Caducidad = (vectorCellEachRowData.get(5).toString());
                    Caducidad = df2.format(df1.parse(Caducidad));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    try {
                        Caducidad = df2.format(df3.parse(Caducidad));
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                Solicitado = ((vectorCellEachRowData.get(3).toString()) + "");
                Lote = ((vectorCellEachRowData.get(4).toString()) + "");
                Referencia = "-";
                qry = qry + "curdate(), 0, '0','" + F_Fecha + "','" + Solicitado + "','" + Lote + "','" + Caducidad + "','" + Referencia + "','" + User + "')"; // agregar campos fuera del excel
                con.insertar(qry);
                con.cierraConexion();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

        }

        try {
            con.conectar();
            
            ResultSet Unidad = con.consulta("SELECT F_ClaUni FROM tb_cargareqlote WHERE F_User='" + User + "' GROUP BY F_ClaUni;");
            while (Unidad.next()) {
                int Contar = 0;
                System.out.println("contar: " +  Contar);
                ResultSet ContUnidad = con.consulta("SELECT COUNT(*) FROM tb_facttemplote WHERE F_StsFact = 3 AND F_ClaCli = '" + Unidad.getString(1) + "';");
                
                if (ContUnidad.next()) {
                    Contar = ContUnidad.getInt(1);
                    
                    System.out.println("Contar en temp: "+Contar);
                

                if (Contar > 0) {   
                    System.out.println("contar claves " + Contar);
                    con.actualizar("DELETE FROM tb_facttemplote WHERE F_ClaCli = '" + Unidad.getString(1) + "' AND F_StsFact = 3;");
                    //ResultSet Claves = con.consulta("SELECT F_ClaUni, C.F_ClaPro, F_CajasReq, SUM(F_PiezasReq) AS F_PiezasReq, F_FecCarg, F_Status, F_Fecha, SUM(F_Solicitado) AS F_Solicitado, F_Lote, F_Caducidad, F_Referencia FROM tb_cargareqlote C INNER JOIN tb_medica M ON C.F_ClaPro = M.F_ClaPro WHERE F_User='" + User + "' AND F_ClaUni='" + Unidad.getString(1) + "' AND M.F_StsPro='A' GROUP BY F_ClaUni, C.F_ClaPro, F_FecCarg, F_Status, F_Fecha, F_Lote, F_Caducidad;");
                    ResultSet Claves = con.consulta("SELECT F_ClaUni, C.F_ClaPro, F_CajasReq, SUM(F_PiezasReq) AS F_PiezasReq, F_FecCarg, F_Status, F_Fecha, SUM(F_Solicitado) AS F_Solicitado, F_Lote, F_Caducidad, F_Referencia, L.F_IdLote FROM tb_cargareqlote C INNER JOIN tb_medica M ON C.F_ClaPro = M.F_ClaPro INNER JOIN ( SELECT F_ClaPro, F_ClaLot, F_FecCad, F_ExiLot, F_IdLote FROM tb_lote WHERE F_ExiLot > 0 AND F_ClaPro IN ('9999', '9998', '9996', '9995')) AS L ON C.F_ClaPro = L.F_ClaPro AND C.F_Lote = L.F_ClaLot AND C.F_Caducidad = L.F_FecCad WHERE F_User='" + User + "' AND F_ClaUni='" + Unidad.getString(1) + "' AND M.F_StsPro = 'A' GROUP BY F_ClaUni, C.F_ClaPro, F_FecCarg, F_Status, F_Fecha, F_Lote, F_Caducidad;");
                    while (Claves.next()) {
                        //con.insertar("INSERT INTO tb_unireqlote VALUES('" + Claves.getString(1) + "','" + Claves.getString(2) + "','" + Claves.getString(3) + "','" + Claves.getString(4) + "','" + Claves.getString(5) + "',0,'" + Claves.getString(6) + "','" + Claves.getString(7) + "','" + Claves.getString(8) + "','" + Claves.getString(9) + "','" + Claves.getString(10) + "','','" + Claves.getString(11) + "');");
                                                
                        con.insertar("INSERT INTO tb_facttemplote VALUES(1,'" + Claves.getString(1) + "','" + Claves.getString(12) + "','" + Claves.getString(8) + "','" + Claves.getString(7) + "','3',0,'" + User + "','" + Claves.getString(8) + "',0);");
                    }
                } else {
                    System.out.println("entre < 0");
                    ResultSet Claves = con.consulta("SELECT F_ClaUni, C.F_ClaPro, F_CajasReq, SUM(F_PiezasReq) AS F_PiezasReq, F_FecCarg, F_Status, F_Fecha, SUM(F_Solicitado) AS F_Solicitado, F_Lote, F_Caducidad, F_Referencia, L.F_IdLote FROM tb_cargareqlote C INNER JOIN tb_medica M ON C.F_ClaPro = M.F_ClaPro INNER JOIN ( SELECT F_ClaPro, F_ClaLot, F_FecCad, F_ExiLot, F_IdLote FROM tb_lote WHERE F_ExiLot > 0 AND F_ClaPro IN ('9999', '9998', '9996', '9995')) AS L ON C.F_ClaPro = L.F_ClaPro AND C.F_Lote = L.F_ClaLot AND C.F_Caducidad = L.F_FecCad WHERE F_User='" + User + "' AND F_ClaUni='" + Unidad.getString(1) + "' AND M.F_StsPro = 'A' GROUP BY F_ClaUni, C.F_ClaPro, F_FecCarg, F_Status, F_Fecha, F_Lote, F_Caducidad;");
//                    System.out.println(Claves);
                    while (Claves.next()) {
                        System.out.println("insertar en facttemp");
                        con.insertar("INSERT INTO tb_facttemplote VALUES(1,'" + Claves.getString(1) + "','" + Claves.getString(12) + "','" + Claves.getString(8) + "','" + Claves.getString(7) + "','3',0,'" + User + "','" + Claves.getString(8) + "',0);");
                    }
                }
                }
            }
            con.cierraConexion();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public String agrega(String clave) {
        String clave2 = "";
        if (clave.length() < 4) {

            if (!clave.substring(0, 1).equals("0")) {
                //System.out.println(clave);
                if (clave.length() == 1) {
                    clave2 = ("000" + clave);
                }
                if (clave.length() == 2) {
                    clave2 = ("00" + clave);
                }
                if (clave.length() >= 3) {
                    clave2 = ("0" + clave);
                }

            }
        } else {
            clave2 = clave;
        }
        return clave2;
    }

}
