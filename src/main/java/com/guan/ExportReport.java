package com.guan;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class ExportReport {
	private ByteArrayOutputStream outputStream;
	private String fileName;
	public static final String CONTENT_TYPE_PDF = "application/pdf;charset=ISO8859-1";
	public static final String CONTENT_TYPE_EXECL = "application/vnd.ms-excel;charset=ISO8859-1";
	private ExportReportType reportType;

	public ExportReport(ExportReportType reportType) {
		this.reportType = reportType;
	}

	public static enum ExportReportType {
		PDF, EXECL;

		private ExportReportType() {
		}
	}

	public void setHSSFWorkbook(HSSFWorkbook wb) throws IOException {
		if (wb != null) {
			this.outputStream = new ByteArrayOutputStream();
			wb.write(this.outputStream);
		}
	}

	public String getFileName() {
		if (StringUtils.isNotBlank(this.fileName)) {
			try {
				return new String(this.fileName.getBytes("gbk"), "ISO8859-1");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return null;
			}
		}

		return this.fileName;
	}

	public String getContentType() {
		if (this.reportType == ExportReportType.EXECL)
			return "application/vnd.ms-excel;charset=ISO8859-1";
		if (this.reportType == ExportReportType.PDF) {
			return "application/pdf;charset=ISO8859-1";
		}
		return "";
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public ByteArrayOutputStream getOutputStream() {
		return this.outputStream;
	}

	public void setOutputStream(ByteArrayOutputStream outputStream) {
		this.outputStream = outputStream;
	}
}
