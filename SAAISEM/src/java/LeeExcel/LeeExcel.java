package LeeExcel;

import conn.ConectionDB;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
 * Procesa archivo excel para carga de requerimiento unidades para la
 * facturación
 *
 * @author MEDALFA SOFTWARE
 * @version 1.40
 */
public class LeeExcel {

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
            con.actualizar("DELETE FROM tb_cargareq WHERE F_User='" + User + "'");
            System.out.println("Registros Eliminados");
            con.cierraConexion();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        try {
            con.conectar();
            for (int i = 1; i < vectorData.size(); i++) {
                Vector vectorCellEachRowData = (Vector) vectorData.get(i);
                String qry = "insert into tb_cargareq values (";
                String qryElimina = "";
                // looping every cell in each row

                String ClaPro = ((vectorCellEachRowData.get(1).toString()) + "");
                if (ClaPro.isEmpty()) {
                    break;
                }
                DecimalFormat formatter = new DecimalFormat("0000.00");

                DecimalFormatSymbols custom = new DecimalFormatSymbols();
                custom.setDecimalSeparator('.');
                custom.setGroupingSeparator(',');
                formatter.setDecimalFormatSymbols(custom);
                /*if (!(ClaPro.equals("5.0"))) {
                        ClaPro = formatter.format(Double.parseDouble(ClaPro));
                    }*/
                String[] punto = ClaPro.split("\\.");
                System.out.println(punto.length);
                if (punto.length > 1) {
                    System.out.println(ClaPro + "***" + punto[0] + "////" + punto[1]);
                    for (int j = punto[0].length(); j < 4; j++) {
                        punto[0] = "0" + punto[0];
                    }
                    if (punto[1].equals("01")) {
                        ClaPro = (punto[0]) + ".01";
                    } else if (punto[1].equals("02")) {
                        ClaPro = (punto[0]) + ".02";
                    } else if (punto[1].equals("10")) {
                        ClaPro = (punto[0]) + ".1";
                    } else if (punto[1].equals("20")) {
                        ClaPro = (punto[0]) + ".2";
                    } else if (punto[1].equals("30")) {
                        ClaPro = (punto[0]) + ".3";
                    } else if (punto[1].equals("40")) {
                        ClaPro = (punto[0]) + ".4";
                    } else if (punto[1].equals("50")) {
                        ClaPro = (punto[0]) + ".5";
                    } else if (punto[1].equals("03")) {
                        ClaPro = (punto[0]) + ".03";
                    } else if (punto[1].equals("04")) {
                        ClaPro = (punto[0]) + ".04";
                    } else if (punto[1].equals("05")) {
                        ClaPro = (punto[0]) + ".05";
                    } else if (punto[1].equals("1")) {
                        ClaPro = (punto[0]) + ".1";
                    } else if (punto[1].equals("2")) {
                        ClaPro = (punto[0]) + ".2";
                    } else if (punto[1].equals("00")) {
                        ClaPro = (punto[0]);
                    } else {
                        ClaPro = (punto[0]);
                    }
                    System.out.println(ClaPro);
                }

                // con.insertar("delete from tb_unireq where F_ClaUni = '" + (vectorCellEachRowData.get(0).toString() + "").trim() + "' and F_ClaPro = '" + agrega(ClaPro) + "' and F_Status=0;");
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
                        System.out.println("agrega 0");
                        ClaPro = ((vectorCellEachRowData.get(j).toString()) + "");
                        formatter = new DecimalFormat("0000.00");
//                            if (ClaPro.equals("260.02") || ClaPro.equals("801.01") || ClaPro.equals("0260.02") || ClaPro.equals("0801.01")) {
//                                formatter = new DecimalFormat("000.00");
//                            }
                        custom = new DecimalFormatSymbols();
                        custom.setDecimalSeparator('.');
                        custom.setGroupingSeparator(',');
                        formatter.setDecimalFormatSymbols(custom);
                        /*if (!(ClaPro.equals("5.0"))) {
                                ClaPro = formatter.format(Double.parseDouble(ClaPro));
                            }*/
                        punto = ClaPro.split("\\.");
                        System.out.println(punto.length);
                        if (punto.length > 1) {
                            System.out.println(ClaPro + "***" + punto[0] + "////" + punto[1]);
                            for (int k = punto[0].length(); k < 4; k++) {
                                punto[0] = "0" + punto[0];
                            }
                            if (punto[1].equals("01")) {
                                ClaPro = (punto[0]) + ".01";
                            } else if (punto[1].equals("02")) {
                                ClaPro = (punto[0]) + ".02";
                            } else if (punto[1].equals("10")) {
                                ClaPro = (punto[0]) + ".1";
                            } else if (punto[1].equals("20")) {
                                ClaPro = (punto[0]) + ".2";
                            } else if (punto[1].equals("30")) {
                                ClaPro = (punto[0]) + ".3";
                            } else if (punto[1].equals("40")) {
                                ClaPro = (punto[0]) + ".4";
                            } else if (punto[1].equals("50")) {
                                ClaPro = (punto[0]) + ".5";
                            } else if (punto[1].equals("03")) {
                                ClaPro = (punto[0]) + ".03";
                            } else if (punto[1].equals("04")) {
                                ClaPro = (punto[0]) + ".04";
                            } else if (punto[1].equals("05")) {
                                ClaPro = (punto[0]) + ".05";
                            } else if (punto[1].equals("1")) {
                                ClaPro = (punto[0]) + ".1";
                            } else if (punto[1].equals("2")) {
                                ClaPro = (punto[0]) + ".2";
                            } else if (punto[1].equals("00")) {
                                ClaPro = (punto[0]);
                            } else {
                                ClaPro = (punto[0]);
                            }
                            System.out.println(ClaPro);
                        }
                        if (ClaPro.equals("5")) {
                            qry = qry + "'" + ClaPro + "' , ";
                        } else {
                            qry = qry + "'" + agrega(ClaPro) + "' , ";
                        }

                    } else if (j == 2) {
                        System.out.println("agrega 2");
                        qry = qry + "'0' , ";
                    } else if (j == 3) {
                        System.out.println("agrega 3");
                        try {
                            String Clave = ((int) Double.parseDouble(vectorCellEachRowData.get(j).toString()) + "");
                            qry = qry + "'" + Clave.trim() + "' , ";
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                }
                String F_Fecha = "";
                String Solicitado = "";
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
                Solicitado = ((vectorCellEachRowData.get(3).toString()) + "");
                qry = qry + "curdate(), 0, '0','" + F_Fecha + "','" + Solicitado + "','" + User + "',NOW())"; // agregar campos fuera del excel
                con.insertar(qry);
            }
            con.cierraConexion();
        } catch (Exception e) {
            try {
                con.cierraConexion();
            } catch (SQLException ex) {
                Logger.getLogger(LeeExcel.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println(e.getMessage());

        }
/**INICIO DE VALIDACION DE CLAVES**/
        try {
            con.conectar();
            String findMonitor = "SELECT c.F_Fecha, c.F_ClaUni from tb_cargareq c where F_User = '" + User + "' GROUP BY F_Fecha, F_ClaUni ;";
            //System.out.println(findMonitor);
            ResultSet Clues = con.consulta(findMonitor);
            while (Clues.next()) {
                String monitorQuery = "INSERT INTO tb_monitor_req ( F_Fecha , F_ClaUni , F_Creacion ) VALUES ( '" + Clues.getString(1) + "' , '" + Clues.getString(2) + "' , NOW() )";
                //System.out.println(monitorQuery);
                con.insertar(monitorQuery);
            }
            con.cierraConexion();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        //unireq inserccion
        try {
            con.conectar();
            int ContarCat = 0, ContarClaves = 0, Facturado = 0, CantMax = 0, Pendiente = 0, Diferencia = 0, Requerido = 0;
            ResultSet Claves = null;
            ResultSet ClavesControl = null;
            ResultSet ClavesFact = null;
            ResultSet CatClaves = null;
            ResultSet Unidad = con.consulta("SELECT F_ClaUni FROM tb_cargareq WHERE F_User='" + User + "' GROUP BY F_ClaUni;");
            while (Unidad.next()) {
                con.actualizar("UPDATE tb_unireq SET F_Status='1' WHERE F_ClaUni='" + Unidad.getString(1) + "' and F_Status='0';");

                ClavesFact = con.consulta("SELECT F_NivelUni FROM tb_uniatn WHERE F_ClaCli = '" + Unidad.getString(1) + "' ");

                if (ClavesFact.next()) {
                    ContarCat = ClavesFact.getInt(1);
                }

           
           
                if (ContarCat == 18) {
                    Claves = con.consulta("SELECT F_ClaUni,C.F_ClaPro,F_CajasReq,SUM(F_PiezasReq) AS F_PiezasReq,F_FecCarg,F_Status,F_Fecha,SUM(F_Solicitado) AS F_Solicitado FROM tb_cargareq C INNER JOIN tb_medica M ON C.F_ClaPro=M.F_ClaPro WHERE F_User='" + User + "' AND F_ClaUni='" + Unidad.getString(1) + "' AND M.F_StsPro='A' AND C.F_Status='0' AND M.F_N18 = 1  GROUP BY F_ClaUni,C.F_ClaPro,F_FecCarg,F_Fecha;");

                }else
                if (ContarCat == 17) {
                    Claves = con.consulta("SELECT F_ClaUni,C.F_ClaPro,F_CajasReq,SUM(F_PiezasReq) AS F_PiezasReq,F_FecCarg,F_Status,F_Fecha,SUM(F_Solicitado) AS F_Solicitado FROM tb_cargareq C INNER JOIN tb_medica M ON C.F_ClaPro=M.F_ClaPro WHERE F_User='" + User + "' AND F_ClaUni='" + Unidad.getString(1) + "' AND M.F_StsPro='A' AND C.F_Status='0' AND M.F_N17 = 1  GROUP BY F_ClaUni,C.F_ClaPro,F_FecCarg,F_Fecha;");

                } else {
                    System.out.println("<script>alert('claves fuera de catalogo')</script>");
                }
               
                
                while (Claves.next()) {
                 
                    con.insertar("INSERT INTO tb_unireq VALUES('" + Claves.getString(1) + "','" + Claves.getString(2) + "','" + Claves.getString(3) + "','" + Claves.getString(4) + "','" + Claves.getString(5) + "',0,'0','" + Claves.getString(7) + "','" + Claves.getString(8) + "','');");
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
    
    private int getParametroUsuario(String usuario, ConectionDB con) throws SQLException{
        String query = "SELECT F_Id from tb_parametrousuario where F_Usuario ='"+usuario+"'";
        ResultSet rs = con.consulta(query);
        while(rs.next()){
            return rs.getInt("F_Id");
        }
        return 0;
    }

}
