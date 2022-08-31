package com.test.common.util.excel;

import java.io.*;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author by Lixq
 * @Classname ExcelUtil
 * @Description TODO
 * @Date 2021/5/16 15:25
 */
@Slf4j
public class ExcelUtil {

    private ExcelUtil INSTANSE = new ExcelUtil ();



    /**
     * excel 2003 suffix
     */
    private static final String EXCEL_XLS_SUFFIX = ".xls" ;

    /**
     * excel 2007 或以上 suffix
     */
    private static final String EXCEL_XLSX_SUFFIX = ".xlsx";

    /**
     * 分隔符 "."
     */
    public static final String POINT = ".";

    /**
     * description: 读取excel数据
     * @param file
     * @return List<List<Object>>
     * @version v1.0
     * @author w
     * @date 2020年3月31日 下午3:36:39
     */
    public static List<List<Object>> importFile (File file) throws Exception{
        if(file == null) {
            return null ;
        }
        if(file.getName().endsWith(EXCEL_XLS_SUFFIX)) {
            return readXls(new FileInputStream(file));
        }
        if(file.getName().endsWith(EXCEL_XLSX_SUFFIX)) {
            return readXlsx(new FileInputStream(file));
        }
        throw new RuntimeException("文件不对,必须是excel文件，后缀名以："+EXCEL_XLS_SUFFIX + " 或者 "+ EXCEL_XLSX_SUFFIX);
    }

    /**
     * description: 导入excel --- 支持web
     * @param fileName
     * @param inputStream
     * @throws Exception
     * @return List<List<Object>>
     * @version v1.0
     * @author w
     * @date 2020年3月31日 下午4:51:01
     */
    public static List<List<Object>> importFile (MultipartFile multipartFile) throws Exception{
        if(multipartFile == null) {
            return null ;
        }
        if(multipartFile.getOriginalFilename().endsWith(EXCEL_XLS_SUFFIX)) {
            return readXls(multipartFile.getInputStream());
        }
        if(multipartFile.getOriginalFilename().endsWith(EXCEL_XLSX_SUFFIX)) {
            return readXlsx(multipartFile.getInputStream());
        }
        throw new RuntimeException("文件不对,必须是excel文件，后缀名以："+EXCEL_XLS_SUFFIX + " 或者 "+ EXCEL_XLSX_SUFFIX);
    }


    /**
     * description: 读取03版excel
     * @param file
     * @return List<List<Object>>
     * @version v1.0
     * @author w
     * @date 2020年3月31日 下午3:38:44
     */
    private static List<List<Object>> readXls(InputStream inputStream) throws Exception {
        List<List<Object>> list = new ArrayList<>();
        // 读取excel
        HSSFWorkbook workbook = new HSSFWorkbook(inputStream);
        // 获取sheet 页数量
        int sheets = workbook.getNumberOfSheets();
        for(int num = 0 ; num < sheets ; num++ ) {
            HSSFSheet sheet = workbook.getSheetAt(num);
            if(null == sheet) {
                continue ;
            }
            // sheet 页的总行数
            int rows = sheet.getLastRowNum();
            // startRow 开始读取的行数 --- 第二行开始读
            for( int startRow = 1 ;startRow <= rows  ; startRow ++) {
                HSSFRow row = sheet.getRow(startRow);
                List<Object> rowList = new ArrayList<>();
                if(null != row) {
                    // row 行中的 单元格总个数
                    short cells = row.getLastCellNum();
                    for(int x = 0 ; x <= cells ; x++) {
                        HSSFCell cell = row.getCell(x);
                        if(null == cell) {
                            rowList.add("");
                        }else {
                            rowList.add(getXlsValue(cell));
                        }
                    }
                    list.add(rowList);
                }
            }
        }
        return list;
    }

