import com.siebel.data.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;



    /**
     * Created by patrick on 3/8/17.
     */
    public class SiebelHelper {

        public void connect(SiebelDataBean sdb){
            Properties prop = new Properties();
            FileInputStream file = null;
            try {
                file = new FileInputStream("config.properties");
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
            try {
                prop.load(file);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            String connectString = prop.getProperty("connectstring");
            String user=prop.getProperty("user");
            String pass= prop.getProperty("pass");
            String lang = prop.getProperty("lang");

            try {
                sdb.login(connectString, user, pass, lang);
            } catch (SiebelException e) {
                e.printStackTrace();
            }
        }

        public static void desconectar(SiebelDataBean sdb){
            try{
                sdb.logoff();
            }catch (SiebelException e){
                e.printStackTrace();
            }

        }
        public static SiebelPropertySet executarWorkflow(SiebelDataBean sdb, SiebelPropertySet inputs){
            SiebelPropertySet outputs = null;

            try{
                SiebelService bsWf = sdb.getService("Workflow Process Manager");
                outputs = sdb.newPropertySet();
                bsWf.invokeMethod("RunProcess", inputs, outputs);
            }catch (SiebelException e){
//            Log.escrever(e.getMessage());
            }
            return outputs;
        }

        public static String getLovLic(SiebelDataBean sdb, String LOV, String value, String lang) {
            String lovLic = "";
            try {
                SiebelBusObject bo = sdb.getBusObject("List of Values Query");
                SiebelBusComp bc = bo.getBusComp("List of Values Query");
                bc.activateField("Language Independent Code");
                bc.clearToQuery();
                bc.setSearchSpec("Type", LOV);
                bc.setSearchSpec("Display Value", value);
                bc.setSearchSpec("Language Code", lang);
                bc.executeQuery(true);
                if (bc.firstRecord()) {
                    lovLic = bc.getFieldValue("Language Independent Code");
                }
                bo=null;
                bc=null;
            } catch (SiebelException e) {
                Log.escrever("ERRO:  " + e.getMessage());
                e.printStackTrace();
            }
            return lovLic;
        }
    }

