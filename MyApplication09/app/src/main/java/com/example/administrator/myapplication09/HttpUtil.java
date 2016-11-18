package com.example.administrator.myapplication09;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;

/**
 * Created by Administrator on 2016/11/16.
 */
public class HttpUtil {
    public String account,pwd;
    public HttpUtil(String account,String pwd){
        this.account = account;
        this.pwd = pwd;
    }
    public Session login(){
        //连接服务器
        Session session=isLoginRight(account,pwd);
        return session;
    }
    public Session isLoginRight(String account,String pwd){
        //判断是否要登入验证
        String host="smtp.163.com";
        MyAuthenticator authenticator=null;
        authenticator=new MyAuthenticator(account,pwd);
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Properties props=System.getProperties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp,auth", true);
        Session sendMailSession=Session.getDefaultInstance(props, authenticator);
        try {
            Transport transport=sendMailSession.getTransport("smtp");
            transport.connect("smtp.163.com",account,pwd);
        } catch (MessagingException e) {
            e.printStackTrace();
            return null;
        }
        return sendMailSession;
    }
}
