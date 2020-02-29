package ncu.soft.blog;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BlogApplicationTests {

    private final static Logger log  = LoggerFactory.getLogger(BlogApplicationTests.class);

    @Autowired
    ValueOperations<String, Object> valueOperations;

    @Test
    public void testRedis() {
        String value = (String) valueOperations.get("test");
        log.info("缓存中的信息：【{}】", value);
    }
}
