
package ClientV3;

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
     * The constant userName.
     */
    public static String userName;

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
     * this is the main method.
     * in this method the reader is initialised and tries to get an initial message from the server.
     * After that the login prompt is loaded
     * after the login prompt the Threads for User-input and Server-input are loaded.
     */
    public void run() {

        try {
            InputStream inputStream = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line = reader.readLine();
            reader.mark(line.length());
            reader.reset();

            //print initial server message
            System.out.println("[Server]: " + line);

            LoginPrompt(reader);

            new UserInput().start();
            new ServerInput(reader).start();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * this method writes its input to the server using a writer.
     *
     * @param input the text that will be send to the server.
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
     * this method pauses a thread for given amount of milliseconds.
     *
     * @param millis the amount of milliseconds a thread should be paused.
     */
    public static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * this method counts the special characters in a String input.
     * if the amount of special chars is > 0 it returns false.
     * else it returns true
     *
     * @param inputString the text to be tested on special characters.
     * @return boolean
     */
    public static boolean hasSpecialChars(String inputString) {
        if (inputString == null || inputString.trim().isEmpty()) {
            return false;
        }
        String specialChars = "'/*!@#$%^&*()\"{}[] |\\?/<>,.";

        return inputString.contains(specialChars);
    }

    /**
     * when this method is called, at that place in the code, the user would have to press [Enter] to continue.
     *
     * @param showText if the method should show the text: "[ Enter to Continue ]".
     */
    public static void EnterToConineu(boolean showText){
        Scanner sc = new Scanner(System.in);
        if (showText) {
            System.out.print("[ Enter to Continue ]");
        }
        sc.nextLine();
    }

    /**
     * This method is the login prompt.
     * it asks the user to input a username (which needs to be valid) Look at hasSpecialChars() for more info on this.
     * if the input is valid and the server accepts the username. the client will tell the user the login is succesfull.
     * if the server responds with an error (for example "-ERR username already logged in" the client wil show this message
     * and prompt the user to AGAIN fill in a valid username.
     *
     * @param reader reader
     */
    public void LoginPrompt(BufferedReader reader){
        Scanner sc = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("+---------------------------------------------------------+");
                System.out.println("| Enter a username                                        |");
                System.out.println("| - It must be unique                                     |");
                System.out.println("| - It may only contain letters, numbers and underscores  |");
                System.out.println("| - It should contain between 3 and 14 characters         |");
                System.out.println("+---------------------------------------------------------+");
                System.out.print("> Input: ");

                String userInput = sc.nextLine();

                if (!Main.hasSpecialChars(userInput)) {

                    Main.WriteToServer("HELO " + userInput);

                    //Necessary to NOT get ahead of the server's response.
                    Main.pause((100));

                    //if (reader.ready()) {

                        String rsp = reader.readLine();
                        reader.mark(rsp.length());
                        reader.reset();

                        if (rsp.equals("+OK HELO " + userInput)) {
                            System.out.println("> You have logged in! (" + userInput + ")");
                            System.out.print("");
                            userName = userInput;

                            break;
                        } else if (rsp.startsWith("-ERR")) {
                            String[] rspSplit = rsp.split(" ", 2);
                            System.out.println("Error: " + rspSplit[1]);
                            System.out.println("Try again...");
                        }
                    //}

                } else {
                    System.out.println("* Input invalid try again '" + userInput +"'");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
