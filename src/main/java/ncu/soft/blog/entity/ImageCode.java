package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/4/8 12:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageCode {

    private BufferedImage image;
    private String code;
    private LocalDateTime expireTime;

    /**
     * @author 熊义杰 www.xyj123.xyz
     * @description 传入过期具体时间来构造
     * @date 2019/4/8 12:45
     * @param [image, code, expireIn]
     * @return
     **/
    public ImageCode(BufferedImage image, String code, int expireIn){
        this.image = image;
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    /**
     * 判断是否过期
     * @return
     */
    public Boolean isExpired(){
        return LocalDateTime.now().isAfter(expireTime);
    }
}