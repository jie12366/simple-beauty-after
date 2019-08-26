package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.Article;
import ncu.soft.blog.service.ArticlesService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/25 19:47
 */
@Service
public class ArticlesServiceImpl implements ArticlesService {

    @Resource(name = "mongoTemplate")
    MongoTemplate mongoTemplate;

    @Override
    public Article save(Article article) {
        return mongoTemplate.insert(article);
    }
}