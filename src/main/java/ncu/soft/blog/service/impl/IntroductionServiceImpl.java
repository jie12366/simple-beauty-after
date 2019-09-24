package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.Introduction;
import ncu.soft.blog.service.IntroductionService;
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
 * @date 2019/9/6 11:34
 */
@Service
public class IntroductionServiceImpl implements IntroductionService {

    @Resource(name = "mongoTemplate")
    MongoTemplate mongoTemplate;

    @Override
    public Introduction save(Introduction introduction) {
        return mongoTemplate.insert(introduction);
    }

    @Override
    public Introduction update(Introduction introduction) {
        Query query = new Query(Criteria.where("uid").is(introduction.getUid()));
        Update update = Update.update("name",introduction.getName()).set("email",introduction.getEmail())
                .set("github",introduction.getGithub()).set("qq",introduction.getQq()).set("introduction",introduction.getIntroduction());
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return mongoTemplate.findAndModify(query,update,options,Introduction.class);
    }

    @Override
    public Introduction findByUid(String uid) {
        return mongoTemplate.findOne(new Query(Criteria.where("uid").is(uid)),Introduction.class);
    }
}