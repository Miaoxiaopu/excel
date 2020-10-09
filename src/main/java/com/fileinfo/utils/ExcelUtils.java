package com.fileinfo.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class ExcelUtils {
    /**
     * 通用HSSF、XSSF的excel操作
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String excelToHtml(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        Workbook workbook = WorkbookFactory.create(bufferedInputStream);

        StringBuilder result = new StringBuilder();

        for (int sheetIdx = 0; sheetIdx < workbook.getNumberOfSheets(); sheetIdx++) {
            Sheet currSheet = workbook.getSheetAt(sheetIdx);
            StringBuilder sheetSb = new StringBuilder();
            sheetSb.append("Sheet" + (sheetIdx+1) + "["+ currSheet.getSheetName() + "]").append("<br>");
            sheetSb.append("<table border style='border-collapse:collapse;'>");

            for (int rowIdx = currSheet.getFirstRowNum(); rowIdx < currSheet.getLastRowNum(); rowIdx++) {
                Row row = currSheet.getRow(rowIdx);
                // 空行直接置空、跳过继续下一行
                if (row == null) {
                    sheetSb.append("<tr><td> &nbsp; </td></tr>");
                    continue;
                } else {
                    sheetSb.append("<tr>");
                }
                for (int colIdx = row.getFirstCellNum(); colIdx < row.getLastCellNum(); colIdx++) {
                    Cell cell = row.getCell(colIdx);
                    String stringCellValue = getCellValue(cell);
                    // 空单元格直接置空、跳过继续下一个单元格
                    if (cell == null) {
                        if(colIdx == row.getLastCellNum()-1){
                            sheetSb.append("<td style='border-left:hidden;'>&nbsp;</td>");
                            break;
                        }else{
                            sheetSb.append("<td style='border-right:hidden;'>&nbsp;</td>");
                            continue;
                        }
                    }
                    // 处理合并单元格(<td>或<td rowspan='' colspan=''>)
                    if (recordRegion(currSheet, rowIdx, colIdx, sheetSb)) {
                        continue;
                    }

                    if (StringUtils.isNotBlank(stringCellValue)) {
                        sheetSb.append(stringCellValue.replace(String.valueOf((char) 160), "&nbsp;"));
                    } else {
                        sheetSb.append(" &nbsp; ");
                    }
                    sheetSb.append("</td>");
                }
                sheetSb.append("</tr>");
            }
            sheetSb.append("</table>" + "<br><br>").append("<hr style=' height:2px;border:none;border-top:2px solid #185598;'/><br>");
            result.append(sheetSb);
        }
        return result.toString();
    }

    /**
     * 处理合并单元格
     *
     * @param sheet   当前sheet
     * @param rowIdx  当前行
     * @param colIdx  当前列
     * @param sheetSb
     * @return
     */
    public static boolean recordRegion(Sheet sheet, int rowIdx, int colIdx, StringBuilder sheetSb) {
        Map<String, String>[] rowpanColSpanMap = getRowpanColSpanMap(sheet);
        if (rowpanColSpanMap[0].containsKey(rowIdx + "," + colIdx)) {
            String regionAddress = rowpanColSpanMap[0].get(rowIdx + "," + colIdx);
            rowpanColSpanMap[0].remove(rowIdx + "," + colIdx);
            String bottomRow = regionAddress.split(",")[0];
            String lastCol = regionAddress.split(",")[1];
            int rowSpan = Integer.valueOf(bottomRow) - rowIdx + 1;
            int colSpan = Integer.valueOf(lastCol) - colIdx + 1;
            sheetSb.append("<td rowspan='" + rowSpan + "' colspan='" + colSpan + "'>");
        } else if (rowpanColSpanMap[1].containsKey(rowIdx + "," + colIdx)) {
            rowpanColSpanMap[1].remove(rowIdx + "," + colIdx);// for GC
            return true;
        } else {
            sheetSb.append("<td>");
        }
        return false;
    }

    /**
     * 记录合并单元格坐标
     *
     * @param sheet
     * @return
     */
    public static Map<String, String>[] getRowpanColSpanMap(Sheet sheet) {
        Map<String, String> map1StoreRegion = new HashMap<>();
        Map<String, String> map2StoreEveryCellInRegion = new HashMap<>();
        sheet.getMergedRegions().forEach((cellRangeAddress) -> {
            int firstColumn = cellRangeAddress.getFirstColumn();
            int topRow = cellRangeAddress.getFirstRow();
            int lastColumn = cellRangeAddress.getLastColumn();
            int bottomRow = cellRangeAddress.getLastRow();
            map1StoreRegion.put(topRow + "," + firstColumn, bottomRow + "," + lastColumn);
            for (int tempRow = topRow; tempRow <= bottomRow; tempRow++) {
                for (int tempColumn = firstColumn; tempColumn <= lastColumn; tempColumn++) {
                    map2StoreEveryCellInRegion.put(tempRow + "," + tempColumn, "");
                }
            }
            map2StoreEveryCellInRegion.remove(topRow + "," + firstColumn);
        });
        Map[] map = {map1StoreRegion, map2StoreEveryCellInRegion};
        return map;
    }

    public static String getCellValue(Cell cell) {
        if(cell == null){
            return null;
        }
        String result = "";
        switch (cell.getCellType()) {
            case NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    if (cell.getCellStyle().getDataFormat() ==
                            HSSFDataFormat.getBuiltinFormat(DatePattern.H_MM.getStringFormat())) {
                        result = DatePattern.H_MM.format(cell.getDateCellValue());
                    } else {
                        result = DatePattern.YYYY_MM_DD.format(cell.getDateCellValue());
                    }
                } else if (cell.getCellStyle().getDataFormat() == 58) {
                    double numericCellValue = cell.getNumericCellValue();
                    result = DatePattern.YYYY_MM_DD.format(DateUtil.getJavaDate(numericCellValue));
                } else {
                    double numericCellValue = cell.getNumericCellValue();
                    CellStyle cellStyle = cell.getCellStyle();
                    DecimalFormat format = new DecimalFormat();
                    String dataFormatString = cellStyle.getDataFormatString();
                    if ("General".equals(dataFormatString)) {
                        format.applyPattern("#");
                    }
                    result = format.format(numericCellValue);
                }
                return result;
                // 处理公式
            case FORMULA:
                try {
                    result = String.valueOf(cell.getNumericCellValue());
                } catch (IllegalStateException e) {
                    result = String.valueOf(cell.getRichStringCellValue());
                }
                return result;
            case BLANK:
                return null;
            case STRING:
                return cell.getStringCellValue();
            case ERROR:
                return "非法字符";
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return null;
        }
    }

    @Deprecated
    public static String excelPlusToHtml(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        XSSFSheet sheet = workbook.getSheetAt(0);
        StringBuilder sb = new StringBuilder();
        sb.append("<table>");
        int lastRowNum = sheet.getLastRowNum();
        final int firstRowNum = sheet.getFirstRowNum();
        for (int i = firstRowNum; i < lastRowNum; i++) {
            XSSFRow row = sheet.getRow(i);
            short firstCellNum = row.getFirstCellNum();
            short lastCellNum = row.getLastCellNum();
            sb.append("<tr>");
            for (int j = firstCellNum; j < lastCellNum; j++) {
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

    public static void main(String[] args) throws IOException {
        excelToHtml(null);
    }
}
