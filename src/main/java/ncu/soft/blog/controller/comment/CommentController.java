package ncu.soft.blog.controller.comment;

import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.Comment;
import ncu.soft.blog.selfannotation.LoginToken;
import ncu.soft.blog.service.CommentService;
import ncu.soft.blog.service.MessageService;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.ResultCode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/31 15:22
 */
@RestController
public class CommentController {

    @Resource
    CommentService commentService;

    @Resource
    MessageService messageService;

    /**
     * 评论文章
     * @param aid 被评论的文章ID
     * @param uid 评论者的ID
     * @param toUid 要评论的用户ID（也就是该文章的作者）
     * @param content 评论的内容
     * @return 操作结果
     */
    @ApiOperation("将评论存入")
    @PostMapping("/comments")
    @LoginToken
    public JsonResult saveComments(@Valid @RequestParam("aid")int aid,@RequestParam("uid") String uid,
                                   @RequestParam("toUid") String toUid,@RequestParam("content") String content){
        Comment comment = new Comment(aid,uid,content);
        if (commentService.save(comment) != null){
            messageService.pushCommentMessage(aid, uid, toUid,"comment");
            return JsonResult.success();
        }else {
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }
    }

    @ApiOperation("将评论回复存入")
    @PostMapping("/replyComments")
    @LoginToken
    public JsonResult saveReply(@RequestParam("aid")int aid,@RequestParam("uid")String uid,@RequestParam("rUid")String rUid,
                                @RequestParam("content")String content,@RequestParam("rContent")String rContent){
        Comment comment = new Comment(aid,uid,rUid,content,rContent);
        if (commentService.saveReply(comment) != null){
            messageService.pushCommentMessage(aid, uid, rUid,"reply");
            return JsonResult.success();
        }else {
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }
    }

    @ApiOperation("根据aid分页取出评论")
    @GetMapping("/comments/{aid}/{index}/{size}")
    public JsonResult getComments(@Valid @PathVariable("aid") int aid,@PathVariable("index") int index,@PathVariable("size") int size){
        List<Comment> comments = commentService.getCommentByPage(aid,index,size).getContent();
        if (comments.isEmpty()) {
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            return JsonResult.success(comments);
        }
    }
}