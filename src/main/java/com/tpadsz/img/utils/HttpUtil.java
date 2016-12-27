package com.tpadsz.img.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by pan.sun on 2016/12/14.
 */
public class HttpUtil {
    private static Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);
    private String workPath = null;
    private String downLoadUrl = null;

    //private DefaultHttpClient httpClient = new DefaultHttpClient();
    public long checkFileLength(String url){
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet httpGet = new HttpGet(url);
            HttpResponse httpResponse = httpClient.execute(httpGet);
            HttpEntity entity = httpResponse.getEntity();
            InputStream in = entity.getContent();
            long length = entity.getContentLength();
            return length/1024/1024;
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(httpClient!=null){
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }
    public static void main(String[] args){
        System.out.println(new HttpUtil().downLoad("http://wap.dl.pinyin.sogou.com/wapdl/android/apk/SogouInput_android_v8.7_sweb.apk?frm=new_pcjs_index","E:"+File.separator+"1.apk"));
    }
    public boolean downLoad(String url, String dst) {
        HttpResponse httpResponse = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpGet httpget = new HttpGet(url);
            httpResponse = httpClient.execute(httpget);
            HttpEntity entity = httpResponse.getEntity();
            InputStream in = entity.getContent();
            long length=entity.getContentLength();
            if(length<=0){
                System.out.println("下载文件不存在！");
                return false;
            }
            File file = new File(dst);
            OutputStream out = new FileOutputStream(file);
            saveTo(in, out);
            if(file.length() == length){
                System.out.println("文件下载成功，大小相同");
                return true;
            }else{
                System.out.println("文件下载后大小有误");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("download链接报错");
            e.printStackTrace();
        }finally {
            if(httpClient!=null){
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public void saveTo(InputStream in, OutputStream out) throws Exception {
        byte[] data = new byte[1024*1024];
        int index =0;
        while ((index=in.read(data) )!= -1) {
            out.write(data,0,index);
        }
        in.close();
        out.close();
    }


    /**
     *  设置本地文件参数
     * */
    public String  LocalFileUpload(String publish_url_suffix,String download_url,String FilePath,long time) {
        HttpPostParams httpPostParams = new HttpPostParams();
        try{
            String username = "uichange";
            String publish_url = "http://apk.uichange.com"+publish_url_suffix;
            String id = md5(publish_url+time);
            String signature = md5(new SimpleDateFormat("yyyyMMdd").format(new Date())+username+"PNSOJXSW");
            File file = new File(FilePath);

            if(!file.exists() || file.isDirectory()){
                System.out.println("文件不存在");
                return null;
            }
            String md5 = md5File(file);
            httpPostParams.setUsername(username);
            httpPostParams.setDownload_url(download_url);
            httpPostParams.setPublish_url(publish_url);
            httpPostParams.setId(id);
            httpPostParams.setSignature(signature);
            httpPostParams.setMd5(md5);
            System.out.println(httpPostParams.toString());
            logger.error(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()),httpPostParams.toString());
            //上传文件
            List<NameValuePair> formparams = new ArrayList<NameValuePair>();
            formparams.add(new BasicNameValuePair("username",username));
            formparams.add(new BasicNameValuePair("id",id));
            formparams.add(new BasicNameValuePair("download_url",download_url));
            formparams.add(new BasicNameValuePair("publish_url",publish_url));
            formparams.add(new BasicNameValuePair("md5",md5));
            formparams.add(new BasicNameValuePair("signature",signature));
            System.out.println("跑进参数设置了吗");
            String status = FileUpload(formparams);
            return status;
        }catch(Exception e){
            e.printStackTrace();
            logger.error("MD5加密失败");
        }
        return null;

    }

    public String FileUpload(List<NameValuePair> formparams){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://fdpull.api.i.qingcdn.com/api/add?op=publish");
        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httpPost.setEntity(uefEntity);
            System.out.println("executing request " + httpPost.getURI());

            CloseableHttpResponse response = httpclient.execute(httpPost);
            String status =  null;
            try {
                HttpEntity entity = response.getEntity();
                status =  EntityUtils.toString(entity, "UTF-8");
                if (entity != null) {
                    System.out.println("-----------------------------------------------------");
                    System.out.println("Response content: " + status);
                    System.out.println("-----------------------------------------------------");
                }
                return  status;


            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            System.out.println("upload链接报错");
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }




    public String md5(String param) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(param.getBytes());
        return new BigInteger(1, md.digest()).toString(16);
    }

    public String md5File(File file) throws IOException, NoSuchAlgorithmException {
        FileInputStream in = new FileInputStream(file);
        MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(byteBuffer);

        return new BigInteger(1, md5.digest()).toString(16);
    }
    public void queryFile(String id){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://fdpull.api.i.qingcdn.com/api/query");
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("id",id));
        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httpPost.setEntity(uefEntity);
            System.out.println("executing request " + httpPost.getURI());
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    System.out.println("--------------------------------------");
                    System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));
                    System.out.println("--------------------------------------");
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public String deleteTaskResult(String publish_url,String md5,String signature) throws NoSuchAlgorithmException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://fdpull.api.i.qingcdn.com/api/add?op=delete");
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
//        formparams.add(new BasicNameValuePair("id",taskId));

        formparams.add(new BasicNameValuePair("username","uichange"));
        formparams.add(new BasicNameValuePair("id",md5(publish_url+System.currentTimeMillis())));
        formparams.add(new BasicNameValuePair("signature",signature));
        formparams.add(new BasicNameValuePair("publish_url",publish_url));
        formparams.add(new BasicNameValuePair("md5",md5));
        UrlEncodedFormEntity uefEntity;
        String taskStatus = "";
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httpPost.setEntity(uefEntity);
            System.out.println("executing request " + httpPost.getURI());
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                taskStatus = EntityUtils.toString(entity, "UTF-8");
                if (entity != null) {
                    System.out.println("--------------------------------------");
                    System.out.println("Response content: " + taskStatus);
                    System.out.println("--------------------------------------");
                }
            } finally {
                response.close();
            }
            return taskStatus;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return taskStatus;
    }


    public String checkFileStatus(String taskId){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://fdpull.api.i.qingcdn.com/api/query");
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
//        formparams.add(new BasicNameValuePair("id",taskId));

        formparams.add(new BasicNameValuePair("id",taskId));
        UrlEncodedFormEntity uefEntity;
        String taskStatus = "";
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httpPost.setEntity(uefEntity);
            System.out.println("executing request " + httpPost.getURI());
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                taskStatus = EntityUtils.toString(entity, "UTF-8");
                if (entity != null) {
                    System.out.println("--------------------------------------");
                    System.out.println("Response content: " + taskStatus);
                    System.out.println("--------------------------------------");
                }
            } finally {
                response.close();
            }
            return taskStatus;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return taskStatus;
    }
}
