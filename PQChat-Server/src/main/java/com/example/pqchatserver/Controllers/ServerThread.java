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
    private int random;

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
                case "validateEmail" -> {
                    String email = (String) reader.get("email");
                    random = getRandomNumberUsingNextInt(10000, 99999);
                    Email.sendEmail(email, "Validation Code", String.valueOf(random));
                }
                case "createAccount" -> {
                    String fullname = (String) reader.getString("fullname");
                    String username = (String) reader.get("username");
                    String password = (String) reader.get("password");
                    String validationCode = (String) reader.get("validationCode");

                    if(!validationCode.equals(String.valueOf(random))){
                        writer.put("flag", "failed");
                        writer.put("message", "Validation Code Not Found");
                    }

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
                case "logout" -> {
                    for(int i = 0; i < Server.serverThreadBus.getOnlineUsers().length(); ++i){
                        JSONObject retrieved = Server.serverThreadBus.getOnlineUsers().getJSONObject(i);
                        if(retrieved.getString("clientID").equals(reader.getString("sender"))) {
                            Server.serverThreadBus.getOnlineUsers().remove(i);
                            Server.serverThreadBus.removeServerThread(reader.getString("sender"));
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

    private void saveFile(String fileName) throws IOException {
        // Nhận dữ liệu file từ client
//        String fileName = serverReader.readLine();
        String filePath = "C:\\Users\\phuquocchamp\\Coding\\Java\\JavaFX\\PQChat-Server\\src\\main\\resources\\Files\\" + fileName;
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = serverSocket.getInputStream().read(buffer)) != -1) {
            fileOutputStream.write(buffer, 0, bytesRead);
        }

        fileOutputStream.close();
    }

    private void transferFile(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int bytesRead;
            OutputStream outputStream = serverSocket.getOutputStream();
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Sending file error!");
        }

    }

}
