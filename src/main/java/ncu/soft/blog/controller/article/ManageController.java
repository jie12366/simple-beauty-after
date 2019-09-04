package ncu.soft.blog.controller.article;

import com.qiniu.common.QiniuException;
import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.Photo;
import ncu.soft.blog.entity.Photos;
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

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/31 11:33
 */
@RestController
public class ManageController {

    @Resource
    PhotosService photosService;

    @Resource
    UploadService uploadService;

    @ApiOperation("将照片集存入")
    @PostMapping("/photos")
    public JsonResult savePhotos(@Valid HttpServletRequest request, @RequestParam("img") MultipartFile img,
                                 @RequestParam("alt") String alt,@RequestParam("uid") int uid){
        try {
            String url = uploadService.getPath(request,img);
            Photo photo = new Photo(url,alt);
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
    public JsonResult updatePhotos(@Valid HttpServletRequest request, @RequestParam("img") MultipartFile img,
                                   @RequestParam("alt") String alt,@RequestParam("uid") int uid){
        try {
            String url = uploadService.getPath(request,img);
            Photo photo = new Photo(url,alt);
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
    @DeleteMapping("/photo/{key}/{alt}/{uid}")
    public JsonResult deletePhoto(@Valid @PathVariable("key") String key,
                                   @PathVariable("alt") String alt,@PathVariable("uid") int uid){
        try {
            uploadService.deleteFile(key);
        } catch (QiniuException e) {
            e.printStackTrace();
        }
        String url = "http://cdn.jie12366.xyz/" + key;
        Photo photo = new Photo(url,alt);
        Photos photos = photosService.deletePhoto(uid,photo);
        if (photos != null){
            return JsonResult.success(photo);
        }
        return JsonResult.failure(ResultCode.UPDATE_ERROR);
    }

    @ApiOperation("获取照片集")
    @GetMapping("/photos/{uid}")
    public JsonResult getPhotos(@Valid @PathVariable("uid")int uid){
        Photos photos = photosService.getPhotos(uid);
        if (photos == null){
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            return JsonResult.success(photos);
        }
    }
}