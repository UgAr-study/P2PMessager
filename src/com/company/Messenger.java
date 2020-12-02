package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Messenger extends Thread{
    private Socket socket;
    private Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader in;

    public void run() {
        String string = "";
        try {
            read(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(string);
    }
    
    public void connect(InetAddress ip, int port) throws IOException {
        socket = new Socket(ip, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

    public void read(String string) throws IOException {
        out.write(string);
    }
}