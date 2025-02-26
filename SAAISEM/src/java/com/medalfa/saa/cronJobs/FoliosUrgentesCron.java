/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.medalfa.saa.cronJobs;

/**
 *
 * @author HP-MEDALFA
 */
import com.medalfa.saa.controllers.services.FoliosUrgentesService;
import com.medalfa.saa.model.NotificacionFoliosUrgentes;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class FoliosUrgentesCron implements ServletContextListener {

    private Thread t = null;
    private ServletContext context;

    @Override
    public void contextInitialized(ServletContextEvent contextEvent) {
        t = new Thread() {
            //task
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10000);
                        FoliosUrgentesService service = new FoliosUrgentesService();
                        NotificacionFoliosUrgentes notificacion = service.getNotificacion();
                        System.out.println("Buscando folios urgentes");
                        if (notificacion != null) {
                         //   System.out.println("Se notifica el folio urgente: " + notificacion.getFolio());
//                            notificacion = service.getNotificationDetails(notificacion);
                            service.enviarNotificacion(notificacion);
                            service.sendInfoToWMS(notificacion);
                        }
                        service.closeConnection();
                    } catch (Exception e) {
                      //  System.out.println("No se encontró nuevo folio urgente");
                    }
                }
            }
        };
        t.start();
        context = contextEvent.getServletContext();
        // you can set a context variable just like this

        context.setAttribute(
                "TEST", "TEST_VALUE");
    }

    @Override
    public void contextDestroyed(ServletContextEvent contextEvent) {
        // si se destruye el contexto se interrumpe el thread
        t.interrupt();
    }
}
