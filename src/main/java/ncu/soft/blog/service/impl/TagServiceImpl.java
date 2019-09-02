package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.MyTag;
import ncu.soft.blog.service.TagService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/26 20:17
 */
@Service
@CacheConfig(cacheNames = "tagService")
public class TagServiceImpl implements TagService {

    @Resource(name = "mongoTemplate")
    MongoTemplate mongoTemplate;

    @Override
    public MyTag findByUid(int uid) {
        return mongoTemplate.findOne(new Query(Criteria.where("uid").is(uid)),MyTag.class);
    }

    @Override
    public MyTag save(MyTag tag) {
        return mongoTemplate.insert(tag);
    }

    @Override
        public MyTag update(MyTag tag) {
        Query query = new Query(Criteria.where("uid").is(tag.getUid()));
        Update update = Update.update("tags",tag.getTags()).set("categorys",tag.getCategorys()).set("archives",tag.getArchives());
        return mongoTemplate.findAndModify(query,update,new FindAndModifyOptions().returnNew(true), MyTag.class);
    }

    @Override
    public MyTag getAllTags(int uid) {
        return mongoTemplate.findOne(new Query(Criteria.where("uid").is(uid)),MyTag.class);
    }

    @Override
    public Map<String, Integer> getAllArchives(int uid) {
        return mongoTemplate.findOne(new Query(Criteria.where("uid").is(uid)),MyTag.class).getArchives();
    }
}