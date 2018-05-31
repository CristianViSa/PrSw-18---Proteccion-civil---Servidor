package Modelo;


import db.BaseDeDatos;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
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
                        mensajeTX.ponerParametros(String.valueOf(alertas.size()));
                        for(int i=0; i< alertas.size(); i++) {
                           mensajeTX.anadirParametro(alertas.get(i).toString());
                        }
                        break;
                    //@author Cristian
                    case ACTIVAR_PLAN:
                        String idAlerta = mensajeRX.verParametros();
                        Alerta alertaBuscada = db.getAlerta(idAlerta);
                        alertaBuscada.activarPlanDeProteccion();
                        
                        ResguardoEmergencia emergenciaAlerta 
                                = alertaBuscada.getEmergencia();
                        ResguardoPlan planEmergencia =
                                emergenciaAlerta.getPlan();
                        int vehiculosNecesarios =
                                planEmergencia.getVehiculosNecesarios();
                        int voluntariosNecesarios =
                                planEmergencia.getVoluntariosNecesarios();
                        String actuacionesNecesarios = 
                                planEmergencia.getActuacionesNecesarias();
                        List<Voluntario> voluntariosDisponibles =
                                db.getVoluntarios();
                        List<Vehiculo> vehiculosDisponibles = 
                                db.getVehiculos();
                        List<ResguardoZona> zonasDeSeguridad =
                                db.getZonasDeSeguridad();
                        // posible excepcion
                        ResguardoZona zonaSegura = zonasDeSeguridad.get(0);
                        alertaBuscada.asignarZona(zonaSegura);
                        for(Voluntario voluntario : voluntariosDisponibles){
                            if(voluntariosNecesarios > 0){
                                alertaBuscada.asignarVoluntario(voluntario);
                            }
                        }
                        for(Vehiculo vehiculo : vehiculosDisponibles){
                            if(vehiculosNecesarios > 0){
                                alertaBuscada.asignarVehiculo(vehiculo);
                            }
                        }
                        List<Albergue> alberguesZona = new ArrayList<Albergue>(zonaSegura.getAlbergues()); 
                        for(Albergue albergue : alberguesZona){
                            if(!albergue.estaLleno()){
                                int afectados = alertaBuscada.getAfectados();
                                int ocupacion = albergue.getOcupacion();
                                albergue.alojar(alertaBuscada.getAfectados());
                                int alojados = albergue.getOcupacion() - ocupacion;
                                if(alojados >= afectados){
                                    break; // no aloja mas gente
                                }
                            }
                        }
                        mensajeTX.ponerParametros("true");
                        break;
                    //@author Cristian
                    case HISTORIAL_ALERTAS:
                        ArrayList<Alerta> historial = db.getAlertas();
                        mensajeTX.ponerParametros(String.valueOf(historial.size()));
                        for(int i=0; i< historial.size(); i++) {
                           mensajeTX.anadirParametro(historial.get(i).toString());
                        } 
                        //mensajeTX.ponerParametros("2,1,terremoto,1,41,-1,1000,true,10,1,2018");
                        //mensajeTX.anadirParametro("2,alud,1,40,-1,100,true,20,5,2018");
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
