package socialnetwork.service;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ObservableBooleanValue;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.Repository;
import socialnetwork.utils.events.MessageEvent;
import socialnetwork.utils.events.UserEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;


import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserService implements Observable<UserEvent> {
    private Repository<Long, User> repo;

    public UserService(Repository<Long, User> repo) {
        this.repo = repo;
    }

    public User addUser(String firstName, String lastName) {
        long id = 0;
        long max = 0;
        for (User user : getAllUsers()) {
            id = user.getId();
            if(max<id){
                max = id;
            }
        }
        max++;
        User user = new User(firstName,lastName);
        user.setId(max);
        User task = repo.save(user);
        return task;
    }

    public void removeUser(String id) {
        long idRight;
        idRight = UserValidator.is_long(id);
        List<User> util;
        util = getAllUsers();
        repo.delete(idRight,util);
    }
    public Boolean checkUser(String username){
        long id = UserValidator.is_long(username);
        boolean ok = false;
        for(User user:getAllUsers()){
            if(user.getId() == id)
                ok = true;
        }
        return ok;
    }

    public User getUser(String username){
        long id = UserValidator.is_long(username);
        for(User user:getAllUsers()){
            if(user.getId() == id)
                return user;
        }
        return null;
    }
    public User getUser(Long id){
        for(User user:getAllUsers()){
            if(user.getId().equals(id))
                return user;
        }
        return null;
    }

    public List<User> getAllUsers() {
        Iterable<User> users = repo.findAll();
        return StreamSupport.stream(users.spliterator(),false).collect(Collectors.toList());
    }
    public Iterable<User> getAll(){
        return repo.findAll();
    }

    public List<User> filterUsersName(String s) {
        return null;
    }
    private List<Observer<UserEvent>> observers=new ArrayList<>();
    @Override
    public void addObserver(Observer<UserEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<UserEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(UserEvent t) {
        observers.stream().forEach(x->x.update(t));
    }
}
