package socialnetwork.repository.file;

import socialnetwork.domain.Friends;

import java.time.LocalDateTime;
import java.util.List;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;

public class FriendsFile extends AbstractFileRepository<Tuple<Long,Long>, Friends> {

    public FriendsFile(String fileName, Validator<Friends> validator) {
        super(fileName, validator);
    }

    /**Extracts a entity from a string
     * @param attributes - string
     * @return a object of type friend
     */
    @Override
    public Friends extractEntity(List<String> attributes) {
        Long idLeft = Long.parseLong(attributes.get(0));
        Long idRight = Long.parseLong(attributes.get(1));
        LocalDateTime date = LocalDateTime.parse(attributes.get(2));
        String status = attributes.get(3);
        Friends friend = new Friends(new Tuple<>(idLeft,idRight), date, status);
        return friend;
    }

    /**Creates entity as a string
     * @param entity - object of type friends
     * @return string
     */
    @Override
    protected String createEntityAsString(Friends entity) {
        return entity.getIdLeft()+";"+entity.getIdRight()+";"+entity.getDate()+";"+entity.getStatus();
    }
}
