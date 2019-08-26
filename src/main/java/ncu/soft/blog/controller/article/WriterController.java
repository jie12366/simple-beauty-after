package ncu.soft.blog.controller.article;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.Article;
import ncu.soft.blog.entity.ArticleDetail;
import ncu.soft.blog.selfAnnotation.LoginToken;
import ncu.soft.blog.service.ArticlesService;
import ncu.soft.blog.service.DetailService;
import ncu.soft.blog.service.UploadService;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.RemoveHtmlTags;
import ncu.soft.blog.utils.ResultCode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/22 16:46
 */
@RestController
public class WriterController {

    @Resource
    UploadService uploadService;

    @Resource
    ArticlesService articlesService;

    @Resource
    DetailService detailService;

    @ApiOperation("将编辑器中选择的图片上传到服务器")
    @PostMapping("/image")
    @LoginToken
    public JsonResult uploadImage(HttpServletRequest request, @RequestParam("image") MultipartFile image) throws IOException {
        String path = uploadService.getPath(request,image);
        return JsonResult.success(path);
    }

    @ApiOperation("将文章信息存入数据库")
    @PostMapping("/articles")
    @LoginToken
    public JsonResult saveArticles(@Valid @RequestParam("uid") int uid, @RequestParam("title") String title,
                                   @RequestParam("category") String category, @RequestParam("tags") String tags,
                                   @RequestParam("contentMd") String contentMd,@RequestParam("contentHtml")String contentHtml){
        Article article = new Article();
        // 解析json字符串为list集合
        List<String> tags1 = JSON.parseArray(tags,String.class);

        //去除html标签和空格，取前150字为文章摘要
        String summary = RemoveHtmlTags.removeHtmlTags(contentHtml).substring(0,150);

        String coverPath = "";

        // 利用jsoup解析html字符串
        Document doc = Jsoup.parse(contentHtml);
        // 获取所有图片标签
        Elements elements = doc.select("img[src]");
        if (!elements.isEmpty()){
            // 取第一个图片路径，作为封面图
            coverPath = elements.get(0).attr("src");
        }

        article.setUid(uid);
        article.setCategory(category);
        article.setTags(tags1);
        article.setSummary(summary);
        article.setCoverPath(coverPath);
        article.setArticleTime(new Date());
        article.setTitle(title);
        article.setReads(0);
        article.setLikes(0);
        article.setComments(0);
        // 保存文章信息并返回
        Article article1 = articlesService.save(article);

        if (article1 == null){
            // 保存数据错误
            return JsonResult.failure(ResultCode.SAVE_ERROR);
        }

        ArticleDetail articleDetail = new ArticleDetail();
        articleDetail.setAid(article1.getId());
        articleDetail.setContentMd(contentMd);
        articleDetail.setContentHtml(contentHtml);
        //保存文章详情内容
        if (detailService.save(articleDetail) != null){
            return JsonResult.success();
        }else {
            return JsonResult.failure(ResultCode.SAVE_ERROR);
        }
    }
}