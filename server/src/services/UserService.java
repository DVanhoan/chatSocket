package services;
import database.MySQLConnection;
import models.User;
import utils.RSAHash;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
public class UserService {
    Connection connection = MySQLConnection.getConnection();

    public boolean login(String username, String password) throws SQLException {
        try{
            String sql = "select * from users where username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.execute();
            if (preparedStatement.getResultSet().next()) {
                if(RSAHash.decrypt(preparedStatement.getResultSet().getString("password")).equals(password)) {
                    return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public boolean register (String username, String password) throws SQLException {
        int check = 0;
        try{
            String sql = "insert into users (username, password) values (?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.execute();
            check = preparedStatement.getUpdateCount();
            if (check > 0) {
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<User> findAll() {
        try{
            String sql = "select * from users";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.execute();

            ArrayList<User> users = new ArrayList<>();
            while (preparedStatement.getResultSet().next()) {
                User user = new User();
                user.setUsername(preparedStatement.getResultSet().getString("username"));
                user.setPassword(preparedStatement.getResultSet().getString("password"));
                users.add(user);
            }
            return users;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}