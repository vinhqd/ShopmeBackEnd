package com.shopme.admin.user;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.shopme.common.entity.User;

public class UserExcelExporter extends AbstractExporter {
	
	private XSSFWorkbook workBook;
	private XSSFSheet sheet;
	
	public UserExcelExporter() {
		workBook = new XSSFWorkbook();
	}
	
	private void writeHeaderLine() {
		sheet = workBook.createSheet();
		
		XSSFRow row = sheet.createRow(0);
		
		XSSFCellStyle cellStyle = workBook.createCellStyle();
		XSSFFont font = workBook.createFont();
		font.setBold(true);
		font.setFontHeight(16);
		cellStyle.setFont(font);
		createCell(row, 0, "User ID", cellStyle);
		createCell(row, 1, "E-mail", cellStyle);
		createCell(row, 2, "First Name", cellStyle);
		createCell(row, 3, "Last Name", cellStyle);
		createCell(row, 4, "Roles", cellStyle);
		createCell(row, 5, "Enabled", cellStyle);
	}
	
	private void createCell(XSSFRow row, int columnIndex, Object value, CellStyle style) {
		XSSFCell cell = row.createCell(columnIndex);
		sheet.autoSizeColumn(columnIndex);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else if (value instanceof String) {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}
	
	public void export(List<User> users, HttpServletResponse response) throws IOException {
		setResponseHeader(response, "application/octet-stream", ".xlsx");
		
		writeHeaderLine();
		writeDataLines(users);
		
		ServletOutputStream outputStream = response.getOutputStream();
		workBook.write(outputStream);
		workBook.close();
		outputStream.close();
	}

	private void writeDataLines(List<User> users) {
		int rowIndex = 1;
		
		XSSFCellStyle cellStyle = workBook.createCellStyle();
		XSSFFont font = workBook.createFont();
		font.setFontHeight(14);
		cellStyle.setFont(font);
		
		for (User user: users) {
			XSSFRow row = sheet.createRow(rowIndex++);
			int columnIndex = 0;
			
			createCell(row, columnIndex++, user.getId(), cellStyle);
			createCell(row, columnIndex++, user.getEmail(), cellStyle);
			createCell(row, columnIndex++, user.getFirstName(), cellStyle);
			createCell(row, columnIndex++, user.getLastName(), cellStyle);
			createCell(row, columnIndex++, user.getRoles().toString(), cellStyle);
			createCell(row, columnIndex++, user.isEnabled(), cellStyle);
		}
		
	}

}
