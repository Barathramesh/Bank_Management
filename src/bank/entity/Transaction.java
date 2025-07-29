package bank.entity;

import java.time.LocalDateTime;

public class Transaction {

    private LocalDateTime transactionDate;
    private String transactionUsername;
    private Double transactionAmount;
    private String transactionType;
    private Double initialBalance;
    private Double finalBalance;
    private String transactionPerformedBy;

    public Transaction(LocalDateTime transactionDate, String transactionUsername, Double transactionAmount, String transactionType, Double initialBalance, Double finalBalance, String transactionPerformedBy) {
        this.transactionDate = transactionDate;
        this.transactionUsername = transactionUsername;
        this.transactionAmount = transactionAmount;
        this.transactionType = transactionType;
        this.initialBalance = initialBalance;
        this.finalBalance = finalBalance;
        this.transactionPerformedBy = transactionPerformedBy;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionUsername() {
        return transactionUsername;
    }

    public void setTransactionUsername(String transactionUsername) {
        this.transactionUsername = transactionUsername;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(Double initialBalance) {
        this.initialBalance = initialBalance;
    }

    public Double getFinalBalance() {
        return finalBalance;
    }

    public void setFinalBalance(Double finalBalance) {
        this.finalBalance = finalBalance;
    }

    public String getTransactionPerformedBy() {
        return transactionPerformedBy;
    }

    public void setTransactionPerformedBy(String transactionPerformedBy) {
        this.transactionPerformedBy = transactionPerformedBy;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionDate=" + transactionDate +
                ", transactionUsername='" + transactionUsername + '\'' +
                ", transactionAmount=" + transactionAmount +
                ", transactionType='" + transactionType + '\'' +
                ", initialBalance=" + initialBalance +
                ", finalBalance=" + finalBalance +
                ", transactionPerformedBy='" + transactionPerformedBy + '\'' +
                '}';
    }
}
