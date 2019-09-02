package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ncu.soft.blog.selfAnnotation.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Map;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/26 19:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "my_tag")
public class MyTag implements Serializable {

    private static final long serialVersionUID = 3204511066736409881L;
    /**
     * myTag的主键
     */
    @AutoIncKey
    @Id
    private int id;

    /**
     * 用户id
     */
    @Indexed
    private int uid;

    /**
     * 用户的个人标签与标签对应的文章数
     */
    private Map<String,Integer> tags;

    /**
     * 用户的个人分类与分类对应的文章数
     */
    private Map<String,Integer> categorys;

    /**
     * 用户的归档时间与对应的文章数
     */
    private Map<String,Integer> archives;
}