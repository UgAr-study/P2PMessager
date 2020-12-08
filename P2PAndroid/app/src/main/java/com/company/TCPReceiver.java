package com.company;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPReceiver extends Thread {

    private Socket socket;
    private ServerSocket serverSocket;
    private final int port = 4000;
    private View v;

    public TCPReceiver(View view) {
        v = view;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                socket = serverSocket.accept();

                Handler mHandler = new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        Bundle b = msg.getData();
                        b.get("Msg");
                    }
                };

                new Messenger(socket).start();
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}

class ReceiveMessage {
    private View v;



}
