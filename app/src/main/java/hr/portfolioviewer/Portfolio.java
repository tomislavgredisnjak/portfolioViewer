package hr.portfolioviewer;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.List;
@Entity
public class Portfolio {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private List<Investment> investments;
    private Date startOfInvestingDate;

    public Portfolio(List<Investment> investments, Date startOfInvestingDate) {
        this.investments = investments;
        this.startOfInvestingDate = startOfInvestingDate;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public List<Investment> getInvestments() {
        return investments;
    }
    public void setInvestments(List<Investment> investments) {
        this.investments = investments;
    }

    public Date getStartOfInvestingDate() {
        return startOfInvestingDate;
    }
    public void setStartOfInvestingDate(Date startOfInvestingDate) {
        this.startOfInvestingDate = startOfInvestingDate;
    }
}
