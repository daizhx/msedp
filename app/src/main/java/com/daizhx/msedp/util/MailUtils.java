package com.daizhx.msedp.util;

import java.util.Date;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class MailUtils {

    /**
     * 创建一封只包含文本的简单邮件
     *
     * @param session 和服务器交互的会话
     * @param sendMail 发件人邮箱
     * @param receiveMail 收件人邮箱
     * @return
     * @throws Exception
     */
    public static MimeMessage createMimeMessage(Session session, String sendMail, String receiveMail) throws Exception {
        // 1. 创建一封邮件
        MimeMessage message = new MimeMessage(session);
        // 2. From: 发件人
        message.setFrom(new InternetAddress(sendMail, "test", "UTF-8"));
        // 3. To: 收件人（可以增加多个收件人、抄送、密送）
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiveMail, "亲爱的开发者", "UTF-8"));
        // 4. Subject: 邮件主题
        message.setSubject("客户端错误信息", "UTF-8");
        // 5. Content: 邮件正文（可以使用html标签）
        message.setContent("这是一条测试邮件", "text/html;charset=UTF-8");
        // 6. 设置发件时间
        message.setSentDate(new Date());
        // 7. 保存设置
        message.saveChanges();
        return message;
    }


    private void sendMail() throws Exception{
        // 1. 创建参数配置, 用于连接邮件服务器的参数配置
        Properties props = new Properties();          // 参数配置
        props.setProperty("mail.transport.protocol", "smtp");  // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", "smtp.qq.com");   // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");      // 需要请求认证
        //如果遇到ssl类错误，请打开一下代码
    /*final String smtpPort = "465";
    props.setProperty("mail.smtp.port", smtpPort);
    props.setProperty("mail.smtp.socketFactory.class", "javax.NET.ssl.SSLSocketFactory");
    props.setProperty("mail.smtp.socketFactory.fallback", "false");
    props.setProperty("mail.smtp.socketFactory.port", smtpPort);*/
        // 2. 根据配置创建会话对象, 用于和邮件服务器交互
        Session session = Session.getDefaultInstance(props);
        // 设置为debug模式, 可以查看详细的发送 log
        session.setDebug(true);
        // 3. 创建一封邮件
        MimeMessage message = MailUtils.createMimeMessage(session, "adabcd1234@qq.com", "357137634@qq.com");//我这里是以163邮箱为发信邮箱测试通过
        // 4. 根据 Session 获取邮件传输对象
        Transport transport = session.getTransport();
        transport.connect("adabcd1234@qq.com", "abcd1234");
        // 6. 发送邮件, 发到所有的收件地址, message.getAllRecipients() 获取到的是在创建邮件对象时添加的所有收件人, 抄送人, 密送人
        transport.sendMessage(message, message.getAllRecipients());
        // 7. 关闭连接
        transport.close();
    }
}
