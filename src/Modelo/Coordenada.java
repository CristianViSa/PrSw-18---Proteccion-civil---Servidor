package Modelo;


/**
 *
 * @author Alejandro Cencerrado
 */
public class Coordenada {
    private float x;
    private float y;
    
    public Coordenada(float x, float y){
        this.x = x;
        this.y = y;
    }
    
    public float getX(){
        return x;
    }
    
    
    public float getY(){
        return y;
    }
    
    @Override
    public String toString() {
        return "Coordeenada[ X=" + getX() + ", Y= "+ getY() + " ]";
    }
}