    /**
     * description: 获取 03 版 excel数据
     * @param cell
     * @return String
     * @version v1.0
     * @author w
     * @date 2020年3月31日 下午3:54:14
     */
    private static String getXlsValue(HSSFCell cell) {
//        if ( cell.getCellTypeEnum() == CellType.BOOLEAN) {
        if ( cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
//        } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            String cellValue = "";
            if(HSSFDateUtil.isCellDateFormatted(cell)){
                Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
                cellValue = new SimpleDateFormat("yyyy/MM/dd").format(date);
            }else{
                DecimalFormat df = new DecimalFormat("#.##");
                cellValue = df.format(cell.getNumericCellValue());
                String strArr = cellValue.substring(cellValue.lastIndexOf(POINT)+1,cellValue.length());
                if(strArr.equals("00")){
                    cellValue = cellValue.substring(0, cellValue.lastIndexOf(POINT));
                }
            }
            return cellValue;
        } else {
            // 其他类型的值，统一设置为 string
            // http://blog.csdn.net/ysughw/article/details/9288307
            cell.setCellType(CellType.STRING);
            return String.valueOf(cell.getStringCellValue());
        }
    }

    /**
     * description: 读取07或以上版本的 excel
     * @param file
     * @throws Exception
     * @return List<List<Object>>
     * @version v1.0
     * @author w
     * @date 2020年3月31日 下午4:01:25
     */
    private static List<List<Object>> readXlsx(InputStream inputStream) throws Exception {
        List<List<Object>> list = new ArrayList<>();
        // 读取excel ，封装到 XSSFWorkbook 对象
        XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
        int sheets = workbook.getNumberOfSheets();
        for(int num = 0 ;num < sheets ; num++) {
            XSSFSheet sheet = workbook.getSheetAt(num);
            if(null == sheet) {
                continue ;
            }
            // 获取sheet页的总行数
            int rows = sheet.getLastRowNum();
            for(int startRow = 1 ; startRow <= rows ; startRow++ ) {
                // startRow 开始读取的行数， 从第二行开始读取
                XSSFRow row = sheet.getRow(startRow);
                List<Object> rowList = new ArrayList<>();
                if(null != row) {
                    // 获取行总单元格个数
                    short cells = row.getLastCellNum();
                    for(int x = 0 ; x < cells ; x++) {
                        XSSFCell cell = row.getCell(x);
                        if(cell == null) {
                            rowList.add("");
                        }else {
                            rowList.add(getXlsxValue(cell));
                        }
                    }
                    list.add(rowList);
                }
            }
        }
        return list;
    }

