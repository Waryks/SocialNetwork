package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Message extends Entity<Long>{
    private Long from_id;
    private Long to_id;
    private String message;
    private String reply=null;
    private LocalDateTime date;
    private Messagetype type;

    public Message(Long from_id, Long to_id, String message,String type, LocalDateTime date) {
        this.from_id = from_id;
        this.to_id = to_id;
        this.message = message;
        this.date = date;
        if(type.equals("privatemessage")){
            this.type=Messagetype.privatemessage;
        }
        if(type.equals("groupmessage")){
            this.type=Messagetype.groupmessage;
        }
    }
    public Message(Long from_id, Long to_id, String message,String type, LocalDateTime date,String reply) {
        this.from_id = from_id;
        this.to_id = to_id;
        this.message = message;
        this.date = date;
        this.reply=reply;
        if(type.equals("privatemessage")){
            this.type=Messagetype.privatemessage;
        }
        if(type.equals("groupmessage")){
            this.type=Messagetype.groupmessage;
        }
    }

    public Message() {

    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public Long getFrom_id() {
        return from_id;
    }

    public void setFrom_id(Long from_id) {
        this.from_id = from_id;
    }

    public Long getTo_id() {
        return to_id;
    }

    public void setTo_id(Long to_id) {
        this.to_id = to_id;
    }


    public Messagetype getType() {
        return type;
    }

    public void setType(Messagetype type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Message{" +
                "from_id=" + from_id +
                ", to_id=" + to_id +
                ", message='" + message + '\'' +
                ", date=" + date +
                ", type=" + type +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message1 = (Message) o;
        return Objects.equals(getFrom_id(), message1.getFrom_id()) &&
                Objects.equals(getTo_id(), message1.getTo_id()) &&
                Objects.equals(getMessage(), message1.getMessage()) &&
                Objects.equals(getDate(), message1.getDate()) &&
                getType() == message1.getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrom_id(), getTo_id(), getMessage(), getDate(),  getType());
    }

}
