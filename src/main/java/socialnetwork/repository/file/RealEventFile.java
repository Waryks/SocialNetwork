package socialnetwork.repository.file;

import socialnetwork.domain.Message;
import socialnetwork.domain.RealEvent;
import socialnetwork.domain.validators.Validator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RealEventFile extends AbstractFileRepository<Long , RealEvent>  {
    public RealEventFile(String fileName, Validator<RealEvent> validator) {
        super(fileName, validator);
    }

    @Override
    public RealEvent extractEntity(List<String> attributes) {
        Long id = Long.parseLong(attributes.get(0));//ID

        String name = attributes.get(1);//Name

        LocalDate date = LocalDate.parse(attributes.get(2));//Date

        Long hour = Long.parseLong(attributes.get(3));//Hour

        Long minute = Long.parseLong(attributes.get(4));//Minute

        Long number_of_people =Long.parseLong(attributes.get(5));//NUMBER

        List<Boolean> announce = new ArrayList<>();
        announce.add(true);
        announce.add(true);
        announce.add(true);
        announce.set(0,Boolean.parseBoolean(attributes.get(6))); //Announce per hour
        announce.set(1,Boolean.parseBoolean(attributes.get(7))); //Announce per half
        announce.set(2,Boolean.parseBoolean(attributes.get(8))); //Announce entry

        List<Long> ids = new ArrayList<>();
        for(int index = 0; index < number_of_people; index+=1){
            ids.add(index,Long.parseLong(attributes.get(9+index)));
        }//IDS

        RealEvent event = new RealEvent(name,date,hour,minute,number_of_people,ids,announce);
        event.setId(id);
        return event;
    }

    @Override
    protected String createEntityAsString(RealEvent entity) {
        String Stringids="";
        List<Long> ids=entity.getIds();
        for(Long id:ids)
            Stringids = Stringids + id.toString() + ";";
        return entity.getId() + ";" + entity.getName() + ";" + entity.getDate() + ";" + entity.getHour() + ";" + entity.getMinute() + ";" + entity.getNumber_of_people() + ";" + entity.getAnnounce().get(0) + ";" + entity.getAnnounce().get(1) + ";" + entity.getAnnounce().get(2) + ";" + Stringids;
    }
}
