package OwnServer;

public class Message {

    private String content;

    //TODO (ONLY WHEN TIME IS AVAILABLE) make an owner transfer protocol. msg.

    /**
     * MessageTypes:
     * HELO: Initial Login
     * BCST: Broadcast to all online users
     * QUIT: Disconnect, go offline, quit
     * PONG: heartbeat, connection test
     * UNKOWN: Default, unknown
     * LOU: List Of Users (Online)
     * WSPR: Wisper, Private message
     * <p>
     * GRP_CRT: Create group
     * GRP_LS: List of Existing Groups
     * GRP_JOIN: Join group
     * GRP_MSG: Message group
     * GRP_LEAV: Leave group
     * GRP_KICK: Kick group member
     */
    public enum MessageType {
        HELO,
        BCST,
        QUIT,
        PONG,
        UNKOWN,
        LOU,
        WSPR,

        GRP_CRT,
        GRP_LS,
        GRP_JOIN,
        GRP_MSG,
        GRP_LEAV,
        GRP_KICK,

        FILE_SEND
    }

    /**
     * the message model class. this class is the standard message class
     * @param content the string contained by the message object.
     */

    public Message(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    /**
     * get the message type of a message.
     * takes appart the message and returns the first part as message-type
     * @return messagetype of the message
     */
    public MessageType getMessageType() {
        MessageType result = MessageType.UNKOWN;
        try {
            if (this.content != null && this.content.length() > 0) {
                String[] splits = this.content.split("\\s+");
                result = MessageType.valueOf(splits[0]);

            }
        } catch (IllegalArgumentException iaex) {
            System.out.println("[ERROR] Unknown command");
        }
        return result;
    }

    /**
     * get the content of the message WITHOUT the fist part (the message type)
     * @return the message content WITHOUT the 'message type'-part
     */
    public String getPayload() {
        if (getMessageType().equals(MessageType.UNKOWN)) {
            return this.content;
        }

        if (this.content == null || this.content.length() < getMessageType().name().length() + 1) {
            return "";
        }

        return this.content.substring(getMessageType().name().length() + 1);
    }
}