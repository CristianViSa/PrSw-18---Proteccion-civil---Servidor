package Modelo;

/**
 *
 * @author Alejandro Cencerrado
 */
public class Voluntario {
    private String id;
    private String nombre;
    private String telefono;
    private String correo;
    private Coordenada coordenadas;
    private boolean esConductor;
    private boolean disponible;

    public Voluntario(String id, String nombre, String telefono, String correo, Coordenada coordenada, boolean esConductor, boolean disponible) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.correo = correo;
        this.coordenadas = coordenada;
        this.esConductor = esConductor;
        this.disponible = disponible;
    }
    
    public void enviarCorreo(String texto){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Coordenada getPosicion() {
        return coordenadas;
    }

    public void setCoordenadas(Coordenada coordenadas) {
        this.coordenadas = coordenadas;
    }

    public boolean getEsConductor() {
        return esConductor;
    }

    public void setEsConductor(boolean esConductor) {
        this.esConductor = esConductor;
    }

    public boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
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
        return id + "," + nombre + "," + telefono + "," + correo + "," +
                coordenadas.getX() + "," + coordenadas.getY() + "," + 
                esConductor + "," + disponible;
    }
}
