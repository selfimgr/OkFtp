package com.wuchaowen.okftp;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wuchaowen.okftp.permission.KbPermission;
import com.wuchaowen.okftp.permission.KbPermissionListener;
import com.wuchaowen.okftp.permission.KbPermissionUtils;
import com.wuchaowen.util.okftp.OkFtp;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    private EditText ip,username,password,spath,dest;
    private ProgressDialog progressDialog;
    private Button downBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ip = (EditText)findViewById(R.id.address);
        username = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        spath = (EditText)findViewById(R.id.server_pth);
        dest = (EditText)findViewById(R.id.dest);
        downBtn = (Button)findViewById(R.id.down);

        if (KbPermissionUtils.needRequestPermission()) {
            KbPermission.with(this)
                    .requestCode(100)
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .callBack(new KbPermissionListener() {
                        @Override
                        public void onPermit(int requestCode, String... permission) {

                        }

                        @Override
                        public void onCancel(int requestCode, String... permission) {
                            KbPermissionUtils.goSetting(MainActivity.this);
                        }
                    })
                    .send();
        }

        downBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFile();
            }
        });

    }



    public  void downloadFile(){
        String address = ip.getText().toString();
        String  user = username.getText().toString();
        String  pass = password.getText().toString();
        String  serverPath = spath.getText().toString();
        String  storePath = dest.getText().toString();

        //默认使用测试的ftp
        if (address.isEmpty()){
             address = "test.rebex.net";
        }
        if (user.isEmpty()){
            user = "demo";
        }
        if (pass.isEmpty()){
            pass = "password";
        }
        if (serverPath.isEmpty()){
            serverPath = "/readme.txt";
        }
        if (storePath.isEmpty()){
            storePath = "/storage/emulated/0/Download/readme.txt";
        }

        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(address,user,pass,serverPath,storePath);
    }



    class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("下载中...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                OkFtp ftp = new OkFtp();
                ftp.connect(params[0],params[1],params[2]);
                ftp.downloadFile(params[3],params[4]);
                return new String("下载成功,文件存在"+params[4]);
            }catch (Exception e){
                String t="下载失败 : " + e.getLocalizedMessage();
                return t;
            }
        }

        @Override
        protected void onPostExecute(String str) {
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this,str,Toast.LENGTH_SHORT).show();
        }
    }


    //必须添加，否则第一次请求成功权限不会走回调
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        KbPermission.onRequestPermissionResult(requestCode, permissions, grantResults);
    }
}
