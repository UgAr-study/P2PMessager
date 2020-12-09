package com.company;

import com.example.p2pandroid.SQLDataBase;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import android.os.Handler;

public class MultiCastReceiver extends Thread {

    protected MulticastSocket socket;
    protected final int port = 1234;
    protected String ip;
    protected SQLDataBase sqlTable;
    protected String myName;
    protected String myPublicKey;
    InetAddress group;
    private Handler mHandler;

    public MultiCastReceiver(SQLDataBase tableInput, Handler handler) {
        sqlTable    = tableInput;
        //myName      = sqlTable.getNameById(String.valueOf(1)).get(0);
        //myPublicKey = sqlTable.getPublicKeyById(String.valueOf(1)).get(0);
        //ip          = sqlTable.getIpAddressById(String.valueOf(1)).get(0);
        myName = "Artem";
        myPublicKey = sqlTable.getPublicKeyByName(myName).get(0);
        ip = sqlTable.getIpAddressByName(myName).get(0);
        mHandler    = handler;
    }

    public void run() {

        try {
            socket = new MulticastSocket(port);
            group = InetAddress.getByName(ip);
            socket.joinGroup(group);
        } catch (IOException e){
            mHandler.sendEmptyMessage(1);
            return;
        }

        byte[] buf = new byte[256];

        while(true) {

            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            try {
                socket.receive(packet);
            } catch (IOException e) {
                mHandler.sendEmptyMessage(2);
                continue;
            }

            String receivedMsg = new String(packet.getData(), 0, packet.getLength());

            String[] subString = receivedMsg.split("\n");

            if (subString.length < 3) {
                mHandler.sendEmptyMessage(2);
                continue;
            }

            String toPublicKey = subString[0];
            String fromName = subString[1];
            String fromPublicKey = subString[2];

            // Add authorized user
            if (toPublicKey.equals("all")) {

                if (fromPublicKey.equals(myPublicKey)) {
                    continue;
                }

                if (sqlTable.getNameByPublicKey(fromPublicKey).isEmpty()) {
                    //String ip = packet.getAddress().toString().substring(1);
                    sqlTable.WriteDB(fromName, packet.getAddress().toString().substring(1), fromPublicKey);
                    mHandler.sendEmptyMessage(0);
                }

                new MultiCastSender(fromPublicKey, myName, myPublicKey).start();

            } else if (toPublicKey.equals(myPublicKey) && sqlTable.getNameByPublicKey(fromPublicKey).isEmpty()) {
                sqlTable.WriteDB(fromName, packet.getAddress().toString().substring(1), fromPublicKey);
                mHandler.sendEmptyMessage(0);
            }
        }
    }

    public void close() {
        socket.close();
    }
}
