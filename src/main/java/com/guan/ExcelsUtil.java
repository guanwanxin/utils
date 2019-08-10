package com.guan;

import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.VerticalAlignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * @author HZH
 * 
 */
public class ExcelsUtil {

	public static Log log = LogFactory.getLog(ExcelsUtil.class);

	public static void writeExcelBatch(List<?> titleList, List<?> datalist,
			HttpServletResponse response, String filename) {
		try {
			OutputStream os = response.getOutputStream();
			WritableWorkbook book = Workbook.createWorkbook(os);
			WritableSheet sheet = book.createSheet("sheet1", 0);

			WritableFont wfont = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);

			WritableCellFormat titleFormat = new WritableCellFormat(wfont);

			for (int k = 0; (titleList != null) && (titleList.size() > 0)
					&& (k < titleList.size()); k++) {
				Label excelTitle = new Label(k, 0, (String) titleList.get(k),
						titleFormat);
				sheet.addCell(excelTitle);
			}

			for (int i = 0; (datalist != null) && (datalist.size() > 0)
					&& (i < datalist.size()); i++) {
				Object[] obj = (Object[]) datalist.get(i);
				for (int j = 0; j < obj.length; j++) {
					Label content = new Label(j, i + 1, obj[j] == null ? ""
							: obj[j].toString());
					sheet.addCell(content);
				}
			}
			book.write();
			response.setHeader("Content-Disposition", "attachment;filename="
					+ filename + ".xls");
			response.setHeader("Connection", "close");
			response.setHeader("Content-Type", "application/vnd.ms-excel");
			book.close();
			os.flush();
			os.close();
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void exportExcelHasMultiSheet(Map<String, String[]> rowTitle,
			Map<String, List<Object[]>> dataMap, HttpServletResponse response,
			String filename, String[] sheetNameArr) {
		try {
			OutputStream os = response.getOutputStream();
			WritableWorkbook book = Workbook.createWorkbook(os);

			WritableFont wfont = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);

			WritableCellFormat titleFormat = new WritableCellFormat(wfont);

			for (int sIndex = 0; sIndex < sheetNameArr.length; sIndex++) {
				String sheetName = sheetNameArr[sIndex];

				WritableSheet sheet = book.createSheet(sheetName, sIndex);

				for (int i = 0; i < ((String[]) rowTitle.get(sheetName)).length; i++) {
					Label excelTitle = new Label(i, 0,
							((String[]) rowTitle.get(sheetName))[i],
							titleFormat);
					sheet.addCell(excelTitle);

					byte[] bstrLength = ((String[]) rowTitle.get(sheetName))[i]
							.getBytes();
					sheet.setColumnView(i, bstrLength.length + 5);
				}

				for (int i = 0; i < ((List<?>) dataMap.get(sheetName)).size(); i++) {
					Object[] obj = (Object[]) ((List<?>) dataMap.get(sheetName))
							.get(i);
					for (int j = 0; j < obj.length; j++) {
						Label content = new Label(j, i + 1, obj[j] == null ? ""
								: obj[j].toString());
						sheet.addCell(content);
					}
				}
			}

			book.write();
			response.setHeader("Content-Disposition", "attachment;filename="
					+ filename + ".xls");
			response.setHeader("Connection", "close");
			response.setHeader("Content-Type", "application/vnd.ms-excel");
			book.close();
			os.flush();
			os.close();
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void exportExcelHasMultiSheetLocal(String filepaht,
			Map<String, String[]> rowTitle,
			Map<String, List<Object[]>> dataMap, HttpServletResponse response,
			String filename, String[] sheetNameArr) {
		try {
			FileWriter fw = new FileWriter(filepaht + filename + ".csv");
			for (int sIndex = 0; sIndex < sheetNameArr.length; sIndex++) {
				String sheetName = sheetNameArr[sIndex];
				String header = "";

				for (int i = 0; i < ((String[]) rowTitle.get(sheetName)).length; i++) {
					header = header + ((String[]) rowTitle.get(sheetName))[i]
							+ ",";
				}
				fw.write(header.substring(0, header.length() - 1) + "\r\n");

				StringBuffer strSum = new StringBuffer();
				for (int i = 0; i < ((List<?>) dataMap.get(sheetName)).size(); i++) {
					Object[] obj = (Object[]) ((List<?>) dataMap.get(sheetName))
							.get(i);

					StringBuffer str = new StringBuffer();
					for (int j = 0; j < obj.length; j++) {
						str.append(
								obj[j] == null ? "" : "\t" + obj[j].toString())
								.append(",");
					}
					strSum.append(str.toString().substring(0,
							str.toString().length() - 1)
							+ "\r\n");
				}
				fw.write(strSum.toString());
				fw.flush();
				fw.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void exportExcelHasMultiSheetTCN(
			Map<String, String[]> rowTitle,
			Map<String, List<Object[]>> dataMap, HttpServletResponse response,
			String filename, String[] sheetNameArr,
			Map<String, Integer> rowspans, int[] cols) {
		try {
			OutputStream os = response.getOutputStream();
			WritableWorkbook book = Workbook.createWorkbook(os);

			WritableFont wfont = new WritableFont(WritableFont.ARIAL, 10,
					WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE,
					Colour.BLACK);

			WritableCellFormat titleFormat = new WritableCellFormat(wfont);

			for (int sIndex = 0; sIndex < sheetNameArr.length; sIndex++) {
				String sheetName = sheetNameArr[sIndex];

				WritableSheet sheet = book.createSheet(sheetName, sIndex);

				for (int i = 0; i < ((String[]) rowTitle.get(sheetName)).length; i++) {
					Label excelTitle = new Label(i, 0,
							((String[]) rowTitle.get(sheetName))[i],
							titleFormat);
					sheet.addCell(excelTitle);

					byte[] bstrLength = ((String[]) rowTitle.get(sheetName))[i]
							.getBytes();
					sheet.setColumnView(i, bstrLength.length + 5);
				}

				WritableCellFormat headerFormat = new WritableCellFormat();

				headerFormat.setAlignment(Alignment.CENTRE);

				headerFormat.setVerticalAlignment(VerticalAlignment.CENTRE);

				for (int i = 0; i < ((List<?>) dataMap.get(sheetName)).size(); i++) {
					Object[] obj = (Object[]) ((List<?>) dataMap.get(sheetName))
							.get(i);
					for (int j = 0; j < obj.length; j++) {
						Label content = new Label(j, i + 1, obj[j] == null ? ""
								: obj[j].toString(), headerFormat);
						sheet.addCell(content);
					}
				}

				if ((sIndex == 1) && (rowspans != null)
						&& (rowspans.size() > 0)) {
					Iterator<String> its = rowspans.keySet().iterator();
					while (its.hasNext()) {
						Integer key = Integer.valueOf(Integer
								.parseInt((String) its.next()));
						Integer value = (Integer) rowspans.get(key + "");
						for (int i : cols) {
							sheet.mergeCells(i, key.intValue(), i,
									key.intValue() + value.intValue() - 1);
						}
					}
				}
			}
			book.write();
			response.setHeader("Content-Disposition", "attachment;filename="
					+ filename + ".xls");
			response.setHeader("Connection", "close");
			response.setHeader("Content-Type", "application/vnd.ms-excel");
			book.close();
			os.flush();
			os.close();
			return;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 调用poi的api生成xlsx
	 * 
	 * @param rowTitle
	 * @param dataMap
	 * @param response
	 * @param filename
	 * @param sheetNameArr
	 * @param rowspans
	 * @param cols
	 */
	public static void exportXlsxHasMultiSheetTCN(
			Map<String, String[]> rowTitle,
			Map<String, List<Object[]>> dataMap, HttpServletResponse response,
			String filename, String[] sheetNameArr,
			Map<String, Integer> rowspans, int[] cols) {

		log.info("票务对账结果明细导出excel准备开始");
		OutputStream os = null;
		try {
			SXSSFWorkbook wb = new SXSSFWorkbook(
					SXSSFWorkbook.DEFAULT_WINDOW_SIZE);
			CellStyle boldFont = getBoldFont(wb, true);
			CellStyle nomalFont = getBoldFont(wb, false);
			for (int sIndex = 0; sIndex < sheetNameArr.length; sIndex++) {
				String sheetName = sheetNameArr[sIndex];
				Sheet sheet = wb.createSheet(sheetName);
				Row row = sheet.createRow(0);
				String[] titles = rowTitle.get(sheetName);
				for (int i = 0; i < titles.length; i++) {
					Cell cell = row.createCell(i);
					cell.setCellValue(titles[i]);
					cell.setCellStyle(boldFont);
				}

				List<Object[]> list = dataMap.get(sheetName);
				for (int i = 0; i < list.size(); i++) {
					Object[] obj = list.get(i);
					row = sheet.createRow(i + 1);
					for (int j = 0; j < obj.length; j++) {
						Cell cell = row.createCell(j);
						cell.setCellValue(obj[j] == null ? "" : obj[j]
								.toString());
						cell.setCellStyle(nomalFont);
						if (i + 1 == list.size()) {
							sheet.autoSizeColumn(j);
							int titleWidth = titles[j].getBytes().length * 384;
							if (sheet.getColumnWidth(j) < titleWidth) {
								System.out.println(sheet.getColumnWidth(j)
										+ "\t" + titleWidth);
								sheet.setColumnWidth(j, titleWidth);
							}
						}
					}
				}

			}
			// FileOutputStream out = new FileOutputStream("C://sxssf.xlsx");
			// wb.write(out);
			response.setContentType("application/x-msdownload");
			String inlineType = "attachment"; // 是否内联附件
			response.setHeader("Content-Disposition", inlineType
					+ ";filename=\"" + filename + ".xlsx\"");
			os = response.getOutputStream();
			wb.write(os);
			os.flush();
			log.info("票务对账结果明细导出excel结束");
		} catch (Exception e) {
			log.error("票务对账结果明细导出excel出现异常", e);
		} finally {
			try {
				os.close();
			} catch (IOException e) {
				log.error("票务对账结果明细导出excel关闭资源出现异常", e);
			}
		}
	}

	private static CellStyle getBoldFont(SXSSFWorkbook wb, boolean isBold) {
		CellStyle cs = wb.createCellStyle();
		Font font = wb.createFont();
		if (isBold) {
			font.setBoldweight(Font.BOLDWEIGHT_BOLD);
		}
		font.setFontName("Arial");
		cs.setFont(font);
		cs.setAlignment(CellStyle.ALIGN_CENTER);
		return cs;
	}
}
