package ncu.soft.blog.service.impl;

import ncu.soft.blog.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author www.xyjz123.xyz
 * @description
 * @date 2019/8/24 15:23
 */
@Service
public class EmailServiceImpl implements EmailService {

    @Resource
    JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    String fromEmail;

    private final static Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    @Override
    public void sendHtmlEmail(String toEmail, String subject, String html) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,true);
            mimeMessageHelper.setTo(toEmail);
            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setSubject(subject);
            mimeMessageHelper.setText(html,true);
            log.info("发送邮件成功");
        } catch (MessagingException e) {
            log.error("发送邮件异常");
            e.printStackTrace();
        }
        javaMailSender.send(message);
    }
}