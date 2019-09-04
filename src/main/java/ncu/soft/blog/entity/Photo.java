package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/3 16:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}