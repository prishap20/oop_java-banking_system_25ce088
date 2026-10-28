package com.bank.ui;

/**
 * ANSI Color and formatting codes for enhanced terminal UX.
 */
public class ConsoleColors {
    public static final String RESET = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static final String BOLD = "\u001B[1m";
    public static final String UNDERLINE = "\u001B[4m";

    public static void printHeader(String title) {
        System.out.println(CYAN + BOLD + "\n======================================================================" + RESET);
        System.out.println(CYAN + BOLD + "   " + title.toUpperCase() + RESET);
        System.out.println(CYAN + BOLD + "======================================================================" + RESET);
    }

    public static void printSuccess(String message) {
        System.out.println(GREEN + "✔ " + message + RESET);
    }

    public static void printError(String message) {
        System.out.println(RED + "✖ Error: " + message + RESET);
    }

    public static void printWarning(String message) {
        System.out.println(YELLOW + "⚠ " + message + RESET);
    }

    public static void printInfo(String message) {
        System.out.println(BLUE + "ℹ " + message + RESET);
    }
}
