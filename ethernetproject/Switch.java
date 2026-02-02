package ethernetproject;

import java.net.*;
import java.util.*;

public class Switch {

    String id;
    DeviceInfo me;
    Map<String, DeviceInfo> neighbors = new HashMap<>();
    // learning table: MAC -> neighbor ID
    Map<String, String> table = new HashMap<>();
    DatagramSocket socket;

    public Switch(String id, Config cfg) throws Exception {
        this.id = id;
        this.me = cfg.devices.get(id);

        List<String> neighborIds = cfg.links.get(id);
        if (neighborIds != null) {
            for (String n : neighborIds) {
                neighbors.put(n, cfg.devices.get(n));
            }
        }

        socket = new DatagramSocket(me.port);
        System.out.println("Switch " + id + " up on port " + me.port);
    }

    public void start() throws Exception {
        byte[] buf = new byte[1024];

        while (true) {
            DatagramPacket pkt = new DatagramPacket(buf, buf.length);
            socket.receive(pkt);

            String msg = new String(pkt.getData(), 0, pkt.getLength());
            handleFrame(msg, pkt.getPort());
        }
    }

    void handleFrame(String frame, int incomingPort) throws Exception {
        String[] parts = frame.split(":", 3);
        String src = parts[0];
        String dst = parts[1];
        String payload = parts[2];

        // learn source
        String incomingNeighbor = neighborByPort(incomingPort);
        if (!table.containsKey(src)) {
            table.put(src, incomingNeighbor);
            printTable();
        }

        // forward
        if (table.containsKey(dst)) {
            sendToNeighbor(dst, frame);
        } else {
            flood(frame, incomingNeighbor);
        }
    }

    String neighborByPort(int port) {
        for (DeviceInfo d : neighbors.values()) {
            if (d.port == port) return d.id;
        }
        return null;
    }

    void sendToNeighbor(String dst, String frame) throws Exception {
        String neighbor = table.get(dst);
        DeviceInfo d = neighbors.get(neighbor);
        send(frame, d);
    }

    void flood(String frame, String except) throws Exception {
        for (DeviceInfo d : neighbors.values()) {
            if (d.id.equals(except)) continue;
            send(frame, d);
        }
    }

    void send(String msg, DeviceInfo d) throws Exception {
        byte[] data = msg.getBytes();
        DatagramPacket pkt = new DatagramPacket(
                data, data.length,
                InetAddress.getByName(d.ip), d.port
        );
        socket.send(pkt);
    }

    void printTable() {
        System.out.println("Switch " + id + " table:");
        table.forEach((k,v) -> System.out.println("  " + k + " -> " + v));
    }

    public static void main(String[] args) throws Exception {
        Config cfg = ConfigParser.parse("config.txt");
        new Switch(args[0], cfg).start();
    }
}
