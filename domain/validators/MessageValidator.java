package socialnetwork.domain.validators;

import socialnetwork.domain.Friends;
import socialnetwork.domain.Message;
import socialnetwork.domain.User;

import java.util.List;

public class MessageValidator implements Validator<Message> {
    /**Checks if the entitiy's sender id equals the reciver one
     * @param entity , the entity that needs to be validated
     * @throws ValidationException
     */
    @Override
    public void validate(Message entity) throws ValidationException {
        for(User el:entity.getTo()) {
            if (entity.getFrom().getId() == el.getId()) {
                throw new ValidationException("The sender id and the reciver id need to be different!");
            }
        }
    }

    /**Checks if entitiy's reciver id's are different
     * @param users , list of users that need to be checked
     * @throws ValidationException
     */
    public static void validateList(List<User> users) throws ValidationException{
        for(int index = 0; index < users.size()-1; index++)
            for(int jndex = index + 1; jndex < users.size(); jndex++){
                if(users.get(index).getId().equals(users.get(jndex).getId()))
                    throw new ValidationException("The reciver id's need to be different!");
            }
    }
    /**Verifies if a string is long
     * @param id string, the checked string
     * @return the string in a long value
     * @throws ValidationException
     */
    public static Long is_long(String id){
        try{
            return Long.parseLong(id);
        }
        catch(NumberFormatException exception){
            throw new ValidationException(id + " is not a valid number!");
        }
    }
    /**Verifies if a string is int
     * @param id string, the checked string
     * @return the string in a int value
     * @throws ValidationException otherwise
     */
    public static Integer is_int(String id){
        try{
            return Integer.parseInt(id);
        }
        catch(NumberFormatException exception){
            throw new ValidationException(id + " is not a valid number!");
        }
    }
}
