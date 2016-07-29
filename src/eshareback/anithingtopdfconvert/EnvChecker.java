/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshareback.anithingtopdfconvert;

import com.sun.deploy.Environment;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sagar
 */
public class EnvChecker {
    public void createSh(){
        try {
            String home = System.getProperty("user.home");
            File f = new File(home+"/startsoffice.sh");
            if(!f.exists()){
                f.createNewFile();
                FileWriter fw = new FileWriter(f);
                fw.write("soffice -headless -accept=\"socket,host=127.0.0.1,port=8100;urp;\" -nofirststartwizard");
                fw.close();
            }
            
            if(!f.canExecute()){
                f.setExecutable(true);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(EnvChecker.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(EnvChecker.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    public static void main(String[] args) {
        new EnvChecker().createSh();
    }
}
