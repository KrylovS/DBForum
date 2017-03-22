package DBForum.service;

import DBForum.mappers.ForumMapper;
import DBForum.mappers.UserMapper;
import DBForum.models.ForumModel;
import DBForum.models.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergey on 13.03.17.
 */
@Repository
public class ForumService {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public final void test() {
        jdbcTemplate.queryForList("SELECT * FROM forum");
    }

    public void create(ForumModel forumModel) {
        jdbcTemplate.update("INSERT into Forum (title, \"user\", slug) values (?, ?, ?)",
                forumModel.getTitle(), forumModel.getUser(), forumModel.getSlug());
    }

    public ForumModel getForum(String slug) {
        return jdbcTemplate.queryForObject("SELECT * FROM Forum WHERE slug = ?::citext",
                new Object[]{slug}, new ForumMapper());
    }

    public void incrementThreads(String slug) {
        jdbcTemplate.update("UPDATE Forum SET threads = threads + 1 WHERE slug = ?::citext", slug);
    }

    public void increasePost(String slug, Integer count) {
        jdbcTemplate.update("UPDATE Forum SET posts = posts + ? WHERE slug = ?::citext", count, slug);
    }

    public List<UserModel> getUsers(String slug, Integer limit, String since, Boolean desc) {
        ArrayList<Object> parameters = new ArrayList<>();
        String query = "SELECT * FROM \"User\" WHERE \"User\".nickname IN " +
                "(SELECT POST.author FROM POST WHERE POST.forum = ?::citext " +
                "UNION " +
                "SELECT Thread.author FROM Thread WHERE Thread.forum = ?::citext)";
        parameters.add(slug);
        parameters.add(slug);

        if (!since.isEmpty() && since != null) {
            if (desc) {
                query += " AND \"User\".nickname < ?::citext ";
            } else {
                query += " AND \"User\".nickname  > ?::citext ";
            }
            parameters.add(since);
        }

        query += "ORDER BY LOWER(\"User\".nickname)";

        if (desc) {
            query += " DESC ";
        }

        if (limit > 0) {
            query += " LIMIT ? ";
            parameters.add(limit);
        }

        return jdbcTemplate.query(query, parameters.toArray(), new UserMapper());
    }


}