package socialnetwork.repository.database;

import socialnetwork.domain.User;
import socialnetwork.domain.validators.Validator;
import socialnetwork.utils.Encryption;

import java.sql.*;

public class UserBD extends AbstractBDRepository<Long, User> {
    public UserBD(String url, String username, String password, Validator<User> validator) {
        super(url, username, password, validator);
    }

    @Override
    protected String findOneQuery(Long id) {
        return "SELECT * from users WHERE id = " + id.toString();
    }

    @Override
    public String getOrder(){
        return "users.id";
    }

    @Override
    protected User createEntity(ResultSet resultSet) throws SQLException {
        String id = resultSet.getString("id");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");
        String age = resultSet.getString("age");
        String fave = resultSet.getString("fav_food");
        User utilizator = new User(firstName, lastName,email, Encryption.decrypt(3,password),age,fave);
        utilizator.setId(Long.parseLong(id));
        return utilizator;
    }

    @Override
    protected String getTableName() {
        return "users";
    }




    @Override
    protected PreparedStatement addQuery(User entity, Connection connection) throws SQLException {
        //System.out.println(entity);
        return connection.prepareStatement("INSERT INTO users(id,first_name,last_name,email,password,fav_food,age) VALUES ("+ entity.getId() +",'"+entity.getFirstName()+"','"+entity.getLastName()+"','"+entity.getEmail() +"','"+ Encryption.encrypt(3,entity.getPassword())+"','" +entity.getFavouriteFood() + "','" + entity.getAge() + "')");
    }

    @Override
    protected PreparedStatement delQuery(Long id, Connection connection) throws SQLException {
        return connection.prepareStatement("DELETE FROM users WHERE id = "+id.toString());
    }

    @Override
    protected PreparedStatement updateQuery(User entity, Connection connection) throws SQLException {
        return connection.prepareStatement("UPDATE  users SET firstname = "+entity.getFirstName()+",lastname = " + entity.getLastName()+" WHERE id = "+entity.getId());
    }



}
