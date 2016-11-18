package com.example.administrator.myapplication09;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;

import javax.mail.internet.MimeMessage;

/**
 * Created by Administrator on 2016/11/15.
 */
public class SendMail extends AppCompatActivity implements View.OnClickListener {
    private EditText ed_to;
    private EditText ed_subject;
    private EditText ed_text;
    private Button bt_send;
    private Button bt_return;
    //private Session session;
    private MimeMessage message;
    private String to;
    private String text;
    private String smtpServer = "smtp.163.com";
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            if(msg.what==1){
                Toast.makeText(SendMail.this, "邮件发送成功!", Toast.LENGTH_SHORT).show();
            }else {
                //   finish();
            }
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendmail);
        init();
        message = new MimeMessage(MySession.session);



        bt_send.setOnClickListener(this);
        bt_return.setOnClickListener(this);
        ed_to.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                to = ed_to.getText().toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        ed_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text = ed_text.getText().toString().trim();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void init() {
        ed_to = (EditText) findViewById(R.id.ed_to);
        ed_subject = (EditText) findViewById(R.id.ed_subject);
        ed_text = (EditText) findViewById(R.id.ed_text);
        bt_send = (Button) findViewById(R.id.bt_send);
        bt_return = (Button) findViewById(R.id.bt_return);

        ed_to.setText("");
        ed_subject.setText("");
        ed_text.setText("");

    }

    @Override
    public void onClick(View v) {
        if (v == findViewById(R.id.bt_send)) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        Socket socket = null;
                        InputStream in = null;
                        OutputStream out = null;
                        socket = new Socket("smtp.163.com", 25);
                        socket.setSoTimeout(3000);
                        in = socket.getInputStream();
                        out = socket.getOutputStream();
                        BufferedReaderProxy reader = new BufferedReaderProxy(new InputStreamReader(in), false);
                        PrintWriterProxy writer = new PrintWriterProxy(out, true);

                        reader.showResponse();
                        writer.println("HELO " + smtpServer);
                        reader.showResponse();
                        writer.println("AUTH LOGIN");
                        reader.showResponse();
                        //   writer.println(new String(Base64.encodeToString(MySession.account.getBytes(),Base64.NO_WRAP)));
                        writer.println(Base64.encodeToString(MySession.account.getBytes(), Base64.NO_WRAP));
                        reader.showResponse();
                        writer.println(Base64.encodeToString(MySession.pwd.getBytes(), Base64.NO_WRAP));
                        reader.showResponse();
                        writer.println("MAIL FROM:<" + MySession.account + ">");
                        reader.showResponse();
                        writer.println("RCPT TO:<" + ed_to.getText().toString() + ">");
                        reader.showResponse();

                        writer.println("DATA");
                        reader.showResponse();

                        writer.println("From:" + MySession.account);//+"m"
                        writer.print("To:");
                        writer.println(ed_to.getText().toString().trim());


                        if (ed_subject.getText().toString() != null) {
                            writer.println("Subject:" + ed_subject.getText().toString());
                        }

                        writer.println();

                        //  writer.println(text);//("\r\t"+Content).getBytes("UTF-8")
                        writer.println(text);
                        writer.println(".");

                        reader.showResponse();
                        writer.println("QUIT");
                        reader.showResponse();
                        socket.close();
                        Message message = new Message();
                        message.what = 1;
                        handler.sendMessage(message);
                    } catch (IOException e) {
                        Toast.makeText(SendMail.this, "发送失败!请检查收件人地址是否有误", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }.start();
        } else if (v == findViewById(R.id.bt_return)) {
            Intent intent = new Intent(SendMail.this, MainActivity.class);
            startActivity(intent);
        }
    }

    static class PrintWriterProxy extends PrintWriter {
        private boolean showRequest;

        public PrintWriterProxy(OutputStream out, boolean showRequest) {
            super(out, true);
            this.showRequest = showRequest;
        }

        @Override
        public void println() {
            if (showRequest)
                System.out.println();
            super.println();
        }

        public void print(String s) {
            if (showRequest)
                System.out.print(s);
            super.print(s);
        }
    }

    static class BufferedReaderProxy extends BufferedReader {
        private boolean showResponse = true;

        public BufferedReaderProxy(Reader in, boolean showResponse) {
            super(in);
            this.showResponse = showResponse;
        }

        public void showResponse() {
            try {
                String line = readLine();
                Log.i("dg", line);
                String number = line.substring(0, 3);
                int num = -1;
                try {
                    num = Integer.parseInt(number);
                } catch (Exception e) {
                }
                if (num == -1) {
                    throw new RuntimeException("响应信息错误 : " + line);
                } else if (num >= 1000) {
                    throw new RuntimeException("发送邮件失败 : " + line);
                }
                if (showResponse) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println("获取响应失败");
            }
        }

    }
}
