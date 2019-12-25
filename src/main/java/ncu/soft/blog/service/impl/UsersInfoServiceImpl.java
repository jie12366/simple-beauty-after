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
public class UsersInfoServiceImpl implements UsersInfoService {

    @Resource(name = "mongoTemplate")
    MongoTemplate mongoTemplate;

    @Resource
    UsersThemeServiceImpl usersThemeService;

    @Override
    public UsersInfo findByUid(String uid) {
        UsersInfo usersInfo = mongoTemplate.findOne(new Query(Criteria.where("uid").is(uid)),UsersInfo.class);
        if (usersInfo != null){
            // 获取该用户的主题，并赋值到用户信息中
            usersInfo.setUsersTheme(usersThemeService.getTheme(uid));
            return usersInfo;
        }else {
            return null;
        }
    }

    @Override
    public String findNameByUid(String uid) {
        UsersInfo usersInfo = mongoTemplate.findOne(new Query(Criteria.where("uid").is(uid)),UsersInfo.class);
        if (usersInfo != null){
            return usersInfo.getNickName();
        }else {
            return "";
        }
    }

    @Override
    public UsersInfo save(UsersInfo usersInfo) {
        return mongoTemplate.insert(usersInfo);
    }

    @Override
    public UsersInfo updateHeadPath(String headPath, String uid) {
        Query query = new Query(Criteria.where("uid").is(uid));
        Update update = Update.update("headPath",headPath);
        //设置returnNew为true，返回更新后的数据
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return mongoTemplate.findAndModify(query,update,options,UsersInfo.class);
    }

    @Override
    public UsersInfo bindEmail(String email, String uid) {
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
    public UsersInfo updateInfo(String nickname, String introduction, String uid) {
        Query query = new Query(Criteria.where("uid").is(uid));
        Update update = Update.update("nickName",nickname).set("introduction",introduction);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return mongoTemplate.findAndModify(query,update,options,UsersInfo.class);
    }

    @Override
    public UsersInfo updateArticles(int articles, String uid) {
        return updateNumber(uid,"articles",articles);
    }

    @Override
    public UsersInfo updateReads(int reads, String uid) {
        return updateNumber(uid,"reads",1);
    }

    @Override
    public UsersInfo updateFans(int fans, String uid) {
        return updateNumber(uid,"fans",fans);
    }

    @Override
    public UsersInfo updateLikes(int likes, String uid) {
        return updateNumber(uid,"likes",likes);
    }

    @Override
    public UsersInfo updateAttentions(int attentions, String uid) {
        return updateNumber(uid,"attentions",attentions);
    }

    /**
     * 更新阅读、评论、喜欢数
     * @param uid 用户id
     * @param key 阅读/评论/喜欢
     * @param num 更新的数量
     * @return Article
     */
    private UsersInfo updateNumber(String uid, String key,int num){
        Query query = new Query(Criteria.where("uid").is(uid));
        Update update = new Update().inc(key,num);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return mongoTemplate.findAndModify(query,update,options,UsersInfo.class);
    }
}