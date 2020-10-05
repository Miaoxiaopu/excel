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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
        String content = ExcelUtils.excelPlusToHtml(file);
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileName(file.getOriginalFilename());
        fileInfo.setFileType(file.getContentType());
        fileInfo.setContent(content.getBytes("utf-8"));
        fileInfo.setBucketName(minioProps.getBucketName());
        // 文件信息存入数据库
        fileInfoService.save(fileInfo);
//        ElasticEntity elasticEntity = new ElasticEntity();
//        Map hashMap = new HashMap();
//        hashMap.put(FILEINFO, file.getOriginalFilename() + "_" + content);
//        elasticEntity.setData(hashMap);
        // 文件内容以字符串形式存入elasticsearch
//        baseElasticUtils.insertOrUpdateOne(elasticProps.getIdxName(), elasticEntity);
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
    public String getHtmlData(@RequestParam("fileName") String fileName) throws IOException {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_name", fileName);
        queryWrapper.eq("bucket_name", minioProps.getBucketName());
        List<FileInfo> fileList = fileInfoService.list(queryWrapper);
        return CollectionUtils.isEmpty(fileList) ? "" : new String(fileList.get(0).getContent(), "utf-8");
    }
    // 根据输入的文件内的关键字查询到文件名
    @RequestMapping("/getFileName")
    public List getFileName(@RequestParam("fileContent") String fileContent) {
//        List list = baseElasticUtils.search(elasticProps.getIdxName(),
//                baseElasticUtils.initSearchSourceBuilder(QueryBuilders.boolQuery().
//                        must(QueryBuilders.wildcardQuery(FILEINFO, "*" + fileContent + "*"))),
//                HashMap.class,new ListEsResponseHandler());
//        return list;
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.like("content",fileContent);
        queryWrapper.eq("bucket_name", minioProps.getBucketName());
        return fileInfoService.list(queryWrapper);
    }
    @RequestMapping
    public void createEsIndex(@RequestParam("idxName") String idxName){
        baseElasticUtils.createIndex(idxName,"");
    }
}
