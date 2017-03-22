package DBForum.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by sergey on 13.03.17.
 */
public class VoteModel {
    private String nickname;
    private Integer voice;

    @JsonCreator
    VoteModel(@JsonProperty("nickname") String nickname, @JsonProperty("voice") Integer voice) {
        this.nickname = nickname;
        this.voice = voice;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setVoice(Integer voice) {
        this.voice = voice;
    }

    public String getNickname() {
        return nickname;
    }

    public Integer getVoice() {
        return voice;
    }
}
