/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DescargaExcel;

import conn.ConectionDB;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author CEDIS TOLUCA3
 */
@WebServlet(name = "ExcelExistenciaFonsabi", urlPatterns = {"/ExcelExistenciaFonsabi"})
public class ExcelExistenciaFonsabi extends HttpServlet {

    private static final double LIMIT = 100000;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String Proyecto = request.getParameter("Proyecto");
        String Tipo = request.getParameter("Tipo");
        String Consulta = request.getParameter("Consulta");
        ConectionDB con = new ConectionDB();
        PrintWriter pw = null;
        HttpSession sesion = request.getSession();
        String Nombre = "";
        ResultSet rsetDatos = null;
        PreparedStatement psDatos = null;
        try {
            //con = ConectionDB.instancias.get(0);
            con.conectar();
            pw = response.getWriter();
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            String QueryProyecto = "SELECT F_DesProy FROM tb_proyectos WHERE F_Id = ?;";
            if (Proyecto.equals("0")) {
                Nombre = "Todos";
            } else {
                psDatos = con.getConn().prepareStatement(QueryProyecto);
                psDatos.setString(1, Proyecto);
                rsetDatos = psDatos.executeQuery();
                if (rsetDatos.next()) {
                    Nombre = rsetDatos.getString(1);
                }
            }

            String path = sesion.getServletContext().getRealPath("");
            String name = "Existencia-%s.xlsx";

            if (Proyecto != null) {
                name = String.format(name, Nombre.concat("-%s"));
            }
            name = String.format(name, Nombre);

            String filename = path + name;
            String sheetName = "Detalles";
            XSSFWorkbook wb = new XSSFWorkbook();

            XSSFSheet sheet = wb.createSheet(sheetName);
            int width = 20;
            sheet.setAutobreaks(true);
            sheet.setDefaultColumnWidth(width);

            //FORMATO DE CELDAS
            XSSFCellStyle style = wb.createCellStyle();
            XSSFFont font = wb.createFont();
            font.setFontName(HSSFFont.FONT_ARIAL);
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setFontHeightInPoints((short) 10);
            font.setBold(true);
            style.setFont(font);
            style.setFillForegroundColor((new XSSFColor(new java.awt.Color(0, 187, 178))));
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            XSSFCellStyle styleBorder = wb.createCellStyle();
            styleBorder.setBorderBottom(XSSFCellStyle.BORDER_MEDIUM);
            styleBorder.setBorderTop(XSSFCellStyle.BORDER_MEDIUM);
            styleBorder.setBorderRight(XSSFCellStyle.BORDER_MEDIUM);
            styleBorder.setBorderLeft(XSSFCellStyle.BORDER_MEDIUM);

            int totPiezas = 0, veces = 0;
            String qry = null, auxLimit;
            ResultSet rset;

            int index = 0;

            XSSFRow rowHeadInv = sheet.createRow(index);
            //rowHeadInv.createCell((int) 0).setCellValue(String.format("Total de existencias en %d unidades de atención: %s", unidades.size(), formatter.format(totPiezas)));

            index = 2;

            rowHeadInv = sheet.createRow(index);
            switch (Consulta) {
                case "1":
                    rowHeadInv.createCell((int) 0).setCellValue("Clave");
                    rowHeadInv.createCell((int) 1).setCellValue("Nombre Genérico");
                    rowHeadInv.createCell((int) 2).setCellValue("Descripción");
                    rowHeadInv.createCell((int) 3).setCellValue("Presentacion");
                    rowHeadInv.createCell((int) 4).setCellValue("Cantidad");
                    for (int i = 0; i <= 4; i++) {
                        rowHeadInv.getCell(i).setCellStyle(style);
                    }
                    break;
                case "2":
                    rowHeadInv.createCell((int) 0).setCellValue("Clave");
                    rowHeadInv.createCell((int) 1).setCellValue("Nombre Genérico");
                    rowHeadInv.createCell((int) 2).setCellValue("Descripción");
                    rowHeadInv.createCell((int) 3).setCellValue("Presentacion");
                    rowHeadInv.createCell((int) 4).setCellValue("Lote");
                    rowHeadInv.createCell((int) 5).setCellValue("Caducidad");
                    rowHeadInv.createCell((int) 6).setCellValue("Cantidad");
                    rowHeadInv.createCell((int) 7).setCellValue("Origen");
                    rowHeadInv.createCell((int) 8).setCellValue("Proveedor");
                    for (int i = 0; i <= 8; i++) {
                        rowHeadInv.getCell(i).setCellStyle(style);
                    }
                    break;
                case "3":
                    rowHeadInv.createCell((int) 0).setCellValue("Clave");
                    rowHeadInv.createCell((int) 1).setCellValue("Nombre Genérico");
                    rowHeadInv.createCell((int) 2).setCellValue("Descripción");
                    rowHeadInv.createCell((int) 3).setCellValue("Presentacion");
                    rowHeadInv.createCell((int) 4).setCellValue("Lote");
                    rowHeadInv.createCell((int) 5).setCellValue("Caducidad");
                    rowHeadInv.createCell((int) 6).setCellValue("Ubicación");
                    rowHeadInv.createCell((int) 7).setCellValue("Cantidad");
                    rowHeadInv.createCell((int) 8).setCellValue("Origen");
                    rowHeadInv.createCell((int) 9).setCellValue("IdLote");
                    rowHeadInv.createCell((int) 10).setCellValue("Orden suministro");
                    rowHeadInv.createCell((int) 11).setCellValue("Remisión");
                    rowHeadInv.createCell((int) 12).setCellValue("Orden Compra");
                    rowHeadInv.createCell((int) 13).setCellValue("Fuente Financiamiento");
                    rowHeadInv.createCell((int) 14).setCellValue("Bodega");
                    
                    for (int i = 0; i <= 14; i++) {
                        rowHeadInv.getCell(i).setCellStyle(style);
                    }
                    break;
                default:
                    break;
            }

            int aux;
            PreparedStatement ps;
            ResultSet rsTemp;
            //for (int i = 0; i < veces; i++) {
            String AND = "", Ubicaciones = "";
            if (Proyecto.equals("1")) {

                switch (Consulta) {

                    case "1":
                        qry = "SELECT P.F_DesProy, L.F_ClaPro, M.F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica,M.F_PrePro,tb_proveedor.F_ClaProve,tb_proveedor.F_NomPro,case when `l`.`F_Ubica` like 'ST%' OR `l`.`F_Ubica` like '%SANTIN%' then 'SANTIN' when `l`.`F_Ubica` like 'MD%' OR `l`.`F_Ubica` like '%AERO%' then 'AEROPUERTO' else 'SENDERO' end AS `lugar`, M.F_NomGen FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id INNER JOIN tb_proveedor ON tb_proveedor.F_ClaProve = L.F_ClaPrv WHERE L.F_Proyecto = '" + Proyecto + "' AND L.F_ExiLot>0 " + AND + " AND L.F_ClaPro NOT IN ('9999', '9998', '9996', '9995') AND L.F_Origen = '19' GROUP BY L.F_ClaPro, L.F_Proyecto;";
                        break;
                    case "2":
                        qry = "SELECT P.F_DesProy, L.F_ClaPro, M.F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica,M.F_PrePro,tb_proveedor.F_ClaProve,tb_proveedor.F_NomPro,case when `l`.`F_Ubica` like 'ST%' OR `l`.`F_Ubica` like '%SANTIN%' then 'SANTIN' when `l`.`F_Ubica` like 'MD%' OR `l`.`F_Ubica` like '%AERO%' then 'AEROPUERTO' when (`l`.`F_Ubica` like 'MICH%' or `l`.`F_Ubica` like 'CROSS%') then 'MICHOACAN' else 'SENDERO' end AS `lugar`, M.F_NomGen FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id INNER JOIN tb_proveedor ON tb_proveedor.F_ClaProve = L.F_ClaPrv WHERE L.F_Proyecto = '" + Proyecto + "' AND L.F_ExiLot>0 " + AND + " AND L.F_ClaPro NOT IN ('9999', '9998', '9996', '9995') AND L.F_Origen = '19' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto,tb_proveedor.F_NomPro;";
                        break;
                    case "3":
                        qry = "SELECT P.F_DesProy, L.F_ClaPro, M.F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica,M.F_PrePro,tb_proveedor.F_ClaProve,tb_proveedor.F_NomPro,case when `l`.`F_Ubica` like 'ST%' OR `l`.`F_Ubica` like '%SANTIN%' then 'SANTIN' when `l`.`F_Ubica` like 'MD%' OR `l`.`F_Ubica` like '%AERO%' then 'AEROPUERTO' when (`l`.`F_Ubica` like 'MICH%' or `l`.`F_Ubica` like 'CROSS%') then 'MICHOACAN' else 'SENDERO' end AS `lugar`, C.F_OrdenSuministro, C.F_FolRemi, C.F_OrdCom, C.F_FuenteFinanza, L.F_IdLote, M.F_NomGen FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id INNER JOIN tb_proveedor ON tb_proveedor.F_ClaProve = L.F_ClaPrv LEFT JOIN tb_compra C ON C.F_Lote = L.F_FolLot AND C.F_ClaPro = L.F_ClaPro WHERE L.F_Proyecto = '" + Proyecto + "' AND L.F_ExiLot>0 " + AND + " AND L.F_ClaPro NOT IN ('9999', '9998', '9996', '9995') AND L.F_Origen = '19' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto, L.F_Ubica, L.F_FolLot;";
                        break;
                    default:
                        break;
                }

            }

            aux = 0;

            ps = con.getConn().prepareStatement(qry);
            rsTemp = ps.executeQuery();
            System.out.println(ps);
            while (rsTemp.next()) {
                aux++;
                index++;
                totPiezas += rsTemp.getInt(6);

                XSSFRow row = sheet.createRow(index);

                switch (Consulta) {
                    case "1":
                        row.createCell((int) 0).setCellValue(rsTemp.getString(2));
                        row.createCell((int) 1).setCellValue(rsTemp.getString(13));
                        row.createCell((int) 2).setCellValue(rsTemp.getString(3));
                        row.createCell((int) 3).setCellValue(rsTemp.getString(9));
                        row.createCell((int) 4).setCellValue(formatter.format(rsTemp.getInt(6)));
                        for (int i = 0; i <= 4; i++) {
                        row.getCell(i).setCellStyle(styleBorder);
                    }
                        break;
                    case "2":
                        row.createCell((int) 0).setCellValue(rsTemp.getString(2));
                        row.createCell((int) 1).setCellValue(rsTemp.getString(13));
                        row.createCell((int) 2).setCellValue(rsTemp.getString(3));
                        row.createCell((int) 3).setCellValue(rsTemp.getString(9));
                        row.createCell((int) 4).setCellValue(rsTemp.getString(4));
                        row.createCell((int) 5).setCellValue(rsTemp.getString(5));
                        row.createCell((int) 6).setCellValue(formatter.format(rsTemp.getInt(6)));
                        row.createCell((int) 7).setCellValue(rsTemp.getString(7));
                        row.createCell((int) 8).setCellValue(rsTemp.getString(11));
                        for (int i = 0; i <= 8; i++) {
                        row.getCell(i).setCellStyle(styleBorder);
                    }
                        break;
                    case "3":
                        row.createCell((int) 0).setCellValue(rsTemp.getString(2));
                        row.createCell((int) 1).setCellValue(rsTemp.getString(18));
                        row.createCell((int) 2).setCellValue(rsTemp.getString(3));
                        row.createCell((int) 3).setCellValue(rsTemp.getString(9));
                        row.createCell((int) 4).setCellValue(rsTemp.getString(4));
                        row.createCell((int) 5).setCellValue(rsTemp.getString(5));
                        row.createCell((int) 6).setCellValue(rsTemp.getString(8));
                        row.createCell((int) 7).setCellValue(formatter.format(rsTemp.getInt(6)));
                        row.createCell((int) 8).setCellValue(rsTemp.getString(7));
                        row.createCell((int) 9).setCellValue(rsTemp.getString(17));
                        row.createCell((int) 10).setCellValue(rsTemp.getString(13));
                        row.createCell((int) 11).setCellValue(rsTemp.getString(14));
                        row.createCell((int) 12).setCellValue(rsTemp.getString(15));                                              
                        row.createCell((int) 13).setCellValue(rsTemp.getString(16));                                              
                        row.createCell((int) 14).setCellValue(rsTemp.getString(12));
                        for (int i = 0; i <= 14; i++) {
                        row.getCell(i).setCellStyle(styleBorder);
                    }
                        break;
                    default:
                        break;
                }
            }
            rsTemp.close();
            ps.close();
            //}

            index = 0;

            rowHeadInv = sheet.getRow(index);
            //rowHeadInv.getCell((int) 0).setCellValue(String.format("Total de existencias en %d unidades de atención: %s", unidades.size(), formatter.format(totPiezas)));
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            try (FileOutputStream fileOut = new FileOutputStream(filename)) {
                wb.write(fileOut);
            }
            wb.close();
            String disHeader = "Attachment;Filename=\"" + name + "\"";
            response.setHeader("Content-Disposition", disHeader);
            File desktopFile = new File(filename);
            System.out.println("Va comenzar escritura.");
            try (FileInputStream fileInputStream = new FileInputStream(desktopFile)) {
                int j;
                while ((j = fileInputStream.read()) != -1) {
                    pw.write(j);
                }
                fileInputStream.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(ExcelExistenciaFonsabi.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                response.flushBuffer();
                pw.flush();

                pw.close();
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExcelExistenciaFonsabi.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
