package socialnetwork.domain;

import java.time.LocalDateTime;


public class Friendship extends Entity<Tuple<Long,Long>> {

    LocalDateTime date;
    private long id1;
    private long id2;
    private String status;

    public Friendship(long id1,long id2,String status) {
        this.id1=id1;
        this.id2=id2;
        this.setId(new Tuple<Long,Long>(id1,id2));
        this.status=status;
    }
    public Friendship(long id1,long id2,LocalDateTime date,String status) {
        this.id1=id1;
        this.id2=id2;
        this.setId(new Tuple<Long,Long>(id1,id2));
        this.date=date;
        this.status=status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    //test
    public long getId1() {
        return id1;
    }
    public void setId1(long id1) {
        this.id1 = id1;
    }
    public long getId2() {
        return id2;
    }
    public void setId2(long id2) {
        this.id2 = id2;
    }

    @Override
    public String toString() {
        return "Friendship{" +
                "id1=" + id1 +
                ", id2=" + id2 +
                "}(Date: "+getDate()+" ;Status: " + getStatus() + ")";
    }

    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }
}
