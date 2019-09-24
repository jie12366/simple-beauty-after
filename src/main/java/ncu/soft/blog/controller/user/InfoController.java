package ncu.soft.blog.controller.user;

import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.UsersInfo;
import ncu.soft.blog.selfAnnotation.LoginToken;
import ncu.soft.blog.service.EmailService;
import ncu.soft.blog.service.UploadService;
import ncu.soft.blog.service.UsersInfoService;
import ncu.soft.blog.utils.GetString;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.ResultCode;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/24 13:10
 */
@RestController
public class InfoController {

    @Resource
    UsersInfoService usersInfoService;

    @Resource
    EmailService emailService;

    @Resource
    ValueOperations<String ,Object> valueOperations;

    @Resource
    UploadService uploadService;

    private final static String EMAIL_CAPTCHA = "emailCaptcha";

    @ApiOperation("根据uid获取用户信息")
    @GetMapping("/users/{uid}")
    public JsonResult getUsersById(@Valid @PathVariable String uid){
        UsersInfo usersInfo = usersInfoService.findByUid(uid);
        if (usersInfo != null){
            return JsonResult.success(usersInfo);
        }else {
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }
    }

    @ApiOperation("更换头像")
    @PutMapping("/headPath")
    @LoginToken
    public JsonResult uploadImage(HttpServletRequest request, @RequestParam("image") MultipartFile image,@RequestParam("uid") int uid) throws IOException {
        String path = uploadService.getPath(request,image);
        if(usersInfoService.updateHeadPath(path,String.valueOf(uid)) != null){
            return JsonResult.success(path);
        }else {
            return JsonResult.failure(ResultCode.UPDATE_ERROR);
        }
    }

    @ApiOperation("发送邮箱验证码")
    @PostMapping("/emailCaptcha")
    public JsonResult sendEmailCaptcha(@Valid @RequestParam("email") String email){
        String code = GetString.getCode();
        StringBuilder html = new StringBuilder();
        String html1 = "<div style=\"background-color:#eeeeee;height: 100%;\"><h1 style=\"padding:50px;color: #ea705b\">Canary</h1>\n" +
                "    <section style=\"background-color: #ffffff;width: 30vw;height: 30%;margin: auto\"><section style=\"margin-left:20%;\"><h1>您好</h1><br/>\n" +
                "        <p>请使用下面的验证码验证您的操作，验证码五分钟有效。</p><p style=\"width: 120px;height:45px;background-color: #ea705b;color: #ffffff;font-size: 30px;text-align: center\">";
        String html2 = "</p></section></div></div>";
        html.append(html1);
        html.append(code);
        html.append(html2);
        emailService.sendHtmlEmail(email,"绑定邮箱验证码",html.toString());
        valueOperations.set(EMAIL_CAPTCHA,code,5, TimeUnit.MINUTES);
        return JsonResult.success();
    }

    @ApiOperation("绑定邮箱")
    @PutMapping("/email")
    @LoginToken
    public JsonResult bindEmail(@Valid @RequestParam("email") String email, @RequestParam("uid") String uid){
        if (usersInfoService.bindEmail(email,uid) != null){
            return JsonResult.success(email);
        }else {
            return JsonResult.failure(ResultCode.UPDATE_ERROR);
        }
    }

    @ApiOperation("检查昵称是否被用过")
    @PostMapping("/nickname")
    public JsonResult checkNickname(@Valid @RequestParam("nickname") String nickname){
        if (usersInfoService.checkNickname(nickname) != null){
            return JsonResult.failure(ResultCode.DATA_ALREADY_EXISTED);
        }else {
            return JsonResult.success();
        }
    }

    @ApiOperation("更新昵称和个人简介")
    @PutMapping("/usersInfo")
    @LoginToken
    public JsonResult updateInfo(@Valid @RequestParam("nickname") String nickname,
                                 @RequestParam("introduction") String introduction, @RequestParam("uid") String uid){
        if (usersInfoService.updateInfo(nickname,introduction,uid) != null){
            return JsonResult.success();
        }else {
            return JsonResult.failure(ResultCode.UPDATE_ERROR);
        }
    }
}