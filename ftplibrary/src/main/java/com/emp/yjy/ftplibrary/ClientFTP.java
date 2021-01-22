package com.emp.yjy.ftplibrary;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * @author Created by LRH
 * @description FTP客户端封装
 * @date 2021/1/15 15:17
 */
public class ClientFTP {
    private static final String CHARSET_UTF8 = "UTF-8";
    private static final String CHARSET_ISO_8859_1 = "ISO-8859-1";

    private String mFtpHost;
    private int mFtpPort;
    private String mFtpUserName = "anonymous";
    private String mFtpPassword;
    private FTPClient mFTPClient;
    private int mConnectTimeout = 5000;

    public ClientFTP(String ftpHost, int ftpPort, String ftpUserName, String ftpPassword) {
        mFtpHost = ftpHost;
        mFtpPort = ftpPort;
        if (ftpUserName != null && !"".equals(ftpUserName)) {
            mFtpUserName = ftpUserName;
        }
        mFtpPassword = ftpPassword;
        mFTPClient = new FTPClient();
    }

    /**
     * 连接到ftp服务器
     *
     * @return
     * @throws IOException
     */
    public void connect2Server() throws IOException {
        mFTPClient.connect(mFtpHost, mFtpPort);
        // 设置用户名和密码
        mFTPClient.login(mFtpUserName, mFtpPassword);
        // 设置连接超时时间,5000毫秒
        mFTPClient.setConnectTimeout(mConnectTimeout);
        // 设置中文编码集，防止中文乱码
        mFTPClient.setControlEncoding(CHARSET_UTF8);
        if (!FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
            throw new IOException("connect2Server error: " + mFTPClient.getReplyCode());
        }
    }


