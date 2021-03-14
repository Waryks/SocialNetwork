package socialnetwork.utils.events;

import socialnetwork.domain.Friendship;

public class FriendshipChangeEvent implements socialnetwork.utils.events.Event {
    private ChangeEvent type;
    private Friendship data, oldData;

    public FriendshipChangeEvent(ChangeEvent type, Friendship data) {
        this.type = type;
        this.data = data;
    }
    public FriendshipChangeEvent(ChangeEvent type, Friendship data, Friendship oldData) {
        this.type = type;
        this.data = data;
        this.oldData=oldData;
    }

    public ChangeEvent getType() {
        return type;
    }

    public Friendship getData() {
        return data;
    }

    public Friendship getOldData() {
        return oldData;
    }

    @Override
    public String getEventType() {
        return "Friendship";
    }
}
