package com.company;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

public class Messenger extends Thread {
    private Socket socket;
    private DataInputStream in;

    public Messenger(Socket socketInput) throws IOException {
        socket = socketInput;
        in = new DataInputStream(socket.getInputStream());
    }

    public void run() {

        try {
            new SendMessages(socket).start();
        }catch (IOException e) {
            e.printStackTrace();
            Close();
            return;
        }

        String dataIn = null;

        while (true) {
            try {
                dataIn = in.readUTF();
                if (dataIn.equals("end"))
                    break;
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
            System.out.println(dataIn);
        }

        Close();
    }

    public void Close () {
        try {
            in.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class SendMessages extends Thread {
    private Socket socket;
    BufferedReader usrData;
    private DataOutputStream out;

    public SendMessages(Socket socketInput) throws IOException {
        socket = socketInput;
        out = new DataOutputStream(socket.getOutputStream());
        usrData = new BufferedReader(new InputStreamReader(System.in));
    }

    public void run() {
        String dataOut = null;

        while (true) {
            try {
                dataOut = usrData.readLine();

                if (dataOut.equals("end")) {
                    out.writeUTF(dataOut);
                    out.flush();
                    break;
                }

                out.writeUTF(dataOut);
                out.flush();

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        Close();
    }

    public void Close () {
        try {
            usrData.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}