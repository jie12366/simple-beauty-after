package ncu.soft.blog.controller.login;

import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.Users;
import ncu.soft.blog.service.UserService;
import ncu.soft.blog.utils.GetString;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.ResultCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

/**
 * @author www.xyjz123.xyz
 * @description 用户注册相关api接口
 * @date 2019/8/18 13:51
 */
@RestController
public class SignUpController {

    @Resource
    UserService userService;

    @Resource
    ValueOperations<String ,Object> valueOperations;

    private final static String SMS = "smsCaptcha";

    @ApiOperation("发送短信验证码")
    @PostMapping("/phone")
    public JsonResult sendMsg(HttpServletRequest request, @RequestParam("phone") String phone) throws Exception{
        System.out.println(phone);
        String code = GetString.getCode();
        System.out.println("code = " + code);
        //榛子短信的SDK
        /*ZhenziSmsClient client = new ZhenziSmsClient("https://sms_developer.zhenzikj.com", "101348", "ZGZmNjM3MWYtZDVjMS00YWUyLWE4NmUtZDI5NjNmOGRjNTA1");
        String result = client.send(phone, "您的验证码为" + code + "\n" + "如果不是本人操作，请忽略。");
        JSONObject jsonObject = JSONObject.parseObject(result);
        if ((Integer) jsonObject.get("code") != 0){
            return JsonResult.errorMsg("验证码发送失败");
        }*/

        //设置验证码1分钟后过期
        valueOperations.set(SMS,code,1, TimeUnit.MINUTES);

        return JsonResult.success();
    }

    @ApiOperation("检查验证码是否正确")
    @PostMapping("/captcha")
    public JsonResult checkVerifyCode(@Valid @RequestParam("key")String key,
            @Valid @RequestParam("code") String code){
        if (!valueOperations.getOperations().hasKey(key)){
            return JsonResult.failure(ResultCode.CAPTCHA_HAS_EXPIRED);
        }
        String verifyCode = (String) valueOperations.get(key);
        if (!StringUtils.equalsIgnoreCase(code,verifyCode)){
            return JsonResult.failure(ResultCode.CAPTCHA_IS_ERROR);
        }
        return JsonResult.success();
}

    @ApiOperation("账号注册")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public JsonResult addUser(@Valid @RequestBody Users users){
        return JsonResult.success(userService.save(users));
    }
}