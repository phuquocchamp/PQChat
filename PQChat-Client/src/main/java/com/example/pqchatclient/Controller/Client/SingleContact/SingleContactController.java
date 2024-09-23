package com.example.pqchatclient.Controller.Client.SingleContact;

import com.example.pqchatclient.Model.Model;
import com.example.pqchatclient.Model.User;
import com.example.pqchatclient.View.SingleMessagesCellFactory;
import com.example.pqchatclient.View.SingleOnlineCellFactory;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.kordamp.ikonli.javafx.FontIcon;
import org.w3c.dom.Text;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.*;

public class SingleContactController implements Initializable {

    public Label welcome_label;
    User selectedUser;
    ObservableList<User> onlineUsers = FXCollections.observableArrayList();
    ObservableList<User> chatHistoryUsers = FXCollections.observableArrayList();
    public FontIcon collapse_btn;
    public ListView<User> userOnline__listView;
    public ListView<User> userChat__listView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        welcome_label.setText("Hi, " + Model.getInstance().getCurrentUser().getFullName().get());
//        try {
//            onUpdateOnlineUsers();
//        } catch (ExecutionException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        onlineUsers = Model.getInstance().getOnlineUsers();
        userOnline__listView.setItems(onlineUsers);
        userOnline__listView.setCellFactory(event -> new SingleOnlineCellFactory());

        // Messages ListView
        userChat__listView.setItems(onlineUsers);
        userChat__listView.setCellFactory(event -> new SingleMessagesCellFactory());

        // Get Selected Client
        userChat__listView.setOnMouseClicked(mouseEvent -> {
            selectedUser = userChat__listView.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                Model.getInstance().getTargetUser().getId().bind(selectedUser.getId());
                Model.getInstance().getTargetUser().getFullName().bind(selectedUser.getFullName());
                Model.getInstance().getTargetUser().getAvatarPath().bind(selectedUser.getAvatarPath());
            }
        });

    }

//    public static void onUpdateOnlineUsers() throws ExecutionException, InterruptedException {
//        ExecutorService service = Executors.newSingleThreadExecutor();
//            Future<String> mr = service.submit(() -> Model.getInstance().getSocketManager().retrieveMessageWithTimeout(5, TimeUnit.SECONDS));
//            if(mr.get() != null) {
//                JSONObject checkOnlineUser = new JSONObject(mr.get());
//                if (checkOnlineUser.getString("prefix").equals("updateOnlineUsers")) {
//                    System.out.println("[LOG] >>> Update Online Users: " + checkOnlineUser);
//                    JSONArray onlineUsersArray = checkOnlineUser.getJSONArray("onlineUsers");
//                    System.out.println("[LOG] >>> Online Users: " + onlineUsersArray);
//
//                    Platform.runLater(() -> {
//                        Model.getInstance().getOnlineUsers().clear();
//                        for (int i = 0; i < onlineUsersArray.length(); i++) {
//                            JSONObject userObject = onlineUsersArray.getJSONObject(i);
//                            String clientID = userObject.getString("clientID");
//                            String fullName = userObject.getString("fullname");
//                            String avatarPath = userObject.optString("avatar", "Images/avatar-default.jpg");
//                            User newUser = new User(clientID, fullName, avatarPath);
//                            if (!isUserAlreadyOnline(newUser)) {
//                                Model.getInstance().getOnlineUsers().add(newUser);
//                            }
//                        }
//                    });
//                }
//            }
//
//    }



}
