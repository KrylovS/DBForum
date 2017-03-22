package DBForum.mappers;


import DBForum.models.ForumModel;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by sergey on 14.03.17.
 */
@Repository
public class ForumMapper implements org.springframework.jdbc.core.RowMapper<ForumModel> {
    @Override
    public ForumModel mapRow(ResultSet resultSet, int i) throws SQLException {
        ForumModel forumModel = new ForumModel();
        forumModel.setPosts(resultSet.getInt("posts"));
        forumModel.setSlug(resultSet.getString("slug"));
        forumModel.setThreads(resultSet.getInt("threads"));
        forumModel.setTitle(resultSet.getString("title"));
        forumModel.setUser(resultSet.getString("user"));

        return forumModel;
    }
}
