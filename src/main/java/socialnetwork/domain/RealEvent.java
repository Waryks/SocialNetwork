package socialnetwork.domain;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RealEvent  extends Entity<Long>{
    Long number_of_people;
    List<Long> ids;
    String name;
    LocalDate date;
    Long hour;
    Long minute;
    List<Boolean> announce = new ArrayList<>();

    public RealEvent() {
    }

    public RealEvent(String name, LocalDate date, Long hour, Long minute, Long number_of_people, List<Long> ids) {
        this.name = name;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.number_of_people=number_of_people;
        this.ids = ids;
    }

    public RealEvent(String name, LocalDate date, Long hour, Long minute, Long number_of_people, List<Long> ids, List<Boolean> announce) {
        this.name = name;
        this.date = date;
        this.hour = hour;
        this.minute = minute;
        this.number_of_people=number_of_people;
        this.ids = ids;
        this.announce = announce;
    }

    public List<Boolean> getAnnounce(){return announce;}

    public void setAnnounce(List<Boolean> announce){this.announce = announce;}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Long getHour() {
        return hour;
    }

    public void setHour(Long hour) {
        this.hour = hour;
    }

    public Long getMinute() {
        return minute;
    }

    public void setMinute(Long minute) {
        this.minute = minute;
    }

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
}
