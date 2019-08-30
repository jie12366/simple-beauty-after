package ncu.soft.blog;

import ncu.soft.blog.entity.Users;
import ncu.soft.blog.entity.UsersInfo;
import ncu.soft.blog.service.UserService;
import ncu.soft.blog.utils.GetString;
import ncu.soft.blog.utils.JwtUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BlogApplicationTests {

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    UserService userService;

    @Autowired
    ValueOperations<String, Object> valueOperations;

    @Test
    public void testFindAndModify() {
        Query query = new Query(Criteria.where("uid").is(11));
        Update update = Update.update("email","2263509062@qq.com");
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        System.out.println(mongoTemplate.findAndModify(query,update,options,UsersInfo.class));
    }
}
