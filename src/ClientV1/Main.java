package ClientV1;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

/**
 * The type Main.
 */
public class Main {
    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        new Main().run();
    }

    // Reminder:
    // Initiate ActualServer: java -jar chat_server.jar --no-colors --port 1338

    /**
     * The constant SERVER_ADDRESS.
     */
    public static String SERVER_ADDRESS = "127.0.0.1";
    /**
     * The constant SERVER_PORT.
     */
    public static int SERVER_PORT = 1338;

    /**
     * The constant socket.
     */
    public static Socket socket;

    static {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            if (!socket.isConnected()) {
                System.exit(-4);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run.
     */
    public void run() {

        try {
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = reader.readLine();
            reader.mark(line.length());
            reader.reset();

            //print initial server message
            System.out.println(line);

            LoginPrompt(reader);

            new UserInput().start();
            new ServerInput(reader).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Write to server.
     *
     * @param input the input
     */
    public static void WriteToServer(String input) {
        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream);
            writer.println(input);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Pause.
     *
     * @param millis the millis
     */
    public static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Has special chars boolean.
     *
     * @param inputString the input string
     * @return the boolean
     */
    public static boolean hasSpecialChars(String inputString) {
        if (inputString == null || inputString.trim().isEmpty()) {
            return false;
        }
        String specialChars = "'/*!@#$%^&*()\"{}[] |\\?/<>,.";

        return inputString.contains(specialChars);
    }

    /**
     * Enter to conineu.
     *
     * @param showText the show text
     */
    public static void EnterToConineu(boolean showText){
        Scanner sc = new Scanner(System.in);
        if (showText) {
            System.out.print("[ Enter to Continue ]");
        }
        sc.nextLine();
    }

    /**
     * Login prompt.
     *
     * @param reader the reader
     */
    public void LoginPrompt(BufferedReader reader){
        Scanner sc = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("Enter a username.");
                System.out.println(" - It must be unique");
                System.out.println(" - It may only contain letters, numbers and underscores");
                System.out.println(" - It should contain between 3 and 14 characters");
                System.out.print("> Input: ");

                String userInput = sc.nextLine();

                if (!hasSpecialChars(userInput)) {
                    WriteToServer("HELO " + userInput);

                    //Necessary to NOT get ahead of the server's response.
                    pause((100));

                    //reader.mark(9 + userInput.length());

                    //if (reader.ready()) {

                    String rsp = reader.readLine();
                    System.out.println("LINE: " + rsp);
                    reader.mark(rsp.length());
                    reader.reset();

                    if (rsp.equals("+OK HELO " + userInput)) {
                        System.out.println("You have logged in! (" + userInput + ")");
                        System.out.print("");
                        break;
                    } else if (rsp.startsWith("-ERR")) {
                        String[] rspSplit = rsp.split(" ", 2);
                        System.out.println("Error: " + rspSplit[1]);
                        System.out.println("Try again...");
                    }
                    //}

                } else {
                    System.out.println("* Input invalid try again");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
