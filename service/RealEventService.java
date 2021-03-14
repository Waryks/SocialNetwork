package socialnetwork.service;

import socialnetwork.controller.Allert_Controller;
import socialnetwork.domain.Group;
import socialnetwork.domain.RealEvent;
import socialnetwork.domain.validators.GroupValidator;
import socialnetwork.domain.validators.RealEventValidator;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.repository.Repository;
import socialnetwork.repository.database.RealEventBD;
import socialnetwork.repository.file.RealEventFile;
import socialnetwork.repository.paging.Page;
import socialnetwork.repository.paging.Pageable;
import socialnetwork.repository.paging.PageableImplementation;
import socialnetwork.utils.events.ChangeEvent;
import socialnetwork.utils.events.MessageGChangeEvent;
import socialnetwork.utils.observer.Observable;
import socialnetwork.utils.observer.Observer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RealEventService implements Observable<MessageGChangeEvent> {
    private RealEventBD repo;
    private List<Observer<MessageGChangeEvent>> observers=new ArrayList<>();

    public RealEventService(RealEventBD repo) {
        this.repo = repo;
    }

    /**
     * create an id which isn't used at the moment by noone
     * @return a new id
     */
    public long getNewId(){
        Iterable<RealEvent> events=getAll();
        long i=0;
        for(RealEvent e:events){
            if(e.getId()>i){
                i=e.getId();
            }
        }
        return i+1;
    }

    public RealEvent addEvent(String name, LocalDate date, Long hour, Long minute, Long numberofPeople, List<Long> ids, List<Boolean> announce) {
        RealEvent new_event=new RealEvent(name,date,hour,minute,numberofPeople,ids, announce);
        new_event.setId(getNewId());
        RealEvent rez=repo.save(new_event);
        notifyObservers(new MessageGChangeEvent(ChangeEvent.ADD,rez));
        return new_event;
    }

    public RealEvent addEventID(String name, LocalDate date, Long hour, Long minute, Long numberofPeople, List<Long> ids, List<Boolean> announce , long ID) {
        RealEvent new_event=new RealEvent(name,date,hour,minute,numberofPeople,ids, announce);
        new_event.setId(ID);
        RealEvent rez=repo.save(new_event);
        notifyObservers(new MessageGChangeEvent(ChangeEvent.ADD,rez));
        return new_event;
    }

    public List<RealEvent> listSubscribed(Long id){
        return StreamSupport.stream(getAll().spliterator(),false)
                .filter(c -> AreyouSubscribed(c.getId().toString(), id.toString()))
                .collect(Collectors.toList());
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
    public RealEvent unsubscribeEvent(String id_group,String id_user){
        RealEvent event_to_update=new RealEvent();
        for(RealEvent e:getAll()){
            if(e.getId()==Long.parseLong(id_group)){
                event_to_update=e;
                for(Long id_index:e.getIds()){
                    if(id_index==Long.parseLong(id_user)){
                        e.getIds().remove(id_index);
                        e.setNumber_of_people(e.getNumber_of_people()-1);
                        event_to_update=removeEvent(id_group);
                        event_to_update=repo.save(e);
                        notifyObservers(new MessageGChangeEvent(ChangeEvent.UPDATE,event_to_update));
                        return event_to_update;
                    }
                }
            }
        }
        return null;
    }

    public RealEvent subscribeEvent(String id_group,String id_user){
        RealEvent event_to_update=new RealEvent();
        for(RealEvent e:getAll()){
            if(e.getId()==Long.parseLong(id_group)){
                event_to_update=e;
                e.getIds().add(Long.parseLong(id_user));
                e.setNumber_of_people(e.getNumber_of_people()+1);
                event_to_update=removeEvent(id_group);
                event_to_update=repo.save(e);
                notifyObservers(new MessageGChangeEvent(ChangeEvent.UPDATE,event_to_update));
                return event_to_update;
            }
        }
        return null;
    }

    public boolean AreyouSubscribed(String id_group,String id_user){
        for(RealEvent e:getAll()){
            if(e.getId().equals(Long.parseLong(id_group))){
                for(Long id_index:e.getIds()){
                    if(id_index.equals(Long.parseLong(id_user))){
                        return true;
                    }
                }
            }
        }
        return false;
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
    public RealEvent removeEvent(String id_to_remove) {
        RealEventValidator.idValidate(id_to_remove);
        RealEvent rez=repo.delete(Long.parseLong(id_to_remove));
        notifyObservers(new MessageGChangeEvent(ChangeEvent.DELETE,rez));
        return rez;
    }

    public boolean refresh() {
        boolean ok;
        List<String> ids_to_remove=new ArrayList<>();
        for(RealEvent e:getAll()){
            ok=true;
            if(LocalDate.now().isAfter(e.getDate())){
                ok=false;
            }
            if(LocalDate.now().equals(e.getDate())){
                if(LocalDateTime.now().getHour() > e.getHour()){
                    ok=false;
                }
                else if(LocalDateTime.now().getHour() == e.getHour()){
                    if(LocalDateTime.now().getMinute() >= e.getMinute()){
                        ok=false;
                    }
                }
            }
            if(!ok){
                ids_to_remove.add(String.valueOf(e.getId()));
            }
        }
        ok=false;
        for(String s_id:ids_to_remove){
            removeEvent(String.valueOf(s_id));
            ok=true;
        }
        return ok;
    }
    /**
     * get the Group from the list of Groups by their ip
     * @param id the id we want to find in the list of Users
     * @return the Group we were searching for
     *
     * @throws ValidationException
     *            if the id is not valid
     */
    public RealEvent getEvent(String id){
        GroupValidator.idValidate(id);
        RealEvent g=repo.findOne(Long.parseLong(id));
        return g;
    }

    /**
     * @return all entities
     */
    public Iterable<RealEvent> getAll(){
        return repo.findAll();
    }

    public List<RealEvent> getAllList(){
        return StreamSupport.stream(getAll().spliterator(),false)
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

    private int page = 0;
    private int size = 1;

    private Pageable pageable;

    public void setPageSize(int size) {
        this.size = size;
    }

//    public void setPageable(Pageable pageable) {
//        this.pageable = pageable;
//    }

    public Set<RealEvent> getNextEvents() {
        this.page++;
        return getEventsOnPage(this.page);
    }

    public Set<RealEvent> getEventsOnPage(int page) {
        this.page=page;
        Pageable pageable = new PageableImplementation(page, this.size);
        Page<RealEvent> studentPage = repo.findAll(pageable);
        return studentPage.getContent().collect(Collectors.toSet());
    }
}
