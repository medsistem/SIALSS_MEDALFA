/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package LeeExcel;

import conn.ConectionDB;
import java.io.FileInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.apache.commons.lang.StringUtils.isNumeric;

//import org.apache.poi.ss.usermodel.Row;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mariadb.jdbc.internal.ColumnType;

/**
 *
 * @author MEDALFA
 */
public class LeeExcelReciboContrato {

    private Vector vectorDataExcelXLSX = new Vector();


    public boolean obtieneArchivo(String path, String file, String Usuario, String  IdContra) {

        String excelXLSXFileName = path + "/exceles/" + file;
        vectorDataExcelXLSX = readDataExcelXLSX(excelXLSXFileName);
        displayDataExcelXLSX(vectorDataExcelXLSX, Usuario ,IdContra);
        return true;
    }

    public Vector readDataExcelXLSX(String fileName) {
        Vector vectorData = new Vector();

        try {
            FileInputStream fileInputStream = new FileInputStream(fileName);

            XSSFWorkbook excelcargaoc = new XSSFWorkbook(fileInputStream);
            XSSFSheet hojas = excelcargaoc.getSheetAt(0);
            Iterator filas = hojas.rowIterator();
            filas.next();

            /*Recorrer excel*/
            while (filas.hasNext()) {
                XSSFRow xssfRow = (XSSFRow) filas.next();
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
    public void displayDataExcelXLSX(Vector vectorData, String User, String IdContrato) {
      
        String usuc;
        int cont = 0;
        String NoOrden = "", Provee = "", ClaPro = "", FteFinanza = "", ProveeNombre = "", Nombre = "";
       
        String Contrato = IdContrato;
//        String Contrato = "";
        ConectionDB con = new ConectionDB();
        
        try {
            con.conectar();

            con.actualizar("DELETE FROM tb_cargaoc WHERE F_Usu='" + User + "'");
            con.cierraConexion();
        } catch (SQLException e) {
            Logger.getLogger("LeeExcelRecibo").log(Level.SEVERE, null, e);
        }

        try {
            con.conectar();
            for (Object vectorData1 : vectorData) {
                Vector vectorCellEachRowData = (Vector) vectorData1;
                
                System.out.println("vector: "+vectorCellEachRowData.size());
                
                String qry = "INSERT INTO tb_cargaoc VALUES (0,";
                boolean blocked = false;
                for (int j = 0; j < 6; j++) {
                    switch (j) {
                        case 0:
                            try {
                            NoOrden = (vectorCellEachRowData.get(j).toString().trim() + "");
                            NoOrden = NoOrden.replaceAll("^\\s*", "");
                            NoOrden = NoOrden.replaceAll(" ", "");
                            NoOrden = NoOrden.replaceAll("&nbsp;", "");
                            
                                if (NoOrden.isEmpty()) {
                                   NoOrden = "Error" ;
                                }
                             qry = qry + "'" + NoOrden + "', ";
                           
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                        case 1:
                            try {
                            Provee = (vectorCellEachRowData.get(j).toString() + "");

                            Provee = Provee.replaceAll("^\\s*", "");
                            Provee = Provee.replaceAll(" ", "");
                            Provee = Provee.replaceAll("&nbsp;", "");
                          
                            ResultSet proveeResult = con.consulta("SELECT pv.F_ClaProve, pv.F_NomPro  FROM  tb_proveedor pv WHERE F_ClaProve = '" + Provee + "' ");
                            while (proveeResult.next()) {
                                ProveeNombre = proveeResult.getString(2);
                                Provee = proveeResult.getString(1);
                            }
                                if (Provee.isEmpty()) {
                                    Provee = "Error";
                                   
                                } 
                                    
                                qry = qry + "'" + Provee + "',"; 
                            
                            System.out.println(qry);

                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                        case 2:
                            try {
                                Nombre = (vectorCellEachRowData.get(j).toString() + "").trim();
                                if (Nombre.isEmpty()) {
                                     Nombre = "Error";
                                } 
                                 qry = qry + "'" + Nombre + "', ";   
                                
                            } catch (Exception e) {
                                System.out.println(e.getMessage());
                            }
                            break;
                        case 3:
                            try {
                            ClaPro = ((vectorCellEachRowData.get(j).toString()) + "");
                            System.out.println("Clave: "+ ClaPro);
                            ResultSet cla = con.consulta("SELECT m.F_ClaProSS, m.F_StsPro FROM tb_medica AS m WHERE m.F_ClaPro ='" + ClaPro + "';");
//                                System.out.println("cla: "+cla.wasNull()+" "+cla.isBeforeFirst());
                                if (!cla.isBeforeFirst()) {
                                    System.out.println("No existe la clave");
                                    qry = qry + "'" + ClaPro + "' ,'" + ClaPro + "'";
                                } else {
                                   while (cla.next()) {  
                                    System.out.println("Si existe la clave");
                                   String claIsem = cla.getString(1);
                                     System.out.println("Clavess: "+ cla.getString(1));
                                    blocked = cla.getString(2).equals("S");
                                    if (!blocked) {
                                        qry = qry + "'" + ClaPro + "' ,'" + claIsem + "'";
                                    } else {
                                         System.out.println("Clave suspendida");
                                        qry = qry + "'" + ClaPro + "' ,'" + claIsem + "'";
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                        case 4:
                        try {
                            FteFinanza = ((vectorCellEachRowData.get(6).toString()) + "");
                            if (FteFinanza.isEmpty()) {
                                FteFinanza = "Pendiente";
                            }
                        } catch (Exception e) {
                              System.out.println(e.getMessage());
                        }
                        break;
                        case 5:
                        try {
                            if (Contrato.isEmpty()) {
                                Contrato = "Error";
                            }
                            
                        } catch (Exception e) {
                              System.out.println(e.getMessage());
                        }

                        default:
                            break;
                    }

                } //fin del for
                
                String Solicitado = "",proyecto = "";
                boolean Sol  = true, Dis = true;
                int cantidad = 0, Proy = 0;
                
                 Solicitado = vectorCellEachRowData.get(4).toString();
                
               
               Sol = isNumeric(Solicitado);
                System.out.println("Sol: "+Sol);
               
                if (Sol) {
                  cantidad = Integer.parseInt(vectorCellEachRowData.get(4).toString());
                }else{
                   cantidad = 0; 
                }
                  proyecto = vectorCellEachRowData.get(5).toString();
                 Dis = isProyect(proyecto);
                  System.out.println("Dis: "+Dis);
                if (Dis) {
                  Proy = Integer.parseInt(vectorCellEachRowData.get(5).toString());
                }else{
                   Proy = 0; 
                }
                

                qry = qry + ",'" + cantidad + "','" + User + "','" + Proy + "',0,0,0,0,0,'" + FteFinanza + "','"+Contrato+"')"; // agregar campos fuera del excel
                
                   con.insertar(qry);
                    System.out.println(qry);
                
            }
            con.cierraConexion();
        } catch (Exception e) {

            Logger.getLogger("LeeExcelRecibo").log(Level.SEVERE, null, e);
        }

        try {
            con.conectar();
            ResultSet BuscaPP2 = con.consulta("SELECT * FROM tb_cargaoc AS O WHERE O.F_Usu = '" + User + "' GROUP BY O.F_Id");//busca claves con referencia proveedor clave
            while (BuscaPP2.next()) {
              int IdOrden = BuscaPP2.getInt(1);
                System.out.println("IdOrden: " + IdOrden);
               if (!BuscaPP2.wasNull()) {
                    con.actualizar("UPDATE tb_cargaoc O INNER JOIN tb_proveedor P ON O.F_Proveedor = P.F_ClaProve SET O.F_ProblemaProvee = 1 WHERE O.F_Id = '" +  IdOrden + "' AND O.F_Proveedor not like 'Error' AND O.F_NombreProvee not like 'Error';");
                    con.actualizar("UPDATE tb_cargaoc O INNER JOIN tb_medica M ON O.F_Clave = M.F_ClaPro AND  M.F_StsPro != 'S' SET O.F_ProblemaClave= 1 WHERE O.F_Id = '" +  IdOrden + "';");
                    con.actualizar("UPDATE tb_cargaoc O INNER JOIN tb_proyectos P ON O.F_Proyecto = P.F_Id SET O.F_ProblemaProyecto = 1 WHERE O.F_Id = '" +  IdOrden + "' AND O.F_Proyecto > 0;");
                    con.actualizar("UPDATE tb_cargaoc O  SET O.F_ProblemaCantidad = 1 WHERE O.F_Id = '" +  IdOrden + "' AND O.F_Cant > 0;");
                    con.actualizar("UPDATE tb_cargaoc O  SET O.F_ProblemaReq = 1 WHERE O.F_Id = '" +  IdOrden + "' AND O.F_NoOc NOT LIKE '%Error%';");
               }
            }
       
                
                    
            ResultSet Consulta = con.consulta("SELECT F_NoOc, F_Proveedor, F_NombreProvee, COUNT(*),SUM(F_ProblemaReq), SUM(F_ProblemaProvee), SUM(F_ProblemaClave),SUM(F_ProblemaCantidad) ,SUM(O.F_ProblemaProyecto),O.F_Proyecto FROM tb_cargaoc O INNER JOIN tb_proveedor P ON O.F_Proveedor = P.F_ClaProve INNER JOIN tb_proyectos PR ON O.F_Proyecto=PR.F_Id WHERE F_Usu = '" + User + "' AND F_NoOc NOT IN ('Error') GROUP BY O.F_Id;");
          
                while (Consulta.next()) {
                    int NoReg = Consulta.getInt(4);
                    
                    int NoOc = Consulta.getInt(5);
                    int NoProvee = Consulta.getInt(6);
                    int NoClave = Consulta.getInt(7);
                    int NoCantidad = Consulta.getInt(8);
                    int NoProyecto = Consulta.getInt(9);

                    
                    if ((NoReg == NoClave) && (NoReg == NoProvee) && (NoReg == NoProyecto) && (NoReg == NoOc) && (NoReg == NoCantidad)) {
                        System.out.println("Entra para insertar: ");
                        con.actualizar("DELETE FROM tb_pedidoisem2017 WHERE F_NoCompra='" + Consulta.getString(1) + "' AND F_Provee='" + Consulta.getString(2) + "' AND F_Proyecto='" + Consulta.getString(10) + "';");

                        ResultSet Claves = con.consulta("SELECT F_NoOc, P.F_ClaProve, F_Clave, F_Clavess, SUM(F_Cant) AS F_Cant,F_Proyecto, O.F_FuenteFinanza,O.F_Contratos FROM tb_cargaoc O INNER JOIN tb_proveedor P ON O.F_Proveedor = P.F_ClaProve INNER JOIN tb_proyectos PR ON O.F_Proyecto=PR.F_Id INNER JOIN tb_medica M ON O.F_Clave = M.F_ClaPro AND  M.F_StsPro != 'S' WHERE F_Usu = '" + User + "' AND F_NoOc ='" + Consulta.getString(1) + "' AND P.F_ClaProve = '" + Consulta.getString(2) + "' AND F_Proyecto='" + Consulta.getString(10) + "' AND o.F_Cant > 0 GROUP BY O.F_Id");
                        while (Claves.next()) {
                            ResultSet usuarioc = con.consulta("SELECT usu.F_IdUsu FROM tb_usuariocompra AS usu WHERE usu.F_Nombre = '" + User + "';");
                            while (usuarioc.next()) {
                                usuc = Integer.toString(usuarioc.getInt(1));

                                ResultSet FechaSur = con.consulta("select DATE_ADD(curdate(), INTERVAL 15 DAY)");
                                String FechaS = "";
                                while (FechaSur.next()) {
                                    FechaS = FechaSur.getString(1);
                                }
                                System.out.println("ya para insertar");
                                con.insertar("INSERT INTO tb_pedidoisem2017 VALUES(0,'" + Claves.getString(1) + "','" + Claves.getString(2) + "','" + Claves.getString(3) + "','" + Claves.getString(4) + "','-','1','-',CURDATE(),'" + Claves.getString(5) + "','-',NOW(),'" + FechaS + "',CURTIME(),'" + usuc + "',1,0,'" + Claves.getString(6) + "','','CC/LP','" + Claves.getString(7) + "',21,'" + Claves.getString(8) + "')");

                            }
                        }
                    } 

                }
            
           
            con.cierraConexion();
        } catch (Exception e) {
            Logger.getLogger("LeeExcelReciboContrato").log(Level.SEVERE, null, e);
        }
    }
    
        private boolean isNumeric(String str){
        String pattern  = "([1-9]|[1-9][0-9]*)";
        return str != null && str.matches( pattern);
    }
      
       private boolean isProyect(String pry){
        String pattern  = "([1-9]|[1-9][0-9])";
        return pry != null && pry.matches( pattern);
    }
}
