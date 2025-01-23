package com.sahak7an.chatt.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Communication implements Runnable{

    private final Socket clientSocket;
    private BufferedReader bufferedReader;

    public Communication(Socket clientSocket) {

        this.clientSocket = clientSocket;

        try {
            this.bufferedReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void run() {

        while (!Thread.currentThread().isInterrupted()) try {

            String message = bufferedReader.readLine();

            if (message == null || message.contentEquals("False")) {
                break;
            }

            Log.d("HELLO", "MESSAGE: " + message);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
