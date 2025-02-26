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
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 *
 * @author Anibal GNKL
 */
@WebServlet(name = "ExcelFacturado", urlPatterns = {"/ExcelFacturado"})
public class ExcelFacturado extends HttpServlet {

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
        String Fechafactura = request.getParameter("Fechafactura");
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
            String QueryProyecto = "SELECT F_Tipo FROM tb_uniatn WHERE F_Tipo = ? GROUP BY F_Tipo;";
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
            String name = "Facturado-%s.xlsx";

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

            List<String> unidades = new ArrayList<>();
            int totPiezas = 0, veces = 0;
            String qry = null, auxLimit;
            ResultSet rset;

            int index = 0;

            XSSFRow rowHeadInv = sheet.createRow(index);
            //rowHeadInv.createCell((int) 0).setCellValue(String.format("Total de existencias en %d unidades de atención: %s", unidades.size(), formatter.format(totPiezas)));

            index = 2;

            rowHeadInv = sheet.createRow(index);
            if (Proyecto.equals("0")) {
                rowHeadInv.createCell((int) 0).setCellValue("Clave");
                rowHeadInv.createCell((int) 1).setCellValue("Cantidad");
            } else {
                rowHeadInv.createCell((int) 0).setCellValue("Clave");
                rowHeadInv.createCell((int) 1).setCellValue("Cantidad");
                rowHeadInv.createCell((int) 2).setCellValue("Tipo");
            }

            int aux;
            PreparedStatement ps;
            ResultSet rsTemp;

            if (Proyecto.equals("0")) {
                qry = "SELECT F_ClaPro, SUM(F_Solicitado) AS SURTIDO, U.F_Tipo FROM tb_unireq F INNER JOIN tb_uniatn U ON F.F_ClaUni = U.F_ClaCli WHERE F_FecCarg = '" + Fechafactura + "' AND F_Status = 0 GROUP BY F_ClaPro HAVING SURTIDO > 0;";
            } else {
                qry = "SELECT F_ClaPro, SUM(F_Solicitado) AS SURTIDO, U.F_Tipo FROM tb_unireq F INNER JOIN tb_uniatn U ON F.F_ClaUni = U.F_ClaCli WHERE F_FecCarg = '" + Fechafactura + "' AND F_Tipo = '" + Proyecto + "' AND F_Status = 0 GROUP BY F_ClaPro HAVING SURTIDO > 0;";
            }

            aux = 0;

            ps = con.getConn().prepareStatement(qry);
            rsTemp = ps.executeQuery();
            System.out.println(ps);
            while (rsTemp.next()) {
                aux++;
                index++;
                totPiezas += rsTemp.getInt(2);

                XSSFRow row = sheet.createRow(index);

                if (Proyecto.equals("0")) {
                    row.createCell((int) 0).setCellValue(rsTemp.getString(1));
                    row.createCell((int) 1).setCellValue(formatter.format(rsTemp.getInt(2)));
                } else {
                    row.createCell((int) 0).setCellValue(rsTemp.getString(1));
                    row.createCell((int) 1).setCellValue(formatter.format(rsTemp.getInt(2)));
                    row.createCell((int) 2).setCellValue(rsTemp.getString(3));
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
            Logger.getLogger(ExcelFacturado.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                response.flushBuffer();
                pw.flush();

                pw.close();
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExcelFacturado.class.getName()).log(Level.SEVERE, null, ex);
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
