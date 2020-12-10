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
    private static String ToPublicKye, FromName, FromPublicKye;
    private static final int port = 1234;

    public MultiCastSender(String to_pk, String from_name, String from_pk) {
        ToPublicKye = to_pk;
        FromName = from_name;
        FromPublicKye = from_pk;
    }

    @Override
    public void run (){

        try {
            //get connection with this group
            //System.out.println("MC Connecting");
            Connect("229.1.2.3");

            //send hello message to all
            //System.out.println("MC Sending");
            SendMultiCastHello();

            //close socket, cause he is unused
            //System.out.println("MC losing");
            CloseSocket();
        } catch (Exception e) {
            //TODO add handler and send the error message to it
            return;
        }
    }

    private void Connect (String host) throws SocketException, UnknownHostException{
        socket = new DatagramSocket();
        inetAddress = InetAddress.getByName(host);
    }

    private void SendMultiCastHello () throws IOException {
        String helloMessage = ToPublicKye + "\n" + FromName + "\n" + FromPublicKye;
        byte[] bytes = helloMessage.getBytes();

        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, inetAddress, port);
        socket.send(packet);
    }

    private void CloseSocket () throws Exception {
        socket.close();
    }

}
