package socialnetwork.repository.file;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Group;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupFile extends AbstractFileRepository<Long ,Group> {
    public GroupFile(String fileName, Validator validator) {
        super(fileName, validator);
    }

    /**
     *  extract entity  - template method design pattern
     *  creates an entity of type E having a specified list of @code attributes
     * @param attributes the parts of the entity
     * @return an entity of type E
     */
    @Override
    public Group extractEntity(List<String> attributes) {


        Long id = Long.parseLong(attributes.get(0));//ID

        String name=attributes.get(1);//NAME

        LocalDateTime time = LocalDateTime.parse(attributes.get(2));//DATE

        Long number_of_people =Long.parseLong(attributes.get(3));//NUMBER

        List<Long> ids = new ArrayList<>();
        for(int index = 0; index < number_of_people; index+=1){
            ids.add(index,Long.parseLong(attributes.get(4+index)));
        }//IDS


        Group group = new Group(ids,number_of_people,name,time);
        group.setId(id);
        return group;
    }

    /**
     * gives the entity in form of a string
     * @param entity
     *         entity must be not null
     * @return the entity in from of a string
     */
    @Override
    protected String createEntityAsString(Group entity) {
        String Stringids="";
        List<Long> ids=entity.getIds();
        Long nr=entity.getNumber_of_people();
        for(int index = 0; index < nr - 1; index+=1) {
            Stringids=Stringids+String.valueOf(ids.get(index))+";";
        }
        Stringids=Stringids+String.valueOf(ids.get((int) (nr-1)));
        return entity.getId()+";"+entity.getName()+";"+entity.getDate()+";"+entity.getNumber_of_people()+";"+Stringids;
    }
}