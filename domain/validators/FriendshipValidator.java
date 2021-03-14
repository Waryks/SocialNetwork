package socialnetwork.domain.validators;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.User;

import java.time.Month;

public class FriendshipValidator implements Validator<Friendship> {
    /**
     * @param entity
     *            entity must be not null
     * @throws ValidationException
     *            if the entity is not valid
     */
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity.getId1()<0){
            throw new ValidationException("The first id is invalid!( the id is greater than 0)");
        }
        if(entity.getId2()<0){
            throw new ValidationException("The second id is invalid!( the id is greater than 0)");
        }
        if(entity.getId1()==entity.getId2()){
            throw new ValidationException("Both id-s are identical!");
        }
        if(!entity.getStatus().equals("pending") && !entity.getStatus().equals("rejected") && !entity.getStatus().equals("approved")){
            throw new ValidationException("Invalid Status!(it needs to be pending/rejected/approved!");
        }
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
     * @param response -the response
     *                  must be yes or no
     * @throws ValidationException
     *                  if response is invalid
     */
    public static void responseValidate(String response) throws ValidationException{
        if(!response.equals("no") && !response.equals("yes")){
            throw new ValidationException("The Response needs to be yes or no!");
        }
    }
    /**
     * @param month -the response
     *                  must be yes or no
     * @throws ValidationException
     *                  if response is invalid
     */
    public static void monthValidate(String month) throws ValidationException{
        String m=month.toUpperCase();
        try{
            Month.valueOf(m);
        }catch(IllegalArgumentException exp){
            throw new ValidationException("Invalid Month!(ex. November)");
        }
    }
    /**
     * @param id_s1 the id1 we need to search in the list
     * @param id_s2 the id2 we need to search in the list
     * @param friends the list in which we verify the existence of a user with the given id
     * @throws ValidationException
     *            if the id1 and id2 are not valid
     */
    public static void idsExistenceValidate(String id_s1,String id_s2,Iterable<Friendship> friends)throws ValidationException{
        idValidate(id_s1);
        idValidate(id_s2);
        Long id1=Long.parseLong(id_s1);
        Long id2=Long.parseLong(id_s2);
        if(id1.equals(id2)){
            throw new ValidationException("Both id-s are identical!");
        }
        for(Friendship f:friends){
            if(f.getId1()==id1 && f.getId2()==id2 || f.getId1()==id2 && f.getId2()==id1){
                return;
            }
        }
        throw new ValidationException("There is no Friendship/Friend Request/Rejected Friendship with those ids");
    }


    /**
     * @param id_s1 the id1 we need to search in the list
     * @param id_s2 the id2 we need to search in the list
     * @param friends the list in which we verify the existence of a user with the given id
     * @throws ValidationException
     *            if the id1 and id2 are not valid
     */
    public static void idsNonExistenceValidate(String id_s1,String id_s2,Iterable<Friendship> friends)throws ValidationException{
        idValidate(id_s1);
        idValidate(id_s2);
        Long id1=Long.parseLong(id_s1);
        Long id2=Long.parseLong(id_s2);
        if(id1.equals(id2)){
            throw new ValidationException("Both id-s are identical!");
        }
        for(Friendship f:friends){
            if(f.getId1()==id1 && f.getId2()==id2 || f.getId1()==id2 && f.getId2()==id1){
                if(f.getStatus().equals("pending")){
                    throw new ValidationException("There already is a friend request from the user with id:" + f.getId1() + " to the user with id:" + f.getId2() + "!");
                }
                if(f.getStatus().equals("rejected")){
                    throw new ValidationException("This Friendship was rejected by the user with id:" + f.getId2());
                }
                if(f.getStatus().equals("approved")){
                    throw new ValidationException("There already is a friendship with those ids!");
                }
            }
        }
    }

    /**
     * @param id_sender the id of who send the friend request
     * @param id_receiver the id of who received the friend request
     * @param friends the list in which we verify the existence of a user with the given id
     * @throws ValidationException
     *            if the id1 and id2 are not valid
     */
    public static void FriendRequestExistence(String id_sender,String id_receiver,Iterable<Friendship> friends)throws ValidationException{
        idValidate(id_sender);
        idValidate(id_receiver);
        Long id1=Long.parseLong(id_sender);
        Long id2=Long.parseLong(id_receiver);
        if(id_sender.equals(id_receiver)){
            throw new ValidationException("Both id-s are identical!");
        }
        for(Friendship f:friends){
            if(f.getId1()==id1 && f.getId2()==id2){
                if(f.getStatus().equals("pending")){
                    return;
                }
                if(f.getStatus().equals("rejected")){
                    throw new ValidationException("This Friendship was already rejected by " + f.getId2());
                }
                if(f.getStatus().equals("approved")){
                    throw new ValidationException("This Friendship already was approved!");
                }
            }
        }
        throw new ValidationException("There is no Friend Request with those ids");
    }

}
