package com.example.pqchatclient.Controller.Login;

import com.example.pqchatclient.Model.Model;
import com.example.pqchatclient.Utils.Email;
import com.example.pqchatclient.Utils.Encrypt;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ForgotPasswordController implements Initializable {
    public TextField emailAddress__textField;
    public Button back__btn;
    public Label error__lbl;
    public Button submitNewPassword__btn;
    public TextField newPassword__textField;
    public TextField validationCode__textField;
    public Button sendCode__btn;

    String validationCode = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
    }

    private void addListeners() {
        validationCode__textField.setEditable(false);
        validationCode__textField.setStyle("-fx-background-color: #CCCC99");
        back__btn.setOnAction(event -> onBackLoginWindow());
        sendCode__btn.setOnAction(event -> onSendValidationCode());
        submitNewPassword__btn.setOnAction(event -> onSubmitNewPassword());

    }

    private void onSendValidationCode() {
        validationCode__textField.setEditable(true);
        validationCode__textField.setStyle("-fx-background-color: white");
        try {
            String sendTo = emailAddress__textField.getText();
            String password = newPassword__textField.getText();
            validationCode = CodeGenerate();

            Email.sendEmail(sendTo, "Reset Password Request", validationCode);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void onSubmitNewPassword() {
        System.out.println(validationCode);
        if (!validationCode__textField.getText().equals(validationCode)) {
            error__lbl.setText("Wrong validation code. Please do it again!");
        } else {

            error__lbl.setText("Successfully!");
            String newPassword = newPassword__textField.getText();
            String email = emailAddress__textField.getText();

            JSONObject resetPassword = new JSONObject();
            resetPassword.put("prefix", "resetPassword");
            resetPassword.put("username", email);
            resetPassword.put("newPassword", Encrypt.encodePassword(newPassword));

            ExecutorService service = Executors.newSingleThreadExecutor();
            service.submit(() -> Model.getInstance().getSocketManager().sendMessage(resetPassword.toString()));
            service.submit(() -> {
                try {
                    String checkFlag = Model.getInstance().getSocketManager().receiverMessage();
                    JSONObject jsonObject = new JSONObject(checkFlag);

                    if (jsonObject.getString("prefix").equals("resetPassword")) {
                        if (jsonObject.getString("flag").equals("success")) {
                            Platform.runLater(() -> {
                                emailAddress__textField.setText("Successfully Reset Password");
                                onBackLoginWindow();

                            });
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private void onBackLoginWindow() {
        Stage stage = (Stage) error__lbl.getScene().getWindow();
        Model.getInstance().getViewFactory().showLoginWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
    }

    // private validation code
    public String CodeGenerate() {
        Random random = new Random();
        int validationCode = random.nextInt(900000) + 100000;
        return String.valueOf(validationCode);
    }
}
