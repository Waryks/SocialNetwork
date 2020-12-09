package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import socialnetwork.domain.Friends;
import socialnetwork.domain.User;
import socialnetwork.domain.UserDTO;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.FriendService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;
import socialnetwork.utils.events.FriendEvent;
import socialnetwork.utils.events.UserEvent;
import socialnetwork.utils.observer.Observer;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendsController implements Observer<FriendEvent> {
    private User user;
    private UserService userService;
    private MessageService msgService;
    private FriendService friendService;
    ObservableList<User> friends = FXCollections.observableArrayList();
    ObservableList<UserDTO> friendRequests = FXCollections.observableArrayList();
    ObservableList<UserDTO> friendSent = FXCollections.observableArrayList();
    public void setService(UserService u, MessageService m, FriendService f){
        userService = u;
        msgService = m;
        friendService = f;
        friendService.addObserver(this);
        initModel();
    }
    public void setUser(User u){
        user = u;
    }
    @FXML
    private TableView<UserDTO> friendreqTable;

    @FXML
    private TableColumn<UserDTO, String> frFirstName;

    @FXML
    private TableColumn<UserDTO, String> frLastName;

    @FXML
    private TableColumn<UserDTO, LocalDateTime> frDate;

    @FXML
    private TableView<User> friendlistTable;

    @FXML
    private TableColumn<User, String> flFirstName;

    @FXML
    private TableColumn<User, String> flLastName;

    @FXML
    private Button closeButton;

    @FXML
    private TableView<UserDTO> sentreqTable;

    @FXML
    private TableColumn<UserDTO, String> srFirstName;

    @FXML
    private TableColumn<UserDTO, String> srLastName;

    @FXML
    private TableColumn<UserDTO, String> frStatus;

    @FXML
    private TextField sendreqText;

    @FXML
    private Button sendButton;

    @FXML
    private Label sendError;

    @FXML
    private Button removeButton;

    @FXML
    private Button acceptreqButton;

    @FXML
    private Button declinereqButton;

    @FXML
    private Button removeFriendButton;

    @FXML
    void handleAccept(ActionEvent event) {
        UserDTO u = friendreqTable.getSelectionModel().getSelectedItem();
        friendService.respondRequest(String.valueOf(user.getId()),String.valueOf(u.getId()),userService,"accept");
    }

    @FXML
    void handleClose(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        friendService.removeObserver(this);
        stage.close();
    }

    @FXML
    void handleDecline(ActionEvent event) {
        UserDTO u = friendreqTable.getSelectionModel().getSelectedItem();
        friendService.respondRequest(String.valueOf(user.getId()),String.valueOf(u.getId()),userService,"decline");
    }

    @FXML
    void handleRemoveFriend(ActionEvent event) {
        User u = friendlistTable.getSelectionModel().getSelectedItem();
        friendService.respondRequest(String.valueOf(user.getId()),String.valueOf(u.getId()),userService,"decline");
    }

    @FXML
    void handleRemoveRequest(ActionEvent event) {
        UserDTO u = sentreqTable.getSelectionModel().getSelectedItem();
        friendService.respondRequest(String.valueOf(user.getId()),String.valueOf(u.getId()),userService,"decline");
    }

    @FXML
    void handleSend(ActionEvent event) {
        String txt = sendreqText.getText();
        if(userService.checkUser(txt)){
            try{
                sendreqText.setText("");
                friendService.addFriend(String.valueOf(user.getId()),txt,userService);}
            catch (ValidationException exception){
                sendError.setText(exception.getMessage());
            }
        }
        else{
            sendError.setText("The user id is invalid!");
        }
    }

    @FXML
    public void initialize() {
        friendlistTable.setItems(friends);
        flFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        flLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        friendreqTable.setItems(friendRequests);
        frFirstName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("firstName"));
        frLastName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("lastName"));
        frDate.setCellValueFactory(new PropertyValueFactory<UserDTO, LocalDateTime>("date"));
        sentreqTable.setItems(friendSent);
        srFirstName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("firstName"));
        srLastName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("lastName"));
        frStatus.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("status"));
    }

    @Override
    public void update(FriendEvent friendEvent) {
        initModel();
    }
    private void initModel() {
        friends.setAll(friendService.getAllFriends()
                .stream()
                .filter(c-> c.getStatus().equals("accept"))
                .filter(c-> c.getIdRight().equals(user.getId()) || c.getIdLeft().equals(user.getId()))
                .map(c -> {
                    if(c.getIdRight().equals(user.getId()))
                        return userService.getUser(c.getIdLeft());
                    return userService.getUser(c.getIdRight());
                })
                .collect(Collectors.toList()));
        friendRequests.setAll(friendService.getAllFriends()
                .stream()
                .filter(c-> c.getStatus().equals("pending"))
                .filter(c-> c.getIdRight().equals(user.getId()))
                .map(c -> {
                    User us = userService.getUser(c.getIdLeft());
                    UserDTO rez_user = new UserDTO(us.getFirstName(),us.getLastName(),c.getDate());
                    rez_user.setId(us.getId());
                    rez_user.setStatus("pending");
                    return rez_user;
                })
                .collect(Collectors.toList()));
        friendSent.setAll(friendService.getAllFriends()
                .stream()
                .filter(c-> c.getStatus().equals("pending"))
                .filter(c-> c.getIdLeft().equals(user.getId()))
                .map(c -> {
                    User us = userService.getUser(c.getIdRight());
                    UserDTO rez_user = new UserDTO(us.getFirstName(),us.getLastName(),c.getDate());
                    rez_user.setId(us.getId());
                    rez_user.setStatus("pending");
                    return rez_user;
                })
                .collect(Collectors.toList()));
    }
}
