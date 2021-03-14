package socialnetwork.repository.file;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;

import java.util.List;

public class UserFile extends AbstractFileRepository<Long, User>{

    public UserFile(String fileName, Validator<User> validator) {
        super(fileName, validator);
    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes  the parts of the entity
     * @return an entity of type E
     */
    @Override
    public User extractEntity(List<String> attributes) {
        User user = new User(attributes.get(1),attributes.get(2),attributes.get(4),attributes.get(3),attributes.get(5),attributes.get(6));
        user.setId(Long.parseLong(attributes.get(0)));
        return user;
    }

    /**
     * gives the entity in form of a string
     * @param entity
     *         entity must be not null
     * @return the entity in from of a string
     */
    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId()+";"+entity.getFirstName()+";"+entity.getLastName()+";"+entity.getPassword()+";" + entity.getEmail() + ";" +entity.getAge()+";"+entity.getFavouriteFood();
    }
}
