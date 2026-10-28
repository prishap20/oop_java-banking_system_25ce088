package com.bank.util;

import com.bank.exception.*;
import com.bank.model.*;
import com.bank.service.BankService;
import com.bank.service.LoanService;
import com.bank.ui.ConsoleColors;

import java.io.File;

/**
 * Automated Showcase Runner.
 * Executes scenarios demonstrating all OOP concepts and extra features end-to-end.
 */
public class DemoRunner {

    public static void runDemo(BankService bankService) {
        ConsoleColors.printHeader("Running Automated Banking System OOP Showcase");
        System.out.println("Student ID: 25CE088 | Course: Object-Oriented Programming (Java)");
        System.out.println("----------------------------------------------------------------------\n");

        try {
            // Step 1: Register Customers
            System.out.println(ConsoleColors.BOLD + "[Step 1] Customer Registration & Encapsulation" + ConsoleColors.RESET);
            Customer custA = bankService.registerCustomer("Prisha Patel", "prisha.patel@example.com", "9876543210", "101 Tech Boulevard");
            Customer custB = bankService.registerCustomer("Aarav Mehta", "aarav.mehta@example.com", "9823456789", "502 Silicon Avenue");
            ConsoleColors.printSuccess("Registered Customer 1: " + custA);
            ConsoleColors.printSuccess("Registered Customer 2: " + custB);
            System.out.println();

            // Step 2: Inheritance & Polymorphism - Opening different types of accounts
            System.out.println(ConsoleColors.BOLD + "[Step 2] Inheritance & Polymorphism - Account Hierarchy" + ConsoleColors.RESET);
            SavingsAccount sa = bankService.openSavingsAccount(custA.getCustomerId(), 20000.0, "1234");
            CurrentAccount ca = bankService.openCurrentAccount(custB.getCustomerId(), 15000.0, "5678", 20000.0);
            FixedDepositAccount fd = bankService.openFixedDepositAccount(custA.getCustomerId(), 50000.0, "9999", 12);

            ConsoleColors.printSuccess("Opened Savings Account : " + sa.getAccountNumber() + " (Min Bal: ₹1000, 4% Int)");
            ConsoleColors.printSuccess("Opened Current Account : " + ca.getAccountNumber() + " (Overdraft: ₹20000)");
            ConsoleColors.printSuccess("Opened Fixed Deposit   : " + fd.getAccountNumber() + " (Tenure: 12 mos, 7.5% Compound Int)");
            System.out.println();

            // Step 3: Deposit & Overloaded Methods
            System.out.println(ConsoleColors.BOLD + "[Step 3] Polymorphism (Overloading) & Deposits" + ConsoleColors.RESET);
            bankService.deposit(sa.getAccountNumber(), 5000.0, "Salary Credited");
            ConsoleColors.printSuccess(String.format("Deposited ₹5,000 into %s. New Balance: ₹%.2f", sa.getAccountNumber(), sa.getBalance()));
            System.out.println();

            // Step 4: Transactable & Fund Transfer
            System.out.println(ConsoleColors.BOLD + "[Step 4] Fund Transfer & Ledger Auditing" + ConsoleColors.RESET);
            bankService.transfer(sa.getAccountNumber(), "1234", ca.getAccountNumber(), 7000.0, "Project Consultant Payment");
            ConsoleColors.printSuccess(String.format("Transferred ₹7,000 from %s to %s successfully!", sa.getAccountNumber(), ca.getAccountNumber()));
            ConsoleColors.printInfo(String.format("Savings Bal: ₹%.2f | Current Bal: ₹%.2f", sa.getBalance(), ca.getBalance()));
            System.out.println();

            // Step 5: Overdraft Feature on Current Account
            System.out.println(ConsoleColors.BOLD + "[Step 5] Current Account Overdraft Protection Facility" + ConsoleColors.RESET);
            double largeWithdrawal = 25000.0;
            ConsoleColors.printInfo(String.format("Current Account Balance before: ₹%.2f (Withdrawal requested: ₹%.2f)", ca.getBalance(), largeWithdrawal));
            bankService.withdraw(ca.getAccountNumber(), "5678", largeWithdrawal, "Commercial Equipment Purchase");
            ConsoleColors.printSuccess(String.format("Withdrawal authorized via overdraft! Balance is now: ₹%.2f", ca.getBalance()));
            System.out.println();

            // Step 6: Exception Handling Demonstration
            System.out.println(ConsoleColors.BOLD + "[Step 6] Custom Exception Handling Demonstration" + ConsoleColors.RESET);
            
            // 6a: Insufficient Funds Exception on Savings Account
            try {
                System.out.println("Attempting to withdraw ₹30,000 from Savings (Balance ₹18,000 with ₹1,000 min balance)...");
                bankService.withdraw(sa.getAccountNumber(), "1234", 30000.0, "Excessive Withdrawal");
            } catch (InsufficientFundsException e) {
                ConsoleColors.printWarning("Caught Expected InsufficientFundsException: " + e.getMessage());
            }

            // 6b: Authentication Exception (Wrong PIN)
            try {
                System.out.println("Attempting to withdraw with wrong PIN '0000'...");
                bankService.withdraw(sa.getAccountNumber(), "0000", 500.0, "Fraud Test");
            } catch (AuthenticationException e) {
                ConsoleColors.printWarning("Caught Expected AuthenticationException: " + e.getMessage());
            }

            // 6c: Overdraft Exceeded Exception on Current Account
            try {
                System.out.println("Attempting to exceed overdraft limit on Current Account...");
                bankService.withdraw(ca.getAccountNumber(), "5678", 50000.0, "Mega Withdrawal");
            } catch (OverdraftExceededException e) {
                ConsoleColors.printWarning("Caught Expected OverdraftExceededException: " + e.getMessage());
            }
            System.out.println();

            // Step 7: Interest Calculation & Payout
            System.out.println(ConsoleColors.BOLD + "[Step 7] Dynamic Polymorphism - Interest Calculation & Payout" + ConsoleColors.RESET);
            double totalInterest = bankService.applyMonthlyInterestToAllSavings();
            ConsoleColors.printSuccess(String.format("Applied monthly interest batch run! Total distributed: ₹%.2f", totalInterest));
            ConsoleColors.printInfo(String.format("Savings Account Balance after interest: ₹%.2f", sa.getBalance()));
            ConsoleColors.printInfo(String.format("FD Projected Maturity Amount: ₹%.2f (Interest: ₹%.2f)", 
                    fd.getEstimatedMaturityAmount(), fd.calculateInterest()));
            System.out.println();

            // Step 8: Loan EMI Calculation
            System.out.println(ConsoleColors.BOLD + "[Step 8] Extra Feature - Loan & EMI Calculator" + ConsoleColors.RESET);
            var loanRes = LoanService.calculateEMI(500000.0, 8.5, 36);
            ConsoleColors.printSuccess(String.format("₹5,00,000 Loan @ 8.5%% for 36 months => Monthly EMI: ₹%.2f (Total Interest: ₹%.2f)",
                    loanRes.monthlyEmi, loanRes.totalInterest));
            System.out.println();

            // Step 9: Report Generation & File Export
            System.out.println(ConsoleColors.BOLD + "[Step 9] Reportable Interface - Passbook Statement Generation & File Export" + ConsoleColors.RESET);
            System.out.println(sa.generateStatement());

            File exportDir = new File("exports");
            if (!exportDir.exists()) exportDir.mkdirs();
            String exportPath = "exports/statement_" + sa.getAccountNumber() + ".txt";
            sa.exportStatementToFile(exportPath);
            ConsoleColors.printSuccess("Exported statement to file: " + exportPath);
            System.out.println();

            // Step 10: Persistence Verification
            System.out.println(ConsoleColors.BOLD + "[Step 10] Persistence & State Integrity" + ConsoleColors.RESET);
            bankService.saveData();
            ConsoleColors.printSuccess("All customer profiles, accounts, and transaction logs persisted to storage successfully!");

            System.out.println(ConsoleColors.GREEN + ConsoleColors.BOLD + "\nAll 10 Verification Steps Completed Successfully!\n" + ConsoleColors.RESET);
        } catch (Exception e) {
            ConsoleColors.printError("Showcase error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
