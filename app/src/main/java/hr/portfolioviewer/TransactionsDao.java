package hr.portfolioviewer;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TransactionsDao {
    @Insert
    void insert(Transactions transactions);
    @Update
    void update(Transactions transactions);
    @Query("DELETE FROM Transactions")
    void deleteAllTransactions();

    @Query("SELECT * FROM Transactions")
    List<Transactions> getAllTransactions();
}