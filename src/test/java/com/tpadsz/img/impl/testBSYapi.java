package com.tpadsz.img.impl;

import com.sun.org.apache.xpath.internal.SourceTree;
import com.tpadsz.img.utils.HttpUtil;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
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
public class testBSYapi {
    public testBSYapi() throws NoSuchAlgorithmException {
    }

    @Test
    public void test1() throws NoSuchAlgorithmException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://fdpull.api.i.qingcdn.com/api/add?op=publish");
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        String username = "uichange";
        String publish_url = "http://apk.uichange.com"+"/test/2.zip";
        String download_url = "http://sw.bos.baidu.com/sw-search-sp/software/6ec7161e30f/cs1_6_Setup.zip";
        String id = md5(publish_url+System.currentTimeMillis());
        String signature = md5("20161214"+username+"PNSOJXSW");
        File file = new File("E:"+File.separator+"sougou.apk");
        String md5 = md5File(file);
        System.out.println("username = "+username);
        System.out.println("id = "+id);
        System.out.println("download_url = "+download_url);
        System.out.println("publish_url = "+publish_url);
        System.out.println("md5 = "+md5);
        System.out.println("signature = "+signature);
        formparams.add(new BasicNameValuePair("username",username));
        formparams.add(new BasicNameValuePair("id",id));
        formparams.add(new BasicNameValuePair("download_url",download_url));
        formparams.add(new BasicNameValuePair("publish_url",publish_url));
        formparams.add(new BasicNameValuePair("md5",md5));
        formparams.add(new BasicNameValuePair("signature",signature));
        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httpPost.setEntity(uefEntity);
            System.out.println("executing request " + httpPost.getURI());

            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    System.out.println("-----------------------------------------------------");
                    System.out.println("Response content: " + EntityUtils.toString(entity, "UTF-8"));
                    System.out.println("-----------------------------------------------------");
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
    @Test
    public void test3() throws IOException, NoSuchAlgorithmException {
        File file = new File("E:"+File.separator+"cs1_6_Setup.zip");
        System.out.println(file.length());
        String md5 = md5File(file);

        System.out.println(md5);

        URL url = new URL("http://sw.bos.baidu.com/sw-search-sp/software/6ec7161e30f/cs1_6_Setup.zip");
        InputStream inputStream = url.openStream();
        int len = inputStream.read(new byte[1024]);

        System.out.println(len);
    }
    @Test
    public void testQueryFile() throws InterruptedException {
        System.out.println("开始时间 = "+new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss").format(new Date()));
        while(true){
            Thread.sleep(5000);
            new HttpUtil().queryFile("d61a3f37293aa91418dff67a81c536d8");

        }
    }
    @Test
    public void test5(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMdd");
        System.out.println(new SimpleDateFormat("yyyyMMdd").format(new Date()));
    }
    @Test
    public void test2(){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://fdpull.api.i.qingcdn.com/api/query");
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("id","c169f4ed9c32e7b1f13c8e8d0677ed4b"));
        UrlEncodedFormEntity uefEntity;
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httpPost.setEntity(uefEntity);
            System.out.println("executing request " + httpPost.getURI());
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                String taskStatus = EntityUtils.toString(entity, "UTF-8");
                if (entity != null) {
                    System.out.println("--------------------------------------");
                    System.out.println("Response content: " + taskStatus);
                    System.out.println(taskStatus.contains("Task Success"));
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


    @Test
    public void testbase64() throws IOException, NoSuchAlgorithmException {
        String md5 = md5File(new File("E:"+File.separator+"com.apk"));
        System.out.println(md5);
//        System.out.println(getBase64(file2String(new File("E:"+File.separator+"com.apk"))));
        System.out.println(getBase64(md5));
    }
    public static String file2String(File file){
        try {
            BufferedReader buffer = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String temp;
            while((temp = buffer.readLine()) !=null ){
                sb.append(temp);

            }
            return sb.toString();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
    //base64加密
    public static String getBase64(String str){
        byte[]  b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if( b != null){
            s = new BASE64Encoder().encode(b);
        }
        return s;

    }
    @Test
    public void test() throws IOException {
        String url = "http://wap.dl.pinyin.sogou.com/wapdl/android/apk/SogouInput_android_v8.7_sweb.apk?frm=new_pcjs_index";
        new HttpUtil().downLoad(url,"E:"+File.separator+"1.apk");
//        URL url = new URL("http://wap.dl.pinyin.sogou.com/wapdl/android/apk/SogouInput_android_v8.7_sweb.apk?frm=new_pcjs_index");
//        InputStream inputStream = url.openStream();
//        FileOutputStream fileOutputStream = null;
//        byte[] bytes = new byte[1024*1024*5];
//        int len = -1;
//        while((len = inputStream.read(bytes))!=-1){
//            fileOutputStream = new FileOutputStream(new File("E:"+File.separator+"1.apk"));
//            fileOutputStream.write(bytes,0,len);
//        }
//        inputStream.close();
//        fileOutputStream.close();
//        System.out.println(inputStream.read(new byte[1024]));
      //  HttpClientDownLoadFile.downloadFile(url,"E:");
    }
    @Test
    public void testHttpUtil(){
        HttpUtil httpUtil = new HttpUtil();
       // httpUtil.LocalFileUpload("/test/2.apk","http://wap.dl.pinyin.sogou.com/wapdl/android/apk/SogouInput_android_v8.7_sweb.apk?frm=new_pcjs_index","E:"+File.separator+"sougou.apk");
    }
    @Test
    public void test11() throws IOException, NoSuchAlgorithmException, DecoderException {
        File file = new File("E:"+File.separator+"bosslocker-release_1.4.0_46_20161213.apk");//com.wtjr.lqg-2.1.0-703.apkcom.chengantou.antoubao-2.1.0-314.apk  bosslocker-release_1.4.0_46_20161213.apk
        System.out.println(md5File(file));

        MessageDigest MD5 = MessageDigest.getInstance("MD5");
        String result =  new BASE64Encoder().encode(MD5.digest(md5File(file).getBytes("utf-8")));

       // byte[] decodedHex = Hex.decodeHex(md5File(file).toCharArray());//b436bcb6f55b5f25f92cfa0e282bb150
        byte[] decodedHex = Hex.decodeHex("b436bcb6f55b5f25f92cfa0e282bb150".toCharArray());
        System.out.println("!!!!!!!" +new String(new Base64().encode(decodedHex)));

    }
    public static byte[] decodeBase64(String input) {
        return Base64.decodeBase64(input);
    }

    protected  MessageDigest messagedigest = MessageDigest.getInstance("MD5");
    @Test
    public void test10(){
        System.out.println(new HttpUtil().checkFileStatus("e05b49887fdc610816bad41a8ecb1f8e"));
    }
    @Test
    public void test12() throws NoSuchAlgorithmException {
        System.out.println(new HttpUtil().deleteTaskResult("http://apk.uichange.com/Apk/apk/boss/sougou.apk","b436bcb6f55b5f25f92cfa0e282bb150",md5(new SimpleDateFormat("yyyyMMdd").format(new Date())+"uichange"+"PNSOJXSW")));
    }

}
