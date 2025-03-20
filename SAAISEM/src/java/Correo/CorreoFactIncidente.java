package Correo;

import conn.ConectionDB;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;



public class CorreoFactIncidente extends HttpServlet {

    ConectionDB obj = new ConectionDB();
private ResultSet rset = null;
    public void enviaCorreoFactIncidente(int folioInct) {

        try {
            /* TODO output your page here. You may use following sample code. */
            Properties props = new Properties();
            props.setProperty("mail.smtp.host", "smtp.gmail.com");
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.smtp.port", "587");
            props.setProperty("mail.smtp.user", "medalfawms2@gmail.com");
            props.setProperty("mail.smtp.auth", "true");

            // Preparamos la sesion
            Session session = Session.getDefaultInstance(props);

            // Construimos el mensaje
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("medalfawms2@gmail.com"));
            
//            message.addRecipient(Message.RecipientType.CC, new InternetAddress("norma.hernandez@medalfa.mx"));
//             message.addRecipient(Message.RecipientType.BCC, new InternetAddress("supervisor.controlados@medalfa.mx"));
//            message.addRecipient(Message.RecipientType.BCC, new InternetAddress("luz.marchan@medalfa.mx"));

            /******QA*****/
             message.addRecipient(Message.RecipientType.CC, new InternetAddress("nidia.leon@medalfa.mx"));
//            message.addRecipient(Message.RecipientType.BCC, new InternetAddress("brenda.santana@medalfa.mx"));
           
            message.setSubject("Incidencia en Folios de Remision / MEDALFA");

             
            String errorubica="ERROR EN LA UBICACION";
            String errortabla="ERROR EN CATALOGO";
            String body="";
            String mensaje ="",menAPE ="", menRF="",menCTR="",menONCAPE="",menONCRF="";
            String fol = "",cla_cli = "",nom_cli = "",clave = "", clavess = "",  lote = "", caducidad = "",ubica = "",tip_folio = "";            
            int req = 0, sur = 0,rf= 0,ctr= 0,ape= 0,oncape= 0,oncrf= 0;
            
            try {
                obj.conectar();
                
            rset = obj.consulta("SELECT F.F_ClaDoc as folio,CASE WHEN F.F_Ubicacion LIKE '%REDFRIA%' THEN CONCAT(F.F_ClaDoc,'-RF') WHEN F.F_Ubicacion RLIKE  '(APE|(APE|ES)(1|2|3|PAL|URGENTE|CDist))' THEN CONCAT(F.F_ClaDoc,'-APE') WHEN ((SELECT COUNT(*) from tb_controlados ctr where ctr.F_ClaPro = F.F_ClaPro) > 0 && F.F_Ubicacion LIKE '%CONTROLADO%')  THEN  CONCAT(F.F_ClaDoc,'-CTR') ELSE CONCAT(F.F_ClaDoc) END Folio_Tipo,F.F_ClaCli as Clave_Cliente,U.F_NomCli as Nombre_Cliente, F.F_ClaPro as Clave, M.F_ClaProSS as Clave_ISEM, M.F_NomGen, M.F_DesProEsp  AS F_Descripion, M.F_PrePro AS F_Presentacion,L.F_ClaLot as Lote, DATE_FORMAT( L.F_FecCad, '%d/%m/%Y' ) AS Caducidad, F.F_CantReq AS Requerido,	F.F_CantSur AS Surtido,	F.F_Ubicacion as Ubicacion, CASE WHEN F.F_Ubicacion RLIKE 'REDFRIA|RFFO' && RF.F_ClaPro IS NOT NULL THEN 2 WHEN F.F_Ubicacion NOT RLIKE 'REDFRIA|RFFO' && RF.F_ClaPro IS NOT NULL THEN 'ERROR DE UBICACION' WHEN F.F_Ubicacion RLIKE 'REDFRIA|RFFO' && RF.F_ClaPro IS NULL THEN 'ERROR DE CATALOGO' ELSE 0 END AS RedFria, CASE WHEN F.F_Ubicacion RLIKE '(APE|(APE|ES)(1|2|3|PAL|URGENTE|CDist))' && A.F_ClaPro IS NOT NULL THEN 3 WHEN F.F_Ubicacion NOT RLIKE '(APE|(APE|ES)(1|2|3|PAL|URGENTE|CDist))' && A.F_ClaPro IS NOT NULL THEN 10 WHEN F.F_Ubicacion RLIKE '(APE|(APE|ES)(1|2|3|PAL|URGENTE|CDist))' && A.F_ClaPro IS NULL THEN 11 ELSE 0 END AS APE,CASE WHEN  F.F_Ubicacion RLIKE 'CONTROLADO|CTRFO' && CTR.F_ClaPro IS NOT NULL THEN 4 WHEN  F.F_Ubicacion NOT RLIKE 'CONTROLADO|CTRFO' && CTR.F_ClaPro IS NOT NULL THEN 10 WHEN  F.F_Ubicacion RLIKE 'CONTROLADO|CTRFO' && CTR.F_ClaPro IS NULL THEN 11 ELSE 0 END AS Controlado, CASE WHEN  F.F_Ubicacion RLIKE 'ONCOAPE' && onc.F_ClaPro IS NOT NULL THEN 7 WHEN F.F_Ubicacion NOT RLIKE 'ONCOAPE' && onc.F_ClaPro IS NOT NULL THEN 10 WHEN F.F_Ubicacion RLIKE 'ONCOAPE' && onc.F_ClaPro IS NULL THEN 11  ELSE 0 END AS OncoAPE,CASE WHEN  F.F_Ubicacion RLIKE 'ONCORF' && onc.F_ClaPro IS NOT NULL THEN 8 WHEN F.F_Ubicacion NOT RLIKE 'ONCORF' && onc.F_ClaPro IS NOT NULL THEN 10 WHEN F.F_Ubicacion RLIKE 'ONCORF' && onc.F_ClaPro IS NULL THEN 11 ELSE 0 END AS OncoRF, CASE WHEN F.F_Ubicacion RLIKE 'REDFRIA|RFFO' THEN 2 WHEN  F.F_Ubicacion RLIKE '(FACFO|APE|(APE|ES)(1|2|3|PAL|URGENTE|CDist))' THEN 3 WHEN F.F_Ubicacion RLIKE 'CONTROLADO|CTRFO' THEN 4	WHEN  F.F_Ubicacion RLIKE 'ONCOAPE' THEN 7 WHEN  F.F_Ubicacion RLIKE 'ONCORF' THEN 8	ELSE 1 END tipoMed, CASE WHEN L.F_Origen = '19' THEN 'FONSABI' ELSE 'NORMAL' END AS Tipo_Folio FROM tb_factura F INNER JOIN tb_lote L ON F.F_Lote = L.F_FolLot	AND F.F_Ubicacion = L.F_Ubica AND F.F_ClaPro = L.F_ClaPro INNER JOIN tb_uniatn U ON F.F_ClaCli = U.F_ClaCli INNER JOIN tb_medica M ON F.F_ClaPro = M.F_ClaPro LEFT JOIN tb_redfria RF ON F.F_ClaPro = RF.F_ClaPro LEFT JOIN tb_ape A ON A.F_ClaPro = F.F_ClaPro LEFT JOIN tb_controlados CTR ON CTR.F_ClaPro = F.F_ClaPro INNER JOIN tb_origen o ON L.F_Origen = o.F_ClaOri LEFT JOIN tb_onco onc ON onc.F_ClaPro = F.F_ClaPro WHERE F_DocAnt != '1' AND F.F_Proyecto = '1' AND F_CantSur > 0 AND F.F_ClaDoc = "+folioInct+" GROUP BY F.F_ClaPro,L.F_ClaLot,L.F_FecCad,tipoMed HAVING RedFria IN (10,11) OR APE IN (10,11) OR Controlado IN (10,11) ORDER BY F.F_ClaPro ASC,L.F_ClaLot ASC,L.F_FecCad ASC;");
                System.out.println("Folio que se recibe: "+folioInct);
                  System.out.println("QUERY: ");
                
                while (rset.next()) {
                      System.out.println("si entro al ciclo de correo para incidencia");
                      
                      fol = rset.getString(2);
//                      System.out.println(fol);
                      cla_cli =  rset.getString(3);
//                      System.out.println(cla_cli);
                      nom_cli = rset.getString(4);
//                      System.out.println(nom_cli);
                      clave = rset.getString(5);
                      System.out.println(clave);
                      clavess = rset.getString(6);
//                      System.out.println(clavess);
                      lote = rset.getString(10);
//                      System.out.println(lote);
                      caducidad = rset.getString(11);
//                      System.out.println(caducidad);
                      req = rset.getInt(13);
//                      System.out.println(req);
                      sur = rset.getInt(12);   
//                      System.out.println(sur);
                      ubica = rset.getString(14);
//                      System.out.println(ubica);
                      rf = rset.getInt(15);
//                      System.out.println(rf);
                      ape = rset.getInt(16);
//                      System.out.println(ape);
                      ctr = rset.getInt(17);       
//                      System.out.println(ctr);
                      oncape = rset.getInt(18);
//                      System.out.println(oncape);
                      oncrf = rset.getInt(19);
//                      System.out.println(oncrf);
                      
                      System.out.println("contorlado: "+ctr+" ape: "+ape+" rf: "+rf+" oncape: "+oncape+" oncrf: "+oncrf);
                    
                    if (ctr == 10 || rf == 10 || ape == 10 || oncape == 10 || oncape == 10) {
                        menAPE = "";
                        menAPE += "<tr><td>" +  clave + "</td><td>" + clavess +"</td><td>" +lote + "</td><td>" + caducidad + "</td><td>" + req + "</td><td>" + sur + "</td><td>" + errorubica + "</td><td>" + ubica + "</td></tr>";
                     System.out.println("Mensajes en ubicacion:  "+menAPE);
                         mensaje += menAPE;
                    }
                    if (ctr == 11 || rf == 11 || ape == 11 || oncape == 11 || oncape == 11) {
                        menRF = "";
                       menRF += "<tr><td>" +  clave + "</td><td>" + clavess +"</td><td>" +lote + "</td><td>" + caducidad + "</td><td>" + req + "</td><td>" + sur + "</td><td>" + errortabla+  "</td><td></td></tr>";
                  System.out.println("Mensajes en catalogo:  "+menRF);
                   mensaje += menRF;
                    }
                        
            
                
                  }
               System.out.println("Mensajes:  " +mensaje);

                obj.cierraConexion();

            } catch (SQLException e) {
                e.getMessage();
            }
//            System.out.println("Mensaje terminado: "+mensaje);
               //Cuerpo del correo
            body = "Folio con incidencia: " + fol +  "\n";
            body += "<h4> Unidad: " + cla_cli +" - "+ nom_cli + "</h4>";
      
            body +=  "<head>\n"
                + "<style>\n"
                + "table {\n"
                + "  border-collapse: collapse;\n"
                + "  width: 100%;\n"
                + "}\n"
                + "\n"
                + "th, td {\n"
                + "  text-align: left;\n"
                + "  padding: 8px;\n"
                + "  border: 1px solid black;\n"
                + "}\n"
                + "\n"
                + "tr:nth-child(even){background-color: #f2f2f2}\n"
                + "\n"
                + "th {\n"
                + "  background-color: #16b7ae;\n"
                + "  color: white;\n"
                + "}\n"
                + "</style>\n"
                + "</head>\n";
                 
            body += "<table><thead><tr><th>Clave</th><th>ClaveSS</th><th>Lote</th><th>Caducidad</th><th>Req</th><th>sur</th><th>Tipo de error</th><th>Ubicacion</th></thead><tbody>";
            body += mensaje;
            body += "</tbody></table>";
            
            message.setContent(body, "text/html; charset=utf-8");

            // Lo enviamos.
            Transport t = session.getTransport("smtp");
            t.connect("medalfawms2@gmail.com", "ezdwaltbtfwcfzef");
            t.sendMessage(message, message.getAllRecipients());

            // Cierre.
            t.close();
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }
    }

}
    

