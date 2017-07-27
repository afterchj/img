package com.tpadsz.img.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hongjian.chen on 2017/4/14.
 */
public class HttpImplUtil {

    public static boolean uploadFile(HttpPostParams params) {
        boolean flag = false;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://fdpull.api.i.qingcdn.com/api/add?op=publish");
        System.out.println("executing request " + httppost.getURI());
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("username", params.getUsername()));
        formparams.add(new BasicNameValuePair("id", params.getId()));
        formparams.add(new BasicNameValuePair("dowland_url", params.getDownload_url()));
        formparams.add(new BasicNameValuePair("publish_url", params.getPublish_url()));
        formparams.add(new BasicNameValuePair("md5", params.getMd5()));
        formparams.add(new BasicNameValuePair("signature", params.getSignature()));
        UrlEncodedFormEntity uefEntity = null;
        CloseableHttpResponse response = null;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(uefEntity);
            try {
                response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    flag = true;
                    System.out.println(response.getStatusLine());
                    System.out.println("上传文件状态: " + EntityUtils.toString(entity, "UTF-8"));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
                if (httpclient != null) {
                    httpclient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static boolean deleteFile(HttpPostParams params) {
        boolean flag = false;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://fdpull.api.i.qingcdn.com/api/add?op=delete");
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("username", params.getUsername()));
        formparams.add(new BasicNameValuePair("id", params.getId()));
        formparams.add(new BasicNameValuePair("publish_url", params.getPublish_url()));
        formparams.add(new BasicNameValuePair("md5", params.getMd5()));
        formparams.add(new BasicNameValuePair("signature", params.getSignature()));
        CloseableHttpResponse response = null;
        try {
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(uefEntity);
            System.out.println("executing request " + httppost.getURI());
            try {
                response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    flag = true;
                    System.out.println(response.getStatusLine());
                    System.out.println("删除文件状态: " + EntityUtils.toString(entity, "UTF-8"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static boolean queryTaskStatus(String id) {
        boolean flag = false;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("http://fdpull.api.i.qingcdn.com/api/query");
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("id", id));
        CloseableHttpResponse response = null;
        try {
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httppost.setEntity(uefEntity);
            System.out.println("executing request " + httppost.getURI());
            try {
                response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                System.out.println(response.getStatusLine());
                if (entity != null) {
                    flag = true;
                    System.out.println("查询文件状态: " + EntityUtils.toString(entity, "UTF-8"));
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                response.close();
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static String md5(String param) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(param.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new BigInteger(1, md.digest()).toString(16);
    }

    public static String md5File(File file) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            try {
                FileInputStream in = new FileInputStream(file);
                MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
                md5.update(byteBuffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new BigInteger(1, md5.digest()).toString(16);
    }

    public static File checkFile(String url, String destFileName) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        File file = new File(destFileName);
        InputStream inputStream = null;
        FileOutputStream fout = null;
        try {
            HttpGet httpget = new HttpGet(url);
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            inputStream = entity.getContent();
            try {
                fout = new FileOutputStream(file);
                int read = -1;
                byte[] tmp = new byte[1024 * 1024];
                while ((read = inputStream.read(tmp)) != -1) {
                    fout.write(tmp, 0, read);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                System.out.println("文件不存在！！！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fout.flush();
                fout.close();
                inputStream.close();
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }
}
