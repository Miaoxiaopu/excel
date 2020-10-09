package com.fileinfo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
public class FileInfo extends BaseEntity{
    @TableField(value = "content")
    private byte[] content;
    @TableId(value = "file_id")
    private long fileId;
    @TableField(value = "file_name")
    private String fileName;
    @TableField(value = "fileType")
    private String fileType;
    @TableField(value = "bucket_name")
    private String bucketName;
    @TableField(exist = false)
    private String contentStr;
}
