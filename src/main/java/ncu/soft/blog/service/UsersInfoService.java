package ncu.soft.blog.service;

import ncu.soft.blog.entity.UsersInfo;

/**
 * @author www.xyjz123.xyz
 * @description 用户信息的业务层
 * @date 2019/8/24 13:19
 */
public interface UsersInfoService {

    /**
     * 根据id获取用户信息
     * @param uid 用户id
     * @return UsersInfo
     */
    UsersInfo findByUid(int uid);

    /**
     * 将用户信息插入数据库
     * @param usersInfo 用户信息
     */
    UsersInfo save(UsersInfo usersInfo);

    /**
     * 更换用户头像
     * @param headPath 头像路径
     * @param uid 用户id
     * @return 更新后的用户数据
     */
    UsersInfo updateHeadPath(String headPath,int uid);

    /**
     * 给指定的用户绑定邮箱
     * @param email 要绑定的邮箱
     * @param uid 用户id
     */
    UsersInfo bindEmail(String email,int uid);

    /**
     * 检查昵称是否被用过
     * @param nickname 昵称
     * @return 是否被用过
     */
    UsersInfo checkNickname(String nickname);

    /**
     * 更新用户昵称和个人简介
     * @param nickname 昵称
     * @param introduction  个人简介
     * @param uid 用户id
     */
    UsersInfo updateInfo(String nickname,String introduction,int uid);

    /**
     * 按指定的数字更新文章数
     * @param articles 文章数(1或-1)
     * @param uid 用户id
     */
    void updateArticles(int articles,int uid);
}