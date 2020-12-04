package socialnetwork.domain.validators;

import socialnetwork.domain.Friends;

public class FriendValidator implements Validator<Friends> {
    /**Validates the friend
     * @param entity a object that's not known
     * @throws ValidationException
     */
    @Override
    public void validate(Friends entity) throws ValidationException {
        if (entity.getIdRight() == entity.getIdLeft()) {
            throw new ValidationException("The id's need to be different!");
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
     * @param number string, the checked string
     * @return the string in a int value
     * @throws ValidationException otherwise
     */
    public static Integer is_int(String number){
        try{
            return Integer.parseInt(number);
        }
        catch(NumberFormatException exception){
            throw new ValidationException("Is not a valid number!");
        }
    }

    /**Verifies if two ids exist
     * @param okRight true, if the right param exists
     * @param okLeft true, if the right param exists
     * @throws ValidationException if it doesn't exist
     */
    public static void exists(boolean okRight, boolean okLeft){
        if(okLeft == false || okRight == false){
            throw new ValidationException("The id's need to exist!");
        }

    }
}
