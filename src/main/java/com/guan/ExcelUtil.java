package com.guan;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtil {
	public static String checkCell(Cell cell) {
		if (cell == null) {
			return "";
		}
		int cellType = cell.getCellType();
		switch (cellType) {
		case 0:
			return cell.getNumericCellValue() + "";
		case 1:
			return cell.getStringCellValue();
		case 2:
			return cell.getDateCellValue() + "";
		case 4:
			return cell.getBooleanCellValue() + "";
		}

		return "";
	}

	public static Workbook getWorkbook(File file) {
		InputStream is = null;
		Workbook wb = null;
		try {
			is = new FileInputStream(file);
			wb = new HSSFWorkbook(is);
		} catch (Exception e) {
			try {
				is.close();
				is = new FileInputStream(file);
				wb = new XSSFWorkbook(is);
			} catch (IOException ioException) {
				throw new RuntimeException(ioException.getMessage());
			}
		}
		return wb;
	}
}
