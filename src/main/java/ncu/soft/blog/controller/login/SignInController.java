package ncu.soft.blog.controller.login;

import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.ImageCode;
import ncu.soft.blog.entity.Users;
import ncu.soft.blog.service.UserService;
import ncu.soft.blog.utils.CreateImageCode;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.JwtUtil;
import ncu.soft.blog.utils.ResultCode;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author www.xyjz123.xyz
 * @description 用户登录相关api接口
 * @date 2019/8/16 22:31
 */
@RestController
public class SignInController {

    @Resource
    UserService userService;

    @Resource
    ValueOperations<String ,Object> valueOperations;

    /**图片验证码的key*/
    private final static String IMAGE = "imageCaptcha";

    @ApiOperation("图形验证码")
    @GetMapping(value = "/imageCaptcha",produces = "image/jpeg")
    @ResponseStatus(HttpStatus.CREATED)
    public void getCode(HttpServletResponse response) throws IOException {

        //生成图片验证码
        ImageCode imageCode = CreateImageCode.createImagecode();
        //将图片验证码存入redis中，并设置过期时间为5分钟
        valueOperations.set(IMAGE,imageCode,5, TimeUnit.MINUTES);
        //将图片验证码以文件流的形式写入到响应中
        ImageIO.write(imageCode.getImage(),"JPEG",response.getOutputStream());
    }

    @ApiOperation("检查账号是否存在")
    @PostMapping("/account")
    public JsonResult checkAccount(@Valid @RequestParam("account") String account){
        if (userService.isExist(account)){
            return JsonResult.success(ResultCode.USER_HAS_EXISTED);
        }else{
            return JsonResult.failure(ResultCode.USER_NOT_EXIST);
        }
    }

    @ApiOperation("账号登录")
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonResult login(@Valid @RequestBody Users users){
        if (!userService.verifyUser(users)){
            return JsonResult.failure(ResultCode.USER_LOGIN_ERROR);
        }else {
            //利用JWT生成token
            String token = JwtUtil.generateToken(users);
            //将token存入redis并设置过期时间为1小时
            valueOperations.set("token",token,1,TimeUnit.HOURS);
            return JsonResult.success(token);
        }
    }

    @ApiOperation("账号注销")
    @DeleteMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public JsonResult logout(@Valid @RequestParam("account")String account){
        if (!userService.isExist(account)){
            return JsonResult.failure(ResultCode.USER_NOT_EXIST);
        }else {
            //删除token
            valueOperations.getOperations().delete("token");
            return JsonResult.success();
        }
    }
}