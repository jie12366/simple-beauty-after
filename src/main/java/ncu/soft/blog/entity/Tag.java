package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ncu.soft.blog.selfAnnotation.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/13 16:25
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tag")
public class Tag {

    /**
     * 主键
     */
    @AutoIncKey
    @Id
    private int id;

    /**
     * 标签名
     */
    private String name;
}