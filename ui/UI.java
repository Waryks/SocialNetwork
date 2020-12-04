package socialnetwork.ui;

import socialnetwork.domain.Friends;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.FriendService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;


import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.StreamSupport;

public class UI {
    private UserService userService;
    private FriendService friendService;
    private MessageService messageService;
    private Scanner sin = new Scanner(System.in);
    public UI(UserService userService, FriendService friendService, MessageService messageService){
        this.userService = userService;
        this.friendService = friendService;
        this.messageService = messageService;
    }
    private void addFriend(){

        System.out.println("Id number one: ");
        String id1 = sin.next();
        System.out.println("Id number two: ");
        String id2 = sin.next();
        try{
            friendService.addFriend(id1,id2,userService);
            System.out.println("The friend request from " + id1 + " to " + id2 + " is pending!\n");
        }
        catch(ValidationException exception ){
            System.out.println(exception.getMessage());
        }
    }

    private void addUser(){
        try{
            System.out.println("First name: ");
            String firstName = sin.next();
            UserValidator.isName(firstName);
            System.out.println("Last name: ");
            String lastName = sin.next();
            UserValidator.isName(lastName);
            userService.addUser(firstName,lastName);
            System.out.println("User "+ firstName + " " + lastName + " has been added!\n");
        }
        catch(ValidationException exception ){
            System.out.println(exception.getMessage());
        }
    }

    private void removeFriend(){

        System.out.println("Id number one: ");
        String id1 = sin.next();
        System.out.println("Id number two: ");
        String id2 = sin.next();

        try{
            friendService.removeFriend(id1,id2);
            System.out.println("The friends " + id1 + ":" + id2 + " were removed!\n");
        }
        catch(ValidationException exception ){
            System.out.println(exception.getMessage());
        }
    }

    private void removeUser(){

        System.out.println("The id of the user which you want to be removed: ");
        String id = sin.next();

        try{
            userService.removeUser(id);
            long idLeft = FriendValidator.is_long(id);
            for (Friends friend : friendService.getAllFriends()) {
                String id1 = String.valueOf(friend.getIdLeft()),id2 = String.valueOf(friend.getIdRight());
                //System.out.println("ID:"+id+ " " + id1+" "+id2);
                if(id.equals(id1) || id.equals(id2)){
                    //System.out.println("Am intrat aici!");
                    friendService.removeFriend(id1,id2);
                }
            }
            System.out.println("User with the id " + id + " has been removed!\n");
        }
        catch(ValidationException exception ){
            System.out.println(exception.getMessage());
        }
    }
    private void showFriends(){
        try{
            System.out.println("The id of the person: ");
            String id = sin.next();
            long id_person = FriendValidator.is_long(id);
            List<String> fr = friendService.friendPerID(id_person, userService.getAllUsers());
            System.out.println("His friends are: ");
            for(String friend:fr){
                System.out.println(friend );
            }
        }
        catch(ValidationException exception ){
            System.out.println(exception.getMessage());
        }
    }
    private void showFriendsYM(){
        try{
            System.out.println("The id of the person: ");
            String id = sin.next();
            long id_person = FriendValidator.is_long(id);
            System.out.println("The year when the friends were made: ");
            String year = sin.next();
            FriendValidator.is_int(year);
            System.out.println("The month when the friends were made: ");
            String month = sin.next();
            FriendValidator.is_int(month);
            List<String> fr = friendService.friendPerIDYM(id_person, year, month,userService.getAllUsers());
            System.out.println("His friends are: ");
            for(String friend:fr){
                System.out.println(friend );
            }
        }
        catch(ValidationException exception ){
            System.out.println(exception.getMessage());
        }
    }
    public void message(){
        try{
            System.out.println("Who sends the message(id): ");
            String idFrom = sin.next();
            System.out.println("To how many people you want to send? ");
            String n = sin.next();
            List <String> idTo = new ArrayList<>();
            int number = MessageValidator.is_int(n);
            for(int index = 0; index < number; index++){
                System.out.println("Id of the person: ");
                idTo.add(index,sin.next());
            }
            String message = null, reply = null;
            System.out.println("Message: ");
            message = sin.nextLine();
            while (true) {
                String str = sin.nextLine();
                if (str.equalsIgnoreCase("")) break;
                message += str;
            }
            System.out.println("Seen or not to seen? Y/N");
            String answer = sin.next();
            if(answer.equals("N")) {
                System.out.println("Their reply: ");
                reply = sin.next();
                while (true) {
                    String s = sin.nextLine();
                    if (s.equalsIgnoreCase("")) break;
                    reply += s;
                }
            }
            else
                reply = "";
            /*System.out.println("Their reply: ");
            reply = sin.next();
            while (true) {
                String s = sin.nextLine();
                if (s.equalsIgnoreCase("")) break;
                reply += s;
            }*/
            messageService.addMessage(idFrom,idTo,message,reply);
            System.out.println("The message was sent!");
        }
        catch(ValidationException exception ){
            System.out.println(exception.getMessage());
        }
    }
    public void deleteMessage(){
        try{
            System.out.println("Give the id that u want to be deleted: ");
            String id = sin.next();
            messageService.removeMessage(id);
        }
            catch(ValidationException exception ){
            System.out.println(exception.getMessage());
        }

    }
    public void nrComunity(){
        int nr = friendService.Comunity(userService.getAllUsers().size()+1);
        if(nr == 0)
            System.out.println("No comunity exists");
        else
            System.out.println("The number of comunities are: " + nr);
    }
    public void nrComunityMax(){
        friendService.ComunityMax(userService.getAllUsers().size()+1);
    }
    public void Conversation(){
        System.out.println("Who sends the message(id): ");
        String idFrom = sin.next();
        System.out.println("To how many people you want to send? ");
        String n = sin.next();
        List <String> idTo = new ArrayList<>();
        int number = MessageValidator.is_int(n);
        for(int index = 0; index < number; index++){
            System.out.println("Id of the person: ");
            idTo.add(index,sin.next());
        }
        messageService.showConversation(idFrom,idTo);
    }
    public void respond(){
        System.out.println("Id number one: ");
        String id1 = sin.next();
        System.out.println("Id number two: ");
        String id2 = sin.next();
        System.out.println("accept|decline");
        String response = sin.next();
        try{
            friendService.respondRequest(id1,id2,userService,response);
            System.out.println("The friend request from " + id1 + " to " + id2 + " has been "+response+"ed!\n");
        }
        catch(ValidationException exception ){
            System.out.println(exception.getMessage());
        }
    }

