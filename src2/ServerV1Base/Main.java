package ServerV1Base;

    public class Main
    {
        public static void main(String[] args) {
            System.out.println("Usage:");
            System.out.println("\t--no-colors: log debug messages without colors in the console.");
            System.out.println("\t--no-pong: client does not send pong messages.");
            System.out.println("\t--port: the port on which the server should listen (default is 1337).");
            System.out.println("");

            if (args.length == 0) {
                System.out.println("Starting the client with the default configuration.");
            } else {
                System.out.println("Starting the client with:");
            }

            ServerConfiguration config = new ServerConfiguration();
            boolean shouldParsePort = false;
            for (String arg : args) {
                if (shouldParsePort) {
                    if (tryParseInt(arg)) {
                        int port = Integer.parseInt(arg);
                        if (port > 1024 && port < 65535) {
                            config.setServerPort(port);
                            System.out.println(" * Port has been configured");
                        } else {
                            System.out.println(" ERROR: Invalid port number (should be between 1024 and 65535)");
                        }
                    } else {
                        System.out.println(" ERROR: Port is not a valid number.");
                    }
                    shouldParsePort = false;
                }
                else if (arg.equals("--no-colors")) {
                    config.setShowColors(false);
                } else if (arg.equals("--no-pong")) {
                    config.setSendPong(false);
                } else if (arg.equals("--port")) {
                    shouldParsePort = true;
                }
            }


            if (config.isShowColors()) {
                System.out.println(" * Colors in debug message enabled");
            } else {
                System.out.println(" * Colors in debug message disabled");
            }
            if (config.isSendPong()) {
                System.out.println(" * Sending of PONG messages enabled");
            } else {
                System.out.println(" * Sending of PONG messages disabled");
            }

            System.out.println("Starting the server:");
            System.out.println("-------------------------------");
            config.getClass(); System.out.println("\tversion:\t" + "1.3");
            System.out.println("\thost:\t\t127.0.0.1");
            System.out.println("\tport:\t\t" + config.getServerPort());
            System.out.println("-------------------------------");
            (new Server(config)).run();
        }

        static boolean tryParseInt(String value) {
            try {
                Integer.parseInt(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }

