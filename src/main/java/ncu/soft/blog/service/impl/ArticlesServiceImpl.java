package ncu.soft.blog.service.impl;

import cn.hutool.core.date.DateUtil;
import ncu.soft.blog.entity.Article;
import ncu.soft.blog.entity.MyTag;
import ncu.soft.blog.service.ArticlesService;
import ncu.soft.blog.utils.RemoveHtmlTags;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
public class ArticlesServiceImpl implements ArticlesService {

    @Resource(name = "mongoTemplate")
    MongoTemplate mongoTemplate;

    @Resource
    UsersInfoServiceImpl usersInfoService;

    @Resource
    CommentServiceImpl commentService;

    @Resource
    DetailServiceImpl detailService;

    @Resource
    TagServiceImpl tagService;

    @Resource
    SetOperations<String,String> setOperations;

    @Override
    public Article save(Article article,String contentHtml,int aid) {

        Date date = new Date();
        MyTag myTag = tagService.findByUid(article.getUid());
        //存入标签（分类）
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

        article.setSummary(summary);
        article.setCoverPath(coverPath);
        article.setArticleTime(date);
        article.setReads(0);
        article.setLikes(0);
        article.setComments(0);
        if (aid == 0){
            return mongoTemplate.insert(article);
        }else if (aid > 0){
            // 删除文章原来的标签（前面已经添加了新的）
            removeMyTag(aid);

            Update update = Update.update("category",article.getCategory()).set("tags",article.getTags())
                    .set("cPath",coverPath).set("title",article.getTitle()).set("summary",summary).set("pwd",article.getPwd());
            FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
            return mongoTemplate.findAndModify(new Query(Criteria.where("id").is(aid)),update,options,Article.class);
        }
        return null;
    }

    @Override
    public void delete(int aid,String uid) {
        // 更新文章数
        usersInfoService.updateArticles(-1,String.valueOf(uid));
        // 更新标签、分类、归档
        removeMyTag(aid);
        // 删除文章对应的评论
        commentService.delete(aid);
        //删除文章详情
        detailService.delete(aid);
        // 删除文章数据
        mongoTemplate.remove(new Query(Criteria.where("id").is(aid)),Article.class);
    }

    @Override
    public PageImpl<Article> getArticlesByPage(int pageIndex, int pageSize) {
        // 设置分页信息
        Pageable pageable = PageRequest.of(pageIndex,pageSize);
        Query query = new Query().with(pageable);
        // 计算总数
        long count = mongoTemplate.count(query,Article.class);
        // 聚合操作，多表查询
        TypedAggregation<Article> aggregation = Aggregation.newAggregation(Article.class,
                // 左连接用户信息表
                Aggregation.lookup("users_info", "uid", "uid", "usersInfo"),
                // 按时间降序排序
                Aggregation.sort(Sort.Direction.DESC, "articleTime"),
                Aggregation.skip((long)pageIndex),
                Aggregation.limit(pageSize));
        // 获取聚合结果
        List<Article> articles = mongoTemplate.aggregate(aggregation, Article.class).getMappedResults();
        // 获取分页信息
        return (PageImpl<Article>) PageableExecutionUtils.getPage(articles,pageable,() -> count);
    }

    @Override
    public PageImpl<Article> getArticlesByUidByPage(int pageIndex, int pageSize, String uid) {
        Query query = new Query(Criteria.where("uid").is(uid)).with(new Sort(Sort.Direction.DESC,"aTime"));
        return getArticles(pageIndex,pageSize,query);
    }

    @Override
    public Article getArticle(int aid) {
        return mongoTemplate.findOne(new Query(Criteria.where("id").is(aid)),Article.class);
    }

