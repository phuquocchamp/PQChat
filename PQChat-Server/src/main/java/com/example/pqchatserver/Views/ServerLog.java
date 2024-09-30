package com.example.pqchatserver.Views;

import com.example.pqchatserver.Controllers.Server;
import com.example.pqchatserver.Util.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Objects;

public class ServerLog extends Application implements Logger {

    private TextArea logArea;
    private TextField portField;
    private Button startButton;
    private Button stopButton;

    private Thread serverThread;
    private volatile boolean isServerRunning = false;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("KL Chat Server Log");

        // Áp dụng lớp CSS
        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.getStyleClass().add("server-log__text-area");

        portField = new TextField("7777");
        portField.setPromptText("Port");
        portField.getStyleClass().add("server-log__port-field");

        startButton = new Button("Start Server");
        startButton.getStyleClass().add("server-log__button");

        stopButton = new Button("Stop Server");
        stopButton.setDisable(true);
        stopButton.getStyleClass().add("server-log__button");

        startButton.setOnAction(e -> startServer());
        stopButton.setOnAction(e -> stopServer());

        VBox layout = new VBox(10, new Label("Server Port:"), portField, startButton, stopButton, new Label("Logs:"), logArea);
        layout.setPadding(new javafx.geometry.Insets(10));
        layout.getStyleClass().add("server-log__container");

        Scene scene = new Scene(layout, 600, 500);

        // Thêm file CSS vào Scene
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/Styles/ServerLog.css")).toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void startServer() {
        if (isServerRunning) {
            log("Server is already running.");
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portField.getText());
        } catch (NumberFormatException e) {
            log("Invalid port number.");
            return;
        }

        isServerRunning = true;
        startButton.setDisable(true);
        stopButton.setDisable(false);

        serverThread = new Thread(() -> Server.startServer(port, this));
        serverThread.setDaemon(true);
        serverThread.start();

        log("Server started on port " + port);
    }

    private void stopServer() {
        if (!isServerRunning) {
            log("Server is not running.");
            return;
        }

        isServerRunning = false;
        serverThread.interrupt();
        startButton.setDisable(false);
        stopButton.setDisable(true);
        log("Server stopped.");
    }

    @Override
    public void log(String message) {
        Platform.runLater(() -> logArea.appendText(message + "\n"));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
