/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esharebackserver;

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
        new FileReceiver().myMain();
    }
    
    public void myMain(){
    
        Socket skt = null;
        byte[] fileByte = new byte[8192];
        InputStream is;
        FileOutputStream fos;
        int readSize;

        try {
            ServerSocket ss = new ServerSocket(Constants.PORT_FILE_C2S);

            while(true){
                
                System.out.println("Waiting...");
                skt = ss.accept();
                System.out.println("Connected...");
                
                //Receiving File Name
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
                System.out.println("Filename: "+result);
                //-- Receiving File Name
                
                //Decode Dir(get file name that to be send)
                JSONObject main = new JSONObject(result);
                String filePath = main.getString(Constants.JSON_FILE_DWNLD); 
                //-- Decode Dir
                
                //Creating New Directories
                File f = new File(Constants.ROOT_DIR+filePath);
                f = f.getParentFile();
                f.mkdirs();
                //-- Creating New Directories
                
                //Sending Dummy Packet
                PrintWriter out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(skt.getOutputStream())
                        ), true);
                out.println("dummy");
                out.flush();
                //-- Sending Dummy Packet

                
                is = skt.getInputStream();
                fos = new FileOutputStream(Constants.ROOT_DIR+filePath);//---actual fileName

                while((readSize=is.read(fileByte))>0)//downloading file 
                {
                    fos.write(fileByte, 0, readSize);
                    fos.flush();
                }
                fos.close();
                skt.close();
                System.out.println("File Sent...");
            }
            
        } catch (IOException ex) {
            String msg = "\n\n**"+Constants.ERR_PORT + Constants.PORT_FILE_C2S+"**";
            String sol = "SOLUTION: Find [PID_OF_PROCESS] running on port using command:"
                    + "\n\tlsof -i :"+Constants.PORT_FILE_C2S+" | grep LISTEN | cut -d' ' -f2"
                    + "\nAnd Kill Process using command:"
                    + "\n\tkill -9 [PID_OF_PROCESS]\n\n";
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, msg + "\n"+ sol);
            
            ex.printStackTrace();
        } catch (JSONException ex) {
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
}
