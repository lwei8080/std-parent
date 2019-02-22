package com.std.common.utils;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author liuwei3
 *
 */
public class POIUtil {
	private final static Logger log = LoggerFactory.getLogger(POIUtil.class);
	// 2007最大行
	public static final int EXCEL_MAX_ROWS = 30000;
	// 默认导出excel 版本
	public static final String EXPORT_XLS_VERSION = "2007";
	// 输出类型
	public static final String RESPONSE_CONTENT_TYPE = "application/msexcel";
	
	public static void outputWorkbook(HttpServletResponse response,Workbook wb,String filename) throws Exception
	{
		response.setContentType(RESPONSE_CONTENT_TYPE);
		response.setHeader("Content-Disposition", "attachment;Filename="
				+ new String(filename.getBytes(), "ISO-8859-1") + ".xlsx");
		OutputStream os = response.getOutputStream();
		wb.write(os);
		os.flush();
		os.close();
	}
	
	@SuppressWarnings("unchecked")
	public static Workbook genaterateXLSData(Map<String, Object> model,Workbook workbook)
	{
		List<String> cellName = (List<String>) model.get("cellName");
		List<Map<String, Object>> dataList = (List<Map<String, Object>>) model
				.get("listData");
		String keys[] = (String[]) model.get("keys");
		CellStyle cellStyle = setCellStyle(workbook);
		Sheet sheet = workbook.createSheet(model.get("sheetName").toString());
		CreationHelper createHelper = workbook.getCreationHelper();
		Row row = sheet.createRow(0);
		Cell cell = null;
		for (int i = 0; i < cellName.size(); i++)
		{
			cell = row.createCell(i);
			cell.setCellStyle(cellStyle);
			cell.setCellValue(createHelper.createRichTextString(cellName.get(i)));
		}
		Map<String, Object> mapBean = null;

		for (int i = 0; i < dataList.size(); i++)
		{

			row = sheet.createRow(i + 1);
			mapBean = dataList.get(i);
			for (int j = 0; j < keys.length; j++)
			{
				// sheet.autoSizeColumn(j);
				cell = row.createCell(j);
				cell.setCellValue(createHelper
						.createRichTextString(null == mapBean.get(keys[j]) ? ""
								: mapBean.get(keys[j]).toString()));
			}

		}
		return workbook;
	}
	
	public static Workbook genaterateXLSData(Map<String, Object> model)
	{
		Workbook workbook = getInstanceBook(EXPORT_XLS_VERSION);
		return genaterateXLSData(model, workbook);
	}
	
	private static CellStyle setCellStyle(Workbook workbook)
	{
		CellStyle cs = workbook.createCellStyle();
		Font f = workbook.createFont();
		f.setFontHeightInPoints((short) 10);
		f.setColor(IndexedColors.RED.getIndex());
		f.setBoldweight(Font.BOLDWEIGHT_BOLD);
		// DataFormat df = workbook.createDataFormat();
		// cs.setDataFormat(df.getFormat(arg0));
		cs.setFont(f);
		return cs;
	}
	
	private static Workbook getInstanceBook(String version)
	{
		Workbook workbook = null;
		if (version.equals("2003"))
		{
			workbook = new HSSFWorkbook();
		} else
		{
			workbook = new XSSFWorkbook();
		}
		return workbook;
	}
}
