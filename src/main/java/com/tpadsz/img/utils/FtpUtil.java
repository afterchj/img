package com.tpadsz.img.utils;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;

/**
 * * Created by hongjian.chen on 2017/4/13.
 *  * @param hostname FTP服务器地址
 *  * @param port FTP服务器端口号
 *  * @param username FTP登录帐号
 *  * @param password FTP登录密码
 *  * @return
 */
public class FtpUtil {
    private static FTPClient ftpClient;
    private static String hostname = "192.168.51.75";//ip地址
    private static Integer port = 2121;//端口号
    private static String username = "ftpuser";//用户名
    private static String password = "1believe,";//密码

    public static FTPClient getFtpClient() throws Exception {
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("UTF-8");
        ftpClient.connect(hostname, port);
        ftpClient.login(username, password);
        int replyCode = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(replyCode)) {
            ftpClient.disconnect();
            System.out.println("连接ftp://" + hostname + ":" + port + " 失败！！");
            return null;
        }
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        ftpClient.setBufferSize(1024 * 1024);//扩大缓存，提高上传速度
        System.out.println("已连接ftp://" + hostname + ":" + port);
        return ftpClient;
    }

    public static void closeFtp(FTPClient ftpClient) {
        if (null != ftpClient && ftpClient.isConnected()) {
            try {
                boolean reuslt = ftpClient.logout();// 退出FTP服务器
                if (reuslt) {
                    System.out.println("成功退出服务器");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("退出FTP服务器异常！" + e.getMessage());
            } finally {
                try {
                    ftpClient.disconnect();// 关闭FTP服务器的连接
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("关闭FTP服务器的连接异常！");
                }
            }
        }
    }
    public static void main(String[] args) throws Exception {
        ftpClient = getFtpClient();
        System.out.println(ftpClient);
    }
}
