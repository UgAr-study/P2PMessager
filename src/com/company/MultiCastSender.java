package com.company;

import java.io.IOException;
import java.net.*;

public class MultiCastSender extends Thread{

    private static DatagramSocket socket;
    private static InetAddress inetAddress;
    private static String Name, PublicKye;
    private static final int port = 3000;

    @Override
    public void run (){

        try {
            Connect("233.0.0.1");
            GetData("Artem", "12QWERTY");
            SendMultiCastHello();
            CloseSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void Connect (String host) throws SocketException, UnknownHostException{
        socket = new DatagramSocket();
        inetAddress = InetAddress.getByName(host);
    }

    public static void GetData (String yourName, String publicKye) {
        Name = yourName;
        PublicKye = publicKye;
    }

    public static void SendMultiCastHello () throws IOException {
        String helloMessage = "all\n" + Name + "\n" + PublicKye;
        byte[] bytes = helloMessage.getBytes();

        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, inetAddress, port);
        socket.send(packet);
    }

    public static void CloseSocket () throws Exception {
        socket.close();
    }

}
