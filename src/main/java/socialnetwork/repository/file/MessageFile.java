package socialnetwork.repository.file;

import socialnetwork.domain.*;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MessageFile  extends AbstractFileRepository<Long, Message>{
    public MessageFile(String fileName, Validator<Message> validator) {
        super(fileName, validator);
    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes the parts of the entity
     * @return an entity of type E
     */
    @Override
    public Message extractEntity(List<String> attributes) {
        Long id = Long.parseLong(attributes.get(0));//ID

        Long from_id = Long.parseLong(attributes.get(1));//FROM ID

        Long to_id = Long.parseLong(attributes.get(2));//TO ID

        String message = attributes.get(3);//MESSAGE

        String type=attributes.get(4);//TYPE

        LocalDateTime time = LocalDateTime.parse(attributes.get(5));//DATE
        try{
            String reply=attributes.get(6);//REPLY
            Message mesg = new Message(from_id,to_id,message,type,time,reply);
            mesg.setId(id);
            return mesg;
        }catch(Exception e){

            Message mesg = new Message(from_id,to_id,message,type,time);
            mesg.setId(id);
            return mesg;
        }
    }

    /**
     * gives the entity in form of a string
     * @param entity
     *         entity must be not null
     * @return the entity in from of a string
     */
    @Override
    protected String createEntityAsString(Message entity) {
        if(entity.getReply()==null) {
            if (entity.getType() == Messagetype.groupmessage)
                return String.valueOf(entity.getId()) + ";" + entity.getFrom_id() + ';' + entity.getTo_id() + ';' + entity.getMessage() + ';' + "groupmessage" + ';' + entity.getDate();
            if (entity.getType() == Messagetype.privatemessage)
                return String.valueOf(entity.getId()) + ";" + entity.getFrom_id() + ';' + entity.getTo_id() + ';' + entity.getMessage() + ';' + "privatemessage" + ';' + entity.getDate();
            return "";
        }else{
            if (entity.getType() == Messagetype.groupmessage)
                return String.valueOf(entity.getId()) + ";" + entity.getFrom_id() + ';' + entity.getTo_id() + ';' + entity.getMessage() + ';' + "groupmessage" + ';' + entity.getDate() + ';' + entity.getReply();
            if (entity.getType() == Messagetype.privatemessage)
                return String.valueOf(entity.getId()) + ";" + entity.getFrom_id() + ';' + entity.getTo_id() + ';' + entity.getMessage() + ';' + "privatemessage" + ';' + entity.getDate() + ';' + entity.getReply();
            return "";
        }
    }
}
