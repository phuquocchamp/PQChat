// File: com/example/pqchatserver/Controllers/ServerThread.java
package com.example.pqchatserver.Controllers;

import com.example.pqchatserver.Model.Model;
import com.example.pqchatserver.Util.Logger;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.UUID;

public class ServerThread implements Runnable {
    private boolean isClosed = false;
    @Getter
    private final UUID threadUUID;
    @Setter
    @Getter
    private String clientID;
    private final Socket serverSocket;
    private final BufferedWriter serverWriter;
    private final BufferedReader serverReader;
    private final Logger logger;

    public ServerThread(Socket serverSocket, Logger logger) throws IOException {
        this.isClosed = false;
        this.threadUUID = UUID.randomUUID();
        this.serverSocket = serverSocket;
        this.serverWriter = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
        this.serverReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
        this.logger = logger;
    }

    @Override
    public void run() {
        String streamMessage;
        try {
            while (!isClosed && (streamMessage = serverReader.readLine()) != null) {
                JSONObject reader = new JSONObject(streamMessage);
                logger.log("Received message from " + getClientInfo() + ": " );
                JSONObject writer = new JSONObject();

                switch (reader.getString("prefix")) {
                    case "evaluateAccount" -> handleEvaluateAccount(reader, writer);
                    case "createAccount" -> handleCreateAccount(reader, writer);
                    case "chat", "imageTransfer", "fileTransfer" -> handleTransfer(reader, streamMessage);
                    case "resetPassword" -> handleResetPassword(reader, writer);
                    case "logout" -> handleLogout(reader);
                    default -> logger.log("Unknown prefix: " + reader.getString("prefix"));
                }
            }
        } catch (IOException e) {
            logger.log("IO Exception in ServerThread: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void handleEvaluateAccount(JSONObject reader, JSONObject writer) {
        String username = reader.getString("username");
        String password = reader.getString("password");

        writer = Model.getInstance().getDatabaseDriver().evaluatedAccount(username, password);
        writer.put("prefix", "evaluateAccount");
        Server.serverThreadBus.messageTransfer(this.threadUUID, writer.toString());

        if (writer.getString("flag").equals("success")) {
            this.setClientID(writer.getString("clientID"));
            logger.log(this.getClientID() + " joined the server.");
            // Broadcast Online Users
            JSONObject user = new JSONObject();
            user.put("clientID", writer.getString("clientID"));
            user.put("fullname", writer.getString("fullname"));
            user.put("avatar", "");
            Server.serverThreadBus.getOnlineUsers().put(user);

            JSONObject onlineUsers = new JSONObject();
            onlineUsers.put("prefix", "updateOnlineUsers");
            onlineUsers.put("onlineUsers", Server.serverThreadBus.getOnlineUsers());
            Server.serverThreadBus.multiCastSend(onlineUsers.toString());
        }
    }

    private void handleCreateAccount(JSONObject reader, JSONObject writer) {
        String fullname = reader.getString("fullname");
        String username = reader.getString("username");
        String password = reader.getString("password");

        writer.put("prefix", "createAccount");
        writer.put("username", username);

        boolean check = Model.getInstance().getDatabaseDriver().fetchUserByUsername(username);
        if (check) {
            writer.put("flag", "failed");
            writer.put("message", username + " account already exists.");
            logger.log("Failed account creation attempt for username: " + username);
        } else {
            Model.getInstance().getDatabaseDriver().createUser(fullname, username, password);
            writer.put("flag", "success");
            logger.log("Account created for username: " + username);
        }
        Server.serverThreadBus.messageTransfer(this.threadUUID, writer.toString());
    }

    private void handleTransfer(JSONObject reader, String streamMessage) {
        String receiver = reader.getString("receiver");
        Server.serverThreadBus.messageTransfer(receiver, streamMessage);
        logger.log("Transferred message from " + getClientInfo() + " to " + receiver);
    }

    private void handleResetPassword(JSONObject reader, JSONObject writer) {
        String username = reader.getString("username");
        String newPassword = reader.getString("newPassword");
        boolean check = Model.getInstance().getDatabaseDriver().updateUserPassword(username, newPassword);
        writer.put("prefix", "resetPassword");
        writer.put("username", username);
        writer.put("flag", check ? "success" : "failed");
        Server.serverThreadBus.messageTransfer(this.threadUUID, writer.toString());
        logger.log("Password reset for username: " + username + " - " + (check ? "Success" : "Failed"));
    }

    private void handleLogout(JSONObject reader) {
        String user = reader.getString("user");
        for(int i = 0; i < Server.serverThreadBus.getOnlineUsers().length(); ++i){
            JSONObject retrieved = Server.serverThreadBus.getOnlineUsers().getJSONObject(i);
            if(retrieved.getString("clientID").equals(user)) {
                Server.serverThreadBus.getOnlineUsers().remove(i);
                Server.serverThreadBus.removeServerThread(user);
                JSONObject removeUser = new JSONObject();
                removeUser.put("prefix", "removeUser");
                removeUser.put("user", user);
                Server.serverThreadBus.boardCast(user, removeUser.toString());
                logger.log("User logged out: " + user);
                break;
            }
        }
    }

    private String getClientInfo() {
        return serverSocket.getInetAddress().getHostAddress() + ":" + serverSocket.getPort();
    }

    // Utilities
    public void writeMessage(String message) throws IOException {
        serverWriter.write(message);
        serverWriter.newLine();
        serverWriter.flush();
        logger.log("Sent message to " + getClientInfo());
    }

    public int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    private void closeConnection() {
        try {
            serverReader.close();
            serverWriter.close();
            serverSocket.close();
            isClosed = true;
            logger.log("Connection closed for " + getClientInfo());
        } catch (IOException e) {
            logger.log("Error closing connection: " + e.getMessage());
        }
    }
}
