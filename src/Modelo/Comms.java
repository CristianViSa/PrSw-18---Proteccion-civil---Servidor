/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Modelo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Cristian
 */
public class Comms extends Thread{
    
        
    private static final String ACTIVAR_PLAN = "ACTIVARPLAN";
    private static final String ALERTAS_MAPA = "ALERTASMAPA";
    private static final String HISTORIAL_ALERTAS = "HISTORIALALERTAS";

    private static final int puerto = 5500;
    private boolean conexiones = true;
    
    private ServerSocket ss;    
    private ObjectOutputStream salida;
    private ObjectInputStream entrada;
    
    public Comms(){
        try {
            ss = new ServerSocket(puerto);
            run();
        } catch (IOException ex) {
            Logger.getLogger(Comms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
    ** Sobreescribe el metodo  run
    ** @author Cristian
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
                        mensajeTX.ponerParametros("2,1,terremoto,1,41,-1,1000,true,10,1,2018");
                        mensajeTX.anadirParametro("2,alud,1,40,-1,100,true,20,5,2018");
                        break;
                    //@author Cristian
                    case ACTIVAR_PLAN:
                        mensajeTX.ponerParametros("true");
                        break;
                    //@author Cristian
                    case HISTORIAL_ALERTAS:
                        mensajeTX.ponerParametros("2,1,terremoto,1,41,-1,1000,true,10,1,2018");
                        mensajeTX.anadirParametro("2,alud,1,40,-1,100,true,20,5,2018");
                        break;
                    case LOGIN:
                        break;
                    case REGISTRO:
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
}
