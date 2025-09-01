package hr.portfolioviewer;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface InvestmentDao {
    @Insert
    void insert(Investment investment);
    @Update
    void update(Investment investment);
    @Query("DELETE FROM Investment")
    void deleteAllInvestments();

    @Query("SELECT * FROM Investment")
    List<Investment> getAllInvestments();
}