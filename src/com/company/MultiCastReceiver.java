package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.sql.SQLException;

class MulticastReceiver extends Thread{
    protected MulticastSocket socket = null;
    protected int port = 1234;
    protected String ip = "229.1.2.3";
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

        System.out.println("Join group");

        byte[] buf = new byte[256];

        while(true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String receivdMsg = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Receive msg:\n" + receivdMsg);
            String[] subString = receivdMsg.split("\n");
            String toPublicKey = subString[0];
            String fromName = subString[1];
            String fromPublicKey = subString[2];
            //System.out.println("packet address from Ignat" + packet.getAddress().toString());
            // Add authorized user
            if (toPublicKey.equals("all")) {
                if (fromPublicKey.equals(myPublicKey)) {
                    continue;
                }
                System.out.println("Receive 'all'");
                if (sqlTable.getNameByPublicKey(fromPublicKey).isEmpty()) {
                    //String ip = packet.getAddress().toString().substring(1);
                    sqlTable.WriteDB(fromName, packet.getAddress().toString().substring(1), fromPublicKey);
                }
                new MultiCastSender(fromPublicKey, myName, myPublicKey).start();
                continue;
            }
            if (toPublicKey.equals(myPublicKey) && sqlTable.getNameByPublicKey(fromPublicKey).isEmpty()) {
                System.out.println("Receive 'personal msg'");
                sqlTable.WriteDB(fromName, packet.getAddress().toString().substring(1), fromPublicKey);
            }
        }
    }

    public void close() {
        socket.close();
    }
}
