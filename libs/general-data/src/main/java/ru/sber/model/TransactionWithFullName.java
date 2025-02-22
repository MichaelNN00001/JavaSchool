package ru.sber.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionWithFullName {

    private String id;
    private TransactionType type;
    private BigDecimal amount;
    private String account;
    private String fullName;
    private LocalDateTime date;

    @Override
    public String toString() {
        return "Transaction{" +
                "id = " + id +
                ", type = " + type +
                ", amount = " + amount +
                ", account = " + account +
                ", fullName = " + fullName +
                ", date = " + date +
                '}';
    }

    public TransactionWithFullName() {
    }

    public TransactionWithFullName(String id, TransactionType type, BigDecimal amount, String account, String fullName, LocalDateTime date) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.account = account;
        this.fullName = fullName;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}

