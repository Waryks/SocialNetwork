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
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jdk.vm.ci.meta.Local;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.RealEventValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.*;
import socialnetwork.utils.PdfGenerator;
import socialnetwork.utils.events.ChangeEvent;
import socialnetwork.utils.events.Event;
import socialnetwork.utils.events.FriendshipChangeEvent;
import socialnetwork.utils.observer.Observer;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FullProfile_Controller  implements Observer<FriendshipChangeEvent> {
    Stage primaryStage;
    //Services+The logged User
    private UserService user_crt;
    private FriendshipService friendscrt;
    private MessageService messagecrt;
    private GroupService groupcrt;
    private RealEventService eventcrt;
    private User u;
    //The number of pages and the number of elements on last page of paged tables(filtered)
    int friends_size,sent_size,received_size,groups_size,SE_size;
    int friends_lastpage,sent_lastpage,received_lastpage,groups_lastpage,SE_lastpage;
    //ObservableLists and more:
    //----------------------------------------------------Details
    ObservableList<UserDTO> modelGrade1 = FXCollections.observableArrayList();
    //----------------------------------------------------Friend Request
    ObservableList<UserDTO> modelGrade2 = FXCollections.observableArrayList();
    ObservableList<UserDTO> modelGrade3 = FXCollections.observableArrayList();
    //----------------------------------------------------Messages
    private User user2=null;
    private Group group=null;
    private String typeofchat="privatemessage";
    ObservableList<Group> modelGrademes1 = FXCollections.observableArrayList();
    ObservableList<User> modelGrademes2 = FXCollections.observableArrayList();
    ObservableList<String> modelmessages = FXCollections.observableArrayList();
    //----------------------------------------------------Groups
    private List<User> the_users=new ArrayList<>();
    private List<Long> the_ids=new ArrayList<>();
    ObservableList<User> modelGradegroup = FXCollections.observableArrayList();
    //----------------------------------------------------Reports
    ObservableList<UserDTO> modelGrade = FXCollections.observableArrayList();
    //----------------------------------------------------Events
    ObservableList<RealEvent> observeSubscribe = FXCollections.observableArrayList();
    ObservableList<RealEvent> observeAll = FXCollections.observableArrayList();
    int pageno=0;
    int size=6;
    //------------------------------------------------------------------------
    @FXML
    public void initialize() {
        //----------------------------------------------------Details
        FriendsTable1.setItems(modelGrade1);
        FriendsDate.setCellValueFactory(new PropertyValueFactory<UserDTO, LocalDateTime>("date"));
        FriendsFirstName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("firstName"));
        FriendsLastName.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("lastName"));
        //----------------------------------------------------Friend Request
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
        //----------------------------------------------------Messages
        GroupTable.setItems(modelGrademes1);
        DateColumn1.setCellValueFactory(new PropertyValueFactory<Group, LocalDateTime>("date"));
        Group_NameColumn.setCellValueFactory(new PropertyValueFactory<Group, String>("name"));

        MembersTable.setItems(modelGrademes2);
        FirstNameColumnMembers.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        LastNameColumnMembers.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

        TheChat.setItems(modelmessages);
        //----------------------------------------------------Groups
        UsersTable.setItems(modelGradegroup);
        FirstNameColumn.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        LastNameColumn1.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));
        //----------------------------------------------------Reports
        FriendsTable.setItems(modelGrade);
        FristNameColumn.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("firstName"));
        LastNameColumn.setCellValueFactory(new PropertyValueFactory<UserDTO, String>("lastName"));
        DateColumn.setCellValueFactory(new PropertyValueFactory<UserDTO, LocalDateTime>("date"));
        //----------------------------------------------------Events
        SubscribedEventsTable.setItems(observeSubscribe);
        SEName.setCellValueFactory(new PropertyValueFactory<RealEvent, String>("name"));
        SEDate.setCellValueFactory(new PropertyValueFactory<RealEvent, LocalDate>("date"));
        SEHour.setCellValueFactory(new PropertyValueFactory<RealEvent, Long>("hour"));
        SEMinute.setCellValueFactory(new PropertyValueFactory<RealEvent, Long>("minute"));
        SENoMembers.setCellValueFactory(new PropertyValueFactory<RealEvent, Long>("number_of_people"));

        AllEventsTable.setItems(observeAll);
        AEName.setCellValueFactory(new PropertyValueFactory<RealEvent, String>("name"));
        AEDate.setCellValueFactory(new PropertyValueFactory<RealEvent, LocalDate>("date"));
        AEHour.setCellValueFactory(new PropertyValueFactory<RealEvent, Long>("hour"));
        AEMinute.setCellValueFactory(new PropertyValueFactory<RealEvent, Long>("minute"));
        AENoMembers.setCellValueFactory(new PropertyValueFactory<RealEvent, Long>("number_of_people"));
    }

    private void initModel() {
        //we find the sizes of the pages and the number for the last page if there is one
        friends_lastpage=friendscrt.getFriendsListUser(u.getId(),user_crt).size()%size;
        friends_size=friendscrt.getFriendsListUser(u.getId(),user_crt).size()/size;
        if(friends_lastpage!=0)
            friends_size++;

        sent_lastpage=friendscrt.getSentRequestListUser(u.getId(),user_crt).size()%size;
        sent_size=friendscrt.getSentRequestListUser(u.getId(),user_crt).size()/size;
        if(sent_lastpage%size!=0)
            sent_size++;

        received_size=friendscrt.getRequestListUser(u.getId(),user_crt).size()/size;
        received_lastpage=friendscrt.getRequestListUser(u.getId(),user_crt).size()%size;
        if(received_lastpage!=0)
            received_size++;

        groups_size=groupcrt.getAllGroupsofUser(u.getId()).size()/size;
        groups_lastpage=groupcrt.getAllGroupsofUser(u.getId()).size()%size;
        if(groups_lastpage!=0)
            groups_size++;

        SE_size=eventcrt.listSubscribed(u.getId()).size()/size;
        SE_lastpage=eventcrt.listSubscribed(u.getId()).size()%size;
        if(SE_lastpage!=0)
            SE_size++;

        //we reset the number of pages
        if(friends_size>0)
            PaginationFriends.setPageCount(friends_size);
        else
            PaginationFriends.setPageCount(1);

        if(sent_size>0)
            PaginationSentRequest.setPageCount(sent_size);
        else
            PaginationSentRequest.setPageCount(1);

        if(received_size>0)
            PaginationReveivedRequest.setPageCount(received_size);
        else
            PaginationReveivedRequest.setPageCount(1);

        if(groups_size>0)
            PaginationGroups.setPageCount(groups_size);
        else
            PaginationGroups.setPageCount(1);

        if(SE_size>0)
            PaginationSubscribedEvents.setPageCount(SE_size);
        else
            PaginationSubscribedEvents.setPageCount(1);

        eventcrt.setPageSize(size);

        //we reset to page 0
        PaginationFriends.setCurrentPageIndex(0);
        PaginationSentRequest.setCurrentPageIndex(0);
        PaginationReveivedRequest.setCurrentPageIndex(0);
        PaginationGroups.setCurrentPageIndex(0);
        PaginationSubscribedEvents.setCurrentPageIndex(0);
        //----------------------------------------------------Details
        noFriendsText.setText("Number of Friends: " + friendscrt.getFriendsListUser(u.getId(),user_crt).size());
        if(friends_size>1 || (friends_lastpage==0 && friends_size!=0)){
            modelGrade1.setAll(friendscrt.getFriendsListUser(u.getId(),user_crt)
                    .subList(0,size));
        }else{
            if(friends_size!=0)
                modelGrade1.setAll(friendscrt.getFriendsListUser(u.getId(),user_crt)
                            .subList(0,friends_lastpage));
            else
                modelGrade1.setAll(friendscrt.getFriendsListUser(u.getId(),user_crt));
        }
        //----------------------------------------------------Friend Request
        if(received_size>1 || (received_lastpage==0 && received_size!=0)){
            modelGrade2.setAll(friendscrt.getRequestListUser(u.getId(),user_crt)
                    .subList(0,size));
        }else{
            if(received_size!=0)
                modelGrade2.setAll(friendscrt.getRequestListUser(u.getId(),user_crt)
                        .subList(0,received_lastpage));
            else
                modelGrade2.setAll(friendscrt.getRequestListUser(u.getId(),user_crt));
        }
        if(sent_size>1 || (sent_lastpage==0 && sent_size!=0)){
            modelGrade3.setAll(friendscrt.getSentRequestListUser(u.getId(),user_crt)
                    .subList(0,size));
        }else{
            if(sent_size!=0)
                modelGrade3.setAll(friendscrt.getSentRequestListUser(u.getId(),user_crt)
                        .subList(0,sent_lastpage));
            else
                modelGrade3.setAll(friendscrt.getSentRequestListUser(u.getId(),user_crt));
        }
        //----------------------------------------------------Messages
        if(groups_size>1 || (groups_lastpage==0 && groups_size!=0)){
            modelGrademes1.setAll(groupcrt.getAllGroupsofUser(u.getId())
                    .subList(0,size));
        }else{
            if(groups_size!=0)
                modelGrademes1.setAll(groupcrt.getAllGroupsofUser(u.getId())
                        .subList(0,groups_lastpage));
            else
                modelGrademes1.setAll(groupcrt.getAllGroupsofUser(u.getId()));
        }
        ;
        if(group!=null){
            modelGrademes2.setAll(getAllMembers());
        }
        showChat();
        //----------------------------------------------------Groups
        modelGradegroup.setAll(the_users);
        //----------------------------------------------------Reports
        modelGrade.setAll(getFriendsListUserinPeriod(u.getId()));
        //----------------------------------------------------Events
        observeAll.setAll(eventcrt.getEventsOnPage(pageno));
        if(SE_size>1 || (SE_lastpage==0 && SE_size!=0)){
            observeSubscribe.setAll(eventcrt.listSubscribed(u.getId())
                    .subList(0,size));
        }else{
            if(SE_size!=0)
                observeSubscribe.setAll(eventcrt.listSubscribed(u.getId())
                        .subList(0,SE_lastpage));
            else
                observeSubscribe.setAll(eventcrt.listSubscribed(u.getId()));
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage=primaryStage;
    }

    @Override
    public void update(FriendshipChangeEvent friendshipChangeEvent) {
        initModel();
    }

    @FXML
    void handleLogOut(ActionEvent event) {
        friendscrt.removeObserver(this);
        primaryStage.close();
    }

    public void setService(UserService service, FriendshipService friendscrt , MessageService messagecrt, GroupService groupcrt, RealEventService eventcrt, String id_user) {
        //Services
        this.user_crt= service;
        this.friendscrt=friendscrt;
        this.messagecrt=messagecrt;
        this.groupcrt=groupcrt;
        this.eventcrt=eventcrt;
        this.u=user_crt.getUser(id_user);
        //Prompt Text for Tables
        FriendsTable1.setPlaceholder(new Label("It looks like you don't have any friends :(.Send some Friend Requests!"));
        SentRequestsTable.setPlaceholder(new Label("No Sent Requests"));
        FRequestsTable.setPlaceholder(new Label("No Received Requests"));
        GroupTable.setPlaceholder(new Label("You are not a member of any group"));
        FriendsTable.setPlaceholder(new Label("You didnt have friends in that period"));
        SubscribedEventsTable.setPlaceholder(new Label("You are not subscribed to any event!"));
        AllEventsTable.setPlaceholder(new Label("There arent any events yet."));
        TheChat.setPlaceholder(new Label("No messages to display"));
        //----------------------------------------------------Details
        idText.setText("ID: " + String.valueOf(u.getId()));
        firstnameText.setText("First Name: " + u.getFirstName());
        lastnameText.setText("Last Name: " + u.getLastName());
        ageText.setText("Age: " + u.getAge());
        FavFoodText.setText("Favourite Food: " + u.getFavouriteFood());
        emailText.setText("Email: " + u.getEmail());
        noFriendsText.setText("Number of Friends: " + friendscrt.getFriendsListUser(u.getId(),user_crt).size());
        RemoveFriendshipButton.setOpacity(0.5);
        RefuseRequestButton.setOpacity(0.5);
        AcceptRequestButton.setOpacity(0.5);
        RemoveSRequest.setOpacity(0.5);
        //----------------------------------------------------Messages
        MembersTable.setVisible(false);
        MembersText.setVisible(false);
        SendMessageButton.setText("Send");
        CancelReplyButton.setVisible(false);
        LeaveGroupButton.setOpacity(0.5);
        GroupChatButton.setOpacity(0.5);
        //----------------------------------------------------Groups
        this.the_users.add(u);
        this.the_ids.add(u.getId());
        RemoveUserButton.setOpacity(0.5);
        //----------------------------------------------------Reports
        StartDate.setValue(LocalDate.now());
        EndDate.setValue(LocalDate.now());
        //----------------------------------------------------Events
        EventDate.setValue(LocalDate.now());
        int more=0;
        if(eventcrt.getAllList().size()%size>0){
            more=1;
        }
        numberofPage.setPageCount(eventcrt.getAllList().size()/size+more);
        UnsubscribeButton.setOpacity(0.5);
        SubscribeButton.setOpacity(0.5);
        for(RealEvent e:eventcrt.getAllList()){
            eventcrt.removeEvent(e.getId().toString());
            List<Boolean> eAnnounce= e.getAnnounce();
            eAnnounce.set(0,true);
            eAnnounce.set(1,true);
            eAnnounce.set(2,true);
            eventcrt.addEventID(e.getName(),e.getDate(),e.getHour(),e.getMinute(),e.getNumber_of_people(),e.getIds(),eAnnounce,e.getId());

        }

        //------------------------------------------------------------------------
        initModel();
        friendscrt.addObserver(this);


    }

    //----------------------------------------------------11Details

    @FXML
    void handleRemoveFriendship(ActionEvent event) {
        UserDTO u2=FriendsTable1.getSelectionModel().getSelectedItem();
        if(u2==null){
            Allert_Controller.showErrorMessage(null, "No Request was selected!");
            return;
        }
        friendscrt.removeFriendship(String.valueOf(u.getId()),String.valueOf(u2.getId()));
        RemoveFriendshipButton.setOpacity(0.5);
    }
    @FXML
    void handleSelectFriends(MouseEvent event) {
        if(!FriendsTable1.getSelectionModel().isEmpty()){
            RemoveFriendshipButton.setOpacity(1);
        }
    }
    //----------------------------------------------------11Friend Request
    @FXML
    void handleSelectSent(MouseEvent event) {
        if(!SentRequestsTable.getSelectionModel().isEmpty()){
            RemoveSRequest.setOpacity(1);
        }
    }
    @FXML
    void handleSelectReceived(MouseEvent event) {
        if(!FRequestsTable.getSelectionModel().isEmpty()){
            AcceptRequestButton.setOpacity(1);
            RefuseRequestButton.setOpacity(1);
        }
    }

    @FXML
    void handleAcceptRequest(ActionEvent event) {
        UserDTO u2=FRequestsTable.getSelectionModel().getSelectedItem();
        if(u2==null){
            Allert_Controller.showErrorMessage(null, "No Request was selected!");
            return;
        }
        friendscrt.respondFriendRequest(String.valueOf(u2.getId()),String.valueOf(u.getId()),"approved");
        AcceptRequestButton.setOpacity(0.5);
        RefuseRequestButton.setOpacity(0.5);
    }

    @FXML
    void handleRefuseRequest(ActionEvent event) {
        UserDTO u2=FRequestsTable.getSelectionModel().getSelectedItem();
        if(u2==null){
            Allert_Controller.showErrorMessage(null, "No Request was selected!");
            return;
        }
        friendscrt.removeFriendship(String.valueOf(u2.getId()),String.valueOf(u.getId()));
        AcceptRequestButton.setOpacity(0.5);
        RefuseRequestButton.setOpacity(0.5);
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
        RemoveSRequest.setOpacity(0.5);
    }


    //----------------------------------------------------11Messages

    private List<User> getAllMembers(){
        return group.getIds().stream()
                .map(c -> user_crt.getUser(String.valueOf(c)))
                .collect(Collectors.toList());
    }

    @FXML
    void handleSelectGroup(MouseEvent event) {
        if(!GroupTable.getSelectionModel().isEmpty()){
            GroupChatButton.setOpacity(1);
            LeaveGroupButton.setOpacity(1);
        }
    }

    @FXML
    void handlePossibleReply(MouseEvent event){
        if(!TheChat.getSelectionModel().isEmpty()){
            if(TheChat.getSelectionModel().getSelectedItem().startsWith("(")){
                SendMessageButton.setText("Reply");
                CancelReplyButton.setVisible(true);}
            else{
                SendMessageButton.setText("Sent");
                TheChat.getSelectionModel().clearSelection();
                CancelReplyButton.setVisible(false);
            }
        }
    }

    @FXML
    void handleCancelReply(ActionEvent event){
        SendMessageButton.setText("Sent");
        TheChat.getSelectionModel().clearSelection();
        CancelReplyButton.setVisible(false);
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
        MembersTable.setVisible(true);
        MembersText.setVisible(true);
        initModel();
        NameChat.setText("The Chat of the Group " + group.getName());
        showChat();
        CancelReplyButton.setVisible(false);
        GroupChatButton.setOpacity(0.5);
        LeaveGroupButton.setOpacity(0.5);
    }

    @FXML
    void handleLeaveGroup(ActionEvent event) {
        Group group_to_leave=GroupTable.getSelectionModel().getSelectedItem();
        if(group_to_leave==null){
            Allert_Controller.showErrorMessage(null, "No group was selected!");
            return;
        }
        Long group_id_to_leave=group_to_leave.getId();
        messagecrt.addMessage(String.valueOf(u.getId()),String.valueOf(group_to_leave.getId()),"-left the Group","groupmessage");
        if(group.getId().equals(group_to_leave.getId())){
            group=null;
            NameChat.setText("The Chat:");
            MembersTable.setVisible(false);
            MembersText.setVisible(false);
            primaryStage.setWidth(650);
        }
        if(group_to_leave.getNumber_of_people()==1){
            messagecrt.deleteAllGroupMessage(group_id_to_leave);
        }
        groupcrt.leaveGroup(String.valueOf(group_to_leave.getId()),String.valueOf(u.getId()));
        friendscrt.notifyObservers(new FriendshipChangeEvent(ChangeEvent.UPDATE,null));
        showChat();
        LeaveGroupButton.setOpacity(0.5);
        GroupChatButton.setOpacity(0.5);
    }

    private List<String> GetMessages(){
        if(typeofchat.equals("groupmessage") && group!=null){
            return messagecrt.Conv(java.lang.String.valueOf(u.getId()), java.lang.String.valueOf(group.getId()),typeofchat);
        }
        if(typeofchat.equals("privatemessage") && user2!=null){
            return messagecrt.Conv(java.lang.String.valueOf(u.getId()), java.lang.String.valueOf(user2.getId()),typeofchat);
        }
        return new ArrayList<>();
    }

    private void showChat(){
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
                messagecrt.addMessage(String.valueOf(u.getId()),String.valueOf(user2.getId()),mess,typeofchat);
            }
            if(group!=null){
                messagecrt.addMessage(String.valueOf(u.getId()),String.valueOf(group.getId()),mess,typeofchat);
            }
            friendscrt.notifyObservers(new FriendshipChangeEvent(ChangeEvent.UPDATE,null));
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
            NameChat.setText("The Chat with the User " + user2.getFirstName() + " " + user2.getLastName());
            showChat();
            CancelReplyButton.setVisible(false);
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
                if(TheChat.getSelectionModel().getSelectedItem()==null) {
                    messagecrt.addMessage(String.valueOf(u.getId()), String.valueOf(user2.getId()), mess, typeofchat);
                }
                else{
                    String s=TheChat.getSelectionModel().getSelectedItem();
                    messagecrt.addMessage(String.valueOf(u.getId()),String.valueOf(user2.getId()),mess,typeofchat,s);
                }

            }
            if(group!=null){
                if(TheChat.getSelectionModel().getSelectedItem()==null) {
                    messagecrt.addMessage(String.valueOf(u.getId()), String.valueOf(group.getId()), mess, typeofchat);
                }
                else{
                    String s=TheChat.getSelectionModel().getSelectedItem();
                    messagecrt.addMessage(String.valueOf(u.getId()),String.valueOf(group.getId()),mess,typeofchat,s);
                }
            }
            friendscrt.notifyObservers(new FriendshipChangeEvent(ChangeEvent.UPDATE,null));
            SendMessageButton.setText("Sent");
            if(!TheChat.getSelectionModel().isEmpty())
                TheChat.getSelectionModel().clearSelection();
            CancelReplyButton.setVisible(false);
        }  catch (ValidationException | IllegalArgumentException e){
            Allert_Controller.showErrorMessage(null,e.getMessage());
        }
    }

    //----------------------------------------------------11Groups

    @FXML
    void handleSelectListUsers(MouseEvent event) {
        if(!UsersTable.getSelectionModel().isEmpty()){
            RemoveUserButton.setOpacity(1);
        }
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
            if(User_to_remove.getId().equals(u.getId())){
                Allert_Controller.showErrorMessage(null, "You cannot remove yourself from the Group you want to create!");
                return;
            }
            the_users.remove(User_to_remove);
            the_ids.remove(User_to_remove.getId());
            initModel();
            RemoveUserButton.setOpacity(0.5);
        }  catch (ValidationException | IllegalArgumentException e){
            Allert_Controller.showErrorMessage(null,e.getMessage());
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        theGroupName.clear();
        IdtoAdd.clear();
        the_users.clear();
        the_ids.clear();
        this.the_users.add(u);
        this.the_ids.add(u.getId());
        initModel();
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
        List<Long> new_the_ids = new ArrayList<>(the_ids);
        groupcrt.addGroup(name,new_the_ids, (long) the_users.size());
        theGroupName.clear();
        IdtoAdd.clear();
        this.the_users.clear();
        this.the_ids.clear();
        this.the_users.add(u);
        this.the_ids.add(u.getId());
        friendscrt.notifyObservers(new FriendshipChangeEvent(ChangeEvent.UPDATE,null));
    }

    //----------------------------------------------------11Reports
    private List<UserDTO> getFriendsListUserinPeriod(Long id) {
        return StreamSupport.stream(friendscrt.getAll().spliterator(),false)
                .filter(c -> c.getId1() == id || c.getId2() == id)
                .filter(c -> c.getStatus().equals("approved"))
                .filter(c ->c.getDate().toLocalDate().isBefore(EndDate.getValue()) || c.getDate().toLocalDate().isEqual(EndDate.getValue()))
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
                .filter(c->(c.getDate().toLocalDate().isAfter(StartDate.getValue()) || c.getDate().toLocalDate().equals(StartDate.getValue()) )
                        && (c.getDate().toLocalDate().isBefore(EndDate.getValue()) || c.getDate().toLocalDate().equals(EndDate.getValue())))
                .filter(c->c.getId1() == u.getId() || c.getId2() == u.getId())
                .filter(c->c.getStatus().equals("approved"))
                .collect(Collectors.toList());
    }

    private List<Message> getAllPrivateMessages(){
        return messagecrt.getAllMessages()
                .stream()
                .filter(c->(c.getDate().toLocalDate().isAfter(StartDate.getValue()) || c.getDate().toLocalDate().equals(StartDate.getValue()) )
                        && (c.getDate().toLocalDate().isBefore(EndDate.getValue()) || c.getDate().toLocalDate().equals(EndDate.getValue())))
                .filter(c->c.getType().equals(Messagetype.privatemessage))
                .filter(c-> c.getTo_id().equals(u.getId()))
                .collect(Collectors.toList());
    }

    private List<Message> getAllGroupMessages(){
        return messagecrt.getAllMessages()
                .stream()
                .filter(c->(c.getDate().toLocalDate().isAfter(StartDate.getValue()) || c.getDate().toLocalDate().equals(StartDate.getValue()) )
                        && (c.getDate().toLocalDate().isBefore(EndDate.getValue()) || c.getDate().toLocalDate().equals(EndDate.getValue())))
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
                .filter(c->(c.getDate().toLocalDate().isAfter(StartDate.getValue()) || c.getDate().toLocalDate().equals(StartDate.getValue()) )
                        && (c.getDate().toLocalDate().isBefore(EndDate.getValue()) || c.getDate().toLocalDate().equals(EndDate.getValue())))
                .filter(c->c.getType().equals(Messagetype.privatemessage))
                .filter(c-> c.getTo_id().equals(u.getId()))
                .filter(c-> c.getFrom_id().equals(friend.getId()))
                .collect(Collectors.toList());
    }

    private List<Message> getAllGroupMessagesfromUser(User friend){
        return messagecrt.getAllMessages()
                .stream()
                .filter(c->(c.getDate().toLocalDate().isAfter(StartDate.getValue()) || c.getDate().toLocalDate().equals(StartDate.getValue()) )
                        && (c.getDate().toLocalDate().isBefore(EndDate.getValue()) || c.getDate().toLocalDate().equals(EndDate.getValue())))
                .filter(c->c.getType().equals(Messagetype.groupmessage))
                .filter(c-> groupcrt.getGroup(c.getTo_id().toString()).getIds().contains(u.getId()) && !c.getFrom_id().equals(u.getId()))
                .filter(c-> c.getFrom_id().equals(friend.getId()))
                .collect(Collectors.toList());
    }

    @FXML
    void handleStartChanged(ActionEvent event) {
        initModel();
    }

    @FXML
    void handleEndChanged(ActionEvent event) {
        initModel();
    }

    //----------------------------------------------------11Events
    @FXML
    void handleSelectSE(MouseEvent event) {
        if(!SubscribedEventsTable.getSelectionModel().isEmpty()){
            UnsubscribeButton.setOpacity(1);
        }
    }

    @FXML
    void handleSelectAE(MouseEvent event) {
        if(!AllEventsTable.getSelectionModel().isEmpty()){
            SubscribeButton.setOpacity(1);
        }
    }

    @FXML
    void handleCreateEvent(ActionEvent event) {
        try{
            if(EventName.getText().isEmpty()){
                Allert_Controller.showErrorMessage(null,"The event needs a name!");
                return;
            }
            if(HourLayer.getText().isEmpty()){
                Allert_Controller.showErrorMessage(null,"Insert the full time of the event!");
                return;
            }
            if(MinuteLayer.getText().isEmpty()){
                Allert_Controller.showErrorMessage(null,"Insert the full time of the event!");
                return;
            }
            String eName = EventName.getText();
            if(LocalDate.now().isAfter(EventDate.getValue())){
                Allert_Controller.showErrorMessage(null,"Are u a time traveler?");
                return ;
            }
            LocalDate eDate = EventDate.getValue();
            if(LocalDate.now().equals(EventDate.getValue())){
               if(LocalDateTime.now().getHour() > Long.parseLong(HourLayer.getText())){
                   Allert_Controller.showErrorMessage(null,"Are u a time traveler?");
                    return;
               }
               else if(LocalDateTime.now().getHour() == Long.parseLong(HourLayer.getText())){
                   if(LocalDateTime.now().getMinute() > Long.parseLong(MinuteLayer.getText())){
                       Allert_Controller.showErrorMessage(null,"Are u a time traveler?");
                        return;
                   }
               }
            }
            RealEventValidator.HourValidate(HourLayer.getText());
            Long eHour = Long.parseLong(HourLayer.getText());
            RealEventValidator.MinuteValidate(HourLayer.getText());
            Long eMinute = Long.parseLong(MinuteLayer.getText());
            List<Boolean> announce = new ArrayList<>(4);
            announce.add(true);
            announce.add(true);
            announce.add(true);
            RealEvent e = eventcrt.addEvent(eName,eDate,eHour,eMinute, (long) 0,new ArrayList<>(),announce);
            eventcrt.subscribeEvent(e.getId().toString(),u.getId().toString());
            friendscrt.notifyObservers(new FriendshipChangeEvent(ChangeEvent.UPDATE,null));
            HourLayer.clear();
            MinuteLayer.clear();
            EventDate.setValue(LocalDate.now());
            EventName.clear();
            refreshpages();
        }
        catch (ValidationException e){
            Allert_Controller.showErrorMessage(null,e.getMessage());
        }
    }

    @FXML
    void handleSubscribe(ActionEvent event) {
        if(AllEventsTable.getSelectionModel().isEmpty()){
            Allert_Controller.showErrorMessage(null,"Nothing selected");
            return ;
        }

        RealEvent e = AllEventsTable.getSelectionModel().getSelectedItem();
        if(eventcrt.AreyouSubscribed(e.getId().toString(),u.getId().toString())){
            Allert_Controller.showErrorMessage(null,"You're already subscribed");
            return ;
        }
        eventcrt.subscribeEvent(e.getId().toString(),u.getId().toString());
        friendscrt.notifyObservers(new FriendshipChangeEvent(ChangeEvent.UPDATE,null));
        SubscribeButton.setOpacity(0.5);
    }

    @FXML
    void handleUnsubscribe(ActionEvent event) {
        if(SubscribedEventsTable.getSelectionModel().isEmpty()){
            Allert_Controller.showErrorMessage(null,"Nothing selected");
            return ;
        }
        RealEvent e = SubscribedEventsTable.getSelectionModel().getSelectedItem();
        eventcrt.unsubscribeEvent(e.getId().toString(),u.getId().toString());
        friendscrt.notifyObservers(new FriendshipChangeEvent(ChangeEvent.UPDATE,null));
        UnsubscribeButton.setOpacity(0.5);
    }

    @FXML
    void handleRefresh1(MouseEvent event){
        for(RealEvent e:eventcrt.listSubscribed(u.getId())){
            if(e.getDate().equals(LocalDate.now())){
                int eventTime = Integer.parseInt(e.getHour().toString())*60 + Integer.parseInt(e.getMinute().toString());
                int myTime = LocalDateTime.now().getHour()*60 + LocalDateTime.now().getMinute();
                if((eventTime - myTime) > 30 && (eventTime - myTime) <=60){
                    if(e.getAnnounce().get(0).equals(true)){
                        eventcrt.removeEvent(e.getId().toString());
                        List<Boolean> eAnnounce = new ArrayList<>();
                        eAnnounce = e.getAnnounce();
                        eAnnounce.set(0,false);
                        eventcrt.addEventID(e.getName(),e.getDate(),e.getHour(),e.getMinute(),e.getNumber_of_people(),e.getIds(),eAnnounce,e.getId());
                        Allert_Controller.showMessage(null, Alert.AlertType.INFORMATION,e.getName(),"One hour until the event starts");
                    }
                }
                else if ((eventTime - myTime) <= 30 && (eventTime - myTime) > 0){
                    if(e.getAnnounce().get(1).equals(true)){
                        eventcrt.removeEvent(e.getId().toString());
                        List<Boolean> eAnnounce = new ArrayList<>();
                        eAnnounce = e.getAnnounce();
                        eAnnounce.set(1,false);
                        eventcrt.addEventID(e.getName(),e.getDate(),e.getHour(),e.getMinute(),e.getNumber_of_people(),e.getIds(),eAnnounce,e.getId());
                        Allert_Controller.showMessage(null, Alert.AlertType.INFORMATION,e.getName(),(eventTime - myTime) + " minutes until the event starts");
                    }
                }
                else if(e.getHour() == LocalDateTime.now().getHour() && e.getMinute()==LocalDateTime.now().getMinute()){
                    Allert_Controller.showMessage(null, Alert.AlertType.INFORMATION,e.getName(),"Your event has started!");
                }
                else{
                    if(e.getAnnounce().get(2).equals(true)){
                        eventcrt.removeEvent(e.getId().toString());
                        List<Boolean> eAnnounce = new ArrayList<>();
                        eAnnounce = e.getAnnounce();
                        eAnnounce.set(2,false);
                        eventcrt.addEventID(e.getName(),e.getDate(),e.getHour(),e.getMinute(),e.getNumber_of_people(),e.getIds(),eAnnounce,e.getId());
                        Allert_Controller.showMessage(null, Alert.AlertType.INFORMATION,e.getName(),"You have an upcoming event!");
                    }
                }
            }

        }
        boolean ok=eventcrt.refresh();
        if(ok) {
            refreshpages();
            initModel();
        }
    }

    //----------------------------------------------------11Pagination
    @FXML
    void handleGetPage(MouseEvent event){
        int i=AllEventsTable.getSelectionModel().getSelectedIndex();
        if(numberofPage.getCurrentPageIndex()==pageno){
            AllEventsTable.getSelectionModel().select(i);
        }else{
            pageno=numberofPage.getCurrentPageIndex();
            observeAll.setAll(eventcrt.getEventsOnPage(pageno));
        }
    }

    void refreshpages(){
        pageno=0;
        numberofPage.setCurrentPageIndex(0);
        int more=0;
        if(eventcrt.getAllList().size()%size>0){
            more=1;
        }
        numberofPage.setPageCount(eventcrt.getAllList().size()/size+more);
        initModel();
    }

    @FXML
    void handleRefreshFriends(MouseEvent event) {
        int index=PaginationFriends.getCurrentPageIndex();
        if(friends_size-1>index || (friends_lastpage==0 && friends_size!=0)){
            modelGrade1.setAll(friendscrt.getFriendsListUser(u.getId(),user_crt)
                    .subList(index*size,(index+1)*size));
        }else{
            if(friends_size!=0)
                modelGrade1.setAll(friendscrt.getFriendsListUser(u.getId(),user_crt)
                        .subList(index*size,index*size+friends_lastpage));
        }
    }

    @FXML
    void handleRefreshGroups(MouseEvent event) {
        int i=GroupTable.getSelectionModel().getSelectedIndex();
        int index=PaginationGroups.getCurrentPageIndex();
        if(groups_size-1>index || (groups_lastpage==0 && groups_size!=0)){
            modelGrademes1.setAll(groupcrt.getAllGroupsofUser(u.getId())
                    .subList(index*size,(index+1)*size));
        }else{
            if(groups_size!=0)
                modelGrademes1.setAll(groupcrt.getAllGroupsofUser(u.getId())
                        .subList(index*size,index*size+groups_lastpage));
        }
        GroupTable.getSelectionModel().select(i);
    }

    @FXML
    void handleRefreshReceived(MouseEvent event) {

        int index=PaginationReveivedRequest.getCurrentPageIndex();
        if(received_size-1>index || (received_lastpage==0 && received_size!=0)){
            modelGrade2.setAll(friendscrt.getRequestListUser(u.getId(),user_crt)
                    .subList(index*size,(index+1)*size));
        }else{
            if(received_size!=0)
                modelGrade2.setAll(friendscrt.getRequestListUser(u.getId(),user_crt)
                        .subList(index*size,index*size+received_lastpage));
        }
    }

    @FXML
    void handleRefreshSE(MouseEvent event) {
        int i=SubscribedEventsTable.getSelectionModel().getSelectedIndex();
        int index=PaginationSubscribedEvents.getCurrentPageIndex();
        if(SE_size-1>index || (SE_lastpage==0 && SE_size!=0)){
            observeSubscribe.setAll(eventcrt.listSubscribed(u.getId())
                    .subList(index*size,(index+1)*size));
        }else{
            if(SE_size!=0)
                observeSubscribe.setAll(eventcrt.listSubscribed(u.getId())
                        .subList(index*size,index*size+SE_lastpage));
        }
        SubscribedEventsTable.getSelectionModel().select(i);
    }

    @FXML
    void handleRefreshSent(MouseEvent event) {
        int index=PaginationSentRequest.getCurrentPageIndex();
        if(sent_size-1>index || (sent_lastpage==0 && sent_size!=0)){
            modelGrade3.setAll(friendscrt.getSentRequestListUser(u.getId(),user_crt)
                    .subList(index*size,(index+1)*size));
        }else{
            if(sent_size!=0)
                modelGrade3.setAll(friendscrt.getSentRequestListUser(u.getId(),user_crt)
                        .subList(index*size,index*size+sent_lastpage));
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
    private Button RemoveFriendshipButton;

    @FXML
    private TableView<UserDTO> FriendsTable1;

    @FXML
    private TableColumn<UserDTO, String> FriendsFirstName;

    @FXML
    private TableColumn<UserDTO, String> FriendsLastName;

    @FXML
    private TableColumn<UserDTO, LocalDateTime> FriendsDate;

    @FXML
    private Button RefuseRequestButton;

    @FXML
    private Button AcceptRequestButton;

    @FXML
    private Button RemoveSRequest;

    @FXML
    private Button SendRequestButton;

    @FXML
    private TableView<UserDTO> SentRequestsTable;

    @FXML
    private TableColumn<UserDTO, String> SRequestsFirstName;

    @FXML
    private TableColumn<UserDTO, String> SRequestsLastName;

    @FXML
    private TableColumn<UserDTO, LocalDateTime> SRequestsDate;

    @FXML
    private TableColumn<UserDTO, String> RFRStatusColumn;

    @FXML
    private TableColumn<UserDTO, String> SFRStatusColumn;


    @FXML
    private Text idText;

    @FXML
    private Text firstnameText;

    @FXML
    private Text lastnameText;

    @FXML
    private Text FavFoodText;

    @FXML
    private Text ageText;

    @FXML
    private Text emailText;

    @FXML
    private Text noFriendsText;

    @FXML
    private TextField IdtoSendRequest;

    @FXML
    private TableView<Group> GroupTable;

    @FXML
    private TableColumn<Group, LocalDateTime> DateColumn1;

    @FXML
    private TableColumn<Group, String> Group_NameColumn;

    @FXML
    private ListView<String> TheChat;

    @FXML
    private Button LeaveGroupButton;

    @FXML
    private Button GroupChatButton;

    @FXML
    private Button OpenPrivateChatButton;

    @FXML
    private TextField privatechatidtext;

    @FXML
    private TextField MessageField;

    @FXML
    private Button SendMessageButton;

    @FXML
    private TableView<User> MembersTable;

    @FXML
    private TableColumn<User, String> FirstNameColumnMembers;

    @FXML
    private TableColumn<User, String> LastNameColumnMembers;

    @FXML
    private Text NameChat;

    @FXML
    private Text MembersText;

    @FXML
    private TextField theGroupName;

    @FXML
    private TextField IdtoAdd;

    @FXML
    private TableView<User> UsersTable;

    @FXML
    private TableColumn<User, String> FirstNameColumn;

    @FXML
    private TableColumn<User, String> LastNameColumn1;

    @FXML
    private Button AddButton;

    @FXML
    private Button FinishButton;

    @FXML
    private Button CancelReplyButton;

    @FXML
    private Button RemoveUserButton;

    @FXML
    private DatePicker StartDate;

    @FXML
    private DatePicker EndDate;

    @FXML
    private TableView<UserDTO> FriendsTable;

    @FXML
    private TableColumn<UserDTO, String> FristNameColumn;

    @FXML
    private TableColumn<UserDTO, String> LastNameColumn;

    @FXML
    private TableColumn<UserDTO, LocalDateTime> DateColumn;

    @FXML
    private Button PDFButton1;

    @FXML
    private Button PDFButton2;

    @FXML
    private Button handleCancelReply;

    @FXML
    private Button LogOutButton;

    @FXML
    private Button CreateEventButton;

    @FXML
    private TextField EventName;

    @FXML
    private DatePicker EventDate;

    @FXML
    private TableView<RealEvent> AllEventsTable;

    @FXML
    private TableColumn<RealEvent, String> AEName;

    @FXML
    private TableColumn<RealEvent, LocalDate> AEDate;

    @FXML
    private TableColumn<RealEvent, Long> AEHour;

    @FXML
    private TableColumn<RealEvent, Long> AEMinute;

    @FXML
    private TableColumn<RealEvent, Long> AENoMembers;

    @FXML
    private Button UnsubscribeButton;

    @FXML
    private TextField HourLayer;

    @FXML
    private TextField MinuteLayer;

    @FXML
    private TableView<RealEvent> SubscribedEventsTable;

    @FXML
    private TableColumn<RealEvent, String> SEName;

    @FXML
    private TableColumn<RealEvent, LocalDate> SEDate;

    @FXML
    private TableColumn<RealEvent, Long> SEHour;

    @FXML
    private TableColumn<RealEvent, Long> SEMinute;

    @FXML
    private TableColumn<RealEvent, Long> SENoMembers;

    @FXML
    private Button SubscribeButton;

    @FXML
    private Pagination numberofPage;

    @FXML
    private Pagination PaginationSubscribedEvents;

    @FXML
    private Pagination PaginationGroups;

    @FXML
    private Pagination PaginationSentRequest;

    @FXML
    private Pagination PaginationReveivedRequest;

    @FXML
    private Pagination PaginationFriends;
}
