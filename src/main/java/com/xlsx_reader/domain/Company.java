package com.xlsx_reader.domain;

public class Company extends Employee {
    private String name;
    private CompanyType type;

    public Company(
            long id,
            String email,
            String phone,
            String address,
            BankAccount bankAccount,
            String name,
            CompanyType type) {
        super(id, email, phone, address, bankAccount);
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public CompanyType getType() {
        return type;
    }
}
