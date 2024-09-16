package com.example.pqchatclient.Controller.Login;

import com.example.pqchatclient.Model.Model;
import com.example.pqchatclient.Model.User;
import com.example.pqchatclient.Utils.Encrypt;
import com.example.pqchatclient.View.LoginViewOptions;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SignInController implements Initializable {
    public TextField email__textField;
    public TextField password__textField;
    public CheckBox remember__checkBox;
    public Button forgotPassword__btn;
    public Button login__btn;
    public Button register__btn;
    public Button hidePassword__btn;
    public Label error__lbl;
    public PasswordField password__passwordField;


    int clickCount = 0;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
    }

    private void addListeners() {
        register__btn.setOnAction(event -> onSignUpView());
        login__btn.setOnAction(event -> {
            try {
                onLogin();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        forgotPassword__btn.setOnAction(event -> onForgotPassword());
        hidePassword__btn.setOnAction(event -> onShowPassword());
        error__lbl.setText("");
    }


    private void onLogin() throws IOException {
        Stage stage = (Stage) error__lbl.getScene().getWindow();
        // Security to validate account.
        String usernameTF = "";
        String passwordTF = "";
        try {
            usernameTF = email__textField.getText();
        } catch (Exception e) {
            error__lbl.setText("Please Input Email");
        }
        try {
            passwordTF = password__passwordField.getText();
        } catch (Exception e) {
            error__lbl.setText("Please Input Password");
        }


        // ---------------------- Verify account from server -------------------------//
        String username = usernameTF;
        String password = Encrypt.encodePassword(passwordTF);
        JSONObject userInfo = new JSONObject();

        userInfo.put("prefix", "evaluateAccount");
        userInfo.put("username", username);
        userInfo.put("password", password);

        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> Model.getInstance().getSocketManager().sendMessage(userInfo.toString()));
        String credential = Model.getInstance().getSocketManager().receiverMessage();
        if (!credential.isEmpty()) {
            JSONObject receiver = new JSONObject(credential);
            System.out.println("[LOG] >>> Receiver: " + receiver);
            if (receiver.getString("prefix").equals("evaluateAccount")) {
                if (receiver.getString("flag").equals("success")) {
                    String clientID = receiver.getString("clientID");
                    String fullName = receiver.getString("fullname");
                    // String avatar = receiver.getString("avatar");

                    String defaultAvatarPath = "Images/avatar-default.jpg";

                    // Model.getInstance().setCurrentClient(clientID);

                    User user = new User(clientID, fullName, defaultAvatarPath);
                    Model.getInstance().setCurrentUser(user);
//                    Model.getInstance().getOnlineUsers().add(user);

                    Model.getInstance().getViewFactory().showClientWindow();
                    Model.getInstance().getViewFactory().closeStage(stage);
                } else {
                    email__textField.setText("");
                    password__textField.setText("");
                    error__lbl.setText("No Such Login Credential!");
                }
            }else{
                System.out.println("[LOG] >>> Other Receiver: " + receiver);
            }
        }

    }

    private void onSignUpView() {
        Model.getInstance().getViewFactory().getLoginSelectedMenuItem().set(LoginViewOptions.SIGNUP);
    }

    private void onShowPassword() {
        clickCount++;
        if (clickCount % 2 == 1) {
            password__textField.textProperty().bindBidirectional(password__passwordField.textProperty());
            password__passwordField.setVisible(false);
        } else {
            password__passwordField.setVisible(true);
        }

    }

    private void onForgotPassword() {
        Stage stage = (Stage) error__lbl.getScene().getWindow();
        Model.getInstance().getViewFactory().showForgotPasswordWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
    }
}
