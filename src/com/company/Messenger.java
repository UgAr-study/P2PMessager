package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.CharBuffer;

public class Messenger extends Thread{
    private Socket socket;
    private BufferedWriter out;
    private BufferedReader in;

    public Messenger(Socket socketInput) throws IOException {
        socket = socketInput;
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void run() {
        String data = "";
        while (true) {
            try {
                receive(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(data);
        }
    }

    public void receive(String string) throws IOException {
        in.read(CharBuffer.wrap(string));
    }

    public void send(String string) throws IOException {
        out.write(string);
    }
}