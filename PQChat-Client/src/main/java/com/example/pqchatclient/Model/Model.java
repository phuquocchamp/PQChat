package com.example.pqchatclient.Model;

import com.example.pqchatclient.Controller.Client.SingleContact.SingleChatController;
import com.example.pqchatclient.View.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Getter
public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private final SocketManager socketManager;

    @Setter
    private User currentUser;
    @Setter
    private User targetUser;
    private final ObservableList<User> onlineUsers;
//    private final SingleChatController singleChat;  // Queue for storing incoming messages

    private Model() throws IOException {
        this.viewFactory = new ViewFactory();
        this.socketManager = new SocketManager();
        this.currentUser = new User("", "", "");
        this.targetUser = new User("", "", "");
        this.onlineUsers = FXCollections.observableArrayList();
//        this.singleChat = new SingleChatController();
    }
    // Singleton
    public static synchronized Model getInstance() {
        if (model == null) {
            try {
                model = new Model();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return model;
    }
}
