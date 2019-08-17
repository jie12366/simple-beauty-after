package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
public class Comment {

    /**
     * 评论者的用户信息
     */
    @Field("uInfo")
    private UsersInfo usersInfo;

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
    private List<ReplyComment> replyComments;
}