package ncu.soft.blog.controller.common;

import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.selfAnnotation.LoginToken;
import ncu.soft.blog.utils.JsonResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/25 10:09
 */
@RestController
public class RequestController {

    @ApiOperation("一个空请求，判断用户是否登录或登录是否过期")
    @GetMapping("/token")
    @LoginToken
    public JsonResult getToken(){
        return JsonResult.success();
    }

}