package com.example.pqchatclient.Controller.Client.SingleContact;

import com.example.pqchatclient.Model.User;
import io.github.gleidson28.GNAvatarView;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SingleOnlineCellController implements Initializable {
    public GNAvatarView clientAvatar_img;
    public Label clientName__lbl;
    private User targetUser;

    public SingleOnlineCellController(User targetClient) {
        this.targetUser = targetClient;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        clientAvatar_img.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(targetUser.getAvatarPath().getValue()))));
        clientName__lbl.textProperty().bind(targetUser.getFullName());
    }
}