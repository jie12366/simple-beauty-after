package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.Users;
import ncu.soft.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/16 21:32
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Users findById(int id){
        return mongoTemplate.findById(id,Users.class);
    }
}