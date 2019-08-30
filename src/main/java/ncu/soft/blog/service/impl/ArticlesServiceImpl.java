package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.Article;
import ncu.soft.blog.entity.ArticleDetail;
import ncu.soft.blog.service.ArticlesService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/25 19:47
 */
@Service
@CacheConfig(cacheNames = "articlesService")
public class ArticlesServiceImpl implements ArticlesService {

    @Resource(name = "mongoTemplate")
    MongoTemplate mongoTemplate;

    @Override
    public Article save(Article article) {
        return mongoTemplate.insert(article);
    }

    @Override
    public PageImpl<Article> getArticlesByPage(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex,pageSize);
        Query query = new Query().with(new Sort(Sort.Direction.DESC,"aTime"));
        query.with(pageable);
        List<Article> articles = mongoTemplate.find(query,Article.class);
        return (PageImpl<Article>) PageableExecutionUtils.getPage(articles,pageable,() -> 0);
    }

    @Override
    public PageImpl<Article> getArticlesByUidByPage(int pageIndex, int pageSize, int uid) {
        Pageable pageable = PageRequest.of(pageIndex,pageSize);
        Query query = new Query(Criteria.where("uid").is(uid)).with(new Sort(Sort.Direction.DESC,"aTime"));
        query.with(pageable);
        List<Article> articles = mongoTemplate.find(query,Article.class);
        return (PageImpl<Article>) PageableExecutionUtils.getPage(articles,pageable,() -> 0);
    }

    @Override
    @Cacheable(key = "'detail' + #aid")
    public ArticleDetail getArticleByAid(int aid) {
        return mongoTemplate.findOne(new Query(Criteria.where("aid").is(aid)),ArticleDetail.class);
    }

    @Override
    @Cacheable(key = "'article' + #aid")
    public Article getArticle(int aid) {
        return mongoTemplate.findOne(new Query(Criteria.where("id").is(aid)),Article.class);
    }
}