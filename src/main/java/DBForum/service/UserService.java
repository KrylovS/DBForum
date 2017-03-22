package DBForum.service;

import DBForum.mappers.UserMapper;
import DBForum.models.UserModel;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;

/**
 * Created by sergey on 14.03.17.
 */
@Repository
public class UserService {
    private final JdbcTemplate jdbcTemplate;
    private final UserMapper userMapper;

    public UserService(JdbcTemplate jdbcTemplate, UserMapper userMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.userMapper = userMapper;
    }

    public void create(String nickname, String fullname, String email, String about) throws DataAccessException {
        jdbcTemplate.update("INSERT INTO \"User\" (nickname, fullname, about, email) values (?, ?, ?, ?)",
                nickname, fullname, about, email);
    }

    public UserModel getUser(String nickname) throws DataAccessException {
        return jdbcTemplate.queryForObject("SELECT * FROM \"User\" WHERE nickname = ?::citext"
                , new Object[]{nickname}, new UserMapper());
    }

    public ArrayList<UserModel> getUsers(String nickname, String email) throws DataAccessException {
        return (ArrayList<UserModel>)jdbcTemplate.query("SELECT * FROM \"User\" WHERE nickname = ?::citext OR email = ?::citext",
                new Object[]{nickname, email}, new UserMapper());
    }

    public void update(UserModel userModel) throws DataAccessException {
        if (!isEmptyField(userModel.getEmail()) || !isEmptyField(userModel.getAbout())||
                !isEmptyField(userModel.getFullname())) {
            String SQL = "UPDATE \"User\" SET ";
            final ArrayList<String> updateArray = new ArrayList<>();
            final ArrayList<Object> parameterList = new ArrayList<>();

            if (!isEmptyField(userModel.getEmail())) {
                updateArray.add("email = ? ");
                parameterList.add(userModel.getEmail());
            }

            if (!isEmptyField(userModel.getAbout())) {
                updateArray.add("about = ? ");
                parameterList.add(userModel.getAbout());
            }

            if (!isEmptyField(userModel.getFullname())) {
                updateArray.add("fullname = ? ");
                parameterList.add(userModel.getFullname());
            }

            parameterList.add(userModel.getNickname());

            SQL += String.join(", ", updateArray);
            SQL += "WHERE nickname = ?::citext;";

            jdbcTemplate.update(SQL, parameterList.toArray());
        }
    }

    private Boolean isEmptyField(String field) {
        return field == null || field.isEmpty();
    }
}
