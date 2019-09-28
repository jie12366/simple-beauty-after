package ncu.soft.blog.controller.user;

import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.UsersTheme;
import ncu.soft.blog.service.UploadService;
import ncu.soft.blog.service.UsersThemeService;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.ResultCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/28 19:26
 */
@RestController
public class ThemeController {

    @Resource
    UploadService uploadService;

    @Resource
    UsersThemeService usersThemeService;

    @ApiOperation("修改主页背景")
    @PutMapping("/indexBackground")
    public JsonResult updateIndex(@Valid HttpServletRequest request, @RequestParam("img") MultipartFile img, @RequestParam("uid")String  uid){
        try {
            String url = uploadService.getPath(request,img);
            UsersTheme usersTheme = usersThemeService.updateIndex(url,uid);
            if (usersTheme != null){
                return JsonResult.success(usersTheme);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonResult.failure(ResultCode.UPDATE_ERROR);
    }

    @ApiOperation("修改侧边背景")
    @PutMapping("/sideBackground")
    public JsonResult updateSide(@Valid HttpServletRequest request, @RequestParam("img") MultipartFile img, @RequestParam("uid")String uid){
        try {
            String url = uploadService.getPath(request,img);
            UsersTheme usersTheme = usersThemeService.updateSide(url,uid);
            if (usersTheme != null){
                return JsonResult.success(usersTheme);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonResult.failure(ResultCode.UPDATE_ERROR);
    }

    @ApiOperation("修改代码样式")
    @PutMapping("/codeStyle")
    public JsonResult updateStyle(@Valid @RequestParam("style") String style, @RequestParam("uid")String uid){
        UsersTheme usersTheme = usersThemeService.updateStyle(style,uid);
        if (usersTheme != null){
            return JsonResult.success(usersTheme);
        }
        return JsonResult.failure(ResultCode.UPDATE_ERROR);
    }

    @ApiOperation("获取用户主题")
    @GetMapping("/theme/{uid}")
    public JsonResult getTheme(@Valid @PathVariable("uid") String uid){
        UsersTheme usersTheme = usersThemeService.getTheme(uid);
        if (usersTheme != null){
            return JsonResult.success(usersTheme);
        }
        return JsonResult.failure(ResultCode.UPDATE_ERROR);
    }
}