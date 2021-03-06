package DBForum.service;

import DBForum.mappers.ThreadMapper;
import DBForum.models.ThreadModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergey on 15.03.17.
 */
@Repository
public class ThreadService {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public ThreadModel getThread(ThreadModel threadModel) {
        if(!isEmptyField(threadModel.getSlug())) {
            return jdbcTemplate.queryForObject("SELECT * FROM Thread WHERE slug = ?::citext",
                    new Object[]{threadModel.getSlug()}, new ThreadMapper());
        } else {
            return jdbcTemplate.queryForObject("SELECT * FROM Thread WHERE forum = ? AND title = ?",
                    new Object[]{threadModel.getForum(), threadModel.getTitle()}, new ThreadMapper());
        }
    }

    public ThreadModel getThreadBySlug(String slug) {
        return jdbcTemplate.queryForObject("SELECT * FROM Thread WHERE slug = ?::citext",
                new Object[]{slug}, new ThreadMapper());
    }

    public ThreadModel getThreadById(Integer id) {
        return jdbcTemplate.queryForObject("SELECT * FROM Thread WHERE id = ?",
                new Object[]{id}, new ThreadMapper());
    }

    public void createThread(ThreadModel threadModel) {
        final ArrayList<Object> values = new ArrayList<>();

        values.add(threadModel.getAuthor());
        values.add(threadModel.getSlug());
        values.add(threadModel.getForum());
        values.add(threadModel.getMessage());
        values.add(threadModel.getTitle());
        if(threadModel.getVotes() != null) {
            values.add(threadModel.getVotes());
        } else {
            values.add(0);
        }

        if(threadModel.getCreated() != null) {
            values.add(threadModel.getCreated());
            jdbcTemplate.update("INSERT INTO Thread (author, slug, forum, message, title, votes, created) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) ", values.toArray());
        } else {
            jdbcTemplate.update("INSERT INTO Thread (author, slug, forum, message, title, votes) " +
                    "VALUES (?, ?, ?, ?, ?, ?) ", values.toArray());
        }
    }

    public List<ThreadModel> getThreads(String slug, Integer limit, String since, Boolean desc) {
        final ArrayList<Object> values = new ArrayList<>();
        values.add(slug);
        String query = "SELECT * FROM Thread WHERE forum = ?";
        if(!isEmptyField(since)) {
            if(desc) {
                query += " AND created <= " + "'" + since + "'";
            } else {
                query += " AND created >= " + "'" + since + "'";
            }
        }

        if(desc) {
            query += " ORDER BY created DESC";
        } else {
            query += " ORDER BY created";
        }

        //if(limit > 0) {
            query += " LIMIT ?";
            values.add(limit);
        //}

        return jdbcTemplate.query(query, values.toArray(), new ThreadMapper());
    }

    public void vote(Integer threadId, Integer userId, Integer voiceValue) {
            if (voiceValue == 2) {
                jdbcTemplate.update("UPDATE Thread SET votes = votes + ? WHERE id = ?; ",
                        voiceValue, threadId);

                jdbcTemplate.update("UPDATE UserVoteThread SET voice = 1 WHERE threadId = ? AND userId = ?",
                        threadId, userId);
            } else {
                jdbcTemplate.update("UPDATE Thread SET votes = votes + 1 WHERE id = ?; " +
                        "INSERT INTO UserVoteThread (threadId, userId, voice) VALUES (?, ?, 1)",
                        threadId, threadId, userId);
            }
    }

    public void unvote(Integer threadId, Integer userId, Integer unvoiceValue) {
        if (unvoiceValue == 2) {
            jdbcTemplate.update("UPDATE Thread SET votes = votes - ? WHERE id = ?; ",
                              unvoiceValue, threadId);

            jdbcTemplate.update("UPDATE UserVoteThread SET voice = -1 WHERE threadId = ? AND userId = ?",
                    threadId, userId);
        } else {
            jdbcTemplate.update("UPDATE Thread SET votes = votes - 1 WHERE id = ?; ",
                    threadId);
            jdbcTemplate.update("INSERT INTO UserVoteThread (threadId, userId, voice) VALUES (?, ?, -1)",
                     threadId, userId);
        }
    }

    public Integer getVoice(Integer threadId, Integer userId) { ;
            return jdbcTemplate.queryForObject("SELECT voice FROM UserVoteThread WHERE threadId = ? AND userId = ?",
                    new Object[]{threadId, userId}, Integer.class);
    }

    public void update(ThreadModel threadModel, ThreadModel newThreadModel) {
        final ArrayList<String> fields = new ArrayList<>();
        final ArrayList<Object> values = new ArrayList<>();

        if (!isEmptyField(newThreadModel.getTitle())) {
            fields.add("title = ?");
            values.add(newThreadModel.getTitle());
        }

        if (!isEmptyField(newThreadModel.getMessage())) {
            fields.add("message = ?");
            values.add(newThreadModel.getMessage());
        }

        if(values.size() > 0) {
            values.add(threadModel.getId());
            jdbcTemplate.update("UPDATE Thread SET " + String.join(", ", fields) + " WHERE id = ?", values.toArray());
        }
    }

    public void updateUserForum(ThreadModel thread, String forumSlug) {
        jdbcTemplate.update("INSERT INTO UserForum (user_nickname, forum)  VALUES (?::citext, ?::citext)" +
                        " ON CONFLICT DO NOTHING",
                thread.getAuthor(), forumSlug);
    }

    private Boolean isEmptyField(String field) {
        return field == null || field.isEmpty();
    }

}
