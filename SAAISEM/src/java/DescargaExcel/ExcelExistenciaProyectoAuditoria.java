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
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
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
@WebServlet(name = "ExcelExistenciaProyectoAuditoria", urlPatterns = {"/ExcelExistenciaProyectoAuditoria"})
public class ExcelExistenciaProyectoAuditoria extends HttpServlet {

    private static final double LIMIT = 100000;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String Tipo = request.getParameter("Tipo");
        ConectionDB con = new ConectionDB();
        String qry = null;
        PrintWriter pw = null;
        HttpSession sesion = request.getSession();
        String Nombre = "";

        try {
            //con = ConectionDB.instancias.get(0);
            con.conectar();
            pw = response.getWriter();
            DecimalFormat formatter = new DecimalFormat("#,###,###");

            String path = sesion.getServletContext().getRealPath("");
            String name = "Reporte_Existencia_%s.xlsx";
            if (Tipo.equals("1")) {
                Nombre = "Lote";
            } else {
                Nombre = "Disponible";
            }
            if (Tipo != null) {
                name = String.format(name, Nombre.concat(""));
            }

            String filename = path + name;
            String sheetName = "Detalles";
            XSSFWorkbook wb = new XSSFWorkbook();

            XSSFSheet sheet = wb.createSheet(sheetName);
            int width = 20;
            sheet.setAutobreaks(true);
            sheet.setDefaultColumnWidth(width);
            
            
            //Formato de la celdas
            XSSFCellStyle style = wb.createCellStyle();
            XSSFFont font = wb.createFont();
            font.setFontName(HSSFFont.FONT_ARIAL);
            font.setColor(IndexedColors.WHITE.getIndex());
            font.setFontHeightInPoints((short) 10);
            font.setBold(true);
            style.setFont(font);
            style.setFillForegroundColor((new XSSFColor(new java.awt.Color(0, 187, 178))));
            style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            

            

            int index = 0;

            XSSFRow rowHeadInv = sheet.createRow(index);
            

            index = 1;

            rowHeadInv = sheet.createRow(index);
                //Consulta por Lote
            switch (Tipo) {
                case "1":
                    rowHeadInv.createCell((int) 0).setCellValue("Proyecto");
                    rowHeadInv.createCell((int) 1).setCellValue("Clave");
                    rowHeadInv.createCell((int) 2).setCellValue("ClaveSS");
                    rowHeadInv.createCell((int) 3).setCellValue("Descripción");
                    rowHeadInv.createCell((int) 4).setCellValue("Lote");
                    rowHeadInv.createCell((int) 5).setCellValue("Caducidad");
                    rowHeadInv.createCell((int) 6).setCellValue("Cantidad");
                    rowHeadInv.createCell((int) 7).setCellValue("Orden de compra");

                    for (int j = 0; j <= 7; j++) {
                        rowHeadInv.getCell(j).setCellStyle(style);
                    }

                    break;
                case "2":
                    rowHeadInv.createCell((int) 0).setCellValue("Proyecto");
                    rowHeadInv.createCell((int) 1).setCellValue("Clave");
                    rowHeadInv.createCell((int) 2).setCellValue("ClaveSS");
                    rowHeadInv.createCell((int) 3).setCellValue("Descripción");
                    rowHeadInv.createCell((int) 4).setCellValue("Lote");
                    rowHeadInv.createCell((int) 5).setCellValue("Caducidad");
                    rowHeadInv.createCell((int) 6).setCellValue("Cantidad");
                    rowHeadInv.createCell((int) 7).setCellValue("Origen");
                    rowHeadInv.createCell((int) 8).setCellValue("Estatus");
                    rowHeadInv.createCell((int) 9).setCellValue("Orden de compra");
                    
                    for (int j = 0; j <= 9; j++) {
                        rowHeadInv.getCell(j).setCellStyle(style);
                    }
                    break;
                default:
                    break;
            }

            PreparedStatement ps;
            ResultSet rsTemp;

            if (!Tipo.equals("0")) {

                switch (Tipo) {
                    case "1":
                        qry = "SELECT * FROM tb_reporteauditoria";
                        break;
                    case "2":
                        qry = "SELECT * FROM tb_reporteauditoria";
                        break;

                }

            }

            System.out.println(qry);
            ps = con.getConn().prepareStatement(qry);
            rsTemp = ps.executeQuery();
            System.out.println(ps);
            while (rsTemp.next()) {

                index++;
//                

                XSSFRow row = sheet.createRow(index);

                switch (Tipo) {
                    case "1":
                        row.createCell((int) 0).setCellValue(rsTemp.getString(2));
                        row.createCell((int) 1).setCellValue(rsTemp.getString(3));
                        row.createCell((int) 2).setCellValue(rsTemp.getString(4));
                        row.createCell((int) 3).setCellValue(rsTemp.getString(5));
                        row.createCell((int) 4).setCellValue(rsTemp.getString(6));
                        row.createCell((int) 5).setCellValue(rsTemp.getString(7));
                        row.createCell((int) 6).setCellValue(formatter.format(rsTemp.getInt(8)));
                        row.createCell((int) 7).setCellValue(rsTemp.getString(11));
                        break;
                    case "2":
                        row.createCell((int) 0).setCellValue(rsTemp.getString(2));
                        row.createCell((int) 1).setCellValue(rsTemp.getString(3));
                        row.createCell((int) 2).setCellValue(rsTemp.getString(4));
                        row.createCell((int) 3).setCellValue(rsTemp.getString(5));
                        row.createCell((int) 4).setCellValue(rsTemp.getString(6));
                        row.createCell((int) 5).setCellValue(rsTemp.getString(7));
                        row.createCell((int) 6).setCellValue(formatter.format(rsTemp.getInt(8)));
                        row.createCell((int) 7).setCellValue(rsTemp.getString(9));
                        row.createCell((int) 8).setCellValue(rsTemp.getString(10));
                        row.createCell((int) 9).setCellValue(rsTemp.getString(11));
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
            Logger.getLogger(ExcelExistenciaProyectoAuditoria.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                response.flushBuffer();
                pw.flush();

                pw.close();
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExcelExistenciaProyectoAuditoria.class.getName()).log(Level.SEVERE, null, ex);
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
