/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eshareback.backend;

import com.google.gson.Gson;
import eshareback.dto.LoginRequestDTO;
import eshareback.dto.LoginResponseDTO;
import eshareback.dto.RegisterRequestDTO;
import eshareback.dto.RegisterResponseDTO;
import eshareback.dto.SessionDTO;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author sagar
 */
public class FeedbackServer {
    
    
    
    final String DB_URL = "jdbc:mysql://localhost:3306/"+TableConstants.DB_NAME;
    Connection con = null;
    Statement stmt = null;
    
    ServerSocket ss = null;

    Callback callback;
    public FeedbackServer(Callback callback) {
        this.callback = callback;
        if(connect()){
            callback.onDbStarted();
        }
    }
    
    public static void main(String[] args) {
        System.out.println("HEre");
        FeedbackServer fs = new FeedbackServer(new Callback() {

            @Override
            public void onFeedbackServerStarted() {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onFeedbackServerStopped() {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onDbStarted() {
               // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        System.out.println("session_info:"+fs.getSessionInfo("test"));
        //fs.startServer();
        //String sessionId = fs.createSesion("Test Session");
        
//        for(int i=0; i<5; i++)
//        fs.insertFeedback(sessionId, "This is sample Comment", 4);
        
        
    }
    
    public void startServer(){

        String result = null;
        
        try {   
                ss = new ServerSocket(Constants.PORT_FEEDBACK);
                callback.onFeedbackServerStarted();
                
        } catch (IOException ex) {
            String msg = "\n\n**"+Constants.ERR_PORT + Constants.PORT_FEEDBACK+"**";
            String sol = "SOLUTION: Find [PID_OF_PROCESS] running on port using command:"
                    + "\n\tlsof -i :"+Constants.PORT_FEEDBACK+" | grep LISTEN | cut -d' ' -f2"
                    + "\nAnd Kill Process using command:"
                    + "\n\tkill -9 [PID_OF_PROCESS]\n\n";
            Logger.getLogger(FileReceiver.class.getName()).log(Level.SEVERE, msg + "\n"+ sol);
            callback.onFeedbackServerStopped();
            return;
        }
            
        while(true){
            //Start Listening
            Socket skt = null;
            try{
                System.out.println("Feedback Waiting...");
                skt = ss.accept();
                System.out.println("Feedback Connection Accepted");
            }
            catch(IOException ex){
                ex.printStackTrace();
                System.out.println("Feedback Server Stopped");
                return;
            }
            //-- Start Listening

            try{
                //Receiving Request
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
                System.out.println("Request: "+result);
                //-- Receiving Request
                
            }
            catch(IOException ex){
                ex.printStackTrace();
                continue;
            } 
            
            //Decode Request
            String path = null; 
            try{
                JSONObject main = new JSONObject(result);
                String requestType = main.getString(Constants.JSON_FB_TYPE);
                switch(requestType){
                    case Constants.FB_EVENT_CREATE_SESSION: 
                        String s = main.getString(Constants.FB_SESSION_NAME);
                        String sessionId = createSesion(s);
                        
                        sendSessionId(skt, sessionId);// Sending SessionId
                        break;
                        
                    case Constants.FB_EVENT_FEEBACK:
                        sessionId = main.getString(Constants.JSON_FB_SESSION_ID);
                        String comment = main.getString(Constants.JSON_FB_COMMENT);
                        int rating  = main.getInt(Constants.JSON_FB_RATING);
                        insertFeedback(sessionId, comment, rating);
                        break;
                        
                    case Constants.FB_EVENT_FILE_ADDED:
                        sessionId = main.getString(Constants.JSON_FB_SESSION_ID);
                        JSONArray files = main.getJSONArray(Constants.JSON_FB_FILES);
                        LinkedHashSet<String> arrfiles = new LinkedHashSet<>();
                        for(int i=0; i<files.length(); i++){
                            arrfiles.add(files.getString(i));
                        }
                        insertFile(sessionId, arrfiles);
                        break;
                        
                    case Constants.JSON_FB_SESSIONS_INFO:
                        String instructorId = main.getString(Constants.JSON_FB_INSTRUCTOR_ID);
                        String sessionInfo = getSessionInfo(instructorId);
                        sendSessionInfo(skt, sessionInfo);
                        break;
                        
                    case Constants.FB_EVENT_SESSION_DETAILS:
                        sessionId = main.getString(Constants.JSON_FB_SESSION_ID);
                        SessionDTO  dto = getSessionDetails(sessionId);
                        sendSessionDetails(skt, new Gson().toJson(dto));
                        break;
                        
                    case Constants.FB_EVENT_LOGIN:
                        String value = main.getJSONObject(Constants.JSON_FB_VALUE).toString();
                        LoginRequestDTO loginDto = new Gson().fromJson(value, LoginRequestDTO.class);
                        LoginResponseDTO responseDTO = login(loginDto.getUsername(), loginDto.getPassword());
                        sendSessionDetails(skt, new Gson().toJson(responseDTO));
                        break;
                        
                    case Constants.FB_EVENT_REGISTER:
                        value = main.getJSONObject(Constants.JSON_FB_VALUE).toString();
                        RegisterRequestDTO regRequestDto = new Gson().fromJson(value, RegisterRequestDTO.class);
                        RegisterResponseDTO regResponseDto = register(regRequestDto);
                        sendSessionDetails(skt, new Gson().toJson(regResponseDto));
                        break;
                }
            }
            catch (JSONException ex) {
                String str = Constants.ERR_JSON + "\n\t" + result;
                Logger.getLogger(LsServer.class.getName()).log(Level.SEVERE, ex.getMessage() );
                continue;
            }
            //-- Decode Request

        }
        
    }
    
    public void stopServer(){
        try {
            ss.close();
            callback.onFeedbackServerStopped();
        } catch (IOException ex) {
            Logger.getLogger(LsServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendSessionId(Socket skt, String response){
        
        try{
                //Sending Response
                PrintWriter out = new PrintWriter(
                            new BufferedWriter(
                                    new OutputStreamWriter(skt.getOutputStream())
                            ), true);
                out.println(response + Constants.END_OF_MSG);
                System.out.println("Response: "+response);
                out.flush();
                //-- Sending Response
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        
    }
    
    private boolean connect(){
        
        try {
            System.out.println("Connecting to Database...");
            con =  DriverManager.getConnection(DB_URL, TableConstants.USERNAME, TableConstants.PASSWORD);
            stmt = con.createStatement();
            
            String sql = "CREATE DATABASE IF NOT EXISTS "+TableConstants.DB_NAME;
            stmt.executeUpdate(sql);
            
            sql = "CREATE TABLE IF NOT EXISTS "+TableConstants.TABLE_SESSIONS+" (\n" +
                    "	"+TableConstants.ATTR_SESSION_ID+"	VARCHAR(32),\n" +
                    "	"+TableConstants.ATTR_SESSION_NAME+"	TEXT,\n" +
                    "	"+TableConstants.ATTR_TIMESTAMP+"	TIMESTAMP DEFAULT CURRENT_TIMESTAMP,\n" +
                    "	"+TableConstants.ATTR_RATING_1+"	INTEGER DEFAULT 0,\n" +
                    "	"+TableConstants.ATTR_RATING_2+"	INTEGER DEFAULT 0,\n" +
                    "	"+TableConstants.ATTR_RATING_3+"	INTEGER DEFAULT 0,\n" +
                    "	"+TableConstants.ATTR_RATING_4+"	INTEGER DEFAULT 0,\n" +
                    "	"+TableConstants.ATTR_RATING_5+"	INTEGER DEFAULT 0\n" +
                    ")";
            System.out.println(sql);
            stmt.executeUpdate(sql);
            
            sql = "CREATE TABLE IF NOT EXISTS "+TableConstants.TABLE_FEEDBACKS+" (\n" +
            "	"+TableConstants.ATTR_SESSION_ID+"	VARCHAR(32) NOT NULL,\n" +
            "	"+TableConstants.ATTR_COMMENT+"	TEXT\n" +
            ")";
            System.out.println(sql);
            stmt.executeUpdate(sql);
            
            sql = "CREATE TABLE IF NOT EXISTS "+TableConstants.TABLE_SESSION_FILES+" (\n" +
            "	"+TableConstants.ATTR_SESSION_ID+"	VARCHAR(32) NOT NULL,\n" +
            "	"+TableConstants.ATTR_FILE+"	TEXT\n" +
            ")";
            System.out.println(sql);
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException ex) {
            Logger.getLogger(FeedbackServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    private String createSesion(String sessionName){  //Returns Generated SessionId

        try {
            Timestamp t = new Timestamp(System.currentTimeMillis());
            String sessionId = getMd5Hash(sessionName+t);
            
            String sql = "INSERT INTO "+TableConstants.TABLE_SESSIONS+" ("+TableConstants.ATTR_SESSION_ID+","+TableConstants.ATTR_SESSION_NAME+")"
                + " VALUES (?,?)";
            
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setString(1, sessionId);
            prepStmt.setString(2, sessionName);
            
            prepStmt.executeUpdate();
            return sessionId;
            
        } catch (SQLException ex) {
            Logger.getLogger(FeedbackServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(FeedbackServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    private void insertFeedback(String sessionId, String comment, int rating){
        
        try {
            String sql = "INSERT INTO "+TableConstants.TABLE_FEEDBACKS+" ("+TableConstants.ATTR_SESSION_ID+","+TableConstants.ATTR_COMMENT+")"
                    + " VALUES (?,?)";
            
            PreparedStatement prepStmt = con.prepareStatement(sql);
            prepStmt.setString(1, sessionId);
            prepStmt.setString(2, comment);
            
            prepStmt.executeUpdate();
            String temp = "";
            switch(rating){
                case 1: temp = TableConstants.ATTR_RATING_1; break;
                case 2: temp = TableConstants.ATTR_RATING_2; break;
                case 3: temp = TableConstants.ATTR_RATING_3; break;
                case 4: temp = TableConstants.ATTR_RATING_4; break;
                case 5: temp = TableConstants.ATTR_RATING_5; break;                                
            }
            sql = "UPDATE "+ TableConstants.TABLE_SESSIONS +" SET "+temp +" = "+ temp +"+1 WHERE "+TableConstants.ATTR_SESSION_ID+"=?";/***ERROR PRONE AREA***/
            prepStmt = con.prepareStatement(sql);
            prepStmt.setString(1, sessionId);
            prepStmt.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(FeedbackServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void insertFile(String sessionId, LinkedHashSet<String> files){
        try {
            
            for(String filePath: files){
                String sql = "INSERT INTO "+TableConstants.TABLE_FEEDBACKS+" ("+TableConstants.ATTR_SESSION_ID+","+TableConstants.ATTR_FILE+")"
                        + " VALUES (?,?)";

                PreparedStatement prepStmt = con.prepareStatement(sql);
                prepStmt.setString(1, sessionId);
                prepStmt.setString(2, filePath);

                prepStmt.executeUpdate();
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(FeedbackServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String getMd5Hash(String value) throws NoSuchAlgorithmException{
    	
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(value.getBytes());
        
        byte byteData[] = md.digest();
 
        //convert the byte to hex format method 1
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
     
        System.out.println("Digest(in hex format):: " + sb.toString());
        return sb.toString();
    }
    
    private String getSessionInfo(String instructorId){
        try {
            String sql = "SELECT "
                    +TableConstants.ATTR_SESSION_ID+", "
                    +TableConstants.ATTR_SESSION_NAME+", "
                    +TableConstants.ATTR_TIMESTAMP+", "
                    +TableConstants.ATTR_RATING_1+", "
                    +TableConstants.ATTR_RATING_2+", "
                    +TableConstants.ATTR_RATING_3+", "
                    +TableConstants.ATTR_RATING_4+", "
                    +TableConstants.ATTR_RATING_5+" "
                    +" FROM "+ TableConstants.TABLE_SESSIONS
                    +" ORDER BY "+TableConstants.ATTR_TIMESTAMP+" DESC "
                    +" LIMIT 10";
                    //+"WHERE "+ATTR_INSTRUCTOR_ID+"=?"
                    ;
            System.out.println(sql);
            PreparedStatement prepStmt = con.prepareStatement(sql);
//            prepStmt.setString(1, instructorId);
            
            ResultSet rs = stmt.executeQuery(sql);
            
            JSONArray mainArr = new JSONArray();
            while(rs.next()){
                String sessionId = rs.getString(TableConstants.ATTR_SESSION_ID);
                String sessionName = rs.getString(TableConstants.ATTR_SESSION_NAME);
                String timeStamp = rs.getString(TableConstants.ATTR_TIMESTAMP);
                String star1 = rs.getString(TableConstants.ATTR_RATING_1);
                String star2 = rs.getString(TableConstants.ATTR_RATING_2);
                String star3 = rs.getString(TableConstants.ATTR_RATING_3);
                String star4 = rs.getString(TableConstants.ATTR_RATING_4);
                String star5 = rs.getString(TableConstants.ATTR_RATING_5);
                
                JSONObject main = new JSONObject();
                main.put(Constants.JSON_FB_SESSION_ID, sessionId);
                main.put(Constants.JSON_FB_SESSION_NAME, sessionName);
                main.put(Constants.JSON_FB_TIMESTAMP, timeStamp);
                
                JSONArray ratingsArr = new JSONArray(); //[2,4,6,7,8] [star1,star2,star3,star4,star5]
                ratingsArr.put(star1);
                ratingsArr.put(star2);
                ratingsArr.put(star3);
                ratingsArr.put(star4);
                ratingsArr.put(star5);               
                main.put(Constants.JSON_FB_RATING, ratingsArr);
                
                mainArr.put(main);
            }
            return mainArr.toString();
        } catch (SQLException ex) {
            Logger.getLogger(FeedbackServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JSONException ex) {
            Logger.getLogger(FeedbackServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public SessionDTO getSessionDetails(String sessionId){
        
        try{
            String sql1 = "SELECT "
                        +TableConstants.ATTR_SESSION_ID+", "
                        +TableConstants.ATTR_SESSION_NAME+", "
                        +TableConstants.ATTR_TIMESTAMP+", "
                        +TableConstants.ATTR_RATING_1+", "
                        +TableConstants.ATTR_RATING_2+", "
                        +TableConstants.ATTR_RATING_3+", "
                        +TableConstants.ATTR_RATING_4+", "
                        +TableConstants.ATTR_RATING_5+" "
                        +" FROM "+ TableConstants.TABLE_SESSIONS
                        +" WHERE "+TableConstants.ATTR_SESSION_ID+"=\""+sessionId+"\"";

            String sql2 = "SELECT "+
                    TableConstants.ATTR_COMMENT + 
                    " FROM " + TableConstants.TABLE_FEEDBACKS +
                    " WHERE " + TableConstants.ATTR_SESSION_ID + "=\""+sessionId+"\"";
            
            
            ResultSet rs2 = stmt.executeQuery(sql2);

            ArrayList<String> comments = new ArrayList<>();
            while(rs2.next()){
                String comment = rs2.getString(TableConstants.ATTR_COMMENT);
                comments.add(comment);
            }

            ResultSet rs1 = stmt.executeQuery(sql1);
            SessionDTO dto = new SessionDTO();
            while(rs1.next()){
                dto.setSessionId(sessionId);

                dto.setComments(comments);

                dto.setDate(rs1.getDate(TableConstants.ATTR_TIMESTAMP));

                int rating[] = new int[TableConstants.MAX_RATING];
                rating[0] = rs1.getInt(TableConstants.ATTR_RATING_1);
                rating[1] = rs1.getInt(TableConstants.ATTR_RATING_2);
                rating[2] = rs1.getInt(TableConstants.ATTR_RATING_3);
                rating[3] = rs1.getInt(TableConstants.ATTR_RATING_4);
                rating[4] = rs1.getInt(TableConstants.ATTR_RATING_5);
                dto.setRating(rating);

                dto.setSessionName(rs1.getString(TableConstants.ATTR_SESSION_NAME));
            }
            return dto;
        }
        catch(SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    
    private void sendSessionInfo(Socket skt, String response){
        
        try{
                //Sending Response
                PrintWriter out = new PrintWriter(
                            new BufferedWriter(
                                    new OutputStreamWriter(skt.getOutputStream())
                            ), true);
                out.println(response + Constants.END_OF_MSG);
                System.out.println("Response: "+response);
                out.flush();
                //-- Sending Response
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        
    }
    
    private void sendSessionDetails(Socket skt, String response){
        
        try{
                //Sending Response
                PrintWriter out = new PrintWriter(
                            new BufferedWriter(
                                    new OutputStreamWriter(skt.getOutputStream())
                            ), true);
                out.println(response + Constants.END_OF_MSG);
                System.out.println("Response: "+response);
                out.flush();
                //-- Sending Response
            }
            catch(IOException ex){
                ex.printStackTrace();
            }
        
    }
    
    private LoginResponseDTO login(String username, String pass){
        /*
        ***LOGIN work
        */
        LoginResponseDTO response = new LoginResponseDTO();
        if(username.equals(pass)){
            response.setLoginToken("BLAH BLAH TOKEN");
        }
        else
            response.setLoginToken(null);
        
        return response;
    }
    
    private RegisterResponseDTO register(RegisterRequestDTO dto){
        String fname = dto.getFname();
        String lname = dto.getLname();
        String email = dto.getEmail();
        String password = dto.getPass();
        
        /*
            ***Register work
        */
        
        RegisterResponseDTO responseDto = new RegisterResponseDTO();
        responseDto.setResult("Sahi hai bhai ho gaya");
        return responseDto;
    }
    
    public interface Callback{
        void onFeedbackServerStarted();
        void onFeedbackServerStopped();
        void onDbStarted();
    }
}
