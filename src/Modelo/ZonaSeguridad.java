/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.util.List;

/**
 *
 * @author Miguel Yanes
 */
public class ZonaSeguridad {
    private Coordenada coordenada;
    private int id;
    private List<Almacen> almacenes;
    private List<Albergue> albergues;
    
    public ZonaSeguridad(Coordenada coordenada, int id, List almacenes, List albergues){
        this.coordenada = coordenada;
        this.id = id;
        this.almacenes = almacenes;
        this.albergues = albergues;
    }
    
    public List getAlmacenes(){
        return this.almacenes;
    }
    
    public List getAlbergues(){
        return this.albergues;
    }
    
    public Coordenada getCoordenada(){
        return this.coordenada;
    }
    
    public int getId(){
        return this.id;
    }
    
    public void setAlmacen(Almacen almacen){
        this.almacenes.add(almacen);
    }
    
    public void setAlbergue(Albergue albergue){
        this.albergues.add(albergue);
    }
    
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
