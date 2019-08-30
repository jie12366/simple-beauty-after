package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ncu.soft.blog.selfAnnotation.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;

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
     * 评论者的用户id
     */
    @Indexed
    private int uid;

    /**
     * 评论的发表时间
     */
    private String cTime;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论回复列表
     */
    @Field("rComments")
    private List<ReplyComment> replyComments;
}