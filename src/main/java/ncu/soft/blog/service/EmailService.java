package ncu.soft.blog.service;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/24 15:22
 */
public interface EmailService {

    /**
     * 发送html邮件
     * @param toEmail 接受者
     * @param subject 邮件主题
     * @param html 邮件内容
     */
    void sendHtmlEmail(String toEmail,String subject,String html);
}