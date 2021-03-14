package socialnetwork.service;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.UserValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;

import java.util.Observable;

public class UserService {
    private Repository<Long, User> repo;

    public UserService(Repository<Long, User> repo) {
        this.repo = repo;
    }

    /**
     * create an id which isn't used at the moment by noone
     * @return a new id
     */
    public long getNewId(){
        Iterable<User> users=getAll();
        long i=0;
        for(User u:users){
            if(u.getId()>i){
                i=u.getId();
            }
        }
        return i+1;
    }

    /**
     * adds a new user
     * @param firstname
     *          must be a string with A-Z and a-z
     * @param lastname
     *          must be a string with A-Z and a-z
     * @param age
     *          must be a number greater than 0
     * @param fav_food
     *          must be a string with a-z
     * @return User
     *          if the User was added succesfully
     * @throws ValidationException
     *            if the params are invalid
     */
    public User addUser(String firstname,String lastname,String email,String password, String age,String fav_food) {
        User new_user=new User(firstname,lastname,email,password,age,fav_food);
        new_user.setId(getNewId());
        return repo.save(new_user);
    }

    /**
     * removes a User
     * @param id_to_remove
     *          must not be null
     * @return User
     *          if the User was removed succesfully
     * @throws ValidationException
     *            if the id_to_remove is invalid
     */
    public User removeUser(String id_to_remove) {
        UserValidator.idValidate(id_to_remove);
        return repo.delete(Long.parseLong(id_to_remove));
    }

    /**
     * Verifies if there is a user with the given id
     * @param id
     *          must not be null
     * @return boolean
     *          if there is a user with the given id
     * @throws ValidationException
     *            if the id is not valid
     */
    public boolean existUser(String id){
        UserValidator.idValidate(id);
        User u=repo.findOne(Long.parseLong(id));
        return u != null;
    }

    /**
     * get the User from the list of Users by their ip
     * @param id the id we want to find in the list of Users
     * @return the User we were searching for
     *
     * @throws ValidationException
     *            if the id is not valid
     */
    public User getUser(String id){
        UserValidator.idValidate(id);
        User u=repo.findOne(Long.parseLong(id));
        return u;
    }
    public User getUserEmail(String email){
        for(User u:getAll()){
            if(u.getEmail().equals(email))
                return u;
        }
        return null;
    }
    /**
     * @return all entities
     */
    public Iterable<User> getAll(){
        return repo.findAll();
    }
}
