package socialnetwork.repository.database;

import socialnetwork.domain.Group;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.ValidationException;
import socialnetwork.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GroupBD extends AbstractBDRepository<Long, Group> {
    public GroupBD(String url, String username, String password, Validator<Group> validator) {
        super(url, username, password, validator);
    }

    @Override
    protected String findOneQuery(Long id) {
        return "SELECT * FROM groups G INNER JOIN groupmembers GM ON G.id = GM.idGroup WHERE id = " + id.toString();
    }

    @Override
    public Iterable<Group> findAll() {
        List<Group> allGroups = new ArrayList<>();

        try ( Connection connection = DriverManager.getConnection(url, username, password)) {
            PreparedStatement statement = connection.prepareStatement
                    ("SELECT * FROM groups g INNER JOIN groupmembers gm ON g.id = gm.idGroup ORDER BY g.id, gm.idUser");
            ResultSet resultSet = statement.executeQuery();

            if(resultSet.next()) {
                Group newGroup = createEntity(resultSet);
                Long idUser = resultSet.getLong("idUser");
                List<Long> ids = new ArrayList<>();
                ids.add(idUser);

                while (resultSet.next()) {
                    if (resultSet.getLong("id") != newGroup.getId()) {
                        newGroup.setIds(ids);
                        allGroups.add(newGroup);
                        newGroup = createEntity(resultSet);
                        ids = new ArrayList<>();
                    }
                    idUser = resultSet.getLong("idUser");
                    ids.add(idUser);
                }

                newGroup.setIds(ids);
                allGroups.add(newGroup);
                return allGroups;
            }
            return allGroups;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Group findOne(Long id) {
        if (id == null)
            throw new IllegalArgumentException("id must be not null");


        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement(findOneQuery(id));
             ResultSet resultSet = statement.executeQuery()) {
            if(resultSet.next()) {
                Group group = createEntity(resultSet);
                Long idUser = resultSet.getLong("idUser");
                List<Long> ids = new ArrayList<>();
                ids.add(idUser);

                while (resultSet.next() && resultSet.getLong("id") == group.getId()) {
                    idUser = resultSet.getLong("idUser");
                    ids.add(idUser);
                }

                group.setIds(ids);
                return group;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected Group createEntity(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");
        List<Long> ids = new ArrayList<>();
        String numberOfPeople = resultSet.getString("numberofpeople");
        String name = resultSet.getString("name");
        String date = resultSet.getString("date");
        Group utilizator = new Group(ids,Long.parseLong(numberOfPeople),name,LocalDateTime.parse(date));
        utilizator.setId(Long.parseLong(id));
        return utilizator;
    }

    @Override
    public String getOrder(){
        return "groups.id";
    }

    @Override
    protected String getTableName() {
        return "groups";
    }




    @Override
    protected PreparedStatement addQuery(Group entity, Connection connection) throws SQLException {
        String members = new String();
        for(Long id : entity.getIds()){
            members+= id.toString()+",";
        }
        return connection.prepareStatement("INSERT INTO groups(id,numberofpeople,name,date) VALUES ("
                + entity.getId() +","+entity.getNumber_of_people()+",'"
                +entity.getName() +"','" +entity.getDate() + "')");
    }

    @Override
    protected PreparedStatement delQuery(Long id, Connection connection) throws SQLException {
        return connection.prepareStatement("DELETE FROM groups WHERE id = "+id.toString());
    }

    @Override
    protected PreparedStatement updateQuery(Group entity, Connection connection) throws SQLException {
        return connection.prepareStatement("UPDATE  groups SET name = "+entity.getName()+" WHERE id = "+entity.getId());
    }

    @Override
    public Group save(Group entity) {
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
                    Connection connection = DriverManager.getConnection(url, username, password);
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO groupmembers(idGroup,idUser) VALUES ("
                            + entity.getId() +","+ id.toString() + ")");
                    statement.execute();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public Group delete(Long id) {
        if (id == null)
            throw new ValidationException("Repository exception: id must be not null!\n");

        Group entity=new Group();
        try (Connection connection = DriverManager.getConnection(url, username, password);
             PreparedStatement statement = connection.prepareStatement("DELETE FROM groupmembers WHERE idGroup = "+id.toString())) {
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
