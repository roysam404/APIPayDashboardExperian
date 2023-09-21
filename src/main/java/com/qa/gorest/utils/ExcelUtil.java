package com.qa.gorest.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;



public class ExcelUtil {	

	public static Workbook book;
	public static Sheet sheet;

	public static Object[][] getTestData(String workBookName, String sheetName, String uniquevalue,String howManyData) throws Exception {

		String TEST_DATA_SHEET_PATH = System.getProperty("user.dir")+"/src/test/resources/testdata/"+workBookName+".xlsx";
		System.out.println("reading the WorkbookName: " + workBookName);
		System.out.println("reading the data from sheet: " + sheetName);
		System.out.println("reading the data from sheet: " + uniquevalue);

		Object data[][] = null;		

		try {
			FileInputStream ip = new FileInputStream(TEST_DATA_SHEET_PATH);
			book = WorkbookFactory.create(ip);
			sheet = book.getSheet(sheetName);
			int uniqueRow = 0;
			
			if(howManyData.toString().toUpperCase()=="UNIQUE")
			{
				data = new Object[sheet.getLastRowNum()][sheet.getRow(0).getLastCellNum()];

				for (int i = 0; i < sheet.getLastRowNum(); i++) {
					for (int j = 0; j < sheet.getRow(0).getLastCellNum(); j++) {
						if (sheet.getRow(i + 1).getCell(0).toString().equalsIgnoreCase(uniquevalue.trim()))
							uniqueRow = i;
						break;
					}
				}

				for (int j = 0; j < sheet.getRow(uniqueRow).getLastCellNum(); j++) {
					data[uniqueRow][j] = sheet.getRow(uniqueRow).getCell(j).toString();
				}
			}
			
			if(howManyData.toString().toUpperCase()=="ALLDATA")
			{
				data = new Object[sheet.getLastRowNum()][sheet.getRow(0).getLastCellNum()];
				
				for(int i=0; i < sheet.getLastRowNum(); i++) {
					for(int j=0; j<sheet.getRow(0).getLastCellNum(); j++) {
						data[i][j] = sheet.getRow(i+1).getCell(j).toString();
					}
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();	
		} catch (IOException e) {
			e.printStackTrace();
		}

		return data;

	}
}