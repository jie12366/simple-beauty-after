package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.UsersInfo;
import ncu.soft.blog.service.UsersInfoService;
import org.springframework.cache.annotation.CacheConfig;
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
 * @date 2019/8/24 13:19
 */
@Service
@CacheConfig(cacheNames = "usersInfoService")
public class UsersInfoServiceImpl implements UsersInfoService {

    @Resource(name = "mongoTemplate")
    MongoTemplate mongoTemplate;

    @Override
    @Cacheable(key = "#uid")
    public UsersInfo findByUid(int uid) {
        return mongoTemplate.findOne(new Query(Criteria.where("uid").is(uid)),UsersInfo.class);
    }

    @Override
    @CachePut(key = "#usersInfo.uid")
    public UsersInfo save(UsersInfo usersInfo) {
        return mongoTemplate.insert(usersInfo);
    }

    @Override
    @CachePut(key = "#uid")
    public UsersInfo updateHeadPath(String headPath, int uid) {
        Query query = new Query(Criteria.where("uid").is(uid));
        Update update = Update.update("headPath",headPath);
        //设置returnNew为true，返回更新后的数据
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return mongoTemplate.findAndModify(query,update,options,UsersInfo.class);
    }

    @Override
    @CachePut(key = "#uid")
    public UsersInfo bindEmail(String email, int uid) {
        Query query = new Query(Criteria.where("uid").is(uid));
        Update update = Update.update("email",email);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return mongoTemplate.findAndModify(query,update,options,UsersInfo.class);
    }

    @Override
    public UsersInfo checkNickname(String nickname) {
        return mongoTemplate.findOne(new Query(Criteria.where("nickName").is(nickname)),UsersInfo.class);
    }

    @Override
    @CachePut(key = "#uid")
    public UsersInfo updateInfo(String nickname, String introduction, int uid) {
        Query query = new Query(Criteria.where("uid").is(uid));
        Update update = Update.update("nickName",nickname).set("introduction",introduction);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return mongoTemplate.findAndModify(query,update,options,UsersInfo.class);
    }

    @Override
    public void updateArticles(int articles, int uid) {
        Query query = new Query(Criteria.where("uid").is(uid));
        //按指定的数字增加
        Update update = new Update().inc("articles",articles);
        mongoTemplate.updateFirst(query,update,UsersInfo.class);
    }
}