package ClientV2;

import java.util.Scanner;

public class UserInput extends Thread {

    public UserInput() {
    }

    /**
     * this is the run-method of the user input thread.
     * in this tread the client prompts the user to input something and respond accordingly
     * for example with sending a message to the server, or quiting the client.
     */
    @Override
    public void run() {
        while (true) {

            printOptionMenu();

            System.out.print("> Input: ");

            Scanner sc = new Scanner(System.in);

            String userInput = sc.nextLine();

            while (!userInput.matches("[0-9]+")) {
                System.out.println("* Invalid number. Try again...");
                System.out.print("> Input: ");
                userInput = sc.nextLine();
            }

            boolean nrvalid = true;

            int input = Integer.parseInt(userInput);

            switch (input) {
                case 0:
                    Main.WriteToServer("QUIT");
                    break;

                case 1:
                    String bsct1 = promptForAnInput("a Message", 2, 50, true, false);
                    if (!bsct1.isEmpty()) {
                        Main.WriteToServer("BCST " + bsct1);
                    }
                    break;

                default:
                    System.out.println("Number not valid ...");
                    nrvalid = false;

                    //TODO add level two protocol to this.
            }

            if (nrvalid) {
                //ClientV2.Main.pause(1000);
                Main.EnterToConineu(false);
            }
        }
    }

    /**
     * this is a method to easily prompt the user for an input.
     *
     * @param what              a string with what this method needs to ask for.
     * @param minLength         the minimal length of the input.
     * @param maxLength         the maximum length of the input.
     * @param showMinMax        if the method should show the boundaries for the length
     * @param specialCharsCheck if the method should check the input given by the user on only containing letter, numbers, and underscores.
     * @return the input given by the user.
     */
    public String promptForAnInput(String what, int minLength, int maxLength, boolean showMinMax, boolean specialCharsCheck) {

        if (showMinMax) {
            System.out.println("> Enter " + what + " ( " + minLength + " - " + maxLength + " ) ");
        } else {
            System.out.println("> Enter " + what);
        }

        Scanner sc = new Scanner(System.in);

        String returnvalue = "";

        boolean inputGood = false;
        while (!inputGood) {
            System.out.print("> Input: ");
            String input = sc.nextLine();

            if (input.equals("q")){
                return returnvalue;
            }

            if (specialCharsCheck) {
                if (input.matches("[a-zA-Z0-9_]*") && input.length() >= minLength && input.length() <= maxLength) {
                    returnvalue = input;
                    inputGood = true;
                } else {
                    System.out.println("* Enter 'q' to go back to the main menu.");
                    System.out.println("* Input not valid. Try again: ");
                }
            } else {
                if (input.length() >= minLength && input.length() <= maxLength) {
                    returnvalue = input;
                    inputGood = true;
                } else {
                    System.out.println("* Enter 'q' to go back to the main menu.");
                    System.out.println("* Input not valid. Try again: ");
                }
            }

        }

        return returnvalue;
    }

    /**
     * a method the prints the option menu.
     */
    private void printOptionMenu() {
        System.out.println("+-------------------< Available Options >--------------------+");
        System.out.println("| 1 - Send a message to all online users                     |");
        System.out.println("| 0 - Logout                                                 |");
        System.out.println("+------------------------------------------------------------+");
        System.out.println("> Enter a corresponding number");
    }
}