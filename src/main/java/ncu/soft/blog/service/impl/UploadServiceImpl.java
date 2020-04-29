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
    String key = null;

    @Override
    public Response uploadFile(File file) throws QiniuException {
        Response response = this.uploadManager.put(file, key, getUploadToken());
        int retry = 0;
        while (response.needRetry() && retry < 3) {
            response = this.uploadManager.put(file, key, getUploadToken());
            retry++;
        }
        return response;
    }

    @Override
    public String getPath(HttpServletRequest request, MultipartFile file) throws IOException {
        //根据时间戳创建文件名
        String fileName = System.currentTimeMillis() + file.getOriginalFilename();
        //创建文件的实际路径
        String destFileName = request.getServletContext().getRealPath("") + File.separator + fileName;
        //根据文件路径创建文件对应的实际文件
        File destFile = new File(destFileName);
        //创建文件实际路径
        destFile.getParentFile().mkdirs();
        //将文件传到对应的文件位置
        file.transferTo(destFile);
        Response response = uploadFile(destFile);
        //解析上传成功的结果
        DefaultPutRet putRet = JSON.parseObject(response.bodyString(), DefaultPutRet.class);
        return  "http://cdn.jie12366.xyz/" + putRet.key;
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
