package ncu.soft.blog.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import ncu.soft.blog.entity.Users;
import ncu.soft.blog.selfAnnotation.LoginToken;
import ncu.soft.blog.service.UserService;
import ncu.soft.blog.utils.GetString;
import ncu.soft.blog.utils.JsonResult;
import ncu.soft.blog.utils.ResultCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/16 21:06
 */
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    UserService userService;

    @Autowired
    ValueOperations<String ,Object> valueOperations;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从http请求头中取出token
        String token = request.getHeader("Authorization");

        //如果不是方法级别的请求，则直接通过
        if (!(handler instanceof HandlerMethod)){
            return true;
        }

        //利用java反射获取到请求的方法
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();

        //如果方法添加了LoginToken注解
        if(method.isAnnotationPresent(LoginToken.class)){
            LoginToken loginToken = method.getAnnotation(LoginToken.class);
            if (loginToken.required()){
                //重置响应
                response.reset();
                if(token == null){
                    response.setStatus(401);
                    return true;
                }
                String uid = "";
                try {
                    uid = JWT.decode(token).getAudience().get(0);
                }catch (JWTDecodeException jd){
                    //token取数据出问题
                    response.setStatus(401);
                    return true;
                }

                Users users = userService.findById(Integer.parseInt(uid));
                //token错误
                String token1 = (String)valueOperations.get(users.getUPwd());
                System.out.println(token1);
                if (token1 == null){
                    response.setStatus(403);
                    return true;
                }
                if (!StringUtils.equals(token1,token)){
                    response.setStatus(401);
                    return true;
                }
            }
            return true;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}