package ncu.soft.blog.selfAnnotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author www.xyjz123.xyz
 * @description 标识需要自增的主键ID
 * @date 2019/8/12 18:13
 */
@Target(ElementType.FIELD) //作用范围是方法
@Retention(RetentionPolicy.RUNTIME) //在运行时起作用
public @interface AutoIncKey {

}