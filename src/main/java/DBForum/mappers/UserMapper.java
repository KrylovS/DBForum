package DBForum.mappers;

import DBForum.models.UserModel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by sergey on 14.03.17.
 */
@Repository
public class UserMapper implements RowMapper<UserModel> {
    @Override
    public UserModel mapRow(ResultSet resultSet, int i) throws SQLException {
        UserModel userModel = new UserModel();
        userModel.setAbout(resultSet.getString("about"));
        userModel.setEmail(resultSet.getString("email"));
        userModel.setFullname(resultSet.getString("fullname"));
        userModel.setNickname(resultSet.getString("nickname"));
        userModel.setId(resultSet.getInt("id"));

        return userModel;
    }
}
