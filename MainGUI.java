package socialnetwork;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import socialnetwork.config.ApplicationContext;
import socialnetwork.controller.LoginController;
import socialnetwork.controller.MenuController;
import socialnetwork.domain.Friends;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendValidator;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.Repository;
import socialnetwork.repository.file.FriendsFile;
import socialnetwork.repository.file.MessageFile;
import socialnetwork.repository.file.UserFile;
import socialnetwork.service.FriendService;
import socialnetwork.service.MessageService;
import socialnetwork.service.UserService;

public class MainGUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader=new FXMLLoader();
        loader.setLocation(getClass().getResource("/views/login.fxml"));
        Parent root=loader.load();

        String users_fileName="d:\\JAVA\\MaSinucid3\\data\\users.csv";
        String friends_fileName="d:\\JAVA\\MaSinucid3\\data\\friends.csv";
        String message_fileName="d:\\JAVA\\MaSinucid3\\data\\messages.csv";

        Repository<Long, User> userFileRepository = new UserFile(users_fileName, new UserValidator());
        UserService userService = new UserService(userFileRepository);

        Repository<Tuple<Long,Long>, Friends> friendFileRepository = new FriendsFile(friends_fileName, new FriendValidator());
        FriendService friendService = new FriendService(friendFileRepository);

        Repository<Long, Message> messageFileRepository = new MessageFile(message_fileName, new MessageValidator());
        MessageService messageService = new MessageService(messageFileRepository,userService,friendService);

        LoginController ctrl=loader.getController();
        ctrl.setService(userService,messageService,friendService);
        ctrl.setPrimaryStage(primaryStage);

        primaryStage.setScene(new Scene(root, 750, 450));
        primaryStage.setTitle("My Social Network");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

}