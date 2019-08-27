package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.ArticleDetail;
import ncu.soft.blog.service.DetailService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/26 8:10
 */
@Service
@CacheConfig(cacheNames = "detailService")
public class DetailServiceImpl implements DetailService {

    @Resource(name = "mongoTemplate")
    MongoTemplate template;

    @Override
    @CachePut
    public ArticleDetail save(ArticleDetail articleDetail) {
        return template.insert(articleDetail);
    }
}