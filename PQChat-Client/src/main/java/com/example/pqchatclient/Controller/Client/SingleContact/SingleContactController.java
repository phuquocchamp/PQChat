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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
        onUpdateOnlineUsers();
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

    private void onUpdateOnlineUsers() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        // Vòng lặp liên tục để lắng nghe thông điệp từ server
        executorService.submit(() -> {
            while (true) {
                try {
                    String receiverMessage = Model.getInstance().getSocketManager().receiverMessage();
                    JSONObject checkOnlineUser = new JSONObject(receiverMessage);

                    // Kiểm tra xem có thông báo cập nhật người dùng online hay không
                    if (checkOnlineUser.getString("prefix").equals("updateOnlineUsers")) {
                        System.out.println("[LOG] >>> Update Online Users: " + checkOnlineUser);
                        JSONArray onlineUsersArray = checkOnlineUser.getJSONArray("onlineUsers");
                        System.out.println("[LOG] >>> Online Users: " + onlineUsersArray);

                        // Cập nhật danh sách online trong luồng JavaFX
                        Platform.runLater(() -> {
                            Model.getInstance().getOnlineUsers().clear();
                            for (int i = 0; i < onlineUsersArray.length(); i++) {
                                JSONObject userObject = onlineUsersArray.getJSONObject(i);
                                String clientID = userObject.getString("clientID");
                                String fullName = userObject.getString("fullname");
                                String avatarPath = userObject.optString("avatar", "Images/avatar-default.jpg");

                                // Tạo đối tượng User và thêm vào danh sách nếu chưa có
                                User newUser = new User(clientID, fullName, avatarPath);
                                if (!isUserAlreadyOnline(newUser)) {
                                    Model.getInstance().getOnlineUsers().add(newUser);
                                }
                            }
                            System.out.println("[LOG] >>> Online Users List Updated.");
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private boolean isUserAlreadyOnline(User user) {
        for (User onlineUser : Model.getInstance().getOnlineUsers()) {
            if (onlineUser.getId().equals(user.getId())) {
                return true;
            }
        }
        return false;
    }
}
