package com.tpadsz.img.utils;

/**
 * Created by pan.sun on 2016/12/9.
 */

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FileFtpUtil {
    private static Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);
    private static FTPClient ftpClient;
    public static boolean connectFtp(Ftp f) throws Exception{
        ftpClient=new FTPClient();
        boolean flag=false;
        int reply;
        try{
            if (f.getPort()==null) {
                ftpClient.connect(f.getIpAddr(),21);
            }else{
                ftpClient.connect(f.getIpAddr(),f.getPort());
            }
            ftpClient.login(f.getUserName(), f.getPwd());
            //TODO 传输文件格式
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);

            reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                return flag;
            }
            ftpClient.changeWorkingDirectory(f.getPath());
            flag = true;
            ftpClient.setBufferSize(1024 * 1024 );//扩大缓存，提高上传速度
            System.out.println("连接服务器【"+f.getIpAddr()+":"+f.getPort()+"】成功");
            return flag;

        }catch(Exception e){
            e.printStackTrace();
            logger.error("连接服务器【"+f.getIpAddr()+":"+f.getPort()+"】异常");
        }
        return flag;
    }
    /**
     *  注销、退出服务器
     * */
    public static void closeFtp(FTPClient ftpClient){
        if(ftpClient != null && ftpClient.isConnected()){
            try{
                ftpClient.logout();
            }catch(Exception e){
                e.printStackTrace();
                logger.error("注销服务器【"+ftpClient.getPassiveLocalIPAddress()+":"+ftpClient.getPassivePort()+"】异常");
            }finally {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("退出服务器【"+ftpClient.getPassiveLocalIPAddress()+":"+ftpClient.getPassivePort()+"】异常");
                }
            }
        }
    }
    public static void changeDir(String filePagh){
        try{
            ftpClient.changeWorkingDirectory(filePagh);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("变更工作目录【"+filePagh+"】错误");
        }
    }
    //上传文件  File
    public static void upload(InputStream inputStream,String fileName)  {
        //如果是路径  会上传路径下的所有文件
        System.out.println("??????????????1232132132131????????????????????");
        BufferedInputStream bufferedInputStream = null;
        try{
            bufferedInputStream = new BufferedInputStream(inputStream);
            ftpClient.storeFile(new String(fileName.getBytes("GBK"), "iso-8859-1"),bufferedInputStream);
        }catch(Exception e){
            e.printStackTrace();
            logger.error("上传文件出现异常");
        }finally {
            try {
                inputStream.close();
                bufferedInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileFtpUtil.closeFtp(ftpClient);
        }
    }

    public static void upload(File file)  {
        //如果是路径  会上传路径下的所有文件
        System.out.println("??????????????????????????????????");
        FileInputStream fileInputStream = null;
        System.out.println(file.getName());
        try{
            if(file.isDirectory()){
                ftpClient.makeDirectory(new String(file.getName().getBytes("GBK"), "iso-8859-1"));
                ftpClient.changeWorkingDirectory(new String(file.getName().getBytes("GBK"), "iso-8859-1"));
                String[] files = file.list();
                for(String fileEach : files){
                    File file1 = new File(file.getPath()+File.separator+fileEach);
                    //如果file1也是路径，递归上传11
                    System.out.println("循环里的fileName = "+file1.getName()+"--路径 = "+file1.getPath());
                    if(file1.isDirectory()){
                        upload(file1);
                        try{
                            ftpClient.changeToParentDirectory();

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        File file2 = new File(file.getPath()+File.separator+fileEach);
                        fileInputStream = new FileInputStream(file2);

                        System.out.println("上传的循环 = "+file2.getName()+"--路径 = "+file2.getPath());
                        ftpClient.storeFile(new String(file2.getName().getBytes("GBK"), "iso-8859-1"),fileInputStream);
                        fileInputStream.close();
                    }
                }
            }else{
                File file2=new File(file.getPath());
                fileInputStream = new FileInputStream(file2);
                System.out.println("ftp是否处于连接状态 = "+ftpClient.isConnected());

                ftpClient.storeFile(new String(file2.getName().getBytes("GBK"), "iso-8859-1"),fileInputStream);
                /**
                 * 查看ftp上文件
                 * */
                for(FTPFile files : ftpClient.listFiles()){
                    System.out.println(files.getName());
                }
                fileInputStream.close();
            }
        }catch(Exception e){
            e.printStackTrace();
            logger.error("上传文件出现异常");
        }finally {
            //closeFtp(ftpClient);
        }
    }




    private  static void downloadFile(FTPFile ftpFile, String relativeLocalPath, String relativeRemotePath) {
        if(ftpFile.isFile()){
            System.out.println("ftpFile.getName() = "+ftpFile.getName());
            if(ftpFile.getName().indexOf("?")==-1){
                OutputStream outputStream = null;
                String newFileName = ftpFile.getName();
                try{
                    File localFile = new File(relativeLocalPath+ftpFile.getName());
                    if(localFile.exists()){
                        //如果本地文件存在
                        newFileName = ftpFile.getName()+"-new";
                    }
                    outputStream = new FileOutputStream(relativeLocalPath+newFileName);
                    ftpClient.retrieveFile(newFileName,outputStream);
                    outputStream.flush();
                }catch(Exception e){
                    logger.error(e.getMessage());
                }finally {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                        logger.error("传输文件异常");
                    }
                }
            }
        }else{
            String newRelativeLocalPath = relativeLocalPath+ftpFile.getName().toString();
            String newRelativeRemotePath = relativeRemotePath+ftpFile.getName().toString();
            File file1 = new File(newRelativeLocalPath);
            if (!file1.exists()){
                file1.mkdirs();
            }
            try{
                newRelativeLocalPath = newRelativeRemotePath+File.separator;
                newRelativeRemotePath = newRelativeRemotePath+File.separator;
                String currentDic = ftpFile.getName().toString();
                boolean  changeToCurrentDic = ftpClient.changeWorkingDirectory(currentDic);
                if(changeToCurrentDic){
                    //递归再次
                    FTPFile[] ftpFiles = ftpClient.listFiles();
                    for(FTPFile f : ftpFiles){
                        downloadFile(f,newRelativeLocalPath,newRelativeLocalPath);
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
                logger.error(e.getMessage());
            }

        }
    }


    public static void startDownLoadService(Ftp f, String localPath, String remotePath) throws Exception {
        if(FileFtpUtil.connectFtp(f)){
            try{
                FTPFile[] ftpFiles = null;
                boolean changeDir = ftpClient.changeWorkingDirectory(remotePath);
                if(changeDir){
                    ftpClient.setControlEncoding("utf-8");
                    ftpFiles = ftpClient.listFiles();
                    for(FTPFile f1 : ftpFiles){
                        try{
                            downloadFile(f1,localPath,remotePath);
                        }catch (Exception e){
                            e.printStackTrace();
                            logger.error(e.getMessage());
                            logger.error("文件:"+f1.getName()+"下载失败");
                        }

                    }
                }
            }catch(Exception e){
                e.printStackTrace();
                logger.error(e.getMessage());
            }finally {
                closeFtp(ftpClient);
            }
        }else{
            logger.error("连接ftp服务器失败");
            throw new RuntimeException("连接ftp服务器失败");

        }
    }

    public static  boolean createDirectorys(String dirPath) throws IOException {
        boolean status = true;
        System.out.println(dirPath);
        String directory = dirPath.substring(0, dirPath.lastIndexOf("\\")+1);
//        ftpClient.makeDirectory(ROOT);
//        if(!directory.equalsIgnoreCase("/")&&!ftpClient.changeWorkingDirectory(directory)){
//        System.out.println(existDirectory(dirPath));
        if(!existDirectory(dirPath)){
//        if(!directory.equalsIgnoreCase("/")&&!ftpClient.changeWorkingDirectory(new String(directory.getBytes("GBK"),"iso-8859-1"))){
            //如果远程目录不存在，则递归创建远程服务器目录
            System.out.println("in------");
            int start=0;
            int end = 0;
            if(directory.startsWith("\\")){
                start = 1;
            }else{
                start = 0;
            }
            end = directory.lastIndexOf(("\\"));
            System.out.println("end = "+end);
            while(true){
                String subDirectory = new String(dirPath.substring(start, end));
                System.out.println("创建目录执行了吗啊");

//                String subDirectory = new String(dirPath.substring(start,end).getBytes("GBK"),"iso-8859-1");
                if(!ftpClient.changeWorkingDirectory(subDirectory)){
                    if(ftpClient.makeDirectory(subDirectory)){
                        ftpClient.changeWorkingDirectory(subDirectory);
                    }else {
                        System.out.println("创建目录失败");
                        return false;
                    }
                }

                start = end + 1;
                end = directory.indexOf("\\",start);

                //检查所有目录是否创建完毕
                if(end <= start){
                    break;
                }
            }
        }
        return status;
    }
    public static  boolean existDirectory(String path) throws IOException {
        boolean flag = false;
        try {

            ftpClient.changeWorkingDirectory("/");
//            System.out.println("回到根目录：" + ftpClient.changeWorkingDirectory("/"));
//        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
//        System.out.println(ftpFileArr.length);
//            flag = false;
//            ftpClient.makeDirectory(this.ftpModel.getRemoteDir());
            if(ftpClient.changeWorkingDirectory(path)){
                flag = true;
            }
        } catch (IOException e) {
            flag = false;
            e.printStackTrace();
        }
        return flag;
    }
}
