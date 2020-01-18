package ClientV2;

import java.io.BufferedReader;
import java.io.IOException;

public class ServerInput extends Thread {

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
                    if (choppdUpRsp[1].equals("BCST")) {
                        System.out.println("Confirmation: Message Sent! [" + choppdUpRsp[2] + "]");
                    }

                    //Create group response to client
                    if (choppdUpRsp[1].equals("GRP_CRT")) {
                        System.out.println("Confirmation: Group created! [" + choppdUpRsp[2] + "]");
                    }

                    //Join confirmation to client
                    if (choppdUpRsp[1].equals("GRP_JOIN")){
                        System.out.println("Confirmation: Group joined! [" + choppdUpRsp[2] + "]");
                    }

                    //Send confirmation to client
                    if (choppdUpRsp[1].equals("GRP_MSG")){
                        System.out.println("Confirmation: Message send to group! [" + choppdUpRsp[2].split(" ", 2)[0] + "]");
                    }

                    //Send group leave confirmation to client
                    if (choppdUpRsp[1].equals("GRP_LEAV")){
                        System.out.println("Confirmation: Group left! [" + choppdUpRsp[2] + "]");
                    }

                    //Send group kick confirmation to client
                    if (choppdUpRsp[1].equals("GRP_KICK")){
                        System.out.println("Confirmation: User has been kicked from the group! [" + choppdUpRsp[2] + "]");
                    }

                    //Send group list to client
                    if (choppdUpRsp[1].equals("GRP_LS")) {
                        choppdUpRsp[2] = " " + choppdUpRsp[2];
                        System.out.println("+-------------------< List of all groups >-------------------+");
                        System.out.println("| GROUPNAME        Member?[YES/NO]     ROLE                  |");
                        for (String group : choppdUpRsp[2].split("/")) {
                            System.out.print("|");

                            //Split the group string into different properties to calculate the bufferspace needed between each property
                            String[] groupProperties = group.split(" ");

                            //Groupname
                            System.out.print(" " + groupProperties[1] + calcBufferSpace(null, 17 - groupProperties[1].length()));

                            // YES/NO attribute
                            System.out.print(groupProperties[2] + calcBufferSpace(null, 20 - groupProperties[2].length()));

                            StringBuilder bufferSpace;
                            //Check if the client is a member of the group by looking at the length of the array
                            if (groupProperties.length == 3){
                                //Is not a member of the group
                                bufferSpace = calcBufferSpace(null, 22);
                            } else {
                                //Has the owner/member property so bufferSpace is less
                                bufferSpace = calcBufferSpace(null, 22 - groupProperties[3].length());
                                System.out.print(groupProperties[3]);
                            }
                            System.out.print(bufferSpace + "|");
                            System.out.println();
                        }
                        System.out.println("+------------------------------------------------------------+");
                    }

                    //Whisper confirmation to client
                    if (choppdUpRsp[1].equals("WSPR")) {
                        System.out.println("Confirmation: Whisper send! [" + choppdUpRsp[2] + "]");
                    }

                    //Send online user list to client
                    if (choppdUpRsp[1].equals("LOU")) {
                        System.out.println("+-----------------< List of online users >-------------------+");
                        for (String username : choppdUpRsp[2].split(" ")) {
                            StringBuilder bufferSpace = calcBufferSpace(username, 59);
                            System.out.println("| " + username + bufferSpace + "|");
                        }
                        System.out.println("+------------------------------------------------------------+");
                    }
                }

                //Send group message send confirmation to client
                if (rsp.startsWith("GRP_MSG")){
                    String[] choppdUpRsp = rsp.split(" ", 4);
                    System.out.println("[" + choppdUpRsp[1] + "] " + choppdUpRsp[2] + ": " + choppdUpRsp[3]);
                }

                //Send group join confirmation to client
                if (rsp.startsWith("GRP_JOIN")){
                    String[] choppdUpRsp = rsp.split(" ", 3);
                    System.out.println("[" + choppdUpRsp[1] + "]: " + choppdUpRsp[2] + " has joined the group" );
                }

                //Send group leave confirmation to client
                if (rsp.startsWith("GRP_LEAV")){
                    String[] choppdUpRsp = rsp.split(" ", 3);
                    System.out.println("[" + choppdUpRsp[1] + "]: " + choppdUpRsp[2] + " has left the group" );
                }

                //Send group kick confirmation to client
                if (rsp.startsWith("GRP_KICK")){
                    String[] choppdUpRsp = rsp.split(" ", 3);
                    System.out.println("[" + choppdUpRsp[1] + "]: " + choppdUpRsp[2] + " has been kicked from the group" );
                }

                //Show whisper to client
                if (rsp.startsWith("WSPR")) {
                    String[] choppdUpRsp = rsp.split(" ", 3);
                    System.out.println("\n[Server]: Message from " + choppdUpRsp[1] + ": " + choppdUpRsp[2]);
                }

                //Global message to all clients
                if (rsp.startsWith("BCST")) {
                    String[] choppdUpRsp = rsp.split(" ", 3);
                    System.out.println("\n[Server]: Message from " + choppdUpRsp[1] + ": " + choppdUpRsp[2]);
                }

                //Automated pong response
                if (rsp.startsWith("PING")) {
                    Main.WriteToServer("PONG");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringBuilder calcBufferSpace(String string, int length) {
        StringBuilder bufferSpace = new StringBuilder();
        if (string == null){
            for (int i = 0; i < length; i++) {
                bufferSpace.append(" ");
            }
        } else {
            for (int i = 0; i < (length - string.length()); i++) {
                bufferSpace.append(" ");
            }
        }

        return bufferSpace;
    }
}
