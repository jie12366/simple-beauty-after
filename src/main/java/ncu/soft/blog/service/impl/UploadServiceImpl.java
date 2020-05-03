package ncu.soft.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import ncu.soft.blog.service.UploadService;
import ncu.soft.blog.utils.QiniuUtil;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/22 16:55
 */
@Service
public class UploadServiceImpl implements UploadService, InitializingBean {

    @Resource
    private UploadManager uploadManager;
    @Resource
    private Auth auth;
    @Resource
    BucketManager bucketManager;
    @Resource
    private QiniuUtil qiNiuProperties;
    private StringMap putPolicy;

    @Override
    public Response uploadFile(InputStream stream, String key) throws QiniuException {
        Response response = this.uploadManager.put(stream, key, getUploadToken(), null, null);
        int retry = 0;
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(stream, key, getUploadToken(), null, null);
            retry++;
        }
        return response;
    }

    @Override
    public String getPath(HttpServletRequest request, MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        // 文件后缀
        if (originalFilename != null) {
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileKey = UUID.randomUUID().toString() + suffix;
            Response response = uploadFile(file.getInputStream(), fileKey);
            //解析上传成功的结果
            DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
            return  "http://cdn.jie12366.xyz/" + putRet.key;
        }
        return null;
    }

    @Override
    public void afterPropertiesSet(){
        this.putPolicy = new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"width\":$(imageInfo.width), \"height\":${imageInfo.height}}");
    }

    /**
     * 获取上传凭证
     * @return 获取token
     */
    private String getUploadToken() {
        return this.auth.uploadToken(qiNiuProperties.getBucket(), null, 3600, putPolicy);
    }

    @Override
    public void deleteFile(String key) throws QiniuException {
        bucketManager.delete(qiNiuProperties.getBucket(), key);
    }
}
