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
@WebServlet(name = "ExcelReporteRecetas", urlPatterns = {"/ExcelReporteRecetas"})
public class ExcelExistenciaReporteRecetas extends HttpServlet {

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
        
        String fechaIni = request.getParameter("fechaIni");
        String fechaFin = request.getParameter("fechaFin");
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
            

            String path = sesion.getServletContext().getRealPath("");
            String name = "Reporte_recetas.xlsx";

            
            name = String.format(name, Nombre);

            String filename = path + name;
            String sheetName = "Reporte";
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

            List<String> unidades = new ArrayList<>();
            int totPiezas = 0, veces = 0;
            String qry = null, auxLimit;
            ResultSet rset;

            int index = 0;

            XSSFRow rowHeadInv = sheet.createRow(index);
            //rowHeadInv.createCell((int) 0).setCellValue(String.format("Total de existencias en %d unidades de atención: %s", unidades.size(), formatter.format(totPiezas)));

            index = 2;

            rowHeadInv = sheet.createRow(index);
            
                    rowHeadInv.createCell((int) 0).setCellValue("FOLIO");
                    rowHeadInv.createCell((int) 1).setCellValue("Jurisdicción");
                    rowHeadInv.createCell((int) 2).setCellValue("Municipio");
                    rowHeadInv.createCell((int) 3).setCellValue("Clave cliente");
                    rowHeadInv.createCell((int) 4).setCellValue("Nombre de la unidad");
                    rowHeadInv.createCell((int) 5).setCellValue("Fecha calendario");
                    for (int i = 0; i <= 5; i++) {
                        rowHeadInv.getCell(i).setCellStyle(style);
                    }
               
            int aux;
            PreparedStatement ps;
            ResultSet rsTemp;
            
            
           qry = " SELECT F.F_ClaDoc AS FOLIO, j.F_DesJurIS AS `Jurisdicción`, m.F_DesMunIS AS Municipio, F.F_ClaCli AS `Clave Cliente`, u.F_NomCli AS `Nombre de la Unidad`, DATE_FORMAT(F.F_FecApl, \"%d-%m-%Y\") AS `Fecha Calendario` FROM tb_factura AS F INNER JOIN tb_uniatn AS u ON F.F_ClaCli = u.F_ClaCli INNER JOIN tb_juriis AS j ON u.F_ClaJur = j.F_ClaJurIS INNER JOIN tb_muniis AS m ON u.F_ClaMun = m.F_ClaMunIS WHERE F.F_ClaPro IN ('9999', '9998', '9996', '9995') AND F.F_StsFact = 'A' AND F.F_FecApl BETWEEN '"+fechaIni+"' AND '"+ fechaFin+"' GROUP BY F.F_ClaDoc";

            aux = 0;

            ps = con.getConn().prepareStatement(qry);
            rsTemp = ps.executeQuery();
System.out.println(ps);
            while (rsTemp.next()) {
                aux++;
                index++;
      

                XSSFRow row = sheet.createRow(index);

               
                   
                        row.createCell((int) 0).setCellValue(rsTemp.getString(1));
                        row.createCell((int) 1).setCellValue(rsTemp.getString(2));
                        row.createCell((int) 2).setCellValue(rsTemp.getString(3));               
                        row.createCell((int) 3).setCellValue(rsTemp.getString(4));
                        row.createCell((int) 4).setCellValue(rsTemp.getString(5));
                        row.createCell((int) 5).setCellValue(rsTemp.getString(6));
                       
                        for (int i = 0; i <= 5; i++) {
                        row.getCell(i).setCellStyle(styleBorder);
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
            Logger.getLogger(ExcelExistenciaReporteRecetas.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                response.flushBuffer();
                pw.flush();

                pw.close();
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExcelExistenciaReporteRecetas.class.getName()).log(Level.SEVERE, null, ex);
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
