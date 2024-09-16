package com.example.pqchatclient.Model;

import com.example.pqchatclient.View.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private final DatabaseDriver databaseDriver;
    private final SocketManager socketManager;

    // Change
    @Getter
    @Setter
    private User currentUser;
    @Getter
    @Setter
    private User targetUser;
    @Getter
    private ObservableList<User> onlineUsers;

    private Model() throws IOException {
        this.viewFactory = new ViewFactory();
        this.databaseDriver = new DatabaseDriver();
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

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public DatabaseDriver getDatabaseDriver() {
        return databaseDriver;
    }

    public SocketManager getSocketManager() {
        return socketManager;
    }
}
