package com.fileinfo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fileinfo.config.ElasticProps;
import com.fileinfo.config.MinioProps;
import com.fileinfo.entity.FileInfo;
import com.fileinfo.service.IFileInfoService;
import com.fileinfo.type.MessageEnumType;
import com.fileinfo.utils.BaseElasticUtils;
import com.fileinfo.utils.ExcelUtils;
import com.fileinfo.utils.MinioUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
@Slf4j
@RestController
public class ExcelController {

    private static final String FILEINFO = "fileInfo";

    @Autowired
    private IFileInfoService fileInfoService;

    @Autowired
    private MinioUtils minioUtils;

    @Autowired
    private MinioProps minioProps;

    @Autowired
    private BaseElasticUtils baseElasticUtils;

    @Autowired
    private ElasticProps elasticProps;

    @Transactional
    @RequestMapping("/uploadExcel")
    public String uploadExcel(@RequestParam("file") MultipartFile file) throws IOException {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bucket_name", minioProps.getBucketName());
        queryWrapper.eq("file_name", file.getOriginalFilename());
        List<FileInfo> fileList = fileInfoService.list(queryWrapper);
        if(!CollectionUtils.isEmpty(fileList)){
            return MessageEnumType.FILE_EXIST.getMessage();
        }
        String content = ExcelUtils.excelPlusToHtml(file);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(file.getOriginalFilename());
        fileInfo.setFileType(file.getContentType());
        fileInfo.setContent(content.getBytes("utf-8"));
        fileInfo.setBucketName(minioProps.getBucketName());
        // 文件信息存入数据库
        fileInfoService.save(fileInfo);
        // 文件上传到本地
        minioUtils.upload(minioProps.getBucketName(), file.getOriginalFilename(), file);
        return MessageEnumType.SUCCESS.getMessage();
    }

    @RequestMapping("/downExcel")
    public String downExcel(@RequestParam("fileName") String fileName, HttpServletResponse response) throws IOException {
        String utf8Filename = new String(fileName.getBytes(), StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(utf8Filename.getBytes("utf-8"), "ISO8859-1"));
        minioUtils.download(minioProps.getBucketName(), utf8Filename, response.getOutputStream());
        return MessageEnumType.SUCCESS.getMessage();
    }

    @RequestMapping("/getHtmlData")
    public List getHtmlData(@RequestParam("fileName") String fileName) throws IOException {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bucket_name", minioProps.getBucketName());
        queryWrapper.like("file_name", fileName);
        List<FileInfo> fileList = fileInfoService.list(queryWrapper);
        handleFileList(fileList);
        return fileList;
    }

    // 根据输入的文件内的关键字查询到文件信息
    @RequestMapping("/getFileInfo")
    public List getFileInfo(@RequestParam("fileContent") String fileContent){
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bucket_name", minioProps.getBucketName());
        queryWrapper.like("content",fileContent);
        List<FileInfo> fileList = fileInfoService.list(queryWrapper);
        handleFileList(fileList);
        return fileList;
    }
    @RequestMapping
    public void createEsIndex(@RequestParam("idxName") String idxName){
        baseElasticUtils.createIndex(idxName,"");
    }

    private void handleFileList(List<FileInfo> fileList){
        if(!CollectionUtils.isEmpty(fileList)){
            fileList.forEach(item ->{
                try {
                    item.setContentStr(new String(item.getContent(), "utf-8"));
                } catch (UnsupportedEncodingException e1) {
                    log.error("要查找的文件内容编码不支持转换为utf-8编码!");
                    throw new RuntimeException("要查找的文件内容编码不支持转换为utf-8编码!");
                }
            });
        }
    }
}
