import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

    /**
     * Created by patrick on 3/9/17.
     */
    public final class Log {

        private static Log instance = null;
        private static FileWriter file;
        private static PrintWriter printer;
        private static BufferedWriter output;


        public static void escrever(String texto){
            try{
                output = new BufferedWriter(new FileWriter("log.log", true));
                output.append(texto);
                output.newLine();
                output.close();
            }catch (IOException e){
                e.printStackTrace();
            }

        }

        public static void fechar() {
            try{
                output.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }


