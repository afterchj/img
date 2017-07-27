package com.tpadsz.img.impl;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import com.tpadsz.img.api.ImageManager;
import com.tpadsz.img.utils.HttpUtil;
import com.tpadsz.img.vo.ImageOffer;
import org.apache.commons.io.FileUtils;
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
import org.apache.log4j.Logger;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.tpadsz.exception.SystemAlgorithmException;

import com.tpadsz.img.utils.Constants;


@Service("imageManager")
public class ImageManagerImpl implements ImageManager {
    private HttpUtil httpUtil = new HttpUtil();
    private Path storagePath = Paths.get(Constants.STORAGE_PREFIX);
    private Logger logger = Logger.getLogger(ImageManagerImpl.class);
    private static ExecutorService threadPool = Executors.newFixedThreadPool(15);

    @PostConstruct
    public void init() throws IOException {
        if (!Files.isDirectory(storagePath)) {
            Files.createDirectories(storagePath);
        }
    }

    class CopyStringThread implements Runnable {

        private String source;
        private Path destination;
        private long time;

        public CopyStringThread(String source, Path destination) {
            super();
            this.source = source;
            this.destination = destination;
        }

        public CopyStringThread(String source, Path destination, long time) {
            super();
            this.source = source;
            this.destination = destination;
            this.time = time;
        }

        @Override
        public void run() {
            try {
                FileUtils.writeStringToFile(destination.toFile(), source);
                //后续根据需求开放功能
//				if(source.length()/1024/1024>50){
//					String url = Constants.URL_PREFIX+destination.toString().replace("\\","/").substring(destination.toString().indexOf(":")+1);
//					boolean isDown = httpUtil.downLoad(url,storagePath+File.separator+time+"File2BYS.apk");
//					if(!isDown){
//						return;
//					}
//					httpUtil.LocalFileUpload(destination.toString().replace("\\","/").substring(destination.toString().indexOf(":")+1),
//							url,storagePath+File.separator+time+"File2BYS.apk",time);
//				}
            } catch (IOException e) {
                logger.error("source:" + source + "destination:" + destination, e);
            }
        }
    }

    class CopyUrlThread implements Runnable {

        private String source;
        private Path destination;
        private long time;

        public CopyUrlThread(String source, Path destination) {
            super();
            this.source = source;
            this.destination = destination;
        }

        public CopyUrlThread(String source, Path destination, long time) {
            super();
            this.source = source;
            this.destination = destination;
            this.time = time;
        }

        @Override
        public void run() {
            try {
                FileUtils.copyURLToFile(new URL(source), destination.toFile());
                //后续根据需求开放功能
//				if(httpUtil.checkFileLength(source)>50){
//					String url = Constants.URL_PREFIX+destination.toString().replace("\\","/").substring(destination.toString().indexOf(":")+1);
//					boolean isDown = httpUtil.downLoad(url,storagePath+File.separator+time+"File2BYS.apk");
//					if(!isDown){
//						return;
//					}
//					httpUtil.LocalFileUpload(destination.toString().replace("\\","/").substring(destination.toString().indexOf(":")+1),
//							url,storagePath+File.separator+time+"File2BYS.apk",time);
//				}
            } catch (IOException e) {
                logger.error("source:" + source + "destination:" + destination, e);
            }
        }
    }

    class CopyLocalFileThread implements Runnable {

        private String localFile;
        private Path destination;
        private long time;

        public CopyLocalFileThread(String localFile, Path destination) {
            super();
            this.localFile = localFile;
            this.destination = destination;
        }

        public CopyLocalFileThread(String localFile, Path destination, long time) {
            super();
            this.localFile = localFile;
            this.destination = destination;
            this.time = time;
        }

