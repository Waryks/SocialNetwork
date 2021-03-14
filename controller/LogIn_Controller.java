package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;
import socialnetwork.service.*;
import socialnetwork.utils.events.FriendshipChangeEvent;
import socialnetwork.utils.events.UserChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class LogIn_Controller implements Observer<UserChangeEvent> {
    Stage primaryStage;
    ObservableList<User> modelGrade = FXCollections.observableArrayList();
    private UserService user_crt;
    private FriendshipService friendscrt;
    private MessageService messagecrt;
    private GroupService groupcrt;
    private RealEventService eventcrt;


    @FXML
    void handleCreateAccount(ActionEvent event) {
        try{
            String email,pass,cpass,first,last,food="-",age="-";
            email = this.EmailField.getText();
            UserValidator.emailValidator(email);
            UserValidator.mailExistenceValidate(email,user_crt.getAll());
            pass = this.passwordField.getText();
            cpass = this.confirmPasswordField.getText();
            UserValidator.passwordValidator(pass,cpass);
            first = this.FirstNameField.getText();
            UserValidator.firstNameValidate(first);
            last = this.LastNameField.getText();
            UserValidator.lastNameValidate(last);
            if(!this.FavouriteFoodField.getText().equals("")){
                food = this.FavouriteFoodField.getText();
                UserValidator.favFoodValidate(food);
            }
            if(!this.AgeField.getText().equals("")){
                age = this.AgeField.getText();
                UserValidator.ageValidate(age);
            }
            user_crt.addUser(first,last,email,pass,age,food);
            initModel();
            SuccesText.setVisible(true);
            EmailField.clear();
            passwordField.clear();
                    confirmPasswordField.clear();
            FirstNameField.clear();
                    LastNameField.clear();
            FavouriteFoodField.clear();
                    AgeField.clear();
        }
        catch (ValidationException exception){
            Allert_Controller.showErrorMessage(null,exception.getMessage());
            SuccesText.setVisible(false);
        }
    }


    @FXML
    void handleRegister(ActionEvent event) {
        this.EmailField.setVisible(true);
        this.passwordField.setVisible(true);
        this.confirmPasswordField.setVisible(true);
        this.FavouriteFoodField.setVisible(true);
        this.FirstNameField.setVisible(true);
        this.LastNameField.setVisible(true);
        this.AgeField.setVisible(true);

        this.Emailtext.setVisible(true);
        this.PasswordText.setVisible(true);
        this.ConPasswordText.setVisible(true);
        this.FavFoodtext.setVisible(true);
        this.FirstNameText.setVisible(true);
        this.LastNameText.setVisible(true);
        this.AgeText.setVisible(true);

        this.NotOpText.setVisible(true);
        this.TitleRegisterText.setVisible(true);
        this.CreateButton.setVisible(true);
        this.Line.setVisible(true);
        primaryStage.setWidth(714);
    }

    @FXML
    public void initialize() {
        TableViewUsers.setItems(modelGrade);
        tableColumnFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("email"));
    }

    private List<User> getUserList() {
        return StreamSupport.stream(user_crt.getAll().spliterator(),false)
                .collect(Collectors.toList());
    }

    public void setService(UserService service, FriendshipService friendscrt ,MessageService messagecrt,GroupService groupcrt,RealEventService eventcrt) {
        this.user_crt= service;
        this.friendscrt=friendscrt;
        this.messagecrt=messagecrt;
        this.groupcrt=groupcrt;
        this.eventcrt=eventcrt;

        this.EmailField.setVisible(false);
        this.passwordField.setVisible(false);
        this.confirmPasswordField.setVisible(false);
        this.FavouriteFoodField.setVisible(false);
        this.FirstNameField.setVisible(false);
        this.LastNameField.setVisible(false);
        this.AgeField.setVisible(false);

        this.Emailtext.setVisible(false);
        this.PasswordText.setVisible(false);
        this.ConPasswordText.setVisible(false);
        this.FavFoodtext.setVisible(false);
        this.FirstNameText.setVisible(false);
        this.LastNameText.setVisible(false);
        this.AgeText.setVisible(false);

        this.NotOpText.setVisible(false);
        this.TitleRegisterText.setVisible(false);
        this.CreateButton.setVisible(false);
        this.Line.setVisible(false);
        SuccesText.setVisible(false);
        initModel();
    }

    public void setPrimaryStage(Stage s){
        primaryStage=s;
        s.setWidth(445);
    }

    private void initModel(){
        modelGrade.setAll(getUserList());
    }

    @Override
    public void update(UserChangeEvent UserChangeEvent) {
        initModel();
    }


    private void showProfile(User u) throws IOException{
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/FullProfile.fxml"));
            AnchorPane root=loader.load();

            FullProfile_Controller ctrl=loader.getController();
            ctrl.setService(user_crt,friendscrt,messagecrt,groupcrt,eventcrt,String.valueOf(u.getId()));

            Stage stage=new Stage();
            stage.setResizable(false);
            stage.setScene(new Scene(root, 755, 500));
            stage.setTitle("Profile of User " + u.getLastName() + " " + u.getFirstName());
            ctrl.setPrimaryStage(stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleLogIn(ActionEvent actionEvent) {
        try {
            String email = loginEmail.getText();
            String pass = loginPass.getText();
            if(email.equals("")){
                Allert_Controller.showErrorMessage(null, "No email was given!");
                return;
            }
            if(pass.equals("")){
                Allert_Controller.showErrorMessage(null, "No password was given!");
                return;
            }

            User u=user_crt.getUserEmail(email);
            if(u!=null)
                if(u.getPassword().equals(pass))
                    showProfile(u);
                else
                    Allert_Controller.showErrorMessage(null,"Incorrect password!");
            else
                Allert_Controller.showErrorMessage(null,"The email doesn't exist would you like to register instead?");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ValidationException | IllegalArgumentException e){
            Allert_Controller.showErrorMessage(null,e.getMessage());
        }
    }

    @FXML
    void handleEasy(MouseEvent event) {
        if(!TableViewUsers.getSelectionModel().isEmpty()){
            loginEmail.setText(TableViewUsers.getSelectionModel().getSelectedItem().getEmail());
        }
    }

    @FXML
    private TextField loginEmail;

    @FXML
    private PasswordField loginPass;

    @FXML
    private TableView<User> TableViewUsers;

    @FXML
    private TableColumn<User, String> tableColumnFirstName;

    @FXML
    private TextField EmailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private TextField FirstNameField;

    @FXML
    private TextField LastNameField;

    @FXML
    private Text PasswordText;

    @FXML
    private Text SuccesText;

    @FXML
    private Text Emailtext;

    @FXML
    private Text ConPasswordText;

    @FXML
    private Text AgeText;

    @FXML
    private Text FavFoodtext;

    @FXML
    private Button RegisterButton;

    @FXML
    private Text TitleRegisterText;

    @FXML
    private javafx.scene.shape.Line Line;

    @FXML
    private Text NotOpText;

    @FXML
    private Button CreateButton;

    @FXML
    private TextField FavouriteFoodField;

    @FXML
    private TextField AgeField;

    @FXML
    private Text FirstNameText;

    @FXML
    private Text LastNameText;
}
