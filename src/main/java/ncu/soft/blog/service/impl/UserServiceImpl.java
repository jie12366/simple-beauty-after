package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.Users;
import ncu.soft.blog.entity.UsersInfo;
import ncu.soft.blog.service.UserService;
import ncu.soft.blog.utils.GetString;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
    @Cacheable(key = "#id")
    public Users findById(int id){
        return mongoTemplate.findById(id,Users.class);
    }

    @Override
    public Users findByAccount(String account) {
        return mongoTemplate.findOne(new Query(Criteria.where("uAccount").is(account)),Users.class);
    }

    @Override
    @CachePut(key = "#users.id")
    public Users save(Users users) {
        String secret = GetString.getMd5(users.getUPwd());
        users.setUPwd(secret);
        return mongoTemplate.insert(users);
    }

    @Override
    public Users verifyUser(Users users) {
        String secret = GetString.getMd5(users.getUPwd());
        return mongoTemplate.findOne(new Query(Criteria.where("uAccount").is(users.getUAccount())
                .and("uPwd").is(secret)),Users.class);
    }

    @Override
    @CacheEvict(allEntries = true)
    public Users updatePwd(String email, String pwd) {
        String secret = GetString.getMd5(pwd);
        UsersInfo usersInfo = mongoTemplate.findOne(new Query(Criteria.where("email").is(email)),UsersInfo.class);
        if (usersInfo != null) {
            Query query = new Query(Criteria.where("id").is(usersInfo.getUid()));
            Update update = Update.update("uPwd",secret);
            FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
            return mongoTemplate.findAndModify(query,update,options,Users.class);
        }else {
            return null;
        }
    }
}