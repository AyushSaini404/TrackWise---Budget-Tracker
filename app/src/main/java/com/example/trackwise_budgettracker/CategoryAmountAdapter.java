package com.example.trackwise_budgettracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAmountAdapter extends RecyclerView.Adapter<CategoryAmountAdapter.ViewHolder> {

    private final List<CategoryAmount> itemList;
    private final OnItemDeleteListener deleteListener;

    public interface OnItemDeleteListener {
        void onItemDeleted(int position, CategoryAmount item);
    }

    public CategoryAmountAdapter(List<CategoryAmount> itemList, OnItemDeleteListener deleteListener) {
        this.itemList = itemList;
        this.deleteListener = deleteListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryText, amountText;
        ImageView deleteButton;

        public ViewHolder(@NonNull View view) {
            super(view);
            categoryText = view.findViewById(R.id.categoryNameText);
            amountText = view.findViewById(R.id.amountText);
            deleteButton = view.findViewById(R.id.deleteButton);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category_amount, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CategoryAmount item = itemList.get(position);
        holder.categoryText.setText(item.getCategory());
        holder.amountText.setText(String.format("₹%s", item.getAmount()));

        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onItemDeleted(holder.getAdapterPosition(), item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
