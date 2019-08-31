package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ncu.soft.blog.selfAnnotation.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;
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
public class Article implements Serializable {

    private static final long serialVersionUID = -6473494395860839879L;
    /**
     * article的主键
     */
    @AutoIncKey
    @Id
    private int id;

    /**
     * 用户uid
     */
    private int uid;

    /**
     * 用户昵称
     */
    private String uNickname;

    /**
     * 文章个人分类
     */
    private String category;

    /**
     * 文章的标签
     */
    private List<String> tags;

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
    private Date articleTime;

    /**
     * 文章口令(公开文章口令为空)
     */
    private String pwd;

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

    public Article(int uid, String uNickname, String category, List<String> tags, String coverPath,
                   String title, String summary, Date articleTime,String pwd, int reads, int likes, int comments) {
        this.uid = uid;
        this.uNickname = uNickname;
        this.category = category;
        this.tags = tags;
        this.coverPath = coverPath;
        this.title = title;
        this.summary = summary;
        this.articleTime = articleTime;
        this.pwd = pwd;
        this.reads = reads;
        this.likes = likes;
        this.comments = comments;
    }

    public Article(int uid, String category, List<String> tags, String title, String pwd) {
        this.uid = uid;
        this.category = category;
        this.tags = tags;
        this.title = title;
        this.pwd = pwd;
    }
}