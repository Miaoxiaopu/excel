package com.fileinfo.type;

public enum MessageEnumType {
    SUCCESS("200","操作成功"),
    FAIL("-1","操作失败");
    private String code;
    private String message;
    MessageEnumType(String code, String message){
        this.code = code;
        this.message = message;
    }
    public String getMessage(){
        return this.code;
    }
}
