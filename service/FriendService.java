package socialnetwork.service;

import socialnetwork.domain.Tuple;
import socialnetwork.domain.Friends;
import socialnetwork.domain.User;
import socialnetwork.domain.validators.FriendValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.utils.Graph;
import socialnetwork.utils.events.FriendEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class FriendService implements Observable<FriendEvent> {
    private Repository<Tuple<Long,Long>, Friends> repo;

    public FriendService(Repository<Tuple<Long,Long>, Friends> repo) {
        this.repo = repo;
    }

    public void addFriend(String id1, String id2, UserService userService){
        addFriend(id1,id2,userService,"pending");
    }

    /**Adds a friend
     * @param id1 -string
     * @param id2 -string
     * @param userService -service of user
     * @param response -the user response to the request
     * @return task
     */
    public Friends addFriend(String id1, String id2, UserService userService, String response) {
        long idLeft;
        idLeft = FriendValidator.is_long(id1);
        long idRight;
        idRight = FriendValidator.is_long(id2);
        long aux = 0;
        if(idLeft > idRight){
            aux = idLeft;
            idLeft = idRight;
            idRight = aux;
        }
        boolean ok1 = false, ok2 = false;
        for (User user : userService.getAllUsers()) {
            if(user.getId() == idLeft)
                ok1 = true;
            if(user.getId() == idRight)
                ok2 = true;
        }
        FriendValidator.exists(ok1,ok2);
        Tuple t = new Tuple(idLeft,idRight);
        Friends friend = new Friends(t,response);
        Friends task = repo.save(friend);
        notifyObservers(new FriendEvent());
        return task;
    }

    /**Removes a friend
     * @param id1
     * @param id2
     */
    public void removeFriend(String id1, String id2) {
        long idRight = FriendValidator.is_long(id1);
        long idLeft = FriendValidator.is_long(id2);
        long aux = 0;
        if(idLeft > idRight){
            aux = idLeft;
            idLeft = idRight;
            idRight = aux;
        }
        Friends t = new Friends(new Tuple<>(idLeft,idRight));
        List<Friends> friends;
        friends = getAllFriends();
        notifyObservers(new FriendEvent());
        repo.delete(t.getId(),friends);
    }
    public void respondRequest(String id1, String id2, UserService userService, String response){
        long idLeft;
        idLeft = FriendValidator.is_long(id1);
        long idRight;
        idRight = FriendValidator.is_long(id2);
        boolean ok1 = false, ok2 = false;
        for (User user : userService.getAllUsers()) {
            if(user.getId() == idLeft)
                ok1 = true;
            if(user.getId() == idRight)
                ok2 = true;
        }
        FriendValidator.exists(ok1,ok2);
        removeFriend(id1,id2);
        if(response.equals("accept")) {
            addFriend(id1,id2,userService,response);
        }
        notifyObservers(new FriendEvent());
    }

    public int Comunity(int size){
        int nrNodes = size;
        Graph graph = new Graph(nrNodes);
        getAllFriends().forEach(x->graph.addEdge(Math.toIntExact(x.getIdLeft()),Math.toIntExact(x.getIdRight())));
        return graph.connectedComponents();
    }
    public void ComunityMax(int size){
        int nrNodes = size;
        Graph graph = new Graph(nrNodes);
        getAllFriends().forEach(x->graph.addEdge(Math.toIntExact(x.getIdLeft()),Math.toIntExact(x.getIdRight())));
        graph.connectedComponents_Max();
    }
    public List<String> friendPerID(long ID,List<User> users){
        //DateTime-ul nu este formatat daca trebuie formatat va rog sa imi spuneti si formatez in timpul orei :^)
        Iterable<Friends> friends = repo.findAll();
        List<String> colected = new ArrayList();
        int index = 0, ok = 0;
        List<Friends> friend_collected = StreamSupport.stream(friends.spliterator(),false).filter(c -> c.getIdLeft() == ID || c.getIdRight() == ID).collect(Collectors.toList());
        for(Friends friend:friend_collected){
            LocalDateTime date = friend.getDate();
            long id1 = friend.getIdLeft(), id2 = friend.getIdRight();
            String first = null, last = null;
            for(User user: users){
                if(id1 == user.getId()) {
                    if (id1 != ID) {
                        first = user.getFirstName();
                        last = user.getLastName();
                    }
                }
                if(id2 == user.getId()) {
                    if (id2 != ID) {
                        first = user.getFirstName();
                        last = user.getLastName();
                    }
                }
            }
            DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME; // se poate formata dar nu am formatat :(
            //DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"); // este formatat :^)
            if(friend.getStatus().equals("accept")) {
                String d = date.format(formatter);
                String fr = first + " " + last + " " + d;
                colected.add(index, fr);
                ok++;
            }
        }
        if(ok == 0){
            throw new ValidationException("Forever alone :(");
        }
        return colected;
    }
    public List<String> friendPerIDYM(long ID,String year, String month, List<User> users){
        //DateTime-ul nu este formatat daca trebuie formatat va rog sa imi spuneti si formatez in timpul orei :^)
        Iterable<Friends> friends = repo.findAll();
        List<String> colected = new ArrayList();
        int index = 0, ok = 0;
        List<Friends> friend_collected = StreamSupport.stream(friends.spliterator(),false).filter(c -> c.getIdLeft() == ID || c.getIdRight() == ID).collect(Collectors.toList());
        for(Friends friend:friend_collected){
            LocalDateTime date = friend.getDate();
            long id1 = friend.getIdLeft(), id2 = friend.getIdRight();
            String first = null, last = null;
            for(User user: users){
                if(id1 == user.getId()) {
                    if (id1 != ID) {
                        first = user.getFirstName();
                        last = user.getLastName();
                    }
                }
                if(id2 == user.getId()) {
                    if (id2 != ID) {
                        first = user.getFirstName();
                        last = user.getLastName();
                    }
                }
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
            String yearD = date.format(formatter);
            formatter = DateTimeFormatter.ofPattern("MM");
            String monthD = date.format(formatter);
            if(yearD.equals(year) && monthD.equals(month) && friend.getStatus().equals("accept")) {
                DateTimeFormatter form = DateTimeFormatter.ISO_DATE_TIME;
                String d = date.format(form);
                String fr = first + " " + last + " " + d;
                colected.add(index, fr);
                ok++;
            }
        }
        if(ok == 0){
            throw new ValidationException("They haven't made any friends at that time");
        }
        return colected;
    }

    public List<Friends> getAllFriends() {
        Iterable<Friends> friends = repo.findAll();
        return StreamSupport.stream(friends.spliterator(),false).collect(Collectors.toList());
    }
    public Iterable<Friends> getAll(){
        return repo.findAll();
    }

    private List<Observer<FriendEvent>> observers=new ArrayList<>();
    @Override
    public void addObserver(Observer<FriendEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<FriendEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(FriendEvent t) {
        observers.stream().forEach(x->x.update(t));
    }
}
