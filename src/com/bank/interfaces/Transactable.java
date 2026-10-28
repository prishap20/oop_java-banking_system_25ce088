package com.bank.interfaces;

import com.bank.exception.InsufficientFundsException;
import com.bank.exception.InvalidAmountException;
import com.bank.exception.OverdraftExceededException;
import com.bank.model.Account;

/**
 * Interface contract defining standard financial transactions.
 * Demonstrates Abstraction in OOP.
 */
public interface Transactable {
    /**
     * Deposit funds into account.
     * @param amount Deposit amount
     * @param remarks Audit notes/remarks
     */
    void deposit(double amount, String remarks) throws InvalidAmountException;

    /**
     * Withdraw funds from account.
     * @param amount Withdrawal amount
     * @param remarks Audit notes/remarks
     */
    void withdraw(double amount, String remarks) 
            throws InsufficientFundsException, InvalidAmountException, OverdraftExceededException;

    /**
     * Transfer funds to destination account.
     * @param toAccount Destination Account
     * @param amount Transfer amount
     * @param remarks Audit notes/remarks
     */
    void transfer(Account toAccount, double amount, String remarks) 
            throws InsufficientFundsException, InvalidAmountException, OverdraftExceededException;
}
