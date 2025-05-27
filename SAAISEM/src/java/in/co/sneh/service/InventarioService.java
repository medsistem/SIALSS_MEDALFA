/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.co.sneh.service;

import conn.ConectionDB;
import in.co.sneh.model.Lote;
import in.co.sneh.model.LoteCompra;
import in.co.sneh.model.Movimiento;
import in.co.sneh.model.Proveedor;
import in.co.sneh.persistance.LoteDAOImpl;
import in.co.sneh.persistance.MovimientoDAOImpl;
import in.co.sneh.persistance.ProveedorDAOImpl;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.DateFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author HP-MEDALFA
 */
public class InventarioService {

    private Vector vectorDataExcelXLSX = new Vector();

    public boolean cargarInventario(String path, String file, String Usuario) {

        String excelXLSXFileName = path + "/exceles/" + file;
        vectorDataExcelXLSX = readDataExcelXLSX(excelXLSXFileName);
        procesarInventario(vectorDataExcelXLSX, Usuario);
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
        } catch (IOException ex) {
            ex.getMessage();
        }

        return vectorData;
    }

    private void procesarInventario(Vector data, String usuario) {
        ConectionDB con = new ConectionDB();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        LoteDAOImpl loteDao = new LoteDAOImpl(con.getConn());
        MovimientoDAOImpl movDao = new MovimientoDAOImpl(con.getConn());
        ProveedorDAOImpl provDao = new ProveedorDAOImpl(con.getConn());
        List<Lote> noRegistrados = new ArrayList<Lote>();
        try {
            con.conectar();
            for (int i = 1; i < data.size(); i++) {

                Vector info = (Vector) data.get(i);
                if (info.get(0).toString().isEmpty()) {
                    break;
                }
               
                //Cambiar en caso de que cambie el formato de Excel
                Lote l = Lote.creatLote(info);
                if (l.getFecCadD() == null) {
                    l.setFecCadD(df.parse(l.getFecCad()));
                }
                
                //Checar si existe el Lote en la ubicación
                LoteCompra existente = loteDao.findLoteUbica(l);
                if (existente != null) {
                    Movimiento m = new Movimiento();
                    m.setConMov(11);
                    m.setDocMov("264303");
                    m.setProMov(l.getClaPro());
                    m.setCantMov(l.getExistencia());
                    m.setCostMov(existente.compra.getCosto());
                    m.setTotalMov(existente.compra.getCosto() * l.getExistencia());
                    m.setSigMov(1);
                    m.setLotMov(existente.lote.getFolLot());
                    m.setUbiMov(existente.lote.getUbicacion());
                    m.setClaProve(existente.lote.getClaProvee());
                    m.setUser("Inventario");
                    loteDao.acutualizaExistencia(existente.lote, l.getExistencia());
                    movDao.addMovimiento(m);
                    continue;
                }

                existente = loteDao.findLote(l);
                if (existente != null) {
                    existente.lote.setClaPro(l.getClaPro());
                    existente.lote.setClaLot(l.getClaLot());
                    
                    existente.lote.setExistencia(l.getExistencia());
                    existente.lote.setUbicacion(l.getUbicacion());
                    loteDao.create(existente.lote);

                    Movimiento m = new Movimiento();
                    m.setConMov(11);
                    m.setDocMov("264303");
                    m.setProMov(l.getClaPro());
                    m.setCantMov(l.getExistencia());
                    m.setCostMov(existente.compra.getCosto());
                    m.setTotalMov(existente.compra.getCosto() * l.getExistencia());
                    m.setSigMov(1);
                    m.setLotMov(existente.lote.getFolLot());
                    m.setUbiMov(l.getUbicacion());
                    m.setClaProve(existente.lote.getClaProvee());
                    m.setUser("Inventario");
                    //loteDao.acutualizaExistencia(existente.lote, l.getExistencia());
                    movDao.addMovimiento(m);
                } else {
                    noRegistrados.add(l);
//                    int indiceFolio = loteDao.getFolioLote();
////                    Proveedor p = provDao.findByName(l.getProveedor());
//                    //CREAR LOTE NUEVO
////                    l.setClaPro(p.getClaProvee()+"");
//                    l.setFolLot(indiceFolio);
//                    l.setOrigen(1);
//                    l.setUniMed("131");
//                    Date fecFab = df.parse(l.getFecCad());
//                    Calendar calendar = Calendar.getInstance();
//                    calendar.setTime(fecFab);
//                    calendar.add(Calendar.YEAR, -5);
//                    fecFab= calendar.getTime();
//                    l.setFecFabD(fecFab);
//                    l.setFechaFab(df.format(fecFab));
//                    
//                    l.setFecCadD(df.parse(l.getFecCad()));
//                    
//                    loteDao.create(l);
//                    loteDao.updateFolioLote(indiceFolio + 1);
//                    
//                    Movimiento m = new Movimiento();
//                    m.setConMov(11);
//                    m.setDocMov("0");
//                    m.setProMov(l.getClaPro());
//                    m.setCantMov(l.getExistencia());
//                    m.setCostMov(0d);
//                    m.setTotalMov(0d);
//                    m.setSigMov(1);
//                    m.setLotMov(indiceFolio);
//                    m.setUbiMov(l.getUbicacion());
//                    m.setClaProve(l.getClaProvee());
//                    m.setUser("sistemas");
////                    loteDao.acutualizaExistencia(existente.lote, l.getExistencia());
//                    movDao.addMovimiento(m);
                }
            }
            con.cierraConexion();
        } catch (Exception ex) {
            Logger.getLogger(InventarioService.class.getName()).log(Level.SEVERE, null, ex);
            try {
                con.cierraConexion();
            } catch (SQLException ex1) {
                Logger.getLogger(InventarioService.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }

        for (Lote l : noRegistrados) {
            System.out.println(l.getClaPro() + "\t" + l.getClaLot() + "\t" + df.format(l.getFecCadD()) + "\t" + l.getUbicacion());
        }

    }

}
