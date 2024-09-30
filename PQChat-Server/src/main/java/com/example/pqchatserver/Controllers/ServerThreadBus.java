// File: com/example/pqchatserver/Controllers/ServerThreadBus.java
package com.example.pqchatserver.Controllers;

import com.example.pqchatserver.Util.Logger;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

@Getter
public class ServerThreadBus {
    private final List<ServerThread> serverThreadList;
    private final JSONArray onlineUsers;
    private final Logger logger;

    public ServerThreadBus(Logger logger) {
        serverThreadList = new ArrayList<>();
        onlineUsers = new JSONArray();
        this.logger = logger;
    }

    public void addServerThread(ServerThread serverThread) {
        serverThreadList.add(serverThread);
//        logger.log("Added new ServerThread: " + serverThread.getClientID());
    }

    public int getServerThreadListSize() {
        return serverThreadList.size();
    }

    // multiCastSend
    public void multiCastSend(String message) {
        for (ServerThread serverThread : serverThreadList) {
            try {
                logger.log("Multicast message to " + serverThread.getClientID());
                serverThread.writeMessage(message);
            } catch (IOException e) {
                logger.log("Error while broadcasting message: " + e.getMessage());
            }
        }
    }

    public void boardCast(String clientID, String message) {
        for (ServerThread serverThread : serverThreadList) {
            if (!serverThread.getClientID().equals(clientID)) {
                try {
                    logger.log("Broadcast message to " + serverThread.getClientID());
                    serverThread.writeMessage(message);
                } catch (IOException e) {
                    logger.log("Error while broadcasting message: " + e.getMessage());
                }
            }
        }
    }

    public void removeServerThread(String clientID) {
        Iterator<ServerThread> iterator = serverThreadList.iterator();
        while (iterator.hasNext()) {
            ServerThread serverThread = iterator.next();
            if (serverThread.getClientID().equals(clientID)) {
                iterator.remove();
                logger.log("Removed ServerThread: " + clientID);
                break;
            }
        }
    }

    public void messageTransfer(String clientID, String message) {
        for (ServerThread serverThread : serverThreadList) {
            if (serverThread.getClientID().equals(clientID)) {
                try {
                    serverThread.writeMessage(message);
                    logger.log("Message transferred to " + clientID);
                    break;
                } catch (IOException e) {
                    logger.log("Message Transfer Error: " + e.getMessage());
                }
            }
        }
    }

    public void messageTransfer(UUID threadUUID, String message) {
        for (ServerThread serverThread : serverThreadList) {
            if (serverThread.getThreadUUID().equals(threadUUID)) {
                try {
                    serverThread.writeMessage(message);
                    logger.log("Message transferred to " + serverThread.getClientID());
                    break;
                } catch (IOException e) {
                    logger.log("Message Transfer Error: " + e.getMessage());
                }
            }
        }
    }
}
