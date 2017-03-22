package DBForum.service;

import DBForum.mappers.PostMapper;
import DBForum.models.PostModel;
import DBForum.models.ThreadModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by sergey on 15.03.17.
 */
@Repository
public class PostService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public void create(PostModel postModel) {
        final ArrayList<String> field = new ArrayList<>();
        final ArrayList<Object> values = new ArrayList<>();

        if (!isEmptyField(postModel.getAuthor())) {
            field.add("author");
            values.add(postModel.getAuthor());
        }

        if (!isEmptyField(postModel.getForum())) {
            field.add("forum");
            values.add(postModel.getForum());
        }

        if (!isEmptyField(postModel.getMessage())) {
            field.add("message");
            values.add(postModel.getMessage());
        }

        if(postModel.getCreated() != null) {
            field.add("created");
            values.add(postModel.getCreated());
        }

        if(postModel.getIsEdited() != null) {
            field.add("isEdited");
            values.add(postModel.getIsEdited());
        } else {
            field.add("isEdited");
            values.add(false);
        }

        if(postModel.getParent() != null) {
            field.add("parent");
            values.add(postModel.getParent());
        }

        if(postModel.getThread() != null) {
            field.add("thread");
            values.add(postModel.getThread());
        }

        String query = "INSERT INTO Post (" + String.join(", ", field) +
                ") VALUES (" + String.join(", ", Collections.nCopies(values.size(), "?")) +")";
        jdbcTemplate.update(query, values.toArray());
    }

    public List<PostModel> getPostsWhereIdGreater(Integer id) {
        return jdbcTemplate.query("SELECT * FROM Post WHERE id > ? ORDER BY id",
                new Object[]{id}, new PostMapper());
    }


    public Integer getLastPostId() {
        return jdbcTemplate.queryForObject("SELECT max(id) FROM Post", Integer.class);
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
        String query = "WITH RECURSIVE recursetree (id, path) AS (" +
                " SELECT id, array_append('{}'::INTEGER[], id) FROM Post"+
                " WHERE parent = 0 AND thread = ?" +
                " UNION ALL"+
                " SELECT p.id, array_append(path, p.id)"+
                " FROM Post p"+
                " JOIN recursetree rt ON rt.id = p.parent AND p.thread = ?"+
                " )"+
                " SELECT p.* FROM recursetree JOIN Post p ON recursetree.id = p.id"+
                " ORDER BY recursetree.path";

        values.add(id);
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

        String query = "WITH RECURSIVE recursetree (id, path) AS (" +
                " SELECT id, array_append('{}'::INTEGER[], id) FROM" +
                " (SELECT DISTINCT id FROM Post" +
                " WHERE thread = ? AND parent = 0" +
                " ORDER BY id ";
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

        query += ") superParents " +
                "UNION ALL " +
                "SELECT p.id, array_append(path, p.id) FROM Post p " +
                "JOIN recursetree rp ON rp.id = p.parent " +
                ") " +
                "SELECT p.* " +
                "FROM recursetree JOIN Post p ON recursetree.id = p.id " +
                "ORDER BY recursetree.path ";

        if (desc) {
            query += " DESC ";
        }

        return jdbcTemplate.query(query, values.toArray(), new PostMapper());
    }

    private Boolean isEmptyField(String field) {
        return field == null || field.isEmpty();
    }

}
