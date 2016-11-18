package com.example.administrator.myapplication09;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView tv_user;
    private Button ed_sendmail;
    private  String account;
    public  List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private ListView lv;

    private Handler handler;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_user = (TextView) findViewById(R.id.tv_user);
        tv_user.setText(account);

        ed_sendmail = (Button) findViewById(R.id.bt_sendmail);
        ed_sendmail.setOnClickListener(this);

        lv = (ListView) findViewById(R.id.lv_mail);

        new Thread(){
            @Override
            public void run() {
                String protocol="pop3";
                boolean isSSL=true;
                String host="pop.163.com";
                int port=995;
//                String username="18066019353@163.com";
//                String password="951001ai";
                Properties props=new Properties();
                props.put("mail.pop3.ssl.enable", isSSL);
                props.put("mail.pop3.host",host);
                props.put("mail.pop3.port", port);

               //   Session session= Session.getDefaultInstance(props);
                Session session = Session.getInstance(props);
                Store store=null;
                Folder folder=null;
                try {
                    store=session.getStore(protocol);
                    store.connect(MySession.account,MySession.pwd);
                    folder=store.getFolder("INBOX");
                    folder.open(Folder.READ_ONLY);
                    Message message[]=folder.getMessages();
//                    Log.i("dg", String.valueOf(message.length));
                    PraseMimeMessage pmm=null;

                    for (int i = 0; i < message.length; i++){
                        Map<String, Object> map = new HashMap<String, Object>();
                        pmm=new PraseMimeMessage((MimeMessage)message[i]);
                        pmm.setDateFormat("yy/MM/dd ");
                        map.put("logo", R.drawable.ic_15);
                        map.put("from", pmm.getFrom1());
                        map.put("subject",pmm.getSubject());
                        map.put("text",pmm.getBodyText());
                        map.put("date",pmm.getSendDate());
                       // Log.i("dg",pmm.getSendDate());
                        list.add(map);
                    }
                }  catch (Exception e) {
                    Log.i("dg","sb");
                    e.printStackTrace();
                }
                android.os.Message message1=handler.obtainMessage();
                message1.what=1;
                message1.sendToTarget();
            }
        }.start();

        handler=new Handler(){
            @Override
            public void handleMessage(android.os.Message msg) {
                if(msg.what==1){
                //    Toast.makeText(MainActivity.this,list.get(0).get("from").toString(), Toast.LENGTH_SHORT).show();
                    MyAdapter adapter = new MyAdapter(MainActivity.this);
                    adapter.setList(list);
                    //关联适配器
                    lv.setAdapter(adapter);
                }else {
                    //   finish();
                }
                super.handleMessage(msg);
            }
        };

//        MyAdapter adapter = new MyAdapter(this);
//        adapter.setList(list);
//
//        //关联适配器
//        lv.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        if (v == findViewById(R.id.bt_sendmail)) {
            Intent intent = new Intent(MainActivity.this, SendMail.class);
            intent.putExtra("account", account);
            startActivity(intent);
        }
    }
}


