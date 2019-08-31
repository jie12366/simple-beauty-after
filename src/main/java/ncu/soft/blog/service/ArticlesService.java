package ncu.soft.blog.service;

import ncu.soft.blog.entity.Article;
import ncu.soft.blog.entity.ArticleDetail;
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
    Article save(Article article,String contentHtml);

    /**
     * 分页查询所有数据
     * @param pageIndex 当前页
     * @param pageSize 每页大小
     * @return PageImpl
     */
    PageImpl<Article> getArticlesByPage(int pageIndex,int pageSize);

    /**
     * 根据用户id分页查询数据
     * @param pageIndex 当前页
     * @param pageSize 每页大小
     * @param uid 用户id
     * @return PageImpl<Article>
     */
    PageImpl<Article> getArticlesByUidByPage(int pageIndex,int pageSize,int uid);

    /**
     * 根据aid获取文章数据
     * @param aid 文章id
     * @return Article
     */
    Article getArticle(int aid);

    /**
     * 阅读量+1
     * @return 更新后的对象
     */
    Article updateReads(int aid);
}