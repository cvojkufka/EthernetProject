package ethernetproject;

import java.net.InetAddress;

public class DeviceInfo {
    public final String id;
    public final InetAddress ip;
    public final int port;

    public DeviceInfo(String id, InetAddress ip, int port) {
        this.id = id;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String toString() {
        return id + " " + ip.getHostAddress() + ":" + port;
    }
}
