package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Customer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

/**
 * Handles persistence of banking system data to local storage.
 * Demonstrates robust File I/O and Exception Handling.
 */
public class StorageService {
    private final String dataFilePath;

    public StorageService(String dataFilePath) {
        this.dataFilePath = dataFilePath;
    }

    public static class BankDataHolder implements Serializable {
        private static final long serialVersionUID = 1L;
        public final Map<String, Customer> customers;
        public final Map<String, Account> accounts;
        public final int nextCustomerSeq;
        public final int nextAccountSeq;

        public BankDataHolder(Map<String, Customer> customers, Map<String, Account> accounts, 
                              int nextCustomerSeq, int nextAccountSeq) {
            this.customers = customers;
            this.accounts = accounts;
            this.nextCustomerSeq = nextCustomerSeq;
            this.nextAccountSeq = nextAccountSeq;
        }
    }

    public synchronized void saveData(Map<String, Customer> customers, Map<String, Account> accounts,
                                     int nextCustomerSeq, int nextAccountSeq) throws IOException {
        File file = new File(dataFilePath);
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            BankDataHolder holder = new BankDataHolder(customers, accounts, nextCustomerSeq, nextAccountSeq);
            oos.writeObject(holder);
        }
    }

    public synchronized BankDataHolder loadData() {
        File file = new File(dataFilePath);
        if (!file.exists()) {
            return null; // No existing file to load
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();
            if (obj instanceof BankDataHolder) {
                return (BankDataHolder) obj;
            }
        } catch (Exception e) {
            System.err.println("Notice: Could not load existing data (" + e.getMessage() + "). Starting fresh or loading defaults.");
        }
        return null;
    }

    public static void exportToFile(String content, String targetFilePath) throws IOException {
        Files.writeString(Paths.get(targetFilePath), content);
    }
}
