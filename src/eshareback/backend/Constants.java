package eshareback.backend;

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
    public static final int PORT_FEEDBACK = 8107;
    public static final int PORT_FILE_OEPRATIONS = 8108;
    public static final int PORT_SESSION_META = 8109;
    
    public static final int BUFFER_SIZE=8192;
    
    public static final String JSON_LIST_DIR = "path"; //for ls json object { "path" : "/Data Structure/"} incoming
    public static final String JSON_DIRS = "dirs"; // response { "dirs" : ["dir1","dir2",...] , 
    public static final String JSON_FILES = "files"; // "files" : [ "file1" , "file2", ... ] }
    public static final String JSON_SERVER_IP = "server_ip";
    public static final String JSON_FILE_DWNLD = "filename"; // for downloading file { "filename" : "abc.pdf" } 
    
    public static final String JSON_FB_TYPE = "type";
    public static final String JSON_FB_RATING = "rating";
    public static final String JSON_FB_COMMENT = "comment";
    public static final String JSON_FB_SESSION_ID = "session_id";
    public static final String JSON_FB_FILES = "files";
    public static final String JSON_FB_SESSION_NAME = "session_name";
    public static final String JSON_FB_SESSIONS_INFO = "sm_request";
    public static final String JSON_FB_INSTRUCTOR_ID = "instructor_id";
    public static final String JSON_FB_TIMESTAMP = "timestamp";
    
    public static final String JSON_FO_OPERATION = "operation";
    public static final String JSON_FO_OLD_FILE = "old_file";
    public static final String JSON_FO_NEW_FILE = "new_file";
    public static final String JSON_FO_DIR_PATH = "new_dir";
    
    public static final String FO_MKDIR = "mkdir";
    public static final String FO_DELETE = "delete";
    public static final String FO_MOVE = "move";
    public static final String FO_RENAME = "rename";
    public static final String FO_COPY = "copy";
    
    
    public static final String FB_EVENT_CREATE_SESSION = "create_session";
    public static final String FB_EVENT_FEEBACK = "feedback";
    public static final String FB_EVENT_FILE_ADDED = "file_added";
    
    public static final String FB_SESSION_NAME = "session_name";
            
    public final static String ERR_PORT = "Port Not Available: ";
    public final static String ERR_JSON = "Improper JSON format: ";
    
    public static final String END_OF_MSG = "iluvuanand#wtf";
}
