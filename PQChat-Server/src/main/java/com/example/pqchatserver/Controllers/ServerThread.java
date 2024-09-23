package com.example.pqchatserver.Controllers;

import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.UUID;

import com.example.pqchatserver.Model.Model;
import com.example.pqchatserver.Model.User;
import com.example.pqchatserver.Util.Email;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;


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

    public ServerThread(Socket serverSocket) throws IOException {
        this.isClosed = false;
        this.threadUUID = UUID.randomUUID();
        this.serverSocket = serverSocket;
        this.serverWriter = new BufferedWriter(new OutputStreamWriter(serverSocket.getOutputStream()));
        this.serverReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
    }


    @Override
    public void run() {

        String streamMessage;
        while (!isClosed) {
            try {
                streamMessage = serverReader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


            JSONObject reader = new JSONObject(streamMessage);
            System.out.println("LOG >>> " + reader.toString());
            JSONObject writer = new JSONObject();

            switch (reader.getString("prefix")) {
                case "evaluateAccount" -> {
                    // Check account from db.
                    String username = (String) reader.get("username");
                    String password = (String) reader.get("password");

                    writer = Model.getInstance().getDatabaseDriver().evaluatedAccount(username, password);
                    writer.put("prefix", "evaluateAccount");
                    Server.serverThreadBus.messageTransfer(this.threadUUID, writer.toString());

                    if (writer.getString("flag").equals("success")) {
                        this.setClientID(writer.getString("clientID"));
                        System.out.println("[LOG] >>> " + this.getClientID() + " Join To Server");
                        // Broadcast Online Users
                        JSONObject user = new JSONObject();
                        user.put("clientID", (String) writer.get("clientID"));
                        user.put("fullname", (String) writer.get("fullname"));
                        user.put("avatar", "");
                        Server.serverThreadBus.getOnlineUsers().put(user);

                        JSONObject onlineUsers = new JSONObject();
                        onlineUsers.put("prefix", "updateOnlineUsers");
                        onlineUsers.put("onlineUsers", Server.serverThreadBus.getOnlineUsers());
                        Server.serverThreadBus.multiCastSend(onlineUsers.toString());
                    }
                }
                case "createAccount" -> {
                    String fullname = (String) reader.get("fullname");
                    String username = (String) reader.get("username");
                    String password = (String) reader.get("password");

                    writer.put("prefix", "createAccount");
                    writer.put("username", username);

                    boolean check = Model.getInstance().getDatabaseDriver().fetchUserByUsername(username);
                    if (check) {
                        writer.put("flag", "failed");
                        writer.put("message", username + " account is exit");
                    } else {
                        Model.getInstance().getDatabaseDriver().createUser(fullname, username, password);
                        writer.put("flag", "success");
                    }
                    Server.serverThreadBus.messageTransfer(this.threadUUID, writer.toString());
                }
                case "chat", "imageTransfer", "fileTransfer" -> {
                    String receiver = (String) reader.get("receiver");
                    Server.serverThreadBus.messageTransfer(receiver, streamMessage);
                }
                case "resetPassword" -> {
                    String username = reader.getString("username");
                    String newPassword = reader.getString("newPassword");
                    boolean check = Model.getInstance().getDatabaseDriver().updateUserPassword(username, newPassword);
                    writer.put("prefix", "resetPassword");
                    writer.put("username", username);
                    writer.put("flag", check ? "success" : "failed");
                    Server.serverThreadBus.messageTransfer(this.threadUUID, writer.toString());
                }
                case "logout" -> {
                    for(int i = 0; i < Server.serverThreadBus.getOnlineUsers().length(); ++i){
                        JSONObject retrieved = Server.serverThreadBus.getOnlineUsers().getJSONObject(i);
                        if(retrieved.getString("clientID").equals(reader.getString("user"))) {
                            Server.serverThreadBus.getOnlineUsers().remove(i);
                            Server.serverThreadBus.removeServerThread(reader.getString("user"));
                            JSONObject removeUser = new JSONObject();
                            removeUser.put("prefix", "removeUser");
                            removeUser.put("user", reader.getString("user"));
                            Server.serverThreadBus.boardCast(reader.getString("user"), removeUser.toString());
                        }
                    }
                }
            }

        }
    }

    // Utilities
    public void writeMessage(String message) throws IOException {
        serverWriter.write(message);
        serverWriter.newLine();
        serverWriter.flush();
    }
    public int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }
}
