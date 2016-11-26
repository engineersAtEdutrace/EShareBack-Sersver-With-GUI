/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshareback.backend;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author sagar
 */
public class FileOperations {
    public boolean mkDir(String dirPath){
        File file = new File(Constants.ROOT_DIR + dirPath);
        return file.mkdirs();
    }
    
    public ArrayList<String> delete(JSONArray set) throws JSONException{
        
        ArrayList<String> err = new ArrayList<>();
        for(int i=0; i<set.length(); i++){
            String filePath = set.getString(i);
            File f = new File(Constants.ROOT_DIR + filePath);
            if(!f.delete())
                err.add(filePath);
        }
        return err;
    }
    
    public boolean rename(String oldPath, String newPath){
        File f = new File(Constants.ROOT_DIR + oldPath);
        File newF = new File(Constants.ROOT_DIR + newPath);
        return f.renameTo(newF);
    }
    
    public boolean copy(String destPath, JSONArray filePaths){
        for(int i=0; i<filePaths.length(); i++){
            try {
                String oldPath = filePaths.getString(i);
                File oldF = new File(Constants.ROOT_DIR + oldPath);
                File newF = new File(Constants.ROOT_DIR + destPath + oldF.getName());
                Files.copy(oldF.toPath(), newF.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException | JSONException ex) {
                Logger.getLogger(FileOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    
    public boolean move(String destPath, JSONArray filePaths){
        for(int i=0; i<filePaths.length(); i++){
            try {
                String oldPath = filePaths.getString(i);
                File oldF = new File(Constants.ROOT_DIR + oldPath);
                File newF = new File(Constants.ROOT_DIR + destPath + oldF.getName());
                Files.move(oldF.toPath(), newF.toPath(), StandardCopyOption.REPLACE_EXISTING);
                return true;
            } catch (IOException | JSONException ex) {
                Logger.getLogger(FileOperations.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
}
