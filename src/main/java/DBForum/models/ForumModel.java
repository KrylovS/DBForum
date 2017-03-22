package DBForum.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sergey on 12.03.17.
 */
public class ForumModel {

    private String title;
    private String user;
    private Integer posts;
    private String slug;
    private Integer threads;


    public ForumModel( @JsonProperty("posts") final Integer posts, @JsonProperty("slug") final String slug,
                       @JsonProperty("threads") final Integer threads, @JsonProperty("title") final String title,
                       @JsonProperty("user") final String user) {
        this.posts = posts;
        this.slug = slug;
        this.threads = threads;
        this.title = title;
        this.user = user;
    }

    public ForumModel() {

    }

    public void setPosts(Integer posts) {
        this.posts = posts;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setThreads(Integer threads) {
        this.threads = threads;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getPosts() {
        return posts;
    }

    public String getSlug() {
        return slug;
    }

    public Integer getThreads() {
        return threads;
    }

    public String getTitle() {
        return title;
    }

    public String getUser() {
        return user;
    }
}
