package com.company;

import com.example.p2pandroid.SQLDataBase;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.sql.SQLException;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MultiCastReceiver extends Thread {

    protected MulticastSocket socket;
    protected final int port = 1234;
    protected String ip = "229.1.2.3";
    protected SQLDataBase sqlTable;
    protected String myName;
    protected String myPublicKey;
    InetAddress group;
    private Handler mHandler;

    private final int ERROR = 1;
    private final int SUCCESS = 0;
    private final String KEY_DATA = "Data";
    private final String KEY_ERROR = "ErrorMsg";

    public MultiCastReceiver(SQLDataBase tableInput, Handler handler) {
        sqlTable    = tableInput;
        myName      = sqlTable.getNameById(String.valueOf(1)).get(0);
        myPublicKey = sqlTable.getPublicKeyById(String.valueOf(1)).get(0);
        //ip          = sqlTable.getIpAddressById(String.valueOf(1)).get(0);
        //myName = "Artem";
        //myPublicKey = sqlTable.getPublicKeyByName(myName).get(0);
        //ip = sqlTable.getIpAddressByName(myName).get(0);
        mHandler    = handler;
    }

    public void run() {

        try {
            socket = new MulticastSocket(port);
            group = InetAddress.getByName(ip);
            socket.joinGroup(group);
        } catch (IOException e){
            Message msg = mHandler.obtainMessage();

            Bundle bundle = new Bundle();
            bundle.putString(KEY_ERROR, e.getMessage());

            msg.setData(bundle);
            msg.what = ERROR;
            mHandler.sendMessage(msg);
            return;
        }

        byte[] buf = new byte[256];

        while(true) {

            DatagramPacket packet = new DatagramPacket(buf, buf.length);

            try {
                socket.receive(packet);
            } catch (IOException e) {
                Message msg = mHandler.obtainMessage();

                Bundle bundle = new Bundle();
                bundle.putString(KEY_ERROR, e.getMessage());

                msg.setData(bundle);
                msg.what = ERROR;
                mHandler.sendMessage(msg);
                continue;
            }

            String receivedMsg = new String(packet.getData(), 0, packet.getLength());

            String[] subString = receivedMsg.split("\n");

            if (subString.length < 3) {
                Message msg = mHandler.obtainMessage();

                Bundle bundle = new Bundle();
                bundle.putString(KEY_ERROR, "Wrong message structure");

                msg.setData(bundle);
                msg.what = ERROR;
                mHandler.sendMessage(msg);
                continue;
            } else {
                Message msg = mHandler.obtainMessage();

                Bundle bundle = new Bundle();
                bundle.putString(KEY_DATA, receivedMsg);

                msg.setData(bundle);
                msg.what = SUCCESS;
                mHandler.sendMessage(msg);
            }

            String toPublicKey = subString[0];
            String fromName = subString[1];
            String fromPublicKey = subString[2];

            String senderIpAddress = packet.getAddress().toString().substring(1);

            // Add authorized user
            if (toPublicKey.equals("all")) {

                if (fromPublicKey.equals(myPublicKey)) {

                    int nrows = sqlTable.updateIpByPublicKey(senderIpAddress, myPublicKey);

                    Message msg = mHandler.obtainMessage();

                    Bundle bundle = new Bundle();
                    bundle.putString(KEY_DATA, String.valueOf(nrows) + " are updated!");

                    msg.setData(bundle);
                    msg.what = SUCCESS;

                    mHandler.sendMessage(msg);

                    continue;
                }

                if (sqlTable.getNameByPublicKey(fromPublicKey).isEmpty()) {
                    //String ip = packet.getAddress().toString().substring(1);
                    sqlTable.WriteDB(fromName, senderIpAddress, fromPublicKey);
                    mHandler.sendEmptyMessage(SUCCESS);
                }

                new MultiCastSender(fromPublicKey, myName, myPublicKey).start();

            } else if (toPublicKey.equals(myPublicKey) && sqlTable.getNameByPublicKey(fromPublicKey).isEmpty()) {
                sqlTable.WriteDB(fromName, senderIpAddress, fromPublicKey);
                mHandler.sendEmptyMessage(SUCCESS);
            }
        }
    }

    public void close() {
        socket.close();
    }
}
