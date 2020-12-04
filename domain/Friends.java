package socialnetwork.domain;

import java.time.LocalDateTime;


public class Friends extends Entity<Tuple<Long,Long>> {

    LocalDateTime date;
    String status;
    public Friends(Tuple<Long,Long> id_tuple, LocalDateTime date, String status) {
        id = id_tuple;
        this.date = date;
        this.status = status;
    }
    public Friends(Tuple<Long,Long> id_tuple, LocalDateTime date) {
        id = id_tuple;
        this.date = date;
        status = "Pending";
    }
    public Friends(Tuple<Long,Long> id_tuple, String status){
        id = id_tuple;
        date = LocalDateTime.now();
        this.status = status;
    }
    public Friends(Tuple<Long,Long> id_tuple){
        id = id_tuple;
        date = LocalDateTime.now();
        status = "Pending";
    }
    /**
     *
     * @return the date when the friendship was created
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * @return the left id
     */
    public Long getIdLeft(){
        return id.getLeft();
    }

    /**
     * @return the right id
     */
    public Long getIdRight(){
        return id.getRight();
    }

    /**
     * @return status of the friendship
     */
    public String getStatus(){return status;}

    /**
     * @param status sets the status
     */
    public void setStatus(String status){this.status = status;}
    @Override
    public String toString() {
        return getIdLeft()+","+getIdRight()+","+getStatus();
    }
}
