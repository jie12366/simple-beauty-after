package ncu.soft.blog.controller.comment;

import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.component.WebSocketServer;
import ncu.soft.blog.entity.Article;
import ncu.soft.blog.entity.Comment;
import ncu.soft.blog.entity.Message;
import ncu.soft.blog.entity.UsersInfo;
import ncu.soft.blog.service.ArticlesService;
import ncu.soft.blog.service.CommentService;
import ncu.soft.blog.service.MessageService;
import ncu.soft.blog.service.UsersInfoService;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.ResultCode;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;
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
    ArticlesService articlesService;

    @Resource
    UsersInfoService usersInfoService;

    @Resource
    MessageService messageService;

    @Resource
    WebSocketServer webSocketServer;

    private static final String COMMENT = "comment";

    @ApiOperation("将评论存入")
    @PostMapping("/comments")
    public JsonResult saveComments(@Valid @RequestParam("aid")int aid,@RequestParam("uid") int uid,
                                   @RequestParam("toUid") int toUid,@RequestParam("content") String content){
        Comment comment = new Comment(aid,uid,content);
        if (commentService.save(comment) != null){
            pushCommentMessage(aid,toUid,"comment");
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
            pushCommentMessage(aid,rUid,"reply");
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

    /**
     * 向客户端推送评论消息
     * @param aid 文章id
     * @param uid 用户id
     */
    private void pushCommentMessage(int aid,int uid,String type){
        Article article = articlesService.getArticle(aid);
        UsersInfo usersInfo = usersInfoService.findByUid(uid);
        // 向客户端推送消息，有人评论了
        webSocketServer.sendInfo("comment",String.valueOf(uid));
        String message = "";
        if (("reply").equals(type)){
            message = "回复了你的评论";
        }else {
            message = "评论了你的博文";
        }
        // 将评论消息存入数据库
        Message message1 = new Message(COMMENT,aid,uid,message,
                article.getTitle(),usersInfo.getNickName(),new Date(),false);
        messageService.save(message1);
    }

}