package com.company;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
	    SQLTable table = new SQLTable();
	    table.Connect();
	    table.CreateDB("users");
	    table.WriteDB("Artem", "233.0.0.1", "12QWERTY");
	    //table.DeleteByName("Artem");
	    new MulticastReceiver(table).start();
	    new TCPReceiver().start();
		try {
			Start(table.getIpByName("Ignat"), 3000);
		} catch (IOException e) {
			e.printStackTrace();
			table.CloseDB();
			return;
		}
	    new MultiCastSender("all", "Artem", "12QWERTY").start();
		table.CloseDB();
    }

    public static void Start(String address, int serverPort) throws IOException {
		InetAddress ipAddr = InetAddress.getByName(address);
		Socket socket = new Socket(ipAddr, serverPort);
		System.out.println("Client has connected to the server!");
		new Messenger(socket);
	}
}
