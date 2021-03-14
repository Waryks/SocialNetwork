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
import socialnetwork.domain.Group;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.GroupService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;
import socialnetwork.utils.events.FriendshipChangeEvent;
import socialnetwork.utils.events.MessageGChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Message_Controller implements Observer<MessageGChangeEvent> {
    Stage primaryStage;
    private UserService user_crt;
    private MessageService messagecrt;
    private GroupService groupcrt;
    private User user;
    private User user2=null;
    private Group group=null;
    private String typeofchat="privatemessage";
    ObservableList<Group> modelGrade = FXCollections.observableArrayList();
    ObservableList<User> modelGrade2 = FXCollections.observableArrayList();
    ObservableList<String> modelmessages = FXCollections.observableArrayList();

    @Override
    public void update(MessageGChangeEvent messageChangeEvent) {
        showChat();
        initModel();
    }


    public void setPrimaryStage(Stage s){
        primaryStage=s;
    }

    public void setService(UserService service , MessageService messagecrt, GroupService groupcrt, User u) {
        this.user_crt= service;
        this.messagecrt=messagecrt;
        this.groupcrt=groupcrt;
        this.user=u;
        messagecrt.addObserver(this);
        groupcrt.addObserver(this);
        MembersTable.setVisible(false);
        MembersText.setVisible(false);
        initModel();
    }

    private List<Group> getAllGroupsofUser(Long id){
        return StreamSupport.stream(groupcrt.getAll().spliterator(),false)
                .filter(c -> c.getIds().contains(id))
                .collect(Collectors.toList());
    }

    private List<User> getAllMembers(){
        return group.getIds().stream()
                .map(c -> user_crt.getUser(String.valueOf(c)))
                .collect(Collectors.toList());
    }

    private void initModel() {
        modelGrade.setAll(getAllGroupsofUser(user.getId()));
        if(group!=null){
            modelGrade2.setAll(getAllMembers());
        }
    }

    @FXML
    public void initialize() {
        GroupTable.setItems(modelGrade);
        DateColumn.setCellValueFactory(new PropertyValueFactory<Group, LocalDateTime>("date"));
        Group_NameColumn.setCellValueFactory(new PropertyValueFactory<Group, String>("name"));

        MembersTable.setItems(modelGrade2);
        FirstNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        LastNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));


        TheChat.setItems(modelmessages);
    }

    @FXML
    void handleCreateGroup(ActionEvent event) {
        try {
            FXMLLoader loader=new FXMLLoader();
            loader.setLocation(getClass().getResource("/views/CreateGroup.fxml"));
            AnchorPane root=loader.load();

            CreateGroup_Controller ctrl=loader.getController();
            ctrl.setService(user_crt,groupcrt,user);

            Stage stage=new Stage();
            stage.setScene(new Scene(root, 500, 440));
            stage.setTitle("Create Group as " + user.getLastName() + " " + user.getFirstName());
            ctrl.setPrimaryStage(stage);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ValidationException | IllegalArgumentException e){
            Allert_Controller.showErrorMessage(null,e.getMessage());
        }
    }

    @FXML
    void handleExit(ActionEvent event) {
        messagecrt.removeObserver(this);
        primaryStage.close();
    }

    @FXML
    void handleGroupChat(ActionEvent event) {
        group=GroupTable.getSelectionModel().getSelectedItem();
        if(group==null){
            Allert_Controller.showErrorMessage(null, "No group was selected!");
            return;
        }
        user2=null;
        typeofchat="groupmessage";
        primaryStage.setWidth(850);
        MembersTable.setVisible(true);
        MembersText.setVisible(true);
        initModel();
        NameChat.setText("The Chat of the Group " + group.getName());
        showChat();

    }

    @FXML
    void handleLeaveGroup(ActionEvent event) {
        Group group_to_leave=GroupTable.getSelectionModel().getSelectedItem();
        if(group_to_leave==null){
            Allert_Controller.showErrorMessage(null, "No group was selected!");
            return;
        }
        Long group_id_to_leave=group_to_leave.getId();
        messagecrt.addMessage(String.valueOf(user.getId()),String.valueOf(group_to_leave.getId()),"-left the Group","groupmessage");
        groupcrt.leaveGroup(String.valueOf(group_to_leave.getId()),String.valueOf(user.getId()));
        if(group==group_to_leave){
            group=null;
            NameChat.setText("The Chat:");
            MembersTable.setVisible(false);
            MembersText.setVisible(false);
            primaryStage.setWidth(650);
        }
        if(group_to_leave.getNumber_of_people()==0){
            messagecrt.deleteAllGroupMessage(group_id_to_leave);
        }
        showChat();
    }

     private List<String> GetMessages(){
        if(typeofchat.equals("groupmessage") && group!=null){
            return messagecrt.Conv(java.lang.String.valueOf(user.getId()), java.lang.String.valueOf(group.getId()),typeofchat);
            /*for(java.lang.String line:messages){
                TheChat.add(line+"\n");
            }*/
        }
        if(typeofchat.equals("privatemessage") && user2!=null){
            return messagecrt.Conv(java.lang.String.valueOf(user.getId()), java.lang.String.valueOf(user2.getId()),typeofchat);
           /* for(java.lang.String line:messages){
                TheChat.appendText(line+"\n");
            }*/
        }
        return new ArrayList<>();
    }

    private void showChat(){
        List<String> messages;
        modelmessages.setAll(GetMessages());
    }

    @FXML
    void OnEnter(ActionEvent event) {
        try {
            String mess =MessageField.getText();
            MessageField.setText("");
            if(mess.equals("")){
                Allert_Controller.showErrorMessage(null, "No Message was written!");
                return;
            }
            if(user2==null && typeofchat.equals("privatemessage")){
                Allert_Controller.showErrorMessage(null, "No Chat was selected!");
                return;
            }
            if(group==null && typeofchat.equals("groupmessage")){
                Allert_Controller.showErrorMessage(null, "No Chat was selected!");
                return;
            }
            if(user2!=null){
                messagecrt.addMessage(String.valueOf(user.getId()),String.valueOf(user2.getId()),mess,typeofchat);
            }
            if(group!=null){
                messagecrt.addMessage(String.valueOf(user.getId()),String.valueOf(group.getId()),mess,typeofchat);
            }
        }  catch (ValidationException | IllegalArgumentException e){
            Allert_Controller.showErrorMessage(null,e.getMessage());
        }
    }

    @FXML
    void handlePrivateChat(ActionEvent event) {
        try {
            String id2 =privatechatidtext.getText();
            if(id2.equals("")){
                Allert_Controller.showErrorMessage(null, "No Id was given!");
                return;
            }
            UserValidator.idValidate(id2);
            UserValidator.idExistenceValidate(id2,user_crt.getAll());
            user2=user_crt.getUser(id2);
            typeofchat="privatemessage";
            group=null;
            MembersTable.setVisible(false);
            MembersText.setVisible(false);
            primaryStage.setWidth(650);
            NameChat.setText("The Chat with the User " + user2.getFirstName() + " " + user2.getLastName());
            showChat();
        }  catch (ValidationException | IllegalArgumentException e){
            Allert_Controller.showErrorMessage(null,e.getMessage());
        }
    }

    @FXML
    void handleSendMessage(ActionEvent event) {
        try {
            String mess =MessageField.getText();
            MessageField.setText("");
            if(mess.equals("")){
                Allert_Controller.showErrorMessage(null, "No Message was written!");
                return;
            }
            if(user2==null && typeofchat.equals("privatemessage")){
                Allert_Controller.showErrorMessage(null, "No Chat was selected!");
                return;
            }
            if(group==null && typeofchat.equals("groupmessage")){
                Allert_Controller.showErrorMessage(null, "No Chat was selected!");
                return;
            }
            if(user2!=null){
                messagecrt.addMessage(String.valueOf(user.getId()),String.valueOf(user2.getId()),mess,typeofchat);
            }
            if(group!=null){
                messagecrt.addMessage(String.valueOf(user.getId()),String.valueOf(group.getId()),mess,typeofchat);
            }
        }  catch (ValidationException | IllegalArgumentException e){
            Allert_Controller.showErrorMessage(null,e.getMessage());
        }
    }

    @FXML
    private Button ExitButton;

    @FXML
    private TableView<Group> GroupTable;

    @FXML
    private TableColumn<Group, LocalDateTime> DateColumn;

    @FXML
    private TableColumn<Group, String> Group_NameColumn;

    @FXML
    private TextField privatechatidtext;

    @FXML
    private Button CreateGroupButton;

    @FXML
    private Button LeaveGroupButton;

    @FXML
    private Button OpenPrivateChatButton;

    @FXML
    private ListView<String> TheChat;

    @FXML
    private Button GroupChatButton;

    @FXML
    private Button SendMessageButton;

    @FXML
    private TextField MessageField;

    @FXML
    private Text NameChat;

    @FXML
    private TableView<User> MembersTable;

    @FXML
    private TableColumn<User, String> FirstNameColumn;

    @FXML
    private TableColumn<User, String> LastNameColumn;

    @FXML
    private Text MembersText;

}
