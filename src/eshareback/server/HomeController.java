/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshareback.server;

import esharebackserver.LsServer;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author sagar
 */
public class HomeController implements Initializable {

    /**
     * Initializes the controller class.
     */
    
    @FXML
    private Button startServer;
    
    @FXML
    private Button stopServer;
    
    @FXML
    private TextArea msgArea;
    
    LsServer lsServer = null;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        lsServer = new LsServer();
    }    
    
    public void startServer(){
        new Thread(new Runnable() {

            @Override
            public void run() {
                lsServer.startServer();
            }
        }).start();
        msgArea.setText("Server Started Successfully...");
    }
    
    public void stopServer(){
        lsServer.stopServer();
        msgArea.setText("Server Stopped...");
    }
}
