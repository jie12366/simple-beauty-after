package ncu.soft.blog.controller.article;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.Article;
import ncu.soft.blog.entity.ArticleDetail;
import ncu.soft.blog.entity.MyTag;
import ncu.soft.blog.selfAnnotation.LoginToken;
import ncu.soft.blog.service.*;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.RemoveHtmlTags;
import ncu.soft.blog.utils.ResultCode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

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

        MyTag myTag = tagService.findByUid(uid);
        //存入或更新标签（分类）
        addTag(myTag,tags1,category,uid);

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
        String nickname = usersInfoService.findByUid(uid).getNickName();
        Article article = new Article(uid,nickname,category,tags1,coverPath,title,summary,new Date(),pwd,0,0,0);
        // 保存文章信息并返回
        Article article1 = articlesService.save(article);

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

    /**
     * 将新加的分类和标签添加到数据库
     * @param myTag MyTag
     * @param tags1 标签
     * @param category 分类
     * @param uid 用户id
     */
    private void addTag(MyTag myTag,List<String > tags1,String category,int uid){
        //如果数据为空，则存入数据
        if(myTag == null){
            MyTag myTag1 = new MyTag();
            Map<String ,Integer> map = new HashMap<>(30);
            //将标签分别存入，并置初始值为1
            for (String tag : tags1){
                map.put(tag,1);
            }
            myTag1.setTags(map);

            Map<String ,Integer> categorys = new HashMap<>(20);
            categorys.put(category,1);
            myTag1.setCategorys(categorys);
            myTag1.setUid(uid);
            tagService.save(myTag1);
        }
        //如果数据不为空，则更新数据
        else {
            Map<String ,Integer> tags2 = myTag.getTags();
            for (String tag : tags1){
                //如果存在键，则更新值
                if (tags2.containsKey(tag)){
                    tags2.put(tag,tags2.get(tag) + 1);
                }
                //如果不存在，则存入键值
                else {
                    tags2.put(tag,1);
                }
            }
            myTag.setTags(tags2);

            Map<String ,Integer> categorys = myTag.getCategorys();
            if (categorys.containsKey(category)){
                categorys.put(category, categorys.get(category) + 1);
            }else {
                categorys.put(category,1);
            }
            myTag.setCategorys(categorys);
            tagService.update(myTag);
        }
    }
}