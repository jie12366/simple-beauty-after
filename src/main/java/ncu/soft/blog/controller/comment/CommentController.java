package ncu.soft.blog.controller.comment;

import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.Comment;
import ncu.soft.blog.service.CommentService;
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

    @ApiOperation("将评论存入")
    @PostMapping("/comments")
    public JsonResult saveComments(@Valid @RequestParam("aid")int aid,@RequestParam("uid") int uid,
                                   @RequestParam("content") String content){
        Comment comment = new Comment(aid,uid,content);
        if (commentService.save(comment) != null){
            return JsonResult.success();
        }else {
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }
    }

    @ApiOperation("将评论回复存入")
    @PostMapping("/replyComments")
    public JsonResult saveReply(@Valid @RequestParam("aid")int aid,@RequestParam("uid")int uid,@RequestParam("rUid")int rUid,
                                @RequestParam("content")String content,@RequestParam("rContent")String rContent){
        Comment comment = new Comment(aid,uid,rUid,content,rContent);
        if (commentService.saveReply(comment) != null){
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