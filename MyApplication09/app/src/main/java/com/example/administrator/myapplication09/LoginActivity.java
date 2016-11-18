package com.example.administrator.myapplication09;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/11/9.
 */
public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText ed_account;//账号
    private EditText ed_pwd;//密码
    private Button btn_login;//登录按钮;
    private ProgressDialog dialog;
    private SharedPreferences sp;
    private CheckBox cb_remember;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(MySession.session==null){
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, "账号或密码错误!", Toast.LENGTH_SHORT).show();
            }else {
                dialog.dismiss();
                Intent intent = new Intent((LoginActivity.this), MainActivity.class);
                startActivity(intent);
             //   finish();
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sp=getSharedPreferences("config", Context.MODE_APPEND);

        initView();
        isRemenberPwd();
        btn_login.setOnClickListener(this);
    }

    private void initView(){
        ed_account = (EditText) findViewById(R.id.ed_account);
        ed_pwd = (EditText) findViewById(R.id.ed_pwd);

        btn_login = (Button) findViewById(R.id.btn_login);

        cb_remember = (CheckBox) findViewById(R.id.cb_remember);

        cb_remember.setOnClickListener(this);
    }

    private void loginEmail(){
        MySession.account = ed_account.getText().toString().trim();
        MySession.pwd = ed_pwd.getText().toString().trim();
        if(TextUtils.isEmpty(MySession.account)){
            Toast.makeText(LoginActivity.this,"地址不能为空!",Toast.LENGTH_LONG).show();
            return;
        }else{
            if(TextUtils.isEmpty(MySession.pwd)){
                Toast.makeText(LoginActivity.this,"密码不能为空!",Toast.LENGTH_LONG).show();
                return;
            }
        }

        //进度条
        dialog = new ProgressDialog(LoginActivity.this);
        dialog.setMessage("正在登入,请稍后");
        dialog.show();

        new Thread(){
            @Override
            public void run() {
                //登入操作
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                HttpUtil util=new HttpUtil(MySession.account,MySession.pwd);
                MySession.session=util.login();
                Message message=handler.obtainMessage();
                message.sendToTarget();
            }
        }.start();
//        Message message=handler.obtainMessage();
//        message.sendToTarget();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_login:
                loginEmail();
                break;
            case R.id.cb_remember:
                remenberPwd();
                break;
        }
    }

    private void isRemenberPwd(){
        boolean isRbPwd=sp.getBoolean("isRbPwd", false);
        if(isRbPwd){
            String addr=sp.getString("address", "");
            String pwd=sp.getString("password", "");
            ed_account.setText(addr);
            ed_pwd.setText(pwd);
            cb_remember.setChecked(true);
        }
    }

    /**
     * 记住密码
     */
    private void remenberPwd(){
        boolean isRbPwd=sp.getBoolean("isRbPwd", false);
        if(isRbPwd){
            sp.edit().putBoolean("isRbPwd", false).commit();
            cb_remember.setChecked(false);
        }else{
            sp.edit().putBoolean("isRbPwd", true).commit();
            sp.edit().putString("address", ed_account.getText().toString().trim()).commit();
            sp.edit().putString("password", ed_pwd.getText().toString().trim()).commit();
            cb_remember.setChecked(true);

        }
    }

}
