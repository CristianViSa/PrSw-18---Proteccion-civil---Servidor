/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

/**
 *
 * @author Miguel Yanes
 */

public class Emergencia {
    private String id;
    private PlanProteccion plan;
    private String tipo;
    private int nivel;
    
    /**
     * 
     * Constructor
     */
    public Emergencia(String id, PlanProteccion plan, String tipo, int nivel){
        this.id = id;
        this.plan = plan;
        this.tipo = tipo;
        this.nivel = nivel;
    }
    
    public Emergencia(String tipo, int nivel){
        this.tipo = tipo;
        this.id = "0";
        this.nivel = nivel;
    }
    
    public String getTipo(){
        return tipo;
    }
    
    public String getId(){
        return id;
    }
    
    public int getNivel(){
        return nivel;
    }
    
    public PlanProteccion getPlan(){
        return plan;
    }
    
    public void setPlan(PlanProteccion plan){
        this.plan = plan;
    }
    /*public String toString(){
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
    }*/
    
    public String toString(){
        return getId() + "," + getPlan().toString()+ "," +getTipo() +
                "," + getNivel();
    }
}
