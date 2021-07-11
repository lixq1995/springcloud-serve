package com.test.mail;

import com.test.mail.service.impl.MailTestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.MessagingException;

/**
 * @author by Lixq
 * @Classname MailTest
 * @Description TODO
 * @Date 2021/5/16 16:15
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MailTest {

    @Autowired
    private MailTestService mailService;

    @Resource
    private TemplateEngine templateEngine;

    @Test
    public void sendSimpleMail() {
        mailService.sendSimpleMail("lixq1995@163.com","测试spring boot imail-主题","测试spring boot imail - 内容");
    }

    @Test
    public void sendHtmlMail() throws MessagingException {
        String content = "<html>\n" +
                "<body>\n" +
                "<h3>hello world</h3>\n" +
                "<h1>html</h1>\n" +
                "<body>\n" +
                "</html>\n";

        mailService.sendHtmlMail("lixq1995@163.com","这是一封HTML邮件",content);
    }

    @Test
    public void sendAttachmentsMail() throws MessagingException {
        String filePath = "E:\\ideaTool\\test.doc";
        String content = "<html>\n" +
                "<body>\n" +
                "<h3>hello world</h3>\n" +
                "<h1>html</h1>\n" +
                "<h1>附件传输</h1>\n" +
                "<body>\n" +
                "</html>\n";
        mailService.sendAttachmentsMail("lixq1995@163.com","这是一封HTML附件邮件",content, filePath);
    }

    @Test
    public void sendInlinkResourceMail() throws MessagingException {
        String imgPath = "E:\\ideaTool\\testImg.jpg";
        String rscId = "imgUrl";
        String content = "<html>" +
                "<body>" +
                "<h3>hello world</h3>" +
                "<h1>html</h1>" +
                "<h1>图片邮件</h1>" +
                "<img src='cid:"+rscId+"'></img>" +
                "<body>" +
                "</html>";

        mailService.sendInlinkResourceMail("lixq1995@163.com","这是一封图片邮件",content, imgPath, rscId);
    }

    @Test
    public void testTemplateMailTest() throws MessagingException {
        Context context = new Context();
        context.setVariable("id","hi,lixq");

        String emailContent = templateEngine.process("emailTeplate", context);
        mailService.sendHtmlMail("lixq1995@163.com","这是一封HTML模板邮件",emailContent);

    }
}
