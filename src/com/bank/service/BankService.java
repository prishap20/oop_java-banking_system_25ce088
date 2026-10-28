package com.bank.service;

import com.bank.exception.*;
import com.bank.model.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Facade and Service Layer handling core business logic, accounts, customers,
 * transactions, and persistent storage.
 */
public class BankService {
    private final Map<String, Customer> customers;
    private final Map<String, Account> accounts;
    private final StorageService storageService;

    private int customerSequence = 1001;
    private int accountSequence = 25001;

    public BankService(String dataFilePath) {
        this.customers = new ConcurrentHashMap<>();
        this.accounts = new ConcurrentHashMap<>();
        this.storageService = new StorageService(dataFilePath);
        loadData();
    }

    // Customer Management
    public synchronized Customer registerCustomer(String fullName, String email, String phoneNumber, String address) {
        String customerId = "CUST" + (customerSequence++);
        Customer customer = new Customer(customerId, fullName, email, phoneNumber, address);
        customers.put(customerId, customer);
        autoSave();
        return customer;
    }

    public Customer getCustomer(String customerId) throws AccountNotFoundException {
        Customer c = customers.get(customerId);
        if (c == null) {
            throw new AccountNotFoundException("Customer with ID " + customerId + " not found.");
        }
        return c;
    }

    public List<Customer> getAllCustomers() {
        return new ArrayList<>(customers.values());
    }

    // Account Creation
    public synchronized SavingsAccount openSavingsAccount(String customerId, double initialDeposit, String pin) 
            throws BankingException {
        Customer customer = getCustomer(customerId);
        String accountNumber = "SB" + (accountSequence++);
        SavingsAccount account = new SavingsAccount(accountNumber, customerId, initialDeposit, pin);
        accounts.put(accountNumber, account);
        customer.addAccountNumber(accountNumber);
        autoSave();
        return account;
    }

    public synchronized CurrentAccount openCurrentAccount(String customerId, double initialDeposit, String pin, double overdraftLimit) 
            throws BankingException {
        Customer customer = getCustomer(customerId);
        String accountNumber = "CA" + (accountSequence++);
        CurrentAccount account = new CurrentAccount(accountNumber, customerId, initialDeposit, pin, 
                                                    overdraftLimit > 0 ? overdraftLimit : CurrentAccount.DEFAULT_OVERDRAFT_LIMIT, 
                                                    CurrentAccount.DEFAULT_OVERDRAFT_FEE_PERCENT);
        accounts.put(accountNumber, account);
        customer.addAccountNumber(accountNumber);
        autoSave();
        return account;
    }

    public synchronized FixedDepositAccount openFixedDepositAccount(String customerId, double depositAmount, String pin, int tenureMonths) 
            throws BankingException {
        Customer customer = getCustomer(customerId);
        String accountNumber = "FD" + (accountSequence++);
        FixedDepositAccount account = new FixedDepositAccount(accountNumber, customerId, depositAmount, pin, tenureMonths);
        accounts.put(accountNumber, account);
        customer.addAccountNumber(accountNumber);
        autoSave();
        return account;
    }

    // Account Operations
    public Account getAccount(String accountNumber) throws AccountNotFoundException {
        Account account = accounts.get(accountNumber);
        if (account == null) {
            throw new AccountNotFoundException(accountNumber);
        }
        return account;
    }

    public Account authenticate(String accountNumber, String pin) 
            throws AccountNotFoundException, AuthenticationException {
        Account account = getAccount(accountNumber);
        account.verifyPin(pin);
        return account;
    }

    public void deposit(String accountNumber, double amount, String remarks) 
            throws AccountNotFoundException, InvalidAmountException {
        Account account = getAccount(accountNumber);
        account.deposit(amount, remarks);
        autoSave();
    }

    public void withdraw(String accountNumber, String pin, double amount, String remarks) 
            throws BankingException {
        Account account = authenticate(accountNumber, pin);
        account.withdraw(amount, remarks);
        autoSave();
    }

    public void transfer(String fromAccountNum, String pin, String toAccountNum, double amount, String remarks) 
            throws BankingException {
        Account fromAccount = authenticate(fromAccountNum, pin);
        Account toAccount = getAccount(toAccountNum);

        fromAccount.transfer(toAccount, amount, remarks);
        autoSave();
    }

    public void changePin(String accountNumber, String currentPin, String newPin) 
            throws BankingException {
        Account account = authenticate(accountNumber, currentPin);
        account.changePin(currentPin, newPin);
        autoSave();
    }

    public void unlockAccount(String accountNumber) throws AccountNotFoundException {
        Account account = getAccount(accountNumber);
        account.unlockAccount();
        autoSave();
    }

    public List<Account> getAllAccounts() {
        return new ArrayList<>(accounts.values());
    }

    public List<Account> getCustomerAccounts(String customerId) {
        List<Account> list = new ArrayList<>();
        for (Account a : accounts.values()) {
            if (a.getCustomerId().equals(customerId)) {
                list.add(a);
            }
        }
        return list;
    }

    /**
     * Batch job to apply monthly interest payouts across all savings accounts.
     * @return Total interest credited across the bank
     */
    public synchronized double applyMonthlyInterestToAllSavings() {
        double totalInterestDistributed = 0.0;
        for (Account acc : accounts.values()) {
            if (acc instanceof SavingsAccount) {
                SavingsAccount sa = (SavingsAccount) acc;
                totalInterestDistributed += sa.applyInterest();
            }
        }
        autoSave();
        return totalInterestDistributed;
    }

    // Persistence
    public synchronized void saveData() {
        try {
            storageService.saveData(customers, accounts, customerSequence, accountSequence);
        } catch (IOException e) {
            System.err.println("Warning: Failed to save bank data: " + e.getMessage());
        }
    }

    public synchronized void loadData() {
        StorageService.BankDataHolder holder = storageService.loadData();
        if (holder != null && holder.customers != null && holder.accounts != null) {
            this.customers.clear();
            this.customers.putAll(holder.customers);
            this.accounts.clear();
            this.accounts.putAll(holder.accounts);
            this.customerSequence = holder.nextCustomerSeq;
            this.accountSequence = holder.nextAccountSeq;
        } else {
            // Seed with realistic demo sample data
            initializeDefaultData();
        }
    }

    private void autoSave() {
        saveData();
    }

    /**
     * Seeds initial demo accounts and customers if database is fresh.
     */
    public void initializeDefaultData() {
        try {
            Customer c1 = registerCustomer("Alice Sharma", "alice.sharma@example.com", "9876543210", "42 MG Road, Bangalore");
            Customer c2 = registerCustomer("Bob Verma", "bob.verma@example.com", "9812345678", "15 Park Street, Kolkata");
            Customer c3 = registerCustomer("Charlie Patel", "charlie.p@example.com", "9898989898", "7 Ring Road, Ahmedabad");

            openSavingsAccount(c1.getCustomerId(), 15000.0, "1234");
            openCurrentAccount(c2.getCustomerId(), 50000.0, "4321", 30000.0);
            openFixedDepositAccount(c3.getCustomerId(), 100000.0, "1111", 12);
            openSavingsAccount(c3.getCustomerId(), 5000.0, "2222");

            saveData();
        } catch (Exception e) {
            System.err.println("Notice initializing demo data: " + e.getMessage());
        }
    }
}
