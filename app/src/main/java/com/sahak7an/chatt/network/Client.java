package com.sahak7an.chatt.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class Client implements Runnable{

    private Socket socket;
    private int serverPort;
    private String ipAddress;
    private BufferedReader bufferedReader;

    public Client(String ipAddress, int serverPort) {

        this.ipAddress = ipAddress;
        this.serverPort = serverPort;

    }

    @Override
    public void run() {

        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            socket = new Socket(inetAddress, serverPort);

            while (!Thread.currentThread().isInterrupted()) {

                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String message = bufferedReader.readLine();

                if (message == null || message.contentEquals("False")) {
                    break;
                }

                Log.d("HELLO", message);

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void sendMessage(String message) {
        Log.d("HELLO", message);
        new Thread(() -> {
            try {
                if (null != socket) {
                    PrintWriter out = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())),
                            true);
                    out.println(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
