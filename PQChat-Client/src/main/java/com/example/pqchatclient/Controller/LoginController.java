package com.example.pqchatclient.Controller;

import com.example.pqchatclient.Model.Model;
import com.example.pqchatclient.View.LoginViewOptions;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public BorderPane login__parent;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Model.getInstance().getViewFactory().getLoginSelectedMenuItem().addListener(((observableValue, oldValue, newValue) -> {
            if (newValue == LoginViewOptions.SIGNIN){
                try {
                    login__parent.setRight(Model.getInstance().getViewFactory().getSignInView());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else if (newValue == LoginViewOptions.SIGNUP) {
                try {
                    login__parent.setRight(Model.getInstance().getViewFactory().getSignUpView());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }));
    }
}
