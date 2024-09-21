package com.xlsx_reader.service;

import com.xlsx_reader.domain.*;
import com.xlsx_reader.exception.ExcelParsingException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XlsxReaderService {
    protected static final int ID_COL = 0;
    protected static final int EMAIL_COL = 2;
    protected static final int PHONE_COL = 3;
    protected static final int ADDRESS_COL = 4;
    protected static final int FIRST_NAME_COL = 6;
    protected static final int LAST_NAME_COL = 7;
    protected static final int HAS_CHILDREN_COL = 8;
    protected static final int AGE_COL = 9;
    protected static final int COMPANY_NAME_COL = 11;
    protected static final int COMPANY_TYPE_COL = 12;
    protected static final int IBAN_COL = 14;
    protected static final int BIC_COL = 15;
    protected static final int ACCOUNT_HOLDER_COL = 16;

    protected static final int HEADER_ROWS_TO_SKIP = 3;

    public List<Employee> readEmployees(String filePath) throws ExcelParsingException {
        List<Employee> employees = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(filePath);
                Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = HEADER_ROWS_TO_SKIP; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                long id = (long) row.getCell(ID_COL).getNumericCellValue();
                if (row == null || id == 0)
                    break;
                Employee employee = processRow(row, id);
                employees.add(employee);
            }
        } catch (IOException e) {
            throw new ExcelParsingException("Error reading Excel file: " + e.getMessage(), e);
        }

        return employees;
    }

    private Employee processRow(Row row, long id) throws ExcelParsingException {
        try {
            String email = getCellValue(row, EMAIL_COL, String.class);
            String phone = getCellValue(row, PHONE_COL, String.class);
            String address = getCellValue(row, ADDRESS_COL, String.class);

            BankAccount bankAccount = createBankAccount(row);

            return createEmployee(row, id, email, phone, address, bankAccount);
        } catch (Exception e) {
            throw new ExcelParsingException("Error processing row: " + row.getRowNum() + " - " + e.getMessage(), e);
        }
    }

    private Employee createEmployee(
            Row row,
            long id,
            String email,
            String phone,
            String address,
            BankAccount bankAccount) throws ExcelParsingException {

        String firstName = getCellValue(row, FIRST_NAME_COL, String.class);
        String lastName = getCellValue(row, LAST_NAME_COL, String.class);
        String companyName = getCellValue(row, COMPANY_NAME_COL, String.class);
        String companyTypeValue = getCellValue(row, COMPANY_TYPE_COL, String.class);

        if (firstName != null && !firstName.trim().isEmpty() &&
                lastName != null && !lastName.trim().isEmpty()) {
            return createIndividual(row, id, email, phone, address, bankAccount, firstName, lastName);
        }

        if (companyName != null && !companyName.trim().isEmpty() &&
                companyTypeValue != null && !companyTypeValue.trim().isEmpty()) {
            return createCompany(row, id, email, phone, address, bankAccount, companyName, companyTypeValue);
        }

        throw new ExcelParsingException("Insufficient data to determine employee type on row: " + row.getRowNum());
    }

    private Individual createIndividual(
            Row row,
            long id,
            String email,
            String phone,
            String address,
            BankAccount bankAccount,
            String firstName,
            String lastName) throws ExcelParsingException {
        boolean hasChildren = getCellValue(row, HAS_CHILDREN_COL, Boolean.class);
        int age = getCellValue(row, AGE_COL, Integer.class);
        return new Individual(id, email, phone, address, bankAccount, firstName, lastName, hasChildren, age);
    }

    private Company createCompany(
            Row row,
            long id,
            String email,
            String phone,
            String address,
            BankAccount bankAccount,
            String companyName,
            String companyTypeValue) throws ExcelParsingException {
        CompanyType companyType;
        try {
            companyType = CompanyType.valueOf(companyTypeValue);
        } catch (IllegalArgumentException e) {
            throw new ExcelParsingException("Invalid company type on row: " + row.getRowNum());
        }

        return new Company(id, email, phone, address, bankAccount, companyName, companyType);
    }

    private BankAccount createBankAccount(Row row) throws ExcelParsingException {
        String iban = getCellValue(row, IBAN_COL, String.class);
        String bic = getCellValue(row, BIC_COL, String.class);
        String accountHolder = getCellValue(row, ACCOUNT_HOLDER_COL, String.class);

        if (iban == null || bic == null || accountHolder == null) {
            throw new ExcelParsingException("Incomplete bank account data on row: " + row.getRowNum());
        }
        return new BankAccount(iban, bic, accountHolder);
    }

    private <T> T getCellValue(Row row, int columnIndex, Class<T> clazz) throws ExcelParsingException {
        Cell cell = row.getCell(columnIndex);
        if (cell == null)
            return null;

        CellType cellType = cell.getCellType();

        try {
            return switch (cellType) {
                case STRING -> {
                    if (clazz == String.class) {
                        yield clazz.cast(cell.getStringCellValue());
                    } else {
                        throw new ExcelParsingException(
                                "Expected " + clazz.getSimpleName() + " but got STRING at row: " + row.getRowNum());
                    }
                }
                case NUMERIC -> {
                    if (clazz == String.class) {
                        double numericValue = cell.getNumericCellValue();
                        if (numericValue == Math.floor(numericValue)) {
                            yield clazz.cast(String.valueOf((long) numericValue));
                        } else {
                            yield clazz.cast(String.valueOf(numericValue));
                        }
                    } else if (clazz == Long.class) {
                        yield clazz.cast((long) cell.getNumericCellValue());
                    } else if (clazz == Integer.class) {
                        yield clazz.cast((int) cell.getNumericCellValue());
                    } else {
                        throw new ExcelParsingException(
                                "Expected " + clazz.getSimpleName() + " but got NUMERIC at row: " + row.getRowNum());
                    }
                }
                case BOOLEAN -> {
                    if (clazz == String.class) {
                        yield clazz.cast(String.valueOf(cell.getBooleanCellValue()));
                    } else if (clazz == Boolean.class) {
                        yield clazz.cast(cell.getBooleanCellValue());
                    } else {
                        throw new ExcelParsingException(
                                "Expected " + clazz.getSimpleName() + " but got BOOLEAN at row: " + row.getRowNum());
                    }
                }
                case BLANK -> null;
                default -> throw new ExcelParsingException("Unsupported cell type: " + cellType);
            };
        } catch (ClassCastException e) {
            throw new ExcelParsingException("Error casting cell value at index " + columnIndex + ": " + e.getMessage(),
                    e);
        }
    }

}
