package DBForum.service;

import DBForum.mappers.PostMapper;
import DBForum.models.PostModel;
import DBForum.models.ThreadModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;


/**
 * Created by sergey on 15.03.17.
 */
@Repository
public class PostService extends JdbcDaoSupport {
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    public PostService(DataSource dataSource) {
        super();
        setDataSource(dataSource);
    }

    public void create(List<PostModel> posts) {

        try (Connection connection = getJdbcTemplate().getDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO Post (author, created, forum, id, message, parent, thread, tree) VALUES(" +
                    "?, ?, ?, ?, ?, ?, ?, array_append((SELECT tree FROM Post WHERE id = ?), ?))", Statement.NO_GENERATED_KEYS);


            for (PostModel post : posts) {
                final Integer id = getJdbcTemplate().queryForObject("SELECT nextval('post_id_seq')", Integer.class);
                preparedStatement.setString(1, post.getAuthor());
                preparedStatement.setTimestamp(2, post.getCreated());
                preparedStatement.setString(3, post.getForum());
                preparedStatement.setInt(4, id);
                preparedStatement.setString(5, post.getMessage());
                preparedStatement.setInt(6, post.getParent() == null ? 0 : post.getParent());
                preparedStatement.setInt(7, post.getThread());
                preparedStatement.setInt(8, post.getParent() == null ? 0 : post.getParent());
                preparedStatement.setInt(9, id);
                preparedStatement.addBatch();
                post.setId(id);
            }

            preparedStatement.executeBatch();
            preparedStatement.close();

        } catch (SQLException e) {
            throw new DataRetrievalFailureException(null);
        }
    }

    public void updateUserForum(List<PostModel> posts, String forumSlug) {
        HashSet<String> authors = new HashSet<String>();
        for(PostModel post : posts) {
            authors.add(post.getAuthor());
        }

        try (Connection connection = getJdbcTemplate().getDataSource().getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO UserForum (user_nickname, forum) VALUES (?::citext, ?::citext) ON CONFLICT DO NOTHING",
                    Statement.NO_GENERATED_KEYS);
            for(String author : authors) {
                preparedStatement.setString(1, author);
                preparedStatement.setString(2, forumSlug);
                preparedStatement.addBatch();
            }

            preparedStatement.executeBatch();
            preparedStatement.close();

        } catch (SQLException e) {
            throw new DataRetrievalFailureException(null);
        }
    }

    public PostModel getPostById(Integer id) {
        return jdbcTemplate.queryForObject("SELECT * FROM Post WHERE id = ? ORDER BY id",
                new Object[]{id}, new PostMapper());
    }

    public PostModel getPostByIdAndThread(Integer postId, Integer threadId) {
        return jdbcTemplate.queryForObject("SELECT * FROM Post WHERE id = ? AND thread = ? ORDER BY id",
                new Object[]{postId, threadId}, new PostMapper());
    }

    public void update(PostModel postModel, PostModel postUpdate) {
        if (!isEmptyField(postUpdate.getMessage()) && !postModel.getMessage().equals(postUpdate.getMessage())) {
            jdbcTemplate.update("UPDATE Post SET message = ?, isEdited = TRUE WHERE id = ?",
                    postUpdate.getMessage(), postModel.getId());
        }
    }

    public List<PostModel> getFlatSortedPosts(ThreadModel threadModel, Integer limit, Integer marker, Boolean desc) {
        Integer id = threadModel.getId();
        final ArrayList<Object> values = new ArrayList<>();

        if (desc) {
            String query = "SELECT * FROM Post Where thread = ? ORDER BY created DESC, id DESC";
            values.add(id);
            if (limit > 0) {
                query += " LIMIT ?";
                values.add(limit);
            }

            if(marker > 0) {
                query += " OFFSET ?";
                values.add(marker);
            }
            return jdbcTemplate.query(query, values.toArray(), new PostMapper());

        } else {
            String query = "SELECT * FROM Post Where thread = ? ORDER BY created, id";
            values.add(id);
            if (limit > 0) {
                query += " LIMIT ?";
                values.add(limit);
            }

            if(marker > 0) {
                query += " OFFSET ?";
                values.add(marker);
            }
            return jdbcTemplate.query(query, values.toArray(), new PostMapper());
        }
    }

    public List<PostModel> getTreeSortedPosts(ThreadModel threadModel, Integer limit, Integer marker, Boolean desc) {
        Integer id = threadModel.getId();
        final ArrayList<Object> values = new ArrayList<>();
        String query =  " SELECT u.nickname, p.* FROM Post p" +
                " JOIN \"User\" u ON (u.nickname = p.author)" +
                " WHERE p.thread = ?" +
                " ORDER BY tree";

        values.add(id);
        if (desc) {
            query += " DESC";
        }
        if (limit > 0) {
            query += " LIMIT ?";
            values.add(limit);
        }

        if(marker > 0) {
            query += " OFFSET ?";
            values.add(marker);
        }
        return jdbcTemplate.query(query, values.toArray(), new PostMapper());
    }

    public List<PostModel> getParentTreeSortedPosts(ThreadModel threadModel, Integer limit, Integer marker, Boolean desc) {
        Integer id = threadModel.getId();
        final ArrayList<Object> values = new ArrayList<>();

        String query = "WITH RECURSIVE " +
                "cond AS (" +
                " SELECT u.nickname, p.* FROM Post p " +
                " JOIN \"User\" u ON (u.nickname = p.author)" +
                " JOIN Forum f ON (f.slug = p.forum)" +
                " WHERE p.thread = ?" +
                "), " +
                " recursetree AS (" +
                " (SELECT * FROM cond" +
                " WHERE parent = 0" +
                " ORDER BY id";

        values.add(id);

        if (desc) {
            query += " DESC";
        }

        if (limit > 0) {
            query += " LIMIT ? ";
            values.add(limit);
        }

        if (marker > 0) {
            query += " OFFSET ? ";
            values.add(marker);
        }

        query += ") UNION ALL" +
                " (SELECT cond.* FROM recursetree"+
                " JOIN cond ON recursetree.id = cond.parent)"+
                ")"+
                " SELECT * FROM recursetree"+
                " ORDER BY recursetree.tree";

        if (desc) {
            query += " DESC ";
        }

        return jdbcTemplate.query(query, values.toArray(), new PostMapper());
    }

    private Boolean isEmptyField(String field) {
        return field == null || field.isEmpty();
    }
}
