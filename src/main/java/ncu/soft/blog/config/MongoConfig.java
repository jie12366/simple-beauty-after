package ncu.soft.blog.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/27 8:49
 */
@Configuration
public class MongoConfig {

    @Bean
    public MongoClient mongoClient(){
        //配置连接mongo客户端，一共三个参数
        //主机端口，账号密码，超时时间等的配置
        return new MongoClient(new ServerAddress("120.78.162.121",27017),
                MongoCredential.createCredential("blog","myblog","blog".toCharArray()),
                MongoClientOptions.builder()
                .socketTimeout(3000)
                .minHeartbeatFrequency(25)
                .heartbeatSocketTimeout(3000)
                .build());
    }

    @Bean
    public MongoDbFactory mongoDbFactory(){
        //注册一个MongoDbFactory，连接到指定数据库
        return new SimpleMongoDbFactory(mongoClient(),"myblog");
    }

    @Bean
    public MongoTemplate mongoTemplate(){
        //将MongoDbFactory作为参数，注册MongoTemplate
        return new MongoTemplate(mongoDbFactory());
    }
}