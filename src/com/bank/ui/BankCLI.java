package com.bank.ui;

import com.bank.exception.BankingException;
import com.bank.model.*;
import com.bank.service.BankService;
import com.bank.service.LoanService;
import com.bank.util.DemoRunner;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive Console Interface for the Banking System.
 */
public class BankCLI {
    private final BankService bankService;
    private final Scanner scanner;

    public BankCLI(BankService bankService) {
        this.bankService = bankService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printBanner();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Select an option (1-8): ", 1, 8);

            switch (choice) {
                case 1:
                    handleOpenAccount();
                    break;
                case 2:
                    handleAccountLogin();
                    break;
                case 3:
                    handleStatementExport();
                    break;
                case 4:
                    handleLoanCalculator();
                    break;
                case 5:
                    handleInterestSimulation();
                    break;
                case 6:
                    handleAdminDashboard();
                    break;
                case 7:
                    DemoRunner.runDemo(bankService);
                    break;
                case 8:
                    bankService.saveData();
                    ConsoleColors.printSuccess("All banking records persisted securely. Thank you for banking with us!");
                    running = false;
                    break;
            }
        }
    }

    private void printBanner() {
        System.out.println(ConsoleColors.CYAN + ConsoleColors.BOLD);
        System.out.println("╔═══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                        SECURE OOP JAVA BANKING SYSTEM                         ║");
        System.out.println("║                Engineered with OOP Principles | Roll: 25CE088                 ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════════════════════╝");
        System.out.println(ConsoleColors.RESET);
    }

    private void printMainMenu() {
        System.out.println("\n" + ConsoleColors.BOLD + "=================== MAIN MENU ===================" + ConsoleColors.RESET);
        System.out.println(" 1.  Open New Bank Account (Savings / Current / FD)");
        System.out.println(" 2.  Account Operations (Deposit / Withdraw / Transfer / Balance)");
        System.out.println(" 3.  View Passbook & Export Statement to File");
        System.out.println(" 4.  Loan & EMI Calculator (Extra Feature)");
        System.out.println(" 5.  Simulate Periodic Interest Payout");
        System.out.println(" 6.  Bank Administration Dashboard & Ledgers");
        System.out.println(" 7.  Run Automated OOP Feature Showcase / Test Suite");
        System.out.println(" 8.  Save & Exit");
        System.out.println("-------------------------------------------------");
    }

    private void handleOpenAccount() {
        ConsoleColors.printHeader("Open New Bank Account");
        System.out.println("1. Existing Customer");
        System.out.println("2. New Customer Registration");
        int custChoice = readInt("Select option (1-2): ", 1, 2);

        String customerId;
        if (custChoice == 1) {
            customerId = readString("Enter Customer ID (e.g. CUST1001): ");
            try {
                Customer c = bankService.getCustomer(customerId);
                ConsoleColors.printSuccess("Customer found: " + c.getFullName());
            } catch (Exception e) {
                ConsoleColors.printError(e.getMessage());
                return;
            }
        } else {
            String name = readString("Enter Full Name: ");
            String email = readString("Enter Email Address: ");
            String phone = readString("Enter 10-digit Phone Number: ");
            String address = readString("Enter Address: ");

            try {
                Customer c = bankService.registerCustomer(name, email, phone, address);
                customerId = c.getCustomerId();
                ConsoleColors.printSuccess("Customer registered successfully! Customer ID: " + customerId);
            } catch (Exception e) {
                ConsoleColors.printError("Registration failed: " + e.getMessage());
                return;
            }
        }

        System.out.println("\nSelect Account Type:");
        System.out.println("1. Savings Account (Min Bal: ₹1000, 4.0% Interest p.a.)");
        System.out.println("2. Current Account (Overdraft facility, business account)");
        System.out.println("3. Fixed Deposit Account (Min: ₹5000, 7.5% Compound Interest)");
        int accTypeChoice = readInt("Select account type (1-3): ", 1, 3);

        String pin = readPin("Set a 4-digit numeric security PIN: ");

        try {
            switch (accTypeChoice) {
                case 1: {
                    double deposit = readDouble("Enter initial deposit amount (Min ₹1,000): ", 1000.0, 10000000.0);
                    SavingsAccount sa = bankService.openSavingsAccount(customerId, deposit, pin);
                    ConsoleColors.printSuccess("Savings Account opened successfully! Account Number: " + sa.getAccountNumber());
                    break;
                }
                case 2: {
                    double deposit = readDouble("Enter initial deposit amount: ", 0.0, 10000000.0);
                    double overdraft = readDouble("Requested Overdraft Limit (Default ₹25,000): ", 5000.0, 500000.0);
                    CurrentAccount ca = bankService.openCurrentAccount(customerId, deposit, pin, overdraft);
                    ConsoleColors.printSuccess("Current Account opened successfully! Account Number: " + ca.getAccountNumber());
                    break;
                }
                case 3: {
                    double deposit = readDouble("Enter Fixed Deposit Principal (Min ₹5,000): ", 5000.0, 10000000.0);
                    int months = readInt("Enter Tenure in Months (e.g. 12, 24, 36): ", 1, 120);
                    FixedDepositAccount fd = bankService.openFixedDepositAccount(customerId, deposit, pin, months);
                    ConsoleColors.printSuccess("Fixed Deposit opened! Account Number: " + fd.getAccountNumber());
                    ConsoleColors.printInfo(String.format("Estimated Maturity Value: ₹%.2f (Interest: ₹%.2f)",
                            fd.getEstimatedMaturityAmount(), fd.calculateInterest()));
                    break;
                }
            }
        } catch (Exception e) {
            ConsoleColors.printError("Account creation error: " + e.getMessage());
        }
    }

    private void handleAccountLogin() {
        ConsoleColors.printHeader("Account Operations Login");
        String accNum = readString("Enter Account Number: ");
        String pin = readPin("Enter 4-Digit Security PIN: ");

        Account account;
        try {
            account = bankService.authenticate(accNum, pin);
            ConsoleColors.printSuccess("Authentication successful! Welcome to " + account.getAccountType());
        } catch (BankingException e) {
            ConsoleColors.printError(e.getMessage());
            return;
        }

        boolean sessionActive = true;
        while (sessionActive) {
            System.out.println("\n" + ConsoleColors.BOLD + "--- Session: " + account.getAccountNumber() + " (" + account.getAccountType() + ") ---" + ConsoleColors.RESET);
            System.out.println("1. Check Balance & Summary");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Transfer Funds to Another Account");
            System.out.println("5. View Mini-Statement");
            System.out.println("6. Change PIN");
            System.out.println("7. Logout");

            int action = readInt("Select action (1-7): ", 1, 7);
            try {
                switch (action) {
                    case 1:
                        ConsoleColors.printInfo(account.getAccountSummary());
                        break;
                    case 2: {
                        double amt = readDouble("Enter amount to deposit: ", 1.0, 10000000.0);
                        String remarks = readString("Enter remarks / description (or press Enter): ");
                        bankService.deposit(account.getAccountNumber(), amt, remarks.isEmpty() ? "Cash Deposit" : remarks);
                        ConsoleColors.printSuccess(String.format("Deposit of ₹%.2f successful! New Balance: ₹%.2f", amt, account.getBalance()));
                        break;
                    }
                    case 3: {
                        double amt = readDouble("Enter amount to withdraw: ", 1.0, 10000000.0);
                        String remarks = readString("Enter remarks (or press Enter): ");
                        bankService.withdraw(account.getAccountNumber(), pin, amt, remarks.isEmpty() ? "Cash Withdrawal" : remarks);
                        ConsoleColors.printSuccess(String.format("Withdrawal of ₹%.2f successful! Current Balance: ₹%.2f", amt, account.getBalance()));
                        break;
                    }
                    case 4: {
                        String toAcc = readString("Enter Destination Account Number: ");
                        double amt = readDouble("Enter transfer amount: ", 1.0, 10000000.0);
                        String remarks = readString("Enter transfer remarks (or press Enter): ");
                        bankService.transfer(account.getAccountNumber(), pin, toAcc, amt, remarks.isEmpty() ? "Inter-account Transfer" : remarks);
                        ConsoleColors.printSuccess(String.format("Transferred ₹%.2f to %s successfully! Remaining Balance: ₹%.2f", 
                                                                amt, toAcc, account.getBalance()));
                        break;
                    }
                    case 5:
                        System.out.println(account.generateStatement());
                        break;
                    case 6: {
                        String oldPin = readPin("Enter current PIN: ");
                        String newPin = readPin("Enter new 4-digit PIN: ");
                        bankService.changePin(account.getAccountNumber(), oldPin, newPin);
                        ConsoleColors.printSuccess("PIN updated successfully!");
                        break;
                    }
                    case 7:
                        sessionActive = false;
                        ConsoleColors.printInfo("Logged out of account " + account.getAccountNumber());
                        break;
                }
            } catch (BankingException e) {
                ConsoleColors.printError(e.getMessage());
            }
        }
    }

    private void handleStatementExport() {
        ConsoleColors.printHeader("Passbook Statement & Export");
        String accNum = readString("Enter Account Number: ");
        try {
            Account account = bankService.getAccount(accNum);
            System.out.println(account.generateStatement());

            String export = readString("Would you like to export this statement to a text file? (y/n): ");
            if (export.equalsIgnoreCase("y") || export.equalsIgnoreCase("yes")) {
                File dir = new File("exports");
                if (!dir.exists()) dir.mkdirs();
                String targetPath = "exports/statement_" + accNum + ".txt";
                account.exportStatementToFile(targetPath);
                ConsoleColors.printSuccess("Statement successfully saved to: " + targetPath);
            }
        } catch (BankingException e) {
            ConsoleColors.printError(e.getMessage());
        } catch (IOException e) {
            ConsoleColors.printError("File export error: " + e.getMessage());
        }
    }

    private void handleLoanCalculator() {
        ConsoleColors.printHeader("Loan & EMI Calculator");
        double principal = readDouble("Enter Loan Principal Amount (₹): ", 1000.0, 100000000.0);
        double rate = readDouble("Enter Annual Interest Rate (%%): ", 0.1, 50.0);
        int tenure = readInt("Enter Loan Tenure in Months (e.g. 12, 24, 36, 60): ", 1, 360);

        System.out.println("\n" + ConsoleColors.BOLD + "=== Loan Amortization Schedule Preview ===" + ConsoleColors.RESET);
        System.out.println(LoanService.getAmortizationPreview(principal, rate, tenure, 6));
    }

    private void handleInterestSimulation() {
        ConsoleColors.printHeader("Periodic Interest Payout Simulation");
        System.out.println("This batch process calculates and credits periodic interest to all Savings accounts in the bank.");
        String confirm = readString("Execute interest payout now? (y/n): ");
        if (confirm.equalsIgnoreCase("y") || confirm.equalsIgnoreCase("yes")) {
            double total = bankService.applyMonthlyInterestToAllSavings();
            ConsoleColors.printSuccess(String.format("Batch process completed! Total interest credited across accounts: ₹%.2f", total));
        } else {
            ConsoleColors.printInfo("Batch process cancelled.");
        }
    }

    private void handleAdminDashboard() {
        ConsoleColors.printHeader("Bank Administration Dashboard");
        List<Account> accounts = bankService.getAllAccounts();
        List<Customer> customers = bankService.getAllCustomers();

        double totalDeposits = 0;
        int lockedCount = 0;
        for (Account a : accounts) {
            if (a.getBalance() > 0) totalDeposits += a.getBalance();
            if (a.isLocked()) lockedCount++;
        }

        System.out.println(String.format("Total Customers Registered : %d", customers.size()));
        System.out.println(String.format("Total Bank Accounts        : %d", accounts.size()));
        System.out.println(String.format("Total Cumulative Deposits  : ₹%.2f", totalDeposits));
        System.out.println(String.format("Accounts Under Security Lock: %d", lockedCount));
        System.out.println("--------------------------------------------------------------------------------");

        System.out.println(ConsoleColors.BOLD + "All Registered Bank Accounts:" + ConsoleColors.RESET);
        for (Account a : accounts) {
            System.out.println(" • " + a.getAccountSummary());
        }

        System.out.println("\nAdmin Actions:");
        System.out.println("1. Unlock a locked account");
        System.out.println("2. Return to Main Menu");
        int sub = readInt("Choice (1-2): ", 1, 2);
        if (sub == 1) {
            String acc = readString("Enter Account Number to unlock: ");
            try {
                bankService.unlockAccount(acc);
                ConsoleColors.printSuccess("Account " + acc + " has been unlocked!");
            } catch (Exception e) {
                ConsoleColors.printError(e.getMessage());
            }
        }
    }

    // Input Helper Methods
    private int readInt(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int val = Integer.parseInt(input);
                if (val >= min && val <= max) {
                    return val;
                }
                ConsoleColors.printWarning(String.format("Please enter a number between %d and %d.", min, max));
            } catch (NumberFormatException e) {
                ConsoleColors.printWarning("Invalid input. Please enter a valid integer.");
            }
        }
    }

    private double readDouble(String prompt, double min, double max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                double val = Double.parseDouble(input);
                if (val >= min && val <= max) {
                    return val;
                }
                ConsoleColors.printWarning(String.format("Please enter a value between ₹%.2f and ₹%.2f.", min, max));
            } catch (NumberFormatException e) {
                ConsoleColors.printWarning("Invalid input. Please enter a valid decimal number.");
            }
        }
    }

    private String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private String readPin(String prompt) {
        while (true) {
            System.out.print(prompt);
            String pin = scanner.nextLine().trim();
            if (pin.length() == 4 && pin.chars().allMatch(Character::isDigit)) {
                return pin;
            }
            ConsoleColors.printWarning("PIN must be exactly 4 numeric digits (e.g., 1234). Try again.");
        }
    }
}
