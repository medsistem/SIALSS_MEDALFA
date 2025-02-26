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

@WebServlet(name = "ExcelConsultaMovCompras", urlPatterns = {"/ExcelConsultaMovCompras"})
public class ExcelConsultaMovCompras extends HttpServlet {

    private static final double LIMIT = 100000;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String clave = request.getParameter("clave");
        String fecha_ini = request.getParameter("fecha_ini");
        String fecha_fin = request.getParameter("fecha_fin");
        String origen = request.getParameter("origen");
        String clacli = request.getParameter("clacli");        
        ConectionDB con = new ConectionDB();
        PrintWriter pw = null;
        HttpSession sesion = request.getSession();
        String Nombre = "", Proyecto = "";
        ResultSet rsetDatos = null;
        PreparedStatement psDatos = null;
        try {
            //con = ConectionDB.instancias.get(0);
            con.conectar();
            pw = response.getWriter();
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            
            String path = sesion.getServletContext().getRealPath("");
            String name = "Reporte_Consulta_Mov%s.xlsx";            

            String filename = path + name;
            String sheetName = "Consulta";
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

            String fechaFol = "", Query = "";
            String qry = null;
            int ban1 = 0;
            ResultSet rset;

            int index = 0;

            XSSFRow rowHeadInv = sheet.createRow(index);
            //rowHeadInv.createCell((int) 0).setCellValue(String.format("Total de existencias en %d unidades de atención: %s", unidades.size(), formatter.format(totPiezas)));

            index = 0;

            rowHeadInv = sheet.createRow(index);
            switch (clacli) {
                case "1":
                    rowHeadInv.createCell((int) 0).setCellValue("Fecha");
                    rowHeadInv.createCell((int) 1).setCellValue("Movimiento");
                    rowHeadInv.createCell((int) 2).setCellValue("Clave");
                    rowHeadInv.createCell((int) 3).setCellValue("ClaveSS");
                    rowHeadInv.createCell((int) 4).setCellValue("Nombre genérico");
                    rowHeadInv.createCell((int) 5).setCellValue("Descripción");
                    rowHeadInv.createCell((int) 6).setCellValue("Presentación");
                    rowHeadInv.createCell((int) 7).setCellValue("Lote");
                    rowHeadInv.createCell((int) 8).setCellValue("Caducidad");
                    rowHeadInv.createCell((int) 9).setCellValue("Cantidad");
                    rowHeadInv.createCell((int) 10).setCellValue("Origen");
                    rowHeadInv.createCell((int) 11).setCellValue("Remisión");
                    rowHeadInv.createCell((int) 12).setCellValue("Proveedor");
                    rowHeadInv.createCell((int) 13).setCellValue("O.C.");
                    rowHeadInv.createCell((int) 14).setCellValue("Contrato");
                    for (int i = 0; i <= 14; i++) {
                        rowHeadInv.getCell(i).setCellStyle(style);
                    }
                    break;
                case "51":
                    rowHeadInv.createCell((int) 0).setCellValue("Fecha");
                    rowHeadInv.createCell((int) 1).setCellValue("Folio");
                    rowHeadInv.createCell((int) 2).setCellValue("Clave unidad");
                    rowHeadInv.createCell((int) 3).setCellValue("Unidad");
                    rowHeadInv.createCell((int) 4).setCellValue("Clave");
                    rowHeadInv.createCell((int) 5).setCellValue("ClaveSS");
                    rowHeadInv.createCell((int) 6).setCellValue("Nombre genéico");
                    rowHeadInv.createCell((int) 7).setCellValue("Descripción");
                    rowHeadInv.createCell((int) 8).setCellValue("Presentación");
                    rowHeadInv.createCell((int) 9).setCellValue("Lote");
                    rowHeadInv.createCell((int) 10).setCellValue("Caducidad");
                    rowHeadInv.createCell((int) 11).setCellValue("Cantidad");
                    rowHeadInv.createCell((int) 12).setCellValue("Origen");
                    rowHeadInv.createCell((int) 13).setCellValue("Movimiento");
//                    for (int i = 0; i <= 13; i++) {
//                        rowHeadInv.getCell(i).setCellStyle(style);
//                    }
                    break;
                default:
                    break;

            }

            int aux;
            PreparedStatement ps;
            ResultSet rsTemp;
            //for (int i = 0; i < veces; i++) {

            switch (clacli) {

                case "1":

                    if (fecha_ini != "" && fecha_fin != "") {
                        ban1 = 1;
                        fechaFol = " c.F_FecApl between '" + fecha_ini + "' and '" + fecha_fin + "' ";
                    }

                    if ((clave != "") && (ban1 != 1) && origen.equals("0")) {
                        Query = " c.F_ClaPro ='" + clave + "' ";
                    } else if ((clave == "") && (ban1 == 1) && origen.equals("0")) {
                        Query = "" + fechaFol + "";
                    } else if ((clave == "") && (ban1 != 1) && (!origen.equals("0"))) {
                        Query = " l.F_Origen='" + origen + "' ";
                    } else if ((clave != "") && (ban1 == 1) && origen.equals("0")) {
                        Query = " c.F_ClaPro ='" + clave + "' AND " + fechaFol + "";
                    } else if ((clave != "") && (ban1 == 1) && (!origen.equals("0"))) {
                        Query = " c.F_ClaPro ='" + clave + "' AND " + fechaFol + " AND l.F_Origen='" + origen + "' ";
                    }
                    qry = "SELECT 'Entrada por compra' AS F_DesCon, c.F_ClaPro, tb_medica.F_DesPro, l.F_ClaLot, DATE_FORMAT( l.F_FecCad, '%d/%m/%Y' ) AS 'F_FecCad', SUM( F_CanCom ) 'F_CantMov', O.F_DesOri, c.F_FolRemi, p.F_NomPro, c.F_OrdCom, DATE_FORMAT( F_FecApl, '%d/%m/%Y' ) AS 'Fecha', tb_medica.F_ClaProSS AS clavess, c.F_ClaDoc, tb_medica.F_PrePro, tb_medica.F_NomGen, IFNULL( ped.F_Contratos, '' ) AS F_Contratos	FROM tb_compra c INNER JOIN tb_proveedor p ON c.F_ClaOrg = p.F_ClaProve LEFT JOIN ( SELECT F_ClaPro, F_FolLot, F_ClaLot, F_FecCad, F_ClaMar, 	F_Origen, F_Proyecto FROM tb_lote WHERE tb_lote.F_ClaLot <> 'X' AND tb_lote.F_ClaPro NOT IN ( SELECT F_ClaPro FROM tb_claves_excluidas ) GROUP BY F_ClaPro, F_FolLot ) l ON c.F_Lote = l.F_FolLot AND c.F_ClaPro = l.F_ClaPro INNER JOIN tb_proyectos pr ON c.F_Proyecto = pr.F_Id LEFT JOIN tb_marca m ON l.F_ClaMar = m.F_ClaMar INNER JOIN tb_medica ON tb_medica.F_ClaPro = c.F_ClaPro INNER JOIN tb_origen AS O ON l.F_Origen = O.F_ClaOri LEFT JOIN ( SELECT P.F_NoCompra, P.F_Clave, P.F_Contratos FROM tb_pedidoisem2017 AS P GROUP BY P.F_NoCompra, P.F_Clave ) AS ped ON ped.F_NoCompra = c.F_OrdCom AND ped.F_Clave = c.F_ClaPro WHERE " + Query + " GROUP BY p.F_NomPro, c.F_ClaPro, l.F_ClaLot, c.F_FecApl, c.F_OrdCom, c.F_ClaDoc, c.F_FolRemi, c.F_User, c.F_Proyecto;";
                   
                    break;
                case "51":
                    if (!"".equals(fecha_ini) && !"".equals(fecha_fin)) {
                        ban1 = 1;
                        fechaFol = " v.FecEnt BETWEEN '" + fecha_ini + "' AND '" + fecha_fin + "' ";
                    }
                    if ((clave != "") && (ban1 != 1) && origen.equals("0")) {
                        Query = " v.clave ='" + clave + "' ";
                    } else if ((clave == "") && (ban1 == 1) && origen.equals("0")) {
                        Query = "" + fechaFol + "";
                    } else if ((clave == "") && (ban1 != 1) && (!origen.equals("0"))) {
                        Query = " v.origen ='" + origen + "' ";
                    } else if ((clave != "") && (ban1 == 1) && origen.equals("0")) {
                        Query = " v.clave ='" + clave + "' AND " + fechaFol + "";
                    } else if ((clave != "") && (ban1 == 1) && (!origen.equals("0"))) {
                        Query = " v.clave ='" + clave + "' AND " + fechaFol + " AND v.origen ='" + origen + "' ";
                    };

                    qry = "SELECT v.FecEnt, v.folio, v.Clave_Unidad, v.Unidad, v.clave, v.F_ClaProSS, v.F_DesPro, v.lote, v.caducidad, v.surtido, v.F_DesOri, v.Nombre_Generico, v.`Presentación`, v.concepto, v.origen FROM v_facturacion AS v WHERE "+Query+"";
                    
                    break;
                default:
                    break;
            }

            aux = 0;

            ps = con.getConn().prepareStatement(qry);
            rsTemp = ps.executeQuery();
            System.out.println(ps);
            while (rsTemp.next()) {
                System.out.println("ps" +  ps);
                aux++;
                index++;
                

                XSSFRow row = sheet.createRow(index);

                switch (clacli) {
                    case "1":
                        row.createCell((int) 0).setCellValue(rsTemp.getString(11));
                        row.createCell((int) 1).setCellValue(rsTemp.getString(1));
                        row.createCell((int) 2).setCellValue(rsTemp.getString(2));
                        row.createCell((int) 3).setCellValue(rsTemp.getString(12));
                        row.createCell((int) 4).setCellValue(rsTemp.getString(15));
                        row.createCell((int) 5).setCellValue(rsTemp.getString(3));
                        row.createCell((int) 6).setCellValue(rsTemp.getString(14));
                        row.createCell((int) 7).setCellValue(rsTemp.getString(4));
                        row.createCell((int) 8).setCellValue(rsTemp.getString(5));
                        row.createCell((int) 9).setCellValue(rsTemp.getString(6));
                        row.createCell((int) 10).setCellValue(rsTemp.getString(7));
                        row.createCell((int) 11).setCellValue(rsTemp.getString(8));
                        row.createCell((int) 12).setCellValue(rsTemp.getString(9));
                        row.createCell((int) 13).setCellValue(rsTemp.getString(10));
                        row.createCell((int) 14).setCellValue(rsTemp.getString(16));                       
                        for (int i = 0; i <= 14; i++) {
                            row.getCell(i).setCellStyle(styleBorder);
                        }
                        break;
                    case "51":
                        row.createCell((int) 0).setCellValue(rsTemp.getString(1));
                        row.createCell((int) 1).setCellValue(rsTemp.getString(2));
                        row.createCell((int) 2).setCellValue(rsTemp.getString(3));
                        row.createCell((int) 3).setCellValue(rsTemp.getString(4));
                        row.createCell((int) 4).setCellValue(rsTemp.getString(5));
                        row.createCell((int) 5).setCellValue(rsTemp.getString(6));
                        row.createCell((int) 6).setCellValue(rsTemp.getString(12));
                        row.createCell((int) 7).setCellValue(rsTemp.getString(7));
                        row.createCell((int) 8).setCellValue(rsTemp.getString(13));
                        row.createCell((int) 9).setCellValue(rsTemp.getString(8));                        
                        row.createCell((int) 10).setCellValue(rsTemp.getString(9));
                        row.createCell((int) 11).setCellValue(rsTemp.getString(10));
                        row.createCell((int) 12).setCellValue(rsTemp.getString(11));
                        row.createCell((int) 13).setCellValue(rsTemp.getString(14));
//                        for (int i = 0; i <= 13; i++) {
//                            row.getCell(i).setCellStyle(styleBorder);
//                        }
                        break;

                    default:
                        break;
                }
            }
            rsTemp.close();
            ps.close();
            //}

            index = 0;

            
            //rowHeadInv.getCell((int) 0).setCellValue(String.format("Total de existencias en %d unidades de atención: %s", unidades.size(), formatter.format(totPiezas)));
            //sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

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
            Logger.getLogger(ExcelConsultaMovCompras.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                response.flushBuffer();
                pw.flush();

                pw.close();
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExcelConsultaMovCompras.class.getName()).log(Level.SEVERE, null, ex);
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
