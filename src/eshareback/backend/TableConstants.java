/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshareback.backend;

/**
 *
 * @author sagar
 */
public class TableConstants {
    
    final static String USERNAME = "root";
    final static String PASSWORD = "";
    final static String DB_NAME = "ESHAREBACK";
    final static String TABLE_SESSIONS = "Sessions";
    final static String TABLE_FEEDBACKS = "Feedbacks";
    final static String TABLE_SESSION_FILES = "SessionFiles";
    
    final static String ATTR_SESSION_ID = "session_id";
    final static String ATTR_SESSION_NAME = "session_name";
    final static String ATTR_TIMESTAMP = "timestamp";
    final static String ATTR_RATING_1 = "rating_1";
    final static String ATTR_RATING_2 = "rating_2";
    final static String ATTR_RATING_3 = "rating_3";
    final static String ATTR_RATING_4 = "rating_4";
    final static String ATTR_RATING_5 = "rating_5";
    final static int    MAX_RATING = 5;
    final static String ATTR_INSTRUCTOR_ID = "instructor_id";
    
    final static String ATTR_COMMENT = "comment";
    
    final static String ATTR_FILE = "file";
}
