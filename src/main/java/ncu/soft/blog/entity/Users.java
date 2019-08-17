package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ncu.soft.blog.selfAnnotation.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @author www.xyjz123.xyz
 * @description 用户类
 * @date 2019/8/12 17:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class Users {

    /**
     * users主键
     */
    @AutoIncKey
    @Id
    private int id;

    /**
     * 用户账号
     */
    private String uAccount;

    /**
     * 用户密码
     */
    private String uPwd;

    public Users(String uAccount,String uPwd){
        this.uAccount = uAccount;
        this.uPwd = uPwd;
    }
}