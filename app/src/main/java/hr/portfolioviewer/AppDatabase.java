package hr.portfolioviewer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Investment.class, Portfolio.class, Transactions.class}, version = 6)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract InvestmentDao investmentDao();
    public abstract PortfolioDao portfolioDao();
    public abstract TransactionsDao transactionsDao();

    private static volatile AppDatabase INSTANCE;

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Example: add a new column
            database.execSQL("ALTER TABLE Investment ADD COLUMN name TEXT DEFAULT 'undefined'");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Example: add a new column
            database.execSQL("ALTER TABLE Investment ADD COLUMN moneyInvested TEXT DEFAULT '0'");
        }
    };

    static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS Portfolio (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, investments TEXT, startOfInvestingDate INTEGER)");
        }
    };

    static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Example: add a new column
            database.execSQL("ALTER TABLE Investment ADD COLUMN moneyCollected TEXT DEFAULT '0'");
        }
    };

    static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // Example: add a new column
            database.execSQL("CREATE TABLE IF NOT EXISTS Transactions (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, investment TEXT, transactionType TEXT, transactionTime INTEGER, amount TEXT, price TEXT)");
        }
    };

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "PortfolioDb")
                            .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5, MIGRATION_5_6)
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
