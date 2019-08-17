package ncu.soft.blog.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import ncu.soft.blog.entity.Users;
import ncu.soft.blog.selfAnnotation.LoginToken;
import ncu.soft.blog.service.UserService;
import ncu.soft.blog.utils.GetString;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //从http请求头中取出token
        String token = request.getHeader("token");

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
                // token不存在
                if (token == null){
                    response.setStatus(401);
                    return false;
                }

                String uid = "";
                try {
                    uid = JWT.decode(token).getAudience().get(0);
                }catch (JWTDecodeException jd){
                    //token取数据出问题
                    response.setStatus(401);
                    return false;
                }

                Users users = userService.findById(Integer.parseInt(uid));
                //用户名不存在
                if (users == null){
                    response.setStatus(401);
                    return false;
                }

                //验证token
                String secret = GetString.getMd5(users.getUPwd());
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secret)).build();
                try {
                    jwtVerifier.verify(token);
                }catch (JWTVerificationException jv){
                    //token错误
                    response.setStatus(403);
                    return false;
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