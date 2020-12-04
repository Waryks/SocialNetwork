package socialnetwork.domain;

import java.io.Serializable;

/**Template for entity
 * @param <ID> the type of entity
 */
public class Entity<ID> implements Serializable {

    protected static final long serialVersionUID = 7331115341259248461L;
    protected ID id;
    public ID getId() {
        return id;
    }
    public void setId(ID id) {
        this.id = id;
    }
}