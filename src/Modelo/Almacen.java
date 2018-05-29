package Modelo;

/**
 *
 * @author Alejandro Cencerrado Tello
 * 
 */
public class Almacen {
    private String id;
    private int cantidadMantas;
    private int cantidadComida;
    private int cantidadAgua;
    private Coordenada coordenadas;
    private int capacidad; //De cada tipo de recurso

    public Almacen(String id, int candidadMantas, int cantidadComida, int cantidadAgua, Coordenada coordenadas, int capacidad) {
        this.id = id;
        this.cantidadMantas = candidadMantas;
        this.cantidadComida = cantidadComida;
        this.cantidadAgua = cantidadAgua;
        this.coordenadas = coordenadas;
        this.capacidad = capacidad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getCantidadMantas() {
        return cantidadMantas;
    }

    public void setCantidadMantas(int candidadMantas) {
        this.cantidadMantas = candidadMantas;
    }

    public int getCantidadComida() {
        return cantidadComida;
    }

    public void setCantidadComida(int cantidadComida) {
        this.cantidadComida = cantidadComida;
    }

    public int getCantidadAgua() {
        return cantidadAgua;
    }

    public void setCantidadAgua(int cantidadAgua) {
        this.cantidadAgua = cantidadAgua;
    }

    public Coordenada getPosicion() {
        return coordenadas;
    }

    public void setCoordenadas(Coordenada coordenadas) {
        this.coordenadas = coordenadas;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }
    
    public void addMantas(int cantidad){
        
        if ((cantidadMantas + cantidad) <= capacidad){
            setCantidadMantas(cantidadMantas + cantidad);
        }
        
        else {
            setCantidadMantas(capacidad);
        }
    } 
    
    public void addComida(int cantidad){
        
        if ((cantidadComida + cantidad) <= capacidad){
            setCantidadComida(cantidadComida + cantidad);
        }
        
        else {
            setCantidadComida(capacidad);
        }
    } 
    
    public void addAgua(int cantidad){
        
        if ((cantidadAgua + cantidad) <= capacidad){
            setCantidadAgua(cantidadAgua + cantidad);
        }
        
        else {
            setCantidadMantas(capacidad);
        }
    } 
    
    public void sacarMantas(int cantidad) throws Exception{
        if ((cantidadMantas - cantidad) < 0){           
            throw new Exception("Cantidad disponible menor que la cantidad deseada");
        }
        
        else {
            setCantidadMantas(cantidadMantas - cantidad);
        } 
    }
    
    public void sacarComida(int cantidad) throws Exception{
        if ((cantidadComida - cantidad) < 0){           
            throw new Exception("Cantidad disponible menor que la cantidad deseada");
        }
        
        else {
            setCantidadComida(cantidadComida - cantidad);
        } 
    }
    
    public void sacarAgua(int cantidad) throws Exception{
        if ((cantidadAgua - cantidad) < 0){           
            throw new Exception("Cantidad disponible menor que la cantidad deseada");
        }
        
        else {
            setCantidadAgua(cantidadAgua - cantidad);
        } 
    }
    
    @Override
    /**
     * Sobreescribo el método para utilzarlo en la clase comms.
     */
    public String toString() {
        return id + "," + cantidadMantas + "," + cantidadComida + "," 
                + cantidadAgua + "," + coordenadas.getX() + "," +
                coordenadas.getY() + "," + capacidad;
    }
}
