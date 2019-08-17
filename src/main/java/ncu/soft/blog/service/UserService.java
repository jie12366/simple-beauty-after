package ncu.soft.blog.service;

import ncu.soft.blog.entity.Users;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/16 21:32
 */
public interface UserService {

    /**
     * 根据id查找users
     * @param id 指定的uid
     * @return Users
     */
    Users findById(int id);
}
