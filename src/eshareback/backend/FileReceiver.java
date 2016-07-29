/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshareback.backend;

import eshareback.anithingtopdfconvert.ToPdfConverter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author sagar
 */
public class FileReceiver {

    public static void main(String[] args) {
        //new FileReceiver().startServer();
    }

    ServerSocket ss;
    
    ToPdfConverter converter;
    FrCallback callback;
    
    public FileReceiver(FrCallback callback){
        this.callback = callback;
        converter = new ToPdfConverter();
    }

    public void startServer() {

        Socket skt = null;
        byte[] fileByte = new byte[8192];
        InputStream is;
        FileOutputStream fos;
        int readSize;

        try {
            ss = new ServerSocket(Constants.PORT_FILE_C2S);
            callback.onFrServerStarted();
        } catch (IOException ex) {
            String msg = "\n\n**" + Constants.ERR_PORT + Constants.PORT_FILE_C2S + "**";
            String sol = "SOLUTION: Find [PID_OF_PROCESS] running on port using command:"
                    + "\n\tlsof -i :" + Constants.PORT_FILE_C2S + " | grep LISTEN | cut -d' ' -f2"
                    + "\nAnd Kill Process using command:"
                    + "\n\tkill -9 [PID_OF_PROCESS]\n\n";
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, msg + "\n" + sol);

            ex.printStackTrace();
            callback.onFrServerStopped();
            return;
        }

        while (true) {

            try {
                System.out.println("FR Waiting...");
                skt = ss.accept();
                System.out.println("FR Connected...");
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("FR Server Stopped");
                return;
            }

            //Receiving File Name
            BufferedReader br;
            String result = "";
            String temp = "";
            try {
                br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
                while ((temp = br.readLine()) != null) {
                    if (temp.contains(Constants.END_OF_MSG)) {
                        temp = temp.replace(Constants.END_OF_MSG, "");
                        result += temp;
                        break;
                    }
                    result += temp;
                }
                System.out.println("Filename: " + result);
            } catch (IOException ex) {
                Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
                //-- Receiving File Name

            String filePath;
            //Decode Dir(get file name that to be send)
            try {
                JSONObject main = new JSONObject(result);
                filePath = main.getString(Constants.JSON_FILE_DWNLD);
            } catch (JSONException ex) {
                Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            //-- Decode Dir

            //Creating New Directories
            File f = new File(Constants.ROOT_DIR + filePath);
            f = f.getParentFile();
            f.mkdirs();
            //-- Creating New Directories

            //Sending Dummy Packet
            PrintWriter out;
            try {
                out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(skt.getOutputStream())
                        ), true);
                out.println("dummy");
                out.flush();
            } catch (IOException ex) {
                Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            //-- Sending Dummy Packet

            try {
                is = skt.getInputStream();
            } catch (IOException ex) {
                ex.printStackTrace();
                continue;
            }

            //Receiving File
            try {
                fos = new FileOutputStream(Constants.ROOT_DIR + filePath);//---actual fileName

                while ((readSize = is.read(fileByte)) > 0) {//downloading file 
                    fos.write(fileByte, 0, readSize);
                    fos.flush();
                }
                fos.close();
                skt.close();
                System.out.println("File Received...");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            //--Receiving File

            //Convert File to Pdf
            if(!filePath.toLowerCase().contains(".pdf"))
                converter.convert(Constants.ROOT_DIR + filePath);
            //-- Convert File to Pdf
        }

    }
    
    public void stopServer(){
        try {
            ss.close();
            callback.onFrServerStopped();
        } catch (IOException ex) {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public interface FrCallback{
        void onFrServerStarted();
        void onFrServerStopped();
    }

}
