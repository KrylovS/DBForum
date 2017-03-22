package DBForum.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sergey on 16.03.17.
 */
public class PostDetailsModel {
    private UserModel author;
    private ForumModel forum;
    private PostModel post;
    private ThreadModel thread;

    @JsonCreator
    public PostDetailsModel(
            @JsonProperty("author") final UserModel author, @JsonProperty("forum") final ForumModel forum,
            @JsonProperty("post") final PostModel post, @JsonProperty("thread") final ThreadModel thread) {
        this.author = author;
        this.forum = forum;
        this.post = post;
        this.thread = thread;
    }

    public void setAuthor(UserModel author) {
        this.author = author;
    }

    public void setForum(ForumModel forum) {
        this.forum = forum;
    }

    public void setPost(PostModel post) {
        this.post = post;
    }

    public void setThread(ThreadModel thread) {
        this.thread = thread;
    }

    public UserModel getAuthor() {
        return author;
    }

    public ForumModel getForum() {
        return forum;
    }

    public PostModel getPost() {
        return post;
    }

    public ThreadModel getThread() {
        return thread;
    }
}
