package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ncu.soft.blog.selfannotation.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/6 11:17
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Introduction {

    @AutoIncKey
    @Id
    private int id;

    /**
     * 用户id
     */
    @Indexed
    private String uid;

    /**
     * 姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * github
     */
    private String github;

    /**
     * QQ
     */
    private String qq;

    /**
     * 个人简介
     */
    private String introduction;
}