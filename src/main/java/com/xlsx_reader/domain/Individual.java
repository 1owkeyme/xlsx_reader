package com.xlsx_reader.domain;

public class Individual extends Employee {
    private String firstName;
    private String lastName;
    private boolean hasChildren;
    private int age;

    public Individual(
            long id,
            String email,
            String phone,
            String address,
            BankAccount bankAccount,
            String name,
            String surname,
            boolean hasChildren,
            int age) {
        super(id, email, phone, address, bankAccount);

        this.firstName = name;
        this.lastName = surname;
        this.hasChildren = hasChildren;
        this.age = age;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public boolean isHasChildren() {
        return hasChildren;
    }

    public int getAge() {
        return age;
    }
}
