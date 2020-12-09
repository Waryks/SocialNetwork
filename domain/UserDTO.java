package socialnetwork.domain;

import java.time.LocalDateTime;

public class UserDTO extends User{
    private LocalDateTime date;
    private String status;
    public UserDTO(String firstName, String lastName) {
        super(firstName, lastName);
    }
    public UserDTO(String firstName, String lastName, LocalDateTime d) {
        super(firstName, lastName);
        date = d;
    }

    public UserDTO(String firstName, String lastName, String s) {
        super(firstName, lastName);
        status = s;
    }
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
