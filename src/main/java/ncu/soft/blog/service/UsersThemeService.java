package ncu.soft.blog.service;

import ncu.soft.blog.entity.UsersTheme;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/28 19:13
 */
public interface UsersThemeService {

    /**
     * 保存默认主题
     * @param usersTheme 主题对象
     * @return UsersTheme
     */
    UsersTheme save(UsersTheme usersTheme);

    /**
     * 更新主页背景
     * @param index 背景图
     * @param uid 用户id
     * @return UsersTheme
     */
    UsersTheme updateIndex(String index, String uid);

    /**
     * 更改侧边背景
     * @param side 侧边图
     * @param uid 用户id
     * @return UsersTheme
     */
    UsersTheme updateSide(String side, String uid);

    /**
     * 更改代码样式
     * @param style 代码样式
     * @param uid 用户id
     * @return UsersTheme
     */
    UsersTheme updateStyle(String style, String uid);

    /**
     * 获取用户主题
     * @param uid 用户id
     * @return UsersTheme
     */
    UsersTheme getTheme(String uid);

    /**
     * 还原设置
     * @param uid 用户id
     */
    UsersTheme revert(String uid);
}