        @Override
        public void run() {
            File tmpFile = new File(localFile);
            try {
                FileUtils.copyFile(tmpFile, destination.toFile());
                if (tmpFile.length() / 1024 / 1024 > 50) {
                    String url = Constants.URL_PREFIX + destination.toString().replace("\\", "/").substring(destination.toString().indexOf(":") + 1);
                    boolean isDown = httpUtil.downLoad(url, storagePath + File.separator + time + "File2BYS.apk");
                    if (!isDown) {
                        return;
                    }
                    httpUtil.LocalFileUpload(destination.toString().replace("\\", "/").substring(destination.toString().indexOf(":") + 1),
                            url, storagePath + File.separator + time + "File2BYS.apk", time);
                }
            } catch (IOException e) {
                System.err.println("文件下载失败！！！");
                logger.error("method:FileUtils#copyFile, source:" + localFile + ",destination:" + destination, e);
            } finally {
                if (tmpFile != null && tmpFile.exists()) {
                    org.apache.commons.io.FileUtils.deleteQuietly(tmpFile);
                }
            }
        }
    }

    private String fillImageUrl(ImageOffer offer) {
        System.out.println("result="+Constants.URL_PREFIX+"?"+offer.getSystem()+"?"+offer.getType()+"?"+offer.getPrefix()+"?"+offer.getName()+"?"+offer.getSuffix());
        return String.format("%s/%s/%s/%s/%s.%s", Constants.URL_PREFIX, offer.getSystem(), offer.getType(), offer.getPrefix(), offer.getName(), offer.getSuffix());
    }

    public String storageURL(ImageOffer offer, String url, long time) throws SystemAlgorithmException {
        try {
            Path filePath = checkFile(offer);
            threadPool.execute(new CopyUrlThread(url, filePath, time));
            //后续根据需求开放功能
//			url = fillImageUrl(offer);
//			long fileLength = httpUtil.checkFileLength(url);
//			if(fileLength > 50){
//				return "http://apk.uichange.com"+(filePath.toString().replace("\\","/").substring(filePath.toString().indexOf(":")+1));
//			}else {
            return fillImageUrl(offer);
//			}
        } catch (Exception e) {
            throw new SystemAlgorithmException("bean:imageManager, method:storageURL", e);
        }
    }

    private Path checkFile(ImageOffer offer) {
        Path path = Paths.get(storagePath.toAbsolutePath().toString(), offer.getSystem(), offer.getType(), offer.getPrefix(), offer.getName() + "." + offer.getSuffix());
        return path;
    }

    @Override
    public boolean delete(ImageOffer offer) {
        Path path = checkFile(offer);
        return FileUtils.deleteQuietly(path.toFile());
    }


    public String storageLocalFile(ImageOffer offer, String file, long time) throws SystemAlgorithmException {
        String result = "http://apk.uichange.com";
        try {
            System.out.println("大文件上传start...");
            Path filePath = checkFile(offer);
            threadPool.execute(new CopyLocalFileThread(file, filePath, time));
            if (!new File(file).exists()) {
                return null;
            }
            String url = fillImageUrl(offer);
            System.out.println("formatUrl=" + url);
            long fileLength = httpUtil.checkFileLength(url);
            System.out.println("Length:" + fileLength);
            if (fileLength > 50) {
                result += (filePath.toString().replace("\\", "/").substring(filePath.toString().indexOf(":") + 1));
            } else {
                result = fillImageUrl(offer);
            }
        } catch (Exception e) {
            System.err.println("大文件上传失败！！！");
            throw new SystemAlgorithmException("bean:imageManager, method:storageLocalFile, file:" + file + ", offer:" + JSONObject.toJSONString(offer), e);
        }
        System.out.println("大文件上传end...");
        return result;
    }

