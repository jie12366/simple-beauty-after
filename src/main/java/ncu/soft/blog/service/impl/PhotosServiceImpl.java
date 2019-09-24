package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.Photo;
import ncu.soft.blog.entity.Photos;
import ncu.soft.blog.service.PhotosService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/3 16:23
 */
@Service
public class PhotosServiceImpl implements PhotosService {

    @Resource(name = "mongoTemplate")
    MongoTemplate mongoTemplate;

    @Override
    public Photos save(Photo photo,String uid) {
        List<Photo> photos1 = new ArrayList<>();
        photos1.add(photo);
        Photos photos = new Photos(uid,photos1);
        return mongoTemplate.insert(photos);
    }

    @Override
    public Photos addPhoto(Photo photo,String uid) {
        Photos photos = getPhotos(uid);
        List<Photo> photos1 = photos.getPhotos();
        photos1.add(photo);
        return updatePhotos(photos,photos1,uid);
    }

    @Override
    public boolean photoIsExist(String url,String uid) {
        return mongoTemplate.exists(new Query(Criteria.where("uid").is(uid).and("photos").elemMatch(Criteria.where("url").is(url))),Photos.class);
    }

    @Override
    public Photos getPhotos(String uid) {
        Query query = new Query(Criteria.where("uid").is(uid));
        query.with(new Sort(Sort.Direction.DESC,"uTime"));
        return mongoTemplate.findOne(query,Photos.class);
    }

    @Override
    public Photos deletePhoto(String uid, String  url) {
        Query query = new Query(Criteria.where("uid").is(uid));
        // 构造一个url键值对，用于删除操作
        Map<String ,String> photo = new HashMap<>(2);
        photo.put("url",url);
        // 删掉内嵌文档中与photo对应的对象
        Update update = new Update().pull("photos",photo);
        return mongoTemplate.findAndModify(query,update,new FindAndModifyOptions().returnNew(true),Photos.class);
    }

    /**
     * 更新照片集
     * @param photos Photos
     * @param photos2 Photo
     * @param uid 用户id
     * @return 更新后的Photos
     */
    private Photos updatePhotos(Photos photos,List<Photo> photos2,String uid){
        photos.setPhotos(photos2);
        Query query = new Query(new Criteria("uid").is(uid));
        Update update = Update.update("photos",photos2);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return mongoTemplate.findAndModify(query,update,options,Photos.class);
    }
}