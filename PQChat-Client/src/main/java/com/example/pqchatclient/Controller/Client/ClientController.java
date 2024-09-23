package com.example.pqchatclient.Controller.Client;

import com.example.pqchatclient.Model.Model;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    public BorderPane client__parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().getClientSelectedMenuItem().addListener((observableValue, oldValue, newValue) ->{
            switch (newValue){
                case SINGLECHAT -> {
                    client__parent.setCenter(Model.getInstance().getViewFactory().getSingleContactView());
                    client__parent.setRight(Model.getInstance().getViewFactory().getSingleChatView());
                }
//                case GROUPCHAT -> {
//                    client__parent.setCenter(Model.getInstance().getViewFactory().getGroupContactView());
//                    client__parent.setRight(Model.getInstance().getViewFactory().getGroupChatView());
//                }
                case CHATBOT -> {
                    client__parent.setCenter(Model.getInstance().getViewFactory().getChatBotContactView());
                    client__parent.setRight(Model.getInstance().getViewFactory().getChatBotChatView());
                }
                default -> {
                    client__parent.setCenter(Model.getInstance().getViewFactory().getSingleContactView());
                    client__parent.setRight(Model.getInstance().getViewFactory().getSingleChatView());
                }
            }
        });
    }
}
