package com.fileinfo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class FileInfo {
    @TableField(value = "content")
    private byte[] content;
    @TableField(value = "file_id")
    private long fileId;
    @TableField(value = "file_name")
    private String fileName;
    @TableField(value = "fileType")
    private String fileType;
    @TableField(value = "bucket_name")
    private String bucketName;
}
