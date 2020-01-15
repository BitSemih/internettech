package ServerV1Base;

public class Message {
    private String line;

    public enum MessageType {
        HELO,
        BCST,
        QUIT,
        PONG,
        UNKOWN;
    }

    public Message(String line) { this.line = line; }

    public String getLine() { return this.line; }

    public MessageType getMessageType() {
        MessageType result = MessageType.UNKOWN;
        try {
            if (this.line != null && this.line.length() > 0) {
                String[] splits = this.line.split("\\s+");
                result = MessageType.valueOf(splits[0]);
            }
        } catch (IllegalArgumentException iaex) {
            System.out.println("[ERROR] Unknown command");
        }
        return result;
    }


    public String getPayload() {
        if (getMessageType().equals(MessageType.UNKOWN)) {
            return this.line;
        }

        if (this.line == null || this.line.length() < getMessageType().name().length() + 1) {
            return "";
        }

        return this.line.substring(getMessageType().name().length() + 1);
    }
}

