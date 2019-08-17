package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/13 14:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "reply_comment")
public class ReplyComment {

    /**
     * 评论者的用户信息
     */
    @Field("rInfo")
    private UsersInfo replyUsersInfo;

    /**
     * 回复的目标用户昵称
     */
    private int toNickName;

    /**
     * 评论回复的时间
     */
    private String rTime;

    /**
     * 评论回复的内容
     */
    private String content;
}