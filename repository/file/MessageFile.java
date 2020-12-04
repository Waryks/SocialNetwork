package socialnetwork.repository.file;

import socialnetwork.domain.Friends;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageFile extends  AbstractFileRepository<Long, Message>{
    public MessageFile(String fileName, Validator<Message> validator) {
        super(fileName, validator);
    }

    /**Extracts a entity from a string
     * @param attributes - string
     * @return a object of type message
     */
    @Override
    public Message extractEntity(List<String> attributes) {
        Long id_message = Long.parseLong(attributes.get(0));
        List<String> attr = Arrays.asList(attributes.get(1).split(","));
        User from = new User(attr.get(1),attr.get(2));
        from.setId(Long.parseLong(attr.get(0)));
        List<User> to = new ArrayList<>();
        //1;1,Aprogramatoarei,Ionut;[2,Apetrei,Ileana];Hello;Hi;2020-11-16T12:45:26.676083700
        List<String> t = Arrays.asList(attributes.get(2).split(","));
        //System.out.println(t);
        for(int index = 0; index < t.size()/3; index++){
            User test = new User(t.get(index*3+1),t.get(index*3+2));
            test.setId(Long.parseLong(t.get(index*3)));
            to.add(index,test);
        }
        String message = attributes.get(3);
        String reply = attributes.get(4);
        LocalDateTime time = LocalDateTime.parse(attributes.get(5));
        Message mesg = new Message(id_message,from,to,message,reply,time);
        return mesg;
    }
    /**Creates entity as a string
     * @param entity - object of type message
     * @return string
     */
    @Override
    protected String createEntityAsString(Message entity) {
        String to = null;
        int index = 0;
        for(User e:entity.getTo()){
            if(index == 0)
                to = e.toString();
            else
                to += "," + e.toString();
            index++;
        }
        return entity.getId()+";"+entity.getFrom()+";"+to+";"+entity.getMessageText()+";"+entity.getReply()+";"+entity.getTimestamp();
    }
}

