package ncu.soft.blog.entity;

import lombok.Data;

/**
 * @author monkJay
 * @description 封装文章类和用户信息类
 * @date 2019/12/25 20:20
 */
@Data
public class ArticleAndUserVo {

    /**
     * 文章总页数
     */
    private Integer total;

    /**
     * 文章
     */
    private Article article;

    /**
     * 文章详情
     */
    private ArticleDetail articleDetail;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户侧边背景
     */
    private String sideBackground;

    /**
     * 用户信息
     */
    private UsersInfo usersInfo;
}