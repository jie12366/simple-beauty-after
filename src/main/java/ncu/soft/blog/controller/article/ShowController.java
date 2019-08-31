package ncu.soft.blog.controller.article;

import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.Article;
import ncu.soft.blog.entity.ArticleDetail;
import ncu.soft.blog.service.ArticlesService;
import ncu.soft.blog.service.DetailService;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.ResultCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

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

    @ApiOperation("首页分页展示文章列表")
    @GetMapping("/articles/{index}/{size}")
    public JsonResult getArticles(@Valid @PathVariable("index")int index,@PathVariable("size") int size){
        return JsonResult.success(articlesService.getArticlesByPage(index, size).getContent());
    }

    @ApiOperation("我的主页分页展示我的文章")
    @GetMapping("/articles/{uid}/{index}/{size}")
    public JsonResult getArticlesByUid(@Valid @PathVariable("uid")int uid, @PathVariable("index")int index,@PathVariable("size") int size){
        List<Article> articles = articlesService.getArticlesByUidByPage(index,size,uid).getContent();
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
    public JsonResult getArticleByAid(@Valid @PathVariable("aid")int aid){
        // 更新阅读量，+1
        articlesService.updateReads(aid);
        Article article = articlesService.getArticle(aid);
        if (article != null){
            return JsonResult.success(article);
        }else {
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }
    }
}