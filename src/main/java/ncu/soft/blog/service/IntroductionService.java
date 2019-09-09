package ncu.soft.blog.service;

import ncu.soft.blog.entity.Introduction;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/6 11:30
 */
public interface IntroductionService {

    /**
     * 将个人简介存入
     * @param introduction Introduction
     * @return 存入后的Introduction
     */
    Introduction save(Introduction introduction);

    /**
     * 更新个人简介
     * @param introduction  Introduction
     * @return 更新后的Introduction
     */
    Introduction update(Introduction introduction);

    /**
     * 获取个人简介
     * @param uid 用户id
     * @return Introduction
     */
    Introduction findByUid(int uid);
}