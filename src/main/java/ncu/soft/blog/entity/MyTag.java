package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ncu.soft.blog.selfAnnotation.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/26 19:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "my_tag")
public class MyTag {

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
     * 用户的个人标签(不能重复)
     */
    private Set<String> tags;

    /**
     * 用户的个人分类(不能重复)
     */
    private Set<String> categorys;
}