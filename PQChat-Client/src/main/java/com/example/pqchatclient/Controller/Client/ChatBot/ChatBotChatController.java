//package com.example.pqchatclient.Controller.Client.ChatBot;
//
//import com.example.pqchatclient.Model.Model;
//import com.example.pqchatclient.Utils.ChatGPTAPI;
//import io.github.gleidson28.GNAvatarView;
//import javafx.application.Platform;
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.fxml.Initializable;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.SnapshotParameters;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
//import javafx.scene.control.ScrollPane;
//import javafx.scene.control.TextArea;
//import javafx.scene.effect.DropShadow;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.image.WritableImage;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.scene.paint.Color;
//import javafx.scene.shape.Rectangle;
//import javafx.scene.text.Text;
//import javafx.scene.text.TextFlow;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//import org.apache.commons.io.FileUtils;
//import org.apache.commons.io.FilenameUtils;
//import org.kordamp.ikonli.javafx.FontIcon;
//
//import java.io.*;
//import java.net.URL;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Base64;
//import java.util.ResourceBundle;
//
//public class ChatBotChatController implements Initializable {
//    public GNAvatarView senderAvatar__img;
//    public Label senderName__lbl;
//    public Label status;
//    public TextArea enterMessage__TextArea;
//    public Button fileSend__btn;
//    public Button imageSend__btn;
//    public Button emojiSend__btn;
//    public Button sendMessage__btn;
//    public ScrollPane messageContainer__scrollPane;
//    VBox messageBox__vBox;
//    public Button download__btn;
//    String currentClientID = Model.getInstance().getCurrentClient().clientIDProperty().get();
//    String projectPath = "/home/phuquocchamp/Coding/BE/Java/JavaFX/PQChat-Client/src/main/resources/Files/";
//    String imageFilePath = "/Images/Profiles";
//    String profileFilePath = "/home/phuquocchamp/Coding/BE/Java/JavaFX/PQChat-Client/src/main/resources/Images/Profiles";
//
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//        messageBox__vBox = new VBox();
//        messageBox__vBox.setFillWidth(true);
//        messageBox__vBox.setPadding(new Insets(10, 0, 0, 10));
//
//        messageContainer__scrollPane.setContent(messageBox__vBox);
//
//        Platform.runLater(() -> {
////            serverResponse();
//            sendMessage__btn.setOnAction(event -> onSendingMessage());
//            messageBox__vBox.heightProperty().addListener(new ChangeListener<Number>() {
//                @Override
//                public void changed(ObservableValue<? extends Number> observableValue, Number oldValue, Number newValue) {
//                    messageContainer__scrollPane.setVvalue((Double) newValue);
//                }
//            });
//        });
//    }
//
//    private void onSendingFile() {
//        Stage stage = (Stage) sendMessage__btn.getScene().getWindow();
//
//        FileChooser fileChooser = new FileChooser();
//        File selectedFile = fileChooser.showOpenDialog(stage);
//        fileChooser.setTitle("Open File");
//        if (selectedFile != null) {
//            try {
//                download__btn = new Button();
//                generateFileBox(selectedFile.getName(), getTimeNow(), "CENTER_RIGHT");
//                saveFile(selectedFile);
//
//                String fileName = selectedFile.getName();
//                byte[] fileContent = FileUtils.readFileToByteArray(selectedFile);
//                String encodedString = Base64.getEncoder().encodeToString(fileContent);
//                String timeCreated = getTimeNow();
//
//                String messageForm = "fileGroupTransfer_" + currentClientID + "_" + fileName + "_" + encodedString + "_" + timeCreated;
//                System.out.println("[Client log] --> Client: " + currentClientID + " sent a file to group.");
//                Model.getInstance().getSocketManager().sendMessage(messageForm);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.out.println("Sending file error!");
//            }
//
//        }
//    }
//
//    private void onSendingEmoji() {
//    }
//
//    private void onSendingImage() {
//        Stage stage = (Stage) sendMessage__btn.getScene().getWindow();
//
//        FileChooser fileChooser = new FileChooser();
//        File selectedImage = fileChooser.showOpenDialog(stage);
//        fileChooser.setTitle("Open Image");
//        if (selectedImage != null) {
//            try {
//                download__btn = new Button();
//                generateImageBox(selectedImage, getTimeNow(), "CENTER_RIGHT");
//                // icon to save the file
//                saveFile(selectedImage);
//                String fileName = selectedImage.getName();
//                byte[] fileContent = FileUtils.readFileToByteArray(selectedImage);
//                String encodedString = Base64.getEncoder().encodeToString(fileContent);
//
//                String timeCreated = getTimeNow();
//                String messageForm = "imageGroupTransfer_" + currentClientID + "_" + fileName + "_" + encodedString + "_" + timeCreated;
//                System.out.println("[Client log] --> Client: " + currentClientID + " sent an image to group.");
//                Model.getInstance().getSocketManager().sendMessage(messageForm);
//            } catch (IOException e) {
//                e.printStackTrace();
//                System.out.println("Sending file error!");
//            }
//        }
//    }
//
//    private void onSendingMessage() {
//        String enterMessage = enterMessage__TextArea.getText();
//        if (!enterMessage.isEmpty()) {
//            String timeCreated = getTimeNow();
//            String messageForm = "chatBot_" + currentClientID + "_" + enterMessage + "_" + timeCreated;
//            System.out.println("[Client log] --> Client: " + currentClientID + " sent a message to chat-bot");
////            Model.getInstance().getSocketManager().sendMessage(messageForm);
//            // Show on GUI
//            Platform.runLater(() -> {
//                generateMessageBox(enterMessage, timeCreated, "CENTER_RIGHT");
//            });
//
//            String chatBotResponse = ChatGPTAPI.chatGPT(enterMessage);
//
//            Platform.runLater(() -> {
//                generateMessageBox(chatBotResponse, getTimeNow(), "CENTER_LEFT");
//            });
//        }
//    }
//
//    private void serverResponse() {
//        Thread clientThread = new Thread(() -> {
//            try {
//                while (true) {
//                    String streamMessage = Model.getInstance().getSocketManager().receiverMessage();
//                    if (streamMessage == null) {
//                        break;
//                    }
//                    String[] messageSplit = streamMessage.split("_");
//
//                    System.out.println(streamMessage);
//
//                    Platform.runLater(() -> {
//                        if (messageSplit[0].equals("chatBot")) {
//                            // Show on GUI
//                            String timeCreated = messageSplit[2];
//                            String messageContent = messageSplit[1];
//                            Platform.runLater(() -> {
//                                generateMessageBox(messageContent, timeCreated, "CENTER_LEFT");
//                            });
//
//                        }
//                    });
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
//        clientThread.setDaemon(true);
//        clientThread.start();
//    }
//
//
//    private void generateMessageBox(String enterMessage, String timeCreated, String align) {
//        HBox container = new HBox();
//        container.setAlignment(Pos.valueOf(align));
//        container.setPadding(new Insets(5, 5, 5, 10));
////        container.set
//        // Text Message
//        Text text = new Text(enterMessage);
//        TextFlow textFlow = new TextFlow();
//        textFlow.setStyle(
//                "-fx-color: rgb(239, 242, 255);" +
//                        "-fx-font-size: 16px;" +
//                        "-fx-background-color: #1B292A;" +
//                        "-fx-background-radius: 15px 15px 15px 15px");
//        textFlow.setPadding(new Insets(5, 10, 5, 10));
//        text.setFill(Color.color(0.934, 0.925, 0.996));
//
//        Text timeText = new Text(timeCreated);
//        timeText.setStyle("-fx-fill: gray;");
//
//        TextFlow timeCreated_tf = new TextFlow(timeText);
//        timeCreated_tf.setPadding(new Insets(15, 0, 0, 15));
//
//        textFlow.getChildren().add(text);
//        container.getChildren().addAll(textFlow, timeCreated_tf);
//
//        messageBox__vBox.getChildren().add(container);
//        enterMessage__TextArea.setText("");
//    }
//
//    private void generateImageBox(File selectedImage, String timeCreated, String align) throws IOException {
//
//        FileInputStream fileInputStream = new FileInputStream(selectedImage);
//        byte[] fileData = new byte[(int) selectedImage.length()];
//        fileInputStream.read(fileData);
//        fileInputStream.close();
//
//        HBox container = new HBox();
//        container.setAlignment(Pos.valueOf(align));
//        container.setPadding(new Insets(10, 5, 5, 10));
//
//        FontIcon dl__icon = new FontIcon("fltfal-arrow-download-16");
//        dl__icon.setIconSize(15);
//        dl__icon.setIconColor(Color.DARKGRAY);
//        download__btn.setGraphic(dl__icon);
//        download__btn.setStyle("-fx-background-color: transparent; -fx-text-alignment: center; -fx-cursor: hand;");
//
//        download__btn.setPadding(new Insets(0, 15, 0, 0));
//        // Download picture function
//
//
//        Image image = new Image(new ByteArrayInputStream(fileData));
//        ImageView imageView = new ImageView(image);
//        imageView.setFitWidth(400);
//        imageView.setPreserveRatio(true);
//
//        Rectangle clip = new Rectangle();
//        clip.setWidth(400);
//        clip.setHeight(200);
//
//        clip.setArcHeight(20.0);
//        clip.setArcWidth(20.0);
//
//        imageView.setClip(clip);
//        SnapshotParameters parameters = new SnapshotParameters();
//        parameters.setFill(Color.TRANSPARENT);
//        WritableImage writableImage = imageView.snapshot(parameters, null);
//        imageView.setClip(null);
//        imageView.setEffect(new DropShadow(20, Color.DARKGRAY));
//        imageView.setImage(writableImage);
//
//        Text timeText = new Text(timeCreated);
//        timeText.setStyle("-fx-fill: gray;");
//
//        TextFlow timeCreated_tf = new TextFlow(timeText);
//        timeCreated_tf.setPadding(new Insets(15, 0, 0, 15));
//        container.getChildren().addAll(download__btn, imageView, timeCreated_tf);
//        messageBox__vBox.getChildren().addAll(container);
//    }
//
//    private void generateFileBox(String enterMessage, String timeCreated, String align) {
//        HBox container = new HBox();
//        container.setAlignment(Pos.valueOf(align));
//        container.setPadding(new Insets(5, 5, 5, 10));
//
//        FontIcon dl__icon = new FontIcon("fltfal-arrow-download-16");
//        dl__icon.setIconSize(15);
//        dl__icon.setIconColor(Color.DARKGRAY);
//        download__btn.setGraphic(dl__icon);
//        download__btn.setStyle("-fx-background-color: transparent; -fx-text-alignment: center; -fx-cursor: hand;");
//        download__btn.setPadding(new Insets(0, 15, 0, 0));
//        // Text Message
//        Text text = new Text(enterMessage);
//        TextFlow textFlow = new TextFlow();
//        textFlow.setStyle(
//                "-fx-color: rgb(239, 242, 255);" +
//                        "-fx-font-size: 16px;" +
//                        "-fx-background-color: #1B292A;" +
//                        "-fx-background-radius: 15px 15px 15px 15px");
//        textFlow.setPadding(new Insets(5, 10, 5, 10));
//        text.setFill(Color.color(0.934, 0.925, 0.996));
//
//        Text timeText = new Text(timeCreated);
//        timeText.setStyle("-fx-fill: gray;");
//
//        TextFlow timeCreated_tf = new TextFlow(timeText);
//        timeCreated_tf.setPadding(new Insets(15, 0, 0, 15));
//
//        textFlow.getChildren().add(text);
//        container.getChildren().addAll(download__btn, textFlow, timeCreated_tf);
//        messageBox__vBox.getChildren().add(container);
//        enterMessage__TextArea.setText("");
//    }
//
//    private void saveFile(File selectedImage) throws IOException {
//        Stage stage = (Stage) sendMessage__btn.getScene().getWindow();
//
//        FileInputStream fileInputStream = new FileInputStream(selectedImage);
//        byte[] fileData = new byte[(int) selectedImage.length()];
//        fileInputStream.read(fileData);
//        fileInputStream.close();
//
//        // Lấy đuôi file
//        String filePath = selectedImage.getAbsolutePath();
//        Path path = Paths.get(filePath);
//        String fileExtension = FilenameUtils.getExtension(path.getFileName().toString());
//
//
//        // download image
//        download__btn.setOnAction(event -> {
//            FileChooser saveFile = new FileChooser();
//            saveFile.setInitialFileName("file." + fileExtension);
//
//            File localSaveFile = saveFile.showSaveDialog(stage);
//            saveFile.setTitle("Save Image");
//            if (localSaveFile != null) {
//                try {
//                    FileOutputStream fileOutputStream = new FileOutputStream(localSaveFile);
//                    fileOutputStream.write(fileData);
//                    fileOutputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    System.out.println("Error at saving image");
//                }
//            }
//
//        });
//
//    }
//
//    private String getTimeNow() {
//        LocalTime localTime = LocalTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
//        return localTime.format(formatter);
//    }
//
//}
