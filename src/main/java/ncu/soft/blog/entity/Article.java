package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ncu.soft.blog.selfAnnotation.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/13 14:28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "article")
public class Article {

    /**
     * article的主键
     */
    @AutoIncKey
    @Id
    private int id;

    /**
     * 博主用户信息
     */
    private UsersInfo usersInfo;

    /**
     * 引用category的id
     */
    private int cid;

    /**
     * 文章的标签id
     */
    private List<Integer> tags;

    /**
     * 文章的封面图片路径
     */
    @Field("cPath")
    private String coverPath;

    /**
     * 文章的标题
     */
    private String title;

    /**
     * 文章的摘要
     */
    private String summary;

    /**
     * 文章的创建或修改时间
     */
    @Field("aTime")
    private String articleTime;

    /**
     * 文章阅读量
     */
    private int reads;

    /**
     * 文章点赞数
     */
    private int likes;

    /**
     * 文章评论数
     */
    private int comments;
}