package ClientV2;

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

                //Possible put this in a switch case.

                if (rsp.startsWith("-ERR")) {
                    String[] msg = rsp.split(" ", 2);
                    System.out.println("[Server]: " + msg[1]);
                }

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

                    if (choppdUpRsp[1].equals("LOU")){
                        System.out.println("+-----------------< List of online users >-------------------+");
                        for (String username : choppdUpRsp[2].split(" ")){
                            StringBuilder bufferspace = new StringBuilder();
                            for (int i = 0; i < (59 - username.length()); i++){
                                bufferspace.append(" ");
                            }
                            System.out.println("| " + username + bufferspace + "|");
                        }
                        System.out.println("+------------------------------------------------------------+");
                    }
                }

                //Show whisper to client
                if (rsp.startsWith("WSPR")){
                    String[] choppdUpRsp = rsp.split(" ", 3);
                    System.out.println("\n[Server]: Message from " + choppdUpRsp[1] + ": " + choppdUpRsp[2]);
                }

                //Global message to all clients
                if (rsp.startsWith("BCST")){
                    String[] choppdUpRsp = rsp.split(" ", 3);
                    System.out.println("\n[Server]: Message from " + choppdUpRsp[1] + ": " + choppdUpRsp[2]);
                }

                //Automated pong response
                if (rsp.startsWith("PING")){
                    Main.WriteToServer("PONG");
                }

                //TODO add protocol things for level 2.
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
