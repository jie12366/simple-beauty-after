package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.ArticleDetail;
import ncu.soft.blog.service.DetailService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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
public class DetailServiceImpl implements DetailService {

    @Resource(name = "mongoTemplate")
    MongoTemplate template;

    @Override
    public ArticleDetail save(ArticleDetail articleDetail) {
        // 将所有的标签放在一个list集合中
        List<Map<String ,String >> directory = getDirectory(articleDetail.getContentHtml());
        articleDetail.setDirectory(directory);
        return template.insert(articleDetail);
    }

    @Override
    public ArticleDetail update(ArticleDetail articleDetail) {
        Update update = Update.update("cHtml",articleDetail.getContentHtml()).set("cMd",articleDetail.getContentMd())
                .set("directory",getDirectory(articleDetail.getContentHtml()));
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return template.findAndModify(new Query(Criteria.where("aid").is(articleDetail.getAid())),update,options,ArticleDetail.class);
    }

    @Override
    public void delete(int aid) {
        template.remove(new Query(Criteria.where("aid").is(aid)),ArticleDetail.class);
    }

    @Override
    public ArticleDetail getArticleByAid(int aid) {
        return template.findOne(new Query(Criteria.where("aid").is(aid)),ArticleDetail.class);
    }

    /**
     * 解析html，返回文章目录
     * @param html html内容
     * @return List<Map<String ,String >>
     */
    private List<Map<String ,String >> getDirectory(String html){
        //通过jsoup获取h标签
        Document document = Jsoup.parse(html);
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
        return directory;
    }
}