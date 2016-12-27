package com.tpadsz.img.utils;

/**
 * Created by pan.sun on 2016/12/14.
 */
public class HttpPostParams{
    private String username;
    private String id;
    private String download_url;
    private String publish_url;
    private String md5;
    private String signature;

    @Override
    public String toString() {
        return "HttpParamsUtil{" +
                "username='" + username + '\'' +
                ", id='" + id + '\'' +
                ", download_url='" + download_url + '\'' +
                ", publish_url='" + publish_url + '\'' +
                ", md5='" + md5 + '\'' +
                ", signature='" + signature + '\'' +
                '}';
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getPublish_url() {
        return publish_url;
    }

    public void setPublish_url(String publish_url) {
        this.publish_url = publish_url;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }
}
