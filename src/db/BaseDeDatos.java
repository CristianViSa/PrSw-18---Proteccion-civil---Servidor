package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import Modelo.Albergue;
import Modelo.Alerta;
import Modelo.Almacen;
import Modelo.Coordenada;
import Modelo.Emergencia;
import Modelo.PlanProteccion;
import Modelo.ZonaSeguridad;
import Modelo.Vehiculo;
import Modelo.Voluntario;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author Alejandro Cencerrado
 */
public class BaseDeDatos {

    private Connection con = null;
    private final String SERVIDOR = "jdbc:mysql://155.210.68.162:3306/u732306";
    private final String USUARIO = "u732306";
    private final String CONTRASENA = "u732306";
    /**
    * @author Alejandro Cencerrado
    * @throws NamingException
    */
    public BaseDeDatos() throws NamingException {
        
    }
    
    /**
     * @author Alejandro Cencerrado
    * Cerrar conexión
    * @throws SQLException
    */
    public void cerrarConexion() throws Exception {
        try{
            if(con != null) {
                con.close();
            }
        }
        catch (SQLException e) {
            throw new Exception("Error cerrando conexión bd");
        }
    }
    
    /**
    * @author Alejandro Cencerrado 
    * Abre conexión con la base de datos
    * @throws SQLException
    */
    public void abrirConexion() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(SERVIDOR,USUARIO,CONTRASENA);
            con.setAutoCommit(false);
        } 
        catch (SQLException e) {
            throw new Exception("Error abriendo conexión bd");
        }
    }
    
    /*** 
    * @author Alejandro Cencerrado  
    * Hace commit
    * @throws Exception
    */
    public void commit() throws Exception {
        try {
            con.commit();
        } 
        catch (SQLException e) {
            throw new Exception("Error haciendo commit");
        }
    }

    /*** Hace rollback
    * @author Alejandro Cencerrado* 
    * @throws Exception
    */
    public void rollback() throws Exception {
        try {
            con.rollback();
        }
        catch (SQLException e) {
            throw new Exception("Error haciendo rollback ");
        }
    }
    
    /**
    * Ejecuta una consulta
    * @param query
    * @throws SQLException 
    * @author Alejandro Cencerrado
    */
    public void ejecutarUpdate(String query) throws SQLException{
        Statement statment = con.createStatement(); 
        statment.executeUpdate(query); 
    }

    /**
     * @author Alejandro Cencerrado
     * @param query
     * @return
     * @throws SQLException 
     */
    public ResultSet ejecutarConsulta(String query) throws SQLException{
        Statement statment = con.createStatement();
        ResultSet resultSet = (ResultSet) statment.executeQuery(query);		
        return resultSet;
    }   
    
    
    /*
        -------------------------------------------------------
        ------------------   ALBERGUES-------------------------
        -------------------------------------------------------
    */
    
    
    /**
     * @author Alejandro Cencerrado
     * @return 
     * 
     * Obtiene y devuelve la lista de todos los albergues almacenados en la BD.
     */
    public ArrayList<Albergue> getAlbergues(){
        try {
            abrirConexion();
            String query="SELECT * from Albergue";
            ArrayList<Albergue> vectorAlbergues = new ArrayList<Albergue>();
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            
            while (resultSet.next()) {
                Albergue albergue = new Albergue(resultSet.getString("id"),
                                        resultSet.getInt("capacidad"),
                                        new Coordenada(resultSet.getFloat("coordenadaX"), 
                                                resultSet.getFloat("coordenadaY")),
                                        resultSet.getInt("ocupacion"));  
                vectorAlbergues.add(albergue);
            }		
            resultSet.close();
            cerrarConexion();

            return vectorAlbergues;
            
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }  
    
    /**
    * @author Alejandro Cencerrado
    * Inserción en la base de datos de un nuevo Albergue. 
    * Devuelve cierto en caso de éxito.
     * @param albergue
     * @return 
     * 
    */
   public boolean insertarAlbergue(Albergue albergue){

        try {
            abrirConexion();
            String query = "Insert into Albergue values('" 
                            + albergue.getId() + "'," 
                            + albergue.getCapacidad() + "," 
                            + albergue.getPosicion().getX()+"," 
                            + albergue.getPosicion().getY() + "," 
                            + albergue.getOcupacion() 
                            + ")";

            ejecutarUpdate(query);
            commit();
            cerrarConexion();
            return true;
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
   }
   
   /**
    * Elimina el albergue deseado de la base de datos.
    * @author Alejandro Cencerrado
    * @param idAlbergue 
    * @return Devuelve cierto si la eliminación es correcta.
    */
   public boolean eliminarAlbergue(String idAlbergue){
       try {            
            if (buscarAlbergue(idAlbergue) != null){
                abrirConexion();
                String query = "Delete from Albergue where id='" + idAlbergue + "'";
                ejecutarUpdate(query);
                commit();
                cerrarConexion();
                return true;
            }
           
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }  
       return false;
   }
   
   /**
    * Modifica el albergue deseado en la BD.
    * @author Alejandro Cencerrado
    * @param albergue
    * @return Devuelve cierto en caso de éxito.
    */
   public boolean modificarAlbergue(Albergue albergue){
       try {
            abrirConexion();
            String query = "Update Albergue set capacidad =" + albergue.getCapacidad() +
                                ", coordenadaX="+albergue.getPosicion().getX() + 
                                ", coordenadaY="+albergue.getPosicion().getY() + 
                                ", ocupacion="+albergue.getOcupacion() +
                                " where id='"+albergue.getId()+"'";

            ejecutarUpdate(query);
            commit();
            cerrarConexion();
            return true;
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        } 
       return false;
   }
   
   /**
    * @author Alejandro Cencerrado
    * @param idAlbergue
    * @return  Devuelve un objeto de la clase Albergue con la id buscada.
    * En caso de no encontrarlo devuelve null.
    */
   public Albergue buscarAlbergue(String idAlbergue){
       
       try {
            abrirConexion();
            String query="SELECT * from Albergue where id='" + idAlbergue +"'";            
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            Albergue albergue = null;
            if(resultSet.next()){
                albergue = new Albergue(resultSet.getString("id"),
                                        resultSet.getInt("capacidad"),
                                        new Coordenada(resultSet.getFloat("coordenadaX"), 
                                        resultSet.getFloat("coordenadaY")),
                                        resultSet.getInt("ocupacion"));		
            }
            resultSet.close();
            cerrarConexion();

            return albergue;
            
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
   }
   
   
   /**
    * @author Cristian
    * 
    */
   public ArrayList<Albergue> getAlberguesZona(String idZona){
       
       try {
            abrirConexion();
            String query="SELECT id_albergue from Tiene where id_zona='" + idZona +"'";            
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            ArrayList<Albergue> albergues = new ArrayList<Albergue>();
            if(resultSet.next()){
                Albergue albergue = this.buscarAlbergue(resultSet.getString("id_albergue"));
                albergues.add(albergue);
            }
            resultSet.close();
            cerrarConexion();

            return albergues;
            
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
   }
    
   
   /*
        -------------------------------------------------------
        ------------------   ALMACENES -------------------------
        -------------------------------------------------------
    */
   
    /**
     * @author Alejandro Cencerrado
     * @return Devuelve los Almacenes existentes en la Base de Datos.
     */
    public ArrayList<Almacen> getAlmacenes() {
        try {
            abrirConexion();
            String query="SELECT * from Almacen";
            ArrayList<Almacen> vectorAlmacenes = new ArrayList<Almacen>();
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            
            while (resultSet.next()) {
                Almacen almacen = new Almacen(resultSet.getString("id"),
                                        resultSet.getInt("cantidadMantas"),
                                        resultSet.getInt("cantidadComida"),
                                        resultSet.getInt("cantidadAgua"),
                                        new Coordenada(resultSet.getFloat("coordenadaX"),
                                                        resultSet.getFloat("coordenadaY")),
                                        resultSet.getInt("capacidad"));
                vectorAlmacenes.add(almacen);
            }		
            resultSet.close();
            cerrarConexion();

            return vectorAlmacenes;
            
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
    * @author Alejandro Cencerrado 
    * Inserción en la base de datos de un nuevo Albergue
    * @param almacen
    * @return Deuvelve cierto en caso de éxito.
    */
   public boolean insertarAlmacen(Almacen almacen){

        try {
            abrirConexion();
            String query = "Insert into Almacen values('" 
                            + almacen.getId() + "'," 
                            + almacen.getCantidadMantas() + "," 
                            + almacen.getCantidadComida() + "," 
                            + almacen.getCantidadAgua() + "," 
                            + almacen.getPosicion().getX()+"," 
                            + almacen.getPosicion().getY() + "," 
                            + almacen.getCapacidad()
                            + ")";

            ejecutarUpdate(query);
            commit();
            cerrarConexion();
            return true;
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
   }
   
   /**
    * @author Alejandro Cencerrado
    * 
    * Elimina el Almacen deseado.
    * @param idAlmacen 
     * @return  Cierto en caso de éxito.
    */
   public boolean eliminarAlmacen(String idAlmacen){
       try {            
            if (buscarAlmacen(idAlmacen) != null){
                abrirConexion();
                String query = "Delete from Almacen where id='" + idAlmacen + "'";
                ejecutarUpdate(query);
                commit();
                cerrarConexion();
                return true;
            }
           
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }  
       return false;
   }
   
   /**
    * @author Alejandro Cencerrado
    * Modifica el Almacen recibido por parámetro.
    * @param almacen 
     * @return  Cierto en caso de éxito.
    */
   public boolean modificarAlmacen(Almacen almacen){
       try {
            abrirConexion();
            String query = "Update Almacen set capacidad =" + almacen.getCapacidad() +
                                ", coordenadaX="+almacen.getPosicion().getX() + 
                                ", coordenadaY="+almacen.getPosicion().getY() + 
                                ", cantidadMantas="+almacen.getCantidadMantas() +
                                ", cantidadComida="+almacen.getCantidadComida() +
                                ", cantidadAgua="+almacen.getCantidadAgua() +
                                " where id='"+almacen.getId()+"'";

            ejecutarUpdate(query);
            commit();
            cerrarConexion();
            return true;
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        } 
       return false;
   }
   
   /**
    * @author Alejandro Cencerrado
    * Busca el Almacen con la id pasada.
    * @param idAlmacen
    * @return Objeto Almacen buscado o null si no lo encuentra en la BD.
    */
   public Almacen buscarAlmacen(String idAlmacen){
       
       try {
            abrirConexion();
            String query="SELECT * from Almacen where id='" + idAlmacen +"'";            
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            Almacen almacen = null;
            if(resultSet.next()){
                almacen = new Almacen(resultSet.getString("id"),
                                        resultSet.getInt("cantidadMantas"),
                                        resultSet.getInt("cantidadComida"),
                                        resultSet.getInt("cantidadAgua"),
                                        new Coordenada(resultSet.getFloat("coordenadaX"), 
                                        resultSet.getFloat("coordenadaY")),
                                        resultSet.getInt("capacidad"));		
            }
            resultSet.close();
            cerrarConexion();

            return almacen;
            
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
   }
   /**
    * @author Cristian
    * 
    */
   public ArrayList<Almacen> getAlmacenesZona(String idZona){
       
       try {
            abrirConexion();
            String query="SELECT id_almacen from Dispone where id_zona='" + idZona +"'";            
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            ArrayList<Almacen> almacenes = new ArrayList<Almacen>();
            if(resultSet.next()){
                Almacen almacen = this.buscarAlmacen(resultSet.getString("id_almacen"));
                almacenes.add(almacen);
            }
            resultSet.close();
            cerrarConexion();

            return almacenes;
            
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
   }
    
    /*
        -------------------------------------------------------
        ------------------   VOLUNTARIOS -------------------------
        -------------------------------------------------------
    */
      
    /**
     * @author Alejandro Cencerrado
     * @return Lista de Voluntarios existentes en la BD.
     */
    public ArrayList<Voluntario> getVoluntarios(){
        try {
            abrirConexion();
            String query="SELECT * from Voluntario";
            ArrayList<Voluntario> vectorVoluntarios = new ArrayList<Voluntario>();
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            
            while (resultSet.next()) {
                Voluntario voluntario = new Voluntario(resultSet.getString("id"),
                                        resultSet.getString("nombre"),
                                        resultSet.getString("telefono"),
                                        resultSet.getString("correo"),
                                        new Coordenada(resultSet.getFloat("coordenadaX"), 
                                                resultSet.getFloat("coordenadaY")),
                                        resultSet.getBoolean("esConductor"),
                                        resultSet.getBoolean("estaDisponible"));  
                vectorVoluntarios.add(voluntario);
            }		
            resultSet.close();
            cerrarConexion();

            return vectorVoluntarios;
            
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

   /**
    * @author Alejandro Cencerrado
    * Lanza la inserción de un Voluntario con los datos pasados por parámetro.
    * @param voluntario
    * @return Cierto en caso de éxito.
    */
   public boolean insertarVoluntario(Voluntario voluntario){

        try {
            abrirConexion();
            String query = "Insert into Voluntario values('" 
                            + voluntario.getId() + "','" 
                            + voluntario.getNombre() + "','" 
                            + voluntario.getTelefono() + "','" 
                            + voluntario.getCorreo()+ "'," 
                            + voluntario.getPosicion().getX()+"," 
                            + voluntario.getPosicion().getY() + ","
                            + voluntario.getEsConductor() + ","
                            + voluntario.getDisponible()
                            + ")";

            ejecutarUpdate(query);
            commit();
            cerrarConexion();
            return true;
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
   }
   
   /**
    * @author Alejandro Cencerrado
    * @param id 
     * @return  Cierto en caso de éxito.
    */
   public boolean eliminarVoluntario(String id){
       try {            
            if (buscarVoluntario(id) != null){
                abrirConexion();
                String query = "Delete from Voluntario where id='" + id + "'";
                ejecutarUpdate(query);
                commit();
                cerrarConexion();
                return true;
            }
           
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }  
       return false;
   }
   
   /**
    * @author Alejandro Cencerrado
    * @param voluntario 
     * @return  
    */
   public boolean modificarVoluntario(Voluntario voluntario){
       try {
            abrirConexion();
            String query = "Update Voluntario set nombre ='" + voluntario.getNombre() +
                                "', telefono='" + voluntario.getTelefono() + "'" +
                                ", correo='" + voluntario.getCorreo()+ "'" +                                
                                ", coordenadaX="+voluntario.getPosicion().getX() + 
                                ", coordenadaY="+voluntario.getPosicion().getY() + 
                                ", esConductor="+voluntario.getEsConductor() +
                                ", estaDisponible=" + voluntario.getDisponible() +
                                " where id='"+voluntario.getId()+"'";

            ejecutarUpdate(query);
            commit();
            cerrarConexion();
            return true;
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        } 
       return false;
   }
   
   /**
    * @author Alejandro Cencerrado
    * @param id
    * @return Devuelve objeto de la clase Voluntario con la id buscada.
    */
   public Voluntario buscarVoluntario(String id){
       
       try {
            abrirConexion();
            String query="SELECT * from Voluntario where id='" + id +"'";            
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            Voluntario voluntario = null;
            if(resultSet.next()){
                voluntario = new Voluntario(resultSet.getString("id"),
                                        resultSet.getString("nombre"),
                                        resultSet.getString("telefono"),
                                        resultSet.getString("correo"),                        
                                        new Coordenada(resultSet.getFloat("coordenadaX"), 
                                        resultSet.getFloat("coordenadaY")),
                                        resultSet.getBoolean("esConductor"),
                                        resultSet.getBoolean("estaDisponible"));		
            }
            resultSet.close();
            cerrarConexion();

            return voluntario;
            
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
   }
    
    
    /*
        -------------------------------------------------------
        ------------------   VEHÍCULOS -------------------------
        -------------------------------------------------------
    */
    
     /**
     * @author Alejandro Cencerrado
     * @return 
     */
    public ArrayList<Vehiculo> getVehiculos(){
        try {
            abrirConexion();
            String query="SELECT * from Vehiculo";
            ArrayList<Vehiculo> vectorVehiculos = new ArrayList<Vehiculo>();
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            
            while (resultSet.next()) {
                Vehiculo vehiculo = new Vehiculo(resultSet.getString("id"),
                                        resultSet.getString("modelo"),
                                        resultSet.getInt("plazas"),
                                        new Coordenada(resultSet.getFloat("coordenadaX"), 
                                                resultSet.getFloat("coordenadaY")),
                                        resultSet.getBoolean("disponible"));  
                vectorVehiculos.add(vehiculo);
            }		
            resultSet.close();
            cerrarConexion();

            return vectorVehiculos;
            
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * @author Alejandro Cencerrado
     * @param vehiculo
     * @return cierto en caso de éxito.
     */
    public boolean insertarVehiculo(Vehiculo vehiculo){

        try {
            abrirConexion();
            String query = "Insert into Vehiculo values('" 
                            + vehiculo.getId() + "','" 
                            + vehiculo.getModelo() + "'," 
                            + vehiculo.getPlazas()+ "," 
                            + vehiculo.getPosicion().getX()+"," 
                            + vehiculo.getPosicion().getY() + ","
                            + vehiculo.isDisponible()
                            + ")";

            ejecutarUpdate(query);
            commit();
            cerrarConexion();
            return true;
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
   }
   
    /**
     * @author Alejandro Cencerrado
     * @param id 
     * @return  
     */
   public boolean eliminarVehiculo(String id){
       try {            
            if (buscarVehiculo(id) != null){
                abrirConexion();
                String query = "Delete from Vehiculo where id='" + id + "'";
                ejecutarUpdate(query);
                commit();
                cerrarConexion();
                return true;
            }
           
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }  
       return false;
   }
   
   /**
    * @author Alejandro Cencerrado
    * @param vehiculo 
    */
   public boolean modificarVehiculo(Vehiculo vehiculo){
       try {
            abrirConexion();
            String query = "Update Vehiculo set modelo ='" + vehiculo.getModelo() +
                                "', plazas=" + vehiculo.getPlazas() +                                
                                ", coordenadaX="+vehiculo.getPosicion().getX() + 
                                ", coordenadaY="+vehiculo.getPosicion().getY() + 
                                ", disponible=" + vehiculo.isDisponible() +
                                " where id='" + vehiculo.getId() + "'";

            ejecutarUpdate(query);
            commit();
            cerrarConexion();
            return true;
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        } 
       return false;
   }
   
   /**
    * @author Alejandro Cencerrado
    * @param id
    * @return 
    */
   public Vehiculo buscarVehiculo(String id){
       
       try {
            abrirConexion();
            String query="SELECT * from Vehiculo where id='" + id +"'";            
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            Vehiculo vehiculo = null;
            if(resultSet.next()){
                vehiculo = new Vehiculo(resultSet.getString("id"),
                                        resultSet.getString("modelo"),
                                        resultSet.getInt("plazas"),                      
                                        new Coordenada(resultSet.getFloat("coordenadaX"), 
                                        resultSet.getFloat("coordenadaY")),
                                        resultSet.getBoolean("disponible"));		
            }
            resultSet.close();
            cerrarConexion();

            return vehiculo;
            
        } catch (Exception ex) {
            Logger.getLogger(BaseDeDatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
   }
   
   
    /*
        -------------------------------------------------------
        ------------------   ALERTAS   -------------------------
        -------------------------------------------------------
    */
    
    
    /**
     * @author Cristian
     * Obtiene y devuelve la lista de todas las alertas almacenadas en la BD.
     */
    public ArrayList<Alerta> getAlertas(){
        try {
            abrirConexion();
            String query="SELECT * from Alerta";
            ArrayList<Alerta> vectorAlertas = new ArrayList<Alerta>();
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            
            while (resultSet.next()) {
                String idEmergencia = resultSet.getString("id_emergencia");
                String idZona = resultSet.getString("id_zona");
                Emergencia emergencia = buscarEmergencia(idEmergencia);
                ZonaSeguridad zona = buscarZona(idZona);
                int gestion = resultSet.getInt("gestionada");
                int activa = resultSet.getInt("activa");
                boolean gestionada = false;
                boolean activada = false;
                if(gestion == 1){   gestionada = true;}
                if(activa == 1){    activada = true;}

                Alerta alerta = new Alerta(new Coordenada(resultSet.getFloat("coordenadaX"), 
                                    resultSet.getFloat("coordenadaY")),
                                    emergencia,
                                    Integer.parseInt(resultSet.getString("id")),
                                    gestionada,
                                    null, null,
                                    new Date(resultSet.getDate("fecha").getTime()),
                                    activada,
                                    zona, 
                                    resultSet.getInt("afectados"));
                vectorAlertas.add(alerta);
            }		
            resultSet.close();
            cerrarConexion();

            return vectorAlertas;
            
        } catch (Exception ex) {
           ex.printStackTrace();
        }
        return null;
    }  
    /**
     * @author Cristian
     * Obtiene y devuelve la lista de todas las alertas almacenadas en la BD.
     */
    public ArrayList<Alerta> getAlertasActivas(){
        try {
            abrirConexion();
            String query="SELECT * from Alerta WHERE activa = 1";
            ArrayList<Alerta> alertas = new ArrayList<Alerta>();
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            
            while (resultSet.next()) {
                String idEmergencia = resultSet.getString("id_emergencia");
                String idZona = resultSet.getString("id_zona");
                Emergencia emergencia = buscarEmergencia(idEmergencia);
                ZonaSeguridad zona = buscarZona(idZona);
                int gestion = resultSet.getInt("gestionada");
                int activa = resultSet.getInt("activa");
                boolean gestionada = false;
                boolean activada = false;
                if(gestion == 1){   gestionada = true;}
                if(activa == 1){    activada = true;}

                Alerta alerta = new Alerta(new Coordenada(resultSet.getFloat("coordenadaX"), 
                                    resultSet.getFloat("coordenadaY")),
                                    emergencia,
                                    Integer.parseInt(resultSet.getString("id")),
                                    gestionada,
                                    null, null,
                                    new Date(resultSet.getDate("fecha").getTime()),
                                    activada,
                                    zona, 
                                    resultSet.getInt("afectados"));
                alertas.add(alerta);
            }		
            resultSet.close();
            cerrarConexion();

            return alertas;
            
        } catch (Exception ex) {
           ex.printStackTrace();
        }
        return null;
    }  
    /**
    * @author Cristian
    */
    public Alerta getAlerta(String id){
       
       try {
            abrirConexion();
            String query="SELECT * from Alerta where id='" + id +"'";            
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            Alerta alerta = null;
            if(resultSet.next()){
                String idEmergencia = resultSet.getString("id_emergencia");
                String idZona = resultSet.getString("id_zona");
                Emergencia emergencia = buscarEmergencia(idEmergencia);
                ZonaSeguridad zona = buscarZona(idZona);
                int gestion = resultSet.getInt("gestionada");
                int activa = resultSet.getInt("activa");
                boolean gestionada = false;
                boolean activada = false;
                if(gestion == 1){   gestionada = true;}
                if(activa == 1){    activada = true;}

                alerta = new Alerta(new Coordenada(resultSet.getFloat("coordenadaX"), 
                                    resultSet.getFloat("coordenadaY")),
                                    emergencia,
                                    Integer.parseInt(resultSet.getString("id")),
                                    gestionada,
                                    new ArrayList<Voluntario>(),
                                    new ArrayList<Vehiculo>(),
                                    new Date(resultSet.getDate("fecha").getTime()),
                                    activada,
                                    zona, 
                                    resultSet.getInt("afectados"));
            }
            resultSet.close();
            cerrarConexion();

            return alerta;
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
   }
    
    
    /*
        -------------------------------------------------------
        ------------------   ZONAS DE SEGURIDAD   -------------------------
        -------------------------------------------------------
    */
    
    /**
    * @author Cristian
    */
    public ZonaSeguridad buscarZona(String id){
       
       try {
            abrirConexion();
            String query="SELECT * from ZonaDeSeguridad where id='" + id +"'";            
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            ZonaSeguridad zona = null;
            if(resultSet.next()){
                List<Albergue> albergues = getAlberguesZona(id);
                List<Almacen> almacenes = getAlmacenesZona(id);
                
                zona = new ZonaSeguridad(new Coordenada(resultSet.getFloat("coordenadaX"), 
                            resultSet.getFloat("coordenadaY")),
                            Integer.parseInt(resultSet.getString("id")),
                            almacenes, albergues);		
            }
            resultSet.close();
            cerrarConexion();

            return zona;
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
   }
    
    /**
     * @author Cristian
     * Obtiene y devuelve la lista de todas las alertas almacenadas en la BD.
     */
    public ArrayList<ZonaSeguridad> getZonasDeSeguridad(){
        try {
            abrirConexion();
            String query="SELECT * from ZonaDeSeguridad ";
            ArrayList<ZonaSeguridad> alertas = new ArrayList<ZonaSeguridad>();
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            
            while (resultSet.next()) {
                String id = resultSet.getString("id");
                ArrayList<Almacen> almacenes = getAlmacenesZona(id);
                ArrayList<Albergue> albergues = getAlberguesZona(id);
                
                ZonaSeguridad zona = new ZonaSeguridad(new Coordenada(
                    resultSet.getInt("coordenadaX"),
                    resultSet.getInt("coordenadaY")),
                    Integer.parseInt(id),
                    almacenes,  albergues);
                alertas.add(zona);
            }		
            resultSet.close();
            cerrarConexion();

            return alertas;
            
        } catch (Exception ex) {
           ex.printStackTrace();
        }
        return null;
    }
    
    /*
        -------------------------------------------------------
        ------------------   EMERGENCIAS   -------------------------
        -------------------------------------------------------
    */
    
    /**
    * @author Cristian
    */
    public Emergencia buscarEmergencia(String id){
       
       try {
            abrirConexion();
            String query="SELECT * from Emergencia where id='" + id +"'";            
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            Emergencia emergencia = null;
            if(resultSet.next()){
                String idPlan = resultSet.getString("idPlan");
                PlanProteccion plan = buscarPlan(idPlan);
                emergencia = new Emergencia(resultSet.getString("tipo"),
                                Integer.parseInt(resultSet.getString("id")),
                                resultSet.getInt("nivel"),
                                plan);		
            }
            resultSet.close();
            cerrarConexion();

            return emergencia;
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
   }
    /*
        -------------------------------------------------------
        ------------------   PLANES DE PROTECCION   -------------------------
        -------------------------------------------------------
    */
       /**
    * @author Cristian
    */
    public PlanProteccion buscarPlan(String id){
        try {
             abrirConexion();
             String query="SELECT * from PlanDeProteccion where id='" + id +"'";            
             ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
             PlanProteccion plan = null;
             if(resultSet.next()){
                plan = new PlanProteccion(resultSet.getString("id"),
                    resultSet.getString("nombre"),
                    resultSet.getInt("vehiculosNecesarios"),
                    resultSet.getInt("voluntariosNecesarios"),
                    resultSet.getString("actuacionesNecesarios"));		
             }
             resultSet.close();
             cerrarConexion();

             return plan;

         } catch (Exception ex) {
             ex.printStackTrace();
         }
         return null;
    } 
    
    /*
     * @author Miguel
    */
    public ArrayList<PlanProteccion> getPlanes(){
        try {
            abrirConexion();
            String query="SELECT * from PlanDeProteccion";
            ArrayList<PlanProteccion> vectorPlanes = new ArrayList<PlanProteccion>();
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            
            while (resultSet.next()) {
                String idPlan = resultSet.getString("id");
                String nombrePlan = resultSet.getString("nombre");
                int vehiculosNecesarios = resultSet.getInt("vehiculosNecesarios");
                int voluntariosNecesarios = resultSet.getInt("voluntariosNecesarios");
                String actuaciones = resultSet.getString("actuacionesNecesarias");
                
                PlanProteccion plan = new PlanProteccion(idPlan, nombrePlan, vehiculosNecesarios, voluntariosNecesarios, actuaciones);
                vectorPlanes.add(plan);
            }		
            resultSet.close();
            cerrarConexion();

            return vectorPlanes;
            
        } catch (Exception ex) {
           ex.printStackTrace();
        }
        return null;
    }  
    
    public void modPlan(PlanProteccion plan){
        try{
            abrirConexion();
            String query = "UPDATE PlanDeProteccion SET ";
            query += " id=" + plan.getId();
            query += ", nombre='" + plan.getNombre() + "'";
            query += ", vehiculosNecesarios=" + plan.getVehiculosNecesarios();
            query += ", voluntariosNecesarios=" + plan.getVoluntariosNecesarios();
            query += ", actuacionesNecesarias='" + plan.getActuacionesNecesarias()+"'";
            query += " WHERE id=" + plan.getId();
            System.out.println("CONSULTA: "+query);
            ejecutarUpdate(query);
            commit();
            cerrarConexion();
            //return true;
        }catch (Exception ex) {
           ex.printStackTrace();
        }
    }
    
    public void eliminarPlan(PlanProteccion plan){
        try{
            abrirConexion();
            String query = "DELETE FROM PlanDeProteccion WHERE id="+plan.getId();
            System.out.println("CONSULTA: "+query);
            ResultSet resultSet = (ResultSet) ejecutarConsulta(query);
            
            //ejecutarUpdate(query);
            commit();
            cerrarConexion();
        }catch (Exception ex) {
           ex.printStackTrace();
        }
    }
    
}
	
