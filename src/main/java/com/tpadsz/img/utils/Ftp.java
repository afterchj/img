package com.tpadsz.img.utils;

/**
 * Created by pan.sun on 2016/12/9.
 */
public class Ftp {
    private String ipAddr;//ip地址
    private Integer port;//端口号
    private String userName;//用户名
    private String pwd;//密码
    private String path;//ftp服务器路径

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Ftp{" +
                "ipAddr='" + ipAddr + '\'' +
                ", port=" + port +
                ", userName='" + userName + '\'' +
                ", pwd='" + pwd + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
