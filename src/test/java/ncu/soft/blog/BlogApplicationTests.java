package ncu.soft.blog;

import ncu.soft.blog.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

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
    public void testQiNiu() {
        String[] keys = "http://cdn.jie12366.xyz/FvMexLvHEcPzdmXf-1GzVVrRzJ0e".split("xyz/");
        System.out.println(keys[1]);
    }
}
