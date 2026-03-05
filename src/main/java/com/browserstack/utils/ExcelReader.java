package com.browserstack.utils;

import com.browserstack.exceptions.DataReadException;
import com.browserstack.models.UserData;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class ExcelReader {

    public static UserData readData(String ruta) {
        try (FileInputStream fis = new FileInputStream(ruta);
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row row = sheet.getRow(1);
            String userName = row.getCell(0).getStringCellValue();
            String password = row.getCell(1).getStringCellValue();
            String firstName = row.getCell(2).getStringCellValue();
            String lastName = row.getCell(3).getStringCellValue();
            String address = row.getCell(4).getStringCellValue();
            String state = row.getCell(5).getStringCellValue();
            String postalCode = String.valueOf((int) row.getCell(6).getNumericCellValue());
            return new UserData(userName, password, firstName, lastName, address, state, postalCode);
        } catch (IOException e) {
            throw new DataReadException("No se pudo leer el archivo de datos: " + ruta, e);
        }
    }
}
