package ncu.soft.blog.controller.article;

import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.selfAnnotation.LoginToken;
import ncu.soft.blog.service.ArticlesService;
import ncu.soft.blog.utils.JsonResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/31 11:33
 */
@RestController
public class ManageController {

    @Resource
    ArticlesService articlesService;

    @ApiOperation("删除文章")
    @DeleteMapping("/articles/{aid}/{uid}")
    @LoginToken
    public JsonResult deleteArticle(@Valid @PathVariable("aid") int aid,@PathVariable("uid") int uid){
        articlesService.delete(aid,uid);
        return JsonResult.success();
    }
}