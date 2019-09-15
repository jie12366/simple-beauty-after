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
     * 删除信息
     * @param aid 文章id
     * @param uid 用户id
     * @param type 信息类型
     */
    void delete(int aid,int uid,String type);

    /**
     * 根据信息类型获取信息
     * @param aid 文章id
     * @param uid 用户id
     * @param type 信息类型
     * @return Message
     */
    Message getMessageByType(int aid,int uid,String type);

    /**
     * 判断用户是否有未读的消息
     * @param uid 用户id
     * @return 未读数
     */
    long getMessageWithoutRead(int uid);

    /**
     * 改变消息未读状态
     * @param aid 文章id
     * @param uid 用户id
     * @param type 信息类型
     * @return Message
     */
    Message changeMessageState(int uid, int aid, String type);

    /**
     * 根据用户id获取信息
     * @param uid 用户id
     * @param index 当前页
     * @param size 每页大小
     * @return PageImpl<Message>
     */
    PageImpl<Message> getMessageByUidByPage(int uid,int index,int size);
}