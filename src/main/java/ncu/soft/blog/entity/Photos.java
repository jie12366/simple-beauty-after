package ncu.soft.blog.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ncu.soft.blog.selfannotation.AutoIncKey;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.List;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/9/3 16:15
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Document(collection = "photos")
public class Photos implements Serializable {

    private static final long serialVersionUID = -6252760086511519057L;

    @AutoIncKey
    @Id
    private int id;

    /**
     * 用户id
     */
    @Indexed
    private String uid;

    /**
     * 用户照片集
     */
    private List<Photo> photos;

    public Photos(String uid, List<Photo> photos) {
        this.uid = uid;
        this.photos = photos;
    }
}