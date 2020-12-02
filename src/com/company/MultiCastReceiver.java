package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.sql.SQLException;

class MulticastReceiver extends Thread{
    protected MulticastSocket socket = null;
    protected int port = 4446;
    protected String ip = "230.0.0.1";
    protected SQLTable sqlTable;
    protected String myName;
    protected String myPublicKey;

    public MulticastReceiver(SQLTable tableInput) {
        sqlTable = tableInput;
        myName = sqlTable.getNameById(1);
        myPublicKey = sqlTable.getPublicKeyById(1);
    }

    public void run() {
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
            String toPublicKey = subString[0];
            String fromName = subString[1];
            String fromPublicKey = subString[2];
            // Add authorized user
            if (toPublicKey.equals("all")) {
                if (sqlTable.getNameByPublicKey(fromPublicKey).isEmpty()) {
                    sqlTable.WriteDB(fromName, packet.getAddress().toString(), fromPublicKey);
                }
                new MultiCastSender(myPublicKey, myName, fromPublicKey).start();
                continue;
            }
            if (toPublicKey.equals(myPublicKey) && sqlTable.getNameByPublicKey(fromPublicKey).isEmpty()) {
                sqlTable.WriteDB(fromName, packet.getAddress().toString(), fromPublicKey);
            }
        }
    }

    public void close() {
        socket.close();
    }
}
