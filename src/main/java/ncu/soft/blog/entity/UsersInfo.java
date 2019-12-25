package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ncu.soft.blog.selfannotation.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/13 15:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users_info")
public class UsersInfo implements Serializable {

    private static final long serialVersionUID = -1391733975883260032L;
    /**
     *主键
     */
    @AutoIncKey
    @Id
    private int id;

    /**
     * 用户id
     */
    @Indexed
    private String uid;

    /**
     * 用户自定义的主题
     */
    private UsersTheme usersTheme;

    /**
     * 我的昵称
     */
    private String nickName;

    /**
     * 我的手机
     */
    private String phone;

    /**
     * 我的邮箱
     */
    private String email;

    /**
     * 我的博客介绍
     */
    private String introduction;

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

    /**
     * 我的浏览量
     */
    private int reads;

    /**
     * 我的文章
     */
    private int articles;

    public UsersInfo(String uid, String nickName, String phone, String headPath, int attentions, int fans, int likes, int reads, int articles) {
        this.uid = uid;
        this.nickName = nickName;
        this.phone = phone;
        this.headPath = headPath;
        this.attentions = attentions;
        this.fans = fans;
        this.likes = likes;
        this.reads = reads;
        this.articles = articles;
    }

    public UsersInfo(String uid, String nickName, String headPath, int attentions, int fans, int likes, int reads, int articles) {
        this.uid = uid;
        this.nickName = nickName;
        this.headPath = headPath;
        this.attentions = attentions;
        this.fans = fans;
        this.likes = likes;
        this.reads = reads;
        this.articles = articles;
    }
}