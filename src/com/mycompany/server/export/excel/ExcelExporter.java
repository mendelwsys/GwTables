package com.mycompany.server.export.excel;


import com.google.gson.annotations.SerializedName;
import com.mycompany.common.FieldDescriptor;
import com.mycompany.common.ListGridDescriptor;
import com.mycompany.common.tables.ColumnHeadBean;
import com.mycompany.common.tables.HeaderSpanMimic;
import com.mycompany.server.export.ExportStore;
import com.smartgwt.client.types.ListGridFieldType;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by Anton.Pozdnev on 27.03.2015.
 */
public class ExcelExporter {
    public ExcelExporter() {
        super();


    }

    public static class MyMap<V, K> extends HashMap<String, String> {

        @SerializedName("groupMembers")
        private MyMap<V, K>[] groupMembers;

        public boolean isGroup() {
            return isGroup;
        }

        public void setGroup(boolean isGroup) {
            this.isGroup = isGroup;
        }

        public MyMap<V, K>[] getGroupMembers() {
            return groupMembers;
        }

        public void setGroupMembers(MyMap<V, K>[] groupMembers) {
            this.groupMembers = groupMembers;
        }

        private boolean isGroup = false;

        @Override
        public String put(String key, String value) {
            if (key != null && key.equalsIgnoreCase("isFolder") && value != null && value.equalsIgnoreCase("true"))
                setGroup(true);
            return super.put(key, value);
        }
    }

    // HSSFWorkbook, File
    /*public String export(Map<String, String>[] res, String[] headers) {
        String fileName = "C:\\Dev\\export_test\\test.xls";
        FileOutputStream fileOut = null;
        try {

            File f = new File(fileName);
            if (!f.getParentFile().exists()) {
                f.getParentFile().mkdirs();
                //f.createNewFile();
            }
          *//*  if (!f.exists())
            {
                f.createNewFile();

            }*//*
            //POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(f));
            HSSFWorkbook wb = new HSSFWorkbook();
            fileOut = new FileOutputStream(f);
            Sheet sheet1 = wb.createSheet("new sheet");
            CreationHelper createHelper = wb.getCreationHelper();

            //Создаем заголовок
            Row row = sheet1.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(headers[i]);


            }
//Раскладываем данные по столбцам и строкам
            for (int i = 0; i < res.length; i++) {
                Map<String, String> mrow = res[i];
                Row row2 = sheet1.createRow(i + 1);
                for (int j = 0; j < headers.length; j++) {
                    Cell cell = row2.createCell(j);
                    cell.setCellValue(mrow.get(headers[j]));


                }


            }
            System.out.println("Finished. Used " + stylesCache.size() + " styles");
            wb.write(fileOut);
            // Create a cell and put a value in it.

            stylesCache.clear();
            return fileName;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.flush();
                    fileOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }


    }*/

    boolean useInmemoryStore = true;

