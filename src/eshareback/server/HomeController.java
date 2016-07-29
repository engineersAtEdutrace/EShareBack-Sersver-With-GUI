/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshareback.server;

import esharebackserver.FeedbackServer;
import esharebackserver.FileReceiver;
import esharebackserver.FileSender;
import esharebackserver.LsServer;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author sagar
 */
public class HomeController 
    implements Initializable
    , LsServer.LsCallback
    , FileReceiver.FrCallback
    , FileSender.FsCallback
    , FeedbackServer.Callback{

    @FXML
    private TextArea msgArea;
    
    LsServer lsServer = null;
    FileReceiver frServer = null;
    FileSender fsServer = null;
    FeedbackServer fbServer = null;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    //Start Ls
    public void startLs(){
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                lsServer.startServer();
            }
        });
        if(lsServer == null){
            lsServer = new LsServer(this);
            t.start();
        }
    }
    //-- Start Ls
    
    //Start Fs
    public void startFs(){    
    
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                fsServer.startServer();
            }
        });
        if(fsServer == null){
            fsServer = new FileSender(this);
            t.start();
        }
    }
    //-- Start Fs
    
    //Start Fr
    public void startFr(){
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                frServer.startServer();
            }
        });
        if(frServer == null){
            frServer = new FileReceiver(this);
            t.start();
        }
    }
    //-- Start Fr
    
    //Start Feedback
    public void startFeedback(){
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                fbServer.startServer();
            }
        });
        if(fbServer == null){
            fbServer = new FeedbackServer(this);
            t.start();
        }
    }
    //-- Start Feedback

    //Stop Ls
    public void stopLs(){
        lsServer.stopServer();
    }
    //Stop Ls
    
    //Stop Fs
    public void stopFs(){
        fsServer.stopServer();
    }
    //Stop Fs
    
    //Stop Fr
    public void stopFr(){
        frServer.stopServer();
    }
    //Stop Fr
    
    //Stop Feedback
    public void stopFeedback(){
        fbServer.stopServer();
    }
    //Stop Feedback

    //Callbacks
    @Override
    public void onFsServerStarted() {
        msgArea.appendText("\nFs Server Started...");
    }

    @Override
    public void onFsServerStopped() {
        msgArea.appendText("\nFs Server Stopped...");
        fsServer = null;
    }

    @Override
    public void onLsServerStarted() {
        msgArea.appendText("\nLs Server Started...");
    }

    @Override
    public void onLsServerStopped() {
        msgArea.appendText("\nLs Server Stopped...");
        lsServer = null;
    }

    @Override
    public void onFrServerStarted() {
        msgArea.appendText("\nFr Server Started...");
    }

    @Override
    public void onFrServerStopped() {
        msgArea.appendText("\nFr Server Stopped...");
        frServer = null;
    }
    //-- Callbacks

    @Override
    public void onFeedbackServerStarted() {
        msgArea.appendText("\nFeedback Server Started...");
    }

    @Override
    public void onFeedbackServerStopped() {
        msgArea.appendText("\nFeedback Server Stopped...");
        frServer = null;
    }

    @Override
    public void onDbStarted() {
        msgArea.appendText("\nDB Connected...");
    }
}
