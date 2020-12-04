package socialnetwork.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Message extends Entity<Long>{
    private User from;
    private List<User> to;
    private String messageText;
    private String reply;
    private LocalDateTime timestamp;
    public Message(){}

    /**Constructor
     * @param id_message long
     * @param from User object
     * @param to List of users
     * @param message String
     * @param reply String
     */
    public Message(Long id_message, User from, List<User> to, String message, String reply){
        this.from = from;
        this.to = to;
        this.messageText = message;
        this.reply = reply;
        timestamp = LocalDateTime.now();
        id = id_message;
    }

    /**Constructor
     * @param id_message long
     * @param from User object
     * @param to List of users
     * @param message String
     * @param reply String
     * @param timestamp LocalDateTime
     */
    public Message(Long id_message, User from, List<User> to, String message, String reply, LocalDateTime timestamp){
        this.from = from;
        this.to = to;
        this.messageText = message;
        this.reply = reply;
        this.timestamp = timestamp;
        id = id_message;
    }

    /**
     * @return from user
     */
    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }

    /**
     * @return to user
     */
    public List<User> getTo() {
        return to;
    }

    public void setTo(List<User> to) {
        this.to = to;
    }

    /**
     * @return message
     */
    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    /**
     * @return reply
     */
    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    /**
     * @return timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return getId()+","+from+","+to+","+messageText+","+reply;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message that = (Message) o;
        boolean ok1, ok2 = true;
        ok1 = getFrom().equals(that.getFrom());
        for(User to: getTo()){
            if(getTo().equals(to)==false)
                ok2 = false;
        }
        return ok1 == true && ok2 == true;
    }

}
