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
 * @author Medalfa
 */
public class LeeExcelCaducos {

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

    @SuppressWarnings("empty-statement")
    public void displayDataExcelXLSX(Vector vectorData, String User) {
        // Looping every row data in vector
        DateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat df3 = new SimpleDateFormat("dd/MM/yyyy");

        ConectionDB con = new ConectionDB();
        try {
            con.conectar();
            con.actualizar("DELETE FROM tb_cargacaducos WHERE F_Usu='" + User + "'");
            con.cierraConexion();
        } catch (Exception e) {
            Logger.getLogger("LeeExcelCaducos").log(Level.SEVERE, null, e);
        }

        for (Object vectorData1 : vectorData) {
            
            Vector vectorCellEachRowData = (Vector) vectorData1;
              
            String qry = "INSERT INTO tb_cargacaducos VALUES (";
            // looping every cell in each row
        try {
                con.conectar();
                for (int j = 0; j < 4; j++) {

                    switch (j) {
                        case 0:
                            try {
                                String NoOrden = (vectorCellEachRowData.get(j).toString().trim() + "");

                                NoOrden = NoOrden.replaceAll("^\\s*", "");
                                NoOrden = NoOrden.replaceAll(" ", "");
                                NoOrden = NoOrden.replaceAll("&nbsp;", "");

                                qry = qry + "'" + NoOrden + "', ";
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        case 1:
                            try {
                                String ClaPro = ((vectorCellEachRowData.get(j).toString()) + "");
                                qry = qry + "'" + ClaPro + "' , ";

                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        case 2:
                            try {
                                String Lote = ((vectorCellEachRowData.get(j).toString()) + "");
                                qry = qry + "'" + Lote + "'";

                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                            break;

                        default:
                            break;
                    }
                }
                String Caducidad = "";
                try {
                    Caducidad = (vectorCellEachRowData.get(3).toString());
                    Caducidad = df2.format(df1.parse(Caducidad));
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    try {
                        Caducidad = df2.format(df3.parse(Caducidad));
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
                String Solicitado = "";
                Solicitado = ((vectorCellEachRowData.get(4).toString()) + "");
                if (Solicitado.equals("Cantidad")) {
                    Solicitado = "0";
                }

                qry = qry + ",'" + Caducidad + "','" + Solicitado + "','" + User + "','1',0,0,0,0)"; // agregar campos fuera del excel
                System.out.println(qry);
                con.insertar(qry);
                con.cierraConexion();
            } catch (Exception e) {
                //System.out.println(qry);
                Logger.getLogger("LeeExcelCaducos").log(Level.SEVERE, null, e);
            }
        }

        try {
            con.conectar();
            con.actualizar("DELETE FROM tb_cargacaducos WHERE F_IdUnidad = 'IdUnidad' AND F_Usu='" + User + "';");

            con.actualizar("UPDATE tb_cargacaducos O INNER JOIN tb_uniatn P ON O.F_IdUnidad = P.F_IdReporte SET O.F_ProblemaUnidad = 1 WHERE O.F_UsU = '" + User + "';");

            con.actualizar("UPDATE tb_cargacaducos O INNER JOIN tb_medica M ON O.F_Clave = M.F_ClaPro SET O.F_ProblemaClave = 1 WHERE O.F_Usu = '" + User + "';");
            con.actualizar("UPDATE tb_cargacaducos O INNER JOIN tb_proyectos P ON O.F_Proyecto = P.F_Id SET O.F_ProblemaProyecto = 1 WHERE O.F_Usu = '" + User + "';");

            String claUni = "";
            ResultSet BuscaIndMoV = null;
            ResultSet ConFol = null;
            ResultSet Consulta2 = con.consulta("SELECT O.F_IdUnidad, COUNT(*), SUM(F_ProblemaUnidad), SUM(F_ProblemaClave), SUM(O.F_ProblemaProyecto), O.F_Proyecto, CONCAT( '( ', P.F_ClaCli, ' - ', P.F_NomCli, ' - ', P.F_IdReporte, ')' ), P.F_ClaCli FROM tb_cargacaducos O INNER JOIN tb_uniatn P ON O.F_IdUnidad = P.F_IdReporte INNER JOIN tb_proyectos PR ON O.F_Proyecto = PR.F_Id WHERE F_Usu = '" + User + "' GROUP BY F_IdUnidad, O.F_Proyecto;");
            while (Consulta2.next()) {
                int IdUnidad = Consulta2.getInt(1);
                int NoReg = Consulta2.getInt(2);
                int NoProvee = Consulta2.getInt(3);
                int NoClave = Consulta2.getInt(4);
                int NoProyecto = Consulta2.getInt(5);
                String Unidad = Consulta2.getString(7);
                claUni = Consulta2.getString(8);

                if ((NoReg == NoClave) && (NoReg == NoProvee) && (NoReg == NoProyecto)) {

                    String FoLote = "" , FoLote2 = "", FoLote3 = "";
                    int FolioMov = 0, Nfolio = 0, FolioLote = 0, Nfollot =0, Exit = 0, canti = 0;
                    //Aumenta indice
                    BuscaIndMoV = con.consulta("SELECT F_IndFolMov FROM tb_indice;");
                    if (BuscaIndMoV.next()) {
                        FolioMov = BuscaIndMoV.getInt(1);
                    }
                    Nfolio = FolioMov + 1;
                    con.actualizar("UPDATE tb_indice SET F_IndFolMov='" + Nfolio + "'");
                    //fin de indice

                    ResultSet Consulta = con.consulta("SELECT F_IdUnidad, F_Clave, F_Lote, F_Caducidad, SUM(F_Cant) AS F_Cant FROM tb_cargacaducos C INNER JOIN tb_uniatn U ON C.F_IdUnidad = U.F_IdReporte WHERE F_Usu = '" + User + "' AND F_IdUnidad = '" + Consulta2.getString(1) + "' GROUP BY F_IdUnidad, F_Clave, F_Lote, F_Caducidad;");
                     System.out.println(claUni);
                    while (Consulta.next()) {
                       
                        ConFol = con.consulta("SELECT F_FolLot, count(*) FROM tb_lote l INNER JOIN tb_factura f on l.F_FolLot = f.F_Lote WHERE f.F_ClaPro = '" + Consulta.getString(2) + "' AND l.F_ClaLot = '" + Consulta.getString(3) + "' AND l.F_FecCad = '" + Consulta.getString(4) + "' AND f.F_ClaCli = '"+claUni+"' LIMIT 1;");
                        if (ConFol.next()) {
                            if( ConFol.getString("F_FolLot") != null && ConFol.getInt("count(*)") > 0){
                               FoLote = ConFol.getString(1);
                               System.out.println("1- " + FoLote); 
                               FolioLote = Integer.parseInt(FoLote);
                               System.out.println("2- numero-" + FolioLote);
                            }else{
                              FolioLote = 0;  
                              System.out.println("3-"+FolioLote);
                            }   
                        }
                        if (FolioLote > 0) {
                             ResultSet Consult3 = con.consulta("select count(*) from tb_lote where F_FolLot = '"+ FolioLote +"' and F_Ubica = 'CADUCADOSFARMACIA'");
                            while(Consult3.next()){
                                FoLote2 = Consult3.getString(1);
                                 Nfollot = Integer.parseInt(FoLote2);
                                  System.out.println("cuantas ubi= "+Nfollot);
                            }
                                
                                if (Nfollot > 0) {
                                    ResultSet Consult4 = con.consulta("select F_ExiLot from tb_lote where F_FolLot = '"+ FolioLote +"' and F_Ubica = 'CADUCADOSFARMACIA'");
                                      while(Consult4.next()){
                                     FoLote3 = Consult4.getString(1);
                                      Exit = Integer.parseInt(FoLote3);
                                       System.out.println("El valor de exis= "+Exit);
                                      }
                                       canti = Integer.parseInt(Consulta.getString(5));
                                       System.out.println("El valor del ing= "+canti);
                                    
                                    Exit = Exit + canti;
                                    System.out.println("suma= "+Exit);
                                    con.actualizar("update tb_lote set F_ExiLot = '"+ Exit +"' where F_FolLot = '"+ FolioLote +"' and F_Ubica = 'CADUCADOSFARMACIA'");                           
                                    con.insertar("INSERT INTO tb_movinv VALUES(0, CURDATE(), '" + FolioMov + "', 30, '" + Consulta.getString(2) + "', '" + Consulta.getString(5) + "', '0.00', '0.00', 1, '" + FolioLote + "', 'CADUCADOSFARMACIA', '900000000',CURTIME(), '" + User + "','');");
                           
                                }
                                else{
                                    System.out.println("no esta ubi");
                                con.insertar("INSERT INTO tb_lote VALUES(0, '" + Consulta.getString(2) + "', '" + Consulta.getString(3) + "', '" + Consulta.getString(4) + "', '" + Consulta.getString(5) + "', '" + FolioLote + "', '900000000', 'CADUCADOSFARMACIA', '" + Consulta.getString(4) + "', '1010101010100', '100', '1', '900000000', '150', '1');");
                                con.insertar("INSERT INTO tb_movinv VALUES(0, CURDATE(), '" + FolioMov + "', 30, '" + Consulta.getString(2) + "', '" + Consulta.getString(5) + "', '0.00', '0.00', 1, '" + FolioLote + "', 'CADUCADOSFARMACIA', '900000000',CURTIME(), '" + User + "','INGRESO DE CADUCADOS');");
                          
                                }
                            
                           
//                            con.insertar("INSERT INTO tb_lote VALUES(0, '" + Consulta.getString(2) + "', '" + Consulta.getString(3) + "', '" + Consulta.getString(4) + "', '" + Consulta.getString(5) + "', '" + FolioLote + "', '900002081', 'CADUCADOSFARMACIA', '" + Consulta.getString(4) + "', '1234567890123', '12327', '1', '900002081', '131', '2');");
//                            con.insertar("INSERT INTO tb_movinv VALUES(0, CURDATE(), '" + FolioMov + "', 30, '" + Consulta.getString(2) + "', '" + Consulta.getString(5) + "', '0.00', '0.00', 1, '" + FolioLote + "', 'CADUCADOSFARMACIA', '900002081',CURTIME(), '" + User + "');");
                        
                        } else {
                            ConFol = con.consulta("SELECT F_IndLote FROM tb_indice;");
                            if (ConFol.next()) {
                                FolioLote = ConFol.getInt(1);
                            }
                            con.actualizar("UPDATE tb_indice SET F_IndLote = '" + (FolioLote + 1) + "'");

                            con.insertar("INSERT INTO tb_lote VALUES(0, '" + Consulta.getString(2) + "', '" + Consulta.getString(3) + "', '" + Consulta.getString(4) + "', '" + Consulta.getString(5) + "', '" + FolioLote + "', '900000000', 'CADUCADOSFARMACIA', '" + Consulta.getString(4) + "', '1010101010100', '100', '1', '900000000', '150', '1');");

                            con.insertar("INSERT INTO tb_movinv VALUES(0, CURDATE(), '" + FolioMov + "', 30, '" + Consulta.getString(2) + "', '" + Consulta.getString(5) + "', '0.00', '0.00', 1, '" + FolioLote + "', 'CADUCADOSFARMACIA', '900000000',CURTIME(), '" + User + "','INGRESO DE CADUCADOS');");

                        }
                        con.actualizar("DELETE FROM tb_cargacaducos WHERE F_IdUnidad = '" + Consulta.getString(1) + "' AND F_Clave = '" + Consulta.getString(2) + "' AND F_Lote = '" + Consulta.getString(3) + "' AND F_Caducidad = '" + Consulta.getString(4) + "' AND F_Usu = '" + User + "';");
                       
                    }
                    con.insertar("INSERT INTO tb_obsmov VALUES ('" + FolioMov + "','" + Unidad + "',0,'" + IdUnidad + "','ORDINARIO');");
                    
                }
                System.out.println("");
            }

            con.cierraConexion();
        } catch (Exception e) {
            Logger.getLogger("LeeExcelCaducos").log(Level.SEVERE, null, e);
        }

    }
}
