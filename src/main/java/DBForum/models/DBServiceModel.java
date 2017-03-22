package DBForum.models;

/**
 * Created by sergey on 20.03.17.
 */
public class DBServiceModel {
    private Integer forum;
    private Integer post;
    private Integer thread;
    private Integer user;

    public void setForum(Integer forum) {
        this.forum = forum;
    }

    public void setPost(Integer post) {
        this.post = post;
    }

    public void setThread(Integer thread) {
        this.thread = thread;
    }

    public void setUser(Integer user) {
        this.user = user;
    }

    public Integer getForum() {
        return forum;
    }

    public Integer getPost() {
        return post;
    }

    public Integer getThread() {
        return thread;
    }

    public Integer getUser() {
        return user;
    }
}
