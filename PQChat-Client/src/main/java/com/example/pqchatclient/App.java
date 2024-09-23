package com.example.pqchatclient;

import com.example.pqchatclient.Model.Model;
import javafx.application.Application;

import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.SQLException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        Model.getInstance().getViewFactory().showLoginWindow();
    }

    @Override
    public void stop(){
        System.out.println("Application is stopped");
        JSONObject removeUser = new JSONObject();
        removeUser.put("prefix", "logout");
        removeUser.put("user", Model.getInstance().getCurrentUser().getId().get());
        Model.getInstance().getSocketManager().sendMessage(removeUser.toString());
        System.out.println(removeUser.toString());
    }
    public static void main(String[] args) {
        launch(args);
    }
}
