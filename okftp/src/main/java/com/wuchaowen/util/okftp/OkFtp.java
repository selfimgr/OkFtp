package com.wuchaowen.util.okftp;

import android.util.Log;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @Author: wuchaowen
 * @Description:  ftp的下载工具类
 **/
public class OkFtp {
    private FTPClient mFtpClient =null;

    public OkFtp(){
        mFtpClient=new FTPClient();
        this.mFtpClient.setConnectTimeout(10*1000);
    }

    public void setFtpClient(FTPClient mFtpClient) {
        this.mFtpClient = mFtpClient;
    }

    public void useCompressedTransfer (){
        try {
            mFtpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.COMPRESSED_TRANSFER_MODE);
        }catch (Exception e){
            e.printStackTrace();
        };
    }

    public String [] listName () throws Exception {
        try{
            return mFtpClient.listNames();
        }catch (Exception e){
            throw e;
        }
    }

    public boolean setWorkingDirectory (String dir)throws  Exception{
        try{
            return mFtpClient.changeWorkingDirectory(dir);
        }catch (Exception e){
            throw e;
        }
    }

    public FTPClient getFtpClient() {return mFtpClient;}

    public void setTimeout (int seconds) throws  Exception{
        try {
            mFtpClient.setConnectTimeout(seconds * 1000);
        }catch (Exception e){
            throw e;
        }
    }

    public boolean makeDir(String dir) throws Exception {
        try {
            return mFtpClient.makeDirectory(dir);
        }catch (Exception e){
            throw e;
        }
    }

    public void disconnect(){
        try {
            mFtpClient.disconnect();
        }catch (Exception e ){
            e.printStackTrace();
        }
    }

    public void connect(String ip, String userName, String pass) throws Exception{
        boolean status = false;
        try {
            try {
                mFtpClient.connect(ip);
            }catch (Exception e){
                e.printStackTrace();
            }
            status = mFtpClient.login(userName, pass);
            Log.e("ftp is connect ", String.valueOf(status));
        } catch (SocketException e) {
            throw e;
        }
        catch (UnknownHostException e) {
            throw e;
        }
        catch (IOException e) {
            throw e;
        }
    }


    /**
     *
     * @param remoteFilePath 服务器的文件位置
     * @param dest   文件下载存储的位置
     * @throws Exception
     */
    public void downloadFile(String remoteFilePath, String dest)  throws Exception{
        File downloadFile=new File(dest);
        File parentDir = downloadFile.getParentFile();
        if (!parentDir.exists())
            parentDir.mkdir();
        OutputStream outputStream = null;
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
            // 设置PassiveMode传输
            mFtpClient.enterLocalPassiveMode();
            mFtpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            /** 此处用查询文件的信息
             FTPFile[] files = mFtpClient.listFiles();
             FTPFile file = files[0];  //文件信息
             long size = file.getSize();
             String fileaName = file.getName();
             **/

            InputStream inputStream = mFtpClient.retrieveFileStream(remoteFilePath);
            FileOutputStream fileOutputStream = new FileOutputStream(dest);
            IOUtils.copy(inputStream, fileOutputStream);
            fileOutputStream.flush();
            IOUtils.closeQuietly(fileOutputStream);
            IOUtils.closeQuietly(inputStream);
            boolean status = mFtpClient.completePendingCommand();
            Log.e("Status", String.valueOf(status));
        } catch (Exception e) {
            throw e;
        }
        finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
    }
}
