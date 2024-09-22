package com.example.pqchatclient.Model;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Base64;

public class SocketManager {
    private final BufferedWriter clientWriter;
    private final BufferedReader clientReader;

    public SocketManager() throws IOException {
        Socket clientSocket = new Socket("localhost", 7777);
        if (clientSocket.isConnected()) {
            System.out.println("Connected successfully!");
        } else {
            System.out.println("Failed to connect to server!");
        }

        this.clientWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        this.clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

    }

    public void sendMessage(String message) {
        try {
            clientWriter.write(message);
            clientWriter.newLine();
            clientWriter.flush();
        } catch (IOException e) {
            System.out.println("Sending file error in SocketManager class");
        }
    }

    public String receiverMessage() throws IOException {
        return clientReader.readLine();
    }
}
