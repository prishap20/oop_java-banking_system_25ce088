# 🏦 Object-Oriented Java Banking System

[![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)
[![Student ID](https://img.shields.io/badge/Student%20ID-25CE088-green?style=for-the-badge)](#)

A comprehensive, production-grade Banking System implemented in **Core Java** demonstrating fundamental **Object-Oriented Programming (OOP)** principles, design patterns, custom exception hierarchies, data persistence, and banking features such as Loan EMI calculation, overdraft management, passbook file export, and multi-tier account handling.

---

## 🌐 GitHub Repository & Quick Clone

Anyone can clone and run this repository directly from the terminal:

```bash
git clone https://github.com/prishap20/oop_java-banking_system_25ce088.git
cd oop_java-banking_system_25ce088
```

---

## 🚀 Quick Start Guide

### Prerequisites
- **JDK 11 or higher** (JDK 17 or JDK 21 recommended)
- macOS / Linux / Windows with Bash or Command Prompt

### Option A: Using the Convenience Shell Scripts (macOS / Linux)

1. **Run the Automated OOP Feature Showcase / Verification Suite**:
   ```bash
   ./run.sh --demo
   ```

2. **Launch the Interactive Console Banking Application**:
   ```bash
   ./run.sh
   ```

### Option B: Using Standard Java Commands (Any OS / IDE)

1. **Compile**:
   ```bash
   javac -d bin $(find src -name "*.java")
   ```
   *(On Windows Command Prompt: `javac -d bin src\com\bank\*.java src\com\bank\model\*.java src\com\bank\service\*.java src\com\bank\ui\*.java src\com\bank\util\*.java src\com\bank\exception\*.java src\com\bank\interfaces\*.java`)*

2. **Run Interactive CLI**:
   ```bash
   java -cp bin com.bank.Main
   ```

3. **Run Automated Demo Mode**:
   ```bash
   java -cp bin com.bank.Main --demo
   ```

---

## 🎯 OOP Principles Implemented

| OOP Principle | Implementation in Project | Key Classes / Files |
| :--- | :--- | :--- |
| **Abstraction** | Abstract classes and interface contracts defining behavior without exposing internal mechanics. | [`Account`](src/com/bank/model/Account.java), [`Transactable`](src/com/bank/interfaces/Transactable.java), [`Reportable`](src/com/bank/interfaces/Reportable.java) |
| **Encapsulation** | Strict `private`/`protected` fields, defensive copying, input validation (phone regex, email format, non-negative amounts), PIN protection, and account lockout mechanics. | [`Customer`](src/com/bank/model/Customer.java), [`Account`](src/com/bank/model/Account.java), [`Transaction`](src/com/bank/model/Transaction.java) |
| **Inheritance** | Base banking model extended into specialized account products inheriting common fields and lifecycle hooks. | [`SavingsAccount`](src/com/bank/model/SavingsAccount.java), [`CurrentAccount`](src/com/bank/model/CurrentAccount.java), [`FixedDepositAccount`](src/com/bank/model/FixedDepositAccount.java) |
| **Polymorphism (Runtime)** | Dynamic method overriding for withdrawals, balance calculations, statement generation, and interest payouts based on concrete account type. | `withdraw()`, `calculateInterest()`, `getAccountSummary()` across derived accounts |
| **Polymorphism (Compile-time)** | Method overloading for flexible transaction invocation with or without audit remarks. | `deposit(double)`, `deposit(double, String)`, `withdraw(double)`, `withdraw(double, String)` |
| **Exception Handling** | Custom checked exception hierarchy preventing illegal operations, insufficient balances, security violations, and overdraft breaches. | [`BankingException`](src/com/bank/exception/BankingException.java), [`InsufficientFundsException`](src/com/bank/exception/InsufficientFundsException.java), [`OverdraftExceededException`](src/com/bank/exception/OverdraftExceededException.java), [`AuthenticationException`](src/com/bank/exception/AuthenticationException.java) |

---

## ✨ Features & Capabilities

### 1. 💼 Multi-Account Product Hierarchy
- **Savings Account**: Enforces minimum balance requirements (₹1,000) and calculates annual compound/simple interest (4.0% p.a.).
- **Current Account (Checking)**: Features overdraft protection facility (up to ₹25,000+), allowing businesses to draw beyond zero balance with automatic fee calculation.
- **Fixed Deposit (FD)**: Term-based deposit with quarterly compound interest (`A = P(1 + r/n)^(nt)`), maturity tracking, and premature liquidation penalty logic.

### 2. 🔐 Security & PIN Authentication
- 4-Digit numeric PIN required for withdrawals, transfers, and balance management.
- Failed attempt counter with **automatic lockout after 3 consecutive invalid attempts** to prevent brute-force attacks.
- Admin unlock mechanism available in the dashboard.

### 3. 💸 Inter-Account Fund Transfers
- Seamless atomic money transfer between accounts.
- Dual-entry ledger logging: records debit on the sender's account and credit on the receiver's account with cross-referenced transaction IDs.

### 4. 🧮 Loan & EMI Calculator (Extra Feature)
- Accurate Equated Monthly Installment (EMI) calculation based on financial formula:
  $$\text{EMI} = \frac{P \times r \times (1+r)^n}{(1+r)^n - 1}$$
- Detailed amortization schedule preview detailing principal vs. interest components.

### 5. 📄 Passbook & Statement Export (Extra Feature)
- Real-time ASCII passbook table showing transaction IDs, date/time timestamps, transaction type symbols (`+`/`-`), amounts, and running balances.
- Export functionality that saves formatted statements to disk (`exports/statement_<accountNumber>.txt`).

### 6. 💾 Persistent Data Storage (Extra Feature)
- Auto-saves all customer records, accounts, and transaction history to file (`data/bank_data.dat`).
- Automatically restores state on application startup so records survive between runs.

---

## 📂 Project Architecture & Directory Structure

```
oop_java-banking_system_25ce088/
├── .gitignore                      # Git ignore configuration
├── README.md                       # Documentation & Project Guide
├── build.sh                        # Build automation script
├── run.sh                          # Execution automation script
├── data/
│   └── bank_data.dat               # Persisted binary database
├── exports/                        # Directory for exported text statements
└── src/
    └── com/
        └── bank/
            ├── Main.java           # Entry point (CLI & Demo mode dispatcher)
            ├── exception/          # Custom Banking Exceptions
            │   ├── BankingException.java
            │   ├── InsufficientFundsException.java
            │   ├── InvalidAmountException.java
            │   ├── AccountNotFoundException.java
            │   ├── AuthenticationException.java
            │   └── OverdraftExceededException.java
            ├── interfaces/         # OOP Abstraction Contracts
            │   ├── Transactable.java
            │   └── Reportable.java
            ├── model/              # Domain Models & Account Hierarchy
            │   ├── Account.java            (Abstract Base Class)
            │   ├── SavingsAccount.java     (Extends Account)
            │   ├── CurrentAccount.java     (Extends Account)
            │   ├── FixedDepositAccount.java(Extends Account)
            │   ├── Customer.java           (Encapsulated Customer profile)
            │   ├── Transaction.java        (Immutable Ledger entry)
            │   └── TransactionType.java    (Enum of operations)
            ├── service/            # Business Logic & Infrastructure
            │   ├── BankService.java        (Core facade & coordinator)
            │   ├── LoanService.java        (EMI & amortization calculations)
            │   └── StorageService.java     (Data persistence & serialization)
            ├── ui/                 # User Interface Layer
            │   ├── BankCLI.java            (Interactive terminal menu)
            │   └── ConsoleColors.java      (ANSI terminal formatting)
            └── util/               # Showcase & Testing Utilities
                └── DemoRunner.java         (Automated OOP verification suite)
```

---

## 🖥 Sample Output (Passbook Statement)

```text
========================================================================================
                     ACCOUNT STATEMENT - SAVINGS ACCOUNT
========================================================================================
Account Number : SB25005
Customer ID    : CUST1004
Account Type   : Savings Account
Created On     : 2026-10-28 13:12:13
Current Balance: ₹18060.00
Status         : ACTIVE
----------------------------------------------------------------------------------------
Date & Time          | Txn ID       | Type / Description       | Amount       | Balance After
----------------------------------------------------------------------------------------
2026-10-28 13:12:13  | TXNDB365A61  | Cash/Cheque Deposit      | +₹20000.00   | ₹20000.00  
2026-10-28 13:12:13  | TXN8DB223A9  | Cash/Cheque Deposit      | +₹5000.00    | ₹25000.00  
2026-10-28 13:12:13  | TXNDF10AF82  | Cash Withdrawal          | -₹7000.00    | ₹18000.00  
2026-10-28 13:12:13  | TXN7EDB6B0D  | Interest Payout          | +₹60.00      | ₹18060.00  
========================================================================================
```

---

## 👤 Author

- **Student ID / Roll No:** 25CE088
- **GitHub:** [@prishap20](https://github.com/prishap20)
- **Course:** Object-Oriented Programming with Java
