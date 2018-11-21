package com.fedex.smartpost.mts.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.fedex.smartpost.mts.factory.TeraDataDaoFactory;
import com.fedex.smartpost.mts.gateway.edw.TeraDataDao;
import com.fedex.smartpost.mts.services.MailSender;
import com.fedex.smartpost.mts.services.WindowsRegistryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ScanDashboard {
	private static final Log logger = LogFactory.getLog(ScanDashboard.class);
	private TeraDataDao teraDataDao;

	public ScanDashboard() throws Exception {
		if (WindowsRegistryService.credentialsSet()) {
			logger.info("Acquiring the TeraData Gateway.");
			teraDataDao = new TeraDataDaoFactory().getTeraDataDao();
		}
		else {
			logger.error("Attempting to run Sort Scan Dashboard without first setting database credentials.");
			throw new Exception("Must set credentials first");
		}
	}

	private XSSFWorkbook buildExcel(List<String[]> result) throws ParseException {
		int rownum = 0;
		Cell cell;
		Calendar calNow = Calendar.getInstance();
		Calendar calRow = Calendar.getInstance();
		calNow.add(Calendar.DATE, -15);
		logger.info("Starting buildExcel.");
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Results");
		CellStyle numberStyle = workbook.createCellStyle();
		numberStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
		CellStyle dateStyle = workbook.createCellStyle();
		dateStyle.setDataFormat(workbook.createDataFormat().getFormat("m/d/yyyy"));
		CellStyle percentStyle = workbook.createCellStyle();
		percentStyle.setDataFormat(workbook.createDataFormat().getFormat("0%"));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		for (String[] stringRow : result) {
			if (rownum > 0) {
				Date date = sdf.parse(stringRow[0]);
				calRow.setTimeInMillis(date.getTime());
				if (calRow.before(calNow)) {
					continue;
				}
			}
			logger.info("Creating row for Excel workbook.");
			Row row = sheet.createRow(rownum++);
			for (int colNum = 0, maxCol = stringRow.length; colNum < maxCol; colNum++) {
				logger.info("Creating cell (" + (rownum - 1) + "," + colNum + ") in Excel workbook.");
				cell = row.createCell(colNum);
				logger.info("Populating cell (" + (rownum - 1) + "," + colNum + ") with " + stringRow[colNum]);
				if (rownum == 1) {
					cell.setCellValue(stringRow[colNum]);
				}
				else {
					switch (colNum) {
						case 0:
							cell.setCellStyle(dateStyle);
							cell.setCellValue(sdf.parse(stringRow[colNum]));
							break;
						case 1:
							cell.setCellStyle(numberStyle);
							cell.setCellValue(Double.parseDouble(stringRow[colNum]));
							break;
						case 2:
							cell.setCellStyle(numberStyle);
							cell.setCellValue(Double.parseDouble(stringRow[colNum]));
							break;
					}
				}
			}
			if (rownum == 1) {
				cell = row.createCell(5);
				cell.setCellValue("Missing_Packages");
			}
			else {
				cell = row.createCell(3);
				cell.setCellType(Cell.CELL_TYPE_FORMULA);
				cell.setCellStyle(percentStyle);
				cell.setCellFormula("C" + rownum + "/B" + rownum);
				cell = row.createCell(5);
				cell.setCellType(Cell.CELL_TYPE_FORMULA);
				cell.setCellStyle(numberStyle);
				cell.setCellFormula("B" + rownum + "-C" + rownum);
			}
		}
		logger.info("Exiting buildExcel.");
		return workbook;
	}

	private String createExcelReport(List<String[]> result, String filename) throws IOException, ParseException {
		logger.info("Starting createExcelReport.");
		XSSFWorkbook workbook = buildExcel(result);
		logger.info("Creating " + filename);
		FileOutputStream fos = new FileOutputStream(new File(filename));
		logger.info("Writing Excel Workbook to " + filename);
		workbook.write(fos);
		logger.info("Closing " + filename);
		fos.close();
		logger.info("Exiting createExcelReport.");
		return filename;
	}

	public void process() throws Exception {
		String filename = "/Extract.xlsx";
		logger.info("process starting.");
		List<String[]> result = teraDataDao.retrieveDashboardResults();
		if ((result == null) || result.isEmpty()) {
			throw new Exception("Unexpected Execution Results - IT must review...");
		}
		createExcelReport(result, filename);
		logger.info("Excel File Created... Sending mail.");
		MailSender mailSender = new MailSender();
		mailSender.sendNotification("john.blimke@fedex.com,abenham@fedex.com,debbie.box@fedex.com,"
				+ "FXSPBusinessDevelopment@fedex.com,clayton.clouse@fedex.com,"
				+ "meenakshi.nagarajan@fedex.com,jeffrey.dodd@fedex.com,"
				+ "mark.dropp@fedex.com,joel.stoffel@fedex.com,srkeuler@fedex.com,"
				+ "ed.chaltry@fedex.com,jaclyn.tubbin@fedex.com,jmakinen@fedex.com,"
				+ "gavin.tierney@fedex.com,jason.esch@fedex.com,lee.vandehy@fedex.com,"
				+ "tplee@fedex.com", filename);
		logger.info("mainProcess exiting.");
	}
}
