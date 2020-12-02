package com.company;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class TCPReceiver extends Thread {

    private Socket socket;
    private ServerSocket serverSocket;
    private final int port = 4000;

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                socket = serverSocket.accept();
                new Messanger(socket).start();
                socket.close();
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
