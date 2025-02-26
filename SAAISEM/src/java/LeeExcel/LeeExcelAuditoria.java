package LeeExcel;

import conn.ConectionDB;
import java.io.FileInputStream;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
//import org.apache.catalina.valves.rewrite.InternalRewriteMap;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class LeeExcelAuditoria {

    private Vector vectorDataExcelXLSX = new Vector();

    public boolean obtieneArchivo(String path, String file, String Usuario, String Proyecto, String Campo) throws SQLException {

        String excelXLSXFileName = path + "/exceles/" + file;
        vectorDataExcelXLSX = readDataExcelXLSX(excelXLSXFileName);
        displayDataExcelXLSX(vectorDataExcelXLSX, Usuario, Proyecto, Campo);
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
            rowIteration.next();
            while (rowIteration.hasNext()) {

                XSSFRow xssfRow = (XSSFRow) rowIteration.next();
                Iterator cellIteration = xssfRow.cellIterator();

                Vector vectorCellEachRowData = new Vector();

                // Looping every cell in each row at sheet 0
                for (int i = 0; i < 7; i++) {

                    XSSFCell xssfCell = xssfRow.getCell(i);
                    vectorCellEachRowData.addElement(xssfCell);
                    System.out.println(vectorCellEachRowData);
                }

                vectorData.addElement(vectorCellEachRowData);

            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return vectorData;
    }

    @SuppressWarnings("empty-statement")
    public void displayDataExcelXLSX(Vector vectorData, String User, String Proyecto, String Campo) throws SQLException {

        // Looping every row data in vector
        DateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df3 = new SimpleDateFormat("dd/MM/yyyy");

        ConectionDB con = new ConectionDB();
        try {
            con.conectar();
            con.actualizar("DELETE FROM tb_reporteauditoriatemp WHERE F_Usu='" + User + "'");
            con.cierraConexion();
        } catch (Exception e) {
            Logger.getLogger("LeeExcelAuditoria").log(Level.SEVERE, null, e);
        }

        for (Object vectorData1 : vectorData) {
            String ClaPro = "";
            Vector vectorCellEachRowData = (Vector) vectorData1;
            String qry = qry = "INSERT INTO tb_reporteauditoriatemp VALUES (";
            // looping every cell in each row
            try {
                con.conectar();
                for (int j = 0; j < 2; j++) {

                    switch (j) {
                        case 0:
                            try {
                            ClaPro = ((vectorCellEachRowData.get(j).toString()) + "");
                            ClaPro = ClaPro.replaceAll("^\\s*", "");
                            ClaPro = ClaPro.replaceAll(" ", "");
                            ClaPro = ClaPro.replaceAll("&nbsp;", "");
                            qry = qry + " '" + ClaPro + "'";
                            System.out.println(ClaPro);
                        } catch (NullPointerException e) {
                            qry = qry + "NULL";
                        }
                        break;

                        case 1:
                            try {
                            String Lote = (vectorCellEachRowData.get(j).toString().trim() + "");

                            Lote = Lote.replaceAll(" ", "");
                            Lote = Lote.replaceAll("&nbsp;", "");
                            qry = qry + ", '" + Lote + "'";

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;

                        default:
                            break;

                    }
                }
//                  
                String Caducidad = "";
                try {
                    Caducidad = (vectorCellEachRowData.get(2).toString());
                    Caducidad = df2.format(df1.parse(Caducidad));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    try {
                        Caducidad = df2.format(df3.parse(Caducidad));
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                        Caducidad = null;
                    }
                }
                String Cantidad = "";                
                try {

                    Cantidad = ((vectorCellEachRowData.get(3).toString()));

                    Cantidad = Cantidad.replaceAll("^\\s*", "");
                    Cantidad = Cantidad.replaceAll("[a-zA-Z]+", "");
                    Cantidad = Cantidad.replaceAll("&nbsp;", "");
                    Cantidad = Cantidad.replaceAll(" ", "");
                    Double d = Double.parseDouble(Cantidad);
                    DecimalFormat df = new DecimalFormat("#############");
                    df.setRoundingMode(RoundingMode.DOWN);
                    df.format(d);                                        
                    Cantidad = df.format(d);
                    if (Cantidad.equals("")) {
                        Cantidad = "0";
                    }
                } catch (NullPointerException | NumberFormatException e) {
                    Cantidad = "0";

                }

                String Origen = "";
                try {
                    Origen = ((vectorCellEachRowData.get(4).toString()));
                    Origen = Origen.replaceAll("^\\s*", "");
                    Origen = Origen.replaceAll("[a-zA-Z]+", "");
                    Origen = Origen.replaceAll(" ", "");
                    Origen = Origen.replaceAll("&nbsp;", "");
                    Double d = Double.parseDouble(Origen);
                    DecimalFormat df = new DecimalFormat("#############");
                    df.setRoundingMode(RoundingMode.DOWN);
                    df.format(d);                                        
                    Origen = df.format(d);
                    if (Origen.equals("")) {
                        Origen = "0";
                    }
                } catch (NumberFormatException | NullPointerException e) {
                    Origen = "0";
                }

                String Disponibilidad = "0";
                try {
                    Disponibilidad = ((vectorCellEachRowData.get(5).toString()));
                    Disponibilidad = Disponibilidad.replaceAll("^\\s*", "");
                    Disponibilidad = Disponibilidad.replaceAll("[a-zA-Z]+", "");
                    Disponibilidad = Disponibilidad.replaceAll("&nbsp;", "");
                    Disponibilidad = Disponibilidad.replaceAll(" ", "");
                    Double d = Double.parseDouble(Disponibilidad);
                    DecimalFormat df = new DecimalFormat("#############");
                    df.setRoundingMode(RoundingMode.DOWN);
                    df.format(d);                                        
                    Disponibilidad = df.format(d);
                    if (Disponibilidad.equals("")) {
                        Disponibilidad = "0";
                    }
                } catch (NumberFormatException | NullPointerException e) {
                    Disponibilidad = "0";
                }

                String ordenCompra = "";
                try {
                    ordenCompra = ((vectorCellEachRowData.get(6).toString()) + "");
                    ordenCompra = ordenCompra.replaceAll("^\\s*", "");
                    ordenCompra = ordenCompra.replaceAll("&nbsp;", "");
                    ordenCompra = ordenCompra.replaceAll(" ", "");
                    if (ordenCompra.equals("")) {
                        ordenCompra = "";
                    }
                } catch (NullPointerException e) {
                }
                
                if (Caducidad != null) {
                    Caducidad = "'" + Caducidad + "'";
                }

                qry = qry + "," + Caducidad + ", '" + Cantidad + "', '" + Origen + "', '" + Disponibilidad + "', '" + ordenCompra + "', '" + User + "', '0', '0', '0', '0', '0', '0');";
                con.insertar(qry);
                con.cierraConexion();
            } catch (Exception e) {
                //System.out.println(qry);
                Logger.getLogger("LeeExceAuditoria").log(Level.SEVERE, null, e);
            }

        }

        try {
            con.conectar();

            con.actualizar("UPDATE tb_reporteauditoriatemp AS rat INNER JOIN tb_medica AS m ON rat.clave = m.F_ClaPro SET rat.validaclave = 1 WHERE rat.F_Usu = '" + User + "';");
            con.actualizar("UPDATE tb_reporteauditoriatemp AS rat INNER JOIN tb_origen as o ON rat.origen = o.F_ClaOri SET rat.validaorigen = 1 WHERE rat.F_Usu = '" + User + "';");
            con.actualizar("UPDATE tb_reporteauditoriatemp AS rat INNER JOIN tb_reporteauditoriaestatus AS ras ON rat.estatus = ras.id_estatus SET rat.validaestatus = 1 WHERE rat.F_Usu = '" + User + "';");
            con.actualizar("UPDATE tb_reporteauditoriatemp AS rat SET rat.validalote = 1 WHERE rat.F_Usu = '" + User + "' AND rat.lote <> '';");
            con.actualizar("UPDATE tb_reporteauditoriatemp AS rat SET rat.validacantidad = 1 WHERE rat.F_Usu = '" + User + "' AND rat.cantidad > 0;");
            con.actualizar("UPDATE tb_reporteauditoriatemp SET validacaducidad = 1 WHERE F_Usu = '" + User + "' AND caducidad > (NOW() + INTERVAL 9 MONTH);");
            con.actualizar("UPDATE tb_reporteauditoriatemp SET ordencompra = UPPER(ordencompra) WHERE F_Usu = '" + User + "';");

//         
            con.cierraConexion();
        } catch (Exception e) {
            Logger.getLogger("LeeExcelRecibo").log(Level.SEVERE, null, e);
        }

    }
}
