package ncu.soft.blog.component;

import ncu.soft.blog.entity.SeqInfo;
import ncu.soft.blog.selfAnnotation.AutoIncKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/12 19:08
 */
@Component
public class SaveEventListener extends AbstractMongoEventListener<Object> {

    @Autowired
    MongoTemplate mongo;

    @Override
    public void onBeforeConvert(BeforeConvertEvent<Object> event) {
        final Object source = event.getSource();
        if (source != null){
            ReflectionUtils.doWithFields(source.getClass(),(filed) -> {
                //将一个字段设置为可读写，主要针对private字段
                ReflectionUtils.makeAccessible(filed);
                //如果该字段添加了我们自增的注解且字段是数字类型
                if (filed.isAnnotationPresent(AutoIncKey.class) &&
                        filed.get(source) instanceof Number && filed.getInt(source) == 0){
                    filed.set(source,getNextId(source.getClass().getSimpleName()));
                }
            });
        }
    }

    /**
     * 获取集合的下一个id(id自增)
     * @param collName 集合名称
     * @return 下一个id
     */
    private Integer getNextId(String collName){
        Query query = new Query(Criteria.where("collName").is(collName));
        Update update = new Update().inc("seqId",1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        options.upsert(true);
        options.returnNew(true);
        SeqInfo seq = mongo.findAndModify(query,update,options,SeqInfo.class);
        return seq.getSeqId();
    }
}