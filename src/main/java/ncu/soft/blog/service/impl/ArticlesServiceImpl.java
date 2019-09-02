package ncu.soft.blog.service.impl;

import cn.hutool.core.date.DateUtil;
import ncu.soft.blog.entity.Article;
import ncu.soft.blog.entity.MyTag;
import ncu.soft.blog.service.ArticlesService;
import ncu.soft.blog.service.TagService;
import ncu.soft.blog.service.UsersInfoService;
import ncu.soft.blog.utils.RemoveHtmlTags;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.cache.annotation.CacheConfig;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public Article save(Article article,String contentHtml) {

        Date date = new Date();
        MyTag myTag = tagService.findByUid(article.getUid());
        //存入或更新标签（分类）
        addTag(myTag,article.getTags(),article.getCategory(),article.getUid(),date);

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
        article.setArticleTime(date);
        article.setReads(0);
        article.setLikes(0);
        article.setComments(0);
        return mongoTemplate.insert(article);
    }

    @Override
    public PageImpl<Article> getArticlesByPage(int pageIndex, int pageSize) {
        Query query = new Query().with(new Sort(Sort.Direction.DESC,"aTime"));
        return getArticles(pageIndex,pageSize,query);
    }

    @Override
    public PageImpl<Article> getArticlesByUidByPage(int pageIndex, int pageSize, int uid) {
        Query query = new Query(Criteria.where("uid").is(uid)).with(new Sort(Sort.Direction.DESC,"aTime"));
        return getArticles(pageIndex,pageSize,query);
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

    @Override
    public PageImpl<Article> getArticleByTag(int index, int size, int uid, String tag) {
        // 通过elemMatch查询子文档
        Query query = new Query(Criteria.where("uid").is(uid).and("tags").elemMatch(Criteria.where("tag").is(tag)));
        query.with(new Sort(Sort.Direction.DESC,"aTime"));
        return getArticles(index,size,query);
    }

    @Override
    public PageImpl<Article> getArticleByCategory(int index, int size, int uid, String category) {
        Query query = new Query(Criteria.where("uid").is(uid).and("category").is(category)).with(new Sort(Sort.Direction.DESC,"aTime"));
        return getArticles(index,size,query);
    }

    @Override
    public PageImpl<Article> getArticleByArchive(int index, int size, int uid, String archive) {
        // 解析归档，获取年月
        String[] date = archive.split("-");
        String year = date[0];
        int month = Integer.parseInt(date[1]);
        // 获取下一个月
        int endMonth = month + 1;
        // 重组年月日，获取某个月的开始时间和截止时间
        Date startDate = DateUtil.parse(year + "-" + month + "-01","yyyy-MM-dd");
        // 因为iso时间要晚8个小时，所以结束时间是下个月月初的八点
        Date endDate = DateUtil.parse(year + "-" + endMonth + "-01" + " 08","yyyy-MM-dd HH");
        Query query = new Query(Criteria.where("uid").is(uid).and("aTime").gte(startDate).lte(endDate));
        query.with(new Sort(Sort.Direction.DESC,"aTime"));
        return getArticles(index,size,query);
    }

    /**
     * 分页方法
     * @param index 当前页
     * @param size 每页大小
     * @param query Query
     * @return PageImpl<Article>
     */
    private PageImpl<Article> getArticles(int index, int size,Query query){
        Pageable pageable = PageRequest.of(index,size);
        query.with(pageable);
        long count = mongoTemplate.count(query,Article.class);
        List<Article> articles = mongoTemplate.find(query,Article.class);
        return (PageImpl<Article>) PageableExecutionUtils.getPage(articles,pageable,() -> count);
    }

    /**
     * 将新加的分类和标签添加到数据库
     * @param myTag MyTag
     * @param tags1 标签
     * @param category 分类
     * @param uid 用户id
     */
    private void addTag(MyTag myTag,List<Map<String ,String > > tags1,String category,int uid, Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        String archive = sdf.format(date);
        //如果数据为空，则存入数据
        if(myTag == null){
            MyTag myTag1 = new MyTag();
            Map<String ,Integer> map = new HashMap<>(5);
            //将标签分别存入，并置初始值为1
            for (Map<String ,String > tag : tags1){
                map.put(tag.get("tag"),1);
            }
            myTag1.setTags(map);

            Map<String ,Integer> categorys = new HashMap<>(2);
            categorys.put(category,1);
            myTag1.setCategorys(categorys);

            Map<String ,Integer> archives = new HashMap<>(2);
            archives.put(archive,1);
            myTag1.setArchives(archives);
            myTag1.setUid(uid);
            tagService.save(myTag1);
        }
        //如果数据不为空，则更新数据
        else {
            Map<String ,Integer> tags2 = myTag.getTags();
            for (Map<String ,String > tag : tags1){
                // 获取key
                String tagKey = tag.get("tag");
                //如果存在键，则更新值
                if (tags2.containsKey(tagKey)){
                    tags2.put(tagKey,tags2.get(tagKey) + 1);
                }
                //如果不存在，则存入键值
                else {
                    tags2.put(tagKey,1);
                }
            }
            myTag.setTags(tags2);

            // 存入分类
            Map<String ,Integer> categorys = myTag.getCategorys();
            if (categorys.containsKey(category)){
                categorys.put(category, categorys.get(category) + 1);
            }else {
                categorys.put(category,1);
            }
            myTag.setCategorys(categorys);

            // 存入归档
            Map<String ,Integer> archives = myTag.getArchives();
            if (archives.containsKey(archive)){
                archives.put(archive, archives.get(archive) + 1);
            }else {
                archives.put(archive,1);
            }
            myTag.setArchives(archives);
            tagService.update(myTag);
        }
    }
}