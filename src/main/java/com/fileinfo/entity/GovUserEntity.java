package com.fileinfo.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("gov_user")
public class GovUserEntity {
    @TableId("user_id")
    private long userId;
    private String username;
    private String password;
    private String salt;
}
