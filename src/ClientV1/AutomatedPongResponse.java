package ClientV1;

import java.io.*;

/**
 * The type Automated pong response.
 */
public class AutomatedPongResponse extends Thread {

    /**
     * The Reader.
     */
    BufferedReader reader;

    /**
     * Instantiates a new Automated pong response.
     *
     * @param reader the reader
     */
    public AutomatedPongResponse(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        while (true) {
            try {

                if (reader.readLine().equals("PING")) {
                    reader.mark(4);
                    Main.WriteToServer("PONG");
                    //System.out.println("TEST > ponged");
                } else if (reader.readLine().startsWith("DSCN")) {
                    System.out.println("Error: Connection Lost!!");
                    System.exit(-2);
                }

            } catch (IOException e) {
            }
        }

    }
}
