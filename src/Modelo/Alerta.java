/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.util.ArrayList;
import java.util.Date; 
import java.util.List;

/**
 *
 * @author Cristian
 */
public class Alerta {
    private Coordenada coordenadas;
    private Emergencia emergencia;
    private int id;
    private boolean gestionada;
    private List<Voluntario> voluntarios;
    private List<Vehiculo> vehiculos;
    private Date fecha;
    private boolean activa;
    private ZonaSeguridad zona; 
    private int afectados;

    /**
     *
     * @author Cristian
     */        
    public Alerta(Coordenada coordenadas, Emergencia emergencia, int afectados){
        this.coordenadas = coordenadas;
        this.emergencia = emergencia;
        id = 0;
        gestionada = false;
        voluntarios = new ArrayList<Voluntario>();
        vehiculos = new ArrayList<Vehiculo>();
        fecha = new Date();
        activa = true;
        zona = null;
        this.afectados = afectados;
    }
    
    /**
     * 
     * @author Cristian
     */        
    public Alerta(Coordenada coordenadas, Emergencia emergencia
            ,int id ,boolean gestionada, List<Voluntario> voluntarios,
            List<Vehiculo> vehiculos, Date fecha, boolean activa, ZonaSeguridad zona
            , int afectados){
        this.coordenadas = coordenadas;
        this.emergencia = emergencia;
        this.id = id;
        this.gestionada = gestionada;
        this.voluntarios = voluntarios;
        this.vehiculos = vehiculos;
        this.fecha = fecha;
        this.activa = activa;
        this.zona = zona;
        this.afectados = afectados;
    }
    /**
     * 
     * @autor Cristian 
     */
    public Coordenada getCoordenadas() {
        return coordenadas;
    }
    /**
     * 
     * @autor Cristian 
     */
    public void setCoordenadas(Coordenada coordenadas) {
        this.coordenadas = coordenadas;
    }
    /**
     * 
     * @autor Cristian 
     */
    public Emergencia getEmergencia() {
        return emergencia;
    }
    /**
     * 
     * @autor Cristian 
     */
    public void setEmergencia(Emergencia emergencia) {
        this.emergencia = emergencia;
    }
    /**
     * 
     * @autor Cristian 
     */
    public int getId() {
        return id;
    }
    /**
     * 
     * @autor Cristian 
     */
    public void setId(int id) {
        this.id = id;
    }
    /**
     * 
     * @autor Cristian 
     */
    public boolean isGestionada() {
        return gestionada;
    }
    /**
     * 
     * @autor Cristian 
     */
    public void setGestionada(boolean gestionada) {
        this.gestionada = gestionada;
    }
    /**
     * 
     * @autor Cristian 
     */
    public List<Voluntario> getVoluntarios() {
        return voluntarios;
    }
    /**
     * 
     * @autor Cristian 
     */
    public void setVoluntarios(List<Voluntario> voluntarios) {
        this.voluntarios = voluntarios;
    }
        /**
     * 
     * @autor Cristian 
     */
    public List<Vehiculo> getVehiculos() {
        return vehiculos;
    }
    /**
     * 
     * @autor Cristian 
     */
    public void setVehiculos(List<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }
    /**
     * 
     * @autor Cristian 
     */
    public Date getFecha() {
        return fecha;
    }
    /**
     * 
     * @autor Cristian 
     */
    public boolean isActiva() {
        return activa;
    }

    /**
     * 
     * @autor Cristian 
     */
    public void finalizar() {
        activa = false;
    }
    
    /**
     * 
     * @autor Cristian 
     */
    public int getAfectados() {
        return afectados;
    }
    /**
     * 
     * @autor Cristian 
     */
    public void setAfectados(int afectados) {
        this.afectados = afectados;
    }
        /**
     * 
     * @autor Cristian 
     */
    public ZonaSeguridad getZona() {
        return zona;
    }
    /**
     * 
     * @autor Cristian 
     */
    public void asignarZona(ZonaSeguridad zona) {
        this.zona = zona;
    }
    /**
     * 
     * @author Cristian 
     */
    public void asignarVoluntario(Voluntario voluntario){
        voluntarios.add(voluntario);
    }
    
    /**
     * 
     * @author Cristian
     */
    public void asignarVehiculo(Vehiculo vehiculo){
        vehiculos.add(vehiculo);
    }
/**
     *
     * @author Cristian
     */
    public void activarPlanDeProteccion(){
        gestionada = true;
    }
    
    @Override
    /**
     * @author Cristian
     */
    public String toString() {
        return id + "," + emergencia.getTipo()+ "," +emergencia.getNivel() +
                "," + coordenadas.getX() + "," + coordenadas.getY() + "," 
                + afectados + "," + activa + "," + fecha.getDay() + "," 
                + fecha.getMonth() + "," + fecha.getYear() + "," + gestionada;
    }
    
}
