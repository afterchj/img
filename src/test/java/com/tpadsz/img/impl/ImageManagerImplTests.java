package com.tpadsz.img.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.sun.corba.se.spi.orbutil.fsm.Input;
import com.tpadsz.img.utils.FileFtpUtil;
import com.tpadsz.img.utils.Ftp;
import com.tpadsz.img.utils.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import com.tpadsz.exception.NotFoundException;
import com.tpadsz.exception.SystemAlgorithmException;
import com.tpadsz.img.api.ImageManager;
import com.tpadsz.img.utils.Constants;
import com.tpadsz.img.vo.ImageOffer;
import org.junit.experimental.theories.suppliers.TestedOn;


public class ImageManagerImplTests {
	private static ImageManager imageManager = new ImageManagerImpl();

	@Test
	public void testStorage() throws MalformedURLException, SystemAlgorithmException, NotFoundException{
		String urlStr = "http://www.pp3.cn/uploads/201510/2015101713.jpg";
		String viewUrl = imageManager.storageURL(new ImageOffer("1", "fun", "cpa", "123456"), urlStr);
		assertThat(viewUrl, equalTo(Constants.URL_PREFIX + "/fun/cpa/icon/123456.jpg"));
	}
	/**
	 *   测试-final1  url
	 * */
	@Test
	public void testURK() throws SystemAlgorithmException {
		//http://wap.dl.pinyin.sogou.com/wapdl/android/apk/SogouInput_android_v8.7_sweb.apk?frm=new_pcjs_index
		//http://www.uichange.com/huodong/tpdasz_update/9/bosslocker-release_1.4.0_46_20161213.apk
		System.out.println("开始时间 = "+new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss").format(new Date()));
		String url = "http://www.uichange.com/public/ctc/app/apk/com.wtjr.lqg-2.1.0-703.apk";
		ImageOffer imageOffer = new ImageOffer("1","Apk","apk","boss","sougou","apk");
		System.out.println(imageManager.storageURL(imageOffer, url));;
		System.out.println("结束时间 = "+new SimpleDateFormat("yyyy-MM-dd  hh:mm:ss").format(new Date()));
	}
	/**
	 *   测试-final1  url
	 * */
	@Test
	public void testLOCAL() throws SystemAlgorithmException {
		File file = new File("E:"+File.separator+"com.wtjr.lqg-2.1.0-703.apk");
		ImageOffer imageOffer = new ImageOffer("1","Apk","apk","boss","sougou","apk");
		System.out.println(imageManager.storageLocalFile(imageOffer,new String("E:"+File.separator+"com.wtjr.lqg-2.1.0-703.apk")));
	}
	@Test
	public void testStorage1() throws MalformedURLException, SystemAlgorithmException, NotFoundException{
		System.out.println("testStorage1 run");
		String urlStr = "D:"+ File.separator+"boss-master.rar";
		ImageOffer imageOffer = new ImageOffer("1","Apk文件","apk","boss",(String)urlStr.substring(urlStr.indexOf(File.separator)+1),"rar");
		String viewUrl = imageManager.storageLocalFile(imageOffer,urlStr);
		System.out.println("viewUrl的值 = "+viewUrl);
		assertThat(viewUrl, equalTo(Constants.URL_PREFIX + "/fun/cpa/icon/test.png"));
	}
	@Test
	public void testFtpConnect(){
		Ftp ftp = new Ftp();
		ftp.setIpAddr("192.168.51.243");
		ftp.setPort(2121);
		ftp.setUserName("ftpTest");
		ftp.setPwd("impanpan@KIM");
		ftp.setPath("/");
		try {
			System.out.println(FileFtpUtil.connectFtp(ftp));//D:\360驱动大师目录
			FileFtpUtil.upload(new File("D:"+File.separator+"ftpTest"));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	@Test
	public void testFtpSetting(){
		Ftp ftp = new Ftp();
		String urlExtra = "192.168.51.243:2121:ftpTest:impanpan@KIM:/";
		String ipAdd = urlExtra.substring(0,urlExtra.indexOf(":",0));
		System.out.println("ipAdd="+ipAdd);
		System.out.println(urlExtra.indexOf(":",0));
		String port = urlExtra.substring(urlExtra.indexOf(":",0)+1,urlExtra.indexOf(":",1)+1);
		System.out.println("port="+port);
		String username = urlExtra.substring(urlExtra.indexOf(":",1),urlExtra.indexOf(":",2));
		System.out.println("username="+username);
		String pwd = urlExtra.substring(urlExtra.indexOf(":",2),urlExtra.indexOf(":",3));
		String dic = urlExtra.substring(urlExtra.indexOf(":",3));
		ftp.setIpAddr(ipAdd);
		ftp.setPort(Integer.parseInt(port));
		ftp.setUserName(username);
		ftp.setPwd(pwd);
		ftp.setPath(dic);
		System.out.println(ftp);
	}
	@Test
	public void testUrltoFtp() throws SystemAlgorithmException {
		String urlStr = "http://www.pp3.cn/uploads/201510/2015101713.jpg";
		String viewUrl = imageManager.storageURL(new ImageOffer("1", "fun", "cpa", "123456"), urlStr);
		assertThat(viewUrl, equalTo(Constants.URL_PREFIX + "/fun/cpa/icon/123456.jpg"));
	}
	@Test
	/**
	 * 针对流 的上传ftp
	 * */
	public void testinputStreamFtp(){
		Ftp ftp = new Ftp();
		ftp.setIpAddr("192.168.51.243");
		ftp.setPort(2121);
		ftp.setUserName("ftpTest");
		ftp.setPwd("impanpan@KIM");
		ftp.setPath("/");
		try {
			System.out.println(FileFtpUtil.connectFtp(ftp));//D:\360驱动大师目录
			//测试本地流
			File file = new File("D:"+File.separator+"boss-master.rar");
			InputStream inputStream = new FileInputStream(file);

			//测试网络流
//			String urlStr = "http://www.pp3.cn/uploads/201510/2015101713.jpg";
//			URL sourceUrl = new URL(urlStr);
//			InputStream inputStream = sourceUrl.openStream();
			FileFtpUtil.upload(inputStream,"boss-master.rar");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
//	@Test
//	/**
//	 * 测试流存储到ftp  ImanageManagerImpl
//	 * */
//	public void testInputStream() throws SystemAlgorithmException {
//		String urlStr = "http://www.pp3.cn/uploads/201510/2015101713.jpg";
//		String viewUrl = imageManager.storageURL(new ImageOffer("1", "fun", "cpa", "123456"), urlStr,"local");
//	}
	@Test
	public void testMkdirDic() throws Exception {
		Ftp ftp = new Ftp();
		ftp.setIpAddr("192.168.51.243");
		ftp.setPort(2121);
		ftp.setUserName("ftpTest");
		ftp.setPwd("impanpan@KIM");
		ftp.setPath("/");
		FileFtpUtil.connectFtp(ftp);
		FileFtpUtil.createDirectorys("ftp_local/1/2/2/3/");
	}
	@Test
	public void testBYS() throws Exception {

		Ftp ftp = new Ftp();
		ftp.setIpAddr("upload.fd.i.qingcdn.com");
		//ftp.setPort(2121);
		ftp.setUserName("uichange");
		ftp.setPwd("5fe1d3");
		ftp.setPath("/");
		String urlStr = "http://www.pp3.cn/uploads/201510/2015101713.jpg";
		URL url = new URL(urlStr);
		File file = new File("D:"+File.separator+"test.png");
		System.out.println(file.length());
		//InputStream inputStream = url.openStream();
		System.out.println(FileFtpUtil.connectFtp(ftp));
		//FileFtpUtil.upload(inputStream,"test.jps");
		FileFtpUtil.upload(file);
	}
	@Test
	public void testCreateDicOnBys() throws Exception {
		Ftp ftp = new Ftp();
		ftp.setIpAddr("upload.fd.i.qingcdn.com");
		//ftp.setPort(2121);
		ftp.setUserName("uichange");
		ftp.setPwd("5fe1d3");
		ftp.setPath("apk.uichange.com/");
		System.out.println(FileFtpUtil.connectFtp(ftp));
		FileFtpUtil.createDirectorys("kim/haha/lalala");
	}
	//测试url获得文件大小
	@Test
	public void test11() throws SystemAlgorithmException {
		String url = "http://wap.dl.pinyin.sogou.com/wapdl/android/apk/SogouInput_android_v8.7_sweb.apk?frm=new_pcjs_index";
		String viewUrl = imageManager.storageURL(new ImageOffer("1", "fun", "cpa", "123456"), url);
	}
	@Test
	public void testFileStatus(){
		String taskId = "";
		ExecutorService threadPool = Executors.newFixedThreadPool(15);

		Future status = threadPool.submit(new CheckStatusThread(taskId));
		System.out.println(status.isDone());
		System.out.println(status.toString());
		threadPool.shutdown();

	}
	class CheckStatusThread implements Callable<String> {
		private String taskId;
		public CheckStatusThread(String taskId){
			System.out.println("?????????????=================");
			this.taskId = taskId;
		}
		@Override
		public String call() {
			System.out.println("??????????????????????");
			String taskStatus = null;
			while(true){
				try {
					Thread.sleep(2000);
					taskStatus = new HttpUtil().checkFileStatus(taskId);
					if(taskStatus.contains("Task Waiting") || taskStatus.contains("Task Runing")){
						continue;
					}else if(taskStatus.contains("Task Success")){
						taskStatus = "Task Success";
						break;
					}else if(taskStatus.contains("Task not Found")){
						taskStatus = "Task not Found";
						break;
					}else if(taskStatus.contains("Task Md5 Error") ){
						taskStatus = "Task Md5 Error";
						break;
					}else if(taskStatus.contains("Task Timeout")){
						taskStatus = "Task Timeout";
						break;
					}else if(taskStatus.contains("Task Error")){
						taskStatus = "Task Error";
						break;
					}else if(taskStatus.contains("Task Failure")){
						taskStatus = "Task Failure";
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

			System.out.println(taskStatus);

			return taskStatus;
		}
	}
	class TaskWithResult implements Callable<String>{//实现这个接口，调用的是call()方法
		private String id;
		public TaskWithResult(String id){
			this.id  = id;
		}

		public String call(){
			System.out.println("??????????????????????");
			String taskStatus = null;
			while(true){
				try {
					Thread.sleep(3000);
					taskStatus = new HttpUtil().checkFileStatus(id);
					if(taskStatus.contains("Task Waiting") || taskStatus.contains("Task Runing")){
						continue;
					}else if(taskStatus.contains("Task Success")){
						taskStatus = "Task Success";
						break;
					}else if(taskStatus.contains("Task not Found")){
						taskStatus = "Task not Found";
						break;
					}else if(taskStatus.contains("Task Md5 Error") ){
						taskStatus = "Task Md5 Error";
						break;
					}else if(taskStatus.contains("Task Timeout")){
						taskStatus = "Task Timeout";
						break;
					}else if(taskStatus.contains("Task Error")){
						taskStatus = "Task Error";
						break;
					}else if(taskStatus.contains("Task Failure")){
						taskStatus = "Task Failure";
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
			System.out.println(taskStatus);

			return taskStatus;
	}
	}

		@Test
		public void testHH(){
			ExecutorService exec = Executors.newCachedThreadPool();
			ArrayList<Future<String>> results = new ArrayList<Future<String>>();

				results.add(exec.submit(new TaskWithResult("")));

			for(Future<String> fs : results){
				try{
					System.out.println(fs.get());
				}catch(Exception e){
					e.printStackTrace();
				}finally{
					exec.shutdown();
				}
			}
		}

	@Test
	public void testDate(){
		String date = "2016-12-21 10:30:12.0";
		System.out.println(date.substring(0,date.lastIndexOf(".")));

	}
	@Test
	public void testContains(){
		System.out.println("Task Add Success".contains("Success"));
	}
	@Test
	public void testUrlFile() throws SystemAlgorithmException {
		//ImageOffer imageOffer = new ImageOffer("8", "ctc", "app", "apk", name, StringUtils.substringAfterLast(apkFile.getOriginalFilename(), ".")
		ImageOffer imageOffer = new ImageOffer("8", "ctc", "app", "apk", "123","apk");
		String url = "http://wap.dl.pinyin.sogou.com/wapdl/android/apk/SogouInput_android_v8.7_sweb.apk?frm=new_pcjs_index";
		String returnUrl = imageManager.storageURL(imageOffer,url);


	}
	@Test
	public void testLoaclFile() throws SystemAlgorithmException {

		String filepath = "E:"+File.separator+"1.apk";
		ImageOffer imageOffer = new ImageOffer("8", "ctc", "app", "apk", "123","apk");

		String url= imageManager.storageLocalFile(imageOffer,filepath,System.currentTimeMillis());
		System.out.println(url);
		while(true){

		}

	}
	@Test
	public void testUrlFile1() throws SystemAlgorithmException {

		//String filepath = "E:"+File.separator+"1.apk";
		String url = "http://121.199.46.166/public/ctc/app/apk/com.yingxiong.dfzj.qihoo360-1.2.1-650.apk";
		ImageOffer imageOffer = new ImageOffer("8", "ctc", "app", "apk", "123","apk");

		String url1= imageManager.storageURL(imageOffer,url,System.currentTimeMillis());
		System.out.println(url1);
		while(true){

		}

	}
	@Test
	public void testStatus() throws SystemAlgorithmException {

		System.out.println(imageManager.getTaskStatus("d6cd8046ac0dc487fc1aef84ba62c53c"));
	}
	//http://121.199.46.166/public/ctc/app/apk/com.yingxiong.dfzj.qihoo360-1.2.1-650.apk
	@Test
	public void testTaskStatus() throws SystemAlgorithmException {
		//9d51230a92f659a6e7d35fb8e79108af
		String result = imageManager.getTaskStatus("9d51230a92f659a6e7d35fb8e79108af");
		System.out.println(result);
	}
	@Test
    public void testDownLoad(){
        System.out.println(new HttpUtil().downLoad("http://wap.dl.pinyin.sogou.com/wapdl/android/apk/SogouInput_android_v8.7_sweb.apk?frm=new_pcjs_index","E:"+File.separator+"2222.apk"));
    }
    @Test
    public void testDate1() throws ParseException {
        String date = "Thu Dec 22 17:04:01 CST 2016";//date.length()-4
        SimpleDateFormat sdf1= new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
        SimpleDateFormat sdf2= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(sdf2.format(sdf1.parse(date)));
    }
    @Test
	public void test3(){
		Path storagePath = Paths.get(Constants.STORAGE_PREFIX);
		System.out.println(storagePath);
	}
}
