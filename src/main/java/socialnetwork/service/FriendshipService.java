package socialnetwork.service;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;
import socialnetwork.domain.UserDTO;
import socialnetwork.domain.validators.FriendshipValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.utils.events.ChangeEvent;
import socialnetwork.utils.events.FriendshipChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;
import socialnetwork.utils.events.Event;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendshipService implements Observable<FriendshipChangeEvent> {
    private Repository<Tuple<Long,Long>, Friendship> repo;
    private List<Observer<FriendshipChangeEvent>> observers=new ArrayList<>();

    public FriendshipService(Repository<Tuple<Long,Long>, Friendship> repo) {
        this.repo = repo;
    }

    /**
     * adds a new friend request
     * @param id1
     *          must not be null
     * @param id2
     *          must not be null
     * @return Friendship
     *           if the Friendship was added succesfully
     *  @throws ValidationException
     *              if the ids are invalid
     */
    public Friendship addFriendship(String id1,String id2) {
        FriendshipValidator.idValidate(id1);
        FriendshipValidator.idValidate(id2);
        Friendship new_f=new Friendship(Long.parseLong(id1),Long.parseLong(id2), LocalDateTime.now(),"pending");
        Friendship rez=repo.save(new_f);
        notifyObservers(new FriendshipChangeEvent(ChangeEvent.ADD,rez));
        return rez;
    }
    /**
     * respond to a new Friend Request and updates the list
     * @param id1
     *          must not be null
     * @param id2
     *          must not be null
     * @param new_status
     *          approved/rejected
     * @return Friendship
     *           if the Friendship was added succesfully
     *  @throws ValidationException
     *              if the ids are invalid
     */
    public Friendship respondFriendRequest(String id1,String id2,String new_status) {
        FriendshipValidator.idValidate(id1);
        FriendshipValidator.idValidate(id2);
        removeFriendship(id1,id2);
        Friendship new_f=new Friendship(Long.parseLong(id1),Long.parseLong(id2), LocalDateTime.now(),new_status);
        Friendship rez=repo.save(new_f);
        notifyObservers(new FriendshipChangeEvent(ChangeEvent.UPDATE,rez));
        return rez;
    }
    /**
     * removes a friendship
     * @param id1
     *          must not be null
     * @param id2
     *          must not be null
     * @return Friendship
     *           if the Friendship was removed succesfully
     * @throws ValidationException
     *              if the ids are invalid
     */
    public Friendship removeFriendship(String id1,String id2) {
        FriendshipValidator.idValidate(id1);
        FriendshipValidator.idValidate(id2);
        Tuple<Long,Long> Fid =new Tuple<Long,Long>(Long.parseLong(id1),Long.parseLong(id2));
        Tuple<Long,Long> Fid2 =new Tuple<Long,Long>(Long.parseLong(id2),Long.parseLong(id1));
        Friendship deleted_f=repo.delete(Fid);
        if(deleted_f==null){
            deleted_f= repo.delete(Fid2);
        }
        notifyObservers(new FriendshipChangeEvent(ChangeEvent.DELETE,deleted_f));
        return deleted_f;
    }

    /**
     * removes a friendship
     * @param id1
     *          must not be null
     * @param id2
     *          must not be null
     */
    public void removeFriendshipNumeric(Long id1,Long id2) {
        Tuple<Long,Long> Fid =new Tuple<Long,Long>(id1,id2);
        Tuple<Long,Long> Fid2 =new Tuple<Long,Long>(id2,id1);
        Friendship rez=repo.delete(Fid);
        rez=repo.delete(Fid2);

        notifyObservers(new FriendshipChangeEvent(ChangeEvent.DELETE,rez));
    }

    /**
     * removes all friendships with the given id
     * @param id_s
     *          must not be null
     * @throws ValidationException
     *            if the id_s is invalid
     */
    public void removeUserFriends(String id_s){
        FriendshipValidator.idValidate(id_s);
        Long id=Long.parseLong(id_s);
        Iterable<Friendship> friends=getAll();
        Long[] de_sters=new Long[1000];
        int index=0;
        for(Friendship f:friends){
            if(f.getId1()==id){
                de_sters[index++]=f.getId2();
            }else{
                if(f.getId2()==id){
                    de_sters[index++]=f.getId1();
                }
            }
        }
        for(Long id2:de_sters){
            if (id2 != null) {
                removeFriendshipNumeric(id,id2);
            }
        }
    }

    /**
     * verifies if there is a friendship with the given ids
     * @param id1
     *          must not be null
     * @param id2
     *          must not be null
     *  @return boolean
     *              if there is a friendship with the given ids
     *  @throws ValidationException
     *              if the ids are invalid
     */
    public boolean existFriendship(String id1,String id2){
        FriendshipValidator.idValidate(id1);
        FriendshipValidator.idValidate(id2);
        Tuple<Long,Long> Fid =new Tuple<Long,Long>(Long.parseLong(id1),Long.parseLong(id2));
        Tuple<Long,Long> Fid2 =new Tuple<Long,Long>(Long.parseLong(id2),Long.parseLong(id1));
        Friendship f=repo.findOne(Fid);
        if(f==null){
            Friendship f2=repo.findOne(Fid2);
            return f2 != null;
        }
        return true;
    }

    /**
     * get the list of friends of the given User
     * @param id_s the id of the User
     * @param users the Service of the existing Users
     * @return the List of Friends of that exact User
     * @throws ValidationException
     *            if the ids are invalid
     */
    public List<String> getAllFriendsUser(String id_s,UserService users){
        FriendshipValidator.idValidate(id_s);
        Long id=Long.parseLong(id_s);

        Iterable<Friendship> friends=getAll();
        return StreamSupport.stream(friends.spliterator(),false)
                .filter(c -> c.getId1() == id || c.getId2() == id)
                .filter(c -> c.getStatus().equals("approved"))
                .map(c -> {
                    if(c.getId1()==id)
                        return users.getUser(String.valueOf(c.getId2())).getFirstName()+';'+users.getUser(String.valueOf(c.getId2())).getLastName()+';'+c.getDate();
                    else
                        return users.getUser(String.valueOf(c.getId1())).getFirstName()+';'+users.getUser(String.valueOf(c.getId1())).getLastName()+';'+c.getDate();
                })
                .collect(Collectors.toList());
    }

    /**
     * get the list of friends of the given User made in a specific month
     * @param id_s the id of the User
     * @param month the month in which was made the friends
     * @param users the Service of the existing Users
     * @return the List of Friends of that exact User
     * @throws ValidationException
     *            if the ids are invalid
     */
    public List<String> getAllFriendsUserMonth(String id_s,String month,UserService users){
        FriendshipValidator.idValidate(id_s);
        Long id=Long.parseLong(id_s);

        Iterable<Friendship> friends=getAll();//getting all the friendships
        return StreamSupport.stream(friends.spliterator(),false)
                .filter(c -> c.getId1() == id || c.getId2() == id)
                .filter(c -> c.getStatus().equals("approved"))
                .filter(c -> c.getDate().getMonth().toString().equals(month.toUpperCase()))
                .map(c -> {
                    if(c.getId1()==id)
                        return users.getUser(String.valueOf(c.getId2())).getFirstName()+';'+users.getUser(String.valueOf(c.getId2())).getLastName()+';'+c.getDate();
                    else
                        return users.getUser(String.valueOf(c.getId1())).getFirstName()+';'+users.getUser(String.valueOf(c.getId1())).getLastName()+';'+c.getDate();
                })
                .collect(Collectors.toList());
    }
    /**
     * @return all entities
     */
    public Iterable<Friendship> getAll(){
        return repo.findAll();
    }

    public List<Friendship> getAllFriends() {
        return StreamSupport.stream(repo.findAll().spliterator(),false).collect(Collectors.toList());
    }

    public List<UserDTO> getFriendsListUser(Long id,UserService user_crt) {
        return StreamSupport.stream(getAll().spliterator(),false)
                .filter(c -> c.getId1() == id || c.getId2() == id)
                .filter(c -> c.getStatus().equals("approved"))
                .map(c-> {
                    User u;
                    if(c.getId1() == id){
                        u=user_crt.getUser(String.valueOf(c.getId2()));
                    }else{
                        u=user_crt.getUser(String.valueOf(c.getId1()));
                    }
                    UserDTO udto =new UserDTO(u.getFirstName(),u.getLastName(),u.getAge(),u.getFavouriteFood(),c.getDate());
                    udto.setId(u.getId());
                    return udto;
                })
                .collect(Collectors.toList());
    }

    public List<UserDTO> getRequestListUser(Long id,UserService user_crt) {
        return StreamSupport.stream(getAll().spliterator(),false)
                .filter(c -> c.getId2() == id)
                .filter(c -> c.getStatus().equals("pending"))
                .map(c-> {
                    User u =user_crt.getUser(String.valueOf(c.getId1()));
                    UserDTO udto =new UserDTO(u.getFirstName(),u.getLastName(),u.getAge(),u.getFavouriteFood(),c.getDate());
                    udto.setId(u.getId());
                    udto.setStatus(c.getStatus());
                    return udto;
                })
                .collect(Collectors.toList());
    }

    public List<UserDTO> getSentRequestListUser(Long id,UserService user_crt) {
        return StreamSupport.stream(getAll().spliterator(),false)
                .filter(c -> c.getId1() == id)
                .filter(c -> c.getStatus().equals("pending"))
                .map(c-> {
                    User u =user_crt.getUser(String.valueOf(c.getId2()));
                    UserDTO udto =new UserDTO(u.getFirstName(),u.getLastName(),u.getAge(),u.getFavouriteFood(),c.getDate());
                    udto.setId(u.getId());
                    udto.setStatus(c.getStatus());
                    return udto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public void addObserver(Observer<FriendshipChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendshipChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendshipChangeEvent t) {
        observers.stream().forEach(x->x.update(t));
    }
}
