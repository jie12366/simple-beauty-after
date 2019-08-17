package ncu.soft.blog.utils;

import java.security.MessageDigest;

/**
 * @author 熊义杰
 * @date 2019-3-16
 */

public class GetString {

    /**
     * 将传入的字符串进行MD5加密
     *
     * @param key 传入的字符串
     * @return 加密后的16进制的md5串
     */
    public static String getMd5(String key) {
        char[] hexDigits = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'
        };
        try {
            byte[] btInput = key.getBytes();
            // 获得MD5摘要算法的 MessageDigest 对象
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            // 使用指定的字节更新摘要
            mdInst.update(btInput);
            // 获得密文
            byte[] md = mdInst.digest();
            // 把密文转换成十六进制的字符串形式
            int j = md.length;
            char[] str = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getCode() {
        String code = (int) ((Math.random() * 9 + 1) * 100000) + "";
        return code;
    }
}
