package Modelo;

/**
 *
 * @author Alejandro Cencerrado
 * 
 * Clase definida para la manipulación del recurso Albergue.
 */
public class Albergue {
    private String id;
    private int capacidad;
    private Coordenada coordenadas;
    private int ocupacion;

    /**
     * 
     * @param id
     * @param capacidad
     * @param coordenadas
     * @param ocupacion 
     * 
     * Construye un Albergue con todos los parámetros.
     */
    public Albergue(String id, int capacidad, Coordenada coordenadas, int ocupacion) {
        this.id = id;
        this.capacidad = capacidad;
        this.coordenadas = coordenadas;
        this.ocupacion = ocupacion;
    }
    
    /**
     * 
     * @param id
     * @param capacidad
     * @param coordenadas 
     * 
     * Construye con albergue desocupado.
     */
    public Albergue(String id, int capacidad, Coordenada coordenadas) {
        this(id,capacidad,coordenadas,0);
    }
    
    /**
     * Devuelve la id.
     * @return 
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 
     * @return 
     */
    public int getCapacidad() {
        return capacidad;
    }

    /**
     * 
     * @param capacidad 
     */
    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    /**
     * 
     * @return 
     */
    public Coordenada getPosicion() {
        return coordenadas;
    }

    /**
     * 
     * @param coordenadas 
     */
    public void setCoordenadas(Coordenada coordenadas) {
        this.coordenadas = coordenadas;
    }

    /**
     * 
     * @return 
     */
    public int getOcupacion() {
        return ocupacion;
    }

    /**
     * 
     * @param ocupacion 
     */
    public void setOcupacion(int ocupacion) {
        this.ocupacion = ocupacion;
    }
    
    /**
     * 
     * @param cantidad 
     */
    public void alojar(int cantidad){
        //comprobar capacidad
        
        ocupacion = ocupacion + cantidad;
    }
    
    /**
     * 
     */
    public void desalojar(int cantidad){
        if (cantidad > ocupacion){
            desalojar();
        }
        else {
            ocupacion = ocupacion - cantidad;
        } 
    }
    
    public void desalojar(){
        ocupacion = 0;
    }
    
    
    public boolean estaLleno(){
        return plazasDisponibles() == 0;
    }
    
    public int plazasDisponibles(){
        return capacidad - ocupacion;
    }
    
    @Override
    /**
     * Sobreescribo el método para utilzarlo en la clase comms.
     */
    public String toString() {
        return id + "," + capacidad + "," + coordenadas.getX() + "," +
                coordenadas.getY() + "," + ocupacion;
    }
}
