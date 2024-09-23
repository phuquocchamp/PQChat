package com.example.pqchatclient.Controller.Login;

import com.example.pqchatclient.Controller.Client.SingleContact.SingleChatController;
import com.example.pqchatclient.Model.Model;
import com.example.pqchatclient.View.LoginViewOptions;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
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
    //    public TextField validationCode__textField;
    public CheckBox pqTerms__checkBox;
    public Label error__lbl;
    public Button hidePassword__btn;
    //    public Button sendValidationCode__btn;
    public TextField fullname__textField;

    private static SignUpController instance;

    public SignUpController() {
    }

//    public static SignUpController getInstance() {
//        if (instance == null) {
//            instance = new SignUpController();
//        }
//        return instance;
//    }

    public static SignUpController getInstance() {
        if (instance == null) {
            FXMLLoader loader = new FXMLLoader(SignUpController.class.getResource("/Fxml/Login/SignUp.fxml"));
            try {
                loader.load();  // Tải FXML và khởi tạo controller
                instance = loader.getController();  // Lấy đối tượng controller
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(3));
        pauseTransition.setOnFinished(
                event -> error__lbl.setText("")
        );
        pauseTransition.play();
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
            error__lbl.setTextFill(Paint.valueOf("WHITE"));
            error__lbl.setText("");

            String fullname = fullname__textField.getText();
            String email = email__textField.getText();
            String password = encodePassword(password__textField.getText());

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("prefix", "createAccount");
            jsonObject.put("fullname", fullname);
            jsonObject.put("username", email);
            jsonObject.put("password", password);
            Model.getInstance().getSocketManager().sendMessage(jsonObject.toString());
        }
    }

    public void displaySuccess() {
        Platform.runLater(() -> {
            error__lbl.setTextFill(Paint.valueOf("GREEN"));
            error__lbl.setText("Created Account Successfully !");
            onSignInView();
        });
    }

    public void displayError(String message) {
        Platform.runLater(() -> {
            error__lbl.setTextFill(Paint.valueOf("RED"));
            error__lbl.setText(message);
        });
    }

    public void loadResponse(String message) {
        JSONObject createdFlag = new JSONObject(message);
        System.out.println(createdFlag.toString());
        if (createdFlag.getString("flag").equals("success")) {
            Platform.runLater(() -> {
                error__lbl.setTextFill(Paint.valueOf("GREEN"));
                error__lbl.setText("Created Account Successfully !");
                onSignInView();
            });
        } else {
            Platform.runLater(() -> {
                error__lbl.setTextFill(Paint.valueOf("RED"));
                error__lbl.setText(createdFlag.getString("message"));
            });
        }
    }

    private void onSignInView() {
        Model.getInstance().getViewFactory().getLoginSelectedMenuItem().set(LoginViewOptions.SIGNIN);
    }
}
