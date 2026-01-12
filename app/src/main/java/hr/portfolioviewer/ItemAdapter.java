package hr.portfolioviewer;

// ItemAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<Transactions> items;

    public ItemAdapter(List<Transactions> items) {
        this.items = items;
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewItem);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        var item = items.get(position);
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.getDefault());
        String formattedDate = item.getTransactionTime().format(formatter);
        TransactionType type = item.getTransactionType();
        BigDecimal amount = item.getAmount();
        BigDecimal price = item.getPrice();
        BigDecimal collected = item.getCollected();
        String signAmount = amount.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        String signPrice = price.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        String signCollected = collected != null && collected.compareTo(BigDecimal.ZERO) > 0 ? "+" : "";
        StringBuilder text = new StringBuilder();

        text.append(type).append(" ");
        if (type != TransactionType.DIVIDEND) {
            if (type == TransactionType.CHANGE) {
                text.append(signAmount);
            }
            text.append(amount).append(" ");
        }
        text.append(item.getInvestment().getName()).append(" for ");
        if (type == TransactionType.CHANGE) {
            text.append(signPrice);
        }
        text.append(price).append(" €");
        if (type == TransactionType.CHANGE) {
            text.append(" collected ")
                    .append(signCollected)
                    .append(collected);
        }
        text.append(" at ").append(formattedDate);
        holder.textView.setText(text.toString());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setItems(List<Transactions> items) {
        this.items = items;
        notifyDataSetChanged();
    }
}