    @Override
    public Article getArticle(int aid, String ip) {
        TypedAggregation<Article> aggregation = Aggregation.newAggregation(Article.class,
                // 左连接用户表
                Aggregation.lookup("users_info", "uid", "uid", "usersInfo"),
                // 左连接文章详情表
                Aggregation.lookup("article_detail", "_id", "aid", "articleDetail"),
                // 过滤
                Aggregation.match(Criteria.where("id").is(aid))
        );
        // 获取结果集的第一个值
        Article article = mongoTemplate.aggregate(aggregation, Article.class).getMappedResults().get(0);
        if (article != null) {
            // 如果文章被阅读过
            if (setOperations.getOperations().hasKey(String.valueOf(aid))) {
                // 如果该键不存在该元素
                if (!setOperations.isMember(String.valueOf(aid), ip)) {
                    // 将阅读这篇文章的uid放进集合中
                    setOperations.add(String.valueOf(aid), ip);
                    // 更新文章阅读量，+1
                    updateReads(aid, 1);
                    article.setReads(article.getReads() + 1);
                    // 更新个人访问，+1
                    usersInfoService.updateReads(1, String.valueOf(article.getUid()));
                }
            } else {
                // 将阅读这篇文章的uid放进集合中
                setOperations.add(String.valueOf(aid), ip);
                // 将key持久化
                setOperations.getOperations().persist(String.valueOf(aid));
                // 更新文章阅读量，+1
                updateReads(aid, 1);
                article.setReads(article.getReads() + 1);
                // 更新个人访问，+1
                usersInfoService.updateReads(1, String.valueOf(article.getUid()));
            }
            return article;
        }else {
            return null;
        }
    }

    @Override
    public Article updateReads(int aid,int num) {
        return updateNumber(aid,"reads",num);
    }

    @Override
    public Article updateComments(int aid,int num) {
        return updateNumber(aid,"comments",num);
    }

    @Override
    public Article updateLikes(int aid,int num) {
        return updateNumber(aid,"likes",num);
    }

    @Override
    public PageImpl<Article> getArticleByTag(int index, int size, String uid, String tag) {
        // 通过elemMatch查询子文档
        Query query = new Query(Criteria.where("uid").is(uid).and("tags").elemMatch(Criteria.where("tag").is(tag)));
        query.with(new Sort(Sort.Direction.DESC,"aTime"));
        return getArticles(index,size,query);
    }

    @Override
    public PageImpl<Article> getArticleByCategory(int index, int size, String uid, String category) {
        Query query = new Query(Criteria.where("uid").is(uid).and("category").is(category)).with(new Sort(Sort.Direction.DESC,"aTime"));
        return getArticles(index,size,query);
    }

    @Override
    public PageImpl<Article> getArticleByArchive(int index, int size, String uid, String archive) {
        // 解析归档，获取年月
        String[] date = archive.split("-");
        String year = date[0];
        int month = Integer.parseInt(date[1]);
        // 获取下一个月
        int endMonth = month + 1;
        // 重组年月日，获取某个月的开始时间和截止时间
        // 开始时间是这个月的八点
        Date startDate = DateUtil.parse(year + "-" + month + "-01" + " 08","yyyy-MM-dd HH");
        // 因为iso时间要晚8个小时，所以结束时间是下个月月初的八点
        Date endDate = DateUtil.parse(year + "-" + endMonth + "-01" + " 08","yyyy-MM-dd HH");
        Query query = new Query(Criteria.where("uid").is(uid).and("aTime").gte(startDate).lte(endDate));
        query.with(new Sort(Sort.Direction.DESC,"aTime"));
        return getArticles(index,size,query);
    }

    @Override
    public PageImpl<Article> getArticleByRegex(int index, int size, String regex) {
        // 设置分页信息
        Pageable pageable = PageRequest.of(index,size);
        Query query = new Query().with(pageable);
        // 计算总数
        long count = mongoTemplate.count(query,Article.class);
        // 聚合操作，多表查询
        TypedAggregation<Article> aggregation = Aggregation.newAggregation(Article.class,
                // 左连接用户信息表
                Aggregation.lookup("users_info", "uid", "uid", "usersInfo"),
                // 按时间降序排序
                Aggregation.sort(Sort.Direction.DESC, "articleTime"),
                // 按正则表达式过滤
                Aggregation.match(Criteria.where("title").regex(".*?" + regex + ".*")),
                Aggregation.skip((long)index),
                Aggregation.limit(size));
        // 获取聚合结果
        List<Article> articles = mongoTemplate.aggregate(aggregation, Article.class).getMappedResults();
        // 获取分页信息
        return (PageImpl<Article>) PageableExecutionUtils.getPage(articles,pageable,() -> count);
    }

