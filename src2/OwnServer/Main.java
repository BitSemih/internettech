package OwnServer;

public class Main
{
    public static void main(String[] args) {

        ConfigClass config = new ConfigClass();
//        System.out.println("How To Use:");
//        System.out.println("\t--nobeat: client does not send pong messages.");
//        System.out.println("\t--changeport: the port on which the server should listen (default is " + config.standardPort + ").");
//        System.out.println("");
//
//        if (args.length == 0) {
//            System.out.println("Activating Server Default!");
//        } else {
//            System.out.println("Activating Server!");
//        }
//
//        boolean changePortQuestionMark = false;
//        for (String arg : args) {
//            if (changePortQuestionMark) {
//                if (isStringParsableAsInt(arg)) {
//                    int port = Integer.parseInt(arg);
//                    if (port > 1024 && port < 65535) {
//                        config.setServerPort(port);
//                        System.out.println("INFO: Port has been configured");
//                    } else {
//                        System.out.println("ERROR: Invalid port number (should be between 1024 and 65535)");
//                    }
//                } else {
//                    System.out.println("ERROR: Port is not a valid number.");
//                }
//                changePortQuestionMark = false;
//            } else if (arg.equals("--nobeat")) {
//                config.setSendHeartBeat(false);
//            } else if (arg.equals("--changeport")) {
//                changePortQuestionMark = true;
//            }
//        }
//
//        if (config.hasHeartBeat()) {
//            System.out.println("INFO: Sending Heart beats ");
//        } else {
//            System.out.println("INFO: NOT Sending Heart beats");
//        }
//
//        System.out.println("+-----------------------+");
//        System.out.println("| version:  " + config.version + "\t\t|");
//        System.out.println("| host:     127.0.0.1\t|");
//        System.out.println("| port:     " + config.getServerPort() + "\t\t|");
//        System.out.println("+-----------------------+");
//
        //heartbeat
        config.setSendHeartBeat(true);
        //config.setServerPort(1338);

        System.out.println("Server Running ...");

        (new ActualServer(config)).run();

    }

    static boolean isStringParsableAsInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
