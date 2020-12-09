package socialnetwork.service;

import socialnetwork.domain.Friends;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.Repository;
import socialnetwork.utils.events.MessageEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageService implements Observable<MessageEvent> {
    private UserService userService;
    private FriendService friendService;
    private Repository<Long, Message> repo;
    public MessageService(Repository<Long, Message> repo,UserService userService,FriendService friendService) {
        this.userService = userService;
        this.friendService = friendService;
        this.repo = repo;
    }
    public Message addMessage(String id_f, List<String> id_t, String message, String reply) {
        long id = 0;
        long max = 0;
        int index = 0;
        Long id_from = MessageValidator.is_long(id_f);
        List<Long> id_to = new ArrayList<>();
        for(String id_tAux: id_t){
            long idAux = MessageValidator.is_long(id_tAux);
            id_to.add(index,idAux);
            index++;
        }
        if(id_to.size()==1){
            if(id_from>id_to.get(0)){
                long aux = id_from;
                id_from = id_to.get(0);
                id_to.set(0,aux);
                String msg_aux = message;
                message = reply;
                reply = msg_aux;
            }

        }
        User from = new User();
        List<User> to = new ArrayList<>();
        index = 0;
        for (User user : userService.getAllUsers()) {
            if(user.getId().equals(id_from))
                from = user;
            for(long id_tAux:id_to){
                if(id_tAux == user.getId()){
                    to.add(index,user);
                    index++;
                }
            }
        }
        MessageValidator.validateList(to);
        new Message();
        Message task;
        Message mess = new Message();
        for(Message msg : getAllMessages()){
            if(msg.getFrom().equals(from) && msg.getTo().equals(to)){
                id = msg.getId();
                repo.delete(id,getAllMessages());
                String m = msg.getMessageText();
                String r = msg.getReply();
                mess = new Message(id,from,to,m+"|"+message,r+"|"+reply);
                break;
            }
        }
        if(id == 0)
        {
            for (Message msg : getAllMessages()) {
                id = msg.getId();
                if(max<id){
                    max = id;
                }
            }
            mess = new Message(max+1,from,to,message,reply);
        }
        task = repo.save(mess);
        return task;
    }
    public void showConversation(String id1, List<String> id2){
        long id = 0;
        long max = 0;
        int index = 0;
        Long id_from = MessageValidator.is_long(id1);
        List<Long> id_to = new ArrayList<>();
        for(String id_tAux: id2){
            long idAux = MessageValidator.is_long(id_tAux);
            id_to.add(index,idAux);
            index++;
        }
        User from = new User();
        List<User> to = new ArrayList<>();
        index = 0;
        for (User user : userService.getAllUsers()) {
            if(user.getId().equals(id_from))
                from = user;
            for(long id_tAux:id_to){
                if(id_tAux == user.getId()){
                    to.add(index,user);
                    index++;
                }
            }
        }
        Message mess = new Message();
        for(Message msg : getAllMessages()){
            if(msg.getFrom().equals(from) && msg.getTo().equals(to)){
                List<String> message = Arrays.asList(msg.getMessageText().split("\\|"));
                List<String> reply = Arrays.asList(msg.getReply().split("\\|"));
                for(index = 0; index < message.size(); index++){
                    System.out.println("Message: "+message.get(index)+"\n Reply: "+reply.get(index));
                }
            }
        }
    }

    public void removeMessage(String id) {
        long id_message;
        id_message = MessageValidator.is_long(id);
        List<Message> mes;
        mes = getAllMessages();
        repo.delete(id_message,mes);
    }

    public List<Message> getAllMessages() {
        Iterable<Message> messages = repo.findAll();
        return StreamSupport.stream(messages.spliterator(),false).collect(Collectors.toList());
    }
    public Iterable<Message> getAll(){
        return repo.findAll();
    }

    private List<Observer<MessageEvent>> observers=new ArrayList<>();
    @Override
    public void addObserver(Observer<MessageEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessageEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageEvent t) {
        observers.stream().forEach(x->x.update(t));
    }
}
