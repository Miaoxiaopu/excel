package com.fileinfo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fileinfo.condition.FileCondition;
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
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

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
        String originalFilename = file.getOriginalFilename();
        queryWrapper.eq("bucket_name", minioProps.getBucketName());
        queryWrapper.eq("file_name", originalFilename);
        List<FileInfo> fileList = fileInfoService.list(queryWrapper);
        // 文件是否已经上传过了
        if (!CollectionUtils.isEmpty(fileList)) {
            return MessageEnumType.FILE_EXIST.getMessage();
        }
        // 文件类型是不是excel
        FileInfo fileInfo = new FileInfo();
        String fileType = originalFilename.substring(originalFilename.lastIndexOf("."));
        if("xlsx".equalsIgnoreCase(fileType) || "xls".equalsIgnoreCase(fileType) ){
            String content = ExcelUtils.excelToHtml(file);
            fileInfo.setContent(content.getBytes("utf-8"));
        }
        fileInfo.setFileName(originalFilename);
        fileInfo.setFileType(fileType.toLowerCase());
        fileInfo.setBucketName(minioProps.getBucketName());
        // 文件信息存入数据库
        fileInfoService.save(fileInfo);
        // 文件上传到本地
        minioUtils.upload(minioProps.getBucketName(), originalFilename, file);
        return MessageEnumType.SUCCESS.getMessage();
    }

    @RequestMapping("/downExcel")
    public void downExcel(@RequestParam(value = "fileName",required = false) String fileName,@RequestParam("fileId") long fileId, HttpServletResponse response) throws IOException {
//        String utf8Filename = new String(fileName.getBytes(), StandardCharsets.UTF_8);
        FileInfo fileInfo = fileInfoService.getById(fileId);
        if(fileInfo == null){
            throw new RuntimeException("文件不存在");
        }
        String fileName1 = fileInfo.getFileName();
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName1.getBytes("utf-8"), "ISO8859-1"));
        response.setContentType("\"application/octet-stream\"");
        minioUtils.download(minioProps.getBucketName(), fileName1, response.getOutputStream());
    }

    @RequestMapping("/getHtmlData")
    public String getHtmlData(@RequestParam("fileName") String fileName,@RequestParam(value = "fileContent",required = false) String content){
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bucket_name", minioProps.getBucketName());
        queryWrapper.like("file_name", fileName);
        queryWrapper.select("content");
        List<FileInfo> fileList = fileInfoService.list(queryWrapper);
        handleFileList(fileList,content);
        return fileList.get(0).getContentStr();
    }
    @RequestMapping("/getHtmlDataById")
    public String getHtmlDataById(@RequestParam("fileId") long fileId,@RequestParam(value = "fileContent",required = false) String content){
        FileInfo fileInfo = fileInfoService.getById(fileId);
        ArrayList<FileInfo> fileList = new ArrayList<>();
        fileList.add(fileInfo);
        handleFileList(fileList,content);
        return fileList.get(0).getContentStr();
    }

    // 根据输入的文件内的关键字或者文件名查询到文件信息
    @RequestMapping("/getFileInfo")
    public IPage getFileInfo(@RequestBody FileCondition fileCondition, Model model) {
        QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bucket_name", minioProps.getBucketName());
        if(StringUtils.isNotBlank(Objects.requireNonNull(fileCondition,
                MessageEnumType.SEARCH_CONDITION_NULL.getMessage()).getFileContent())){
            queryWrapper.like("content", Objects.requireNonNull(fileCondition,
                    MessageEnumType.SEARCH_CONDITION_NULL.getMessage()).getFileContent());
        }
        if(StringUtils.isNotBlank(Objects.requireNonNull(fileCondition,
                MessageEnumType.SEARCH_CONDITION_NULL.getMessage()).getFileName())){
            queryWrapper.like("file_name",Objects.requireNonNull(fileCondition,
                    MessageEnumType.SEARCH_CONDITION_NULL.getMessage()).getFileName());
        }
        // 查询除了content的其他字段
        queryWrapper.select(FileInfo.class, (column) -> !column.getColumn().equals("content"));
        queryWrapper.orderByDesc("create_time");
        FileCondition res = fileInfoService.page(fileCondition, queryWrapper);
        model.addAttribute("data",res);
        return res;
    }

    @RequestMapping("/createEsIndex")
    public void createEsIndex(@RequestParam("idxName") String idxName) {
        baseElasticUtils.createIndex(idxName, "");
    }

    private void handleFileList(List<FileInfo> fileList,String fileContent) {
        if (!CollectionUtils.isEmpty(fileList)) {
            Consumer<FileInfo> fileInfoConsumer = item -> {
                try {
                    String contentStr = new String(item.getContent(), "utf-8");
                    if(StringUtils.isNotBlank(fileContent)){
                        item.setContentStr(contentStr.replace(fileContent,
                                "<b style=color:red;>"+fileContent+"</b>"));
                    }else{
                        item.setContentStr(contentStr);
                    }
                } catch (UnsupportedEncodingException e1) {
                    log.error(MessageEnumType.NOT_SUPPORT_UTF8.getMessage());
                    throw new RuntimeException(MessageEnumType.NOT_SUPPORT_UTF8.getMessage());
                }
            }; fileList.forEach(fileInfoConsumer);
        }
    }
}
