package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;
import socialnetwork.domain.UserDTO;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.GroupService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;
import socialnetwork.utils.events.FriendshipChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Profile_Controller  implements Observer<FriendshipChangeEvent> {
    Stage primaryStage;
    ObservableList<UserDTO> modelGrade1 = FXCollections.observableArrayList();
    ObservableList<UserDTO> modelGrade2 = FXCollections.observableArrayList();
    ObservableList<UserDTO> modelGrade3 = FXCollections.observableArrayList();
    private UserService user_crt;
    private FriendshipService friendscrt;
    private MessageService messagecrt;
    private GroupService groupcrt;
    private User u;

    @FXML
    public void initialize() {
        FriendsTable1.setItems(modelGrade1);
        FriendsDate.setCellValueFactory(new PropertyValueFactory<UserDTO, LocalDateTime>("date"));
        FriendsFirstName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("firstName"));
        FriendsLastName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("lastName"));

        FRequestsTable.setItems(modelGrade2);
        RequestsDate.setCellValueFactory(new PropertyValueFactory<UserDTO, LocalDateTime>("date"));
        RequestsFirstName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("firstName"));
        RequestsLastName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("lastName"));
        RFRStatusColumn.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("status"));

        SentRequestsTable.setItems(modelGrade3);
        SRequestsDate.setCellValueFactory(new PropertyValueFactory<UserDTO, LocalDateTime>("date"));
        SRequestsFirstName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("firstName"));
        SRequestsLastName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("lastName"));
        SFRStatusColumn.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("status"));
    }

    private List<UserDTO> getFriendsListUser(Long id) {
        return StreamSupport.stream(friendscrt.getAll().spliterator(),false)
                .filter(c -> c.getId1() == id || c.getId2() == id)
                .filter(c -> c.getStatus().equals("approved"))
                .map(c-> {
                    User u;
                    if(c.getId1() == id){
                        u=user_crt.getUser(String.valueOf(c.getId2()));
                    }else{
                        u=user_crt.getUser(String.valueOf(c.getId1()));
                    }
                    UserDTO udto =new UserDTO(u.getFirstName(),u.getLastName(),u.getAge(),u.getFavouriteFood(),c.getDate());
                    udto.setId(u.getId());
                    return udto;
                })
                .collect(Collectors.toList());
    }

    private List<UserDTO> getRequestListUser(Long id) {
        return StreamSupport.stream(friendscrt.getAll().spliterator(),false)
                .filter(c -> c.getId2() == id)
                .filter(c -> c.getStatus().equals("pending"))
                .map(c-> {
                    User u =user_crt.getUser(String.valueOf(c.getId1()));
                    UserDTO udto =new UserDTO(u.getFirstName(),u.getLastName(),u.getAge(),u.getFavouriteFood(),c.getDate());
                    udto.setId(u.getId());
                    udto.setStatus(c.getStatus());
                    return udto;
                })
                .collect(Collectors.toList());
    }

    private List<UserDTO> getSentRequestListUser(Long id) {
        return StreamSupport.stream(friendscrt.getAll().spliterator(),false)
                .filter(c -> c.getId1() == id)
                .filter(c -> c.getStatus().equals("pending"))
                .map(c-> {
                    User u =user_crt.getUser(String.valueOf(c.getId2()));
                    UserDTO udto =new UserDTO(u.getFirstName(),u.getLastName(),u.getAge(),u.getFavouriteFood(),c.getDate());
                    udto.setId(u.getId());
                    udto.setStatus(c.getStatus());
                    return udto;
                })
                .collect(Collectors.toList());
    }

    private void initModel() {
        modelGrade1.setAll(getFriendsListUser(u.getId()));
        modelGrade2.setAll(getRequestListUser(u.getId()));
        modelGrade3.setAll(getSentRequestListUser(u.getId()));
    }

    public void setService(UserService service, FriendshipService friendscrt , MessageService messagecrt, GroupService groupcrt, String id_user) {
        this.user_crt= service;
        this.friendscrt=friendscrt;
        this.messagecrt=messagecrt;
        this.groupcrt=groupcrt;
        this.u=user_crt.getUser(id_user);

        TheUserID.setText(String.valueOf(u.getId()));
        TheUserFirstName.setText(u.getFirstName());
        TheUserLastName.setText(u.getLastName());
        TheUserAge.setText(u.getAge());
        TheUserFavfood.setText(u.getFavouriteFood());

        initModel();
        friendscrt.addObserver(this);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage=primaryStage;
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        initModel();
    }

    @FXML
    void handleAcceptRequest(ActionEvent event) {
        UserDTO u2=FRequestsTable.getSelectionModel().getSelectedItem();
        if(u2==null){
            Allert_Controller.showErrorMessage(null, "No Request was selected!");
            return;
        }
        friendscrt.respondFriendRequest(String.valueOf(u2.getId()),String.valueOf(u.getId()),"approved");
    }

    @FXML
    void handleRefuseRequest(ActionEvent event) {
        UserDTO u2=FRequestsTable.getSelectionModel().getSelectedItem();
        if(u2==null){
            Allert_Controller.showErrorMessage(null, "No Request was selected!");
            return;
        }
        friendscrt.removeFriendship(String.valueOf(u2.getId()),String.valueOf(u.getId()));
    }

    @FXML
    void handleSendRequest(ActionEvent event) {
        try{
            String id2=IdtoSendRequest.getText();
            if(id2.equals("")){
                Allert_Controller.showErrorMessage(null, "No Id was given!");
                return;
            }
            UserValidator.idValidate(id2);
            UserValidator.idExistenceValidate(id2,user_crt.getAll());
            FriendshipValidator.idsNonExistenceValidate(id2,String.valueOf(u.getId()),friendscrt.getAll());
            User u2=user_crt.getUser(id2);
            friendscrt.addFriendship(String.valueOf(u.getId()),id2);
        }catch (ValidationException | IllegalArgumentException e){
            Allert_Controller.showErrorMessage(null,e.getMessage());
        }
    }

    @FXML
    void handleRemoveSentRequest(ActionEvent event) {
        UserDTO u2=SentRequestsTable.getSelectionModel().getSelectedItem();
        if(u2==null){
            Allert_Controller.showErrorMessage(null, "No Request was selected!");
            return;
        }
        friendscrt.removeFriendship(String.valueOf(u.getId()),String.valueOf(u2.getId()));
    }

    @FXML
    void handleRemoveFriendship(ActionEvent event) {
        UserDTO u2=FriendsTable1.getSelectionModel().getSelectedItem();
        if(u2==null){
            Allert_Controller.showErrorMessage(null, "No Request was selected!");
            return;
        }
        friendscrt.removeFriendship(String.valueOf(u.getId()),String.valueOf(u2.getId()));
    }


    @FXML
    void handleMessages(ActionEvent event) {
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/Messages.fxml"));
            AnchorPane root=loader.load();

            Message_Controller ctrl=loader.getController();
            ctrl.setService(user_crt,messagecrt,groupcrt,u);

            Stage stage=new Stage();
            stage.setScene(new Scene(root, 650, 540));
            stage.setTitle("Messages of User " + u.getLastName() + " " + u.getFirstName());
            ctrl.setPrimaryStage(stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ValidationException | IllegalArgumentException e){
            Allert_Controller.showErrorMessage(null,e.getMessage());
        }
    }

    @FXML
    void handleLogOut(ActionEvent event) {
        friendscrt.removeObserver(this);
        primaryStage.close();
    }


    @FXML
    void handleReports(ActionEvent event) {
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/Reports.fxml"));
            AnchorPane root=loader.load();

            Report_Controller ctrl=loader.getController();
            ctrl.setService(user_crt,friendscrt,messagecrt,groupcrt,String.valueOf(u.getId()));

            Stage stage=new Stage();
            stage.setScene(new Scene(root, 620, 390));
            stage.setTitle("Reports for User " + u.getLastName() + " " + u.getFirstName());
            ctrl.setPrimaryStage(stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ValidationException | IllegalArgumentException e){
            Allert_Controller.showErrorMessage(null,e.getMessage());
        }
    }

    @FXML
    private TableView<UserDTO> FRequestsTable;

    @FXML
    private TableColumn<UserDTO, String> RequestsFirstName;

    @FXML
    private TableColumn<UserDTO, String> RequestsLastName;

    @FXML
    private TableColumn<UserDTO, LocalDateTime> RequestsDate;

    @FXML
    private Text TheUserID;

    @FXML
    private Text TheUserFirstName;

    @FXML
    private Text TheUserLastName;

    @FXML
    private Text TheUserAge;

    @FXML
    private Text TheUserFavfood;

    @FXML
    private Button LogOutButton;

    @FXML
    private TableView<UserDTO> FriendsTable1;

    @FXML
    private TableColumn<UserDTO, String> FriendsFirstName;

    @FXML
    private TableColumn<UserDTO, String> FriendsLastName;

    @FXML
    private TableColumn<UserDTO, LocalDateTime> FriendsDate;

    @FXML
    private Button SendRequestButton;

    @FXML
    private Button AcceptRequestButton;

    @FXML
    private Button RefuseRequestButton;

    @FXML
    private TextField IdtoSendRequest;

    @FXML
    private TableView<UserDTO> SentRequestsTable;

    @FXML
    private TableColumn<UserDTO, String> SRequestsFirstName;

    @FXML
    private TableColumn<UserDTO, String> SRequestsLastName;

    @FXML
    private TableColumn<UserDTO, LocalDateTime> SRequestsDate;

    @FXML
    private Button RemoveSRequest;

    @FXML
    private Button MessagesButton;

    @FXML
    private Button RemoveFriendshipButton;

    @FXML
    private TableColumn<UserDTO, String> RFRStatusColumn;

    @FXML
    private TableColumn<UserDTO, String> SFRStatusColumn;

    @FXML
    private Button ReportsButton;
}
