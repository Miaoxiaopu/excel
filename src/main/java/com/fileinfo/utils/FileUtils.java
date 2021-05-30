package com.fileinfo.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

@Slf4j
public final class FileUtils {

    // default 3M
    private static final int LIMIT_SIZE = 3 * 1024 * 1024;

    public static void downloadByPart(InputStream inputStream, HttpServletResponse response, int start, int end, int fileSize) {
        // 断点续传字节大小
        int range = end - start + 1;
        if (range > 0) {
            if (range > LIMIT_SIZE) {
                range = LIMIT_SIZE;
                end = start + range - 1;
            }
        } else {
            throw new IllegalArgumentException("range error start " + start + " end" + end);
        }
        int status = start == 0 && end + 1 >= fileSize ? HttpStatus.OK.value() : HttpStatus.PARTIAL_CONTENT.value();
        response.setStatus(status);
        // 支持字节输入
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Range", String.format("bytes %s-%s/%s", start, end, fileSize));

        ServletOutputStream outputStream = null;
        try {
            int readLength = -1, readTotal = 0;
            byte[] buffer = new byte[40960];
            outputStream = response.getOutputStream();
            while (readTotal < range && (readLength = inputStream.read(buffer, start, end)) != -1) {
                readTotal += readLength;

                // 一共读取的长度大于断点续传字节大小
                if (readTotal > range) {
                    // 设置本次读取长度为超出长度
                    readLength = readLength - (readTotal - range);
                }

                outputStream.write(buffer, 0, readLength);
            }
        } catch (Exception e) {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception ex) {
                    throw new RuntimeException("网络输出流关闭失败 {}" + ex.getMessage());
                }
            }
        }
    }

    public static void download(InputStream inputStream, HttpServletResponse response) {
        ServletOutputStream outputStream = null;
        try {
            int readLength = -1;
            byte[] buffer = new byte[1024];
            outputStream = response.getOutputStream();
            while ((readLength = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, readLength);
            }
        } catch (Exception e) {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception ex) {
                    throw new RuntimeException("网络输出流关闭失败 {}" + ex.getMessage());
                }
            }
        }
    }

    public static void uploadByPart(HttpServletRequest request) {
        request.getHeader("");
    }

    public static void handleRange(HttpServletRequest request, final int fileSize, RangeConsumer consumer) {
        String range = request.getHeader("Range");
        int start = -1, end = -1;
        String[] ranges = range.substring(6).split("-");
        String first = ranges[0];
        String second = ranges[1];
        if (org.springframework.util.StringUtils.isEmpty(first)) {
            start = fileSize - Integer.parseInt(second);
            end = fileSize - 1;

        }
        if (org.springframework.util.StringUtils.isEmpty(second)) {
            start = Integer.parseInt(first);
            end = fileSize - 1;
        }
        consumer.accept(start, end);
    }

    public interface RangeConsumer {
        void accept(int start, int end);
    }
}
