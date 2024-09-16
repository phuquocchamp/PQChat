package com.example.pqchatserver.Controllers;

import com.example.pqchatserver.Model.User;
import lombok.Getter;
import org.json.JSONArray;

import java.io.IOException;
import java.util.*;

// Lớp quản lí server thread
@Getter
public class ServerThreadBus {
    private final List<ServerThread> serverThreadList;

    private final JSONArray onlineUsers;

    public ServerThreadBus() {
        serverThreadList = new ArrayList<>();
        onlineUsers = new JSONArray();
    }


    public void addServerThread(ServerThread serverThread) {
        serverThreadList.add(serverThread);
    }

    public int getServerThreadListSize() {
        return serverThreadList.size();
    }

    // multiCastSend
    public void multiCastSend(String message)  {
        for (ServerThread serverThread : Server.serverThreadBus.getServerThreadList()) {
            try{
                System.out.println("[LOG] >>> Multicast message to " + serverThread.getClientID() + " " + message);
                serverThread.writeMessage(message);
            }catch (IOException e) {
                System.out.println("[LOG] >>> Error while broadcast message");
            }
        }
    }

    public void boardCast(String clientID, String message) {
        for (ServerThread serverThread : Server.serverThreadBus.getServerThreadList()) {
            if (!serverThread.getClientID().equals(clientID)) {
                try {
                    System.out.println("[LOG] >>> broadcast message to " + serverThread.getClientID());
                    serverThread.writeMessage(message);
                } catch (IOException e) {
                    System.out.println("[LOG] >>> Error while broadcast message");
                }
            }
        }
    }


    public void removeServerThread(String clientID) {
        for (int i = 0; i < Server.serverThreadBus.getServerThreadListSize(); i++) {
            if (Server.serverThreadBus.getServerThreadList().get(i).getClientID().equals(clientID)) {
                Server.serverThreadBus.serverThreadList.remove(i);
                System.out.println("[LOG] >>> Removed server thread " + clientID);
                break;
            }
        }
    }


    public void messageTransfer(String clientID,  String message) {
        for (ServerThread serverThread : Server.serverThreadBus.getServerThreadList()) {
            if (serverThread.getClientID().equals(clientID)) {
                try {
                    serverThread.writeMessage(message);
                    break;
                } catch (IOException e) {
                    System.out.println("[LOG] >>> Message Transfer Error: " + e.getMessage());
                }
            }
        }
    }


    public void messageTransfer(UUID threadUUID, String message) {
        for (ServerThread serverThread : Server.serverThreadBus.getServerThreadList()) {
            if (serverThread.getThreadUUID().equals(threadUUID)) {
                try {
                    serverThread.writeMessage(message);
                    break;
                } catch (IOException e) {
                    System.out.println("[LOG] >>> Message Transfer Error: " + e.getMessage());
                }
            }
        }
    }


}
