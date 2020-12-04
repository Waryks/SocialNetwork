package socialnetwork.domain.validators;

import socialnetwork.domain.User;

public class UserValidator implements Validator<User> {
    /**Checks if the entity has invalid names
     * @param entity , object that needs to be checked
     * @throws ValidationException
     */
    @Override
    public void validate(User entity) throws ValidationException {
        String name1;
        name1 = entity.getFirstName();
        for(int index = 0; index < name1.length(); index++){
            if(!Character.isLetter(name1.charAt(index)))
                throw new ValidationException("The file has a invalid name!");
        }
        name1 = entity.getLastName();
        for(int index = 0; index < name1.length(); index++){
            if(!Character.isLetter(name1.charAt(index)))
                throw new ValidationException("The file has a invalid name!");
        }
    }

    /**Checks if a string can be called a name
     * @param name1 ,string that needs to be checked
     * @throws ValidationException if its not a name
     */
    public static void isName(String name1){
        for(int index = 0; index < name1.length(); index++){
            if(!Character.isLetter(name1.charAt(index)))
                throw new ValidationException("Invalid name!");
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
}
