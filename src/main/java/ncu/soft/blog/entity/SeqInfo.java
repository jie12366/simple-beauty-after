package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author www.xyjz123.xyz
 * @description 存储每个集合的ID记录
 * @date 2019/8/12 18:08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "sequence")
public class SeqInfo {

    @Id
    /**
     * 主键
     */
    private String id;

    /**
     * 序列值
     */
    private Integer seqId;

    /**
     * 集合名称
     */
    private String collName;
}