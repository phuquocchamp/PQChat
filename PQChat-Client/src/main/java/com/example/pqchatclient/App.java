package com.example.pqchatclient;

import com.example.pqchatclient.Model.Model;
import javafx.application.Application;

import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        Model.getInstance().getViewFactory().showLoginWindow();
    }

//    @Override
//    public void stop(){
//        System.out.println("Application is stopped");
//        Model.getInstance().getSocketManager().sendMessage("removeClient_"+Model.getInstance().getCurrentClient().clientIDProperty().get());
//    }
    public static void main(String[] args) {
        launch(args);
    }
}
