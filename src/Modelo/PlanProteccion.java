/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

/**
 *
 * @author Cristian
 * Reemplazar por el original en un futuro.
 */
public class PlanProteccion {
    private String id_plan;
    private String nombre;
    private int vehiculosNecesarios;
    private int voluntariosNecesarios;
    private String actuacionesNecesarias;
    
    /**
     *
     * @author Cristian
     */
    public PlanProteccion(String nombre, int vehiculosNecesarios, int voluntariosNecesarios, String actuacionesNecesarias){
            this.id_plan = "0";
            this.nombre = nombre;
            this.vehiculosNecesarios = vehiculosNecesarios;
            this.voluntariosNecesarios = voluntariosNecesarios;
            this.actuacionesNecesarias = actuacionesNecesarias;
    }
    /**
     *
     * @author Cristian
     */
    public PlanProteccion(String id,String nombre, int vehiculosNecesarios, int voluntariosNecesarios, String actuacionesNecesarias){
        this.id_plan = id;
        this.nombre = nombre;
        this.vehiculosNecesarios = vehiculosNecesarios;
        this.voluntariosNecesarios = voluntariosNecesarios;
        this.actuacionesNecesarias = actuacionesNecesarias;
    }
    /**
     *
     * @author Cristian
     */
    public int getVehiculosNecesarios() {
            return vehiculosNecesarios;
    }
    /**
     *
     * @author Cristian
     */
    public void setVehiculosNecesarios(int vehiculosNecesarios) {
            this.vehiculosNecesarios = vehiculosNecesarios;
    }
    /**
     *
     * @author Cristian
     */
    public int getVoluntariosNecesarios() {
            return voluntariosNecesarios;
    }
    /**
     *
     * @author Cristian
     */
    public void setVoluntariosNecesarios(int voluntariosNecesarios) {
            this.voluntariosNecesarios = voluntariosNecesarios;
    }
    /**
     *
     * @author Cristian
     */
    public String getActuacionesNecesarias() {
            return actuacionesNecesarias;
    }
    /**
     *
     * @author Cristian
     */
    public void setActuacionesNecesarias(String actuacionesNecesarias) {
            this.actuacionesNecesarias = actuacionesNecesarias;
    }
    /**
     *
     * @author Cristian
     */
    public String getId() {
            return id_plan;
    }
    /**
     *
     * @author Cristian
     */
    public String getNombre(){
            return nombre;
    }
    /**
     *
     * @author Cristian
     */
    public void setNombre(String nombre) {
            this.nombre = nombre;
    }
    /**
     *
     * @author Cristian
     */
    public String toString(){
            /*String cadena = "\n" + getNombre();
            cadena += "\n\nCódigo del plan: " + getId();
            cadena += "\nVehículos necesarios: " + getVehiculosNecesarios();
            cadena += "\nVoluntarios necesarios: " + getVoluntariosNecesarios();
            cadena += "\n\n\t" + getActuacionesNecesarias();
            cadena += "\n";
            return cadena;*/
            return getId() + "," + getNombre()+ "," +getVehiculosNecesarios() +
                "," + getVoluntariosNecesarios() + "," + getActuacionesNecesarias();
    }
    
}
