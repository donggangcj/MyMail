package com.example.administrator.myapplication09;

import javax.mail.PasswordAuthentication;

/**
 * Created by Administrator on 2016/11/9.
 */
public class MyAuthenticator extends javax.mail.Authenticator {
    private String strUser;
    private String strPwd;
    public MyAuthenticator(String user, String password) {
        this.strUser = user;
        this.strPwd = password;
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(strUser, strPwd);
    }
}

