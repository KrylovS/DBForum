package DBForum;

import DBForum.models.*;
import DBForum.service.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.NumberUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sergey on 12.03.17.
 */

@RestController
public class Controller {
    private static final ObjectMapper mapper = new ObjectMapper();

    private JdbcTemplate jdbcTemplate;

    @Autowired
    ForumService forumService;

    @Autowired
    UserService userService;

    @Autowired
    ThreadService threadService;

    @Autowired
    PostService postService;

    @Autowired
    DBService dbService;


    @PostMapping(value = "/api/forum/create")
    public ForumModel createForum(@RequestBody ForumModel forumModel, HttpServletResponse response) {
        try {
            final ForumModel existedForum = forumService.getForum(forumModel.getSlug());
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return existedForum;
        } catch (DataAccessException e) {
            try {
                final UserModel userModel = userService.getUser(forumModel.getUser());
                forumModel.setUser(userModel.getNickname());
                forumService.create(forumModel);
                response.setStatus(HttpServletResponse.SC_CREATED);
                ForumModel forumModel1 = forumService.getForum(forumModel.getSlug());
                return forumService.getForum(forumModel.getSlug());
            } catch (DataAccessException e1) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }
        }

    }

    @PostMapping(value = "/api/forum/{slug}/create")
    public ThreadModel createThread(@PathVariable("slug") String slugForum, @RequestBody ThreadModel threadModel, HttpServletResponse response) {
        final  UserModel userModel;
        try {
            userModel = userService.getUser(threadModel.getAuthor());
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        final  ForumModel forumModel;
        try {
            forumModel = forumService.getForum(slugForum);
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        try {
            final ThreadModel existedThread = threadService.getThread(threadModel);
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return existedThread;
        } catch (DataAccessException e) {
            threadModel.setAuthor(userModel.getNickname());
            threadModel.setForum(forumModel.getSlug());
            threadService.createThread(threadModel);
            forumService.incrementThreads(forumModel.getSlug());
            response.setStatus(HttpServletResponse.SC_CREATED);

            return threadService.getThread(threadModel);
        }
    }

    @GetMapping(value = "api/forum/{slug}/details")
    public ForumModel getForum(@PathVariable("slug") String slug, HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
        try {
            return forumService.getForum(slug);
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }

    @GetMapping(value = "api/forum/{slug}/threads")
    public List<ThreadModel> getThreads(@PathVariable("slug") String slug,
                                        @RequestParam(value = "limit", defaultValue = "0") Integer limit,
                                        @RequestParam(value = "since", defaultValue = "") String since,
                                        @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
                                        HttpServletResponse response) {
        try{
            forumService.getForum(slug);
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        List<ThreadModel> threadModelList = threadService.getThreads(slug,limit,since,desc);
        if(threadModelList == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        } else {
            return threadModelList;
        }
    }

    @GetMapping(value = "api/forum/{slug}/users")
    public List<UserModel> getUsers(@PathVariable String slug,
                                    @RequestParam(value = "limit", defaultValue = "0") Integer limit,
                                    @RequestParam(value = "since", defaultValue = "") String since,
                                    @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
                                    HttpServletResponse response) {
        try {
            forumService.getForum(slug);
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        return forumService.getUsers(slug, limit, since, desc);
    }


    @PostMapping(value = "/api/user/{nickname}/create")
    public String createUser(@RequestBody UserModel user, @PathVariable("nickname") String nickname, HttpServletResponse response) throws IOException {
        try {
            userService.create(nickname, user.getFullname(), user.getEmail(), user.getAbout());
            response.setStatus(HttpServletResponse.SC_CREATED);
            return userService.getUser(nickname).toObjectNode(mapper).toString();
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);

            final ArrayList<UserModel> oldUsers = userService.getUsers(nickname, user.getEmail());

            final StringWriter sw = new StringWriter();
            mapper.writeValue(sw, oldUsers);
            return sw.toString();
        }
    }

    @GetMapping(value = "/api/user/{nickname}/profile")
    public UserModel getUser(@PathVariable("nickname") String nickname, HttpServletResponse response) {
        try {
            final UserModel user = userService.getUser(nickname);
            response.setStatus(HttpServletResponse.SC_OK);
            return user;
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
    }

    @PostMapping(value = "/api/user/{nickname}/profile")
    public UserModel updateUser(@RequestBody UserModel user, @PathVariable("nickname") String nickname, HttpServletResponse response) throws IOException {
        user.setNickname(nickname);
        try {
            try {
                userService.getUser(nickname);
            } catch (DataAccessException e) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return null;
            }

            userService.update(user);
            response.setStatus(HttpServletResponse.SC_OK);
            return userService.getUser(nickname);
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        }
    }

    @PostMapping(value = "api/thread/{slug_or_id}/create")
    public List<PostModel> createPosts(@PathVariable("slug_or_id") String slugOrId, @RequestBody List<PostModel> postModelList,
                                       HttpServletResponse response) {
        final Timestamp createdTime = new Timestamp(System.currentTimeMillis());
        final ThreadModel threadModel = getThreadBySlugOrId(slugOrId);
        if(threadModel == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        try {
            final ForumModel forumModel = forumService.getForum(threadModel.getForum());
            for (PostModel post : postModelList) {
                post.setForum(forumModel.getSlug());
                post.setThread(threadModel.getId());
                post.setCreated(createdTime);
            }

            for (PostModel post : postModelList) {

                try {
                    userService.getUser(post.getAuthor());
                } catch (DataAccessException e) {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    return null;
                }

                final Integer parentId = post.getParent();
                if (parentId != null && !parentId.equals(0)) {
                    postService.getPostByIdAndThread(parentId, threadModel.getId());
                }

                postService.create(post);
            }
            forumService.increasePost(forumModel.getSlug(),postModelList.size());
            response.setStatus(HttpServletResponse.SC_CREATED);
            Integer id = postService.getLastPostId();
            if(id == null) {
                id = 0;
            }
            return postService.getPostsWhereIdGreater(id - postModelList.size());


        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return null;
        }
    }

    @PostMapping(value = "api/thread/{slug_or_id}/vote")
    public ThreadModel vote(@PathVariable("slug_or_id") String slugOrId, @RequestBody VoteModel voteModel,
                       HttpServletResponse response) {
        final UserModel userModel;
        try {
            userModel = userService.getUser(voteModel.getNickname());
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        final ThreadModel threadModel = getThreadBySlugOrId(slugOrId);
        if (threadModel == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        Integer voice = getVoice(threadModel.getId(), userModel.getId());

        if ((voteModel.getVoice() == 1) && (voice != 1)) {
            threadService.vote(threadModel.getId(), userModel.getId(), voteModel.getVoice() - voice);
        }

        if ((voteModel.getVoice() == -1) && (voice != -1)) {
            threadService.unvote(threadModel.getId(), userModel.getId(), -voteModel.getVoice() + voice);
        }

        response.setStatus(HttpServletResponse.SC_OK);
        return threadService.getThreadById(threadModel.getId());
    }

    private Integer getVoice(Integer threadId, Integer userId) {
        try {
            return threadService.getVoice(threadId, userId);
        } catch (DataAccessException e) {
            return 0;
        }
    }

    @GetMapping(value = "api/thread/{slug_or_id}/details")
    public ThreadModel getthreadDetails(@PathVariable("slug_or_id") String slugOrId, HttpServletResponse response) {
        final ThreadModel threadModel = getThreadBySlugOrId(slugOrId);
        if (threadModel == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        return threadModel;
    }


    @PostMapping(value = "api/thread/{slug_or_id}/details")
    public ThreadModel updateThread(@PathVariable("slug_or_id") String slugOrId, @RequestBody ThreadModel newThreadModel,
                                    HttpServletResponse response) {
        final ThreadModel threadModel = getThreadBySlugOrId(slugOrId);
        if (threadModel == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }
        threadService.update(threadModel, newThreadModel);
        return threadService.getThreadById(threadModel.getId());
    }

    @GetMapping(value = "api/thread/{slug_or_id}/posts")
    public ObjectNode getPosts(@PathVariable("slug_or_id") String slugOrId,
                               @RequestParam(value = "limit", defaultValue = "0") Integer limit,
                               @RequestParam(value = "marker", defaultValue = "0") Integer marker,
                               @RequestParam(value = "sort", defaultValue = "flat") String sort,
                               @RequestParam(value = "desc", defaultValue = "false") Boolean desc,
                               HttpServletResponse response) {

        final ThreadModel threadModel = getThreadBySlugOrId(slugOrId);
        if (threadModel == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        List<PostModel> sortedPosts = null;
        if (sort.equals("flat")) {
            sortedPosts = postService.getFlatSortedPosts(threadModel, limit, marker, desc);
            marker += sortedPosts.size();
        }

        if (sort.equals("tree")) {
            sortedPosts = postService.getTreeSortedPosts(threadModel, limit, marker, desc);
            marker += sortedPosts.size();
        }

        if (sort.equals("parent_tree")) {
            sortedPosts = postService.getParentTreeSortedPosts(threadModel, limit, marker, desc);
            for (PostModel post : sortedPosts) {
                if(post.getParent() == 0 || post.getParent() == null) {
                    marker++;
                }
            }
        }
        final ArrayNode postsJSON = mapper.createArrayNode();
        for (PostModel post : sortedPosts) {
            postsJSON.add(mapper.convertValue(post, JsonNode.class));
        }

        final ObjectNode sortedPostsJSON = mapper.createObjectNode();
        sortedPostsJSON.put("marker", marker.toString());
        sortedPostsJSON.set("posts", postsJSON);
        return sortedPostsJSON;
    }

    @PostMapping(value = "api/post/{id}/details")
    public PostModel updatePost(@PathVariable("id") Integer id, @RequestBody PostModel newPost,
                                    HttpServletResponse response) {
        final PostModel postModel;
        try {
            postModel = postService.getPostById(id);
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        postService.update(postModel, newPost);
        return postService.getPostById(id);

    }

    @GetMapping(value = "api/post/{id}/details")
    public PostDetailsModel getPostDetails(@PathVariable("id") Integer id,
                                     @RequestParam(value = "related", required = false) String[] related,
                                     HttpServletResponse response) {

        final PostModel postModel;
        try {
            postModel = postService.getPostById(id);
        } catch (DataAccessException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return null;
        }

        UserModel userModel = null;
        ForumModel forumModel = null;
        ThreadModel threadModel = null;

        if (related == null) {
            return new PostDetailsModel(userModel, forumModel, postModel, threadModel);
        }

        for (String elem : related) {
            if (elem.equals("user")) {
                userModel = userService.getUser(postModel.getAuthor());
            }

            if (elem.equals("forum")) {
                forumModel = forumService.getForum(postModel.getForum());
            }

            if (elem.equals("thread")) {
                threadModel = threadService.getThreadById(postModel.getThread());
            }
        }
        return new PostDetailsModel(userModel, forumModel, postModel, threadModel);
    }


    @GetMapping("api/service/status")
    DBServiceModel getStatus() {
        return dbService.getStatus();
    }

    @PostMapping("api/service/clear")
    void clearDB() {
        dbService.clear();
    }

    private ThreadModel getThreadBySlugOrId(String slugOrId) {
        ThreadModel threadModel;
        if(isDigit(slugOrId)) {
            try {
                return threadService.getThreadById(Integer.parseInt(slugOrId));
            } catch (DataAccessException e) {
                try {
                    return threadService.getThreadBySlug(slugOrId);
                } catch (DataAccessException e1) {
                    return null;
                }
            }
        } else {
            try {
                return threadService.getThreadBySlug(slugOrId);
            } catch (DataAccessException e) {
                return null;
            }
        }
    }
    private static boolean isDigit(String s) throws NumberFormatException {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
