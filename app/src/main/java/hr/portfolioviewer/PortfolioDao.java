package hr.portfolioviewer;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PortfolioDao {
    @Insert
    void insert(Portfolio portfolio);
    @Update
    void update(Portfolio portfolio);
    @Query("DELETE FROM Portfolio")
    void deleteAllPortfolios();

    @Query("SELECT * FROM Portfolio")
    List<Portfolio> getAllPortfolios();
}
