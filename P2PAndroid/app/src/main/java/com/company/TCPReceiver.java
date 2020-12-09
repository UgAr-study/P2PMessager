package com.company;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;

public class TCPReceiver extends Thread {

    private Socket socket;
    private ServerSocket serverSocket;
    private final int port = 4000;
    private Handler mHandler;

    public TCPReceiver(Handler handler) {
        mHandler = handler;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                socket = serverSocket.accept();

                DataInputStream in = new DataInputStream(socket.getInputStream());
                String text = in.readUTF();

                Message msg = mHandler.obtainMessage();

                Bundle bundle = new Bundle();
                bundle.putString("Data", text);

                msg.setData(bundle);
                mHandler.sendMessage(msg);

                socket.close();
            }
        }catch (Exception e) {
            mHandler.sendEmptyMessage(1);
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
