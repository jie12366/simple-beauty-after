package ncu.soft.blog.controller.login;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.xkcoding.justauth.AuthRequestFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import ncu.soft.blog.entity.UsersInfo;
import ncu.soft.blog.service.UsersInfoService;
import ncu.soft.blog.utils.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/22 18:15
 */
@Slf4j
@RestController
@RequestMapping("oauth")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class OauthController {

    private final AuthRequestFactory factory;

    private final UsersInfoService usersInfoService;

    @Resource
    private ValueOperations<String,String > valueOperations;

    /**
     * 登录
     *
     * @param oauthType 第三方登录类型
     */
    @GetMapping("/login/{oauthType}")
    public void renderAuth(@PathVariable String oauthType, HttpServletResponse response) throws IOException {
        AuthRequest authRequest = factory.get(getAuthSource(oauthType));
        response.sendRedirect(authRequest.authorize(oauthType + "::" + AuthStateUtils.createState()));
    }

    /**
     * 登录成功后的回调
     *
     * @param oauthType 第三方登录类型
     * @param callback  携带返回的信息
     */
    @GetMapping("/{oauthType}/callback")
    public void login(@PathVariable String oauthType, AuthCallback callback, HttpServletResponse response1) throws IOException {
        AuthRequest authRequest = factory.get(getAuthSource(oauthType));
        // 登录
        AuthResponse response = authRequest.login(callback);
        // 将响应的数据格式化为字符串json
        String result = JSONUtil.toJsonStr(response);
        // 将字符串转化为json对象
        JSONObject jsonObject = JSONObject.parseObject(result);
        // 获取json对象中的data对象
        JSONObject data = jsonObject.getJSONObject("data");
        int uid = Integer.parseInt(data.getString("uuid"));
        UsersInfo usersInfo1 = usersInfoService.findByUid(uid);
        // 如果用户不存在，则将获取的第三方应用的信息存入(唯一标识uid,昵称,头像)
        if (usersInfo1 == null){
            UsersInfo usersInfo = new UsersInfo(uid,data.getString("username"),
                    data.getString("avatar"),0,0,0,0,0);
            usersInfoService.save(usersInfo);
        }
        // 传给前端的数据
        JSONObject res = new JSONObject();
        res.put("username",data.getString("username"));
        res.put("avatar",data.getString("avatar"));
        res.put("uid",uid);
        // 取出token
        String token = data.getJSONObject("token").getString("accessToken");
        res.put("token",token);
        // 设置token的过期时间
        valueOperations.set(token,token,7, TimeUnit.HOURS);
        // 重定向到前端的第三方登录中转页面
        response1.sendRedirect("http://jie12366.xyz:8081/#/oauth?result=" + res);
    }

    private AuthSource getAuthSource(String type) {
        if (StrUtil.isNotBlank(type)) {
            return AuthSource.valueOf(type.toUpperCase());
        } else {
            throw new RuntimeException("不支持的类型");
        }
    }
}