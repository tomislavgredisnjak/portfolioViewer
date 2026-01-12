package hr.portfolioviewer;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransactionsFragment extends Fragment {

    private Button newTransactionButton;
    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private AppDatabase db;
    private InvestmentDao investmentDao;
    private TransactionsDao transactionsDao;

    private TransactionViewModel viewModel;
    private List<Transactions> transactions = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.transactions_activity, container, false);
        this.newTransactionButton = view.findViewById(R.id.newTransactionButton);
        recyclerView = view.findViewById(R.id.transactionsRecycler);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );
        db = AppDatabase.getDatabase(requireContext());
        investmentDao = db.investmentDao();
        transactionsDao = db.transactionsDao();
        viewModel = new ViewModelProvider(this)
                .get(TransactionViewModel.class);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        adapter = new ItemAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);
        newTransactionButton.setOnClickListener(v -> showNewTransactionDialog());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                transactions.addAll(transactionsDao.getAllTransactions());
                requireActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setItems(transactions);
                    }
                });
            }
        });

        return view;
    }

    private void showNewTransactionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.transaction_dialog, null);
        builder.setView(dialogView);

        Spinner investmentSpinner = dialogView.findViewById(R.id.investmentSpinner);
        RadioGroup transactionTypeGroup = dialogView.findViewById(R.id.transactionTypeGroup);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        EditText etPrice = dialogView.findViewById(R.id.etPrice);

        // Load investments into spinner
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            List<Investment> investments = investmentDao.getAllInvestments(); // background
            requireActivity().runOnUiThread(() -> {
                ArrayAdapter<Investment> adapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, investments);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                investmentSpinner.setAdapter(adapter);
            });
        });

        transactionTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbDividend) {
                etAmount.setText("");       // clear amount
                etAmount.setEnabled(false); // disable input
                etAmount.setBackgroundColor(Color.LTGRAY);
            } else {
                etAmount.setEnabled(true);  // enable input
                etAmount.setHint("Enter amount");
                etAmount.setBackgroundColor(Color.WHITE);
            }
        });

        // Dialog buttons
        builder.setTitle("New Transaction")
                .setPositiveButton("Submit", null) // We'll override later
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Override Submit to prevent auto-dismiss if validation fails
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            // Validation
            if (investmentSpinner.getSelectedItem() == null) {
                Toast.makeText(requireContext(), "Select an investment", Toast.LENGTH_SHORT).show();
                return;
            }
            if (transactionTypeGroup.getCheckedRadioButtonId() == -1) {
                Toast.makeText(requireContext(), "Select transaction type", Toast.LENGTH_SHORT).show();
                return;
            }
            Investment selectedInvestment = (Investment) investmentSpinner.getSelectedItem();
            int checkedId = transactionTypeGroup.getCheckedRadioButtonId();
            TransactionType type = checkedId == R.id.rbBuy ? TransactionType.BUY :
                    checkedId == R.id.rbSell ? TransactionType.SELL :
                            TransactionType.DIVIDEND;
            if ((type != TransactionType.DIVIDEND && etAmount.getText().toString().isEmpty()) || etPrice.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Enter amount and price", Toast.LENGTH_SHORT).show();
                return;
            }

            BigDecimal amount = BigDecimal.ZERO;

            if (type == TransactionType.BUY || type == TransactionType.SELL) {
                // Only parse amount if relevant
                try {
                    amount = new BigDecimal(etAmount.getText().toString());
                    if(amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || amount.compareTo(selectedInvestment.getAmount()) > 0)
                        throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            BigDecimal price = new BigDecimal(etPrice.getText().toString());

            // Insert transaction
            Transactions transaction = new Transactions(type, selectedInvestment,
                    LocalDateTime.now(), amount, price, null);
            viewModel.insertTransactionWithInvestment(transaction);

            Toast.makeText(requireContext(), "Transaction added", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            transactions.add(transaction);
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.setItems(transactions);
                }
            });
        });
    }
}
