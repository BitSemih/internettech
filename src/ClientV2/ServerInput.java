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

                    // Global message confirmation
                    if (choppdUpRsp[1].equals("BCST")){
                        System.out.println("Confirmation: Message Sent! [" + choppdUpRsp[2] + "]");
                    }

                    //Create group response to client
                    if (choppdUpRsp[1].equals("GRP_CRT")){
                        System.out.println("Confirmation: Group created! [" + choppdUpRsp[2] + "]");
                    }

                    //Send group list to client
                    if (choppdUpRsp[1].equals("GRP_LS")){
                        System.out.println("+-------------------< List of all groups >-------------------+");
                        System.out.println("| GROUPNAME        Member?[YES/NO]     Owner?[YES/NO]        |");
                        for (String group : choppdUpRsp[2].split("/")){
                            System.out.print("| ");
                            for (String groupProperty : group.split(" ")){
                                System.out.print(groupProperty + " ");
                            }
                            StringBuilder bufferspace = calcBufferSpace(group);
                            System.out.print(bufferspace + "|");
                        }
                        System.out.println("Confirmation: Group created! [" + choppdUpRsp[2] + "]");
                    }

                    //Whisper confirmation to client
                    if (choppdUpRsp[1].equals("WSPR")){
                        System.out.println("Confirmation: Whisper send! [" + choppdUpRsp[2] + "]");
                    }

                    //Send online user list to client
                    if (choppdUpRsp[1].equals("LOU")){
                        System.out.println("+-----------------< List of online users >-------------------+");
                        for (String username : choppdUpRsp[2].split(" ")){
                            StringBuilder bufferspace = calcBufferSpace(username);
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

    public StringBuilder calcBufferSpace(String string){
        StringBuilder bufferspace = new StringBuilder();
        for (int i = 0; i < (59 - string.length()); i++){
            bufferspace.append(" ");
        }
        return bufferspace;
    }
}
