package ncu.soft.blog.service.impl;

import ncu.soft.blog.entity.Photo;
import ncu.soft.blog.entity.Photos;
import ncu.soft.blog.service.PhotosService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/3 16:23
 */
@Service
@CacheConfig(cacheNames = "photoService")
public class PhotosServiceImpl implements PhotosService {

    @Resource(name = "mongoTemplate")
    MongoTemplate mongoTemplate;

    @Override
    @CachePut(key = "#uid")
    public Photos save(Photo photo,int uid) {
        List<Photo> photos1 = new ArrayList<>();
        photos1.add(photo);
        Photos photos = new Photos(uid,photos1);
        return mongoTemplate.insert(photos);
    }

    @Override
    @CachePut(key = "#uid")
    public Photos addPhoto(Photo photo,int uid) {
        Photos photos = getPhotos(uid);
        List<Photo> photos1 = photos.getPhotos();
        photos1.add(photo);
        return updatePhotos(photos,photos1,uid);
    }

    @Override
    public boolean photoIsExist(String url,int uid) {
        return mongoTemplate.exists(new Query(Criteria.where("uid").is(uid).and("photos").elemMatch(Criteria.where("url").is(url))),Photos.class);
    }

    @Override
    @CacheEvict(key = "#uid")
    public Photos getPhotos(int uid) {
        return mongoTemplate.findOne(new Query(Criteria.where("uid").is(uid)),Photos.class);
    }

    @Override
    public Photos deletePhoto(int uid, Photo photo) {
        Photos photos = getPhotos(uid);
        List<Photo> photos2 = photos.getPhotos();
        photos2.remove(photo);
        return updatePhotos(photos,photos2,uid);
    }

    /**
     * 更新照片集
     * @param photos Photos
     * @param photos2 Photo
     * @param uid 用户id
     * @return 更新后的Photos
     */
    Photos updatePhotos(Photos photos,List<Photo> photos2,int uid){
        photos.setPhotos(photos2);
        Query query = new Query(new Criteria("uid").is(uid));
        Update update = Update.update("photos",photos2);
        FindAndModifyOptions options = new FindAndModifyOptions().returnNew(true);
        return mongoTemplate.findAndModify(query,update,options,Photos.class);
    }
}