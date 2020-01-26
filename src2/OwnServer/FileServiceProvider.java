package OwnServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServiceProvider extends Thread {
    public final static String FILE_TO_SEND = "C:/Users/semih/OneDrive/Documenten/message.txt";
    public Socket socket;

    public FileServiceProvider(Socket socket){
        this.socket = socket;
    }

    public void run(){
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        try {
            File myFile = new File (FILE_TO_SEND);
            byte [] mybytearray  = new byte [(int)myFile.length()];
            fis = new FileInputStream(myFile);
            bis = new BufferedInputStream(fis);
            bis.read(mybytearray,0,mybytearray.length);
            os = socket.getOutputStream();
            System.out.println("Sending " + FILE_TO_SEND + "(" + mybytearray.length + " bytes)");
            os.write(mybytearray,0,mybytearray.length);
            os.flush();
            System.out.println("Done.");
            if (fis != null) {
                fis.close();
            }
            if (os != null) {
                os.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
