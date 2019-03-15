# OkFtp
android 实现ftp的下载工具类


# 使用说明

## 添加依赖
```
implementation 'com.chaowen.util:okftp:1.0'
```


```

## 在Activity或Fragment代码中使用
```
 OkFtp ftp = new OkFtp();
 ftp.connect("ip","userName","password");
 ftp.downloadFile("serverFilePath","local File Path");
```
