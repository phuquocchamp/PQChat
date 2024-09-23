package com.example.pqchatclient.Model;

import com.example.pqchatclient.Controller.Client.SingleContact.SingleChatController;
import com.example.pqchatclient.Controller.Client.SingleContact.SingleContactController;
import javafx.application.Platform;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.*;

public class SocketManager {
    private final BufferedWriter clientWriter;
    private final BufferedReader clientReader;
    private final BlockingQueue<String> messageQueue;  // Queue for storing incoming messages
    private final ExecutorService executorService;     // ExecutorService to manage threads

//    private SingleChatController singleChat;

    public SocketManager() throws IOException {
        Socket clientSocket = new Socket("localhost", 7777);
        if (clientSocket.isConnected()) {
            System.out.println("Connected successfully!");
        } else {
            System.out.println("Failed to connect to server!");
        }
//        this.singleChat = new SingleChatController();

        this.clientWriter = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        this.clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        this.messageQueue = new LinkedBlockingQueue<>();

        this.executorService = Executors.newSingleThreadExecutor();
        this.executorService.submit(this::listenForMessages);
    }

    public void sendMessage(String message) {
        try {
            clientWriter.write(message);
            clientWriter.newLine();
            clientWriter.flush();
        } catch (IOException e) {
            System.out.println("Error sending message: " + e.getMessage());
        }
    }

    private void listenForMessages() {
        try {
            String message;
            while ((message = clientReader.readLine()) != null) {
                JSONObject jsonObject = new JSONObject(message);
                if (jsonObject.getString("prefix").equals("updateOnlineUsers")) {
                    if (jsonObject.getString("prefix").equals("updateOnlineUsers")) {
                        JSONArray onlineUsersArray = jsonObject.getJSONArray("onlineUsers");
                        System.out.println("[LOG] >>> Update Online Users: " + onlineUsersArray);
                        Platform.runLater(() -> {
                            Model.getInstance().getOnlineUsers().clear();
                            for (int i = 0; i < onlineUsersArray.length(); i++) {
                                JSONObject userObject = onlineUsersArray.getJSONObject(i);
                                String clientID = userObject.getString("clientID");
                                String fullName = userObject.getString("fullname");
                                String avatarPath = userObject.optString("avatar", "Images/avatar-default.jpg");
                                User newUser = new User(clientID, fullName, avatarPath);
                                if (!isUserAlreadyOnline(newUser)) {
                                    Model.getInstance().getOnlineUsers().add(newUser);
                                }
                            }
                        });
                    }
                }
                if(
                    jsonObject.getString("prefix").equals("chat") ||
                    jsonObject.getString("prefix").equals("imageTransfer") ||
                    jsonObject.getString("prefix").equals("fileTransfer")
                ) {
                    System.out.println("[LOG] >>> Chat, Image, File received: " + jsonObject.toString());
                    SingleChatController.getInstance().loadMessage(jsonObject.toString());
                }else {
                    messageQueue.offer(message);
                    System.out.println("[LOG] >>> Socket Client Received: " + jsonObject.toString(4));
                }
            }
        } catch (IOException e) {
            System.out.println("Error receiving message: " + e.getMessage());
        }
    }

    public String retrieveMessage() {
        String message = messageQueue.poll();
        return Objects.requireNonNullElse(message, "");
    }


    public String retrieveMessageWithTimeout(long timeout, TimeUnit unit) {
        try {
            return messageQueue.poll(timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    public void removeMessage() {
        messageQueue.remove();
    }

    public void stopListening() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }

    private static boolean isUserAlreadyOnline(User user) {
        for (User onlineUser : Model.getInstance().getOnlineUsers()) {
            if (onlineUser.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }
}
