package ncu.soft.blog.controller.article;

import io.swagger.annotations.ApiOperation;
import ncu.soft.blog.entity.Article;
import ncu.soft.blog.entity.Message;
import ncu.soft.blog.entity.UsersInfo;
import ncu.soft.blog.selfAnnotation.LoginToken;
import ncu.soft.blog.service.ArticlesService;
import ncu.soft.blog.service.MessageService;
import ncu.soft.blog.service.SocketService;
import ncu.soft.blog.service.UsersInfoService;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.ResultCode;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/31 11:33
 */
@RestController
public class ManageController {

    @Resource
    ArticlesService articlesService;

    @Resource
    UsersInfoService usersInfoService;

    @Resource
    MessageService messageService;

    @Resource
    SocketService socketService;

    private final static String LIKE = "like";

    @ApiOperation("删除文章")
    @DeleteMapping("/articles/{aid}/{uid}")
    @LoginToken
    public JsonResult deleteArticle(@Valid @PathVariable("aid") int aid,@PathVariable("uid") int uid){
        articlesService.delete(aid,uid);
        return JsonResult.success();
    }

    @ApiOperation("文章点赞并推送消息")
    @PostMapping("/article/{aid}/{uid}")
    public JsonResult likeArticle(@Valid @PathVariable("aid") int aid,@PathVariable("uid") int uid){
        Article article = articlesService.getArticle(aid);
        UsersInfo usersInfo = usersInfoService.findByUid(uid);
        socketService.pushMessage(String.valueOf(uid), "like");
        int likes = 0;
        if (messageService.getMessageByType(aid,uid,LIKE) != null){
            messageService.delete(aid,uid,LIKE);
            likes = articlesService.updateLikes(aid,-1).getLikes();
        }else {
            Message message1 = new Message(LIKE,aid,uid,"点赞了你的博文",
                    article.getTitle(),usersInfo.getNickName(),new Date(),false);
            messageService.save(message1);
            likes = articlesService.updateLikes(aid,1).getLikes();
        }
        return JsonResult.success(likes);
    }

    @ApiOperation("获取某用户是否对文章点赞")
    @GetMapping("/article/{aid}/{uid}")
    public JsonResult isLike(@Valid @PathVariable("aid") int aid,@PathVariable("uid") int uid){
        Message message = messageService.getMessageByType(aid,uid,LIKE);
        if (message == null){
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            return JsonResult.success();
        }
    }

    @ApiOperation("获取用户未读消息数")
    @GetMapping("/message/{uid}")
    public JsonResult getNoReadMessage(@Valid @PathVariable("uid") int uid){
        long num = messageService.getMessageWithoutRead(uid);
        if (num >= 0) {
            return JsonResult.success(num);
        }else {
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }
    }

    @ApiOperation("改变消息未读为已读")
    @PutMapping("/message/{aid}/{uid}/{type}")
    public JsonResult changeState(@Valid @PathVariable("aid") int aid, @PathVariable("uid") int uid,
                                  @PathVariable("type") String type){
        Message message = messageService.changeMessageState(uid, aid, type);
        if (message != null){
            socketService.pushMessage(String.valueOf(uid), "hasRead");
            return JsonResult.success();
        }else {
            return JsonResult.failure(ResultCode.UPDATE_ERROR);
        }
    }

    @ApiOperation("分页获取点赞列表")
    @GetMapping("/message/{uid}/{index}/{size}")
    public JsonResult getMessageByPage(@Valid @PathVariable("uid") int uid,
                                       @PathVariable("index") int index,@PathVariable("size") int size){
        PageImpl<Message> messages = messageService.getMessageByUidByPage(uid, index, size);
        if (messages.isEmpty()) {
            return JsonResult.failure(ResultCode.RESULE_DATA_NONE);
        }else {
            return JsonResult.success(messages);
        }
    }
}