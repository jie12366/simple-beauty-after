package ncu.soft.blog.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author www.xyjz123.xyz
 * @date 2019/3/28 15:17
 */

@Data
@Component
@ConfigurationProperties(prefix = "qiniu")
public class QiniuUtil {

    private String accessKey;

    private String secretKey;

    private String bucket;

    private String path;
}