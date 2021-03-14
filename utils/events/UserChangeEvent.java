package socialnetwork.utils.events;

public class UserChangeEvent implements socialnetwork.utils.events.Event {
    public UserChangeEvent() {
    }

    @Override
    public String getEventType() {
        return "User";
    }
}
