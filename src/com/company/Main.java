package com.company;

public class Main {

    public static void main(String[] args) {
	    SQLTable table = new SQLTable();
	    table.Connect();
	    table.CreateDB("users");
	    table.WriteDB("Artem", "233.0.0.1", "12QWERTY");
	    //table.DeleteByName("Artem");
	    new MulticastReceiver(table).start();
	    new TCPReceiver();
	    new MultiCastSender("all", "Artem", "12QWERTY");
		table.CloseDB();
    }
}
