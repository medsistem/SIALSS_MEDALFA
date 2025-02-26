/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DescargaExcel;

//import com.sun.xml.internal.bind.v2.runtime.output.SAXOutput;
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
 * @author CEDIS TOLUCA3
 */
@WebServlet(name = "ExcelExistenciaProyectoCompraProgama", urlPatterns = {"/ExcelExistenciaProyectoCompraProgama"})
public class ExcelExistenciaProyectoCompraProgama extends HttpServlet {

    private static final double LIMIT = 100000;

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
            String name = "Inventario-Proyecto-%s.xlsx";

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
            String qry2 = null;
            ResultSet rset;

            int index = 0;

            XSSFRow rowHeadInv = sheet.createRow(index);
          
            index = 2;

            rowHeadInv = sheet.createRow(index);
            switch (Consulta) {
                case "1":
                  /**   rowHeadInv.createCell((int) 0).setCellValue("Proyecto");**/
                    rowHeadInv.createCell((int) 0).setCellValue("Clave");
                    rowHeadInv.createCell((int) 1).setCellValue("Descripción específica");
                    rowHeadInv.createCell((int) 2).setCellValue("Cantidad");
                  //  rowHeadInv.createCell((int) 4).setCellValue("Orden de suministro");
                    break;
                case "2":
                   /**   rowHeadInv.createCell((int) 0).setCellValue("Proyecto");**/
                    rowHeadInv.createCell((int) 0).setCellValue("Clave");
                    rowHeadInv.createCell((int) 1).setCellValue("Descripción específica");
                    rowHeadInv.createCell((int) 2).setCellValue("Lote");
                    rowHeadInv.createCell((int) 3).setCellValue("Caducidad");
                    rowHeadInv.createCell((int) 4).setCellValue("Cantidad");
                   // rowHeadInv.createCell((int) 6).setCellValue("Orden de suministro");
                    
               
                    break;
                default:
                    break;
            }

            int aux;
            PreparedStatement ps;
            ResultSet rsTemp;
            //for (int i = 0; i < veces; i++) {
            String AND = "", Ubicaciones = "", ubicapro = "", AND2 = "";
            ArrayList lista = new ArrayList();
            ArrayList lista2 = new ArrayList();

            qry2 = "SELECT F_ClaPro FROM tb_medicaprograma;";
            ps = con.getConn().prepareStatement(qry2);
            rsTemp = ps.executeQuery();
            while (rsTemp.next()) {
                for (int i = 0; i < 1; i++) {
                    for (int j = 0; j < 1; j++) {
                        ubicapro = rsTemp.getString(1);
                        System.out.println("Claves PARA EXCEL: " + ubicapro);
                        lista2.add(i, "'" + ubicapro + "'");
                    }
                }
            }
            ubicapro = lista2.toString().replace("[", "").replace("]", "");
            AND2 = " AND L.F_ClaPro IN (" + ubicapro + ") ";

            if (Tipo.equals("Compra")) {

                qry = "SELECT ubicacion FROM ubicaciones_excluidas;";
                ps = con.getConn().prepareStatement(qry);
                rsTemp = ps.executeQuery();
                while (rsTemp.next()) {
                     for (int i = 0; i < 1; i++) {
                            for (int j = 0; j < 1; j++) {
                                Ubicaciones = rsTemp.getString(1);
                               
                        lista.add(i, "'"+Ubicaciones+"'");
                            }
                        }
                }
                 Ubicaciones = lista.toString().replace("[", "").replace("]", "");
                AND = " AND L.F_Ubica NOT IN (" + Ubicaciones + ") ";
                ps.clearParameters();
            } else {
                AND = "";
            }

           


            
            if (Proyecto.equals("0")) {

                switch (Consulta) {
                    case "1":
                        qry = "SELECT P.F_DesProy, L.F_ClaPro, M.F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica, M.F_ClaProSS,M.F_NomGen,M.F_FormaFarm,M.F_Concentracion,M.F_DesProEsp FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id  WHERE L.F_ExiLot > 0 " + AND + " AND L.F_ClaPro NOT IN ('9999', '9998', '9996', '9995') "+AND2+"  AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_Proyecto;";
                        break;
                    case "2":
                        qry = "SELECT P.F_DesProy, L.F_ClaPro, M.F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica, M.F_ClaProSS,M.F_NomGen,M.F_FormaFarm,M.F_Concentracion,M.F_DesProEsp FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id  WHERE L.F_ExiLot > 0 " + AND + " AND L.F_ClaPro NOT IN ('9999', '9998', '9996', '9995') "+AND2+"  AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";
                        break;
                  
                    default:
                        qry = "SELECT P.F_DesProy, L.F_ClaPro, M.F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica, M.F_ClaProSS,CASE WHEN L.F_Ubica = 'CUARENTENA' THEN 'NO DISPONIBLE' ELSE 'DISPONIBLE' END  AS  Estatus,M.F_NomGen,M.F_FormaFarm,M.F_Concentracion,M.F_DesProEsp FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_ExiLot > 0 " + AND + " AND L.F_ClaPro NOT IN ('9999', '9998', '9996', '9995') "+AND2+"  AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad,  L.F_Proyecto;";
                        break;
                }

            } else {
                switch (Consulta) {
                    case "1":
                        qry = "SELECT P.F_DesProy, L.F_ClaPro, M.F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica , M.F_ClaProSS,M.F_NomGen,M.F_FormaFarm,M.F_Concentracion,M.F_DesProEsp FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_Proyecto = '" + Proyecto + "' AND L.F_ExiLot>0 " + AND + " AND L.F_ClaPro NOT IN ('9999', '9998', '9996', '9995')  "+AND2+" AND L.F_FecCad > CURDATE()  AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_Proyecto;";
                        break;
                    case "2":
                        qry = "SELECT P.F_DesProy, L.F_ClaPro, M.F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica, M.F_ClaProSS,M.F_NomGen,M.F_FormaFarm,M.F_Concentracion,M.F_DesProEsp FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_Proyecto = '" + Proyecto + "' AND L.F_ExiLot>0 " + AND + " AND L.F_ClaPro NOT IN ('9999', '9998', '9996', '9995') "+AND2+" AND L.F_FecCad > CURDATE()  AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Origen, L.F_Proyecto;";
                        break;
                   
                    default:
                        qry = "SELECT P.F_DesProy, L.F_ClaPro, M.F_DesPro, L.F_ClaLot, DATE_FORMAT(L.F_FecCad, '%d/%m/%Y') AS F_FecCad, SUM(L.F_ExiLot) AS F_ExiLot, O.F_DesOri, L.F_Ubica, M.F_ClaProSS,M.F_NomGen,M.F_FormaFarm,M.F_Concentracion,M.F_DesProEsp FROM tb_lote L INNER JOIN tb_medica M ON L.F_ClaPro = M.F_ClaPro INNER JOIN tb_origen O ON L.F_Origen = O.F_ClaOri INNER JOIN tb_proyectos P ON L.F_Proyecto = P.F_Id WHERE L.F_Proyecto = '" + Proyecto + "' AND L.F_ExiLot>0 " + AND + " AND L.F_ClaPro NOT IN ('9999', '9998', '9996', '9995') "+AND2+" AND L.F_FecCad > CURDATE()  AND M.F_StsPro = 'A' GROUP BY L.F_ClaPro, L.F_ClaLot, L.F_FecCad, L.F_Proyecto;";                       
                        break;
                }

            }

