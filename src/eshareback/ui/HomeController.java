/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshareback.ui;

import eshareback.anithingtopdfconvert.EnvChecker;
import eshareback.backend.FeedbackServer;
import eshareback.backend.FileOperationServer;
import eshareback.backend.FileReceiver;
import eshareback.backend.FileSender;
import eshareback.backend.LsServer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
    , FeedbackServer.Callback
    , FileOperationServer.FileOperationsCallback{

    @FXML
    private TextArea msgArea;
    
    LsServer lsServer = null;
    FileReceiver frServer = null;
    FileSender fsServer = null;
    FeedbackServer fbServer = null;
    FileOperationServer foServer = null;
    
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
    
    //Start FO
    public void startFo(){
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                foServer.startServer();
            }
        });
        if(foServer == null){
            foServer = new FileOperationServer(this);
            t.start();
        }
    }
    //-- Start FO
    
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
    
    //Start SOffice
    public void startSoffice(){
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    startSOfficeService();
                } catch (InterruptedException ex) {
                    Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
                    Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        t.start();
    }
    //-- Start SOffice
    
    //Stop SOffice
    public void stopSoffice(){
        if (System.getProperty("os.name").matches(("(?i).*Linux.*"))) {
            try {
                Runtime.getRuntime().exec("pkill soffice");
            } catch (IOException ex) {
                Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
            }
    	}
    }
    //-- Stop SOffice
    
    //Start OpenOffice
   private void startSOfficeService() throws InterruptedException, IOException {
       
        EnvChecker envChecker = new EnvChecker();
        envChecker.createSh();
       
    	//Check if soffice is already running
    	String commands = "pgrep soffice";
    	Process process = Runtime.getRuntime().exec(commands);
    	int code = process.waitFor();
        //-- Check if soffice is already running
        
    	//If we get anything back from readLine, then we know the process is running
    	BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
    	if (in.readLine() == null) {
            
    		//Nothing back, then we should execute the process
                String home = System.getProperty("user.home");
                String location = home+"/startsoffice.sh";
    		process = Runtime.getRuntime().exec(location);
    		code = process.waitFor();
    		      System.out.println("soffice script started");
    	} else {
    		System.out.println("soffice script is already running");
    	}

    	in.close();
    }
    //Stop OpenOffice

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
    
//    public static void main(String[] args) {
//        try {
//            HomeController con = new HomeController();
//            EnvChecker e = new EnvChecker();
//            e.createSh();
//            con.startSOfficeService();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }

    @Override
    public void onFOServerStarted() {
        msgArea.appendText("\nFileOperations Server Started...");
    }

    @Override
    public void onFOServerStopped() {        
        msgArea.appendText("\nFileOperations Server Stopped...");
        foServer = null;
    }
}
