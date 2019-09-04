package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.Comment;
import ncu.soft.blog.service.CommentService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/31 15:20
 */
@Service
@CacheConfig(cacheNames = "commentService")
public class CommentServiceImpl implements CommentService {

    @Resource(name = "mongoTemplate")
    MongoTemplate mongoTemplate;

    @Override
    @CacheEvict(allEntries = true)
    public Comment save(Comment comment) {
        comment.setCTime(new Date());
        return mongoTemplate.insert(comment);
    }

    @Override
    @CacheEvict(allEntries = true)
    public Comment saveReply(Comment comment) {
        comment.setCTime(new Date());
        return mongoTemplate.insert(comment);
    }

    @Override
    public void delete(int aid) {
        mongoTemplate.remove(new Query(Criteria.where("aid").is(aid)),Comment.class);
    }

    @Override
    @Cacheable(key = "#aid + '_' + #index + '_' + #size")
    public PageImpl<Comment> getCommentByPage(int aid,int index, int size) {
        Pageable pageable = PageRequest.of(index,size);
        Query query = new Query(Criteria.where("aid").is(aid)).with(new Sort(Sort.Direction.DESC,"cTime"));
        query.with(pageable);
        List<Comment> comments = mongoTemplate.find(query,Comment.class);
        return (PageImpl<Comment>) PageableExecutionUtils.getPage(comments,pageable,() -> 0);
    }
}