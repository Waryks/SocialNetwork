package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import socialnetwork.domain.Group;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.GroupService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CreateGroup_Controller {
    Stage primaryStage;
    private UserService user_crt;
    private GroupService groupcrt;
    private User user;
    private List<User> the_users=new ArrayList<>();
    private List<Long> the_ids=new ArrayList<>();
    ObservableList<User> modelGrade = FXCollections.observableArrayList();

    public void setPrimaryStage(Stage s){
        primaryStage=s;
    }

    @FXML
    public void initialize() {
        UsersTable.setItems(modelGrade);
        FirstNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        LastNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
    }

    public void setService(UserService service , GroupService groupcrt, User u) {
        this.user_crt= service;
        this.groupcrt=groupcrt;
        this.user=u;
        this.the_users.add(u);
        this.the_ids.add(u.getId());
        initModel();
    }

    public void initModel(){
        modelGrade.setAll(the_users);
    }

    @FXML
    void handleAddUser(ActionEvent event) {
        try {
            String id =IdtoAdd.getText();
            if(id.equals("")){
                Allert_Controller.showErrorMessage(null, "No Id was given!");
                return;
            }
            User new_user=user_crt.getUser(id);
            UserValidator.idValidate(id);
            UserValidator.idExistenceValidate(id,user_crt.getAll());
            if(the_users.contains(new_user)){
                Allert_Controller.showErrorMessage(null, "This User is already in the group!");
                return;
            }
            the_users.add(new_user);
            the_ids.add(new_user.getId());
            initModel();
        }  catch (ValidationException | IllegalArgumentException e){
            Allert_Controller.showErrorMessage(null,e.getMessage());
        }
    }

    @FXML
    void handleRemove(ActionEvent event) {
        try {
            User User_to_remove=UsersTable.getSelectionModel().getSelectedItem();
            if(User_to_remove==null){
                Allert_Controller.showErrorMessage(null, "No user was selected!");
                return;
            }
            if(User_to_remove.getId().equals(user.getId())){
                Allert_Controller.showErrorMessage(null, "You cannot remove yourself from the Group you want to create!");
                return;
            }
            the_users.remove(User_to_remove);
            the_ids.remove(User_to_remove.getId());
            initModel();
        }  catch (ValidationException | IllegalArgumentException e){
            Allert_Controller.showErrorMessage(null,e.getMessage());
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        primaryStage.close();
    }

    @FXML
    void handleFInish(ActionEvent event) {
        if(the_users.size()<3){
            Allert_Controller.showErrorMessage(null, "Not enough members!(needs to be 3 or more)");
            return;
        }
        String name=theGroupName.getText();
        if(name.equals("")){
            Allert_Controller.showErrorMessage(null, "The Group Name was not given");
            return;
        }
        groupcrt.addGroup(name,the_ids, (long) the_users.size());
        primaryStage.close();
    }

    @FXML
    private TextField theGroupName;

    @FXML
    private Button FinishButton;

    @FXML
    private Button CancelButton;

    @FXML
    private TableView<User> UsersTable;

    @FXML
    private TableColumn<User, String> FirstNameColumn;

    @FXML
    private TableColumn<User, String> LastNameColumn;

    @FXML
    private TextField IdtoAdd;

    @FXML
    private Button RemoveUserButton;

    @FXML
    private Button AddButton;

}
