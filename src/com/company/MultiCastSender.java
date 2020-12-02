package com.company;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


public class MultiCastSender extends Thread{

    private static DatagramSocket socket;
    private static InetAddress inetAddress;
    private static String To, From, PublicKye;
    private static final int port = 3000;

    public MultiCastSender(String to, String from, String publicKye) {
        To = to;
        From = from;
        PublicKye = publicKye;
    }

    @Override
    public void run (){

        try {
            //get connection with this group
            Connect("233.0.0.1");

            //send hello message to all
            SendMultiCastHello();

            //close socket, cause he is unused
            CloseSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Connect (String host) throws SocketException, UnknownHostException{
        socket = new DatagramSocket();
        inetAddress = InetAddress.getByName(host);
    }

    public static void SendMultiCastHello () throws IOException {
        String helloMessage = To + "\n" + From + "\n" + PublicKye;
        byte[] bytes = helloMessage.getBytes();

        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, inetAddress, port);
        socket.send(packet);
    }

    public static void CloseSocket () throws Exception {
        socket.close();
    }

}
