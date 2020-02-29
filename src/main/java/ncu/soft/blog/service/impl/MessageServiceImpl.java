package ncu.soft.blog.service.impl;

import ncu.soft.blog.component.WebSocketServer;
import ncu.soft.blog.entity.Article;
import ncu.soft.blog.entity.Message;
import ncu.soft.blog.entity.UsersInfo;
import ncu.soft.blog.service.ArticlesService;
import ncu.soft.blog.service.MessageService;
import ncu.soft.blog.service.UsersInfoService;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/10 9:17
 */
@Service
public class MessageServiceImpl implements MessageService {

    @Resource(name = "mongoTemplate")
    MongoTemplate mongoTemplate;

    @Resource
    ArticlesService articlesService;

    @Resource
    UsersInfoService usersInfoService;

    @Resource
    WebSocketServer webSocketServer;

    private final static String LIKE = "like";
    private static final String COMMENT = "comment";

    @Override
    public Message save(Message message) {
        return mongoTemplate.insert(message);
    }

    @Override
    public int likeArticle(int aid, String uid){
        Article article = articlesService.getArticle(aid);
        UsersInfo usersInfo = usersInfoService.findByUid(uid);
        int likes;
        // 判断该文章是否已被该用户点赞
        if (getMessageByType(aid,uid,LIKE) != null){
            // 向客户端推送消息，有人取消点赞了
            webSocketServer.sendInfo("unLike",uid);
            delete(aid,uid,LIKE);
            likes = articlesService.updateLikes(aid,-1).getLikes();
            usersInfoService.updateLikes(-1,uid);
        }else {
            // 向客户端推送消息，有人点赞了
            webSocketServer.sendInfo("like",uid);
            // 将点赞消息存入数据库
            Message message1 = new Message(LIKE,aid,uid,"点赞了你的博文",
                    article.getTitle(),usersInfo.getNickName(),new Date(),false);
            save(message1);
            // 更新文章喜欢量
            likes = articlesService.updateLikes(aid,1).getLikes();
            // 更新个人喜欢量
            usersInfoService.updateLikes(1,uid);
        }
        return likes;
    }

    @Override
    public void pushCommentMessage(int aid,String uid, String toUid,String type){
        Article article = articlesService.getArticle(aid);
        UsersInfo usersInfo = usersInfoService.findByUid(String.valueOf(uid));
        // 向客户端推送消息，有人评论了
        webSocketServer.sendInfo("comment",String.valueOf(toUid));
        String message;
        if (("reply").equals(type)){
            message = "回复了你的评论";
        }else {
            message = "评论了你的博文";
        }
        // 将评论消息存入数据库
        Message message1 = new Message(COMMENT,aid,uid,message,
                article.getTitle(),usersInfo.getNickName(),new Date(),false);
        save(message1);
    }

    @Override
    public void delete(int aid, String uid, String type) {
        mongoTemplate.remove(new Query(Criteria.where("type").is(type).
                and("aid").is(aid).and("uid").is(uid)),Message.class);
    }

    @Override
    public void delete(int id) {
        mongoTemplate.remove(new Query(Criteria.where("id").is(id)),Message.class);
    }

    @Override
    public Message getMessageByType(int aid, String uid, String type) {
        return mongoTemplate.findOne(new Query(Criteria.where("type").is(type).
                and("aid").is(aid).and("uid").is(uid)),Message.class);
    }

    @Override
    public long getMessageWithoutRead(String uid) {
        return mongoTemplate.count(new Query(Criteria.where("uid").is(uid).and("hasRead").is(false)),Message.class);
    }

    @Override
    public long getMessageWithoutReadByType(String type, String uid) {
        return mongoTemplate.count(new Query(Criteria.where("uid").is(uid)
                .and("type").is(type).and("hasRead").is(false)),Message.class);
    }

    @Override
    public Boolean changeMessageState(int id, String uid) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = Update.update("hasRead",true);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        Message message = mongoTemplate.findAndModify(query,update,options,Message.class);
        if (message != null){
            // 通知用户消息已读
            webSocketServer.sendInfo("hasRead",uid);
            return true;
        }else {
            return false;
        }
    }

    @Override
    public PageImpl<Message> getMessageByUidByPage(String uid,String type,int index,int size) {
        Pageable pageable = PageRequest.of(index,size);
        Query query = new Query(Criteria.where("uid").is(uid).and("type").is(type));
        query.with(new Sort(Sort.Direction.DESC,"mTime"));
        query.with(pageable);
        List<Message> messages = mongoTemplate.find(query,Message.class);
        long count = mongoTemplate.count(query,Message.class);
        return (PageImpl<Message>) PageableExecutionUtils.getPage(messages,pageable,() -> count);
    }
}