package ncu.soft.blog.controller.article;

import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.service.ArticlesService;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/31 11:33
 */
@RestController
public class ManageController {

    @Resource
    ArticlesService articlesService;
}