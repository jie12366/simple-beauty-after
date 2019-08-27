package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.Users;
import ncu.soft.blog.service.UserService;
import ncu.soft.blog.utils.GetString;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/16 21:32
 */
@Service
@CacheConfig(cacheNames = "userService")
public class UserServiceImpl implements UserService {

    @Resource(name = "mongoTemplate")
    MongoTemplate mongoTemplate;

    @Override
    @Cacheable
    public Users findById(int id){
        return mongoTemplate.findById(id,Users.class);
    }

    @Override
    @Cacheable
    public Users findByAccount(String account) {
        return mongoTemplate.findOne(new Query(Criteria.where("uAccount").is(account)),Users.class);
    }

    @Override
    @Cacheable
    public boolean isExist(String account) {
        return (mongoTemplate.findOne(
                new Query(Criteria.where("uAccount").
                        is(account)),Users.class) != null);
    }

    @Override
    @CachePut
    public Users save(Users users) {
        String secret = GetString.getMd5(users.getUPwd());
        users.setUPwd(secret);
        return mongoTemplate.insert(users);
    }

    @Override
    @Cacheable
    public boolean verifyUser(Users users) {
        String secret = GetString.getMd5(users.getUPwd());
        Users users1 = mongoTemplate.findOne(new Query(Criteria.where("uAccount").is(users.getUAccount())
                .and("uPwd").is(secret)),Users.class);
        if (users1 != null){
            return true;
        }else {
            return false;
        }
    }
}