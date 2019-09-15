package ncu.soft.blog.component;

import com.corundumstudio.socketio.SocketIOClient;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/9 21:17
 */

@Component
public class ClientCache {

    /**
     * 本地缓存
     */
    private static Map<String, HashMap<UUID, SocketIOClient>> concurrentHashMap=new ConcurrentHashMap<>();

    /**
     * 存入本地缓存
     * @param uid 用户ID
     * @param sessionId 页面sessionID
     * @param socketIOClient 页面对应的通道连接信息
     */
    public void saveClient(String uid, UUID sessionId,SocketIOClient socketIOClient){
        HashMap<UUID, SocketIOClient> sessionIdClientCache=concurrentHashMap.get(uid);
        if(sessionIdClientCache==null){
            sessionIdClientCache = new HashMap<>();
        }
        sessionIdClientCache.put(sessionId,socketIOClient);
        concurrentHashMap.put(uid,sessionIdClientCache);
    }
    /**
     * 根据用户ID获取所有通道信息
     * @param uid
     * @return
     */
    public HashMap<UUID, SocketIOClient> getUserClient(String uid){
        return concurrentHashMap.get(uid);
    }
    /**
     * 根据用户ID及页面sessionID删除页面链接信息
     * @param uid
     * @param sessionId
     */
    public void deleteSessionClient(String uid,UUID sessionId){
        concurrentHashMap.get(uid).remove(sessionId);
    }
}