    public String storageStr(ImageOffer offer, String source, long time) throws SystemAlgorithmException {
        try {
            Path filePath = checkFile(offer);
            threadPool.execute(new CopyStringThread(source, filePath, time));
            //后续根据需求开放功能
//			String url = fillImageUrl(offer);
//			long fileLength = httpUtil.checkFileLength(url);
//			if(fileLength > 50){
//				return "http://apk.uichange.com"+(filePath.toString().replace("\\","/").substring(filePath.toString().indexOf(":")+1));
//			}else {
            return fillImageUrl(offer);
//			}
        } catch (Exception e) {
            throw new SystemAlgorithmException("bean:imageManager, method:storageInputStream", e);
        }
    }


    /**
     * 获取删除任务返回状态
     */
    public String deleteTaskResult(String publish_url, String md5) {
        System.out.println("文件删除starting...");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://fdpull.api.i.qingcdn.com/api/add?op=delete");
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("username", "uichange"));
        String taskStatus = "";
        try {
            formparams.add(new BasicNameValuePair("id", new HttpUtil().md5(publish_url + System.currentTimeMillis())));
            formparams.add(new BasicNameValuePair("signature", new HttpUtil().md5(new SimpleDateFormat("yyyyMMdd").format(new Date()) + "uichange" + "PNSOJXSW")));
            formparams.add(new BasicNameValuePair("publish_url", publish_url));
            formparams.add(new BasicNameValuePair("md5", md5));
            UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httpPost.setEntity(uefEntity);
            System.out.println("executing url= " + httpPost.getURI());
            CloseableHttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            try {
                taskStatus = EntityUtils.toString(entity, "UTF-8");
                if (entity != null) {
                    System.out.println("--------------------------------------");
                    System.out.println("taskStatus = " + taskStatus);
                    System.out.println("--------------------------------------");
                }
            } finally {
                response.close();
            }
            //return taskStatus;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("文件删除Ending...");
        return taskStatus;
    }

    /**
     * 获取任务状态
     */

    public String getTaskStatus(String taskId) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost("http://fdpull.api.i.qingcdn.com/api/query");
        System.out.println("executing request " + httpPost.getURI());
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        //"c169f4ed9c32e7b1f13c8e8d0677ed4b"
        formparams.add(new BasicNameValuePair("id", taskId));
        UrlEncodedFormEntity uefEntity = null;
        CloseableHttpResponse response = null;
        String taskStatus = "";
        try {
            uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
            httpPost.setEntity(uefEntity);
            HttpEntity entity = response.getEntity();
            taskStatus = EntityUtils.toString(entity, "UTF-8");
            try {
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

    @Override
    public String storageURL(ImageOffer imageOffer, @NotBlank String url) throws SystemAlgorithmException {
        try {
            Path filePath = checkFile(imageOffer);
            threadPool.execute(new CopyUrlThread(url, filePath));
            return fillImageUrl(imageOffer);
        } catch (Exception e) {
            throw new SystemAlgorithmException("bean:imageManager, method:storageURL", e);
        }
    }

    @Override
    public String storageLocalFile(ImageOffer imageOffer, String file) throws SystemAlgorithmException {
        String result = "";
        try {
            System.out.println("普通文件上传start...");
            Path filePath = checkFile(imageOffer);
            threadPool.execute(new CopyLocalFileThread(file, filePath));
            System.out.println("formatUrl" + fillImageUrl(imageOffer));
            result = fillImageUrl(imageOffer);
        } catch (Exception e) {
            System.out.println("普通文件上传失败！！！");
            throw new SystemAlgorithmException("bean:imageManager, method:storageLocalFile, file:" + file + ", offer:" + JSONObject.toJSONString(imageOffer), e);
        }
        System.out.println("普通文件上传end...");
        return result;
    }

    @Override
    public String storageStr(ImageOffer imageOffer, String source) throws SystemAlgorithmException {
        try {
            Path filePath = checkFile(imageOffer);
            threadPool.execute(new CopyStringThread(source, filePath));
            return fillImageUrl(imageOffer);
        } catch (Exception e) {
            throw new SystemAlgorithmException("bean:imageManager, method:storageInputStream", e);
        }
    }

}
