package com.fileinfo.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
public enum DatePattern {
    YYYY_MM_DD("YYYY-MM-dd"),
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"),
    HH_MM("HH:mm"),
    H_MM("h:mm");
    private SimpleDateFormat simpleDateFormat;
    private String stringFormat;
    DatePattern(String dataPattern){
        stringFormat = dataPattern;
        simpleDateFormat = new SimpleDateFormat(dataPattern);
    }
    public Date parse(String date){
        if(date == null){
            return null;
        }
        try {
            return this.simpleDateFormat.parse(date);
        } catch (ParseException e) {
            log.error(date + "时间转换异常"+e.getMessage());
        }
        return null;
    }
    public String format(Date date){
        if(date == null){
            return null;
        }
        return this.simpleDateFormat.format(date);
    }
    public String getStringFormat(){
        return this.stringFormat;
    }
}
