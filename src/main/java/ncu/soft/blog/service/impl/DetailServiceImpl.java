package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.ArticleDetail;
import ncu.soft.blog.service.DetailService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    @CachePut(key = "#articleDetail.aid")
    public ArticleDetail save(ArticleDetail articleDetail) {
        //通过jsoup获取h标签
        Document document = Jsoup.parse(articleDetail.getContentHtml());
        Elements elements = document.select("h2, h3");
        // 将所有的标签放在一个list集合中
        List<Map<String ,String >> directory = new ArrayList<>();
        for(Element element : elements){
            if (element.is("h2")) {
                Map<String ,String> h = new HashMap<>(1);
                h.put("h2",element.text());
                h.put("id",element.select("a").attr("id"));
                directory.add(h);
            } else if (element.is("h3")){
                Map<String ,String> h = new HashMap<>(1);
                h.put("h3",element.text());
                h.put("id",element.select("a").attr("id"));
                directory.add(h);
            }
        }
        articleDetail.setDirectory(directory);
        return template.insert(articleDetail);
    }

    @Override
    @Cacheable(key = "#aid")
    public ArticleDetail getArticleByAid(int aid) {
        return template.findOne(new Query(Criteria.where("aid").is(aid)),ArticleDetail.class);
    }
}