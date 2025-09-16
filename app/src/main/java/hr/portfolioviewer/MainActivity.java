package hr.portfolioviewer;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private TextView etfView;
    private TextView zabaView;
    private TextView cryptoView;
    private TextView portfolioView;
    private TextView etfProfitView;
    private TextView zabaProfitView;
    private TextView cryptoProfitView;
    private TextView portfolioProfitView;
    private TextView profitPerMonthView;
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

    private EditText vwceMoneyCollected;
    private EditText fwraMoneyCollected;
    private EditText zabaMoneyCollected;
    private EditText bitcoinMoneyCollected;
    private EditText etheriumMoneyCollected;

    private BigDecimal etfValue;
    private BigDecimal zabaValue;
    private BigDecimal cryptoValue;

    private DataFetcher fetcher;
    private EditText startOfInvestingDate;

    private BigDecimal portfolioValue;

    private AppDatabase db;

    private InvestmentDao investmentDao;
    private PortfolioDao portfolioDao;

    private List<Investment> investments;
    private List<Portfolio> portfolios;
    private Portfolio currentPortfolio;

    private BigDecimal etfProfit;
    private BigDecimal zabaProfit;
    private BigDecimal cryptoProfit;
    private Date startOfInvestingDateValue;
    private BigDecimal profit;
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

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
                portfolioDao = db.portfolioDao();
                investmentDao = db.investmentDao();
                investments = investmentDao.getAllInvestments();
                portfolios = portfolioDao.getAllPortfolios();
                if(investments.isEmpty()) {
                    investmentDao.insert(new Investment("VWCE", InvestmentType.ETF, new BigDecimal(0), new BigDecimal(0), new BigDecimal(0)));
                    investmentDao.insert(new Investment("FWRA", InvestmentType.ETF, new BigDecimal(0), new BigDecimal(0), new BigDecimal(0)));
                    investmentDao.insert(new Investment("ZABA", InvestmentType.STOCK, new BigDecimal(0), new BigDecimal(0), new BigDecimal(0)));
                    investmentDao.insert(new Investment("Bitcoin", InvestmentType.CRYPTO, new BigDecimal(0), new BigDecimal(0), new BigDecimal(0)));
                    investmentDao.insert(new Investment("Etherium", InvestmentType.CRYPTO, new BigDecimal(0), new BigDecimal(0), new BigDecimal(0)));
                    investments = investmentDao.getAllInvestments();
                }
                if(portfolios.isEmpty()) {
                    startOfInvestingDateValue = new Date();
                    currentPortfolio = new Portfolio(investments, startOfInvestingDateValue);
                    portfolioDao.insert(currentPortfolio);
                } else {
                    currentPortfolio = portfolios.get(0);
                    startOfInvestingDateValue = currentPortfolio.getStartOfInvestingDate();
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
                                    if (i.getMoneyCollected() != null)
                                        vwceMoneyCollected.setText(i.getMoneyCollected().toString());
                                } else if (i.getName().equals("FWRA")) {
                                    fwraAmountValue = i.getAmount();
                                    fwraAmount.setText(i.getAmount().toString());
                                    if (i.getMoneyInvested() != null)
                                        fwraMoneyInvested.setText(i.getMoneyInvested().toString());
                                    if (i.getMoneyCollected() != null)
                                        fwraMoneyCollected.setText(i.getMoneyCollected().toString());
                                } else if (i.getName().equals("ZABA")) {
                                    zabaAmountValue = i.getAmount();
                                    zabaAmount.setText(i.getAmount().toString());
                                    if (i.getMoneyInvested() != null)
                                        zabaMoneyInvested.setText(i.getMoneyInvested().toString());
                                    if (i.getMoneyCollected() != null)
                                        zabaMoneyCollected.setText(i.getMoneyCollected().toString());
                                } else if (i.getName().equals("Bitcoin")) {
                                    bitcoinAmountValue = i.getAmount();
                                    bitcoinAmount.setText(i.getAmount().toString());
                                    if (i.getMoneyInvested() != null)
                                        bitcoinMoneyInvested.setText(i.getMoneyInvested().toString());
                                    if (i.getMoneyCollected() != null)
                                        bitcoinMoneyCollected.setText(i.getMoneyCollected().toString());
                                } else if (i.getName().equals("Etherium")) {
                                    etheriumAmountValue = i.getAmount();
                                    etheriumAmount.setText(i.getAmount().toString());
                                    if (i.getMoneyInvested() != null)
                                        etheriumMoneyInvested.setText(i.getMoneyInvested().toString());
                                    if (i.getMoneyCollected() != null)
                                        etheriumMoneyCollected.setText(i.getMoneyCollected().toString());
                                }
                            }
                        }
                        startOfInvestingDate.setText(sdf.format(currentPortfolio.getStartOfInvestingDate()));
                        fetchData();
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
        this.etfProfitView = findViewById(R.id.etfProfit);
        this.zabaProfitView = findViewById(R.id.zabaProfit);
        this.cryptoProfitView = findViewById(R.id.cryptoProfit);
        this.portfolioProfitView = findViewById(R.id.portfolioProfit);
        this.profitPerMonthView = findViewById(R.id.profitPerMonth);
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
        this.vwceMoneyCollected = findViewById(R.id.vwceMoneyCollected);
        this.fwraMoneyCollected = findViewById(R.id.fwraMoneyCollected);
        this.zabaMoneyCollected = findViewById(R.id.zabaMoneyCollected);
        this.bitcoinMoneyCollected = findViewById(R.id.bitcoinMoneyCollected);
        this.etheriumMoneyCollected = findViewById(R.id.etheriumMoneyCollected);
        this.saveButton = findViewById(R.id.saveButton);
        this.startOfInvestingDate = findViewById(R.id.startOfInvestingDate);

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

        startOfInvestingDate.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        String date = dayOfMonth + "." + (month + 1) + "." + year;
                        startOfInvestingDate.setText(date);
                        calendar.set(year, month, dayOfMonth);
                        startOfInvestingDateValue.setTime(calendar.getTimeInMillis());
                        currentPortfolio.setStartOfInvestingDate(startOfInvestingDateValue);
                        Executors.newSingleThreadExecutor().execute(() -> {
                            portfolioDao.update(currentPortfolio);
                            runOnUiThread(this::calculateMonthlyProfit);
                        });
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.show();
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
                etfProfit = price.subtract(Converters.fromString(String.valueOf(vwceMoneyInvested.getText())).add(Converters.fromString(String.valueOf(fwraMoneyInvested.getText()))))
                        .add(Converters.fromString(String.valueOf(fwraMoneyCollected.getText()))).add(Converters.fromString(String.valueOf(vwceMoneyCollected.getText())));
                etfView.setText(price.toPlainString() + " €");
                etfProfitView.setText(etfProfit.compareTo(BigDecimal.ZERO) > 0 ? "+" + etfProfit + " €" : etfProfit.toString() + " €");
                etfProfitView.setTextColor(etfProfit.compareTo(BigDecimal.ZERO) > 0 ? Color.parseColor("#006400") : Color.RED);
                updatePortfolioValue();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                etfValue = null;
                etfView.setText("??");
                etfProfitView.setText("??");
                etfProfitView.setTextColor(Color.RED);
                Toast.makeText(MainActivity.this, "Error fetching ETF", Toast.LENGTH_SHORT).show();
            }
        });

        fetcher.getZaba(zabaAmountValue, new DataFetcher.Callback() {
            @Override
            public void onResult(BigDecimal price) {
                // Update UI
                zabaValue = price;
                portfolioValue = portfolioValue.add(price);
                zabaProfit = price.subtract(Converters.fromString(String.valueOf(zabaMoneyInvested.getText()))).add(Converters.fromString(String.valueOf(zabaMoneyCollected.getText())));
                zabaView.setText(price.toPlainString() + " €");
                zabaProfitView.setText(zabaProfit.compareTo(BigDecimal.ZERO) > 0 ? "+" + zabaProfit + " €" : zabaProfit.toString() + " €");
                zabaProfitView.setTextColor(zabaProfit.compareTo(BigDecimal.ZERO) > 0 ? Color.parseColor("#006400") : Color.RED);
                updatePortfolioValue();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                zabaValue = null;
                zabaView.setText("??");
                zabaProfitView.setText("??");
                zabaProfitView.setTextColor(Color.RED);
                Toast.makeText(MainActivity.this, "Error fetching ZABA", Toast.LENGTH_SHORT).show();
            }
        });

        fetcher.getCrypto(bitcoinAmountValue, etheriumAmountValue, new DataFetcher.Callback() {
            @Override
            public void onResult(BigDecimal price) {
                // Update UI
                cryptoValue = price;
                portfolioValue = portfolioValue.add(price);
                cryptoProfit = price.subtract(Converters.fromString(String.valueOf(bitcoinMoneyInvested.getText())).add(Converters.fromString(String.valueOf(etheriumMoneyInvested.getText()))))
                        .add(Converters.fromString(String.valueOf(etheriumMoneyCollected.getText()))).add(Converters.fromString(String.valueOf(bitcoinMoneyCollected.getText())));
                cryptoView.setText(price.toPlainString() + " €");
                cryptoProfitView.setText(cryptoProfit.compareTo(BigDecimal.ZERO) > 0 ? "+" + cryptoProfit + " €" : cryptoProfit.toString() + " €");
                cryptoProfitView.setTextColor(cryptoProfit.compareTo(BigDecimal.ZERO) > 0 ? Color.parseColor("#006400") : Color.RED);
                updatePortfolioValue();
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                cryptoValue = null;
                cryptoView.setText("??");
                cryptoProfitView.setText("??");
                cryptoProfitView.setTextColor(Color.RED);
                Toast.makeText(MainActivity.this, "Error fetching Crypto", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void saveData() {
        for (Investment i : investments) {
            boolean changes = false;

            if (i.getName().equals("VWCE") && (!i.getAmount().toString().contentEquals(vwceAmount.getText()) ||
                    (!i.getMoneyInvested().toString().contentEquals(vwceMoneyInvested.getText())) ||
                    (!i.getMoneyCollected().toString().contentEquals(vwceMoneyCollected.getText())))) {
                i.setAmount(new BigDecimal(vwceAmount.getText().toString()));
                vwceAmountValue = new BigDecimal(vwceAmount.getText().toString());
                if(!vwceMoneyInvested.getText().toString().isEmpty())
                    i.setMoneyInvested(new BigDecimal(vwceMoneyInvested.getText().toString()));
                if(!vwceMoneyCollected.getText().toString().isEmpty())
                    i.setMoneyCollected(new BigDecimal(vwceMoneyCollected.getText().toString()));
                changes = true;
            } else if (i.getName().equals("FWRA") && (!i.getAmount().toString().contentEquals(fwraAmount.getText()) ||
                    (!i.getMoneyInvested().toString().contentEquals(fwraMoneyInvested.getText())) ||
                    (!i.getMoneyCollected().toString().contentEquals(fwraMoneyCollected.getText())))) {
                i.setAmount(new BigDecimal(fwraAmount.getText().toString()));
                fwraAmountValue = new BigDecimal(fwraAmount.getText().toString());
                if(!fwraMoneyInvested.getText().toString().isEmpty())
                    i.setMoneyInvested(new BigDecimal(fwraMoneyInvested.getText().toString()));
                if(!fwraMoneyCollected.getText().toString().isEmpty())
                    i.setMoneyCollected(new BigDecimal(fwraMoneyCollected.getText().toString()));
                changes = true;
            } else if (i.getName().equals("ZABA") && (!i.getAmount().toString().contentEquals(zabaAmount.getText()) ||
                    (!i.getMoneyInvested().toString().contentEquals(zabaMoneyInvested.getText())) ||
                    (!i.getMoneyCollected().toString().contentEquals(zabaMoneyCollected.getText())))) {
                i.setAmount(new BigDecimal(zabaAmount.getText().toString()));
                zabaAmountValue = new BigDecimal(zabaAmount.getText().toString());
                if(!zabaMoneyInvested.getText().toString().isEmpty())
                    i.setMoneyInvested(new BigDecimal(zabaMoneyInvested.getText().toString()));
                if(!zabaMoneyCollected.getText().toString().isEmpty())
                    i.setMoneyCollected(new BigDecimal(zabaMoneyCollected.getText().toString()));
                changes = true;
            } else if (i.getName().equals("Bitcoin") && (!i.getAmount().toString().contentEquals(bitcoinAmount.getText()) ||
                    (!i.getMoneyInvested().toString().contentEquals(bitcoinMoneyInvested.getText())) ||
                    (!i.getMoneyCollected().toString().contentEquals(bitcoinMoneyCollected.getText())))) {
                i.setAmount(new BigDecimal(bitcoinAmount.getText().toString()));
                bitcoinAmountValue = new BigDecimal(bitcoinAmount.getText().toString());
                if(!bitcoinMoneyInvested.getText().toString().isEmpty())
                    i.setMoneyInvested(new BigDecimal(bitcoinMoneyInvested.getText().toString()));
                if(!bitcoinMoneyCollected.getText().toString().isEmpty())
                    i.setMoneyCollected(new BigDecimal(bitcoinMoneyCollected.getText().toString()));
                changes = true;
            } else if (i.getName().equals("Etherium") && (!i.getAmount().toString().contentEquals(etheriumAmount.getText()) ||
                    (!i.getMoneyInvested().toString().contentEquals(etheriumMoneyInvested.getText())) ||
                    (!i.getMoneyCollected().toString().contentEquals(etheriumMoneyCollected.getText())))) {
                i.setAmount(new BigDecimal(etheriumAmount.getText().toString()));
                etheriumAmountValue = new BigDecimal(etheriumAmount.getText().toString());
                if(!etheriumMoneyInvested.getText().toString().isEmpty())
                    i.setMoneyInvested(new BigDecimal(etheriumMoneyInvested.getText().toString()));
                if(!etheriumMoneyCollected.getText().toString().isEmpty())
                    i.setMoneyCollected(new BigDecimal(etheriumMoneyCollected.getText().toString()));
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
        profit = BigDecimal.ZERO;
        if (etfProfit != null && zabaProfit != null && cryptoProfit != null) {
            portfolioView.setText(portfolioValue.toPlainString() + " €");
            profit = etfProfit.add(zabaProfit).add(cryptoProfit);
            portfolioProfitView.setText(profit.compareTo(BigDecimal.ZERO) > 0 ? "+" + profit + " €" : profit.toString() + " €");
            portfolioProfitView.setTextColor(profit.compareTo(BigDecimal.ZERO) > 0 ? Color.parseColor("#006400") : Color.RED);
        }
        else {
            portfolioView.setText(portfolioValue.toPlainString() + " €");
            profit = etfProfit != null ? etfProfit : BigDecimal.ZERO;
            profit = profit.add(zabaProfit != null ? zabaProfit : BigDecimal.ZERO);
            profit = profit.add(cryptoProfit != null ? cryptoProfit : BigDecimal.ZERO);
            portfolioProfitView.setText(profit.compareTo(BigDecimal.ZERO) > 0 ? "+" + profit + " €" : profit.toString() + " €");
            portfolioProfitView.setTextColor(profit.compareTo(BigDecimal.ZERO) > 0 ? Color.parseColor("#006400") : Color.RED);
        }
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
        calculateMonthlyProfit();
    }

    private void calculateMonthlyProfit() {
        startOfInvestingDate.setText(startOfInvestingDateValue != null ? sdf.format(startOfInvestingDateValue) : "");
        LocalDate startDate = startOfInvestingDateValue.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate today = LocalDate.now();
        long monthsSinceStartOfInvesting = ChronoUnit.MONTHS.between(startDate, today);
        if (monthsSinceStartOfInvesting > 0) {
            BigDecimal profitPerMonth = profit.divide(new BigDecimal(monthsSinceStartOfInvesting), 2, RoundingMode.HALF_UP);
            profitPerMonthView.setText(profitPerMonth.compareTo(BigDecimal.ZERO) >= 0 ? "+" + profitPerMonth.toString() + " €" : profitPerMonth.toString() + " €");
            profitPerMonthView.setTextColor(profit.compareTo(BigDecimal.ZERO) > 0 ? Color.parseColor("#006400") : Color.RED);
        }
        else {
            profitPerMonthView.setText("--");
            profitPerMonthView.setTextColor(Color.BLACK);
        }
    }
}