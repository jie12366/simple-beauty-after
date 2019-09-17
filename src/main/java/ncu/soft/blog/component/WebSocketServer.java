package ncu.soft.blog.component;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/17 9:02
 */
@Slf4j
@ServerEndpoint("/webSocket/{uid}")
@Component
public class WebSocketServer {

    /**
     * 当前在线连接数(使用原子类型保证线程安全)
     */
    private static AtomicLong onlineCount = new AtomicLong();

    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象
     */
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 用户id
     */
    private String uid;

    /**
     * 构造一个固定线程的线程池
     */
    private ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(10, 10,
            0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(200),
            new ThreadFactoryBuilder().setNameFormat("monJay-%d").build());

    @OnOpen
    public void onOpen(Session session,@PathParam("uid") String uid){
        this.session = session;
        this.uid = uid;
        //加入set中
        webSocketSet.add(this);
        //在线数加1
        onlineCount.incrementAndGet();
        log.info("有新连接加入！当前在线人数为" + onlineCount);
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        //从set中删除
        webSocketSet.remove(this);
        //在线数减1
        onlineCount.decrementAndGet();
        log.info("有一连接关闭！当前在线人数为" + onlineCount);
    }

    /**
     *连接发生错误
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 向客户端推送消息
     * @param message 消息内容
     */
    private void sendMessage(String message){
        this.session.getAsyncRemote().sendText(message);
    }

    /**
     * 一对一推送消息
     * */
    public void sendInfo(String message,String uid){
        log.info("推送消息到窗口"+uid+"，推送内容:"+message);
        // 遍历webSocketSet集合
        for (WebSocketServer item : webSocketSet) {
            poolExecutor.submit(() -> {
                if(item.uid.equals(uid)){
                    item.sendMessage(message);
                }
            });
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}