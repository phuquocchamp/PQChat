package com.example.pqchatclient.Controller.Client.SingleContact;

import com.example.pqchatclient.Model.User;
import io.github.gleidson28.GNAvatarView;
import javafx.beans.binding.Bindings;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class SingleMessagesCellController implements Initializable {
 
    public Label timeCreated__lbl;
    public GNAvatarView clientAvatar__img;
    public Label clientName__lbl;
    public Label lastMessages__lbl;
    private final User targetUser;

    public SingleMessagesCellController(User targetClient){
        this.targetUser = targetClient;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try{
            clientName__lbl.textProperty().bind(targetUser.getFullName());
//            clientAvatar__img.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream(targetUser.getAvatarPath().getValue()))));
        }catch (Exception e){
            System.out.println("[LOG] >>> Error at SingleMessagesCellController");
        }

    }
}
