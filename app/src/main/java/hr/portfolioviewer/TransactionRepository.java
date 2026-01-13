package hr.portfolioviewer;

import java.util.concurrent.Executors;

public class TransactionRepository {

    private final AppDatabase db;
    private final TransactionsDao transactionsDao;
    private final InvestmentDao investmentDao;

    public TransactionRepository(AppDatabase db) {
        this.db = db;
        this.transactionsDao = db.transactionsDao();
        this.investmentDao = db.investmentDao();
    }

    public void insertTransactionAndInvestment(
            Transactions transactions
    ) {
        Executors.newSingleThreadExecutor().execute(() -> {
            db.runInTransaction(() -> {
                transactionsDao.insert(transactions);
                Investment investment = investmentDao.getInvestmentById(transactions.getInvestment().getId());
                switch (transactions.getTransactionType()) {
                    case BUY:
                        investment.setAmount(investment.getAmount().add(transactions.getAmount()));
                        investment.setMoneyInvested(investment.getMoneyInvested().add(transactions.getPrice()));
                        break;
                    case SELL:
                        investment.setAmount(investment.getAmount().subtract(transactions.getAmount()));
                        investment.setMoneyCollected(investment.getMoneyCollected().add(transactions.getPrice()));
                        break;
                    case DIVIDEND:
                        investment.setMoneyCollected(investment.getMoneyCollected().add(transactions.getPrice()));
                        break;
                    case CHANGE:
                        investment.setAmount(investment.getAmount().add(transactions.getAmount()));
                        investment.setMoneyInvested(investment.getMoneyInvested().add(transactions.getPrice()));
                        investment.setMoneyCollected(investment.getMoneyCollected().add(transactions.getCollected()));
                        break;
                }

                investmentDao.update(investment);
            });
        });
    }
}
