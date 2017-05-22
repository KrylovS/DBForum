package DBForum.service;

import DBForum.models.DBServiceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by sergey on 20.03.17.
 */
@Repository
public class DBService {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public DBServiceModel getStatus() {
        final DBServiceModel DBStatus = new DBServiceModel();
        DBStatus.setForum(jdbcTemplate.queryForObject("SELECT count(*) FROM Forum", Integer.class));
        DBStatus.setUser(jdbcTemplate.queryForObject("SELECT count(*) FROM \"User\"", Integer.class));
        DBStatus.setThread(jdbcTemplate.queryForObject("SELECT count(*) FROM Thread", Integer.class));
        DBStatus.setPost(jdbcTemplate.queryForObject("SELECT count(*) FROM Post", Integer.class));
        return DBStatus;
    }

    public void clear() {
        for (String tableName : new String[]{"Forum", "\"User\"", "Thread", "Post", "UserForum"}) {
            jdbcTemplate.update("DELETE FROM " + tableName);
        }
    }

}
