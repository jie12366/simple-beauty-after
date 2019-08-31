package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.Article;
import ncu.soft.blog.entity.ArticleDetail;
import ncu.soft.blog.entity.MyTag;
import ncu.soft.blog.service.ArticlesService;
import ncu.soft.blog.service.TagService;
import ncu.soft.blog.service.UsersInfoService;
import ncu.soft.blog.utils.RemoveHtmlTags;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Resource
    UsersInfoService usersInfoService;

    @Resource
    TagService tagService;

    @Override
    @CacheEvict(allEntries = true)
    public Article save(Article article,String contentHtml) {

        MyTag myTag = tagService.findByUid(article.getUid());
        //存入或更新标签（分类）
        addTag(myTag,article.getTags(),article.getCategory(),article.getUid());

        //去除html标签和空格，取前70字为文章摘要
        String summary = "摘要：" + RemoveHtmlTags.removeHtmlTags(contentHtml).substring(0,70) + "...";

        String coverPath = "";
        // 利用jsoup解析html字符串
        Document doc = Jsoup.parse(contentHtml);
        // 获取所有图片标签
        Elements elements = doc.select("img[src]");
        if (!elements.isEmpty()){
            // 取第一个图片路径，作为封面图
            coverPath = elements.get(0).attr("src");
        }

        // 获取用户昵称
        String nickname = usersInfoService.findByUid(article.getUid()).getNickName();
        article.setSummary(summary);
        article.setCoverPath(coverPath);
        article.setUNickname(nickname);
        article.setReads(0);
        article.setLikes(0);
        article.setComments(0);
        return mongoTemplate.insert(article);
    }

    @Override
    @Cacheable(key = "#pageIndex + '_' + #pageSize")
    public PageImpl<Article> getArticlesByPage(int pageIndex, int pageSize) {
        Pageable pageable = PageRequest.of(pageIndex,pageSize);
        Query query = new Query().with(new Sort(Sort.Direction.DESC,"aTime"));
        query.with(pageable);
        List<Article> articles = mongoTemplate.find(query,Article.class);
        return (PageImpl<Article>) PageableExecutionUtils.getPage(articles,pageable,() -> 0);
    }

    @Override
    @Cacheable(key = "#pageIndex + '_' + #pageSize + '_' + #uid")
    public PageImpl<Article> getArticlesByUidByPage(int pageIndex, int pageSize, int uid) {
        Pageable pageable = PageRequest.of(pageIndex,pageSize);
        Query query = new Query(Criteria.where("uid").is(uid)).with(new Sort(Sort.Direction.DESC,"aTime"));
        query.with(pageable);
        List<Article> articles = mongoTemplate.find(query,Article.class);
        return (PageImpl<Article>) PageableExecutionUtils.getPage(articles,pageable,() -> 0);
    }

    @Override
    @Cacheable(key = "'article' + #aid")
    public Article getArticle(int aid) {
        return mongoTemplate.findOne(new Query(Criteria.where("id").is(aid)),Article.class);
    }

    @Override
    @CachePut(key = "'article' + #aid")
    public Article updateReads(int aid) {
        Query query = new Query(Criteria.where("id").is(aid));
        Update update = new Update().inc("reads",1);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return mongoTemplate.findAndModify(query,update,options,Article.class);
    }

    /**
     * 将新加的分类和标签添加到数据库
     * @param myTag MyTag
     * @param tags1 标签
     * @param category 分类
     * @param uid 用户id
     */
    private void addTag(MyTag myTag,List<String > tags1,String category,int uid){
        //如果数据为空，则存入数据
        if(myTag == null){
            MyTag myTag1 = new MyTag();
            Map<String ,Integer> map = new HashMap<>(30);
            //将标签分别存入，并置初始值为1
            for (String tag : tags1){
                map.put(tag,1);
            }
            myTag1.setTags(map);

            Map<String ,Integer> categorys = new HashMap<>(20);
            categorys.put(category,1);
            myTag1.setCategorys(categorys);
            myTag1.setUid(uid);
            tagService.save(myTag1);
        }
        //如果数据不为空，则更新数据
        else {
            Map<String ,Integer> tags2 = myTag.getTags();
            for (String tag : tags1){
                //如果存在键，则更新值
                if (tags2.containsKey(tag)){
                    tags2.put(tag,tags2.get(tag) + 1);
                }
                //如果不存在，则存入键值
                else {
                    tags2.put(tag,1);
                }
            }
            myTag.setTags(tags2);

            Map<String ,Integer> categorys = myTag.getCategorys();
            if (categorys.containsKey(category)){
                categorys.put(category, categorys.get(category) + 1);
            }else {
                categorys.put(category,1);
            }
            myTag.setCategorys(categorys);
            tagService.update(myTag);
        }
    }
}