package com.fileinfo.utils;

import io.minio.MinioClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@Data
@Slf4j
public final class MinioUtils {

    @Autowired
    private MinioClient minioClient;

    /**
     * 下载桶内某个文件
     *
     * @param bucketName 桶名
     * @param fileName   文件名
     * @throws Exception
     */
    public void download(String bucketName, String fileName, OutputStream out){
        InputStream in = null;
        try {
            byte[] bytes = new byte[1024];
            int length = -1;
            in = minioClient.getObject(bucketName, fileName);
            while((length = in.read(bytes)) != -1){
                out.write(bytes,0,length);
            }
            out.flush();
        } catch (Exception e) {
            log.error("从minio下载失败" + e.getMessage());
        }finally {
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 上传
     *
     * @param bucketName 桶名
     * @param ObjectName 对象名
     * @param file
     * @throws Exception
     */
    public void upload(String bucketName, String ObjectName, MultipartFile file) {
        try {
            createBucket(bucketName);
            minioClient.putObject(bucketName, ObjectName, file.getInputStream(), file.getContentType());
        } catch (Exception e) {
            log.error("minio上传失败" + e.getMessage());
        }
    }

    /**
     * 创建桶
     *
     * @param bucketName 桶名
     * @throws Exception
     */
    public void createBucket(String bucketName) {
        try {
            if (!minioClient.bucketExists(bucketName)) {
                minioClient.makeBucket(bucketName);
            }
        } catch (Exception e) {
            log.error("minio创建桶失败" + e.getMessage());
        }
    }

    /**
     * 获取文件存储地址
     * @param bucketName 桶名
     * @param fileName 文件名
     * @return 文件存储地址
     */
    public String getObjectUrl(String bucketName,String fileName){
        try {
            return minioClient.getObjectUrl(bucketName,fileName);
        } catch (Exception e) {
            log.error("获取文件地址失败" + e.getMessage());
        }
        return "";
    }
}
