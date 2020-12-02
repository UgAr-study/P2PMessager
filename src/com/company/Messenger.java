package com.company;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Messenger extends Thread{
    private Socket socket;
    private Socket clientSocket;
    private BufferedWriter out;
    private BufferedReader in;

    public Messenger(Socket socket) {
        
    }

    public void run() {
        String string = "";
        try {
            read(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(string);
    }

    public void read(String string) throws IOException {
        out.write(string);
    }
}