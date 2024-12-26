package sbp.school.kafka.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private TransactionType type;
    private BigDecimal amount;
    private String account;
    private LocalDateTime date;

    @Override
    public String toString() {
        return "Transaction{" +
                "type=" + type +
                ", amount=" + amount +
                ", account='" + account + '\'' +
                ", date=" + date +
                '}';
    }

    public Transaction() {
    }

    public Transaction(TransactionType type, BigDecimal amount, String account, LocalDateTime date) {
        this.type = type;
        this.amount = amount;
        this.account = account;
        this.date = date;
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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
