package ncu.soft.blog.controller.article;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.Article;
import ncu.soft.blog.entity.ArticleDetail;
import ncu.soft.blog.entity.MyTag;
import ncu.soft.blog.selfannotation.LoginToken;
import ncu.soft.blog.service.*;
import ncu.soft.blog.service.impl.ArticlesServiceImpl;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.ResultCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    ArticlesServiceImpl articlesService;

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
    public JsonResult saveArticles(@Valid @RequestParam("uid") String uid, @RequestParam("title") String title,
                                   @RequestParam("category") String category, @RequestParam("tags") String tags,@RequestParam("pwd")String pwd,
                                   @RequestParam("contentMd") String contentMd,@RequestParam("contentHtml")String contentHtml,@RequestParam("aid") int aid){
        // 解析json字符串为list集合
        List<String> tags1 = JSON.parseArray(tags,String.class);
        List<Map<String ,String >> tagList = new ArrayList<>();
        for (String tag: tags1){
            Map<String ,String > tagMap = new HashMap<>(3);
            tagMap.put("tag",tag);
            tagList.add(tagMap);
        }
        Article article = new Article(uid,category,tagList,title,pwd);
        // 保存文章信息并返回
        Article article1 = articlesService.save(article,contentHtml,aid);
        if(aid == 0) {
            //文章数加1
            usersInfoService.updateArticles(1,uid);
        }
        if (article1 == null){
            // 保存数据错误
            return JsonResult.failure(ResultCode.SAVE_ERROR);
        }
        ArticleDetail articleDetail = new ArticleDetail(article1.getId(), contentMd, contentHtml);

        ArticleDetail articleDetail1;
        if (aid > 0){
            articleDetail1 = detailService.update(articleDetail);
        }else {
            articleDetail1 = detailService.save(articleDetail);
        }
        //保存文章详情内容
        if (articleDetail1 != null){
            return JsonResult.success();
        }else {
            return JsonResult.failure(ResultCode.SAVE_ERROR);
        }
    }

    @ApiOperation("获取用户所有个人分类")
    @GetMapping("/categorys/{uid}")
    public JsonResult getCategorys(@Valid @PathVariable("uid") String uid){
        MyTag myTag = tagService.findByUid(uid);
        if (myTag != null){
            return JsonResult.success(myTag.getCategorys());
        }else {
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }
    }
}