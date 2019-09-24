package ncu.soft.blog.controller.article;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

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
    public JsonResult getArticleByAid(@Valid @PathVariable("aid")int aid) throws UnknownHostException {
        // 获取本机ip
        InetAddress address = InetAddress.getLocalHost();
        String ip = address.getHostAddress();

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
}