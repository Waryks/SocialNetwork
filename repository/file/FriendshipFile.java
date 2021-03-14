package socialnetwork.repository.file;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.util.List;

public class FriendshipFile extends AbstractFileRepository<Tuple<Long,Long>, Friendship>{

    public FriendshipFile(String fileName, Validator<Friendship> validator) {
        super(fileName, validator);
    }
    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes the parts of the entity
     * @return an entity of type E
     */
    @Override
    public Friendship extractEntity(List<String> attributes) {
        return new Friendship(Long.parseLong(attributes.get(0)),Long.parseLong(attributes.get(1)), LocalDateTime.parse(attributes.get(2)),attributes.get(3));
    }

    /**
     * gives the entity in form of a string
     * @param entity
     *         entity must be not null
     * @return the entity in from of a string
     */
    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getId1()+";"+entity.getId2()+";"+entity.getDate()+";"+entity.getStatus();
    }
}