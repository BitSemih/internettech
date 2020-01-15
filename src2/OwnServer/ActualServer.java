package OwnServer;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ActualServer {
    private final int PING_TIMEOUT = 3000;

    private ServerSocket serverSocket;

    private Set<ClientThread> clientThreads;
    private ConfigClass config;

    private GroupManager groupManager;

    public ActualServer(ConfigClass config) {
        this.config = config;
        this.groupManager = new GroupManager();
    }

    /**
     *
     */
    public void run() {
        try {
            this.serverSocket = new ServerSocket(this.config.getServerPort());
            this.clientThreads = new HashSet<>();

            while (true) {
                Socket socket = this.serverSocket.accept();

                ClientThread clientThread = new ClientThread(socket);
                this.clientThreads.add(clientThread);
                (new Thread(clientThread)).start();
                System.out.println("Amount of clients: " + this.clientThreads.size());


                if (this.config.hasHeartBeat()) {
                    HeartBeatThread heartBeatThread = new HeartBeatThread(clientThread);
                    (new Thread(heartBeatThread)).start();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * this is the heartbeat thread.
     * in this thread the server sends a message to a client to test the connection.
     * The message contains the string "PING" the client should reply withing 3 seconds with a "PONG".
     * if the client doesn't. the client is disconnected automatically.
     */
    public class HeartBeatThread extends Thread {
        ActualServer.ClientThread clientThread;

        private boolean shouldPing = true;

        HeartBeatThread(ActualServer.ClientThread ct) {
            this.clientThread = ct;
        }

        public void run() {
            while (this.shouldPing) {

                try {
                    sleep(10 + new Random().nextInt(10) * 1000);
                    this.clientThread.heartBeatRecieved = false;
                    this.clientThread.writeToClient("PING");

                    sleep(3000);
                    if (!this.clientThread.heartBeatRecieved) {
                        this.shouldPing = false;
                        this.clientThread.writeToClient("DSCN Pong timeout");
                        this.clientThread.kill();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * this is the client thread (or user thread).
     * in instance of this thread runs for every user.
     * this server checks if the client has sent a message.
     * if the client has sent a message the server responds accordingly.
     */
    public class ClientThread extends Thread {
        private DataInputStream is;

        private OutputStream outputStream;

        private Socket socket;

        private Status status;
        private String username;
        public boolean heartBeatRecieved = false;

        public ClientThread(Socket socket) {
            this.status = Status.INIT;
            this.socket = socket;
        }

        public String getUsername() {
            return this.username;
        }


        public void run() {
            try {
                this.outputStream = this.socket.getOutputStream();
                this.is = new DataInputStream(this.socket.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(this.is));

                this.status = Status.CONNECTING;
                ActualServer.this.config.getClass();

                String welcomeMessage = "HELO " + "Welkom to WhatsUpp!";

                writeToClient(welcomeMessage);

                while (!this.status.equals(Status.FINISHED)) {

                    String line = reader.readLine();

                    if (line != null) {

                        boolean userExists, isValidUsername, isIncomingMessage = true;

                        logMessage(isIncomingMessage, line);

                        Message message = new Message(line);

                        // this is the 'master' Switch-case statement which determines what should be done in response to the message of the client.

                        switch (message.getMessageType()) {

                            case HELO:
                                isValidUsername = message.getPayload().matches("[a-zA-Z0-9_]{3,14}");
                                if (!isValidUsername) {
                                    this.status = Status.FINISHED;
                                    writeToClient("-ERR username has an invalid format (only characters, numbers and underscores are allowed, between 3 and 14 characters long)");
                                    continue;
                                }
                                userExists = false;
                                for (ClientThread ct : ActualServer.this.clientThreads) {
                                    if (ct != this && message.getPayload().equals(ct.getUsername())) {
                                        userExists = true;
                                        break;
                                    }
                                }
                                if (userExists) {
                                    writeToClient("-ERR user already logged in");
                                    continue;
                                }

                                this.status = Status.CONNECTED;
                                this.username = message.getPayload();
                                writeToClient("+OK " + message.getContent());
                                break;

                            case BCST:
                                for (ClientThread ct : ActualServer.this.clientThreads) {
                                    if (ct != this) {
                                        ct.writeToClient("BCST [" + getUsername() + "] " + message.getPayload());
                                    }
                                }
                                writeToClient("+OK " + message.getContent());
                                break;

                            case QUIT:
                                this.status = Status.FINISHED;
                                writeToClient("+OK Goodbye");
                                break;

                            case LOU:

                                String returnStringLOU;

                                if (ActualServer.this.clientThreads.size() > 0) {
                                    returnStringLOU = "+OK LOU ";
                                    for (ClientThread ct : ActualServer.this.clientThreads) {
                                        returnStringLOU += ct.getUsername() + " ";
                                    }
                                } else {
                                    returnStringLOU = "-ERR no users are online";
                                }

                                writeToClient(returnStringLOU);
                                break;

                            case WSPR:

                                String[] strippedWSPR = message.getPayload().split(" ", 2);
                                boolean userFound = false;

                                for (ClientThread ct : ActualServer.this.clientThreads) {
                                    if (ct.getUsername().equals(strippedWSPR[0])) {
                                        ct.writeToClient("WSPR [" + this.username + "] " + strippedWSPR[1]);
                                        userFound = true;
                                        break;
                                    }
                                }
                                if (userFound) {
                                    writeToClient("+OK WSPR " + message.getPayload());
                                } else {
                                    writeToClient("-ERR adressed user [" + strippedWSPR[0] + "] is not connected and can not recieve your message");
                                }
                                break;

                            case GRP_CRT:

                                String returnStringGRP_CRT = "";

                                if (!GroupManager.isValidGroupName(message.getPayload())) {
                                    returnStringGRP_CRT = "-ERR groupname invallid. (only characters, numbers and underscores are allowed, length between 5 and 15 characters long)";
                                } else {
                                    if (groupManager.doesGroupExist(message.getPayload())) {
                                        returnStringGRP_CRT = "-ERR group with that name already exists";
                                    } else {
                                        groupManager.addNewGroup(message.getPayload(), this.username);
                                        returnStringGRP_CRT = "+OK GRP_CRT " + message.getPayload();
                                    }
                                }

                                writeToClient(returnStringGRP_CRT);
                                break;

                            case GRP_LS:

                                String returnStringGRP_LS;

                                if (groupManager.doGroupsExist()) {

                                    returnStringGRP_LS = "+OK GRP_LS";

                                    for (ChatGroup cg : groupManager.getGroupList()) {

                                        returnStringGRP_LS += " ";

                                        // add group name
                                        returnStringGRP_LS += cg.getName() + " ";

                                        // check if username is a member.
                                        boolean member = false;
                                        //System.out.println(cg.getMembers() + " s: " + this.username);
                                        for (String u : cg.getMembers()) {
                                            //System.out.println("u: '" + u + "' tu: '" + this.username + "'");
                                            if (u.equals(this.username)) {
                                                member = true;
                                                break;
                                            }
                                            //System.out.println("u: '" + u + "' tu: '" + this.username + "'" + " m: " + member);
                                        }

                                        // if member YES, else NO,
                                        if (member) {
                                            returnStringGRP_LS += "YES ";

                                            // if Owner OWNER, else MEMBER
                                            if (this.username.equals(cg.getOwner())) {
                                                returnStringGRP_LS += "OWNER";
                                            } else {
                                                returnStringGRP_LS += "MEMBER";
                                            }

                                        } else {
                                            returnStringGRP_LS += "NO";
                                        }

                                    }
                                } else {
                                    returnStringGRP_LS = "-ERR no groups exist yet";
                                }

                                writeToClient(returnStringGRP_LS);
                                break;

                            case GRP_JOIN:

                                String returnStringGRP_JOIN = "";

                                if (!GroupManager.isValidGroupName(message.getPayload())) {
                                    returnStringGRP_JOIN = "-ERR groupname invallid. (only characters, numbers and underscores are allowed, length between 5 and 15 characters long)";
                                } else {
                                    if (!groupManager.doesGroupExist(message.getPayload())){
                                        returnStringGRP_JOIN = "-ERR a group with that name does not exist";
                                    } else {
                                        if (groupManager.isUserMemberOfGroup(this.username, message.getPayload())){
                                            returnStringGRP_JOIN = "-ERR user is already a member of this group";
                                        } else {
                                            //message to initial client
                                            groupManager.getGroupByName(message.getPayload()).addClient(this.username);
                                            returnStringGRP_JOIN = "+OK GRP_JOIN " + message.getPayload();

                                            //message to all members ALREADY part of the group
                                            String[] members = groupManager.getGroupByName(message.getPayload()).getMembers().toArray(new String[0]);
                                            for (String s : members) {
                                                for (ClientThread ct : ActualServer.this.clientThreads) {
                                                    if (s.equals(ct.getUsername())) {
                                                        ct.writeToClient("GRP_JOIN " + message.getPayload() + " " + this.username);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                writeToClient(returnStringGRP_JOIN);
                                break;

                            case GRP_MSG:

                                String returnStringGRP_MSG = "";

                                String[] strippedGRP_MSG = message.getPayload().split(" ", 2);

                                if (!GroupManager.isValidGroupName(strippedGRP_MSG[0])) {
                                    returnStringGRP_MSG = "-ERR groupname invallid. (only characters, numbers and underscores are allowed, length between 5 and 15 characters long)";
                                } else {
                                    if (!groupManager.doesGroupExist(strippedGRP_MSG[0])){
                                        returnStringGRP_MSG = "-ERR a group with that name does not exist";
                                    } else {
                                        if (!groupManager.isUserMemberOfGroup(this.username, strippedGRP_MSG[0])){
                                            returnStringGRP_MSG = "-ERR user is not a member of this group.";
                                        } else {
                                            String[] members = groupManager.getGroupByName(strippedGRP_MSG[0]).getMembers().toArray(new String[0]);
                                            for (String s : members) {
                                                for (ClientThread ct : ActualServer.this.clientThreads) {
                                                    if (s.equals(ct.getUsername())) {
                                                        ct.writeToClient("GRP_MSG " + strippedGRP_MSG[0] + " " + this.username + " " + strippedGRP_MSG[1]);
                                                    }
                                                }
                                            }
                                            returnStringGRP_MSG = "+OK GRP_MSG " + message.getPayload();
                                        }
                                    }
                                }

                                writeToClient(returnStringGRP_MSG);
                                break;

                            case GRP_LEAV:

                                String returnStringGRP_LEAV = "";

                                if (!GroupManager.isValidGroupName(message.getPayload())) {
                                    returnStringGRP_LEAV = "-ERR groupname invallid. (only characters, numbers and underscores are allowed, length between 5 and 15 characters long)";
                                } else {
                                    if (!groupManager.doesGroupExist(message.getPayload())) {
                                        returnStringGRP_LEAV = "-ERR a group with that name does not exist";
                                    } else {
                                        if (!groupManager.isUserMemberOfGroup(this.username, message.getPayload())){
                                            returnStringGRP_LEAV = "-ERR user is not a member of this group.";
                                        } else {
                                            //message to initial client
                                            groupManager.getGroupByName(message.getPayload()).removeClient(this.username);
                                            returnStringGRP_LEAV = "+OK GRP_LEAV " + message.getPayload();

                                            //message to all members ALREADY part of the group
                                            String[] members = groupManager.getGroupByName(message.getPayload()).getMembers().toArray(new String[0]);
                                            for (String s : members) {
                                                for (ClientThread ct : ActualServer.this.clientThreads) {
                                                    if (s.equals(ct.getUsername())) {
                                                        ct.writeToClient("GRP_LEAV " + message.getPayload() + " " + this.username);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                writeToClient(returnStringGRP_LEAV);
                                break;

                            case GRP_KICK:
                                String[] strippedGRP_KICK = message.getPayload().split(" ", 3);

                                String returnStringGRP_KICK = "";

                                if (!GroupManager.isValidGroupName(strippedGRP_KICK[0])) {
                                    returnStringGRP_KICK = "-ERR groupname invallid. (only characters, numbers and underscores are allowed, length between 5 and 15 characters long)";
                                } else {
                                    if (!groupManager.doesGroupExist(strippedGRP_KICK[0])) {
                                        returnStringGRP_KICK = "-ERR a group with that name does not exist";
                                    } else {
                                        if (!groupManager.isUserMemberOfGroup(strippedGRP_KICK[1], strippedGRP_KICK[0])){
                                            returnStringGRP_KICK = "-ERR user is not a member of this group.";
                                        } else {
                                            if (!groupManager.getGroupByName(strippedGRP_KICK[0]).getOwner().equals(this.username)){
                                                returnStringGRP_KICK = "-ERR you are not the owner of this group, only an owner can kick some one.";
                                            } else {
                                                //message to initial client
                                                groupManager.getGroupByName(message.getPayload()).removeClient(this.username);
                                                returnStringGRP_KICK = "+OK GRP_KICK " + strippedGRP_KICK[0] + " " + strippedGRP_KICK[1] + " " + this.username + " " + strippedGRP_KICK[2];

                                                //message to all members ALREADY part of the group
                                                String[] members = groupManager.getGroupByName(message.getPayload()).getMembers().toArray(new String[0]);
                                                for (String s : members) {
                                                    for (ClientThread ct : ActualServer.this.clientThreads) {
                                                        if (s.equals(ct.getUsername())) {
                                                            ct.writeToClient("GRP_KICK " + strippedGRP_KICK[0] + " " + strippedGRP_KICK[1] + " " + this.username + " " + strippedGRP_KICK[2]);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                writeToClient(returnStringGRP_KICK);
                                break;

                            case PONG:
                                this.heartBeatRecieved = true;
                                break;

                            case UNKOWN:
                                writeToClient("-ERR Unkown command");
                                break;
                        }

                    }
                }


                ActualServer.this.clientThreads.remove(this);
                this.socket.close();
            } catch (IOException e) {
                System.out.println("Exception: " + e.getMessage());
            }
        }

        /**
         * this method disconnects the client from the server and shuts down the thread responsible for the just disconnected user.
         */
        public void kill() {
            try {
                System.out.println("[DROP CONNECTION] " + getUsername());
                ActualServer.this.clientThreads.remove(this);
                this.socket.close();
            } catch (Exception ex) {
                System.out.println("Exception when closing outputstream: " + ex.getMessage());
            }
            this.status = Status.FINISHED;
        }

        /**
         * this method writes a given string to the client this instance of the thread is responsible for.
         * @param message the message sent to the client
         */
        public void writeToClient(String message) {
            PrintWriter writer = new PrintWriter(this.outputStream);
            writer.println(message);
            writer.flush();

            boolean isIncomingMessage = false;
            logMessage(isIncomingMessage, message);
        }

        /**
         * this is a method that logs a message in the command line.
         * @param isIncoming if the message is incomming or outgoing.
         * @param message the message that will be logged.
         */
        private void logMessage(boolean isIncoming, String message) {
            String logMessage;
            String directionString = "<< ";
            if (isIncoming) {
                directionString = ">> ";
            }

            if (getUsername() == null) {
                logMessage = directionString + message;
            } else {
                logMessage = directionString + "[" + getUsername() + "] " + message;
            }

            System.out.println(logMessage);
        }
    }
}

