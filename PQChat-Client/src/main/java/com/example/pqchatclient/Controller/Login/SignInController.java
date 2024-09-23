package com.example.pqchatclient.Controller.Login;

import com.example.pqchatclient.Model.Model;
import com.example.pqchatclient.Model.User;
import com.example.pqchatclient.Utils.Encrypt;
import com.example.pqchatclient.View.LoginViewOptions;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.*;

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
            } catch (IOException | ExecutionException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        forgotPassword__btn.setOnAction(event -> onForgotPassword());
        hidePassword__btn.setOnAction(event -> onShowPassword());
        error__lbl.setText("");
    }


    private void onLogin() throws IOException, ExecutionException, InterruptedException {
        Stage stage = (Stage) error__lbl.getScene().getWindow();
        String usernameTF = email__textField.getText();
        String passwordTF = password__passwordField.getText();

        // ---------------------- Verify account from server -------------------------//
        String username = usernameTF;
        String password = Encrypt.encodePassword(passwordTF);
        JSONObject userInfo = new JSONObject();

        userInfo.put("prefix", "evaluateAccount");
        userInfo.put("username", username);
        userInfo.put("password", password);

        // Gửi thông tin đăng nhập lên server
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(() -> Model.getInstance().getSocketManager().sendMessage(userInfo.toString()));


        boolean messageProcessed = false;
        while (!messageProcessed) {
            // Lấy phản hồi từ server
            Future<String> mr = service.submit(() -> Model.getInstance().getSocketManager().retrieveMessageWithTimeout(5, TimeUnit.SECONDS));
            System.out.println("[LOG] >> mr: " + mr.get());
            if (mr.get() != null) {
                JSONObject receiver = new JSONObject(mr.get());
                System.out.println("[LOG] >>> Receiver: " + receiver);

                if (receiver.getString("prefix").equals("evaluateAccount")) {
                    if (receiver.getString("flag").equals("success")) {
//                        Model.getInstance().getSocketManager().removeMessage();

                        String clientID = receiver.getString("clientID");
                        String fullName = receiver.getString("fullname");
                        String defaultAvatarPath = "Images/avatar-default.jpg";
                        User user = new User(clientID, fullName, defaultAvatarPath);
                        Model.getInstance().setCurrentUser(user);
                        Platform.runLater(() -> {
                            Model.getInstance().getViewFactory().showClientWindow();
                            Model.getInstance().getViewFactory().closeStage(stage);
                        });
                    } else {
                        Platform.runLater(() -> {
                            error__lbl.setTextFill(Paint.valueOf("RED"));
                            error__lbl.setText("No Such Login Credential!");
                        });
                    }
                    messageProcessed = true;
                }
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
