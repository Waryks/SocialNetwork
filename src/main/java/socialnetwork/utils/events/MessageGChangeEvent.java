package socialnetwork.utils.events;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Group;
import socialnetwork.domain.Message;
import socialnetwork.domain.RealEvent;

public class MessageGChangeEvent implements socialnetwork.utils.events.Event {
    private ChangeEvent type;
    private Message data, oldData;
    private Group gdata, goldData;
    private RealEvent  edata, eoldData;

    public MessageGChangeEvent(ChangeEvent type, Message data) {
        this.type = type;
        this.data = data;
    }
    public MessageGChangeEvent(ChangeEvent type, Message data, Message oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }
    public MessageGChangeEvent(ChangeEvent type, Group data) {
        this.type = type;
        this.gdata = data;
    }
    public MessageGChangeEvent(ChangeEvent type, Group data, Group oldData) {
        this.type = type;
        this.gdata = data;
        this.goldData=oldData;
    }
    public MessageGChangeEvent(ChangeEvent type, RealEvent data) {
        this.type = type;
        this.edata = data;
    }
    public MessageGChangeEvent(ChangeEvent type, RealEvent data, RealEvent oldData) {
        this.type = type;
        this.edata = data;
        this.eoldData=oldData;
    }

    public ChangeEvent getType() {
        return type;
    }

    public Message getData() {
        return data;
    }

    public Message getOldData() {
        return oldData;
    }

    public Group getGdata() {
        return gdata;
    }

    public Group getGoldData() {
        return goldData;
    }

    @Override
    public String getEventType() {
        return "MessageorGroup";
    }
}