            aux = 0;
            System.out.println(qry);
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
                         /*  row.createCell((int) 0).setCellValue(rsTemp.getString(1));*/
                        row.createCell((int) 0).setCellValue(rsTemp.getString(2));
                        row.createCell((int) 1).setCellValue(rsTemp.getString(3));
                        row.createCell((int) 2).setCellValue(formatter.format(rsTemp.getInt(6)));
                       // row.createCell((int) 4).setCellValue(rsTemp.getString(14));
                        break;
                    case "2":
                      /*  row.createCell((int) 0).setCellValue(rsTemp.getString(1));*/
                        row.createCell((int) 0).setCellValue(rsTemp.getString(2));
                        row.createCell((int) 1).setCellValue(rsTemp.getString(3));
                        row.createCell((int) 2).setCellValue(rsTemp.getString(4));
                        row.createCell((int) 3).setCellValue(rsTemp.getString(5));
                        row.createCell((int) 4).setCellValue(formatter.format(rsTemp.getInt(6)));
                    //    row.createCell((int) 6).setCellValue(rsTemp.getString(14));
                        break;
                 
                    default:
                        /*row.createCell((int) 0).setCellValue(rsTemp.getString(1));*/
                        row.createCell((int) 0).setCellValue(rsTemp.getString(2));
                        row.createCell((int) 1).setCellValue(rsTemp.getString(3));
                        row.createCell((int) 2).setCellValue(rsTemp.getString(4));
                        row.createCell((int) 3).setCellValue(rsTemp.getString(7));
                        row.createCell((int) 4).setCellValue(rsTemp.getString(11));
                        row.createCell((int) 5).setCellValue(rsTemp.getString(12));
                        row.createCell((int) 6).setCellValue(rsTemp.getString(13));
                     //   row.createCell((int) 8).setCellValue(rsTemp.getString(14));
                        break;
                }
            }
            rsTemp.close();
            ps.close();
            //}

            index = 0;

            rowHeadInv = sheet.getRow(index);
             sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

            try (FileOutputStream fileOut = new FileOutputStream(filename)) {
                wb.write(fileOut);
            }
            wb.close();
            String disHeader = "Attachment;Filename=\"" + name + "\"";
            response.setHeader("Content-Disposition", disHeader);
            File desktopFile = new File(filename);
            System.out.println("Va comenzar escritura. existencia programas");
            try (FileInputStream fileInputStream = new FileInputStream(desktopFile)) {
                int j;
                while ((j = fileInputStream.read()) != -1) {
                    pw.write(j);
                }
                fileInputStream.close();
            }
        } catch (Exception ex) {
            Logger.getLogger(ExcelExistenciaProyectoCompraProgama.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                response.flushBuffer();
                pw.flush();

                pw.close();
                con.cierraConexion();
            } catch (Exception ex) {
                Logger.getLogger(ExcelExistenciaProyectoCompraProgama.class.getName()).log(Level.SEVERE, null, ex);
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
