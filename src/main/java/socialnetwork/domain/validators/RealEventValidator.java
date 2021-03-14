package socialnetwork.domain.validators;

import socialnetwork.domain.Group;
import socialnetwork.domain.RealEvent;
import socialnetwork.domain.User;

public class RealEventValidator implements Validator<RealEvent> {
    /**
     * @param entity
     *            entity must be not null
     * @throws ValidationException
     *            if the entity is not valid
     */
    @Override
    public void validate(RealEvent entity) throws ValidationException {
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

    public static void idExistenceValidate(String id_s,Iterable<RealEvent> events)throws ValidationException{
        idValidate(id_s);
        Long id=Long.parseLong(id_s);
        for(RealEvent e:events){
            if(e.getId().equals(id)){
                return;
            }
        }
        throw new ValidationException("There is no Event with that id!");
    }

    public static void HourValidate(String hour_s) throws ValidationException{
        Long hour;
        try{
            hour=Long.parseLong(hour_s);
        }catch(NumberFormatException exp){
            throw new ValidationException("The hour is invalid!(the hour needs to be a number beetween 0 and 23)");
        }
        if(hour<0 || hour>23){
            throw new ValidationException("the hour needs to be beetween 0 and 23!");
        }
    }

    public static void MinuteValidate(String minute_s) throws ValidationException{
        Long minute;
        try{
            minute=Long.parseLong(minute_s);
        }catch(NumberFormatException exp){
            throw new ValidationException("The minute is invalid!(the hour needs to be a number beetween 0 and 59)");
        }
        if(minute<0 || minute>59){
            throw new ValidationException("the minute needs to be beetween 0 and 59!");
        }
    }

}