    public void showMenu(){
        System.out.println("Choose option:\n" +
                //            "0: Hidden option for showing all of the lists\n"+
                "M: Menu\n" +
                "1: Add friend\n" +
                "2: Add user\n" +
                "3: Remove friend\n" +
                "4: Remove user\n" +
                "5: Number of comunities\n" +
                "6: Biggest comunity and its component\n" +
                "7: All the friends of a person\n" +
                "8: All the friends of a person from a specific year and month\n" +
                "9: Messenger \n" +
                "10: The conversation of certain users \n" +
                "11: Respond to friend requests \n" +
                "Exit\n");
    }
    public void menu(){
        showMenu();
        while(true){
            System.out.println("Choose command\n:");
            String command = sin.next();
            switch(command){
                case "M":
                    showMenu();
                    break;
                case "0":
                    /*System.out.println(userService.getAll());
                   */ System.out.println("Users: ");
                    for (User user : userService.getAllUsers()) {
                        System.out.println(user);
                    }
                    System.out.println("Friends: ");
                    for (Friends friend : friendService.getAllFriends()) {
                        System.out.println(friend+" "+friend.getDate());
                    }
                    System.out.println("Messages: ");
                    for(Message message: messageService.getAllMessages()){
                        System.out.println(message.getFrom() + " " + message.getTo() + "\nMessage: " + message.getMessageText() + "\nReply: " + message.getReply());
                    }
                    break;
                case "1":
                    addFriend();
                    break;
                case "2":
                    addUser();
                    break;
                case "3":
                    removeFriend();
                    break;
                case "4":
                    removeUser();
                    break;
                case "5":
                    nrComunity();
                    break;
                case "6":
                    nrComunityMax();
                    break;
                case "7":
                    showFriends();
                    break;
                case "8":
                    showFriendsYM();
                    break;
                case "9":
                    message();
                    break;
                case "10":
                    Conversation();
                    break;
                case "11":
                    respond();
                    break;
                default:
                    System.out.println("Goodbye! :^)\n");
                    return;
            }

        }
    }
}
