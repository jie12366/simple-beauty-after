package ncu.soft.blog.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import ncu.soft.blog.entity.Users;

import java.util.Date;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/16 20:23
 */
public class JwtUtil {

    /**
     * 设置jwt的过期时间为10分钟
     */
    private static final long EXPIREDTIME = 600000L;

    /**
     * 生成token，将用户id存入数据
     * 用户密码的md5加密字符作为密钥
     * 设置过期时间为10分钟
     * @param users Users
     * @return 生成的token字符串
     */
    public static String generateToken(Users users){
        String secret = GetString.getMd5(users.getUPwd());
        return JWT.create().withAudience(String.valueOf(users.getId()))
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIREDTIME))
                .sign(Algorithm.HMAC256(secret));
    }
}