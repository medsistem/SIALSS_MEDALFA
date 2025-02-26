/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Impresiones;

//import NumeroLetra.Numero_a_Letra;
import conn.ConectionDB;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JasperRunManager;

/**
 *
 * @author MEDALFA
 */
@WebServlet(name = "ImprimeFolio", urlPatterns = {"/ImprimeFolio"})
public class ImprimeFolio extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        HttpSession sesion = request.getSession(true);
        DecimalFormat df = new DecimalFormat("#,###.00");
        ServletContext context = request.getServletContext();
        String usua = "";
        int SumaMedReq = 0, SumaMedSur = 0, SumaMedReqT = 0, SumaMedSurT = 0, Diferencia = 0;
        double MontoMed = 0.0, MontoTMed = 0.0, Costo = 0.0;
        String Unidad = "", Fecha = "", Direc = "", F_FecApl = "", F_Obs = "", F_Obs2 = "", Razon = "", Proyecto = "", Jurisdiccion = "", Municipio = "";
        int SumaMatReq = 0, SumaMatSur = 0, SumaMatReqT = 0, SumaMatSurT = 0;
        double MontoMat = 0.0, MontoTMat = 0.0;
        int RegistroC = 0, Ban = 0, HojasC = 0, HojasR = 0, Origen = 0, ContarControlado = 0;
        String DesV = "", Letra = "", Contrato = "", OC = "", Nomenclatura = "", Encabezado = "", RedFria = "", Imgape = "", NoImgApe = "", ImagenControlado = "", CargoResponsable = "", NombreResponsable = "";
        double Hoja = 0.0, Hoja2 = 0.0;

        int TotalReq = 0, TotalSur = 0, ContarRedF = 0, Contarape = 0;
        double TotalMonto = 0.0, MTotalMonto = 0.0, Iva = 0.0;
        String remis = request.getParameter("fol_gnkl");
        String ProyectoF = request.getParameter("Proyecto");
        String IdProyecto = request.getParameter("idProyecto");
        String sts = request.getParameter("idsts");
        int BanDato = Integer.parseInt(request.getParameter("BanDato"));
        int TipBanDato = Integer.parseInt(request.getParameter("TipBanDato"));
        usua = (String) sesion.getAttribute("nombre");
        ConectionDB con = new ConectionDB();
        Connection conexion;
        String agrupacion = "";
       
        if (sts == null) {
            sts = "C";
        }
        
        File reportFile = null;
         System.out.println("STS: "+ sts);
        try {
            conexion = con.getConn();

            ResultSet RsetNomenc = con.consulta("SELECT F_Nomenclatura, F_Encabezado FROM tb_proyectos WHERE F_Id='" + IdProyecto + "';");
            while (RsetNomenc.next()) {
                Nomenclatura = RsetNomenc.getString(1);
                Encabezado = RsetNomenc.getString(2);
            }

            //Eliminar registros de folios anteriores para la misma unidad médica
            ResultSet claCliente = con.consulta("SELECT F_ClaCli FROM tb_factura WHERE F_ClaDoc = '" + remis + "'");
            while (claCliente.next()) {
                String clave = claCliente.getString(1);
                con.actualizar("DELETE FROM tb_imprefolio WHERE F_Folio='" + remis + "';");
                break;
            }

            claCliente = con.consulta("Select o.F_TipOri from tb_lote l inner join tb_origen o on l.F_Origen = o.F_ClaOri inner join tb_factura f on f.F_Lote = l.F_FolLot where F_ClaDoc = '" + remis + "' Limit 1");

            while (claCliente.next()) {
                agrupacion = claCliente.getString(1);
                break;
            }

            ResultSet ObsFact = con.consulta("SELECT CONCAT(F_Obser, ' - ', F_Tipo) AS F_Obser FROM tb_obserfact WHERE F_IdFact='" + remis + "' AND F_Proyecto = '" + IdProyecto + "' GROUP BY F_IdFact;");
            while (ObsFact.next()) {
                F_Obs = ObsFact.getString(1);
            }

            if (agrupacion.equals("ADMINISTRACION")) {
                agrupacion = "ADMINISTRACIÓN - INSABI";
            } else {
                agrupacion = "AR";
            }

//            F_Obs+="\nTIPO: "+agrupacion;
            ResultSet RsetControlado = con.consulta("SELECT F.F_ClaDoc, COUNT(*) AS CONTAR, IFNULL(FC.CONTARCONTROLADO, 0) AS CONTARCONTROLADO, COUNT(*) - IFNULL(FC.CONTARCONTROLADO, 0) AS DIF FROM tb_factura F LEFT JOIN ( SELECT F_ClaDoc, COUNT(*) AS CONTARCONTROLADO FROM tb_factura WHERE F_ClaDoc = '" + remis + "' AND F_Proyecto = '" + IdProyecto + "' AND F_StsFact= 'A' AND (F_Ubicacion RLIKE 'CONTROLADO|CTRFO' AND F_ClaPro IN (SELECT F_ClaPro FROM tb_controlados)) AND F_CantSur > 0) FC ON F.F_ClaDoc = FC.F_ClaDoc WHERE F.F_ClaDoc = '" + remis + "' AND F_Proyecto = '" + IdProyecto + "' AND F_CantSur > 0 GROUP BY F.F_ClaDoc;");
            if (RsetControlado.next()) {
                ContarControlado = RsetControlado.getInt(3);
            }
            System.out.println("contarcontrolado: "+ContarControlado);
            if (ContarControlado > 0) {
                ImagenControlado = "image/Controlado.jpg";
                NoImgApe = "image/no_imgape.jpg";
                Imgape = NoImgApe;
                RedFria = "image/Nored_fria.jpg";

                ResultSet RsetUsuCargo = con.consulta("SELECT uc.Usuario_Nombre, uc.Cargo, uc.Nomeclatura_Usu FROM tb_usuariocargo AS uc WHERE uc.`Status` = 1 AND uc.IdTipoUsu = 16;");
                if (RsetUsuCargo.next()) {

                    CargoResponsable = RsetUsuCargo.getString(2);
                    NombreResponsable = RsetUsuCargo.getString(3) + ' ' + RsetUsuCargo.getString(1);
                }

            } else {
                ImagenControlado = "image/NoControlado.jpg";
                CargoResponsable = " ";
                NombreResponsable = " ";

            }
                    ResultSet DatosRedF = con.consulta("SELECT COUNT(*) FROM tb_redfria r INNER JOIN tb_factura f ON r.F_ClaPro = f.F_ClaPro WHERE F_StsFact = 'A' AND F_ClaDoc = '" + remis + "' AND F_CantSur > 0 AND F_Proyecto = '" + IdProyecto + "';");
                if (DatosRedF.next()) {
                    ContarRedF = DatosRedF.getInt(1);
                }
                if (ContarRedF > 0) {
                    RedFria = "image/red_fria.jpg";
                } else {
                    RedFria = "image/Nored_fria.jpg";
                }

                ResultSet DatosAPE = con.consulta("SELECT COUNT(*) FROM tb_ape ap INNER JOIN tb_factura f ON ap.F_ClaPro = f.F_ClaPro WHERE F_StsFact = 'A' AND F_ClaDoc = '" + remis + "' AND F_CantSur > 0 AND F_Proyecto = '" + IdProyecto + "';");
                if (DatosAPE.next()) {
                    Contarape = DatosAPE.getInt(1);
                }
                NoImgApe = "image/no_imgape.jpg";
                if (Contarape > 0) {
                    Imgape = "image/imgape.png";
                } else {

                    Imgape = NoImgApe;
                }

            /////////////////////////////////////
            /////////proyecto isem///////
            /////////////////////////////////
//            if (ProyectoF.equals("1")) {

                if (BanDato == 0) {
                    System.out.println("entro a cero");
                        reportFile = new File(context.getRealPath("/reportes/ImprimeFoliosIsemCero.jasper"));
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", usua);
                        parameters.put("F_Obs", F_Obs);
                        parameters.put("Imgape", Imgape);
                        parameters.put("RedFria", RedFria);
                        parameters.put("TipoInsumo", BanDato);
                        parameters.put("BanTip", TipBanDato);
                        parameters.put("ImagenControlado", ImagenControlado);
                        parameters.put("CargoResponsable", CargoResponsable);
                        parameters.put("NombreResponsable", NombreResponsable);
                        byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conexion);
                        response.setContentType("application/pdf");
                        response.setContentLength(bytes.length);
                        ServletOutputStream ouputStream = response.getOutputStream();
                        ouputStream.write(bytes, 0, bytes.length);
                        ouputStream.flush();
                        ouputStream.close();

                }
                //********* NORMAL *************
                if (BanDato == 1) {

                    ResultSet ContarDatos = con.consulta("SELECT F.F_ClaDoc, COUNT(*) AS CONTARFACT, IFNULL(C.CONTARC, 0) AS CONTARREG, ( COUNT(*) - IFNULL(C.CONTARC, 0)) AS DIF FROM tb_factura F LEFT JOIN ( SELECT F_ClaDoc, COUNT(*) AS CONTARC FROM tb_factura WHERE F_ClaDoc = '" + remis + "' AND F_ClaPro IN ('9999', '9998', '9996', '9995') ) AS C ON F.F_ClaDoc = C.F_ClaDoc WHERE F.F_ClaDoc = '" + remis + "';");
                    if (ContarDatos.next()) {
                        Diferencia = ContarDatos.getInt(4);
                    }
                    if (Diferencia == 0) {
                        ///controlado   
                        Ban = 3;
                    } else {
                        Ban = 1;
                        Integer cantControlado = ContarDatos.getInt("CONTARREG");
                        if (cantControlado > 0) {
                            ImagenControlado = "image/Controlado.jpg";
                        }
                    }
                    /*Establecemos la ruta del reporte*/
                    if (Ban == 1) {
                        if (sts.equals("C")) {
                            System.out.println("Cancelados");
                            reportFile = new File(context.getRealPath("/reportes/ImprimeFoliosCancelados.jasper"));
                          
                        } else {
                              reportFile = new File(context.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                        }
                       // File reportFile = new File(context.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                        /* No enviamos parámetros porque nuestro reporte no los necesita asi que escriba 
     cualquier cadena de texto ya que solo seguiremos el formato del método runReportToPdf*/
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", usua);
                        parameters.put("F_Obs", F_Obs);
                        parameters.put("Imgape", Imgape);
                        parameters.put("RedFria", RedFria);
                        parameters.put("TipoInsumo", BanDato);
                        parameters.put("ImagenControlado", ImagenControlado);
                        parameters.put("CargoResponsable", CargoResponsable);
                        parameters.put("NombreResponsable", NombreResponsable);
                        /*Enviamos la ruta del reporte, los parámetros y la conexión(objeto Connection)*/
                        byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conexion);
                        /*Indicamos que la respuesta va a ser en formato PDF*/ response.setContentType("application/pdf");
                        response.setContentLength(bytes.length);
                        ServletOutputStream ouputStream = response.getOutputStream();
                        ouputStream.write(bytes, 0, bytes.length);
                        /*Limpiamos y cerramos flujos de salida*/ ouputStream.flush();
                        ouputStream.close();
                    } else if (Ban == 3) {
                        reportFile = new File(context.getRealPath("/reportes/ImprimeFoliosIsemReceta.jasper"));
                        /* No enviamos parámetros porque nuestro reporte no los necesita asi que escriba 
     cualquier cadena de texto ya que solo seguiremos el formato del método runReportToPdf*/
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", usua);
                        parameters.put("F_Obs", F_Obs);
                        parameters.put("RedFria", RedFria);
                        parameters.put("ImagenControlado", ImagenControlado);
                        parameters.put("CargoResponsable", CargoResponsable);
                        parameters.put("NombreResponsable", NombreResponsable);
                        /*Enviamos la ruta del reporte, los parámetros y la conexión(objeto Connection)*/
                        byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conexion);
                        /*Indicamos que la respuesta va a ser en formato PDF*/ response.setContentType("application/pdf");
                        response.setContentLength(bytes.length);
                        ServletOutputStream ouputStream = response.getOutputStream();
                        ouputStream.write(bytes, 0, bytes.length);
                        /*Limpiamos y cerramos flujos de salida*/ ouputStream.flush();
                        ouputStream.close();
                    }
                }

                //*****CONTROLADO *****/
                if (BanDato == 4) {
                     if (sts.equals("C")) {
                            System.out.println("Cancelados");
                            reportFile = new File(context.getRealPath("/reportes/ImprimeFoliosCancelados.jasper"));
                          
                        } else {
                              reportFile = new File(context.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                        }
//                    File reportFile = new File(context.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                    /* No enviamos parámetros porque nuestro reporte no los necesita asi que escriba 
     cualquier cadena de texto ya que solo seguiremos el formato del método runReportToPdf*/
                    Map parameters = new HashMap();
                    parameters.put("Folfact", remis);
                    parameters.put("Usuario", usua);
                    parameters.put("F_Obs", F_Obs);
                    parameters.put("Imgape", Imgape);
                    parameters.put("RedFria", RedFria);
                    parameters.put("TipoInsumo", BanDato);
                    parameters.put("BanTip", TipBanDato);
                    parameters.put("ImagenControlado", ImagenControlado);
                    parameters.put("CargoResponsable", CargoResponsable);
                    parameters.put("NombreResponsable", NombreResponsable);
                    /*Enviamos la ruta del reporte, los parámetros y la conexión(objeto Connection)*/
                    byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conexion);
                    /*Indicamos que la respuesta va a ser en formato PDF*/ response.setContentType("application/pdf");
                    response.setContentLength(bytes.length);
                    ServletOutputStream ouputStream = response.getOutputStream();
                    ouputStream.write(bytes, 0, bytes.length);
                    /*Limpiamos y cerramos flujos de salida*/ ouputStream.flush();
                    ouputStream.close();

                }

                //*********Redfria
                if (BanDato == 2) {
                    RedFria = "image/red_fria.jpg";
                    Imgape = "image/no_imgape.jpg";

                    Ban = 1;

                    /*Establecemos la ruta del reporte*/
                    if (Ban == 1) {
                      if (sts.equals("C")) {
                           System.out.println("Cancelados");
                            reportFile = new File(context.getRealPath("/reportes/ImprimeFoliosCancelados.jasper"));
                          
                        } else {
                              reportFile = new File(context.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                        }
                        /* No enviamos parámetros porque nuestro reporte no los necesita asi que escriba 
     cualquier cadena de texto ya que solo seguiremos el formato del método runReportToPdf*/
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", usua);
                        parameters.put("F_Obs", F_Obs);
                        parameters.put("Imgape", Imgape);
                        parameters.put("RedFria", RedFria);
                        parameters.put("TipoInsumo", BanDato);
                        parameters.put("BanTip", TipBanDato);
                        parameters.put("ImagenControlado", ImagenControlado);
                        parameters.put("CargoResponsable", CargoResponsable);
                        parameters.put("NombreResponsable", NombreResponsable);
                        /*Enviamos la ruta del reporte, los parámetros y la conexión(objeto Connection)*/
                        byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conexion);
                        /*Indicamos que la respuesta va a ser en formato PDF*/ response.setContentType("application/pdf");
                        response.setContentLength(bytes.length);
                        ServletOutputStream ouputStream = response.getOutputStream();
                        ouputStream.write(bytes, 0, bytes.length);
                        /*Limpiamos y cerramos flujos de salida*/ ouputStream.flush();
                        ouputStream.close();
                    }
                }

                //*********APE
                if (BanDato == 3) {

                    Imgape = "image/imgape.png";
                    RedFria = "image/Nored_fria.jpg";

                    Ban = 1;
                    if (Ban == 1) {
                        if (sts.equals("C")) {
                             System.out.println("Cancelados");
                            reportFile = new File(context.getRealPath("/reportes/ImprimeFoliosCancelados.jasper"));
                          
                        } else {
                              reportFile = new File(context.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                        }
                        /* No enviamos parámetros porque nuestro reporte no los necesita asi que escriba 
     cualquier cadena de texto ya que solo seguiremos el formato del método runReportToPdf*/
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", usua);
                        parameters.put("F_Obs", F_Obs);
                        parameters.put("Imgape", Imgape);
                        parameters.put("RedFria", RedFria);
                        parameters.put("TipoInsumo", BanDato);
                        parameters.put("BanTip", TipBanDato);
                        parameters.put("ImagenControlado", ImagenControlado);
                        parameters.put("CargoResponsable", CargoResponsable);
                        parameters.put("NombreResponsable", NombreResponsable);
                        /*Enviamos la ruta del reporte, los parámetros y la conexión(objeto Connection)*/
                        byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conexion);
                        /*Indicamos que la respuesta va a ser en formato PDF*/ response.setContentType("application/pdf");
                        response.setContentLength(bytes.length);
                        ServletOutputStream ouputStream = response.getOutputStream();
                        ouputStream.write(bytes, 0, bytes.length);
                        /*Limpiamos y cerramos flujos de salida*/ ouputStream.flush();
                        ouputStream.close();
                    }
                }

                    if (BanDato == 6) {
                        System.out.println("entro a fonsabi");
                    
                        if (sts.equals("C")) {
                            System.out.println("Cancelados");
                            reportFile = new File(context.getRealPath("/reportes/ImprimeFoliosCancelados.jasper"));
                          
                        } else {
                              reportFile = new File(context.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                        }                                                                                                                                                                                                        
                        /* No enviamos parámetros porque nuestro reporte no los necesita asi que escriba 
     cualquier cadena de texto ya que solo seguiremos el formato del método runReportToPdf*/
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", usua);
                        parameters.put("F_Obs", F_Obs);
                        parameters.put("Imgape", Imgape);
                        parameters.put("RedFria", RedFria);
                          parameters.put("TipoInsumo", TipBanDato);
                        parameters.put("BanTip", BanDato);
                        parameters.put("ImagenControlado", ImagenControlado);
                        parameters.put("CargoResponsable", CargoResponsable);
                        parameters.put("NombreResponsable", NombreResponsable);
                        /*Enviamos la ruta del reporte, los parámetros y la conexión(objeto Connection)*/
                        byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conexion);
                        /*Indicamos que la respuesta va a ser en formato PDF*/ response.setContentType("application/pdf");
                        response.setContentLength(bytes.length);
                        ServletOutputStream ouputStream = response.getOutputStream();
                        ouputStream.write(bytes, 0, bytes.length);
                        /*Limpiamos y cerramos flujos de salida*/ ouputStream.flush();
                        ouputStream.close();
                    
                }
                        if (BanDato == 7) {
                        System.out.println("entro a onco");
                    
                        if (sts.equals("C")) {
                             System.out.println("Cancelados");
                            reportFile = new File(context.getRealPath("/reportes/ImprimeFoliosCancelados.jasper"));
                          
                        } else {
                              reportFile = new File(context.getRealPath("/reportes/ImprimeFoliosIsem.jasper"));
                        }
                        /* No enviamos parámetros porque nuestro reporte no los necesita asi que escriba 
     cualquier cadena de texto ya que solo seguiremos el formato del método runReportToPdf*/
                        Map parameters = new HashMap();
                        parameters.put("Folfact", remis);
                        parameters.put("Usuario", usua);
                        parameters.put("F_Obs", F_Obs);
                        parameters.put("Imgape", Imgape);
                        parameters.put("RedFria", RedFria);
                        parameters.put("TipoInsumo", TipBanDato);
                        parameters.put("BanTip", BanDato);
                        parameters.put("ImagenControlado", ImagenControlado);
                        parameters.put("CargoResponsable", CargoResponsable);
                        parameters.put("NombreResponsable", NombreResponsable);
                        /*Enviamos la ruta del reporte, los parámetros y la conexión(objeto Connection)*/
                        byte[] bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, conexion);
                        /*Indicamos que la respuesta va a ser en formato PDF*/ response.setContentType("application/pdf");
                        response.setContentLength(bytes.length);
                        ServletOutputStream ouputStream = response.getOutputStream();
                        ouputStream.write(bytes, 0, bytes.length);
                        /*Limpiamos y cerramos flujos de salida*/ ouputStream.flush();
                        ouputStream.close();
                    
                }

//                   
//            }
            conexion.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private static boolean band() {
//        if (Math.random() > .5) {
//            return true;
//        } else {
//            return false;
//        }
//    }

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
