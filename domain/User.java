package socialnetwork.domain;

import java.util.List;
import java.util.Objects;

public class User extends Entity<Long>{
    private String firstName;
    private String lastName;
    private String age;
    private String favouriteFood;
    private String email;
    private String password;
    private List<User> friends;

    public User(String firstName, String lastName, String age, String favouriteFood) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.favouriteFood = favouriteFood;
    }

    public User(String firstName, String lastName,String email,String password, String age, String favouriteFood) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email=email;
        this.password=password;
        this.age = age;
        this.favouriteFood = favouriteFood;
    }
    public User() {

    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getFavouriteFood() {
        return favouriteFood;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String age) {
        this.email = age;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String age) {
        this.password = age;
    }

    public void setFavouriteFood(String favouriteFood) {
        this.favouriteFood = favouriteFood;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<User> getFriends() {
        return friends;
    }

    @Override
    public String toString() {
        return "Name=" + firstName + ' ' + lastName + ';' + "(ID:" + getId() + " Email:" + getEmail() + ", Age: " + age + " ,Favourite Food: "+ favouriteFood +")";
    }

    public void addFriend(User u){
        friends.add(u);
    }
    public void removeFriend(User u){
        friends.remove(u);
    }
    public List<User> showFriends(){
        return friends;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User that = (User) o;
        return getFirstName().equals(that.getFirstName()) &&
                getLastName().equals(that.getLastName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFirstName(), getLastName(), getFriends());
    }
}