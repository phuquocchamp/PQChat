package com.example.pqchatclient.Controller.Client.GroupContact;

import com.example.pqchatclient.Model.Client;
import com.example.pqchatclient.Model.Model;
import com.example.pqchatclient.View.GroupMessagesCellFactory;
import com.example.pqchatclient.View.GroupOnlineCellFactory;
import com.example.pqchatclient.View.SingleOnlineCellFactory;
import io.github.gleidson28.GNAvatarView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ResourceBundle;

public class GroupContactController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

//    public FontIcon collapse_btn;
//
//    public ListView<Client> userOnline__listView;
//    public ListView<Client> groupChat__listView;
//    ObservableList<Client> onlineClientList = FXCollections.observableArrayList();
//    ObservableList<Client> groupChat = FXCollections.observableArrayList();
//
//
//    @Override
//    public void initialize(URL url, ResourceBundle resourceBundle) {
//
//        onlineClientList = Model.getInstance().getOnlineClientList();
//        userOnline__listView.setItems(onlineClientList);
//        userOnline__listView.setCellFactory(event -> new SingleOnlineCellFactory());
//
//        groupChat.add(new Client("", "", "Group", "VKU-ers", "/Images/Profiles/vku.png"));
//
//        // Group Chat View
//        groupChat__listView.setItems(groupChat);
//        groupChat__listView.setCellFactory(event -> new GroupMessagesCellFactory());
//    }
}
