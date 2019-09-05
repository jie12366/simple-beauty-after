package ncu.soft.blog.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/3 16:20
 */
@Data
@NoArgsConstructor
public class Photo implements Serializable {

    private static final long serialVersionUID = 8634113905649168780L;

    /**
     * 图片地址
     */
    private String url;

    /**
     * 图片描述
     */
    private String alt;

    /**
     * 照片上传时间
     */
    @Field("uTime")
    private Date uploadTime;

    public Photo(String url, String alt, Date uploadTime) {
        this.url = url;
        this.alt = alt;
        this.uploadTime = uploadTime;
    }
}