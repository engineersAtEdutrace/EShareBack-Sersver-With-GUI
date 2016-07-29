package eshareback.anithingtopdfconvert;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToPdfConverter {
    public static void main(String[] args)throws UnknownHostException, IOException {
        (new ToPdfConverter()).convert("/home/sagar/Desktop/test.docx");
    }
    
    public boolean convert(String inputFilePath){
        
        try{
            File inputFile = new File(inputFilePath);
            int index = inputFile.getName().lastIndexOf(".");
            String newFilename = inputFile.getParent()+"/"+inputFile.getName().substring(0,index)+".pdf";
            
            File outputFile = new File(newFilename);

            // connect to an OpenOffice.org instance running on port 8100
            OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
            connection.connect();

            // convert
            DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
            converter.convert(inputFile, outputFile);

            // close the connection
            connection.disconnect();

            inputFile.delete();
            return true;
        }
        catch(ConnectException e){
            String err = "*** OpenOffice service in not running please run the following command ***";
            String cmd="\tcommand : soffice -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\" -nofirststartwizard\n\n";
            Logger.getLogger(ToPdfConverter.class.getName()).log(Level.SEVERE, err+"\n"+cmd);
            //System.out.println("OpenOffice is not connected,please connect");
        }
        return false;
    }
    
}
