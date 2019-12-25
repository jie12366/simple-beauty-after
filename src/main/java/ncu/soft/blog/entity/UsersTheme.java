package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ncu.soft.blog.selfannotation.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/28 19:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users_theme")
public class UsersTheme {

    @AutoIncKey
    @Id
    private int id;

    /**
     * 用户id
     */
    @Indexed
    private String uid;

    /**
     * 主页背景
     */
    @Field("index")
    private String indexBackground;

    /**
     * 侧边背景
     */
    @Field("side")
    private String sideBackground;

    /**
     * 代码样式
     */
    private String style;

    public UsersTheme(String uid, String indexBackground, String sideBackground, String style) {
        this.uid = uid;
        this.indexBackground = indexBackground;
        this.sideBackground = sideBackground;
        this.style = style;
    }
}