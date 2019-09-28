package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.UsersTheme;
import ncu.soft.blog.service.UsersThemeService;
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
 * @date 2019/9/28 19:13
 */
@Service
public class UsersThemeServiceImpl implements UsersThemeService {

    @Resource(name = "mongoTemplate")
    MongoTemplate mongoTemplate;

    @Override
    public UsersTheme save(UsersTheme usersTheme) {
        return mongoTemplate.insert(usersTheme);
    }

    @Override
    public UsersTheme updateIndex(String index, String uid) {
        return update("index",index, uid);
    }

    @Override
    public UsersTheme updateSide(String side, String uid) {
        return update("side",side, uid);
    }

    @Override
    public UsersTheme updateStyle(String style, String uid) {
        return update("style",style, uid);
    }

    @Override
    public UsersTheme getTheme(String uid) {
        return mongoTemplate.findOne(new Query(Criteria.where("uid").is(uid)),UsersTheme.class);
    }

    private UsersTheme update(String key,String value, String uid){
        Query query = new Query(Criteria.where("uid").is(uid));
        Update update = new Update().set(key,value);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return mongoTemplate.findAndModify(query,update,options, UsersTheme.class);
    }
}