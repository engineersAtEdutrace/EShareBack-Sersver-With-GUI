/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshareback.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author sagar
 */
public class FileOperationServer {
    
    ServerSocket ss;
    FileOperationsCallback callback;
    FileOperations fileOperations;
    public FileOperationServer(FileOperationsCallback callback){
        this.callback = callback;
        fileOperations = new FileOperations();
    }

    public interface FileOperationsCallback{
        void onFOServerStarted();
        void onFOServerStopped();
    }
    
    public void startServer(){

        String result = null;
        
        try {   
                ss = new ServerSocket(Constants.PORT_FILE_OEPRATIONS);
                callback.onFOServerStarted();
                
        } catch (IOException ex) {
            String msg = "\n\n**"+Constants.ERR_PORT + Constants.PORT_FILE_OEPRATIONS+"**";
            String sol = "SOLUTION: Find [PID_OF_PROCESS] running on port using command:"
                    + "\n\tlsof -i :"+Constants.PORT_FILE_OEPRATIONS+" | grep LISTEN | cut -d' ' -f2"
                    + "\nAnd Kill Process using command:"
                    + "\n\tkill -9 [PID_OF_PROCESS]\n\n";
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, msg + "\n"+ sol);
            callback.onFOServerStopped();
            return;
        }
            
        while(true){
            //Start Listening
            Socket skt = null;
            try{
                System.out.println("FILE_OPERATIONS Waiting...");
                skt = ss.accept();
                System.out.println("FILE_OPERATIONS Accepted");
            }
            catch(IOException ex){
                ex.printStackTrace();
                System.out.println("Feedback Server Stopped");
                return;
            }
            //-- Start Listening

            //Receive Request
            try{
                result = receiveRequest(skt);
            }
            catch(IOException ex){
                ex.printStackTrace();
                continue;
            }
            //-- Receive Request
            
            //Decode Request
            String path = null; 
            try{
                JSONObject main = new JSONObject(result);
                String requestType = main.getString(Constants.JSON_FO_OPERATION);
                switch(requestType){
                    case Constants.FO_COPY:
                        String oldPath = main.getString(Constants.JSON_FO_OLD_FILE);
                        String newPath = main.getString(Constants.JSON_FO_NEW_FILE);
                        
                        boolean response = fileOperations.copy(oldPath, newPath);
                        break;
                        
                    case Constants.FO_DELETE:
                        String filePaths = main.getString(Constants.JSON_FO_OLD_FILE);
                        JSONArray files = new JSONArray();
                        files.put(filePaths);
                        ArrayList<String> al = fileOperations.delete(files);
                        //return***
                        break;
                        
                    case Constants.FO_MKDIR:
                        String filePath = main.getString(Constants.JSON_FO_DIR_PATH);
                        response = fileOperations.mkDir(filePath);
                        break;
                        
                    case Constants.FO_MOVE:
                        oldPath = main.getString(Constants.JSON_FO_OLD_FILE);
                        newPath = main.getString(Constants.JSON_FO_NEW_FILE);
                        response = fileOperations.move(oldPath, newPath);
                        break;
                        
                    case Constants.FO_RENAME:
                        oldPath = main.getString(Constants.JSON_FO_OLD_FILE);
                        newPath = main.getString(Constants.JSON_FO_NEW_FILE);
                        response = fileOperations.rename(oldPath, newPath);
                        break;
                }
            }
            catch (JSONException ex) {
                String str = Constants.ERR_JSON + "\n\t" + result;
                Logger.getLogger(LsServer.class.getName()).log(Level.SEVERE, str );
                continue;
            }
            //-- Decode Request

        }
        
    }
    
    private String receiveRequest(Socket skt) throws IOException{
        
        //Receiving Request
        BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
        String result = "";
        String temp = "";
        while((temp = br.readLine()) != null){
            if(temp.contains(Constants.END_OF_MSG)){
                temp = temp.replace(Constants.END_OF_MSG, "");
                result+=temp;
                break;
            }
            result += temp;
        }
        System.out.println("Request: "+result);
        //-- Receiving Request
        
        return result;
    }
    
    private void sendResponse(Socket skt, JSONObject msg){
    
    }
}
