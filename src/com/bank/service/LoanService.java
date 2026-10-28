package com.bank.service;

/**
 * Service providing loan assessment, EMI calculations, and amortization schedules.
 * Added extra banking feature.
 */
public class LoanService {

    public static class LoanCalculationResult {
        public final double principal;
        public final double annualInterestRate;
        public final int tenureMonths;
        public final double monthlyEmi;
        public final double totalRepayment;
        public final double totalInterest;

        public LoanCalculationResult(double principal, double annualInterestRate, int tenureMonths, 
                                     double monthlyEmi, double totalRepayment, double totalInterest) {
            this.principal = principal;
            this.annualInterestRate = annualInterestRate;
            this.tenureMonths = tenureMonths;
            this.monthlyEmi = monthlyEmi;
            this.totalRepayment = totalRepayment;
            this.totalInterest = totalInterest;
        }
    }

    /**
     * Calculates Equated Monthly Installment (EMI).
     * Formula: EMI = [P x R x (1+R)^N] / [(1+R)^N - 1]
     */
    public static LoanCalculationResult calculateEMI(double principal, double annualRate, int tenureMonths) {
        if (principal <= 0 || annualRate <= 0 || tenureMonths <= 0) {
            throw new IllegalArgumentException("Loan parameters must be strictly positive.");
        }

        double monthlyRate = annualRate / (12.0 * 100.0);
        double factor = Math.pow(1 + monthlyRate, tenureMonths);
        double emi = (principal * monthlyRate * factor) / (factor - 1);
        double totalRepayment = emi * tenureMonths;
        double totalInterest = totalRepayment - principal;

        return new LoanCalculationResult(principal, annualRate, tenureMonths, emi, totalRepayment, totalInterest);
    }

    /**
     * Returns a formatted text amortization breakdown for the first few installments.
     */
    public static String getAmortizationPreview(double principal, double annualRate, int tenureMonths, int previewMonths) {
        LoanCalculationResult res = calculateEMI(principal, annualRate, tenureMonths);
        double monthlyRate = annualRate / (12.0 * 100.0);
        double balance = principal;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Loan Amount      : ₹%.2f\n", principal));
        sb.append(String.format("Annual Rate      : %.2f%%\n", annualRate));
        sb.append(String.format("Tenure           : %d Months (%.1f Years)\n", tenureMonths, tenureMonths / 12.0));
        sb.append(String.format("Monthly EMI      : ₹%.2f\n", res.monthlyEmi));
        sb.append(String.format("Total Interest   : ₹%.2f\n", res.totalInterest));
        sb.append(String.format("Total Repayment  : ₹%.2f\n", res.totalRepayment));
        sb.append("---------------------------------------------------------------------------------\n");
        sb.append(String.format("%-8s | %-14s | %-14s | %-14s | %-14s\n", "Month", "EMI (₹)", "Principal (₹)", "Interest (₹)", "Remaining (₹)"));
        sb.append("---------------------------------------------------------------------------------\n");

        int limit = Math.min(previewMonths, tenureMonths);
        for (int m = 1; m <= limit; m++) {
            double interestPortion = balance * monthlyRate;
            double principalPortion = res.monthlyEmi - interestPortion;
            balance -= principalPortion;
            if (balance < 0) balance = 0;

            sb.append(String.format("%-8d | %-14.2f | %-14.2f | %-14.2f | %-14.2f\n",
                    m, res.monthlyEmi, principalPortion, interestPortion, balance));
        }

        if (limit < tenureMonths) {
            sb.append(String.format("... and %d more monthly installments.\n", tenureMonths - limit));
        }
        sb.append("---------------------------------------------------------------------------------\n");
        return sb.toString();
    }
}
