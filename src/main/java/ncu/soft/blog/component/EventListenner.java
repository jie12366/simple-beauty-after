package ncu.soft.blog.component;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/9 21:16
 */

@Component
public class EventListenner {
    @Resource
    private ClientCache clientCache;
    /**
     * 客户端连接
     * @param client
     */
    @OnConnect
    public void onConnect(SocketIOClient client) {
        String uid = client.getHandshakeData().getSingleUrlParam("uid");
        UUID sessionId = client.getSessionId();
        clientCache.saveClient(uid,sessionId,client);
        System.out.println("建立连接");
    }
    /**
     * 客户端断开
     * @param client
     */
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        String uid = client.getHandshakeData().getSingleUrlParam("uid");
        clientCache.deleteSessionClient(uid,client.getSessionId());
        System.out.println("关闭连接");
    }

    /**
     * 消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息
     * 暂未使用
     * @param client
     * @param request
     */
    @OnEvent("messageevent")
    public void onEvent(SocketIOClient client, AckRequest request) {
    }
}

