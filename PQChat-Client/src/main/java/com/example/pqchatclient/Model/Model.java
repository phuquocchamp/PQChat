package com.example.pqchatclient.Model;

import com.example.pqchatclient.View.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;

@Getter
public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private final SocketManager socketManager;

    // Change
    @Setter
    private User currentUser;
    @Setter
    private User targetUser;
    private ObservableList<User> onlineUsers;

    private Model() throws IOException {
        this.viewFactory = new ViewFactory();
        this.socketManager = new SocketManager();
        this.currentUser = new User("", "", "");
        this.targetUser = new User("", "", "");
        this.onlineUsers = FXCollections.observableArrayList();
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