    /**
     * description: 获取07或以上版本 excel 数据
     * @param cell
     * @return Object
     * @version v1.0
     * @author w
     * @date 2020年3月31日 下午4:09:03
     */
    private static Object getXlsxValue(XSSFCell cell) {
//        if (cell.getCellTypeEnum() == CellType.BOOLEAN) {
        if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
//        } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            String cellValue = "";
            if (DateUtil.isCellDateFormatted(cell)) {
                Date date = DateUtil.getJavaDate(cell.getNumericCellValue());
                cellValue = new SimpleDateFormat("yyyy/MM/dd").format(date);
            } else {
                DecimalFormat df = new DecimalFormat("#.##");
                cellValue = df.format(cell.getNumericCellValue());
                String strArr = cellValue.substring(cellValue.lastIndexOf(POINT) + 1, cellValue.length());
                if (strArr.equals("00")) {
                    cellValue = cellValue.substring(0, cellValue.lastIndexOf(POINT));
                }
            }
            return cellValue;
        } else {
            // 其他类型的值，统一设置为 string
            // http://blog.csdn.net/ysughw/article/details/9288307
            //cell.setCellType(Cell.CELL_TYPE_STRING);
            return String.valueOf(cell.getStringCellValue());
        }
    }

    // todo 导出

    /**
     * description: 导出数据excel
     * @param sheetName
     * @param headers
     * @param dataList
     * @param destFile
     * @return void
     * @version v1.0
     * @author w
     * @date 2020年3月30日 下午2:23:39
     */
    public static void export(String sheetName, String[] headers, List<List<Object>> dataList, File destFile) throws Exception {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        createSheet(sheetName, headers, dataList, workbook);
        workbook.write(new FileOutputStream(destFile));
    }


    /**
     * description: 导出excel --- 支持web
     * @param sheetName  sheet表名字
     * @param headers 表头
     * @param dataList 表数据
     * @param fileName  导出文件名
     * @param response
     * @return void
     * @version v1.0
     * @author w
     * @date 2020年3月31日 下午2:48:46
     */
    public static void export(String sheetName , String[] headers , List<List<Object>> dataList ,String fileName
            , HttpServletResponse response) throws Exception {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        createSheet(sheetName, headers, dataList, workbook);
        response.reset();
        response.setContentType("application/octet-stream; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename="+URLEncoder.encode(fileName ,"UTF-8"));
        workbook.write(response.getOutputStream());
        // 删除临时文件
        workbook.dispose();
    }

    /**
     * description: 创建sheet表格
     * @param sheetName  表sheet 名字
     * @param headers  表头
     * @param dataList 表数据
     * @param wb
     * @return void
     * @version v1.0
     * @author w
     * @date 2020年3月30日 下午2:33:39
     */
    public static void createSheet(String sheetName , String[] headers , List<List<Object>> dataList , SXSSFWorkbook wb) {
        SXSSFSheet sheet = wb.createSheet(sheetName);
        // 设置表头和单元格格式
        CellStyle headStyle = setHeaderStyle(wb);
        CellStyle bodyStyle = setBodyStyle(wb);
        // 创建表头和单元格数据
        createHeader(headers, sheet, headStyle);
        createBody(dataList, sheet, bodyStyle);
    }

    /**
     * description: 创建表头
     * @param headers
     * @param sheet
     * @param headStyle
     * @return void
     * @version v1.0
     * @author w
     * @date 2020年3月30日 下午3:03
     */
    private static void createHeader(String[] headers, SXSSFSheet sheet, CellStyle headStyle) {
        SXSSFRow row = sheet.createRow(0);
        row.setHeightInPoints(16F);
        for (int i = 0; i < headers.length; i++) {
            // 创建单元格
            SXSSFCell cell = row.createCell(i);
            cell.setCellStyle(headStyle);
            XSSFRichTextString text = new XSSFRichTextString(headers[i]);
            cell.setCellValue(text);
            sheet.trackAllColumnsForAutoSizing();
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * description: 表格中填充数据
     * @param dataList
     * @param sheet
     * @param bodyStyle
     * @return void
     * @version v1.0
     * @author w
     * @date  2020年3月30日 下午3:13
     */
    private static void createBody(List<List<Object>> dataList, SXSSFSheet sheet, CellStyle bodyStyle) {
        for (int i = 0; i < dataList.size(); i++) {
            // 从第二行开始，第一行做表头
            SXSSFRow row = sheet.createRow(i+1);
            List<Object> rowList = dataList.get(i);
            for (int j = 0; j < rowList.size(); j++) {
                SXSSFCell cell = row.createCell(j);
                cell.setCellStyle(bodyStyle);
                XSSFRichTextString text = new XSSFRichTextString(rowList.get(j).toString());
                cell.setCellValue(text);
                sheet.trackAllColumnsForAutoSizing();
                sheet.autoSizeColumn(i);
            }
        }
    }

    /**
     * description: 设置单元格内容样式
     * @param wb
     * @return HSSFCellStyle
     * @version v1.0
     * @author w
     * @date 2020年3月30日 下午2:42:39
     */
    private static CellStyle setBodyStyle(SXSSFWorkbook wb) {
        // 设置表格单元格格式
        CellStyle style = wb.createCellStyle();
        style.setFillForegroundColor(HSSFColor.WHITE.index);
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.LEFT);

        // 设置字体格式
        Font font = wb.createFont();
        font.setFontName("微软雅黑");
        // 字体是否加粗
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    /**
     * description: 设置表头样式
     * @param wb
     * @return
     * @return HSSFCellStyle
     * @version v1.0
     * @author w
     * @date 2020年3月30日 下午2:38:39
     */
    private static CellStyle setHeaderStyle(SXSSFWorkbook wb) {
        // 设置表格单元格格式
        CellStyle style = wb.createCellStyle();
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());

        // 设置字体格式
        Font font = wb.createFont();
        font.setFontName("微软雅黑");
        font.setFontHeightInPoints((short) 10);
        style.setFont(font);
        return style;
    }

}
