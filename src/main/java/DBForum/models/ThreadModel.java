package DBForum.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;

/**
 * Created by sergey on 13.03.17.
 */
public class ThreadModel {
    private String author;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private Timestamp created;
    private String forum;
    private Integer id;
    private String message;
    private String slug;
    private String title;
    private Integer votes;

    @JsonCreator
    public ThreadModel(@JsonProperty("author") String author, @JsonProperty("created") Timestamp created,
                       @JsonProperty("forum") String forum, @JsonProperty("id") Integer id,
                       @JsonProperty("message") String message, @JsonProperty("slug") String slug,
                       @JsonProperty("title") String title, @JsonProperty("votes") Integer votes) {
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.id = id;
        this.message = message;
        this.slug = slug;
        this.title = title;
        this.votes = votes;

    }

    public ThreadModel() {

    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public void setForum(String forum) {
        this.forum = forum;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setVotes(Integer votes) {
        this.votes = votes;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public Timestamp getCreated() {
        return created;
    }

    public String getForum() {
        return forum;
    }

    public Integer getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public String getSlug() {
        return slug;
    }

    public Integer getVotes() {
        return votes;
    }

    public String getTitle() {
        return title;
    }
}
