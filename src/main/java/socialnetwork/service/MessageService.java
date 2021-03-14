package socialnetwork.service;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Message;
import socialnetwork.domain.Messagetype;
import socialnetwork.domain.validators.MessageValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.utils.events.ChangeEvent;
import socialnetwork.utils.events.MessageGChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class MessageService implements Observable<MessageGChangeEvent> {
    private UserService user_crt;
    private FriendshipService friends_crt;
    private Repository<Long, Message> repo;
    private List<Observer<MessageGChangeEvent>> observers=new ArrayList<>();

    public MessageService(Repository<Long, Message> repo, UserService user_crt, FriendshipService friends_crt) {
        this.repo = repo;
        this.user_crt = user_crt;
        this.friends_crt = friends_crt;
    }
    /**
     * create an id which isn't used at the moment by noone
     * @return a new id
     */
    public long getNewId(){
        Iterable<Message> messages=getAll();
        long i=0;
        for(Message m:messages){
            if(m.getId()>i){
                i=m.getId();
            }
        }
        return i+1;
    }

    /**
     * add a new chat message /modify an existing chat message with a reply
     * @param id_from_s the id of the sender
     * @param id_to_s the id of the User or Group who will get the message
     * @param message the content of the message
     * @param type the type of message(group or user)
     * @return The New added message
     * @throws ValidationException
     *            if the params are invalid
     */
    public Message addMessage(String id_from_s, String id_to_s, String message, String type) {
        MessageValidator.idValidate(id_from_s);
        MessageValidator.idValidate(id_to_s);
        Message mess=new Message(Long.parseLong(id_from_s),Long.parseLong(id_to_s),message,type,LocalDateTime.now());
        mess.setId(getNewId());
        Message rez=repo.save(mess);
        notifyObservers(new MessageGChangeEvent(ChangeEvent.ADD,rez));
        return rez;
    }
    public Message addMessage(String id_from_s, String id_to_s, String message, String type, String reply) {
        MessageValidator.idValidate(id_from_s);
        MessageValidator.idValidate(id_to_s);
        Message mess=new Message(Long.parseLong(id_from_s),Long.parseLong(id_to_s),message,type,LocalDateTime.now(),reply);
        mess.setId(getNewId());
        Message rez=repo.save(mess);
        notifyObservers(new MessageGChangeEvent(ChangeEvent.ADD,rez));
        return rez;
    }

    /**
     * get the conversation beetween two users(by their ip)
     * @param id1 the id of the 1st user
     * @param id2 the id of the second user
     * @param type the type of the chat
     * @return String(the conversation beetween the two given users)
     * @throws ValidationException
     *            if the ids are invalid
     */
    public List<String> Conv(String id1, String id2,String type){
        int index = 0;
        MessageValidator.idValidate(id1);
        String aux;
        List<String> Conversation=new ArrayList<>();
        if(type.equals("groupmessage")){
            for(Message msg : getAll()){
                if(msg!=null){
                    if(String.valueOf(msg.getTo_id()).equals(id2) && msg.getType()==Messagetype.groupmessage){
                        String id1m=String.valueOf(msg.getFrom_id());
                        if(msg.getReply()!=null){
                            Conversation.add("Reply to: " + msg.getReply());
                            Conversation.add("(" + String.valueOf(msg.getDate().getHour())+":"+String.valueOf(msg.getDate().getMinute())+ ")" +user_crt.getUser(id1m).getFirstName() + " " +user_crt.getUser(id1m).getLastName() + ": " + msg.getMessage());
                        }else{
                            Conversation.add("(" + String.valueOf(msg.getDate().getHour())+":"+String.valueOf(msg.getDate().getMinute())+ ")" +user_crt.getUser(id1m).getFirstName() + " " +user_crt.getUser(id1m).getLastName() + ": " + msg.getMessage());
                        }
                    }
                }
            }
        }else if(type.equals("privatemessage")){
            for(Message msg : getAll()){
                if(msg!=null){
                    if(String.valueOf(msg.getTo_id()).equals(id2) && String.valueOf(msg.getFrom_id()).equals(id1) && msg.getType()==Messagetype.privatemessage){
                        if(msg.getReply()!=null){
                            Conversation.add("Reply to: " + msg.getReply());
                            Conversation.add("(" + String.valueOf(msg.getDate().getHour())+":"+String.valueOf(msg.getDate().getMinute())+ ")" + user_crt.getUser(id1).getFirstName() + " " +user_crt.getUser(id1).getLastName() + ": " + msg.getMessage());
                        }else
                            Conversation.add("(" + String.valueOf(msg.getDate().getHour())+":"+String.valueOf(msg.getDate().getMinute())+ ")" + user_crt.getUser(id1).getFirstName() + " " +user_crt.getUser(id1).getLastName() + ": " + msg.getMessage());
                    }
                    if(String.valueOf(msg.getTo_id()).equals(id1) && String.valueOf(msg.getFrom_id()).equals(id2)&& msg.getType()==Messagetype.privatemessage){
                        if(msg.getReply()!=null){
                            Conversation.add("Reply to: " + msg.getReply());
                            Conversation.add("(" + String.valueOf(msg.getDate().getHour())+":"+String.valueOf(msg.getDate().getMinute())+ ")" + user_crt.getUser(id2).getFirstName() + " " +user_crt.getUser(id2).getLastName() + ": " + msg.getMessage());
                        }else
                            Conversation.add("(" + String.valueOf(msg.getDate().getHour())+":"+String.valueOf(msg.getDate().getMinute())+ ")" + user_crt.getUser(id2).getFirstName() + " " +user_crt.getUser(id2).getLastName() + ": " + msg.getMessage());
                    }
                }
            }
        }
        return Conversation;
    }
    /**
     * removes a message
     * @param id_de_sters
     *          must not be null
     * @return Message
     *          if the Message was removed succesfully
     * @throws ValidationException
     *            if the id_to_remove is invalid
     */
    public Message RemoveMessage(Long id_de_sters){
        Message rez=repo.delete(id_de_sters);
        notifyObservers(new MessageGChangeEvent(ChangeEvent.DELETE,rez));
        return rez;
    }
    /**
     * removes all messages from a specific group
     * @param group_id
     *          must not be null
     * @throws ValidationException
     *            if the group_id is invalid
     */
    public void deleteAllGroupMessage(Long group_id){
        Iterable<Message> messages=getAll();
        Long[] de_sters=new Long[1000];
        int index=0;
        for(Message m:messages){
            if(m.getType() == Messagetype.groupmessage){
                if(m.getTo_id().equals(group_id)){
                    de_sters[index++]=m.getId();
                }
            }
        }
        for(Long id_de_sters:de_sters){
            if(id_de_sters!=null){
                RemoveMessage(id_de_sters);
            }
        }
    }
    /**
     * @return all entities
     */
    public Iterable<Message> getAll(){
        return repo.findAll();
    }

    public List<Message> getAllMessages() {
        return StreamSupport.stream(repo.findAll().spliterator(),false).collect(Collectors.toList());
    }

    @Override
    public void addObserver(Observer<MessageGChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessageGChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageGChangeEvent t) {
        observers.stream()
                .forEach(x->x.update(t));
    }
}
