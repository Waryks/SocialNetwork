package socialnetwork.service;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.repository.Repository;



import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UserService {
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
}
