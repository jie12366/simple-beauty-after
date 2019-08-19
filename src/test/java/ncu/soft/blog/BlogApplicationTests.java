package ncu.soft.blog;

import ncu.soft.blog.entity.Users;
import ncu.soft.blog.service.UserService;
import ncu.soft.blog.utils.GetString;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
    public void testMongo(){
        Users users = new Users("13330114338","123456");
        String secret = GetString.getMd5(users.getUPwd());
        System.out.println(mongoTemplate.findOne(new Query(Criteria.where("uAccount").is(users.getUAccount())
                .and("uPwd").is(secret)),Users.class));
    }

    @Test
    public void testRedis() {
        Users users = new Users("133","123");
        valueOperations.set("user",users,10,TimeUnit.MINUTES);

        System.out.println("getUser" + valueOperations.get("user"));
    }
}
