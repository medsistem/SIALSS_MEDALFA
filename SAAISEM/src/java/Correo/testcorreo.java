/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Correo;

/**
 *
 * @author DotorMedalfa
 */
public class testcorreo {

    /**
     * @param args the command line arguments
     */
    
     CorreoFactIncidente cfactinc = new CorreoFactIncidente();
     Correo correo1 = new Correo();
     CorreoCartaCanje CartaCanje = new CorreoCartaCanje();
    CorreoCambiaFecha cambiafecha = new CorreoCambiaFecha();
     CorreoConfirmaAT  at = new CorreoConfirmaAT();
     CorreoConfirmaRemision Remision= new CorreoConfirmaRemision();
     
     int folio =145919;
     String ordenCompra = "", remision = "";
     String claveAT = "", Ubicacion = "",Usuario = "",Proy = "" ,LoteDesc = "",UbicaAn = "", Cadu = "";
    int CantMov = 0;
    
    public static void main(String[] args) {
        // TODO code application logic here
testcorreo ps = new testcorreo();
    
    ps.correo();
        
          }
    

    public void correo(){
          cfactinc.enviaCorreoFactIncidente(folio);

    }
    
}
