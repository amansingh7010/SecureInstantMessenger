/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package mychatappp.networking;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.security.KeyPairGenerator;
import java.security.*;
import javax.crypto.*;

/**
 *
 * @author silent
 */
public class MessageTransmitter extends Thread {

    String message, hostname;
    int transmitPort;
    public static final int kBufferSize = 8192;
    
    public MessageTransmitter() {
    }

    public MessageTransmitter(String message, String hostname, int port) {
        this.message = message;
        this.hostname = hostname;
        this.transmitPort = port;
    }

    @Override
    public void run() {
        try {
            
            
            
            /** Security
             * 
             */
            
            //Generate new key
            KeyPair keyPair = KeyPairGenerator.getInstance("RSA").genKeyPair();
            PrivateKey privateKey = keyPair.getPrivate();
            PublicKey publicKey = keyPair.getPublic();
            
            //Compute Signature
           Signature instance = Signature.getInstance("SHA1withRSA");
           instance.initSign(privateKey);
           instance.update(message.getBytes());
           byte[] signature = instance.sign();
            
            
            //Encrypt Message
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encryptedMsg = cipher.doFinal(message.getBytes());
            
            //Store the key in a file
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("KeyFile.xx"));
            out.writeObject(privateKey);
            out.close();
            

            Socket s = new Socket(hostname, transmitPort);
            DataOutputStream os = new DataOutputStream(s.getOutputStream());
            
            //Open stream to cipher server

            os.writeInt(encryptedMsg.length);
            os.write(encryptedMsg);
            os.writeInt(signature.length);
            os.write(signature);
            os.flush();
            os.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
