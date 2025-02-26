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


public class CorreoCartaCanje extends HttpServlet {

    ConectionDB obj = new ConectionDB();

    public void enviaCorreo(String ordenCompra, String remision) {

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
            
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress("luz.marchan@medalfa.mx"));
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress("subdirector.operaciones@medalfa.mx"));
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress("coordinventarios@medalfa.mx"));
              message.addRecipient(Message.RecipientType.BCC, new InternetAddress("jefe.almacen@medalfa.mx"));
              message.addRecipient(Message.RecipientType.BCC, new InternetAddress("supervisor.almacen@medalfa.mx"));
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress("hmorales@medalfa.mx"));
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress("norma.hernandez@medalfa.mx"));
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress("devoluciones@medalfa.mx"));
            message.addRecipient(Message.RecipientType.BCC, new InternetAddress("supervisor.recibo@medalfa.mx"));
            message.setSubject("Medicamento controlado con caducidad menor a un año / MEDALFA ISEM");

            String mensaje = "",clave = "", cantidad = "", tipo = "", lote = "", caducidad = "", cartaCanje = "", body = "", proveedor = "", recepcion = "", usuario = "";
            try {
                obj.conectar();
                ResultSet rset = obj.consulta("SELECT c.F_OrdCom, c.F_FolRemi, p.F_NomPro, c.F_User, c.F_FecApl FROM tb_compra AS c INNER JOIN tb_proveedor AS p ON c.F_ProVee = p.F_ClaProve WHERE c.F_OrdCom ='" + ordenCompra + "' AND c.F_FolRemi ='" + remision + "'GROUP BY c.F_FolRemi;");
               
                System.out.println("SELECT c.F_OrdCom, c.F_FolRemi, p.F_NomPro, c.F_User, c.F_FecApl FROM tb_compra AS c INNER JOIN tb_proveedor AS p ON c.F_ProVee = p.F_ClaProve WHERE c.F_OrdCom ='" + ordenCompra + "' AND c.F_FolRemi ='" + remision + "'GROUP BY c.F_FolRemi;");
                while (rset.next()) {
                    System.out.println("Comienza el ciclo");
                    proveedor = rset.getString("p.F_NomPro");
                    recepcion = rset.getString("c.F_FecApl");
                    usuario = rset.getString("c.F_User");
                    System.out.println(proveedor + recepcion + usuario);
                }
                

                ResultSet rset2 = obj.consulta("SELECT c.F_ClaPro, l.F_ClaLot, l.F_FecCad, c.F_CanCom, c.F_CartaCanje FROM tb_compra AS c INNER JOIN tb_controlados AS ct ON c.F_ClaPro = ct.F_ClaPro INNER JOIN tb_lote AS l ON c.F_Lote = l.F_FolLot WHERE c.F_OrdCom = '" + ordenCompra + "' AND c.F_FolRemi = '" + remision + "' AND c.F_CartaCanje <> '' ORDER BY c.F_ClaPro ASC;");

                while (rset2.next()) {
                    System.out.println("Comienza el ciclo Controlado");
                    clave = rset2.getString("c.F_ClaPro");
                    lote = rset2.getString("l.F_ClaLot");
                    caducidad = rset2.getString("l.F_FecCad");
                    cantidad = rset2.getString("c.F_CanCom");
                    cartaCanje = rset2.getString("c.F_CartaCanje");
                    tipo = "Controlado";   
                    
                    mensaje += "<tr><td>" +  clave + "</td><td>" + lote + "</td><td>" + caducidad + "</td><td>" + cantidad + "</td><td>" + cartaCanje + "</td><td>" + tipo + "</td></tr>";
                     }               
               

                obj.cierraConexion();

            } catch (SQLException e) {
                e.getMessage();
            }
            
               //Cuerpo del correo
            body = "Se Ingreso en ISEM la siguiente orden de compra: " + ordenCompra + " con la remision: " + remision + "\n";
            body += "<h4> Proveedor: " + proveedor + "</h4>";
            body += "<h4> Fecha de Recepcion: " + recepcion + "</h4>";
            body += "<h4> Orden Capturada por: " + usuario + "</h4>";
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
            body += "<table><thead><tr><th>Clave</th><th>Lote</th><th>Caducidad</th><th>Cantidad</th><th>Carta Canje</th><th>Tipo</th></thead><tbody>";
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
    

