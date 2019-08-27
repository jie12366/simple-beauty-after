package ncu.soft.blog.service;

import ncu.soft.blog.entity.Article;
import org.springframework.data.domain.PageImpl;

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

    /**
     * 分页查询所有数据
     * @param pageIndex 当前页
     * @param pageSize 每页大小
     * @return PageImpl
     */
    PageImpl<Article> getArticlesByPage(int pageIndex,int pageSize);
}