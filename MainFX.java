package socialnetwork;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import socialnetwork.controller.LogIn_Controller;
import socialnetwork.domain.*;
import socialnetwork.domain.validators.*;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.*;
import socialnetwork.repository.file.*;
import socialnetwork.service.*;

public class MainFX extends Application {
    private UserService usercrt;
    private FriendshipService friendscrt;
    private MessageService messagecrt;
    private GroupService groupcrt;
    private RealEventService eventcrt;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //files
        //repository
        //Repository<Long, User> userFileRepository = new UserFile(fileName_users, new UserValidator());
        //Repository<Tuple<Long,Long>, Friendship> friendshipFileRepository= new FriendshipFile(fileName_friends, new FriendshipValidator());
        //Repository<Long, Message> messageFileRepository = new MessageFile(fileName_messages, new MessageValidator());
        //Repository<Long, Group> GroupFileRepository = new GroupFile(fileName_groups, new GroupValidator());
        //RealEventFile realEventRepository = new RealEventFile(fileName_events, new RealEventValidator());
        //-----------------------------------------------------------------------------------------------------------
        Repository<Long, User> userFileRepository = new UserBD("jdbc:postgresql://localhost:5432/postgres","postgres","DEXTER13ALX", new UserValidator());
        Repository<Long, Message> messageFileRepository = new MessageBD("jdbc:postgresql://localhost:5432/postgres","postgres","DEXTER13ALX", new MessageValidator());
        Repository<Tuple<Long,Long>, Friendship> friendshipFileRepository= new FriendshipBD("jdbc:postgresql://localhost:5432/postgres","postgres","DEXTER13ALX", new FriendshipValidator());
        Repository<Long, Group> GroupFileRepository = new GroupBD("jdbc:postgresql://localhost:5432/postgres","postgres","DEXTER13ALX", new GroupValidator());
        RealEventBD realEventRepository = new RealEventBD("jdbc:postgresql://localhost:5432/postgres","postgres","DEXTER13ALX", new RealEventValidator());
        /*User user = new User("Alex","Turcut","a@t.com","123","12","esd");
        userFileRepository.save(user);*/
        //services
        this.usercrt=new UserService(userFileRepository);
        this.friendscrt=new FriendshipService(friendshipFileRepository);
        this.messagecrt=new MessageService(messageFileRepository,usercrt,friendscrt);
        this.groupcrt=new GroupService(GroupFileRepository);
        this.eventcrt=new RealEventService(realEventRepository);
        init1(primaryStage);

    }

    private void init1(Stage primaryStage) throws Exception{
        //main menu
        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/Menu.fxml"));
        AnchorPane root=loader.load();

        LogIn_Controller ctrl=loader.getController();
        ctrl.setService(usercrt,friendscrt,messagecrt,groupcrt,eventcrt);
        ctrl.setPrimaryStage(primaryStage);

        primaryStage.setScene(new Scene(root, 750, 450));
        primaryStage.setTitle("My Social Network");
        primaryStage.show();
    }


}