    /**
     * 下载FTP下指定文件（测试通过）
     *
     * @param filePath      FTP文件路径
     * @param fileName      文件名
     * @param downPath      下载保存的目录
     * @param deleteFtpFile 是是否下载源文件
     * @return
     */
    public boolean downLoadFile(String filePath, String fileName,
                                String downPath, boolean deleteFtpFile) {
        // 默认失败
        boolean flag = false;
        try {
            // 跳转到文件目录
            mFTPClient.changeWorkingDirectory(filePath);
            // 获取目录下文件集合
            mFTPClient.enterLocalPassiveMode();
            FTPFile[] files = mFTPClient.listFiles();
            for (FTPFile file : files) {
                // 取得指定文件并下载
                if (file.getName().equals(fileName)) {
                    File downFile = new File(downPath + File.separator
                            + file.getName());
                    OutputStream out = new FileOutputStream(downFile);
                    // 绑定输出流下载文件,需要设置编码集，不然可能出现文件为空的情况
                    flag = mFTPClient.retrieveFile(new String(file.getName().getBytes(CHARSET_UTF8), CHARSET_ISO_8859_1), out);
                    // 下载成功删除文件,看项目需求
                    if (deleteFtpFile) {
                        mFTPClient.deleteFile(new String(fileName.getBytes(CHARSET_UTF8), CHARSET_ISO_8859_1));
                    }
                    out.flush();
                    out.close();
                    if (flag) {
                        System.out.println("下载成功");
                    } else {
                        System.out.println("下载失败");
                    }
                }
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return flag;
    }


    /**
     * 文件上传（未测试）
     *
     * @param uploadFilePath
     * @param ftpPath
     * @return
     */
    public boolean uploadFile(String uploadFilePath, String ftpPath) {
        boolean flag = false;
        InputStream in = null;
        try {
            // 设置PassiveMode传输
            mFTPClient.enterLocalPassiveMode();
            //设置二进制传输，使用BINARY_FILE_TYPE，ASC容易造成文件损坏
            mFTPClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            //判断FPT目标文件夹时候存在不存在则创建
            if (!mFTPClient.changeWorkingDirectory(ftpPath)) {
                mFTPClient.makeDirectory(ftpPath);
            }
            //跳转目标目录
            mFTPClient.changeWorkingDirectory(ftpPath);

            //上传文件
            File file = new File(uploadFilePath);
            in = new FileInputStream(file);
            String tempName = ftpPath + File.separator + file.getName();
            flag = mFTPClient.storeFile(new String(tempName.getBytes(CHARSET_UTF8), CHARSET_ISO_8859_1), in);
            if (flag) {
                System.out.println("上传成功");
            } else {
                System.out.println("上传失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传失败");
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }


    /**
     * FPT上文件的复制(未测试)
     *
     * @param olePath  原文件地址
     * @param newPath  新保存地址
     * @param fileName 文件名
     * @return
     */
    public boolean copyFile(String olePath, String newPath, String fileName) {
        boolean flag = false;
        try {
            // 跳转到文件目录
            mFTPClient.changeWorkingDirectory(olePath);
            //设置连接模式，不设置会获取为空
            mFTPClient.enterLocalPassiveMode();
            // 获取目录下文件集合
            FTPFile[] files = mFTPClient.listFiles();
            ByteArrayInputStream in = null;
            ByteArrayOutputStream out = null;
            for (FTPFile file : files) {
                // 取得指定文件并下载
                if (file.getName().equals(fileName)) {
                    //读取文件，使用下载文件的方法把文件写入内存,绑定到out流上
                    out = new ByteArrayOutputStream();
                    mFTPClient.retrieveFile(new String(file.getName().getBytes(CHARSET_UTF8), CHARSET_ISO_8859_1), out);
                    in = new ByteArrayInputStream(out.toByteArray());
                    //创建新目录
                    mFTPClient.makeDirectory(newPath);
                    //文件复制，先读，再写
                    //二进制
                    mFTPClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                    flag = mFTPClient.storeFile(newPath + File.separator + (new String(file.getName().getBytes(CHARSET_UTF8), CHARSET_ISO_8859_1)), in);
                    out.flush();
                    out.close();
                    in.close();
                    if (flag) {
                        System.out.println("转存成功");
                    } else {
                        System.out.println("复制失败");
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("复制失败");
        }
        return flag;
    }

    /**
     * 实现文件的移动，这里做的是一个文件夹下的所有内容移动到新的文件，
     * 如果要做指定文件移动，加个判断判断文件名
     * 如果不需要移动，只是需要文件重命名，可以使用ftp.rename(oldName,newName)
     * （未测试）
     *
     * @param oldPath
     * @param newPath
     * @return
     */
    public boolean moveFile(String oldPath, String newPath) {
        boolean flag = false;

        try {
            mFTPClient.changeWorkingDirectory(oldPath);
            mFTPClient.enterLocalPassiveMode();
            //获取文件数组
            FTPFile[] files = mFTPClient.listFiles();
            //新文件夹不存在则创建
            if (!mFTPClient.changeWorkingDirectory(newPath)) {
                mFTPClient.makeDirectory(newPath);
            }
            //回到原有工作目录
            mFTPClient.changeWorkingDirectory(oldPath);
            for (FTPFile file : files) {

                //转存目录
                flag = mFTPClient.rename(new String(file.getName().getBytes("UTF-8"), "ISO-8859-1"), newPath + File.separator + new String(file.getName().getBytes("UTF-8"), "ISO-8859-1"));
                if (flag) {
                    System.out.println(file.getName() + "移动成功");
                } else {
                    System.out.println(file.getName() + "移动失败");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("移动文件失败");
        }
        return flag;
    }

    /**
     * 删除FTP上指定文件夹下文件及其子文件方法，添加了对中文目录的支持（未测试）
     *
     * @param FtpFolder 需要删除的文件夹
     * @return
     */
    public boolean deleteByFolder(String FtpFolder) {
        boolean flag = false;
        try {
            mFTPClient.changeWorkingDirectory(new String(FtpFolder.getBytes("UTF-8"), "ISO-8859-1"));
            mFTPClient.enterLocalPassiveMode();
            FTPFile[] files = mFTPClient.listFiles();
            for (FTPFile file : files) {
                //判断为文件则删除
                if (file.isFile()) {
                    mFTPClient.deleteFile(new String(file.getName().getBytes("UTF-8"), "ISO-8859-1"));
                }
                //判断是文件夹
                if (file.isDirectory()) {
                    String childPath = FtpFolder + File.separator + file.getName();
                    //递归删除子文件夹
                    deleteByFolder(childPath);
                }
            }
            //循环完成后删除文件夹
            flag = mFTPClient.removeDirectory(new String(FtpFolder.getBytes("UTF-8"), "ISO-8859-1"));
            if (flag) {
                System.out.println(FtpFolder + "文件夹删除成功");
            } else {
                System.out.println(FtpFolder + "文件夹删除成功");
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("删除失败");
        }
        return flag;

    }

    /**
     * 遍历解析文件夹下所有文件（未测试）
     *
     * @param folderPath 需要解析的的文件夹
     * @return
     */
    public boolean readFileByFolder(String folderPath) {
        boolean flage = false;
        try {
            mFTPClient.changeWorkingDirectory(new String(folderPath.getBytes("UTF-8"), "ISO-8859-1"));
            //设置FTP连接模式
            mFTPClient.enterLocalPassiveMode();
            //获取指定目录下文件文件对象集合
            FTPFile files[] = mFTPClient.listFiles();
            InputStream in = null;
            BufferedReader reader = null;
            for (FTPFile file : files) {
                //判断为txt文件则解析
                if (file.isFile()) {
                    String fileName = file.getName();
                    if (fileName.endsWith(".txt")) {
                        in = mFTPClient.retrieveFileStream(new String(file.getName().getBytes("UTF-8"), "ISO-8859-1"));
                        reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                        String temp;
                        StringBuffer buffer = new StringBuffer();
                        while ((temp = reader.readLine()) != null) {
                            buffer.append(temp);
                        }
                        if (reader != null) {
                            reader.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                        //ftp.retrieveFileStream使用了流，需要释放一下，不然会返回空指针
                        mFTPClient.completePendingCommand();
                        //这里就把一个txt文件完整解析成了个字符串，就可以调用实际需要操作的方法
                        System.out.println(buffer.toString());
                    }
                }
                //判断为文件夹，递归
                if (file.isDirectory()) {
                    String path = folderPath + File.separator + file.getName();
                    readFileByFolder(path);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("文件解析失败");
        }

        return flage;

    }


    /**
     * 关闭FTP方法
     *
     * @return
     */
    public boolean close() {
        try {
            mFTPClient.logout();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        } finally {
            if (mFTPClient.isConnected()) {
                try {
                    mFTPClient.disconnect();
                } catch (IOException ioe) {
                    System.out.println(ioe.getMessage());
                    return false;
                }
            }
        }
        return true;
    }


    /**
     * 测试代码
     *
     * @param args
     */
    public static void main(String[] args) {
        ClientFTP clientFTP = new ClientFTP("10.200.12.120", 21, "", "");
        try {
            clientFTP.connect2Server();
            System.out.println("连接ftp服务器成功");
            boolean ret = clientFTP.downLoadFile("/ftptest.log", "ftptest.log", "D:\\work\\project\\vaccination\\souce_code\\vaccination\\ftplibrary\\src\\main\\java\\com\\emp\\yjy\\ftplibrary", false);
            System.out.println("ret = " + ret);
            boolean close = clientFTP.close();
            System.out.println("关闭ftp：" + close);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }


}
