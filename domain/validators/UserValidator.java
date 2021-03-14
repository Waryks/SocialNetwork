package socialnetwork.domain.validators;

import socialnetwork.domain.User;

public class UserValidator implements Validator<User> {
    /**
     * @param entity
     *            entity must be not null
     * @throws ValidationException
     *            if the entity is not valid
     */
    @Override
    public void validate(User entity) throws ValidationException {
        if(!entity.getFirstName().matches("[A-Z]+[a-z]*")){
            throw new ValidationException("The first name is invalid!(correct form: one letter from A to Z than any character from a to z)");
        }
        if(!entity.getLastName().matches("[A-Z]+[a-z]*")){
            throw new ValidationException("the last name is invalid!(correct form: one letter from A to Z than any character from a to z)");
        }

        if(!entity.getEmail().matches(".*@.*\\..*"))
            throw new ValidationException("Incorrect email!");

    }
    public static void emailValidator(String email) throws ValidationException{
        if(!email.matches(".*@.*\\..*"))
            throw new ValidationException("Incorrect email!");
    }

    public static void passwordValidator(String pass, String cpass) throws ValidationException{
        if(!pass.equals(cpass))
            throw new ValidationException("Passwords don't match!");
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
     * @param name -the first name of the entity to be validated
     * @throws ValidationException
     *            if the id is not valid
     */
    public static void firstNameValidate(String name) throws ValidationException{
        if(!name.matches("[A-Z]+[a-z]*")){
            throw new ValidationException("The first name is invalid!(correct form: one letter from A to Z than any character from a to z)");
        }
    }
    /**
     * @param name -the last name of the entity to be validated
     * @throws ValidationException
     *            if the id is not valid
     */
    public static void lastNameValidate(String name) throws ValidationException{
        if(!name.matches("[A-Z]+[a-z]*")){
            throw new ValidationException("the last name is invalid!(correct form: one letter from A to Z than any character from a to z)");
        }
    }
    /**
     * @param age_s -the age of the entity to be validated
     * @throws ValidationException
     *            if the id is not valid
     */
    public static void ageValidate(String age_s) throws ValidationException{
        Long age;
        try{
            age=Long.parseLong(age_s);
        }catch(NumberFormatException exp){
            throw new ValidationException("The age is invalid!(the age needs to be a number greater than 0)");
        }
        if(age<=0){
            throw new ValidationException("The age needs to be greater than 0!");
        }
    }
    /**
     * @param favFood -the favourite food of the entity to be validated
     * @throws ValidationException
     *            if the id is not valid
     */
    public static void favFoodValidate(String favFood) throws ValidationException{
        if(!favFood.matches("[a-z]+")){
            throw new ValidationException("the favourite food is invalid!(correct form: one or more letters from a to z)");
        }
    }


    /**
     * @param id_s the id we need to search in the list
     * @param users the list in which we verifi the existence of a user with the id given
     * @throws ValidationException
     *            if the id is not valid
     */
    public static void idExistenceValidate(String id_s,Iterable<User> users)throws ValidationException{
        idValidate(id_s);
        Long id=Long.parseLong(id_s);
        for(User u:users){
            if(u.getId().equals(id)){
                return;
            }
        }
        throw new ValidationException("There is no User with that id!");
    }

    public static void mailExistenceValidate(String email,Iterable<User> users)throws ValidationException{
        for(User u:users){
            if(u.getEmail().equals(email)){
                throw new ValidationException("There is already a account made on that email!");
            }
        }
    }
}
