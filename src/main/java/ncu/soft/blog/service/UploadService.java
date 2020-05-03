package ncu.soft.blog.service;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/22 16:53
 */
public interface UploadService {
    /**
     * 上传文件
     * @param stream InputStream
     * @return 服务器返回的响应
     * @throws QiniuException
     */
    Response uploadFile(InputStream stream, String key) throws QiniuException;

    /**
     * 上传文件到七牛云并返回地址
     * @param request HttpServletRequest
     * @param file MultipartFile
     * @return 文件的服务器地址
     * @throws IOException
     */
    String getPath(HttpServletRequest request, MultipartFile file) throws IOException;

    /**
     * 根据文件名删除空间中的文件
     * @param key 文件名
     */
    void deleteFile(String key) throws QiniuException;
}
