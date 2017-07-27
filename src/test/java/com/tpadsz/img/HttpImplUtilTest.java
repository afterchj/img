package com.tpadsz.img;

import com.tpadsz.img.utils.HttpImplUtil;
import com.tpadsz.img.utils.HttpPostParams;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class HttpImplUtilTest {

//    public static void main(String[] args) throws Exception {
//        HttpImplUtil utilTest = new HttpImplUtil();
//        DateFormat format = new SimpleDateFormat("yyyyMMdd");
//        String date = format.format(new Date());
//        String innercode = "PNSOJXSW";
//        HttpPostParams params = new HttpPostParams();
//        params.setUsername("uichange");
//        long timestamp = new Date().getTime();
//        System.out.println("upload-->timesamp=" + timestamp);
//        params.setId(utilTest.md5(params.getPublish_url() + timestamp));
//        params.setDownload_url("http://wap.dl.pinyin.sogou.com/wapdl/android/apk/SogouInput_android_v8.7_sweb.apk?frm=new_pcjs_index");
//        params.setPublish_url("http://www.uichange.com");//http://www.uichange.com
//        params.setSignature(utilTest.md5(date + params.getUsername() + innercode));
//        System.out.println(params.toString());
//        String str = String.format("Hi,%s:%s.%s", "王南", "王力", "王张");
//        System.out.println(str);
//        File file = utilTest.checkFile(params.getDownload_url(), "C:/test/02.apk");
//        params.setMd5(utilTest.md5File(file));
//        HttpImplUtil.uploadFile(params);
//        HttpImplUtil.deleteFile(params);
//        System.out.println("文件加密后的值="+utilTest.md5File(file));
//        HttpImplUtil.queryTaskStatus("123122");
//    }
}
