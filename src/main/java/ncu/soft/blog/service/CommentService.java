package ncu.soft.blog.service;

import ncu.soft.blog.entity.Comment;
import org.springframework.data.domain.PageImpl;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/31 15:18
 */
public interface CommentService {

    /**
     * 将评论存入数据库中
     * @param comment 评论对象
     * @return 返回的Comment
     */
    Comment save(Comment comment);

    /**
     * 评论回复存入数据库
     * @param comment 回复的对象
     * @return Comment
     */
    Comment saveReply(Comment comment);

    /**
     * 根据aid分页获取评论数据
     * @param aid 文章id
     * @param index 当前页
     * @param size 每页大小
     * @return PageImpl<Comment>
     */
    PageImpl<Comment> getCommentByPage(int aid,int index,int size);
}