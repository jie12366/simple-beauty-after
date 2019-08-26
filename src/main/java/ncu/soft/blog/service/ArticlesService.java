package ncu.soft.blog.service;

import ncu.soft.blog.entity.Article;

/**
 * @author www.xyjz123.xyz
 * @description 文章管理业务层
 * @date 2019/8/25 19:47
 */
public interface ArticlesService {

    /**
     * 保存文章信息
     * @param article Article
     * @return 返回存入的对象
     */
    Article save(Article article);
}