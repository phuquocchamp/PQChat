package com.example.pqchatclient.Controller.Client.SingleContact;

import com.example.pqchatclient.Model.Model;
import io.github.cdimascio.dotenv.Dotenv;
import io.github.gleidson28.GNAvatarView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.json.JSONObject;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class SingleChatController implements Initializable {
    public Label senderName__lbl;
    public GNAvatarView senderAvatar__img;
    public ScrollPane messageContainer__scrollPane;
    public TextArea enterMessage__TextArea;
    public Button sendMessage__btn;

    public Label status;
    public Button fileSend__btn;
    public Button imageSend__btn;
    public Button emojiSend__btn;

    public VBox messageBox__vBox;
    public static Button download__btn;
    String currentUserID = Model.getInstance().getCurrentUser().getId().get();

    private static String targetUserID;
    private static final Map<String, VBox> messageBoxMap = new HashMap<>();

    private static SingleChatController instance;

    public SingleChatController() {
    }

    public static SingleChatController getInstance() {
        if (instance == null) {
            instance = new SingleChatController();
        }
        return instance;
    }

    Dotenv dotenv = Dotenv.load();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        senderName__lbl.textProperty().bind(Model.getInstance().getTargetUser().getFullName());
        targetUserID = Model.getInstance().getTargetUser().getId().get();
        Model.getInstance().getTargetUser().getId().addListener((observableValue, oldValue, newValue) -> {
            targetUserID = Model.getInstance().getTargetUser().getId().get();
            senderName__lbl.textProperty().bind(Model.getInstance().getTargetUser().getFullName());
            Platform.runLater(() -> {
                senderAvatar__img.imageProperty().bind(Bindings.createObjectBinding(() -> new Image(String.valueOf(getClass().getResource("/Images/avatar-default.jpg")))));

                if (!messageBoxMap.containsKey(targetUserID)) {

                    messageBox__vBox = new VBox();
                    messageBox__vBox.setFillWidth(true);
                    messageBox__vBox.setPadding(new Insets(10, 0, 0, 10));
                    // Add messageBox__vBox into map
                    messageBoxMap.put(targetUserID, messageBox__vBox);
                }

                VBox targetBox = messageBoxMap.get(targetUserID);
                targetBox.setFillWidth(true);
                targetBox.setPadding(new Insets(10, 0, 0, 10));
                messageContainer__scrollPane.setContent(targetBox);

                // Sending message
                sendMessage__btn.setOnAction(event -> onSendingMessage());
                fileSend__btn.setOnAction(event -> onSendingFile());
                imageSend__btn.setOnAction(event -> onSendingImage());

                targetBox.heightProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
                        messageContainer__scrollPane.setVvalue((Double) newValue);
                    }
                });

            });
        });

    }

    private void onSendingMessage() {

        String enterMessage = enterMessage__TextArea.getText();
        if (!enterMessage.isEmpty()) {
            String timeCreated = getTimeNow();
            JSONObject message = new JSONObject();
            message.put("prefix", "chat");
            message.put("sender", currentUserID);
            System.out.println("Sender ID" + currentUserID);
            message.put("message", enterMessage);
            message.put("receiver", targetUserID);
            message.put("timeCreated", timeCreated);

            System.out.println("[LOG] >>> send to: " + message.getString("receiver") + message.toString());
            Model.getInstance().getSocketManager().sendMessage(message.toString());
            Platform.runLater(() -> {
                generateMessageBox(enterMessage, timeCreated, "CENTER_RIGHT");
                enterMessage__TextArea.clear();
            });
        }
    }

    private void onSendingFile() {

        Stage stage = (Stage) sendMessage__btn.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(stage);
        fileChooser.setTitle("Open File");
        if (selectedFile != null) {
            try {
                download__btn = new Button();
                generateFileBox(selectedFile.getName(), getTimeNow(), "CENTER_RIGHT");
                saveFile(selectedFile);

                // Sending data to socket
                String targetClientID = Model.getInstance().getTargetUser().getId().get();

                String fileName = selectedFile.getName();
                byte[] fileContent = FileUtils.readFileToByteArray(selectedFile);
                String encodedString = Base64.getEncoder().encodeToString(fileContent);
                String timeCreated = getTimeNow();


                JSONObject fileMessage = new JSONObject();
                fileMessage.put("prefix", "fileTransfer");
                fileMessage.put("sender", currentUserID);
                fileMessage.put("receiver", targetClientID);
                fileMessage.put("fileName", fileName);
                fileMessage.put("data", encodedString);
                fileMessage.put("timeCreated", timeCreated);
                System.out.println("[LOG] >>> send to: " + fileMessage.getString("receiver"));
                Model.getInstance().getSocketManager().sendMessage(fileMessage.toString());

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Sending file error!");
            }
        }
    }

    private void onSendingImage() {
        Stage stage = (Stage) sendMessage__btn.getScene().getWindow();

        FileChooser fileChooser = new FileChooser();
        File selectedImage = fileChooser.showOpenDialog(stage);
        fileChooser.setTitle("Open Image");
        if (selectedImage != null) {
            try {
                download__btn = new Button();
                generateImageBox(selectedImage, getTimeNow(), "CENTER_RIGHT");
                // icon to save the file
                saveFile(selectedImage);
                // Sending data to socket

                String targetClientID = Model.getInstance().getTargetUser().getId().get();
                String fileName = selectedImage.getName();

                byte[] fileContent = FileUtils.readFileToByteArray(selectedImage);
                String encodedString = Base64.getEncoder().encodeToString(fileContent);

                String timeCreated = getTimeNow();

                JSONObject fileMessage = new JSONObject();
                fileMessage.put("prefix", "imageTransfer");
                fileMessage.put("sender", currentUserID);
                fileMessage.put("receiver", targetClientID);
                fileMessage.put("timeCreated", timeCreated);
                fileMessage.put("fileName", fileName);
                fileMessage.put("data", encodedString);

                Model.getInstance().getSocketManager().sendMessage(fileMessage.toString());


            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Sending file error!");
            }
        }
    }

    private void saveFile(File selectedImage) throws IOException {

        Stage stage = (Stage) sendMessage__btn.getScene().getWindow();

        FileInputStream fileInputStream = new FileInputStream(selectedImage);
        byte[] fileData = new byte[(int) selectedImage.length()];
        fileInputStream.read(fileData);
        fileInputStream.close();
        String filePath = selectedImage.getAbsolutePath();
        Path path = Paths.get(filePath);
        System.out.println("[LOG] >>> save to: " + filePath);
        String fileExtension = FilenameUtils.getExtension(path.getFileName().toString());
        System.out.println("[LOG] >>> file extension: " + fileExtension);

        // download image
        download__btn.setOnAction(event -> {
            FileChooser saveFile = new FileChooser();
            saveFile.setInitialFileName("file." + fileExtension);

            File localSaveFile = saveFile.showSaveDialog(stage);
            saveFile.setTitle("Save Image");
            if (localSaveFile != null) {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(localSaveFile);
                    fileOutputStream.write(fileData);
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error at saving image");
                }
            }

        });
    }

    private void generateImageBox(File selectedImage, String timeCreated, String align) throws IOException {

        FileInputStream fileInputStream = new FileInputStream(selectedImage);
        byte[] fileData = new byte[(int) selectedImage.length()];
        fileInputStream.read(fileData);
        fileInputStream.close();

        HBox container = new HBox();
        container.setAlignment(Pos.valueOf(align));
        container.setPadding(new Insets(10, 5, 5, 10));

        FontIcon dl__icon = new FontIcon("fltfal-arrow-download-16");
        dl__icon.setIconSize(15);
        dl__icon.setIconColor(Color.DARKGRAY);
        download__btn.setGraphic(dl__icon);
        download__btn.setStyle("-fx-background-color: transparent; -fx-text-alignment: center; -fx-cursor: hand;");

        download__btn.setPadding(new Insets(0, 15, 0, 0));
        // Download picture function
        Image image = new Image(new ByteArrayInputStream(fileData));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(400);
        imageView.setPreserveRatio(true);

        Rectangle clip = new Rectangle();
        clip.setWidth(400);
        clip.setHeight(200);

        clip.setArcHeight(20.0);
        clip.setArcWidth(20.0);

        imageView.setClip(clip);
        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        WritableImage writableImage = imageView.snapshot(parameters, null);
        imageView.setClip(null);
        imageView.setEffect(new DropShadow(20, Color.DARKGRAY));
        imageView.setImage(writableImage);

        Text timeText = new Text(timeCreated);
        timeText.setStyle("-fx-fill: gray;");

        TextFlow timeCreated_tf = new TextFlow(timeText);
        timeCreated_tf.setPadding(new Insets(15, 0, 0, 15));
        if(align.equals("CENTER_RIGHT")){
            timeCreated_tf.setPadding(new Insets(15, 15, 0, 0));
            container.getChildren().addAll(timeCreated_tf, imageView);
        }else{
            timeCreated_tf.setPadding(new Insets(15, 0, 0, 15));
            container.getChildren().addAll(imageView, timeCreated_tf);
        }
//        container.getChildren().addAll(download__btn, imageView, timeCreated_tf);

        Platform.runLater(() -> {
            messageBoxMap.get(targetUserID).getChildren().addAll(container);
        });


    }

    private void generateFileBox(String enterMessage, String timeCreated, String align) {
        HBox container = new HBox();
        container.setAlignment(Pos.valueOf(align));
        container.setPadding(new Insets(5, 5, 5, 10));

        FontIcon dl__icon = new FontIcon("fltfal-arrow-download-16");
        dl__icon.setIconSize(15);
        dl__icon.setIconColor(Color.DARKGRAY);
//        download__btn.setGraphic(dl__icon);
//        download__btn.setStyle("-fx-background-color: transparent; -fx-text-alignment: center; -fx-cursor: hand;");
//
//        download__btn.setPadding(new Insets(0, 15, 0, 0));
        // Download picture function


        // Text Message
        Text text = new Text(enterMessage);
        TextFlow textFlow = new TextFlow();
        textFlow.setStyle(
                "-fx-color: rgb(239, 242, 255);" +
                        "-fx-font-size: 16px;" +
                        "-fx-background-color: #1B292A;" +
                        "-fx-background-radius: 15px 15px 15px 15px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        text.setFill(Color.color(0.934, 0.925, 0.996));

        Text timeText = new Text(timeCreated);
        timeText.setStyle("-fx-fill: gray;");

        TextFlow timeCreated_tf = new TextFlow(timeText);
        if(align.equals("CENTER_RIGHT")){
            timeCreated_tf.setPadding(new Insets(15, 15, 0, 0));
            container.getChildren().addAll(timeCreated_tf, textFlow);
        }else{
            timeCreated_tf.setPadding(new Insets(15, 0, 0, 15));
            container.getChildren().addAll(textFlow, timeCreated_tf);
        }
        textFlow.getChildren().add(text);
//        container.getChildren().addAll(download__btn, textFlow, timeCreated_tf);
        Platform.runLater(() -> {
            messageBoxMap.get(targetUserID).getChildren().add(container);
        });

    }

    private String getTimeNow() {
        LocalTime localTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return localTime.format(formatter);
    }

    private void generateMessageBox(String enterMessage, String timeCreated, String align) {

        HBox container = new HBox();
        container.setAlignment(Pos.valueOf(align));
        container.setPadding(new Insets(5, 5, 5, 10));

        // Text Message
        Text text = new Text(enterMessage);
        TextFlow textFlow = new TextFlow();
        textFlow.setStyle(
                "-fx-color: rgb(239, 242, 255);" +
                        "-fx-font-size: 16px;" +
                        "-fx-background-color: #1B292A;" +
                        "-fx-background-radius: 15px 15px 15px 15px");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        text.setFill(Color.color(0.934, 0.925, 0.996));

        Text timeText = new Text(timeCreated);
        timeText.setStyle("-fx-fill: gray;");
        TextFlow timeCreated_tf = new TextFlow(timeText);
        textFlow.getChildren().add(text);
        if(align.equals("CENTER_RIGHT")){
            timeCreated_tf.setPadding(new Insets(15, 15, 0, 0));
            container.getChildren().addAll(timeCreated_tf, textFlow);
        }else{
            timeCreated_tf.setPadding(new Insets(15, 0, 0, 15));
            container.getChildren().addAll(textFlow, timeCreated_tf);
        }
        Platform.runLater(() -> {
            messageBoxMap.get(Model.getInstance().getTargetUser().getId().get()).getChildren().add(container);
        });

    }

    public void loadMessage(String serverMessage) {
        JSONObject receiver = new JSONObject(serverMessage);
        switch (receiver.getString("prefix")) {
            case "chat" -> {
                String sender = receiver.getString("sender");
                String message = receiver.getString("message");
                String timeCreated = receiver.getString("timeCreated");
                System.out.println("[LOG] >>> receiver from: " + sender + " message: " + message);
                Platform.runLater(() -> {
                    generateMessageBox(message, timeCreated, "CENTER_LEFT");
                    if (enterMessage__TextArea != null) {
                        enterMessage__TextArea.clear();
                    }
                });
            }
            case "imageTransfer" -> {
                System.out.println("[LOG] >>> Received an image from server !");
                String fileName = receiver.getString("fileName");
                String sender = receiver.getString("sender");
                String encodedString = receiver.getString("data");
                String timeCreated = receiver.getString("timeCreated");

                File imageFile = new File(dotenv.get("RESOURCE_PATH") + "Images/Downloaded/" + fileName);
                System.out.println("check " + imageFile);
                byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
                try {
                    FileUtils.writeByteArrayToFile(imageFile, decodedBytes);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    try {
                        download__btn = new Button();
                        generateImageBox(imageFile, timeCreated, "CENTER_LEFT");
                        saveFile(imageFile);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
            case "fileTransfer" -> {
                String fileName = receiver.getString("fileName");
                String sender = receiver.getString("sender");
                String encodedString = receiver.getString("data");
                String timeCreated = receiver.getString("timeCreated");

                File file = new File(dotenv.get("RESOURCE_PATH") + "Files/Downloaded/" + fileName);
                byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
                try {
                    FileUtils.writeByteArrayToFile(file, decodedBytes);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    download__btn = new Button();
                    generateFileBox(fileName, timeCreated, "CENTER_LEFT");
                    try {
                        saveFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }
}




