package DBForum.mappers;

import DBForum.models.ThreadModel;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by sergey on 15.03.17.
 */
@Repository
public class ThreadMapper implements RowMapper<ThreadModel>{

    @Override
    public ThreadModel mapRow(ResultSet resultSet, int i) throws SQLException {
        ThreadModel threadModel = new ThreadModel();
        threadModel.setAuthor(resultSet.getString("author"));
        threadModel.setCreated(resultSet.getTimestamp("created"));
        threadModel.setForum(resultSet.getString("forum"));
        threadModel.setId(resultSet.getInt("id"));
        threadModel.setMessage(resultSet.getString("message"));
        threadModel.setSlug(resultSet.getString("slug"));
        threadModel.setTitle(resultSet.getString("title"));
        threadModel.setVotes(resultSet.getInt("votes"));
        return threadModel;
    }
}
