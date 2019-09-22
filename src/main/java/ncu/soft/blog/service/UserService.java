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
    Users verifyUser(Users users);

    /**
     * 重置密码
     * @param email 绑定的邮箱
     * @param pwd 新密码
     * @return Users
     */
    Users updatePwd(String email,String pwd);
}
