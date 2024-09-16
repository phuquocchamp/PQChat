package com.example.pqchatclient.Controller.Client.SingleContact;

import com.example.pqchatclient.Model.Model;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    public Button download__btn;
    String currentUserID = Model.getInstance().getCurrentUser().getId().get();

    private String targetUserID;
    private Map<String, VBox> messageBoxMap = new HashMap<>();

    String projectPath = "/home/phuquocchamp/Coding/BE/Java/JavaFX/PQChat-Client/src/main/resources/Files/";
    String imageFilePath = "/Images/Profiles";
    String profileFilePath = "/home/phuquocchamp/Coding/BE/Java/JavaFX/PQChat-Client/src/main/resources/Images/Profiles";


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

                serverResponse();
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

            System.out.println("[LOG] >>> send to: "+ message.getString("receiver")+ message.toString());
            Model.getInstance().getSocketManager().sendMessage(message.toString());
            Platform.runLater(() -> generateMessageBox( enterMessage, timeCreated, "CENTER_RIGHT") );
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
        // Lấy đuôi file
        String filePath = selectedImage.getAbsolutePath();
        Path path = Paths.get(filePath);
        String fileExtension = FilenameUtils.getExtension(path.getFileName().toString());

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


        container.getChildren().addAll(download__btn, imageView, timeCreated_tf);
        messageBoxMap.get(targetUserID).getChildren().addAll(container);

    }

    private void generateFileBox(String enterMessage, String timeCreated, String align) {
        HBox container = new HBox();
        container.setAlignment(Pos.valueOf(align));
        container.setPadding(new Insets(5, 5, 5, 10));

        FontIcon dl__icon = new FontIcon("fltfal-arrow-download-16");
        dl__icon.setIconSize(15);
        dl__icon.setIconColor(Color.DARKGRAY);
        download__btn.setGraphic(dl__icon);
        download__btn.setStyle("-fx-background-color: transparent; -fx-text-alignment: center; -fx-cursor: hand;");

        download__btn.setPadding(new Insets(0, 15, 0, 0));
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
        timeCreated_tf.setPadding(new Insets(15, 0, 0, 15));

        textFlow.getChildren().add(text);
        container.getChildren().addAll(download__btn, textFlow, timeCreated_tf);
        messageBoxMap.get(targetUserID).getChildren().add(container);
        enterMessage__TextArea.setText("");
    }

    private String getTimeNow() {
        LocalTime localTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return localTime.format(formatter);
    }

    // Generate MessageBox
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
        timeCreated_tf.setPadding(new Insets(15, 0, 0, 15));
        textFlow.getChildren().add(text);
        container.getChildren().addAll(textFlow, timeCreated_tf);

        messageBoxMap.get(Model.getInstance().getTargetUser().getId().get()).getChildren().add(container);
        enterMessage__TextArea.setText("");
    }

    private void serverResponse() {


        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> {
            while (true) {
                try {
                    String receiverMessage = Model.getInstance().getSocketManager().receiverMessage();
                    JSONObject receiver = new JSONObject(receiverMessage);


                    switch (receiver.getString("prefix")){
                        case "chat" -> {
                            String sender = receiver.getString("sender");
                            String message = receiver.getString("message");
                            String timeCreated = receiver.getString("timeCreated");
                            System.out.println("[LOG] >>> receiver from: " + sender + " message: " + message);
                            Platform.runLater(() -> generateMessageBox(message, timeCreated, "CENTER_LEFT"));
                        }
                        case "imageTransfer" -> {
                            System.out.println("[Log] --> Received an image from server !");
                            String fileName = receiver.getString("fileName");
                            String sender = receiver.getString("sender");
                            String encodedString = receiver.getString("data");
                            String timeCreated = receiver.getString("timeCreated");

                            File imageFile = new File( getClass().getResource("/Images/") + fileName);
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

                            File file = new File(getClass().getResource("/Files/") + fileName);
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

                } catch (Exception e) {
                    System.out.println("[LOG] >>> Server Response Error");
                }
            }
        });
    }



//        try {
//            Thread clientThread = new Thread(() -> {
//                try {
//                    while (true) {
//                        String streamMessage = Model.getInstance().getSocketManager().receiverMessage();
//                        if (streamMessage == null) {
//                            break;
//                        }
//                        String[] messageSplit = streamMessage.split("_");
//
//                        Platform.runLater(() -> {
//                            if (messageSplit[0].equals("singleChat")) {
//                                // Show on GUI
//                                String timeCreated = messageSplit[4];
//                                String receiver = messageSplit[2];
//                                String sender = messageSplit[1];
//                                String messageContent = messageSplit[3];
//
//                                if (receiver.equals(Model.getInstance().getCurrentClient().clientIDProperty().get())) {
//                                    System.out.println(streamMessage);
//
//                                    Platform.runLater(() -> {
//                                        generateMessageBox(messageSplit[3], timeCreated, "CENTER_LEFT");
//                                    });
//                                }
//
//                            } else if (messageSplit[0].equals("removeClient")) {
////                                for (Client client : Model.getInstance().getOnlineClientList()) {
////                                    if (client.clientIDProperty().get().equals(messageSplit[1])) {
////                                        Model.getInstance().getOnlineClientList().remove(client);
////                                        break;
////                                    }
////                                }
////                                System.out.println("[Client log] --> Client: " + currentClientID + " exited.");
//
//                            } else if (messageSplit[0].equals("onlineClient")) {
////                                Platform.runLater(() -> {
////                                    System.out.println("[Log] --> updated online client from server !");
////
////                                    String encodedString = messageSplit[4];
////                                    byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
////                                    String imagePath = profileFilePath + "/" + messageSplit[1] + "." + messageSplit[5];
////                                    File imageFile = new File(imagePath);
////                                    try {
////                                        FileUtils.writeByteArrayToFile(imageFile, decodedBytes);
////                                    } catch (IOException e) {
////                                        throw new RuntimeException(e);
////                                    }
////                                    String profilePath = imageFilePath + "/" + messageSplit[1] + "." + messageSplit[5];
////
////                                    Client onlineClient = new Client(messageSplit[1], messageSplit[1], messageSplit[2], messageSplit[3], profilePath);
////                                    Model.getInstance().getOnlineClientList().add(onlineClient);
//                                });
//
//                            } else if (messageSplit[0].equals("fileTransfer")) {
////                                String receiver = messageSplit[2];
////                                String fileName = messageSplit[3];
////                                String encodedString = messageSplit[4];
////                                String timeCreated = messageSplit[5];
////
////                                if (receiver.equals(Model.getInstance().getCurrentClient().clientIDProperty().get())) {
////                                    System.out.println("[Log] --> Received file from server !");
////
////
////                                    File file = new File(projectPath + fileName);
////                                    byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
////                                    try {
////                                        FileUtils.writeByteArrayToFile(file, decodedBytes);
////                                    } catch (IOException e) {
////                                        throw new RuntimeException(e);
////                                    }
////                                    Platform.runLater(() -> {
////                                        download__btn = new Button();
////                                        generateFileBox(fileName, timeCreated, "CENTER_LEFT");
////                                        try {
////                                            saveFile(file);
////                                        } catch (IOException e) {
////                                            throw new RuntimeException(e);
////                                        }
////                                    });
////                                }
//
//                            } else if (messageSplit[0].equals("imageTransfer")) {
////                                String receiver = messageSplit[2];
////                                String fileName = messageSplit[3];
////                                String encodedString = messageSplit[4];
////                                String timeCreated = messageSplit[5];
////
////                                if (receiver.equals(Model.getInstance().getCurrentClient().clientIDProperty().get())) {
////                                    System.out.println("[Log] --> Received an image from server !");
////
////                                    File imageFile = new File(projectPath + fileName);
////                                    byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
////                                    try {
////                                        FileUtils.writeByteArrayToFile(imageFile, decodedBytes);
////                                    } catch (IOException e) {
////                                        throw new RuntimeException(e);
////                                    }
////                                    Platform.runLater(() -> {
////                                        try {
////                                            download__btn = new Button();
////                                            generateImageBox(imageFile, timeCreated, "CENTER_LEFT");
////                                            saveFile(imageFile);
////                                        } catch (IOException e) {
////                                            throw new RuntimeException(e);
////                                        }
////                                    });
////                                }
//                            }
//                        });
//
//                    }
//                } catch (Exception e) {
//                    System.out.println("Client Receive Stream Error!");
//                }
//            });
//
//            clientThread.setDaemon(true); // Set as daemon thread to automatically terminate with the main thread
//            clientThread.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }




}




