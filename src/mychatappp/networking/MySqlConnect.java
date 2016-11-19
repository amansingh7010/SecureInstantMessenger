/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mychatappp.networking;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Silent
 */
public class MySqlConnect {
    Connection conn = null;
    public static Connection ConnectDb(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver Loaded");
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/mychatapp", "aman", "kelakha");
            System.out.println("Connected to Database");
            return conn;
            
        } catch(Exception e){
            JOptionPane.showMessageDialog(null, e);
            return null;
        }
    }
}
