package socialnetwork.repository.database;

import socialnetwork.domain.Group;
import socialnetwork.domain.Message;
import socialnetwork.domain.RealEvent;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RealEventBD extends AbstractBDRepository<Long, RealEvent> {
    public RealEventBD(String url, String username, String password, Validator<RealEvent> validator) {
        super(url, username, password, validator);
    }
    @Override
    protected String findOneQuery(Long id) {
        return "SELECT * from realevent E left join eventsubs es on e.id = es.idEvent WHERE id = " + id.toString();
    }

    @Override
    public Iterable<RealEvent> findAll() {
        List<RealEvent> allevents = new ArrayList<>();

        try ( Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement
                    ("SELECT * from realevent E left join eventsubs es on e.id = es.idEvent ORDER BY e.id, es.idUser");
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                RealEvent newevent = createEntity(resultSet);
                Long idUser = resultSet.getLong("idUser");
                List<Long> ids = new ArrayList<>();
                ids.add(idUser);

                while (resultSet.next()) {
                    if (resultSet.getLong("id") != newevent.getId()) {
                        newevent.setIds(ids);
                        allevents.add(newevent);
                        newevent = createEntity(resultSet);
                        ids = new ArrayList<>();
                    }
                    idUser = resultSet.getLong("idUser");
                    ids.add(idUser);
                }

                newevent.setIds(ids);
                allevents.add(newevent);
                return allevents;
            }
            return allevents;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public RealEvent findOne(Long id) {
        if (id == null)
            throw new IllegalArgumentException("id must be not null");


        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(findOneQuery(id));
             ResultSet resultSet = statement.executeQuery()) {
            if(resultSet.next()) {
                RealEvent event = createEntity(resultSet);
                Long idUser = resultSet.getLong("idUser");
                List<Long> ids = new ArrayList<>();
                ids.add(idUser);

                while (resultSet.next() && resultSet.getLong("id") == event.getId()) {
                    idUser = resultSet.getLong("idUser");
                    ids.add(idUser);
                }

                event.setIds(ids);
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected RealEvent createEntity(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");
        List<Long> ids = new ArrayList<>();
        String numberOfPeople = resultSet.getString("numberofpeople");
        String name = resultSet.getString("name");
        String date = resultSet.getString("date");
        String hour = resultSet.getString("hour");
        String minute = resultSet.getString("minute");
        String announce = resultSet.getString("announce");
        List<Boolean> an = new ArrayList<>();
        for(String announcement: announce.split(",")){
            if(!announcement.equals(""))
                an.add(Boolean.parseBoolean(announcement));
        }
        RealEvent utilizator = new RealEvent(name, LocalDate.parse(date),Long.parseLong(hour),Long.parseLong(minute),Long.parseLong(numberOfPeople),ids,an);
        utilizator.setId(Long.parseLong(id));
        return utilizator;
    }

    @Override
    public String getOrder(){
        return "e.id,es.idUser";
    }

    @Override
    protected String getTableName() {
        return "realevent E inner join eventsubs es on e.id = es.idEvent";
    }

    @Override
    protected PreparedStatement addQuery(RealEvent entity, Connection connection) throws SQLException {
        String members = new String();
        for(Long id : entity.getIds()){
            members+= id.toString()+",";
        }
        String an = new String();
        for(Boolean announcement: entity.getAnnounce()){
            an+= announcement.toString()+",";
        }
        return connection.prepareStatement("INSERT INTO realevent(id,numberofpeople,name,date,announce,hour,minute) VALUES ("
                + entity.getId() +","+entity.getNumber_of_people()+",'"
                +entity.getName() +"','" +entity.getDate() +"','" + an +"','"
                +entity.getHour() +"','" +entity.getMinute() + "')");
    }

    @Override
    protected PreparedStatement delQuery(Long id, Connection connection) throws SQLException {
        return connection.prepareStatement("DELETE FROM realevent WHERE id = "+id.toString());
    }

    @Override
    protected PreparedStatement updateQuery(RealEvent entity, Connection connection) throws SQLException {
        return connection.prepareStatement("UPDATE  realevent SET name = "+entity.getName()+" WHERE id = "+entity.getId());
    }

    @Override
    public RealEvent save(RealEvent entity) {
        if (entity == null)
            throw new ValidationException("Repository exception: id must be not null!\n");
        validator.validate(entity);
        if (findOne(entity.getId()) != null) {
            return entity;
        } else {
            try{
                Connection connection = DriverManager.getConnection(url, username, password);
                PreparedStatement statement = this.addQuery(entity, connection);
                statement.execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            try{
                List<Long> ids=entity.getIds();
                for(Long id:ids){
                    if(id!=0){
                        Connection connection = DriverManager.getConnection(url, username, password);
                        PreparedStatement statement = connection.prepareStatement("INSERT INTO eventsubs(idEvent,idUser) VALUES ("
                                + entity.getId().toString() +","+ id.toString() + ")");
                        statement.execute();
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public RealEvent delete(Long id) {
        if (id == null)
            throw new ValidationException("Repository exception: id must be not null!\n");

        RealEvent entity=new RealEvent();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM eventsubs WHERE idEvent = "+id.toString())) {
            entity = findOne(id);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = delQuery(id, connection)) {
            entity = findOne(id);
            statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return entity;
    }

}
