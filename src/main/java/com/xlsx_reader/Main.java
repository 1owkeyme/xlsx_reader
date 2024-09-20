package com.xlsx_reader;

import com.xlsx_reader.domain.Employee;
import com.xlsx_reader.exception.ExcelParsingException;
import com.xlsx_reader.exception.InvalidFilePathException;
import com.xlsx_reader.service.XlsxReaderService;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Please provide the path to the xlsx file");
            System.exit(1);
        }

        String filePath = args[0];

        try {
            validateFilePath(filePath);
        } catch (InvalidFilePathException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        XlsxReaderService XlsxReaderService = new XlsxReaderService();

        List<Employee> employees = new ArrayList<Employee>();
        try {
            employees.addAll(XlsxReaderService.readEmployees(filePath));
        } catch (ExcelParsingException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        System.out.println("Total employees: " + employees.size());

        printSummary(employees);
    }

    private static void printSummary(List<Employee> employees) {
        long individualCount = employees.stream()
                .filter(e -> e instanceof com.xlsx_reader.domain.Individual)
                .count();

        long companyCount = employees.stream()
                .filter(e -> e instanceof com.xlsx_reader.domain.Company)
                .count();

        long individualsUnder20 = employees.stream()
                .filter(e -> e instanceof com.xlsx_reader.domain.Individual)
                .map(e -> (com.xlsx_reader.domain.Individual) e)
                .filter(i -> i.getAge() < 20)
                .count();

        System.out.println("Total individuals: " + individualCount);
        System.out.println("Total companies: " + companyCount);
        System.out.println("Total individuals under 20: " + individualsUnder20);

        System.out.println("Names and surnames of employees:");
        employees.stream()
                .filter(e -> e instanceof com.xlsx_reader.domain.Individual)
                .map(e -> (com.xlsx_reader.domain.Individual) e)
                .forEach(i -> System.out.println(i.getFirstName() + " " + i.getLastName()));
    }

    private static void validateFilePath(String filePath) throws InvalidFilePathException {
        File file = new File(filePath);
        if (!Files.exists(file.toPath()) || !Files.isRegularFile(file.toPath())) {
            throw new InvalidFilePathException("Invalid file path: " + filePath);
        }
    }
}