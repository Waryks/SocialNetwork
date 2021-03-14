package socialnetwork.service;

import socialnetwork.domain.Group;
import socialnetwork.domain.validators.GroupValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.utils.events.ChangeEvent;
import socialnetwork.utils.events.MessageGChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GroupService implements Observable<MessageGChangeEvent> {
    private Repository<Long, Group> repo;
    private List<Observer<MessageGChangeEvent>> observers=new ArrayList<>();

    public GroupService(Repository<Long, Group> repo) {
        this.repo = repo;
    }

    /**
     * create an id which isn't used at the moment by noone
     * @return a new id
     */
    public long getNewId(){
        Iterable<Group> groups=getAll();
        long i=0;
        for(Group g:groups){
            if(g.getId()>i){
                i=g.getId();
            }
        }
        return i+1;
    }

    /**
     * adds a new group
     * @param name
     *          must be a string not null
     * @param ids
     *          must have at least 2 ids
     * @param nr
     *          must be a number greater than 0
     * @return Group
     *          if the Group was added succesfully
     * @throws ValidationException
     *            if the params are invalid
     */
    public Group addGroup(String name, List<Long> ids,Long nr) {
        Group new_group=new Group(ids,nr,name,LocalDateTime.now());
        new_group.setId(getNewId());
        Group rez=repo.save(new_group);
        notifyObservers(new MessageGChangeEvent(ChangeEvent.ADD,rez));
        return rez;
    }

    /**
     * leave a group as a user
     * @param id_group
     *          must not be null
     * @param id_user
     *          must not be null
     * @return Group
     *          if the Group was left/removed succesfully
     * @throws ValidationException
     *            if the params are invalid
     */
    public Group leaveGroup(String id_group,String id_user){
        Group group_to_update=new Group();
        for(Group g:getAll()){
            if(g.getId()==Long.parseLong(id_group)){
                group_to_update=g;
                for(Long id_index:g.getIds()){
                    if(id_index==Long.parseLong(id_user)){
                        g.getIds().remove(id_index);
                        g.setNumber_of_people(g.getNumber_of_people()-1);
                        group_to_update=removeGroup(id_group);
                        if(!g.getIds().isEmpty()){
                            group_to_update=repo.save(g);
                        }
                        notifyObservers(new MessageGChangeEvent(ChangeEvent.UPDATE,group_to_update));
                        return group_to_update;
                    }
                }
            }
        }
        return null;

    }
    /**
     * removes a Group
     * @param id_to_remove
     *          must not be null
     * @return Group
     *          if the group was removed succesfully
     * @throws ValidationException
     *            if the id_to_remove is invalid
     */
    public Group removeGroup(String id_to_remove) {
        GroupValidator.idValidate(id_to_remove);
        Group rez=repo.delete(Long.parseLong(id_to_remove));
        notifyObservers(new MessageGChangeEvent(ChangeEvent.DELETE,rez));
        return rez;
    }

    /**
     * get the Group from the list of Groups by their ip
     * @param id the id we want to find in the list of Users
     * @return the Group we were searching for
     *
     * @throws ValidationException
     *            if the id is not valid
     */
    public Group getGroup(String id){
        GroupValidator.idValidate(id);
        Group g=repo.findOne(Long.parseLong(id));
        return g;
    }

    /**
     * @return all entities
     */
    public Iterable<Group> getAll(){
        return repo.findAll();
    }

    public List<Group> getAllGroupsofUser(Long id){
        return StreamSupport.stream(getAll().spliterator(),false)
                .filter(c -> c.getIds().contains(id))
                .collect(Collectors.toList());
    }

    @Override
    public void addObserver(Observer<MessageGChangeEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<MessageGChangeEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(MessageGChangeEvent t) {
        observers.stream()
                .forEach(x->x.update(t));
    }
}
