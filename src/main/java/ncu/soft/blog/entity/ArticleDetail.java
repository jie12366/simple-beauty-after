package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ncu.soft.blog.selfannotation.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

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

    /**
     * 文章目录
     */
    private List<Map<String ,String >> directory;

    public ArticleDetail(int aid, String contentHtml, String contentMd) {
        this.aid = aid;
        this.contentHtml = contentHtml;
        this.contentMd = contentMd;
    }
}