package ncu.soft.blog.controller.article;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.Article;
import ncu.soft.blog.entity.ArticleDetail;
import ncu.soft.blog.entity.MyTag;
import ncu.soft.blog.service.ArticlesService;
import ncu.soft.blog.service.DetailService;
import ncu.soft.blog.service.TagService;
import ncu.soft.blog.service.UsersInfoService;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.ResultCode;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/26 13:23
 */
@RestController
public class ShowController {

    @Resource
    ArticlesService articlesService;

    @Resource
    DetailService detailService;

    @Resource
    TagService tagService;

    @Resource
    UsersInfoService usersInfoService;

    @Resource
    SetOperations<String,String> setOperations;

    private ThreadPoolExecutor executor = new ThreadPoolExecutor(10,10,
            0L,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<>(255),
            new ThreadFactoryBuilder().setNameFormat("res-%d").build());

    @ApiOperation("首页分页展示文章列表")
    @GetMapping("/articles/{index}/{size}")
    public JsonResult getArticles(@Valid @PathVariable("index")int index,@PathVariable("size") int size){
        return JsonResult.success(articlesService.getArticlesByPage(index, size));
    }

    @ApiOperation("我的主页分页展示我的文章")
    @GetMapping("/articles/{uid}/{index}/{size}")
    public JsonResult getArticlesByUid(@Valid @PathVariable("uid")String uid, @PathVariable("index")int index,@PathVariable("size") int size){
        PageImpl<Article> articles = articlesService.getArticlesByUidByPage(index,size,uid);
        if (!articles.isEmpty()){
            return JsonResult.success(articles);
        }else {
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }
    }

    @ApiOperation("获取文章内容")
    @GetMapping("/articleDetail/{aid}")
    public JsonResult getArticlesByAid(@Valid @PathVariable("aid")int aid){
        ArticleDetail articleDetail = detailService.getArticleByAid(aid);
        if (articleDetail != null){
            return JsonResult.success(articleDetail);
        }else {
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }
    }

    @ApiOperation("获取文章数据")
    @GetMapping("/article/{aid}")
    public JsonResult getArticleByAid(@Valid @PathVariable("aid")int aid, HttpServletRequest request) throws UnknownHostException {
        // 获取访问者真实ip
        String ip = getIp(request);

        Article article = articlesService.getArticle(aid);
        if (article != null){
            // 如果键存在
            if (setOperations.getOperations().hasKey(String.valueOf(aid))){
                // 如果该键不存在该元素
                if (!setOperations.isMember(String.valueOf(aid),ip)){
                    // 将点赞这篇文章的uid放进集合中
                    setOperations.add(String.valueOf(aid),ip);
                    // 更新文章阅读量，+1
                    articlesService.updateReads(aid,1);
                    // 更新个人访问，+1
                    usersInfoService.updateReads(1,String.valueOf(article.getUid()));
                }
            }else {
                // 将点赞这篇文章的uid放进集合中
                setOperations.add(String.valueOf(aid),ip);
                // 将key持久化
                setOperations.getOperations().persist(String.valueOf(aid));
                // 更新文章阅读量，+1
                articlesService.updateReads(aid,1);
                // 更新个人访问，+1
                usersInfoService.updateReads(1,String.valueOf(article.getUid()));
            }
            return JsonResult.success(article);
        }else {
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }
    }

    @ApiOperation("获取我的所有标签")
    @GetMapping("/tags/{uid}")
    public JsonResult getAllTags(@Valid @PathVariable("uid") String uid){
        MyTag myTag = tagService.getAllTags(uid);
        if (myTag == null){
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            return JsonResult.success(myTag.getTags());
        }
    }

    @ApiOperation("根据我的标签分页获取文章")
    @GetMapping("/articles/{uid}/tag/{tag}/{index}/{size}")
    public JsonResult getArticlesByTag(@Valid @PathVariable("uid")String uid,@PathVariable("tag")String  tag,
                                       @PathVariable("index")int index,@PathVariable("size")int size){
        PageImpl<Article> articles = articlesService.getArticleByTag(index,size,uid,tag);
        if (articles.isEmpty()){
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            return JsonResult.success(articles);
        }
    }

    @ApiOperation("根据我的分类分页获取文章")
    @GetMapping("/articles/{uid}/category/{category}/{index}/{size}")
    public JsonResult getArticlesByCategory(@Valid @PathVariable("uid")String uid,@PathVariable("category")String  category,
                                       @PathVariable("index")int index,@PathVariable("size")int size){
        PageImpl<Article> articles = articlesService.getArticleByCategory(index,size,uid,category);
        if (articles.isEmpty()){
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            return JsonResult.success(articles);
        }
    }

    @ApiOperation("获取我的所有归档")
    @GetMapping("/archives/{uid}")
    public JsonResult getAllArchives(@Valid @PathVariable("uid") String uid){
        Map<String , Integer> archives = tagService.getAllArchives(uid);
        if (archives == null){
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            return JsonResult.success(archives);
        }
    }

    @ApiOperation("根据我的归档分页获取文章")
    @GetMapping("/articles/{uid}/archive/{archive}/{index}/{size}")
    public JsonResult getArticlesByDate(@Valid @PathVariable("uid")String uid,@PathVariable("archive")String  archive,
                                            @PathVariable("index")int index,@PathVariable("size")int size){
        PageImpl<Article> articles = articlesService.getArticleByArchive(index,size,uid,archive);
        if (articles.isEmpty()){
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            return JsonResult.success(articles);
        }
    }

    @ApiOperation("模糊匹配文章标题")
    @GetMapping("/title/regex/{regex}/{index}/{size}")
    public JsonResult getTitleByRegex(@Valid @PathVariable("regex")String regex,
                                        @PathVariable("index")int index,@PathVariable("size")int size){
        PageImpl<Article> articles = articlesService.getArticleByRegex(index, size, regex);
        if (articles.isEmpty()){
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            List<JSONObject> result = new ArrayList<>();
            for (Article article : articles.getContent()){
                // 使用多线程加快处理速度
                executor.execute(() ->{
                    JSONObject json = new JSONObject();
                    // 取出标题返回给前端
                    json.put("value",article.getTitle());
                    result.add(json);
                });
            }
            // 等待多线程所有任务运行结束
            while (result.size() == articles.getContent().size()){
                if (executor.isTerminated()){
                    break;
                }
            }
            return JsonResult.success(result);
        }
    }

    @ApiOperation("模糊匹配文章")
    @PostMapping("/article/regex")
    public JsonResult getArticleByRegex(@Valid @RequestParam("regex")String regex,
                                        @RequestParam("index")int index,@RequestParam("size")int size){
        PageImpl<Article> articles = articlesService.getArticleByRegex(index, size, regex);
        if (articles.isEmpty()){
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            return JsonResult.success(articles);
        }
    }

    @ApiOperation("站内文章模糊搜索")
    @GetMapping("/article/{regex}/{uid}/{index}/{size}")
    public JsonResult getArticleByRegex(@Valid @PathVariable("regex")String regex,@PathVariable("uid")String uid,
                                        @PathVariable("index")int index,@PathVariable("size")int size){
        PageImpl<Article> articles = articlesService.getArticleByRegexByUid(index, size, regex, uid);
        if (articles.isEmpty()){
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            return JsonResult.success(articles);
        }
    }

    private final static String UNKNOWN = "unknown";

    /**
     * 获取访问用户的真实ip
     * @param request HttpServletRequest
     * @return 获取到的ip地址
     */
    private String getIp(HttpServletRequest request){
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
}