package ncu.soft.blog.controller.login;

import com.google.code.kaptcha.Producer;
import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.Users;
import ncu.soft.blog.service.UserService;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.JwtUtil;
import ncu.soft.blog.utils.ResultCode;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.awt.image.BufferedImage;
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

    @Resource
    private Producer captchaProducer;

    private final static String IMAGE = "imageCaptcha";

    @ApiOperation("图形验证码")
    @GetMapping(value = "/imageCaptcha",produces = "image/jpeg")
    public void getCode(HttpServletResponse response) throws IOException {
        // 生成验证码
        String capText = captchaProducer.createText();
        valueOperations.set(IMAGE,capText);
        //生成图片验证码
        BufferedImage bi = captchaProducer.createImage(capText);
        //将图片验证码以文件流的形式写入到响应中
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "JPEG", out);
        try {
            out.flush();
        } finally {
            out.close();
        }
    }

    @ApiOperation("检查账号是否存在")
    @PostMapping("/account")
    public JsonResult checkAccount(@Valid @RequestParam("account") String account){
        if (userService.findByAccount(account) != null){
            return JsonResult.success(ResultCode.USER_HAS_EXISTED);
        }else{
            return JsonResult.failure(ResultCode.USER_NOT_EXIST);
        }
    }

    @ApiOperation("账号登录")
    @PostMapping("/login")
    public JsonResult login(@Valid @RequestBody Users users){
        if (userService.verifyUser(users) == null){
            return JsonResult.failure(ResultCode.USER_LOGIN_ERROR);
        }else {
            Users users1 = userService.findByAccount(users.getUAccount());
            //利用JWT生成token
            String token = JwtUtil.generateToken(users1);
            //将生成的token的签证作为redis的键
            String key = token.split("\\.")[2];
            //将token存入redis并设置过期时间为5小时
            valueOperations.set(key,token,5,TimeUnit.HOURS);
            return JsonResult.success(token);
        }
    }

    @ApiOperation("账号注销")
    @DeleteMapping("/logout")
    public JsonResult logout(@Valid @RequestParam("account")String account){
        if (userService.findByAccount(account) == null){
            return JsonResult.failure(ResultCode.USER_NOT_EXIST);
        }else {
            //删除token
            valueOperations.getOperations().delete("token");
            return JsonResult.success();
        }
    }
}