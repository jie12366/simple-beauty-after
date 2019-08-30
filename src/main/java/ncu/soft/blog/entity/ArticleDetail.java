package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ncu.soft.blog.selfAnnotation.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/13 16:34
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "article_detail")
public class ArticleDetail implements Serializable {

    private static final long serialVersionUID = 8948260579774224506L;
    /**
     * 主键
     */
    @AutoIncKey
    @Id
    private int id;

    /**
     * 文章id
     */
    @Indexed
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