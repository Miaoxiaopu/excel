package com.fileinfo.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
@Slf4j
public final class ExcelUtils {
    public static String excelToHtml(MultipartFile file)throws IOException{
        InputStream inputStream = file.getInputStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        POIFSFileSystem fileSystem = new POIFSFileSystem(bufferedInputStream);
        HSSFWorkbook workbook = new HSSFWorkbook(fileSystem);
        //HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
        // workbook.getNumberOfSheets();
        HSSFSheet sheet = workbook.getSheetAt(0);
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        int lastRowNum = sheet.getLastRowNum();
        final int firstRowNum = sheet.getFirstRowNum();
        for (int i = firstRowNum; i < lastRowNum; i++) {
            HSSFRow row = sheet.getRow(i);
            short firstCellNum = row.getFirstCellNum();
            short lastCellNum = row.getLastCellNum();
            sb.append("<tr>");
            for(int j = firstCellNum; j < lastCellNum; j ++){
                final HSSFCell cell = row.getCell(j);
                sb.append("<td>").append(cell.getStringCellValue()).append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }

    public static String excelPlusToHtml(MultipartFile file)throws IOException{
        InputStream inputStream = file.getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        StringBuilder sb = new StringBuilder();
        sb.append("<table >");
        int lastRowNum = sheet.getLastRowNum();
        final int firstRowNum = sheet.getFirstRowNum();
        for (int i = firstRowNum; i < lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            short firstCellNum = row.getFirstCellNum();
            short lastCellNum = row.getLastCellNum();
            sb.append("<tr>");
            for(int j = firstCellNum; j < lastCellNum; j ++){
                final XSSFCell cell = row.getCell(j);
                sb.append("<td>").append(cell.getStringCellValue()).append("</td>");
            }
            sb.append("</tr>");
        }
        sb.append("</table>");
        return sb.toString();
    }
    public static void writeFile(String content, String path) {
        FileOutputStream fos = null;
        BufferedWriter bw = null;
        File file = new File(path);
        try {
            fos = new FileOutputStream(file);
            bw = new BufferedWriter(new OutputStreamWriter(fos, "UTF-8"));
            bw.write(content);
        } catch (FileNotFoundException ex) {
           log.info("要下载的文件不存在");
        } catch (UnsupportedEncodingException ex) {
            log.info("不支持的文件编码");
        } catch (IOException ex) {
           log.info("io异常");
        } finally {
            try {
                if (null != bw) {
                    bw.close();
                }
                if (null != fos) {
                    fos.close();
                }
            } catch (IOException ex) {
              //
            }

        }
    }
}
