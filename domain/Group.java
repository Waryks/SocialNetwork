package socialnetwork.domain;

import jdk.vm.ci.meta.Local;

import java.time.LocalDateTime;
import java.util.List;

public class Group extends Entity<Long>{
    Long number_of_people;
    List<Long> ids;
    LocalDateTime date;
    String name;

    public Group(List<Long> ids,Long number_of_people,String name, LocalDateTime date) {
        this.ids = ids;
        this.date = date;
        this.number_of_people=number_of_people;
        this.name = name;
    }

    public Group() {}

    public Long getNumber_of_people() {
        return number_of_people;
    }

    public void setNumber_of_people(Long number_of_people) {
        this.number_of_people = number_of_people;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean findid(Long id){
        for(Long index:getIds()){
            if(index.equals(id)){
                return true;
            }
        }
        return false;
    }
}
