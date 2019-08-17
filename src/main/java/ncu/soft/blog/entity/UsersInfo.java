package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ncu.soft.blog.selfAnnotation.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/13 15:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users_info")
public class UsersInfo {

    /**
     *主键
     */
    @AutoIncKey
    @Id
    private int id;

    /**
     * 用户id
     */
    private int uid;

    /**
     * 我的昵称
     */
    private String nikeName;

    /**
     * 我的个人介绍
     */
    @Field("dec")
    private String description;

    /**
     * 我的头像路径
     */
    @Field("hPath")
    private String headPath;

    /**
     * 我的关注
     */
    private int attentions;

    /**
     * 我的粉丝
     */
    private int fans;

    /**
     * 我收获的喜欢
     */
    private int likes;
}