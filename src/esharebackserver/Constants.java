package esharebackserver;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sagar
 */
public class Constants {
    
    public static final String ROOT_DIR = System.getProperty("user.home")+"/EShareback";
    
    public static final int PORT_FILE_C2S = 8110;
    public static final int PORT_LS = 8103;
    public static final int PORT_FILE_S2C=8104;
    
    public static final int BUFFER_SIZE=8192;
    
    public static final String JSON_LIST_DIR = "path"; //for ls json object { "path" : "/Data Structure/"} incoming
    public static final String JSON_DIRS = "dirs"; // response { "dirs" : ["dir1","dir2",...] , 
    public static final String JSON_FILES = "files"; // "files" : [ "file1" , "file2", ... ] }
    public static final String JSON_FILE_DWNLD = "filename"; // for downloading file { "filename" : "abc.pdf" } 
            
    public final static String ERR_PORT = "Port Not Available: ";
    public final static String ERR_JSON = "Improper JSON format: ";
    
    public static final String END_OF_MSG = "iluvuanand#wtf";
}
