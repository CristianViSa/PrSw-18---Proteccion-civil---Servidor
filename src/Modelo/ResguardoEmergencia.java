/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

/**
 *
 * @author Cristian
 */

public class ResguardoEmergencia {
    private String tipo;
    private int id;
    private int nivel;
    private ResguardoPlan plan;
    
    /**
     * 
     * Constructor
     * @author Cristian
     */
    public ResguardoEmergencia(String tipo, int id, int nivel, ResguardoPlan plan){
        this.tipo = tipo;
        this.id = id;
        this.nivel = nivel;
        this.plan = plan;
    }
    
    /**
     * 
     * Constructor
     * @author Cristian
     */
    public ResguardoEmergencia(String tipo, int nivel){
        this.tipo = tipo;
        this.id = 0;
        this.nivel = nivel;
    }
    
    /**
     * 
     * Getter
     * @author Cristian
     */
    public String getTipo(){
        return tipo;
    }
    
    /**
     * Getter
     * @author Cristian
     */
    public int getId(){
        return id;
    }
    
    /**
     * Getter
     * @author Cristian
     */
    public int getNivel(){
        return nivel;
    }
    
    /**
     * Getter
     * @author Cristian
     */
    public ResguardoPlan getPlan(){
        return plan;
    }
    
    /**
     * 
     * Setter 
     * @author Cristian
     */
    public void setPlan(ResguardoPlan plan){
        this.plan = plan;
    }
    public String toString(){
        String cadena = "Emergencia de tipo: " + tipo;
        cadena += "\n\tCódigo emergencia: " + id;
        cadena += "\n\tNivel: " + nivel;
        if(plan != null){
            cadena += "\n\tPlan protección: código " + plan.getId();
            cadena += "\n\t\t" + plan.toString();
        } else {
            cadena += "\n\tPlan Proteccion: -";
        }
        return cadena;
    }
    
}
