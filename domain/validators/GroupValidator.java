package socialnetwork.domain.validators;

import socialnetwork.domain.Group;
import socialnetwork.domain.User;

public class GroupValidator implements Validator<Group> {
    /**
     * @param entity
     *            entity must be not null
     * @throws ValidationException
     *            if the entity is not valid
     */
    @Override
    public void validate(Group entity) throws ValidationException {
        /*if(!entity.getFirstName().matches("[A-Z]+[a-z]*")){
            throw new ValidationException("The first name is invalid!(correct form: one letter from A to Z than any character from a to z)");
        }
        if(!entity.getLastName().matches("[A-Z]+[a-z]*")){
            throw new ValidationException("the last name is invalid!(correct form: one letter from A to Z than any character from a to z)");
        }
        Long age;
        try{
            age=Long.parseLong(entity.getAge());
        }catch(NumberFormatException exp){
            throw new ValidationException("The age is invalid!(the age needs to be a number greater than 0)");
        }
        if(age<=0){
            throw new ValidationException("The age needs to be greater than 0!");
        }
        if(!entity.getFavouriteFood().matches("[a-z]+")){
            throw new ValidationException("the favourite food is invalid!(correct form: one or more letters from a to z)");
        }*/
    }
    /**
     * @param id_s -the id of the entity to be validated
     *                 id must not be null
     * @throws ValidationException
     *            if the id is not valid
     */
    public static void idValidate(String id_s) throws ValidationException{
        Long id;
        try{
            id=Long.parseLong(id_s);
        }catch(NumberFormatException exp){
            throw new ValidationException("The id is invalid!(the id needs to be a number greater than 0)");
        }
        if(id<=0){
            throw new ValidationException("The id needs to be greater than 0!");
        }
    }

    /**
     * @param id_s the id we need to search in the list
     * @param groups the list in which we verifi the existence of a user with the id given
     * @throws ValidationException
     *            if the id is not valid
     */
    public static void idExistenceValidate(String id_s,Iterable<Group> groups)throws ValidationException{
        idValidate(id_s);
        Long id=Long.parseLong(id_s);
        for(Group g:groups){
            if(g.getId().equals(id)){
                return;
            }
        }
        throw new ValidationException("There is no Group with that id!");
    }
}
