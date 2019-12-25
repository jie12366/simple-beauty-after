package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ncu.soft.blog.selfannotation.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/13 14:38
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "comment")
public class Comment implements Serializable {

    private static final long serialVersionUID = -7237665666204550844L;
    /**
     * Comment的主键
     */
    @AutoIncKey
    @Id
    private int id;

    /**
     * 文章id
     */
    private int aid;

    /**
     * 评论者的用户id
     */
    @Indexed
    private String uid;

    /**
     * 回复的目标id
     */
    private String rUid;

    /**
     * 回复的目标的内容
     */
    private String rContent;

    /**
     * 评论的发表时间
     */
    private Date cTime;

    /**
     * 评论内容
     */
    private String content;

    public Comment(int aid,String uid, String content) {
        this.aid = aid;
        this.uid = uid;
        this.content = content;
    }

    public Comment(int aid,String uid, String rUid, String content,String rContent) {
        this.aid = aid;
        this.uid = uid;
        this.rUid = rUid;
        this.content = content;
        this.rContent = rContent;
    }
}