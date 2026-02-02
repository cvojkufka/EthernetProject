package ethernetproject;

import java.net.*;

public class TestSender {
    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket();

        String msg = "A:B:hello";
        byte[] data = msg.getBytes();

        DatagramPacket pkt = new DatagramPacket(
                data,
                data.length,
                InetAddress.getByName("127.0.0.1"),
                6001
        );

        socket.send(pkt);
        socket.close();
    }
}
