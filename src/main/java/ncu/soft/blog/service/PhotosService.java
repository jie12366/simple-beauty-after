package ncu.soft.blog.service;

import ncu.soft.blog.entity.Photo;
import ncu.soft.blog.entity.Photos;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/3 16:22
 */
public interface PhotosService {

    /**
     * 将照片集存入
     * @param photo 照片对象
     * @param uid 用户id
     * @return Photos
     */
    Photos save(Photo photo,int uid);

    /**
     * 更新照片集
     * @param photo 照片对象
     * @param uid 用户id
     */
    Photos addPhoto(Photo photo,int uid);

    /**
     * 判断某个图片路径是否已存在
     * @param url 图片路径
     * @return Photos
     */
    boolean photoIsExist(String url,int uid);

    /**
     * 获取照片集
     * @param uid 用户id
     * @return Photos
     */
    Photos getPhotos(int uid);

    /**
     * 删除照片
     * @param uid 用户id
     * @param photo 照片对象
     */
    Photos deletePhoto(int uid,Photo photo);
}