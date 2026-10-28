package com.bank;

import com.bank.service.BankService;
import com.bank.ui.BankCLI;
import com.bank.util.DemoRunner;

import java.io.File;

/**
 * Entry point for the OOP Java Banking System.
 * Student ID: 25CE088
 */
public class Main {
    private static final String DATA_FILE = "data/bank_data.dat";

    public static void main(String[] args) {
        // Ensure data directory exists
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }

        // Initialize core banking service
        BankService bankService = new BankService(DATA_FILE);

        // Check if automated demo mode requested via command line
        if (args.length > 0 && (args[0].equalsIgnoreCase("--demo") || args[0].equalsIgnoreCase("-d"))) {
            DemoRunner.runDemo(bankService);
            return;
        }

        // Start interactive Terminal CLI
        BankCLI cli = new BankCLI(bankService);
        cli.start();
    }
}
