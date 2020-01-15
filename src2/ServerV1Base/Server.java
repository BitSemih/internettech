package ServerV1Base;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class Server {
    private final int PING_TIMEOUT = 3000;
    private ServerSocket serverSocket;
    private Set<ClientThread> threads;
    private ServerConfiguration conf;

    public Server(ServerConfiguration conf) { this.conf = conf; }

    public void run() {
        try {
            this.serverSocket = new ServerSocket(this.conf.getServerPort());
            this.threads = new HashSet<>();


            while (true) {

                Socket socket = this.serverSocket.accept();

                ClientThread ct = new ClientThread(socket);
                this.threads.add(ct);
                (new Thread(ct)).start();
                System.out.println("Num clients: " + this.threads.size());


                if (this.conf.isSendPong()) {
                    PingClientThread dct = new PingClientThread(ct);
                    (new Thread(dct)).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    private class PingClientThread
            implements Runnable
    {
        Server.ClientThread ct;

        private boolean shouldPing = true;

        PingClientThread(Server.ClientThread ct) { this.ct = ct; }

        public void run() {
            while (this.shouldPing) {

                try {
                    int sleep = (10 + (new Random()).nextInt(10)) * 1000;
                    Thread.sleep(sleep);
                    this.ct.pongReceived = false;
                    this.ct.writeToClient("PING");


                    Thread.sleep(3000L);
                    if (!this.ct.pongReceived) {
                        this.shouldPing = false;
                        this.ct.writeToClient("DSCN Pong timeout");
                        this.ct.kill();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ClientThread implements Runnable
    {
        private DataInputStream is;

        private OutputStream os;

        private Socket socket;

        private ServerState state;
        private String username;
        private boolean pongReceived = false;

        public ClientThread(Socket socket) {
            this.state = ServerState.INIT;
            this.socket = socket;
        }

        public String getUsername() { return this.username; }


        public void run() {
            try {
                this.os = this.socket.getOutputStream();
                this.is = new DataInputStream(this.socket.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(this.is));


                this.state = ServerState.CONNECTING;
                Server.this.conf.getClass(); String welcomeMessage = "HELO " + "Welkom to WhatsUpp!";
                writeToClient(welcomeMessage);

                while (!this.state.equals(ServerState.FINISHED)) {

                    String line = reader.readLine();

                    if (line != null) {

                        boolean userExists, isValidUsername, isIncomingMessage = true;
                        logMessage(isIncomingMessage, line);

                        Message message = new Message(line);

                        switch (message.getMessageType()) {

                            case HELO:
                                isValidUsername = message.getPayload().matches("[a-zA-Z0-9_]{3,14}");
                                if (!isValidUsername) {
                                    this.state = ServerState.FINISHED;
                                    writeToClient("-ERR username has an invalid format (only characters, numbers and underscores are allowed)");
                                    continue;
                                }
                                userExists = false;
                                for (ClientThread ct : Server.this.threads) {
                                    if (ct != this && message.getPayload().equals(ct.getUsername())) {
                                        userExists = true;
                                        break;
                                    }
                                }
                                if (userExists) {
                                    writeToClient("-ERR user already logged in"); continue;
                                }
                                this.state = ServerState.CONNECTED;
                                this.username = message.getPayload();
                                writeToClient("+OK " + message.getLine());

                            case BCST:
                                for (ClientThread ct : Server.this.threads) {
                                    if (ct != this) {
                                        ct.writeToClient("BCST [" + getUsername() + "] " + message.getPayload());
                                    }
                                }
                                writeToClient("+OK " + message.getLine());

                            case QUIT:
                                this.state = ServerState.FINISHED;
                                writeToClient("+OK Goodbye");

                            case PONG:
                                this.pongReceived = true;

                            case UNKOWN:
                                writeToClient("-ERR Unkown command");
                        }

                    }
                }
                Server.this.threads.remove(this);
                this.socket.close();
            } catch (IOException e) {
                System.out.println("ActualServer Exception: " + e.getMessage());
            }
        }


        public void kill() {
            try {
                System.out.println("[DROP CONNECTION] " + getUsername());
                Server.this.threads.remove(this);
                this.socket.close();
            } catch (Exception ex) {
                System.out.println("Exception when closing outputstream: " + ex.getMessage());
            }
            this.state = ServerState.FINISHED;
        }

        private void writeToClient(String message) {
            PrintWriter writer = new PrintWriter(this.os);
            writer.println(message);
            writer.flush();


            boolean isIncomingMessage = false;
            logMessage(isIncomingMessage, message);
        }

        private void logMessage(boolean isIncoming, String message) {
            String logMessage;
            Server.this.conf.getClass(); String colorCode = "\033[32m";
            String directionString = "<< ";
            if (isIncoming) {
                Server.this.conf.getClass(); colorCode = "\033[31m";
                directionString = ">> ";
            }



            if (getUsername() == null) {
                logMessage = directionString + message;
            } else {
                logMessage = directionString + "[" + getUsername() + "] " + message;
            }


            if (Server.this.conf.isShowColors()) {
                Server.this.conf.getClass(); System.out.println(colorCode + logMessage + "\033[0m");
            } else {
                System.out.println(logMessage);
            }
        }
    }
}

