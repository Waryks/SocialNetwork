/*
package socialnetwork.ui;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.service.FriendshipService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UI {
    private UserService user_crt;
    private FriendshipService friendship_crt;
    private MessageService message_crt;

    public UI(UserService user_crt, FriendshipService friendship_crt,MessageService message_crt) {
        this.user_crt = user_crt;
        this.friendship_crt = friendship_crt;
        this.message_crt = message_crt;
    }


    public void addUserUi(){
        try{
            Scanner f = new Scanner(System.in);
            System.out.println("Add First Name:");
            String firstname = f.next();
            UserValidator.firstNameValidate(firstname);

            System.out.println("Add Last Name:");
            String lastname = f.next();
            UserValidator.lastNameValidate(lastname);

            System.out.println("Add Age:");
            String age = f.next();
            UserValidator.ageValidate(age);

            System.out.println("Add Favourite Food:");
            String favF = f.next();
            UserValidator.favFoodValidate(favF);

            user_crt.addUser(firstname,lastname,age,favF);
            System.out.println("The user was succesfully added!");
            System.out.println("Welcome!");
        }catch(ValidationException | IllegalArgumentException exp){
            System.out.println(exp.getMessage());
        }
    }

    public void removeUserUi(){
        try{
            Scanner f = new Scanner(System.in);
            System.out.println("The Id of the User you want to delete:");
            String Id_to_remove_s=f.next();
            UserValidator.idExistenceValidate(Id_to_remove_s,user_crt.getAll());

            user_crt.removeUser(Id_to_remove_s);
            friendship_crt.removeUserFriends(Id_to_remove_s);
            System.out.println("The User was succesfully removed!");
        }catch(ValidationException | IllegalArgumentException exp){
            System.out.println(exp.getMessage());
        }
    }

    public void removeFriendshipUI(){
        try {
            Scanner f = new Scanner(System.in);

            System.out.println("The id of the 1st friend:");
            String id1_s = f.next();
            UserValidator.idExistenceValidate(id1_s,user_crt.getAll());

            System.out.println("The id of the 2nd friend:");
            String id2_s = f.next();
            UserValidator.idExistenceValidate(id2_s,user_crt.getAll());

            FriendshipValidator.idsExistenceValidate(id1_s,id2_s,friendship_crt.getAll());

            Friendship new_f = friendship_crt.removeFriendship(id1_s, id2_s);
            System.out.println("The Friendship was succesfully removed!So sad!");
        }catch(ValidationException | IllegalArgumentException exp){
            System.out.println(exp.getMessage());
        }
    }

    public void showFriendsofUserUI(){
        try{
            Scanner f = new Scanner(System.in);

            System.out.println("The id of User:");
            String id1_s = f.next();
            UserValidator.idExistenceValidate(id1_s,user_crt.getAll());

            List<String> list_of_friends = friendship_crt.getAllFriendsUser(id1_s,user_crt);
            for(String friend:list_of_friends){
                System.out.println(friend);
            }
            if(list_of_friends.isEmpty()){
                System.out.println("No Friends yet :'(");
            }
        }catch(ValidationException | IllegalArgumentException exp){
            System.out.println(exp.getMessage());
        }
    }

    public void showFriendsofUserUIMonth(){
        try{
            Scanner f = new Scanner(System.in);

            System.out.println("The id of User:");
            String id1_s = f.next();
            UserValidator.idExistenceValidate(id1_s,user_crt.getAll());

            System.out.println("The month:");
            String month = f.next();
            FriendshipValidator.monthValidate(month);

            List<String> list_of_friends = friendship_crt.getAllFriendsUserMonth(id1_s,month,user_crt);
            for(String friend:list_of_friends){
                System.out.println(friend);
            }
            if(list_of_friends.isEmpty()){
                System.out.println("No Friends from that time yet!");
            }
        }catch(ValidationException | IllegalArgumentException exp){
            System.out.println(exp.getMessage());
        }
    }

    public void SendFriendRequestUI(){
        try{
            Scanner f = new Scanner(System.in);

            System.out.println("The id of the sender:");
            String id1_s = f.next();
            UserValidator.idExistenceValidate(id1_s,user_crt.getAll());

            System.out.println("The id of the receiver:");
            String id2_s = f.next();
            UserValidator.idExistenceValidate(id2_s,user_crt.getAll());

            FriendshipValidator.idsNonExistenceValidate(id1_s,id2_s,friendship_crt.getAll());

            Friendship new_f = friendship_crt.addFriendship(id1_s,id2_s);
            System.out.println("The Friend Request was sent succesfully!");
        }catch(ValidationException | IllegalArgumentException exp){
            System.out.println(exp.getMessage());
        }
    }

    public void respondFriendRequestUI(){
        try{
            Scanner f = new Scanner(System.in);

            System.out.println("The id of the receiver:");
            String id1_s = f.next();
            UserValidator.idExistenceValidate(id1_s,user_crt.getAll());

            System.out.println("The id of the sender:");
            String id2_s = f.next();
            UserValidator.idExistenceValidate(id2_s,user_crt.getAll());

            FriendshipValidator.FriendRequestExistence(id2_s,id1_s,friendship_crt.getAll());

            System.out.println("The Response:(yes/no)");
            String response = f.next();
            FriendshipValidator.responseValidate(response);
            if(response.equals("yes")){
                friendship_crt.respondFriendRequest(id1_s,id2_s,"approved");
                System.out.println("The Friend Request was approved succesfully!");
            }
            if(response.equals("no")){
                //friendship_crt.respondFriendRequest(id1_s,id2_s,"rejected");    --in caz ca dorim sa salvam versiunea rejected(un fel de block)
                friendship_crt.removeFriendship(id1_s,id2_s);
                System.out.println("The Friend Request was rejected succesfully!");
            }
        }catch(ValidationException | IllegalArgumentException exp){
            System.out.println(exp.getMessage());
        }
    }

    public void sendMessageUI(){
        try {
            Scanner f = new Scanner(System.in);

            System.out.println("The id of the sender:");
            String from = f.next();
            UserValidator.idExistenceValidate(from,user_crt.getAll());

            System.out.println("To how many friends you want to send this message:");
            String nr = f.next();
            MessageValidator.NumberValidate(nr);
            Long nrl = Long.parseLong(nr);

            List <String> to = new ArrayList<>();
            System.out.println("Insert the friend's ids:");
            for(int i=1;i<=nrl;i++){
                System.out.println("Id number " + i + " :");
                String idf = f.next();
                UserValidator.idExistenceValidate(idf,user_crt.getAll());
                MessageValidator.differentidsValidate(idf,to,from);
                to.add(idf);
            }
            System.out.println("Insert your message:");
            String mess = f.nextLine();
            String part;
            while(true)
            {
                part=f.nextLine();
                if (part.equalsIgnoreCase("")) break;
                mess=mess+" "+part;
            }
            message_crt.addMessage(from,to,mess,nrl);
            System.out.println("The Message was sent succesfully!");
        }catch(ValidationException | IllegalArgumentException exp){
            System.out.println(exp.getMessage());
        }
    }

    public void ConversationUI(){
        try {
            Scanner f = new Scanner(System.in);

            System.out.println("The id of the 1st friend:");
            String id1_s = f.next();
            UserValidator.idExistenceValidate(id1_s,user_crt.getAll());

            System.out.println("The id of the 2nd friend:");
            String id2_s = f.next();
            UserValidator.idExistenceValidate(id2_s,user_crt.getAll());

            List<String> conv=message_crt.Conv(id1_s,id2_s);
            if(!conv.isEmpty()){
                System.out.println("--The start of the conversation--\n");
                for(String line:conv){
                    System.out.println(line);
                }
                System.out.println("\n--The end of the conversation--");
            }else{
                System.out.println("This Conversation has no messages yet!");
            }
        }catch(ValidationException | IllegalArgumentException exp){
            System.out.println(exp.getMessage());
        }
    }

    public void respondGroupChat(){
        try {
            Scanner f = new Scanner(System.in);

            System.out.println("The id of the User who wants to reply:");
            String from = f.next();
            UserValidator.idExistenceValidate(from,user_crt.getAll());

            System.out.println("The id of the Group Chat:");
            String id_group = f.next();
            MessageValidator.idValidate(id_group);
            MessageValidator.idExistenceValidatefrom(id_group,message_crt.getAll(),from);

            System.out.println("Insert your reply:");
            String mess = f.nextLine();
            String part;
            while(true)
            {
                part=f.nextLine();
                if (part.equalsIgnoreCase("")) break;
                mess=mess+" "+part;
            }

            System.out.println("Mesajul tau:"+mess);
            message_crt.respondGroupchat(from,id_group,mess);
            System.out.println("The Reply was sent succesfully!");
        }catch(ValidationException | IllegalArgumentException exp){
            System.out.println(exp.getMessage());
        }
    }

    public void run(){
        Scanner in=new Scanner(System.in);
        Running:while(true){
            System.out.println(
                    "--The List of Commands:\n" +
                    "--0:Exit\n" +
                    "--1:Show Users\n" +
                    "--2:Add User\n" +
                    "--3:Remove User\n" +
                    "--4:Send Friend Request\n" +
                    "--5:Respond Friend Request\n" +
                    "--6:Remove Friend Request or approved/rejected Friendship\n" +
                    "--7:Show All Friends of an User\n" +
                    "--8:Show All Friends of an User made in a specific month\n" +
                    "--9:Message Users\n" +
                    "--10:Show Conversation of 2 Users\n" +
                    "--11:Respond to a group chat\n" +
                    "--39:Show All Messages\n" +
                    "--40:Show All Friendships\n\n" +
                    "()Your Command:");
            String cmd = in.next();
            while(!cmd.matches("[//-]?[1-9][0-9]*") && !cmd.matches("0")){
                System.out.println("The Command needs to be a number!");
                System.out.println("Your Command:");
                cmd=in.next();
            }
            int nr = Integer.parseInt(cmd);
            switch(nr)
            {
                case 0:
                    System.out.println("Exiting...");
                    break Running;
                case 1:
                    System.out.println("Users:");
                    Iterable<User> users=user_crt.getAll();;
                    for(User i:users){
                        System.out.println(i);
                    }
                    System.out.println("");
                    break;
                case 2:
                    System.out.println("Adding User...");
                    addUserUi();
                    break;
                case 3:
                    System.out.println("Removing User...");
                    removeUserUi();
                    break;
                case 4:
                    System.out.println("Sending Friend Request...");
                    SendFriendRequestUI();
                    break;
                case 5:
                    System.out.println("Responding Friend Request...");
                    respondFriendRequestUI();
                    break;
                case 6:
                    System.out.println("Removing Friendship...");
                    removeFriendshipUI();
                    break;
                case 7:
                    System.out.println("Showing Friends of an User...");
                    showFriendsofUserUI();
                    break;
                case 8:
                    System.out.println("Showing Friends of an User in a specific month...");
                    showFriendsofUserUIMonth();
                    break;
                case 9:
                    System.out.println("Messaging...");
                    sendMessageUI();
                    break;
                case 10:
                    System.out.println("Showing conversation beetween 2 Users...");
                    ConversationUI();
                    break;
                case 11:
                    System.out.println("Responding Group Chat...");
                    respondGroupChat();
                    break;
                case 39:
                    System.out.println("Messages:");
                    Iterable<Message> messages=message_crt.getAll();;
                    for(Message i:messages){
                        System.out.println(i);
                    }
                    System.out.println("");
                    break;
                case 40:
                    System.out.println("Friendships:");
                    Iterable<Friendship> friends=friendship_crt.getAll();;
                    for(Friendship i:friends){
                        System.out.println(i);
                    }
                    System.out.println("");
                    break;
                default:
                    System.out.println("There is no valid command with that number!");
                    break;
            }
        }
    }
}
*/
