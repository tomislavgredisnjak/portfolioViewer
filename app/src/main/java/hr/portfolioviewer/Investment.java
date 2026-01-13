package hr.portfolioviewer;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.math.BigDecimal;

@Entity
public class Investment {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private InvestmentType type;
    private BigDecimal amount;
    private BigDecimal moneyInvested;
    private BigDecimal moneyCollected;


    public Investment(String name, InvestmentType type, BigDecimal amount, BigDecimal moneyInvested, BigDecimal moneyCollected) {
        this.name = name;
        this.type = type;
        this.amount = amount;
        this.moneyInvested = moneyInvested;
        this.moneyCollected = moneyCollected;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public InvestmentType getType() {
        return type;
    }

    public void setType(InvestmentType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getMoneyInvested() {
        return moneyInvested;
    }

    public void setMoneyInvested(BigDecimal moneyInvested) {
        this.moneyInvested = moneyInvested;
    }
    public BigDecimal getMoneyCollected() {
        return moneyCollected;
    }
    public void setMoneyCollected(BigDecimal moneyCollected) {
        this.moneyCollected = moneyCollected;
    }
    @Override
    public String toString() {
        return name;
    }
}
