
import eshareback.backend.FeedbackServer;
import eshareback.dto.SessionDTO;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sagar
 */
public class TestClass {
    public static void main(String[] args) {
        FeedbackServer ser = new FeedbackServer(new FeedbackServer.Callback() {

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
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        
        
        SessionDTO dto = ser.getSessionDetails("32a232e8bea7bb200307d7e6b76d22fa");
        System.out.print("");
    }
}
