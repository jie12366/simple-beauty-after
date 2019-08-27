package ncu.soft.blog.service;

import ncu.soft.blog.entity.MyTag;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/26 20:15
 */
public interface TagService {

    /**
     * 根据uid获取数据
     * @param uid 用户id
     * @return MyTag
     */
    MyTag findByUid(int uid);

    /**
     * 保存数据
     * @param  myTag 数据
     * @return MyTag
     */
    MyTag save(MyTag myTag);

    /**
     * 更新数据
     * @param myTag MyTag
     */
    void update(MyTag myTag);
}