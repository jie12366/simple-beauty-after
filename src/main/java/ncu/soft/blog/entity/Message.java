package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ncu.soft.blog.selfannotation.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/10 9:11
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "message")
public class Message {

    @AutoIncKey
    @Id
    private int id;

    /**
     * 消息类型
     */
    private String type;

    /**
     * 文章id
     */
    private int aid;

    /**
     * 用户id
     */
    private String uid;

    /**
     * 消息
     */
    private String message;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 点赞用户昵称
     */
    private String nickname;

    /**
     * 消息时间
     */
    private Date mTime;

    /**
     * 是否已读
     */
    private boolean hasRead;

    public Message(String type, int aid, String uid, String message, String title, String nickname, Date mTime,boolean hasRead) {
        this.type = type;
        this.aid = aid;
        this.uid = uid;
        this.message = message;
        this.title = title;
        this.nickname = nickname;
        this.mTime = mTime;
        this.hasRead = hasRead;
    }
}