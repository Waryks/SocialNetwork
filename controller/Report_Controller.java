package socialnetwork.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import jdk.javadoc.internal.tool.Start;
import jdk.vm.ci.meta.Local;
import socialnetwork.domain.*;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.GroupService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;
import socialnetwork.utils.PdfGenerator;
import socialnetwork.utils.events.FriendshipChangeEvent;
import socialnetwork.utils.events.UserChangeEvent;
import socialnetwork.utils.observer.Observer;

import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Report_Controller implements Observer<FriendshipChangeEvent> {
    Stage primaryStage;
    ObservableList<UserDTO> modelGrade = FXCollections.observableArrayList();
    private UserService user_crt;
    private FriendshipService friendscrt;
    private MessageService messagecrt;
    private GroupService groupcrt;
    private User u;

    @FXML
    public void initialize() {
        FriendsTable.setItems(modelGrade);
        FristNameColumn.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("firstName"));
        LastNameColumn.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("lastName"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<UserDTO, LocalDateTime>("date"));
    }

    private List<UserDTO> getFriendsListUser(Long id) {
        return StreamSupport.stream(friendscrt.getAll().spliterator(),false)
                .filter(c -> c.getId1() == id || c.getId2() == id)
                .filter(c -> c.getStatus().equals("approved"))
                .filter(c ->c.getDate().toLocalDate().isBefore(EndDate.getValue()))
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

    private void initModel() {
        modelGrade.setAll(getFriendsListUser(u.getId()));
    }

    public void setService(UserService service, FriendshipService friendscrt , MessageService messagecrt, GroupService groupcrt, String id_user) {
        this.user_crt= service;
        this.friendscrt=friendscrt;
        this.messagecrt=messagecrt;
        this.groupcrt=groupcrt;
        this.u=user_crt.getUser(id_user);
        StartDate.setValue(LocalDate.now());
        EndDate.setValue(LocalDate.now());
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
    void handleCreatePDF1(ActionEvent event) {
        LocalDate start = StartDate.getValue();
        LocalDate ending = EndDate.getValue();
        if(start.isAfter(ending)) {
            Allert_Controller.showErrorMessage(null, "The start date has to be before the ending date!");
            return;
        }
        List<Friendship> friends = getAllFriends();
        List<Message> pmsgs = getAllPrivateMessages();
        List<Message> gmsgs = getAllGroupMessages();
        List<String> content = getContent(pmsgs,gmsgs,friends);
        PdfGenerator pdf = new PdfGenerator("report.pdf");
        pdf.setTitle_doc("Activities");
        pdf.setUser_doc(u.getFirstName()+ " " + u.getLastName());
        pdf.setLines_doc(content);
        pdf.generatePDF();
    }

    private List<String> getContent(List<Message> privateMsg, List<Message> groupMsg){
        return getContent(privateMsg,groupMsg,null);
    }

    private List<String> getContent(List<Message> privateMsg, List<Message> groupMsg, List<Friendship> friends){
        List<String> finalString = new ArrayList<>();
        finalString.add("Start Date: " + StartDate.getValue());
        finalString.add("End Date: " + EndDate.getValue());
        finalString.add("\nPrivate messages: \n");
        for(Message msg:privateMsg)
            finalString.add("(" + msg.getDate().toLocalDate() + " " + msg.getDate().getHour()+ ":"+ msg.getDate().getMinute()+ ") "
                    + user_crt.getUser(msg.getFrom_id().toString()).getFirstName()
                    + " " + user_crt.getUser(msg.getFrom_id().toString()).getLastName()
                    + ": " + msg.getMessage());
        if(privateMsg.isEmpty()){
            finalString.add("-none");
        }
        finalString.add("\nGroup messages: \n");
        for(Message msg:groupMsg)
            finalString.add("(" + msg.getDate().toLocalDate() + " " + msg.getDate().getHour()+ ":"+ msg.getDate().getMinute()+ ") "
                    + user_crt.getUser(msg.getFrom_id().toString()).getFirstName()
                    + " " + user_crt.getUser(msg.getFrom_id().toString()).getLastName()
                    + " to group " + groupcrt.getGroup(msg.getTo_id().toString()).getName()
                    + ": " + msg.getMessage());
        if(groupMsg.isEmpty()){
            finalString.add("-none");
        }
        if(friends!=null){
            finalString.add("\nMade friends: \n");
            for(Friendship fr:friends){
                if(fr.getId1() == u.getId())
                    finalString.add(user_crt.getUser(String.valueOf(fr.getId2())).getFirstName()
                            + " " + user_crt.getUser(String.valueOf(fr.getId2())).getLastName()
                            + " became friends with " + u.getFirstName() + " " + u.getLastName() + " at " + fr.getDate().toLocalDate()
                    );
                else
                    finalString.add(user_crt.getUser(String.valueOf(fr.getId1())).getFirstName()
                            + " " + user_crt.getUser(String.valueOf(fr.getId1())).getLastName()
                            + " became friends with " + u.getFirstName() + " " + u.getLastName() + " at " + fr.getDate().toLocalDate()
                    );
            }
            if(friends.isEmpty()){
                finalString.add("-none");
            }
        }
        finalString.add("\nGoodbye :^)");
        return finalString;
    }

    private List<Friendship> getAllFriends(){
        return friendscrt.getAllFriends()
                .stream()
                .filter(c->c.getDate().toLocalDate().isAfter(StartDate.getValue()) && c.getDate().toLocalDate().isBefore(EndDate.getValue()))
                .filter(c->c.getId1() == u.getId() || c.getId2() == u.getId())
                .filter(c->c.getStatus().equals("approved"))
                .collect(Collectors.toList());
    }

    private List<Message> getAllPrivateMessages(){
        return messagecrt.getAllMessages()
                .stream()
                .filter(c->c.getDate().toLocalDate().isAfter(StartDate.getValue()) && c.getDate().toLocalDate().isBefore(EndDate.getValue()))
                .filter(c->c.getType().equals(Messagetype.privatemessage))
                .filter(c-> c.getTo_id().equals(u.getId()))
                .collect(Collectors.toList());
    }

    private List<Message> getAllGroupMessages(){
        return messagecrt.getAllMessages()
                .stream()
                .filter(c->c.getDate().toLocalDate().isAfter(StartDate.getValue()) && c.getDate().toLocalDate().isBefore(EndDate.getValue()))
                .filter(c->c.getType().equals(Messagetype.groupmessage))
                .filter(c-> groupcrt.getGroup(c.getTo_id().toString()).getIds().contains(u.getId()) && !c.getFrom_id().equals(u.getId()))
                .collect(Collectors.toList());
    }

    @FXML
    void handleCreatePDF2(ActionEvent event) {
        User friend=FriendsTable.getSelectionModel().getSelectedItem();
        if(friend==null){
            Allert_Controller.showErrorMessage(null, "No Friend was selected!");
            return;
        }
        LocalDate start = StartDate.getValue();
        LocalDate ending = EndDate.getValue();
        if(start.isAfter(ending)) {
            Allert_Controller.showErrorMessage(null, "The start date has to be before the ending date!");
            return;
        }
        List<Message> pmsgs = getAllPrivateMessagesfromUser(friend);
        List<Message> gmsgs = getAllGroupMessagesfromUser(friend);
        List<String> content = getContent(pmsgs,gmsgs);
        PdfGenerator pdf = new PdfGenerator("report.pdf");
        pdf.setTitle_doc("Messages from "+friend.getFirstName()+" "+friend.getLastName());
        pdf.setUser_doc(u.getFirstName()+ " " + u.getLastName());
        pdf.setLines_doc(content);
        pdf.generatePDF();
    }

    private List<Message> getAllPrivateMessagesfromUser(User friend){
        return messagecrt.getAllMessages()
                .stream()
                .filter(c->c.getDate().toLocalDate().isAfter(StartDate.getValue()) && c.getDate().toLocalDate().isBefore(EndDate.getValue()))
                .filter(c->c.getType().equals(Messagetype.privatemessage))
                .filter(c-> c.getTo_id().equals(u.getId()))
                .filter(c-> c.getFrom_id().equals(friend.getId()))
                .collect(Collectors.toList());
    }

    private List<Message> getAllGroupMessagesfromUser(User friend){
        return messagecrt.getAllMessages()
                .stream()
                .filter(c->c.getDate().toLocalDate().isAfter(StartDate.getValue()) && c.getDate().toLocalDate().isBefore(EndDate.getValue()))
                .filter(c->c.getType().equals(Messagetype.groupmessage))
                .filter(c-> groupcrt.getGroup(c.getTo_id().toString()).getIds().contains(u.getId()) && !c.getFrom_id().equals(u.getId()))
                .filter(c-> c.getFrom_id().equals(friend.getId()))
                .collect(Collectors.toList());
    }

    @FXML
    void handleExit(ActionEvent event) {
        primaryStage.close();
    }

    @FXML
    void handleStartChanged(ActionEvent event) {
        initModel();
    }
    @FXML
    void handleEndChanged(ActionEvent event) {
        initModel();
    }

    @FXML
    private Button ExitButton;

    @FXML
    private Button PDFButton1;

    @FXML
    private Button PDFButton2;

    @FXML
    private TableView<UserDTO> FriendsTable;

    @FXML
    private TableColumn<UserDTO, String> FristNameColumn;

    @FXML
    private TableColumn<UserDTO, String> LastNameColumn;


    @FXML
    private TableColumn<UserDTO, LocalDateTime> DateColumn;

    @FXML
    private DatePicker StartDate;

    @FXML
    private DatePicker EndDate;
}
