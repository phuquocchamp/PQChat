package com.example.pqchatclient.Controller.Login;

import com.example.pqchatclient.Model.Model;
import com.example.pqchatclient.View.LoginViewOptions;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.example.pqchatclient.Utils.Encrypt.encodePassword;

public class SignUpController implements Initializable {
    public TextField email__textField;
    public PasswordField password__textField;
    public Button createAccount__btn;
    public Button signIn__btn;
    public TextField validationCode__textField;
    public CheckBox pqTerms__checkBox;
    public Label error__lbl;
    public Button hidePassword__btn;
    public Button sendValidationCode__btn;
    public TextField fullname__textField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(3));
        pauseTransition.setOnFinished(
                event -> error__lbl.setText("")
        );
        pauseTransition.play();

//        sendValidationCode__btn.setOnAction(event -> onValidateCode());
    }

    private void onValidateCode() {
        String email = email__textField.getText();
        String password = encodePassword(password__textField.getText());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("prefix", "validateEmail");
        jsonObject.put("email", email);
        Model.getInstance().getSocketManager().sendMessage(jsonObject.toString());
    }

    private void addListeners() {
        signIn__btn.setOnAction(event -> onSignInView());
        createAccount__btn.setOnAction(event -> onCreateAccount());
    }

    private void onCreateAccount() {
        if (!pqTerms__checkBox.isSelected()) {
            error__lbl.setTextFill(Paint.valueOf("RED"));
            error__lbl.setText("Click the phuquocchamp's Terms & Condition first !");
        } else {
            String fullname = fullname__textField.getText();
            String email = email__textField.getText();
            String password = encodePassword(password__textField.getText());
//            String validationCode = validationCode__textField.getText();

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("prefix", "createAccount");
            jsonObject.put("fullname", fullname);
            jsonObject.put("username", email);
            jsonObject.put("password", password);
//            jsonObject.put("validationCode", validationCode);

            ExecutorService service = Executors.newSingleThreadExecutor();
            service.submit(() -> {
                Model.getInstance().getSocketManager().sendMessage(jsonObject.toString());
            });

            service.submit(() -> {
                String checkCreateAccount = null;
                checkCreateAccount = Model.getInstance().getSocketManager().retrieveMessage();
                JSONObject createdFlag = new JSONObject(checkCreateAccount);
                Platform.runLater(() -> {
                    if (createdFlag.getString("flag").equals("success")) {
                        error__lbl.setTextFill(Paint.valueOf("GREEN"));
                        error__lbl.setText("Created Account Successfully !");
                        onSignInView();
                    } else {
                        error__lbl.setTextFill(Paint.valueOf("RED"));
                        error__lbl.setText("Failed to created account !");
                    }
                });


            });

        }

    }

    private void onSignInView() {
        Model.getInstance().getViewFactory().getLoginSelectedMenuItem().set(LoginViewOptions.SIGNIN);
    }
}
