package ncu.soft.blog.controller.article;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.Article;
import ncu.soft.blog.entity.ArticleDetail;
import ncu.soft.blog.entity.MyTag;
import ncu.soft.blog.selfAnnotation.LoginToken;
import ncu.soft.blog.service.*;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.ResultCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
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
    UsersInfoService usersInfoService;

    @Resource
    TagService tagService;

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
                                   @RequestParam("category") String category, @RequestParam("tags") String tags,@RequestParam("pwd")String pwd,
                                   @RequestParam("contentMd") String contentMd,@RequestParam("contentHtml")String contentHtml){
        // 解析json字符串为list集合
        List<String> tags1 = JSON.parseArray(tags,String.class);
        Article article = new Article(uid,category,tags1,title,pwd);
        // 保存文章信息并返回
        Article article1 = articlesService.save(article,contentHtml);

        //文章数加1
        usersInfoService.updateArticles(1,uid);

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

    @ApiOperation("获取用户所有个人分类")
    @GetMapping("/categorys/{uid}")
    @LoginToken
    public JsonResult getCategorys(@Valid @PathVariable("uid") int uid){
        MyTag myTag = tagService.findByUid(uid);
        if (myTag != null){
            return JsonResult.success(myTag.getCategorys());
        }else {
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }
    }
}