/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshareback.backend;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Anand Singh
 *
 * Sends file to client side on its request
 */
public class FileSender {

    public static void main(String[] args) {
        //(new FileSender()).startServer();
    }

    ServerSocket ss = null;
    
    FsCallback callback;
    public FileSender(FsCallback callback){
        this.callback = callback;
    }
    
    public void startServer() {
        String result = "";
        File dwnldFile = null;
        BufferedOutputStream bos;
        BufferedInputStream bis;
        byte[] fileBytes = new byte[Constants.BUFFER_SIZE]; //using 8k buffer size
        long numOfBuffer;
        int readSize;

        //Create root Dir
        File rootDir = new File(Constants.ROOT_DIR);
        if (!rootDir.exists()) {
            rootDir.mkdir();
        }
        //-- Create root dir

        //Client Connection
        try {
            ss = new ServerSocket(Constants.PORT_FILE_S2C);
            callback.onFsServerStarted();
        } catch (IOException ex) {
            String msg = "\n\n**" + Constants.ERR_PORT + Constants.PORT_FILE_S2C + "**";
            String sol = "SOLUTION: Find [PID_OF_PROCESS] running on port using command:"
                    + "\n\tlsof -i :" + Constants.PORT_FILE_S2C + " | grep LISTEN | cut -d' ' -f2"
                    + "\nAnd Kill Process using command:"
                    + "\n\tkill -9 [PID_OF_PROCESS]\n\n";
            Logger.getLogger(FileSender.class.getName()).log(Level.SEVERE, msg + "\n" + sol);

            ex.printStackTrace();
            callback.onFsServerStopped();
            return;
        }
        Socket skt;
        while (true) {
            //Start Listening
            try {
                System.out.println("FS Waiting...");
                skt = ss.accept();
                System.out.println("FS Connection Accepted");
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("FS Server Stopped");
                return;
            }
            //-- Start Listening
            BufferedReader br;
            result = "";
            String temp = "";
            try {
                br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
                System.out.println("Receiving...");
                while ((temp = br.readLine()) != null) {
                    if (temp.contains(Constants.END_OF_MSG)) {
                        temp = temp.replace(Constants.END_OF_MSG, "");
                        result += temp;
                        break;
                    }
                    result += temp;
                }
                System.out.println("Result: " + result);
            } catch (IOException ex) {
                ex.printStackTrace();
                continue;
            }
                //-- Receiving File Request

            //Decode Dir(get file name that to be send)
            String fileName;
            try {
                JSONObject main = new JSONObject(result);
                fileName = main.getString(Constants.JSON_FILE_DWNLD);
            } catch (JSONException ex) {
                ex.printStackTrace();
                continue;
            }
                //-- Decode Dir

            //Sending File
            try {
                dwnldFile = new File(Constants.ROOT_DIR + fileName);

                if ((dwnldFile.length() % Constants.BUFFER_SIZE) != 0) {
                    numOfBuffer = (dwnldFile.length() / Constants.BUFFER_SIZE) + 1;
                } else {
                    numOfBuffer = (dwnldFile.length() / Constants.BUFFER_SIZE);
                }

                bis = new BufferedInputStream(new FileInputStream(dwnldFile));
                bos = new BufferedOutputStream(skt.getOutputStream());
                while ((readSize = bis.read(fileBytes)) > 0) {
                    bos.write(fileBytes, 0, readSize);
                    bos.flush();
                }
                bos.close();
                skt.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
                //--Sending File

        } //end of while
    }
    
    public void stopServer(){
        try {
            ss.close();
            callback.onFsServerStopped();
        } catch (IOException ex) {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public interface FsCallback{
        void onFsServerStarted();
        void onFsServerStopped();
    }

}
