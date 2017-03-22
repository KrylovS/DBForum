package DBForum.mappers;

import DBForum.models.PostModel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by sergey on 15.03.17.
 */
@Repository
public class PostMapper implements RowMapper<PostModel> {
    @Override
    public PostModel mapRow(ResultSet resultSet, int i) throws SQLException {
        PostModel postModel = new PostModel();
        postModel.setAuthor(resultSet.getString("author"));
        postModel.setCreated(resultSet.getTimestamp("created"));
        postModel.setForum(resultSet.getString("forum"));
        postModel.setId(resultSet.getInt("id"));
        postModel.setIsEdited(resultSet.getBoolean("isEdited"));
        postModel.setMessage(resultSet.getString("message"));
        postModel.setParent(resultSet.getInt("parent"));
        postModel.setThread(resultSet.getInt("thread"));
        return postModel;
    }
}
