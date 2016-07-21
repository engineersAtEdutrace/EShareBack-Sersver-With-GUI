/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package esharebackserver.FileDownload;

import esharebackserver.Constants;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
 * @author Anand Singh
 */
public class DownloadFileServer {
    public static void main(String[] args) {
        (new DownloadFileServer()).mymain();
    }
    
    public void mymain()
    {
        //Create root Dir
        File rootDir = new File(Constants.ROOT_DIR);
        if(!rootDir.exists())
            rootDir.mkdir();
        //-- Create root dir
        
        //connect to client
        ServerSocket ss = null;
        String result = "";
        File dwnldFile = null;
        //PrintWriter sktWriter;
        BufferedOutputStream bos;
        BufferedInputStream bis;
        byte[] fileBytes = new byte[Constants.BUFFER_SIZE]; //using 8k buffer size
        long  numOfBuffer;
        int readSize;
//        byte fileName = ;
        
        try{
             ss = new ServerSocket(Constants.PORT_FILE_S2C);
             
             while(true){
                //Start Listening
                System.out.println("Waiting...");
                Socket skt = ss.accept();
                System.out.println("Connection Accepted");
                //-- Start Listening
                
                //Receiving File Request
                BufferedReader br = new BufferedReader(new InputStreamReader(skt.getInputStream()));
                result = "";
                String temp = "";
                while((temp = br.readLine()) != null){
                    if(temp.contains(Constants.END_OF_MSG)){
                        temp = temp.replace(Constants.END_OF_MSG, "");
                        result+=temp;
                        break;
                    }
                    result += temp;
                }
                System.out.println("Result: "+result);
                //-- Receiving File Request
                
                //Decode Dir(get file name that to be send)
                JSONObject main = new JSONObject(result);
                String fileName = main.getString(Constants.JSON_FILE_DWNLD); 
                //-- Decode Dir
                
                //Send file
                dwnldFile = new File(fileName);
                
                if((dwnldFile.length()%Constants.BUFFER_SIZE)!=0)
                    numOfBuffer = (dwnldFile.length()/Constants.BUFFER_SIZE) + 1;
                else
                    numOfBuffer = (dwnldFile.length()/Constants.BUFFER_SIZE);
                
                bis = new BufferedInputStream(new FileInputStream(dwnldFile));
                bos =new BufferedOutputStream(skt.getOutputStream());
                while((readSize=bis.read(fileBytes))>0)
                {
                    bos.write(fileBytes,0,readSize);
                    bos.flush();
                }
                bos.close();
                skt.close();                
                //--Send file
                
                //--listen for other client
                
            } //end of while
                    
        }catch(IOException ex){
            String msg = "\n\n**"+Constants.ERR_PORT + Constants.PORT_FILE_S2C+"**";
            String sol = "SOLUTION: Find [PID_OF_PROCESS] running on port using command:"
                    + "\n\tlsof -i :"+Constants.PORT_FILE_S2C+" | grep LISTEN | cut -d' ' -f2"
                    + "\nAnd Kill Process using command:"
                    + "\n\tkill -9 [PID_OF_PROCESS]\n\n";
           Logger.getLogger(DownloadFileServer.class.getName()).log(Level.SEVERE, msg + "\n"+ sol);
        } catch (JSONException ex) {
            Logger.getLogger(DownloadFileServer.class.getName()).log(Level.SEVERE, null, ex);
        } 
//           catch (JSONException ex) {
//            String str = Constants.ERR_JSON + "\n\t" + result;
//            Logger.getLogger(DownloadFileServer.class.getName()).log(Level.SEVERE, str );
//        }
    }
            
}
