package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.UsersInfo;
import ncu.soft.blog.service.UsersInfoService;
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

    @Override
    public UsersInfo findByUid(int uid) {
        return mongoTemplate.findOne(new Query(Criteria.where("uid").is(uid)),UsersInfo.class);
    }

    @Override
    public void save(UsersInfo usersInfo) {
        mongoTemplate.insert(usersInfo);
    }

    @Override
    public void updateHeadPath(String headPath, int uid) {
        Query query = new Query(Criteria.where("uid").is(uid));
        Update update = Update.update("headPath",headPath);
        mongoTemplate.updateFirst(query,update,UsersInfo.class);
    }

    @Override
    public void bindEmail(String email, int uid) {
        Query query = new Query(Criteria.where("uid").is(uid));
        Update update = Update.update("email",email);
        mongoTemplate.updateFirst(query,update,UsersInfo.class);
    }

    @Override
    public boolean checkNickname(String nickname) {
        if(mongoTemplate.findOne(new Query(Criteria.where("nickName").is(nickname)),UsersInfo.class) != null){
            return true;
        }else {
            return false;
        }
    }

    @Override
    public void updateInfo(String nickname, String introduction, int uid) {
        Query query = new Query(Criteria.where("uid").is(uid));
        Update update = Update.update("nickName",nickname).set("introduction",introduction);
        mongoTemplate.updateFirst(query,update,UsersInfo.class);
    }
}