package DBForum.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.sql.Timestamp;

/**
 * Created by sergey on 13.03.17.
 */
public class PostModel {
    private String author;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    private Timestamp created;
    private String forum;
    private Integer id;
    private Boolean isEdited;
    private String message;
    private Integer parent;
    private Integer thread;

    PostModel(@JsonProperty String author,
              @JsonProperty Timestamp created,
              @JsonProperty String forum, @JsonProperty Integer id, @JsonProperty Boolean isEdited,
              @JsonProperty String message, @JsonProperty Integer parent, @JsonProperty Integer thread) {
        this.author = author;
        this.created = created;
        this.forum = forum;
        this.id = id;
        this.isEdited = isEdited;
        this.message = message;
        this.parent = parent;
        this.thread = thread;
    }

    public PostModel() {

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

    public void setIsEdited(Boolean isEdited) {
        this.isEdited = isEdited;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setParent(Integer parent) {
        this.parent = parent;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
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

    public Boolean getIsEdited() {
        return isEdited;
    }

    public String getMessage() {
        return message;
    }

    public Integer getParent() {
        return parent;
    }

    public Integer getThread() {
        return thread;
    }

}
