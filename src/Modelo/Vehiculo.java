package Modelo;

/**
 *
 * @author Alejandro Cencerrado
 */
public class Vehiculo {
    private String id;
    private String modelo;
    private int plazas;
    private Coordenada coordenadas;
    private boolean disponible;

    public Vehiculo(String id, String modelo, int plazas, Coordenada coordenadas, boolean disponible) {
        this.id = id;
        this.modelo = modelo;
        this.plazas = plazas;
        this.coordenadas = coordenadas;
        this.disponible = disponible;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getPlazas() {
        return plazas;
    }

    public void setPlazas(int plazas) {
        this.plazas = plazas;
    }

    public Coordenada getPosicion() {
        return coordenadas;
    }

    public void setCoordenadas(Coordenada coordenadas) {
        this.coordenadas = coordenadas;
    }

    public boolean isDisponible() {
        return disponible;
    }

    private void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    public void ocupar(){
        setDisponible(false);
    }
    
    public void desocupar(){
        setDisponible(true);
    }   
    
    @Override
    /**
     * Sobreescribo el método para utilzarlo en la clase comms.
     */
    public String toString() {
        return id + "," + modelo + "," + plazas + "," + coordenadas.getX() + "," +
                coordenadas.getY() + "," + disponible;
    }
}
