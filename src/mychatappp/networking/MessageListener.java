/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mychatappp.networking;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import mychatappp.gui.LoginScreen;
import java.security.*;
import javax.crypto.*;

/**
 *
 * @author silent
 */
public class MessageListener extends Thread {
    
    ServerSocket server;
    int listenPort = 8877;
    WritableGUI gui;
    LoginScreen sc = new LoginScreen();
    String pt=null;
    
    public MessageListener(WritableGUI gui, int port){
        this.listenPort = port;
        this.gui = gui;
        try {
            server = new ServerSocket(port);
        } catch (IOException ex) {
            Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public MessageListener(){
        try {
            server = new ServerSocket(listenPort);
        } catch (IOException ex) {
            Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /** Security
     * 
     */
    
    public void decryptMessage(InputStream inStream) throws IOException, NoSuchAlgorithmException
    {
        try {

            //Create the Data input stream from the socket
            DataInputStream dis = new DataInputStream(inStream);

            //Get the key
            ObjectInputStream in = new ObjectInputStream(new FileInputStream("KeyFile.xx"));

            PrivateKey privatekey = (PrivateKey) in.readObject();
            System.out.println("Key Used: " + in.toString());
            in.close();

            //Initiate the cipher
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");                        
            cipher.init(Cipher.DECRYPT_MODE,privatekey);

            int len = dis.readInt();
            byte[] encryptedMsg = new byte[len];
            dis.readFully(encryptedMsg);         

            System.out.println("Server - Msg Length: " + len);
            System.out.println("Server - Encrypted: " + asHex(encryptedMsg));

            // -Print out the decrypt String to see if it matches the original message.
            byte[] plainText = cipher.doFinal(encryptedMsg);
            System.out.println("Decrypted Message: " + new String(plainText, "UTF-8"));
            
            pt = new String(plainText, "UTF-8");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Function to make the bytes printable (hex format)
    public static String asHex(byte buf[]) {
        StringBuilder strbuf = new StringBuilder(buf.length * 2);
        int i;
        for (i = 0; i < buf.length; i++) {
            if (((int) buf[i] & 0xff) < 0x10) {
                strbuf.append("0");
            }
            strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
        }
        return strbuf.toString();
    }

    @Override
    public void run() {
        Socket clientSocket;
        
        try {
            while((clientSocket = server.accept()) != null){
                InputStream is = clientSocket.getInputStream();
                decryptMessage(is);
                gui.write(pt);
            }
        } catch (IOException ex) {
            Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(MessageListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
