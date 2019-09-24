package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.Message;
import ncu.soft.blog.service.MessageService;
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

    @Override
    public Message save(Message message) {
        return mongoTemplate.insert(message);
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
    public Message changeMessageState(int id) {
        Query query = new Query(Criteria.where("id").is(id));
        Update update = Update.update("hasRead",true);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return mongoTemplate.findAndModify(query,update,options,Message.class);
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