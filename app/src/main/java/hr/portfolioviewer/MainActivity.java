package hr.portfolioviewer;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextView etfView;
    private TextView zabaView;
    private TextView cryptoView;
    private TextView portfolioView;
    private TextView etfPercentage;
    private TextView zabaPercentage;
    private TextView cryptoPercentage;
    private Button fetchButton;

    private EditText vwceAmount;
    private EditText fwraAmount;
    private EditText zabaAmount;
    private EditText bitcoinAmount;
    private EditText etheriumAmount;
    private Button saveButton;

    private BigDecimal vwceAmountValue;
    private BigDecimal fwraAmountValue;
    private BigDecimal zabaAmountValue;
    private BigDecimal bitcoinAmountValue;
    private BigDecimal etheriumAmountValue;

    private EditText vwceMoneyInvested;
    private EditText fwraMoneyInvested;
    private EditText zabaMoneyInvested;
    private EditText bitcoinMoneyInvested;
    private EditText etheriumMoneyInvested;

    private BigDecimal etfValue;
    private BigDecimal zabaValue;
    private BigDecimal cryptoValue;

    private DataFetcher fetcher;

    private BigDecimal portfolioValue;

    private AppDatabase db;

    private InvestmentDao investmentDao;

    private List<Investment> investments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        db = AppDatabase.getDatabase(getApplicationContext());
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                investmentDao = db.investmentDao();
                investments = investmentDao.getAllInvestments();
                if(investments.isEmpty()) {
                    investmentDao.insert(new Investment("VWCE", InvestmentType.ETF, new BigDecimal(0), new BigDecimal(0)));
                    investmentDao.insert(new Investment("FWRA", InvestmentType.ETF, new BigDecimal(0), new BigDecimal(0)));
                    investmentDao.insert(new Investment("ZABA", InvestmentType.STOCK, new BigDecimal(0), new BigDecimal(0)));
                    investmentDao.insert(new Investment("Bitcoin", InvestmentType.CRYPTO, new BigDecimal(0), new BigDecimal(0)));
                    investmentDao.insert(new Investment("Etherium", InvestmentType.CRYPTO, new BigDecimal(0), new BigDecimal(0)));
                }
                // Update UI on main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!investments.isEmpty()) {
                            for (Investment i : investments) {
                                if (i.getName().equals("VWCE")) {
                                    vwceAmountValue = i.getAmount();
                                    vwceAmount.setText(i.getAmount().toString());
                                    if (i.getMoneyInvested() != null)
                                        vwceMoneyInvested.setText(i.getMoneyInvested().toString());
                                } else if (i.getName().equals("FWRA")) {
                                    fwraAmountValue = i.getAmount();
                                    fwraAmount.setText(i.getAmount().toString());
                                    if (i.getMoneyInvested() != null)
                                        fwraMoneyInvested.setText(i.getMoneyInvested().toString());
                                } else if (i.getName().equals("ZABA")) {
                                    zabaAmountValue = i.getAmount();
                                    zabaAmount.setText(i.getAmount().toString());
                                    if (i.getMoneyInvested() != null)
                                        zabaMoneyInvested.setText(i.getMoneyInvested().toString());
                                } else if (i.getName().equals("Bitcoin")) {
                                    bitcoinAmountValue = i.getAmount();
                                    bitcoinAmount.setText(i.getAmount().toString());
                                    if (i.getMoneyInvested() != null)
                                        bitcoinMoneyInvested.setText(i.getMoneyInvested().toString());
                                } else if (i.getName().equals("Etherium")) {
                                    etheriumAmountValue = i.getAmount();
                                    etheriumAmount.setText(i.getAmount().toString());
                                    if (i.getMoneyInvested() != null)
                                        etheriumMoneyInvested.setText(i.getMoneyInvested().toString());
                                }
                            }
                            fetchData();
                        }
                    }
                });
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.etfView = findViewById(R.id.etfValue);
        this.zabaView = findViewById(R.id.zabaValue);
        this.cryptoView = findViewById(R.id.cryptoValue);
        this.etfPercentage = findViewById(R.id.etfPercentage);
        this.zabaPercentage = findViewById(R.id.zabaPercentage);
        this.cryptoPercentage = findViewById(R.id.cryptoPercentage);
        this.portfolioView = findViewById(R.id.portfolioValue);
        this.fetchButton = findViewById(R.id.fetchButton);
        this.vwceAmount = findViewById(R.id.vwceInput);
        this.fwraAmount = findViewById(R.id.fwraInput);
        this.zabaAmount = findViewById(R.id.zabaInput);
        this.bitcoinAmount = findViewById(R.id.bitcoinInput);
        this.etheriumAmount = findViewById(R.id.etheriumInput);
        this.vwceMoneyInvested = findViewById(R.id.vwceMoneyInvested);
        this.fwraMoneyInvested = findViewById(R.id.fwraMoneyInvested);
        this.zabaMoneyInvested = findViewById(R.id.zabaMoneyInvested);
        this.bitcoinMoneyInvested = findViewById(R.id.bitcoinMoneyInvested);
        this.etheriumMoneyInvested = findViewById(R.id.etheriumMoneyInvested);
        this.saveButton = findViewById(R.id.saveButton);

        fetcher = new DataFetcher();
        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchData();
            }
        });
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
                fetchData();
            }
        });
    }

    private void fetchData() {
        portfolioValue = BigDecimal.ZERO;
        fetcher.getEtf(vwceAmountValue, fwraAmountValue, new DataFetcher.Callback() {
            @Override
            public void onResult(BigDecimal price) {
                // Update UI
                etfValue = price;
                portfolioValue = portfolioValue.add(price);
                etfView.setText("ETF total: " + price.toPlainString());
                updatePortfolioValue();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                etfValue = null;
                etfView.setText("ETF total: ??");
                Toast.makeText(MainActivity.this, "Error fetching ETF", Toast.LENGTH_SHORT).show();
            }
        });

        fetcher.getZaba(zabaAmountValue, new DataFetcher.Callback() {
            @Override
            public void onResult(BigDecimal price) {
                // Update UI
                zabaValue = price;
                portfolioValue = portfolioValue.add(price);
                zabaView.setText("ZABA total: " + price.toPlainString());
                updatePortfolioValue();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                zabaValue = null;
                zabaView.setText("ZABA total: ??");
                Toast.makeText(MainActivity.this, "Error fetching ZABA", Toast.LENGTH_SHORT).show();
            }
        });

        fetcher.getCrypto(bitcoinAmountValue, etheriumAmountValue, new DataFetcher.Callback() {
            @Override
            public void onResult(BigDecimal price) {
                // Update UI
                cryptoValue = price;
                portfolioValue = portfolioValue.add(price);
                cryptoView.setText("Crypto total: " + price.toPlainString());
                updatePortfolioValue();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                cryptoValue = null;
                cryptoView.setText("Crypto total: ??");
                Toast.makeText(MainActivity.this, "Error fetching Crypto", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveData() {
        for (Investment i : investments) {
            boolean changes = false;

            if (i.getName().equals("VWCE") && (!i.getAmount().toString().contentEquals(vwceAmount.getText()) ||
                    (!i.getMoneyInvested().toString().contentEquals(vwceMoneyInvested.getText())))) {
                i.setAmount(new BigDecimal(vwceAmount.getText().toString()));
                vwceAmountValue = new BigDecimal(vwceAmount.getText().toString());
                if(!vwceMoneyInvested.getText().toString().isEmpty())
                    i.setMoneyInvested(new BigDecimal(vwceMoneyInvested.getText().toString()));
                changes = true;
            } else if (i.getName().equals("FWRA") && (!i.getAmount().toString().contentEquals(fwraAmount.getText()) || (!i.getMoneyInvested().toString().contentEquals(fwraMoneyInvested.getText())))) {
                i.setAmount(new BigDecimal(fwraAmount.getText().toString()));
                fwraAmountValue = new BigDecimal(fwraAmount.getText().toString());
                if(!fwraMoneyInvested.getText().toString().isEmpty())
                    i.setMoneyInvested(new BigDecimal(fwraMoneyInvested.getText().toString()));
                changes = true;
            } else if (i.getName().equals("ZABA") && (!i.getAmount().toString().contentEquals(zabaAmount.getText()) || (!i.getMoneyInvested().toString().contentEquals(zabaMoneyInvested.getText())))) {
                i.setAmount(new BigDecimal(zabaAmount.getText().toString()));
                zabaAmountValue = new BigDecimal(zabaAmount.getText().toString());
                if(!zabaMoneyInvested.getText().toString().isEmpty())
                    i.setMoneyInvested(new BigDecimal(zabaMoneyInvested.getText().toString()));
                changes = true;
            } else if (i.getName().equals("Bitcoin") && (!i.getAmount().toString().contentEquals(bitcoinAmount.getText()) || (!i.getMoneyInvested().toString().contentEquals(bitcoinMoneyInvested.getText())))) {
                i.setAmount(new BigDecimal(bitcoinAmount.getText().toString()));
                bitcoinAmountValue = new BigDecimal(bitcoinAmount.getText().toString());
                if(!bitcoinMoneyInvested.getText().toString().isEmpty())
                    i.setMoneyInvested(new BigDecimal(bitcoinMoneyInvested.getText().toString()));
                changes = true;
            } else if (i.getName().equals("Etherium") && (!i.getAmount().toString().contentEquals(etheriumAmount.getText()) || (!i.getMoneyInvested().toString().contentEquals(etheriumMoneyInvested.getText())))) {
                i.setAmount(new BigDecimal(etheriumAmount.getText().toString()));
                etheriumAmountValue = new BigDecimal(etheriumAmount.getText().toString());
                if(!etheriumMoneyInvested.getText().toString().isEmpty())
                    i.setMoneyInvested(new BigDecimal(etheriumMoneyInvested.getText().toString()));
                changes = true;
            }

            if (changes) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        investmentDao.update(i);
                    }
                });
            }
        }
    }

    private void updatePortfolioValue() {
        portfolioView.setText("Portfolio value: " + portfolioValue.toPlainString());
        if (etfValue != null) {
            String etfPercentageValue = etfValue.divide(portfolioValue, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toString() + "%";
            etfPercentage.setText(etfPercentageValue);
        }
        if (zabaValue != null) {
            String zabaPercentageValue = zabaValue.divide(portfolioValue, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toString() + "%";
            zabaPercentage.setText(zabaPercentageValue);
        }
        if (cryptoValue != null) {
            String cryptoPercentageValue = cryptoValue.divide(portfolioValue, 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100)).setScale(2, RoundingMode.HALF_UP).toString() + "%";
            cryptoPercentage.setText(cryptoPercentageValue);
        }
    }
}