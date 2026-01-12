package hr.portfolioviewer;

// ItemAdapter.java
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.getDefault());
        String formattedDate =
                items.get(position)
                        .getTransactionTime()
                        .format(formatter);
        TransactionType type = items.get(position).getTransactionType();
        String text = type + " " + (type != TransactionType.DIVIDEND ? items.get(position).getAmount() + " " : "") + items.get(position).getInvestment().getName() + " for " + items.get(position).getPrice() + " at " + formattedDate;
        holder.textView.setText(text);
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
