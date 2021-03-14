package socialnetwork.repository.database;

import socialnetwork.domain.Friendship;
import socialnetwork.domain.Message;
import socialnetwork.domain.Tuple;
import socialnetwork.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;

public class FriendshipBD extends AbstractBDRepository<Tuple<Long,Long>, Friendship> {
    public FriendshipBD(String url, String username, String password, Validator<Friendship> validator) {
        super(url, username, password, validator);
    }

    @Override
    protected String findOneQuery(Tuple<Long, Long> id) {
        return "SELECT * from friendships WHERE id1 = " + id.getLeft() + " AND id2 = " + id.getRight();
    }



    @Override
    protected Friendship createEntity(ResultSet resultSet) throws SQLException {
        String id1 = resultSet.getString("id1");
        String id2 = resultSet.getString("id2");
        String date = resultSet.getString("date");
        String status = resultSet.getString("status");
        Friendship utilizator = new Friendship(Long.parseLong(id1),Long.parseLong(id2),LocalDateTime.parse(date),status);//,reply);
        return utilizator;
    }

    @Override
    public String getOrder(){
        return "friendships.id1, friendships.id2";
    }

    @Override
    protected String getTableName() {
        return "friendships";
    }




    @Override
    protected PreparedStatement addQuery(Friendship entity, Connection connection) throws SQLException {
        return connection.prepareStatement("INSERT INTO friendships(id1,id2,date,status) VALUES ("
                + entity.getId1() +","+entity.getId2()+",'"
                +entity.getDate() +"','"+entity.getStatus()+ "')");
    }

    @Override
    protected PreparedStatement delQuery(Tuple<Long, Long> id, Connection connection) throws SQLException {
        return connection.prepareStatement("DELETE FROM friendships WHERE id1 = "+id.getLeft().toString() + " AND id2 = " + id.getRight().toString());
    }

    @Override
    protected PreparedStatement updateQuery(Friendship entity, Connection connection) throws SQLException {
        return connection.prepareStatement("UPDATE  friendships SET status = "+entity.getStatus() +" WHERE id1 = "+entity.getId1() + " AND id2 = " + entity.getId2());
    }
}
