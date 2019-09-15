package ncu.soft.blog.service;

import java.util.Map;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/9 21:35
 */
public interface SocketService {

    /**
     * 服务端推送消息到客户端
     * @param uid id
     * @param message 消息
     */
    void pushMessage(String uid,String message);
}