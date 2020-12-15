package com.company;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.example.p2pandroid.SQLDataBase;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

import javax.crypto.SealedObject;

public class TCPReceiver extends Thread {

    private Socket socket;
    private ServerSocket serverSocket;
    private final int port = 4000;
    private Handler mHandler;
    private SQLDataBase UserTable;
    private String userPassword;

    private final int ERROR = 1;
    private final int SUCCESS = 0;
    private final String KEY_DATA = "Data";
    private final String KEY_NAME = "Name";
    private final String KEY_ERROR = "ErrorMsg";

    public TCPReceiver(Handler handler, SQLDataBase db, String pwd) {
        mHandler = handler;
        UserTable = db;
        userPassword = pwd;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {

                socket = serverSocket.accept();

                String text;

                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                SealedObject sobj = (SealedObject) in.readObject();
                String ipAddress = socket.getInetAddress().getHostAddress();

                ArrayList<String> aesKeys = UserTable.getAESKeyByIpAddress(ipAddress);

                if (aesKeys.isEmpty()) {
                    AsymCryptography S = new AsymCryptography();
                    S.loadPrivateKey(userPassword);
                    String symKey = S.decryptMsg(sobj);

                    UserTable.updateAESKeyByIpAddress(symKey, ipAddress);

                    sobj = (SealedObject) in.readObject();
                    text = SymCryptography.decryptMsg(sobj, SymCryptography.getSecretKeyByString(symKey));

                } else {

                    String symKey = aesKeys.get(0);
                    text = SymCryptography.decryptMsg(sobj, SymCryptography.getSecretKeyByString(symKey));
                }



                Message msg = mHandler.obtainMessage();

                Bundle bundle = new Bundle();
                bundle.putString(KEY_DATA, text);
                bundle.putString(KEY_NAME, socket.getInetAddress().getHostAddress());

                msg.setData(bundle);
                msg.what = SUCCESS;
                mHandler.sendMessage(msg);

                socket.close();
            }
        }catch (Exception e) {
            Message msg = mHandler.obtainMessage();

            Bundle bundle = new Bundle();
            bundle.putString(KEY_ERROR, e.getMessage());

            msg.setData(bundle);
            msg.what = ERROR;
            mHandler.sendMessage(msg);

            return;
        } finally {
            try {
                serverSocket.close();
            } catch (Exception e) {
                Log.d("myLog", Objects.requireNonNull(e.getMessage()));
            }
        }
    }
}
