package com.company;

import javax.crypto.SealedObject;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public abstract class Messenger {

    private static final int serverPort = 4000;

    public static boolean SendMessageToIp (String message, String ipAddress) {
        try {
            InetAddress ipAddr = InetAddress.getByName(ipAddress);

            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddr, serverPort), 1000);

            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            out.writeUTF(message);
            out.flush();
            out.close();

            socket.close();
            return true;

        } catch (UnknownHostException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean SendEncryptMessageToIp (SealedObject encryptMsg, String ipAddress) {
        try {
            InetAddress ipAddr = InetAddress.getByName(ipAddress);
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ipAddr, serverPort), 1000);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

            out.writeObject(encryptMsg);
            out.flush();
            out.close();
            socket.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}