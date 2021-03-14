package socialnetwork.domain.validators;

import socialnetwork.domain.Message;
import socialnetwork.domain.User;

import java.util.List;

public class MessageValidator  implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        //TODO
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

    public static void NumberValidate(String nr) throws ValidationException{
        Long Nr;
        try{
            Nr=Long.parseLong(nr);
        }catch(NumberFormatException exp){
            throw new ValidationException("You need to enter a number!");
        }
        if(Nr<=0){
            throw new ValidationException("The number needs to be greater than 0!");
        }
    }

}
