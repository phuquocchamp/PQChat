// File: com/example/pqchatserver/Controllers/Server.java
package com.example.pqchatserver.Controllers;

import com.example.pqchatserver.Util.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

public class Server {
    public static ServerThreadBus serverThreadBus;
    public static Socket serverOfSocket;


    public static void startServer(int port, Logger logger) {
        try {
            ServerSocket listener = new ServerSocket(port);
            serverThreadBus = new ServerThreadBus(logger);
            ExecutorService executor = Executors.newCachedThreadPool();
            logger.log("Server is waiting to accept users on port " + port + "...");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = listener.accept();
                ServerThread serverThread = new ServerThread(clientSocket, logger);
                serverThreadBus.addServerThread(serverThread);
                // Lấy thời gian login hiện tại
                String loginTime = LocalDateTime.now().format(dateTimeFormatter);

                // Log thông tin về thời gian kết nối và địa chỉ IP của client
                logger.log("Client connected from " + clientSocket.getInetAddress().getHostAddress() + " at " + loginTime);
                logger.log("Number of running threads: " + serverThreadBus.getServerThreadListSize());
                executor.submit(serverThread);
            }

            listener.close();
            logger.log("Server socket closed.");
        } catch (IOException e) {
            if (isServerRunning()) { // Implement this method based on your shutdown logic
                logger.log("Server encountered an error: " + e.getMessage());
            } else {
                logger.log("Server stopped.");
            }
        }
    }

    private static boolean isServerRunning() {
        // Implement based on your server's state management
        return true;
    }
}
