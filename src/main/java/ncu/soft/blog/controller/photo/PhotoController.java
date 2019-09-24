package ncu.soft.blog.controller.photo;

import com.qiniu.common.QiniuException;
import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.Photo;
import ncu.soft.blog.entity.Photos;
import ncu.soft.blog.selfAnnotation.LoginToken;
import ncu.soft.blog.service.ArticlesService;
import ncu.soft.blog.service.PhotosService;
import ncu.soft.blog.service.UploadService;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.ResultCode;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/5 13:54
 */
@RestController
public class PhotoController {

    @Resource
    PhotosService photosService;

    @Resource
    UploadService uploadService;

    @ApiOperation("将照片集存入")
    @PostMapping("/photos")
    @LoginToken
    public JsonResult savePhotos(@Valid HttpServletRequest request, @RequestParam("img") MultipartFile img,
                                 @RequestParam("alt") String alt, @RequestParam("uid") String uid){
        try {
            String url = uploadService.getPath(request,img);
            Photo photo = new Photo(url,alt,new Date());
            Photos photos = photosService.save(photo,uid);
            if (photos != null){
                return JsonResult.success(photo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonResult.failure(ResultCode.SAVE_ERROR);
    }

    @ApiOperation("增加照片")
    @PutMapping("/photos")
    @LoginToken
    public JsonResult updatePhotos(@Valid HttpServletRequest request, @RequestParam("img") MultipartFile img,
                                   @RequestParam("alt") String alt,@RequestParam("uid") String uid){
        try {
            String url = uploadService.getPath(request,img);
            Photo photo = new Photo(url,alt,new Date());
            if (photosService.photoIsExist(url,uid)){
                return JsonResult.success(photo);
            }else {
                Photos photos = photosService.addPhoto(photo,uid);
                if (photos != null){
                    return JsonResult.success(photo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return JsonResult.failure(ResultCode.UPDATE_ERROR);
    }

    @ApiOperation("删除照片")
    @DeleteMapping("/photo/{key}/{uid}")
    @LoginToken
    public JsonResult deletePhoto(@Valid @PathVariable("key") String key,@PathVariable("uid") String uid){
        try {
            uploadService.deleteFile(key);
        } catch (QiniuException e) {
            e.printStackTrace();
        }
        String url = "http://cdn.jie12366.xyz/" + key;
        Photos photos = photosService.deletePhoto(uid,url);
        if (photos != null){
            return JsonResult.success();
        }
        return JsonResult.failure(ResultCode.UPDATE_ERROR);
    }

    @ApiOperation("获取照片集")
    @GetMapping("/photos/{uid}")
    public JsonResult getPhotos(@Valid @PathVariable("uid")String uid){
        Photos photos = photosService.getPhotos(uid);
        if (photos == null){
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            return JsonResult.success(photos);
        }
    }
}