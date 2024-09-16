package com.example.pqchatclient.View;

import com.example.pqchatclient.Controller.Client.GroupContact.GroupMessagesCellController;
import com.example.pqchatclient.Controller.Client.SingleContact.SingleMessagesCellController;
import com.example.pqchatclient.Model.Client;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;

public class GroupMessagesCellFactory extends ListCell<Client> {
    @Override
    public void updateItem(Client client, boolean empty){
        super.updateItem(client, empty);
        if(empty){
            setText(null);
            setGraphic(null);
        }else{
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Fxml/Client/GroupContact/GroupMessagesCell.fxml"));
            GroupMessagesCellController controller = new GroupMessagesCellController(client);
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
