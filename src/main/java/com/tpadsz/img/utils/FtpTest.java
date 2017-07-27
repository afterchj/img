package com.tpadsz.img.utils;

import java.io.*;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.junit.Test;

/**
 * * Created by hongjian.chen on 2017/4/13.
 *  * 上传,下载,删除,文件（可对文件进行重命名）
 *  * @param remotePath FTP服务器保存目录
 *  * @param fileName 上传到FTP服务器后的文件名称
 *  * @param localFilename 待上传文件的名称（绝对地址）
 *  * @return
 */
public class FtpTest {

    public static boolean uploadFtp(String remotePath, String fileName, InputStream inputStream) {
        boolean flag = false;
        FTPClient ftpClient = new FTPClient();
        BufferedInputStream bufferedInputStream = null;
        try {
            ftpClient = FtpUtil.getFtpClient();
            ftpClient.makeDirectory(remotePath);
            ftpClient.changeWorkingDirectory(remotePath);
            bufferedInputStream = new BufferedInputStream(inputStream);
            ftpClient.storeFile(new String(fileName.getBytes("GBK"), "iso-8859-1"), bufferedInputStream);
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FtpUtil.closeFtp(ftpClient);
            try {
                inputStream.close();
                bufferedInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static boolean deleteDirectory(String remotePath) {
        boolean flag = false;
        FTPClient ftp = new FTPClient();
        try {
            ftp = FtpUtil.getFtpClient();
            FTPFile[] allFile = ftp.listFiles(remotePath);
            for (int i = 0; i < allFile.length; i++) {
                String fileName = remotePath + File.separator + allFile[i].getName();
                if (allFile[i].isFile()) {
                    ftp.deleteFile(fileName);
                    System.out.println("remoteFileName:" + allFile[i].getName());
                } else if (allFile[i].isDirectory()) {
                    deleteDirectory(fileName);
                }
            }
            // 每次删除文件夹以后就去查看该文件夹下面是否还有文件，没有就删除该空文件夹
            FTPFile[] files2 = ftp.listFiles(remotePath);
            if (files2.length == 0) {
                flag = ftp.removeDirectory(remotePath);
            } else {
                flag = false;
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FtpUtil.closeFtp(ftp);
        }
        return flag;
    }

    public static boolean downloadFtp(String remotePath, String filename, String localpath) {
        boolean flag = false;
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient = FtpUtil.getFtpClient();
            ftpClient.changeWorkingDirectory(remotePath);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                if (filename.equalsIgnoreCase(file.getName())) {
                    File localFile = new File(localpath + "/" + file.getName());
                    System.out.println("localFile="+localpath + "/" + file.getName());
                    OutputStream os = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(file.getName(), os);
                    os.close();
                }
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            FtpUtil.closeFtp(ftpClient);
        }
        return flag;
    }

    /***
     * @上传文件夹
     * */
    private static void upload(File file) {
        FTPClient ftp = new FTPClient();
        try {
            if (file.isDirectory()) {
                ftp = FtpUtil.getFtpClient();
                ftp.makeDirectory(file.getName());
                ftp.changeWorkingDirectory(file.getName());
                String[] files = file.list();
                for (int i = 0; i < files.length; i++) {
                    File file1 = new File(file.getPath() + "\\" + files[i]);
                    if (file1.isDirectory()) {
                        upload(file1);//递归上传文件夹下的文件
                        ftp.changeToParentDirectory();
                    } else {
                        File file2 = new File(file.getPath() + "\\" + files[i]);
                        FileInputStream input = new FileInputStream(file2);
                        ftp.storeFile(file2.getName(), input);
                        input.close();
                    }
                }
            } else {
                File file2 = new File(file.getPath());
                FileInputStream input = new FileInputStream(file2);
                ftp.storeFile(file2.getName(), input);
                input.close();
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            FtpUtil.closeFtp(ftp);
        }
    }

    public static boolean downloadFile(String remoteFileName, String localDires, String remotePath) {
        boolean success = false;
        String strFilePath = localDires + remoteFileName;
        System.out.println("下载后文件夹：" + strFilePath);
        BufferedOutputStream buffOutStream = null;
        //FTPClient ftp =null;
        try {
            FTPClient ftp = FtpUtil.getFtpClient();
            ftp.changeWorkingDirectory(remotePath);
            //buffOutStream = new BufferedOutputStream(new FileOutputStream(strFilePath));
            File localFile = new File(strFilePath);
            OutputStream os = new FileOutputStream(localFile);
            success = ftp.retrieveFile(remoteFileName, os);
            if (success == true) {
                System.out.println((remoteFileName + "下载成功！"));
                return success;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            FtpUtil.closeFtp(ftp);
//            if (null != buffOutStream) {
//                try {
//                    buffOutStream.flush();
//                    buffOutStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
        }
        if (success == false) {
            System.out.println(remoteFileName + "下载失败!!!");
        }
        return success;
    }

    /***
     * @下载文件夹
     * @param localDirectoryPath 本地文件
     * @param remoteDirectory 远程文件
     * */
    public static boolean downLoadDirectory(String localDirectoryPath, String remoteDirectory) {
        FTPClient ftp = new FTPClient();
        try {
            ftp = FtpUtil.getFtpClient();
            String fileName = new File(remoteDirectory).getName();
            System.out.println("new File(remoteDirectory).getName()=" + fileName);
            localDirectoryPath = localDirectoryPath + fileName;
            new File(localDirectoryPath).mkdirs();
            System.out.println("localDirectoryPath=" + localDirectoryPath);
            FTPFile[] allFile = ftp.listFiles(remoteDirectory);
            for (int i = 0; i < allFile.length; i++) {
                System.out.println("allFile[i].getName()=" + allFile[i].getName());
                if (!allFile[i].isDirectory()) {
                    downloadFile(allFile[i].getName(), localDirectoryPath, remoteDirectory);
                }
            }
            for (int i = 0; i < allFile.length; i++) {
                if (allFile[i].isDirectory()) {
                    String strremoteDirectoryPath = remoteDirectory + "/" + allFile[i].getName();
                    downLoadDirectory(localDirectoryPath, strremoteDirectoryPath);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            FtpUtil.closeFtp(ftp);
        }
        return true;
    }

    public static void main(String[] args) throws Exception {
//        deleteFile("test/upload/","test.txt");
//        System.out.println(deleteDirectory("mytest/upload"));
        //uploadFtp("共享文件夹/","result.txt", new FileInputStream("C:\\test\\readme.txt"));
        //downloadFtp("共享文件夹/", "result.txt", "D:/temp/download");
        downloadFile("sayHi.txt", "D:/temp/","共享文件夹/test/");
        //upload(new File("C:\\FTPUpload"));
        //downLoadDirectory("C:/test/download","共享文件夹/mytest");
    }

    @Test
    public void test(String a) {
        System.out.println("asda");
    }
}