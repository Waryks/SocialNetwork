package socialnetwork;

import socialnetwork.config.ApplicationContext;
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
import socialnetwork.ui.UI;

public class Main {
    public static void main(String[] args) {
        String users_fileName="d:\\JAVA\\MaSinucid3\\data\\users.csv";
        String friends_fileName="d:\\JAVA\\MaSinucid3\\data\\friends.csv";
        String message_fileName="d:\\JAVA\\MaSinucid3\\data\\messages.csv";
        //String fileName="data/users.csv";
        /*Repository0<Long,Utilizator> userFileRepository = new UtilizatorFile0(users_fileName
                , new UtilizatorValidator());*/

        Repository<Long, User> userFileRepository = new UserFile(users_fileName, new UserValidator());
        UserService userService = new UserService(userFileRepository);

        Repository<Tuple<Long,Long>, Friends> friendFileRepository = new FriendsFile(friends_fileName, new FriendValidator());
        FriendService friendService = new FriendService(friendFileRepository);

        Repository<Long, Message> messageFileRepository = new MessageFile(message_fileName, new MessageValidator());
        MessageService messageService = new MessageService(messageFileRepository,userService,friendService);

        UI ui = new UI(userService,friendService,messageService);
        /*Utilizator use = new Utilizator("Florin","Peste");
        long id = 69;
        use.setId(id);
        System.out.println(use);
        userFileRepository.save(use);*/
        ui.menu();
    }
}


