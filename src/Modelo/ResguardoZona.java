/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.util.List;

/**
 *
 * @author Cristian
 */
public class ResguardoZona {
    private Coordenada coordenada;
    private int id;
    private List<Almacen> almacenes;
    private List<Albergue> albergues;
    /**
     *
     * @author Cristian
     */
    public ResguardoZona(Coordenada coordenada, int id, List almacenes, List albergues){
        this.coordenada = coordenada;
        this.id = id;
        this.almacenes = almacenes;
        this.albergues = albergues;
    }
    /**
     *
     * @author Cristian
     */
    public List getAlmacenes(){
        return this.almacenes;
    }
    /**
     *
     * @author Cristian
     */
    public List getAlbergues(){
        return this.albergues;
    }
    /**
     *
     * @author Cristian
     */
    public Coordenada getCoordenada(){
        return this.coordenada;
    }
    /**
     *
     * @author Cristian
     */
    public int getId(){
        return this.id;
    }
    /**
     *
     * @author Cristian
     */
    public void setAlmacen(Almacen almacen){
        this.almacenes.add(almacen);
    }
    /**
     *
     * @author Cristian
     */
    public void setAlbergue(Albergue albergue){
        this.albergues.add(albergue);
    }
    /**
     *
     * @author Cristian
     */
    public int getCapacidadAlbergues(){
        int capacidad = 0;
        if(!albergues.isEmpty()){
            for(int i = 0; i < albergues.size();i++){
                capacidad += albergues.get(i).getCapacidad();
            }
        }
        return capacidad;
    }
}
