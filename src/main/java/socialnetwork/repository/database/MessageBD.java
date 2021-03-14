package socialnetwork.repository.database;

import socialnetwork.domain.Message;
import socialnetwork.domain.validators.Validator;

import java.sql.*;
import java.time.LocalDateTime;

public class MessageBD extends AbstractBDRepository<Long, Message> {
    public MessageBD(String url, String username, String password, Validator<Message> validator) {
        super(url, username, password, validator);
    }

    @Override
    protected String findOneQuery(Long id) {
        return "SELECT * from messages WHERE id = " + id.toString();
    }



    @Override
    protected Message createEntity(ResultSet resultSet) throws SQLException {
        String from_id = resultSet.getString("fromid");
        String to_id = resultSet.getString("toid");
        String message = resultSet.getString("msg");
        String type = resultSet.getString("type");
        String date = resultSet.getString("date");
        String reply = resultSet.getString("reply");
        String id = resultSet.getString("id");
        Message utilizator;
        if(reply.equals("NULL")){
            utilizator = new Message(Long.parseLong(from_id),Long.parseLong(to_id),message,type, LocalDateTime.parse(date));
            utilizator.setId(Long.parseLong(id));
        }
        else{
            utilizator = new Message(Long.parseLong(from_id),Long.parseLong(to_id),message,type, LocalDateTime.parse(date),reply);
            utilizator.setId(Long.parseLong(id));
        }
        return utilizator;
    }

    @Override
    public String getOrder(){
        return "messages.date";
    }

    @Override
    protected String getTableName() {
        return "messages";
    }




    @Override
    protected PreparedStatement addQuery(Message entity, Connection connection) throws SQLException {
        //System.out.println(entity);
        String reply;
        if(entity.getReply()==null)
            reply = "NULL";
        else
            reply = entity.getReply();
        return connection.prepareStatement("INSERT INTO messages(id,fromid,toid,msg,type,date,reply) VALUES ("
                + entity.getId() +","+entity.getFrom_id()+","+entity.getTo_id()+",'"
                +entity.getMessage() +"','"+entity.getType()+"','" +entity.getDate() + "','" + reply + "')");
    }

    @Override
    protected PreparedStatement delQuery(Long id, Connection connection) throws SQLException {
        return connection.prepareStatement("DELETE FROM messages WHERE id = "+id.toString());
    }

    @Override
    protected PreparedStatement updateQuery(Message entity, Connection connection) throws SQLException {
        return connection.prepareStatement("UPDATE  messages SET msg = "+entity.getMessage()+",reply = " + entity.getReply() + ",date = " + LocalDateTime.now() +" WHERE id = "+entity.getId());
    }
}
;