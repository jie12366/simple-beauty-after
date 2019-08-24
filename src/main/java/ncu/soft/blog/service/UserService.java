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

    /**
     * 根据uaccount查找users
     * @param account 指定得账号
     * @return Users
     */
    Users findByAccount(String account);

    /**
     * 检查账号是否存在
     * @param account 账号
     * @return 是否存在
     */
    boolean isExist(String account);

    /**
     * 保存users到数据库
     * @param users Users
     * @return 返回user信息
     */
    Users save(Users users);

    /**
     * 验证用户的账号密码是否匹配
     * @param users Users
     * @return 是否匹配
     */
    boolean verifyUser(Users users);
}
