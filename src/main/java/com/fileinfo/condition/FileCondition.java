package com.fileinfo.condition;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FileCondition extends Page {
    private String fileContent;
    private String fileName;
    public FileCondition(int current,int size){
        super(current,size);
    }
}
