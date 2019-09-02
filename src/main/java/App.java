import com.siebel.data.SiebelBusComp;
import com.siebel.data.SiebelBusObject;
import com.siebel.data.SiebelDataBean;
import com.siebel.data.SiebelException;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.io.InputStream;

public class App {

    public static void main (String args[]) throws SiebelException {
//        String ERROR = "'The value entered in field CTBC SubSegmento of buscomp CTBC Account Sync does not match any value in the bounded pick list CTBC Sub Segment.(SBL-DAT-00225)'";

        Log.escrever("# INICIO PROCESSO");

        SiebelDataBean sdb;
        SiebelHelper siebel = new SiebelHelper();
        String statusInput = args[0];

        sdb = new SiebelDataBean();
        siebel.connect(sdb);

        Log.escrever("# CONECTADO COM SUCESSO");


        try{
            SiebelBusObject bo = sdb.getBusObject("CTBC Queue");
            SiebelBusComp bc = bo.getBusComp("CTBC Queue");
            bc.activateField("Type");
            bc.activateField("Param12");
            bc.activateField("Status");
            bc.activateField("Param1");
            bc.activateField("Created");

            bc.setViewMode(3);
            bc.clearToQuery();

            bc.setSearchSpec("Type", "CREATE_ACCOUNT_ASYNC");
//            bc.setSearchSpec("Param10", ERROR);
            bc.setSearchSpec("Status", statusInput);

            bc.setSortSpec("Created (ASCENDING)");

            bc.executeQuery(true);


            bc.firstRecord();



            do{
                Log.escrever("EXECUTANDO QUEUE ID = "+bc.getFieldValue("Id")+" Cliente: "+bc.getFieldValue("Param1"));

                bc.setFieldValue("Status", "FIXED");
                bc.writeRecord();


                String xml = bc.getFieldValue("Param12").toString();
                xml = treatString(xml);



                sendRequest(xml);


            }while(bc.nextRecord());


            sdb.logoff(); //Isso não estava

        }catch (Exception e ){
            sdb.logoff(); //Nem isso
            Log.escrever("# ERRO : "+e.getMessage());
            System.out.println(e.getMessage());
        }

    }

    private static String treatString(String xml) {

        String treated = xml.replace("\n",  "");
        treated = xml
                .replace("\n", "")
                .replace("\r", "")
                .replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?><SiebelMessage MessageId=\"\" MessageType=\"Integration Object\" IntObjectName=\"CTBC Account Sync Request\" IntObjectFormat=\"Siebel Hierarchical\">", "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:crm=\"http://siebel.com/ctbc/CRMCreateAccountSync\" xmlns:acc=\"http://www.siebel.com/ctbc/AccountSyncRequest\"><soapenv:Header/><soapenv:Body><ns2:CreateAccount_Input xmlns:ns2=\"http://siebel.com/ctbc/CRMCreateAccountSync\" xmlns=\"http://www.siebel.com/ctbc/AccountSyncRequest\">")
                .replace("</Customer></SiebelMessage>", "</Customer></ns2:CreateAccount_Input></soapenv:Body></soapenv:Envelope>");

        return treated;
    }

    public static void sendRequest(String xml) {
            InputStream in ;

                StringEntity entity = new StringEntity(xml, ContentType.create(
            "text/xml", Consts.UTF_8));

            HttpPost httppost = new HttpPost(
                    "http://10.51.0.11:7777/eai_enu/start.swe?SWEExtSource=WebService&SWEExtCmd=Execute&UserName=SOAUSER&Password=S0a!us3r");

            httppost.setHeader("Content-Type", "text/xml;charset=UTF-8");
            httppost.setHeader("SOAPAction", "\"document/http://siebel.com/ctbc/CRMCreateAccountSync:CreateAccount\"");
            httppost.setEntity(entity);


            HttpClient client = HttpClients.createDefault();

            try {
                HttpResponse response = client.execute(httppost);
                System.out.println(response.toString());

            } catch (ClientProtocolException e) {
                System.out.println(e);
            } catch (IOException e) {
                System.out.println(e);
            }

        }
    }
