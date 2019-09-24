package ncu.soft.blog.controller.user;

import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.Introduction;
import ncu.soft.blog.service.IntroductionService;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.ResultCode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/6 11:35
 */
@RestController
public class IntroductionController {

    @Resource
    IntroductionService introductionService;

    @ApiOperation("保存个人简介")
    @PostMapping("/introduction")
    public JsonResult save(@Valid @RequestBody Introduction introduction){
        Introduction introduction1 =  introductionService.save(introduction);
        if (introduction1 == null){
            return JsonResult.failure(ResultCode.SAVE_ERROR);
        }else {
            return JsonResult.success(introduction1);
        }
    }

    @ApiOperation("更新个人简介")
    @PutMapping("/introduction")
    public JsonResult update(@Valid @RequestBody Introduction introduction){
        Introduction introduction1 =  introductionService.update(introduction);
        if (introduction1 == null){
            return JsonResult.failure(ResultCode.UPDATE_ERROR);
        }else {
            return JsonResult.success(introduction1);
        }
    }

    @ApiOperation("获取个人简介")
    @GetMapping("/introduction/{uid}")
    public JsonResult get(@Valid @PathVariable("uid") String uid){
        Introduction introduction = introductionService.findByUid(uid);
        if (introduction == null){
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            return JsonResult.success(introduction);
        }
    }
}