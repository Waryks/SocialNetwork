package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.domain.Friends;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.file.FriendsFile;
import socialnetwork.repository.file.MessageFile;
import socialnetwork.repository.file.UserFile;
import socialnetwork.service.FriendService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.io.IOException;

public class LoginController {
    private UserService userService;
    private MessageService msgService;
    private FriendService friendService;
    private Stage stage;
    public void setService(UserService u, MessageService m, FriendService f){
        userService = u;
        msgService = m;
        friendService = f;
    }
    public void setPrimaryStage(Stage s){
        stage = s;
    }
    @FXML
    private Label loginError;

    @FXML
    private TextField usernameText;

    @FXML
    private Button registerButton;

    @FXML
    private PasswordField passwordText;

    @FXML
    private Button cancelButton;

    @FXML
    private Button loginButton;

    @FXML
    void handleCancel(ActionEvent event) {
        this.stage.close();
    }

    @FXML
    void handleLogin(ActionEvent event) {
        try{
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/menu.fxml"));
            Parent root=loader.load();

            MenuController ctrl=loader.getController();

            String user = usernameText.getText();
            String pass = passwordText.getText();
            if(user.isBlank() == false && pass.isBlank() == false){
                loginError.setText("");
                if(userService.checkUser(user) == true){
                    ctrl.setUser(userService.getUser(user));
                    ctrl.setService(userService,msgService,friendService);
                    passwordText.setText("");
                    Stage primaryStage = new Stage();
                    primaryStage.setScene(new Scene(root, 750, 450));
                    primaryStage.setTitle("Menu");
                    primaryStage.show();
                }
                else
                    loginError.setText("Username or password is invalid");
            }
            else if((user.isBlank() == true && pass.isBlank() == true) || (user.isBlank() == true && pass.isBlank() == false) || (user.isBlank() == false && pass.isBlank() == true)){
                loginError.setText("Please enter username and password");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleRegister(ActionEvent event) {

    }

}

