package ncu.soft.blog.utils;

import lombok.Data;

/**
 * 自定义响应数据结构
 * 门户接受此类数据后需要使用本类的方法转换成对于的数据类型格式（类，或者list）
 * 200：表示成功
 * 401：未登录
 * 403：客户端token过期
 * 500：其他错误，错误信息在msg字段中
 * @author www.xyj123.xyz
 * @date 2019/3/30 13:57
 */
@Data
public class JsonResult {

    /**
     * 响应业务状态
     */
    private Integer status;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应中的数据
     */
    private Object data;

    /**
     * 带数据成功返回
     * @param data 返回的数据
     * @return new JsonResult(data)
     */
    public static JsonResult ok(Object data) {
        return new JsonResult(data);
    }

    /**
     * 无参返回成功，OK
     * @return new JsonResult(msg)
     */
    public static JsonResult ok() {
        Object msg = "OK";
        return new JsonResult(msg);
    }

    /**
     * 用户未登录
     * @param msg 返回的错误信息
     * @return new JsonResult(401,msg)
     */
    public static JsonResult errorNotLogin(String msg) {
        return new JsonResult(401,msg);
    }

    /**
     * token过期
     * @param msg 错误信息
     * @return new JsonResult(403,msg)
     */
    public static JsonResult errorTokenExpired(String msg) {
        return new JsonResult(403,msg);
    }

    /**
     * 其他错误
     * @param msg 要返回的错误信息
     * @return new JsonResult(500,msg)
     */
    public static JsonResult errorMsg(String msg) {
        return new JsonResult(500,msg);
    }

    private JsonResult(Object data) {
        this.status = 200;
        this.msg = "OK";
        this.data = data;
    }

    private JsonResult(int status,String msg) {
        this.status = status;
        this.msg = msg;
        this.data = null;
    }
}