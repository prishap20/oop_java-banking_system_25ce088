package com.bank.interfaces;

import java.io.IOException;

/**
 * Interface contract for generating reports, statements, and summaries.
 * Demonstrates Abstraction in OOP.
 */
public interface Reportable {
    /**
     * Generates a detailed statement of the account and its transaction ledger.
     * @return Formatted multi-line statement
     */
    String generateStatement();

    /**
     * Returns a concise one-line summary of the account.
     * @return Concise account summary
     */
    String getAccountSummary();

    /**
     * Exports the account statement to an external file on disk.
     * @param targetFilePath Path of destination file
     * @throws IOException If file export encounters I/O failure
     */
    void exportStatementToFile(String targetFilePath) throws IOException;
}
