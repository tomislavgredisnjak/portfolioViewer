package hr.portfolioviewer;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Transactions {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private Investment investment;
    private TransactionType transactionType;
    private LocalDateTime transactionTime;
    private BigDecimal amount;
    private BigDecimal price;
    private BigDecimal collected;

    public Transactions(TransactionType transactionType, Investment investment, LocalDateTime transactionTime, BigDecimal amount, BigDecimal price, BigDecimal collected) {
        this.transactionType = transactionType;
        this.investment = investment;
        this.transactionTime = transactionTime;
        this.amount = amount;
        this.price = price;
        this.collected = collected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Investment getInvestment() {
        return investment;
    }

    public void setInvestment(Investment investment) {
        this.investment = investment;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }
    public BigDecimal getAmount() {
        return amount;
    }
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    public BigDecimal getCollected() {
        return collected;
    }
    public void setCollected(BigDecimal collected) {
        this.collected = collected;
    }
}
