package socialnetwork.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import socialnetwork.domain.User;
import socialnetwork.service.FriendService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.io.IOException;

public class MenuController {
    private User user;
    private UserService userService;
    private MessageService msgService;
    private FriendService friendService;
    public void setService(UserService u, MessageService m, FriendService f){
        userService = u;
        msgService = m;
        friendService = f;
    }
    public void setUser(User u){
        user = u;
    }
    @FXML
    private TextField showNameText;

    @FXML
    private Button exitButton;

    @FXML
    private Button chatButton;

    @FXML
    private Button friendsButton;

    @FXML
    void handleChats(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/chat.fxml"));
            Parent root = loader.load();

            ChatController ctrl = loader.getController();

            ctrl.setUser(user);
            ctrl.setService(userService,msgService,friendService);


            Stage primaryStage = new Stage();
            primaryStage.setScene(new Scene(root, 750, 450));
            primaryStage.setTitle("Chat");
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleExit(ActionEvent event) {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    void handleFriends(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/friends.fxml"));
            Parent root = loader.load();
            FriendsController ctrl = loader.getController();
            ctrl.setUser(user);
            ctrl.setService(userService,msgService,friendService);
            Stage primaryStage = new Stage();
            primaryStage.setScene(new Scene(root, 750,450));
            primaryStage.setTitle("Friends");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
