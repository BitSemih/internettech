package OwnServer;

/**
 * this class holds some settings for the server.
 * ea. the port, and if the server should 'heartbeat' a client.
 */
public class ConfigClass {
    public final int standardPort = 1338;

    private int serverPort;

    private boolean heartBeat = false;

    public ConfigClass() {
        this.serverPort = standardPort;
    }

    public int getServerPort() {
        return this.serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public boolean hasHeartBeat() {
        return this.heartBeat;
    }

    public void setSendHeartBeat(boolean sendHeartBeat) {
        this.heartBeat = sendHeartBeat;
    }

}
