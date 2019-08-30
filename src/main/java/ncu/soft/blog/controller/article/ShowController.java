package ncu.soft.blog.controller.article;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.Article;
import ncu.soft.blog.entity.ArticleDetail;
import ncu.soft.blog.service.ArticlesService;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.ResultCode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
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
    ArticlesService articlesService;

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
        ArticleDetail articleDetail = articlesService.getArticleByAid(aid);
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
        //将数据封装在map集合中返回
        Map<String ,Object> results = new HashMap<>(3);
        results.put("directory",directory);
        results.put("detail",articleDetail);
        if (articleDetail != null){
            return JsonResult.success(results);
        }else {
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }
    }

    @ApiOperation("获取文章数据")
    @GetMapping("/article/{aid}")
    public JsonResult getArticleByAid(@Valid @PathVariable("aid")int aid){
        Article article = articlesService.getArticle(aid);
        if (article != null){
            return JsonResult.success(article);
        }else {
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }
    }
}