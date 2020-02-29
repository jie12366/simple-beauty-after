package ncu.soft.blog.controller.article;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.Article;
import ncu.soft.blog.entity.MyTag;
import ncu.soft.blog.service.TagService;
import ncu.soft.blog.service.impl.ArticlesServiceImpl;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.ResultCode;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/26 13:23
 */
@RestController
public class ShowController {

    @Resource
    ArticlesServiceImpl articlesService;

    @Resource
    TagService tagService;

    @ApiOperation("首页分页展示文章列表")
    @GetMapping("/articles/{index}/{size}")
    public JsonResult getArticles(@PathVariable("index")int index,@PathVariable("size") int size){
        return JsonResult.success(articlesService.getArticlesByPage(index, size));
    }

    @ApiOperation("我的主页分页展示我的文章")
    @GetMapping("/articles/{uid}/{index}/{size}")
    public JsonResult getArticlesByUid(@PathVariable("uid")String uid, @PathVariable("index")int index,@PathVariable("size") int size){
        PageImpl<Article> articles = articlesService.getArticlesByUidByPage(index,size,uid);
        if (!articles.isEmpty()){
            return JsonResult.success(articles);
        }else {
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }
    }

    @ApiOperation("获取文章数据")
    @GetMapping("/articles/{aid}")
    public JsonResult getArticleByAid(@PathVariable("aid")int aid, HttpServletRequest request) {
        // 获取访问者真实ip
        String ip = articlesService.getIp(request);
        Article article = articlesService.getArticle(aid, ip);
        if (article != null){
            return JsonResult.success(article);
        }else {
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }
    }

    @ApiOperation("获取我的所有标签")
    @GetMapping("/tags/{uid}")
    public JsonResult getAllTags(@PathVariable("uid") String uid){
        MyTag myTag = tagService.getAllTags(uid);
        if (myTag == null){
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            return JsonResult.success(myTag.getTags());
        }
    }

    @ApiOperation("根据我的标签分页获取文章")
    @GetMapping("/articles/{uid}/tag/{tag}/{index}/{size}")
    public JsonResult getArticlesByTag(@PathVariable("uid")String uid,@PathVariable("tag")String  tag,
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
    public JsonResult getArticlesByCategory(@PathVariable("uid")String uid,@PathVariable("category")String  category,
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
    public JsonResult getAllArchives(@PathVariable("uid") String uid){
        Map<String , Integer> archives = tagService.getAllArchives(uid);
        if (archives == null){
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            return JsonResult.success(archives);
        }
    }

    @ApiOperation("根据我的归档分页获取文章")
    @GetMapping("/articles/{uid}/archive/{archive}/{index}/{size}")
    public JsonResult getArticlesByDate(@PathVariable("uid")String uid,@PathVariable("archive")String  archive,
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
    public JsonResult getTitleByRegex(@PathVariable("regex")String regex,
                                        @PathVariable("index")int index,@PathVariable("size")int size){
        PageImpl<Article> articles = articlesService.getArticleByRegex(index, size, regex);
        if (articles.isEmpty()){
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            List<JSONObject> result = new ArrayList<>();
            for (Article article : articles.getContent()){
                JSONObject json = new JSONObject();
                // 取出标题返回给前端
                json.put("value",article.getTitle());
                result.add(json);
            }
            return JsonResult.success(result);
        }
    }

    @ApiOperation("模糊匹配文章")
    @PostMapping("/article/regex")
    public JsonResult getArticleByRegex(@RequestParam("regex")String regex,
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
    public JsonResult getArticleByRegex(@PathVariable("regex")String regex,@PathVariable("uid")String uid,
                                        @PathVariable("index")int index,@PathVariable("size")int size){
        PageImpl<Article> articles = articlesService.getArticleByRegexByUid(index, size, regex, uid);
        if (articles.isEmpty()){
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            return JsonResult.success(articles);
        }
    }
}