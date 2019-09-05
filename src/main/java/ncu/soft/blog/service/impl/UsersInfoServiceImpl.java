package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.Article;
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
    @CachePut(key = "#uid")
    public UsersInfo updateArticles(int articles, int uid) {
        return updateNumber(uid,"articles");
    }

    @Override
    @CachePut(key = "#uid")
    public UsersInfo updateReads(int reads, int uid) {
        return updateNumber(uid,"reads");
    }

    @Override
    @CachePut(key = "#uid")
    public UsersInfo updateFans(int fans, int uid) {
        return updateNumber(uid,"fans");
    }

    @Override
    @CachePut(key = "#uid")
    public UsersInfo updateLikes(int likes, int uid) {
        return updateNumber(uid,"likes");
    }

    @Override
    @CachePut(key = "#uid")
    public UsersInfo updateAttentions(int attentions, int uid) {
        return updateNumber(uid,"attentions");
    }

    /**
     * 更新阅读、评论、喜欢数
     * @param uid 用户id
     * @param key 阅读/评论/喜欢
     * @return Article
     */
    private UsersInfo updateNumber(int uid, String key){
        Query query = new Query(Criteria.where("uid").is(uid));
        Update update = new Update().inc(key,1);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return mongoTemplate.findAndModify(query,update,options,UsersInfo.class);
    }
}