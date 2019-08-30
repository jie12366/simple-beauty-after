package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.MyTag;
import ncu.soft.blog.service.TagService;
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
 * @date 2019/8/26 20:17
 */
@Service
@CacheConfig(cacheNames = "tagService")
public class TagServiceImpl implements TagService {

    @Resource(name = "mongoTemplate")
    MongoTemplate mongoTemplate;

    @Override
    @Cacheable(key = "#uid")
    public MyTag findByUid(int uid) {
        return mongoTemplate.findOne(new Query(Criteria.where("uid").is(uid)),MyTag.class);
    }

    @Override
    @CachePut(key = "#tag.uid")
    public MyTag save(MyTag tag) {
        return mongoTemplate.insert(tag);
    }

    @Override
    @CacheEvict(key = "#tag.uid")
        public MyTag update(MyTag tag) {
        Query query = new Query(Criteria.where("uid").is(tag.getUid()));
        Update update = Update.update("tags",tag.getTags()).set("categorys",tag.getCategorys());
        return mongoTemplate.findAndModify(query,update,new FindAndModifyOptions().returnNew(true), MyTag.class);
    }
}