package ncu.soft.blog.controller.login;

import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.Users;
import ncu.soft.blog.service.UserService;
import ncu.soft.blog.service.UsersInfoService;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.JwtUtil;
import ncu.soft.blog.utils.ResultCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
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
    UsersInfoService usersInfoService;

    @Resource
    ValueOperations<String ,Object> valueOperations;

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
    public JsonResult login(@Valid @RequestBody Users users,@RequestParam("remeberMe")boolean remeberMe){
        if (userService.verifyUser(users) == null){
            return JsonResult.failure(ResultCode.USER_LOGIN_ERROR);
        }else {
            Users users1 = userService.findByAccount(users.getUAccount());
            // 利用JWT生成token
            String token = JwtUtil.generateToken(users1);
            // 将生成的token的签证作为redis的键
            String key = token.split("\\.")[2];
            if (remeberMe){
                // 如果勾选了记住密码，则将过期时间设置为七天
                valueOperations.set(key,token,7,TimeUnit.DAYS);
            }else {
                // 将token存入redis并设置过期时间为7小时
                valueOperations.set(key,token,7,TimeUnit.HOURS);
            }
            return JsonResult.success(token);
        }
    }

    @ApiOperation("账号注销")
    @DeleteMapping("/logout/{uid}")
    public JsonResult logout(@Valid @PathVariable("uid")String uid, HttpServletRequest request){
        // 从http请求头中取出token
        String token = request.getHeader("Authorization");
        // 如果uid不全是数字（说明是QQ登录的id），或者不存在这个账号，那么就是第三方登录
        if (!StringUtils.isNumeric(uid) || userService.findById(Integer.parseInt(uid)) == null){
            if (usersInfoService.findByUid(uid) == null){
                return JsonResult.failure(ResultCode.USER_NOT_EXIST);
            }else {
                //删除token
                valueOperations.getOperations().delete(token);
                return JsonResult.success();
            }
        }else {
            //将生成的token的签证作为redis的键
            String key = token.split("\\.")[2];
            //删除token
            valueOperations.getOperations().delete(key);
            return JsonResult.success();
        }
    }
}