package ClientV1;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerInput extends Thread{

    BufferedReader reader;

    public ServerInput(BufferedReader reader) {
        this.reader = reader;
    }

    /**
     * this is the run-method from the Server-input thread.
     * this tread continuously checks for a message sent by the server,
     * and responds with a message to the user accordingly.
     */
    @Override
    public void run() {
        try {
            while (true) {
                String rsp = reader.readLine();
                reader.mark(rsp.length());
                reader.reset();

                if (rsp.startsWith("+OK")) {
                    String[] choppdUpRsp = rsp.split(" ", 3);
                    if (choppdUpRsp[1].equals("Goodbye")) {
                        System.out.println("ActualServer says: " + choppdUpRsp[1]);
                        System.out.println("Exiting...");
                        System.exit(0);
                        break;
                    }
                    if (choppdUpRsp[1].equals("BCST")){
                        System.out.println("Confirmation: Message Sent! [" + choppdUpRsp[2] + "]");
                    }
                }

                if (rsp.startsWith("BCST")){
                    String[] choppdUpRsp = rsp.split(" ", 3);
                    System.out.println("\nMessage from " + choppdUpRsp[1] + ": " + choppdUpRsp[2]);
                }

                if (rsp.startsWith("PING")){
                    Main.WriteToServer("PONG");
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
