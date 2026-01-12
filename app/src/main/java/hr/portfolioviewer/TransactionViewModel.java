package hr.portfolioviewer;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.room.Transaction;

public class TransactionViewModel extends AndroidViewModel {

    private final TransactionRepository repository;

    public TransactionViewModel(@NonNull Application application) {
        super(application);

        AppDatabase db = AppDatabase.getDatabase(application);
        repository = new TransactionRepository(db);
    }

    public void insertTransactionWithInvestment(
            Transactions transaction
    ) {
        repository.insertTransactionAndInvestment(transaction);
    }
}
