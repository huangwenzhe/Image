package dao;

import Exception.JavaImageServerException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ImageDao {
    public void inset(Image image){
        Connection connection = DBUtil.getConnection();
        String sql = "insert into image_table values(null,?,?,?,?,?,?)";
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1,image.getImageName());
            statement.setInt(2,image.getSize());
            statement.setInt(2, image.getSize());
            statement.setString(3, image.getUploadTime());
            statement.setString(4, image.getContentType());
            statement.setString(5, image.getPath());
            statement.setString(6, image.getMd5());

            int ret = statement.executeUpdate();
            if(ret!=1){
                throw new JavaImageServerException("插入数据出错");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JavaImageServerException e) {
            e.printStackTrace();
        } finally {
            DBUtil.Close(connection,statement,null);
        }
    }
    public List<Image > selectAll (){
        List<Image> list = new ArrayList<>();

        String sql = "select * from image_table";
        Connection connection = DBUtil.getConnection();
        ResultSet resultSet = null;
        PreparedStatement statement = null;
        try {statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            while(resultSet.next()){
                Image image = new Image();
                image.setImageId(resultSet.getInt("ImageId"));
                image.setImageName(resultSet.getString("ImageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("md5"));
                list.add(image);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.Close(connection,statement,resultSet);
        }
        return null;
    }
    public Image selectOne(int imageId){
            String sql = "select * from image_table where ImageId = ?";
            Connection connection =DBUtil.getConnection();
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1,imageId);
             resultSet = statement.executeQuery();
            if(resultSet != null){
                Image image= new Image();
                image.setImageId(resultSet.getInt("ImageId"));
                image.setImageName(resultSet.getString("imageName"));
                image.setSize(resultSet.getInt("size"));
                image.setUploadTime(resultSet.getString("uploadTime"));
                image.setContentType(resultSet.getString("contentType"));
                image.setPath(resultSet.getString("path"));
                image.setMd5(resultSet.getString("md5"));
                return image;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DBUtil.Close(connection,statement,resultSet);
        }
        return null;
    }
    public void deleteOne(int imageId){
        Connection connection = DBUtil.getConnection();
        // 2. 拼装 SQL 语句
        String sql = "delete from image_table where imageId = ?";
        PreparedStatement statement = null;
        // 3. 执行 SQL 语句
        try {
            statement = connection.prepareStatement(sql);
            statement.setInt(1, imageId);
            int ret = statement.executeUpdate();
            if (ret != 1) {
                throw new JavaImageServerException("删除数据库操作失败");
            }
        } catch (SQLException | JavaImageServerException e) {
            e.printStackTrace();
        } finally {
            // 4. 关闭连接
            DBUtil.Close(connection, statement, null);
        }
    }
    public static void main(String[] args) {

    }
}
