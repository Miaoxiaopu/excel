package com.fileinfo.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.fileinfo.condition.FileCondition;
import com.fileinfo.config.MinioProps;
import com.fileinfo.entity.FileInfo;
import com.fileinfo.service.IFileInfoService;
import com.fileinfo.type.MessageEnumType;
import com.fileinfo.utils.ExcelUtils;
import com.fileinfo.utils.FileUtils;
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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController
public class ExcelController {

    private IFileInfoService fileInfoService;

    private MinioUtils minioUtils;

    private MinioProps minioProps;

    @Autowired
    public ExcelController(IFileInfoService fileInfoService, MinioUtils minioUtils, MinioProps minioProps) {
        this.fileInfoService = fileInfoService;
        this.minioUtils = minioUtils;
        this.minioProps = minioProps;
    }

    @Transactional(rollbackFor = {Exception.class})
    @RequestMapping("/uploadExcel")
    public String uploadExcel(@RequestParam("file") MultipartFile file) throws Exception {
        QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<>();
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
        String fileType = getFileType(originalFilename);
        if ("xlsx".equalsIgnoreCase(fileType) || "xls".equalsIgnoreCase(fileType)) {
            String content = ExcelUtils.excelToHtml(file);
            fileInfo.setContent(content.getBytes(StandardCharsets.UTF_8));
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
    public void downExcel(@RequestParam("fileId") long fileId, HttpServletResponse response) throws IOException {
        FileInfo fileInfo = fileInfoService.getById(fileId);
        if (fileInfo == null) {
            throw new RuntimeException("文件不存在");
        }
        String fileName1 = fileInfo.getFileName();
        response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName1.getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        response.setContentType("application/octet-stream");
        minioUtils.download(minioProps.getBucketName(), fileName1, response.getOutputStream());
    }

    @RequestMapping("/downByPart")
    public void downByPart(@RequestParam("fileId") long fileId, HttpServletRequest request, HttpServletResponse response) {
        FileInfo fileInfo = fileInfoService.getById(fileId);
        if (fileInfo == null) {
            throw new RuntimeException("文件不存在");
        }
        String dbFileName = fileInfo.getFileName();
        int fileSize = fileInfo.getFileSize();
        String range = request.getHeader("Range");
        if (range.startsWith("bytes") && range.contains("-")) {
            FileUtils.handleRange(request, fileSize, (start, end) -> {
                InputStream fileInputStream = minioUtils.getFileInputStream(minioProps.getBucketName(), dbFileName);
                FileUtils.downloadByPart(fileInputStream, response, start, end, fileSize);
                Cookie cookie = new Cookie("content_range", String.valueOf(end + 1));
                response.addCookie(cookie);
            });
        } else {
            FileUtils.download(minioUtils.getFileInputStream(minioProps.getBucketName(), dbFileName), response);
        }
    }

    @RequestMapping("/getHtmlData")
    public String getHtmlData(@RequestParam("fileName") String fileName, @RequestParam(value = "fileContent", required = false) String content) {
        QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bucket_name", minioProps.getBucketName());
        queryWrapper.like("file_name", fileName);
        queryWrapper.select("content");
        List<FileInfo> fileList = fileInfoService.list(queryWrapper);
        handleFileList(fileList, content);
        return fileList.get(0).getContentStr();
    }

    @RequestMapping("/getHtmlDataById")
    public String getHtmlDataById(@RequestParam("fileId") long fileId, @RequestParam(value = "fileContent", required = false) String content) {
        FileInfo fileInfo = fileInfoService.getById(fileId);
        ArrayList<FileInfo> fileList = new ArrayList<>();
        fileList.add(fileInfo);
        handleFileList(fileList, content);
        return fileList.get(0).getContentStr();
    }

    // 根据输入的文件内的关键字或者文件名查询到文件信息
    @RequestMapping("/getFileInfo")
    public FileCondition getFileInfo(@RequestBody FileCondition fileCondition, Model model) {
        QueryWrapper<FileInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bucket_name", minioProps.getBucketName());
        if (StringUtils.isNotBlank(Objects.requireNonNull(fileCondition,
                MessageEnumType.SEARCH_CONDITION_NULL.getMessage()).getFileContent())) {
            queryWrapper.like("content", Objects.requireNonNull(fileCondition,
                    MessageEnumType.SEARCH_CONDITION_NULL.getMessage()).getFileContent());
        }
        if (StringUtils.isNotBlank(Objects.requireNonNull(fileCondition,
                MessageEnumType.SEARCH_CONDITION_NULL.getMessage()).getFileName())) {
            queryWrapper.like("file_name", Objects.requireNonNull(fileCondition,
                    MessageEnumType.SEARCH_CONDITION_NULL.getMessage()).getFileName());
        }
        // 查询除了content的其他字段
        queryWrapper.select(FileInfo.class, (column) -> !column.getColumn().equals("content"));
        queryWrapper.orderByDesc("create_time");
        FileCondition res = fileInfoService.page(fileCondition, queryWrapper);
        model.addAttribute("data", res);
        return res;
    }

    private String getFileType(String filename) {
        int index;
        if ((index = filename.lastIndexOf(".")) == -1) {
            return "unknow";
        }
        return filename.substring(index + 1);
    }

    private void handleFileList(List<FileInfo> fileList, String fileContent) {
        if (!CollectionUtils.isEmpty(fileList)) {
            for (FileInfo fileInfo : fileList) {
                byte[] fileByteContent = fileInfo.getContent();
                if (fileByteContent == null) {
                    continue;
                }
                String contentStr = new String(fileByteContent, StandardCharsets.UTF_8);
                if (StringUtils.isNotBlank(fileContent)) {
                    fileInfo.setContentStr(contentStr.replace(fileContent,
                            "<b style=color:red;>" + fileContent + "</b>"));
                } else {
                    fileInfo.setContentStr(contentStr);
                }
            }
        }
    }
}
