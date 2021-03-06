package ncu.soft.blog.service;

import ncu.soft.blog.entity.Message;
import org.springframework.data.domain.PageImpl;

import java.util.List;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/10 9:16
 */
public interface MessageService {

    /**
     * 保存信息
     * @param message 消息对象
     * @return Message
     */
    Message save(Message message);

    /**
     * 用户对文章点赞
     * @param aid 文章id
     * @param uid 用户id
     * @return 文章点赞数
     */
    int likeArticle(int aid, String uid);

    /**
     * 向客户端推送评论消息
     * @param aid 文章id
     * @param uid 评论者用户id
     * @param toUid 被评论者ID
     * @param type 评论类型
     */
    void pushCommentMessage(int aid, String uid, String toUid, String type);

    /**
     * 删除信息
     * @param aid 文章id
     * @param uid 用户id
     * @param type 信息类型
     */
    void delete(int aid,String uid,String type);

    /**
     * 根据id删除消息
     * @param id 消息id
     */
    void delete(int id);

    /**
     * 根据信息类型获取信息
     * @param aid 文章id
     * @param uid 用户id
     * @param type 信息类型
     * @return Message
     */
    Message getMessageByType(int aid,String uid,String type);

    /**
     * 判断用户是否有未读的消息
     * @param uid 用户id
     * @return 未读数
     */
    long getMessageWithoutRead(String uid);

    /**
     * 判断用户是否有未读的消息
     * @param type 消息类型
     * @param uid 用户id
     * @return 未读数
     */
    long getMessageWithoutReadByType(String type,String uid);

    /**
     * 改变消息未读状态
     * @param id 消息id
     * @param uid 用户id
     * @return 是否成功
     */
    Boolean changeMessageState(int id, String uid);

    /**
     * 根据用户id获取信息
     * @param uid 用户id
     * @param type 消息类型
     * @param index 当前页
     * @param size 每页大小
     * @return PageImpl<Message>
     */
    PageImpl<Message> getMessageByUidByPage(String uid,String type,int index,int size);
}