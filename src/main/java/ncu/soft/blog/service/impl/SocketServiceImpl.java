package ncu.soft.blog.service.impl;

import com.corundumstudio.socketio.SocketIOClient;
import ncu.soft.blog.component.ClientCache;
import ncu.soft.blog.entity.Message;
import ncu.soft.blog.service.MessageService;
import ncu.soft.blog.service.SocketService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/9 21:35
 */
@Service
public class SocketServiceImpl implements SocketService {

    @Resource
    private ClientCache clientCache;

    @Override
    public void pushMessage(String uid,String message) {
        HashMap<UUID, SocketIOClient> userClient = clientCache.getUserClient(uid);
        userClient.forEach((uuid,socketIOClient) -> {
            socketIOClient.sendEvent("pushMessage",message);
        });
    }
}