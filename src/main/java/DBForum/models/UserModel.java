package DBForum.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;



/**
 * Created by sergey on 13.03.17.
 */
public class UserModel {
    private String about;
    private String email;
    private String fullname;
    private String nickname;
    private Integer id;


    public UserModel(@JsonProperty("about") String about, @JsonProperty("email") String email,
                     @JsonProperty("fullname") String fullname, @JsonProperty("nickname") String nickname) {
        this.about = about;
        this.email = email;
        this.fullname = fullname;
        this.nickname = nickname;
    }

    public UserModel() {

    }

    public void setAbout(String about) {
        this.about = about;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAbout() {
        return about;
    }

    public String getEmail() {
        return email;
    }

    public String getFullname() {
        return fullname;
    }

    public String getNickname() {
        return nickname;
    }

    public Integer getId() {
        return id;
    }

    public ObjectNode toObjectNode(ObjectMapper mapper) {
        final ObjectNode result = mapper.createObjectNode();
        result.put("nickname", nickname);
        result.put("email", email);
        result.put("about", about);
        result.put("fullname", fullname);

        return result;
    }
}
