package socialnetwork.domain;

import java.time.LocalDateTime;

public class UserDTO extends User{
    String status;
    LocalDateTime date;

    public UserDTO(String firstName, String lastName, String age, String favouriteFood, LocalDateTime date) {
        super(firstName, lastName, age, favouriteFood);
        this.date = date;
    }

    public UserDTO(String firstName, String lastName, String age, String favouriteFood, String status, LocalDateTime date) {
        super(firstName, lastName, age, favouriteFood);
        this.status = status;
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
