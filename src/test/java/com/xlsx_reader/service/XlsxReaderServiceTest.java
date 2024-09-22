package com.xlsx_reader.service;

import com.xlsx_reader.domain.Employee;
import com.xlsx_reader.domain.Individual;
import com.xlsx_reader.domain.Company;
import com.xlsx_reader.exception.ExcelParsingException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class XlsxReaderServiceTest {

    private final XlsxReaderService service = new XlsxReaderService();
    private File tempFile;

    @AfterEach
    public void cleanup() throws IOException {
        if (tempFile != null && tempFile.exists()) {
            Files.delete(tempFile.toPath());
        }
    }

    @Test
    public void testReadEmployees_SkipHeaderRows() throws Exception {
        tempFile = File.createTempFile("testSkipHeaderRows", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(tempFile);
                Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();

            for (int i = 0; i < XlsxReaderService.HEADER_ROWS_TO_SKIP; i++) {
                sheet.createRow(i);
            }

            Row individualRow = sheet.createRow(XlsxReaderService.HEADER_ROWS_TO_SKIP);
            individualRow.createCell(XlsxReaderService.ID_COL).setCellValue(1);
            individualRow.createCell(XlsxReaderService.EMAIL_COL).setCellValue("john@example.com");
            individualRow.createCell(XlsxReaderService.PHONE_COL).setCellValue("1234567890");
            individualRow.createCell(XlsxReaderService.ADDRESS_COL).setCellValue("123 Main St");
            individualRow.createCell(XlsxReaderService.FIRST_NAME_COL).setCellValue("John");
            individualRow.createCell(XlsxReaderService.LAST_NAME_COL).setCellValue("Doe");
            individualRow.createCell(XlsxReaderService.HAS_CHILDREN_COL).setCellValue(true);
            individualRow.createCell(XlsxReaderService.AGE_COL).setCellValue(30);
            individualRow.createCell(XlsxReaderService.IBAN_COL).setCellValue("some-iban");
            individualRow.createCell(XlsxReaderService.BIC_COL).setCellValue("some-bic");
            individualRow.createCell(XlsxReaderService.ACCOUNT_HOLDER_COL).setCellValue("Some Holder");

            Row companyRow = sheet.createRow(XlsxReaderService.HEADER_ROWS_TO_SKIP + 1);
            companyRow.createCell(XlsxReaderService.ID_COL).setCellValue(2);
            companyRow.createCell(XlsxReaderService.EMAIL_COL).setCellValue("jane@example.com");
            companyRow.createCell(XlsxReaderService.PHONE_COL).setCellValue("0987654321");
            companyRow.createCell(XlsxReaderService.ADDRESS_COL).setCellValue("456 Elm St");
            companyRow.createCell(XlsxReaderService.COMPANY_NAME_COL).setCellValue("Company Inc");
            companyRow.createCell(XlsxReaderService.COMPANY_TYPE_COL).setCellValue("SARS");
            companyRow.createCell(XlsxReaderService.IBAN_COL).setCellValue("some-iban");
            companyRow.createCell(XlsxReaderService.BIC_COL).setCellValue("some-bic");
            companyRow.createCell(XlsxReaderService.ACCOUNT_HOLDER_COL).setCellValue("Some Holder");

            workbook.write(fos);
        }

        List<Employee> employees = service.readEmployees(tempFile.getAbsolutePath());

        assertEquals(2, employees.size());
        assertTrue(employees.get(0) instanceof Individual);
        assertTrue(employees.get(1) instanceof Company);
    }

    @Test
    public void testReadEmployees_EmptyTable() throws Exception {
        tempFile = File.createTempFile("testEmptyEmployees", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(tempFile);
                Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();

            sheet.createRow(0);
            workbook.write(fos);
        }

        List<Employee> employees = service.readEmployees(tempFile.getAbsolutePath());

        assertTrue(employees.isEmpty(), "Employees list should be empty for an empty table");
    }

    @Test
    public void testReadEmployees_InvalidData_MissingFirstName() throws Exception {
        tempFile = File.createTempFile("testInvalidMissingFirstName", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(tempFile);
                Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();

            for (int i = 0; i < XlsxReaderService.HEADER_ROWS_TO_SKIP; i++) {
                sheet.createRow(i);
            }

            Row row = sheet.createRow(XlsxReaderService.HEADER_ROWS_TO_SKIP);
            row.createCell(XlsxReaderService.ID_COL).setCellValue(1);
            row.createCell(XlsxReaderService.EMAIL_COL).setCellValue("john@example.com");
            row.createCell(XlsxReaderService.PHONE_COL).setCellValue("1234567890");
            row.createCell(XlsxReaderService.ADDRESS_COL).setCellValue("123 Main St");
            // First name is missing
            row.createCell(XlsxReaderService.LAST_NAME_COL).setCellValue("Doe");
            row.createCell(XlsxReaderService.HAS_CHILDREN_COL).setCellValue("ИСТИНА");
            row.createCell(XlsxReaderService.AGE_COL).setCellValue(30);

            workbook.write(fos);
        }

        assertThrows(ExcelParsingException.class, () -> {
            service.readEmployees(tempFile.getAbsolutePath());
        });
    }

    @Test
    public void testReadEmployees_ValidIndividual() throws Exception {
        tempFile = File.createTempFile("testValidIndividual", ".xlsx");
        try (FileOutputStream fos = new FileOutputStream(tempFile);
                Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet();

            for (int i = 0; i < XlsxReaderService.HEADER_ROWS_TO_SKIP; i++) {
                sheet.createRow(i);
            }

            Row row = sheet.createRow(XlsxReaderService.HEADER_ROWS_TO_SKIP);
            row.createCell(XlsxReaderService.ID_COL).setCellValue(1);
            row.createCell(XlsxReaderService.EMAIL_COL).setCellValue("john@example.com");
            row.createCell(XlsxReaderService.PHONE_COL).setCellValue("1234567890");
            row.createCell(XlsxReaderService.ADDRESS_COL).setCellValue("123 Main St");
            row.createCell(XlsxReaderService.FIRST_NAME_COL).setCellValue("John");
            row.createCell(XlsxReaderService.LAST_NAME_COL).setCellValue("Doe");
            row.createCell(XlsxReaderService.HAS_CHILDREN_COL).setCellValue(true);
            row.createCell(XlsxReaderService.AGE_COL).setCellValue(30);
            row.createCell(XlsxReaderService.IBAN_COL).setCellValue("some-iban");
            row.createCell(XlsxReaderService.BIC_COL).setCellValue("some-bic");
            row.createCell(XlsxReaderService.ACCOUNT_HOLDER_COL).setCellValue("Some Holder");

            workbook.write(fos);
        }

        List<Employee> employees = service.readEmployees(tempFile.getAbsolutePath());

        assertEquals(1, employees.size());
        assertTrue(employees.get(0) instanceof Individual);
    }
}
