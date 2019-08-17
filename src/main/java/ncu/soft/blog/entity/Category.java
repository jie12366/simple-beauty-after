package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/13 14:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "category")
public class Category {

    /**
     * category的主键
     */
    @Id
    private int id;

    /**
     * 分类的名称
     */
    private String name;
}