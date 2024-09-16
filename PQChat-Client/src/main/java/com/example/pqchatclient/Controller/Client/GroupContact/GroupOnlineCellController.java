package com.example.pqchatclient.Controller.Client.GroupContact;

import com.example.pqchatclient.Model.Client;
import io.github.gleidson28.GNAvatarView;
import javafx.fxml.Initializable;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.ResourceBundle;

public class GroupOnlineCellController implements Initializable {
    public GNAvatarView clientAvatar_img;
    public Label clientName__lbl;

    public Button addClient__btn;

    private final Client targetClient;

    public GroupOnlineCellController(Client targetClient) {
        this.targetClient = targetClient;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clientAvatar_img.setImage(new Image(getClass().getResourceAsStream(targetClient.imagePathProperty().getValue())));
        clientName__lbl.textProperty().bind(targetClient.firstNameProperty());
//        addClient__btn.setOnAction(event -> addClientToGroup());
    }

    private void addClientToGroup() {
    }
}
