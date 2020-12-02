package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.sql.SQLException;




public class MulticastReceiver extends Thread{
    protected MulticastSocket socket = null;
    protected int port;
    protected String ip;

    public void run() {
        port = 4446;
        ip = "230.0.0.0";

        try {
            socket = new MulticastSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InetAddress group = null;
        try {
            group = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            socket.joinGroup(group);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] buf = new byte[256];

        while(true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String receivdMsg = new String(packet.getData(), 0, packet.getLength());
            String[] subString = receivdMsg.split(" ");
            try {
                String publicKey = SQLTable.getPublicKeyByName(subString[0]);
                if (!publicKey.equals(subString[1])) {
                    continue;
                }
                SQLTable.WriteDB(subString[0], packet.getAddress().toString(), subString[1]);
            } catch (SQLException | ClassNotFoundException throwables) {
                throwables.printStackTrace();
            }
        }
    }
}
