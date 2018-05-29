package Modelo;

import java.io.Serializable;

/**
 *
 * @author Cristian
 */
public class Mensaje implements Serializable {
    private Operacion operacion;
    private String parametros;
    
    public Mensaje(Operacion operacion, String parametros){    
        ponerParametros(parametros);
        ponerOperacion(operacion);
    }
    
    public Mensaje(){
        this.operacion = null;
        this.parametros = "";
    }
    /*
    ** Setter
    */
    public void ponerOperacion(Operacion operacion){
        this.operacion = operacion;
    }
    
    /*
    ** Setter
    */
    public void ponerParametros(String parametros){
        this.parametros = parametros;
    }
    
     
    /**
     * Getter
     */
    public String verParametros(){
        return parametros;
    }
    
    
    /**
     * Getter
     */
    public Operacion verOperacion(){
        return operacion;
    }
    
    /**
     * Añade un parametro a los parametros
     */
    public void anadirParametro(String parametro){
        parametros += "," + parametro;
    }
}
