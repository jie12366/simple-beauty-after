package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.MyTag;
import ncu.soft.blog.service.TagService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
    @Cacheable
    public MyTag findByUid(int uid) {
        return mongoTemplate.findOne(new Query(Criteria.where("uid").is(uid)),MyTag.class);
    }

    @Override
    @CachePut
    public MyTag save(MyTag tag) {
        return mongoTemplate.insert(tag);
    }

    @Override
    @CachePut
    public void update(MyTag tag) {
        Query query = new Query(Criteria.where("uid").is(tag.getUid()));
        Update update = Update.update("tags",tag.getTags()).set("categorys",tag.getCategorys());
        mongoTemplate.updateFirst(query,update,MyTag.class);
    }
}