package com.fileinfo.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum DatePattern {
    YYYY_MM_DD("YYYY-MM-dd"),
    YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat simpleDateFormat;
    DatePattern(String dataPattern){
        simpleDateFormat = new SimpleDateFormat(dataPattern);
    }
    public Date parse(String date){
        try {
            return this.simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String format(Date date){
        return this.simpleDateFormat.format(date);
    }
}