    public String export_new(String clientId, String exportId, MyMap<String, String>[] res, Map<String, String> styles, ListGridDescriptor headers) {
        String fileName = "C:\\Dev\\export_test\\test.xls";
        OutputStream out = null;
        try {
            if (exportId != null)
                fileName = exportId;
            HSSFWorkbook wb = new HSSFWorkbook();
            if (useInmemoryStore) {
                out = new ByteArrayOutputStream();
            } else {
                File f = new File(fileName);
                if (!f.getParentFile().exists()) {
                    f.getParentFile().mkdirs();
                    //f.createNewFile();
                }
                out = new FileOutputStream(f);
            }
            headerStyle = wb.createCellStyle();
            Font headerFont = wb.createFont();
            //font.setColor(HSSFColor.GREY_50_PERCENT.index);
            headerStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
            headerStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
            headerFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
            headerStyle.setFont(headerFont);
            headerStyle.setWrapText(true);
            headerStyle.setBorderBottom(CellStyle.BORDER_THIN);
            headerStyle.setBorderTop(CellStyle.BORDER_THIN);
            headerStyle.setBorderLeft(CellStyle.BORDER_THIN);
            headerStyle.setBorderRight(CellStyle.BORDER_THIN);
            headerStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
            headerStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
            final String tableTitle = headers.getTableTitle();
            Sheet sheet1;
            if (tableTitle==null || tableTitle.length()==0)
                sheet1 = wb.createSheet();
            else
                sheet1= wb.createSheet(tableTitle);
            CreationHelper createHelper = wb.getCreationHelper();
            int lastRow = createCustomHeader(wb, sheet1, res, headers, createHelper);
            // int startRow = lastRow;
            int headersChsLength = headers.getChs().length;
            if (headers.getGroupedHeader() != null) {
                lastRow = createGroupedHeader(++lastRow, wb, sheet1, headers, createHelper);
            } else {
                //Создаем заголовок
                Row row = sheet1.createRow(++lastRow);

                for (int i = 0; i < headersChsLength; i++) {
                    Cell cell = row.createCell(i);
                    cell.setCellValue(prepareString(headers.getChs()[i].getTitle()));
                    cell.setCellStyle(getHeaderStyle());
                }
            }
            sheet1.setRowSumsBelow(false);
//Раскладываем данные по столбцам и строкам
            int headersEnd = lastRow;
            for (int i = 0, resLength = res.length; i < resLength; i++) {
                MyMap<String, String> mrow = res[i];

                if (mrow.isGroup()) {
                    int[] rowsIndex = createGroup(++lastRow, wb, sheet1, mrow, headers, createHelper, lastRow - 1 - headersEnd, styles);
                    lastRow = rowsIndex[0];
                } else
                    createRow(++lastRow, wb, sheet1, mrow, headers, createHelper, styles, lastRow - 2);
            }
            for (int i = 0; i < headersChsLength; i++) {

                if (((FieldDescriptor) headers.getChs()[i]).getWidth() > 0)
                    sheet1.setColumnWidth(i, ((FieldDescriptor) headers.getChs()[i]).getWidth());
                else {

                    //Фикс для ширины колонок с типом Дата - почему-то при автоширине не достаточно расширяется
                    if (ListGridFieldType.valueOf(((FieldDescriptor) headers.getChs()[i]).getType().toUpperCase()).equals(ListGridFieldType.DATETIME)) {
                        System.out.println("Setting custom width");
                        sheet1.setColumnWidth(i, 8000);
                    } else
                        sheet1.autoSizeColumn(i);
                }
            }
            wb.write(out);
            System.out.println("Finished. Used " + stylesCache.size() + " styles");
            // Create a cell and put a value in it.
            ExportStore.putExportRecord(clientId, exportId, out);
            return fileName;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void addRegionBorder(Cell cell, CellRangeAddress range, Sheet sheet) {
        RegionUtil.setBorderBottom(cell.getCellStyle().getBorderBottom(), range, sheet, sheet.getWorkbook());
        RegionUtil.setBorderTop(cell.getCellStyle().getBorderTop(), range, sheet, sheet.getWorkbook());
        RegionUtil.setBorderLeft(cell.getCellStyle().getBorderLeft(), range, sheet, sheet.getWorkbook());
        RegionUtil.setBorderRight(cell.getCellStyle().getBorderRight(), range, sheet, sheet.getWorkbook());
        RegionUtil.setBottomBorderColor(cell.getCellStyle().getBottomBorderColor(), range, sheet, sheet.getWorkbook());
        RegionUtil.setTopBorderColor(cell.getCellStyle().getTopBorderColor(), range, sheet, sheet.getWorkbook());
        RegionUtil.setLeftBorderColor(cell.getCellStyle().getLeftBorderColor(), range, sheet, sheet.getWorkbook());
        RegionUtil.setRightBorderColor(cell.getCellStyle().getRightBorderColor(), range, sheet, sheet.getWorkbook());
    }


    private int createGroupedHeader(int lastRow, HSSFWorkbook wb, Sheet sheet1, ListGridDescriptor headers, CreationHelper createHelper) {
        int inti = 0;
        int grouped_header_index = 0;
        int excelCol = 0;
        for (int j = 0; j < headers.getChs().length; j++) {
            if (!findInSubHeaders(headers.getChs()[j], headers.getGroupedHeader().getSubs())) {
                Row row = sheet1.createRow(lastRow);
                Cell c1 = row.createCell(j);
                c1.setCellValue(prepareString(headers.getChs()[j].getTitle()));
                CellRangeAddress region = new CellRangeAddress(lastRow, lastRow + headers.getGroupedHeader().getRowspan() - 1, excelCol, excelCol);
                sheet1.addMergedRegion(region);
                c1.setCellStyle(getHeaderStyle());
                addRegionBorder(c1, region, sheet1);
                inti++;
                excelCol++;
            } else {
                createGroupedHeaderInternal(lastRow, inti, excelCol, wb, sheet1, headers.getGroupedHeader().getSubs()[grouped_header_index], headers, createHelper);
                j += headers.getGroupedHeader().getSubs()[grouped_header_index].getColspan() - 1;
                excelCol += headers.getGroupedHeader().getSubs()[grouped_header_index].getColspan();
                grouped_header_index++;
            }
        }
        return lastRow + headers.getGroupedHeader().getRowspan() - 1;
    }

    private boolean findInSubHeaders(ColumnHeadBean columnHeadBean, HeaderSpanMimic[] nodes) {
        boolean found = false;
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i].getFieldNames() != null) {
                String[] n = Arrays.copyOfRange(nodes[i].getFieldNames(), 0, nodes[i].getFieldNames().length);
                Arrays.sort(n);
                if (Arrays.binarySearch(n, columnHeadBean.getName(), new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        return o1.equals(o2) ? 0 : 1;
                    }
                }) > -1) {
                    found = true;
                    break;
                }
            } else if (nodes[i].getSubs() != null) {
                boolean found2 = findInSubHeaders(columnHeadBean, nodes[i].getSubs());
                if (found2) {
                    found = found2;
                    break;
                }
            }
        }
        return found;
    }

    private void createGroupedHeaderInternal(int lastRow, int col, int excelCol, HSSFWorkbook wb, Sheet sheet1, HeaderSpanMimic node, ListGridDescriptor headers, CreationHelper createHelper) {
        Row row = sheet1.getRow(lastRow);
        if (row == null)
            row = sheet1.createRow(lastRow);
        if (!findInSubHeaders(headers.getChs()[col], headers.getGroupedHeader().getSubs())) {
            Cell c1 = row.createCell(excelCol);
            c1.setCellValue(prepareString(headers.getChs()[col].getTitle()));
            c1.setCellStyle(getHeaderStyle());
            if (headers.getGroupedHeader().getRowspan() > 1) {
                CellRangeAddress region = new CellRangeAddress(lastRow, lastRow + headers.getGroupedHeader().getRowspan() - 1, excelCol, excelCol);
                sheet1.addMergedRegion(region);
                addRegionBorder(c1, region, sheet1);
            }
            col++;
            excelCol++;
        } else {
            Cell c1 = row.createCell(excelCol);
            c1.setCellValue(prepareString(node.getName()));
            c1.setCellStyle(getHeaderStyle());
            if (node.getRowspan() > 1 || node.getColspan() > 1) {
                CellRangeAddress region = new CellRangeAddress(lastRow, lastRow + node.getRowspan() - 1, excelCol, excelCol + node.getColspan() - 1);
                sheet1.addMergedRegion(region);
                addRegionBorder(c1, region, sheet1);
            }

            if (node.getSubs() != null) {
                for (int i = 0; i < node.getSubs().length; i++) {
                    createGroupedHeaderInternal(lastRow + node.getRowspan(), col++, excelCol, wb, sheet1, node.getSubs()[i], headers, createHelper);
                    excelCol += node.getSubs()[i].getColspan();
                }
            }
            if (node.getFieldNames() != null) {
                lastRow += node.getRowspan();
                Row row2 = sheet1.getRow(lastRow);
                if (row2 == null)
                    row2 = sheet1.createRow(lastRow);
                if (node.getFieldNames() != null && node.getFieldNames().length == 1 && getHeaderField(headers.getChs()[col].getName(), headers.getChs()).getTitle().equals(node.getName())) {
                    row2.setZeroHeight(true);
                }
                for (int i = 0; i < node.getFieldNames().length; i++, col++, excelCol++) {
                    Cell c2 = row2.createCell(excelCol);
                    ColumnHeadBean f = getHeaderField(node.getFieldNames()[i], headers.getChs());
                    c2.setCellValue(prepareString(f.getTitle()));
                    c2.setCellStyle(getHeaderStyle());
                }
            }
        }

    }

    private ColumnHeadBean getHeaderField(String s, ColumnHeadBean[] chs) {
        for (int i = 0; i < chs.length; i++) {
            if (chs[i].getName().equals(s)) return chs[i];
        }
        return null;
    }


    public static CellStyle getHeaderStyle() {
        return headerStyle;
    }

    public static void setHeaderStyle(CellStyle headerStyle) {
        ExcelExporter.headerStyle = headerStyle;
    }

    static CellStyle headerStyle = null;

    private void groupRows(HSSFWorkbook wb, Sheet sheet1, MyMap<String, String>[] res, ListGridDescriptor headers, CreationHelper createHelper) {


        for (int i = 0, resLength = res.length; i < resLength; i++)
            if (res[i].isGroup()) {
                sheet1.groupRow(Integer.parseInt(res[i].get("startRow")) + 1, Integer.parseInt(res[i].get("endRow")));
                // sheet1.setRowGroupCollapsed(groupStartRow - 1, true);

                groupRows(wb, sheet1, res[i].getGroupMembers(), headers, createHelper);
            }


    }

    private void createRow(int lastRow, HSSFWorkbook wb, Sheet sheet1, MyMap<String, String> mrow, ListGridDescriptor headers, CreationHelper createHelper, Map<String, String> styles, int row) {

        Row row2 = sheet1.createRow(lastRow);
        for (int j = 0; j < headers.getChs().length; j++) {
            Cell cell = row2.createCell(j);
            setCellContent(wb, cell, mrow, (FieldDescriptor) headers.getChs()[j], createHelper, styles, row, j);


        }


    }

    private int[] createGroup(int lastRow, HSSFWorkbook wb, Sheet sheet1, MyMap<String, String> mrow, ListGridDescriptor headers, CreationHelper createHelper, int row, Map<String, String> styles) {

        int groupStartRow = lastRow;
        createRow(lastRow, wb, sheet1, mrow, headers, createHelper, styles, row);
        for (int i = 0, groupMembersLength = mrow.getGroupMembers().length; i < groupMembersLength; i++) {

            if (mrow.getGroupMembers()[i].isGroup()) {
                int[] rowsIndex = createGroup(++lastRow, wb, sheet1, mrow.getGroupMembers()[i], headers, createHelper, ++row, styles);
                lastRow = rowsIndex[0];
                row = rowsIndex[1];
            } else {
                createRow(++lastRow, wb, sheet1, mrow.getGroupMembers()[i], headers, createHelper, styles, ++row);
            }
        }

        int groupEndRow = lastRow;
        //  mrow.put("startRow", "" + groupStartRow);
        //  mrow.put("endRow", "" + groupEndRow);
        // System.out.println("Группа " + groupStartRow + " - " + groupEndRow);
        //Группируем записи
        sheet1.groupRow(groupStartRow + 1, groupEndRow);

        return new int[]{lastRow, row};
    }


    private final static SimpleDateFormat titleSdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    private static final ThreadLocal<SimpleDateFormat> thsdf =
            new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    return titleSdf;
                }
            };
    private static final ThreadLocal<SimpleDateFormat> datasdf =
            new ThreadLocal<SimpleDateFormat>() {
                @Override
                protected SimpleDateFormat initialValue() {
                    return sdf;
                }
            };

    private int createCustomHeader(HSSFWorkbook wb, Sheet sheet1, MyMap<String, String>[] res, ListGridDescriptor headers, CreationHelper createHelper) {
        Row row = sheet1.createRow(0);
        Cell c1 = row.createCell(0);
        c1.setCellValue("Данные на " + thsdf.get().format(new Date()));
        sheet1.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.getChs().length));


        return 0;
    }

    Map<String, CellStyle> stylesCache = Collections.synchronizedMap(new HashMap<String, CellStyle>());
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private void setCellContent(HSSFWorkbook wb, Cell c, Map<String, String> m, FieldDescriptor f, CreationHelper ch, Map<String, String> styles, int row, int col) {

        try {


            // CellStyle cs = wb.createCellStyle();
            ListGridFieldType type = ListGridFieldType.valueOf(f.getType().toUpperCase());
            CellStyle cs = getCellStyle(wb, f, m, ch, styles, row, col);
            if (type.equals(ListGridFieldType.DATETIME)) {
                try {
                    datasdf.get().parse(m.get(f.getName()));

                } catch (Exception e) {

                    type = ListGridFieldType.TEXT;
                }
            }
            switch (type) {
                case DATETIME: {

                    if (m.get(f.getName()) != null)
                        c.setCellValue(datasdf.get().parse(m.get(f.getName())));
                    break;
                }
                case FLOAT: {
                    if (m.get(f.getName()) != null)
                        c.setCellValue(Double.parseDouble(m.get(f.getName())));
                    c.setCellType(Cell.CELL_TYPE_NUMERIC);
                    break;

                }
                case INTEGER: {
                    if (m.get(f.getName()) != null)
                        c.setCellValue(Integer.parseInt(m.get(f.getName())));
                    c.setCellType(Cell.CELL_TYPE_NUMERIC);
                    break;
                }
                case TEXT: {
                    if (m.get(f.getName()) != null)
                        c.setCellValue(prepareString(m.get(f.getName())));
                    else if (m.get("isFolder") != null && Boolean.parseBoolean(m.get("isFolder"))) {
                        c.setCellValue(prepareString(m.get("groupTitle")));

                    }
                    break;

                }
                case LINK: {
                    if (m.get(f.getName()) != null) {
                        c.setCellValue(new HSSFRichTextString(m.get(f.getLinkNameField())));
                        HSSFHyperlink url_link = new HSSFHyperlink(HSSFHyperlink.LINK_URL);
                        url_link.setAddress(m.get(f.getLinkURLField()));
                        c.setHyperlink(url_link);
                    }
                }


            }
            if (cs != null)
                c.setCellStyle(cs);

        } catch (Throwable t) {
            t.printStackTrace();

        }


    }

    public static final String BACKGROUND_COLOR = "background-color";
    public static final String FONT_COLOR = "color";

    public static final String[] styleKey = {BACKGROUND_COLOR, FONT_COLOR};

    private CellStyle getCellStyle(HSSFWorkbook wb, FieldDescriptor f, Map<String, String> m, CreationHelper ch, Map<String, String> styles, int row, int col) {
        try {
            // CellStyle cs = wb.createCellStyle();
            ListGridFieldType type = ListGridFieldType.valueOf(f.getType().toUpperCase());
            //String bgcolor = getBackGroundColor(styles, row, col);
            Map<String, String> cellStringStyles = getStyle(styles, row, col);

            ListGridFieldType tempType = ListGridFieldType.valueOf(f.getType().toUpperCase());
            if (type.equals(ListGridFieldType.FLOAT) || type.equals(ListGridFieldType.TEXT))
                type = ListGridFieldType.INTEGER;


            // В поле Дата может прийти значение "До отмены" или другое текстовое и тогда считаем ее текстом
        /*    if (type.equals(ListGridFieldType.DATETIME)) {
                try {
                    sdf.parse(m.get(f.getName()));

                } catch (Exception e) {
                    tempType = ListGridFieldType.TEXT;
                    type = ListGridFieldType.TEXT;
                }
            }
*/

            String styleKey = buildStyleKey(type, cellStringStyles);


            CellStyle cs = stylesCache.get(styleKey);

            if (cs == null) {
                cs = wb.createCellStyle();
                switch (tempType) {
                    case DATETIME: {

                        cs.setDataFormat(
                                ch.createDataFormat().getFormat("m/d/yy h:mm"));

                        break;
                    }
                    case FLOAT: {

                        break;

                    }
                    case INTEGER: {

                        break;
                    }
                    case TEXT: {


                        break;

                    }
                    case LINK: {
                        Font hlink_font = wb.createFont();
                        hlink_font.setUnderline(Font.U_SINGLE);
                        hlink_font.setColor(IndexedColors.BLUE.getIndex());
                        cs.setFont(hlink_font);

                    }


                }

                cs.setAlignment(HSSFCellStyle.ALIGN_CENTER);
                cs.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

                if (cellStringStyles.get(BACKGROUND_COLOR) != null) {

                    java.awt.Color color = null;

                    try {
                        color = java.awt.Color.decode(cellStringStyles.get(BACKGROUND_COLOR));
                    } catch (NumberFormatException e) {
                        color = java.awt.Color.decode(staticColors.get(cellStringStyles.get(BACKGROUND_COLOR).toLowerCase()));
                    }

                    HSSFPalette palette = wb.getCustomPalette();
                    HSSFColor poicolor = palette.findSimilarColor(color.getRed(), color.getGreen(), color.getBlue());
                    cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
                    cs.setFillForegroundColor(poicolor.getIndex());


                }
                if (cellStringStyles.get(FONT_COLOR) != null) {

                    java.awt.Color color = null;

                    try {
                        color = java.awt.Color.decode(cellStringStyles.get(FONT_COLOR));
                    } catch (NumberFormatException e) {
                        color = java.awt.Color.decode(staticColors.get(cellStringStyles.get(FONT_COLOR).toLowerCase()));
                    }

                    HSSFPalette palette = wb.getCustomPalette();
                    HSSFColor poicolor = palette.findSimilarColor(color.getRed(), color.getGreen(), color.getBlue());
                    Font font = wb.createFont();

                    font.setColor(poicolor.getIndex());
                    cs.setFont(font);


                }

                cs.setBorderBottom(CellStyle.BORDER_THIN);
                cs.setBorderTop(CellStyle.BORDER_THIN);
                cs.setBorderLeft(CellStyle.BORDER_THIN);
                cs.setBorderRight(CellStyle.BORDER_THIN);

                cs.setWrapText(true);

                stylesCache.put(styleKey, cs);

            }

            return cs;
        } catch (Throwable t) {
            t.printStackTrace();

        }
        return null;

        // c.setCellStyle(cs);


    }

    String buildStyleKey(ListGridFieldType type, Map<String, String> cellStringStyles)

    {
        String key = "" + type;
        for (int i = 0, styleKeyLength = styleKey.length; i < styleKeyLength; i++)
            key += "_" + cellStringStyles.get(styleKey[i]);
        return key;
    }


    /*String getBackGroundColor(Map<String, String> m) {
        String bgcolor = m.get("rowstyle");
        if (bgcolor != null)
            return bgcolor.split(":")[1].replaceAll(";", "");
        else return null;

    }


    String getBackGroundColor(Map<String, String> styles, int row, int col) {
        String bgcolor = styles.get("" + row + "_" + col);
        if (bgcolor != null && bgcolor.startsWith("#!#")) {
            bgcolor = styles.get(bgcolor.replace("#!#", ""));
        }
        if (bgcolor != null && !bgcolor.equals("null"))
            return bgcolor.split(":")[1].replaceAll(";", "");
        else return null;

    }*/

    Map<String, String> getStyle(Map<String, String> styles, int row, int col) {
        Map<String, String> cellMapped = new HashMap<String, String>();
        String style = styles.get("" + row + "_" + col);
        if (style != null && style.startsWith("#!#")) {
            style = styles.get(style.replace("#!#", ""));
        }
        if (style != null && !style.equals("null")) {

            String[] cellStyles = style.split(";");
            for (int i = 0, cellStylesLength = cellStyles == null ? 0 : cellStyles.length; i < cellStylesLength; i++) {
                String[] concreteStyle = cellStyles[i].split(":");
                cellMapped.put(concreteStyle[0].trim(), concreteStyle[1].trim());

            }

        }
        return cellMapped;
    }


    static String prepareString(String s) {

        return s.replaceAll("<br>", "\n").replaceAll("<b>", "").replaceAll("</b>", "");
    }


    static Map<String, String> staticColors = Collections.synchronizedMap(new HashMap<String, String>());

    static {
        staticColors.put("red", "#FF0000");


    }


}