    @Override
    public PageImpl<Article> getArticleByRegexByUid(int index, int size, String regex, String uid) {
        Query query = new Query(Criteria.where("uid").is(uid).and("title").regex(".*?" + regex + ".*"));
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
     * 更新阅读、评论、喜欢数
     * @param aid 文章id
     * @param key 阅读/评论/喜欢
     * @return Article
     */
    private Article updateNumber(int aid,String key,int num){
        Query query = new Query(Criteria.where("id").is(aid));
        Update update = new Update().inc(key,num);
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
    private void addTag(MyTag myTag,List<Map<String ,String > > tags1,String category,String uid, Date date){
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
                updateAddMap(tags2,tagKey);
            }
            myTag.setTags(tags2);

            // 存入分类
            Map<String ,Integer> categorys = myTag.getCategorys();
            updateAddMap(categorys,category);
            myTag.setCategorys(categorys);

            // 存入归档
            Map<String ,Integer> archives = myTag.getArchives();
            updateAddMap(archives,archive);
            myTag.setArchives(archives);
            tagService.update(myTag);
        }
    }

    /**
     * 移除标签、分类、归档
     * @param myTag MyTag
     * @param tags1 标签
     * @param category 分类
     * @param archive 归档
     */
    private void removeTag(MyTag myTag,List<Map<String ,String > > tags1,String category,String archive){
        //移除标签
        Map<String ,Integer > tags2 = myTag.getTags();
        for (Map<String ,String > tag : tags1){
            updateRemoveMap(tags2,tag.get("tag"));
        }
        myTag.setTags(tags2);

        // 移除分类
        Map<String ,Integer> categorys = myTag.getCategorys();
        updateRemoveMap(categorys,category);
        myTag.setCategorys(categorys);

        // 移除归档
        Map<String ,Integer> archives = myTag.getArchives();
        updateRemoveMap(archives,archive);
        myTag.setArchives(archives);
        tagService.update(myTag);
    }

    /**
     * 根据文章id删除文章对应的标签、分类和归档
     * @param aid 文章id
     */
    private void removeMyTag(int aid){
        Article article = mongoTemplate.findOne(new Query(Criteria.where("id").is(aid)),Article.class);
        if (article != null){
            MyTag myTag = tagService.findByUid(article.getUid());
            String archive = DateUtil.format(article.getArticleTime(),"yyyy-MM");
            removeTag(myTag,article.getTags(),article.getCategory(),archive);
        }
    }

    /**
     * 更新map集合,删去值
     * @param maps Map<String ,Integer>
     * @param key 键
     */
    private void updateRemoveMap(Map<String ,Integer> maps,String key){
        if (maps.containsKey(key)){
            // 如果值大于1就减1
            if (maps.get(key) > 1){
                maps.put(key, maps.get(key) - 1);
            }else { // 否则移除这个键
                maps.remove(key);
            }
        }
    }

    /**
     * 更新map集合,增加值
     * @param maps Map<String ,Integer>
     * @param key 键
     */
    private void updateAddMap(Map<String ,Integer> maps,String key){
        // 如果键存在则+1
        if (maps.containsKey(key)){
            maps.put(key, maps.get(key) + 1);
        }else { // 如果不存在就put进去
            maps.put(key,1);
        }
    }

    private final static String UNKNOWN = "unknown";

    /**
     * 获取访问用户的真实ip
     * @param request HttpServletRequest
     * @return 获取到的ip地址
     */
    public String getIp(HttpServletRequest request){
        String ip = request.getHeader("x-forwarded-for");
        // 获取用户的真实ip地址（避免用户使用了多级代理而取不到真实ip）
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 遍历文章数据，设置文章对应的作者
     * @param articles 文章分页数据
     * @return 设置作者后的数据
     */
    private PageImpl<Article> setUserNickName(PageImpl<Article> articles){
        Integer total = articles.getTotalPages();
        for (Article article : articles.getContent()){

        }
        return articles;
    }
}