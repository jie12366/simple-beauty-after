package ncu.soft.blog.utils;

import lombok.Data;

/**
 * 自定义响应数据结构
 * 门户接受此类数据后需要使用本类的方法转换成对于的数据类型格式（类，或者list）
 * 200：表示成功
 * 401：未登录
 * 403：客户端token过期
 * 500：服务器错误，错误信息在msg字段中
 * @author www.xyj123.xyz
 * @date 2019/3/30 13:57
 */
@Data
public class JsonResult {

    /**
     * 响应业务状态
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应中的数据
     */
    private Object data;

    public JsonResult() {}

    public JsonResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static JsonResult success() {
        JsonResult result = new JsonResult();
        result.setResultCode(ResultCode.SUCCESS);
        return result;
    }

    public static JsonResult success(Object data) {
        JsonResult result = new JsonResult();
        result.setResultCode(ResultCode.SUCCESS);
        result.setData(data);
        return result;
    }

    public static JsonResult failure(ResultCode resultCode) {
        JsonResult result = new JsonResult();
        result.setResultCode(resultCode);
        return result;
    }

    public static JsonResult failure(ResultCode resultCode, Object data) {
        JsonResult result = new JsonResult();
        result.setResultCode(resultCode);
        result.setData(data);
        return result;
    }

    private void setResultCode(ResultCode code) {
        this.code = code.code();
        this.msg = code.message();
    }
}