package com.example.pqchatclient.View;

import com.example.pqchatclient.Controller.Client.SingleContact.SingleMessagesCellController;
import com.example.pqchatclient.Model.Client;
import com.example.pqchatclient.Model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;

public class SingleMessagesCellFactory extends ListCell<User> {
    @Override
    public void updateItem(User user, boolean empty){
        super.updateItem(user, empty);
        if(empty){
            setText(null);
            setGraphic(null);
        }else{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Client/SingleContact/SingleMessagesCell.fxml"));
            SingleMessagesCellController controller = new SingleMessagesCellController(user);
            fxmlLoader.setController(controller);
            setText(null);
            try {
                setGraphic(fxmlLoader.load());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
