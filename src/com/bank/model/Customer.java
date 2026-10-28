package com.bank.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a Bank Customer holding one or more accounts.
 * Demonstrates Encapsulation and Data Validation.
 */
public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String customerId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private final List<String> accountNumbers;

    public Customer(String customerId, String fullName, String email, String phoneNumber, String address) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty.");
        }
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Valid email address is required (e.g., user@example.com).");
        }
        if (phoneNumber == null || !phoneNumber.matches("^\\d{10}$")) {
            throw new IllegalArgumentException("Phone number must be a valid 10-digit number.");
        }

        this.customerId = customerId;
        this.fullName = fullName.trim();
        this.email = email.trim();
        this.phoneNumber = phoneNumber.trim();
        this.address = (address != null && !address.trim().isEmpty()) ? address.trim() : "Unspecified";
        this.accountNumbers = new ArrayList<>();
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName != null && !fullName.trim().isEmpty()) {
            this.fullName = fullName.trim();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            this.email = email.trim();
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber != null && phoneNumber.matches("^\\d{10}$")) {
            this.phoneNumber = phoneNumber.trim();
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getAccountNumbers() {
        return Collections.unmodifiableList(accountNumbers);
    }

    public void addAccountNumber(String accountNumber) {
        if (!accountNumbers.contains(accountNumber)) {
            accountNumbers.add(accountNumber);
        }
    }

    @Override
    public String toString() {
        return String.format("Customer [%s] Name: %s | Phone: %s | Email: %s | Accounts: %d",
                customerId, fullName, phoneNumber, email, accountNumbers.size());
    }
}
