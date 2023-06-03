package com.sahak7an.chatt.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable{

    private int serverPort;
    private ServerSocket serverSocket;

    public Server(int serverPort) {

        this.serverPort = serverPort;

    }

    @Override
    public void run() {

        Socket socket;

        try {
            serverSocket = new ServerSocket(serverPort);

            while (!Thread.currentThread().isInterrupted()) {

                try {
                    socket = serverSocket.accept();
                    Communication communication = new Communication(socket);
                    new Thread(communication).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
