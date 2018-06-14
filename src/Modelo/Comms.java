package Modelo;


import db.BaseDeDatos;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.naming.NamingException;

/**
 *
 * @author Cristian, Alejandro
 */
public class Comms extends Thread{

    private static final int puerto = 5500;
    private boolean conexiones = true;
    
    private ServerSocket ss;    
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    
    BaseDeDatos db;
    /**
     * 
     * @author Cristian
     */
    public Comms() throws Exception{
        try {
            ss = new ServerSocket(puerto);
            try {
                this.db = new BaseDeDatos();
             
            } 
            
            catch (NamingException ex) {
                throw new Exception("Error al conectar con servidor.");
            }
            run();
    
        } catch (IOException ex) {
            Logger.getLogger(Comms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
    ** Sobreescribe el metodo  run
    ** @author Cristian, Alejandro
    */
    public void run(){
         while(conexiones){
            Socket s;
            Mensaje mensajeTX;
            Mensaje mensajeRX;
            try {
                s = ss.accept();
                salida = new ObjectOutputStream(s.getOutputStream());
                entrada = new ObjectInputStream(s.getInputStream());
                
                mensajeRX =(Mensaje) entrada.readObject();
                mensajeTX = new Mensaje();
                switch(mensajeRX.verOperacion()){

                    //@author Cristian
                    case ALERTAS_MAPA: 
                        ArrayList<Alerta> alertas = db.getAlertasActivas();
                        if(!alertas.equals(null)){
                            mensajeTX.ponerParametros(String.valueOf(alertas.size()));
                            for(int i=0; i< alertas.size(); i++) {
                               mensajeTX.anadirParametro(alertas.get(i).toString());
                            }
                        }
                        else{
                        mensajeTX.ponerOperacion(Operacion.ERROR);
                        mensajeTX.ponerParametros("No se han encontrado alertas");    
                        }
                        break;
                    //@author Cristian
                    case ACTIVAR_PLAN:
                        String idAlerta = mensajeRX.verParametros();
                        Alerta alertaBuscada = db.getAlerta(idAlerta);
                        alertaBuscada.activarPlanDeProteccion();
                        Emergencia emergenciaAlerta 
                                = alertaBuscada.getEmergencia();
                        PlanProteccion planEmergencia =
                                emergenciaAlerta.getPlan();
                        if(planEmergencia.equals(null)){
                            mensajeTX.ponerOperacion(Operacion.ERROR);
                            mensajeTX.ponerParametros("Error. "
                                    + "No se ha encontrado el plan la alerta"); 
                            break;
                        }
                        int vehiculosNecesarios =
                                planEmergencia.getVehiculosNecesarios();

                        int voluntariosNecesarios =
                                planEmergencia.getVoluntariosNecesarios();
                        String actuacionesNecesarios = 
                                planEmergencia.getActuacionesNecesarias();
                        List<Voluntario> voluntariosDisponibles =
                                db.getVoluntarios();
                        if(voluntariosDisponibles.equals(null)){
                            mensajeTX.ponerOperacion(Operacion.ERROR);
                            mensajeTX.ponerParametros("Error. "
                                    + "No se han encontrado voluntarios"
                                    + " para la alerta"); 
                            break;
                        }
                        List<Vehiculo> vehiculosDisponibles = 
                                db.getVehiculos();
                        if(vehiculosDisponibles.equals(null)){
                            mensajeTX.ponerOperacion(Operacion.ERROR);
                            mensajeTX.ponerParametros("Error. "
                                    + "No se han encontrado vehiculos "
                                    + "para la alerta"); 
                            break;
                        }
                        List<ZonaSeguridad> zonasDeSeguridad =
                                db.getZonasDeSeguridad();
                        if(zonasDeSeguridad.equals(null)){
                            mensajeTX.ponerOperacion(Operacion.ERROR);
                            mensajeTX.ponerParametros("Error. "
                                    + "No se ha encontrado la zona de "
                                    + "seguridad para la alerta"); 
                            break;
                        }
                        ZonaSeguridad zonaSegura = null;
                        
                        for (ZonaSeguridad zona : zonasDeSeguridad){
                            if(zona.getCapacidadAlbergues() > alertaBuscada.getAfectados()){
                                zonaSegura = zona;
                                alertaBuscada.asignarZona(zonaSegura);
                                break;
                            }
                        }
                        if(zonaSegura == null){
                            mensajeTX.ponerOperacion(Operacion.ERROR);
                            mensajeTX.ponerParametros("No se han encontrado"
                                    + " zonas con suficiente capacidad para"
                                    + " todos los afectados."); 
                            break;
                        };
                        int voluntariosAsignados = 0;
                        List<Voluntario> voluntariosAsignar = new ArrayList<Voluntario>();
                        for(Voluntario voluntario : voluntariosDisponibles){
                            String idVol = voluntario.getId();
          
                            if(voluntariosNecesarios > voluntariosAsignados){
                                if(voluntario.getDisponible()){
                                    alertaBuscada.asignarVoluntario(voluntario);
                                    voluntario.ocupar();
                                    voluntariosAsignar.add(voluntario);
                                    voluntariosAsignados++;
                                }
                            }
                        }
                        if(voluntariosAsignados < voluntariosNecesarios){
                            mensajeTX.ponerOperacion(Operacion.ERROR);
                            mensajeTX.ponerParametros("No se han podido"
                                    + " asignar todos los "
                                    + "voluntarios necesarios. Faltan "+
                                    (voluntariosNecesarios - voluntariosAsignados)); 
                            break;
                        }
                        int vehiculosAsignados = 0;
                        List<Vehiculo> vehiculosAsignar = new ArrayList<Vehiculo>();
                        for(Vehiculo vehiculo : vehiculosDisponibles){
                            if(vehiculosNecesarios > vehiculosAsignados){
                                if(vehiculo.isDisponible()){
                                    alertaBuscada.asignarVehiculo(vehiculo);
                                    vehiculo.ocupar();
                                    vehiculosAsignar.add(vehiculo);
                                    vehiculosAsignados++;
                                }
                            }
                        }
                        if(vehiculosAsignados < vehiculosNecesarios){
                            mensajeTX.ponerOperacion(Operacion.ERROR);
                            mensajeTX.ponerParametros("No se han podido"
                                    + " asignar todos los "
                                    + "vehiculos necesarios. Faltan "
                                    + (vehiculosNecesarios - vehiculosAsignados)); 
                            break;
                        }
                        List<Albergue> alberguesZona = new ArrayList<Albergue>(zonaSegura.getAlbergues()); 
                        int afectados = alertaBuscada.getAfectados();
                        int alojados = 0;
                        List<Albergue> alberguesAsignar = new ArrayList<Albergue>();
                        for(Albergue albergue : alberguesZona){
                            if(!albergue.estaLleno()){
                                if(afectados > alojados){
                                    int ocupacion = albergue.getOcupacion();
                                    int capacidad = albergue.getCapacidad();
                                    if(afectados > capacidad){
                                        albergue.alojar(capacidad);
                                        alberguesAsignar.add(albergue);
                                        alojados += capacidad; 
                                    }
                                    else{
                                        albergue.alojar(afectados);
                                        alberguesAsignar.add(albergue);
                                        alojados += afectados;
                                    }
                                }
                            }
                        }
                        if(alojados < afectados){
                            mensajeTX.ponerOperacion(Operacion.ERROR);
                            mensajeTX.ponerParametros("No se han podido"
                                    + " alojar a todos los afectados."
                                    + "Quedan por alojar "+ (afectados - alojados)); 
                            break;
                                    }
                        mensajeTX.ponerParametros("true");
                    //Si todo es correcto, actualiza la BD. 
                    //Para asegurar que si algo falla, no toca nada. Todo tiene
                    // que ser correcto.
                    //Voluntarios
                    for(Voluntario voluntario : voluntariosAsignar){
                        db.modificarVoluntario(voluntario);//
                        db.asignarVoluntarioAlerta(idAlerta, voluntario.getId());//
                    }
                    //Vehiculos
                    for(Vehiculo vehiculo : vehiculosAsignar){
                        db.modificarVehiculo(vehiculo);
                        db.asignarVehiculoAlerta(idAlerta, vehiculo.getId());
                    }           
                    //Zona
                    db.asignarZonaAlerta(idAlerta, String.valueOf(zonaSegura.getId()));
                    //Albergue
                    for(Albergue albergue : alberguesAsignar){
                        db.modificarAlbergue(albergue);
                    }
                    db.gestionarAlerta(idAlerta);
                    break;

                    //@author Cristian
                    case DESACTIVAR_ALERTA:
                        String idAlertaD = mensajeRX.verParametros();
                        Alerta alertaBuscadaD = db.getAlerta(idAlertaD);
                        List<Voluntario> voluntariosAlerta =
                                db.getVoluntariosAlerta(idAlertaD);
                        List<Vehiculo> vehiculosAlerta = 
                                db.getVehiculosAlerta(idAlertaD);
                        List<ZonaSeguridad> zonasSeguras =
                                db.getZonasAlerta(idAlertaD);

                        for(Voluntario voluntario : voluntariosAlerta){
                            voluntario.desocupar();
                            voluntario.toString();
                            db.modificarVoluntario(voluntario);
                            db.desAsignarVoluntarioAlerta(idAlertaD, voluntario.getId());
                        }
                        for(Vehiculo vehiculo : vehiculosAlerta){
                            vehiculo.desocupar();
                            vehiculo.toString();
                            db.modificarVehiculo(vehiculo);
                            db.desAsignarVehiculoAlerta(idAlertaD, vehiculo.getId());
                            
                        }
                        int desalojados = 0;
                        int afectadosAlerta = alertaBuscadaD.getAfectados();
                        for(ZonaSeguridad zona : zonasSeguras){
                            List<Albergue> albergues = new ArrayList<Albergue>(zona.getAlbergues()); 

                            for(Albergue albergue : albergues){
                                if(!albergue.estaLleno()){
                                    if(afectadosAlerta > desalojados){
                                        int ocupacion = albergue.getOcupacion();
                                        int capacidad = albergue.getCapacidad();
                                        if(afectadosAlerta > capacidad){
                                            albergue.desalojar(capacidad);
                                            db.modificarAlbergue(albergue);
                                            desalojados += capacidad; 
                                        }
                                        else{
                                            albergue.desalojar(afectadosAlerta);
                                            db.modificarAlbergue(albergue);
                                            desalojados += afectadosAlerta;
                                        }
                                    }
                                }
                            }
                            db.desAsignarZonaAlerta(idAlertaD, String.valueOf(zona.getId()));
                        }
                        db.desactivarAlerta(idAlertaD);
                        mensajeTX.ponerParametros("true");
                        
                        break;   
                        
                    //@author Cristian
                    case HISTORIAL_ALERTAS:
                        ArrayList<Alerta> historial = db.getAlertas();
                         if(!historial.equals(null)){
                            mensajeTX.ponerParametros(String.valueOf(historial.size()));
                            for(int i=0; i< historial.size(); i++) {
                               mensajeTX.anadirParametro(historial.get(i).toString());
                            } 
                        }
                        else{
                        mensajeTX.ponerOperacion(Operacion.ERROR);
                        mensajeTX.ponerParametros("Error. No se ha encontrado"
                                + " el historial de alertas");    
                        }
                        break;

                    // @author Alejandro
                    case OBTENER_LISTA_VOLUNTARIOS:
                        ArrayList<Voluntario> listaVoluntarios = db.getVoluntarios();
                        mensajeTX.ponerParametros(String.valueOf(listaVoluntarios.size()));
                        for(int i=0; i< listaVoluntarios.size(); i++) {
                            mensajeTX.anadirParametro(listaVoluntarios.get(i).toString());
                        } 
                        break;  
                        
                    // @author Alejandro
                    case OBTENER_LISTA_VEHICULOS:
                        ArrayList<Vehiculo> listaVehiculos = db.getVehiculos();
                        mensajeTX.ponerParametros(String.valueOf(listaVehiculos.size()));
                        for(int i=0; i< listaVehiculos.size(); i++) {
                            mensajeTX.anadirParametro(listaVehiculos.get(i).toString());
                        } 
                        break;  
                        
                    // @author Alejandro
                    case OBTENER_LISTA_ALMACENES:
                        ArrayList<Almacen> listaAlmacenes = db.getAlmacenes();
                        mensajeTX.ponerParametros(String.valueOf(listaAlmacenes.size()));
                        for(int i=0; i< listaAlmacenes.size(); i++) {
                            mensajeTX.anadirParametro(listaAlmacenes.get(i).toString());
                        } 
                        break;  
                        
                    // @author Alejandro
                    case OBTENER_LISTA_ALBERGUES:
                        ArrayList<Albergue> listaAlbergues = db.getAlbergues();
                        mensajeTX.ponerParametros(String.valueOf(listaAlbergues.size()));
                        for(int i=0; i< listaAlbergues.size(); i++) {
                            mensajeTX.anadirParametro(listaAlbergues.get(i).toString());
                        } 
                        break; 
                        
                    // @author Alejandro    
                    case INSERTAR_ALBERGUE:
                        mensajeTX.ponerParametros(String.valueOf(insertarAlbergue(mensajeRX)));
                        break;
                        
                    // @author Alejandro    
                    case INSERTAR_ALMACEN:
                        mensajeTX.ponerParametros(String.valueOf(insertarAlmacen(mensajeRX)));
                        break;
                        
                    // @author Alejandro    
                    case INSERTAR_VOLUNTARIO:
                        mensajeTX.ponerParametros(String.valueOf(insertarVoluntario(mensajeRX)));
                        break;
                    
                    // @author Alejandro    
                    case INSERTAR_VEHICULO:
                        mensajeTX.ponerParametros(String.valueOf(insertarVehiculo(mensajeRX)));
                        break;
                    
                    // @author Alejandro
                    case MODIFICAR_VOLUNTARIO:
                        mensajeTX.ponerParametros(String.valueOf(modificarVoluntario(mensajeRX)));
                        break;
                    // @author Alejandro
                    case MODIFICAR_VEHICULO:
                        mensajeTX.ponerParametros(String.valueOf(modificarVehiculo(mensajeRX)));
                        break;
                        
                    // @author Alejandro
                    case MODIFICAR_ALMACEN:
                        mensajeTX.ponerParametros(String.valueOf(modificarAlmacen(mensajeRX)));
                        break;
                        
                    // @author Alejandro
                    case MODIFICAR_ALBERGUE:
                        mensajeTX.ponerParametros(String.valueOf(modificarAlbergue(mensajeRX)));
                        break;
                    
                    // @author Alejandro
                    case ELIMINAR_VOLUNTARIO:
                        mensajeTX.ponerParametros(String.valueOf(eliminarVoluntario(mensajeRX)));
                        break;
                    // @author Alejandro
                    case ELIMINAR_VEHICULO:
                        mensajeTX.ponerParametros(String.valueOf(eliminarVehiculo(mensajeRX)));
                        break;
                        
                    // @author Alejandro
                    case ELIMINAR_ALMACEN:
                        mensajeTX.ponerParametros(String.valueOf(eliminarAlmacen(mensajeRX)));
                        break;
                        
                    // @author Alejandro
                    case ELIMINAR_ALBERGUE:
                        mensajeTX.ponerParametros(String.valueOf(eliminarAlbergue(mensajeRX)));
                        break;
                    
                    // @author Alejandro
                    case BUSCAR_VOLUNTARIO:                    
                        Object busqueda = db.buscarVoluntario(mensajeRX.verParametros());
                        if (busqueda == null){
                            mensajeTX.ponerParametros("null");
                        }
                        else {
                            mensajeTX.ponerParametros(busqueda.toString());
                        }                        
                        break;
                        
                    // @author Alejandro
                    case BUSCAR_VEHICULO:
                        busqueda = db.buscarVehiculo(mensajeRX.verParametros());
                        if (busqueda == null){
                            mensajeTX.ponerParametros("null");
                        }
                        else {
                            mensajeTX.ponerParametros(busqueda.toString());
                        }
                        break;
                    // @author Alejandro
                    case BUSCAR_ALMACEN:
                        busqueda = db.buscarAlmacen(mensajeRX.verParametros());
                        if (busqueda == null){
                            mensajeTX.ponerParametros("null");
                        }
                        else {
                            mensajeTX.ponerParametros(busqueda.toString());
                        }
                        break;
                    
                    // @author Alejandro
                    case BUSCAR_ALBERGUE:
                        busqueda = db.buscarAlbergue(mensajeRX.verParametros());
                        if (busqueda == null){
                            mensajeTX.ponerParametros("null");
                        }
                        else {
                            mensajeTX.ponerParametros(busqueda.toString());
                        }
                        break;
                    // @author Miguel
                    case LISTAR_PLANES:
                        ArrayList<PlanProteccion> listaPlanes = db.getPlanes();
                        mensajeTX.ponerParametros(String.valueOf(listaPlanes.size()));
                        for(int i=0; i< listaPlanes.size(); i++) {
                            mensajeTX.anadirParametro(listaPlanes.get(i).toString());
                        } 
                        break;
                    case ADD_PLAN:
                        String parametros = mensajeRX.verParametros();
                        System.out.println("comms add plan: " + parametros);
                        String delims = ",";
                        String[] tokens = parametros.split(delims);
                        int numPlanes = Integer.parseInt(tokens[0]);
                        int longitudParametros = 5;
                        int posicion;
//                        for(int i = 0; i < numPlanes; i++){
//                            posicion = i*longitudParametros;
//                            posicion -= 1;
                            System.out.println("--PLAN RECIBIDO:\n\t"+tokens[0/*+posicion*/] + "\n\t" + tokens[1/*+posicion*/]+ "\n\t" + tokens[2/*+posicion*/]+ "\n\t" + tokens[3/*+posicion*/]+ "\n\t" + tokens[4/*+posicion*/]);
                            String idPlan = tokens[0/*+posicion*/];
                            //System.out.println("\t"+tokens[2+posicion]);
                            String nombrePlan = tokens[1/*+posicion*/];
                            int vehiculos = Integer.parseInt(tokens[2/*+posicion*/]);
                            int voluntarios = Integer.parseInt(tokens[3/*+posicion*/]);
                            String actuaciones = tokens[4/*+posicion*/];

                            PlanProteccion plan = new PlanProteccion(idPlan, nombrePlan, 
                                    vehiculos, voluntarios, actuaciones);
                            db.addPlan(plan);
                        
                    case MOD_PLAN:
                        parametros = mensajeRX.verParametros();
                        delims = ",";
                        tokens = parametros.split(delims);
                        numPlanes = Integer.parseInt(tokens[0]);
                        longitudParametros = 5;
                        for(int i = 0; i < numPlanes; i++){
                            posicion = i*longitudParametros;
                            posicion -= 1;
                            System.out.println("--PLAN RECIBIDO:\n\t"+tokens[1+posicion] + "\n\t" + tokens[2+posicion]+ "\n\t" + tokens[3+posicion]+ "\n\t" + tokens[4+posicion]+ "\n\t" + tokens[5+posicion]);
                            idPlan = tokens[1+posicion];
                            //System.out.println("\t"+tokens[2+posicion]);
                            nombrePlan = tokens[2+posicion];
                            vehiculos = Integer.parseInt(tokens[3+posicion]);
                            voluntarios = Integer.parseInt(tokens[4+posicion]);
                            actuaciones = tokens[5+posicion];

                            plan = new PlanProteccion(idPlan, nombrePlan, 
                                    vehiculos, voluntarios, actuaciones);
                            db.modPlan(plan);
                        }
                        //System.out.println(cadena);
                        //PlanProteccion plan = new PlanProteccion(mensajeRX.verParametros());
                        break;
                    case ELIMINAR_PLAN:
                        parametros = mensajeRX.verParametros();
                        delims = ",";
                        tokens = parametros.split(delims);
                        numPlanes = Integer.parseInt(tokens[0]);
                        /*longitudParametros = 5;
                        for(int i = 0; i < numPlanes; i++){
                            posicion = i*longitudParametros;
                            posicion -= 1;
                            System.out.println("--PLAN RECIBIDO:\n\t"+tokens[1+posicion] + "\n\t" + tokens[2+posicion]+ "\n\t" + tokens[3+posicion]+ "\n\t" + tokens[4+posicion]+ "\n\t" + tokens[5+posicion]);
                            idPlan = tokens[1+posicion];
                            //System.out.println("\t"+tokens[2+posicion]);
                            nombrePlan = tokens[2+posicion];
                            vehiculos = Integer.parseInt(tokens[3+posicion]);
                            voluntarios = Integer.parseInt(tokens[4+posicion]);
                            actuaciones = tokens[5+posicion];

                            plan = new PlanProteccion(idPlan, nombrePlan, 
                                    vehiculos, voluntarios, actuaciones);*/
                            db.eliminarPlan(tokens[0]);
                        //}
                        break;
                    //---------------
                    //--EMERGENCIAS--
                    //---------------
                    // @author Miguel
                    case LISTAR_EMERGENCIAS:
                        ArrayList<Emergencia> listaEmergencias = db.getEmergencias();
                        mensajeTX.ponerParametros(String.valueOf(listaEmergencias.size()));
                        for(int i=0; i< listaEmergencias.size(); i++) {
                            mensajeTX.anadirParametro(listaEmergencias.get(i).toString());
                        } 
                        break;
                    case ADD_EMERGENCIA:
                        parametros = mensajeRX.verParametros();
                        System.out.println("comms add emergencia: " + parametros);
                        delims = ",";
                        tokens = parametros.split(delims);
                        numPlanes = Integer.parseInt(tokens[0]);
                        //System.out.println("--PLAN RECIBIDO:\n\t"+tokens[0/*+posicion*/] + "\n\t" + tokens[1/*+posicion*/]+ "\n\t" + tokens[2/*+posicion*/]+ "\n\t" + tokens[3/*+posicion*/]+ "\n\t" + tokens[4/*+posicion*/]);
                        String idEmergencia = tokens[0];
                        
                        idPlan = tokens[1];
                        nombrePlan = tokens[2];
                        vehiculos = Integer.parseInt(tokens[3]);
                        voluntarios = Integer.parseInt(tokens[4]);
                        actuaciones = tokens[5];

                        plan = new PlanProteccion(idPlan, nombrePlan, 
                                    vehiculos, voluntarios, actuaciones);
                            
                        String tipo = tokens[6];
                        int nivel = Integer.parseInt(tokens[7]);
                        Emergencia emergencia = new Emergencia(idEmergencia, plan, tipo, nivel);
                        db.addEmergencia(emergencia);
                        
                    case MOD_EMERGENCIA:
                        parametros = mensajeRX.verParametros();
                        delims = ",";
                        tokens = parametros.split(delims);
                        numPlanes = Integer.parseInt(tokens[0]);
                        longitudParametros = 4;
                        for(int i = 0; i < numPlanes; i++){
                            posicion = i*longitudParametros;
                            posicion -= 1;
                            System.out.println("--PLAN RECIBIDO:\n\t"+tokens[1+posicion] + "\n\t" + tokens[2+posicion]+ "\n\t" + tokens[3+posicion]+ "\n\t" + tokens[4+posicion]+ "\n\t" + tokens[5+posicion]);
                            idEmergencia = tokens[0];
                        
                            idPlan = tokens[1];
                            nombrePlan = tokens[2];
                            vehiculos = Integer.parseInt(tokens[3]);
                            voluntarios = Integer.parseInt(tokens[4]);
                            actuaciones = tokens[5];

                            plan = new PlanProteccion(idPlan, nombrePlan, 
                                        vehiculos, voluntarios, actuaciones);

                            tipo = tokens[6];
                            nivel = Integer.parseInt(tokens[7]);
                            emergencia = new Emergencia(idEmergencia, plan, tipo, nivel);
                            db.modEmergencia(emergencia);
                        }
                        //System.out.println(cadena);
                        //PlanProteccion plan = new PlanProteccion(mensajeRX.verParametros());
                        break;
                    case ELIMINAR_EMERGENCIA:
                        parametros = mensajeRX.verParametros();
                        delims = ",";
                        tokens = parametros.split(delims);
                        numPlanes = Integer.parseInt(tokens[0]);
                        /*longitudParametros = 5;
                        for(int i = 0; i < numPlanes; i++){
                            posicion = i*longitudParametros;
                            posicion -= 1;
                            System.out.println("--PLAN RECIBIDO:\n\t"+tokens[1+posicion] + "\n\t" + tokens[2+posicion]+ "\n\t" + tokens[3+posicion]+ "\n\t" + tokens[4+posicion]+ "\n\t" + tokens[5+posicion]);
                            idPlan = tokens[1+posicion];
                            //System.out.println("\t"+tokens[2+posicion]);
                            nombrePlan = tokens[2+posicion];
                            vehiculos = Integer.parseInt(tokens[3+posicion]);
                            voluntarios = Integer.parseInt(tokens[4+posicion]);
                            actuaciones = tokens[5+posicion];

                            plan = new PlanProteccion(idPlan, nombrePlan, 
                                    vehiculos, voluntarios, actuaciones);*/
                            db.eliminarPlan(tokens[0]);
                        //}
                        break;
                    case ADD_ALERTA:
                        parametros = mensajeRX.verParametros();
                        System.out.println("comms add emergencia: " + parametros);
                        delims = ",";
                        tokens = parametros.split(delims);
                        numPlanes = Integer.parseInt(tokens[0]);
                        //System.out.println("--PLAN RECIBIDO:\n\t"+tokens[0/*+posicion*/] + "\n\t" + tokens[1/*+posicion*/]+ "\n\t" + tokens[2/*+posicion*/]+ "\n\t" + tokens[3/*+posicion*/]+ "\n\t" + tokens[4/*+posicion*/]);
                        float coordX = Float.parseFloat(tokens[0]);
                        float coordY = Float.parseFloat(tokens[1]);
                        //Coordenada coordenada = new Coordenada(coordX, coordY);
                        idEmergencia = tokens[2];
                        emergencia = db.getEmergencia(idEmergencia);
                        idAlerta = tokens[3];
                        boolean gestionada;
                        if(tokens[4]=="true")
                            gestionada = true;
                        else
                            gestionada = false;
                        Date fecha = new Date(tokens[5]);
                        boolean activa;
                        if(tokens[6]=="true")
                            activa = true;
                        else
                            activa = false;
                        afectados = Integer.parseInt(tokens[7]);
                        
                        Alerta alerta = new Alerta(new Coordenada(coordX, coordY), 
                                emergencia, Integer.parseInt(idAlerta), gestionada, fecha, activa, afectados);
                        break; 
                    case LISTAR_ZONAS:
                        ArrayList<ZonaSeguridad> listaZonas = db.getZonas();
                        mensajeTX.ponerParametros(String.valueOf(listaZonas.size()));
                        for(int i=0; i< listaZonas.size(); i++) {
                            mensajeTX.anadirParametro(listaZonas.get(i).toString());
                        } 
                        break;
                    case LOGIN:
                        //POR HACER
                        break;
                    case REGISTRO:
                        //POR HACER
                        break;
                }
                salida.writeObject(mensajeTX);
                
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ClassNotFoundException ex) {
                 Logger.getLogger(Comms.class.getName()).log(Level.SEVERE, null, ex);
             }
        }
    }
    /*
    ** Acepta conexiones
    **@author Cristian
    */
    public void start(){
        conexiones = true;
    }
    
    /*
    ** Deja de aceptap conexiones
    **@author Cristian
    */
    public void parar(){
        conexiones = false;
    }
 
    /**
     * Envia un correo a una cuenta de Gmail
     * @author Cristian
     */
    public void enviarCorreoGmail(String destino){
        String usuarioCorreo = "admsis2cn@gmail.com";
        String contrasena = "administracion@";
        String asunto = "Activar plan de proteccion";
        String cuerpo = "Tienes que activar el plan de proteccion...";
 
        try{
            Properties p = new Properties();
            p.put("mail.smtp.host", "smtp.gmail.com");
            p.setProperty("mail.smtp.starttls.enable", "true");
            p.setProperty("mail.smtp.port", "587");
            p.setProperty("mail.smtp.user", usuarioCorreo);
            p.setProperty("mail.smpt.auth","true");
            
            Session s = Session.getDefaultInstance(p,null);
            
            BodyPart texto = new MimeBodyPart();
            texto.setText(cuerpo);
            
            MimeMultipart mp = new MimeMultipart();
            mp.addBodyPart(texto);
            
            MimeMessage m = new MimeMessage(s);
            m.setFrom(new InternetAddress(usuarioCorreo));
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(destino));
            m.setContent(mp);
            m.setSubject(asunto);

            Transport t = s.getTransport("smtp");
            t.connect(usuarioCorreo, contrasena);
            t.sendMessage(m, m.getAllRecipients());
            t.close();
  
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    /**
     * @author Alejandro Cencerrado
     * Lee el mansaje del cliente creando un objeto que tratará de insertar en la BD.
     * @param mensajeRX
     * @return Devuelve cierto en caso de éxito.
     */
    private boolean insertarAlbergue(Mensaje mensajeRX){
        String parametros = mensajeRX.verParametros();
        String delims = ",";
        String[] tokens = parametros.split(delims);
        String id = tokens[0];
        int capacidad = Integer.parseInt(tokens[1]);

        float x = Float.parseFloat(tokens[2]);
        float y = Float.parseFloat(tokens[3]);                
        Coordenada coordenadas = new Coordenada(x, y);

        int ocupacion = Integer.parseInt(tokens[4]);

        Albergue albergue = new Albergue(id, capacidad, coordenadas,ocupacion);
        
        return db.insertarAlbergue(albergue);
    }
    
    /**
     * @author Alejandro Cencerrado
     * Lee el mansaje del cliente creando un objeto que tratará de insertar en la BD.
     * @param mensajeRX
     * @return Devuelve cierto en caso de éxito.
     */
    private boolean insertarAlmacen(Mensaje mensajeRX){
        String parametros = mensajeRX.verParametros();
        String delims = ",";
        String[] tokens = parametros.split(delims);
        
        String id = tokens[0];
        int cantidadMantas = Integer.parseInt(tokens[1]);
        int cantidadComida = Integer.parseInt(tokens[2]);
        int cantidadAgua = Integer.parseInt(tokens[3]);

        float x = Float.parseFloat(tokens[4]);
        float y = Float.parseFloat(tokens[5]);                
        Coordenada coordenadas = new Coordenada(x, y);

        int capacidad = Integer.parseInt(tokens[6]);

        Almacen almacen = new Almacen(id, cantidadMantas, cantidadComida, cantidadAgua, coordenadas, capacidad);

        return db.insertarAlmacen(almacen);
    }
    
    /**
     * @author Alejandro Cencerrado
     * Lee el mansaje del cliente creando un objeto que tratará de insertar en la BD.
     * @param mensajeRX
     * @return Devuelve cierto en caso de éxito.
     */
    private boolean insertarVoluntario(Mensaje mensajeRX){
        String parametros = mensajeRX.verParametros();
        String delims = ",";
        String[] tokens = parametros.split(delims);
        
        String id = tokens[0];
        String nombre = tokens[1];
        String telefono = tokens[2];
        String correo = tokens[3];

        float x = Float.parseFloat(tokens[4]);
        float y = Float.parseFloat(tokens[5]);                
        Coordenada coordenadas = new Coordenada(x, y);

        boolean esConductor = Boolean.parseBoolean(tokens[6]);
        boolean disponible = Boolean.parseBoolean(tokens[7]);

        Voluntario voluntario = new Voluntario(id, nombre, telefono, 
                                    correo, coordenadas, esConductor, disponible);
        
        return db.insertarVoluntario(voluntario);
    }
    
    /**
     * @author Alejandro Cencerrado
     * Lee el mansaje del cliente creando un objeto que tratará de insertar en la BD.
     * @param mensajeRX
     * @return Devuelve cierto en caso de éxito.
     */
    private boolean insertarVehiculo(Mensaje mensajeRX){
        String parametros = mensajeRX.verParametros();
        String delims = ",";
        String[] tokens = parametros.split(delims);
        
        String id = tokens[0];
        String modelo = tokens[1];
        int plazas = Integer.parseInt(tokens[2]);

        float x = Float.parseFloat(tokens[3]);
        float y = Float.parseFloat(tokens[4]);                
        Coordenada coordenadas = new Coordenada(x, y);

        boolean disponible = Boolean.parseBoolean(tokens[5]);

        Vehiculo vehiculo = new Vehiculo(id, modelo, plazas, coordenadas, disponible);

        return db.insertarVehiculo(vehiculo);
    }
    
        /**
     * @author Alejandro Cencerrado
     * Lee el mansaje del cliente creando un objeto que tratará de modificar en la BD.
     * @param mensajeRX
     * @return Devuelve cierto en caso de éxito.
     */
    private boolean modificarAlbergue(Mensaje mensajeRX){
        String parametros = mensajeRX.verParametros();
        String delims = ",";
        String[] tokens = parametros.split(delims);
        
        String id = tokens[0];
        int capacidad = Integer.parseInt(tokens[1]);

        float x = Float.parseFloat(tokens[2]);
        float y = Float.parseFloat(tokens[3]);                
        Coordenada coordenadas = new Coordenada(x, y);

        int ocupacion = Integer.parseInt(tokens[4]);

        Albergue albergue = new Albergue(id, capacidad, coordenadas,ocupacion);
        
        return db.modificarAlbergue(albergue);
    }
    
    /**
     * @author Alejandro Cencerrado
     * Lee el mansaje del cliente creando un objeto que tratará de modificar en la BD.
     * @param mensajeRX
     * @return Devuelve cierto en caso de éxito.
     */
    private boolean modificarAlmacen(Mensaje mensajeRX){
        String parametros = mensajeRX.verParametros();
        String delims = ",";
        String[] tokens = parametros.split(delims);
        
        String id = tokens[0];
        int cantidadMantas = Integer.parseInt(tokens[1]);
        int cantidadComida = Integer.parseInt(tokens[2]);
        int cantidadAgua = Integer.parseInt(tokens[3]);

        float x = Float.parseFloat(tokens[4]);
        float y = Float.parseFloat(tokens[5]);                
        Coordenada coordenadas = new Coordenada(x, y);

        int capacidad = Integer.parseInt(tokens[6]);

        Almacen almacen = new Almacen(id, cantidadMantas, cantidadComida, cantidadAgua, coordenadas, capacidad);

        return db.modificarAlmacen(almacen);
    }
    
    /**
     * @author Alejandro Cencerrado
     * Lee el mansaje del cliente creando un objeto que tratará de modificar en la BD.
     * @param mensajeRX
     * @return Devuelve cierto en caso de éxito.
     */
    private boolean modificarVoluntario(Mensaje mensajeRX){
        String parametros = mensajeRX.verParametros();
        String delims = ",";
        String[] tokens = parametros.split(delims);
        
        String id = tokens[0];
        String nombre = tokens[1];
        String telefono = tokens[2];
        String correo = tokens[3];

        float x = Float.parseFloat(tokens[4]);
        float y = Float.parseFloat(tokens[5]);                
        Coordenada coordenadas = new Coordenada(x, y);

        boolean esConductor = Boolean.parseBoolean(tokens[6]);
        boolean disponible = Boolean.parseBoolean(tokens[7]);

        Voluntario voluntario = new Voluntario(id, nombre, telefono, 
                                    correo, coordenadas, esConductor, disponible);
        
        return db.modificarVoluntario(voluntario);
    }
    
    /**
     * @author Alejandro Cencerrado
     * Lee el mansaje del cliente creando un objeto que tratará de modificar en la BD.
     * @param mensajeRX
     * @return Devuelve cierto en caso de éxito.
     */
    private boolean modificarVehiculo(Mensaje mensajeRX){
        String parametros = mensajeRX.verParametros();
        String delims = ",";
        String[] tokens = parametros.split(delims);
        
        String id = tokens[0];
        String modelo = tokens[1];
        int plazas = Integer.parseInt(tokens[2]);

        float x = Float.parseFloat(tokens[3]);
        float y = Float.parseFloat(tokens[4]);                
        Coordenada coordenadas = new Coordenada(x, y);

        boolean disponible = Boolean.parseBoolean(tokens[5]);

        Vehiculo vehiculo = new Vehiculo(id, modelo, plazas, coordenadas, disponible);
        
        return db.modificarVehiculo(vehiculo);
    }
    
    /**
     * @author Alejandro Cencerrado
     * Lee el mansaje del cliente y da la orden de eliminar de la Base 
     * de datos el elemento con dicha id.
     * @param mensajeRX
     * @return Devuelve cierto en caso de éxito.
     */
    private boolean eliminarVoluntario(Mensaje mensajeRX){
        String id = mensajeRX.verParametros();
        return db.eliminarVoluntario(id);
    }
    
    /**
     * @author Alejandro Cencerrado
     * Lee el mansaje del cliente y da la orden de eliminar de la Base 
     * de datos el elemento con dicha id.
     * @param mensajeRX
     * @return Devuelve cierto en caso de éxito.
     */
    private boolean eliminarVehiculo(Mensaje mensajeRX){
        String id = mensajeRX.verParametros();
        return db.eliminarVehiculo(id);
    }
    
    /**
     * @author Alejandro Cencerrado
     * Lee el mansaje del cliente y da la orden de eliminar de la Base 
     * de datos el elemento con dicha id.
     * @param mensajeRX
     * @return Devuelve cierto en caso de éxito.
     */
    private boolean eliminarAlmacen(Mensaje mensajeRX){
        String id = mensajeRX.verParametros();
        return db.eliminarAlmacen(id);
    }
    
    /**
     * @author Alejandro Cencerrado
     * Lee el mansaje del cliente y da la orden de eliminar de la Base 
     * de datos el elemento con dicha id.
     * @param mensajeRX
     * @return Devuelve cierto en caso de éxito.
     */
    private boolean eliminarAlbergue(Mensaje mensajeRX){
        String id = mensajeRX.verParametros();
        return db.eliminarAlbergue(id);
    }
    
   
}
