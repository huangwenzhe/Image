package dao;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBUtil {
    private static final String url = "jdbc:mysql://127.0.0.1:3306/image_server?characterEncoding=utf8&useSSL=false";
    private static final String username = "root";
    private static final String password = "123456789";
    private static volatile DataSource dataSource = null;
    public static DataSource getDataSource(){
        if(dataSource == null){
            synchronized (DBUtil.class){
                if(dataSource==null){
                    dataSource = new MysqlDataSource();
                    MysqlDataSource mysqlDataSource = (MysqlDataSource)dataSource;
                    mysqlDataSource.setURL(url);
                    mysqlDataSource.setUser(username);
                    mysqlDataSource.setPassword(password);
                }
            }
        }
        return dataSource;
    }
    public static Connection getConnection(){
        try {
            return getDataSource().getConnection();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }
    public static void Close(Connection connection, PreparedStatement preparedStatement, ResultSet resultSet){
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
