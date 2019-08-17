package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ncu.soft.blog.selfAnnotation.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/13 16:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "article_detail")
public class ArticleDetail {

    /**
     * 主键
     */
    @AutoIncKey
    @Id
    private int id;

    /**
     * 文章id
     */
    private int aid;

    /**
     * 文章内容的html形式
     */
    @Field("cHtml")
    private String contentHtml;

    /**
     * 文章内容的md形式
     */
    @Field("cMd")
    private String contentMd;
}