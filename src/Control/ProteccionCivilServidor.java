package Control;

import Modelo.Comms;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cristian, Alejandro
 */
public class ProteccionCivilServidor {

    public ProteccionCivilServidor(){
        try {
            new Comms();
        } catch (Exception ex) {
            Logger.getLogger(ProteccionCivilServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new ProteccionCivilServidor();
    }
    
}
