package com.example.pqchatserver.Controllers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;


public class Server {
    public static volatile ServerThreadBus serverThreadBus;
    public static Socket serverOfSocket;

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(7777);
        serverThreadBus = new ServerThreadBus();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10,
                100,
                10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(8)
        );
        System.out.println("Server is waiting to accept user...");

        while (true) {
            serverOfSocket = listener.accept();
            ServerThread serverThread = new ServerThread(serverOfSocket);
            serverThreadBus.addServerThread(serverThread);
            System.out.println("Server accepted");
            System.out.println("Number of running threads : " + serverThreadBus.getServerThreadListSize());
            executor.execute(serverThread);
        }
    }
}
