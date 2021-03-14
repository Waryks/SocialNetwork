package socialnetwork.domain.validators;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.User;

public class EmptyValidator implements Validator<Tuple<Long,Long>>{
    @Override
    public void validate(Tuple<Long,Long> entity) throws ValidationException {
        //-
    }
}
