package com.example.pqchatclient.Controller.Client.GroupContact;

import com.example.pqchatclient.Model.Client;
import io.github.gleidson28.GNAvatarView;
import javafx.beans.binding.Bindings;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.ResourceBundle;

public class GroupMessagesCellController implements Initializable {
    public Label groupName__lbl;

    public GNAvatarView groupAvatar__img;

    private final Client targetClient;

    public GroupMessagesCellController(Client targetClient){
        this.targetClient = targetClient;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        groupName__lbl.textProperty().bind(Bindings.concat(targetClient.lastNameProperty().concat(" ").concat(targetClient.firstNameProperty())));
        groupAvatar__img.setImage(new Image(getClass().getResourceAsStream(targetClient.imagePathProperty().getValue())));
    }
}
