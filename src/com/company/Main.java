package com.company;

import org.ibex.nestedvm.util.Seekable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
	    SQLTable table = new SQLTable();
	    table.Connect();
	    table.CreateDB("users");
	    table.WriteDB("Artem", "192.168.43.78", "12QWERTY");
	    //table.DeleteByName("Artem");
	    new MulticastReceiver(table).start();
	    new TCPReceiver().start();
		new MultiCastSender("all", "Artem", "12QWERTY").start();

		InputStreamReader in = new InputStreamReader(System.in);

		try {

			in.read();
			String IgnatAddr = table.getIpByName("Ignat");
			System.out.println("[" + IgnatAddr + "]");
			Start(IgnatAddr, 4000);
		} catch (Exception e) {
			e.printStackTrace();
			table.CloseDB();
			return;
		}
		//table.CloseDB();
		try {
			in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public static void Start(String address, int serverPort) throws IOException {
		InetAddress ipAddr = InetAddress.getByName(address);
		Socket socket = new Socket(ipAddr, serverPort);
		System.out.println("Client has connected to the server!");
		new Messenger(socket).start();
	}
}
